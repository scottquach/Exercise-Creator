package com.example.scott.speaksteps;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Instrumentation.getInstance().track(Instrumentation.TrackEvents.OPEN_SETTINGS,
                Instrumentation.TrackParams.SUCCESS);
    }
}
