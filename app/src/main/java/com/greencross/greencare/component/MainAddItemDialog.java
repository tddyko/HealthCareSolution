package com.greencross.greencare.component;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.greencross.greencare.R;
import com.greencross.greencare.util.Logger;

public class MainAddItemDialog extends Dialog {
    private boolean mIsAutoDismiss = true;

    private TextView mTitleView;
    private TextView mMessageView;
    private Button mNoButton;
    private Button mOkButton;
    private String mTitle;
    private String mMessage;

    private View.OnClickListener mLeftClickListener;
    private View.OnClickListener mRightClickListener;

    private static MainAddItemDialog instance;

    public static MainAddItemDialog getInstance(Context context) {
        if (instance == null) {
            instance = new MainAddItemDialog(context);
        }

        instance.show();
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.custom_dialog);
        setLayout();
        setClickListener();
    }

    public MainAddItemDialog(Context context) {
        // Dialog 배경을 투명 처리 해준다.
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
    }

    public void setTitle(String title) {
        mTitleView.setText(title);
    }

    public void setMessage(String message) {
        mMessageView.setText(message);
    }

    private void setClickListener() {//final View.OnClickListener noClickListener, final View.OnClickListener okClickListener) {
        Logger.i("", "setClickListener=" + mNoButton);

        if (mLeftClickListener == null)
            mNoButton.setVisibility(View.GONE);

        mNoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLeftClickListener != null) {
                    mLeftClickListener.onClick(v);

                    if (mIsAutoDismiss) {
                        MainAddItemDialog.this.dismiss();
                    }
                } else {
                    MainAddItemDialog.this.dismiss();
                }
            }
        });

        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRightClickListener != null) {
                    mRightClickListener.onClick(v);

                    if (mIsAutoDismiss) {
                        MainAddItemDialog.this.dismiss();
                    }
                } else {
                    MainAddItemDialog.this.dismiss();
                }
            }
        });
    }

    /**
     * 왼쪽 버튼 세팅
     *
     * @param okClickListener
     */
    public void setNoButton(View.OnClickListener okClickListener) {
        String label = mNoButton.getText().toString();
        setNoButton(label, okClickListener);
    }

    public void setNoButton(String label, final View.OnClickListener okClickListener) {
        this.mLeftClickListener = okClickListener;
        mNoButton.setVisibility(View.VISIBLE);
        mNoButton.setText(label);
        setAlertButtonClickListener(mNoButton, okClickListener);
    }

    /**
     * 오른쪽 버튼 세팅
     *
     * @param okClickListener
     */
    public void setOkButton(View.OnClickListener okClickListener) {
        String label = mOkButton.getText().toString();
        setOkButton(label, okClickListener);
    }

    public void setOkButton(String label, final View.OnClickListener okClickListener) {
        this.mRightClickListener = okClickListener;
        mOkButton.setVisibility(View.VISIBLE);
        mOkButton.setText(label);
        setAlertButtonClickListener(mOkButton, okClickListener);
    }

    private void setAlertButtonClickListener(Button button, final View.OnClickListener clickListener) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onClick(v);

                    if (mIsAutoDismiss) {
                        MainAddItemDialog.this.dismiss();
                    }
                } else {
                    MainAddItemDialog.this.dismiss();
                }
            }
        });
    }

    /*
     * Layout
     */
    private void setLayout() {
        mTitleView = (TextView) findViewById(R.id.dialog_title);
        mMessageView = (TextView) findViewById(R.id.dialog_content_tv);
        mNoButton = (Button) findViewById(R.id.dialog_btn_no);
        mOkButton = (Button) findViewById(R.id.dialog_btn_ok);
    }

}









