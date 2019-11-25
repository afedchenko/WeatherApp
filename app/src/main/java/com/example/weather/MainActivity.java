package com.example.weather;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String CITY_NAME_MAIN = "ActivityMainCityName";
    public static String SETTINGS = "SETTINGS";
    private final int weatherSettingsActivityResultCode = 7;
    private LinearLayout humidity, pressure, windSpeed;
    private Button browser, settings;
    private TextView currentCity;
    RecyclerView recyclerView;
    Settings weatherSettings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showLog("OnCreate");
        setContentView(R.layout.activity_main);

        //Выставляем дефолтные значения в объекте настроек
        weatherSettings = new Settings(true, true,true, getString(R.string.novosibirsk));

        initViews();
        loadDataInMainActivity();
        restoreDataTextView(savedInstanceState);
    }

    private void initViews() {
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

        //Инициализируем остальные view
        humidity = findViewById(R.id.activity_main_linear_layout_humidity);
        pressure = findViewById(R.id.activity_main_linear_layuot_pressure);
        windSpeed = findViewById(R.id.activity_main_linear_layout_wind_speed);
        currentCity = findViewById(R.id.activity_main_city_current);
        recyclerView = findViewById(R.id.activity_main_recycler_view);
    }

    //Подготавливаем данные для отправки в weather_settings
    private void clickOnSettingsButton() {
        Intent intent = new Intent(this, WeatherSettingsActivity.class);
        intent.putExtra(SETTINGS, weatherSettings);
        startActivityForResult(intent, weatherSettingsActivityResultCode);
    }

    //Проверяем результат интента, если ОК, то обновляемся, если не ок, задаем дефолтное значение города
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && requestCode == weatherSettingsActivityResultCode) {
            updateWeatherParams(data);
        }
    }

    //Получаем данные с weather_settings
    private void updateWeatherParams(@Nullable Intent data) {
        weatherSettings = getIntent().getParcelableExtra("SETTINGS");

        if (weatherSettings != null) {
            currentCity.setText(weatherSettings.getCity());

            changeVisibilityView(weatherSettings.isHumidityEnabled(), humidity);
            changeVisibilityView(weatherSettings.isPressureEnabled(), pressure);
            changeVisibilityView(weatherSettings.isWindSpeedEnabled(), windSpeed);
        } else {
            Toast.makeText(MainActivity.this, "Упс...", Toast.LENGTH_LONG).show();
        }
    }

    //Загружаем все пришедшие значения свитчей сеттингов
    private void loadDataInMainActivity() {
        changeVisibilityView(weatherSettings.isHumidityEnabled(), humidity);
        changeVisibilityView(weatherSettings.isPressureEnabled(), pressure);
        changeVisibilityView(weatherSettings.isWindSpeedEnabled(), windSpeed);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        String[] days = {"Сегодня", "Завтра", "Послезавтра", "Через 2 дня"};
        recyclerView.setAdapter(new DayAdapter(days, new OnCityItemClickListener() {
            @Override
            public void onClick(String data) {
                Toast.makeText(MainActivity.this, data, Toast.LENGTH_LONG).show();
            }
        }));
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);
        itemDecoration.setDrawable(getDrawable(R.drawable.list_separator));
        recyclerView.addItemDecoration(itemDecoration);
    }

    //Проверяем, что нам пришло из настроек свичей, и в зависимости от true/false скрываем или показываем view
    private void changeVisibilityView(boolean isChecked, LinearLayout layout) {
        if (isChecked) {
            layout.setVisibility(View.VISIBLE);
        } else layout.setVisibility(View.GONE);
    }

    //Сохраняем состояние вьюшек на главном экране
    @Override
    public void onSaveInstanceState(Bundle savedInsanceState) {
        super.onSaveInstanceState(savedInsanceState);
        savedInsanceState.putString(CITY_NAME_MAIN, currentCity.getText().toString());
    }

    //Восстанавливаем состояние вьюшек на главном экране после пересоздания
    public void restoreDataTextView(Bundle savedInstanceState) {
        if (savedInstanceState == null) return;
        currentCity.setText(savedInstanceState.getString(CITY_NAME_MAIN, currentCity.getText().toString()));
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