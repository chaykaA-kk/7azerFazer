package com.example.a7azerfazer.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a7azerfazer.R;
import com.example.a7azerfazer.models.Question;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class QuizActivity extends AppCompatActivity {

    private TextView tvQuestionNumber, tvScore, tvQuestion;
    private Button btnOptionA, btnOptionB, btnOptionC, btnOptionD, btnNext;

    private FirebaseFirestore db;
    private List<Question> questionList;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private int selectedAnswerIndex = -1;
    private boolean answerSelected = false;

    private String categoryId;
    private String categoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // Initialiser Firestore
        db = FirebaseFirestore.getInstance();
        questionList = new ArrayList<>();

        // Récupérer les données passées depuis CategoryActivity
        categoryId = getIntent().getStringExtra("categoryId");
        categoryName = getIntent().getStringExtra("categoryName");

        // Lier les éléments
        tvQuestionNumber = findViewById(R.id.tvQuestionNumber);
        tvScore = findViewById(R.id.tvScore);
        tvQuestion = findViewById(R.id.tvQuestion);
        btnOptionA = findViewById(R.id.btnOptionA);
        btnOptionB = findViewById(R.id.btnOptionB);
        btnOptionC = findViewById(R.id.btnOptionC);
        btnOptionD = findViewById(R.id.btnOptionD);
        btnNext = findViewById(R.id.btnNext);

        // Charger les questions de cette catégorie
        loadQuestions(categoryId);

        // Gérer les clics sur les options
        setupOptionListeners();

        // Bouton Suivant
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToNextQuestion();
            }
        });
    }

    private void loadQuestions(String categoryId) {
        db.collection("questions")
                .whereEqualTo("categoryId", categoryId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            questionList.clear();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Question question = document.toObject(Question.class);
                                questionList.add(question);
                            }

                            if (questionList.size() > 0) {
                                // Afficher la première question
                                displayQuestion();
                            } else {
                                Toast.makeText(QuizActivity.this,
                                        "Aucune question disponible pour cette catégorie",
                                        Toast.LENGTH_LONG).show();
                                finish();
                            }

                        } else {
                            Toast.makeText(QuizActivity.this,
                                    "Erreur de chargement des questions",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
    }

    private void displayQuestion() {
        if (currentQuestionIndex < questionList.size()) {
            Question question = questionList.get(currentQuestionIndex);

            // Afficher le numéro de question
            tvQuestionNumber.setText("Question " + (currentQuestionIndex + 1) + "/" + questionList.size());

            // Afficher le score
            tvScore.setText("Score: " + score);

            // Afficher le texte de la question
            String questionText = question.getQuestionText();
            if (questionText != null && !questionText.isEmpty()) {
                tvQuestion.setText(questionText);
            } else {
                tvQuestion.setText("Question non disponible");
            }

            // Afficher les options
            List<String> options = question.getOptions();
            if (options != null && options.size() >= 4) {
                btnOptionA.setText(options.get(0));
                btnOptionB.setText(options.get(1));
                btnOptionC.setText(options.get(2));
                btnOptionD.setText(options.get(3));
            } else {
                Toast.makeText(this, "Erreur: options manquantes", Toast.LENGTH_SHORT).show();
            }

            // Réinitialiser les couleurs des boutons
            resetButtonColors();

            // Réactiver les boutons d'options
            enableOptionButtons(true);

            // Réinitialiser la sélection
            selectedAnswerIndex = -1;
            answerSelected = false;
            btnNext.setEnabled(false);
        }
    }

    private void setupOptionListeners() {
        btnOptionA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectAnswer(0, btnOptionA);
            }
        });

        btnOptionB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectAnswer(1, btnOptionB);
            }
        });

        btnOptionC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectAnswer(2, btnOptionC);
            }
        });

        btnOptionD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectAnswer(3, btnOptionD);
            }
        });
    }

    private void selectAnswer(int answerIndex, Button selectedButton) {
        // Empêcher de sélectionner plusieurs fois
        if (answerSelected) {
            return;
        }

        answerSelected = true;

        // Sauvegarder l'index de la réponse sélectionnée
        selectedAnswerIndex = answerIndex;

        // Désactiver tous les boutons d'options
        enableOptionButtons(false);

        // Récupérer la question actuelle
        Question currentQuestion = questionList.get(currentQuestionIndex);

        // Vérifier si la réponse est correcte
        if (answerIndex == currentQuestion.getCorrectAnswerIndex()) {
            // Bonne réponse : vert
            selectedButton.setBackgroundColor(getResources().getColor(R.color.correct_answer));
            score += 10; // Ajouter 10 points
            tvScore.setText("Score: " + score);
            Toast.makeText(this, "Correct ! +10 points", Toast.LENGTH_SHORT).show();
        } else {
            // Mauvaise réponse : rouge
            selectedButton.setBackgroundColor(getResources().getColor(R.color.wrong_answer));

            // Afficher la bonne réponse en vert
            Button correctButton = getButtonByIndex(currentQuestion.getCorrectAnswerIndex());
            if (correctButton != null) {
                correctButton.setBackgroundColor(getResources().getColor(R.color.correct_answer));
            }
            Toast.makeText(this, "Incorrect !", Toast.LENGTH_SHORT).show();
        }

        // Activer le bouton Suivant
        btnNext.setEnabled(true);
    }

    private void enableOptionButtons(boolean enabled) {
        btnOptionA.setEnabled(enabled);
        btnOptionB.setEnabled(enabled);
        btnOptionC.setEnabled(enabled);
        btnOptionD.setEnabled(enabled);
    }

    private Button getButtonByIndex(int index) {
        switch (index) {
            case 0: return btnOptionA;
            case 1: return btnOptionB;
            case 2: return btnOptionC;
            case 3: return btnOptionD;
            default: return null;
        }
    }

    private void resetButtonColors() {
        btnOptionA.setBackgroundColor(Color.WHITE);
        btnOptionB.setBackgroundColor(Color.WHITE);
        btnOptionC.setBackgroundColor(Color.WHITE);
        btnOptionD.setBackgroundColor(Color.WHITE);
    }

    private void goToNextQuestion() {
        currentQuestionIndex++;

        if (currentQuestionIndex < questionList.size()) {
            // Afficher la question suivante
            displayQuestion();
        } else {
            // Quiz terminé - Aller vers ResultActivity
            Intent intent = new Intent(QuizActivity.this, ResultActivity.class);
            intent.putExtra("score", score);
            intent.putExtra("totalQuestions", questionList.size());
            intent.putExtra("categoryId", categoryId);
            intent.putExtra("categoryName", categoryName);
            startActivity(intent);
            finish();
        }
    }
}