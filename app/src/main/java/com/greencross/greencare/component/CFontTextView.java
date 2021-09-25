package com.greencross.greencare.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

import com.greencross.greencare.R;
import com.greencross.greencare.util.Logger;

/**
 * Created by mrsohn on 2017. 3. 21..
 */
public class CFontTextView extends TextView {

    public CFontTextView(Context context) {
        super(context);
    }

    public CFontTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CFontTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {

        TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.TextViewWithFont,
                0, 0);

        String typeface = typedArray.getString(R.styleable.TextViewWithFont_font);
        if (typeface != null) {
            try {
                Typeface tf = Typeface.createFromAsset(getContext().getAssets(), typeface);
                setTypeface(tf);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), getContext().getString(R.string.KelsonSansRegular));
            setTypeface(tf);
        }
    }
}