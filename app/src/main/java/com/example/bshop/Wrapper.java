package com.example.bshop;
public class Wrapper {
    private String shopName;
    private String Availability,nextpage;
    private String imageUrl;  // Store the image URL
    private double price;

    // Default constructor required for Firebase
    public Wrapper() {
    }

    public Wrapper(String shopName, String imageUrl, double price, String Availability,String nextpage) {
        this.shopName = shopName;
        this.imageUrl = imageUrl;
        this.Availability=Availability;
        this.price = price;
        this.nextpage = nextpage;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }
    public String getnextpage() {
        return nextpage;
    }

    public void setnextpage(String nextpage) {
        this.nextpage = nextpage;
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
}

