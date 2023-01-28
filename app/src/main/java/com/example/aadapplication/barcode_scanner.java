package com.example.aadapplication;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraDevice;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;



import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import androidx.camera.lifecycle.ProcessCameraProvider;


import java.io.IOException;
import java.util.List;

public class barcode_scanner extends AppCompatActivity {

    private SurfaceView cameraView;
    private BarcodeScanner barcodeScanner;

    private static final int REQUEST_CAMERA_PERMISSION = 201;
    private ToneGenerator toneGen1;
    private TextView barcodeText;
    private String barcodeData;



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
                        .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
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
                            android.hardware.Camera camera =
                                    android.hardware.Camera.open();
                            camera.setPreviewDisplay(holder);
                            camera.setPreviewCallback(new android.hardware.Camera.PreviewCallback() {
                                @Override
                                public void onPreviewFrame(byte[] data, android.hardware.Camera camera) {
                                    // Convert the preview frame data into an InputImage
                                    android.hardware.Camera.Size size = camera.getParameters().getPreviewSize();
                                    InputImage image = InputImage.fromByteArray(data, size.width, size.height, 0,InputImage.IMAGE_FORMAT_NV21);

                                    // Pass the InputImage to the barcode scanner
                                    Task<List<Barcode>> result = barcodeScanner.process(image);



                                    result.addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
                                                                    @Override
                                                                    public void onSuccess(List<Barcode> barcodes) {
                                                                        System.out.println(barcodes.size());
                                                                        for (int i = 0; i < barcodes.size(); i++) {

                                                                            Barcode barcode = barcodes.get(0);
                                                                            barcodeText.setText(barcode.getRawValue());
                                                                            System.out.println("Barcode Scanner "+ "Barcode value: " + barcode.getRawValue());
                                                                        }
                                                                    }
                                                                }
                                    );
                                    result.addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                            Log.e("Barcode Scanner", "Error scanning barcode: " + e.getMessage());
                                        }
                                    });
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
}



