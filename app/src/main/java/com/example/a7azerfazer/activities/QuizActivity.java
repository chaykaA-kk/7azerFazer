package com.example.a7azerfazer.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
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

    // Éléments de l'interface
    private TextView tvQuestionNumber, tvQuestion, tvTimer;
    private Button btnOption1, btnOption2, btnOption3, btnOption4;
    private ProgressBar progressBar;

    // Firebase
    private FirebaseFirestore db;

    // Variables du quiz
    private List<Question> questionList;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private String categoryId;
    private String categoryName;
    private boolean answerSelected = false;

    // Timer
    private CountDownTimer countDownTimer;
    private static final long QUESTION_TIME_LIMIT = 10000; // 10 secondes en millisecondes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // Initialiser Firebase
        db = FirebaseFirestore.getInstance();

        // Récupérer les données de l'Intent
        categoryId = getIntent().getStringExtra("categoryId");
        categoryName = getIntent().getStringExtra("categoryName");

        // Lier les éléments
        tvQuestionNumber = findViewById(R.id.tvQuestionNumber);
        tvQuestion = findViewById(R.id.tvQuestion);
        tvTimer = findViewById(R.id.tvTimer);
        btnOption1 = findViewById(R.id.btnOption1);
        btnOption2 = findViewById(R.id.btnOption2);
        btnOption3 = findViewById(R.id.btnOption3);
        btnOption4 = findViewById(R.id.btnOption4);
        progressBar = findViewById(R.id.progressBar);

        // Initialiser la liste
        questionList = new ArrayList<>();

        // Charger les questions depuis Firestore
        loadQuestions();
    }

    private void loadQuestions() {
        progressBar.setVisibility(View.VISIBLE);

        db.collection("questions")
                .whereEqualTo("categoryId", categoryId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        progressBar.setVisibility(View.GONE);

                        if (task.isSuccessful()) {
                            questionList.clear();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Question question = document.toObject(Question.class);
                                question.setQuestionId(document.getId());
                                questionList.add(question);
                            }

                            if (questionList.isEmpty()) {
                                Toast.makeText(QuizActivity.this,
                                        "Aucune question disponible pour cette catégorie",
                                        Toast.LENGTH_LONG).show();
                                finish();
                            } else {
                                // Afficher la première question
                                displayQuestion();
                            }
                        } else {
                            Toast.makeText(QuizActivity.this,
                                    "Erreur lors du chargement des questions",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
    }

    private void displayQuestion() {
        answerSelected = false;

        // Annuler le timer précédent s'il existe
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        // Récupérer la question actuelle
        Question currentQuestion = questionList.get(currentQuestionIndex);

        // Afficher le numéro de question
        tvQuestionNumber.setText("Question " + (currentQuestionIndex + 1) + "/" + questionList.size());

        // Afficher la question
        tvQuestion.setText(currentQuestion.getQuestionText());

        // Afficher les options
        List<String> options = currentQuestion.getOptions();
        btnOption1.setText(options.get(0));
        btnOption2.setText(options.get(1));
        btnOption3.setText(options.get(2));
        btnOption4.setText(options.get(3));

        // Réinitialiser les couleurs des boutons
        resetButtonColors();

        // Activer tous les boutons
        enableAllButtons(true);

        // Écouter les clics
        btnOption1.setOnClickListener(optionClickListener);
        btnOption2.setOnClickListener(optionClickListener);
        btnOption3.setOnClickListener(optionClickListener);
        btnOption4.setOnClickListener(optionClickListener);

        // Démarrer le timer de 10 secondes
        startTimer();
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(QUESTION_TIME_LIMIT, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Mettre à jour l'affichage du timer
                int secondsLeft = (int) (millisUntilFinished / 1000);
                tvTimer.setText("⏱ " + secondsLeft + "s");

                // Changer la couleur selon le temps restant
                if (secondsLeft <= 3) {
                    tvTimer.setTextColor(Color.parseColor("#F44336")); // Rouge
                } else if (secondsLeft <= 5) {
                    tvTimer.setTextColor(Color.parseColor("#FF9800")); // Orange
                } else {
                    tvTimer.setTextColor(Color.parseColor("#4CAF50")); // Vert
                }
            }

            @Override
            public void onFinish() {
                // Temps écoulé !
                if (!answerSelected) {
                    tvTimer.setText("⏱ 0s");
                    tvTimer.setTextColor(Color.parseColor("#F44336"));

                    Toast.makeText(QuizActivity.this,
                            "Temps écoulé ! ⏰",
                            Toast.LENGTH_SHORT).show();

                    // Marquer comme mauvaise réponse
                    answerSelected = true;
                    enableAllButtons(false);

                    // Afficher la bonne réponse
                    Question currentQuestion = questionList.get(currentQuestionIndex);
                    showCorrectAnswer(currentQuestion.getCorrectAnswerIndex());

                    // Passer à la question suivante après 1.5 secondes
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            goToNextQuestion();
                        }
                    }, 1500);
                }
            }
        };

        countDownTimer.start();
    }

    // Listener pour les options
    private View.OnClickListener optionClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (answerSelected) {
                return; // Ne rien faire si une réponse a déjà été sélectionnée
            }

            answerSelected = true;

            // Arrêter le timer
            if (countDownTimer != null) {
                countDownTimer.cancel();
            }

            // Désactiver tous les boutons
            enableAllButtons(false);

            // Récupérer la question actuelle
            Question currentQuestion = questionList.get(currentQuestionIndex);

            // Trouver quel bouton a été cliqué
            int selectedOption = -1;
            if (v.getId() == btnOption1.getId()) selectedOption = 0;
            else if (v.getId() == btnOption2.getId()) selectedOption = 1;
            else if (v.getId() == btnOption3.getId()) selectedOption = 2;
            else if (v.getId() == btnOption4.getId()) selectedOption = 3;

            // Vérifier si la réponse est correcte
            if (selectedOption == currentQuestion.getCorrectAnswerIndex()) {
                // Bonne réponse
                ((Button) v).setBackgroundColor(Color.parseColor("#4CAF50")); // Vert
                score += 10; // +10 points par bonne réponse
                Toast.makeText(QuizActivity.this, "Bonne réponse ! ✓", Toast.LENGTH_SHORT).show();
            } else {
                // Mauvaise réponse
                ((Button) v).setBackgroundColor(Color.parseColor("#F44336")); // Rouge

                // Afficher la bonne réponse en vert
                showCorrectAnswer(currentQuestion.getCorrectAnswerIndex());

                Toast.makeText(QuizActivity.this, "Mauvaise réponse ✗", Toast.LENGTH_SHORT).show();
            }

            // Passer à la question suivante après 1.5 secondes
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    goToNextQuestion();
                }
            }, 1500);
        }
    };

    private void goToNextQuestion() {
        currentQuestionIndex++;

        if (currentQuestionIndex < questionList.size()) {
            // Il reste des questions
            displayQuestion();
        } else {
            // Quiz terminé
            showResults();
        }
    }

    private void showCorrectAnswer(int correctIndex) {
        switch (correctIndex) {
            case 0:
                btnOption1.setBackgroundColor(Color.parseColor("#4CAF50"));
                break;
            case 1:
                btnOption2.setBackgroundColor(Color.parseColor("#4CAF50"));
                break;
            case 2:
                btnOption3.setBackgroundColor(Color.parseColor("#4CAF50"));
                break;
            case 3:
                btnOption4.setBackgroundColor(Color.parseColor("#4CAF50"));
                break;
        }
    }

    private void resetButtonColors() {
        btnOption1.setBackgroundColor(Color.parseColor("#FFFFFF"));
        btnOption2.setBackgroundColor(Color.parseColor("#FFFFFF"));
        btnOption3.setBackgroundColor(Color.parseColor("#FFFFFF"));
        btnOption4.setBackgroundColor(Color.parseColor("#FFFFFF"));
    }

    private void enableAllButtons(boolean enabled) {
        btnOption1.setEnabled(enabled);
        btnOption2.setEnabled(enabled);
        btnOption3.setEnabled(enabled);
        btnOption4.setEnabled(enabled);
    }

    private void showResults() {
        // Annuler le timer s'il existe
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        Intent intent = new Intent(QuizActivity.this, ResultActivity.class);
        intent.putExtra("score", score);
        intent.putExtra("totalQuestions", questionList.size());
        intent.putExtra("correctAnswers", score / 10); // Chaque bonne réponse = 10 points
        intent.putExtra("categoryName", categoryName);
        intent.putExtra("categoryId", categoryId);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Annuler le timer quand l'activité est détruite
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}