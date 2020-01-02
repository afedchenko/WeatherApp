package com.example.weather.db;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.Closeable;
import java.io.IOException;

import com.example.weather.City;

public class DataReader implements Closeable {
    private final SQLiteDatabase database;
    private Cursor cursor;
    private String[] allColumn = {
            DataHelper.TABLE_ID,
            DataHelper.TABLE_TITLE
    };

    public DataReader(SQLiteDatabase database) {
        this.database = database;
    }

    public void open(){
        query();
        cursor.moveToFirst();
    }

    private void query() {
        cursor = database.query(DataHelper.TABLE_NAME, allColumn,
                null,
                null,
                null,
                null,
                null);
    }

    public void refresh() {
        int pos = cursor.getPosition();
        query();
        cursor.moveToPosition(pos);
    }

    @Override
    public void close() throws IOException {
        cursor.close();
    }

    private City cursorToNote(){
        City city = new City("Novosibirsk");
        city.setId(cursor.getLong(0));
        city.setName(cursor.getString(1));
        return city;
    }

    public City getPosition(int position) {
        cursor.moveToPosition(position);
        return cursorToNote();
    }

    public int getCount(){
        return cursor.getCount();
    }
}