package com.example.kurskcity;

import java.io.Serializable;

public class Attraction implements Serializable {
    private int id;
    private String name;
    private String description;
    private byte[] image;
    private String categories;
    private String type;
    private String location;

    public Attraction(int id, String name, String description, byte[] image, String categories) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.image = image;
        this.categories = categories;
        this.type = "Достопримечательность";
        this.location = "Курск";
    }

    public Attraction(int id, String name, String description, byte[] image, String categories, String type, String location) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.image = image;
        this.categories = categories;
        this.type = type;
        this.location = location;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public byte[] getImage() { return image; }
    public String getCategories() { return categories; }
    public String getType() { return type; }
    public String getLocation() { return location; }

    public void setType(String type) { this.type = type; }
    public void setLocation(String location) { this.location = location; }
}