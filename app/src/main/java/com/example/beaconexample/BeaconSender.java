package com.example.beaconexample;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseSettings;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.BeaconTransmitter;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

public class BeaconSender {

    Context context;
    Beacon beacon;
    BeaconParser beaconParser;
    BeaconTransmitter beaconTransmitter;
    SenderCallback mSenderCallback;
    int sendCount = 0;


        public void init(Context c) {
            Log.d("---", "---");
            Log.d("//===========//", "================================================");
            Log.d("", "\n" + "[BeaconSender > init() 메소드 : 초기화 실시]");
            Log.d("//===========//", "================================================");
            Log.d("---", "---");

            context = c;
        }

        public void setCallback(SenderCallback callback){
            mSenderCallback = callback;
        }

        public void start(int id, int major, int minor) {

            if(getBleStateCheck() == true){ // [블루투스 및 GPS 기능이 모두 활성 상태]



                String uuid = "ffffffff-1111-2222-3333-123456789" + Integer.toString(id); // [8 / 4 / 4 / 12]
                String majorCode = Integer.toString(major);
                String minorCode = Integer.toString(minor);
                BeaconSendStart(uuid, majorCode, minorCode); // 비콘 신호 활성 시작

                /*
                //TODO [10초뒤 실행 (작업 예약)]
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        BeaconSendStop(); //[비콘 신호 활성 종료 실시]
                    }
                }, 10000);


                 */
            }

        }


        //TODO [블루투스 활성 여부 및 GPS 기능 활성 여부 확인 수행]





