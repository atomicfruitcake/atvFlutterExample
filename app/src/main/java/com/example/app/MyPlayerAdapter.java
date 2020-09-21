package com.example.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
//import android.media.session.MediaController;
import android.widget.MediaController;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.view.SurfaceHolder;

import androidx.leanback.R;
import androidx.leanback.media.PlaybackGlueHost;
import androidx.leanback.media.PlayerAdapter;
import androidx.leanback.media.SurfaceHolderGlueHost;

import java.io.IOException;
import java.io.Serializable;
import java.util.Objects;

/**
 * This implementation extends the {@link PlayerAdapter} with a {@link MediaPlayer}.
 */
public class MyPlayerAdapter extends PlayerAdapter implements Serializable {

    private static final String TAG = "WirewaxMediaPlayerAdapter";
    Context mContext;

    @SuppressLint("StaticFieldLeak")
    private static volatile MyPlayerAdapter playerAdapter;

    /**
     * Private Constructor. Cannot be directly instantiated as the adapter is a Singleton
     */
    private MyPlayerAdapter(){
        if (playerAdapter != null){
            throw new RuntimeException(
                "Use getInstance() method to get the single instance of this class."
            );
        }
        if (mContext == null) {
            mContext = MyApplication.context();
        }
        if( mSession == null ) {
            initMediaSession();
        }


        mPlayer = getMediaPlayer();
    }

    /**
     * Singleton instance access method
     * @return WirewaxMediaPlayerAdapter
     */
    public static MyPlayerAdapter getInstance() {
        if (playerAdapter == null) {
            synchronized (MyPlayerAdapter.class) {
                if (playerAdapter == null) playerAdapter = new MyPlayerAdapter();
            }
        }
        return playerAdapter;
    }

    protected MyPlayerAdapter readResolve() {
        return getInstance();
    }

    private MediaPlayer mPlayer = getMediaPlayer();

    public MediaSession mSession;


    PlaybackState.Builder getPlaybackStateBuilder() {
        PlaybackState.Builder playbackStateBuilder = new PlaybackState.Builder();
        playbackStateBuilder.setActions(
            PlaybackState.ACTION_PLAY
            & PlaybackState.ACTION_PAUSE
            & PlaybackState.ACTION_STOP
            & PlaybackState.ACTION_SKIP_TO_NEXT
            & PlaybackState.ACTION_SKIP_TO_PREVIOUS
            & PlaybackState.ACTION_FAST_FORWARD
            & PlaybackState.ACTION_REWIND
            & PlaybackState.ACTION_SEEK_TO
        );
        return playbackStateBuilder;
    }


    @SuppressLint("WrongConstant")
    private void initMediaSession() {
        if (mSession != null) {
            return;
        }
        if (mContext == null) {
            mContext = MyApplication.context();
        }

        mSession = new MediaSession(
            mContext.getApplicationContext(), "Player Session"
        );
        mSession.setFlags(
            MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
            MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
        );

        mInitialized = true;
        mSession.setActive(true);
        mSession.setPlaybackState(
            getPlaybackStateBuilder().setState(
                PlaybackState.STATE_PLAYING,
                0,
                (float) 0.0).build()
        );
    }

