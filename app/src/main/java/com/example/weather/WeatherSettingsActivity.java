package com.example.weather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class WeatherSettingsActivity extends Activity {
    private EditText cityName;
    private Switch humidity, pressure, windSpeed;
    private Button backButton;
    private RadioButton moscow, saintPetersburg, other;
    private String cityNameValue;

    //Теги
    private static final String TAG = "WeatherSettingsActivity";
    public static final String HUMIDITY = "WeatherSettingsActivityHumidity";
    public static final String PRESSURE = "WeatherSettingsActivityPressure";
    public static final String WIND_SPEED = "WeatherSettingsActivityWindSpeed";
    public static final String CITY_NAME = "WeatherSettingsCityName";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showLog("OnCreate");
        setContentView(R.layout.weather_settings);

        humidity = findViewById(R.id.weather_settings_humidity);
        pressure = findViewById(R.id.weather_settings_pressure);
        windSpeed = findViewById(R.id.weather_settings_wind_speed);
        backButton = findViewById(R.id.weather_settings_button_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickOnBackButton();
            }
        });

        cityName = findViewById(R.id.weather_settings_city_other_change);
        moscow = findViewById(R.id.weather_settings_city_moscow);
        saintPetersburg = findViewById(R.id.weather_settings_city_saint_petersburg);
        other = findViewById(R.id.weather_settings_city_other);
        restoreData(savedInstanceState);
        cityNameToActivityMain();
        getDataFromMain();
    }


    //По аппаратной кнопке "Назад" делаем всё то же, что и по кнопке "Back"
    @Override
    public void onBackPressed() {
        clickOnBackButton();
    }

    //Если otherCityCheckValue вернул true, то можем выходить назад в Main
    private void clickOnBackButton() {
        if (otherCityCheckValue()) {
            prepareResult();
            finish();
        }
    }

    // Определяем, какой город нужно отправить в activityMain
    public String cityNameToActivityMain() {
        if (other.isChecked() && !cityName.getText().toString().equals("")) {
            return cityNameValue = cityName.getText().toString();
        } else if (moscow.isChecked()) {
            return cityNameValue = getString(R.string.moscow);
        } else if (saintPetersburg.isChecked()) {
            return cityNameValue = getString(R.string.saint_petersburg);
        } else return getString(R.string.city_name_main_screen);
    }

    //Получаем данные с activityMain
    private void getDataFromMain() {
        humidity.setChecked(getIntent().getBooleanExtra(HUMIDITY, false));
        pressure.setChecked(getIntent().getBooleanExtra(PRESSURE, false));
        windSpeed.setChecked(getIntent().getBooleanExtra(WIND_SPEED, false));

        if (getIntent().getStringExtra(CITY_NAME).equals(getString(R.string.moscow))) {
            cityName.setText("");
            moscow.setChecked(true);
        } else if (getIntent().getStringExtra(CITY_NAME).equals(getString(R.string.saint_petersburg))) {
            cityName.setText("");
            saintPetersburg.setChecked(true);
        } else {
            other.setChecked(true);
            cityName.setText(getIntent().getStringExtra(CITY_NAME));
        }
    }

    //Подготавливаем данные для отправки в activityMain
    private void prepareResult() {
        Intent intent = new Intent();
        intent.putExtra(PRESSURE, pressure.isChecked());
        intent.putExtra(HUMIDITY, humidity.isChecked());
        intent.putExtra(WIND_SPEED, windSpeed.isChecked());
        intent.putExtra(CITY_NAME, cityNameToActivityMain());
        setResult(RESULT_OK, intent);
    }

    //Проверяем, если выбран Other, но не заполнен EditText, то возвращаем false
    public boolean otherCityCheckValue() {
        if (other.isChecked() & cityName.getText().toString().equals("")) {
            Toast.makeText(this, "Выберите город", Toast.LENGTH_SHORT).show();
            return false;
        } else return true;
    }

    private void restoreData(Bundle savedInstanceState) {
        if (savedInstanceState == null) return;
        cityName.setText(savedInstanceState.getString(CITY_NAME, getString(R.string.novosibirsk)));
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(CITY_NAME, cityName.getText().toString());
    }

    //Выводим логи по событиям активностей

    @Override
    protected void onStart() {
        super.onStart();
        showLog("onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        showLog("onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        showLog("onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        showLog("onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        showLog("onDestroy");
    }

    private void showLog(String logMessage) {
        Log.d(TAG, logMessage);
        //Toast.makeText(getApplicationContext(), logMessage, Toast.LENGTH_SHORT).show();
    }
}
