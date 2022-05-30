package com.example.beaconexample;

import android.content.Context;

import java.util.Random;

public class GameManager implements BeaconScanner.ScannerCallback, BeaconSender.SenderCallback {

    Context mContext;
    Users me;
    private BeaconSender mBeaconSender;
    private BeaconScanner mBeaconScanner;


    public void init(Context context) {
        Random mRandom = new Random();

        mContext = context;
        me.id = mRandom.nextInt(998) + 1;
        me.state = 0;

        mBeaconScanner = new BeaconScanner();
        mBeaconScanner.init(mContext);
        mBeaconScanner.setCallback(this);
        mBeaconScanner.startOfficial();

        mBeaconSender = new BeaconSender();
        mBeaconSender.init(mContext);
        mBeaconSender.setCallback(this);
        mBeaconSender.start(me.id, me.state, 0);
    }


    /*
    Implements
     */
    @Override
    public void onBeaconScanResult(String text) {
    }

    @Override
    public void onBeaconSendResult(String text) {
    }
}
