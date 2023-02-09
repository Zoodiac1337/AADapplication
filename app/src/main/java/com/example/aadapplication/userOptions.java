package com.example.aadapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.appcompat.app.AppCompatActivity;

public class userOptions extends AppCompatActivity {
    private String email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_options);
        Bundle bundle = getIntent().getExtras();
        email = bundle.getString("email");

    }

    public void passwordForm(View view) {

        Intent myIntent = new Intent(userOptions.this, PasswordPopup.class);
        myIntent.putExtra("email", email);
        startActivity(myIntent);

    }
}