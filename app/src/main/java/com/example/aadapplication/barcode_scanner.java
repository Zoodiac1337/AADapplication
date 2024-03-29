package com.example.aadapplication;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
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
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import JsontoJava.BarcodeObject;
import Rest.GetProduct;


public class barcode_scanner extends AppCompatActivity {

    private String fridgeID;
    private String name;

    private SurfaceView cameraView;
    private android.hardware.Camera camera;
    private BarcodeScanner barcodeScanner;


    private static final int REQUEST_CAMERA_PERMISSION = 201;
    private ToneGenerator toneGen1;
    private TextView barcodeText;
    private EditText ProductNameField;

    private EditText QuanityField;

    private TextView ExpiryDateField;
    private DatePickerDialog datePickerDialog;


    private String barcodeData;

    private ByteBuffer byteBuffer;


    private boolean stopScanning = false;
    private BarcodeObject barinfo;
    private int quant;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_insert);

        Bundle bundle = getIntent().getExtras();
        name = bundle.getString("name");
        fridgeID = bundle.getString("fridgeID");

        //toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
        //surfaceView = findViewById(R.id.surface_view);
        barcodeText = findViewById(R.id.barcode_text);
        ProductNameField = findViewById(R.id.ProductNameField);
        //initialiseDetectorsAndSources();

        cameraView = findViewById(R.id.surface_view);

        ExpiryDateField = findViewById(R.id.ExpiryDateField);
        ExpiryDateField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePickerDialog = new DatePickerDialog(barcode_scanner.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                ExpiryDateField.setText(dayOfMonth +"/"+
                                        (monthOfYear + 1) + "/"+ year);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

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
                        if (ActivityCompat.checkSelfPermission(barcode_scanner.this,
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
                                                                            cameraView.setVisibility(View.GONE);

                                                                            //call api https://openfoodfacts.github.io/api-documentation/
                                                                            //if product found use this information
                                                                            //if product not found prompt to add to database

                                                                                apigetter(barcodeText.getText().toString());



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
                Intent myIntent = new Intent(barcode_scanner.this, barcode_scanner.class);
                startActivity(myIntent);
                finish();
            } else {
                Log.e("Barcode Scanner", "Camera permission denied");
            }
        }
    }

    private void apigetter(String code){


        //call api https://openfoodfacts.github.io/api-documentation/
        //if product found use this information
        //if product not found prompt to add to database
        GetProduct prod= new GetProduct(this,ProductNameField);
        prod.execute("https://world.openfoodfacts.org/api/v2/product/"+code+"?fields=product_name");
        System.out.println("result->"+prod.results);
    }

    public void resumePreview(View view){
        cameraView.setVisibility(View.VISIBLE);
        stopScanning=false;
        camera.startPreview();
    }



    public void addItems(View view) {
        //check all fields are valid
        barcodeText = findViewById(R.id.barcode_text);
        ProductNameField = findViewById(R.id.ProductNameField);
        ExpiryDateField = findViewById(R.id.ExpiryDateField);
        QuanityField = findViewById(R.id.QuanityField);

        //barcode check
        if (!(barcodeText.getText().length() >1) ||barcodeText.getText().toString().contains(" ")){
            Toast.makeText(this, "no barcode scanned", Toast.LENGTH_SHORT).show();
            return;
        }
        //ProductNameField
        if(!(ProductNameField.getText().length()>1)){
            Toast.makeText(this, "no productname", Toast.LENGTH_SHORT).show();
            return;
        }
        //check quantity
        int quantity=0;
        try {
            quantity = Integer.parseInt(QuanityField.getText().toString());
            if (quantity<1){
                Toast.makeText(this, "quantity to low", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        catch (Exception e) {
            Toast.makeText(this, "invalid quantity", Toast.LENGTH_SHORT).show();
            return;
        }
        //check date

        String date =ExpiryDateField.getText().toString();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date dateObject = null;
        dateFormat.setLenient(false);
        try {
            dateObject=dateFormat.parse(date);

        }  catch (ParseException e) {
            Toast.makeText(this,"invalid date format",Toast.LENGTH_SHORT).show();
            return;
        }




        //initialize firebase
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String field=barcodeText.getText().toString();
        CollectionReference collection = db.collection("/Fridges/"+fridgeID+"/Items/"+field+"/Items");
        DocumentReference documentReference= db.document("/Fridges/"+fridgeID+"/Items/"+field);




        for(int i = 0; i != quantity; i++){

            Map<String, Object> data = new HashMap<>();
            data.put("ExpiryDate", dateObject);
            data.put("InsertedOn", Timestamp.now());
            data.put("Insertedby",name);

            collection.add(data);
        }

        Map<String, Object> barcodedata = new HashMap<>();
        barcodedata.put("Name",ProductNameField.getText().toString() );
        barcodedata.put("ModifiedOn", Timestamp.now());
        barcodedata.put("Quantity", FieldValue.increment(quantity));

        Date finalDateObject = dateObject;
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        if (finalDateObject.after(document.getDate("Last to expire")))
                            barcodedata.put("Last to expire", finalDateObject);
                        else if (finalDateObject.before(document.getDate("First to expire")))
                            barcodedata.put("First to expire", finalDateObject);
                        documentReference.update(barcodedata);
                    } else {
                        barcodedata.put("First to expire", finalDateObject);
                        barcodedata.put("Last to expire", finalDateObject);
                        documentReference.set(barcodedata);
                    }
                }
            }
        });



        //make list persist and write to text document


        //add values to list view of recently added
        ListView items;
        items=findViewById(R.id.AddedValues);
        ArrayList<Object> entries = new ArrayList<>();
        ArrayAdapter<Object> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, entries);
        items.setAdapter(adapter);

        // Add items to the list
        entries.add(quantity+"X "+ProductNameField.getText().toString()+" expires:"+ExpiryDateField.getText().toString());
        adapter.notifyDataSetChanged();

    }
}




