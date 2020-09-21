/**
 * Playback Activity Class. CLass for loading in the Playback Video Fragment object
 *
 * @author       atomicfruitcake (WIREWAX)
 * @date         2020
 * @see          com.example.app.video.Video
 */
package com.example.app.activities;

import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import com.example.app.fragments.PlaybackVideoFragment;


public class PlaybackActivity extends FragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(android.R.id.content, new PlaybackVideoFragment())
                    .commit();
        }
    }

}
