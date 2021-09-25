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
import android.util.SparseArray;

import com.greencross.greencare.bluetooth.model.BloodModel;
import com.greencross.greencare.util.CDateUtil;

import java.util.Calendar;
import java.util.TimerTask;
import java.util.UUID;

/**
 * Created by jongrakmoon on 2017. 3. 2..
 */

public class BloodDevice extends BaseDevice<SparseArray<BloodModel>> {

    private final String TAG = BloodDevice.class.getSimpleName();

    //Service
    public final static UUID BLE_SERVICE_GLUCOSE = UUID.fromString("00001808-0000-1000-8000-00805f9b34fb");
    public final static UUID BLE_SERVICE_DEVICE_INFO = UUID.fromString("0000180A-0000-1000-8000-00805f9b34fb");
    public final static UUID BLE_SERVICE_CUSTOM = UUID.fromString("0000FFF0-0000-1000-8000-00805f9b34fb");
    //Characteristic
    public final static UUID BLE_CHAR_GLUCOSE_SERIALNUM = UUID.fromString("00002A25-0000-1000-8000-00805f9b34fb");
    public final static UUID BLE_CHAR_SOFTWARE_REVISION = UUID.fromString("00002A28-0000-1000-8000-00805f9b34fb");
    public final static UUID BLE_CHAR_GLUCOSE_MEASUREMENT = UUID.fromString("00002A18-0000-1000-8000-00805f9b34fb");
    public final static UUID BLE_CHAR_GLUCOSE_CONTEXT = UUID.fromString("00002A34-0000-1000-8000-00805f9b34fb");
    public final static UUID BLE_CHAR_GLUCOSE_RACP = UUID.fromString("00002A52-0000-1000-8000-00805f9b34fb");
    public final static UUID BLE_CHAR_CUSTOM_TIME = UUID.fromString("0000FFF1-0000-1000-8000-00805f9b34fb");
    public final static UUID BLE_CHAR_CUSTOM_TIME_MC = UUID.fromString("01020304-0506-0708-0900-0A0B0C0D0E0F");
    //Descriptor
    public final static UUID BLE_DESCRIPTOR_DESCRIPTOR = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    private final static int FILTER_TYPE_SEQUENCE_NUMBER = 1;
    private final static int OP_CODE_REPORT_STORED_RECORDS = 1;
    private final static int OP_CODE_REPORT_NUMBER_OF_RECORDS = 4;
    private final static int OP_CODE_NUMBER_OF_STORED_RECORDS_RESPONSE = 5;
    private final static int OPERATOR_GREATER_OR_EQUAL_RECORDS = 3;
    private final static int OPERATOR_ALL_RECORDS = 1;
    private final static int OP_CODE_RESPONSE_CODE = 6;

    private final static int RESPONSE_SUCCESS = 1;
    private final static int RESPONSE_OP_CODE_NOT_SUPPORTED = 2;
    private final static int RESPONSE_NO_RECORDS_FOUND = 6;
    private final static int RESPONSE_ABORT_UNSUCCESSFUL = 7;
    private final static int RESPONSE_PROCEDURE_NOT_COMPLETED = 8;

    private BluetoothGatt mBluetoothGatt;

    private BluetoothGattCharacteristic mGlucoseMeasurementCharacteristic;
    private BluetoothGattCharacteristic mGlucoseMeasurementContextCharacteristic;
    private BluetoothGattCharacteristic mRACPCharacteristic;
    private BluetoothGattCharacteristic mDeviceSerialCharacteristic;
    private BluetoothGattCharacteristic mDeviceSoftwareRevisionCharacteristic;
    private BluetoothGattCharacteristic mCustomTimeCharacteristic;

    private OnBluetoothListener<SparseArray<BloodModel>> listener;

    private int lastDataIndex;

    public BloodDevice(BluetoothDevice bluetoothDevice) {
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
        if (mCustomTimeCharacteristic.getUuid().equals(BLE_CHAR_CUSTOM_TIME_MC))
            setOpCode(mRACPCharacteristic, OP_CODE_REPORT_NUMBER_OF_RECORDS, OPERATOR_GREATER_OR_EQUAL_RECORDS, lastDataIndex);
        else
            setOpCode(mRACPCharacteristic, OP_CODE_REPORT_STORED_RECORDS, OPERATOR_GREATER_OR_EQUAL_RECORDS, lastDataIndex);
        return mBluetoothGatt.writeCharacteristic(mRACPCharacteristic);
    }

