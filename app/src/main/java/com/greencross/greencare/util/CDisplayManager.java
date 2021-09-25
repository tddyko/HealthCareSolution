package com.greencross.greencare.util;

import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;

/**
 * @author shinys
 */
public class CDisplayManager {
    private static final String TAG = CDisplayManager.class.getSimpleName();

    public static final int PORTRAIT = 1;
    public static final int LANDSCAPE = 0;
    public static final int REVERSE_LANDSCAPE = 8;

    public static final float EDGE_PADDING = 15F;                                        // dip
    public static final float INTER_PADDING = 0F;                                        // dip

    public static final int WIDE_VIEW_WIDTH_DIP = 500;
    public static final int WIDE_VIEW_MENU_DIP = 666;

    public static final int TABLET_DIP = 600;

    public static boolean isTablet(Context context) {
        boolean ret = true;
        // XXX 관심종목 가로보기 모드 제거됨

        return ret;
    }

    public static void onExit() {
    }

    public static boolean isHighResolution(Context context) {
        boolean ret = false;

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int size = (int) Math.ceil(Math.min(displayMetrics.heightPixels, displayMetrics.widthPixels) / displayMetrics.density);
        if (size >= TABLET_DIP) {
            ret = true;
        }

        return ret;
    }

    public static float getDensity(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.density;
    }

    public static int getMaxDimension(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.max(displayMetrics.widthPixels, displayMetrics.heightPixels);
    }

    public static int getMinDimension(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.min(displayMetrics.widthPixels, displayMetrics.heightPixels);
    }

    public static int getDisplayWidth(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.widthPixels;
    }

    public static int getDisplayHeight(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.heightPixels;
    }

    /**
     * T Stock 화면 가로 폭
     *
     * @param context
     * @return
     */
    public static int getTotalWidth(Context context) {
        int totalWidth = getDisplayWidth(context);
        return totalWidth;
    }

    public static int getTotalWidthDip(Context context) {
        int totalWidth = getDisplayWidth(context);
        return totalWidth;
    }

    /**
     * 화면 컨텐츠 뷰
     *
     * @param context
     * @return
     */
    public static int getContentWidth(Context context) {
        int totalWidth = getTotalWidth(context);
        return totalWidth;
    }

    public static int getContentPaddingWidth(Context context) {
        int paddingWidth = 0;
        return paddingWidth;
    }

    public static int getRightContentPaddingWidth(Context context) {
        int paddingWidth = 0;
        return paddingWidth;
    }

    public static int getContextMenuPositionX(Context context, int rawX) {
        int positionX = 0;
        return positionX;
    }

    public static int dipToPix(Context context, float dip) {
        float val = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, context.getResources().getDisplayMetrics());
        return Math.round(val);
    }

    private static final float DEFAULT_HDIP_DENSITY_SCALE = 1.5f;

    public static int pixelToDp(Context context, int pixel) {
        float scale = context.getResources().getDisplayMetrics().density;
        int dip = (int) (pixel / DEFAULT_HDIP_DENSITY_SCALE * scale);

        return dip;
    }

    public static int spToPix(Context context, float dip) {
        float val = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, dip, context.getResources().getDisplayMetrics());
        return Math.round(val);
    }

    public static int getDisplayDip(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int ret = Math.round(Math.min(displayMetrics.widthPixels, displayMetrics.heightPixels) / displayMetrics.density);
        return ret;
    }

    public static int getDisplayWidthDip(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int ret = Math.round(displayMetrics.widthPixels / displayMetrics.density);
        return ret;
    }

    public static int setTextScaleX(TextView textView) {
        int textViewWidth = textView.getWidth();
        if (textViewWidth <= 0F) {
            textViewWidth = textView.getMeasuredWidth();
        }

        setTextScaleX(textView, textViewWidth);

        return textViewWidth;
    }

    /**
     * 1/5까지 축소 가능
     *
     * @param textView
     * @param textViewWidth
     */
    public static void setTextScaleX(TextView textView, float textViewWidth) {
        if (textViewWidth <= 0F) {
            return;
        }

        float scaleX = 1.0F;
        textView.setTextScaleX(scaleX);

        String text = textView.getText().toString();
        if (text.indexOf("\n") != -1) {
            String[] tokens = text.split("\n");
            String maxStr = "";
            for (String token : tokens) {
                if (token.length() > maxStr.length()) {
                    maxStr = token;
                }
            }
            text = maxStr;
        }
        textViewWidth -= (textView.getPaddingLeft() + textView.getPaddingRight() + textView.getCompoundPaddingLeft() + textView.getCompoundPaddingRight() + textView.getCompoundDrawablePadding());

        while (textView.getPaint().measureText(text) > textViewWidth) {
            scaleX -= 0.05F;
            textView.setTextScaleX(scaleX);
            if (scaleX < 0.2F) {
                break;
            }
        }
    }


    public static boolean useWideView(Context context, int orientation) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();

        if (Logger.mUseLogSetting)
            Logger.d(TAG, "useWideView():" + displayMetrics);

        if (Math.round(displayMetrics.widthPixels / displayMetrics.density) >= WIDE_VIEW_WIDTH_DIP) {
            return true;
        } else {
            return false;
        }
    }

    public static int getColumnWidthDip(Context context, float minWidthDip, int columnCount) {
        return getColumnWidthDip(context, 0, minWidthDip, columnCount);
    }

    public static int getColumnWidthDip(Context context, float remainWidthDip, float minWidthDip, int columnCount) {
        int displayWidth = getDisplayWidthDip(context);

        float computeValue = (displayWidth - remainWidthDip) / columnCount - 1F;
        return (int) (Math.max(computeValue, minWidthDip));
    }

    /**
     * HoneyComb 기준으로 View.setAlpha를 사용할지 AlphaAnimation을 사용할지 분기
     *
     * @param view  Alpha값을 변경할 view
     * @param alpha 변경할 Alpha값 (0< alpha < 1)
     */
    public static void setViewAlpha(View view, float alpha) {
        if (Build.VERSION.SDK_INT >= 11) {
            view.setAlpha(alpha);
        } else {
            AlphaAnimation alphaAnimation = new AlphaAnimation(alpha, alpha);
            alphaAnimation.setDuration(0);
            alphaAnimation.setFillAfter(true);
            view.startAnimation(alphaAnimation);
        }
    }
}
