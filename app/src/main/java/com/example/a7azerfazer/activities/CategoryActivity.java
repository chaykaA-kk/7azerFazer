package com.example.a7azerfazer.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.a7azerfazer.R;
import com.example.a7azerfazer.adapters.CategoryAdapter;
import com.example.a7azerfazer.models.Category;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity implements CategoryAdapter.OnCategoryClickListener {

    private RecyclerView recyclerView;
    private CategoryAdapter adapter;
    private List<Category> categoryList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        // Initialiser Firestore
        db = FirebaseFirestore.getInstance();

        // Initialiser la liste
        categoryList = new ArrayList<>();

        // Initialiser l'adapter
        adapter = new CategoryAdapter(categoryList, this);

        // Configurer le RecyclerView
        recyclerView = findViewById(R.id.recyclerViewCategories);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Charger les catégories
        loadCategories();
    }

    private void loadCategories() {
        db.collection("categories")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            categoryList.clear();

                            // Parcourir tous les documents
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Category category = document.toObject(Category.class);
                                categoryList.add(category);
                            }

                            // Notifier l'adapter que les données ont changé
                            adapter.notifyDataSetChanged();

                            Toast.makeText(CategoryActivity.this,
                                    categoryList.size() + " catégories chargées",
                                    Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(CategoryActivity.this,
                                    "Erreur de chargement des catégories",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onCategoryClick(Category category) {
        // Aller vers QuizActivity avec categoryId et categoryName
        Intent intent = new Intent(CategoryActivity.this, QuizActivity.class);
        intent.putExtra("categoryId", category.getCategoryId());
        intent.putExtra("categoryName", category.getName());
        startActivity(intent);
    }
}