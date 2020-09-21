package com.example.app.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.leanback.app.VideoSupportFragment;
import androidx.leanback.app.VideoSupportFragmentGlueHost;
import androidx.leanback.media.PlaybackGlue;
import androidx.leanback.media.PlaybackTransportControlGlue;
import androidx.leanback.widget.PlaybackControlsRow;

import com.example.app.MyPlayerAdapter;
import com.example.app.activities.DetailsActivity;
import com.example.app.video.Video;

import java.io.IOException;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.android.FlutterActivityLaunchConfigs;

public class PlaybackVideoFragment extends VideoSupportFragment {


    private PlaybackTransportControlGlue<MyPlayerAdapter> mTransportControlGlue;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Video video =
                (Video) getActivity().getIntent().getSerializableExtra(DetailsActivity.VIDEO);
        VideoSupportFragmentGlueHost glueHost =
                new VideoSupportFragmentGlueHost(PlaybackVideoFragment.this);
        MyPlayerAdapter playerAdapter = MyPlayerAdapter.getInstance();
        playerAdapter.setRepeatAction(PlaybackControlsRow.RepeatAction.INDEX_NONE);
        assert video != null;
        mTransportControlGlue = new PlaybackTransportControlGlue<>(getContext(), playerAdapter);
        mTransportControlGlue.setHost(glueHost);
        mTransportControlGlue.setTitle(video.getTitle());
        mTransportControlGlue.setSubtitle(video.getDescription());
        mTransportControlGlue.setSeekEnabled(true);

        try {
            playerAdapter.setDataSource(Uri.parse(video.getVideoUrl()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        mTransportControlGlue.addPlayerCallback(
            new PlaybackGlue.PlayerCallback(){
                @Override
                public void onPlayStateChanged(PlaybackGlue glue) {
                super.onPlayStateChanged(glue);
                boolean glueIsPlaying  = glue.isPlaying();
                    if(glueIsPlaying) {
                        playerAdapter.play();
                    } else {
                        playerAdapter.pause();
                    }
                }
            });
        startActivity(
            FlutterActivity
                .withCachedEngine("flutter_engine")
                .backgroundMode(FlutterActivityLaunchConfigs.BackgroundMode.transparent)
                .build(requireContext())
        );
    }

}