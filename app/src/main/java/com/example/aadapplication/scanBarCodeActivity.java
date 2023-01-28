package com.example.aadapplication;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

public class scanBarCodeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_bar_code);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void toManualInsert(View view) {
        Bundle bundle = getIntent().getExtras();
        String email = bundle.getString("message");
        Intent myIntent = new Intent(scanBarCodeActivity.this, manualInsertActivity.class);
        myIntent.putExtra("message", email);
        startActivity(myIntent);
    }
}