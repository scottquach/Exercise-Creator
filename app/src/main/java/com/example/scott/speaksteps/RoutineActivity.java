package com.example.scott.speaksteps;

import android.app.ActivityOptions;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
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
import com.example.scott.speaksteps.databinding.ActivityExerciseBinding;
import com.pacific.timer.Rx2Timer;

import java.util.ArrayList;
import java.util.Locale;

import timber.log.Timber;

public class RoutineActivity extends BaseDataActivity {

    private RecyclerAdapter adapter;

    private String exerciseName;

    private ArrayList<String> entries = new ArrayList<>();
    private ArrayList<Integer> breakValues = new ArrayList<>();
    private ArrayList<Integer> entryType = new ArrayList<>(); //0 represents a task, 1 represents a break

    private TextToSpeech tts;

    private int step = 0;
    private int exerciseSize = 0;

    private int playButtonActionToggle = 0;
    private final static int BUTTON_TOGGLE_PLAY = 0;
    private final static int BUTTON_TOGGLE_PAUSE = 1;
    private final static int BUTTON_TOGGLE_RESET = 3;

    Rx2Timer timer;

    ActivityExerciseBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_exercise);

        setupWindowTransitions();

        //initialization
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    tts.setLanguage(Locale.US);
                }
            }
        });

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

    }

    @Override
    protected void onPause() {
        super.onPause();
       tts.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tts.stop();
        tts.shutdown();
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

    private void updateProgressIndicators() {
        if (step != 0) {
            binding.recycleViewRoutines.findViewHolderForAdapterPosition(step - 1).itemView.findViewById(R.id.row_card_view)
                    .setBackgroundColor(getResources().getColor(R.color.primary_light));
        }
        binding.recycleViewRoutines.findViewHolderForAdapterPosition(step).itemView.findViewById(R.id.row_card_view)
                .setBackgroundColor(getResources().getColor(R.color.divider));
    }

    private void determineNextStep() {

        if (step < exerciseSize) {
            updateProgressIndicators();
            if (entryType.get(step) == 0) {
                Timber.d("current type is 0");
                playTask(entries.get(step));
            } else if (entryType.get(step) == 1) {
                Timber.d("current type is 1");
                startBreak(Integer.valueOf(breakValues.get(step)));
            }
        } else {
            //Called when the routine has been complete
            playButtonActionToggle = BUTTON_TOGGLE_RESET;
            binding.buttonPlay.setImageResource(R.drawable.ic_reset);
        }

    }

    private void breakComplete() {
        step++;
        determineNextStep();
    }

    private void playTask(String task) {
        tts.speak(task, TextToSpeech.QUEUE_ADD, null);
        step++;
        determineNextStep();
    }

    private void startBreak(int totalSeconds) {
        timer = Rx2Timer.builder()
                .initialDelay(0) //default is 0
                .period(1) //default is 1
                .take(totalSeconds) //default is 60
                .onCount(count -> {

                })
                .onComplete(() -> breakComplete())
                .build();

        timer.start();
    }

    private void pauseProgress() {
        timer.pause();
    }

    private void resumeProgress() {
        timer.resume();
    }

    private void resetProgress() {
        step = 0;
        for (int i = 0; i < exerciseSize; i++) {
            binding.recycleViewRoutines.findViewHolderForAdapterPosition(i).itemView.findViewById(R.id.row_card_view)
                    .setBackgroundColor(getResources().getColor(R.color.backgroundColor));
        }
    }

    public void playButtonClicked(View view) {
        switch (playButtonActionToggle) {
            case BUTTON_TOGGLE_PLAY:
                playButtonActionToggle = BUTTON_TOGGLE_PAUSE;
                binding.buttonPlay.setImageResource(R.drawable.ic_pause);
                if (timer != null && timer.isPause()) {
                    resumeProgress();
                } else determineNextStep();
                Instrumentation.getInstance().track(Instrumentation.TrackEvents.ROUTINE_PLAYED,
                        Instrumentation.TrackParams.SUCCESS);
                break;

            case BUTTON_TOGGLE_PAUSE:
                playButtonActionToggle = BUTTON_TOGGLE_PLAY;
                binding.buttonPlay.setImageResource(R.drawable.ic_play_arrow);
                pauseProgress();
                Instrumentation.getInstance().track(Instrumentation.TrackEvents.ROUTINE_PAUSED,
                        Instrumentation.TrackParams.SUCCESS);
                break;

            case BUTTON_TOGGLE_RESET:
                playButtonActionToggle = BUTTON_TOGGLE_PLAY;
                binding.buttonPlay.setImageResource(R.drawable.ic_play_arrow);
                resetProgress();
                Instrumentation.getInstance().track(Instrumentation.TrackEvents.ROUTINE_RESET,
                        Instrumentation.TrackParams.SUCCESS);
                break;
        }
    }

    public void editModeButtonClicked(View view) {
        View sharedPlayView = (FloatingActionButton) findViewById(R.id.button_play);
        ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(RoutineActivity.this, sharedPlayView, getString(R.string.transition_button_play));

        Intent openEditMode = new Intent(RoutineActivity.this, EditModeActivity.class);
        openEditMode.putExtra(KeyConstants.NAME, exerciseName);
        startActivity(openEditMode, transitionActivityOptions.toBundle());
    }
}
