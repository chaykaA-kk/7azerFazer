package com.example.a7azerfazer.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import com.example.a7azerfazer.R;

public class RegisterActivity extends AppCompatActivity {

    // Éléments de l'interface
    private EditText etUsername, etEmail, etPassword;
    private Button btnRegister;
    private TextView tvGoToLogin;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialiser Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Lier les éléments
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvGoToLogin = findViewById(R.id.tvGoToLogin);

        // Bouton S'inscrire
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        // Lien vers Login
        tvGoToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void registerUser() {
        // Récupérer les valeurs
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validation
        if (username.isEmpty()) {
            etUsername.setError("Le nom d'utilisateur est requis");
            etUsername.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            etEmail.setError("L'email est requis");
            etEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError("Le mot de passe est requis");
            etPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("Le mot de passe doit contenir au moins 6 caractères");
            etPassword.requestFocus();
            return;
        }

        // Désactiver le bouton pendant l'inscription
        btnRegister.setEnabled(false);

        // Créer l'utilisateur avec Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Inscription réussie
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();

                            if (firebaseUser != null) {
                                // Sauvegarder les infos dans Firestore
                                saveUserToFirestore(firebaseUser.getUid(), username, email);
                            }
                        } else {
                            // Erreur lors de l'inscription
                            btnRegister.setEnabled(true);
                            String errorMessage = task.getException() != null ?
                                    task.getException().getMessage() : "Erreur d'inscription";
                            Toast.makeText(RegisterActivity.this,
                                    errorMessage,
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void saveUserToFirestore(String userId, String username, String email) {
        // Créer un objet Map avec les données de l'utilisateur
        Map<String, Object> user = new HashMap<>();
        user.put("userId", userId);
        user.put("username", username);
        user.put("email", email);

        // Sauvegarder dans Firestore
        db.collection("users").document(userId)
                .set(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        btnRegister.setEnabled(true);

                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this,
                                    "Inscription réussie !",
                                    Toast.LENGTH_SHORT).show();

                            // Retourner à LoginActivity
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this,
                                    "Erreur lors de l'enregistrement des données",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}