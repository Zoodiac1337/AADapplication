package com.example.aadapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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

    public void contactHeadChefButton(View view) {
        Intent myIntent = new Intent(deliveryDriverActivity.this, contactHeadChefActivity.class);
        myIntent.putExtra("name", name);
        myIntent.putExtra("fridgeID", fridgeID);
        startActivity(myIntent);
    }

    public void showMyPinNumberButton(View view){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference= db.document("/Fridges/"+fridgeID+"/Users/"+email);

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Toast.makeText(deliveryDriverActivity.this, "Your pin number: "+document.getLong("PinNumber"), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}