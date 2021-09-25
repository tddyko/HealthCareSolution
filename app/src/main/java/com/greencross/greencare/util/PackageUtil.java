package com.greencross.greencare.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.util.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * 패키지 유틸
 */
public class PackageUtil {
    private static final String MARKET_URI_BASE = "market://details?id=";

    /**
     * 해당 패키지를 설치할 수 있도록 마켓으로 보냅니다.
     * @param ctx Context
     * @param packageName String
     */
    public static void installPackage(Context ctx, String packageName) {
        ctx.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(MARKET_URI_BASE + packageName)));
    }

    /**
     * 패키지 설치여부를 판단
     * @param objContext Context
     * @param packageName String
     * @return boolean
     */
    public static boolean isInstalledPackage(Context objContext, String packageName) {
        PackageManager pm = objContext.getPackageManager();
        List<ApplicationInfo> appList = pm.getInstalledApplications(0);
        ApplicationInfo app = null;
        int nSize = appList.size();
        for (int i = 0; i < nSize; ++i) {
            app = appList.get(i);
            if (app.packageName.indexOf(packageName) != -1)
                return true;
        }
        return false;
    }

    /**
     * 설치된 패키지의 버전정보 get
     * @param context Context
     * @return String
     */
    public static String getVersionInfo(Context context) {
        String strRetVal = "";
        try  {
            PackageInfo i = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            strRetVal = i.versionName;
        }
        catch (PackageManager.NameNotFoundException e) {
            return "";
        }
        catch (Exception e) {
            return "";
        }

        return strRetVal;
    }

    /**
     * 설치된 패키지의 버전코드 get
     * @param objContext Context
     * @param strPackageName String
     * @return int
     */
    public static int getVersionCode(Context objContext, String strPackageName) {
        int nRetVal = 0;
        try  {
            PackageInfo i = objContext.getPackageManager().getPackageInfo(strPackageName, 0);
            nRetVal = i.versionCode;
        }
        catch (PackageManager.NameNotFoundException e) {
            return 0;
        }
        catch (Exception e) {
            return 0;
        }

        return nRetVal;
    }

    /**
     * 설치된 패키지에 대한 서명값 SHA 값
     * @param context Context
     * @param packageName String
     * @return String
     */
    public static String getHashKeySHA(Context context, String packageName) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");

                md.update(signature.toByteArray());
                return Base64.encodeToString(md.digest(), Base64.DEFAULT).trim();
            }
        } catch (PackageManager.NameNotFoundException e) {
        } catch (NoSuchAlgorithmException e) {
        }

        return "";
    }

    /**
     * 설치된 패키지에 대한 서명값 MD5
     * @param context Context
     * @param packageName String
     * @return String
     */
    public static String getHashKeyMD5(Context context, String packageName) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("MD5");

                md.update(signature.toByteArray());
                return Base64.encodeToString(md.digest(), Base64.DEFAULT).trim();
            }
        } catch (PackageManager.NameNotFoundException e) {
        } catch (NoSuchAlgorithmException e) {
        }

        return "";
    }


    public static boolean needUpdate(Context context, String version){
        String cur = getVersionInfo(context);

        Logger.e("needUpdate", cur + "/" + version + " : " + (cur.compareTo(version) < 0));

        return cur.compareTo(version) < 0;
    }
}
