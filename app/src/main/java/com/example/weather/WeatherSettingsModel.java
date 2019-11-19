package com.example.weather;

public class WeatherSettingsModel {
    private static final String DEFAULT_CITY_NAME = "Новосибирск";
    private String cityName = DEFAULT_CITY_NAME;
    private boolean windSpeedEnabled = true;
    private boolean pressureEnabled = true;
    private boolean humidityEnabled = true;

    private static WeatherSettingsModel instance;

    static WeatherSettingsModel getInstance() {
        instance = instance == null? new WeatherSettingsModel() : instance;
        return instance;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    boolean isWindSpeedEnabled() {
        return windSpeedEnabled;
    }

    public void setWindSpeedEnabled(boolean windSpeedEnabled) {
        this.windSpeedEnabled = windSpeedEnabled;
    }

    boolean isPressureEnabled() {
        return pressureEnabled;
    }

    public void setPressureEnabled(boolean pressureEnabled) {
        this.pressureEnabled = pressureEnabled;
    }

    boolean isHumidityEnabled() {
        return humidityEnabled;
    }

    public void setHumidityEnabled(boolean humidityEnabled) {
        this.humidityEnabled = humidityEnabled;
    }
}
