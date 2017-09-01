package com.example.scott.speaksteps.services;

import android.content.Context;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;

import java.util.Locale;

import timber.log.Timber;

public class TextToSpeechService extends JobIntentService {

    private TextToSpeech textToSpeech;

    /**
     * Unique job ID for this service.
     */
    static final int JOB_ID = 1000;

    /**
     * Convenience method for enqueuing work in to this service.
     */
    static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, TextToSpeechService.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Timber.d("doing job work");
        if (intent.getExtras() != null) {
            String task = intent.getExtras().getString("task");
            textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status == TextToSpeech.SUCCESS) {
                        Timber.d("SPEAKING");
                        textToSpeech.setLanguage(Locale.US);
                        textToSpeech.speak(task, TextToSpeech.QUEUE_ADD, null);
                    } else Timber.d("TTS couldn't initialize");
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (textToSpeech != null) {
            textToSpeech.shutdown();
        }
    }
}
