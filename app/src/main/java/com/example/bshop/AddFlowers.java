package com.example.bshop;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.bshop.UserData;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bshop.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class AddFlowers extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText priceEditText,nameedit;
    private ImageView wrapperImageView;
    private Button uploadButton;

    private Uri imageUri;
    private StorageReference storageReference;
    private DatabaseReference wrappersReference;
    private String shopName; // Add this variable to store shopName

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_flowers);

        // Initialize Firebase storage and database references
        storageReference = FirebaseStorage.getInstance().getReference().child("flowers");
        wrappersReference = FirebaseDatabase.getInstance().getReference("flowers");

        // Get the shopName from the UserData singleton class
        UserData userData = UserData.getInstance();
        shopName = userData.getshopName();


        priceEditText = findViewById(R.id.priceEditText);
        nameedit= findViewById(R.id.nameText);
        wrapperImageView = findViewById(R.id.wrapperImageView);
        uploadButton = findViewById(R.id.uploadButton);

        wrapperImageView.setOnClickListener(v -> openFileChooser());

        uploadButton.setOnClickListener(v -> uploadWrapper());
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void uploadWrapper() {
        String priceString = priceEditText.getText().toString();
        String nameString = nameedit.getText().toString();

        // Check if both image and price are provided
        if (imageUri != null && !priceString.isEmpty()) {
            double price = Double.parseDouble(priceString);

            StorageReference fileReference = storageReference.child(shopName).child("flowers" + System.currentTimeMillis() + ".jpg");

            fileReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Get the download URL from the task snapshot
                        fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            // Create a new Wrapper object with shopName
                            Uploadflower wrapper = new Uploadflower(shopName, uri.toString(), price, "Available",nameString );

                            // Add the wrapper to the database under shopName/coverX
                            DatabaseReference newWrapperRef = wrappersReference.child(shopName).push();
                            newWrapperRef.setValue(wrapper);

                            Toast.makeText(AddFlowers.this, "Wrapper added successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(AddFlowers.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "Please provide both image and price", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            wrapperImageView.setImageURI(imageUri);
        }
    }
}
