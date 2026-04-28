package com.example.lostandfound;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "LostFound.db";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, 3);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE items (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "type TEXT," +
                "name TEXT," +
                "phone TEXT," +
                "description TEXT," +
                "category TEXT," +
                "imagePath TEXT," +
                "date TEXT," +
                "createdAt TEXT," +
                "location TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS items");
        onCreate(db);
    }
}