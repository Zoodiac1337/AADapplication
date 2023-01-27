package com.example.aadapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class scanBarCodeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_bar_code);
    }

    public void toManualInsert(View view) {
        Intent myIntent = new Intent(scanBarCodeActivity.this, manualInsertActivity.class);
        startActivity(myIntent);
    }
}