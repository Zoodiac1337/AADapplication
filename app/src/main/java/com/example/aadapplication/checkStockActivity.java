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

import java.util.Date;

public class checkStockActivity extends AppCompatActivity {

    int[] quantity = {1, 5, 4, 23};
    String[] name = {"Milk", "Eggs", "Beef meat patties", "Chicken strips"};
    String[] c = {"14/04/2023", "02/02/2023", "17/03/2023", "31/01/2023"};
    String[] date2 = {"18/04/2023", "08/02/2023", "28/03/2023", "07/02/2023"};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_stock);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Fridges/12345/Items").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                int[] quantity = new int[task.getResult().size()];
                String[] name = new String[task.getResult().size()];
                Date[] date1 = new Date[task.getResult().size()];
                Date[] date2 = new Date[task.getResult().size()];
                if (task.isSuccessful()) {
                    int i = 0;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        name[i]=document.getString("Name");
                        date1[i]=document.getDate("First to expire");
                        date2[i]=document.getDate("Last to expire");
                        quantity[i]=document.getDouble("Quantity").intValue();
                        i++;
                    }

                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
                ListView lView;

                ListAdapterStock lAdapter;

                lView = (ListView) findViewById(R.id.androidList);

                lAdapter = new ListAdapterStock(checkStockActivity.this, name, quantity, date1, date2);

                lView.setAdapter(lAdapter);

                lView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                        Toast.makeText(checkStockActivity.this, name[i]+" "+quantity[i], Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });
    }
}