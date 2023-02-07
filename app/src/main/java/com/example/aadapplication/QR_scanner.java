package com.example.aadapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import JsontoJava.BarcodeObject;

public class QR_scanner extends AppCompatActivity {

    private SurfaceView cameraView;
    private android.hardware.Camera camera;
    private BarcodeScanner barcodeScanner;


    private static final int REQUEST_CAMERA_PERMISSION = 201;

    private ByteBuffer byteBuffer;
    private String barcodeData = "";

    private boolean stopScanning = false;
    private BarcodeObject barinfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_bar_code);
        //toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
        //surfaceView = findViewById(R.id.surface_view);
        //initialiseDetectorsAndSources();

        cameraView = findViewById(R.id.surface_view);

        // Initialize the barcode scanner
        BarcodeScannerOptions options = new BarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_QR_CODE).build();
        barcodeScanner = BarcodeScanning.getClient(options);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Request camera permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 201);
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
                    if (ActivityCompat.checkSelfPermission(QR_scanner.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
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
                                InputImage image = InputImage.fromByteArray(data, size.width, size.height, 0, InputImage.IMAGE_FORMAT_NV21);

                                // Pass the InputImage to the barcode scanner
                                Task<List<Barcode>> result = barcodeScanner.process(image);


                                result.addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {

                                    @Override
                                    public void onSuccess(List<Barcode> barcodes) {

                                        if (barcodes.size() > 0) {

                                            //set stopscanning to true stops multiple
                                            // frames being called at once
                                            if (stopScanning) {
                                                return;
                                            } else {
                                                stopScanning = true;
                                            }

                                            //toneGen1.startTone(,2);

                                            //pause camera
                                            camera.stopPreview();

                                            System.out.println(barcodes.size());
                                            Barcode barcode = barcodes.get(0);
//                                            barcodeText.setText(barcode.getRawValue());

                                            barcodeData = barcode.getRawValue();
                                            Intent intent = new Intent();
                                            intent.putExtra("fridgeID", barcodeData);
                                            setResult(RESULT_OK, intent);
                                            finish();



                                            System.out.println("Barcode Scanner" + barcode.getFormat() + "" + barcode.getDisplayValue() + "Barcode value: ");

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


    private void resumePreview() {


    }


}