    private boolean readDataFrom(int index) {
        if (mCustomTimeCharacteristic.getUuid().equals(BLE_CHAR_CUSTOM_TIME_MC))
            setOpCode(mRACPCharacteristic, OP_CODE_REPORT_NUMBER_OF_RECORDS, OPERATOR_GREATER_OR_EQUAL_RECORDS, index);
        else
            setOpCode(mRACPCharacteristic, OP_CODE_REPORT_STORED_RECORDS, OPERATOR_GREATER_OR_EQUAL_RECORDS, index);
        return mBluetoothGatt.writeCharacteristic(mRACPCharacteristic);
    }

    public boolean readDataAll() {
        if (mRACPCharacteristic != null) {
            setOpCode(mRACPCharacteristic, OP_CODE_REPORT_STORED_RECORDS, OPERATOR_ALL_RECORDS);
            return mBluetoothGatt.writeCharacteristic(mRACPCharacteristic);
        } else {
            return false;
        }
    }

    public int getLastDataIndex() {
        return lastDataIndex;
    }


    public void setOnBluetoothListener(OnBluetoothListener<SparseArray<BloodModel>> listener) {
        this.listener = listener;
    }

    public OnBluetoothListener<SparseArray<BloodModel>> getOnBluetoothListener() {
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
                for (BluetoothGattService service : gatt.getServices()) {
                    if (BLE_SERVICE_GLUCOSE.equals(service.getUuid())) { //1808
                        mGlucoseMeasurementCharacteristic = service.getCharacteristic(BLE_CHAR_GLUCOSE_MEASUREMENT); //2A18
                        mGlucoseMeasurementContextCharacteristic = service.getCharacteristic(BLE_CHAR_GLUCOSE_CONTEXT); //2A34
                        mRACPCharacteristic = service.getCharacteristic(BLE_CHAR_GLUCOSE_RACP);//2A52
                    } else if (BLE_SERVICE_DEVICE_INFO.equals(service.getUuid())) { //180A
                        mDeviceSerialCharacteristic = service.getCharacteristic(BLE_CHAR_GLUCOSE_SERIALNUM);//2A25
                        mDeviceSoftwareRevisionCharacteristic = service.getCharacteristic(BLE_CHAR_SOFTWARE_REVISION); //2A28
                    } else if (BLE_SERVICE_CUSTOM.equals(service.getUuid())) {//FFF0
                        mCustomTimeCharacteristic = service.getCharacteristic(BLE_CHAR_CUSTOM_TIME);//FFF1
                        if (mCustomTimeCharacteristic != null)
                            gatt.setCharacteristicNotification(mCustomTimeCharacteristic, true);
                    }
                }
                // Validate the device for required characteristics
                if (mGlucoseMeasurementCharacteristic == null || mRACPCharacteristic == null) {
                    gatt.close();
                    return;
                }
                readDeviceSoftwareRevision(gatt);
            } else {
                Log.d(TAG, "Service Discover Fail");
                gatt.close();
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.d(TAG, "onCharacteristicRead");

            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (characteristic.getUuid().equals(BLE_CHAR_SOFTWARE_REVISION)) {
                    readDeviceSerial(gatt);
                } else if (characteristic.getUuid().equals(BLE_CHAR_GLUCOSE_SERIALNUM)) {
                    enableRecordAccessControlPointIndication(gatt);
                }
            }
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            Log.d(TAG, "onDescriptorWrite:" + descriptor.getUuid());

            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, descriptor.getUuid().toString());
                if (BLE_CHAR_GLUCOSE_MEASUREMENT.equals(descriptor.getCharacteristic().getUuid())) { //2A18
                    Log.d(TAG, "BLE_CHAR_GLUCOSE_MEASUREMENT");
                    enableGlucoseContextNotification(gatt);
                }
                if (BLE_CHAR_GLUCOSE_CONTEXT.equals(descriptor.getCharacteristic().getUuid())) { //2A34
                    Log.d(TAG, "BLE_CHAR_GLUCOSE_CONTEXT");

                    enableTimeSyncIndication(gatt);
                }
                if (BLE_CHAR_GLUCOSE_RACP.equals(descriptor.getCharacteristic().getUuid())) { //2A52
                    Log.d(TAG, "BLE_CHAR_GLUCOSE_RACP");

                    enableGlucoseMeasurementNotification(gatt);
                }
                if (BLE_CHAR_CUSTOM_TIME.equals(descriptor.getCharacteristic().getUuid())) { //FFF1
                    Log.d(TAG, "BLE_CHAR_CUSTOM_TIME");
                    getSequenceNumber();
                }
            }
        }

        private SparseArray<BloodModel> readData = new SparseArray<>();

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            Log.d(TAG, "onCharacteristicChanged");
            Log.d(TAG, characteristic.getUuid().toString());
            if (characteristic.getUuid().equals(BLE_CHAR_GLUCOSE_MEASUREMENT)) {
                BloodModel model = readCharacteristic(characteristic);
                readData.put(model.getSequenceNumber(), model);
            } else if (characteristic.getUuid().equals(BLE_CHAR_GLUCOSE_CONTEXT)) {
                BloodModel model = readCharacteristic2(characteristic);
                readData.put(model.getSequenceNumber(), model);
            } else if (characteristic.getUuid().equals(BLE_CHAR_GLUCOSE_RACP)) { // Record Access Control Point characteristic 2A52
                int offset = 0;
                final int opCode = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset);
                Log.d(TAG, "opcode:" + opCode);
                offset += 2; // skip the operator

                if (opCode == OP_CODE_RESPONSE_CODE) { //C0
                    final int responseCode = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset + 1);

                    switch (responseCode) {
                        case RESPONSE_SUCCESS:
                            setCurrentData(readData);
                            readData = new SparseArray<>();
                            if (listener != null) {
                                mUIHandler.post(new TimerTask() {
                                    @Override
                                    public void run() {
                                        listener.onReceivedData(getCurrentData());
                                    }
                                });
                            }
                            break;
                        case RESPONSE_NO_RECORDS_FOUND:
                            break;
                        case RESPONSE_OP_CODE_NOT_SUPPORTED:
                            break;
                        case RESPONSE_PROCEDURE_NOT_COMPLETED:
                            break;
                        case RESPONSE_ABORT_UNSUCCESSFUL:
                        default:
                            break;
                    }
                } else if (opCode == OP_CODE_NUMBER_OF_STORED_RECORDS_RESPONSE) {
                    lastDataIndex = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, offset);
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

        private BloodModel readCharacteristic(BluetoothGattCharacteristic characteristic) {
            int offset = 0;
            final int flags = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset);
            offset += 1;

            final boolean timeOffsetPresent = (flags & 0x01) > 0;
            final boolean typeAndLocationPresent = (flags & 0x02) > 0;
            final boolean sensorStatusAnnunciationPresent = (flags & 0x08) > 0;
            final boolean contextInfoFollows = (flags & 0x10) > 0;

            int sequenceNumber = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, offset);

            BloodModel bloodModel = readData.get(sequenceNumber);
            if (bloodModel == null) {
                bloodModel = new BloodModel();
            }

            offset += 2;

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, offset));
            offset += 2;
            calendar.set(Calendar.MONTH, characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset) - 1);
            offset++;
            calendar.set(Calendar.DATE, characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset));
            offset++;
            calendar.set(Calendar.HOUR_OF_DAY, characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset));
            offset++;
            calendar.set(Calendar.MINUTE, characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset));
            offset++;
            calendar.set(Calendar.SECOND, characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset));
            offset++;

            if (timeOffsetPresent) {
                int timeoffset = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_SINT16, offset);
                calendar.setTimeInMillis(calendar.getTimeInMillis() + (timeoffset * 60));
                offset += 2;
            }

            bloodModel.setTime(calendar.getTime());
            bloodModel.setIdx(CDateUtil.getForamtyyMMddHHmmssSS(calendar.getTime()));

            if (typeAndLocationPresent) {
                byte[] value = characteristic.getValue();
                float sugar = bytesToFloat(value[offset], value[offset + 1]);
                bloodModel.setSugar(sugar);
                final int typeAndLocation = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset + 2);
                int type = (typeAndLocation & 0xF0) >> 4;
                offset += 3;
            }

            if (sensorStatusAnnunciationPresent) {
                int hiLow = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, offset);
                if (hiLow == 64) {
                    bloodModel.setSugar(BloodModel.FLAG_SUGAR_LOW);
                } else if (hiLow == 32) {
                    bloodModel.setSugar(BloodModel.FLAG_SUGAR_HIGH);
                }
            }
            bloodModel.setSequenceNumber(sequenceNumber);
            return bloodModel;

        }

        private BloodModel readCharacteristic2(BluetoothGattCharacteristic characteristic) {
            Log.d(TAG, "식단데이터 가져오기 시작");
            int offset = 0;
            final int flags = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset);
            offset += 1;

            final boolean carbohydratePresent = (flags & 0x01) > 0;
            final boolean mealPresent = (flags & 0x02) > 0;
            final boolean moreFlagsPresent = (flags & 0x80) > 0;

            final int sequenceNumber = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, offset);
            offset += 2;

            if (moreFlagsPresent) offset += 1;

            if (carbohydratePresent) offset += 3;

            final int meal = mealPresent ? characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset) : -1;

            BloodModel bloodModel = readData.get(sequenceNumber);
            if (bloodModel == null) {
                bloodModel = new BloodModel();
            }
            bloodModel.setEatType(meal);

            return bloodModel;
        }

        private void readDeviceSoftwareRevision(final BluetoothGatt gatt) {
            gatt.readCharacteristic(mDeviceSoftwareRevisionCharacteristic);
        }

        private void readDeviceSerial(final BluetoothGatt gatt) {
            gatt.readCharacteristic(mDeviceSerialCharacteristic);
        }

        private void enableGlucoseMeasurementNotification(final BluetoothGatt gatt) {
            if (mGlucoseMeasurementCharacteristic == null) return;

            gatt.setCharacteristicNotification(mGlucoseMeasurementCharacteristic, true);
            final BluetoothGattDescriptor descriptor = mGlucoseMeasurementCharacteristic.getDescriptor(BLE_DESCRIPTOR_DESCRIPTOR);
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            gatt.writeDescriptor(descriptor);
        }

        private void enableGlucoseContextNotification(final BluetoothGatt gatt) {
            if (mGlucoseMeasurementContextCharacteristic == null) return;

            gatt.setCharacteristicNotification(mGlucoseMeasurementContextCharacteristic, true);
            final BluetoothGattDescriptor descriptor = mGlucoseMeasurementContextCharacteristic.getDescriptor(BLE_DESCRIPTOR_DESCRIPTOR);
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            gatt.writeDescriptor(descriptor);
        }

        private void enableRecordAccessControlPointIndication(final BluetoothGatt gatt) {
            if (mRACPCharacteristic == null) return;

            gatt.setCharacteristicNotification(mRACPCharacteristic, true);
            final BluetoothGattDescriptor descriptor = mRACPCharacteristic.getDescriptor(BLE_DESCRIPTOR_DESCRIPTOR);
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
            gatt.writeDescriptor(descriptor);
        }

        private void enableTimeSyncIndication(final BluetoothGatt gatt) {
            if (mCustomTimeCharacteristic == null) return;

            gatt.setCharacteristicNotification(mCustomTimeCharacteristic, true);
            final BluetoothGattDescriptor descriptor = mCustomTimeCharacteristic.getDescriptor(BLE_DESCRIPTOR_DESCRIPTOR);
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            gatt.writeDescriptor(descriptor);
        }

        private boolean getSequenceNumber() {
            if (mRACPCharacteristic != null) {
                setOpCode(mRACPCharacteristic, OP_CODE_REPORT_NUMBER_OF_RECORDS, OPERATOR_ALL_RECORDS);
                return mBluetoothGatt.writeCharacteristic(mRACPCharacteristic);
            } else {
                return false;
            }
        }

        private float bytesToFloat(byte b0, byte b1) {
            return (float) unsignedByteToInt(b0) + ((unsignedByteToInt(b1) & 0x0F) << 8);

        }

        private int unsignedByteToInt(byte b) {
            return b & 0xFF;
        }
    };

    private void setOpCode(final BluetoothGattCharacteristic characteristic, final int opCode, final int operator, final Integer... params) {
        final int size = 2 + ((params.length > 0) ? 1 : 0) + params.length * 2; // 1 byte for opCode, 1 for operator, 1 for filter type (if parameters exists) and 2 for each parameter
        characteristic.setValue(new byte[size]);

        int offset = 0;
        characteristic.setValue(opCode, BluetoothGattCharacteristic.FORMAT_UINT8, offset);
        offset += 1;

        characteristic.setValue(operator, BluetoothGattCharacteristic.FORMAT_UINT8, offset);
        offset += 1;

        if (params.length > 0) {
            characteristic.setValue(FILTER_TYPE_SEQUENCE_NUMBER, BluetoothGattCharacteristic.FORMAT_UINT8, offset);
            offset += 1;

            for (final Integer i : params) {
                characteristic.setValue(i, BluetoothGattCharacteristic.FORMAT_UINT16, offset);
                offset += 2;
            }
        }
    }
}
