package com.example.weather;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;

import static android.content.Context.MODE_PRIVATE;

public class Settings implements Parcelable {
    public boolean humidityEnabled;
    private boolean pressureEnabled;
    private boolean windSpeedEnabled;
    private City city;
    private String SHARED_SETTINGS = "SHARED_SETTINGS";
    private String HUMIDITY_NAME = "HUMIDITY_NAME";
    private String PRESSURE_NAME = "PRESSURE_NAME";
    private String WIND_SPEED_NAME = "WIND_SPEED_NAME";

    public Settings(Activity activity, String defaultCityName) {
        this.humidityEnabled = getSharedPreferences(activity).getBoolean(HUMIDITY_NAME, true);
        this.pressureEnabled = getSharedPreferences(activity).getBoolean(PRESSURE_NAME, true);
        this.windSpeedEnabled = getSharedPreferences(activity).getBoolean(WIND_SPEED_NAME, true);
        city = new City(defaultCityName);
    }

    protected Settings(Parcel in) {
        humidityEnabled = in.readByte() != 0;
        pressureEnabled = in.readByte() != 0;
        windSpeedEnabled = in.readByte() != 0;
        city = in.readParcelable(City.class.getClassLoader());
    }

    public static final Creator<Settings> CREATOR = new Creator<Settings>() {
        @Override
        public Settings createFromParcel(Parcel in) {
            return new Settings(in);
        }

        @Override
        public Settings[] newArray(int size) {
            return new Settings[size];
        }
    };

    public void setHumidityEnabled(Activity activity, boolean humidityEnabled) {
        this.humidityEnabled = humidityEnabled;
        getSharedPreferences(activity).edit().putBoolean(HUMIDITY_NAME, humidityEnabled).apply();
    }

    public void setPressureEnabled(Activity activity, boolean pressureEnabled) {
        this.pressureEnabled = pressureEnabled;
        getSharedPreferences(activity).edit().putBoolean(PRESSURE_NAME, pressureEnabled).apply();
    }

    public void setWindSpeedEnabled(Activity activity, boolean windSpeedEnabled) {
        this.windSpeedEnabled = windSpeedEnabled;
        getSharedPreferences(activity).edit().putBoolean(WIND_SPEED_NAME, windSpeedEnabled).apply();
    }

    public void setCity(City city) {
        this.city = city;
    }

    public boolean isHumidityEnabled() {
        return humidityEnabled;
    }

    public boolean isPressureEnabled() {
        return pressureEnabled;
    }

    public boolean isWindSpeedEnabled() {
        return windSpeedEnabled;
    }

    private SharedPreferences getSharedPreferences(Activity activity) {
        return activity.getSharedPreferences(SHARED_SETTINGS, MODE_PRIVATE);
    }

    public City getCity() {
        return city;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeByte((byte) (humidityEnabled ? 1 : 0));
        parcel.writeByte((byte) (pressureEnabled ? 1 : 0));
        parcel.writeByte((byte) (windSpeedEnabled ? 1 : 0));
        parcel.writeParcelable(city, 0);
    }
}