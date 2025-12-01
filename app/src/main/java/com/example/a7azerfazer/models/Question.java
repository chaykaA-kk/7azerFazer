package com.example.a7azerfazer.models;

import com.google.firebase.firestore.PropertyName;

import java.util.List;

public class Question {
    private String questionId;
    private String categoryId;
    private String questionText;
    private List<String> options;
    private int correctAnswerIndex;

    // Constructeur vide requis pour Firebase
    public Question() {}

    public Question(String questionId, String categoryId, String questionText,
                    List<String> options, int correctAnswerIndex) {
        this.questionId = questionId;
        this.categoryId = categoryId;
        this.questionText = questionText;
        this.options = options;
        this.correctAnswerIndex = correctAnswerIndex;
    }

    // Getters
    @PropertyName("questionId")
    public String getQuestionId() {
        return questionId;
    }
    @PropertyName("categoryId")

    public String getCategoryId() {
        return categoryId;
    }
    @PropertyName("questionText")

    public String getQuestionText() {
        return questionText;
    }
    @PropertyName("options")

    public List<String> getOptions() {
        return options;
    }

    @PropertyName("correctAnswerIndex")

    public int getCorrectAnswerIndex() {
        return correctAnswerIndex;
    }

    // Setters
    @PropertyName("questionId")
    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }
    @PropertyName("categoryId")

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }
    @PropertyName("questionText")

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }
    @PropertyName("options")

    public void setOptions(List<String> options) {
        this.options = options;
    }

    @PropertyName("correctAnswerIndex")
    public void setCorrectAnswerIndex(int correctAnswerIndex) {
        this.correctAnswerIndex = correctAnswerIndex;
    }
}