package com.example.kurskcity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Arrays;
import java.util.List;

public class AllFactsActivity extends AppCompatActivity {

    private RecyclerView factsRecyclerView;
    private FactAdapter factAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_facts);

        // Скрываем ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

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
                new Fact("История Курска", "Город был основан в 1032 году...",
                        "Курск был основан в 1032 году и впервые упоминается в Житии Феодосия Печерского. Город имеет богатую историю, был важным оборонительным пунктом против набегов кочевников."),
                new Fact("Курская битва", "Одно из ключевых сражений ВОВ...",
                        "Курская битва (1943 год) стала одним из ключевых сражений Великой Отечественной войны. Это было крупнейшее танковое сражение в истории, в котором участвовало более 6000 танков."),
                new Fact("Курские соловьи", "Символ города и его жителей...",
                        "Курские соловьи известны на всю Россию своими прекрасными трелями. Они стали символом города, а жителей Курска иногда называют \"курскими соловьями\"."),
                new Fact("Коренная пустынь", "Знаменитый монастырь...",
                        "Коренная пустынь - один из самых известных монастырей России. Ежегодно здесь проходит крестный ход с Курской Коренной иконой Божией Матери."),
                new Fact("Курская антоновка", "Знаменитый сорт яблок...",
                        "Курская антоновка - один из самых известных сортов яблок в России, известный своим особым вкусом и ароматом."),
                new Fact("Триумфальная арка", "Символ победы в Курской битве...",
                        "Триумфальная арка была установлена в Курске в честь победы в Курской битве и стала одним из символов города."),
                new Fact("Река Сейм", "Главная водная артерия...",
                        "Река Сейм протекает через Курск и является главной водной артерией города, придающей ему особое очарование.")
        );
    }
}