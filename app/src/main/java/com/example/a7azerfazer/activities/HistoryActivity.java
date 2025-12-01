package com.example.a7azerfazer.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a7azerfazer.R;
import com.example.a7azerfazer.adapters.HistoryAdapter;
import com.example.a7azerfazer.models.QuizResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private static final String TAG = "HistoryActivity";

    private RecyclerView recyclerView;
    private LinearLayout tvNoHistory;  // Changed to LinearLayout
    private TextView btnBack;
    private HistoryAdapter adapter;
    private List<QuizResult> resultList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_history);
            Log.d(TAG, "HistoryActivity created");

            // Initialize Firebase
            db = FirebaseFirestore.getInstance();
            mAuth = FirebaseAuth.getInstance();

            // Check if user is logged in
            if (mAuth.getCurrentUser() == null) {
                Log.e(TAG, "No user logged in");
                Toast.makeText(this, "Utilisateur non connecté", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            // Find views
            recyclerView = findViewById(R.id.recyclerViewHistory);
            tvNoHistory = findViewById(R.id.tvNoHistory);  // This is LinearLayout in new layout
            btnBack = findViewById(R.id.btnBack);

            if (recyclerView == null) {
                Log.e(TAG, "recyclerView is NULL!");
                Toast.makeText(this, "Error: RecyclerView not found", Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            if (tvNoHistory == null) {
                Log.e(TAG, "tvNoHistory is NULL!");
            }

            // Back button
            if (btnBack != null) {
                btnBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "Back button clicked");
                        finish();
                    }
                });
            }

            // Initialize list
            resultList = new ArrayList<>();

            // Initialize adapter
            adapter = new HistoryAdapter(resultList);

            // Configure RecyclerView
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);

            Log.d(TAG, "RecyclerView configured successfully");

            // Load history
            loadHistory();

        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate", e);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void loadHistory() {
        if (mAuth.getCurrentUser() == null) {
            Log.e(TAG, "User not connected");
            Toast.makeText(this, "Utilisateur non connecté", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        Log.d(TAG, "Loading history for userId: " + userId);

        // Show loading state
        if (tvNoHistory != null) {
            tvNoHistory.setVisibility(View.GONE);
        }
        recyclerView.setVisibility(View.VISIBLE);

        // Query Firestore
        db.collection("results")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            resultList.clear();

                            Log.d(TAG, "Number of documents found: " + task.getResult().size());

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                try {
                                    Log.d(TAG, "Document ID: " + document.getId());
                                    Log.d(TAG, "Document data: " + document.getData());

                                    QuizResult result = document.toObject(QuizResult.class);
                                    result.setResultId(document.getId());
                                    resultList.add(result);

                                    Log.d(TAG, "Result added: " + result.getCategoryName() +
                                            " - Score: " + result.getScore());
                                } catch (Exception e) {
                                    Log.e(TAG, "Error converting document: " + document.getId(), e);
                                }
                            }

                            // Sort by date (most recent first)
                            try {
                                Collections.sort(resultList, new Comparator<QuizResult>() {
                                    @Override
                                    public int compare(QuizResult r1, QuizResult r2) {
                                        if (r1.getTimestamp() == null || r2.getTimestamp() == null) {
                                            return 0;
                                        }
                                        return r2.getTimestamp().compareTo(r1.getTimestamp());
                                    }
                                });
                            } catch (Exception e) {
                                Log.e(TAG, "Error sorting results", e);
                            }

                            if (resultList.isEmpty()) {
                                // No history
                                Log.d(TAG, "No results found");
                                recyclerView.setVisibility(View.GONE);
                                if (tvNoHistory != null) {
                                    tvNoHistory.setVisibility(View.VISIBLE);
                                }
                            } else {
                                // Show history
                                Log.d(TAG, "Displaying " + resultList.size() + " results");
                                recyclerView.setVisibility(View.VISIBLE);
                                if (tvNoHistory != null) {
                                    tvNoHistory.setVisibility(View.GONE);
                                }
                                adapter.notifyDataSetChanged();
                            }

                        } else {
                            Log.e(TAG, "Error loading history", task.getException());

                            String errorMessage = "Erreur de chargement de l'historique";
                            if (task.getException() != null) {
                                errorMessage += ": " + task.getException().getMessage();
                            }

                            Toast.makeText(HistoryActivity.this,
                                    errorMessage,
                                    Toast.LENGTH_LONG).show();

                            // Show empty state on error
                            recyclerView.setVisibility(View.GONE);
                            if (tvNoHistory != null) {
                                tvNoHistory.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });
    }
}