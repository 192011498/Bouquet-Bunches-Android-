package com.example.bshop;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class signup2 extends AppCompatActivity {

    EditText signupName, signupEmail, signupUsername, signupPassword,signupAddress,signupnumber;
    TextView loginRedirectText;
    Button signupButton;
    FirebaseDatabase database;
    DatabaseReference reference;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup2);

        signupName = findViewById(R.id.signup_shopname1);
        signupEmail = findViewById(R.id.signup_email1);
        signupUsername = findViewById(R.id.signup_username1);
        signupAddress = findViewById(R.id.signup_address1);
        signupnumber=findViewById(R.id.signup_phonenumber);
        signupPassword = findViewById(R.id.signup_password1);
        signupButton = findViewById(R.id.signup_button1);
        loginRedirectText = findViewById(R.id.loginRedirectText1);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                database = FirebaseDatabase.getInstance();
                reference = database.getReference("admin");

                String username = signupUsername.getText().toString();
                String email = signupEmail.getText().toString();
                String shopname = signupName.getText().toString();
                String address = signupAddress.getText().toString();
                String number = signupnumber.getText().toString();
                String password = signupPassword.getText().toString();

                // Check if any of the fields is empty
                if (username.isEmpty() || email.isEmpty() || shopname.isEmpty() || address.isEmpty() ||  number.isEmpty() || password.isEmpty()) {
                    Toast.makeText(signup2.this, "Please fill in all the fields", Toast.LENGTH_SHORT).show();
                } else {
                    // All fields are provided, proceed with signup
                    HelperClass2 helperClass = new HelperClass2(username, email, shopname, address, number, password);
                    reference.child(username).setValue(helperClass);

                    Toast.makeText(signup2.this, "You have signed up successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(signup2.this, login2.class);
                    startActivity(intent);
                }
            }
        });

        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(signup2.this, login2.class);
                startActivity(intent);
            }
        });
    }
}