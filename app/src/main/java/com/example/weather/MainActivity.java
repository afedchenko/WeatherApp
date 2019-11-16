package com.example.weather;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private final int weatherSettingsActivityResultCode = 7;
    private boolean humidityEnabled = true;
    private boolean pressureEnabled = true;
    private boolean windSpeedEnabled = true;
    private LinearLayout humidity, pressure, windSpeed;
    private Button browser, settings;
    private TextView currentCity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showLog("OnCreate");
        setContentView(R.layout.activity_main);

        //Инициализируем view кнопку настроек и открываем по ней экран настроек
        settings = findViewById(R.id.activity_main_button_setings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickOnSettingsButton();
            }
        });

        //Инициализируем view кнопку браузера и открываем по ней браузер
        browser = findViewById(R.id.activity_main_button_browser);
        browser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openBrowser = new Intent(Intent.ACTION_VIEW, Uri.parse("https://pogoda.ngs.ru/"));
                startActivity(openBrowser);
            }
        });

        //Инициализируем view параметров погоды
        humidity = findViewById(R.id.activity_main_linear_layout_humidity);
        pressure = findViewById(R.id.activity_main_linear_layuot_pressure);
        windSpeed = findViewById(R.id.activity_main_linear_layout_wind_speed);
        currentCity = findViewById(R.id.activity_main_city_current);
    }

    //Подготавливаем данные для отправки в Settings
    private void clickOnSettingsButton() {
        Intent intent = new Intent(this, WeatherSettingsActivity.class);
        intent.putExtra(WeatherSettingsActivity.PRESSURE, pressureEnabled);
        intent.putExtra(WeatherSettingsActivity.HUMIDITY, humidityEnabled);
        intent.putExtra(WeatherSettingsActivity.WIND_SPEED, windSpeedEnabled);
        intent.putExtra(WeatherSettingsActivity.CITY_NAME, currentCity.getText().toString());
        startActivityForResult(intent, weatherSettingsActivityResultCode);
    }

    //Проверяем результат интента, если ОК, то обновляемся, если не ок, задаем дефолтное значение города
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && requestCode == weatherSettingsActivityResultCode) {
            updateWeatherParams(data);
        }
    }

    //Получаем данные с weatherSettingsActivity
    private void updateWeatherParams(@Nullable Intent data) {
        humidityEnabled = data.getBooleanExtra(WeatherSettingsActivity.HUMIDITY, false);
        pressureEnabled = data.getBooleanExtra(WeatherSettingsActivity.PRESSURE, false);
        windSpeedEnabled = data.getBooleanExtra(WeatherSettingsActivity.WIND_SPEED, false);
        currentCity.setText(data.getStringExtra(WeatherSettingsActivity.CITY_NAME));
        checkSwitchValues();
    }

    //Проверяем все пришедшие значения свитчей сеттингов
    private void checkSwitchValues() {
        changeVisibilityView(humidityEnabled, humidity);
        changeVisibilityView(pressureEnabled, pressure);
        changeVisibilityView(windSpeedEnabled, windSpeed);
    }

    //Проверяем, что нам пришло из настроек свичей, и в зависимости от true/false скрываем или показываем view
    private void changeVisibilityView(boolean isChecked, LinearLayout layout) {
        if (isChecked) {
            layout.setVisibility(View.VISIBLE);
        } else layout.setVisibility(View.GONE);
    }

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