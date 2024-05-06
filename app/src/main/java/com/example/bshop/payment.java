package com.example.bshop;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class payment extends AppCompatActivity implements PaymentResultListener {

    private TextView amount;
    private Button payBtn;
    private ImageView ivCombinedImage;
    private BroadcastReceiver razorpayReceiver;
    private DatabaseReference databaseReference,databaseReference1;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        amount = findViewById(R.id.Price);
        payBtn = findViewById(R.id.idBtnPay);
        ivCombinedImage = findViewById(R.id.ivCombinedImage);

        databaseReference = FirebaseDatabase.getInstance().getReference("orders");
        databaseReference1 = FirebaseDatabase.getInstance().getReference("order");
        storage = FirebaseStorage.getInstance();

        amount.setText("Total Price: $" + UserData.getInstance().getprice());

        Bitmap screenshot = UserData.getInstance().getScreenshot();
        if (screenshot != null) {
            ivCombinedImage.setImageBitmap(screenshot);
        }

        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String samount = String.valueOf(UserData.getInstance().getprice());
                int amount = Math.round(Float.parseFloat(samount) * 100);

                // Generate a unique filename using UUID
                String filename = "screenshot_" + System.currentTimeMillis() + ".png";

                // Upload screenshot to Firebase Storage
                uploadScreenshot(filename, screenshot);

                // Continue with Razorpay payment
                startRazorpayPayment(amount, filename);
            }
        });
    }

    private void uploadScreenshot(String filename, Bitmap screenshot) {
        // Reference to the root directory
        StorageReference storageRef = storage.getReference();

        // Reference to the image file in Firebase Storage
        StorageReference imageRef = storageRef.child("screenshots").child(filename);

        // Convert Bitmap to byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        screenshot.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        // Upload the image to Firebase Storage
        UploadTask uploadTask = imageRef.putBytes(data);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            // Image uploaded successfully
            // Get the download URL and continue with payment
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageUrl = uri.toString();
                // Now, continue with payment or any other action
                makePayment(imageUrl);
            });
        }).addOnFailureListener(e -> {
            // Handle the failure
            Toast.makeText(payment.this, "Failed to upload screenshot.", Toast.LENGTH_SHORT).show();
        });
    }

    private void startRazorpayPayment(int amount, String imageUrl) {
        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_FWpu8hnRZ68fWr");
        checkout.setImage(R.drawable.logo2);

        JSONObject options = new JSONObject();
        try {
            options.put("name", "CodingSTUFF");
            options.put("description", "Reference No. #123456");
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            options.put("theme.color", "#3399cc");
            options.put("currency", "INR");
            options.put("amount", amount);
            options.put("prefill.email", "gaurav.kumar@example.com");
            options.put("prefill.contact", "9988776655");
            JSONObject retryObj = new JSONObject();
            retryObj.put("enabled", true);
            retryObj.put("max_count", 4);
            options.put("retry", retryObj);

            razorpayReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    // Handle the broadcast if needed
                }
            };

            registerReceiver(razorpayReceiver, new IntentFilter("some_intent_filter"));

            checkout.open(payment.this, options);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void makePayment(String imageUrl) {
        // Retrieve data
        String username = UserData.getInstance().getUsername();
        String shopName = UserData.getInstance().getshopName();
        float price = UserData.getInstance().getprice();
        String description = UserData.getInstance().getDescription();
        String address = UserData.getInstance().getAddress();
        String deliveryDate = UserData.getInstance().getDate();
        String name = UserData.getInstance().getname();

        // Create OrderDatabaseHelper object
        OrderDatabaseHelper orderDatabaseHelper = new OrderDatabaseHelper(username, shopName, imageUrl, price, description, address, deliveryDate, "pending", name);

        // Create a unique key for the order
        String orderId = databaseReference.push().getKey();

        // Store the order in the database
        databaseReference.child(username).child(orderId).setValue(orderDatabaseHelper);

        // Store the order in the database
        databaseReference1.child(shopName).child(orderId).setValue(orderDatabaseHelper);

        // Continue with the Razorpay payment (if needed)
        // You can add your Razorpay payment logic here if it depends on imageUrl
    }

    @Override
    public void onPaymentSuccess(String s) {
        Toast.makeText(payment.this, "Payment is successful : " + s, Toast.LENGTH_SHORT).show();

        Intent addressIntent = new Intent(payment.this, cusHome.class);

        // Start the Address activity
        startActivity(addressIntent);
    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(this, "Payment Failed due to error : " + s, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 1000);
        if (razorpayReceiver != null) {
            unregisterReceiver(razorpayReceiver);
        }
    }
}
