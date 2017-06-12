package com.example.scott.excecisecreator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;
import android.widget.Toast;

/**
 * Created by scott on 5/18/2017.
 */

public class DataBaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "exercises.db";
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_MASTER = "exerciseNames";

    Context context;

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + DATABASE_MASTER + " (id INTEGER PRIMARY KEY AUTOINCREMENT," +
                " exerciseName TEXT NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void saveNewExercise(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("exerciseName", name);
        long newRow = db.insert(DATABASE_MASTER, null, values);
        if (newRow == -1){
            Toast.makeText(context, "Error saving data", Toast.LENGTH_SHORT).show();
        }

        db.execSQL("CREATE TABLE " + name + " (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "task TEXT, break INTEGER)");

    }

    public void saveNewTask(){

    }

    public void saveNewBreak(){

    }

    public Cursor getExerciseNames(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT exerciseName FROM " + DATABASE_MASTER,null);
        return cursor;
    }

    public Cursor getExerciseTask(String tableName){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT task FROM " + tableName, null);
        return cursor;
    }

    public Cursor getExerciseBreak(String tableName){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT break FROM " + tableName, null);
        return cursor;
    }
}
