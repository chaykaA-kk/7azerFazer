package com.example.a7azerfazer.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
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

    private TextView tvWelcome;
    private Button btnStartQuiz, btnViewHistory, btnLogout;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish();
            return;
        }

        tvWelcome = findViewById(R.id.tvWelcome);
        btnStartQuiz = findViewById(R.id.btnStartQuiz);
        btnViewHistory = findViewById(R.id.btnViewHistory);
        btnLogout = findViewById(R.id.btnLogout);

        loadUsername(currentUser.getUid());

        // Bouton Commencer un Quiz
        btnStartQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, CategoryActivity.class);
                startActivity(intent);
            }
        });

        // Bouton Voir l'historique
        btnViewHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, HistoryActivity.class);
                startActivity(intent);
            }
        });

        // Bouton Déconnexion
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

    private void loadUsername(String userId) {
        db.collection("users").document(userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();

                            if (document != null && document.exists()) {
                                String username = document.getString("username");

                                if (username != null && !username.isEmpty()) {
                                    tvWelcome.setText("Bienvenue, " + username + " !");
                                } else {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if (user != null) {
                                        tvWelcome.setText("Bienvenue, " + user.getEmail());
                                    }
                                }
                            } else {
                                tvWelcome.setText("Bienvenue !");
                            }
                        } else {
                            Toast.makeText(HomeActivity.this,
                                    "Erreur lors du chargement du profil",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}