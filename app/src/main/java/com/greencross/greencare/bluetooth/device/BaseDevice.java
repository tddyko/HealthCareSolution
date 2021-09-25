package com.greencross.greencare.bluetooth.device;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.greencross.greencare.R;

/**
 * Created by jongrakmoon on 2017. 3. 2..
 */

public abstract class BaseDevice<D> {
    public static String HEART_RATE_MEASUREMENT = "0000fff1-0000-1000-8000-00805f9b34fb";
    private D currentData;

    protected Handler mUIHandler;

    protected BluetoothDevice bluetoothDevice;

    protected BaseDevice(BluetoothDevice bluetoothDevice) {
        mUIHandler = new Handler(Looper.getMainLooper());
        this.bluetoothDevice = bluetoothDevice;
    }

    public abstract void connect(Context context);

    public abstract void disconnect();

    public abstract boolean readData();

    public D getCurrentData() {
        return this.currentData;
    }

    public void setCurrentData(D data) {
        this.currentData = data;
    }


    public interface OnBluetoothListener<M> {
        public void onStart();

        public void onConnected();

        public void onDisConnected();

        public void onReceivedData(M dataModel);
    }


    public enum Gender {
        MAN(true), WOMAN(false);
        private boolean value;

        Gender(boolean value) {
            this.value = value;
        }

        public boolean getValue() {
            return this.value;
        }

        @Override
        public String toString() {
            return value ? "남자" : "여자";
        }
    }
}
