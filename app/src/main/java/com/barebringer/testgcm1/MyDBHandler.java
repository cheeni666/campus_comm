package com.barebringer.testgcm1;

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
    public static final String COLUMN_NAME = "post";

    public MyDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
        cont = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + "posts" + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_NAME + " TEXT " +
                ");";
        db.execSQL(query);
        query = "CREATE TABLE " + "fposts" + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_NAME + " TEXT " +
                ");";
        db.execSQL(query);
        query = "CREATE TABLE " + "dposts" + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_NAME + " TEXT " +
                ");";
        db.execSQL(query);
    }

    //Add a new row to the database
    public void addName(String json, String table) {
        String id = "";
        try {
            JSONObject js = new JSONObject(json);
            id = js.getString("msg_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, json);
        values.put(COLUMN_ID, Integer.parseInt(id));
        SQLiteDatabase db = getWritableDatabase();
        db.insert(table, null, values);
        db.close();
    }

    public SQLiteDatabase getDB() {
        return getWritableDatabase();
    }

    //keeps only latest local N messages and deletes rest
    public void limitTabletoN(String table,Integer N){
        SQLiteDatabase db = getWritableDatabase();

        String query = "SELECT * FROM " + table + " WHERE 1 ORDER BY " + "_id" + " DESC;";
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        Integer lat_id = 0;

        //Iterate from latest message down by N messages and deletes the rest
        while (!c.isAfterLast()) {
            if (c.getString(c.getColumnIndex("post")) != null) {
                lat_id++;
                if (lat_id == N) {
                    lat_id = c.getInt(c.getColumnIndex("_id"));
                    query = "DELETE FROM " + table + " WHERE _id < " + lat_id + ";";
                    db.execSQL(query);
                    break;
                }
            }
            c.moveToNext();
        }

        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
