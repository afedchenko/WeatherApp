package com.example.weather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.weather.City;

import java.io.Closeable;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.SQLOutput;

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


    //Читаю всю колонку городов и если нахожу совпадающий вариант, возвращаю false
    public boolean searchCityInTable(String city) {
        String query = "SELECT " + DataHelper.TABLE_ID + ", " + DataHelper.TABLE_TITLE + " FROM " + DataHelper.TABLE_NAME;
        Cursor cursor = database.rawQuery(query, null);
        cursor.moveToFirst();
        String cityName;
        while (!cursor.isAfterLast()) {
            cityName = cursor.getString(cursor.getColumnIndex(DataHelper.TABLE_TITLE));
            if (cityName.equals(city)) {
                return false;
            }
            cursor.moveToNext();
        }
        cursor.close();
        return true;
    }

    public void edit(City city, String title, String desc) {
        ContentValues values = new ContentValues();
        values.put(DataHelper.TABLE_TITLE, title);
        values.put(DataHelper.TABLE_ID, city.getId());
        database.update(DataHelper.TABLE_NAME, values, DataHelper.TABLE_ID + "=" + city.getId(), null);
    }

    public void delete(City city) {
        database.delete(DataHelper.TABLE_NAME, DataHelper.TABLE_ID + "=" + city.getId(), null);
    }

    public void deleteAll() {
        database.delete(DataHelper.TABLE_NAME, null, null);
    }

    public DataReader getReader() {
        return reader;
    }
}
