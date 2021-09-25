package com.greencross.greencare.sample;

/**
 * Created by MrsWin on 2017-03-06.
 */

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.greencross.greencare.R;
import com.greencross.greencare.base.BaseFragment;
import com.greencross.greencare.util.FileUtil;
import com.greencross.greencare.util.IntentUtil;
import com.greencross.greencare.util.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;

public class CameraExamFragment extends BaseFragment {
    private final String TAG = CameraExamFragment.class.getSimpleName();

    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    private final int REQUEST_IMAGE_CAPTURE = 111;
    private final int REQUEST_IMAGE_ALBUM = 222;
    private final int REQUEST_IMAGE_CROP = 333;

    private Uri contentUri;

    private ImageView mSampleimageview;

    public CameraExamFragment() {
    }

    public static Fragment newInstance() {
        CameraExamFragment fragment = new CameraExamFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sample_camera_layout, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSampleimageview = (ImageView) view.findViewById(R.id.sample_image_view);
        view.findViewById(R.id.sample_camera_button).setOnClickListener(mOnClickListener);
        view.findViewById(R.id.sample_gallery_button).setOnClickListener(mOnClickListener);
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int vId = v.getId();
            if (vId == R.id.sample_camera_button) {

                    // 다운로드 폴더
                Uri uri = Uri.fromFile(FileUtil.getPublicDownloadFile(".tmp_image.png"));
                File saveFile = new File(uri.getPath());
                contentUri = Uri.fromFile(saveFile);

                IntentUtil.requestCarmeraImage(CameraExamFragment.this, REQUEST_IMAGE_CAPTURE);
            } else if (vId == R.id.sample_gallery_button) {
                selectImage();
            }
        }
    };


    public void selectImage() {
        //버튼 클릭시 처리로직
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_ALBUM);
    }

    /**
     * EXIF정보를 회전각도로 변환하는 메서드
     *
     * @param exifOrientation EXIF 회전각
     * @return 실제 각도
     */
    public int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    /**
     * 이미지를 회전시킵니다.
     *
     * @param bitmap  비트맵 이미지
     * @return 회전된 이미지
     */
    public Bitmap rotate(Bitmap bitmap, String path) {

        // 이미지를 상황에 맞게 회전시킨다
        try {
            ExifInterface exif = new ExifInterface(path);
            int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            int degrees = exifOrientationToDegrees(exifOrientation);

            Bitmap retBitmap = bitmap;

            if (degrees != 0 && bitmap != null) {
                Matrix m = new Matrix();
                m.setRotate(degrees, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);

                try {
                    Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
                    if (bitmap != converted) {
                        retBitmap = converted;
                        bitmap.recycle();
                        bitmap = null;
                    }
                } catch (OutOfMemoryError ex) {
                    // 메모리가 부족하여 회전을 시키지 못할 경우 그냥 원본을 반환합니다.
                }
            }
            return retBitmap;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public String saveBitmapToFile(Bitmap bitmap, String name) {
        File tempFile = new File(name);
        try {
            tempFile.createNewFile(); // 파일을 생성해주고
            FileOutputStream out = new FileOutputStream(tempFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // 넘거 받은 bitmap을 png로 저장해줌
            out.close(); // 마무리로 닫아줍니다.
        } catch (IOException e) {
            e.printStackTrace();
        }
        Logger.i(TAG, "mFilePath="+tempFile.getAbsolutePath());
        return tempFile.getAbsolutePath(); // 임시파일 저장경로를 리턴해주면 끝!
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_ALBUM) {
            if (data != null) {
                contentUri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), contentUri);
                    bitmap = rotate(bitmap, contentUri.getPath());
                    cropImage(contentUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), contentUri);
                bitmap = rotate(bitmap, contentUri.getPath());
                saveBitmapToFile(bitmap, contentUri.getPath());
                cropImage(contentUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == REQUEST_IMAGE_CROP) {
            if (data != null) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    Bitmap bitmap = (Bitmap) extras.get("data");
                    Logger.i(TAG, "REQUEST_IMAGE_CROP.contentUri");
                    mSampleimageview.setImageBitmap(bitmap);
                }
            }
        }
    }

    private void cropImage(Uri contentUri) {
        if (contentUri != null) {
            File file = new File(contentUri.toString());
            Logger.i(TAG, "cropImage.exists=" + file.exists());
            Logger.i(TAG, "cropImage.isFile=" + file.isFile());
            Logger.i(TAG, "cropImage.length=" + file.length());
        }
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        // indicate image type and Uri of image
        cropIntent.setDataAndType(contentUri, "image/*");
        // set crop properties
        cropIntent.putExtra("crop", "true");
        // indicate aspect of desired crop
        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("aspectY", 1);
        // indicate output X and Y
        cropIntent.putExtra("outputX", 250);
        cropIntent.putExtra("outputY", 250);
        // retrieve data on return
        cropIntent.putExtra("return-data", true);
        startActivityForResult(cropIntent, REQUEST_IMAGE_CROP);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }
}