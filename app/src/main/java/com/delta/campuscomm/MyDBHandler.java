package com.delta.campuscomm;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.content.Context;
import android.content.ContentValues;

import org.json.JSONException;
import org.json.JSONObject;

public class MyDBHandler extends SQLiteOpenHelper {
    Context cont;

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "testgcm1.db";
    public static final String COLUMN_ID = "_id";
    public static final String TABLE = "Posts";
    public static final String COLUMN_POST = "post";

    public MyDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
        cont = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_POST + " TEXT " +
                ");";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    //Add a new row to the database
    public void add(String jsonString) {
        String id = "";
        try {
            JSONObject jsonEntry = new JSONObject(jsonString);
            id = jsonEntry.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ContentValues values = new ContentValues();
        values.put(COLUMN_POST, jsonString);
        values.put(COLUMN_ID, Integer.parseInt(id));
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE, null, values);
        db.close();
    }

    public SQLiteDatabase getDB() {
        return getWritableDatabase();
    }

    //keeps only latest local N messages and deletes rest
    public void limitTabletoN(Integer N){
        SQLiteDatabase db = getWritableDatabase();

        String query = "SELECT * FROM " + TABLE + " WHERE 1 ORDER BY " + COLUMN_ID + " DESC;";
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        Integer counter = 0;

        //Iterate from latest message down by N messages and deletes the rest
        while (!cursor.isAfterLast()) {
            if (cursor.getString(cursor.getColumnIndex(COLUMN_POST)) != null) {
                counter++;
                if (counter == N) {
                    counter = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                    query = "DELETE FROM " + TABLE + " WHERE "+ COLUMN_ID + " < " + counter + ";";
                    db.execSQL(query);
                    break;
                }
            }
            cursor.moveToNext();
        }

        db.close();
    }

}
