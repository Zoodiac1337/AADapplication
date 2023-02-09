package com.example.aadapplication;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.Calendar;

public class headChefActivity extends AppCompatActivity {

    private String email;
    private String fridgeID;
    private String name;

    private TextView fridgeTextID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_head_chef);
        Bundle bundle = getIntent().getExtras();
        email = bundle.getString("email");
        name = bundle.getString("name");
        fridgeID = bundle.getString("fridgeID");
        System.out.println("here");

        fridgeTextID = findViewById(R.id.fridgeIDtext);
        fridgeTextID.setText("Fridge #"+fridgeID);

        //check if notification has already been created

        SharedPreferences preferences = this.getSharedPreferences("MidDayTask", Context.MODE_PRIVATE);
        String currentFridgeValue = preferences.getString(fridgeID, null);
        if (currentFridgeValue == null) {
            System.out.println("herexxxxxxxx");
            // The trigger with the same fridge value already exists
            createNotificationChannel();
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(this, MidDayTaskReceiver.class);
            intent.putExtra("fridgeID", fridgeID);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
            System.out.println("set schedule");
            //check that task has been enabled

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 12);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            alarmManager.setInexactRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
            //Calendar calendar = Calendar.getInstance();
            //calendar.setTimeInMillis(System.currentTimeMillis());
            //alarmManager.setInexactRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), 30 * 1000, pendingIntent);



            SharedPreferences sharedPreferences = getSharedPreferences("MidDayTask", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(fridgeID, fridgeID);
            editor.apply();
        }






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
        if (localFile.exists()){
            Toast.makeText(this, "file already exists please check downloads folder", Toast.LENGTH_SHORT).show();
            return;
        }

        File finalLocalFile = localFile;



        fileReference.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {
                // The document exists, so we can proceed with the download:
                fileReference.getFile(finalLocalFile)
                        .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    CharSequence name = "download_notifications";
                                    String description = "download_notifications";
                                    int importance = NotificationManager.IMPORTANCE_DEFAULT;
                                    NotificationChannel channel = new NotificationChannel("download_notifications", name, importance);
                                    channel.setDescription(description);
                                    // Register the channel with the system; you can't change the importance
                                    // or other notification behaviors after this
                                    NotificationManager notificationManager = getSystemService(NotificationManager.class);
                                    notificationManager.createNotificationChannel(channel);
                                }

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
                                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                                        .setContentIntent(pendingIntent);

                                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(headChefActivity.this);
                                notificationManager.notify(4, builder.build());



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

    private boolean isScheduleCreated() {
        SharedPreferences sharedPreferences = getSharedPreferences("MidDayTask", MODE_PRIVATE);
        return sharedPreferences.getBoolean("schedule_created", false);
    }
    private boolean isMidDayTaskEnabled() {
        SharedPreferences sharedPreferences = getSharedPreferences("MidDayTask", MODE_PRIVATE);
        return sharedPreferences.getBoolean("enabled", true);
    }


    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "fridgeValueChannel";
            String description = "description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("fridgeValueChannel", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void optionMenu(View view) {
        Intent myIntent = new Intent(headChefActivity.this, userOptions.class);
        myIntent.putExtra("email", email);
        startActivity(myIntent);
    }

    public void ReorderDocument(View view) {


        //function that downloads health report document
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReferenceFromUrl("gs://advanced-analysis-and-design.appspot.com");

// Next, you need to create a reference to the file you want to download:
        StorageReference fileReference = storageReference.child("OrderDocument/" + fridgeID);


// You will also need to create a local file to store the downloaded data:
        File localFile = null;
        File downloadsDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        localFile = new File(downloadsDirectory, fridgeID + " Order Document"+ Timestamp.now().toDate().toString()+".txt");
        if (localFile.exists()){
            Toast.makeText(this, "file already exists please check downloads folder", Toast.LENGTH_SHORT).show();
            return;
        }

        File finalLocalFile = localFile;



        fileReference.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {
                // The document exists, so we can proceed with the download:
                fileReference.getFile(finalLocalFile)
                        .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    CharSequence name = "download_notifications";
                                    String description = "download_notifications";
                                    int importance = NotificationManager.IMPORTANCE_DEFAULT;
                                    NotificationChannel channel = new NotificationChannel("download_notifications", name, importance);
                                    channel.setDescription(description);
                                    // Register the channel with the system; you can't change the importance
                                    // or other notification behaviors after this
                                    NotificationManager notificationManager = getSystemService(NotificationManager.class);
                                    notificationManager.createNotificationChannel(channel);
                                }

                                Uri uri = FileProvider.getUriForFile(headChefActivity.this, "com.example.app.provider", finalLocalFile);

                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setDataAndType(uri, "text/plain");
                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                                PendingIntent pendingIntent = PendingIntent.getActivity(headChefActivity.this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

                                //PendingIntent pendingIntent = PendingIntent.getActivity(headChefActivity.this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

                                NotificationCompat.Builder builder = new NotificationCompat.Builder(headChefActivity.this, "download_notifications")
                                        .setSmallIcon(R.drawable.ic_launcher_background)
                                        .setContentTitle("Download complete")
                                        .setContentText("Re order Document saved in downloads folder")
                                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

                                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(headChefActivity.this);
                                notificationManager.notify(5, builder.build());

                                Toast.makeText(headChefActivity.this, fridgeID +" in Downloads folder", Toast.LENGTH_SHORT).show();
                                return;

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(headChefActivity.this, "error occurred or document may not be available currently", Toast.LENGTH_SHORT).show();
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