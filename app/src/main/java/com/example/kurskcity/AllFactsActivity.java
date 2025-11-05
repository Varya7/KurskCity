package com.example.kurskcity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Arrays;
import java.util.List;

public class AllFactsActivity extends AppCompatActivity {

    private RecyclerView factsRecyclerView;
    private FactAdapter factAdapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_facts);

        factsRecyclerView = findViewById(R.id.rv_all_facts);
        setupFactsRecyclerView();
    }

    private void setupFactsRecyclerView() {
        List<Fact> facts = getAllFacts();
        factAdapter = new FactAdapter(facts, false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        factsRecyclerView.setLayoutManager(layoutManager);
        factsRecyclerView.setAdapter(factAdapter);
    }

    private List<Fact> getAllFacts() {
        return Arrays.asList(
                new Fact("История Курска", "Город был основан в 1032 году...", "Полная история..."),
                new Fact("Курская битва", "Одно из ключевых сражений ВОВ...", "Полное описание..."),
                // Добавьте остальные факты
                new Fact("Курская антоновка", "Знаменитый сорт яблок...", "Курская антоновка - один из самых известных сортов яблок в России, известный своим особым вкусом и ароматом."),
                new Fact("Триумфальная арка", "Символ победы в Курской битве...", "Триумфальная арка была установлена в Курске в честь победы в Курской битве и стала одним из символов города."),
                new Fact("Река Сейм", "Главная водная артерия...", "Река Сейм протекает через Курск и является главной водной артерией города, придающей ему особое очарование.")
        );
    }
}