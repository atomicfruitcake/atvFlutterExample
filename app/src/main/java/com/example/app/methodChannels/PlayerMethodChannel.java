package com.example.app.methodChannels;

import android.content.Context;
import android.util.Log;

import com.example.app.MyPlayerAdapter;


import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

public class PlayerMethodChannel {

    Context mContext;
    private static final String channelName = "com.example.app/player";
    private static final String unavailable = "UNAVAILABLE";

    MyPlayerAdapter playerAdapter = MyPlayerAdapter.getInstance();

    public String getChannelName() {
        return channelName;
    }

    public PlayerMethodChannel(Context mContext) {
        this.mContext = mContext;
    }

    public boolean pausePlayer() {
        playerAdapter.pause();
        return true;
    }

    public boolean startPlayer() {
        playerAdapter.play();
        return true;
    }

    public long getLoadedMediaDuration() {
        return playerAdapter.getDuration();
    }


    public void callback(MethodCall call, MethodChannel.Result result) {
        {
            Log.i(
                "PlayerMethodChannelListener",
                "Received method call from flutter to "+ call.method
            );
            if (call.method.equals("pause")) {
                if (pausePlayer()) {
                    result.success(true);
                } else {
                    result.error(
                        unavailable,
                        "Error pausing video",
                        null
                    );
                }
            }
            if (call.method.equals("play")) {
                if (startPlayer()) {
                    result.success(true);
                } else {
                    result.error(
                        unavailable,
                        "Error pausing video",
                        null
                    );
                }
            }
            if (call.method.equals("getLoadedMediaDuration")) {
                try {
                    result.success(
                        getLoadedMediaDuration()
                    );
                } catch (Exception e) {
                    result.error(
                        unavailable,
                        "Error getting loaded media duration",
                        null
                    );
                }
            }
        }
    }
}
