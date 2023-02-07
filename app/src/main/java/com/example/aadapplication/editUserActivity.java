package com.example.aadapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
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

public class editUserActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String type;
    private String fridgeID;
    private String sEmail;
    private String sName;
    private String[] items;



    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        TextView email = findViewById(R.id.textEmailAddress);
        EditText name = findViewById(R.id.editTextName);
        Spinner dropdown = findViewById(R.id.spinner);

        Bundle bundle = getIntent().getExtras();
        fridgeID = bundle.getString("fridgeID");
        sEmail = bundle.getString("Email");
        sName = bundle.getString("Name");
        type = bundle.getString("Type");

        email.setText(sEmail);
        name.setText(sName);


        if (type.equals("Chef")) items = new String[]{"Chef", "DeliveryDriver", "HeadChef"};
        else if (type.equals("HeadChef")) items = new String[]{"HeadChef", "DeliveryDriver", "Chef"};
        else if (type.equals("DeliveryDriver")) items = new String[]{"DeliveryDriver", "Chef", "HeadChef"};

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
        EditText name = findViewById(R.id.editTextName);
        sName = name.getText().toString();
        DocumentReference docRef = db.collection("Fridges/" + fridgeID + "/Users").document(sEmail);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (!document.exists()) Toast.makeText(editUserActivity.this, "User with this email does not exists!", Toast.LENGTH_SHORT).show();
                    else if (!sEmail.contains("@gmail.com")) Toast.makeText(editUserActivity.this, "Wrong email type!", Toast.LENGTH_SHORT).show();
                    else {
                        Map<String, Object> user = new HashMap<>();
                        user.put("Name", sName);
                        user.put("Type", type);
                        docRef.set(user);
                        Toast.makeText(editUserActivity.this, "Succesfully changed details!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
    public void onDeleteClicked(View view) {
        DocumentReference docRef = db.collection("Fridges/" + fridgeID + "/Users").document(sEmail);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (!document.exists()) Toast.makeText(editUserActivity.this, "User with this email does not exists!", Toast.LENGTH_SHORT).show();
                    else {
                        docRef.delete();
                        Toast.makeText(editUserActivity.this, "Removed a "+type+"!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }
        });
    }
}

