package com.example.aadapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class signInActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        mAuth = FirebaseAuth.getInstance();
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent myIntent = new Intent(signInActivity.this, chooseFridgeActivity.class);
            String email = currentUser.getEmail();
            myIntent.putExtra("message", email);
            startActivity(myIntent);
            Toast.makeText(signInActivity.this, email+" logged in", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    public void signin(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    FirebaseUser user = mAuth.getCurrentUser();
//                    Toast.makeText(SignInActivity.this, "Authentication successful, UID: "+uid, Toast.LENGTH_SHORT).show();
                    Intent myIntent = new Intent(signInActivity.this, chooseFridgeActivity.class);
                    myIntent.putExtra("message", email);
                    startActivity(myIntent);
                    finish();
                    Toast.makeText(signInActivity.this, email+" logged in", Toast.LENGTH_SHORT).show();
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("SignInActivity", "signInWithEmail:failure", task.getException());
                    Toast.makeText(signInActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void getFridge(String email){
        DocumentReference docRef = db.collection("Fridges/12345/Users").document(email);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("SignInActivity", "Account Type: " + document.getString("Type"));
                        Toast.makeText(signInActivity.this, "Account Type: " + document.getString("Type"), Toast.LENGTH_SHORT).show();

                    } else {
                        Log.d("SignInActivity", "No such document");
                        Toast.makeText(signInActivity.this, "No such document", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d("SignInActivity", "get failed with ", task.getException());
                    Toast.makeText(signInActivity.this, "get failed with ", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void signinButtonClicked(View view){
        EditText email = findViewById(R.id.editTextTextEmailAddress);
        EditText password = findViewById(R.id.editTextTextPassword);
        String sEmail = email.getText().toString();
        String sPassword = password.getText().toString();
        signin(sEmail, sPassword);
    }
    public void toSignupButtonClicked(View view) {
        Intent myIntent = new Intent(signInActivity.this, MainActivity.class);
        startActivity(myIntent);
    }
}