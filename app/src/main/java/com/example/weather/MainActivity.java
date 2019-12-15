package com.example.weather;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weather.model.WeatherRequest;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;

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
    private static final String WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather?q=Novosibirsk,RU&appid=";
    private static final String WEATHER_API_KEY = "b75e79b82cfa5ac7d01ece82f8dfcd51";

    private AsyncTask<String, String, WeatherRequest> task;

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
            refreshParams();
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
        weatherSettings = new Settings(true, true, true, getString(R.string.novosibirsk));

        initViews();
        loadDataInMainActivity();
        refreshParams();
        restoreDataTextView(savedInstanceState);
    }

    private void initViews() {
        //Инициализируем view кнопку настроек и открываем по ней экран настроек
/*        settings = findViewById(R.id.activity_main_button_setings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickOnSettingsButton();
            }
        });*/

/*        //Инициализируем view кнопку браузера и открываем по ней браузер
        browser = findViewById(R.id.activity_main_button_browser);
        browser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openBrowser = new Intent(Intent.ACTION_VIEW, Uri.parse("https://pogoda.ngs.ru/"));
                startActivity(openBrowser);
            }
        });*/

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
/*        refresh = findViewById(R.id.activity_main_button_refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshParams();
                Toast.makeText(getApplicationContext(), "Обновлено", Toast.LENGTH_SHORT).show();
            }
        });*/
    }

    //Асинктаск с получением погоды с сервера
    @SuppressLint("StaticFieldLeak")
    private void refreshParams() {
        if (task != null) {
            task.cancel(true);
            task = null;
        }
        task = new AsyncTask<String, String, WeatherRequest>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            protected WeatherRequest doInBackground(String... strings) {
                HttpsURLConnection urlConnection = null;
                try {
                    URL uri = new URL(WEATHER_URL + WEATHER_API_KEY);
                    urlConnection = (HttpsURLConnection) uri.openConnection();
                    urlConnection.setRequestMethod("GET"); // установка метода получения данных -GET
                    urlConnection.setReadTimeout(10000); // установка таймаута - 10 000 миллисекунд
                    BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream())); // читаем  данные в поток

                    String result = getLines(in);

                    // преобразование данных запроса в модель
                    Gson gson = new Gson();
                    return gson.fromJson(result, WeatherRequest.class);

                } catch (Exception e) {
                    Log.e(TAG, "Fail connection", e);
                    e.printStackTrace();
                } finally {
                    if (null != urlConnection) {
                        urlConnection.disconnect();
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(WeatherRequest weatherRequest) {
                if (weatherRequest != null) {
                    displayWeather(weatherRequest);
                }
            }
        };
        task.execute(WEATHER_URL + WEATHER_API_KEY);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private String getLines(BufferedReader in) {
        return in.lines().collect(Collectors.joining("\n"));
    }

    private void displayWeather(WeatherRequest weatherRequest) {
        currentCity.setText(weatherRequest.getName());
        String temp = Math.round(weatherRequest.getMain().getTemp() - 273.0) + " °C";
        double press = Math.round(weatherRequest.getMain().getPressure() * 0.750062);
        temperature.setText(temp);
        pressureFromApi.setText(Double.toString(press) + " мм");
        humidityFromApi.setText(String.format("%d", weatherRequest.getMain().getHumidity()) + " %");
        windSpeedFromApi.setText(String.format("%d", weatherRequest.getWind().getSpeed()) + " м/с");
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
        changeVisibilityView(weatherSettings.isHumidityEnabled(this), humidity);
        changeVisibilityView(weatherSettings.isPressureEnabled(this), pressure);
        changeVisibilityView(weatherSettings.isWindSpeedEnabled(this), windSpeed);
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
        if (task != null) {
            task.cancel(true);
            task = null;
        }
        showLog("onDestroy");
    }

    private void showLog(String logMessage) {
        Log.d(TAG, logMessage);
        //Toast.makeText(getApplicationContext(), logMessage, Toast.LENGTH_SHORT).show();
    }
}