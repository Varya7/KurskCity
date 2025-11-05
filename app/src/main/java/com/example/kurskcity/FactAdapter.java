package com.example.kurskcity;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class FactAdapter extends RecyclerView.Adapter<FactAdapter.FactViewHolder> {

    private List<Fact> facts;
    private boolean isHorizontal;

    public FactAdapter(List<Fact> facts, boolean isHorizontal) {
        this.facts = facts;
        this.isHorizontal = isHorizontal;
    }

    @NonNull
    @Override
    public FactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = isHorizontal ? R.layout.item_fact_horizontal : R.layout.item_fact_vertical;
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new FactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FactViewHolder holder, int position) {
        Fact fact = facts.get(position);
        holder.bind(fact);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), FactDetailActivity.class);
            intent.putExtra("fact", fact);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return facts.size();
    }

    static class FactViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView;
        private TextView descriptionTextView;

        public FactViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.tv_fact_title);
            descriptionTextView = itemView.findViewById(R.id.tv_fact_description);
        }

        public void bind(Fact fact) {
            titleTextView.setText(fact.getTitle());
            descriptionTextView.setText(fact.getDescription());
        }
    }
}