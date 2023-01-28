package com.example.aadapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class headChefActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_head_chef);
    }

    public void insertItemButton(View view) {

        startActivity(new Intent(headChefActivity.this, barcode_scanner.class));

    }

    public void toScanBarCode(View view) {
        Bundle bundle = getIntent().getExtras();
        String email = bundle.getString("message");
        Intent myIntent = new Intent(headChefActivity.this, scanBarCodeActivity.class);
        myIntent.putExtra("message", email);
        startActivity(myIntent);
    }
}