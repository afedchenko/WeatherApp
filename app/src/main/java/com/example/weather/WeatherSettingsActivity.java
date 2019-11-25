package com.example.weather;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.regex.Pattern;

public class WeatherSettingsActivity extends AppCompatActivity {
    private Switch humidity, pressure, windSpeed;
    private Button backButton;
    private RadioButton moscow, saintPetersburg, other;
    private RecyclerView recyclerView;
    private TextInputEditText inputCityName;
    Settings weatherSettings;

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
        initViews();
        loadSettings();
        restoreData(savedInstanceState);
        validateCityName();
    }

    private void initViews() {
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
        recyclerView = findViewById(R.id.weather_settings_recycler_view);
        inputCityName = findViewById(R.id.weather_settings_input_city_name);
    }

    //По аппаратной кнопке "Назад" делаем всё то же, что и по кнопке "Back"
    @Override
    public void onBackPressed() {
        clickOnBackButton();
    }

    //По клику "назад" сохраняем данные и подготавливаем их для интента
    private void clickOnBackButton() {
        saveSettings();
        prepareResult();
        finish();
    }

    //Валидируем значение города
    private void validateCityName(){
        final Pattern patternCityName = Pattern.compile("^[-A-Za-z ]+$");
        inputCityName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus) return;
                TextView textView = (TextView)view;
                if(patternCityName.matcher(textView.getText().toString()).matches()) {
                    ((TextView) view).setError(null);
                } else {
                    ((TextView) view).setError("This is not city");
                }
            }
        });
    }

    //Получаем данные из настроек
    private void loadSettings() {
        weatherSettings = getIntent().getParcelableExtra("SETTINGS");

        if (weatherSettings != null) {
            inputCityName.setText(weatherSettings.getCity());
            humidity.setChecked(weatherSettings.isHumidityEnabled());
            pressure.setChecked(weatherSettings.isPressureEnabled());
            windSpeed.setChecked(weatherSettings.isWindSpeedEnabled());
        } else {
            Toast.makeText(WeatherSettingsActivity.this, "Упс...", Toast.LENGTH_LONG).show();
        }

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        String[] cities = getResources().getStringArray(R.array.city_name);
/*        ArrayList<String> arrayList = new ArrayList<>();
        for (String str : cities) {
            arrayList.add(ResourceUtils.getLocalizedStringResource(this, str));
        }*/
        recyclerView.setAdapter(new CityAdapter(cities, new OnCityItemClickListener() {
            @Override
            public void onClick(String data) {
                Toast.makeText(WeatherSettingsActivity.this, data, Toast.LENGTH_LONG).show();
            }
        }));

        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);
        itemDecoration.setDrawable(getDrawable(R.drawable.list_separator));
        recyclerView.addItemDecoration(itemDecoration);
    }

    //Метод сохраняет данные в настройках
    private void saveSettings() {
        weatherSettings.setHumidityEnabled(humidity.isChecked());
        weatherSettings.setPressureEnabled(pressure.isChecked());
        weatherSettings.setWindSpeedEnabled(windSpeed.isChecked());
    }

    //Подготавливаем данные для отправки в activityMain
    private void prepareResult() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("SETTINGS", weatherSettings);
        setResult(RESULT_OK, intent);
    }

    private void restoreData(Bundle savedInstanceState) {
        if (savedInstanceState == null) return;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        saveSettings();
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