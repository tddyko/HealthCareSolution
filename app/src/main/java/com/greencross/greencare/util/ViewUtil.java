package com.greencross.greencare.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.greencross.greencare.R;
import com.greencross.greencare.base.value.Define;

import java.io.File;

/**
 * Created by mrsohn on 2017. 3. 21..
 */

public class ViewUtil {

    private static final String TAG = ViewUtil.class.getSimpleName();

    public static void setCFontTypeface(Context context, AttributeSet attrs, View view) {

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.TextViewWithFont,
                0, 0);

        String typeface = typedArray.getString(R.styleable.TextViewWithFont_font);
        if (typeface != null) {
            // Logger.i("", "typeface="+typeface);
            try {
                Typeface tf = Typeface.createFromAsset(context.getAssets(), typeface);
                setTypeface(tf, view);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            setTypefaceKelsonSansRegular(context, view);
        }
    }

    public static void setTypeface(Typeface typeface, View view) {
        if (view instanceof TextView) {
            if (((TextView) view).getTypeface() == null)
                ((TextView) view).setTypeface(typeface);
        } else if (view instanceof EditText) {
            if (((EditText) view).getTypeface() == null)
                ((EditText) view).setTypeface(typeface);
        } else if (view instanceof Button) {
            if (((Button) view).getTypeface() == null)
                ((Button) view).setTypeface(typeface);
        } else if (view instanceof RadioButton) {
            if (((RadioButton) view).getTypeface() == null)
                ((RadioButton) view).setTypeface(typeface);
        } else if (view instanceof CheckBox) {
            if (((TextView) view).getTypeface() == null)
                ((TextView) view).setTypeface(typeface);
        } else if (view instanceof CheckedTextView) {
            if (((CheckedTextView) view).getTypeface() == null)
                ((CheckedTextView) view).setTypeface(typeface);
        } else if (view instanceof ToggleButton) {
            if (((ToggleButton) view).getTypeface() == null)
                ((ToggleButton) view).setTypeface(typeface);
        }
    }

    /**
     * Kelson-Sans-Bold.otf
     *
     * @param context
     * @param view
     */
    public static void setTypefaceKelsonSansBold(Context context, View view) {
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), context.getString(R.string.KelsonSansBold));//Kelson-Sans-Bold.otf");
        setTypeface(typeface, view);
    }

    /**
     * Kelson-Sans-Light.otf
     *
     * @param context
     * @param view
     */
    public static void setTypefaceKelsonSansLight(Context context, View view) {
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), context.getString(R.string.KelsonSansLight));//"Kelson-Sans-Light.otf");
        setTypeface(typeface, view);
    }

    /**
     * Kelson-Sans-Regular.otf
     *
     * @param context
     * @param view
     */
    public static void setTypefaceKelsonSansRegular(Context context, View view) {
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), context.getString(R.string.KelsonSansRegular));//"Kelson-Sans-Regular.otf");
        setTypeface(typeface, view);
    }

    /**
     * NotoSansKR-Bold.otf
     *
     * @param context
     * @param view
     */
    public static void setTypefaceNotoSansKRBold(Context context, View view) {
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), context.getString(R.string.NotoSansKRBold));//"NotoSansKR-Bold.otf");
        setTypeface(typeface, view);
    }

    /**
     * NotoSansKR-Medium.otf
     *
     * @param context
     * @param view
     */
    public static void setTypefaceNotoSansKRMedium(Context context, View view) {
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), context.getString(R.string.NotoSansKRMedium));//"NotoSansKR-Medium.otf");
        setTypeface(typeface, view);
    }

    /**
     * NotoSansKR-Regular.otf
     *
     * @param context
     * @param view
     */
    public static void setTypefaceNotoSansKRRegular(Context context, View view) {
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), context.getString(R.string.NotoSansKRRegular));//"NotoSansKR-Regular.otf");
        setTypeface(typeface, view);
    }


    /**
     * 이미지 URL에서 이미지를 bitmap로 가져온 후 ImageView 에 세팅
     *
     * @param idx
     * @param iv
     */
    public static void getIndexToImageData(final String idx, final ImageView iv) {
        if (TextUtils.isEmpty(idx)) {
            Logger.e(TAG, "getIndexToImageData idx is null");
            return;
        }

        // 로컬에서 받아오기
        Bitmap bitmap;
        try {
            String path = Define.getFoodPhotoPath(idx);
            Logger.i(TAG, "getIndexToImageData.path="+path);
            File imgFile = new File(path);
            if (imgFile.exists()) {
                bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                iv.setImageBitmap(bitmap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
