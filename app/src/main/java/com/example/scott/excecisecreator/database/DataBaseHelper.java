package com.example.scott.excecisecreator.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
        Timber.d("full name is" + fullName + " condensed name is " + condensedName);
        if (db.insert(MasterTable.TABLE_NAME, null, values) == -1) {
            Timber.e("Error creating new routine reference");
        } else Timber.d("new routine saved");

        db.execSQL(RoutineTable.createTable(condensedName));
    }

    //ToDo Delete/Clear table and update Master TAble for names
    public void saveRoutineEdits(String tableName, ArrayList<String> entries, ArrayList<Integer> breakValues,
                                 ArrayList<Integer> types) {
        if (!entries.isEmpty()) {
            SQLiteDatabase db = this.getWritableDatabase();
            //clear table
             db.delete(tableName, null, null);
            ContentValues values = new ContentValues();

            for (int i = 0; i < entries.size(); i++) {
                Timber.d("looping through routine edits");
                values.put(RoutineTable.COLUMN_ENTRY, entries.get(i));
                values.put(RoutineTable.COLUMN_BREAK_VALUE, breakValues.get(i));
                values.put(RoutineTable.COLUMN_TYPE, types.get(i));
                Timber.d(entries.get(i));
                Timber.d(breakValues.get(i).toString());
                Timber.d(types.get(i).toString());
                if (db.insert(tableName, null, values) == -1) {
                    Timber.e("Error saving edits");
                } else Timber.d("routine edits saved");
                values.clear();
            }

        }
    }

    public String getFullRoutineName(String condensedName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(MasterTable.TABLE_NAME, new String[]{MasterTable.COLUMN_FULL_NAME}, MasterTable.COLUMN_CONDENSED_NAME + "=?",
                new String[]{condensedName}, null, null, null);

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
        Cursor cursor = db.query(MasterTable.TABLE_NAME, new String[]{MasterTable.COLUMN_FULL_NAME},
                null, null, null, null, null);

        return cursor;
    }

    /*returns a cursor containing the entire table
    of the passed in exercise name tableName.
     */
    public Cursor getExercise(String tableName) {
        Timber.d("getExercise: " + tableName);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(tableName, null,
                null, null, null, null, null);

        return cursor;
    }

    /*Deletes the table matching with in name with the
    passed in string tableName
     */
    public void deleteExercise(String tableName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + tableName);
        db.delete(MasterTable.TABLE_NAME, MasterTable.COLUMN_CONDENSED_NAME + "=?", new String[]{tableName});
        Timber.d("table " + tableName + " deleted");
    }

}
