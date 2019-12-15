package com.example.weather;

import android.content.SharedPreferences;
import android.app.Activity;
import static android.content.Context.MODE_PRIVATE;
import android.os.Parcel;
import android.os.Parcelable;

public class Settings implements Parcelable {
    public boolean humidityEnabled;
    private boolean pressureEnabled;
    private boolean windSpeedEnabled;
    private String city;
    SharedPreferences preferences;

    public Settings(boolean humidity, boolean pressure, boolean windSpeed, String city) {
        this.humidityEnabled = humidity;
        this.pressureEnabled = pressure;
        this.windSpeedEnabled = windSpeed;
        this.city = city;
    }

    protected Settings(Parcel in) {
        humidityEnabled = in.readByte() != 0;
        pressureEnabled = in.readByte() != 0;
        windSpeedEnabled = in.readByte() != 0;
        city = in.readString();
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
        activity.getSharedPreferences("Settings", MODE_PRIVATE).edit().putBoolean("humidity", humidityEnabled).apply();
    }

    public void setPressureEnabled(Activity activity, boolean pressureEnabled) {
        this.pressureEnabled = pressureEnabled;
        activity.getSharedPreferences("Settings", MODE_PRIVATE).edit().putBoolean("pressure", pressureEnabled).apply();
    }

    public void setWindSpeedEnabled(Activity activity, boolean windSpeedEnabled) {
        this.windSpeedEnabled = windSpeedEnabled;
        activity.getSharedPreferences("Settings", MODE_PRIVATE).edit().putBoolean("windSpeed", windSpeedEnabled).apply();
    }

    public void setCity(String city) {
        this.city = city;
    }

    public boolean isHumidityEnabled(Activity activity) {
        return humidityEnabled = activity.getSharedPreferences("Settings", MODE_PRIVATE).getBoolean("humidity", true);
    }

    public boolean isPressureEnabled(Activity activity) {
        return pressureEnabled  = activity.getSharedPreferences("Settings", MODE_PRIVATE).getBoolean("pressure", true);
    }

    public boolean isWindSpeedEnabled(Activity activity) {
        return windSpeedEnabled = activity.getSharedPreferences("Settings", MODE_PRIVATE).getBoolean("windSpeed", true);
    }

    public String getCity() {
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
        parcel.writeString(city);
    }
}