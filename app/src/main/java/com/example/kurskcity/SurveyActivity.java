package com.example.kurskcity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;

import android.view.View;
import android.widget.*;

import java.util.ArrayList;
import java.util.List;

public class SurveyActivity extends AppCompatActivity {

    private Spinner timeAvailableSpinner, startTimeSpinner, travelCompanionSpinner;
    private EditText startLocationEditText, budgetEditText;
    private CheckBox architectureCheckBox, historyCheckBox, artCheckBox, museumsCheckBox;
    private CheckBox shoppingCheckBox, foodCheckBox, natureCheckBox, entertainmentCheckBox;
    private CheckBox concertsCheckBox, theaterCheckBox;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);

        initializeViews();
        setupSpinners();
        setupSubmitButton();
    }

    private void initializeViews() {
        // Инициализация Spinner
        timeAvailableSpinner = findViewById(R.id.spinner_time_available);
        startTimeSpinner = findViewById(R.id.spinner_start_time);
        travelCompanionSpinner = findViewById(R.id.spinner_travel_companion);

        // Инициализация EditText
        startLocationEditText = findViewById(R.id.et_start_location);
        budgetEditText = findViewById(R.id.et_budget);

        // Инициализация CheckBox для интересов
        architectureCheckBox = findViewById(R.id.cb_architecture);
        historyCheckBox = findViewById(R.id.cb_history);
        artCheckBox = findViewById(R.id.cb_art);
        museumsCheckBox = findViewById(R.id.cb_museums);
        shoppingCheckBox = findViewById(R.id.cb_shopping);
        foodCheckBox = findViewById(R.id.cb_food);
        natureCheckBox = findViewById(R.id.cb_nature);
        entertainmentCheckBox = findViewById(R.id.cb_entertainment);
        concertsCheckBox = findViewById(R.id.cb_concerts);
        theaterCheckBox = findViewById(R.id.cb_theater);

        // Инициализация кнопки
        submitButton = findViewById(R.id.btn_submit);
    }

    private void setupSpinners() {
        // Настройка Spinner для времени
        ArrayAdapter<CharSequence> timeAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.time_available_options,
                android.R.layout.simple_spinner_item
        );
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeAvailableSpinner.setAdapter(timeAdapter);

        // Настройка Spinner для времени начала
        ArrayAdapter<CharSequence> startTimeAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.start_time_options,
                android.R.layout.simple_spinner_item
        );
        startTimeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        startTimeSpinner.setAdapter(startTimeAdapter);

        // Настройка Spinner для спутников
        ArrayAdapter<CharSequence> companionAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.travel_companion_options,
                android.R.layout.simple_spinner_item
        );
        companionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        travelCompanionSpinner.setAdapter(companionAdapter);
    }

    private void setupSubmitButton() {
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInput()) {
                    submitSurvey();
                }
            }
        });
    }

    private boolean validateInput() {
        if (startLocationEditText.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Пожалуйста, укажите начальную точку маршрута", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (budgetEditText.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Пожалуйста, укажите бюджет", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!hasSelectedInterests()) {
            Toast.makeText(this, "Пожалуйста, выберите хотя бы один интерес", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean hasSelectedInterests() {
        return architectureCheckBox.isChecked() || historyCheckBox.isChecked() ||
                artCheckBox.isChecked() || museumsCheckBox.isChecked() ||
                shoppingCheckBox.isChecked() || foodCheckBox.isChecked() ||
                natureCheckBox.isChecked() || entertainmentCheckBox.isChecked() ||
                concertsCheckBox.isChecked() || theaterCheckBox.isChecked();
    }

    private void submitSurvey() {
        // Сбор данных
        String timeAvailable = timeAvailableSpinner.getSelectedItem().toString();
        String startTime = startTimeSpinner.getSelectedItem().toString();
        String startLocation = startLocationEditText.getText().toString().trim();
        String budget = budgetEditText.getText().toString().trim();
        String travelCompanion = travelCompanionSpinner.getSelectedItem().toString();

        List<String> interests = getSelectedInterests();

        // Создание результата
        String result = createSurveyResult(timeAvailable, startTime, startLocation, budget, travelCompanion, interests);

        // Отправка результата (можно изменить на переход к следующему экрану)
        Intent resultIntent = new Intent();
        resultIntent.putExtra("survey_result", result);
        setResult(RESULT_OK, resultIntent);

        Toast.makeText(this, "Опрос завершен! Создаем ваш маршрут...", Toast.LENGTH_LONG).show();
        finish();
    }

    private List<String> getSelectedInterests() {
        List<String> interests = new ArrayList<>();

        if (architectureCheckBox.isChecked()) interests.add("Архитектура");
        if (historyCheckBox.isChecked()) interests.add("История");
        if (artCheckBox.isChecked()) interests.add("Искусство");
        if (museumsCheckBox.isChecked()) interests.add("Музеи");
        if (shoppingCheckBox.isChecked()) interests.add("Шопинг");
        if (foodCheckBox.isChecked()) interests.add("Еда");
        if (natureCheckBox.isChecked()) interests.add("Отдых на природе");
        if (entertainmentCheckBox.isChecked()) interests.add("Развлечения и активность");
        if (concertsCheckBox.isChecked()) interests.add("Концерты");
        if (theaterCheckBox.isChecked()) interests.add("Спектакли");

        return interests;
    }

    private String createSurveyResult(String timeAvailable, String startTime, String startLocation,
                                      String budget, String travelCompanion, List<String> interests) {
        StringBuilder result = new StringBuilder();
        result.append("Результаты опроса:\n\n");
        result.append("Время доступное: ").append(timeAvailable).append("\n");
        result.append("Время начала: ").append(startTime).append("\n");
        result.append("Начальная точка: ").append(startLocation).append("\n");
        result.append("Бюджет: ").append(budget).append("\n");
        result.append("Компания: ").append(travelCompanion).append("\n");
        result.append("Интересы: ").append(String.join(", ", interests)).append("\n");

        return result.toString();
    }
}