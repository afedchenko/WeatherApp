package com.example.weather;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weather.model.WeatherRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String CITY_NAME_MAIN = "ActivityMainCityName";
    public static String SETTINGS = "SETTINGS";
    private final int weatherSettingsActivityResultCode = 7;
    private LinearLayout humidity, pressure, windSpeed;
    private TextView currentCity;
    RecyclerView recyclerView;
    Settings weatherSettings;


    SensorManager manager;
    SensorEventListener eventListener;

    //Данные погоды с сервера
    private TextView city;
    private TextView temperature;
    private TextView pressureFromApi;
    private TextView humidityFromApi;
    private TextView windSpeedFromApi;
    private static final String BASE_URL = "https://api.openweathermap.org";
    private static final String WEATHER_API_KEY = "b75e79b82cfa5ac7d01ece82f8dfcd51";
    private String cityName = "Novosibirsk";
    Retrofit retrofit;
    private OpenWeather openWeather;

    //Инфлейтим меню
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //Меню на главном экране
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_main_action_refresh) {
            refreshWeatherFromApi();
            Toast.makeText(getApplicationContext(), R.string.updated, Toast.LENGTH_SHORT).show();
        }
        if (id == R.id.menu_main_action_settings) {
            clickOnSettingsButton();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showLog("OnCreate");
        setContentView(R.layout.activity_main);

        //Выставляем дефолтные значения в объекте настроек
        weatherSettings = new Settings(this);

        initViews();
        loadDataInMainActivity();
        refreshWeatherFromApi();
        restoreDataTextView(savedInstanceState);
    }

    private void initViews() {
        //Инициализируем остальные view
        humidity = findViewById(R.id.activity_main_linear_layout_humidity);
        pressure = findViewById(R.id.activity_main_linear_layuot_pressure);
        windSpeed = findViewById(R.id.activity_main_linear_layout_wind_speed);
        currentCity = findViewById(R.id.activity_main_city_current);
        recyclerView = findViewById(R.id.activity_main_recycler_view);
        pressureFromApi = findViewById(R.id.fragment_weather_widget_pressure);
        humidityFromApi = findViewById(R.id.fragment_weather_widget_humidity);
        windSpeedFromApi = findViewById(R.id.fragment_weather_widget_wind_speed);
        temperature = findViewById(R.id.activity_main_temperature);
    }

    //Создаем интерфейс доступа к web-сервису
    interface OpenWeather {
        @GET("/data/2.5/weather/")
        Call<WeatherRequest> loadWeather(@Query("q") String q, @Query("appid") String apiKey);
    }

    //Метод по обработке API
    private void refreshWeatherFromApi() {
        initRetrofit();
        requestRetrofit(cityName);
    }

    //Инициализируем retrofit
    private void initRetrofit() {
        retrofit = new Retrofit.Builder().baseUrl(BASE_URL).
                addConverterFactory(GsonConverterFactory.create()).build();
        openWeather = retrofit.create(OpenWeather.class);
    }

    //Передаем запрос и обрабатываем ответ
    private void requestRetrofit(String city) {
        openWeather.loadWeather(city + ",ru", WEATHER_API_KEY).enqueue(new Callback<WeatherRequest>() {
            @Override
            public void onResponse(Call<WeatherRequest> call, Response<WeatherRequest> response) {
                if (response.body() != null) {
                    displayWeatherFromRsponse(response);
                }
            }

            @Override
            public void onFailure(Call<WeatherRequest> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Data update error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Отдельный метод для обработки полей ответа
    private void displayWeatherFromRsponse(Response<WeatherRequest> response) {
        String temp = Math.round(response.body().getMain().getTemp() - 273.0) + " °C";
        temperature.setText(temp);
        currentCity.setText(response.body().getName());
        double press = Math.round(response.body().getMain().getPressure() * 0.750062);
        pressureFromApi.setText(press + " мм");
        humidityFromApi.setText(String.format("%d", response.body().getMain().getHumidity()) + " %");
        windSpeedFromApi.setText(String.format("%d", response.body().getWind().getSpeed()) + " м/с");
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
        assert data != null;
        weatherSettings = data.getParcelableExtra(SETTINGS);

        if (weatherSettings != null) {
            currentCity.setText(weatherSettings.getCity());
            setVisibilityParams();
        } else {
            Toast.makeText(MainActivity.this, "Упс...", Toast.LENGTH_LONG).show();
        }
    }

    //Загружаем все пришедшие значения свитчей сеттингов
    private void loadDataInMainActivity() {
        setVisibilityParams();
/*        recyclerView.setHasFixedSize(true);
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
        recyclerView.addItemDecoration(itemDecoration);*/
    }

    //Метод для изменения видимости параметров погоды
    private void setVisibilityParams() {
        changeVisibilityView(weatherSettings.isHumidityEnabled(), humidity);
        changeVisibilityView(weatherSettings.isPressureEnabled(), pressure);
        changeVisibilityView(weatherSettings.isWindSpeedEnabled(), windSpeed);
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
        savedInsanceState.putParcelable("savedSetting", weatherSettings);
    }

    //Восстанавливаем состояние вьюшек на главном экране после пересоздания
    public void restoreDataTextView(Bundle savedInstanceState) {
        if (savedInstanceState == null) return;
        currentCity.setText(savedInstanceState.getString(CITY_NAME_MAIN, currentCity.getText().toString()));
        weatherSettings = savedInstanceState.getParcelable("savedSetting");
        setVisibilityParams();
    }

    @Override
    protected void onStart() {
        super.onStart();
        showLog("onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Добавляю датчики температуры и влажности (Выводить значение не буду, датчиков нет)
        manager = (SensorManager) getSystemService(SENSOR_SERVICE);
        assert manager != null;
        List<Sensor> sensors = manager.getSensorList(Sensor.TYPE_ALL);
        for (Sensor sensor : sensors) {
            Log.i("Sensors", sensor.getName());
        }

        Sensor sensorAmbientTemperature = manager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        Sensor sensorAbsoluteHumidity = manager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        if (sensorAmbientTemperature == null) return;
        if (sensorAbsoluteHumidity == null) return;

        eventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
//                Log.i("Sensors", "Temperature = " + sensorEvent.values[0]);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };

        manager.registerListener(eventListener, sensorAmbientTemperature, 1000);
        manager.registerListener(eventListener, sensorAbsoluteHumidity, 1000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        manager.unregisterListener(eventListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        showLog("onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
/*        if (task != null) {
            task.cancel(true);
            task = null;
        }*/
        showLog("onDestroy");
    }

    private void showLog(String logMessage) {
        Log.d(TAG, logMessage);
        //Toast.makeText(getApplicationContext(), logMessage, Toast.LENGTH_SHORT).show();
    }
}