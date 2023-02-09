package com.example.aadapplication;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

import java.util.ArrayList;

public class contactHeadChefActivity extends AppCompatActivity {

    private ListView listView;
    private String fridgeID;
    private String name;
    private ArrayList<String> arrayList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_head_chef);

        Bundle bundle = getIntent().getExtras();
        fridgeID = bundle.getString("fridgeID");
        name = bundle.getString("name");

        listView = findViewById(R.id.headChefsListView);

        arrayList = new ArrayList<>();
        adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(adapter);


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query =db.collection("Fridges/"+fridgeID+"/Users").whereEqualTo("Type","HeadChef");
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                String[] email = new String[task.getResult().size()];
                if (task.isSuccessful()) {
                    int i = 0;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        arrayList.add(document.getString("Name"));
                        adapter.notifyDataSetChanged();
                        email[i] = document.getId();
                        i++;
                    }
                } else Log.d(TAG, "Error getting documents: ", task.getException());
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Toast.makeText(contactHeadChefActivity.this, " "+email[i], Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        Uri data = Uri.parse("mailto:"+email[i]+"?subject=" + Uri.encode("Message from delivery driver: "+name));
                        intent.setData(data);
                        startActivity(intent);
                    }
                });
            }
        });


    }
}