package com.greencross.greencare.util;

import android.content.Context;
import android.os.Handler;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Created by mrsohn on 2017. 2. 26..
 */

public class EditTextUtil {

    /**
     * 포커스 및 키보드 올리기
     */
    public static void focusAndShowKeyboard(final Context context, final EditText editText) {

        editText.requestFocus();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
            }
        }, 100);
    }

    /**
     * 키보드 내리기
     *
     * @param context
     * @param editText
     */
    public static void hideKeyboard(final Context context, final EditText editText) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            }
        }, 100);

    }
}
