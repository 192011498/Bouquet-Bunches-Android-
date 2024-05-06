package com.example.bshop;

public class Flower {
    private String Availability;
    private String imageUrl;
    private int price;
    private String name;

    public Flower() {
        // Default constructor required for Firebase
    }

    public Flower(String Availability, String imageUrl, int price, String name) {
        this.Availability = Availability;
        this.imageUrl = imageUrl;
        this.price = price;
        this.name = name;
    }

    public String getAvailability() {
        return Availability;
    }

    public void setAvailability(String Availability) {
        this.Availability = Availability;
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
