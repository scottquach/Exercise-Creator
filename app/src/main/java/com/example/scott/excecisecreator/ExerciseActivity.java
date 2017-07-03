package com.example.scott.excecisecreator;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ExerciseActivity extends AppCompatActivity {

    private DataBaseHelper db;

    @BindView(R.id.exerciseRecycleView) RecyclerView recyclerView;
    private RecyclerAdapter adapter;

    private String exerciseName;

    private ArrayList<String> entries = new ArrayList<String>();
    //0 represents a task, 1 represents a break
    private ArrayList<Integer> types = new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        //initialization
        db = new DataBaseHelper(this);
        ButterKnife.bind(this);

        //get exercise name to load
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            exerciseName = bundle.getString("name");
        }else{
            Toast.makeText(this, R.string.error_loading_exercise, Toast.LENGTH_SHORT).show();
            Intent exitToHome = new Intent(ExerciseActivity.this, StartMenuActivity.class);
            startActivity(exitToHome);
        }

        if (getSupportActionBar() != null) getSupportActionBar().setTitle(exerciseName);


        loadData();
        setUpRecycleView();
    }

    /*Setup up layout manager and adapter for the
    recycle view, uses the data set entries ArrayList
     */
    private void setUpRecycleView(){
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecyclerAdapter(entries, this);
        recyclerView.setAdapter(adapter);
    }

    /*Notifies the adapter to update
    the recycleview with the edited content
     */
    private void updateRecycleView(){
        adapter.notifyDataSetChanged();
    }

    /*fill the ArrayLists entries and types with the appropriate
    values from the db table named exerciseName
     */
    private void loadData(){
        Cursor dataCursor = db.getExercise(exerciseName);

        if (dataCursor.moveToFirst()){
            do{
                String entry = dataCursor.getString(1);
                int type = dataCursor.getInt(2);

                entries.add(entry);
                types.add(type);
            }while (dataCursor.moveToNext());
        }
    }

    public void playButtonClicked(View view) {

    }
}
