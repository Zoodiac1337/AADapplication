package com.example.aadapplication;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class userAccessActivity extends AppCompatActivity {

    private String fridgeID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_stock);

        Bundle bundle = getIntent().getExtras();
        fridgeID = bundle.getString("fridgeID");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Fridges/"+fridgeID+"/Users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                String[] emails = new String[task.getResult().size()];
                String[] types = new String[task.getResult().size()];
                String[] names = new String[task.getResult().size()];
                if (task.isSuccessful()) {
                    int i = 0;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        emails[i] = document.getId();
                        types[i]=document.getString("Type");
                        names[i]=document.getString("Name");
                        i++;
                    }

                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
                ListView lView;

                ListAdapterAccess lAdapter;

                lView = (ListView) findViewById(R.id.androidList);

                lAdapter = new ListAdapterAccess(userAccessActivity.this, names, emails, types);

                lView.setAdapter(lAdapter);

                lView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Toast.makeText(userAccessActivity.this, names[i]+" "+emails[i], Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });






    }
}