package com.example.a7azerfazer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.a7azerfazer.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class ResultActivity extends AppCompatActivity {

    private TextView tvFinalScore, tvPercentage;
    private Button btnReplay, btnHome;
    private String categoryId;
    private String categoryName;
    private int score;
    private int totalQuestions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        tvFinalScore = findViewById(R.id.tvFinalScore);
        tvPercentage = findViewById(R.id.tvPercentage);
        btnReplay = findViewById(R.id.btnReplay);
        btnHome = findViewById(R.id.btnHome);

        // Récupérer les données du quiz
        score = getIntent().getIntExtra("score", 0);
        totalQuestions = getIntent().getIntExtra("totalQuestions", 1);
        categoryId = getIntent().getStringExtra("categoryId");
        categoryName = getIntent().getStringExtra("categoryName");

        // Afficher le résultat
        int percentage = (int) ((score * 100.0) / (totalQuestions * 10));
        tvFinalScore.setText("Score : " + score + " / " + (totalQuestions * 10));
        tvPercentage.setText(percentage + "%");

        // Couleur du pourcentage
        if (percentage >= 80) {
            tvPercentage.setTextColor(getResources().getColor(R.color.correct_answer));
        } else if (percentage >= 50) {
            tvPercentage.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
        } else {
            tvPercentage.setTextColor(getResources().getColor(R.color.wrong_answer));
        }

        // Sauvegarder le score dans Firestore
        saveScoreToFirestore();

        // Boutons
        btnReplay.setOnClickListener(v -> {
            Intent intent = new Intent(ResultActivity.this, QuizActivity.class);
            intent.putExtra("categoryId", categoryId);
            startActivity(intent);
            finish();
        });

        btnHome.setOnClickListener(v -> {
            startActivity(new Intent(ResultActivity.this, HomeActivity.class));
            finish();
        });
    }

    private void saveScoreToFirestore() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Map<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("categoryId", categoryId);
        result.put("categoryName", categoryName);
        result.put("score", score);
        result.put("total", totalQuestions * 10);
        result.put("percentage", (score * 100.0) / (totalQuestions * 10));
        result.put("timestamp", System.currentTimeMillis());

        FirebaseFirestore.getInstance()
                .collection("results")
                .add(result);
        // Pas de .addOnSuccessListener pour éviter de bloquer l’UI
    }
}