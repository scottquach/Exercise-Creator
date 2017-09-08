package com.example.scott.speaksteps;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Instrumentation.getInstance().track(Instrumentation.TrackEvents.OPEN_ABOUT,
                Instrumentation.TrackParams.SUCCESS);
    }
}
