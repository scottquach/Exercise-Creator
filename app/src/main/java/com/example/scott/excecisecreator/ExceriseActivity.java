package com.example.scott.excecisecreator;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class ExceriseActivity extends AppCompatActivity {

    private String exerciseName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excerise);

        /* get the name of the exercise to load from
        the intent extras
         */
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            exerciseName = bundle.getString("name");
        }else{
            Toast.makeText(this, "Error creating new exercise", Toast.LENGTH_SHORT).show();
            Intent exitToHome = new Intent(ExceriseActivity.this, StartMenuActivity.class);
            startActivity(exitToHome);
        }
    }

}
