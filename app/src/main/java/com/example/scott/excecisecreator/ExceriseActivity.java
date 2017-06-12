package com.example.scott.excecisecreator;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;

public class ExceriseActivity extends AppCompatActivity {

    private DataBaseHelper db;

    private RecyclerView recycleView;
    private RecyclerAdapter adapter;

    private String exerciseName;

    private ArrayList<String> entries = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excerise);

        //initialization
        db = new DataBaseHelper(this);
        recycleView = (RecyclerView) findViewById(R.id.exerciseRecycleView);

        //get exercise name to load
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            exerciseName = bundle.getString("name");
        }else{
            Toast.makeText(this, "Error creating new exercise", Toast.LENGTH_SHORT).show();
            Intent exitToHome = new Intent(ExceriseActivity.this, StartMenuActivity.class);
            startActivity(exitToHome);
        }

        getSupportActionBar().setTitle(exerciseName);

        loadData();
        setUpRecycleView();
    }

    /*Setup up layout manager and adapter for the
    recycle view, uses the data set entries ArrayList
     */
    private void setUpRecycleView(){
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recycleView.setLayoutManager(layoutManager);
        adapter = new RecyclerAdapter(entries, this);
        recycleView.setAdapter(adapter);
    }

    /*Notifies the adapter to update
    the recycleview with the edited content
     */
    private void updateRecycleView(){
        adapter.notifyDataSetChanged();
    }

    private void loadData(){
        Cursor dataCursor = db.getExercise(exerciseName);

        if (dataCursor.moveToFirst()){
            do{
                String entry = dataCursor.getString(1);
                int type = dataCursor.getInt(2);


            }while (dataCursor.moveToNext());
        }
    }

}
