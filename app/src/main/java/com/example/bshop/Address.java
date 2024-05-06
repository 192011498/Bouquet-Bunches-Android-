package com.example.bshop;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class Address extends AppCompatActivity {

    private EditText etFullName, etPhoneNumber, etCity, etState, etPinCode, etDate;
    private TextView tvPrice;
    private Button btnPayNow;
    private ImageView ivCombinedImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        // Initialize UI elements
        etFullName = findViewById(R.id.etFullName);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etCity = findViewById(R.id.etCity);
        etState = findViewById(R.id.etState);
        etPinCode = findViewById(R.id.etPinCode);
        etDate = findViewById(R.id.etDate);

        btnPayNow = findViewById(R.id.btnPayNow);






        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });



        // Set onClickListener for the Pay Now button
        btnPayNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Capture user input
                String fullName = etFullName.getText().toString();
                String phoneNumber = etPhoneNumber.getText().toString();
                String city = etCity.getText().toString();
                String state = etState.getText().toString();
                String pinCode = etPinCode.getText().toString();
                String date = etDate.getText().toString();

                // Combine user input into a single string
                String combinedData =
                        "  " + city +
                        ",  " + state +
                        ", " + pinCode+
                        ", " + phoneNumber;

                // Save combined data in UserData session
                UserData.getInstance().setAddress(combinedData);
                UserData.getInstance().setDate(date);
                UserData.getInstance().setname(fullName);



                Intent addressIntent = new Intent(Address.this, payment.class);

                // Start the Address activity
                startActivity(addressIntent);

            }
        });
    }
    private void showDatePickerDialog() {
        // Get the current date
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        // Create a DatePickerDialog and show it
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int selectedYear, int selectedMonth, int selectedDay) {
                        // Update the EditText with the selected date
                        etDate.setText(selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear);
                    }
                },
                year, month, dayOfMonth
        );

        // Show the dialog
        datePickerDialog.show();
    }


}
