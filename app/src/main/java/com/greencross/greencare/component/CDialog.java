package com.greencross.greencare.component;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.greencross.greencare.R;
import com.greencross.greencare.util.Logger;

import static com.greencross.greencare.R.id.dialog_content_layout;


/**
 * 공통 메시지 다이얼로그
 */
public class CDialog extends Dialog {
    private static final String TAG = CDialog.class.getSimpleName();
    private boolean mIsAutoDismiss = true;

    private TextView mTitleView;
    private TextView mMessageView;
    private Button mNoButton;
    private View viewTerm;
    private Button mOkButton;
    private String mTitle;
    private String mMessage;
    private LinearLayout mContentLayout;
    private LinearLayout mBtnLayout;

    private View.OnClickListener mNoClickListener;
    private View.OnClickListener mOkClickListener;
    private static DismissListener mDismissListener;

    private static CDialog instance;

    private static CDialog getInstance(Context context) {
        instance = new CDialog(context);
        initDialog(instance);
        return instance;
    }

    private static CDialog getInstance(Activity activity) {
        instance = new CDialog(activity);
        initDialog(instance);
        return instance;
    }

    private static void initDialog(CDialog instance) {
        if (instance == null) {
            return;
        }

        if (instance.mOkButton != null) {
            instance.mOkButton.setVisibility(View.GONE);
            instance.mOkButton.setOnClickListener(null);
        }

        if (instance.mNoButton != null) {
            instance.mNoButton.setVisibility(View.GONE);
            instance.mNoButton.setOnClickListener(null);
            instance.viewTerm.setVisibility(View.GONE);
        }

        if (instance.mBtnLayout != null)
            instance.mBtnLayout.setVisibility(View.GONE);

        if (mDismissListener != null)
            mDismissListener = null;

        if (instance.mTitleView != null)
            instance.mTitleView.setText(instance.getContext().getString(R.string.text_alert));

        instance.setOnDismissListener(null);

        if (instance.isShowing() == false)
            instance.show();
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

    public CDialog(Context context) {
        // Dialog 배경을 투명 처리 해준다.
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
    }

    public static CDialog showDlg(Context context, String message) {
        final CDialog dlg = getInstance(context);
        dlg.setMessage(message);
        dlg.mOkButton.setVisibility(View.VISIBLE);
        dlg.mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
            }
        });

        return dlg;
    }

    public static CDialog LoginshowDlg(Context context, String title, String message) {
        CDialog dlg = getInstance(context);
        dlg.setTitle(title);
        dlg.setMessage(message);

        return dlg;
    }

    public static CDialog showDlg(Context context, String title, String message) {
        CDialog dlg = getInstance(context);
        dlg.setTitle(title);
        dlg.setMessage(message);

        return dlg;
    }

    public static CDialog showDlg(Context context, String message, View.OnClickListener okListener) {
        CDialog dlg = getInstance(context);
        dlg.setMessage(message);
        dlg.setOkButton(okListener);

        return dlg;
    }

    public static CDialog showDlg(Context context, String title, String message, View.OnClickListener okListener) {
        CDialog dlg = getInstance(context);
        dlg.setTitle(title);
        dlg.setMessage(message);
        dlg.setOkButton(okListener);

        return dlg;
    }

    public static CDialog showDlg(Activity activity, String message, final CDialog.DismissListener dismissListener) {
        final CDialog dlg = getInstance(activity);
        mDismissListener = dismissListener;
        dlg.setMessage(message);
        dlg.mOkButton.setVisibility(View.VISIBLE);
        dlg.mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
            }
        });

        return dlg;
    }

    public static CDialog showDlg(Context context, String message, final CDialog.DismissListener dismissListener) {
        final CDialog dlg = getInstance(context);
        mDismissListener = dismissListener;
        dlg.setMessage(message);
        dlg.mOkButton.setVisibility(View.VISIBLE);
        dlg.mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
            }
        });

        return dlg;
    }

    public static CDialog showDlg(Context context, String message, View.OnClickListener okListener, View.OnClickListener noListener) {
        CDialog dlg = getInstance(context);
        dlg.setMessage(message);

        dlg.setOkButton(okListener);
        dlg.setNoButton(noListener);

        return dlg;
    }

    public static CDialog UpdateshowDlg(Context context, String message, View.OnClickListener okListener, View.OnClickListener noListener) {
        CDialog dlg = getInstance(context);
        dlg.setMessage(message);
        dlg.mOkButton.setText("업데이트");
        dlg.setOkButton(okListener);
        dlg.setNoButton(noListener);

        return dlg;
    }


    public static CDialog showDlg(Context context, View view) {
        CDialog dlg = getInstance(context);
        dlg.mContentLayout.removeAllViews();
        dlg.mContentLayout.addView(view);
        dlg.mOkButton.setVisibility(View.GONE);
        dlg.mBtnLayout.setVisibility(View.GONE);
        dlg.setTitle("지역 선택");

        return dlg;
    }

    public static CDialog showDlg(Context context, View view, View.OnClickListener okListener, View.OnClickListener noListener) {
        CDialog dlg = getInstance(context);
        dlg.setContentView(view);

        dlg.setOkButton(okListener);
        dlg.setNoButton(noListener);

        return dlg;
    }

    public static void showDlg(Context context, String title, String message, View.OnClickListener okListener, View.OnClickListener noListener) {
        CDialog dlg = getInstance(context);

        dlg.setTitle(title);
        dlg.setMessage(message);
        dlg.setOkButton(okListener);
        dlg.setNoButton(noListener);
    }

    public void setTitle(String title) {
        mTitleView.setText(title);
    }

    public void setMessage(String message) {
        mMessageView.setText(message);
    }

    private void setClickListener() {   //final View.OnClickListener noClickListener, final View.OnClickListener okClickListener) {
        Logger.i("", "setClickListener=" + mNoButton);

        if (mNoClickListener == null) {
            mNoButton.setVisibility(View.GONE);
            viewTerm.setVisibility(View.GONE);
        }

        mNoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mNoClickListener != null) {
                    mNoClickListener.onClick(v);

                    if (mIsAutoDismiss) {
                        CDialog.this.dismiss();
                    }
                } else {
                    CDialog.this.dismiss();
                }
            }
        });

        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOkClickListener != null) {
                    mOkClickListener.onClick(v);

                    if (mIsAutoDismiss) {
                        CDialog.this.dismiss();
                    }
                } else {
                    CDialog.this.dismiss();
                }
            }
        });
    }

    /**
     * 왼쪽 버튼 세팅
     */
    public void setNoButton(View.OnClickListener noClickListener) {
        mNoButton.setVisibility(View.VISIBLE);
        viewTerm.setVisibility(View.VISIBLE);
        setAlertButtonClickListener(mNoButton, noClickListener);
    }

    public void setNoButton(String label, View.OnClickListener noClickListener) {
        mNoButton.setVisibility(View.VISIBLE);
        viewTerm.setVisibility(View.VISIBLE);
        mNoButton.setText(label);
        setAlertButtonClickListener(mNoButton, noClickListener);
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
        this.mOkClickListener = okClickListener;
        mOkButton.setVisibility(View.VISIBLE);
        mOkButton.setText(label);
        setAlertButtonClickListener(mOkButton, okClickListener);
    }

    public void setOkButtonDissmissListenr(Button button, View.OnClickListener clickListener) {
        this.mOkClickListener = clickListener;
    }

    public void setNoButtonDissmissListenr(Button button, View.OnClickListener clickListener) {
        button.hasOnClickListeners();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (mDismissListener != null)
            mDismissListener.onDissmiss();
    }

    public interface DismissListener {
        void onDissmiss();
    }

    private void setAlertButtonClickListener(Button button, final View.OnClickListener clickListener) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onClick(v);

                    if (mIsAutoDismiss) {
                        CDialog.this.dismiss();
                    }
                } else {
                    CDialog.this.dismiss();
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
        viewTerm = (View) findViewById(R.id.view_term);
        mOkButton = (Button) findViewById(R.id.dialog_btn_ok);
        mContentLayout = (LinearLayout) findViewById(dialog_content_layout);
        mBtnLayout = (LinearLayout) findViewById(R.id.dialog_btn_layout);
    }
}