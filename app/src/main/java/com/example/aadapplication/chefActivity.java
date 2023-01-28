package com.example.aadapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class chefActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle bundle = getIntent().getExtras();
        String email = bundle.getString("message");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chef);
    }
}