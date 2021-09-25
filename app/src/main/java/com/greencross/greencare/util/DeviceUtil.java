package com.greencross.greencare.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.UUID;

/**
 * Hunikey (2016. 6. 29.)
 */

public class DeviceUtil {
    public static final String TAG = DeviceUtil.class.getSimpleName();

    /**
     * 장치가 테블릿인지 확인
     *
     * @param ctx Context
     * @return T : 테블릿 F : 일반
     */
    public static boolean isTabletDevice(Context ctx) {
        Configuration conf = ctx.getResources().getConfiguration();
        int screenSize = conf.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
        return screenSize > Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    /**
     * 장치가 네트워크에 연결되었는지 확인
     *
     * @param context Context
     * @return boolean isOnline
     */
    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo bluetooth = cm.getNetworkInfo(ConnectivityManager.TYPE_BLUETOOTH);
        NetworkInfo lte = cm.getNetworkInfo(ConnectivityManager.TYPE_WIMAX);

        boolean isConnected = false;
        if ((wifi != null && wifi.isConnected()) || (mobile != null && mobile.isConnected()) || (bluetooth != null && bluetooth.isConnected()) || (lte != null && lte.isConnected())) {
            isConnected = true;
        }

        return isConnected;
    }

    /**
     * WIFI접속 여부 체크
     *
     * @param ctx Context
     * @return T wifi접속/F wifi접속 아님
     */
    public static boolean isWifiMode(Context ctx) {
        ConnectivityManager m_NetConnectMgr = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = m_NetConnectMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return info.isConnected();
    }

    /**
     * 3G접속인가
     *
     * @param ctx Context
     * @return T : 3g 상태/ F : 3g 아님
     */
    public static boolean is3GConnection(Context ctx) {
        ConnectivityManager m_NetConnectMgr = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = m_NetConnectMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return (info != null && info.isConnected());
    }

    /**
     * 4G접속인가
     *
     * @param ctx Context
     * @return T : 4g 상태/ F : 4g 아님
     */
    public static boolean is4GConnection(Context ctx) {
        ConnectivityManager m_NetConnectMgr = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = m_NetConnectMgr.getNetworkInfo(ConnectivityManager.TYPE_WIMAX);
        return (info != null && info.isConnected());
    }


    /**
     * 해당 APP의 versionName 가져옴
     *
     * @param ctx Context
     * @return String versionName;
     */
    public static String getAppVerion(Context ctx) {
        PackageInfo pki;
        String rtnVersion = "";
        try {
            pki = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
            rtnVersion = pki.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Logger.e(e);
        }
        return rtnVersion;
    }

    /**
     * 폰 os version
     *
     * @return String
     */
    public static String getPhoneOsVersion() {
        return Build.VERSION.RELEASE;
    }


    public static String getPhoneModelName() {
        return Build.MODEL;
    }


    /**
     * density 얻기
     *
     * @param context Context
     * @return String
     */
    public static String getDensityName(Context context) {
        float density = context.getResources().getDisplayMetrics().density;

        // density 범위로 값을 구해야 한다.
        String dpi = "xhdpi";
        if (density > 3.0)
            dpi = "xxxhdpi";
        else if (density > 2.0)
            dpi = "xxhdpi";
        else if (density > 1.5)
            dpi = "xhdpi";
        else if (density > 1.0)
            dpi = "hdpi";
        else if (density > 0.75)
            dpi = "mdpi";
        else if (density <= 0.75)
            dpi = "ldpi";

        return dpi;
    }

