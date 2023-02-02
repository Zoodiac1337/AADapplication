package com.example.aadapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class headChefActivity extends AppCompatActivity {

    private String email;
    private String fridgeID;
    private String name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_head_chef);
        Bundle bundle = getIntent().getExtras();
        email = bundle.getString("email");
        name = bundle.getString("name");
        fridgeID = bundle.getString("fridgeID");
    }

    public void insertItemButton(View view) {
        Intent myIntent = new Intent(headChefActivity.this, barcode_scanner.class);
        myIntent.putExtra("name", name);
        myIntent.putExtra("fridgeID", fridgeID);
        startActivity(myIntent);

    }

    public void checkStockButton(View view) {
        Intent myIntent = new Intent(headChefActivity.this, checkStockActivity.class);
        myIntent.putExtra("fridgeID", fridgeID);
        startActivity(myIntent);
    }

    public void userAccessButton(View view) {
        Intent myIntent = new Intent(headChefActivity.this, userAccessActivity.class);
        myIntent.putExtra("fridgeID", fridgeID);
        startActivity(myIntent);
    }

    public void removeItemButton(View view) {
        Intent myIntent = new Intent(headChefActivity.this, Remove_barcode_scanner.class);
        myIntent.putExtra("fridgeID", fridgeID);
        myIntent.putExtra("name", name);
        startActivity(myIntent);
    }
}