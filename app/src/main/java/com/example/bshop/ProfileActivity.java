package com.example.bshop;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bshop.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    TextView profileName, profileEmail, profileUsername, profilePassword, number;

    Button editProfile;
    ImageView profileImage;  // New ImageView for displaying the profile image

    private static final int EDIT_PROFILE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileName = findViewById(R.id.profileName);
        profileEmail = findViewById(R.id.profileEmail);
        profileUsername = findViewById(R.id.profileUsername);
        profilePassword = findViewById(R.id.profilePassword);
        number = findViewById(R.id.PhoneNumber2);
        editProfile = findViewById(R.id.editButton);
        profileImage = findViewById(R.id.profileImg);  // Initialize the ImageView

        showUserData();

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startEditProfileActivity();
            }
        });
    }

    private void showUserData() {
        UserData userData = UserData.getInstance();
        String usernameUser = userData.getUsername();




        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUserDatabase = reference.orderByChild("username").equalTo(usernameUser);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    DataSnapshot userSnapshot = snapshot.getChildren().iterator().next();
                    String nameFromDB = userSnapshot.child("name").getValue(String.class);
                    String emailFromDB = userSnapshot.child("email").getValue(String.class);
                    String numberFromDB = userSnapshot.child("phonenumber").getValue(String.class);
                    String passwordFromDB = userSnapshot.child("password").getValue(String.class);
                    String imageFromDB = userSnapshot.child("imageURL").getValue(String.class);  // Updated to "imageURL"

                    profileUsername.setText(usernameUser);
                    profileName.setText(nameFromDB);
                    profileEmail.setText(emailFromDB);
                    number.setText(numberFromDB);
                    profilePassword.setText(passwordFromDB);

                    // Load the profile image using Picasso
                    Picasso.get().load(imageFromDB).into(profileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled if needed
            }
        });
    }


    private void startEditProfileActivity() {
        Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
        intent.putExtra("name", profileName.getText().toString());
        intent.putExtra("email", profileEmail.getText().toString());
        intent.putExtra("username",profileUsername.getText().toString());
        intent.putExtra("number",number.getText().toString());
        intent.putExtra("password", profilePassword.getText().toString());

        startActivityForResult(intent, EDIT_PROFILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EDIT_PROFILE_REQUEST && resultCode == RESULT_OK) {
            // Handle the data returned from EditProfileActivity if needed
            // For example, you can update the UI with the edited data
            showUserData();
        }
    }
}
