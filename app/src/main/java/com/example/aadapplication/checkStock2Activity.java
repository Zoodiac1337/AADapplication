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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;

public class checkStock2Activity extends AppCompatActivity {

    private String fridgeID;
    private String documentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_stock2);

        Bundle bundle = getIntent().getExtras();
        fridgeID = bundle.getString("fridgeID");
        documentId = bundle.getString("documentID");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Fridges/"+fridgeID+"/Items/"+documentId+"/Items").orderBy("ExpiryDate", Query.Direction.ASCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                String[] name = new String[task.getResult().size()];
                Date[] date1 = new Date[task.getResult().size()];
                Date[] date2 = new Date[task.getResult().size()];
                if (task.isSuccessful()) {
                    int i = 0;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        name[i]=document.getString("Insertedby");
                        date1[i]=document.getDate("InsertedOn");
                        date2[i]=document.getDate("ExpiryDate");
                        i++;
                    }

                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
                ListView lView;

                ListAdapterStock2 lAdapter;

                lView = (ListView) findViewById(R.id.androidList);

                lAdapter = new ListAdapterStock2(checkStock2Activity.this, name, date1, date2);

                lView.setAdapter(lAdapter);

                lView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                        Toast.makeText(checkStock2Activity.this, name[i]+" ", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}