package com.example.aadapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class userAccessActivity extends AppCompatActivity {

    String[] names = {"Pawel Wydra", "James Williams", "Will Smith"};
    String[] emails = {"pawelx14@gmail.com", "pawelx140@gmail.com", "pawelx1404@gmail.com"};
    String[] types = {"Head Chef", "Chef", "Delivery Driver"};
    ListView lView;

    ListAdapterAccess lAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_stock);

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
}