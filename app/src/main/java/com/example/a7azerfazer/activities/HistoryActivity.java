package com.example.a7azerfazer.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
    private TextView tvNoHistory;
    private HistoryAdapter adapter;
    private List<QuizResult> resultList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        Log.d(TAG, "HistoryActivity créée");

        // Initialiser Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Lier les éléments
        recyclerView = findViewById(R.id.recyclerViewHistory);
        tvNoHistory = findViewById(R.id.tvNoHistory);

        // Initialiser la liste
        resultList = new ArrayList<>();

        // Initialiser l'adapter
        adapter = new HistoryAdapter(resultList);

        // Configurer le RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        Log.d(TAG, "RecyclerView configuré");

        // Charger l'historique
        loadHistory();
    }

    private void loadHistory() {
        if (mAuth.getCurrentUser() == null) {
            Log.e(TAG, "Utilisateur non connecté");
            Toast.makeText(this, "Utilisateur non connecté", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        Log.d(TAG, "Chargement de l'historique pour userId: " + userId);

        // Requête Firestore sans orderBy
        db.collection("results")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            resultList.clear();

                            Log.d(TAG, "Nombre de documents trouvés: " + task.getResult().size());

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                try {
                                    Log.d(TAG, "Document ID: " + document.getId());
                                    Log.d(TAG, "Document data: " + document.getData());

                                    QuizResult result = document.toObject(QuizResult.class);
                                    result.setResultId(document.getId());
                                    resultList.add(result);

                                    Log.d(TAG, "Résultat ajouté: " + result.getCategoryName() +
                                            " - Score: " + result.getScore());
                                } catch (Exception e) {
                                    Log.e(TAG, "Erreur lors de la conversion du document: " + document.getId(), e);
                                }
                            }

                            // Trier par date (plus récent en premier)
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
                                Log.e(TAG, "Erreur lors du tri", e);
                            }

                            if (resultList.isEmpty()) {
                                // Pas d'historique
                                Log.d(TAG, "Aucun résultat trouvé");
                                recyclerView.setVisibility(View.GONE);
                                tvNoHistory.setVisibility(View.VISIBLE);
                            } else {
                                // Afficher l'historique
                                Log.d(TAG, "Affichage de " + resultList.size() + " résultats");
                                recyclerView.setVisibility(View.VISIBLE);
                                tvNoHistory.setVisibility(View.GONE);
                                adapter.notifyDataSetChanged();
                            }

                        } else {
                            Log.e(TAG, "Erreur lors du chargement", task.getException());

                            String errorMessage = "Erreur de chargement de l'historique";
                            if (task.getException() != null) {
                                errorMessage += ": " + task.getException().getMessage();
                            }

                            Toast.makeText(HistoryActivity.this,
                                    errorMessage,
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}