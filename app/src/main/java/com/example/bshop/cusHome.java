package com.example.bshop;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class cusHome extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    LinearLayout containerLayout;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cus_home);

        containerLayout = findViewById(R.id.containerLayout);
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        ImageView menuIcon = findViewById(R.id.menuIcon);
        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show or hide the popup menu based on your requirements
                Intent logoutIntent = new Intent(cusHome.this, home.class);
                startActivity(logoutIntent);
            }
        });
        // Show admins' data
        showAdminsData();
    }

    private void showAdminsData() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("admin");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot adminSnapshot : snapshot.getChildren()) {
                    final String shopName = adminSnapshot.child("shopname").getValue(String.class);
                    String shopAddress = adminSnapshot.child("address").getValue(String.class);
                    String imageUrl = adminSnapshot.child("imageURL").getValue(String.class);

                    // Inflate the CardView layout
                    View cardViewLayout = getLayoutInflater().inflate(R.layout.cushome_layout, null);

                    // Find Views inside the CardView layout
                    ImageView shopImageView = cardViewLayout.findViewById(R.id.shopImageView);
                    TextView shopNameTextView = cardViewLayout.findViewById(R.id.shopNameTextView);
                    TextView shopAddressTextView = cardViewLayout.findViewById(R.id.shopAddressTextView);

                    // Load the image using Picasso
                    Picasso.get().load(imageUrl).into(shopImageView);

                    // Set shopName and shopAddress to TextViews
                    shopNameTextView.setText("Shop Name: " + shopName);
                    shopAddressTextView.setText("Shop Address: " + shopAddress);

                    // Set onClickListener for the CardView
                    cardViewLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Pass the selected shopName to Covers activity
                            UserData userData = UserData.getInstance();
                            userData.setshopName(shopName);

                            Intent intent = new Intent(cusHome.this, covers.class);
                            startActivity(intent);
                        }
                    });

                    // Add the inflated CardView to the container layout
                    containerLayout.addView(cardViewLayout);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled if needed
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_home) {
            // Handle home icon click
        } else if (itemId == R.id.action_profile) {
            // Handle profile icon click
            Intent profileIntent = new Intent(cusHome.this, ProfileActivity.class);
            startActivity(profileIntent);
        } else if (itemId == R.id.action_orders) {
            Intent ordersIntent = new Intent(cusHome.this, cusOrders.class);
            startActivity(ordersIntent);
        }

        return true;
    }
}
