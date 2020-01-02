package com.example.weather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.List;

public class DataHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "cities.db";
    private static final int DB_VERSION = 2;
    public static final String TABLE_NAME = "cities";
    public static final String TABLE_ID = "_id";
    public static final String TABLE_TITLE = "title";
    List<Migration> migrations;

    public DataHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME + " (" + TABLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + TABLE_TITLE + " TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if(oldVersion == 1 && newVersion == 2) {
            String upgradeStr = "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + TABLE_TITLE +  " TEXT DEFAULT Title";
            sqLiteDatabase.execSQL(upgradeStr);
        }
    }
}
