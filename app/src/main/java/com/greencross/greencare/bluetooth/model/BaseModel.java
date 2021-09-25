package com.greencross.greencare.bluetooth.model;

import java.lang.reflect.Field;

/**
 * Created by jongrakmoon on 2017. 3. 3..
 */

public class BaseModel {
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("{");

        Class tmpClass = getClass();

        do {
            Field[] fields = tmpClass.getDeclaredFields();

            for (Field field : fields) {
                try {
                    field.setAccessible(true);
                    Object data = field.get(this);
                    if (data != null) {
                        builder.append(field.getName())
                                .append(":")
                                .append(data.toString())
                                .append(",");
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

        } while ((tmpClass = tmpClass.getSuperclass()) != null);

        builder.deleteCharAt(builder.lastIndexOf(","))
                .append("}");
        return builder.toString();
    }
}
