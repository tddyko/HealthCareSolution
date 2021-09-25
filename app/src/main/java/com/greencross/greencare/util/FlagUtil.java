package com.greencross.greencare.util;

import android.content.Intent;
import android.os.Bundle;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;


/**
 * 인텐트 플래그 처리 유틸
 */
public class FlagUtil
{
    private static Map<String, Integer> flagMap =
            new HashMap<String, Integer>();

    static {
        flagMap.put("FLAG_ACTIVITY_RETAIN_IN_RECENTS", Intent.FLAG_ACTIVITY_RETAIN_IN_RECENTS);         //0x00002000
        flagMap.put("FLAG_ACTIVITY_TASK_ON_HOME", Intent.FLAG_ACTIVITY_TASK_ON_HOME);                   //0x00004000
        flagMap.put("FLAG_ACTIVITY_CLEAR_TASK", Intent.FLAG_ACTIVITY_CLEAR_TASK);                       //0x00008000
        flagMap.put("FLAG_ACTIVITY_NO_ANIMATION", Intent.FLAG_ACTIVITY_NO_ANIMATION);                   //0x00010000
        flagMap.put("FLAG_ACTIVITY_REORDER_TO_FRONT", Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);           //0x00020000
        flagMap.put("FLAG_ACTIVITY_NO_USER_ACTION", Intent.FLAG_ACTIVITY_NO_USER_ACTION);               //0x00040000
        flagMap.put("FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET or Intent.FLAG_ACTIVITY_NEW_DOCUMENT",
                Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);                                            //0x00080000
        flagMap.put("FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY", Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY); //0x00100000
        flagMap.put("FLAG_ACTIVITY_RESET_TASK_IF_NEEDED", Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);   //0x00200000
        flagMap.put("FLAG_ACTIVITY_BROUGHT_TO_FRONT", Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);           //0x00400000
        flagMap.put("FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS", Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);   //0x00800000
        flagMap.put("FLAG_ACTIVITY_PREVIOUS_IS_TOP", Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);             //0x01000000
        flagMap.put("FLAG_ACTIVITY_FORWARD_RESULT", Intent.FLAG_ACTIVITY_FORWARD_RESULT);               //0x02000000
        flagMap.put("FLAG_ACTIVITY_CLEAR_TOP", Intent.FLAG_ACTIVITY_CLEAR_TOP);                         //0x04000000
        flagMap.put("FLAG_ACTIVITY_MULTIPLE_TASK", Intent.FLAG_ACTIVITY_MULTIPLE_TASK);                 //0x08000000
        flagMap.put("FLAG_ACTIVITY_NEW_TASK", Intent.FLAG_ACTIVITY_NEW_TASK);                           //0x10000000
        flagMap.put("FLAG_ACTIVITY_SINGLE_TOP", Intent.FLAG_ACTIVITY_SINGLE_TOP);                       //0x20000000
        flagMap.put("FLAG_ACTIVITY_NO_HISTORY", Intent.FLAG_ACTIVITY_NO_HISTORY);                       //0x40000000
    }

    private static Set<Map.Entry<String, Integer>> getFlags() {
        TreeSet<Map.Entry<String, Integer>> result = new TreeSet<>(new EntryComparator());
        result.addAll(flagMap.entrySet());
        return result;
    }

    private static class EntryComparator implements
            Comparator<Map.Entry<String, ?>>
    {
        @Override
        public int compare(Map.Entry<String, ?> object1, Map.Entry<String, ?> object2) {
            return object1.getKey().compareTo(object2.getKey());
        }
    }

    /**
     * Intent내 플래그 목록 문자열
     * @param intent Intent
     * @return 플래그 목록 문자열
     */
    public static String getFlagString(Intent intent){
        if(intent == null)
            return "NULL_INTENT";

        int currentFlagsInt = intent.getFlags();

        StringBuilder currentFlags = new StringBuilder();
        for (Map.Entry<String, Integer> flagEntry : FlagUtil.getFlags()) {
            if ((flagEntry.getValue() & currentFlagsInt) != 0) {
                currentFlags.append(flagEntry.getKey() + "\n");
            }
        }

        return currentFlags.toString();
    }

    /**
     * @param intent Intent
     * @return Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY 설정 여부
     */
    public static boolean isLaunchedFromHistory(Intent intent){
        if(intent == null)
            return false;

        int currentFlagsInt = intent.getFlags();

        return (currentFlagsInt & Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) != 0;
    }


    /**
     * Intent내 extra Bundle key:value 문자열
     * @param intent Intent
     * @return extra Bundle key:value 문자열
     */
    public static String getExtraString(Intent intent){
        Bundle bundle = intent.getExtras();
        if(bundle == null)
            return "NULL_DATA";

        StringBuilder builder = new StringBuilder();
        Set<String> keys = bundle.keySet();
        for(String key:keys){
            if(builder.length() != 0)
                builder.append("\n");
            builder.append(key + " : " + bundle.get(key));
        }

        return builder.toString();
    }
}