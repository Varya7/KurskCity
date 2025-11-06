package com.example.kurskcity;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class RecommendationsActivity extends AppCompatActivity {

    private RecyclerView rvRecommendations;
    private ProgressBar progressBar;
    private TextView tvTitle, tvEmpty;
    private AttractionsDbHelper dbHelper;
    private RecommendationsAdapter recommendationsAdapter;
    private List<RecommendationItem> recommendationItems = new ArrayList<>();

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
        tvEmpty = findViewById(R.id.tv_empty);
        progressBar = findViewById(R.id.progress_bar);
        rvRecommendations = findViewById(R.id.rv_recommendations);

        rvRecommendations.setLayoutManager(new LinearLayoutManager(this));
        progressBar.setVisibility(View.VISIBLE);
    }

    private void setupRecommendations() {
        SurveyActivity.RecommendationRequest request =
                (SurveyActivity.RecommendationRequest) getIntent().getSerializableExtra("recommendation_request");

        if (request != null) {
            String dateText = request.getDate().isEmpty() ? "" : " на " + request.getDate();
            tvTitle.setText("Ваши рекомендации" + dateText);

            loadRecommendations(request);
        } else {
            progressBar.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        }
    }

    private void loadRecommendations(SurveyActivity.RecommendationRequest request) {
        new Thread(() -> {
            try {
                List<Attraction> recommendedAttractions = getRecommendedAttractions(request);
                List<KurskEventsParser.Event> recommendedEvents = getRecommendedEvents(request);

                recommendationItems = combineRecommendations(recommendedAttractions, recommendedEvents);

                runOnUiThread(() -> {
                    // Создаем и устанавливаем адаптер
                    recommendationsAdapter = new RecommendationsAdapter(recommendationItems);
                    rvRecommendations.setAdapter(recommendationsAdapter);

                    progressBar.setVisibility(View.GONE);

                    if (recommendationItems.isEmpty()) {
                        tvEmpty.setVisibility(View.VISIBLE);
                        rvRecommendations.setVisibility(View.GONE);
                    } else {
                        tvEmpty.setVisibility(View.GONE);
                        rvRecommendations.setVisibility(View.VISIBLE);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    tvEmpty.setVisibility(View.VISIBLE);
                    rvRecommendations.setVisibility(View.GONE);
                    tvEmpty.setText("Ошибка загрузки рекомендаций");
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
        }

        return recommended;
    }

    private List<KurskEventsParser.Event> getRecommendedEvents(SurveyActivity.RecommendationRequest request) {
        if (request.getEventCategories().isEmpty()) {
            return new ArrayList<>();
        }

        // Парсим события с сайта
        List<KurskEventsParser.Event> allEvents = KurskEventsParser.parseEvents("https://welcomekursk.ru/events", 50);
        List<KurskEventsParser.Event> recommended = new ArrayList<>();

        for (KurskEventsParser.Event event : allEvents) {
            if (matchesEventCategories(event, request.getEventCategories())) {
                recommended.add(event);
            }
        }

        return recommended;
    }

    private List<RecommendationItem> combineRecommendations(List<Attraction> attractions, List<KurskEventsParser.Event> events) {
        List<RecommendationItem> combined = new ArrayList<>();

        for (Attraction attraction : attractions) {
            combined.add(new RecommendationItem(attraction));
        }

        for (KurskEventsParser.Event event : events) {
            combined.add(new RecommendationItem(event));
        }

        return combined;
    }

    private boolean matchesAttractionCategories(Attraction attraction, List<String> selectedCategories) {
        if (selectedCategories.isEmpty()) return false;

        String attractionCategory = attraction.getCategories();
        if (attractionCategory == null) return false;

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

        String eventTitle = event.getTitle() != null ? event.getTitle().toLowerCase() : "";
        String eventCategory = event.getCategory() != null ? event.getCategory().toLowerCase() : "";

        for (String category : selectedCategories) {
            String categoryLower = category.toLowerCase();

            if (eventTitle.contains(categoryLower) ||
                    eventCategory.contains(categoryLower) ||
                    matchesEventCategoryByTitle(eventTitle, categoryLower)) {
                return true;
            }
        }
        return false;
    }

    private boolean matchesEventCategoryByTitle(String eventTitle, String category) {
        switch (category) {
            case "театр":
                return eventTitle.contains("театр") || eventTitle.contains("спектакль");
            case "концерты":
                return eventTitle.contains("концерт") || eventTitle.contains("музык");
            case "выставки":
                return eventTitle.contains("выставк") || eventTitle.contains("экспозиц");
            case "фестивали":
                return eventTitle.contains("фестиваль") || eventTitle.contains("праздник");
            case "кино":
                return eventTitle.contains("кино") || eventTitle.contains("фильм");
            case "спорт":
                return eventTitle.contains("спорт") || eventTitle.contains("матч");
            case "образование":
                return eventTitle.contains("лекци") || eventTitle.contains("семинар");
            case "развлечения":
                return eventTitle.contains("шоу") || eventTitle.contains("развлечен");
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