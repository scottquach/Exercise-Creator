package com.example.scott.excecisecreator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class EditModeActivity extends AppCompatActivity {

    private String exerciseName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_mode);

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            exerciseName = extras.getString("name");
        }else{
            Toast.makeText(this, "Error creating new exercise", Toast.LENGTH_SHORT).show();
            Intent exitToHome = new Intent(EditModeActivity.this, StartMenuActivity.class);
            startActivity(exitToHome);
        }

        getSupportActionBar().setTitle("Edit Mode (tb beatified)");
    }

}
