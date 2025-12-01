package com.example.a7azerfazer.models;

import com.google.firebase.Timestamp;

public class QuizResult {
    private String resultId;
    private String userId;
    private String categoryId;
    private String categoryName;
    private int score;
    private int total;
    private double percentage;
    private Object timestamp; // CHANGEMENT : Object au lieu de Timestamp

    // Constructeur vide requis pour Firebase
    public QuizResult() {}

    public QuizResult(String userId, String categoryId, String categoryName,
                      int score, int total, double percentage) {
        this.userId = userId;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.score = score;
        this.total = total;
        this.percentage = percentage;
        this.timestamp = Timestamp.now();
    }

    // Getters
    public String getResultId() {
        return resultId;
    }

    public String getUserId() {
        return userId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public int getScore() {
        return score;
    }

    public int getTotal() {
        return total;
    }

    public double getPercentage() {
        return percentage;
    }

    // MÉTHODE MODIFIÉE : Gère à la fois Timestamp et Long
    public Timestamp getTimestamp() {
        if (timestamp == null) {
            return null;
        }

        // Si c'est déjà un Timestamp
        if (timestamp instanceof Timestamp) {
            return (Timestamp) timestamp;
        }

        // Si c'est un Long (millisecondes)
        if (timestamp instanceof Long) {
            long milliseconds = (Long) timestamp;
            return new Timestamp(milliseconds / 1000, 0);
        }

        // Si c'est une Map (format Firestore)
        if (timestamp instanceof java.util.Map) {
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> map = (java.util.Map<String, Object>) timestamp;

            if (map.containsKey("seconds") && map.containsKey("nanoseconds")) {
                long seconds = ((Number) map.get("seconds")).longValue();
                int nanoseconds = ((Number) map.get("nanoseconds")).intValue();
                return new Timestamp(seconds, nanoseconds);
            }
        }

        return null;
    }

    // Setters
    public void setResultId(String resultId) {
        this.resultId = resultId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public void setTimestamp(Object timestamp) {
        this.timestamp = timestamp;
    }
}