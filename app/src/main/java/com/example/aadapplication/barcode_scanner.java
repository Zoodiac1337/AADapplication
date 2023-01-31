package com.example.aadapplication;

import android.Manifest;

import android.content.pm.PackageManager;

import android.graphics.ImageFormat;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.util.Log;

import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;


import java.io.BufferedReader;
import java.io.IOException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.List;

import JsontoJava.BarcodeObject;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class barcode_scanner extends AppCompatActivity {

    private SurfaceView cameraView;
    private android.hardware.Camera camera;
    private BarcodeScanner barcodeScanner;


    private static final int REQUEST_CAMERA_PERMISSION = 201;
    private ToneGenerator toneGen1;
    private TextView barcodeText;
    private String barcodeData;

    private ByteBuffer byteBuffer;


    private boolean stopScanning = false;
    private BarcodeObject barinfo;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_insert);
        //toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
        //surfaceView = findViewById(R.id.surface_view);
        barcodeText = findViewById(R.id.barcode_text);
        //initialiseDetectorsAndSources();

        cameraView = findViewById(R.id.surface_view);

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
                            camera.setParameters(parameters);
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
                                                                            try{
                                                                            apigetter(barcode.getRawValue());} catch (
                                                                                    Exception e) {
                                                                                throw new RuntimeException(e);
                                                                            }


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

    private void apigetter(String code){


        //call api https://openfoodfacts.github.io/api-documentation/
        //if product found use this information
        //if product not found prompt to add to database
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url("https://world.openfoodfacts.org/api/v2/product/"+code+"?fields=product_name")
                .method("GET", body)
                .build();
        try {
            Response response = client.newCall(request).execute();


            //convert json into java object and extract productname data
            // Read the JSON from file

            System.out.println(response.body());



        } catch (
                IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void resumePreview(){


    }


}




