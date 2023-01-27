package com.example.aadapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;

public class chooseFridgeActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    ListView listView;
    Button button;
    EditText editText;


    ArrayList<String> arrayList;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_fridge);

        editText = findViewById(R.id.PlainTextView);
        button = findViewById(R.id.addButton);
        listView = findViewById(R.id.fridges_listview);

        arrayList = new ArrayList<>();
        adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(adapter);

        Bundle bundle = getIntent().getExtras();
        String email = bundle.getString("message");
        SharedPreferences savedFridges = getApplicationContext().getSharedPreferences(email, 0);
        Map<String, ?> allFridges = savedFridges.getAll();
        for (Map.Entry<String, ?> entry : allFridges.entrySet()) {
            Log.d("map values", entry.getKey() + ": " + entry.getValue().toString());
            arrayList.add(entry.getValue().toString());
            adapter.notifyDataSetChanged();
        }



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchFridge = editText.getText().toString();

                DocumentReference docRef = db.collection("Fridges").document(searchFridge);
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists() && !arrayList.contains(searchFridge)) {
                                arrayList.add(searchFridge);
                                adapter.notifyDataSetChanged();
                                editText.setText("");
                                SharedPreferences.Editor editor = savedFridges.edit();
                                editor.putString("Fridges", searchFridge);
                                editor.commit();

                            } else if (arrayList.contains(searchFridge)){
                                Toast.makeText(chooseFridgeActivity.this, "Fridge already saved", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.d("chooseFridgeActivity", "No fridge with this ID exists");
                                Toast.makeText(chooseFridgeActivity.this, "No fridge with this ID exists", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.d("chooseFridgeActivity", "get failed with ", task.getException());
                            Toast.makeText(chooseFridgeActivity.this, "Error connecting to the database", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DocumentReference docRef = db.collection("Fridges/"+parent.getItemAtPosition(position)+"/Users").document(email);
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d("chooseFridgeActivity", "Account Type: " + document.getString("Type"));
                                Toast.makeText(chooseFridgeActivity.this, "Account Type: " + document.getString("Type"), Toast.LENGTH_SHORT).show();
                                if (document.getString("Type").equals("HeadChef")) {
                                    Intent myIntent = new Intent(chooseFridgeActivity.this, headChefActivity.class);
                                    startActivity(myIntent);
                                } else if (document.getString("Type").equals("Chef")) {
                                    Intent myIntent = new Intent(chooseFridgeActivity.this, chefActivity.class);
                                    startActivity(myIntent);
                                } else if (document.getString("Type").equals("DeliveryDriver")) {
                                    Intent myIntent = new Intent(chooseFridgeActivity.this, deliveryDriverActivity.class);
                                    startActivity(myIntent);
                                }

                            } else {
                                Log.d("chooseFridgeActivity", "No such document");
                                Toast.makeText(chooseFridgeActivity.this, "No such document", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.d("chooseFridgeActivity", "get failed with ", task.getException());
                            Toast.makeText(chooseFridgeActivity.this, "Error connecting to the database", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                Toast.makeText(chooseFridgeActivity.this, " "+ parent.getItemAtPosition(position), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void signoutButtonClicked(View view){
        FirebaseAuth.getInstance().signOut();
        Intent myIntent = new Intent(chooseFridgeActivity.this, signInActivity.class);
        startActivity(myIntent);
        finish();
    }
}