package com.example.kurskcity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kurskcity.databinding.ActivityMainBinding;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private EventAdapter adapter;
    private FactAdapter factAdapter;
    private List<KurskEventsParser.Event> allEventsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Инициализация binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        EdgeToEdge.enable(this);

        setupFactsRecyclerView();
        initEvents();

        binding.btnSurvey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SurveyActivity.class);
                startActivity(intent);
            }
        });

        binding.btnPoster.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, EventActivity.class);
                startActivity(intent);
            }
        });

        binding.btnViewAllFacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AllFactsActivity.class);
                startActivity(intent);
            }
        });

        binding.btnViewAllAttractions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EventActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initEvents() {
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "Нет подключения к интернету", Toast.LENGTH_SHORT).show();
            binding.progressBarAttractions.setVisibility(View.GONE);
            return;
        }

        binding.progressBarAttractions.setVisibility(View.VISIBLE);

        new Thread(() -> {
            allEventsList = KurskEventsParser.parseEvents("https://welcomekursk.ru/events", 15);

            runOnUiThread(() -> {
                if (!allEventsList.isEmpty()) {
                    binding.recyclerViewEvent.setLayoutManager(new LinearLayoutManager(
                            MainActivity.this, LinearLayoutManager.HORIZONTAL, false
                    ));
                    adapter = new EventAdapter(allEventsList);
                    binding.recyclerViewEvent.setAdapter(adapter);
                } else {
                    Toast.makeText(MainActivity.this, "Достопримечательности не найдены", Toast.LENGTH_SHORT).show();
                }
                binding.progressBarAttractions.setVisibility(View.GONE);
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
}