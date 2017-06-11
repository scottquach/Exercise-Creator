package com.example.scott.excecisecreator;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import java.util.ArrayList;

public class EditModeActivity extends AppCompatActivity {

    private String exerciseName;

    private RecyclerView recyclerview;
    private RecyclerAdapter adapter;

    private ArrayList<String> entries = new ArrayList<>();

    //0 represents a task, and 1 represents a break
    private ArrayList<Integer> entryType = new ArrayList<Integer>();

    private DataBaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_mode);

        //dummy entries
        entries.add(0,"Hello");
        entries.add(1, "GoodBye");
        entries.add(2, "JK LOL");

        //initialization
        recyclerview = (RecyclerView) findViewById(R.id.editExerciseRecycleView);
        dbHelper = new DataBaseHelper(this);

        //Get exercise name to be edited
        Bundle extras = getIntent().getExtras();
        if (extras != null){
            exerciseName = extras.getString("name");
        }else{
            Toast.makeText(this, "Error creating new exercise", Toast.LENGTH_SHORT).show();
            Intent exitToHome = new Intent(EditModeActivity.this, StartMenuActivity.class);
            startActivity(exitToHome);
        }

        //changes action bar name based on exerciseName
        getSupportActionBar().setTitle(exerciseName);

        setUpRecycleView();
    }

    private void setUpRecycleView(){
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerview.setLayoutManager(layoutManager);

        adapter = new RecyclerAdapter(entries, this);
        recyclerview.setAdapter(adapter);
    }

    private void updateRecycleView(){
        adapter.notifyDataSetChanged();
    }

    /*Retrives the specified exercises data from the db and
    indexes them into arrayLists, with the entryType list discerning
    between tasks and breaks
     */
    private void loadData(){
        Cursor tasks = dbHelper.getExerciseTask(exerciseName);
        Cursor breaks = dbHelper.getExerciseBreak(exerciseName);


        if (tasks.moveToFirst()){

        }else{
            Log.d("debug", "error loading tasks cursor");
        }

        if (breaks.moveToFirst()){

        }else{
            Log.d("debug", "error loading break cursor");
        }

        //// TODO: 5/30/2017 populate an arraylist based of the nulls of tasks and breaks
        ArrayList<String> contentOrder = new ArrayList<String>();
    }


    private void createTask(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New Task");

        final EditText input = (EditText) new EditText(this);
        input.setHint("Task Name");
        builder.setView(input);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String task = input.getText().toString();
                Toast.makeText(EditModeActivity.this, task, Toast.LENGTH_SHORT).show();
                entries.add(task);
                entryType.add(0);
                updateRecycleView();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();



    }

    // TODO: 6/11/2017 trying to create a break freezes the phone

    private void createBreak(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create Break");

        builder.setView(R.layout.dialog_new_break);

        final NumberPicker minuteNP = (NumberPicker) findViewById(R.id.minutePicker);
        final NumberPicker secondNP = (NumberPicker) findViewById(R.id.secondPicker);
//        minuteNP.setMinValue(0);
//        minuteNP.setMaxValue(60);
//        secondNP.setMinValue(1);
//        minuteNP.setMaxValue(59);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int minutes = minuteNP.getValue();
                int seconds= secondNP.getValue();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    public void createBreakClicked(View view) {
        createBreak();
    }

    public void createTaskClicked(View view) {
        createTask();
    }
}
