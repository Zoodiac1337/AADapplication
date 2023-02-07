package com.example.aadapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import JsontoJava.BarcodeObject;




public class Remove_barcode_scanner extends AppCompatActivity {


    private SurfaceView cameraView;
    private android.hardware.Camera camera;
    private BarcodeScanner barcodeScanner;


    private static final int REQUEST_CAMERA_PERMISSION = 201;
    private ToneGenerator toneGen1;
    private TextView barcodeText;
    private EditText ProductNameField;

    private EditText QuanityField;

    private EditText ExpiryDateField;


    private String fridgeID;
    private String name;


    private String barcodeData;

    private ByteBuffer byteBuffer;


    private boolean stopScanning = false;
    private BarcodeObject barinfo;
    private int quant;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_barcode_scanner);

        Bundle bundle = getIntent().getExtras();
        fridgeID = bundle.getString("fridgeID");
        name = bundle.getString("name");

        //toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
        //surfaceView = findViewById(R.id.surface_view);
        barcodeText = findViewById(R.id.barcode_text_remove);

        ProductNameField = findViewById(R.id.ProductNameField_remove);
        //initialiseDetectorsAndSources();

        cameraView = findViewById(R.id.surface_view_remove);

        // Initialize the barcode scanner
        BarcodeScannerOptions options =
                new BarcodeScannerOptions.Builder()
                        .setBarcodeFormats(Barcode.FORMAT_EAN_13)
                        .build();
        barcodeScanner = BarcodeScanning.getClient(options);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Request camera permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        } else {
            // Camera permission already granted, start camera preview
            startCameraPreview();
        }

    }

    private void startCameraPreview() {
        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(Remove_barcode_scanner.this,
                            Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        // Start camera preview
                        cameraView.getHolder().setKeepScreenOn(true);
                        camera = android.hardware.Camera.open();
                        android.hardware.Camera.Parameters parameters = camera.getParameters();
                        camera.setDisplayOrientation(90);

                        parameters.setFocusMode(android.hardware.Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                        try{
                            camera.setParameters(parameters);} catch (Exception ignored) {}

                        camera.setPreviewDisplay(holder);
                        // Get the preview frame size
                        android.hardware.Camera.Size size = parameters.getPreviewSize();

                        // Create a ByteBuffer for the preview frame data
                        int length = size.width * size.height * ImageFormat.getBitsPerPixel(parameters.getPreviewFormat()) / 8;
                        byteBuffer = ByteBuffer.allocate(length);
                        // Set the ByteBuffer as the data for the camera preview callback
                        camera.addCallbackBuffer(byteBuffer.array());


                        camera.setPreviewCallbackWithBuffer(new android.hardware.Camera.PreviewCallback() {

                            @Override
                            public void onPreviewFrame(byte[] data, android.hardware.Camera camera) {

                                // Check if scanning should stop
                                if (stopScanning) {
                                    return;
                                }

                                // Convert the preview frame data into an InputImage
                                android.hardware.Camera.Size size = camera.getParameters().getPreviewSize();
                                InputImage image = InputImage.fromByteArray(data, size.width, size.height, 0,InputImage.IMAGE_FORMAT_NV21);

                                // Pass the InputImage to the barcode scanner
                                Task<List<Barcode>> result = barcodeScanner.process(image);





                                result.addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {

                                    @Override
                                    public void onSuccess(List<Barcode> barcodes) {

                                        if(barcodes.size()>0) {

                                            //set stopscanning to true stops multiple
                                            // frames being called at once
                                            if(stopScanning){return;}
                                            else{
                                                stopScanning=true;
                                            }

                                            //toneGen1.startTone(,2);

                                            //pause camera
                                            camera.stopPreview();






                                            System.out.println(barcodes.size());
                                            Barcode barcode = barcodes.get(0);
                                            barcodeText.setText(barcode.getRawValue());


                                            //call api https://openfoodfacts.github.io/api-documentation/
                                            //if product found use this information
                                            //if product not found prompt to add to database

                                            dbgetter(barcodeText.getText().toString());



                                            System.out.println("Barcode Scanner"+barcode.getFormat()+""
                                                    + barcode.getDisplayValue()+"Barcode value: ");

                                        }



                                    }

                                });

                                result.addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        Log.e("Barcode Scanner", "Error scanning barcode: " + e.getMessage());
                                    }


                                });
                                // Add the ByteBuffer back to the camera for reuse
                                camera.addCallbackBuffer(byteBuffer.array());
                            }
                        });

                        camera.startPreview();
                    }
                } catch (IOException e) {
                    Log.e("Barcode Scanner", "Error starting camera preview: " + e.getMessage());
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCameraPreview();
            } else {
                Log.e("Barcode Scanner", "Camera permission denied");
            }
        }
    }

    private void dbgetter(String field){
        System.out.println("here");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        System.out.println(field);

        DocumentReference docRef = db.document("/Fridges/"+fridgeID+"/Items/" + field);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        //set barcode text
                        System.out.println("here");
                        ProductNameField.setText(document.get("Name").toString());
                    } else {
                       //no current barcode found
                        System.out.println("non found");

                    }
                } else {
                    //error
                    System.out.println("error");

                }
            }
        });





    }

    public void resumePreview(View view){
        stopScanning=false;
        camera.startPreview();

    }


    public void addToListViewRemove() {
        //add values to list view of recently added
        ListView items;
        items = findViewById(R.id.AddedValues);
        ArrayList<Object> entries = new ArrayList<>();
        ArrayAdapter<Object> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, entries);
        items.setAdapter(adapter);

        Toast.makeText(this, "Removed", Toast.LENGTH_SHORT).show();

        // Add items to the list
        entries.add("Removed: " + ProductNameField.getText().toString() + "\nExpires: " + ExpiryDateField.getText().toString());
        adapter.notifyDataSetChanged();
    }
    public void clearListViewRemove() {
        //add values to list view of recently added
        ListView items;
        items = findViewById(R.id.AddedValues);
        ArrayList<Object> entries = new ArrayList<>();
        ArrayAdapter<Object> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, entries);
        items.setAdapter(adapter);

        Toast.makeText(this, "No item found", Toast.LENGTH_SHORT).show();

        // Clear items from the list
        entries.clear();
        adapter.notifyDataSetChanged();
    }
    public void removeItems(View view) {
        //check all fields are valid
        barcodeText = findViewById(R.id.barcode_text_remove);
        ProductNameField = findViewById(R.id.ProductNameField_remove);
        ExpiryDateField = findViewById(R.id.ExpiryDateField_remove);


        //barcode check
        if (!(barcodeText.getText().length() > 1) || barcodeText.getText().toString().contains(" ")) {
            Toast.makeText(this, "no barcode scanned", Toast.LENGTH_SHORT).show();
            return;
        }
        //ProductNameField
        if (!(ProductNameField.getText().length() > 1)) {
            Toast.makeText(this, "no productname", Toast.LENGTH_SHORT).show();
            return;
        }
        //check quantity

        //check date

        String date = ExpiryDateField.getText().toString();
        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
        Date dateObject = null;
        dateFormat.setLenient(false);
        try {
            dateObject = dateFormat.parse(date);

        } catch (ParseException e) {
            Toast.makeText(this, "invalid date format", Toast.LENGTH_SHORT).show();
            return;
        }


        //initialize firebase
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String field = barcodeText.getText().toString();
        CollectionReference collection = db.collection("/Fridges/"+fridgeID+"/Items/" + field + "/Items");
        String d =ExpiryDateField.getText().toString();
        SimpleDateFormat df = new SimpleDateFormat("ddMMyyyy");
        Date dO = null;
        df.setLenient(false);
        try {
            dateObject=dateFormat.parse(date);

        }  catch (ParseException e) {
            Toast.makeText(this,"invalid date format",Toast.LENGTH_SHORT).show();
            return;
        }



        Query query = collection.whereEqualTo("ExpiryDate", dateObject);


        //query.get();
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
                if (!documentSnapshots.isEmpty()) {
                    DocumentSnapshot documentSnapshot = documentSnapshots.get(0);
                    documentSnapshot.getReference().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            DocumentReference newref=db.document("/Fridges/"+fridgeID+"/Items/" + field);
                            Map<String, Object> qdata = new HashMap<>();
                            qdata.put("Quantity", FieldValue.increment(-1) );
                            newref.update(qdata);

                            addToListViewRemove();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            return;
                        }
                    });
                }
                else clearListViewRemove();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                return;
            }
        });


    }
}





