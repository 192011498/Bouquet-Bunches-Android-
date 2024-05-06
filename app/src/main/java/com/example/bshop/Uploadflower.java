package com.example.bshop;
public class Uploadflower {
    private String shopName;
    private String Availability;
    private String imageUrl,name;  // Store the image URL
    private double price;

    // Default constructor required for Firebase
    public Uploadflower() {
    }

    public Uploadflower(String shopName, String imageUrl, double price, String Availability, String name) {
        this.shopName = shopName;
        this.imageUrl = imageUrl;
        this.Availability=Availability;
        this.price = price;
        this.name = name;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getname() {
        return name;
    }

    public void setname(String name) {
        this.name = name;
    }
}

