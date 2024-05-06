package com.example.bshop;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
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

public class covers extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    LinearLayout coversContainer;
    String shopName;
    String username;
    private PopupMenu popupMenu;
    private boolean isPopupMenuShowing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_covers);

        coversContainer = findViewById(R.id.coversContainer);

        // Get the shopName from the UserData singleton class
        UserData userData = UserData.getInstance();
        shopName = userData.getshopName();
        // Menu Icon
        ImageView menuIcon = findViewById(R.id.menuIcon);
        // Show all covers for the specific shop
        showCoversForShop();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        // Menu Icon

        menuIcon.setVisibility(View.GONE);
    }

    private void showCoversForShop() {
        if (shopName == null || shopName.isEmpty()) {
            // Handle the case where shopName is not available
            Log.e("Covers", "ShopName is null or empty");
            return;
        }

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("wrappers").child(shopName);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("Covers", "Querying covers for shop: " + shopName);

                if (!snapshot.exists()) {
                    // No data found for the specific shop
                    Log.e("Covers", "No data found for covers in shop: " + shopName);
                    return;
                }

                Log.d("Covers", "Number of entries found for covers in shop " + shopName + ": " + snapshot.getChildrenCount());

                for (DataSnapshot coversSnapshot : snapshot.getChildren()) {
                    Log.d("Covers", "Processing entry for covers");

                    String imageUrl = coversSnapshot.child("imageUrl").getValue(String.class);
                    String availability = coversSnapshot.child("availability").getValue(String.class);
                    int price = coversSnapshot.child("price").getValue(Integer.class);
                    String nextPage = coversSnapshot.child("nextpage").getValue(String.class); // Retrieve the nextpage value

                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        Log.d("Covers", "Image URL: " + imageUrl);

                        // Inflate the item_cover.xml layout
                        View coverItemView = getLayoutInflater().inflate(R.layout.item_cover, coversContainer, false);

                        // Find views in the inflated layout
                        ImageView coverImageView = coverItemView.findViewById(R.id.coverImageView);
                        TextView coverPriceTextView = coverItemView.findViewById(R.id.priceTextView);
                        Button customizeButton = coverItemView.findViewById(R.id.customizeButton);

                        // Set data for each view
                        Picasso.get().load(imageUrl).into(coverImageView); // Load image using Picasso
                        coverPriceTextView.setText("Price: " + price);
                        if ("Available".equals(availability)) {
                            // Set a click listener for the Customize button
                            customizeButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (nextPage != null && !nextPage.isEmpty()) {
                                        try {
                                            // Construct the class name dynamically
                                            String packageName = getPackageName();
                                            String className = packageName + "." + nextPage;

                                            // Start the activity dynamically
                                            Class<?> nextActivityClass = Class.forName(className);
                                            Intent nextActivityIntent = new Intent(covers.this, nextActivityClass);
                                            nextActivityIntent.putExtra("imageUrl", imageUrl);
                                            nextActivityIntent.putExtra("price", price);
                                            startActivity(nextActivityIntent);

                                        } catch (ClassNotFoundException e) {
                                            e.printStackTrace();
                                            // Handle the case where the class is not found
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            // Handle other exceptions if needed
                                        }
                                    } else {
                                        // Handle the case where "nextpage" is null or empty
                                    }
                                }
                            });
                        } else {
                            // If the availability is not "Available," show "Out of Stock" in red color
                            customizeButton.setVisibility(View.VISIBLE);
                            customizeButton.setText("Out of Stock");
                            customizeButton.setTextColor(getResources().getColor(android.R.color.holo_red_dark));

                            // Optionally, you can disable the button if you don't want it to be clickable
                            customizeButton.setEnabled(false);
                        }

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
            Intent profileIntent = new Intent(covers.this, cusHome.class);
            startActivity(profileIntent);
        } else if (itemId == R.id.action_profile) {
            // Handle profile icon click
            Intent profileIntent = new Intent(covers.this, ProfileActivity.class);

            startActivity(profileIntent);
        } else if (itemId == R.id.action_orders) {
            Intent profileIntent = new Intent(covers.this, cusOrders.class);

            startActivity(profileIntent);
        }

        return true;
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
                    Intent logoutIntent = new Intent(covers.this, home.class);
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