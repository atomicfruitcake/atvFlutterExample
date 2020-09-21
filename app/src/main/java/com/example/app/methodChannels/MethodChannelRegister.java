package com.example.app.methodChannels;

import android.app.Activity;

import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;

public class MethodChannelRegister {

    public MethodChannelRegister(FlutterEngine flutterEngine, Activity activity) {
        registerMethodChannels(flutterEngine, activity);
    }

    private void registerMethodChannels(FlutterEngine flutterEngine, Activity activity) {
        PlayerMethodChannel playerMethodChannel = new PlayerMethodChannel(activity);
        new MethodChannel(
                flutterEngine.getDartExecutor().getBinaryMessenger(),
                playerMethodChannel.getChannelName()
        ).setMethodCallHandler(
                playerMethodChannel::callback
        );

    }
}
