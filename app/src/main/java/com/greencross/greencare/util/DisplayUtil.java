package com.greencross.greencare.util;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

/**
 * 디스플레이 유틸
 */
public class DisplayUtil {
    /**
     * @param context Context
     * @param dip     float
     * @return 폰트 크기
     */
    public static int ftDipToPx(Context context, float dip) {
        float val = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, context.getResources().getDisplayMetrics());
        return Math.round(val);
    }

    /**
     * @param context Context
     * @param sp      float
     * @return 폰트 크기
     */
    public static int ftSpToPx(Context context, float sp) {
        float val = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
        return Math.round(val);
    }

    /**
     * @param con Context
     * @return Density
     */
    public static float getDensity(Context con) {
        return con.getResources().getDisplayMetrics().density;
    }

    /**
     * @param con Context
     * @param px  int
     * @return px에 대한 dp값
     */
    public static int getPxToDp(Context con, float px) {
        return (int) (px / getDensity(con));
    }

    /**
     * @param con Context
     * @param dp  float
     * @return dp에 대한 px값
     */
    public static int getDpToPix(Context con, float dp) {
        return (int) (dp * getDensity(con) + 0.5);
    }

    /**
     * @param context Context
     * @return Screen Width
     */
    public static int getScreenWidth(Context context) {
        Display dis = ((WindowManager)
                context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point point = new Point();
            dis.getSize(point);

            return point.x;
        } else {
            return dis.getWidth();
        }
    }

    /**
     * @param context Context
     * @return Screen Height
     */
    public static int getScreenHeight(Context context) {
        Display dis = ((WindowManager)
                context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point point = new Point();
            dis.getSize(point);

            return point.y;
        } else {
            return dis.getHeight();
        }
    }

    public static int getSysStatebarSize(View view) {
        Rect rect = new Rect();
        view.getWindowVisibleDisplayFrame(rect);

        int stateBarHeight = rect.top;

        return stateBarHeight;
    }
}
