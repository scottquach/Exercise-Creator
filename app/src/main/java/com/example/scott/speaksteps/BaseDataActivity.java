package com.example.scott.speaksteps;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.scott.speaksteps.database.DataBaseHelper;

/**
 * Created by Scott Quach on 7/26/2017.
 */

public class BaseDataActivity extends AppCompatActivity{

    protected DataBaseHelper dbHelper;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new DataBaseHelper(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
    }
}
