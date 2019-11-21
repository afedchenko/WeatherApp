package com.example.weather;

public class Settings {
    private boolean humidityEnabled = true;
    private boolean pressureEnabled = true;
    private boolean windSpeedEnabled = true;
    private String city;

    public Settings(boolean humidity, boolean pressure, boolean windSpeed, String city) {
        this.humidityEnabled = humidity;
        this.pressureEnabled = pressure;
        this.windSpeedEnabled = windSpeed;
        this.city = city;
    }

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
}