package com.example.kurskcity;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.kurskcity.databinding.ActivityAllAttractionsBinding;
import java.util.List;

public class AllAttractionsActivity extends AppCompatActivity {

    private ActivityAllAttractionsBinding binding;
    private AttractionAdapter attractionAdapter;
    private AttractionsDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAllAttractionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Скрываем ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        dbHelper = new AttractionsDbHelper(this);
        setupRecyclerView();
        setupBackButton();
    }

    private void setupRecyclerView() {
        List<Attraction> attractions = dbHelper.getAllAttractions();

        if (attractions.isEmpty()) {
            Toast.makeText(this, "Достопримечательности не найдены", Toast.LENGTH_SHORT).show();
            return;
        }

        attractionAdapter = new AttractionAdapter(attractions, false); // false для вертикального режима
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.recyclerViewAttractions.setLayoutManager(layoutManager);
        binding.recyclerViewAttractions.setAdapter(attractionAdapter);
    }

    private void setupBackButton() {
        binding.backBtn.setOnClickListener(v -> onBackPressed());
    }

    @Override
    protected void onDestroy() {
        if (dbHelper != null) {
            dbHelper.close();
        }
        super.onDestroy();
    }
}