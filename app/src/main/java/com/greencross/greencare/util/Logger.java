package com.greencross.greencare.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * t stock Logger
 *
 * @author shinys
 */
public class Logger {
    private static final String TAG = "GreenCare";

    private static final int MAX_SAVE_SIZE = 5000000; // 5M
    private static final SimpleDateFormat DateForm = new SimpleDateFormat("MM-dd HH:mm:ss:SSS");

    // 사용할 로그 지정
    private static boolean mIsAndroidLog = false;
    private static boolean mIsShowLog = false;

    private static String mSavePath = null;

    // 로그 설정 화면 사용 여부
    public static boolean mUseLogSetting = false;

    public static void setLogMode(boolean isAndroid, boolean isShowP) {
        mIsAndroidLog = isAndroid;
        mIsShowLog = isShowP;
    }

    public static String getSavePath() {
        return mSavePath;
    }

    /**
     * 로거 초기화
     *
     * @param context
     */
    public static void initLogger(Context context) {
        ApplicationInfo appInfo = context.getApplicationInfo();
        if (appInfo == null) {
            return;
        }

        int flags = appInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE;
        if (flags == ApplicationInfo.FLAG_DEBUGGABLE) {
            mUseLogSetting = true;
        } else {
            mUseLogSetting = false;
        }

        mIsAndroidLog = mUseLogSetting;
        mIsShowLog = mUseLogSetting;

        Log.d(TAG, "mIsAndroidLog=" + mIsAndroidLog + ", mIsShowLog=" + mIsShowLog);

        if (mSavePath == null) {
            String saveDir = "";
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                saveDir = Environment.getExternalStorageDirectory().toString();
            } else {
                saveDir = context.getApplicationInfo().dataDir;
            }
            mSavePath = saveDir + "/" + context.getPackageName() + ".log";
        }
    }

    private Logger() {
        // 일반 인스턴스 생성 불가 처리
    }

    public static int v(String tag, String msg) {
        return println(Log.VERBOSE, tag, msg);
    }

    public static int v(String tag, String msg, Throwable tr) {
        return println(Log.VERBOSE, tag, msg + '\n' + getStackTraceString(tr));
    }

    public static int d(String tag, String msg) {
        return println(Log.DEBUG, tag, msg);
    }

    public static int d(String tag, String msg, Throwable tr) {
        return println(Log.DEBUG, tag, msg + '\n' + getStackTraceString(tr));
    }

    public static int i(String tag, String msg) {
        return println(Log.INFO, tag, msg);
    }

    public static int i(String tag, String msg, Throwable tr) {
        return println(Log.INFO, tag, msg + '\n' + getStackTraceString(tr));
    }

    public static int w(String tag, String msg) {
        return println(Log.WARN, tag, msg);
    }

    public static int w(String tag, String msg, Throwable tr) {
        return println(Log.WARN, tag, msg + '\n' + getStackTraceString(tr));
    }

    public static int w(String tag, Throwable tr) {
        return println(Log.WARN, tag, getStackTraceString(tr));
    }

    public static int e(String tag, String msg) {
        return println(Log.ERROR, tag, msg);
    }

    public static void e(Exception e) {
        e.printStackTrace();
    }

    public static int e(String tag, String msg, Throwable tr) {
        return println(Log.ERROR, tag, msg + '\n' + getStackTraceString(tr));
    }

    public static String getStackTraceString(Throwable tr) {
        String retStr = "";
        if (tr == null) {
            return retStr;
        }

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        tr.printStackTrace(pw);
        retStr = sw.toString();

        pw.close();
        pw = null;
        sw = null;

        return retStr;
    }

    public static int println(int priority, String tag, String msg) {
        int ret = 0;
        if (mIsAndroidLog) {
            Log.println(priority, TAG, tag + " \t" + msg);
        }
        if (mIsShowLog) {
            printlnForShowLog(priority, tag, msg);

            rollingSaveFile();
        }
        return ret;
    }

    /**
     * 로거 파일 롤링 체크
     *
     * @return
     */
    private static boolean rollingSaveFile() {
        boolean isRolling = false;
        String savePath = getSavePath();
        if (savePath != null) {
            File file = new File(savePath);
            if (file.exists() && file.length() > MAX_SAVE_SIZE) {

                // 백업 없이 삭제
                file.delete();

                try {
                    file.createNewFile();
                } catch (IOException e) {
                }

                isRolling = true;
            }
        }
        return isRolling;
    }

    /**
     * 내부 로거
     * @param priority
     * @param tag
     * @param msg
     * @return
     */
    public static int printlnForShowLog(int priority, String tag, String msg) {
        int ret = 0;
        String priorityStr = "";
        switch (priority) {
            case Log.VERBOSE:
                priorityStr = "VERBOSE";
                break;
            case Log.INFO:
                priorityStr = "INFO";
                break;
            case Log.DEBUG:
                priorityStr = "DEBUG";
                break;
            case Log.WARN:
                priorityStr = "WARN";
                break;
            case Log.ERROR:
                priorityStr = "ERROR";
                break;
        }

        String savePath = getSavePath();
        if (savePath != null) {
            FileWriter fw = null;
            try {
                fw = new FileWriter(savePath, true);
                fw.append(DateForm.format(new Date()) + "\t");
                fw.append(priorityStr + "\t");
                fw.append(tag + "\t");
                fw.append(msg + "\r\n");
                fw.flush();
            } catch (IOException e) {
            } finally {
                if (fw != null) {
                    try {
                        fw.close();
                    } catch (IOException e) {
                    }
                }
                fw = null;
            }
        }
        // TODO 차후 적용 예정

        return ret;
    }
}
