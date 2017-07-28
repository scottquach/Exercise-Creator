package com.example.scott.excecisecreator.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import timber.log.Timber;

/**
 * Created by scott on 5/18/2017.
 */

public class DataBaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "routines.db";
    public static final int DATABASE_VERSION = 1;

    Context context;

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(MasterTable.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void saveNewRoutine(String fullName) {
        String condensedName = fullName.replaceAll("\\s+", "");

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MasterTable.COLUMN_CONDENSED_NAME, condensedName);
        values.put(MasterTable.COLUMN_FULL_NAME, fullName);
        if (db.insert(MasterTable.TABLE_NAME, null, values) == -1) Timber.e("Error creating new routine reference");
    }

    //ToDo Delete/Clear table and update Master TAble for names
    public void saveExerciseEdits(String tableName, ArrayList<String> entries, ArrayList<Integer> types) {
        SQLiteDatabase db = this.getWritableDatabase();

        //clear table
        db.delete(tableName, null, null);

        ContentValues values = new ContentValues();

        for (int i = 0; i < entries.size(); i++) {
            values.put(RoutineTable.COLUMN_ENTRY, entries.get(i));
            values.put(RoutineTable.COLUMN_TYPE, types.get(i));
            if (db.insert(tableName, null, values) == -1) Timber.e("Error saving edits");

        }

    }

    public String getFullRoutineName(String condensedName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(MasterTable.TABLE_NAME, new String[]{MasterTable.COLUMN_FULL_NAME},
                MasterTable.COLUMN_CONDENSED_NAME + "=" + condensedName, null, null, null, null);

        String fullName = "";
        if (cursor.moveToFirst()) {
            fullName = cursor.getString(0);
            Timber.d("fullName is: " + fullName);
        } else {
            Timber.d("cursor was null");
        }
        return fullName;
    }

    /*Return a cursor containing all of the names
    of exercise tables
     */
    public Cursor getExerciseNames() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(MasterTable.TABLE_NAME, new String[] {MasterTable.COLUMN_FULL_NAME},
                null, null, null, null, null);

        return cursor;
    }

    /*returns a cursor containing the entire table
    of the passed in exercise name tableName.
     */
    public Cursor getExercise(String tableName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(tableName, new String[] {RoutineTable.COLUMN_ENTRY, RoutineTable.COLUMN_TYPE},
                null, null, null, null, null);

        return cursor;
    }

    /*Deletes the table matching with in name with the
    passed in string tableName
     */
    public void deleteExercise(String tableName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE TABLE IF EXISTS " + tableName);
        db.delete(MasterTable.TABLE_NAME, MasterTable.COLUMN_CONDENSED_NAME + "=?", new String[] {tableName});
        Timber.d("table " + tableName + " deleted");
    }

}
