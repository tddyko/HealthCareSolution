package com.greencross.greencare.bluetooth.device;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.util.Log;

import com.greencross.greencare.base.value.Define;
import com.greencross.greencare.bluetooth.model.WeightModel;
import com.greencross.greencare.network.tr.data.Tr_login;
import com.greencross.greencare.util.StringUtil;

import java.util.UUID;

/**
 * Created by jongrakmoon on 2017. 3. 2..
 */

public class WeightDevice extends BaseDevice<WeightModel> {
    private final String TAG = WeightDevice.class.getSimpleName();

    public final static UUID CLIENT_CHARACTERISTIC_CONFIG = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    public final static UUID UUID_RTX_SERVICE = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");
    public final static UUID UUID_TX_CHAR = UUID.fromString("0000fff2-0000-1000-8000-00805f9b34fb");
    public final static UUID UUID_RX_CHAR = UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb");

    private BluetoothGatt mBluetoothGatt;
    private byte[] userInfoValue;

    private OnBluetoothListener<WeightModel> listener;

    public WeightDevice(BluetoothDevice bluetoothDevice) {
        super(bluetoothDevice);
    }

    public void setUserInfo(int age, int height, Gender gender) {
        userInfoValue = new byte[20];
        userInfoValue[0] = (byte) (0xCA & 0xFF);  // header
        userInfoValue[1] = (byte) (0x10 & 0xFF);  // version
        userInfoValue[2] = (byte) (0x0E & 0xFF);  // lenth
        userInfoValue[3] = (byte) (0x01 & 0xFF);  // command

        userInfoValue[4] = (byte) (0x0E & 0xFF);  // year
        userInfoValue[5] = (byte) (0x0C & 0xFF);  // month
        userInfoValue[6] = (byte) (0x0C & 0xFF);  // day
        userInfoValue[7] = (byte) (0x0C & 0xFF);  // hour
        userInfoValue[8] = (byte) (0x0C & 0xFF);  // minute
        userInfoValue[9] = (byte) (0x00 & 0xFF);  // second

        userInfoValue[10] = (byte) (0x00 & 0xFF); // userid
        userInfoValue[11] = (byte) (0x00 & 0xFF); // userid
        userInfoValue[12] = (byte) (0x00 & 0xFF); // userid
        userInfoValue[13] = (byte) (0x00 & 0xFF); // userid

        if (gender.getValue()) {
            userInfoValue[14] = (byte) (0x01 & 0xFF); // gender(man)
        } else {
            userInfoValue[14] = (byte) (0x00 & 0xFF); // gender(woman)
        }
        userInfoValue[15] = (byte) (age & 0xFF); // age
        userInfoValue[16] = (byte) (height & 0xFF); // height

        userInfoValue[17] = (byte) (userInfoValue[1] ^ userInfoValue[16]); // check bit
        userInfoValue[18] = (byte) (0x00 & 0xFF); // padding bit
        userInfoValue[19] = (byte) (0x00 & 0xFF); // padding bit
    }

    private boolean isForceDisconnect;

    private Context context;

