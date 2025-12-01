package com.example.a7azerfazer.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.example.a7azerfazer.R;
public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Initialiser Firebase
        mAuth = FirebaseAuth.getInstance();

        // Attendre 2 secondes
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Vérifier si l'utilisateur est déjà connecté
                FirebaseUser currentUser = mAuth.getCurrentUser();

                Intent intent;
                if (currentUser != null) {
                    // Déjà connecté → Aller à Home
                    intent = new Intent(SplashActivity.this, HomeActivity.class);
                } else {
                    // Pas connecté → Aller à Login
                    intent = new Intent(SplashActivity.this, LoginActivity.class);
                }

                startActivity(intent);
                finish();
            }
        }, 2000);
    }
}
