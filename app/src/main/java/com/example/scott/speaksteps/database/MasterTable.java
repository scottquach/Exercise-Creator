package com.example.scott.speaksteps.database;

import android.provider.BaseColumns;

/**
 * Created by Scott Quach on 7/27/2017.
 */

public class MasterTable implements BaseColumns {

    public static final String TABLE_NAME = "master_table";
    public static final String COLUMN_CONDENSED_NAME = "condensed_name";
    public static final String COLUMN_FULL_NAME = "full_name";

    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_CONDENSED_NAME + " TEXT, " +
            COLUMN_FULL_NAME + " TEXT)";
}
