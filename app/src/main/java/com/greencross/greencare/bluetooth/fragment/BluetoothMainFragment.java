package com.greencross.greencare.bluetooth.fragment;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.greencross.greencare.R;
import com.greencross.greencare.base.CommonActionBar;
import com.greencross.greencare.base.value.Define;
import com.greencross.greencare.bluetooth.device.BandDevice;
import com.greencross.greencare.bluetooth.device.BaseDevice;
import com.greencross.greencare.bluetooth.device.BloodDevice;
import com.greencross.greencare.bluetooth.device.PressureDevice;
import com.greencross.greencare.bluetooth.device.WeightDevice;
import com.greencross.greencare.bluetooth.manager.DeviceDataUtil;
import com.greencross.greencare.bluetooth.manager.DeviceManager;
import com.greencross.greencare.bluetooth.model.BandModel;
import com.greencross.greencare.bluetooth.model.BloodModel;
import com.greencross.greencare.bluetooth.model.PressureModel;
import com.greencross.greencare.bluetooth.model.WeightModel;
import com.greencross.greencare.main.BluetoothManager;
import com.greencross.greencare.network.tr.data.Tr_login;
import com.greencross.greencare.util.CDateUtil;
import com.greencross.greencare.util.Logger;
import com.greencross.greencare.util.StringUtil;

import java.util.Date;
import java.util.List;
import java.util.Set;

public class BluetoothMainFragment extends BluetoothBaseFragment implements View.OnClickListener {
    private final String TAG = BluetoothMainFragment.class.getSimpleName();
    private TextView mTextViewBlood;
    private TextView mTextViewPressure;
    private TextView mTextViewWeight;
    private TextView mTextViewBand;

    private ImageView mImageViewBloodStatus;
    private ImageView mImageViewPressureStatus;
    private ImageView mImageViewWeightStatus;
    private ImageView mImageViewBandStatus;

    private Button mButtonMeasureHearthRate;
    private Button mButtonMeasureAltitude;

    private ImageView mImageViewSetting;

    private Animation flashAnimation;

    private BloodDevice mDeviceBlood;
    private PressureDevice mDevicePressure;
    private WeightDevice mDeviceWeight;
    private BandDevice mDeviceBand;

    private BluetoothAdapter bluetoothAdapter;

    private boolean bluetoothOpenCheck = false;

