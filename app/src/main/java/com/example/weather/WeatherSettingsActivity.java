package com.example.weather;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class WeatherSettingsActivity extends AppCompatActivity {
    private TextView cityName;
    private Switch humidity, pressure, windSpeed;
    private Button backButton;
    private ViewGroup parantViewCityList;
    private RadioButton moscow, saintPetersburg, other;

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
        parantViewCityList = findViewById(R.id.weather_settings_city_list);

        loadFromSettingsModel();


/*        moscow = findViewById(R.id.weather_settings_city_moscow);
        saintPetersburg = findViewById(R.id.weather_settings_city_saint_petersburg);
        other = findViewById(R.id.weather_settings_city_other);*/
        restoreData(savedInstanceState);
    }

    //По аппаратной кнопке "Назад" делаем всё то же, что и по кнопке "Back"
    @Override
    public void onBackPressed() {
        clickOnBackButton();
    }

    //Если otherCityCheckValue вернул true, то можем выходить назад в Main
    private void clickOnBackButton() {
//        if (otherCityCheckValue()) {
            saveSettingsToModel();
            prepareResult();
            finish();
//        }
    }

    // Определяем, какой город нужно отправить в activityMain
/*    public String cityNameToActivityMain() {
        if (other.isChecked() && !cityName.getText().toString().equals("")) {
            return cityName.getText().toString();
        } else if (moscow.isChecked()) {
            return getString(R.string.moscow);
        } else if (saintPetersburg.isChecked()) {
            return getString(R.string.saint_petersburg);
        } else return getString(R.string.city_name_main_screen);
    }*/

    //Получаем данные из модели настроек

    private void loadFromSettingsModel() {
        humidity.setChecked(WeatherSettingsModel.getInstance().isHumidityEnabled());
        pressure.setChecked(WeatherSettingsModel.getInstance().isPressureEnabled());
        windSpeed.setChecked(WeatherSettingsModel.getInstance().isWindSpeedEnabled());
        parantViewCityList = findViewById(R.id.weather_settings_city_list);
        String[] cities = getResources().getStringArray(R.array.city_name);
        LayoutInflater layoutInflater = getLayoutInflater();
        for (String i : cities) {
            View cityItem = layoutInflater.inflate(R.layout.city_item, parantViewCityList, false);
            ((TextView)cityItem.findViewById(R.id.city_item_label)).setText(ResourceUtils.getLocalizedStringResource(this, i));
            parantViewCityList.addView(cityItem);
        }
    }

    private void saveSettingsToModel() {
        WeatherSettingsModel.getInstance().setHumidityEnabled(humidity.isChecked());
        WeatherSettingsModel.getInstance().setPressureEnabled(pressure.isChecked());
        WeatherSettingsModel.getInstance().setWindSpeedEnabled(windSpeed.isChecked());
    }


    //Получаем данные с activityMain
    private void getDataFromMain() {
/*        if (Objects.equals(getIntent().getStringExtra(CITY_NAME), getString(R.string.moscow))) {
            cityName.setText("");
            moscow.setChecked(true);
        } else if (Objects.equals(getIntent().getStringExtra(CITY_NAME), getString(R.string.saint_petersburg))) {
            cityName.setText("");
            saintPetersburg.setChecked(true);
        } else {
            other.setChecked(true);
            cityName.setText(getIntent().getStringExtra(CITY_NAME));
        }*/
    }

    //Подготавливаем данные для отправки в activityMain
    private void prepareResult() {
        Intent intent = new Intent();
//        intent.putExtra(CITY_NAME, cityNameToActivityMain());
        setResult(RESULT_OK, intent);
    }

    //Проверяем, если выбран Other, но не заполнен EditText, то возвращаем false
/*    public boolean otherCityCheckValue() {
        if (other.isChecked() & cityName.getText().toString().equals("")) {
            Toast.makeText(this, "Выберите город", Toast.LENGTH_SHORT).show();
            return false;
        } else return true;
    }*/

    private void restoreData(Bundle savedInstanceState) {
        if (savedInstanceState == null) return;
//        cityName.setText(savedInstanceState.getString(CITY_NAME, getString(R.string.novosibirsk)));
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
//        outState.putString(CITY_NAME, cityName.getText().toString());
        saveSettingsToModel();
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