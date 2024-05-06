package com.example.bshop;
import android.graphics.Bitmap;
public class OrderDatabaseHelper {
    private String username;
    private String shopName;
    private String imageUrl;  // String to store image URL in the database
    private float price;
    private String description;
    private String address;
    private String deliveryDate;
    private String status;  // New field for order status
    private String name;  // New field for the user's name

    // Default constructor required for Firebase
    public OrderDatabaseHelper() {
        // Default constructor required for calls to DataSnapshot.getValue(OrderDatabaseHelper.class)
    }

    // Constructor
    public OrderDatabaseHelper(String username, String shopName, String imageUrl, float price, String description, String address, String deliveryDate, String status, String name) {
        this.username = username;
        this.shopName = shopName;
        this.imageUrl = imageUrl;
        this.price = price;
        this.description = description;
        this.address = address;
        this.deliveryDate = deliveryDate;
        this.status = status;
        this.name = name;
    }

    // Getter methods if needed

    public String getUsername() {
        return username;
    }

    public String getShopName() {
        return shopName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public float getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public String getAddress() {
        return address;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public String getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }
}