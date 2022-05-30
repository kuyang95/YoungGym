package com.example.beaconexample;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements BeaconScanner.ScannerCallback ,BeaconSender.SenderCallback {




    private TextView tv;
    private TextView tv2;
    private Button btn;
    private BeaconSender mBeaconSender;
    private BeaconScanner mBeaconScanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBeaconScanner = new BeaconScanner();
        mBeaconScanner.init(this);
        mBeaconScanner.setCallback(this);
        mBeaconScanner.startOfficial();

        mBeaconSender = new BeaconSender();
        mBeaconSender.init(this);
        mBeaconSender.setCallback(this);
        //mBeaconSender.start();

        tv = findViewById(R.id.tv);
        tv2 = findViewById(R.id.tv2);
        btn = findViewById(R.id.btn);



        // 위치권한
        ActivityResultLauncher<String[]> locationPermissionRequest =
                registerForActivityResult(new ActivityResultContracts
                                .RequestMultiplePermissions(), result -> {
                            Boolean fineLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_FINE_LOCATION, false);
                            Boolean coarseLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_COARSE_LOCATION,false);
                            if (fineLocationGranted != null && fineLocationGranted) {
                                // Precise location access granted.
                            } else if (coarseLocationGranted != null && coarseLocationGranted) {
                                // Only approximate location access granted.
                            } else {
                                // No location access granted.
                            }
                        }
                );

// ...

// Before you perform the actual permission request, check whether your app
// already has the permissions, and whether your app needs to show a permission
// rationale dialog. For more details, see Request permissions.
        locationPermissionRequest.launch(new String[] {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });


    }

    @Override
    public void onBeaconScanResult(String text) {
        tv.setText(text);
    }

    @Override
    public void onBeaconSendResult(String text) {
        //tv2.setText(text);
    }
    }


