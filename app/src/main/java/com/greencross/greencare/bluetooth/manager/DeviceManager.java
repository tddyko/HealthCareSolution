package com.greencross.greencare.bluetooth.manager;

import android.content.Context;

import com.greencross.greencare.bluetooth.fragment.DeviceManagementFragment;


/**
 * Created by jongrakmoon on 2017. 3. 2..
 */

public class DeviceManager {
    public static final String DEVICE_TAG = "DEVICE";

    public static final String FLAG_BLE_DEVICE_WEIGHT   = "weight";
    public static final String FLAG_BLE_DEVICE_PRESSURE = "pressure";
    public static final String FLAG_BLE_DEVICE_BLOOD    = "blood";
    public static final String FLAG_BLE_DEVICE_BAND     = "band";

    public static String deviceNameToFlag(String deviceName) {
        if (deviceName.equals(DeviceManagementFragment.BLE_DEVICE_WEIGHT)) {
            return FLAG_BLE_DEVICE_WEIGHT;
        } else if (deviceName.equals(DeviceManagementFragment.BLE_DEVICE_PRESSURE)) {
            return FLAG_BLE_DEVICE_PRESSURE;
        } else if (deviceName.equals(DeviceManagementFragment.BLE_DEVICE_BLOOD)) {
            return FLAG_BLE_DEVICE_BLOOD;
        } else if (deviceName.equals(DeviceManagementFragment.BLE_DEVICE_BAND)) {
            return FLAG_BLE_DEVICE_BAND;
        } else {
            return null;
        }
    }

    public static String getRegDeviceAddress(Context context, String flag) {
        return context.getSharedPreferences(DEVICE_TAG, Context.MODE_PRIVATE).getString(flag, null);
    }

    public static boolean isRegDevice(Context context, String flag) {
        return context.getSharedPreferences(DEVICE_TAG, Context.MODE_PRIVATE).getString(flag, null) != null;
    }

    public static void removeRegDeviceAddress(Context context, String flag) {
        context.getSharedPreferences(DEVICE_TAG, Context.MODE_PRIVATE)
                .edit()
                .remove(flag)
                .apply();
    }

    public static void regDeviceAddress(Context context, String name, String address) {
        context.getSharedPreferences(DEVICE_TAG, Context.MODE_PRIVATE)
                .edit()
                .putString(deviceNameToFlag(name), address)
                .apply();
    }
}
