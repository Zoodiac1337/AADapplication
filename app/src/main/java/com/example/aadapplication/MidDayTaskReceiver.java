package com.example.aadapplication;

//import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

        import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
        import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

//import androidx.core.app.NotificationCompat;
//import androidx.core.app.NotificationManagerCompat;

public class MidDayTaskReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        //create notification channel
        String fridgeID= intent.getStringExtra("fridgeID");
        //read fridge data and output into a notification
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference =db.document("Fridges/"+fridgeID);
        CollectionReference fridgecollection = documentReference.collection("/Items");

        //get current time
        Timestamp TodaysTime= Timestamp.now();
        //Query query=fridgecollection.whereLessThan("ExpiryDate",TodaysTime);




// Define the formatter for expiry date field


// Create arrays to store document id of the values in 1day, 2day, 3day
        HashMap<String,Integer>oneDay=new HashMap<>();
        HashMap<String,Integer>twoDay=new HashMap<>();
        HashMap<String,Integer>threeDay=new HashMap<>();


        db.collection("Fridges").document(fridgeID).collection("Items")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Timestamp currentTime = Timestamp.now();
                            for (QueryDocumentSnapshot itemDoc : task.getResult()) {
                                String itemId = itemDoc.getId();
                                db.collection("Fridges").document(fridgeID).collection("Items")
                                        .document(itemId).collection("Items")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    //for each expiry date convert into seconds then subtract from current time to get difference
                                                    for (QueryDocumentSnapshot foodDoc : task.getResult()) {
                                                        Timestamp expiryDate = foodDoc.getTimestamp("ExpiryDate");
                                                        long difference = expiryDate.toDate().getTime() - currentTime.toDate().getTime();
                                                        //System.out.println("current --> "+foodDoc.getId().toString()+"expire"+expiryDate.toString());
                                                        if (difference >= 0 && difference <= 86400000) {
                                                            System.out.println("found"+foodDoc.getId().toString());
                                                            //use merge to add dupllicate items
                                                            oneDay.merge(itemId,1,Integer::sum);

                                                        } else if (difference > 86400000 && difference <= 172800000) {
                                                            twoDay.merge(itemId,1,Integer::sum);
                                                        } else if (difference > 172800000 && difference <= 259200000) {
                                                            threeDay.merge(itemId,1,Integer::sum);
                                                        }
                                                    }
                                                }
                                               //add code here that is called only when the firebase function has completed
                                                Integer count;
                                                if(! oneDay.isEmpty()){

                                                    String Text = stringBuilder(oneDay);


                                                    count=oneDay.values().stream().mapToInt(Integer::intValue).sum();
                                                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "fridgeValueChannel")
                                                            .setSmallIcon(R.mipmap.ic_fridge)
                                                            .setContentTitle("Expiring in one day")
                                                            .setContentText("Fridge:"+fridgeID +"contains "+count+" Items \n")
                                                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                                            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                                                            .setStyle(new NotificationCompat.BigTextStyle()
                                                                    .bigText(Text));

                                                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                                                    notificationManager.notify(0, builder.build());
                                                }
                                                if(! twoDay.isEmpty()){
                                                    String Text = stringBuilder(twoDay);

                                                    count=oneDay.values().stream().mapToInt(Integer::intValue).sum();
                                                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "fridgeValueChannel")
                                                            .setSmallIcon(R.mipmap.ic_fridge)
                                                            .setContentTitle("Expiring in two days")
                                                            .setContentText("Fridge: "+fridgeID +" contains "+count+" Items \n")
                                                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                                            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                                                            .setStyle(new NotificationCompat.BigTextStyle()
                                                                    .bigText(Text));

                                                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                                                    notificationManager.notify(1, builder.build());

                                                }
                                                if(! threeDay.isEmpty()){
                                                    String Text = stringBuilder(threeDay);

                                                    count=oneDay.values().stream().mapToInt(Integer::intValue).sum();
                                                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "fridgeValueChannel")
                                                            .setSmallIcon(R.mipmap.ic_fridge)
                                                            .setContentTitle("Expiring in three days")
                                                            .setContentText("Fridge: "+fridgeID +" contains "+count+" Items \n")
                                                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                                            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                                                            .setStyle(new NotificationCompat.BigTextStyle()
                                                                    .bigText(Text));

                                                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                                                    notificationManager.notify(2, builder.build());

                                                }

                                            }
                                        });
                            }
                        }
                    }
                });






    }
    private String stringBuilder(HashMap<String,Integer> map) {
        StringBuilder sb = new StringBuilder();
        for (String key : map.keySet()) {
            sb.append(key + " contains " + map.get(key)+" items" + System.lineSeparator());
        }
        String Text = sb.toString();
        return Text;

    }


}