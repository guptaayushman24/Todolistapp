package com.example.todolistapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "TODOLIST";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_CART = "todolist";

    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_TIME = "time";


    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_CART + "(" +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_NAME + " TEXT, " +
                KEY_TIME + " TEXT" +
                ")");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART);
        onCreate(db);
    }

    // Adding the item in the database
    public void addItem(String name, String time) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_TIME, time);
        database.insert(TABLE_CART, null, values);

    }


    // Fetching all items from the database
    public Cursor viewData() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_CART;
        return db.rawQuery(query, null);
    }

    // Deleting the item from the database
    public void deleteItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CART, KEY_ID + "=?", new String[]{String.valueOf(id)});

    }

    public void deleteAlldata() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CART, null, null);

    }

    public void updateTime(int itemId, String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TIME, time);
        db.update(TABLE_CART, values, KEY_ID + "=?", new String[]{String.valueOf(itemId)});

    }


}
