package com.example.kurskcity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.kurskcity.databinding.ActivityMainBinding;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private EventAdapter eventAdapter;
    private FactAdapter factAdapter;
    private AttractionAdapter attractionAdapter;
    private List<KurskEventsParser.Event> allEventsList = new ArrayList<>();
    private AttractionsDbHelper dbHelper;

    private static final int REQUEST_CODE_RECOMMENDATIONS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Инициализация базы данных
        dbHelper = new AttractionsDbHelper(this);

        // Настройка всех RecyclerView
        setupAttractionsRecyclerView();
        setupFactsRecyclerView();
        initEvents();

        // Заменяем FAB маршрута на FAB рекомендаций
        binding.fabRecommendation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRecommendationSurvey();
            }
        });

        // Меняем иконку и описание FAB (убедитесь что ic_recommendation существует в res/drawable)
        try {
            binding.fabRecommendation.setImageResource(R.drawable.ic_recommendation);
        } catch (Exception e) {
            // Если иконка не найдена, используем стандартную
            binding.fabRecommendation.setImageResource(android.R.drawable.ic_menu_help);
        }
        binding.fabRecommendation.setContentDescription("Получить персонализированные рекомендации");

        binding.btnViewAllFacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AllFactsActivity.class);
                startActivity(intent);
            }
        });

        binding.btnViewAllEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EventActivity.class);
                startActivity(intent);
            }
        });

        binding.btnViewAllPlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AllAttractionsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void openRecommendationSurvey() {
        Intent intent = new Intent(MainActivity.this, SurveyActivity.class);
        startActivityForResult(intent, REQUEST_CODE_RECOMMENDATIONS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_RECOMMENDATIONS && resultCode == RESULT_OK && data != null) {
            SurveyActivity.RecommendationRequest request =
                    (SurveyActivity.RecommendationRequest) data.getSerializableExtra("recommendation_request");

            if (request != null) {
                // Получаем рекомендации на основе выбранных параметров
                getRecommendations(request);
            }
        }
    }

    private void getRecommendations(SurveyActivity.RecommendationRequest request) {
        // Здесь получаем рекомендации из базы данных
        List<Attraction> recommendedAttractions = getRecommendedAttractions(request);
        List<KurskEventsParser.Event> recommendedEvents = getRecommendedEvents(request);

        // Открываем активити с рекомендациями
        Intent intent = new Intent(this, RecommendationsActivity.class);
        intent.putExtra("recommendation_request", request);
        startActivity(intent);

        Toast.makeText(this, "Нашли " + recommendedAttractions.size() +
                        " достопримечательностей и " + recommendedEvents.size() + " мероприятий",
                Toast.LENGTH_SHORT).show();
    }

    private List<Attraction> getRecommendedAttractions(SurveyActivity.RecommendationRequest request) {
        List<Attraction> allAttractions = dbHelper.getAllAttractions(); // Используем getAllAttractions вместо getAttractionsForMainPage
        List<Attraction> recommended = new ArrayList<>();

        if (request.getAttractionCategories().isEmpty()) {
            return recommended;
        }

        for (Attraction attraction : allAttractions) {
            // Проверяем, подходит ли достопримечательность под выбранные категории
            if (matchesAttractionCategories(attraction, request.getAttractionCategories())) {
                recommended.add(attraction);
            }
        }

        return recommended;
    }

    private List<KurskEventsParser.Event> getRecommendedEvents(SurveyActivity.RecommendationRequest request) {
        List<KurskEventsParser.Event> recommended = new ArrayList<>();

        if (request.getEventCategories().isEmpty()) {
            return recommended;
        }

        for (KurskEventsParser.Event event : allEventsList) {
            // Проверяем категории мероприятий
            if (matchesEventCategories(event, request.getEventCategories())) {
                recommended.add(event);
            }
        }

        return recommended;
    }

    private boolean matchesAttractionCategories(Attraction attraction, List<String> selectedCategories) {
        String attractionCategory = attraction.getCategories();
        if (attractionCategory == null || selectedCategories.isEmpty()) return false;

        String attractionCategoryLower = attractionCategory.toLowerCase();

        for (String selectedCategory : selectedCategories) {
            if (attractionCategoryLower.contains(selectedCategory.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private boolean matchesEventCategories(KurskEventsParser.Event event, List<String> selectedCategories) {
        if (selectedCategories.isEmpty()) return false;

        String eventTitle = event.getTitle() != null ? event.getTitle().toLowerCase() : "";

        for (String category : selectedCategories) {
            String categoryLower = category.toLowerCase();

            // Проверяем только по названию, так как description может быть null
            if (eventTitle.contains(categoryLower)) {
                return true;
            }

            // Дополнительная проверка по ключевым словам
            if (matchesEventCategoryByKeywords(eventTitle, categoryLower)) {
                return true;
            }
        }
        return false;
    }

    private boolean matchesEventCategoryByKeywords(String eventTitle, String category) {
        // Сопоставление категорий по ключевым словам
        switch (category) {
            case "театр":
                return eventTitle.contains("театр") || eventTitle.contains("спектакль") ||
                        eventTitle.contains("постановка");
            case "концерты":
                return eventTitle.contains("концерт") || eventTitle.contains("музык") ||
                        eventTitle.contains("групп") || eventTitle.contains("пев");
            case "выставки":
                return eventTitle.contains("выставк") || eventTitle.contains("экспозиц") ||
                        eventTitle.contains("галерея");
            case "фестивали":
                return eventTitle.contains("фестиваль") || eventTitle.contains("праздник");
            case "кино":
                return eventTitle.contains("кино") || eventTitle.contains("фильм") ||
                        eventTitle.contains("кинопоказ");
            case "спорт":
                return eventTitle.contains("спорт") || eventTitle.contains("соревнован") ||
                        eventTitle.contains("матч");
            case "образование":
                return eventTitle.contains("лекци") || eventTitle.contains("семинар") ||
                        eventTitle.contains("мастер-класс");
            case "развлечения":
                return eventTitle.contains("шоу") || eventTitle.contains("развлечен") ||
                        eventTitle.contains("игр");
            default:
                return eventTitle.contains(category);
        }
    }

    private void setupAttractionsRecyclerView() {
        List<Attraction> attractions = dbHelper.getAttractionsForMainPage(10); // 10 для главного экрана

        if (attractions.isEmpty()) {
            Toast.makeText(this, "Достопримечательности не найдены", Toast.LENGTH_SHORT).show();
            return;
        }

        attractionAdapter = new AttractionAdapter(attractions, true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                this, LinearLayoutManager.HORIZONTAL, false
        );

        binding.rvAttractions.setLayoutManager(layoutManager);
        binding.rvAttractions.setAdapter(attractionAdapter);
    }

    private void initEvents() {
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "Нет подключения к интернету", Toast.LENGTH_SHORT).show();
            binding.progressBarEvents.setVisibility(View.GONE);
            return;
        }

        binding.progressBarEvents.setVisibility(View.VISIBLE);

        new Thread(() -> {
            allEventsList = KurskEventsParser.parseEvents("https://welcomekursk.ru/events", 10);

            runOnUiThread(() -> {
                if (!allEventsList.isEmpty()) {
                    binding.recyclerViewEvent.setLayoutManager(new LinearLayoutManager(
                            MainActivity.this, LinearLayoutManager.HORIZONTAL, false
                    ));
                    eventAdapter = new EventAdapter(allEventsList);
                    binding.recyclerViewEvent.setAdapter(eventAdapter);
                } else {
                    Toast.makeText(MainActivity.this, "Мероприятия не найдены", Toast.LENGTH_SHORT).show();
                }
                binding.progressBarEvents.setVisibility(View.GONE);
            });
        }).start();
    }

    private void setupFactsRecyclerView() {
        List<Fact> facts = getSampleFacts();
        factAdapter = new FactAdapter(facts, true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(
                this, LinearLayoutManager.HORIZONTAL, false
        );

        binding.rvFacts.setLayoutManager(layoutManager);
        binding.rvFacts.setAdapter(factAdapter);
    }

    private List<Fact> getSampleFacts() {
        return Arrays.asList(
                new Fact(
                        "История Курска",
                        "Город был основан в 1032 году...",
                        "Курск был основан в 1032 году и впервые упоминается в Житии Феодосия Печерского. Город имеет богатую историю, был важным оборонительным пунктом против набегов кочевников."
                ),
                new Fact(
                        "Курская битва",
                        "Одно из ключевых сражений ВОВ...",
                        "Курская битва (1943 год) стала одним из ключевых сражений Великой Отечественной войны. Это было крупнейшее танковое сражение в истории."
                ),
                new Fact(
                        "Курские соловьи",
                        "Символ города и его жителей...",
                        "Курские соловьи известны на всю Россию своими прекрасными трелями. Они стали символом города, а жителей Курска иногда называют \"курскими соловьями\"."
                ),
                new Fact(
                        "Коренная пустынь",
                        "Знаменитый монастырь...",
                        "Коренная пустынь - один из самых известных монастырей России. Ежегодно здесь проходит крестный ход с Курской Коренной иконой Божией Матери."
                )
        );
    }

    private boolean isNetworkAvailable() {
        try {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        } catch (Exception e) {
            return false;
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