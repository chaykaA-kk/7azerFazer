package com.example.a7azerfazer.models;

public class Category {
    private String categoryId;
    private String name;
    private String description;

    // Constructeur vide requis pour Firebase
    public Category() {}

    public Category(String categoryId, String name, String description) {
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
    }

    // Getters
    public String getCategoryId() {
        return categoryId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    // Setters
    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}