package com.example.bshop;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bshop.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class WrapperItem extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    LinearLayout wrapperItemContainer;
    String shopName;
    String username;
    private PopupMenu popupMenu;
    private boolean isPopupMenuShowing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wrapper_item);

        wrapperItemContainer = findViewById(R.id.coversContainer);

        // Get the shopName from the UserData singleton class
        UserData userData = UserData.getInstance();
        shopName = userData.getshopName();
        // Menu Icon
        ImageView menuIcon = findViewById(R.id.menuIcon);
        // Show all wrapperItem for the specific shop
        showwrapperItemForShop();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show or hide the popup menu based on your requirements
                if (isPopupMenuShowing) {
                    popupMenu.dismiss();
                } else {
                    showPopupMenu(v);
                }
            }
        });
    }

    private void showwrapperItemForShop() {
        if (shopName == null || shopName.isEmpty()) {
            // Handle the case where shopName is not available
            Log.e("wrapperItem", "ShopName is null or empty");
            return;
        }

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("wrappers").child(shopName);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("wrapperItem", "Querying wrapperItem for shop: " + shopName);

                if (!snapshot.exists()) {
                    // No data found for the specific shop
                    Log.e("wrapperItem", "No data found for wrapperItem in shop: " + shopName);
                    return;
                }

                Log.d("wrapperItem", "Number of entries found for wrapperItem in shop " + shopName + ": " + snapshot.getChildrenCount());

                for (DataSnapshot wrapperItemSnapshot : snapshot.getChildren()) {
                    Log.d("wrapperItem", "Processing entry for wrapperItem");

                    String imageUrl = wrapperItemSnapshot.child("imageUrl").getValue(String.class);
                    String availability = wrapperItemSnapshot.child("availability").getValue(String.class);
                    int price = wrapperItemSnapshot.child("price").getValue(Integer.class);


                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        Log.d("wrapperItem", "Image URL: " + imageUrl);

                        // Inflate the item_cover.xml layout
                        View coverItemView = getLayoutInflater().inflate(R.layout.item_wrapper, wrapperItemContainer, false);

                        // Find views in the inflated layout
                        ImageView coverImageView = coverItemView.findViewById(R.id.coverImageView);
                        TextView coverPriceTextView = coverItemView.findViewById(R.id.priceTextView);
                        Button editButton = coverItemView.findViewById(R.id.editButton);
                        Button deleteButton2 = coverItemView.findViewById(R.id.editButton2);

                        // Set data for each view
                        Picasso.get().load(imageUrl).into(coverImageView); // Load image using Picasso
                        coverPriceTextView.setText("Price: " + price);

                        // Set a click listener for the Customize button
                        editButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                showPopupDetails(imageUrl, price, shopName, availability);
                            }
                        });
                        // Inside the showwrapperItemForShop() method, after setting click listener for editButton
                        deleteButton2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(WrapperItem.this);
                                builder.setTitle("Confirmation");
                                builder.setMessage("Are you sure you want to delete this item?");
                                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        // Get the reference to the wrapper item in the database
                                        DatabaseReference wrapperRef = FirebaseDatabase.getInstance().getReference("wrappers")
                                                .child(shopName).child(wrapperItemSnapshot.getKey());

                                        // Remove the wrapper item from the database
                                        wrapperRef.removeValue()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        // Remove the view from the layout
                                                        wrapperItemContainer.removeView(coverItemView);
                                                        // Reload wrapper items to update the UI
                                                        reloadWrapperItems();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.e("DeleteWrapper", "Error deleting wrapper item: " + e.getMessage());
                                                        // Handle the failure scenario if needed
                                                    }
                                                });
                                    }
                                });
                                builder.setNegativeButton("Cancel", null);
                                builder.show();
                            }
                        });


                        // Add the inflated layout to the wrapperItemContainer
                        wrapperItemContainer.addView(coverItemView);
                    } else {
                        Log.e("wrapperItem", "Image URL is null or empty for an entry");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("wrapperItem", "Database error: " + error.getMessage());
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_home) {
            Intent profileIntent = new Intent(WrapperItem.this, sellHome.class);
            startActivity(profileIntent);
        } else if (itemId == R.id.action_profile) {
            // Handle profile icon click
            Intent profileIntent = new Intent(WrapperItem.this, ProfileActivity2.class);

            startActivity(profileIntent);
        } else if (itemId == R.id.action_orders) {
            Intent profileIntent = new Intent(WrapperItem.this, sellOrders.class);

            startActivity(profileIntent);
        }

        return true;
    }
    private void showPopupMenu(View view) {
        popupMenu = new PopupMenu(this, view);
        popupMenu.inflate(R.menu.popup_menu);

        // Set a click listener for each item in the popup menu
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.menu_add_wrappers) {
                    // Redirect to AddWrapperActivity when "Add Wrappers" is clicked
                    Intent addWrapperIntent = new Intent(WrapperItem.this, addWrapper.class);

                    startActivity(addWrapperIntent);
                    return true;
                } else if (itemId == R.id.menu_view_wrappers) {
                    Intent viewWrappersIntent = new Intent(WrapperItem.this, WrapperItem.class);

                    startActivity(viewWrappersIntent);
                    return true;
                } else if (itemId == R.id.menu_logout) {
                    Intent logoutIntent = new Intent(WrapperItem.this, home.class);
                    startActivity(logoutIntent);
                    return true;
                } else if (itemId == R.id.menu_add_flowers) {
                    Intent viewWrappersIntent = new Intent(WrapperItem.this, AddFlowers.class);

                    startActivity(viewWrappersIntent);
                    return true;
                } else if (itemId == R.id.menu_view_flowers) {
                    Intent logoutIntent = new Intent(WrapperItem.this, FlowerItem.class);
                    startActivity(logoutIntent);
                    return true;
                }else if (itemId == R.id.menu_add_ties) {
                    Intent viewWrappersIntent = new Intent(WrapperItem.this, AddTies.class);

                    startActivity(viewWrappersIntent);
                    return true;
                } else if (itemId == R.id.menu_view_ties) {
                    Intent logoutIntent = new Intent(WrapperItem.this, TiesItem.class);
                    startActivity(logoutIntent);
                    return true;
                }
                else {
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
    private void showPopupDetails(String imageUrl, int price, String shopName, String availability) {
        // Inflate the wrapper_popup.xml layout
        View popupView = getLayoutInflater().inflate(R.layout.wrapper_popup, null);

        // Find views in the inflated layout
        ImageView popupImageView = popupView.findViewById(R.id.popupImageView);
        EditText editPriceEditText = popupView.findViewById(R.id.editPriceEditText);
        Spinner availabilitySpinner = popupView.findViewById(R.id.availabilitySpinner);

        Button closeButton = popupView.findViewById(R.id.closeButton);

        // Set data for each view
        Picasso.get().load(imageUrl).into(popupImageView); // Load image using Picasso
        editPriceEditText.setText(String.valueOf(price));


        // Set availability based on the value retrieved from the database
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.availability_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        availabilitySpinner.setAdapter(adapter);

        if (availability != null) {
            // Convert both strings to lowercase for case-insensitive comparison
            String lowerCaseAvailability = availability.toLowerCase();

            if (lowerCaseAvailability.equals("available")) {
                availabilitySpinner.setSelection(adapter.getPosition("Available"));
            } else if (lowerCaseAvailability.equals("out of stock")) {
                availabilitySpinner.setSelection(adapter.getPosition("Out of Stock"));
            } else {
                Log.e("AvailabilityError", "Unexpected availability value: " + availability);
            }
        } else {
            Log.e("AvailabilityError", "Availability is null");
        }
        // Create and show the popup
        PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the updated values from the views
                int updatedPrice = Integer.parseInt(editPriceEditText.getText().toString());
                String updatedAvailability = availabilitySpinner.getSelectedItem().toString();

                changeOrdersStatus(shopName, updatedPrice, updatedAvailability,  imageUrl);

                // Dismiss the popup
                popupWindow.dismiss();

            }
        });

        // Set a dismiss listener to close the popup when the background is clicked
        popupWindow.setOnDismissListener(() -> {
            // Update any UI elements or perform actions when the popup is dismissed
        });

        // Show the popup at the center of the screen
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

        // Set additional properties for the popup window, if needed
        popupWindow.setTouchable(true);
        popupWindow.setFocusable(true);
    }
    private void changeOrdersStatus(String shopName, int newprice, String newStatus,String imageUrl) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("wrappers").child(shopName);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                        String Availability = orderSnapshot.child("Availability").getValue(String.class);
                        int prices = orderSnapshot.child("price").getValue(Integer.class);
                        String ordersimageUrl = orderSnapshot.child("imageUrl").getValue(String.class);
                        if (ordersimageUrl != null && ordersimageUrl.equals(imageUrl)) {

                                orderSnapshot.getRef().child("price").setValue(newprice);
                                orderSnapshot.getRef().child("availability").setValue(newStatus);
                            reloadWrapperItems();

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ChangeStatus", "Database error: " + error.getMessage());
            }
        });
    }
    private void reloadWrapperItems() {
        wrapperItemContainer.removeAllViews(); // Clear existing items

        // Call the method to show wrapper items again
        showwrapperItemForShop();
    }
}