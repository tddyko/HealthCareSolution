package com.greencross.greencare.step;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.greencross.greencare.base.value.Define;
import com.greencross.greencare.bluetooth.device.BandDevice;
import com.greencross.greencare.bluetooth.device.BaseDevice;
import com.greencross.greencare.bluetooth.device.BloodDevice;
import com.greencross.greencare.bluetooth.device.PressureDevice;
import com.greencross.greencare.bluetooth.device.WeightDevice;
import com.greencross.greencare.bluetooth.manager.DeviceDataUtil;
import com.greencross.greencare.bluetooth.manager.DeviceManager;
import com.greencross.greencare.bluetooth.model.BandModel;
import com.greencross.greencare.component.CDialog;
import com.greencross.greencare.main.BluetoothManager;
import com.greencross.greencare.network.tr.data.Tr_login;
import com.greencross.greencare.util.CDateUtil;
import com.greencross.greencare.util.Logger;
import com.greencross.greencare.util.StringUtil;

import java.util.Date;
import java.util.List;

public class BluetoothStepManager {
    private final String TAG = BluetoothStepManager.class.getSimpleName();

    private BloodDevice mDeviceBlood;
    private PressureDevice mDevicePressure;
    private WeightDevice mDeviceWeight;
    private BandDevice mDeviceBand;

    private BluetoothAdapter bluetoothAdapter;
    private boolean bluetoothOpenCheck = false;

    private StepManageFragment mStepManageFragment;

    public BluetoothStepManager(StepManageFragment stepManageFragment) {
        mStepManageFragment = stepManageFragment;
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {

            CDialog.showDlg(stepManageFragment.getContext(), "해당 단말은 블루투스를 지원하지 않습니다.");
            return;
        }
        Logger.i(TAG, "BluetoothManager start");

        connectBandDevice();
    }

    public void onResume() {
        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled() && !bluetoothOpenCheck) {
            Toast.makeText(mStepManageFragment.getContext(), "해당 기능을 진행하기위해 블루투스를 켜주세요.", Toast.LENGTH_LONG).show();
            openBluetoothSetting();
            bluetoothOpenCheck = true;
            return;
        }
        connectBandDevice();
    }



    public void connectBandDevice() {
        if (mDeviceBand != null) {
            mDeviceBand.disconnect();
        }


        if (DeviceManager.isRegDevice(mStepManageFragment.getContext(), DeviceManager.FLAG_BLE_DEVICE_BAND)) {
            /*
            회원정보로 벤드업데이트-벤트초기화 되어 보류합니다. */
            Tr_login info = Define.getInstance().getLoginInfo();
            int mber_height = StringUtil.getIntVal(info.mber_height);
            int mber_sex = StringUtil.getIntVal(info.mber_sex);
            int rBirth      = StringUtil.getIntVal(info.mber_lifyea.substring(0, 4));                      // 회원 생년
            String nowYear  = CDateUtil.getFormattedString_yyyy(System.currentTimeMillis());                // 현재 년도
            int rAge        = (StringUtil.getIntVal(nowYear) - rBirth);                                     // 회원 나이
            BaseDevice.Gender sex = mber_sex==1?WeightDevice.Gender.MAN:WeightDevice.Gender.WOMAN;


            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(DeviceManager.getRegDeviceAddress(mStepManageFragment.getContext(), DeviceManager.FLAG_BLE_DEVICE_BAND));
            mDeviceBand = new BandDevice(device);
            mDeviceBand.setOnBluetoothListener(onStepBluetoothListener);
            mDeviceBand.setUserInfo(rAge, mber_height, 65, sex);
            mDeviceBand.connect(mStepManageFragment.getContext());
        } else {
            Logger.e(TAG, "BlueTooth 활동량계가 등록되어 있지 않습니다.");
        }
    }



    /**
     * 활동량계
     [ {
     calories: 23,
     distance: 0.39,
     heartRate: 0,
     latestTimeMs: 0,
     step: 549,
     stress: 0,
     time: 11,
     serialVersionUID: 7579522124476046343,
     serialVersionUID: 7888357950147579467,
     shadow$_klass_: class com.greencross.greencare.bluetooth.model.BandModel,
     shadow$_monitor_: 0
     }]

     * */
    private BandDevice.OnBluetoothListener<BandModel> onStepBluetoothListener = new BandDevice.OnBluetoothListener<BandModel>() {
        @Override
        public void onStart() {
        }

        @Override
        public void onConnected() {
            //시간당 걸음데이터 가져오기
            mDeviceBand.readTimeData();
            //시간당 심박데이터 가져오기
            mDeviceBand.readTimePPGData();
        }

        @Override
        public void onDisConnected() {
        }

        @Override
        public void onReceivedData(BandModel dataModel) {
            Log.d(TAG, "Read Band:" + dataModel.toString());

            dataModel.setRegtype("D");
            dataModel.setIdx(CDateUtil.getForamtyyMMddHHmmssSS(new Date(System.currentTimeMillis())));
            dataModel.setRegDate(CDateUtil.getForamtyyyyMMddHHmmss(new Date(System.currentTimeMillis())));

        }
        @Override
        public void onReceivedTimeData(List<BandModel> dataModel) {
            if (dataModel != null) {
                Log.d(TAG, "Read Band.size="+dataModel.size() +":" + dataModel.toString());

                new DeviceDataUtil().uploadStepData(mStepManageFragment, dataModel, new BluetoothManager.IBluetoothResult() {
                    @Override
                    public void onResult(boolean isSuccess) {
                        Logger.i(TAG, "Bluetooth uploadStepData:"+isSuccess);
                        mStepManageFragment.onChartViewResume();
                    }
                });

            } else {
                Logger.e(TAG, "Read Band:데이터가 없습니다.");
            }
        }

        @Override
        public void onReceivedPPGData(List<BandModel> dataModel) {

            Logger.e(TAG, "onReceivedPPGData");

            if (dataModel != null) {
                Log.d(TAG, "Read PPG.size="+dataModel.size() +":" + dataModel.toString());

                new DeviceDataUtil().uploadPPGData(mStepManageFragment, dataModel, new BluetoothManager.IBluetoothResult() {
                    @Override
                    public void onResult(boolean isSuccess) {
                        Logger.i(TAG, "Bluetooth uploadPPGData:"+isSuccess);
//                        mStepManageFragment.onChartViewResume();
                    }
                });

            } else {
                Logger.e(TAG, "Read Band:데이터가 없습니다.");
            }
        }

        @Override
        public void onHeartRateMeasured(int heartRate) {
            Logger.i(TAG, "bluetooth.heartRate="+heartRate);
        }

        @Override
        public void onAltitudeMeasured(int altitude) {
            Logger.i(TAG, "bluetooth.altitude="+altitude +" 미터");

        }
    };

    protected void openBluetoothSetting() {
        Intent intentOpenBluetoothSettings = new Intent();
        intentOpenBluetoothSettings.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
        mStepManageFragment.startActivity(intentOpenBluetoothSettings);
    }

    public interface IBluetoothResult {
        void onResult(boolean isSuccess);
    }
}
