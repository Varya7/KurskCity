package com.example.kurskcity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AttractionAdapter extends RecyclerView.Adapter<AttractionAdapter.AttractionViewHolder> {

    private List<Attraction> attractions;
    private boolean isHorizontal;

    public AttractionAdapter(List<Attraction> attractions, boolean isHorizontal) {
        this.attractions = attractions;
        this.isHorizontal = isHorizontal;
    }

    @NonNull
    @Override
    public AttractionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = isHorizontal ? R.layout.item_attraction_horizontal : R.layout.item_attraction_vertical;
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new AttractionViewHolder(view, isHorizontal);
    }

    @Override
    public void onBindViewHolder(@NonNull AttractionViewHolder holder, int position) {
        Attraction attraction = attractions.get(position);
        holder.bind(attraction);

        holder.itemView.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(v.getContext(), AttractionDetailActivity.class);
            intent.putExtra("attraction", attraction);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return attractions.size();
    }

    static class AttractionViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView titleTextView;
        private TextView categoryTextView;
        private TextView typeTextView;
        private TextView locationTextView;
        private boolean isHorizontal;

        public AttractionViewHolder(@NonNull View itemView, boolean isHorizontal) {
            super(itemView);
            this.isHorizontal = isHorizontal;

            // Общие элементы для обоих layout
            imageView = itemView.findViewById(R.id.pic);
            titleTextView = itemView.findViewById(R.id.titleTxt);
            categoryTextView = itemView.findViewById(R.id.categoryTxt);

            if (isHorizontal) {
                // Для горизонтального layout
                typeTextView = itemView.findViewById(R.id.typeTxt);
                locationTextView = itemView.findViewById(R.id.locationTxt);
            } else {
                // Для вертикального layout
                typeTextView = itemView.findViewById(R.id.typeTxt);
                locationTextView = itemView.findViewById(R.id.locationTxt);
            }
        }

        public void bind(Attraction attraction) {
            titleTextView.setText(attraction.getName());
            categoryTextView.setText(attraction.getCategories());

            // Загрузка изображения из BLOB
            if (attraction.getImage() != null && attraction.getImage().length > 0) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(attraction.getImage(), 0, attraction.getImage().length);
                imageView.setImageBitmap(bitmap);
            }

            // Устанавливаем тип и местоположение для обоих layout
            if (typeTextView != null) {
                typeTextView.setText(attraction.getType());
            }

            if (locationTextView != null) {
                locationTextView.setText(attraction.getLocation());
            }
        }
    }
}