    /**
     * 안드로이드 6.0 대응(WifiManager 로 맥주소 추출안됨)
     * 안드로이드ID 가져오기
     *
     * @param ctx Context
     * @return 16자리 대문자 안드로이드 ID
     */
    private static String getUDIDForM(Context ctx) {
        return Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.ANDROID_ID).toUpperCase(Locale.getDefault());
    }

    /**
     * chipSerial 얻기
     *
     * @param ctx Context
     * @return String
     */
    public static String getUDID(Context ctx) {

        String chipSerial = Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.ANDROID_ID);
        return chipSerial;
    }


    /**
     * 현재 앱의 버전이름을 반환
     *
     * @return String
     * @since : 2013. 5. 9.
     */
    public static String getVersionName(Context ctx) {
        String result = "";
        try {
            result = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Logger.e(e);
        }
        return result;
    }


    /**
     * PhoneID얻기
     *
     * @return String
     */
    public static String getPhoneID(Context ctx) {
        final TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);

        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        return deviceUuid.toString();
    }

    /**
     * 폰 OS가 기준버전 이상이면 true
     *
     * @param release1 Build.VERSION.RELEASE 에 해당하는 정보
     * @return 기준버전 이상이면 true
     */
    public static boolean afterOsVersion(String release1) {
        String[] split1 = release1.split("\\.");

        String release2 = Build.VERSION.RELEASE;
        String[] split2 = release2.split("\\.");

        int len = 0;
        if (split1.length > split2.length) len = split2.length;
        else len = split1.length;

        for (int i = 0; i < len; i++) {
            if (Integer.valueOf(split1[i]) > Integer.valueOf(split2[i])) return false;
            else if (Integer.valueOf(split1[i]) < Integer.valueOf(split2[i])) return true;
        }

        if (split1.length > split2.length) return false;

        return true;
    }

    /**
     * ssoKey발급을 위해 필요한 PhoneID 구함.
     *
     * @return String sso device id
     */
    public static String getSsoPhoneID(Context ctx) {
        final TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        String result = tm.getDeviceId();
        if (result != null && !result.equals("")) {
            return result;
        } else {
            final String tmDevice, tmSerial, androidId;
            tmDevice = "" + tm.getDeviceId();
            tmSerial = "" + tm.getSimSerialNumber();
            androidId = "" + Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.ANDROID_ID);

            UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
            return deviceUuid.toString().substring(0, 15);
        }
    }

    /**
     * 지역 설정을 변경
     *
     * @param activity  변경될 Activity
     * @param character 바뀔 locale code
     */
    public static void setLocale(final Activity activity, String character) {
        Locale locale = new Locale(character);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        activity.getResources().updateConfiguration(config, activity.getResources().getDisplayMetrics());
    }

    /**
     * 뷰 그룹을 다시 그림
     *
     * @param context 현제 context
     * @param root    업데이트될 뷰 그룹
     * @throws Exception
     */
    public static void setRefreshViewGroup(Context context, ViewGroup root) throws Exception {
        for (int i = 0; i < root.getChildCount(); i++) {
            View child = root.getChildAt(i);

            if (child instanceof TextView) {
                if (child.getTag() != null) {

                    if (((TextView) child).getText() != null && ((TextView) child).getText().toString().length() > 0) {
                        int stringId = getResourceId(context, child.getTag());
                        ((TextView) child).setText(stringId);
                    }

                    if (((TextView) child).getHint() != null && ((TextView) child).getHint().toString().length() > 0) {
                        int hintId = getResourceId(context, child.getTag());
                        ((TextView) child).setHint(hintId);
                    }
                }
            } else if (child instanceof ViewGroup) setRefreshViewGroup(context, (ViewGroup) child);
        }
    }

    public static int getResourceId(Context context, Object tag) {
        return context.getResources().getIdentifier((String) tag, "string", context.getPackageName());
    }

    /**
     * 앱설치여부체크
     */
    public static boolean isAppInstalled(Context ctx, String pkgName) {
        boolean appInstalled = false;
        try {
            ctx.getPackageManager().getPackageInfo(pkgName, PackageManager.GET_ACTIVITIES);
            appInstalled = true;
        } catch (PackageManager.NameNotFoundException e) {
            Logger.e(e);
        }
        return appInstalled;
    }

    public static String getProductName() {
        return Build.PRODUCT;
    }

    /**
     * 디바이스 정보를 가져온다.
     *
     * @param ctx    Context
     * @param pageId 태블릿에서 메인/컨텐츠 웹뷰 가로사이즈 처리하기 위해 추가됨.
     * @return String
     */
    public static String getDeviceInfo(Context ctx, String pageId) {
        JSONObject result = new JSONObject();
        try {
            result.put("uuid", getUDID(ctx));
            result.put("version", getPhoneOsVersion());
            result.put("platform", "Android");
            result.put("devicetype", isTabletDevice(ctx) ? "tablet" : "phone");
            result.put("frdNm", DeviceUtil.getProductName());

            float tabletWebviewWidth = 0;

            Logger.d(TAG, "tabletWebviewWidth[" + tabletWebviewWidth + "]");
            result.put("tabletWebviewWidth", tabletWebviewWidth);

        } catch (JSONException e) {
        }

        return result.toString();
    }

    /**
     * 특정단말에서 OS Icon push count 표시 변경.
     *
     * @param ctx Context
     * @param cnt 표시할 갯수
     */
    public static void resetPushIconState(Context ctx, int cnt) {
        Intent intent_badge = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        intent_badge.putExtra("badge_count", cnt);
        // 메인메뉴에 나타나는 어플의 패키지명
        intent_badge.putExtra("badge_count_package_name", ctx.getPackageName());
        // 메인메뉴에 나타나는 어플의 클래스명
        //2015.12.23 스타뱅킹3.0 에서 붙은 뱃지 변경을 위해 수정 - T003046
        intent_badge.putExtra("badge_count_class_name", "com.greencross.UI.CIntro");
        //intent_badge.putExtra("badge_count_class_name", Intro.class.getCardName());
        ctx.sendBroadcast(intent_badge);
    }

    public static String getServiceVender(Context ctx) {
        TelephonyManager systemService = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        return systemService.getNetworkOperatorName();
    }

    /**
     * 폰번호 얻기
     *
     * @param ctx Context
     * @return String
     */
    public static String getPhoneNumber(Context ctx) {
        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        String phoneNumber = tm.getLine1Number();

        if ((phoneNumber != null) && (!phoneNumber.equals(""))) {

            boolean codeCheck = phoneNumber.startsWith("+82");
            if (codeCheck) {
                String cut = phoneNumber.substring(0, 3);
                phoneNumber = phoneNumber.replace(cut, "0");
            }

        }
        return phoneNumber;
    }

    /**
     * USIM 일련번호 가져오기
     *
     * @param cont Context
     * @return String
     */
    public static String getSimSerialNumber(Context cont) {
        TelephonyManager operator = (TelephonyManager) cont.getSystemService(Context.TELEPHONY_SERVICE);
        return operator.getSimSerialNumber();
    }

    /**
     * UICC-ID (USIM 일련번호 앞뒤 2자리씩 삭제 가져오기)
     *
     * @param cont Context
     * @return String
     */
    public static String getUiccId(Context cont) {
        TelephonyManager operator = (TelephonyManager) cont.getSystemService(Context.TELEPHONY_SERVICE);
        String uiccId = operator.getSimSerialNumber();
        if (uiccId != null && uiccId.length() >= 18) {
            uiccId = uiccId.substring(2, 18);
        }
        return uiccId;
    }

    /**
     * IMEI 값을 가져오기
     *
     * @param context 컨텍스트 정보
     * @return IMEI 값
     */
    public static String getDeviceId(Context context) {
        TelephonyManager telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telManager.getDeviceId();
    }
}
