package com.example.aadapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class checkStockActivity extends AppCompatActivity {

    int[] quantity = {1, 5, 4, 23};

    String[] name = {"Milk", "Eggs", "Beef meat patties", "Chicken strips"};

    String[] date1 = {"14/04/2023", "02/02/2023", "17/03/2023", "31/01/2023"};
    String[] date2 = {"18/04/2023", "08/02/2023", "28/03/2023", "07/02/2023"};

    ListView lView;

    ListAdapterStock lAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_stock);

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
}