    public static Fragment newInstance() {
        BluetoothMainFragment fragment = new BluetoothMainFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main_bluetooth, container, false);
        setHasOptionsMenu(true);
        return view;
    }


    @Override
    public void loadActionbar(CommonActionBar actionBar) {
        super.loadActionbar(actionBar);
        actionBar.setActionBarTitle(getString(R.string.text_device));
        actionBar.setActionBarSettingBtn(true, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                movePage(DeviceManagementFragment.newInstance());
            }
        });
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(getContext(), getString(R.string.text_alert_bluetooth_pairing), Toast.LENGTH_LONG).show();
            super.onBackPressed();
            return;
        }

        flashAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.anim_flashing);

        mImageViewBloodStatus = (ImageView) view.findViewById(R.id.imageViewBloodStatus);
        mImageViewBloodStatus.setOnClickListener(this);
        mImageViewPressureStatus = (ImageView) view.findViewById(R.id.imageViewPressureStatus);
        mImageViewPressureStatus.setOnClickListener(this);
        mImageViewWeightStatus = (ImageView) view.findViewById(R.id.imageViewWeightStatus);
        mImageViewWeightStatus.setOnClickListener(this);
        mImageViewBandStatus = (ImageView) view.findViewById(R.id.imageViewBandStatus);
        mImageViewBandStatus.setOnClickListener(this);

        mTextViewBlood = (TextView) view.findViewById(R.id.textViewBlood);
        mTextViewPressure = (TextView) view.findViewById(R.id.textViewPressure);
        mTextViewWeight = (TextView) view.findViewById(R.id.textViewWeight);
        mTextViewBand = (TextView) view.findViewById(R.id.textViewBand);

        mButtonMeasureAltitude = (Button) view.findViewById(R.id.buttonMeasureAltitude);
        mButtonMeasureAltitude.setOnClickListener(this);
        mButtonMeasureHearthRate = (Button) view.findViewById(R.id.buttonMeasureHeartRate);
        mButtonMeasureHearthRate.setOnClickListener(this);
    }


    @Override
    public void onResume() {
        super.onResume();
        if (!bluetoothAdapter.isEnabled() && !bluetoothOpenCheck) {
            Toast.makeText(getContext(), getString(R.string.text_alert_bluetooth_on_message), Toast.LENGTH_LONG).show();
            openBluetoothSetting();
            bluetoothOpenCheck = true;
            return;
        }
        connectDevices();
    }


    @Override
    public void onPause() {
        super.onPause();
        disconnectDevices();
    }


    private void connectDevices() {
        connectBloodDevice();
        connectPressureDevice();
        connectWeightDevice();
        connectBandDevice();
    }

    private void connectBloodDevice() {

        if (mDeviceBlood != null) {
            mDeviceBlood.disconnect();
        }

        if (DeviceManager.isRegDevice(getContext(), DeviceManager.FLAG_BLE_DEVICE_BLOOD)) {
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(DeviceManager.getRegDeviceAddress(getContext(), DeviceManager.FLAG_BLE_DEVICE_BLOOD));

            if (isPairedDevice(device)) {
                mDeviceBlood = new BloodDevice(device);
                mDeviceBlood.setOnBluetoothListener(onBloodDeviceListener);
                mDeviceBlood.connect(getContext());
            } else {

//                String message = getString(R.string.text_alert_bluetooth_pairing);
//                CDialog.showDlg(getContext(), message, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        openBluetoothSetting();
//                        mImageViewBloodStatus.setImageResource(R.drawable.ic_do_not_disturb_on_black_24dp);
//                        mImageViewBloodStatus.clearAnimation();
//                    }
//                }, null);
            }
        } else {
            mImageViewBloodStatus.setImageResource(R.drawable.ic_do_not_disturb_on_black_24dp);
            mImageViewBloodStatus.clearAnimation();

        }
    }

    private boolean isPairedDevice(BluetoothDevice device) {
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices != null) {
            for (BluetoothDevice pairedDevice : pairedDevices) {
                if (pairedDevice.equals(device)) {
                    return true;
                }
            }
        }

        return false;
    }

    private void connectPressureDevice() {

        if (mDevicePressure != null) {
            mDevicePressure.disconnect();
        }
        if (DeviceManager.isRegDevice(getContext(), DeviceManager.FLAG_BLE_DEVICE_PRESSURE)) {
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(DeviceManager.getRegDeviceAddress(getContext(), DeviceManager.FLAG_BLE_DEVICE_PRESSURE));
            mDevicePressure = new PressureDevice(device);
            mDevicePressure.setOnBluetoothListener(onPressureDeviceListener);
            mDevicePressure.connect(getContext());
        } else {
            Log.e(TAG, "BlueTooth 혈압계가 등록되어 있지 않습니다.");
            mImageViewPressureStatus.setImageResource(R.drawable.ic_do_not_disturb_on_black_24dp);
            mImageViewPressureStatus.clearAnimation();
        }

    }

    private void connectWeightDevice() {

        if (mDeviceWeight != null) {
            mDeviceWeight.disconnect();
        }
        if (DeviceManager.isRegDevice(getContext(), DeviceManager.FLAG_BLE_DEVICE_WEIGHT)) {
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(DeviceManager.getRegDeviceAddress(getContext(), DeviceManager.FLAG_BLE_DEVICE_WEIGHT));
            mDeviceWeight = new WeightDevice(device);
            mDeviceWeight.setOnBluetoothListener(onWeightDeviceListener);

            /*
            회원정보로 벤드업데이트-벤트초기화 되어 보류합니다.*/
            Tr_login info = Define.getInstance().getLoginInfo();
            int mber_height = StringUtil.getIntVal(info.mber_height);
            int mber_sex = StringUtil.getIntVal(info.mber_sex);
            int rBirth      = StringUtil.getIntVal(info.mber_lifyea.substring(0, 4));                      // 회원 생년
            String nowYear  = CDateUtil.getFormattedString_yyyy(System.currentTimeMillis());                // 현재 년도
            int rAge        = (StringUtil.getIntVal(nowYear) - rBirth);                                     // 회원 나이
            BaseDevice.Gender sex = mber_sex==1?WeightDevice.Gender.MAN:WeightDevice.Gender.WOMAN;

            mDeviceWeight.setUserInfo(rAge, mber_height, sex);
            mDeviceWeight.connect(getContext());
        } else {
            Log.e(TAG, "BlueTooth 체중계가 등록되어 있지 않습니다.");

            mImageViewWeightStatus.setImageResource(R.drawable.ic_do_not_disturb_on_black_24dp);
            mImageViewWeightStatus.clearAnimation();
        }

    }

    private void connectBandDevice() {
        if (mDeviceBand != null) {
            mDeviceBand.disconnect();
        }

        if (DeviceManager.isRegDevice(getContext(), DeviceManager.FLAG_BLE_DEVICE_BAND)) {

            /*
            회원정보로 벤드업데이트-벤트초기화 되어 보류합니다.*/
            Tr_login info = Define.getInstance().getLoginInfo();
            int mber_height = StringUtil.getIntVal(info.mber_height);
            int mber_sex = StringUtil.getIntVal(info.mber_sex);
            int rBirth      = StringUtil.getIntVal(info.mber_lifyea.substring(0, 4));                      // 회원 생년
            String nowYear  = CDateUtil.getFormattedString_yyyy(System.currentTimeMillis());                // 현재 년도
            int rAge        = (StringUtil.getIntVal(nowYear) - rBirth);                                     // 회원 나이
            BaseDevice.Gender sex = mber_sex==1?WeightDevice.Gender.MAN:WeightDevice.Gender.WOMAN;

            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(DeviceManager.getRegDeviceAddress(getContext(), DeviceManager.FLAG_BLE_DEVICE_BAND));
            mDeviceBand = new BandDevice(device);
            mDeviceBand.setOnBluetoothListener(onBluetoothListener);
            mDeviceBand.setUserInfo(rAge, mber_height, 65, sex);
            mDeviceBand.connect(getContext());

        } else {
            Log.e(TAG, "BlueTooth 활동계가 등록되어 있지 않습니다.");
            mImageViewBandStatus.setImageResource(R.drawable.ic_do_not_disturb_on_black_24dp);
            mImageViewBandStatus.clearAnimation();

        }
    }


    private void disconnectDevices() {
        if (mDeviceBlood != null) {
            mDeviceBlood.disconnect();
            mDeviceBlood = null;
        }
        if (mDevicePressure != null) {
            mDevicePressure.disconnect();
            mDevicePressure = null;
        }
        if (mDeviceWeight != null) {
            mDeviceWeight.disconnect();
            mDeviceWeight = null;
        }
    }


    private BaseDevice.OnBluetoothListener<SparseArray<BloodModel>> onBloodDeviceListener = new BaseDevice.OnBluetoothListener<SparseArray<BloodModel>>() {
        @Override
        public void onStart() {
            mImageViewBloodStatus.setImageResource(R.drawable.ic_bluetooth_searching_black_24dp);
            mImageViewBloodStatus.startAnimation(flashAnimation);
        }


        @Override
        public void onConnected() {
            mImageViewBloodStatus.setImageResource(R.drawable.ic_bluetooth_connected_black_24dp);
            mImageViewBloodStatus.clearAnimation();

            mDeviceBlood.readData();
        }

        @Override
        public void onDisConnected() {
            mImageViewBloodStatus.setImageResource(R.drawable.ic_bluetooth_disabled_black_24dp);
            mImageViewBloodStatus.clearAnimation();

        }

        @Override
        public void onReceivedData(SparseArray<BloodModel> dataModel) {
            Log.d(TAG, "BlueTooth Read Blood: size="+dataModel.size() +", " + dataModel.toString());
            if (dataModel.size() > 0) {
                BloodModel data = dataModel.get(dataModel.keyAt(dataModel.size() - 1));
                mTextViewBlood.setText(data.getSugar() + "mg/dL");
                data.setRegtype(BloodModel.INPUT_TYPE_DEVICE);
                data.setRegTime(CDateUtil.getForamtyyyyMMddHHmmss(data.getTime()));
                data.setBefore("0");
                Logger.i(TAG, "onReceivedData getEatType="+data.getEatType());
                Logger.i(TAG, "onReceivedData getTime="+data.getTime());
                Logger.i(TAG, "onReceivedData getIdx="+data.getIdx());
                Logger.i(TAG, "onReceivedData getSequenceNumber="+data.getSequenceNumber());
                Logger.i(TAG, "onReceivedData getSugar="+data.getSugar());
                Logger.i(TAG, "onReceivedData getRegTime="+data.getRegTime());

                new DeviceDataUtil().uploadSugarData(BluetoothMainFragment.this, dataModel, null);
            }
        }
    };

    private BaseDevice.OnBluetoothListener<PressureModel> onPressureDeviceListener = new BaseDevice.OnBluetoothListener<PressureModel>() {
        @Override
        public void onStart() {
            mImageViewPressureStatus.setImageResource(R.drawable.ic_bluetooth_searching_black_24dp);
            mImageViewPressureStatus.startAnimation(flashAnimation);
        }

        @Override
        public void onConnected() {
            mImageViewPressureStatus.setImageResource(R.drawable.ic_bluetooth_connected_black_24dp);
            mImageViewPressureStatus.clearAnimation();

            mDevicePressure.readData();

        }

        @Override
        public void onDisConnected() {
            mImageViewPressureStatus.setImageResource(R.drawable.ic_bluetooth_disabled_black_24dp);
            mImageViewPressureStatus.clearAnimation();

        }

        @Override
        public void onReceivedData(PressureModel dataModel) {
            Log.d(TAG, "BlueTooth Read Pressure:" + dataModel.toString());
            mTextViewPressure.setText(dataModel.getSystolicPressure() + "/" + dataModel.getDiastolicPressure());
            new DeviceDataUtil().uploadPresure(BluetoothMainFragment.this, dataModel, new BluetoothManager.IBluetoothResult() {
                @Override
                public void onResult(boolean isSuccess) {

                }
            });
        }
    };

    private BaseDevice.OnBluetoothListener<WeightModel> onWeightDeviceListener = new BaseDevice.OnBluetoothListener<WeightModel>() {
        @Override
        public void onStart() {
            mImageViewWeightStatus.setImageResource(R.drawable.ic_bluetooth_searching_black_24dp);
            mImageViewWeightStatus.startAnimation(flashAnimation);
        }

        @Override
        public void onConnected() {
            mImageViewWeightStatus.setImageResource(R.drawable.ic_bluetooth_connected_black_24dp);
            mImageViewWeightStatus.clearAnimation();
            mDeviceWeight.readData();
        }

        @Override
        public void onDisConnected() {
            mImageViewWeightStatus.setImageResource(R.drawable.ic_bluetooth_disabled_black_24dp);
            mImageViewWeightStatus.clearAnimation();
        }

        @Override
        public void onReceivedData(WeightModel dataModel) {
            Log.d(TAG, "BlueTooth Read Weight:" + dataModel.toString());
            mTextViewWeight.setText(dataModel.getWeight() + "Kg");
        }
    };

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
    private BandDevice.OnBluetoothListener<BandModel> onBluetoothListener = new BandDevice.OnBluetoothListener<BandModel>() {
        @Override
        public void onStart() {
            mImageViewBandStatus.setImageResource(R.drawable.ic_bluetooth_searching_black_24dp);
            mImageViewBandStatus.startAnimation(flashAnimation);
        }

        @Override
        public void onConnected() {
            mImageViewBandStatus.setImageResource(R.drawable.ic_bluetooth_connected_black_24dp);
            mImageViewBandStatus.clearAnimation();
            //시간당 걸음데이터 가져오기
            mDeviceBand.readTimeData();
            //시간당 심박데이터 가져오기
//            mDeviceBand.readTimePPGData();

            mButtonMeasureAltitude.setEnabled(true);
            mButtonMeasureHearthRate.setEnabled(true);
        }

        @Override
        public void onDisConnected() {
            mImageViewBandStatus.setImageResource(R.drawable.ic_bluetooth_disabled_black_24dp);
            mImageViewBandStatus.clearAnimation();

            mButtonMeasureAltitude.setEnabled(false);
            mButtonMeasureHearthRate.setEnabled(false);
        }

        @Override
        public void onReceivedData(BandModel dataModel) {
            Log.d(TAG, "BlueTooth Read Band:" + dataModel.toString());
            mTextViewBand.setText(dataModel.getCalories() + "kcal,"
                    + dataModel.getHeartRate() + "bpm,"
                    + dataModel.getStep() + "걸음,"
                    + dataModel.getDistance() + "미터,"
                    + dataModel.getStress() + "스트레스");

                dataModel.setRegtype("D");
                dataModel.setIdx(CDateUtil.getForamtyyMMddHHmmssSS(new Date(System.currentTimeMillis())));
                dataModel.setRegDate(CDateUtil.getForamtyyyyMMddHHmmss(new Date(System.currentTimeMillis())));

        }

        @Override
        public void onReceivedTimeData(List<BandModel> dataModel) {
                if (dataModel != null) {
                    Log.d(TAG, "Read Band.size="+dataModel.size() +":" + dataModel.toString());
                    mTextViewBand.setText(dataModel.toString());

                    new DeviceDataUtil().uploadStepData(BluetoothMainFragment.this, dataModel);
                } else {
                    mTextViewBand.setText(getString(R.string.text_network_non_data));
                }
        }


        @Override
        public void onReceivedPPGData(List<BandModel> dataModel) {
            if (dataModel != null) {
                Log.d(TAG, "Read PPG.size="+dataModel.size() +":" + dataModel.toString());

                new DeviceDataUtil().uploadPPGData(BluetoothMainFragment.this, dataModel);
            }
        }

        @Override
        public void onHeartRateMeasured(int heartRate) {
            mTextViewBand.setText(heartRate + "bpm");
        }

        @Override
        public void onAltitudeMeasured(int altitude) {
            mTextViewBand.setText(altitude + "미터");

        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageViewSetting:
                movePage(DeviceManagementFragment.newInstance());
                break;
            case R.id.imageViewBloodStatus:
                connectBloodDevice();
                break;
            case R.id.imageViewPressureStatus:
                connectPressureDevice();
                break;
            case R.id.imageViewWeightStatus:
                connectWeightDevice();
                break;
            case R.id.imageViewBandStatus:
                connectBandDevice();
                break;
            case R.id.buttonMeasureHeartRate:
                if (mDeviceBand != null) {
                    mDeviceBand.measureHeartRate();
                }
                break;
            case R.id.buttonMeasureAltitude:
                if (mDeviceBand != null) {
                    mDeviceBand.mesureAltitude(1011.2f);
                }
                break;
        }
    }
}