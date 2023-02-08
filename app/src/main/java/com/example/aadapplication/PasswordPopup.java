package com.example.aadapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class PasswordPopup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_popup);
    }
    private void changePassword(View view){

        //check all required fields are not null
        String oldPass = findViewById(R.id.OldPasswordField).toString();
        String newPassOne = findViewById(R.id.NewPasswordField1).toString();
        String newPassTwo = findViewById(R.id.NewPasswordField2).toString();

        if(oldPass.contains(" ")||newPassOne.contains(" ")||newPassTwo.contains(" ")){
            Toast.makeText(this,"one or more fields containe a space",Toast.LENGTH_SHORT);
            return;
        }


        //check that old password is correct



        //check that new passwords match

        //replace new password

        //give feedback

    }
}