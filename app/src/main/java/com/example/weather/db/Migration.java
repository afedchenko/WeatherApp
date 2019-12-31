package com.example.weather.db;

import android.database.sqlite.SQLiteDatabase;

interface Migration {
    boolean migrate(SQLiteDatabase db);
}