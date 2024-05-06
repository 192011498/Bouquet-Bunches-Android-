// UserData.java
package com.example.bshop;

import android.graphics.Bitmap;

public class UserData {
    private static UserData instance;
    private String username;
    private String shopName;
    private String name;

    private String date;
    private String pricesText;
    private int totalWithDelivery;
    private Bitmap combinedImage;
    private String address;

    private UserData() {
        // Private constructor to prevent instantiation from outside
    }

    public static synchronized UserData getInstance() {
        if (instance == null) {
            instance = new UserData();
        }
        return instance;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public String getname() {
        return name;
    }

    public void setname(String name) {
        this.name = name;
    }

    public String getshopName() {
        return shopName;
    }

    public void setshopName(String shopName) {
        this.shopName = shopName;
    }

    public String getDescription() {
        return pricesText;
    }

    public void setDescription(String pricesText) {
        this.pricesText = pricesText;
    }

    public int getprice() {
        return totalWithDelivery;
    }

    public void setprice(int totalWithDelivery) {
        this.totalWithDelivery = totalWithDelivery;
    }

    public Bitmap getScreenshot() {
        return combinedImage;
    }

    public void setScreenshot(Bitmap combinedImage) {
        this.combinedImage = combinedImage;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
