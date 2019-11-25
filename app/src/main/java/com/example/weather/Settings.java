package com.example.weather;

import android.os.Parcel;
import android.os.Parcelable;

public class Settings implements Parcelable {
    private boolean humidityEnabled;
    private boolean pressureEnabled;
    private boolean windSpeedEnabled;
    private String city;

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

    public void setHumidityEnabled(boolean humidityEnabled) {
        this.humidityEnabled = humidityEnabled;
    }

    public void setPressureEnabled(boolean pressureEnabled) {
        this.pressureEnabled = pressureEnabled;
    }

    public void setWindSpeedEnabled(boolean windSpeedEnabled) {
        this.windSpeedEnabled = windSpeedEnabled;
    }

    public void setCity(String city) {
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