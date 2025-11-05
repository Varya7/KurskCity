package com.example.kurskcity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class FactDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fact_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Fact fact = (Fact) getIntent().getSerializableExtra("fact");

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView titleTextView = findViewById(R.id.tv_fact_detail_title);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView contentTextView = findViewById(R.id.tv_fact_detail_content);

        titleTextView.setText(fact.getTitle());
        contentTextView.setText(fact.getFullContent());

        toolbar.setTitle(fact.getTitle());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}