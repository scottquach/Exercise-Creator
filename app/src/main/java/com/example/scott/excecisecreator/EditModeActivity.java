package com.example.scott.excecisecreator;

import android.app.ActivityOptions;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.view.View;
import android.widget.Toast;

import com.example.scott.excecisecreator.database.KeyConstants;
import com.example.scott.excecisecreator.databinding.ActivityEditModeBinding;
import com.example.scott.excecisecreator.fragments.BreakDialogFragment;
import com.example.scott.excecisecreator.fragments.TaskDialogFragment;
import java.util.ArrayList;

import timber.log.Timber;


public class EditModeActivity extends BaseDataActivity implements BreakDialogFragment.BreakDialogListener,
        TaskDialogFragment.TaskDialogListener{

    private String exerciseName;

    private RecyclerAdapter adapter;

    private ArrayList<String> entries = new ArrayList<>();
    private ArrayList<Integer> breakValues = new ArrayList<>();
    //0 represents a task, and 1 represents a break
    private ArrayList<Integer> entryType = new ArrayList<Integer>();

    ActivityEditModeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_mode);

        setUpWindowTransitions();

        //Get name of exercise to be edited
        Bundle extras = getIntent().getExtras();
        if (extras != null){
            Timber.d("extras were not null");
            exerciseName = extras.getString(KeyConstants.NAME).replaceAll("\\s+", "");
            if (getSupportActionBar() != null) getSupportActionBar()
                    .setTitle(dbHelper.getFullRoutineName(exerciseName) + " " + getString(R.string.edit_mode));

            loadData();
            setUpRecycleView();
        }else{
            Toast.makeText(this, getString(R.string.error_loading_routine), Toast.LENGTH_SHORT).show();
            Intent exitToHome = new Intent(EditModeActivity.this, StartMenuActivity.class);
            startActivity(exitToHome);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        loadData();
//        updateRecycleView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveData();
    }

    private void setUpWindowTransitions(){
        Fade slide = new Fade();
        slide.setDuration(1000);
        getWindow().setEnterTransition(slide);
        getWindow().setExitTransition(slide);
    }

    private void setUpRecycleView(){
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        binding.recycleViewEditMode.setLayoutManager(layoutManager);

        adapter = new RecyclerAdapter(entries, this);
        binding.recycleViewEditMode.setAdapter(adapter);

        ItemTouchHelper.SimpleCallback recyclerEditCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP|ItemTouchHelper.DOWN,
                ItemTouchHelper.RIGHT|ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

                // TODO: 8/5/2017 actually return if successful
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                deleteRecyclerRow(viewHolder.getAdapterPosition());
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(recyclerEditCallback);
        itemTouchHelper.attachToRecyclerView(binding.recycleViewEditMode);
    }

    private void updateRecycleView(){
        adapter.notifyDataSetChanged();
    }

    private void deleteRecyclerRow(int row){
        if (entryType.get(row).equals(KeyConstants.BREAK)){
            entries.remove(row);
            breakValues.remove(row);
        } else {
            entries.remove(row);
        }
        entryType.remove(row);
        Timber.d("Recycler row removed");
        updateRecycleView();
    }

    private void editRecyclerRow(){

    }

    /*Retrieves the specified exercises data from the db and
    indexes them into arrayLists, with the entryType list discerning
    between tasks and breaks
     */
    private void loadData(){
        Timber.d("load data called");
        Cursor dataCursor = dbHelper.getExercise(exerciseName);

        if (dataCursor.moveToFirst()){
            do{
                entries.add(dataCursor.getString(1));
                breakValues.add(dataCursor.getInt(2));
                entryType.add(dataCursor.getInt(3));

                Timber.d("entry: " + dataCursor.getString(1) + " type: " + dataCursor.getInt(3));
            }while (dataCursor.moveToNext());
        } else {
            Timber.d("loadData cursor was null");
        }

        dataCursor.close();
    }

    /*Saves the edited data back into
    the db
     */
    private void saveData(){

        dbHelper.saveRoutineEdits(exerciseName, entries, breakValues, entryType);
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

    public void createTaskClicked(View view) {
        TaskDialogFragment.newInstance().show(getFragmentManager(), "TASK_DIALOG_FRAGMENT");
    }

    public void createBreakClicked(View view) {
        BreakDialogFragment.newInstance().show(getFragmentManager(), "BREAK_DIALOG_FRAGMENT");
    }

    public void startExerciseButtonClicked(View view) {
        ActivityOptions transitionActivityOptions = ActivityOptions
                .makeSceneTransitionAnimation(EditModeActivity.this, binding.buttonPlay, getString(R.string.transition_button_play));

        Intent startExercise = new Intent(EditModeActivity.this, RoutineActivity.class);
        startExercise.putExtra(KeyConstants.NAME, exerciseName);
        startActivity(startExercise, transitionActivityOptions.toBundle());
    }

    public void deleteButtonClicked(View view) {
        dbHelper.deleteExercise(exerciseName);
        finish();
    }

    @Override
    public void createTask(String task) {
        entries.add(task);
        breakValues.add(0);
        entryType.add(KeyConstants.TASK);
        updateRecycleView();
    }

    @Override
    public void createBreak(int minutes, int seconds) {
        int totalSeconds = convertToSeconds(minutes, seconds);
        Toast.makeText(EditModeActivity.this, "Minutes :" + String.valueOf(minutes) + " Seconds :" + String.valueOf(seconds), Toast.LENGTH_SHORT).show();
        entries.add("Minutes: " + String.valueOf(minutes) + " Seconds: " + String.valueOf(seconds));
        breakValues.add(totalSeconds);
        entryType.add(KeyConstants.BREAK);
        updateRecycleView();
    }


}
