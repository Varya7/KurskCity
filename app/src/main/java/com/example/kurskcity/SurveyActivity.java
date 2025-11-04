package com.example.kurskcity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class SurveyActivity extends AppCompatActivity {

    private TextInputEditText etDate, etStartTime, etDurationHours, etDurationMinutes,
            etStartLocation, etBudget;
    private Spinner travelCompanionSpinner;
    private CheckBox cbArchitecture, cbHistory, cbArt, cbMuseums, cbShopping, cbFood;
    private CheckBox cbNature, cbEntertainment, cbConcerts, cbTheater;
    private Button submitButton;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);

        calendar = Calendar.getInstance();
        initializeViews();
        setupDateAndTimePickers();
        setupSpinner();
        setupSubmitButton();
    }

    private void initializeViews() {
        etDate = findViewById(R.id.et_date);
        etStartTime = findViewById(R.id.et_start_time);
        etDurationHours = findViewById(R.id.et_duration_hours);
        etDurationMinutes = findViewById(R.id.et_duration_minutes);
        etStartLocation = findViewById(R.id.et_start_location);
        etBudget = findViewById(R.id.et_budget);
        travelCompanionSpinner = findViewById(R.id.spinner_travel_companion);

        cbArchitecture = findViewById(R.id.cb_architecture);
        cbHistory = findViewById(R.id.cb_history);
        cbArt = findViewById(R.id.cb_art);
        cbMuseums = findViewById(R.id.cb_museums);
        cbShopping = findViewById(R.id.cb_shopping);
        cbFood = findViewById(R.id.cb_food);
        cbNature = findViewById(R.id.cb_nature);
        cbEntertainment = findViewById(R.id.cb_entertainment);
        cbConcerts = findViewById(R.id.cb_concerts);
        cbTheater = findViewById(R.id.cb_theater);

        submitButton = findViewById(R.id.btn_submit);
    }

    private void setupDateAndTimePickers() {
        etDate.setOnClickListener(v -> showDatePicker());
        etStartTime.setOnClickListener(v -> showTimePicker());
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateDateLabel();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void showTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);
                    updateTimeLabel();
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
        );
        timePickerDialog.show();
    }

    private void updateDateLabel() {
        String dateFormat = "dd.MM.yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.getDefault());
        etDate.setText(sdf.format(calendar.getTime()));
    }

    private void updateTimeLabel() {
        String timeFormat = "HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(timeFormat, Locale.getDefault());
        etStartTime.setText(sdf.format(calendar.getTime()));
    }

    private void setupSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.travel_companion_options,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        travelCompanionSpinner.setAdapter(adapter);
    }

    private void setupSubmitButton() {
        submitButton.setOnClickListener(v -> {
            if (validateInput()) {
                submitSurvey();
            }
        });
    }

    private boolean validateInput() {
        if (etDate.getText().toString().trim().isEmpty()) {
            showError("Пожалуйста, выберите дату");
            return false;
        }

        if (etStartTime.getText().toString().trim().isEmpty()) {
            showError("Пожалуйста, выберите время начала");
            return false;
        }

        String hoursText = etDurationHours.getText().toString().trim();
        String minutesText = etDurationMinutes.getText().toString().trim();

        if (hoursText.isEmpty() && minutesText.isEmpty()) {
            showError("Пожалуйста, укажите продолжительность (часы или минуты)");
            return false;
        }

        if (etStartLocation.getText().toString().trim().isEmpty()) {
            showError("Пожалуйста, укажите начальную точку маршрута");
            return false;
        }

        if (etBudget.getText().toString().trim().isEmpty()) {
            showError("Пожалуйста, укажите бюджет");
            return false;
        }

        if (!hasSelectedInterests()) {
            showError("Пожалуйста, выберите хотя бы один интерес");
            return false;
        }

        return true;
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private boolean hasSelectedInterests() {
        return cbArchitecture.isChecked() || cbHistory.isChecked() ||
                cbArt.isChecked() || cbMuseums.isChecked() ||
                cbShopping.isChecked() || cbFood.isChecked() ||
                cbNature.isChecked() || cbEntertainment.isChecked() ||
                cbConcerts.isChecked() || cbTheater.isChecked();
    }

    private void submitSurvey() {
        String date = etDate.getText().toString();
        String startTime = etStartTime.getText().toString();
        String hoursText = etDurationHours.getText().toString().trim();
        String minutesText = etDurationMinutes.getText().toString().trim();
        String startLocation = etStartLocation.getText().toString();
        String budget = etBudget.getText().toString();
        String travelCompanion = travelCompanionSpinner.getSelectedItem().toString();

        int hours = hoursText.isEmpty() ? 0 : Integer.parseInt(hoursText);
        int minutes = minutesText.isEmpty() ? 0 : Integer.parseInt(minutesText);
        int totalMinutes = hours * 60 + minutes;

        if (totalMinutes == 0) {
            showError("Пожалуйста, укажите продолжительность маршрута");
            return;
        }

        List<String> interests = getSelectedInterests();

        String result = createSurveyResult(date, startTime,
                hours, minutes, startLocation, budget, travelCompanion, interests);

        Intent resultIntent = new Intent();
        resultIntent.putExtra("survey_result", result);
        setResult(RESULT_OK, resultIntent);

        Toast.makeText(this, "Отлично! Создаем ваш идеальный маршрут...", Toast.LENGTH_LONG).show();
        finish();
    }

    private List<String> getSelectedInterests() {
        List<String> interests = new ArrayList<>();

        if (cbArchitecture.isChecked()) interests.add("Архитектура");
        if (cbHistory.isChecked()) interests.add("История");
        if (cbArt.isChecked()) interests.add("Искусство");
        if (cbMuseums.isChecked()) interests.add("Музеи");
        if (cbShopping.isChecked()) interests.add("Шопинг");
        if (cbFood.isChecked()) interests.add("Еда");
        if (cbNature.isChecked()) interests.add("Природа");
        if (cbEntertainment.isChecked()) interests.add("Развлечения");
        if (cbConcerts.isChecked()) interests.add("Концерты");
        if (cbTheater.isChecked()) interests.add("Театр");

        return interests;
    }

    private String createSurveyResult(String date, String startTime, int hours, int minutes,
                                      String startLocation, String budget, String travelCompanion, List<String> interests) {
        StringBuilder result = new StringBuilder();
        result.append("Ваш персонализированный маршрут:\n\n");
        result.append("Дата: ").append(date).append("\n");
        result.append("Время начала: ").append(startTime).append("\n");

        if (hours > 0) result.append("Продолжительность: ").append(hours).append(" ч ");
        if (minutes > 0) result.append(minutes).append(" мин");
        result.append("\n");

        result.append("Старт: ").append(startLocation).append("\n");
        result.append("Бюджет: ").append(budget).append(" руб\n");
        result.append("Компания: ").append(travelCompanion).append("\n");
        result.append("Интересы: ").append(String.join(", ", interests)).append("\n");

        return result.toString();
    }
}
