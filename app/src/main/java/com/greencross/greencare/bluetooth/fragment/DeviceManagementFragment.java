package com.greencross.greencare.bluetooth.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.greencross.greencare.R;
import com.greencross.greencare.base.CommonActionBar;
import com.greencross.greencare.base.value.Define;
import com.greencross.greencare.bluetooth.device.BloodDevice;
import com.greencross.greencare.bluetooth.manager.DeviceManager;
import com.greencross.greencare.component.CDialog;
import com.greencross.greencare.util.Logger;
import com.greencross.greencare.util.SharedPref;

import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by jongrakmoon on 2017. 3. 2..
 */

public class DeviceManagementFragment extends BluetoothBaseFragment implements View.OnClickListener {
    private static final int PERMISSION_REQUEST_CODE = 1000;

    public static String IS_STEP_DEVICE_REGIST = "is_step_device_regist";   // 걸음 소스타입을 밴드로 선택했을때

    private TextView mTextViewWeight;
    private TextView mTextViewPressure;
    private TextView mTextViewBlood;
    private TextView mTextViewBand;

    private ImageView mImageViewSearchWeight;
    private ImageView mImageViewSearchPressure;
    private ImageView mImageViewSearchBlood;
    private ImageView mImageViewSearchBand;

    private ImageView mImageViewDisconnectWeight;
    private ImageView mImageViewDisconnectPressure;
    private ImageView mImageViewDisconnectBlood;
    private ImageView mImageViewDisconnectBand;

    private boolean deviceAlertCheck;
    private boolean bluetoochOpenCheck;

    // 블루투스 스캔관련
    public static final String INTENT_BLE_DEVICE = "_device";
    public static final String BLE_DEVICE_WEIGHT = "Chipsea";
    public static final String BLE_DEVICE_PRESSURE = "A&D";
    public static final String BLE_DEVICE_BLOOD = "CareSens";
    public static final String BLE_DEVICE_BAND = "URBAN";

    private String deviceName;

    private BluetoothAdapter bluetoothAdapter;
    private ScanCallback scanCallback;
    private Boolean isExistBlood = false;       // 혈당계 안드로이드 설정에 이미 등록되어 있는지 여부.

    private BluetoothDevice _device;            // 현재 스캔 디바이스.
    private int nearStreet = -300;               // 0에 가까울 수록 가까움.
    private Handler timeHandler;
    private Timer timer;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    public static Fragment newInstance() {
        DeviceManagementFragment fragment = new DeviceManagementFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_device_management, container, false);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void loadActionbar(CommonActionBar actionBar) {
        super.loadActionbar(actionBar);
        actionBar.setActionBarTitle(getString(R.string.text_device));
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        deviceAlertCheck = false;
        bluetoochOpenCheck = false;

        mTextViewWeight = (TextView) view.findViewById(R.id.textViewWeight);
        mTextViewPressure = (TextView) view.findViewById(R.id.textViewPressure);
        mTextViewBlood = (TextView) view.findViewById(R.id.textViewBlood);
        mTextViewBand = (TextView) view.findViewById(R.id.textViewBand);

        mImageViewSearchWeight = (ImageView) view.findViewById(R.id.imageViewSearchWeight);
        mImageViewSearchWeight.setOnClickListener(this);
        mImageViewSearchPressure = (ImageView) view.findViewById(R.id.imageViewSearchPressure);
        mImageViewSearchPressure.setOnClickListener(this);
        mImageViewSearchBlood = (ImageView) view.findViewById(R.id.imageViewSearchBlood);
        mImageViewSearchBlood.setOnClickListener(this);
        mImageViewSearchBand = (ImageView) view.findViewById(R.id.imageViewSearchBand);
        mImageViewSearchBand.setOnClickListener(this);

        mImageViewDisconnectWeight = (ImageView) view.findViewById(R.id.imageViewDisconnectWeight);
        mImageViewDisconnectWeight.setOnClickListener(this);
        mImageViewDisconnectPressure = (ImageView) view.findViewById(R.id.imageViewDisconnectPressure);
        mImageViewDisconnectPressure.setOnClickListener(this);
        mImageViewDisconnectBlood = (ImageView) view.findViewById(R.id.imageViewDisconnectBlood);
        mImageViewDisconnectBlood.setOnClickListener(this);
        mImageViewDisconnectBand = (ImageView) view.findViewById(R.id.imageViewDisconnectBand);
        mImageViewDisconnectBand.setOnClickListener(this);

        // 블루투스스캔관련
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(getContext(), getString(R.string.text_alert_bluetooth_unsupported_device), Toast.LENGTH_LONG).show();
            return;
        }else {
            scanCallback = new ScanCallback();
        }

