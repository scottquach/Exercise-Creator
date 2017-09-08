package com.example.scott.speaksteps;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Fade;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.scott.speaksteps.database.KeyConstants;
import com.example.scott.speaksteps.databinding.ActivityRoutineBinding;
import com.example.scott.speaksteps.services.RoutineService;

import java.util.ArrayList;

import timber.log.Timber;

import static com.example.scott.speaksteps.Constants.BUTTON_TOGGLE_PAUSE;
import static com.example.scott.speaksteps.Constants.BUTTON_TOGGLE_PLAY;
import static com.example.scott.speaksteps.Constants.BUTTON_TOGGLE_RESET;

public class RoutineActivity extends BaseDataActivity {

    private RecyclerAdapter adapter;

    private String exerciseName;

    private ArrayList<String> entries = new ArrayList<>();
    private ArrayList<Integer> breakValues = new ArrayList<>();
    private ArrayList<Integer> entryType = new ArrayList<>(); //0 represents a task, 1 represents a break

    private int exerciseSize = 0;

    private int playButtonActionToggle = 0;

    RoutineSyncReceiver receiver;

    private RoutineService routineService;
    private boolean isBounded = false;

    private ActivityRoutineBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_routine);
        setupWindowTransitions();

        //get exercise name to load
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            exerciseName = bundle.getString("name").replaceAll("\\s+", "");
            if (getSupportActionBar() != null) getSupportActionBar()
                    .setTitle(dbHelper.getFullRoutineName(exerciseName));
            loadData();
            setUpRecycleView();
        } else {
            Toast.makeText(this, R.string.error_loading_routine, Toast.LENGTH_SHORT).show();
            Intent exitToHome = new Intent(RoutineActivity.this, StartMenuActivity.class);
            startActivity(exitToHome);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        receiver = new RoutineSyncReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("routine_update");
        registerReceiver(receiver, filter);

        if (routineService == null) {
            startRoutineService();
            handleActivityResumedState();
        } else {
            handleActivityResumedState();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_action_home:
                startActivity(new Intent(this, StartMenuActivity.class));
                break;
        }
        return true;
    }

    private void setupWindowTransitions() {
        Fade slide = new Fade();
        slide.setDuration(1000);
        getWindow().setEnterTransition(slide);
        getWindow().setExitTransition(slide);
    }

    /**
     * Setup up layout manager and adapter for the
     * recycle view, uses the data set entries ArrayList
     */
    private void setUpRecycleView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        binding.recycleViewRoutines.setLayoutManager(layoutManager);
        adapter = new RecyclerAdapter(entries, this);
        binding.recycleViewRoutines.setAdapter(adapter);
    }

    /**
     * Notifies the adapter to update
     * the recycleview with the edited content
     */
    private void updateRecyclerView() {
        adapter.notifyDataSetChanged();
    }

    /**
     * fill the ArrayLists entries and entryType with the appropriate
     * values from the db table named exerciseName
     */
    private void loadData() {
        Cursor dataCursor = dbHelper.getExercise(exerciseName);

        String entry;
        int dataValue;
        int type;

        if (dataCursor.moveToFirst()) {
            do {
                entry = dataCursor.getString(1);
                dataValue = dataCursor.getInt(2);
                type = dataCursor.getInt(3);

                entries.add(entry);
                breakValues.add(dataCursor.getInt(2));
                entryType.add(type);

                Timber.d("Entry: " + entry + " dataValue: " + dataValue + " Type: " + entryType);
            } while (dataCursor.moveToNext());
        }
        exerciseSize = entries.size();
    }

    /**Will update the Recycler View with shaded colors
     * to indicate the routines progress
     */
    private void updateProgressIndicators() {
        adapter.setStep(RoutineService.step);
        adapter.resetTracker();
        adapter.notifyDataSetChanged();
    }

    private void pauseProgress() {
        RoutineService.pauseProgress();
    }

    private void resumeProgress() {
        RoutineService.resumeProgress();
    }

    private void resetProgress() {
        RoutineService.resetRoutine();
        adapter.setStep(RoutineService.step);
        adapter.notifyDataSetChanged();
    }

    /**
     * Handles the different states for the play button. Such as whether to play, pause, or restart
     *
     * @param view
     */
    public void playButtonClicked(View view) {
        switch (playButtonActionToggle) {
            case BUTTON_TOGGLE_PLAY:
                playButtonActionToggle = BUTTON_TOGGLE_PAUSE;
                BaseApplication.getInstance().getAppPrefs().edit().putInt(Constants.BUTTON_TOGGLE, BUTTON_TOGGLE_PAUSE).apply();
                binding.buttonPlay.setImageResource(R.drawable.ic_pause);
                if (routineService != null) {
                    if (routineService.isTimerPaused()) {
                        Timber.d("called timer paused");
                        resumeProgress();
                    } else routineService.startRoutine(); Timber.d("started new routine");
                } else Timber.d("Routine service was null");

                Instrumentation.getInstance().track(Instrumentation.TrackEvents.ROUTINE_PLAYED,
                        Instrumentation.TrackParams.SUCCESS);
                break;

            case BUTTON_TOGGLE_PAUSE:
                playButtonActionToggle = BUTTON_TOGGLE_PLAY;
                BaseApplication.getInstance().getAppPrefs().edit().putInt(Constants.BUTTON_TOGGLE, BUTTON_TOGGLE_PLAY).apply();
                binding.buttonPlay.setImageResource(R.drawable.ic_play_arrow);
                pauseProgress();
                Instrumentation.getInstance().track(Instrumentation.TrackEvents.ROUTINE_PAUSED,
                        Instrumentation.TrackParams.SUCCESS);
                break;

            case BUTTON_TOGGLE_RESET:
                playButtonActionToggle = BUTTON_TOGGLE_PLAY;
                BaseApplication.getInstance().getAppPrefs().edit().putInt(Constants.BUTTON_TOGGLE, BUTTON_TOGGLE_PLAY).apply();
                binding.buttonPlay.setImageResource(R.drawable.ic_play_arrow);
                resetProgress();
                startRoutineService();
                Instrumentation.getInstance().track(Instrumentation.TrackEvents.ROUTINE_RESET,
                        Instrumentation.TrackParams.SUCCESS);
                break;
        }
    }

    public void editModeButtonClicked(View view) {
        stopRoutineService();
        BaseApplication.getInstance().getAppPrefs().edit().putInt(Constants.BUTTON_TOGGLE, BUTTON_TOGGLE_PLAY).apply();

        View sharedPlayView = (FloatingActionButton) findViewById(R.id.button_play);
        ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(RoutineActivity.this, sharedPlayView, getString(R.string.transition_button_play));

        Intent openEditMode = new Intent(RoutineActivity.this, EditModeActivity.class);
        openEditMode.putExtra(KeyConstants.NAME, exerciseName);
        startActivity(openEditMode, transitionActivityOptions.toBundle());
    }

    public void stopButtonClicked(View view) {
        stopRoutineService();
        playButtonActionToggle = BUTTON_TOGGLE_PLAY;
        BaseApplication.getInstance().getAppPrefs().edit().putInt(Constants.BUTTON_TOGGLE, BUTTON_TOGGLE_PLAY).apply();
        binding.buttonPlay.setImageResource(R.drawable.ic_play_arrow);
        resetProgress();
        startRoutineService();    }

    /**
     * Handles when a routine is running in the background
     * and the user left and then returned to the activity
     */
    private void handleActivityResumedState() {
        updateProgressIndicators();
        Timber.d("step is " + RoutineService.step);

        playButtonActionToggle = BaseApplication.getInstance().getAppPrefs().getInt(Constants.BUTTON_TOGGLE, BUTTON_TOGGLE_PLAY);
        switch (playButtonActionToggle) {
            case BUTTON_TOGGLE_PLAY:
                binding.buttonPlay.setImageResource(R.drawable.ic_play_arrow);
                break;
            case BUTTON_TOGGLE_PAUSE:
                binding.buttonPlay.setImageResource(R.drawable.ic_pause);
                break;
            case BUTTON_TOGGLE_RESET:
                binding.buttonPlay.setImageResource(R.drawable.ic_reset);
                break;
        }
    }

    private void startRoutineService() {
        adapter.resetTracker();
        if (routineService == null) {
            Intent serviceIntent = new Intent(this, RoutineService.class);
            serviceIntent.putExtra("name", exerciseName);
            serviceIntent.putStringArrayListExtra("entries", entries);
            serviceIntent.putIntegerArrayListExtra("breakValues", breakValues);
            serviceIntent.putIntegerArrayListExtra("entryType", entryType);
            startService(serviceIntent);
            bindService(serviceIntent, routineConnection, BIND_AUTO_CREATE);
        }
    }

    private void stopRoutineService() {
        if (routineService != null) {
            routineService.stopForegroundService();
            routineService.resetRoutine();
        }
    }

    private ServiceConnection routineConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            RoutineService.RoutineBinder binder = (RoutineService.RoutineBinder) iBinder;
            routineService = binder.getService();
            isBounded = true;
            Timber.d("RoutineService connected");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isBounded = false;
            Timber.d("RoutineService disconnected");
        }
    };

    public class RoutineSyncReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!intent.getExtras().getBoolean("isFinished")) {
                Timber.d("Received update broadcast");

                updateProgressIndicators();
            } else {
                playButtonActionToggle = BUTTON_TOGGLE_RESET;
                BaseApplication.getInstance().getAppPrefs().edit().putInt(Constants.BUTTON_TOGGLE, BUTTON_TOGGLE_RESET).apply();
                binding.buttonPlay.setImageResource(R.drawable.ic_reset);
                stopRoutineService();
            }
        }
    }
}
