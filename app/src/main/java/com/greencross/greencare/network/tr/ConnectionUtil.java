package com.greencross.greencare.network.tr;

import android.text.TextUtils;
import android.util.Log;

import com.greencross.greencare.base.value.Define;
import com.greencross.greencare.network.tr.data.Tr_get_infomation;
import com.greencross.greencare.util.JsonLogPrint;
import com.greencross.greencare.util.Logger;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ConnectionUtil {
    private final String TAG = ConnectionUtil.class.getSimpleName();

    private String mUrl;
    private StringBuffer paramSb = new StringBuffer();

    public ConnectionUtil() {
    }

    public ConnectionUtil(String url) {
        mUrl = url;
    }


    public String getParam() {
        String params = paramSb.toString();
        if ("".equals(params.trim()) == false)
            params = params.substring(0, params.length() - 1);

        return params;
    }


    public String doConnection(JSONObject body, String name) {
        URL mURL;
        HttpURLConnection conn = null;
        int mIntResponse = 0;
        String result = "";
        try {
            Tr_get_infomation info = Define.getInstance().getInformation();
            if (info != null && TextUtils.isEmpty(info.apiURL) == false) {
                mURL = new URL(info.apiURL);
            } else {
                mURL = new URL(BaseUrl.COMMON_URL);
            }
            mURL = new URL(BaseUrl.COMMON_URL);

            conn = (HttpURLConnection) mURL.openConnection();
            conn.setConnectTimeout(3 * 1000);
            conn.setReadTimeout(3 * 1000);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Accept-Charset", "EUC-KR");

            Log.i(TAG, "###############  ConnectionUtil."+name+"  ###############");
            Log.i(TAG, "url=" + mURL);
            JsonLogPrint.printJson(body.toString());
            Log.i(TAG, "###############  ConnectionUtil."+name+"  ###############");

            OutputStream os = conn.getOutputStream();
            os.write("json=".getBytes("EUC-KR"));
            os.write(body.toString().getBytes("EUC-KR"));
            os.flush();
            os.close();
            mIntResponse = conn.getResponseCode();
        } catch (Exception e) {
            Log.d("hsh", "getJSONData ex" + e);
            mIntResponse = 1000;
        }

        if (mIntResponse == HttpURLConnection.HTTP_OK) {
            BufferedReader br;
            try {
                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                for (; ; ) {
                    String line = null;
                    try {
                        line = br.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (line == null) break;
                    result += line;
                }
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
        }
        // 불필요 부분 제거
        result = result.replace("<?xml version=\"1.0\" encoding=\"utf-8\"?><string xmlns=\"http://tempuri.org/\">", "").replace("</string>","");
        Logger.i(TAG, "doConnection.result="+result);
        return result;
    }

}
