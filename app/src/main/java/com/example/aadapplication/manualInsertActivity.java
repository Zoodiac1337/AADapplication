package com.example.aadapplication;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@RequiresApi(api = Build.VERSION_CODES.O)
public class manualInsertActivity extends AppCompatActivity {

    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    LocalDateTime now = LocalDateTime.now();


    FirebaseFirestore db = FirebaseFirestore.getInstance();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_insert);
    }

    public void saveAddingItemButtonClicked(View view){
        Bundle bundle = getIntent().getExtras();
        String email = bundle.getString("message");

        EditText itemsName = findViewById(R.id.insertItemsName);
        EditText itemQuantity = findViewById(R.id.insertItemQuantity);
        EditText expiryDate = findViewById(R.id.insertExpiryDate);

        if ((itemsName.getText().toString().isEmpty()) || (itemQuantity.getText().toString().isEmpty()) || (expiryDate.getText().toString().isEmpty())) {
            Toast.makeText(manualInsertActivity.this, "Missing information!", Toast.LENGTH_SHORT).show();
            return;
        }
        DocumentReference docRef = db.collection("Fridges/12345/Items").document();
        Map<String, Object> item = new HashMap<>();
        int Quantity = Integer.parseInt(itemQuantity.getText().toString());
        Date date = null;
        try {
            date = new SimpleDateFormat("dd/MM/yyyy").parse(expiryDate.getText().toString());
        } catch (ParseException e) {
            Toast.makeText(manualInsertActivity.this, "Incorrect date format!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return;

        }
        Date currentDate = new Date();

        item.put("Items name", itemsName.getText().toString().toLowerCase());
        item.put("Quantity", Quantity);
        item.put("Expires on", date);
        item.put("Inserted by", email);
        item.put("Inserted on", currentDate);

        itemsName.setText("");
        itemQuantity.setText("");
        expiryDate.setText("");

        docRef.set(item).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(manualInsertActivity.this, "Saved", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(manualInsertActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }


}