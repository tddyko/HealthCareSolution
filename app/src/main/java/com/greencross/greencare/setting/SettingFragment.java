package com.greencross.greencare.setting;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.greencross.greencare.R;
import com.greencross.greencare.base.BaseFragment;
import com.greencross.greencare.base.CommonActionBar;
import com.greencross.greencare.base.IBaseFragment;
import com.greencross.greencare.base.value.Define;
import com.greencross.greencare.component.CDialog;
import com.greencross.greencare.component.CFontTextView;
import com.greencross.greencare.login.JoinStep1Fragment;
import com.greencross.greencare.login.JoinStep2Fragment;
import com.greencross.greencare.login.JoinStep3Fragment;
import com.greencross.greencare.network.tr.data.Tr_get_infomation;
import com.greencross.greencare.util.PackageUtil;
import com.greencross.greencare.util.SharedPref;

/**
 * Created by insystemscompany on 2017. 2. 28..
 */

public class SettingFragment extends BaseFragment implements IBaseFragment {

    private TextView new_imageview;
    private CheckBox autoCheckBox;

    String serverVersion;
    String appVersion;

    public static Fragment newInstance() {
        SettingFragment fragment = new SettingFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_setting, container, false);
        return view;
    }


    @Override
    public void loadActionbar(CommonActionBar actionBar) {
        super.loadActionbar(actionBar);
        actionBar.setActionBarTitle(getString(R.string.text_setting));
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        CFontTextView versionTextView = (CFontTextView) view.findViewById(R.id.setting_version_textview);
        new_imageview = (TextView) view.findViewById(R.id.new_imageview);
        autoCheckBox = (CheckBox) view.findViewById(R.id.setting_active_autologin_checkbox);
        versionTextView.setText(PackageUtil.getVersionInfo(getContext()));

        view.findViewById(R.id.setting_active_modify_layout).setOnClickListener(mClickListener);
        view.findViewById(R.id.setting_medical_examination_modify_layout).setOnClickListener(mClickListener);
        view.findViewById(R.id.setting_member_info_layout).setOnClickListener(mClickListener);
        view.findViewById(R.id.li_version).setOnClickListener(mClickListener);
        view.findViewById(R.id.setting_active_autologin_checkbox).setOnClickListener(mClickListener);

        // 버전체크
        Tr_get_infomation info = Define.getInstance().getInformation();
        serverVersion = info.appVersion;
        PackageInfo i = null;
        try {
            i = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            appVersion = i.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String appV = appVersion.replace(".", "");
        String svrV = info.appVersion.replace(".", "");
        if (Integer.parseInt(appV) >= Integer.parseInt(svrV)) {
            new_imageview.setVisibility(View.GONE);
        }

        // 자동 로그인 체크
        boolean isAutoLogin = SharedPref.getInstance().getPreferences(SharedPref.IS_AUTO_LOGIN, false);
        autoCheckBox.setChecked(isAutoLogin);
    }

    View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int vId = v.getId();
            Bundle bundle = new Bundle();
            if (vId == R.id.li_version) {
                Tr_get_infomation info = Define.getInstance().getInformation();
                if (TextUtils.isEmpty(info.appVersion) == false && TextUtils.isEmpty(info.apiURL) == false) {

                    try {
                        String appV = appVersion.replace(".", "");
                        String svrV = info.appVersion.replace(".", "");
                        // 서버버전이 높다면
                        if (Integer.parseInt(appV) >= Integer.parseInt(svrV)) {
                            CDialog.showDlg(getContext(), "현재버전 : " + appVersion + "\n\n 최신 버전입니다.", new CDialog.DismissListener() {
                                @Override
                                public void onDissmiss() {

                                }
                            });
                        } else {
                            CDialog.UpdateshowDlg(getContext(), "현재버전 : " + appVersion + "\n\n 최신 버전 : " + serverVersion, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Tr_get_infomation info = Define.getInstance().getInformation();
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(info.apiURL));
                                    startActivity(browserIntent);
                                }
                            }, null);
                        }
                    } catch (Exception e) {
                    }
                }
            } else if (vId == R.id.setting_member_info_layout) {
                // 가입정보수정
                bundle.putString(CommonActionBar.ACTION_BAR_TITLE, getString(R.string.text_member_info_modify));
                movePage(JoinStep1Fragment.newInstance(), bundle);
            } else if (vId == R.id.setting_medical_examination_modify_layout) {
                // 문진정보수정
                bundle.putString(CommonActionBar.ACTION_BAR_TITLE, getString(R.string.text_medical_examination_modify));
                movePage(JoinStep2Fragment.newInstance(), bundle);
            } else if (vId == R.id.setting_active_modify_layout) {
                bundle.putString(CommonActionBar.ACTION_BAR_TITLE, getString(R.string.text_active_modify));
                movePage(JoinStep3Fragment.newInstance(), bundle);
            } else if (vId == R.id.setting_active_autologin_checkbox) {
                if (autoCheckBox.isChecked()) {
                    SharedPref.getInstance().savePreferences(SharedPref.IS_AUTO_LOGIN, true);
                } else {
                    SharedPref.getInstance().savePreferences(SharedPref.IS_AUTO_LOGIN, false);
                }
            }
        }
    };
}
