package com.example.kurskcity;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class RecommendationsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ATTRACTION = 0;
    private static final int TYPE_EVENT = 1;

    private List<RecommendationItem> items;

    public RecommendationsAdapter(List<RecommendationItem> items) {
        this.items = items;
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).isAttraction() ? TYPE_ATTRACTION : TYPE_EVENT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == TYPE_ATTRACTION) {
            View view = inflater.inflate(R.layout.item_attraction_vertical, parent, false);
            return new AttractionViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.viewholder_events, parent, false);
            return new EventViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        RecommendationItem item = items.get(position);

        if (holder instanceof AttractionViewHolder && item.isAttraction()) {
            ((AttractionViewHolder) holder).bind(item.getAttraction());
        } else if (holder instanceof EventViewHolder && item.isEvent()) {
            ((EventViewHolder) holder).bind(item.getEvent());
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void updateData(List<RecommendationItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    // ViewHolder для достопримечательностей (использует item_attraction_vertical)
    static class AttractionViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView titleTextView;
        private TextView categoryTextView;
        private TextView typeTextView;
        private TextView locationTextView;

        public AttractionViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.pic);
            titleTextView = itemView.findViewById(R.id.titleTxt);
            categoryTextView = itemView.findViewById(R.id.categoryTxt);
            typeTextView = itemView.findViewById(R.id.typeTxt);
            locationTextView = itemView.findViewById(R.id.locationTxt);
        }

        public void bind(Attraction attraction) {
            titleTextView.setText(attraction.getName());
            categoryTextView.setText(attraction.getCategories());

            // Используем фиктивные данные для type и location
            typeTextView.setText("Достопримечательность");
            locationTextView.setText("Курск");

            // Загрузка изображения из BLOB
            if (attraction.getImage() != null && attraction.getImage().length > 0) {
                android.graphics.Bitmap bitmap = android.graphics.BitmapFactory.decodeByteArray(
                        attraction.getImage(), 0, attraction.getImage().length);
                imageView.setImageBitmap(bitmap);
            }

            // Обработчик клика - открываем детальную страницу достопримечательности
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(itemView.getContext(), AttractionDetailActivity.class);
                intent.putExtra("attraction", attraction);
                itemView.getContext().startActivity(intent);
            });
        }
    }

    // ViewHolder для мероприятий (использует item_events)
    static class EventViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView titleTextView;
        private TextView dateTextView;
        private TextView priceTextView;
        private TextView categoryTextView;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.pic);
            titleTextView = itemView.findViewById(R.id.titleTxt);
            dateTextView = itemView.findViewById(R.id.dateTxt);
            priceTextView = itemView.findViewById(R.id.priceTxt);
            categoryTextView = itemView.findViewById(R.id.categoryTxt);
        }

        public void bind(KurskEventsParser.Event event) {
            titleTextView.setText(event.getTitle());
            dateTextView.setText(event.getDate());
            priceTextView.setText(event.getPrice());
            categoryTextView.setText(event.getCategory());

            // Загрузка изображения мероприятия
            if (event.getImageUrl() != null && !event.getImageUrl().isEmpty()) {
                // Используем Glide для загрузки изображения
                try {
                    Glide.with(itemView.getContext())
                            .load(event.getImageUrl())
                            .into(imageView);
                } catch (Exception e) {
                    // Если Glide не доступен, оставляем стандартное изображение
                }
            }

            // Обработчик клика - открываем детальную страницу мероприятия
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(itemView.getContext(), Detail_EventActivity.class);
                intent.putExtra("object", event);
                itemView.getContext().startActivity(intent);
            });
        }
    }
}