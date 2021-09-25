package com.greencross.greencare.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URISyntaxException;
import java.util.List;

/**
 * 작업일 : 2016. 11. 15.
 */
public class IntentUtil {
    private static final String TAG = IntentUtil.class.getSimpleName();

    /**
     * 일부런처에서만 사용가능한 런처 뱃지 표시 (비표준 동작임으로 사용에 제한이 있음)
     *
     * @param context Context
     * @param count int
     */

    /**
     * 라인으로 이미지 보내기
     *
     * @param activity Activity
     * @param filepath 전송 이미지 파일 경로
     */
    public static void sendToLineImage(Activity activity, String filepath) {
        if (activity == null || TextUtils.isEmpty(filepath))
            return;

        String url = "line://msg/image" + filepath;
        Logger.e(TAG, "sendToLineImage - " + url);

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        activity.startActivity(intent);
    }

    /**
     * 라인으로 메시지 보내기
     *
     * @param activity Activity
     * @param message  전송 메시지
     */
    public static void sendToLineMessage(Activity activity, String message) {
        if (activity == null || TextUtils.isEmpty(message))
            return;

        String url = "line://msg/text/" + message;
        Logger.e(TAG, "sendToLineMessage - " + url);

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        activity.startActivity(intent);
    }

    /**
     * 이미지 가져오기
     *
     * @param activity    Activity
     * @param requestCode int
     */
    public static void pickImageFromActivity(Activity activity, int requestCode) {
        pickImage(activity, null, requestCode);
    }


    /**
     * 이미지 가져오기
     *
     * @param fragment    Fragment
     * @param requestCode int
     */
    public static void pickImageFromFragment(Fragment fragment, int requestCode) {
        pickImage(null, fragment, requestCode);
    }


    private static void pickImage(Activity activity, Fragment fragment, int requestCode) {
        Intent intent = null;

        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }

        intent.setType("image/*");

        if (activity != null) {
            activity.startActivityForResult(intent, requestCode);
        } else if (fragment != null) {
            fragment.startActivityForResult(intent, requestCode);
        }
    }


    @SuppressLint("NewApi")
    public static String getFilePath(Context context, Uri uri) throws URISyntaxException {
        String selection = null;
        String[] selectionArgs = null;
        if (Build.VERSION.SDK_INT >= 19 && DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[]{
                        split[1]
                };
            }
        }

        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {
                    MediaStore.Images.Media.DATA
            };
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver()
                        .query(uri, projection, selection, selectionArgs, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }


        return null;
    }

    public static String findHomeLauncher(Context context) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        ResolveInfo resolveInfo = context.getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (resolveInfo != null && resolveInfo.activityInfo != null)
            return resolveInfo.activityInfo.packageName;
        else
            return null;
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }


    public static void requestPhoneDialActivity(Context context, String telno) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + telno));
        context.startActivity(intent);
    }

    /**
     * @param context Context
     * @param intent  Intent
     * @return 호출인텐트에 대한 응답 Activity가 있는지 체크
     */
    public static boolean checkIntentAvailable(Context context, Intent intent) {
        if (intent == null || context == null)
            return false;

        try {
            return intent.resolveActivity(context.getPackageManager()) != null;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isLaunchedFromHistory(Intent intent) {
        if (intent == null)
            return false;

        return (intent.getFlags() & Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) == Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY;
    }


    public static void shareImageFile(Context context, String filePath) {
        File file = new File(filePath);

        if (!file.exists()) {
            Toast.makeText(context, "파일이 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        } else {
            Uri uri = Uri.fromFile(file);
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.setType("image/png");
            context.startActivity(Intent.createChooser(shareIntent, "공유하기"));
        }
    }

    public static boolean isIntentAvailable(Context context, String action) {
        PackageManager pm = context.getPackageManager();
        Intent intent = new Intent(action);

        List<ResolveInfo> list = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    // requestCarmeraImage / requestCropImage에 사용됨
    public static Uri getTempImageUri() {
        return Uri.fromFile(FileUtil.getPublicDownloadFile(".tmp_image.png"));
    }

    public static File getTempImageFile() {
        return new File(getTempImageUri().getPath());
    }

    public static void requestCarmeraImage(Fragment fragment, int requestCode) {
        String action = MediaStore.ACTION_IMAGE_CAPTURE;

        Intent takePictureIntent = new Intent(action);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, getTempImageUri());

        if (isIntentAvailable(fragment.getContext(), action)) {
            fragment.startActivityForResult(takePictureIntent, requestCode);
        } else {
            Toast.makeText(fragment.getContext(), "카메라 실행에 실패 하였습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    public static void requestCarmeraImage(Activity activity, int requestCode) {
        String action = MediaStore.ACTION_IMAGE_CAPTURE;

        Intent takePictureIntent = new Intent(action);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, getTempImageUri());

        if (isIntentAvailable(activity, action)) {
            activity.startActivityForResult(takePictureIntent, requestCode);
        } else {
            Toast.makeText(activity, "카메라 실행에 실패 하였습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean requestCropImage(Fragment fragment, int requestCode, int width, int height, int aspectX, int aspectY, boolean scaleable) {
        if (getTempImageFile().exists() == false) {
            Logger.e("requestCropImage", "file not found");
            return false;
        }

        String action = "com.android.camera.action.CROP";

        Intent intent = new Intent(action);
        intent.setDataAndType(getTempImageUri(), "image/*");

        intent.putExtra("outputX", width);
        intent.putExtra("outputY", height);
        intent.putExtra("aspectX", aspectX);
        intent.putExtra("aspectY", aspectY);

        intent.putExtra("scale", scaleable);
        intent.putExtra("return-data", true);

        if (isIntentAvailable(fragment.getContext(), action)) {
            fragment.startActivityForResult(intent, requestCode);
            return true;
        } else {
            return false;
        }
    }

    public static void processCropResult(Intent data, String filePath) {
        Bundle bundle = data.getExtras();

        if (bundle != null) {
            Bitmap photo = bundle.getParcelable("data");
            storeCropImage(photo, filePath);
        }

        File file = getTempImageFile();
        if (file.exists())
            file.delete();

    }

    public static boolean storeCropImage(Bitmap bitmap, String filePath) {
        File copyFile = new File(filePath);
        BufferedOutputStream bos = null;

        boolean retVal = true;
        try {
            copyFile.createNewFile();

            bos = new BufferedOutputStream(new FileOutputStream(copyFile));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
            bos.flush();
        } catch (Exception e) {
            retVal = false;
        } finally {
            try {
                if (bos != null) bos.close();
            } catch (Exception e) {
            }
        }

        return retVal;
    }
}
