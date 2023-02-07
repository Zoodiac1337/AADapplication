package com.example.aadapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class addNewUserActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String type;
    private String fridgeID;
    private String sEmail;
    private String sName;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_user);

        Bundle bundle = getIntent().getExtras();
        fridgeID = bundle.getString("fridgeID");

        Spinner dropdown = findViewById(R.id.spinner);

        String[] items = new String[]{"DeliveryDriver", "Chef", "HeadChef"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);

        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(this);

    }
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        type = parent.getItemAtPosition(pos).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        return;
    }

    public void onSubmitClicked(View view) {
        EditText email = findViewById(R.id.editTextTextEmailAddress2);
        EditText name = findViewById(R.id.editTextName);

        sName = name.getText().toString();
        sEmail = email.getText().toString();
        DocumentReference docRef = db.collection("Fridges/" + fridgeID + "/Users").document(sEmail);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) Toast.makeText(addNewUserActivity.this, "User with this email address already exists!", Toast.LENGTH_SHORT).show();
                    else if (!sEmail.contains("@gmail.com")) Toast.makeText(addNewUserActivity.this, "Wrong email type!", Toast.LENGTH_SHORT).show();
                    else {
                        Map<String, Object> user = new HashMap<>();
                        user.put("Name", sName);
                        user.put("Type", type);
                        docRef.set(user);
                        Toast.makeText(addNewUserActivity.this, "Added a new "+type+"!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}

