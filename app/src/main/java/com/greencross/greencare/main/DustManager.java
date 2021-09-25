package com.greencross.greencare.main;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.greencross.greencare.R;
import com.greencross.greencare.network.tr.data.Tr_get_dust;
import com.greencross.greencare.util.Logger;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class DustManager {

    private static final String TAG = DustManager.class.getSimpleName();
    private static MainFragment mMainFragment;
    private static Map<String, String> CITY_MAP = null;

    static {
        CITY_MAP = new HashMap<String, String>();
        CITY_MAP.put("서울시", "1");
        CITY_MAP.put("부산광역시", "2");
        CITY_MAP.put("대구광역시", "3");
        CITY_MAP.put("인천광역시", "4");
        CITY_MAP.put("광주광역시", "5");
        CITY_MAP.put("대전광역시", "6");
        CITY_MAP.put("울산광역시", "7");
        CITY_MAP.put("경기도", "8");
        CITY_MAP.put("강원도", "9");
        CITY_MAP.put("충청북도", "10");
        CITY_MAP.put("충청남도", "11");
        CITY_MAP.put("전라북도", "12");
        CITY_MAP.put("전라남도", "13");
        CITY_MAP.put("경상북도", "14");
        CITY_MAP.put("경상남도", "15");
        CITY_MAP.put("제주특별자치도", "16");
    }

    public static String getCityNumber(String cityKrName) {
        return CITY_MAP.get(cityKrName);
    }

    public static String getCityName(String cityKey) {

        Iterator myVeryOwnIterator = CITY_MAP.keySet().iterator();
        while (myVeryOwnIterator.hasNext()) {
            String key = (String) myVeryOwnIterator.next();
            String value = (String) CITY_MAP.get(key);
            if (value.equals(cityKey)) {
                return key;
            }
        }
        return null;
    }


    private static String checkCity(String cityKrName) {
        if (cityKrName.contains("경기도")) {
            return "경기";
        } else if (cityKrName.contains("강원도")) {
            return "강원";
        } else if (cityKrName.contains("충청북도")) {
            return "충북";
        } else if (cityKrName.contains("충청남도")) {
            return "충남";
        } else if (cityKrName.contains("전라북도")) {
            return "전북";
        } else if (cityKrName.contains("전라남도")) {
            return "전남";
        } else if (cityKrName.contains("경상북도")) {
            return "경북";
        } else if (cityKrName.contains("경상남도")) {
            return "경남";
        } else if (cityKrName.contains("제주")) {
            return "제주";
        } else
            return cityKrName;
    }

    public static String getDustLocation(String zoneName) {
        String zonCode = "1";
        if(zoneName.contains("서울")){
            zonCode="1";
        }else if(zoneName.contains("부산")){
            zonCode="2";
        }else if(zoneName.contains("대구")){
            zonCode="3";
        }else if(zoneName.contains("인천")){
            zonCode="4";
        }else if(zoneName.contains("광주")){
            zonCode="5";
        }else if(zoneName.contains("대전")){
            zonCode="6";
        }else if(zoneName.contains("울산")){
            zonCode="7";
        }else if(zoneName.contains("경기")){
            zonCode="8";
        }else if(zoneName.contains("강원")){
            zonCode="9";
        }else if(zoneName.contains("충청북")|| zoneName.contains("충북")){
            zonCode="10";
        }else if(zoneName.contains("충청남")|| zoneName.contains("충남")){
            zonCode="11";
        }else if(zoneName.contains("전라북")|| zoneName.contains("전북")){
            zonCode="12";
        }else if(zoneName.contains("전라남")|| zoneName.contains("전남")){
            zonCode="13";
        }else if(zoneName.contains("경상북")|| zoneName.contains("경북")){
            zonCode="14";
        }else if(zoneName.contains("경상남")|| zoneName.contains("경남")){
            zonCode="15";
        }else if(zoneName.contains("제주")) {
            zonCode = "16";
        }
        return zonCode;
    }

    /**
     * 미세먼지 정보
     * @param data
     * @return
     */
    public static String getDustStatusStr(Tr_get_dust data) {
        int iDust = Integer.valueOf(data.dusn_qy); // 미세먼지 수치값
        String sConditon="";
        if(0<=iDust && iDust<31){
            sConditon = "좋음";
        }else if(31<=iDust && iDust<81){
            sConditon = "보통";
        }else if(81<=iDust && iDust<151){
            sConditon = "나쁨";
        }else{
            sConditon = "매우나쁨";
        }

        return sConditon;
    }

    public static String getCityKrName(Context ctx, Double latitude, Double longitude) throws Exception {
        String sido = "";
        String gogun = "";

        Geocoder gc = new Geocoder(ctx, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gc.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                sido = address.getAdminArea();
                gogun = address.getLocality();
            }
        } catch (Exception e) {
            // 1회 재시도
            SystemClock.sleep(500);
            addresses = gc.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                sido = address.getAdminArea();
                gogun = address.getLocality();
            }
        }

        if (Locale.getDefault().getLanguage().toString().equals(Locale.KOREAN.toString())) {
            if (!"".equals(sido)) {
                sido = filterCityKrName(sido);
            }

            if (!"".equals(gogun)) {
                sido = sido + "@" + gogun;
            }
        }
        return sido;
    }

    private static String filterCityKrName(String srcCityName) {
        String result = srcCityName.replace("특별시", "");
        result = result.replace("광역시", "");
        return result;
    }

    public void getWeather(MainFragment mainFragment, String location, IResult iResult) {
        mMainFragment = mainFragment;
        new WeatherTask(filterCityKrName(location), iResult).execute();
    }


    //날씨 정보 얻기 msn asynctask
    private class WeatherTask extends AsyncTask<Void, Void, MsnWeatherData> {

        private IResult mIResult;
        String mCityKrName = "";
        String mGugunKrName = "";

        public WeatherTask(String cityKrName, IResult iResult) {
            mIResult = iResult;

            if (cityKrName.indexOf("@") > 0) {
                String[] tmpStr = cityKrName.split("@");
                mCityKrName = tmpStr[0];
                mGugunKrName = tmpStr[1];
            } else {
                mCityKrName = cityKrName;
            }
        }

        @Override
        protected MsnWeatherData doInBackground(Void... params) {
            MsnWeatherData msnWeather = null;
            try {
                doConnection(mCityKrName);
            } catch (Exception e) {
            }
            return msnWeather;
        }

        @Override
        protected void onPostExecute(MsnWeatherData weather) {
            super.onPostExecute(weather);

            mIResult.result(weaterData);
        }
    }

    public String doConnection(String mCityKrName) {
        HttpURLConnection conn = null;
        int mIntResponse = 0;
        String result = "";

        mCityKrName = checkCity(mCityKrName);
        try {
            String str = "http://weather.service.msn.com/data.aspx?weadegreetype=C"
                    + "&culture=ko-KR"
                    + "&weasearchstr=" + URLEncoder.encode(mCityKrName, "UTF-8")
                    + "&src=outlook";
            Logger.i(TAG, "mURL=" + str);
            URL url = new URL(str);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla/4.0");
            conn.setConnectTimeout(30000);
            conn.setRequestMethod("GET");


            mIntResponse = conn.getResponseCode();
        } catch (Exception e) {
            mIntResponse = 1000;
        }
        Logger.i(TAG, "getWeather.mIntResponse=" + mIntResponse);
        if (mIntResponse == HttpURLConnection.HTTP_OK) {
            BufferedReader br = null;
            try {
                // 요청한 URL에 대한 응답 내용 출력.
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                StringBuffer buffer = new StringBuffer();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\r\n");
                }
                reader.close();


                result = buffer.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
        }
        Logger.i(TAG, "getWeather.result=" + result);
        xmlParser(result);
        return result;
    }

    private MsnWeatherData weaterData = new MsnWeatherData();

    private void xmlParser(String xml) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();

            xpp.setInput(new StringReader(xml));
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {

                if (eventType == XmlPullParser.START_TAG) {

                    if (xpp.getName().equals("current")) {

                        for (int i = 0; i < xpp.getAttributeCount(); i++) {

                            Logger.i(TAG, "current[" + i + "]" + xpp.getAttributeName(i) + "=" + xpp.getAttributeValue(i));

                            if (xpp.getAttributeName(i).equals("skytext")) {
                                Log.d(TAG, "##### 날씨  #####################");
                                Log.d(TAG, "skytext:" + xpp.getAttributeValue(i));
                                Log.d(TAG, "################################");
                                weaterData.weather = xpp.getAttributeValue(i);

                                weaterData.weather = TextUtils.isEmpty(weaterData.weather) || "null".equals(weaterData.temp) ? "" : weaterData.weather;
                            }
                            if (xpp.getAttributeName(i).equals("temperature")) {
                                Log.d(TAG, "##### 온도  #####################");
                                Log.d(TAG, "temperature:" + xpp.getAttributeValue(i));
                                Log.d(TAG, "################################");
                                weaterData.temp = xpp.getAttributeValue(i);

                                weaterData.temp = TextUtils.isEmpty(weaterData.temp) || "null".equals(weaterData.temp) ? "" : weaterData.temp;
                            }
                        }
                    }

                    if (xpp.getName().equals("forecast")) {
                        for (int i = 0; i < xpp.getAttributeCount(); i++) {

                            Logger.i(TAG, "forecast[" + i + "]" + xpp.getAttributeName(i) + "=" + xpp.getAttributeValue(i));
                        }
                    }

                    if (xpp.getName().equals("toolbar")) {
                        for (int i = 0; i < xpp.getAttributeCount(); i++) {

                            Logger.i(TAG, "toolbar[" + i + "]" + xpp.getAttributeName(i) + "=" + xpp.getAttributeValue(i));
                        }
                    }

                    if (xpp.getName().equals("weather")) {
                        for (int i = 0; i < xpp.getAttributeCount(); i++) {

                            Logger.i(TAG, "weather[" + i + "]" + xpp.getAttributeName(i) + "=" + xpp.getAttributeValue(i));
                        }
                    }

                }
                eventType = xpp.next();
            }
            System.out.println("End document");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface IResult {
        void result(MsnWeatherData weather);
    }

    public class MsnWeatherData {
        public String temp;
        public String weather;
        public String dust;
        public String cityName;
    }
}