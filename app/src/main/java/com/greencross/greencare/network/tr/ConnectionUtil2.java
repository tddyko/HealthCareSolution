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
import java.net.SocketTimeoutException;
import java.net.URL;

public class ConnectionUtil2 {
    private final String TAG = ConnectionUtil2.class.getSimpleName();
    private String ENCORDING_EUCKR = "euc-kr";

    private String mUrl;
    private StringBuffer paramSb = new StringBuffer();

    public ConnectionUtil2() {
    }

    public ConnectionUtil2(String url) {
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

        int contentLength = -1;
        try {
            Tr_get_infomation info = Define.getInstance().getInformation();

            if (info != null && TextUtils.isEmpty(info.apiURL) == false) {
                mURL = new URL(info.apiURL);
            } else {
                mURL = new URL(BaseUrl.COMMON_URL);
            }
            mURL = new URL(BaseUrl.COMMON_URL);

            conn = (HttpURLConnection) mURL.openConnection();
            // TimeOut 시간 (서버 접속시 연결 시간)
            conn.setConnectTimeout(10 * 1000);
            conn.setReadTimeout(10 * 1000);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            os.write("json=".getBytes(ENCORDING_EUCKR));       // json={...JSON DATA....} 형태로 보냄
            os.write(body.toString().getBytes(ENCORDING_EUCKR));
            os.flush();
            os.close();

            Log.i(TAG, "###############  ConnectionUtil."+name+"  ###############");
            Log.i(TAG, "url=" + mURL);
            JsonLogPrint.printJson(body.toString());
            Log.i(TAG, "###############  ConnectionUtil."+name+"  ###############");

            mIntResponse = conn.getResponseCode();
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }

        if (mIntResponse == HttpURLConnection.HTTP_OK) {
            try {

                int totalRead = 0;

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuffer buffer = new StringBuffer();
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);

                    totalRead += line.getBytes().length + 2;

                }
                reader.close();
                result = buffer.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Logger.e(TAG, "mIntResponse="+mIntResponse);
        }
        // 불필요 부분 제거
        result = result.replace("<?xml version=\"1.0\" encoding=\"utf-8\"?><string xmlns=\"http://tempuri.org/\">", "").replace("</string>","");
        Logger.i(TAG, "doConnection.result="+result);
        return result;
    }

}
