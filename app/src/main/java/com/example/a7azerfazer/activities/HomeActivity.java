package com.example.a7azerfazer.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a7azerfazer.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";

    private TextView tvWelcome;
    private Button btnStartQuiz, btnViewHistory, btnLogout;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Log.d(TAG, "HomeActivity onCreate started");

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Log.e(TAG, "No current user, redirecting to login");
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish();
            return;
        }

        // Initialize views
        try {
            tvWelcome = findViewById(R.id.tvWelcome);
            btnStartQuiz = findViewById(R.id.btnStartQuiz);
            btnViewHistory = findViewById(R.id.btnViewHistory);
            btnLogout = findViewById(R.id.btnLogout);

            Log.d(TAG, "All views initialized successfully");

            // Check if buttons are null
            if (btnStartQuiz == null) {
                Log.e(TAG, "btnStartQuiz is NULL!");
            }
            if (btnViewHistory == null) {
                Log.e(TAG, "btnViewHistory is NULL!");
            }
            if (btnLogout == null) {
                Log.e(TAG, "btnLogout is NULL!");
            }

        } catch (Exception e) {
            Log.e(TAG, "Error initializing views", e);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        loadUsername(currentUser.getUid());

        // Bouton Commencer un Quiz
        if (btnStartQuiz != null) {
            btnStartQuiz.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Start Quiz button clicked");
                    try {
                        Intent intent = new Intent(HomeActivity.this, CategoryActivity.class);
                        startActivity(intent);
                        Log.d(TAG, "CategoryActivity started successfully");
                    } catch (Exception e) {
                        Log.e(TAG, "Error starting CategoryActivity", e);
                        Toast.makeText(HomeActivity.this,
                                "Error: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        // Bouton Voir l'historique
        if (btnViewHistory != null) {
            btnViewHistory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "View History button clicked");
                    try {
                        Intent intent = new Intent(HomeActivity.this, HistoryActivity.class);
                        startActivity(intent);
                        Log.d(TAG, "HistoryActivity started successfully");
                    } catch (Exception e) {
                        Log.e(TAG, "Error starting HistoryActivity", e);
                        Toast.makeText(HomeActivity.this,
                                "Error: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            Log.e(TAG, "btnViewHistory is null, cannot set click listener!");
        }

        // Bouton Déconnexion
        if (btnLogout != null) {
            btnLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Logout button clicked");
                    mAuth.signOut();
                    Toast.makeText(HomeActivity.this,
                            "Déconnexion réussie",
                            Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }

        Log.d(TAG, "HomeActivity onCreate completed");
    }

    private void loadUsername(String userId) {
        Log.d(TAG, "Loading username for userId: " + userId);

        db.collection("users").document(userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();

                            if (document != null && document.exists()) {
                                String username = document.getString("username");
                                Log.d(TAG, "Username retrieved: " + username);

                                if (username != null && !username.isEmpty()) {
                                    tvWelcome.setText("Bienvenue, " + username + " !");
                                } else {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if (user != null) {
                                        tvWelcome.setText("Bienvenue, " + user.getEmail());
                                    }
                                }
                            } else {
                                Log.w(TAG, "User document does not exist");
                                tvWelcome.setText("Bienvenue !");
                            }
                        } else {
                            Log.e(TAG, "Error loading username", task.getException());
                            Toast.makeText(HomeActivity.this,
                                    "Erreur lors du chargement du profil",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}