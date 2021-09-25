package com.greencross.greencare.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DecimalFormat;

/**
 * Created by kb_card_mini_9 on 2016. 8. 17..
 */
public class ClearEditTextView extends EditText implements TextWatcher, View.OnTouchListener, View.OnFocusChangeListener {
    private final String TAG = ClearEditTextView.class.getSimpleName();

    /**
     * 소프트 키보드 및 하드웨어 버튼이 눌러졌을때 먼저 호출되는 인터페이스
     */
    public interface OnKeyPreImeListener {
        void onKeyPreIme(int keyCode, KeyEvent event);
    }

    private OnKeyPreImeListener mOnKeyPreImeListener;
    private Drawable clearDrawable;
    private OnFocusChangeListener onFocusChangeListener;
    private OnTouchListener onTouchListener;

    private DecimalFormat mPriceFormat = new DecimalFormat("#,###");

    public final static int FORMAT_TYPE_PRICE = 10001;  // 0,000 형태로 변환
    public final static int FORMAT_TYPE_PHONE = 10002;  // 010-0000-0000 변환

    private int mFormatType = -1;


    public ClearEditTextView(final Context context) {
        super(context);
        init();
    }

    public ClearEditTextView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ClearEditTextView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    public void setOnFocusChangeListener(OnFocusChangeListener onFocusChangeListener) {
        this.onFocusChangeListener = onFocusChangeListener;
    }

    @Override
    public void setOnTouchListener(OnTouchListener onTouchListener) {
        this.onTouchListener = onTouchListener;
    }

    private void init() {

        Drawable tempDrawable = ContextCompat.getDrawable(getContext(), android.R.drawable.ic_menu_close_clear_cancel);
        clearDrawable = DrawableCompat.wrap(tempDrawable);
        clearDrawable.setBounds(0, 0, clearDrawable.getIntrinsicWidth(), clearDrawable.getIntrinsicHeight()); // 오른쪽 X 표시

        super.setOnTouchListener(this);
        super.setOnFocusChangeListener(this);
        addTextChangedListener(this);

        setClearIconVisible(getText().length() > 0);

    }

    /**
     * 포커스 및 키보드 올리기
     */
    public void focusAndShowKeyboard() {

        requestFocus();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager inputManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(ClearEditTextView.this, InputMethodManager.SHOW_IMPLICIT);
            }
        }, 100);
    }


    /**
     * 소프트 키보드 및 하드웨어 버튼이 눌러졌을때 먼저 호출되는 콜백 메서드
     * OnKeyPreImeListener 를 구현하여 처리를 담당한다.
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (mOnKeyPreImeListener != null) {
            mOnKeyPreImeListener.onKeyPreIme(keyCode, event);
        }
        return super.onKeyPreIme(keyCode, event);
    }

    /**
     * 소프트 키보드 및 하드웨어 버튼이 눌러졌을때 먼저 호출되는 콜백 리스너
     *
     * @param onKeyPreImeListener
     */
    public void setOnKeyPreImeListener(OnKeyPreImeListener onKeyPreImeListener) {
        this.mOnKeyPreImeListener = onKeyPreImeListener;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (enabled) {
            setClearIconVisible(getText().length() > 0);
        } else {
            setClearIconVisible(false);
        }
    }


    public void setEditable(boolean isEnable) {
        setEnabled(isEnable);
        setFocusableInTouchMode(isEnable);
        setFocusable(isEnable);
        setClickable(isEnable);
    }

    public void setShowClearIcon(boolean isShowIcon) {
        setClearIconVisible(isShowIcon);
    }

    /**
     * 0,000형, 전화번호 형태의 입력값을 세팅
     *
     * @param type
     */
    public void addFormatWatcher(int type) {
        mFormatType = type;
        new ClearEditTextViewWatcher(this, type);
    }

    /**
     * 최대값 입력 하여 그 이상입력 되지 않도록 숫자 포멧만 가능 그외엔 Exception
     *
     * @param maxVal
     */
    public void addValueWatcher(long maxVal) {
        mFormatType = FORMAT_TYPE_PRICE;
        new ClearEditTextViewWatcher(this, maxVal);
    }

    /**
     * 최대 입력 값, InputType 설정하여
     *
     * @param maxVal
     * @param inputType
     */
    public void addValueWatcher(long maxVal, int inputType) {
        setInputType(inputType);
        addValueWatcher(maxVal);
    }

    @Override
    public void onFocusChange(final View view, final boolean hasFocus) {
        setClearIconVisible(getText().length() > 0);

        if (onFocusChangeListener != null) {
            onFocusChangeListener.onFocusChange(view, hasFocus);
        }

    }


    @Override
    public boolean onTouch(final View view, final MotionEvent motionEvent) {
        final int x = (int) motionEvent.getX();
        if (clearDrawable.isVisible() && x > getWidth() - getPaddingRight() - clearDrawable.getIntrinsicWidth()) {// 오른쪽
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                requestFocus();
                setError(null);
                setText(null);
                setClearIconVisible(false);
            }

            return true;
        }

        if (onTouchListener != null) {
            return onTouchListener.onTouch(view, motionEvent);
        } else {
            return false;
        }

    }

    @Override
    public final void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
        setClearIconVisible(s.length() > 0);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    /**
     * X 아이콘 표시 처리
     *
     * @param visible boolean
     */
    private void setClearIconVisible(boolean visible) {
        if (isEnabled()) {
            if (clearDrawable != null) {
                clearDrawable.setVisible(visible, false);
                setCompoundDrawables(null, null, visible ? clearDrawable : null, null);
            }
        } else {
            // disable 일때는 x 표시 없애기
            if (clearDrawable != null) {
                clearDrawable.setVisible(visible, false);
                setCompoundDrawables(null, null, null, null);
            }
        }
    }


    public boolean isValidPhoneNumber() {
        if (mFormatType != FORMAT_TYPE_PHONE) {
            throw new IllegalArgumentException("핸드폰번호 형식이 아닙니다.");
        }

        String text = getText().toString();
        return text.matches("(01[016789])-(\\d{3,4})-(\\d{4})");
    }


    /**
     * 가변 텍스트 사이즈 처리
     */
    public void setAutoScale(boolean isSacle) {
        if (isSacle) {
            this.addTextChangedListener(mTextSizeWatcher);
        } else {
            this.removeTextChangedListener(mTextSizeWatcher);
        }
    }

    TextWatcher mTextSizeWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(final CharSequence text, final int start, final int before, final int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            setTextScale(ClearEditTextView.this, ClearEditTextView.this.getMeasuredWidth());
        }
    };

    /**
     * 텍스트 자간 조정
     *
     * @param textView
     * @param textViewWidth
     */
    private void setTextScale(TextView textView, float textViewWidth) {
        if (textViewWidth <= 0F) {
            return;
        }

        float scaleX = 1.0F;
        textView.setTextScaleX(1.0F);

        String text = textView.getText().toString();
        if (text.indexOf("\n") != -1) {
            String[] tokens = text.split("\n");
            String maxStr = "";
            for (String token : tokens) {
                maxStr = token;
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
}