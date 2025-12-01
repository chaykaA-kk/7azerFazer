package com.example.a7azerfazer.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a7azerfazer.R;
import com.example.a7azerfazer.models.Category;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<Category> categoryList;
    private OnCategoryClickListener listener;

    // Interface pour gérer les clics
    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    // Constructeur
    public CategoryAdapter(List<Category> categoryList, OnCategoryClickListener listener) {
        this.categoryList = categoryList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Créer la vue à partir du layout item_category.xml
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        // Récupérer la catégorie à cette position
        Category category = categoryList.get(position);

        // Remplir les données dans la vue
        holder.tvCategoryName.setText(category.getName());
        holder.tvCategoryDescription.setText(category.getDescription());

        // Gérer le clic sur la catégorie
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onCategoryClick(category);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        // Retourner le nombre de catégories
        return categoryList.size();
    }

    // ViewHolder : contient les références aux vues
    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategoryName;
        TextView tvCategoryDescription;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            tvCategoryDescription = itemView.findViewById(R.id.tvCategoryDescription);
        }
    }
}