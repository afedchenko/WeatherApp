package com.example.weather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.io.Closeable;
import java.io.IOException;
import java.sql.SQLException;

import com.example.weather.City;

public class DataSource implements Closeable {

    private final DataHelper dbHelper;
    private SQLiteDatabase database;
    private DataReader reader;

    public DataSource(Context context) {
        dbHelper = new DataHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
        reader = new DataReader(database);
        reader.open();
    }

    @Override
    public void close() throws IOException {
        database.close();
        reader.close();
    }

    public City add(String name) {
        City city = new City("Novosibirsk");
        ContentValues values = new ContentValues();
        values.put(DataHelper.TABLE_TITLE, name);
        long id = database.insert(DataHelper.TABLE_NAME, null, values);
        city.setId(id);
        city.setName(name);
        return city;
    }

    public void edit(City city, String title, String desc) {
        ContentValues values = new ContentValues();
        values.put(DataHelper.TABLE_TITLE, title);
        values.put(DataHelper.TABLE_ID, city.getId());

        database.update(DataHelper.TABLE_NAME, values, DataHelper.TABLE_ID + "=" + city.getId(), null);
    }

    public  void delete(City city){
        database.delete(DataHelper.TABLE_NAME, DataHelper.TABLE_ID + "=" + city.getId(), null);
    }

    public void deleteAll() {
        database.delete(DataHelper.TABLE_NAME, null, null);
    }

    public DataReader getReader() {
        return reader;
    }
}
