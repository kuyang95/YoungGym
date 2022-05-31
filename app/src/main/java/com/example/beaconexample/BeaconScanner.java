package com.example.beaconexample;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;

import android.provider.Settings;

import android.util.Log;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BeaconScanner implements BeaconConsumer {

    // 아마도 연습 주석

    //TODO [실시간 비콘 스캐닝을 하기 위한 변수 및 객체 선언 실시]
    private BeaconManager beaconManager; // [비콘 매니저 객체]
    private List<Beacon> beaconList = new ArrayList<>(); // [실시간 비콘 감지 배열]
    int beaconScanCount = 1; // [비콘 스캔 횟수를 카운트하기 위함]
    ArrayList beaconFormatList = new ArrayList<>(); // [스캔한 비콘 리스트를 포맷해서 저장하기 위함]
    Context mContext;
    ScannerCallback mScannerCallback;
    protected static final String TAG = "MonitoringActivity";


    public void init(Context context) {
        mContext = context;

        // 비콘 매니저 초기 설정 및 레이아웃 지정 실시
        beaconSetting();
    }

    public void setCallback(ScannerCallback scannerCallback) {
        mScannerCallback = scannerCallback;
    }

    public void start() {
        if (getBleStateCheck() == true) { // 블루투스 및 GPS 기능이 모두 활성 상태
            beaconScanStart(); // 비콘 스캔 시작 실시
        }
    }

    public void startOfficial() {
        if (getBleStateCheck() == true) {
            official();
        }
    }

/*
        //TODO [5초뒤 실행 (작업 예약)]
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                BeaconScanStop(); //[비콘 스캔 종료 실시]
            }
        }, 60000);

 */

    //TODO [비콘 스캐닝을 위한 초기 설정]
    public void beaconSetting() {
        Log.d("---", "---");
        Log.d("//===========//", "================================================");
        Log.d("", "\n" + "[BeaconScan > BeaconSettiong() 메소드 : 비콘 매니저 초기 설정 수행]");
        Log.d("", "\n" + "[레이아웃 : m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25]");
        Log.d("//===========//", "================================================");
        Log.d("---", "---");
        try {
            //TODO [비콘 매니저 생성]
            beaconManager = BeaconManager.getInstanceForApplication(mContext);

            //TODO [블루투스가 스캔을 중지하지 않도록 설정]
            beaconManager.setEnableScheduledScanJobs(false);

            //TODO [레이아웃 지정 - IOS , Android 모두 스캔 가능]
            beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //TODO [실시간 비콘 스캐닝 시작]
    public void beaconScanStart() {
        Log.d("---", "---");
        Log.w("//===========//", "================================================");
        Log.d("", "\n" + "[BeaconScan > beaconScanStart() 메소드 : 실시간 비콘 스캐닝 시작]");
        Log.w("//===========//", "================================================");
        Log.d("---", "---");
        try {
            //TODO [변수값 초기화 실시]
            beaconScanCount = 1;

            //TODO [beaconManager Bind 설정]
            beaconManager.bind(BeaconScanner.this);

            //TODO [실시간 비콘 스캔 수행 핸들러 호출]
            BeaconHandler.sendEmptyMessage(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //TODO [실시간 비콘 스캐닝 종료]
    public void BeaconScanStop() {
        Log.d("---", "---");
        Log.e("//===========//", "================================================");
        Log.d("", "\n" + "[BeaconScan > BeaconScanStop() 메소드 : 실시간 비콘 스캐닝 종료]");
        Log.e("//===========//", "================================================");
        Log.d("---", "---");

        try {
            //TODO [변수값 초기화 실시]
            beaconScanCount = 1;

            //TODO [핸들러 사용 종료]
            BeaconHandler.removeMessages(0);
            BeaconHandler.removeCallbacks(null);

            //TODO [beaconManager Bind 해제]
            beaconManager.unbind(BeaconScanner.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //TODO [실시간 비콘 스캐닝 감지 부분]
    @Override
    public void onBeaconServiceConnect() {
        RangeNotifier rangeNotifier = new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                //TODO [비콘이 감지되면 해당 함수가 호출]
                //TODO [비콘들에 대응하는 Region 객체가 들어들어옴]
                if (beacons.size() > 0) {
                    beaconList.clear();
                    for (Beacon beacon : beacons) {
                        beaconList.add(beacon);
                    }
                }
            }
        };
        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
            beaconManager.addRangeNotifier(rangeNotifier);
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
            beaconManager.addRangeNotifier(rangeNotifier);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    Handler BeaconHandler = new Handler() {
        public void handleMessage(Message msg) {
            try {
                //TODO [기존에 저장된 배열 데이터 초기화 실시]
                if (beaconFormatList.size() > 0) {
                    beaconFormatList.clear();
                }

                int filteredCount = 0;
                // text = "비콘 스캔 개수 확인: " + beaconList.size() + "\n비콘 스캔 정보: " + String.valueOf(beaconFormatList.toString());
                String text = "";


                //TODO [for 문 사용해 실시간 스캔된 비콘 개별 정보 확인]
                for (Beacon beacon : beaconList) {
                    //TODO [비콘 스캔 정보 추출 참고]
                    Log.d("//===========//", "================================================");
                    Log.d("", "\n" + "[비콘 스캔 Name] " + " [" + String.valueOf(beacon.getBluetoothName()) + "]");
                    Log.d("", "\n" + "[비콘 스캔 MAC] " + " [" + String.valueOf(beacon.getBluetoothAddress()) + "]");
                    Log.d("", "\n" + "[비콘 스캔 UUID] " + " [" + String.valueOf(beacon.getId1().toString()) + "]");
                    Log.d("", "\n" + "[비콘 스캔 Major] " + " [" + String.valueOf(beacon.getId2().toString()) + "]");
                    Log.d("", "\n" + "[비콘 스캔 Minor] " + " [" + String.valueOf(beacon.getId3().toString()) + "]");
                    Log.d("", "\n" + "[비콘 스캔 MPower] " + " [" + String.valueOf(beacon.getTxPower()) + "]");
                    Log.d("", "\n" + "[비콘 스캔 RSSI] " + " [" + String.valueOf(beacon.getRssi()) + "]");
                    Log.d("", "\n" + "[비콘 스캔 ServiceUuid] " + " [" + String.valueOf(beacon.getServiceUuid()) + "]");
                    Log.d("", "\n" + "[비콘 스캔 beacon] " + " [" + String.valueOf(beacon.toString()) + "]");
                    Log.d("//===========//", "================================================");


                    if (beacon.getId1().toString().startsWith("ffffffff")) {
                        filteredCount += 1;
                        text += beacon.getId1().toString() + "," + beacon.getId2().toString() + "," + beacon.getId3().toString();
                    }

                    //TODO [스캔한 비콘 정보 포맷 실시]
                    JSONObject jsonBeacon = new JSONObject();
                    jsonBeacon.put("UUID", String.valueOf(beacon.getBluetoothName()));
                    jsonBeacon.put("MAJOR", String.valueOf(beacon.getId2().toString()));
                    jsonBeacon.put("MINOR", String.valueOf(beacon.getId3().toString()));

                    //TODO [배열에 데이터 저장 실시]
                    beaconFormatList.add(jsonBeacon.toString());

                }//TODO [for 문 종료]




                if (mScannerCallback != null) {
                    mScannerCallback.onBeaconScanResult(filteredCount + text);
                }

                //TODO [실시간 스캔된 비콘 정보 확인 실시]
                Log.d("---", "---");
                Log.w("//===========//", "================================================");
                Log.d("", "\n" + "[비콘 스캔 실행 횟수] " + " [" + String.valueOf(beaconScanCount) + "]");
                Log.d("", "\n" + "[비콘 스캔 개수 확인] " + " [" + String.valueOf(beaconFormatList.size()) + "]");
                Log.d("", "\n" + "[비콘 스캔 정보 확인] " + " [" + String.valueOf(beaconFormatList.toString()) + "]");
                Log.w("//===========//", "================================================");
                Log.d("---", "---");

                //TODO [중간 필요한 로직 처리 실시]

                //TODO [비콘 스캔 카운트 증가]
                beaconScanCount++;
            } catch (Exception e) {
                e.printStackTrace();
            }
            //TODO [자기 자신을 1초마다 호출]
            BeaconHandler.sendEmptyMessageDelayed(0, 100);
        }
    };

    //TODO [블루투스 기능 활성 여부 및 GPS 기능 활성 여부 확인]
    public Boolean getBleStateCheck() {
        boolean state_result = false;
        try {
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) { //TODO [블루투스를 지원하는 기기인지 확인]
                Log.d("---", "---");
                Log.e("//===========//", "================================================");
                Log.d("", "\n" + "[BeaconScan > getBleStateCheck() 메소드 : 블루투스 지원 기기 확인]");
                Log.d("", "\n" + "[디바이스 : 블루투스를 지원하지 않는 기기]");
                Log.e("//===========//", "================================================");
                Log.d("---", "---");
                //TODO [Alert 팝업창 알림 실시]
                String alertTitle = "[블루투스 기능 지원 여부 확인]";
                String alertMessage = "사용자 디바이스는 블루투스 기능을 지원하지 않는 단말기입니다.";
                String buttonYes = "확인";
                String buttonNo = "취소";
                new AlertDialog.Builder(mContext)
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
            } else { //TODO [블루투스가 켜져있는지 확인]
                Log.d("---", "---");
                Log.w("//===========//", "================================================");
                Log.d("", "\n" + "[BeaconScan > getBleStateCheck() 메소드 : 블루투스 지원 기기 확인]");
                Log.d("", "\n" + "[디바이스 : 블루투스를 지원하는 기기]");
                Log.w("//===========//", "================================================");
                Log.d("---", "---");
                if (mBluetoothAdapter.isEnabled() == true) {
                    Log.d("---", "---");
                    Log.w("//===========//", "================================================");
                    Log.d("", "\n" + "[BeaconScan > getBleStateCheck() 메소드 : 블루투스 기능 활성 확인]");
                    Log.d("", "\n" + "[상태 : 블루투스 기능 활성]");
                    Log.w("//===========//", "================================================");
                    Log.d("---", "---");

                    //TODO [GPS 활성 상태 확인 실시]
                    try {
                        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
                        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) { //TODO 위치 권한 비활성인 경우
                            Log.d("---", "---");
                            Log.e("//===========//", "================================================");
                            Log.d("", "\n" + "[BeaconScan > getBleStateCheck() 메소드 : GPS 위치 권한 활성 여부 확인]");
                            Log.d("", "\n" + "[상태 : 비활성]");
                            Log.e("//===========//", "================================================");
                            Log.d("---", "---");
                            //TODO [Alert 팝업창 알림 실시]
                            String alertTitle = "[GPS 기능 활성 여부 확인]";
                            String alertMessage = "GPS 기능이 비활성화 상태입니다.\nGPS 기능을 활성화해야 정상 기능 사용이 가능합니다.";
                            String buttonYes = "확인";
                            String buttonNo = "취소";
                            new AlertDialog.Builder(mContext)
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
                        } else { //TODO 위치 권한 활성인 경우
                            Log.d("---", "---");
                            Log.w("//===========//", "================================================");
                            Log.d("", "\n" + "[BeaconScan > getBleStateCheck() 메소드 : GPS 위치 권한 활성 여부 확인]");
                            Log.d("", "\n" + "[상태 : 활성]");
                            Log.w("//===========//", "================================================");
                            Log.d("---", "---");
                            state_result = true;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.d("---", "---");
                    Log.e("//===========//", "================================================");
                    Log.d("", "\n" + "[BeaconScan > getBleStateCheck() 메소드 : 블루투스 기능 활성 확인]");
                    Log.d("", "\n" + "[상태 : 블루투스 기능 비활성]");
                    Log.e("//===========//", "================================================");
                    Log.d("---", "---");

                    //TODO [Alert 팝업창 알림 실시]
                    String alertTitle = "[블루투스 기능 활성 여부 확인]";
                    String alertMessage = "블루투스 기능이 비활성화 상태입니다.\n블루투스 기능을 활성화해야 정상 기능 사용이 가능합니다.";
                    String buttonYes = "확인";
                    String buttonNo = "취소";
                    new AlertDialog.Builder(mContext)
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("---", "---");
        Log.d("//===========//", "================================================");
        Log.d("", "\n" + "[BeaconScan > getBleStateCheck() 메소드 : 블루투스 및 GPS 활성 상태 리턴 값 확인]");
        Log.d("", "\n" + "[리턴 값 : " + String.valueOf(state_result) + "]");
        Log.d("//===========//", "================================================");
        Log.d("---", "---");
        return state_result;
    }

    //TODO [안드로이드 시스템 블루투스 설정창 이동 메소드]
    public void goBleSettingsIntent() {
        try {
            Log.d("---", "---");
            Log.w("//===========//", "================================================");
            Log.d("", "\n" + "[BeaconScan > goBleSettingsIntent() 메소드 : 블루투스 설정창 인텐트 이동 실시]");
            Log.w("//===========//", "================================================");
            Log.d("---", "---");
            Intent go_ble = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
            go_ble.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            mContext.startActivity(go_ble);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //TODO [안드로이드 시스템 GPS 설정창 이동 메소드]
    public void goGpsSettingsIntent() {
        try {
            Log.d("---", "---");
            Log.w("//===========//", "================================================");
            Log.d("", "\n" + "[BeaconScan > goGpsSettingsIntent() 메소드 : 위치 권한 설정창 인텐트 이동 실시]");
            Log.w("//===========//", "================================================");
            Log.d("---", "---");
            Intent go_gps = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            go_gps.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            mContext.startActivity(go_gps);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // official
    public void official() {
        beaconManager.addMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                Log.i(TAG, "I just saw an beacon for the first time!");
            }

            @Override
            public void didExitRegion(Region region) {
                Log.i(TAG, "I no longer see an beacon");
            }

            @Override
            public void didDetermineStateForRegion(int state, Region region) {
                Log.i(TAG, "I have just switched from seeing/not seeing beacons: " + state);
            }
        });

        beaconManager.startMonitoring(new Region("myMonitoringUniqueId", null, null, null));

        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    Log.i(TAG, "The first beacon I see is about " + beacons.iterator().next().getDistance() + " meters away.");

                    beaconList.clear();
                    for (Beacon beacon : beacons) {
                        beaconList.add(beacon);
                    }

                    BeaconHandler.sendEmptyMessage(0);

                }
            }
        });

        beaconManager.startRangingBeacons(new Region("myRangingUniqueId", null, null, null));
    }


    @Override
    public Context getApplicationContext() {
        return null;
    }

    @Override
    public void unbindService(ServiceConnection connection) {
    }

    @Override
    public boolean bindService(Intent intent, ServiceConnection connection, int mode) {
        return false;
    }

    interface ScannerCallback {
        void onBeaconScanResult(String text);
    }
}//TODO 클래스 종료