    @Override
    public void connect(Context context) {
        this.context = context;
        if (userInfoValue == null) {
            throw new NullPointerException("Connect 하기전에 setUserInfo()먼저 실행해주세요.");
        }
        isForceDisconnect = false;
        if (listener != null) {
            mUIHandler.post(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "connect listener onStart()");
                    listener.onStart();
                }
            });
        }

        Log.d(TAG, "connect mBluetoothGatt initize..");
        mBluetoothGatt = bluetoothDevice.connectGatt(context.getApplicationContext(), true, mGattCallback);
    }


    @Override
    public void disconnect() {
        isForceDisconnect = true;
        if (mBluetoothGatt != null) {
            mBluetoothGatt.disconnect();
            mBluetoothGatt.close();
        }
    }

    public void setOnBluetoothListener(OnBluetoothListener<WeightModel> listener) {
        this.listener = listener;
    }

    public OnBluetoothListener<WeightModel> getOnBluetoothListener() {
        return listener;
    }

    @Override
    public boolean readData() {
        BluetoothGattService service = mBluetoothGatt.getService(UUID_RTX_SERVICE);
        if (service != null) {
            BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID_RX_CHAR);
            if (characteristic != null) {
                mBluetoothGatt.setCharacteristicNotification(characteristic, true);

                BluetoothGattDescriptor descriptor = characteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG);
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                Log.d(TAG, "readData :" + descriptor);
                return mBluetoothGatt.writeDescriptor(descriptor);
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public WeightModel getCurrentData() {
        return null;
    }

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) {
            Log.d(TAG, "onConnectionStateChange:Status(" + status + ")");

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.d(TAG, "mGattCallback Connected");
                gatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {

                if (!isForceDisconnect) {
                    gatt.close();
                    connect(context);
                }

                Log.d(TAG, "mGattCallback Disconnected 1 ");
                if (listener != null) {
                    mUIHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "mGattCallback Disconnected 2");
                            listener.onDisConnected();
                        }
                    });
                }
            } else {

                Log.d(TAG, "mGattCallback unKnow");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.d(TAG, "mGattCallback onServicesDiscovered");
            if (status == BluetoothGatt.GATT_SUCCESS) {
                writeUserInfo();
                Log.d(TAG, "mGattCallback onServicesDiscovered GATT_SUCCESS 1");
                if (listener != null) {
                    mUIHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "mGattCallback onServicesDiscovered GATT_SUCCESS 2");
                            listener.onConnected();
                        }
                    }, 1000);
                }
            } else {
                Log.d(TAG, "mGattCallback onServicesDiscovered CLOSE");
                gatt.close();
            }

        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.d(TAG, "onCharacteristicRead");
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            Log.d(TAG, "onDescriptorWrite");
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            Log.d(TAG, "onCharacteristicChanged:" + characteristic.getUuid());
            if (characteristic.getUuid().equals(UUID_RX_CHAR)) {
                final WeightModel readData = readCharacteristic(characteristic);
                setCurrentData(readData);
                if (listener != null && readData.getWeight() >= 20.0f) {
                    mUIHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onReceivedData(readData);
                        }
                    });
                }
            }
        }

        private WeightModel readCharacteristic(BluetoothGattCharacteristic characteristic) {
            WeightModel weightModel = new WeightModel();
            int flag = characteristic.getProperties();
            int format;
            if ((flag & 0x01) != 0) {
                format = BluetoothGattCharacteristic.FORMAT_UINT16;
            } else {
                format = BluetoothGattCharacteristic.FORMAT_UINT8;
            }
            try {
                String str = getHexString(characteristic.getValue()).toUpperCase();

                Tr_login login = Define.getInstance().getLoginInfo();

                weightModel.setWeight(getHexToDec(str.substring(10, 14)) / 10);
                weightModel.setFat(getHexToDec(str.substring(14, 18)) / 10);
                weightModel.setBodyWater(getHexToDec(str.substring(18, 22)) / 10);
                weightModel.setMuscle(getHexToDec(str.substring(22, 26)) / 10);
                weightModel.setBmr((getHexToDec(str.substring(26, 30)) / 10));
                weightModel.setObesity(getHexToDec(str.substring(30, 34)) / 10);    // 내장지방
                weightModel.setBone(getHexToDec(str.substring(34, 36)) / 10);
                weightModel.setHeartRate(characteristic.getIntValue(format, 1));
                weightModel.setBdwgh_goal(StringUtil.getFloatVal(login.mber_bdwgh_goal));

            } catch (Exception e) {
                e.printStackTrace();
            }

            return weightModel;
        }

        private float getHexToDec(String hex) {
            long v = Long.parseLong(hex, 16);
            return Float.parseFloat(String.valueOf(v));
        }

        private String getHexString(byte[] b) {
            String result = "";
            try {
                for (int i = 0; i < b.length; i++) {
                    result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
                }
                return result;
            } catch (Exception e) {
                return null;
            }
        }

        private boolean writeUserInfo() {
            BluetoothGattService mCustomService = mBluetoothGatt.getService(UUID_RTX_SERVICE);
            if (mCustomService == null) {
                return false;
            }
        /*get the read characteristic from the service*/
            BluetoothGattCharacteristic mWriteCharacteristic = mCustomService.getCharacteristic(UUID_TX_CHAR);
            mWriteCharacteristic.setValue(userInfoValue);
            return mBluetoothGatt.writeCharacteristic(mWriteCharacteristic);
        }
    };
}