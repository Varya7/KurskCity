package com.example.kurskcity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.kurskcity.databinding.ActivityAttractionDetailBinding;

public class AttractionDetailActivity extends AppCompatActivity {

    private ActivityAttractionDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAttractionDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Скрываем ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Получаем данные из Intent
        Attraction attraction = (Attraction) getIntent().getSerializableExtra("attraction");

        if (attraction != null) {
            setupViews(attraction);
        }

        setupBackButton();
    }

    private void setupViews(Attraction attraction) {
        // Устанавливаем изображение
        if (attraction.getImage() != null && attraction.getImage().length > 0) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(attraction.getImage(), 0, attraction.getImage().length);
            binding.pic.setImageBitmap(bitmap);
        }

        binding.titleTxt.setText(attraction.getName());
        binding.categoryTxt.setText(attraction.getCategories());
        binding.descriptionTxt.setText(attraction.getDescription());

    }

    private void setupBackButton() {
        binding.backBtn.setOnClickListener(v -> onBackPressed());
    }
}