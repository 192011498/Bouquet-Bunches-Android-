package com.example.bshop;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Random;

public class customize6 extends AppCompatActivity {

    private LinearLayout flowerList, tieList;


    private TextView totalAmount;
    private RelativeLayout tieContainer;
    private Button flowersButton, tiesButton, completeButton, finishButton, placeorder;
    private ImageView imagePreview;
    private RelativeLayout flowerContainer;
    private HashMap<Integer, Integer> flowerPrices;

    // Add this to store selected flowers and their quantities
    private HashMap<String, Integer[]> selectedFlowerIds;
    private int wrapperCost;  // Added variable for wrapper cost
    private String  selectedTie,shopName;
    private boolean completeButtonClicked = false;
    private TextView finalAmount;
    private static final int MAX_FLOWERS = 30;
    private int flowerCounter = 0;
    private Integer flowerId, tieId,tieprice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customize);

        // Find views by ID
        flowerList = findViewById(R.id.flower_list);
        tieList = findViewById(R.id.tie_list);
        totalAmount = findViewById(R.id.total_amount);
        finalAmount = findViewById(R.id.final_amount);
        flowersButton = findViewById(R.id.flowers_button);
        placeorder = findViewById(R.id.placeorder_button);
        tiesButton = findViewById(R.id.ties_button);
        completeButton = findViewById(R.id.complete_button);
        finishButton = findViewById(R.id.finish_button);
        imagePreview = findViewById(R.id.image_preview);
        flowerContainer = findViewById(R.id.flower_container);
        tieContainer = findViewById(R.id.tie_container);

        UserData userData = UserData.getInstance();
        shopName = userData.getshopName();
        // Menu Icon
        ImageView menuIcon = findViewById(R.id.menuIcon);
        menuIcon.setVisibility(View.GONE);
        // Initialize flower prices
        flowerPrices = new HashMap<>();
        flowerPrices.put(R.drawable.red_rose, 10);
        flowerPrices.put(R.drawable.white_rose, 15);
        flowerPrices.put(R.drawable.pink_rose, 10);



        // Initialize selected flowers
        selectedFlowerIds = new HashMap<>();

        // Set initial visibility
        flowerList.setVisibility(View.VISIBLE);
        tieList.setVisibility(View.GONE);

        // Get extras from the intent
        Intent intent = getIntent();
        if (intent != null) {
            String imageUrl = intent.getStringExtra("imageUrl");
            int price = intent.getIntExtra("price", 0);

            // Assign wrapperCost with the price from the previous page
            wrapperCost = price;

            // Update imagePreview with the selected image
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Picasso.get().load(imageUrl).into(imagePreview);
            }

            // Handle the price as needed (e.g., updating totalAmount TextView)
            totalAmount.setText("Price: Rs " + price);
        }

        // Initialize flower prices and wrapper cost

        fetchFlowersFromDatabase(); // Fetch flowers from Firebase database
        fetchTiesFromDatabase();

        // Set click listeners
        flowersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Show flower list and hide tie list
                flowerList.setVisibility(View.VISIBLE);
                tieList.setVisibility(View.GONE);
            }
        });

        tiesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Show tie list and hide flower list
                flowerList.setVisibility(View.GONE);
                tieList.setVisibility(View.VISIBLE);
            }
        });


        // Set click listener for ties in the tie list


        // Add similar click listeners for other tie buttons

        completeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedFlowerIds.isEmpty()) {
                    // Display a Toast message indicating that no flower is selected
                    Toast.makeText(customize6.this, "Please select at least one flower.", Toast.LENGTH_SHORT).show();
                    return; // Exit the method
                }
                else {

                    // Show selected flowers and hide flower list
                    displaySelectedFlowers();
                    completeButtonClicked = true;
                }
            }
        });

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                updateTotalPrice();
            }
        });
        placeorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                // Create an Intent to start the Address activity
                Intent addressIntent = new Intent(customize6.this, Address.class);

                // Start the Address activity
                startActivity(addressIntent);
            }
        });
    }

    private void fetchFlowersFromDatabase() {
        FirebaseDatabase.getInstance().getReference().child("flowers").child(shopName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot flowerSnapshot : dataSnapshot.getChildren()) {
                            Flower flower = flowerSnapshot.getValue(Flower.class);
                            if (flower != null && "Available".equals(flower.getAvailability())) {
                                // Create a new ImageView for the flower
                                ImageView flowerImageView = new ImageView(customize6.this);
                                String flowerName = flower.getname();

                                // Load image using Picasso or Glide library
                                Picasso.get().load(flower.getImageUrl()).into(flowerImageView);

                                // Set layout parameters and click listener
                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                        dpToPx(60), dpToPx(60) // Set the size to 80dp x 80dp
                                );
                                int margin = dpToPx(5); // Set the margin to 10dp
                                layoutParams.setMargins(margin, margin, margin, margin);
                                flowerImageView.setBackgroundResource(R.drawable.lavender_border);
                                flowerImageView.setLayoutParams(layoutParams);

                                flowerImageView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        // Handle click event
                                        addFlowerToPreview(flower.getImageUrl(), flower.getPrice(), flowerName);
                                    }
                                });

                                // Add the ImageView to the flowerList
                                flowerList.addView(flowerImageView);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle error
                    }
                });
    }

    private void fetchTiesFromDatabase() {
        FirebaseDatabase.getInstance().getReference().child("ties").child(shopName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot tieSnapshot : dataSnapshot.getChildren()) {
                            Tie tie = tieSnapshot.getValue(Tie.class);
                            if (tie != null && "Available".equals(tie.getAvailability())) {
                                // Create a new ImageView for the tie
                                ImageView tieImageView = new ImageView(customize6.this);
                                String tieName = tie.getname();

                                // Load image using Picasso or Glide library
                                Picasso.get().load(tie.getImageUrl()).into(tieImageView);

                                // Set layout parameters and click listener
                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                        dpToPx(60), dpToPx(60) // Set the size to 80dp x 80dp
                                );
                                int margin = dpToPx(10); // Set the margin to 10dp
                                layoutParams.setMargins(margin, margin, margin, margin);
                                tieImageView.setLayoutParams(layoutParams);
                                tieImageView.setBackgroundResource(R.drawable.lavender_border);
                                tieImageView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        // Handle click event
                                        addTieToPreview(tie.getImageUrl(), tie.getPrice(), tieName);
                                    }
                                });

                                // Add the ImageView to the tieList
                                tieList.addView(tieImageView);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle error
                    }
                });
    }

    private int calculateTotalAmount() {
        int total = 0;

        // Calculate total amount based on selected flowers
        for (String flowerName : selectedFlowerIds.keySet()) {
            Integer[] data = selectedFlowerIds.get(flowerName);
            int quantity = data[0];
            int price = data[1];
            total += quantity * price;
        }

        // Add tie cost if a tie is selected
        if (selectedTie != null) {
            // Assuming `tieprice` is the variable containing the tie cost
            total += tieprice;
        }

        return total;
    }


    // New method to calculate total amount based on selected flowers


    // Helper method to show a toast message
    private void addFlowerToPreview(String imageUrl, int price, String flowerName) {
        // Check if the maximum limit is reached
        if (flowerCounter >= MAX_FLOWERS) {
            // Display a Toast indicating that the maximum limit is reached
            Toast.makeText(this, "Maximum limit of flowers reached (30 flowers)", Toast.LENGTH_SHORT).show();
            return;
        }
        ImageView flowerImageView = new ImageView(this);
        // Load image using Picasso or Glide library
        Picasso.get().load(imageUrl).into(flowerImageView);

        // Increment the flower counter
        flowerCounter++;

        // Use Random to generate random values for left and top margins within the specified area
        Random random = new Random();

        // Generate random top and left values within the specified area
        int top = dpToPx(30) + random.nextInt(dpToPx(75) - dpToPx(30) + 1);
        int left = dpToPx(70) + random.nextInt(dpToPx(130) - dpToPx(70) + 1);

        // Apply layout parameters to the flower ImageView
        RelativeLayout.LayoutParams flowerLayoutParams = new RelativeLayout.LayoutParams(
                dpToPx(30), dpToPx(30) // Set the size to 30dp x 30dp (you can adjust the size)
        );
        flowerLayoutParams.leftMargin = left;
        flowerLayoutParams.topMargin = top;
        flowerImageView.setLayoutParams(flowerLayoutParams);

        // Add the flower ImageView to the flower_container directly in the XML layout
        flowerContainer.addView(flowerImageView);

        // Add the flower to the selectedFlowerIds HashMap with flower name as the ID
        // Add the flower to the selectedFlowerIds HashMap with flower name as the ID
        if (selectedFlowerIds.containsKey(flowerName)) {
            Integer[] data = selectedFlowerIds.get(flowerName);
            int quantity = data[0] + 1;
            data[0] = quantity;
            selectedFlowerIds.put(flowerName, data);
        } else {
            // Create a new array with quantity = 1 and price = flowerPrice
            Integer[] data = new Integer[]{1, price};
            selectedFlowerIds.put(flowerName, data);
        }


        flowerImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Decrement the flower counter
                flowerCounter--;

                // Remove the clicked flower from the flowerContainer
                removeFlower(flowerContainer.indexOfChild(flowerImageView));

                // Update the selectedFlowers quantity
                // Update the selectedFlowers quantity
                if (selectedFlowerIds.containsKey(flowerName)) {
                    Integer[] data = selectedFlowerIds.get(flowerName);
                    int quantity = data[0] - 1;
                    if (quantity > 0) {
                        data[0] = quantity;
                        selectedFlowerIds.put(flowerName, data);
                    } else {
                        // Remove the flower if quantity becomes 0
                        selectedFlowerIds.remove(flowerName);
                    }
                }

            }
        });
    }


    // New method to add the selected tie to the tie container
    private void addTieToPreview(String imageUrl, int price, String tieName) {
        // Set the selected tie
        tieprice = price;

        // Create a new ImageView for the tie
        ImageView tieImageView = new ImageView(this);

        // Load the tie image using Picasso or Glide library
        Picasso.get().load(imageUrl).into(tieImageView);
        // Set tie size to 30dp x 30dp
        RelativeLayout.LayoutParams tieLayoutParams = new RelativeLayout.LayoutParams(
                dpToPx(60), dpToPx(60)
        );
        tieImageView.setLayoutParams(tieLayoutParams);

        // Set the location for the tie (left: 150dp, top: 200dp)
        tieLayoutParams.leftMargin = dpToPx(0);
        tieLayoutParams.topMargin = dpToPx(0);

        // Add the tie ImageView to the tie_container directly in the XML layout
        tieContainer.removeAllViews();  // Remove any existing ties
        tieContainer.addView(tieImageView);
    }




    private void removeFlower(int flowerIndex) {
        if (completeButtonClicked) {
            return;
        }
        // Remove the flower at the specified index from the flowerContainer
        if (flowerIndex >= 0 && flowerIndex < flowerContainer.getChildCount()) {
            flowerContainer.removeViewAt(flowerIndex);
        }
    }



    private void displaySelectedFlowers() {
        totalAmount.setVisibility(View.VISIBLE);

        // Display selected flowers and their quantities in the flower container
        StringBuilder pricesText = new StringBuilder();

        // Display wrapper cost
        pricesText.append("       Wrapper Cost:                         Rs ").append(wrapperCost).append("\n");

        // Display selected tie and its cost
        if (tieprice != null) {
            int tieCost = tieprice;
            pricesText.append("       Rippen Cost:                           Rs ").append(tieCost).append("\n");
        }

        // Loop through selected flowers
        for (String flowerName : selectedFlowerIds.keySet()) {
            Integer[] data = selectedFlowerIds.get(flowerName);
            int quantity = data[0];
            int price = data[1];

            // Display each flower and its price
            pricesText.append("       ").append(flowerName).append("  :          (").append(quantity).append("*").append(price).append(")    =    Rs ").append(quantity * price).append("\n");
        }
        if (tieprice != null) {
            // Display total price
            pricesText.append("       Total Price:                                 Rs ").append( calculateTotalAmount()+tieprice+ wrapperCost);
        }
        else{
            pricesText.append("       Total Price:                                 Rs ").append( calculateTotalAmount()+ wrapperCost);
        }
        // Set the formatted prices list to the totalAmount TextView
        totalAmount.setText(pricesText.toString());

        // Save pricesText in UserData as a session
        UserData.getInstance().setDescription(pricesText.toString());

        // Hide buttons and lists after completion
        flowersButton.setVisibility(View.GONE);
        tiesButton.setVisibility(View.GONE);
        flowerList.setVisibility(View.GONE);
        tieList.setVisibility(View.GONE);
        completeButton.setVisibility(View.GONE);
        finishButton.setVisibility(View.VISIBLE);

        combineAndSaveImage();
        centerViewsHorizontally();
    }

    private void combineAndSaveImage() {
        // Capture and save the screenshot of the specified area
        Bitmap screenshotBitmap = captureScreenshotOfArea(
                flowerContainer,
                dpToPx(30),  // left margin in pixels
                dpToPx(150), // top margin in pixels
                dpToPx(222), // width in pixels
                dpToPx(234)  // height in pixels
        );

        // Save the screenshot in UserData
        UserData userData = UserData.getInstance();
        userData.setScreenshot(screenshotBitmap);


    }

    // Helper method to capture a screenshot of a specific area
    private Bitmap captureScreenshotOfArea(View view, int left, int top, int width, int height) {
        Bitmap screenshotBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        // Create a Canvas using the screenshotBitmap
        Canvas canvas = new Canvas(screenshotBitmap);

        // Translate the canvas to the specified area
        canvas.translate(-left, -top);

        // Draw the specified area onto the Canvas
        findViewById(android.R.id.content).draw(canvas);

        return screenshotBitmap;
    }



    // Helper method to convert dp to pixels
    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                getResources().getDisplayMetrics()
        );
    }
    private void updateTotalPrice() {
        // Calculate total price based on selected flowers, wrapper cost, and tie cost

        int totalPrice = calculateTotalAmount()  + wrapperCost;

        if (tieprice != null) {
            totalPrice+=tieprice;
        }
        // Add delivery fee of 40
        int totalWithDelivery = totalPrice + 40;

        // Create a StringBuilder to build the formatted text
        StringBuilder deliveryFeeTextView = new StringBuilder();

        // Append total amount to the StringBuilder
        deliveryFeeTextView.append("       Total Amount:                       Rs ").append(totalPrice);

        // Append delivery fee if there is a selected tie

        deliveryFeeTextView.append("\n       Delivery Fee:                          Rs 40");


        // Append total with delivery to the StringBuilder
        deliveryFeeTextView.append("\n       Total:                                     Rs ").append(totalWithDelivery);

        // Set the formatted prices list to the finalAmount TextView
        totalAmount.setText(deliveryFeeTextView.toString());
        finishButton.setVisibility(View.GONE);

        placeorder.setVisibility(View.VISIBLE);
        // Save pricesText and totalWithDelivery in UserData as session data

        UserData.getInstance().setprice(totalWithDelivery);

    }
    private void centerViewsHorizontally() {
        // Center imagePreview
        RelativeLayout.LayoutParams imagePreviewLayoutParams = (RelativeLayout.LayoutParams) imagePreview.getLayoutParams();
        imagePreviewLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        imagePreview.setLayoutParams(imagePreviewLayoutParams);

        // Center flowerContainer
        RelativeLayout.LayoutParams flowerContainerLayoutParams = (RelativeLayout.LayoutParams) flowerContainer.getLayoutParams();
        flowerContainerLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        flowerContainer.setLayoutParams(flowerContainerLayoutParams);

        // Center tieContainer
        RelativeLayout.LayoutParams tieContainerLayoutParams = (RelativeLayout.LayoutParams) tieContainer.getLayoutParams();
        tieContainerLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        tieContainer.setLayoutParams(tieContainerLayoutParams);
    }

}
