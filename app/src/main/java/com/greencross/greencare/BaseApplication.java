package com.greencross.greencare;

import android.app.Application;
import android.graphics.Typeface;

import java.lang.reflect.Field;

/**
 * Created by mrsohn on 2017. 3. 21..
 */

public class BaseApplication extends Application {

    public void onCreate() {
        super.onCreate();

        overrideFont();
    }

    private void overrideFont() {
        try {

            Typeface typeface = Typeface.createFromAsset(getBaseContext().getAssets(), getBaseContext().getString(R.string.KelsonSansRegular));//"NotoSansKR-Bold.otf");
            Field defaultFontTypefaceField = Typeface.class.getDeclaredField("DEFAULT");
            defaultFontTypefaceField.setAccessible(true);
            defaultFontTypefaceField.set(null, typeface);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    protected static void replaceFont(String staticTypefaceFieldName, final Typeface newTypeface) {
        try {
            final Field StaticField = Typeface.class.getDeclaredField(staticTypefaceFieldName);
            StaticField.setAccessible(true);
            StaticField.set(null, newTypeface);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}