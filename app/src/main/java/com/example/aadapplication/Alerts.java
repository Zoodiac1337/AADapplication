package com.example.aadapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Map;

public class Alerts extends AppCompatActivity {

    private ListView listView;
    private ArrayList<String> notificationsList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alerts);


        listView = findViewById(R.id.AlertList);
        notificationsList = new ArrayList<>();

        // Load notifications from SharedPreferences
        loadNotifications();

        // Set the adapter for the ListView
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, notificationsList);
        listView.setAdapter(adapter);


        adapter.notifyDataSetChanged();





    }
    private void loadNotifications() {
        SharedPreferences sharedPreferences = getSharedPreferences("Notifications", Context.MODE_PRIVATE);
        Map<String, ?> allEntries = sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            notificationsList.add((String) entry.getValue());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the list when the activity is resumed
        loadNotifications();
        adapter.notifyDataSetChanged();
    }
}