        timeHandler = new Handler();
    }

    BloodDevice bloodDevice;

    private void updateRegDevice() {
        if (DeviceManager.isRegDevice(getContext(), DeviceManager.FLAG_BLE_DEVICE_WEIGHT)) {
            mImageViewSearchWeight.setVisibility(View.GONE);
            mImageViewDisconnectWeight.setVisibility(View.VISIBLE);
        } else {
            mImageViewSearchWeight.setVisibility(View.VISIBLE);
            mImageViewDisconnectWeight.setVisibility(View.GONE);
        }
        if (DeviceManager.isRegDevice(getContext(), DeviceManager.FLAG_BLE_DEVICE_PRESSURE)) {
            mImageViewSearchPressure.setVisibility(View.GONE);
            mImageViewDisconnectPressure.setVisibility(View.VISIBLE);
        } else {
            mImageViewSearchPressure.setVisibility(View.VISIBLE);
            mImageViewDisconnectPressure.setVisibility(View.GONE);
        }
//        if (DeviceManager.isRegDevice(getContext(), DeviceManager.FLAG_BLE_DEVICE_BLOOD)) {
//            mImageViewSearchBlood.setVisibility(View.GONE);
//            mImageViewDisconnectBlood.setVisibility(View.VISIBLE);
//        } else {
//            mImageViewSearchBlood.setVisibility(View.VISIBLE);
//            mImageViewDisconnectBlood.setVisibility(View.GONE);
//        }
        // 혈당계는 안드로이드 설정에 등록되어 있느냐 여부에 따라 구분
        if (isExistBlood) {
            mImageViewSearchBlood.setVisibility(View.GONE);
            mImageViewDisconnectBlood.setVisibility(View.VISIBLE);
        }else {
            mImageViewSearchBlood.setVisibility(View.VISIBLE);
            mImageViewDisconnectBlood.setVisibility(View.GONE);
        }
        if (DeviceManager.isRegDevice(getContext(), DeviceManager.FLAG_BLE_DEVICE_BAND)) {
            mImageViewSearchBand.setVisibility(View.GONE);
            mImageViewDisconnectBand.setVisibility(View.VISIBLE);
            Bundle bundle = getArguments();
            if (bundle != null && bundle.getBoolean(IS_STEP_DEVICE_REGIST, false)) {
                SharedPref.getInstance().savePreferences(SharedPref.STEP_DATA_SOURCE_TYPE, Define.STEP_DATA_SOURCE_BAND);
            }
        } else {
            mImageViewSearchBand.setVisibility(View.VISIBLE);
            mImageViewDisconnectBand.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        isExistBlood = false;
        if (!bluetoothAdapter.isEnabled() && !bluetoochOpenCheck) {
            Toast.makeText(getContext(), getString(R.string.text_alert_bluetooth_on_message), Toast.LENGTH_LONG).show();
            openBluetoothSetting();
            bluetoochOpenCheck = true;
        }


        // 안드로이드 설정에 블루투스 목록에 있으면 X버튼으로 변경
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        for(BluetoothDevice bt : pairedDevices) {
            if (bt.getName().contains("CareSens")) {

                deviceName = BLE_DEVICE_BLOOD;
                DeviceManager.regDeviceAddress(getContext(), deviceName, bt.getAddress());

                mImageViewSearchBlood.setVisibility(View.GONE);
                mImageViewDisconnectBlood.setVisibility(View.VISIBLE);
                isExistBlood = true;
            }
        }
        updateRegDevice();
    }

    @Override
    public void onClick(View v) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getContext().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE);
                return;
            }
        }
        _device     = null;
        nearStreet   = -300;

        Bundle bundle = new Bundle();
        switch (v.getId()) {
            case R.id.imageViewSearchWeight:
                bundle.putString(INTENT_BLE_DEVICE, BLE_DEVICE_WEIGHT);
                deviceName = BLE_DEVICE_WEIGHT;

                startSearchBluetooth();
                break;
            case R.id.imageViewSearchPressure:
                bundle.putString(INTENT_BLE_DEVICE, BLE_DEVICE_PRESSURE);
                deviceName = BLE_DEVICE_PRESSURE;

                startSearchBluetooth();
                break;
            case R.id.imageViewSearchBlood:

                // 안드로이드설정에 블루투스 목록에 없으면 메시지 띄울것.
                if (!isExistBlood){
                    // 혈당계가 안드로이드 설정에 등록되어 있지 않다면, 안드로이드 설정으로 이동.
                    CDialog.showDlg(getContext(), getString(R.string.text_alert_bluetooth_caresens_pairing), new CDialog.DismissListener() {
                        @Override
                        public void onDissmiss() {
                            openBluetoothSetting();
                        }
                    });
                    return;
                }

//                bundle.putString(INTENT_BLE_DEVICE, BLE_DEVICE_BLOOD);
//                deviceName = BLE_DEVICE_BLOOD;
//
//                startSearchBluetooth();
                break;
            case R.id.imageViewSearchBand:
                bundle.putString(INTENT_BLE_DEVICE, BLE_DEVICE_BAND);
                deviceName = BLE_DEVICE_BAND;


                startSearchBluetooth();
                break;
            case R.id.imageViewDisconnectWeight:

                String message1 = getContext().getString(R.string.text_alert_bluetooth_weight);
                CDialog.showDlg(getContext(), message1, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DeviceManager.removeRegDeviceAddress(getContext(), DeviceManager.FLAG_BLE_DEVICE_WEIGHT);
                        updateRegDevice();
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                break;
            case R.id.imageViewDisconnectPressure:
                String message2 = getContext().getString(R.string.text_alert_bluetooth_pressure);
                CDialog.showDlg(getContext(), message2, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DeviceManager.removeRegDeviceAddress(getContext(), DeviceManager.FLAG_BLE_DEVICE_PRESSURE);
                        updateRegDevice();
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                break;
            case R.id.imageViewDisconnectBlood:


                String message3 = getContext().getString(R.string.text_alert_bluetooth_sugar);
                CDialog.showDlg(getContext(), message3, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DeviceManager.removeRegDeviceAddress(getContext(), DeviceManager.FLAG_BLE_DEVICE_BLOOD);
                        openBluetoothSetting();
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DeviceManager.removeRegDeviceAddress(getContext(), DeviceManager.FLAG_BLE_DEVICE_BLOOD);
                        updateRegDevice();
                    }
                });
                break;
            case R.id.imageViewDisconnectBand:

                String message4 = getContext().getString(R.string.text_alert_bluetooth_band);
                CDialog.showDlg(getContext(), message4, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DeviceManager.removeRegDeviceAddress(getContext(), DeviceManager.FLAG_BLE_DEVICE_BAND);
                        updateRegDevice();

                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                break;
        }
        updateRegDevice();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), getString(R.string.text_alert_bluetooth_le_permission), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(), getString(R.string.permission_access_success), Toast.LENGTH_LONG).show();
            }
        }
    }

    // 블루투스 관련
    private Dialog searchDialog;

    private void startSearchBluetooth() {
        Logger.i(TAG, "startSearchBluetooth="+deviceName);

        if (!bluetoothAdapter.startLeScan(scanCallback)) {
            Toast.makeText(getContext(), getString(R.string.text_alert_bluetooth_le_non_support), Toast.LENGTH_LONG).show();
            return;
        }

        if (searchDialog != null) {
            searchDialog.dismiss();
        }
        searchDialog = ProgressDialog.show(getContext(), getString(R.string.text_alert_bluetooth_search_message), getString(R.string.text_alert_bluetooth_device_search_message), true, true, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                bluetoothAdapter.stopLeScan(scanCallback);
            }
        });
        searchDialog.show();
    }

    private class ScanCallback implements BluetoothAdapter.LeScanCallback {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {

            if (device != null && device.getName() != null) {
                Logger.i(TAG, "ScanCallback.onLeScan="+deviceName+", device.getName()="+device.getName()+"rssi::"+rssi);

                if (device.getName().contains(deviceName)) {
                    if (nearStreet < rssi){

                        Logger.i(TAG, "Near Street Rssi::"+rssi);
                        nearStreet = rssi;

                        if(timer != null) {
                            timer.cancel();
                            timer = null;
                        }
                        if (timer == null) {
                            _device = device;
                            timer = new Timer();
                            timer.schedule(new RssiSeekTimer(), 3000);
                        }
                    }
                }
            }
        }
    }

    class RssiSeekTimer extends TimerTask {
        public void run() {
            timeHandler.post(new Runnable() {
                public void run() {
                    if (searchDialog != null && _device != null) {
                        bluetoothAdapter.stopLeScan(scanCallback);
                        if(deviceAlertCheck == false){

                            Logger.i(TAG, "Rssi RssiSeekTimer 실행==> 연결");
                            if(timer != null) {
                                timer.cancel();
                                timer = null;
                            }
                            deviceAlertCheck = true;
                            showDeviceRegAlert(_device);
                        }
                    }
                }
            });
        }
    }

    private void showDeviceRegAlert(final BluetoothDevice device) {

        new AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.text_alert))
                .setMessage(device.getName() + "기기가 발견되었습니다.\n등록 하시겠습니까?")
                .setPositiveButton(getString(R.string.text_regist), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DeviceManager.regDeviceAddress(getContext(), deviceName, device.getAddress());
                        dialog.dismiss();
                        updateRegDevice();
                        searchDialog.dismiss();
                        deviceAlertCheck = false;
                        Toast.makeText(getContext(), device.getName()+" 기기가 등록되었습니다.", Toast.LENGTH_LONG).show();
                    }
                }).setNegativeButton(getString(R.string.text_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                searchDialog.dismiss();
                deviceAlertCheck = false;
            }
        }).show();
    }
}