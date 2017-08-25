package com.example.scott.speaksteps;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

import timber.log.Timber;

/**
 * Created by Scott Quach on 8/24/2017.
 */

public class Instrumentation {

    public static FirebaseAnalytics firebaseAnalytics;

    public static Instrumentation instance = new Instrumentation();

    //empty constructor
    public Instrumentation() {
    }

    public static Instrumentation getInstance() {
        return instance;
    }

    public void init(Context context) {
        firebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    public void track(String event, String value) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.VALUE, value);
        firebaseAnalytics.logEvent(event, bundle);
        Timber.i("FirebaseAnalytics: Event - " + event + " Value - " + value);

    }

    public static class TrackEvents {
        public static final String OPEN_SETTINGS = "open_settings";
        public static final String OPEN_ABOUT = "open_about";
        public static final String ADD_TASK = "add_task";
        public static final String ADD_BREAK = "add_break";
        public static final String ROUTINE_DELETED = "routine_deleted";
        public static final String ROUTINE_CREATED = "routine_created";
        public static final String ROUTINE_PLAYED = "routine_played";
        public static final String ROUTINE_PAUSED = "routine_paused";
        public static final String ROUTINE_RESET = "routine_reset";
        public static final String ROUTINE_ROW_EDITED = "routine_row_edited";
        public static final String ROUTINE_ROW_DELETED = "reoutine_row_deleted";
    }

    public static class TrackParams {
        public static final String SUCCESS = "success";
        public static final String FAILURE = "failure";
    }
}
