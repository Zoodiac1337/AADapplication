package com.example.aadapplication;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class headChefActivity extends AppCompatActivity {

    private String email;
    private String fridgeID;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_head_chef);
        Bundle bundle = getIntent().getExtras();
        email = bundle.getString("email");
        name = bundle.getString("name");
        fridgeID = bundle.getString("fridgeID");
    }

    public void insertItemButton(View view) {
        Intent myIntent = new Intent(headChefActivity.this, barcode_scanner.class);
        myIntent.putExtra("name", name);
        myIntent.putExtra("fridgeID", fridgeID);
        startActivity(myIntent);

    }

    public void checkStockButton(View view) {
        Intent myIntent = new Intent(headChefActivity.this, checkStockActivity.class);
        myIntent.putExtra("fridgeID", fridgeID);
        startActivity(myIntent);
    }

    public void userAccessButton(View view) {
        Intent myIntent = new Intent(headChefActivity.this, userAccessActivity.class);
        myIntent.putExtra("fridgeID", fridgeID);
        startActivity(myIntent);
    }

    public void removeItemButton(View view) {
        Intent myIntent = new Intent(headChefActivity.this, Remove_barcode_scanner.class);
        myIntent.putExtra("fridgeID", fridgeID);
        myIntent.putExtra("name", name);
        startActivity(myIntent);
    }

    public void DownloadHealthReport(View view) {


        //function that downloads health report document
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReferenceFromUrl("gs://advanced-analysis-and-design.appspot.com");

// Next, you need to create a reference to the file you want to download:
        StorageReference fileReference = storageReference.child("Reports/" + fridgeID);

// You will also need to create a local file to store the downloaded data:
        File localFile = null;
        File downloadsDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        localFile = new File(downloadsDirectory, fridgeID + " Report.txt");

        File finalLocalFile = localFile;

        fileReference.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {
                // The document exists, so we can proceed with the download:
                fileReference.getFile(finalLocalFile)
                        .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                                Uri uri = FileProvider.getUriForFile(headChefActivity.this, "com.example.app.provider", finalLocalFile);

                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setDataAndType(uri, "text/plain");
                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                                PendingIntent pendingIntent = PendingIntent.getActivity(headChefActivity.this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

                                //PendingIntent pendingIntent = PendingIntent.getActivity(headChefActivity.this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

                                NotificationCompat.Builder builder = new NotificationCompat.Builder(headChefActivity.this, "download_notifications")
                                        .setSmallIcon(R.drawable.ic_launcher_background)
                                        .setContentTitle("Download complete")
                                        .setContentText("File saved in downloads folder")
                                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                        .setContentIntent(pendingIntent)
                                        .setAutoCancel(true);






                                Toast.makeText(headChefActivity.this, fridgeID +" in Downloads folder", Toast.LENGTH_SHORT).show();
                                return;







                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(headChefActivity.this, "error occurred", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // The document does not exist:
                Toast.makeText(headChefActivity.this, "no report exists ", Toast.LENGTH_SHORT).show();
                return;
            }
        });


    }
}