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
        Timber.plant(new MyDebugTree());
    }

    public class MyDebugTree extends Timber.DebugTree {
        @Override
        protected String createStackElementTag(StackTraceElement element) {
            return String.format("[L:%s] [M:%s] [C:%s]",
                    element.getLineNumber(),
                    element.getMethodName(),
                    super.createStackElementTag(element));
        }
    }
}
