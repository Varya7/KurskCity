package com.example.kurskcity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.*;

public class SurveyActivity extends AppCompatActivity {

    private TextInputEditText etDate;
    private LinearLayout layoutAttractionCategories, layoutEventCategories;
    private TextView tvDateLabel;
    private Button submitButton;
    private Calendar calendar;
    private AttractionsDbHelper dbHelper;
    private List<String> availableAttractionCategories = new ArrayList<>();
    private List<String> availableEventCategories = Arrays.asList(
            "Театр", "Концерты", "Выставки", "Фестивали", "Кино", "Спорт", "Образование", "Развлечения"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);

        dbHelper = new AttractionsDbHelper(this);
        calendar = Calendar.getInstance();
        initializeViews();
        loadCategoriesFromDatabase();
        setupDatePicker();
        setupSubmitButton();
        updateUIState();
    }

    private void initializeViews() {
        etDate = findViewById(R.id.et_date);
        layoutAttractionCategories = findViewById(R.id.layout_attraction_categories);
        layoutEventCategories = findViewById(R.id.layout_event_categories);
        tvDateLabel = findViewById(R.id.tv_date_label);
        submitButton = findViewById(R.id.btn_submit);
    }

    private void loadCategoriesFromDatabase() {
        try {
            // Загружаем категории достопримечательностей из базы данных
            availableAttractionCategories = dbHelper.getAttractionCategories();
        } catch (Exception e) {
            e.printStackTrace();
            // Если произошла ошибка, используем стандартные категории
            availableAttractionCategories = Arrays.asList(
                    "Архитектура", "История", "Искусство", "Музеи", "Религия", "Памятники",
                    "Парки", "Природа", "Культура", "Развлечения"
            );
        }

        // Создаем CheckBox для категорий достопримечательностей
        createCategoryCheckBoxes(layoutAttractionCategories, availableAttractionCategories, "attraction_");

        // Создаем CheckBox для категорий мероприятий
        createCategoryCheckBoxes(layoutEventCategories, availableEventCategories, "event_");

        // Добавляем слушатели для обновления состояния кнопки
        setupCheckboxListeners();
    }

    private void createCategoryCheckBoxes(LinearLayout layout, List<String> categories, String prefix) {
        layout.removeAllViews();

        if (categories.isEmpty()) {
            TextView noCategoriesText = new TextView(this);
            noCategoriesText.setText("Категории не найдены");
            noCategoriesText.setTextSize(14);
            noCategoriesText.setTextColor(0xFF666666);
            layout.addView(noCategoriesText);
            return;
        }

        for (String category : categories) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setId(View.generateViewId());
            checkBox.setTag(prefix + category);
            checkBox.setText(category);
            checkBox.setTextSize(14);
            checkBox.setPadding(0, 8, 0, 8);

            // Добавляем слушатель изменения состояния
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                updateUIState();
            });

            layout.addView(checkBox);
        }
    }

    private void setupCheckboxListeners() {
        // Слушатели уже добавлены в createCategoryCheckBoxes
    }

    private void setupDatePicker() {
        etDate.setOnClickListener(v -> showDatePicker());
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateDateLabel();
                    updateUIState(); // Обновляем состояние после выбора даты
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void updateDateLabel() {
        String dateFormat = "dd.MM.yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.getDefault());
        etDate.setText(sdf.format(calendar.getTime()));
    }

    private void updateUIState() {
        boolean hasSelectedAttractions = getSelectedAttractionCategories().size() > 0;
        boolean hasSelectedEvents = getSelectedEventCategories().size() > 0;
        boolean hasDate = !etDate.getText().toString().trim().isEmpty();

        // Проверяем условия: если выбраны мероприятия - нужна дата, если только достопримечательности - дата не нужна
        boolean canProceed = hasSelectedAttractions || (hasSelectedEvents && hasDate);

        submitButton.setEnabled(canProceed);
        submitButton.setAlpha(canProceed ? 1.0f : 0.5f);

        // Обновляем подсказку о дате
        if (hasSelectedEvents && !hasDate) {
            tvDateLabel.setText("Для подбора мероприятий выберите дату");
            tvDateLabel.setTextColor(0xFFFF0000); // Красный цвет для предупреждения
        } else {
            tvDateLabel.setText("Дата посещения (требуется для мероприятий)");
            tvDateLabel.setTextColor(0xFF2196F3); // Синий цвет по умолчанию
        }
    }

    private void setupSubmitButton() {
        submitButton.setOnClickListener(v -> {
            if (validateInput()) {
                submitSurvey();
            }
        });
    }

    private boolean validateInput() {
        List<String> eventCategories = getSelectedEventCategories();
        boolean hasDate = !etDate.getText().toString().trim().isEmpty();

        // Если выбраны мероприятия, но не выбрана дата - показываем ошибку
        if (!eventCategories.isEmpty() && !hasDate) {
            showError("Для подбора мероприятий необходимо выбрать дату");
            return false;
        }

        if (!hasSelectedCategories()) {
            showError("Пожалуйста, выберите хотя бы одну категорию интересов");
            return false;
        }

        return true;
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private boolean hasSelectedCategories() {
        return getSelectedAttractionCategories().size() > 0 || getSelectedEventCategories().size() > 0;
    }

    private void submitSurvey() {
        String date = etDate.getText().toString();
        List<String> attractionCategories = getSelectedAttractionCategories();
        List<String> eventCategories = getSelectedEventCategories();

        // Создаем объект с данными для рекомендаций
        RecommendationRequest request = new RecommendationRequest(
                date, attractionCategories, eventCategories
        );

        Intent resultIntent = new Intent();
        resultIntent.putExtra("recommendation_request", request);
        setResult(RESULT_OK, resultIntent);

        String message;
        if (!attractionCategories.isEmpty() && !eventCategories.isEmpty()) {
            message = "Подбираем достопримечательности и мероприятия...";
        } else if (!attractionCategories.isEmpty()) {
            message = "Подбираем достопримечательности...";
        } else {
            message = "Подбираем мероприятия...";
        }

        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        finish();
    }

    private List<String> getSelectedAttractionCategories() {
        return getSelectedCategories(layoutAttractionCategories);
    }

    private List<String> getSelectedEventCategories() {
        return getSelectedCategories(layoutEventCategories);
    }

    private List<String> getSelectedCategories(LinearLayout layout) {
        List<String> selected = new ArrayList<>();
        for (int i = 0; i < layout.getChildCount(); i++) {
            View view = layout.getChildAt(i);
            if (view instanceof CheckBox) {
                CheckBox checkBox = (CheckBox) view;
                if (checkBox.isChecked()) {
                    // Убираем префикс из тега
                    String tag = (String) checkBox.getTag();
                    String category = tag.substring(tag.indexOf('_') + 1);
                    selected.add(category);
                }
            }
        }
        return selected;
    }

    @Override
    protected void onDestroy() {
        if (dbHelper != null) {
            dbHelper.close();
        }
        super.onDestroy();
    }

    // Класс для передачи данных рекомендаций
    public static class RecommendationRequest implements java.io.Serializable {
        private String date;
        private List<String> attractionCategories;
        private List<String> eventCategories;

        public RecommendationRequest(String date, List<String> attractionCategories,
                                     List<String> eventCategories) {
            this.date = date;
            this.attractionCategories = attractionCategories;
            this.eventCategories = eventCategories;
        }

        // Геттеры
        public String getDate() { return date; }
        public List<String> getAttractionCategories() { return attractionCategories; }
        public List<String> getEventCategories() { return eventCategories; }
    }
}