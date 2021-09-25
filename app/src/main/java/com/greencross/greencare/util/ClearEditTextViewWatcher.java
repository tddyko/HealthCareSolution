package com.greencross.greencare.util;

import android.annotation.TargetApi;
import android.os.Build;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.widget.EditText;

import java.text.DecimalFormat;

/**
 * Created by kb_card_mini_9 on 2016. 8. 17..
 */
public class ClearEditTextViewWatcher {
    private final String TAG = ClearEditTextViewWatcher.class.getSimpleName();

    private final int DEFAULT_MAX_LENGTH = 9; // 기본 (9,999,999) 까지 입력 가능

    private DecimalFormat mPriceFormat = new DecimalFormat("#,###");

    public final static int FORMAT_TYPE_PRICE = 10001;  // 0,000 형태로 변환
    public final static int FORMAT_TYPE_PHONE = 10002;  // 010-0000-0000 변환

    private EditText editText;
    private String beforeText = "";
    private int mFormatType = -1;

    public ClearEditTextViewWatcher(EditText editText, int mFormatType) {
        this.editText = editText;
        this.mFormatType = mFormatType;

        for (InputFilter filter : editText.getFilters()) {
            Logger.i(TAG, "ClearEditTextViewWatcher.filter=" + filter);
        }

        // 금액 관련 EditText 일ClearEditTextViewWatcher때 기본 천만단위까지만
        if (FORMAT_TYPE_PRICE == getFormatType()) {
            setMaxLength(editText, DEFAULT_MAX_LENGTH);
        }

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (beforeText.equals(s.toString()) == false)
                    setFormatStr(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    /**
     * 숫자입력 포멧만 사용
     * limitVal 보다 적게 입력 됨
     *
     * @param editText
     * @param limitVal
     */
    public ClearEditTextViewWatcher(EditText editText, final long limitVal) {
        this.editText = editText;
        this.mFormatType = FORMAT_TYPE_PRICE;   // 1,000 숫자 포멧

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    Long inputVal = StringUtil.getLong(s.toString());
                    if (beforeText.equals(s.toString()) == false) {
                        setFormatStr(s.toString());
                    } else if (inputVal > limitVal) {
                        // 입력값이 최대값을 초과 하면 마지막 숫자 삭제
                        String str = beforeText;
                        str = str.substring(0, beforeText.length() - 1);
                        setFormatStr(str);
                    }
                } catch (Exception e) {
                    Logger.e(e);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public ClearEditTextViewWatcher(final EditText editText, final float limitVal) {
        this.editText = editText;
        this.mFormatType = FORMAT_TYPE_PRICE;   // 1,000 숫자 포멧

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    float inputVal = StringUtil.getFloat(s.toString());
                    if (beforeText.equals(s.toString()) == false) {
                        setFormatStr(s.toString());
                    } else if (inputVal > limitVal) {
                        // 입력값이 최대값을 초과 하면 마지막 숫자 삭제
                        String str = beforeText;
                        str = str.substring(0, beforeText.length() - 1);
                        setFormatStr(str);
                    }
                } catch (Exception e) {
                    Logger.e(e);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private int getFormatType() {
        return mFormatType;
    }

    private void setFormatStr(String str) {
        String text = str;
        Long longVal = 0L;
        if (FORMAT_TYPE_PRICE == getFormatType()) {

            if (text.startsWith("-")) {
                text = "-" + StringUtil.getIntString(text);
                if ("-".equals(text)) {
                    return;
                }

                longVal = Long.valueOf(text);
            } else {
                longVal = StringUtil.getLong(text);
            }

            if ("".equals(text) == false) {
                try {
                    text = String.format("%,d", longVal);
                } catch (Exception e) {
                    Logger.e(e);
                    return;
                }
            }
            beforeText = text;

            editText.setText(beforeText);
            editText.setSelection(beforeText.length());
        } else if (FORMAT_TYPE_PHONE == getFormatType()) {
            text = StringUtil.getIntString(text);
            text = StringUtil.getPhoneNumFormat(text);
            beforeText = text;

            editText.setText(beforeText);
            editText.setSelection(beforeText.length());
        }

        Logger.i(TAG, "setFormatStr=" + text);
    }


    private void setFormatStr(String str, int stringResId) {
        String text = str;
        float val = 0f;
        if (FORMAT_TYPE_PRICE == getFormatType()) {

            if (text.startsWith("-")) {
                text = "-" + StringUtil.getIntString(text);
                if ("-".equals(text)) {
                    return;
                }

                val = Float.valueOf(text);
            } else {
                val = StringUtil.getFloat(text);
            }

            text = editText.getContext().getString(stringResId, val);
            beforeText = text;

            editText.setText(beforeText);
            editText.setSelection(beforeText.length());
        }

        Logger.i(TAG, "setFormatStr=" + text);
    }


    /**
     * This sets the maximum length in characters of an EditText view. Since the
     * max length must be done with a filter, this method gets the current
     * filters. If there is already a length filter in the view, it will replace
     * it, otherwise, it will add the max length filter preserving the other
     *
     * @param view   EditText
     * @param length int
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setMaxLength(EditText view, int length) {
        InputFilter curFilters[];
        InputFilter.LengthFilter lengthFilter;
        int idx;

        lengthFilter = new InputFilter.LengthFilter(length);

        curFilters = view.getFilters();
        if (curFilters != null) {
            for (idx = 0; idx < curFilters.length; idx++) {
                if (curFilters[idx] instanceof InputFilter.LengthFilter) {
                    InputFilter.LengthFilter filter = (InputFilter.LengthFilter) curFilters[idx];
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        Logger.i(TAG, "setMaxLength return lenght=(" + filter.getMax() + ")");
                    }
                    return;
                }
            }

            // since the length filter was not part of the list, but
            // there are filters, then add the length filter
            InputFilter newFilters[] = new InputFilter[curFilters.length + 1];
            System.arraycopy(curFilters, 0, newFilters, 0, curFilters.length);
            newFilters[curFilters.length] = lengthFilter;
            view.setFilters(newFilters);

            Logger.i(TAG, "setMaxLength setFilters.length=" + length);
        } else {
            view.setFilters(new InputFilter[]{lengthFilter});
            Logger.i(TAG, "setMaxLength setFilters.length else=" + length);
        }
    }
}