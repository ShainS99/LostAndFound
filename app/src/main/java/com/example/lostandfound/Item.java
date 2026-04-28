package com.example.lostandfound;

public class Item {
    int id;
    String type, name, category, date, imagePath;

    public Item(int id, String type, String name, String category, String date, String imagePath) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.category = category;
        this.date = date;
        this.imagePath = imagePath;
    }
}