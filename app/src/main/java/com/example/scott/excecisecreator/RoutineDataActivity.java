package com.example.scott.excecisecreator;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.scott.excecisecreator.database.DataBaseHelper;
import com.pacific.timer.Rx2Timer;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;

public class RoutineDataActivity extends BaseDataActivity {

    private DataBaseHelper db;

    @BindView(R.id.exerciseRecycleView)
    RecyclerView recyclerView;
    private RecyclerAdapter adapter;

    private String exerciseName;

    private ArrayList<String> entries = new ArrayList<>();
    private ArrayList<Integer> types = new ArrayList<>(); //0 represents a task, 1 represents a break

    private TextToSpeech tts;

    private int step = 0;
    private int exerciseSize = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        //initialization
        db = new DataBaseHelper(this);
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS){
                    tts.setLanguage(Locale.US);
                }
            }
        });

        //get exercise name to load
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            exerciseName = bundle.getString("name");
            if (getSupportActionBar() != null) getSupportActionBar().setTitle(exerciseName);
            exerciseName = exerciseName.replaceAll("\\s+", "");
            loadData();
            setUpRecycleView();
        }else{
            Toast.makeText(this, R.string.error_loading_exercise, Toast.LENGTH_SHORT).show();
            Intent exitToHome = new Intent(RoutineDataActivity.this, StartMenuDataActivity.class);
            startActivity(exitToHome);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tts.stop();
        tts.shutdown();
    }

    /*Setup up layout manager and adapter for the
        recycle view, uses the data set entries ArrayList
         */
    private void setUpRecycleView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecyclerAdapter(entries, this);
        recyclerView.setAdapter(adapter);
    }

    /*Notifies the adapter to update
    the recycleview with the edited content
     */
    private void updateRecyclerView() {
        adapter.notifyDataSetChanged();
    }

    /*fill the ArrayLists entries and types with the appropriate
    values from the db table named exerciseName
     */
    private void loadData() {
        Cursor dataCursor = db.getExercise(exerciseName);

        String entry;
        int type;

        if (dataCursor.moveToFirst()) {
            do {
                entry = dataCursor.getString(1);
                type = dataCursor.getInt(2);

                entries.add(entry);
                types.add(type);
            } while (dataCursor.moveToNext());
        }

        exerciseSize = entries.size();
    }

    private void determineNextStep() {
        if (step < exerciseSize) {
            if (types.get(step) == 0) {
                Log.d("step count", String.valueOf(step) + "was zero");
                playTask(entries.get(step));
            } else if (types.get(step) == 1) {
                Log.d("step count", String.valueOf(step) + "was one");
                startBreak(Integer.valueOf(entries.get(step)));
            }
        } else {
            step = 0;
            Log.d("step count", "step reset to zero");
        }

    }

    private void breakComplete() {
        step++;
        determineNextStep();
    }

    private void playTask(String task) {
        step++;
        tts.speak(task, TextToSpeech.QUEUE_ADD, null);
        determineNextStep();
    }

    private void startBreak(int totalSeconds) {
        Rx2Timer timer = Rx2Timer.builder()
                .initialDelay(0) //default is 0
                .period(1) //default is 1
                .take(totalSeconds) //default is 60
                .onCount(count -> {

                })
                .onComplete(() -> breakComplete())
                .build();

        timer.start();
    }

    public void playButtonClicked(View view) {
        determineNextStep();
    }

    public void editModeButtonClicked(View view) {
        Intent openEditMode = new Intent(RoutineDataActivity.this, EditModeDataActivity.class);
        openEditMode.putExtra("name", exerciseName);
        startActivity(openEditMode);
        finish();
    }
}
