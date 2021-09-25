package com.greencross.greencare.util;

/**
 * Created by mrsohn on 2017. 3. 29..
 */
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

/**
 * Created by mrsohn on 2017. 3. 8..
 */

public class PermissionUtil {
    public static boolean checkPermissions(Context context) {
        int permissionState = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }
}
