package com.example.bshop;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class EditProfileActivity2 extends AppCompatActivity {

    private EditText editName, editShopname, editAddress, editEmail, editPassword,editnumber;
    private ImageView profileImageView;
    private Button saveButton;

    private String nameUser, shopnameUser, addressUser, emailUser, passwordUser, number;
    private DatabaseReference reference;
    private FirebaseAuth mAuth;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile2);

        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference("admin");

        editName = findViewById(R.id.editName);
        editShopname = findViewById(R.id.editShopname);
        editAddress = findViewById(R.id.editAddress);
        editEmail = findViewById(R.id.editEmail);
        editnumber = findViewById(R.id.signup_phonenumber);
        editPassword = findViewById(R.id.editPassword);
        profileImageView = findViewById(R.id.profileImageView);
        saveButton = findViewById(R.id.saveButton);

        showData();

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImagePicker();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateUserData()) {
                    if (imageUri != null) {
                        uploadImageToStorage(nameUser);
                    } else {
                        saveUserData();
                    }
                } else {
                    Toast.makeText(EditProfileActivity2.this, "Please enter valid data", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean validateUserData() {
        String newName = editName.getText().toString();
        String newShopname = editShopname.getText().toString();
        String newAddress = editAddress.getText().toString();
        String newEmail = editEmail.getText().toString();
        String newPassword = editPassword.getText().toString();

        return !newName.isEmpty() && !newShopname.isEmpty() && !newAddress.isEmpty()
                && !newEmail.isEmpty() && !newPassword.isEmpty();
    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            profileImageView.setImageURI(imageUri);
        }
    }

    private void uploadImageToStorage(String name) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("admin_profile_images");
        StorageReference shopImageRef = storageRef.child(name + ".jpg");

        shopImageRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        shopImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                reference.child(nameUser).child("imageURL").setValue(uri.toString());
                                saveUserData();
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditProfileActivity2.this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserData() {
        if (validateUserData()) {
            String newName = editName.getText().toString();
            String newShopname = editShopname.getText().toString();
            String newAddress = editAddress.getText().toString();
            String newEmail = editEmail.getText().toString();
            String newphonenumber = editnumber.getText().toString();
            String newPassword = editPassword.getText().toString();

            reference.child(nameUser).child("name").setValue(newName);
            reference.child(nameUser).child("shopname").setValue(newShopname);
            reference.child(nameUser).child("address").setValue(newAddress);
            reference.child(nameUser).child("email").setValue(newEmail);
            reference.child(nameUser).child("phonenumber").setValue(newphonenumber);
            reference.child(nameUser).child("password").setValue(newPassword);

            Toast.makeText(EditProfileActivity2.this, "Data saved successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(EditProfileActivity2.this, sellHome.class);
            startActivity(intent);
        } else {
            Toast.makeText(EditProfileActivity2.this, "Please enter valid data", Toast.LENGTH_SHORT).show();
        }
    }

    private void showData() {
        Intent intent = getIntent();

        nameUser = intent.getStringExtra("name");
        shopnameUser = intent.getStringExtra("shopname");
        addressUser = intent.getStringExtra("address");
        emailUser = intent.getStringExtra("email");
        number = intent.getStringExtra("number");
        passwordUser = intent.getStringExtra("password");

        editName.setText(nameUser);
        editShopname.setText(shopnameUser);
        editAddress.setText(addressUser);
        editEmail.setText(emailUser);
        editnumber.setText(number);
        editPassword.setText(passwordUser);

        // Fetch and load the profile image using Picasso
        DatabaseReference shopReference = reference.child(nameUser);
        shopReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String imageURL = snapshot.child("imageURL").getValue(String.class);
                    if (imageURL != null && !imageURL.isEmpty()) {
                        Picasso.get().load(imageURL).into(profileImageView);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled if needed
            }
        });
    }
}
