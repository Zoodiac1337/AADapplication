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

    public void toScanBarCode(View view) {
        Intent myIntent = new Intent(headChefActivity.this, scanBarCodeActivity.class);
        startActivity(myIntent);
    }
}