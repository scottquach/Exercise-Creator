package com.example.scott.speaksteps;

import android.app.Application;
import android.content.SharedPreferences;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

/**
 * Created by Scott Quach on 7/27/2017.
 */

public class BaseApplication extends Application {

    private static BaseApplication INSTANCE = null;

    public BaseApplication() {}

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        Timber.plant(new MyDebugTree());

        Instrumentation.getInstance().init(this);
        INSTANCE = this;
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

    public static BaseApplication getInstance(){
        return INSTANCE;
    }

    public SharedPreferences getAppPrefs() {
        return getSharedPreferences("app", MODE_PRIVATE);
    }
}
