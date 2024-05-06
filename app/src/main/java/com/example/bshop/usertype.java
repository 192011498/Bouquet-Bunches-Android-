package com.example.bshop;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;

public class usertype extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usertype);

        LinearLayout likeLayout1 = findViewById(R.id.likeLayout1);
        LinearLayout likeLayout2 = findViewById(R.id.likeLayout);

        likeLayout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start the LoginActivity when the Customer LinearLayout is clicked
                Intent intent = new Intent(usertype.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        likeLayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start the login2 (Seller Login) activity when the Seller LinearLayout is clicked
                Intent intent = new Intent(usertype.this, login2.class);
                startActivity(intent);
            }
        });
    }
}
