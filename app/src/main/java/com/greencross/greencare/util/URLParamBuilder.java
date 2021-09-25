package com.greencross.greencare.util;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class URLParamBuilder {

    private Map<String, Object> mParamsMap;

    public URLParamBuilder() {
        mParamsMap = new LinkedHashMap<>();
    }

    public URLParamBuilder add(String key, String value) {
        mParamsMap.put(key, value);
        return this;
    }

    public boolean isContainsKey(String key) {
        return mParamsMap.containsKey(key);
    }

    public URLParamBuilder remove(String key) {
        mParamsMap.remove(key);

        return this;
    }

    public void clear() {
        mParamsMap.clear();
    }

    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();

        Iterator<String> it = mParamsMap.keySet().iterator();
        while (it.hasNext()) {

            String key = it.next();
            Object value = mParamsMap.get(key);

            if (builder.length() > 0) {
                builder.append("&");
            }
            builder.append(key);
            builder.append("=");
            builder.append(value);
        }

        return builder.toString();
    }
}