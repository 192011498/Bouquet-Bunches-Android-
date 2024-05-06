package com.example.bshop;

public class Tie {
    private String availability;
    private String imageUrl;
    private int price;
    private String name;

    public Tie() {
        // Default constructor required for Firebase
    }

    public Tie(String availability, String imageUrl, int price, String name) {
        this.availability = availability;
        this.imageUrl = imageUrl;
        this.price = price;
        this.name = name;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getname() {
        return name;
    }

    public void setname(String name) {
        this.name = name;
    }
}

