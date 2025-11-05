package com.example.kurskcity;

import java.io.Serializable;

public class Fact implements Serializable {
    private String title;
    private String description;
    private String fullContent;

    public Fact(String title, String description, String fullContent) {
        this.title = title;
        this.description = description;
        this.fullContent = fullContent;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getFullContent() {
        return fullContent;
    }
}