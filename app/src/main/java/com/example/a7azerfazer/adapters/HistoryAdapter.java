package com.example.a7azerfazer.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a7azerfazer.R;
import com.example.a7azerfazer.models.QuizResult;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private List<QuizResult> resultList;

    public HistoryAdapter(List<QuizResult> resultList) {
        this.resultList = resultList;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        QuizResult result = resultList.get(position);

        // Nom de la catégorie
        holder.tvCategoryName.setText(result.getCategoryName());

        // Score
        holder.tvScore.setText("Score : " + result.getScore() + " / " + result.getTotal());

        // Pourcentage
        int percentage = (int) result.getPercentage();
        holder.tvPercentage.setText(percentage + "%");

        // Couleur du pourcentage selon le score
        if (percentage >= 80) {
            holder.tvPercentage.setTextColor(Color.parseColor("#4CAF50")); // Vert
        } else if (percentage >= 50) {
            holder.tvPercentage.setTextColor(Color.parseColor("#FF9800")); // Orange
        } else {
            holder.tvPercentage.setTextColor(Color.parseColor("#F44336")); // Rouge
        }

        // Date
        if (result.getTimestamp() != null) {
            Date date = result.getTimestamp().toDate();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            holder.tvDate.setText(sdf.format(date));
        }
    }

    @Override
    public int getItemCount() {
        return resultList.size();
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategoryName, tvScore, tvPercentage, tvDate;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tvHistoryCategoryName);
            tvScore = itemView.findViewById(R.id.tvHistoryScore);
            tvPercentage = itemView.findViewById(R.id.tvHistoryPercentage);
            tvDate = itemView.findViewById(R.id.tvHistoryDate);
        }
    }
}