    //TODO [실시간 비콘 신호 활성 시작]
    private void BeaconSendStart(final String UUID, final String MAJOR, final String MINOR){
        Log.d("---","---");
        Log.d("//===========//","================================================");
        Log.d("","\n"+"[BeaconSend > BeaconScanStart() 메소드 : 실시간 비콘 신호 활성 수행]");
        Log.d("//===========//","================================================");
        Log.d("---","---");
        try {
            //TODO [beacon 객체 설정 실시]
            beacon = new Beacon.Builder()
                    .setId1(UUID.toLowerCase()) //TODO [UUID 지정]
                    .setId2(MAJOR) //TODO [major 지정]
                    .setId3(MINOR) //TODO [minor 지정]
                    .setManufacturer(0x004C) //TODO [제조사 지정 : IOS 호환]
                    //.setManufacturer(0x0118) // [제조사 지정]
                    .setTxPower(-59) //TODO [신호 세기]
                    //.setTxPower(59) //[신호 세기]
                    .setDataFields(Arrays.asList(new Long[]{0L})) //TODO [레이아웃 필드]
                    .build();

            //TODO [레이아웃 지정 : IOS 호환 (ibeacon)]
            beaconParser = new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24");
            //beaconParser = new BeaconParser().setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25");

            //TODO [비콘 신호 활성 상태 확인 실시]
            beaconTransmitter = new BeaconTransmitter(context.getApplicationContext(), beaconParser);
            beaconTransmitter.startAdvertising(beacon, new AdvertiseCallback() {
                @Override
                public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                    super.onStartSuccess(settingsInEffect);

                    String text = "========================================\n비콘 신호 활성 성공\n"+String.valueOf(UUID) +"[UUID : "+String.valueOf(UUID)+"]"
                            + "[MAJOR : "+String.valueOf(MAJOR)+"]" + "[MINOR : "+String.valueOf(MINOR)+"]"
                            +"\n========================================" + "count: " + ++sendCount;

                    if(mSenderCallback != null){
                        mSenderCallback.onBeaconSendResult(text);
                    }

                    Log.d("---","---");
                    Log.w("//===========//","===========================================");
                    Log.d("","\n"+"[BeaconSend > BeaconScanStart() 메소드 : 실시간 비콘 신호 활성 성공]");
                    Log.d("","\n"+"[UUID : "+String.valueOf(UUID)+"]");
                    Log.d("","\n"+"[MAJOR : "+String.valueOf(MAJOR)+"]");
                    Log.d("","\n"+"[MINOR : "+String.valueOf(MINOR)+"]");
                    Log.d("","\n"+"[시작 시간 : "+String.valueOf(getNowTime())+"]");
                    Log.w("//===========//","===========================================");
                    Log.d("---","---");


                }
                @Override
                public void onStartFailure(int errorCode) {
                    super.onStartFailure(errorCode);

                    String text = "===================================\n비콘 신호 활성 실패\n"+String.valueOf(UUID)  +"[UUID : "+String.valueOf(UUID)+"]"
                            + "[MAJOR : "+String.valueOf(MAJOR)+"]" + "[MINOR : "+String.valueOf(MINOR)+"]"
                            +"\n===================================";

                    if(mSenderCallback != null){
                        mSenderCallback.onBeaconSendResult(text);
                    }

                    Log.d("---","---");
                    Log.e("//===========//","================================================");
                    Log.d("","\n"+"[BeaconSend > BeaconScanStart() 메소드 : 실시간 비콘 신호 활성 실패]");
                    Log.d("","\n"+"[UUID : "+String.valueOf(UUID)+"]");
                    Log.d("","\n"+"[MAJOR : "+String.valueOf(MAJOR)+"]");
                    Log.d("","\n"+"[MINOR : "+String.valueOf(MINOR)+"]");
                    Log.d("","\n"+"[Error : "+String.valueOf(errorCode)+"]");
                    Log.e("//===========//","================================================");
                    Log.d("---","---");
                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    //TODO [실시간 비콘 신호 활성 종료]
    public void BeaconSendStop(){
        Log.d("---","---");
        Log.e("//===========//","================================================");
        Log.d("","\n"+"[BeaconSend > BeaconSendStop() 메소드 : 실시간 비콘 신호 활성 종료]");
        Log.d("","\n"+"[종료 시간 : "+String.valueOf(getNowTime())+"]");
        Log.e("//===========//","================================================");
        Log.d("---","---");
        try {
            //TODO [변수 값 초기화 지정 실시]
            beacon = null;
            beaconTransmitter.stopAdvertising();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    //TODO [블루투스 기능 활성 여부 및 GPS 기능 활성 여부 확인]
    public Boolean getBleStateCheck(){
        boolean state_result = false;
        try {
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if(mBluetoothAdapter == null){ //TODO [블루투스를 지원하는 기기인지 확인]
                Log.d("---","---");
                Log.e("//===========//","================================================");
                Log.d("","\n"+"[BeaconSend > getBleStateCheck() 메소드 : 블루투스 지원 기기 확인]");
                Log.d("","\n"+"[디바이스 : 블루투스를 지원하지 않는 기기]");
                Log.e("//===========//","================================================");
                Log.d("---","---");
                //TODO [Alert 팝업창 알림 실시]
                String alertTitle = "[블루투스 기능 지원 여부 확인]";
                String alertMessage = "사용자 디바이스는 블루투스 기능을 지원하지 않는 단말기입니다.";
                String buttonYes = "확인";
                String buttonNo = "취소";
                new AlertDialog.Builder(context)
                        .setTitle(alertTitle)
                        .setMessage(alertMessage)
                        .setCancelable(false)
                        .setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                            }
                        })
                        .setNegativeButton(buttonNo, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                            }
                        })
                        .show();
            }
            else { //TODO [블루투스가 켜져있는지 확인]
                Log.d("---","---");
                Log.w("//===========//","================================================");
                Log.d("","\n"+"[BeaconSend > getBleStateCheck() 메소드 : 블루투스 지원 기기 확인]");
                Log.d("","\n"+"[디바이스 : 블루투스를 지원하는 기기]");
                Log.w("//===========//","================================================");
                Log.d("---","---");
                if(mBluetoothAdapter.isEnabled() == true){
                    Log.d("---","---");
                    Log.w("//===========//","================================================");
                    Log.d("","\n"+"[BeaconSend > getBleStateCheck() 메소드 : 블루투스 기능 활성 확인]");
                    Log.d("","\n"+"[상태 : 블루투스 기능 활성]");
                    Log.w("//===========//","================================================");
                    Log.d("---","---");

                    //TODO [GPS 활성 상태 확인 실시]
                    try {
                        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){ //TODO 위치 권한 비활성인 경우
                            Log.d("---","---");
                            Log.e("//===========//","================================================");
                            Log.d("","\n"+"[BeaconSend > getBleStateCheck() 메소드 : GPS 위치 권한 활성 여부 확인]");
                            Log.d("","\n"+"[상태 : 비활성]");
                            Log.e("//===========//","================================================");
                            Log.d("---","---");
                            //TODO [Alert 팝업창 알림 실시]
                            String alertTitle = "[GPS 기능 활성 여부 확인]";
                            String alertMessage = "GPS 기능이 비활성화 상태입니다.\nGPS 기능을 활성화해야 정상 기능 사용이 가능합니다.";
                            String buttonYes = "확인";
                            String buttonNo = "취소";
                            new AlertDialog.Builder(context)
                                    .setTitle(alertTitle)
                                    .setMessage(alertMessage)
                                    .setCancelable(false)
                                    .setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // TODO Auto-generated method stub
                                            goGpsSettingsIntent(); //TODO [GPS 기능 설정창 이동 실시]
                                        }
                                    })
                                    .setNegativeButton(buttonNo, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // TODO Auto-generated method stub
                                        }
                                    })
                                    .show();
                        }
                        else { //TODO 위치 권한 활성인 경우
                            Log.d("---","---");
                            Log.w("//===========//","================================================");
                            Log.d("","\n"+"[BeaconSend > getBleStateCheck() 메소드 : GPS 위치 권한 활성 여부 확인]");
                            Log.d("","\n"+"[상태 : 활성]");
                            Log.w("//===========//","================================================");
                            Log.d("---","---");
                            state_result = true;
                        }
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
                else {
                    Log.d("---","---");
                    Log.e("//===========//","================================================");
                    Log.d("","\n"+"[BeaconSend > getBleStateCheck() 메소드 : 블루투스 기능 활성 확인]");
                    Log.d("","\n"+"[상태 : 블루투스 기능 비활성]");
                    Log.e("//===========//","================================================");
                    Log.d("---","---");

                    //TODO [Alert 팝업창 알림 실시]
                    String alertTitle = "[블루투스 기능 활성 여부 확인]";
                    String alertMessage = "블루투스 기능이 비활성화 상태입니다.\n블루투스 기능을 활성화해야 정상 기능 사용이 가능합니다.";
                    String buttonYes = "확인";
                    String buttonNo = "취소";
                    new AlertDialog.Builder(context)
                            .setTitle(alertTitle)
                            .setMessage(alertMessage)
                            .setCancelable(false)
                            .setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO Auto-generated method stub
                                    goBleSettingsIntent(); //TODO [블루투스 설정창 이동 실시]
                                }
                            })
                            .setNegativeButton(buttonNo, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO Auto-generated method stub
                                }
                            })
                            .show();
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        Log.d("---","---");
        Log.d("//===========//","================================================");
        Log.d("","\n"+"[BeaconSend > getBleStateCheck() 메소드 : 블루투스 및 GPS 활성 상태 리턴 값 확인]");
        Log.d("","\n"+"[리턴 값 : "+String.valueOf(state_result)+"]");
        Log.d("//===========//","================================================");
        Log.d("---","---");
        return state_result;
    }

    //TODO [안드로이드 시스템 블루투스 설정창 이동 메소드]
    public void goBleSettingsIntent(){
        try {
            Log.d("---","---");
            Log.w("//===========//","================================================");
            Log.d("","\n"+"[BeaconSend > goBleSettingsIntent() 메소드 : 블루투스 설정창 인텐트 이동 실시]");
            Log.w("//===========//","================================================");
            Log.d("---","---");
            Intent go_ble = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
            go_ble.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            context.startActivity(go_ble);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    //TODO [안드로이드 시스템 GPS 설정창 이동 메소드]
    public void goGpsSettingsIntent(){
        try {
            Log.d("---","---");
            Log.w("//===========//","================================================");
            Log.d("","\n"+"[BeaconSend > goGpsSettingsIntent() 메소드 : 위치 권한 설정창 인텐트 이동 실시]");
            Log.w("//===========//","================================================");
            Log.d("---","---");
            Intent go_gps = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            go_gps.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            context.startActivity(go_gps);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    // [현재 시간 알아오는 메소드]
    public static String getNowTime() {
        long time = System.currentTimeMillis();
        SimpleDateFormat dayTime = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        String str = dayTime.format(new Date(time));
        return str;
    }

    interface SenderCallback {
            void onBeaconSendResult(String text);
    }
}

