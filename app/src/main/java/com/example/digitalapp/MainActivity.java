package com.example.digitalapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button rlogin = findViewById(R.id.btnCustomerLogin);
        Button rdriverlogin = findViewById(R.id.btnDriverLogin);

        startService(new Intent(MainActivity.this, OnAppKilled.class));

        rlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                intent = new Intent(MainActivity.this, CustomerLoginActivity.class);
                startActivity(intent);
                //finish();
            }
        });
        rdriverlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DriverLoginActivity.class);
                startActivity(intent);
            }
        });

    }
}
