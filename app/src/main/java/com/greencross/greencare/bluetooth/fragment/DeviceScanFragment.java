package com.greencross.greencare.bluetooth.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.greencross.greencare.R;
import com.greencross.greencare.base.CommonActionBar;
import com.greencross.greencare.bluetooth.manager.DeviceManager;


/**
 * Created by jongrakmoon on 2017. 3. 2..
 */

public class DeviceScanFragment extends BluetoothBaseFragment implements View.OnClickListener {

    private Button mButtonStartSearch;

    private String deviceName;
    private boolean deviceAlertCheck;
    private boolean bluetoothOpenCheck;

    private BluetoothAdapter bluetoothAdapter;
    private ScanCallback scanCallback;

    public static Fragment newInstance() {
        DeviceScanFragment fragment = new DeviceScanFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_device_scan, container, false);
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
        bluetoothOpenCheck = false;

        if (getArguments() != null)
            deviceName = getArguments().getString(DeviceManagementFragment.INTENT_BLE_DEVICE);

        if (deviceName == null) {
            Toast.makeText(getContext(), getString(R.string.text_alert_bluetooth_Abnormal_approach), Toast.LENGTH_LONG).show();
            return;
        }

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(getContext(), getString(R.string.text_alert_bluetooth_unsupported_device), Toast.LENGTH_LONG).show();
            return;
        }
        scanCallback = new ScanCallback();

        mButtonStartSearch = (Button) view.findViewById(R.id.buttonSearchStart);
        mButtonStartSearch.setOnClickListener(this);


        startSearchBluetooth();
    }


    @Override
    public void onResume() {
        super.onResume();
        if (!bluetoothAdapter.isEnabled() && !bluetoothOpenCheck) {
            Toast.makeText(getContext(), getString(R.string.text_alert_bluetooth_on_message), Toast.LENGTH_LONG).show();
            openBluetoothSetting();
            bluetoothOpenCheck = true;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonSearchStart:
                startSearchBluetooth();
                break;
        }
    }

    private Dialog searchDialog;

    private void startSearchBluetooth() {

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
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            if (device != null && device.getName() != null) {
                if (device.getName().contains(deviceName)) {
                    bluetoothAdapter.stopLeScan(this);
                    if (searchDialog != null) {
                        searchDialog.dismiss();

                        if(deviceAlertCheck == false){
                            deviceAlertCheck = true;
                            showDeviceRegAlert(device);

                        }

                    }
                }
            }
        }
    }

    private void showDeviceRegAlert(final BluetoothDevice device) {
        new AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.text_alert))
                .setMessage(device.getName() + "기기가 발견되었습니다.\n등록하시겠습니까?")
                .setPositiveButton(getString(R.string.text_regist), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deviceAlertCheck = false;
                        DeviceManager.regDeviceAddress(getContext(), deviceName, device.getAddress());
                        dialog.dismiss();
                        onBackPressed();
                    }
                }).setNegativeButton(getString(R.string.text_cancel), null).show();
    }
}