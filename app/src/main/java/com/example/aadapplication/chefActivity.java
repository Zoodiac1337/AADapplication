package com.example.aadapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class chefActivity extends AppCompatActivity {

    private String email;
    private String fridgeID;
    private String name;
    private TextView fridgeTextID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chef);

        Bundle bundle = getIntent().getExtras();
        email = bundle.getString("email");
        name = bundle.getString("name");
        fridgeID = bundle.getString("fridgeID");

        fridgeTextID = findViewById(R.id.fridgeIDtext2);
        fridgeTextID.setText("Fridge #"+fridgeID);
    }

    public void insertItemButton(View view) {
        Intent myIntent = new Intent(chefActivity.this, barcode_scanner.class);
        myIntent.putExtra("name", name);
        myIntent.putExtra("fridgeID", fridgeID);
        startActivity(myIntent);

    }

    public void checkStockButton(View view) {
        Intent myIntent = new Intent(chefActivity.this, checkStockActivity.class);
        myIntent.putExtra("fridgeID", fridgeID);
        startActivity(myIntent);
    }

    public void removeItemButton(View view) {
        Intent myIntent = new Intent(chefActivity.this, Remove_barcode_scanner.class);
        myIntent.putExtra("fridgeID", fridgeID);
        myIntent.putExtra("name", name);
        startActivity(myIntent);
    }

    public void optionMenu(View view) {
        Intent myIntent = new Intent(chefActivity.this, userOptions.class);
        myIntent.putExtra("email", email);
        startActivity(myIntent);
    }
}