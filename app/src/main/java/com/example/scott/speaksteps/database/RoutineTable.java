package com.example.scott.speaksteps.database;

import android.provider.BaseColumns;

/**
 * Created by Scott Quach on 7/27/2017.
 */

public class RoutineTable implements BaseColumns{

    public static final String COLUMN_ENTRY = "entry";
    public static final String COLUMN_BREAK_VALUE = "break_value";
    public static final String COLUMN_TYPE = "type";

    public static String createTable(String condensedName){
        String createTable = "CREATE TABLE IF NOT EXISTS " + condensedName + " (" + _ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_ENTRY + " TEXT, " +
                COLUMN_BREAK_VALUE + " INTEGER, " + COLUMN_TYPE + " INTEGER)";
        return createTable;
    }
}
