package com.greencross.greencare.network.tr;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.greencross.greencare.base.value.Define;
import com.greencross.greencare.util.JsonLogPrint;
import com.greencross.greencare.util.Logger;

import org.json.JSONObject;

import java.lang.reflect.Constructor;

public class ApiData {
	private final String	TAG			= ApiData.class.getSimpleName();
	public static final int	TYPTE_NONE	= -1;

	private int				trMode		= -1;

	/**
	 * 통신하여 json 데이터 Class<?> 세팅
	 * @param cls
	 * @return
	 */

	public Object getData(Context context, Class<?> cls, Object obj) {
		JSONObject body = null;
		IBaseData dataCls = null;
		try {
			Class<?> cl = Class.forName(cls.getName());
			Constructor<?> co = cl.getConstructor();
			dataCls = (BaseData) co.newInstance();

			body = dataCls.makeJson(obj);

		} catch (Exception e) {
            try {
                Class<?> cl = Class.forName(cls.getName());
                Constructor<?> co = cl.getConstructor(Context.class);
                dataCls = (BaseData) co.newInstance(context);

                body = dataCls.makeJson(obj);
            } catch (Exception e1) {
                e1.printStackTrace();
                Log.e(TAG, "ApiData Class 생성 실패", e);
            }
		}

        String url = BaseUrl.COMMON_URL;
        if (Define.getInstance().getInformation() != null) {
            // 로드벨런싱 후 Url
            if (TextUtils.isEmpty(Define.getInstance().getInformation().apiURL))
                url = Define.getInstance().getInformation().apiURL;
        }

        Logger.i(TAG, "ApiData.url="+url);
        ConnectionUtil connectionUtil = new ConnectionUtil(url);


		String result = null;
		if (body != null) {
            result = connectionUtil.doConnection(body, cls.getSimpleName());
        }

		if (TextUtils.isEmpty(result)) {
			Logger.e(TAG, "getData.result="+result);
		} else {
			Logger.i(TAG, "####################### API RESULT."+cls.getSimpleName()+" #####################");
			JsonLogPrint.printJson(result);
			Logger.i(TAG, "####################### API RESULT."+cls.getSimpleName()+" #####################");
		}

		Gson gson = new Gson();
		return gson.fromJson(result, dataCls.getClass());
	}

	private String decodeUniCode(String unicode) {
		try {
			StringBuffer str = new StringBuffer();

			char ch = 0;
			for (int i = unicode.indexOf("\\u"); i > -1; i = unicode.indexOf("\\u")) {
				ch = (char) Integer.parseInt(unicode.substring(i + 2, i + 6), 16);
				str.append(unicode.substring(0, i));
				str.append(String.valueOf(ch));
				unicode = unicode.substring(i + 6);
			}
			str.append(unicode);

			return str.toString();
		} catch (Exception e) {
			return unicode;
		}
		
	}

	public interface IStep {
		void next(Object obj);
	}

    public interface IFailStep {
        void fail();
    }
}
