package com.example.aadapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent myIntent = new Intent(MainActivity.this, chooseFridgeActivity.class);
            String email = currentUser.getEmail();
            myIntent.putExtra("message", email);
            startActivity(myIntent);
            Toast.makeText(MainActivity.this, email+" logged in", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    public void signup(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d("MainActivity", "createUserWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();

                    Toast.makeText(MainActivity.this, "Registration successful. Please sign in.", Toast.LENGTH_SHORT).show();
                    Intent myIntent = new Intent(MainActivity.this, signInActivity.class);
                    startActivity(myIntent);
                    finish();
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("MainActivity", "createUserWithEmail:failure", task.getException());
                    Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void signupButtonClicked(View view){
        EditText email = findViewById(R.id.editTextTextEmailAddress);
        EditText password = findViewById(R.id.editTextTextPassword);
        String sEmail = email.getText().toString();
        String sPassword = password.getText().toString();
        signup(sEmail, sPassword);
    }
    public void toSigninButtonClicked(View view) {
        Intent myIntent = new Intent(MainActivity.this, signInActivity.class);
        startActivity(myIntent);
    }
}