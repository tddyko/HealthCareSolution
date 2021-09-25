package com.greencross.greencare.component;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.StyleRes;
import android.view.View;

/**
 * Created by mrsohn on 2017. 3. 27..
 */

public class CDatePicker extends DatePickerDialog {
    @RequiresApi(api = Build.VERSION_CODES.N)
    public CDatePicker(@NonNull Context context) {
        super(context);
        initYearSelect();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public CDatePicker(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        initYearSelect();
    }

    public CDatePicker(@NonNull Context context, @Nullable OnDateSetListener listener, int year, int month, int dayOfMonth) {
        super(context, listener, year, month, dayOfMonth);
        initYearSelect();
    }

    public CDatePicker(@NonNull Context context, @Nullable OnDateSetListener listener, int year, int month, int dayOfMonth, boolean isYearStart) {
        super(context, listener, year, month, dayOfMonth);
        if (isYearStart)
            initYearSelect();
    }

    public CDatePicker(@NonNull Context context, @StyleRes int themeResId, @Nullable OnDateSetListener listener, int year, int monthOfYear, int dayOfMonth) {
        super(context, themeResId, listener, year, monthOfYear, dayOfMonth);
        initYearSelect();
    }

    /**
     * 년도부터 사용하도록 처리
     */
    private void initYearSelect() {
        int id = Resources.getSystem().getIdentifier("date_picker_header_year", "id", "android");
        View vv = getDatePicker().findViewById(id);
        if(vv != null) {
            vv.performClick();
        }
    }
}
