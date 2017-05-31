package com.example.scott.excecisecreator;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class EditModeActivity extends AppCompatActivity {

    private String exerciseName;
    private RecyclerView recyclerview;
    private ArrayList<String> entries = new ArrayList<>();
    private DataBaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_mode);

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

        getSupportActionBar().setTitle(exerciseName);

        setUpRecycleView();
    }

    private void setUpRecycleView(){
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerview.setLayoutManager(layoutManager);

        RecyclerAdapter adapter = new RecyclerAdapter(entries, this);
        recyclerview.setAdapter(adapter);
    }

    private void loadData(){
        Cursor tasks = dbHelper.getExerciseTask(exerciseName);
        Cursor breaks = dbHelper.getExerciseBreak(exerciseName);

        //// TODO: 5/30/2017 populate an arraylist based of the nulls of tasks and breaks
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

    private void createPause(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create Pause");

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    public void createPauseClicked(View view) {
        createPause();
    }

    public void createTaskClicked(View view) {
        createTask();
    }
}
