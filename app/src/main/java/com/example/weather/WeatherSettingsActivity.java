package com.example.weather;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weather.db.DataSource;
import com.google.android.material.textfield.TextInputEditText;

import java.sql.SQLException;
import java.util.Objects;
import java.util.regex.Pattern;

import static com.example.weather.MainActivity.SETTINGS;

public class WeatherSettingsActivity extends AppCompatActivity {
    private Switch humidity;
    private Switch windSpeed;
    private Switch pressure;
    private RecyclerView recyclerView;
    private TextInputEditText inputCityName;
    Settings weatherSettings;
    private CityAdapter adapter;
    private DataSource dataSource;

    //Теги
    private static final String TAG = "WeatherSettingsActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showLog("OnCreate");
        setContentView(R.layout.weather_settings);

        dataSource = new DataSource(this);
        try {
            dataSource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        initViews();
        loadSettings();
        restoreData(savedInstanceState);
        validateCityName();
    }

    private void initViews() {
        humidity = findViewById(R.id.weather_settings_humidity);
        pressure = findViewById(R.id.weather_settings_pressure);
        windSpeed = findViewById(R.id.weather_settings_wind_speed);
        recyclerView = findViewById(R.id.weather_settings_recycler_view);
        inputCityName = findViewById(R.id.weather_settings_input_city_name);
    }

    //Инфлейтим меню
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    //Меню на главном экране
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_settings_action_back:
                clickOnBackButton();
                Toast.makeText(getApplicationContext(), R.string.setings_updated, Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_clear:
                clearList();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //По аппаратной кнопке "Назад" делаем всё то же, что и по кнопке "Back"
    @Override
    public void onBackPressed() {
        clickOnBackButton();
    }

    //По клику "назад" сохраняем данные и подготавливаем их для интента
    private void clickOnBackButton() {
        addCityToDB();
        refreshData();
        saveSettings();
        prepareResult();
        finish();
    }

    //Валидируем значение города
    private void validateCityName() {
        final Pattern patternCityName = Pattern.compile("^[-A-Za-z ]+$");
        inputCityName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) return;
                TextView textView = (TextView) view;
                if (patternCityName.matcher(textView.getText().toString()).matches()) {
                    ((TextView) view).setError(null);
                } else {
                    ((TextView) view).setError("This is not city");
                }
            }
        });
    }

    //Получаем данные из настроек
    private void loadSettings() {
        weatherSettings = getIntent().getParcelableExtra(SETTINGS);

        if (weatherSettings != null) {
            inputCityName.setText(weatherSettings.getCity().getName());
            humidity.setChecked(weatherSettings.isHumidityEnabled());
            pressure.setChecked(weatherSettings.isPressureEnabled());
            windSpeed.setChecked(weatherSettings.isWindSpeedEnabled());
        } else {
            Toast.makeText(WeatherSettingsActivity.this, "Упс...", Toast.LENGTH_LONG).show();
        }

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new CityAdapter(dataSource.getReader());
        adapter.setOnMenuItemClickListener(new CityAdapter.OnMenuItemClickListener() {
            @Override
            public void onItemSelectClick(City city) {
                selectElement(city);
            }

            @Override
            public void onItemDeleteClick(City city) {
                deleteElement(city);
            }
        });
        recyclerView.setAdapter(adapter);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);
        itemDecoration.setDrawable(Objects.requireNonNull(getDrawable(R.drawable.list_separator)));
        recyclerView.addItemDecoration(itemDecoration);
    }

    //Метод сохраняет данные в настройках
    private void saveSettings() {
        weatherSettings.setHumidityEnabled(this, humidity.isChecked());
        weatherSettings.setPressureEnabled(this, pressure.isChecked());
        weatherSettings.setWindSpeedEnabled(this, windSpeed.isChecked());
        weatherSettings.setCityName(this, inputCityName.getText().toString());
    }

    //Подготавливаем данные для отправки в activityMain
    private void prepareResult() {
        Intent intent = new Intent();
        intent.putExtra(SETTINGS, weatherSettings);
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


    /**Работа с базой данных*/
    //Ищем совпадения по DB и если их нет, то добавляем новую запись
    private void addCityToDB() {
        String cityToDataBase = Objects.requireNonNull(inputCityName.getText()).toString();
        if (dataSource.searchCityInTable(cityToDataBase)) {
            dataSource.add(Objects.requireNonNull(cityToDataBase.substring(0, 1).toUpperCase() + cityToDataBase.substring(1)));
        }
    }

    private void selectElement(City city) {
        inputCityName.setText(city.getName());
        refreshData();
    }

    private void deleteElement(City city) {
        dataSource.delete(city);
        refreshData();
    }

    private void clearList() {
        dataSource.deleteAll();
        refreshData();
    }

    private void refreshData() {
        dataSource.getReader().refresh();
        adapter.notifyDataSetChanged();
    }

}