package com.example.kurskcity;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class RecommendationsActivity extends AppCompatActivity {

    private RecyclerView rvRecommendedAttractions, rvRecommendedEvents;
    private AttractionAdapter attractionAdapter;
    private EventAdapter eventAdapter;
    private TextView tvTitle, tvEmptyAttractions, tvEmptyEvents;
    private ProgressBar progressBar;
    private AttractionsDbHelper dbHelper;
    private List<KurskEventsParser.Event> recommendedEvents = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendations);

        dbHelper = new AttractionsDbHelper(this);
        initializeViews();
        setupRecommendations();
    }

    private void initializeViews() {
        tvTitle = findViewById(R.id.tv_title);
        tvEmptyAttractions = findViewById(R.id.tv_empty_attractions);
        tvEmptyEvents = findViewById(R.id.tv_empty_events);
        rvRecommendedAttractions = findViewById(R.id.rv_recommended_attractions);
        rvRecommendedEvents = findViewById(R.id.rv_recommended_events);
        progressBar = findViewById(R.id.progress_bar);

        rvRecommendedAttractions.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvRecommendedEvents.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        progressBar.setVisibility(View.VISIBLE);
    }

    private void setupRecommendations() {
        SurveyActivity.RecommendationRequest request =
                (SurveyActivity.RecommendationRequest) getIntent().getSerializableExtra("recommendation_request");

        if (request != null) {
            tvTitle.setText("Рекомендации на " + request.getDate());

            List<Attraction> recommendedAttractions = getRecommendedAttractions(request);
            setupAttractionsRecyclerView(recommendedAttractions);

            loadRecommendedEvents(request);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void setupAttractionsRecyclerView(List<Attraction> recommendedAttractions) {
        if (recommendedAttractions.isEmpty()) {
            tvEmptyAttractions.setVisibility(View.VISIBLE);
            rvRecommendedAttractions.setVisibility(View.GONE);
        } else {
            tvEmptyAttractions.setVisibility(View.GONE);
            rvRecommendedAttractions.setVisibility(View.VISIBLE);
            attractionAdapter = new AttractionAdapter(recommendedAttractions, true);
            rvRecommendedAttractions.setAdapter(attractionAdapter);
        }
    }

    private void setupEventsRecyclerView(List<KurskEventsParser.Event> recommendedEvents) {
        if (recommendedEvents.isEmpty()) {
            tvEmptyEvents.setVisibility(View.VISIBLE);
            rvRecommendedEvents.setVisibility(View.GONE);
        } else {
            tvEmptyEvents.setVisibility(View.GONE);
            rvRecommendedEvents.setVisibility(View.VISIBLE);
            eventAdapter = new EventAdapter(recommendedEvents);
            rvRecommendedEvents.setAdapter(eventAdapter);
        }

        // Скрываем прогресс бар когда все загружено
        progressBar.setVisibility(View.GONE);
    }

    private void loadRecommendedEvents(SurveyActivity.RecommendationRequest request) {
        new Thread(() -> {
            try {
                recommendedEvents = getRecommendedEvents(request);

                runOnUiThread(() -> {
                    setupEventsRecyclerView(recommendedEvents);
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    tvEmptyEvents.setVisibility(View.VISIBLE);
                    rvRecommendedEvents.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(RecommendationsActivity.this,
                            "Ошибка загрузки мероприятий", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private List<Attraction> getRecommendedAttractions(SurveyActivity.RecommendationRequest request) {
        List<Attraction> allAttractions = dbHelper.getAllAttractions();
        List<Attraction> recommended = new ArrayList<>();

        for (Attraction attraction : allAttractions) {
            if (matchesAttractionCategories(attraction, request.getAttractionCategories())) {
                recommended.add(attraction);
            }

            if (recommended.size() >= 10) {
                break;
            }
        }

        return recommended;
    }

    private List<KurskEventsParser.Event> getRecommendedEvents(SurveyActivity.RecommendationRequest request) {
        // Парсим события с сайта в фоновом потоке
        List<KurskEventsParser.Event> allEvents = KurskEventsParser.parseEvents("https://welcomekursk.ru/events", 50);
        List<KurskEventsParser.Event> recommended = new ArrayList<>();

        for (KurskEventsParser.Event event : allEvents) {
            if (matchesEventCategories(event, request.getEventCategories())) {
                recommended.add(event);
            }

            // Ограничиваем количество рекомендаций
            if (recommended.size() >= 10) {
                break;
            }
        }

        return recommended;
    }

    private boolean matchesAttractionCategories(Attraction attraction, List<String> selectedCategories) {
        if (selectedCategories.isEmpty()) return false;

        String attractionCategory = attraction.getCategories();
        if (attractionCategory == null) return false;

        // Приводим к нижнему регистру для сравнения
        String attractionCategoryLower = attractionCategory.toLowerCase();

        for (String selectedCategory : selectedCategories) {
            String selectedCategoryLower = selectedCategory.toLowerCase();
            if (attractionCategoryLower.contains(selectedCategoryLower)) {
                return true;
            }
        }
        return false;
    }

    private boolean matchesEventCategories(KurskEventsParser.Event event, List<String> selectedCategories) {
        if (selectedCategories.isEmpty()) return false;

        // Получаем данные из события
        String eventTitle = event.getTitle() != null ? event.getTitle().toLowerCase() : "";
        String eventDate = event.getDate() != null ? event.getDate().toLowerCase() : "";
        String eventPrice = event.getPrice() != null ? event.getPrice().toLowerCase() : "";

        // Объединяем все текстовые поля для поиска
        String eventText = eventTitle + " " + eventDate + " " + eventPrice;

        for (String category : selectedCategories) {
            String categoryLower = category.toLowerCase();

            // Ищем категорию в названии, дате или цене
            if (eventTitle.contains(categoryLower) ||
                    eventDate.contains(categoryLower) ||
                    eventPrice.contains(categoryLower) ||
                    matchesEventCategoryByTitle(eventTitle, categoryLower)) {
                return true;
            }
        }
        return false;
    }

    private boolean matchesEventCategoryByTitle(String eventTitle, String category) {
        // Сопоставление категорий по ключевым словам в названии события
        switch (category) {
            case "театр":
                return eventTitle.contains("театр") || eventTitle.contains("спектакль") ||
                        eventTitle.contains("постановка") || eventTitle.contains("драма");
            case "концерты":
                return eventTitle.contains("концерт") || eventTitle.contains("музык") ||
                        eventTitle.contains("групп") || eventTitle.contains("пев") ||
                        eventTitle.contains("оркестр");
            case "выставки":
                return eventTitle.contains("выставк") || eventTitle.contains("экспозиц") ||
                        eventTitle.contains("галерея") || eventTitle.contains("худож");
            case "фестивали":
                return eventTitle.contains("фестиваль") || eventTitle.contains("праздник") ||
                        eventTitle.contains("карнавал") || eventTitle.contains("народн");
            case "кино":
                return eventTitle.contains("кино") || eventTitle.contains("фильм") ||
                        eventTitle.contains("кинопоказ") || eventTitle.contains("премьер");
            case "спорт":
                return eventTitle.contains("спорт") || eventTitle.contains("соревнован") ||
                        eventTitle.contains("матч") || eventTitle.contains("турнир");
            case "образование":
                return eventTitle.contains("лекци") || eventTitle.contains("семинар") ||
                        eventTitle.contains("мастер-класс") || eventTitle.contains("обучен");
            case "развлечения":
                return eventTitle.contains("шоу") || eventTitle.contains("развлечен") ||
                        eventTitle.contains("аттракцион") || eventTitle.contains("игр");
            default:
                return eventTitle.contains(category);
        }
    }

    @Override
    protected void onDestroy() {
        if (dbHelper != null) {
            dbHelper.close();
        }
        super.onDestroy();
    }
}