    SurfaceHolderGlueHost mSurfaceHolderGlueHost;
    final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            getCallback().onCurrentPositionChanged(MyPlayerAdapter.this);
            mHandler.postDelayed(this, getProgressUpdatingInterval());
        }
    };
    final Handler mHandler = new Handler();
    boolean mInitialized = false; // true when the MediaPlayer is prepared/initialized
    Uri mMediaSourceUri = null;
    boolean mHasDisplay;
    long mBufferedProgress;


    MediaPlayer.OnPreparedListener mOnPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            mInitialized = true;
            notifyBufferingStartEnd();
            if (mSurfaceHolderGlueHost == null || mHasDisplay) {
                getCallback().onPreparedStateChanged(MyPlayerAdapter.this);
            }
        }
    };

    final MediaPlayer.OnCompletionListener mOnCompletionListener =
            mediaPlayer -> {
                getCallback().onPlayStateChanged(MyPlayerAdapter.this);
                getCallback().onPlayCompleted(MyPlayerAdapter.this);
            };

    final MediaPlayer.OnBufferingUpdateListener mOnBufferingUpdateListener =
            new MediaPlayer.OnBufferingUpdateListener() {
                @Override
                public void onBufferingUpdate(MediaPlayer mp, int percent) {
                    mBufferedProgress = getDuration() * percent / 100;
                    getCallback().onBufferedPositionChanged(MyPlayerAdapter.this);
                }
            };

    final MediaPlayer.OnVideoSizeChangedListener mOnVideoSizeChangedListener =
            (mediaPlayer, width, height) -> getCallback().onVideoSizeChanged(MyPlayerAdapter.this, width, height);

    final MediaPlayer.OnErrorListener mOnErrorListener =
            new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    getCallback().onError(MyPlayerAdapter.this, what,
                            mContext.getString(R.string.lb_media_player_error, what, extra));
                    return MyPlayerAdapter.this.onError(what, extra);
                }
            };

    final MediaPlayer.OnSeekCompleteListener mOnSeekCompleteListener =
            mp -> MyPlayerAdapter.this.onSeekComplete();

    final MediaPlayer.OnInfoListener mOnInfoListener = new MediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            boolean handled = false;
            switch (what) {
                case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                    mBufferingStart = true;
                    notifyBufferingStartEnd();
                    handled = true;
                    break;
                case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                    mBufferingStart = false;
                    notifyBufferingStartEnd();
                    handled = true;
                    break;
            }
            boolean thisHandled = MyPlayerAdapter.this.onInfo(what, extra);
            return handled || thisHandled;
        }
    };

    boolean mBufferingStart;

    void notifyBufferingStartEnd() {
        getCallback().onBufferingStateChanged(MyPlayerAdapter.this,
                mBufferingStart || !mInitialized);
    }

    @Override
    public void onAttachedToHost(PlaybackGlueHost host) {
        if (host instanceof SurfaceHolderGlueHost) {
            mSurfaceHolderGlueHost = ((SurfaceHolderGlueHost) host);
            mSurfaceHolderGlueHost.setSurfaceHolderCallback(new VideoPlayerSurfaceHolderCallback());
        }
    }

    /**
     * Will reset the {@link MediaPlayer} and the glue such that a new file can be played. You are
     * not required to call this method before playing the first file. However you have to call it
     * before playing a second one.
     */
    public void reset() {
        changeToUninitialized();
        mPlayer.reset();
    }

    void changeToUninitialized() {
        if (mInitialized) {
            mInitialized = false;
            notifyBufferingStartEnd();
            if (mHasDisplay) {
                getCallback().onPreparedStateChanged(MyPlayerAdapter.this);
            }
        }
    }

    /**
     * Release internal MediaPlayer. Should not use the object after call release().
     */
    public void release() {
        changeToUninitialized();
        mHasDisplay = false;
        mPlayer.release();
    }

    @Override
    public void onDetachedFromHost() {
        if (mSurfaceHolderGlueHost != null) {
            mSurfaceHolderGlueHost.setSurfaceHolderCallback(null);
            mSurfaceHolderGlueHost = null;
        }
        reset();
        release();
    }

    protected boolean onError(int what, int extra) {
        return false;
    }


    protected void onSeekComplete() {}

    protected boolean onInfo(int what, int extra) {
        return false;
    }

    void setDisplay(SurfaceHolder surfaceHolder) {
        boolean hadDisplay = mHasDisplay;
        mHasDisplay = surfaceHolder != null;
        if (hadDisplay == mHasDisplay) {
            return;
        }
        mPlayer.setDisplay(surfaceHolder);
        if (mInitialized) {
            getCallback().onPreparedStateChanged(MyPlayerAdapter.this);
        }

    }

    @Override
    public void setProgressUpdatingEnabled(final boolean enabled) {
        mHandler.removeCallbacks(mRunnable);
        if (!enabled) {
            return;
        }
        mHandler.postDelayed(mRunnable, getProgressUpdatingInterval());
    }

    public int getProgressUpdatingInterval() {
        return 16;
    }

    @Override
    public boolean isPlaying() {
        return mInitialized && mPlayer.isPlaying();
    }

    @Override
    public long getDuration() {
        return mInitialized ? mPlayer.getDuration() : -1;
    }

    @Override
    public long getCurrentPosition() {
        return mInitialized ? mPlayer.getCurrentPosition() : -1;
    }

    @Override
    public void play() {
        Log.i("WirewaxMediaPlayerAdapter", "Playing Media in Player");
        if (!mInitialized || mPlayer.isPlaying()) {
            return;
        }
        mPlayer.start();
        mSession.setPlaybackState(
            getPlaybackStateBuilder().setState(
                PlaybackState.STATE_PLAYING,
                0,
                (float) 0.0).build()
        );
        getCallback().onPlayStateChanged(MyPlayerAdapter.this);
        getCallback().onCurrentPositionChanged(MyPlayerAdapter.this);
    }

    @Override
    public void pause() {
        Log.i(TAG, "Pausing Media in Player");
        if (isPlaying()) {
            mPlayer.pause();
            mSession.setPlaybackState(
                getPlaybackStateBuilder().setState(
                    PlaybackState.STATE_PAUSED,
                    0,
                    (float) 0.0).build()
            );
            getCallback().onPlayStateChanged(MyPlayerAdapter.this);
            getCallback().onCurrentPositionChanged(MyPlayerAdapter.this);
        }
    }

    @Override
    public void seekTo(long newPosition) {
        if (!mInitialized) {
            return;
        }
        mPlayer.seekTo((int) newPosition);
        mSession.setPlaybackState((
            getPlaybackStateBuilder().setState(
                PlaybackState.STATE_FAST_FORWARDING,
                0,
                (float) 0.0).build()
                )
            );
    }

    @Override
    public long getBufferedPosition() {
        return mBufferedProgress;
    }

    /**
     * Sets the media source of the player witha given URI.
     *
     * @see MediaPlayer#setDataSource(String)
     */
    public void setDataSource(Uri uri) throws IOException {
        if (Objects.equals(mMediaSourceUri, uri)) {
            return;
        }
        mMediaSourceUri = uri;
        prepareMediaForPlaying();
    }

    private void prepareMediaForPlaying() throws IOException {
        reset();
        try {
            if (mMediaSourceUri != null) {
                mPlayer.setDataSource(mContext, mMediaSourceUri);
            } else {
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mPlayer.setOnPreparedListener(mOnPreparedListener);
        mPlayer.setOnVideoSizeChangedListener(mOnVideoSizeChangedListener);
        mPlayer.setOnErrorListener(mOnErrorListener);
        mPlayer.setOnSeekCompleteListener(mOnSeekCompleteListener);
        mPlayer.setOnCompletionListener(mOnCompletionListener);
        mPlayer.setOnInfoListener(mOnInfoListener);
        mPlayer.setOnBufferingUpdateListener(mOnBufferingUpdateListener);
        mPlayer.setScreenOnWhilePlaying(true);
        notifyBufferingStartEnd();
        mPlayer.prepare();
        mPlayer.start();
        getCallback().onPlayStateChanged(MyPlayerAdapter.this);
    }

    public MediaPlayer getMediaPlayer() {
        if (mPlayer == null) {
            mPlayer = new MediaPlayer();
        }
        return mPlayer;
    }

    @Override
    public boolean isPrepared() {
        return mInitialized && (mSurfaceHolderGlueHost == null || mHasDisplay);
    }

    class VideoPlayerSurfaceHolderCallback implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            setDisplay(surfaceHolder);
            mPlayer.setDisplay(surfaceHolder);
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            setDisplay(null);
        }
    }

}
