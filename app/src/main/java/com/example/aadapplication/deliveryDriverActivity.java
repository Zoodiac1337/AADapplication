package com.example.aadapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class deliveryDriverActivity extends AppCompatActivity {

    private String email;
    private String fridgeID;
    private String name;
    private TextView fridgeTextID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_driver);

        Bundle bundle = getIntent().getExtras();
        email = bundle.getString("email");
        name = bundle.getString("name");
        fridgeID = bundle.getString("fridgeID");

        fridgeTextID = findViewById(R.id.fridgeIDtext3);
        fridgeTextID.setText("Fridge #"+fridgeID);
    }

    public void insertItemButton(View view) {
        Intent myIntent = new Intent(deliveryDriverActivity.this, barcode_scanner.class);
        myIntent.putExtra("name", name);
        myIntent.putExtra("fridgeID", fridgeID);
        startActivity(myIntent);

    }
}