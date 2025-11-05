package com.example.kurskcity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button surveyButton, posterButton;
    private TextView viewAllFactsButton;
    private RecyclerView factsRecyclerView;
    private FactAdapter factAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        surveyButton = findViewById(R.id.btn_survey);
        posterButton = findViewById(R.id.btn_poster);
        viewAllFactsButton = findViewById(R.id.btn_view_all_facts);
        factsRecyclerView = findViewById(R.id.rv_facts);

        setupFactsRecyclerView();

        surveyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SurveyActivity.class);
                startActivity(intent);
            }
        });

        posterButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, EventActivity.class);
                startActivity(intent);
            }
        });

        viewAllFactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AllFactsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setupFactsRecyclerView() {
        List<Fact> facts = getSampleFacts();
        factAdapter = new FactAdapter(facts, true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(
                this, LinearLayoutManager.HORIZONTAL, false
        );

        factsRecyclerView.setLayoutManager(layoutManager);
        factsRecyclerView.setAdapter(factAdapter);
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
}