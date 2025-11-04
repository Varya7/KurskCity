package com.example.kurskcity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.example.kurskcity.databinding.ActivityEventBinding;

import java.util.ArrayList;

/**
 * Активность для отображения списка событий. Позволяет просматривать мероприятия,
 * фильтровать их по категориям и выполнять поиск.
 * Также активность включает в себя нижнее навигационное меню для перехода к другим экранам.
 */
public class EventActivity extends BaseActivity {
    private ActivityEventBinding binding;
    private EventsAdapter adapter;
    private ArrayList<KurskEventsParser.Event> eventList = new ArrayList<>();

    /**
     * Инициализирует активность, загружает список категорий и настраивает поиск и навигацию
     * @param savedInstanceState Сохраненное состояние активности (может быть null)
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEventBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initEvents();
        setupSearchListener();
        enableImmersiveMode();

    }

    /**
     * Скрывает системную навигацию (включает иммерсивный режим).
     */

    private void enableImmersiveMode() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
    }

    /**
     * Загружает список событий с сайта в фоновом режиме.
     * После загружки обновляет RecyclerView.
     */


    private void initEvents() {
        binding.progressBarEvent.setVisibility(View.VISIBLE);

        new Thread(() -> {
            eventList.addAll(KurskEventsParser.parseEvents("https://welcomekursk.ru/events", 100));

            runOnUiThread(() -> {
                if (!eventList.isEmpty()) {
                    binding.RecyclerViewEvents.setLayoutManager(new LinearLayoutManager(this));
                    adapter = new EventsAdapter(eventList);
                    binding.RecyclerViewEvents.setAdapter(adapter);
                }
                binding.progressBarEvent.setVisibility(View.GONE);
            });
        }).start();
    }

    /**
     * Фильтрует события по категориям.
     * @param category выбранная пользовтелем категория.
     */

    private void filterEventsByCategory(String category) {
        if (adapter != null) {
            adapter.filterByCategory(category);
        }
    }

    /**
     * Настраивает слушатель текста для реализации поиска событий
     * в реальном времени.
     */

    private void setupSearchListener() {
        binding.editTextText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (adapter != null) {
                    adapter.filter(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
}