package com.example.bshop;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bshop.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class cusOrders extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    LinearLayout coversContainer;
    String shopName;
    String username;
    private PopupMenu popupMenu;
    private boolean isPopupMenuShowing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cus_orders);

        coversContainer = findViewById(R.id.coversContainer);

        // Get the shopName from the UserData singleton class
        UserData userData = UserData.getInstance();
        username = userData.getUsername();
        ImageView menuIcon = findViewById(R.id.menuIcon);
        // Show all covers for the specific shop
        showCoversForShop();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show or hide the popup menu based on your requirements
                Intent logoutIntent = new Intent(cusOrders.this, home.class);
                startActivity(logoutIntent);
            }
        });
    }

    private void showCoversForShop() {
        if (username == null || username.isEmpty()) {
            // Handle the case where shopName is not available
            Log.e("Covers", "ShopName is null or empty");
            return;
        }

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("orders").child(username);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("Covers", "Querying covers for shop: " + username);

                if (!snapshot.exists()) {
                    // No data found for the specific shop
                    Log.e("Covers", "No data found for covers in shop: " + username);
                    return;
                }

                Log.d("Covers", "Number of entries found for covers in shop " + username + ": " + snapshot.getChildrenCount());

                for (DataSnapshot coversSnapshot : snapshot.getChildren()) {
                    Log.d("Covers", "Processing entry for covers");

                    String imageUrl = coversSnapshot.child("imageUrl").getValue(String.class);
                    String address = coversSnapshot.child("address").getValue(String.class);
                    String deliveryDate = coversSnapshot.child("deliveryDate").getValue(String.class);
                    String description = coversSnapshot.child("description").getValue(String.class);
                    String shopName = coversSnapshot.child("shopName").getValue(String.class);
                    String status = coversSnapshot.child("status").getValue(String.class);
                    int price = coversSnapshot.child("price").getValue(Integer.class);

                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        Log.d("Covers", "Image URL: " + imageUrl);

                        // Inflate the item_cover.xml layout
                        View coverItemView = getLayoutInflater().inflate(R.layout.orders_design, coversContainer, false);

                        // Find views in the inflated layout
                        ImageView coverImageView = coverItemView.findViewById(R.id.ImageView);
                        TextView coverPriceTextView = coverItemView.findViewById(R.id.TextView);
                        TextView deliveraddress = coverItemView.findViewById(R.id.deliveraddress);
                        TextView deliverprice = coverItemView.findViewById(R.id.deliverprice);



                        // Set data for each view
                        Picasso.get().load(imageUrl).into(coverImageView); // Load image using Picasso
                        coverPriceTextView.setText("Name:" + username);
                        deliveraddress.setText("Address: " + address);
                        deliverprice.setText("Price: " + price);

                        // Set a click listener for the Customize button
                        coverItemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Handle Customize button click
                                // Start the Customize activity and pass image URL and price as extras
                                showPopupDetails(imageUrl, price, address,deliveryDate, description, shopName, status);
                            }
                        });

                        // Add the inflated layout to the coversContainer
                        coversContainer.addView(coverItemView);
                    } else {
                        Log.e("Covers", "Image URL is null or empty for an entry");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Covers", "Database error: " + error.getMessage());
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_home) {
            Intent profileIntent = new Intent(cusOrders.this, cusHome.class);
            startActivity(profileIntent);
        } else if (itemId == R.id.action_profile) {
            // Handle profile icon click
            Intent profileIntent = new Intent(cusOrders.this, ProfileActivity.class);

            startActivity(profileIntent);
        } else if (itemId == R.id.action_orders) {
            // Handle orders icon click
            // You can add the logic for the orders screen if needed
        }

        return true;
    }
    // Method to show the popup with details
    // Method to show the popup with details
    private void showPopupDetails(String imageUrl, int price, String address, String deliveryDate, String description, String shopName, String status) {
        // Inflate the index_popviewxml.xml layout
        View popupView = getLayoutInflater().inflate(R.layout.popup_details, null);

        // Find views in the inflated layout
        ImageView popupImageView = popupView.findViewById(R.id.popupImageView);
        TextView popupPriceTextView = popupView.findViewById(R.id.popupPriceTextView);
        TextView popupAddressTextView = popupView.findViewById(R.id.popupAddressTextView);
        TextView shopname = popupView.findViewById(R.id.shopname);
        TextView statuss = popupView.findViewById(R.id.status);
        TextView descriptionn = popupView.findViewById(R.id.description);

        descriptionn.setVisibility(View.GONE);


        TextView deliverydate = popupView.findViewById(R.id.deliverydate);
        Button closeButton = popupView.findViewById(R.id.closeButton);

        // Set data for each view
        Picasso.get().load(imageUrl).into(popupImageView); // Load image using Picasso
        popupPriceTextView.setText("Price: " + price);
        shopname.setText("Shopname: " + shopName);
        statuss.setText("Status: " + status);

        deliverydate.setText("Delivery Date: " + deliveryDate);
        popupAddressTextView.setText("Delivery Address: " + address);

        // Create and show the popup
        PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);

        // Set a dismiss listener to close the popup when the background is clicked
        popupWindow.setOnDismissListener(() -> {
            // Update any UI elements or perform actions when the popup is dismissed
        });

        // Show the popup at the center of the screen
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

        // Set additional properties for the popup window, if needed
        popupWindow.setTouchable(true);
        popupWindow.setFocusable(true);

        // Set a click listener for the closeButton
        closeButton.setOnClickListener(v -> {
            // Close the popup when the closeButton is clicked
            popupWindow.dismiss();
        });

        // Now, you have a PopupWindow with your custom layout displayed on top of the activity
    }
    private void showPopupMenu(View view) {
        popupMenu = new PopupMenu(this, view);
        popupMenu.inflate(R.menu.cus_popup_menu);

        // Set a click listener for each item in the popup menu
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.menu_logout) {
                    Intent logoutIntent = new Intent(cusOrders.this, home.class);
                    startActivity(logoutIntent);
                    return true;
                } else {
                    return false;
                }
            }
        });

        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
                isPopupMenuShowing = false;
            }
        });

        popupMenu.show();
        isPopupMenuShowing = true;
    }

}