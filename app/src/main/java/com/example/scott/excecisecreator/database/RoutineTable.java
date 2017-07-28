package com.example.scott.excecisecreator.database;

import android.provider.BaseColumns;

/**
 * Created by Scott Quach on 7/27/2017.
 */

public class RoutineTable implements BaseColumns{

    public static final String COLUMN_ENTRY = "entry";
    public static final String COLUMN_TYPE = "type";

    public static String createTable(String condensedName){
        String createTable = "CREATE TABLE IF NOT EXISTS " + condensedName + " (" + _ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_ENTRY + " TEXT, " +
                COLUMN_TYPE + " INTEGER)";
        return createTable;
    }
}
