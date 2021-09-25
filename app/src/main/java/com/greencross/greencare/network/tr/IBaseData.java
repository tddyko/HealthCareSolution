package com.greencross.greencare.network.tr;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mrsohn on 2017. 3. 30..
 */

public interface IBaseData {

    JSONObject makeJson(Object obj) throws JSONException;
}
