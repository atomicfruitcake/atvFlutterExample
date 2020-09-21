package com.example.app.activities;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.example.app.R;
import com.example.app.methodChannels.MethodChannelRegister;

import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.FlutterEngineCache;
import io.flutter.embedding.engine.dart.DartExecutor;
import io.flutter.plugins.GeneratedPluginRegistrant;


public class MainActivity extends Activity {

    private static final String flutterEngineId = "flutter_engine";

    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        GeneratedPluginRegistrant.registerWith(flutterEngine);
        new MethodChannelRegister(flutterEngine, this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FlutterEngine flutterEngine = new FlutterEngine(this);
        flutterEngine.getNavigationChannel()
            .setInitialRoute("../atvFlutterExampleActivity/lib/main.dart");
        flutterEngine.getDartExecutor()
            .executeDartEntrypoint(
                DartExecutor.DartEntrypoint.createDefault()
            );

        configureFlutterEngine(flutterEngine);
        FlutterEngineCache
            .getInstance()
            .put(flutterEngineId, flutterEngine);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        }
    }
