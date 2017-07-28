package com.example.scott.excecisecreator;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.scott.excecisecreator.database.DataBaseHelper;
import com.example.scott.excecisecreator.fragments.BreakDialogFragment;
import com.example.scott.excecisecreator.fragments.TaskDialogFragment;
import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import butterknife.BindView;

public class EditModeDataActivity extends BaseDataActivity implements BreakDialogFragment.BreakDialogListener,
        TaskDialogFragment.TaskDialogListener{

    private String exerciseName;

    @BindView(R.id.editExerciseRecycleView) RecyclerView recyclerView;
    private RecyclerAdapter adapter;

    private ArrayList<String> entries = new ArrayList<>();

    //0 represents a task, and 1 represents a break
    private ArrayList<Integer> entryType = new ArrayList<Integer>();

    private DataBaseHelper dbHelper;

    @BindView(R.id.addTaskButton) FloatingActionButton addTaskButton;
    @BindView(R.id.addBreakButton) FloatingActionButton addBreakButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_mode);

        //initialization
        dbHelper = new DataBaseHelper(this);

        //changes action bar name based on exerciseName

        //Get exercise name to be edited
        Bundle extras = getIntent().getExtras();
        if (extras != null){
            exerciseName = extras.getString("name");
            if (getSupportActionBar() != null) getSupportActionBar().setTitle(exerciseName + " " + getString(R.string.edit_mode));
            exerciseName = exerciseName.replaceAll("\\s+", "");
            loadData();
            setUpRecycleView();
        }else{
            Toast.makeText(this, "Error creating new exercise", Toast.LENGTH_SHORT).show();
            Intent exitToHome = new Intent(EditModeDataActivity.this, StartMenuDataActivity.class);
            startActivity(exitToHome);
        }

        //changes action bar name based on exerciseName
        if (getSupportActionBar() != null) getSupportActionBar().setTitle(exerciseName + " " + getString(R.string.edit_mode));
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveData();
    }

    private void setUpRecycleView(){
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new RecyclerAdapter(entries, this);
        recyclerView.setAdapter(adapter);
    }

    private void updateRecycleView(){
        adapter.notifyDataSetChanged();
    }

    /*Retrieves the specified exercises data from the db and
    indexes them into arrayLists, with the entryType list discerning
    between tasks and breaks
     */
    private void loadData(){
        Cursor dataCursor = dbHelper.getExercise(exerciseName);

        if (dataCursor.moveToFirst()){
            String entry;
            int type;
            do{
                entry = dataCursor.getString(1);
                type = dataCursor.getInt(2);
                entries.add(entry);
                entryType.add(type);
            }while (dataCursor.moveToNext());

        }else{
            Log.d("debug", "error loading cursor data");
        }
    }

    /*Saves the edited data back into
    the db
     */
    private void saveData(){
        dbHelper.saveExerciseEdits(exerciseName, entries, entryType);
    }



    private int convertToSeconds(int minute, int seconds){
        int totalSeconds;
        if (minute == 0){
            return seconds;
        }else{
            totalSeconds = (minute * 60);
            totalSeconds += seconds;
            return totalSeconds;
        }
    }

    public void createBreakClicked(View view) {
        BreakDialogFragment.newInstance().show(getFragmentManager(), "BREAK_DIALOG_FRAGMENT");
    }

    public void createTaskClicked(View view) {
        TaskDialogFragment.newInstance().show(getFragmentManager(), "TASK_DIALOG_FRAGMENT");
    }

    public void startExerciseButtonClicked(View view) {
        Intent startExercise = new Intent(EditModeDataActivity.this, RoutineDataActivity.class);
        startExercise.putExtra("name", exerciseName);
        startActivity(startExercise);
        finish();
    }

    public void deleteButtonClicked(View view) {
        dbHelper.deleteExercise(exerciseName);
        finish();
    }

    @Override
    public void createBreak(int minutes, int seconds) {
        int totalSeconds = convertToSeconds(minutes, seconds);
        Toast.makeText(EditModeDataActivity.this, "Minutes :" + String.valueOf(minutes) + " Seconds :" + String.valueOf(seconds), Toast.LENGTH_SHORT).show();
        entries.add(String.valueOf(totalSeconds));
        entryType.add(1);
        updateRecycleView();
    }

    @Override
    public void createTask(String task) {
        entries.add(task);
        entryType.add(0);
        updateRecycleView();
    }
}
