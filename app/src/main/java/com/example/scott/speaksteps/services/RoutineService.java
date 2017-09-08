package com.example.scott.speaksteps.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.example.scott.speaksteps.BaseApplication;
import com.example.scott.speaksteps.Constants;
import com.example.scott.speaksteps.R;
import com.example.scott.speaksteps.RoutineActivity;
import com.pacific.timer.Rx2Timer;

import java.util.ArrayList;
import java.util.Locale;

import timber.log.Timber;

import static com.example.scott.speaksteps.Constants.BUTTON_TOGGLE_RESET;

/**
 * Created by Scott Quach on 8/30/2017.
 */

public class RoutineService extends Service {

    private ArrayList<String> entries = new ArrayList<>();
    private ArrayList<Integer> breakValues = new ArrayList<>();
    private ArrayList<Integer> entryType = new ArrayList<>(); //0 represents a task, 1 represents a break

    private static Rx2Timer timer;

    private TextToSpeech textToSpeech;

    private String exerciseName;

    public static int step = 0;
    private int exerciseSize = 0;

    PowerManager.WakeLock wakeLock;

    private final IBinder routineBinder = new RoutineBinder();

    public class RoutineBinder extends Binder {
        public RoutineService getService() {
            return RoutineService.this;
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return routineBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getExtras() != null) {
            exerciseName = intent.getExtras().getString("name");

            entries = intent.getExtras().getStringArrayList("entries");
            breakValues = intent.getExtras().getIntegerArrayList("breakValues");
            entryType = intent.getExtras().getIntegerArrayList("entryType");
            exerciseSize = entries.size();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS) {
                    textToSpeech.setLanguage(Locale.US);
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        resetRoutine();
        if (textToSpeech != null) {
            textToSpeech.shutdown();
        }
    }

    // TODO: 9/8/2017 transition to alarmmanager instead of wakelock

    /**Starts the routine and acquires a necessary wakelock
     *
     */
    public void startRoutine() {
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "routine_wake_lock");
              wakeLock.acquire();
        startForegroundService();
        determineNextStep();
    }

    /**
     * Will determine whether to start a break, task,
     * or if the routine is done
     */
    private void determineNextStep() {
        if (step < exerciseSize) {
            sendUpdateBroadcast(false);
            if (entryType.get(step) == 0) {
                Timber.d("current type is 0");
                playTask(entries.get(step));
            } else if (entryType.get(step) == 1) {
                Timber.d("current type is 1");
                Timber.d(Integer.valueOf(breakValues.get(step)).toString());
                startBreak(Integer.valueOf(breakValues.get(step)));
            }
        } else {
            Timber.d("routine completed");
            BaseApplication.getInstance().getAppPrefs().edit().putInt(Constants.BUTTON_TOGGLE, BUTTON_TOGGLE_RESET).apply();
            sendUpdateBroadcast(true);
            stopForegroundService();
            //Called when the routine has been complete
        }
    }

    private void startBreak(int totalSeconds) {
        timer = Rx2Timer.builder()
                .initialDelay(0) //default is 0
                .period(1) //default is 1
                .take(totalSeconds) //default is 60
                .onCount(count -> {
                    Timber.d("counted up " + count);
                })
                .onComplete(() -> breakComplete())
                .build();
        timer.start();
    }

    public static void pauseProgress() {
        Timber.d("timer paused");
        if (timer != null) timer.pause();
    }

    public static void resumeProgress() {
        Timber.d("timber resumed");
        if (timer != null) timer.resume();
    }

    public static void resetRoutine() {
        step = 0;
        if (timer != null) {
            timer.stop();
        }
    }

    public boolean isTimerPaused() {
        return timer != null ? timer.isPause() : false;
    }

    private void breakComplete() {
        timer = null;
        step++;
        determineNextStep();
    }

    private void playTask(String task) {
        textToSpeech.speak(task, TextToSpeech.QUEUE_ADD, null);
        Timber.d(task);
        step++;
        determineNextStep();
    }

    /**
     * Updates the RoutineActivity on routine progress so that it can update
     * the UI
     *
     * @param isFinished
     */
    private void sendUpdateBroadcast(boolean isFinished) {
        Intent broadcastIntent = new Intent("routine_update");
        broadcastIntent.putExtra("step", step);
        broadcastIntent.putExtra("isFinished", isFinished);

        sendBroadcast(broadcastIntent);
    }

    /**Starts this service as a foreground service as required by Android O
     *
     */
    private void startForegroundService() {
        Intent notificationIntent = new Intent(this, RoutineActivity.class);
        notificationIntent.putExtra("name", exerciseName);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 101, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this, "speak_steps_notif_channel")
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Routine Running")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(101, notification);
        Timber.d("starting foreground service");
    }

    /**Stops the foreground service and releases held wakelocks if any
     *
     */
    public void stopForegroundService() {
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }
        stopForeground(true);
        stopSelf();
    }
}
