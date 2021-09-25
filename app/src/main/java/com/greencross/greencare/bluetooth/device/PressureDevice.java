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

import com.greencross.greencare.bluetooth.model.PressureModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimerTask;
import java.util.UUID;

import static android.R.attr.key;

/**
 * Created by jongrakmoon on 2017. 3. 2..
 */

public class PressureDevice extends BaseDevice<PressureModel> {

    private final String TAG = PressureDevice.class.getSimpleName();

    public static final UUID UUID_BLOOD_PRESSURE_SERVICE = UUID.fromString("00001810-0000-1000-8000-00805f9b34fb");
    public static final UUID UUID_BLOOD_PRESSURE_MEASUREMENT = UUID.fromString("00002a35-0000-1000-8000-00805f9b34fb");
    public static final UUID UUID_CLIENT_CHARACTERISTIC_CONFIGURATION = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");//
    public static final UUID UUID_DATA_TIME = UUID.fromString("00002a08-0000-1000-8000-00805f9b34fb");

    private BluetoothGatt mBluetoothGatt;
    private OnBluetoothListener<PressureModel> listener;

    public PressureDevice(BluetoothDevice bluetoothDevice) {
        super(bluetoothDevice);
    }


    @Override
    public void connect(Context context) {
        if (listener != null) {
            mUIHandler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onStart();
                }
            });
        }
        mBluetoothGatt = bluetoothDevice.connectGatt(context.getApplicationContext(), true, mGattCallback);
    }


    @Override
    public void disconnect() {
        if (mBluetoothGatt != null) {
            mBluetoothGatt.close();
        }
    }

    @Override
    public boolean readData() {
        BluetoothGattService service = mBluetoothGatt.getService(UUID_BLOOD_PRESSURE_SERVICE);

        if (service != null) {
            BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID_BLOOD_PRESSURE_MEASUREMENT);
            if (characteristic != null) {
                mBluetoothGatt.setCharacteristicNotification(characteristic, true);
                BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID_CLIENT_CHARACTERISTIC_CONFIGURATION);
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
                return mBluetoothGatt.writeDescriptor(descriptor);
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public void setOnBluetoothListener(OnBluetoothListener<PressureModel> listener) {
        this.listener = listener;
    }

    public OnBluetoothListener<PressureModel> getOnBluetoothListener() {
        return listener;
    }

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) {
            Log.d(TAG, "onConnectionStateChange:Status(" + status + ")");

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.d(TAG, "Connected");
                gatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.d(TAG, "Disconnected");
                if (listener != null) {
                    mUIHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onDisConnected();
                        }
                    });
                }
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.d(TAG, "onServicesDiscovered");
            if (status == BluetoothGatt.GATT_SUCCESS) {
                BluetoothGattService service = gatt.getService(UUID_BLOOD_PRESSURE_SERVICE);
                if (service != null) {
                    Log.d(TAG,"SERVICE NOT NULL");

                    BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID_DATA_TIME);
                    if (characteristic != null) {
                        Log.d(TAG,"Character NOT NULL");

                        Calendar calendar = Calendar.getInstance();
                        int year = calendar.get(Calendar.YEAR);
                        int month = calendar.get(Calendar.MONTH) + 1;
                        int day = calendar.get(Calendar.DAY_OF_MONTH);
                        int hour = calendar.get(Calendar.HOUR_OF_DAY);
                        int min = calendar.get(Calendar.MINUTE);
                        int sec = calendar.get(Calendar.SECOND);

                        byte[] value = {
                                (byte) (year & 0x0FF),    // year 2bit
                                (byte) (year >> 8),        //
                                (byte) month,            // month
                                (byte) day,                // day
                                (byte) hour,                // hour
                                (byte) min,                // min
                                (byte) sec                // sec
                        };
                        characteristic.setValue(value);
                        gatt.writeCharacteristic(characteristic);

                        if (listener != null) {
                            mUIHandler.postDelayed(new TimerTask() {
                                @Override
                                public void run() {
                                    listener.onConnected();
                                }
                            },500);
                        }
                    }
                }
            } else {
                gatt.close();
            }
        }


        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.d(TAG, "onCharacteristicRead");
        }


        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            Log.d(TAG, "onDescriptorWrite:" + descriptor.getUuid() + ":" + status);
        }


        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
            Log.d(TAG, "onCharacteristicChanged");
            final PressureModel receivedData = readCharacteristic(characteristic);
            setCurrentData(receivedData);
            if (listener != null) {
                mUIHandler.post(new TimerTask() {
                    @Override
                    public void run() {
                        listener.onReceivedData(receivedData);
                    }
                });
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            Log.d(TAG, "onCharacteristicWrite");

            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (characteristic.getUuid().equals(UUID_DATA_TIME)) {
                    if (listener != null) {
                        mUIHandler.post(new TimerTask() {
                            @Override
                            public void run() {
                                listener.onConnected();
                            }
                        });
                    }
                }
            }
        }

        private PressureModel readCharacteristic(BluetoothGattCharacteristic characteristic) {
            PressureModel pressureModel = new PressureModel();

            int flag = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
            String flagString = Integer.toBinaryString(flag);
            int offset = 0;
            for (int index = flagString.length(); 0 < index; index--) {
                String key = flagString.substring(index - 1, index);

                if (index == flagString.length()) {
                    // Unit
                    offset += 1;
                    pressureModel.setSystolicPressure(characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT, offset));
                    offset += 2;
                    pressureModel.setDiastolicPressure(characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT, offset));
                    offset += 2;
                    pressureModel.setArterialPressure(characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT, offset));
                    offset += 2;
                } else if (index == flagString.length() - 1) {

                    Calendar calendar = Calendar.getInstance();

                    if (key.equals("1")) {
                        calendar.set(Calendar.YEAR, characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, offset));
                        offset += 2;
                        calendar.set(Calendar.MONTH, characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset) - 1);
                        offset += 1;
                        calendar.set(Calendar.DATE, characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset));
                        offset += 1;
                        calendar.set(Calendar.HOUR_OF_DAY, characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset));
                        offset += 1;
                        calendar.set(Calendar.MINUTE, characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset));
                        offset += 1;
                        calendar.set(Calendar.SECOND, characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset));
                        offset += 1;
                    }
                        // 측정시 시간이 없으면 현재시간.
                        pressureModel.setTime(calendar.getTime());


                } else if (index == flagString.length() - 2) {
                    if (key.equals("1")) {
                        // Pulse Rate
                        pressureModel.setPulseRate(characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT, offset));
                        offset += 2;
                    }
                }
            }

            Log.d(TAG,new SimpleDateFormat("yyyyMMdd-HH:mm:ss").format(pressureModel.getTime()));
            return pressureModel;
        }

    };
}