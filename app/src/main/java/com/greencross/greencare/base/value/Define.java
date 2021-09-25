package com.greencross.greencare.base.value;

import android.os.Environment;

import com.greencross.greencare.R;
import com.greencross.greencare.main.DustManager;
import com.greencross.greencare.main.MainFragment;
import com.greencross.greencare.network.tr.data.Tr_get_dust;
import com.greencross.greencare.network.tr.data.Tr_get_infomation;
import com.greencross.greencare.network.tr.data.Tr_login;

import java.io.File;

public class Define {
    private static Define instance;
    private Tr_get_infomation information;
    private Tr_login loginInfo;

    private Tr_get_dust dustData;
    private DustManager.MsnWeatherData weatherData;

    private int weatherRequestedTime = -1;  // 메인 날씨 정보 조회 시간

    public static int SUGAR_TYPE_ALL = 0;
    public static int SUGAR_TYPE_BEFORE = 1;
    public static int SUGAR_TYPE_AFTER = 2;

    private int healthMessageCnt = -1;


    // 스텝 타입 값
    public static final int STEP_DATA_SOURCE_GOOGLE_FIT = 111;    // 구글 피트니스
    public static final int STEP_DATA_SOURCE_BAND = 222;            // 스마트밴드

    public static String IMAGE_SAVE_PATH = Environment.getExternalStorageDirectory().getPath()+ File.separator+"GreenCare"+File.separator+".nomedia";

    public static Define getInstance() {
        if (instance == null) {
            instance = new Define();
        }
        return instance;
    }

    public void setInformation(Tr_get_infomation information) {
        this.information = information;
    }

    /**
     * 접속시 아이피 정보 및 저장
     * @return
     */
    public Tr_get_infomation getInformation() {
        if (information == null) {
            information = new Tr_get_infomation();
        }
        return information;
    }


    /**
     * 로그인 정보 가져오기
     * @return
     */
    public Tr_login getLoginInfo() {
        return loginInfo;
    }

    /**
     * 로그인 정보 저장하기
     * @param loginInfo
     */
    public void setLoginInfo(Tr_login loginInfo) {
        this.loginInfo = loginInfo;
    }

    public static String getFoodPhotoPath(String idx) {
        return Define.IMAGE_SAVE_PATH+File.separator+idx+".png";
    }

    /**
     * 메인날씨정보
     * @return
     */
    public int getWeatherRequestedTime() {
        return weatherRequestedTime;
    }

    public void setWeatherRequestedTime(int weatherRequestedTime) {
        this.weatherRequestedTime = weatherRequestedTime;
    }

    public Tr_get_dust getDustData() {
        return dustData;
    }

    public void setDustData(Tr_get_dust dustData) {
        this.dustData = dustData;
    }

    public DustManager.MsnWeatherData getWeatherData() {
        return weatherData;
    }

    public void setWeatherData(DustManager.MsnWeatherData weatherData) {
        this.weatherData = weatherData;
    }

    /**
     * 액션바 설정 관련
     */
    public enum ACTION_BAR {
        NO_RIGHT_MENU
        , RIGHT_MENU1
        , RIGHT_MENU2
    }

    public enum LOCATION_DATA {
        DATA1(new Location("서울시","1"))
        ,DATA2(new Location("부산광역시","2"))
        ,DATA3(new Location("대구광역시","3"))
        ,DATA4(new Location("인천광역시","4"))
        ,DATA5(new Location("광주광역시","5"))
        ,DATA6(new Location("대전광역시","6"))
        ,DATA7(new Location("울산광역시","7"))
        ,DATA8(new Location("경기도","8"))
        ,DATA9(new Location("강원도","9"))
        ,DATA10(new Location("충청북도","10"))
        ,DATA11(new Location("충청남도","11"))
        ,DATA12(new Location("전라북도","12"))
        ,DATA13(new Location("전라남도","13"))
        ,DATA14(new Location("경상북도","14"))
        ,DATA15(new Location("경상남도","15"))
        ,DATA16(new Location("제주특별자치도","16"));

        private Location mLocation;
        LOCATION_DATA(Location location) {
            mLocation = location;
        }
        public Location getLocation() {
            return mLocation;
        }
    }

    public static class Location {
        private String city;
        private String code;
        public Location(String location, String code) {
            this.city = location;
            this.code = code;
        }

        public String getCity() {
            return city;
        }
        public String getCode() {
            return code;
        }
    }

    public int getHealthMessageCnt() {
        return healthMessageCnt;
    }

    public void setHealthMessageCnt(int healthMessageCnt) {
        this.healthMessageCnt = healthMessageCnt;
    }
}
