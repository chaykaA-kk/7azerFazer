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
import com.example.a7azerfazer.R;
public class LoginActivity extends AppCompatActivity {

    // Éléments de l'interface
    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvGoToRegister;

    // Firebase
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialiser Firebase
        mAuth = FirebaseAuth.getInstance();

        // Vérifier si l'utilisateur est déjà connecté
        if (mAuth.getCurrentUser() != null) {
            // Utilisateur déjà connecté, aller directement à l'accueil
            Toast.makeText(this, "Déjà connecté !", Toast.LENGTH_SHORT).show();
            // On créera HomeActivity plus tard
            // startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            // finish();
        }

        // Lier les éléments
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoToRegister = findViewById(R.id.tvGoToRegister);

        // Bouton Se connecter
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        // Lien vers Register
        tvGoToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loginUser() {
        // Récupérer les valeurs
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validation
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

        // Désactiver le bouton pendant la connexion
        btnLogin.setEnabled(false);

        // Se connecter avec Firebase
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        btnLogin.setEnabled(true);

                        if (task.isSuccessful()) {
                            // Connexion réussie
                            Toast.makeText(LoginActivity.this,
                                    "Connexion réussie !",
                                    Toast.LENGTH_SHORT).show();

                            // Aller vers HomeActivity
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // Erreur de connexion
                            String errorMessage = task.getException() != null ?
                                    task.getException().getMessage() : "Erreur de connexion";
                            Toast.makeText(LoginActivity.this,
                                    errorMessage,
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}