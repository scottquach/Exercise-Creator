package com.example.scott.excecisecreator;

import android.app.Application;

import timber.log.Timber;

/**
 * Created by Scott Quach on 7/27/2017.
 */

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new Timber.DebugTree());
    }
}
