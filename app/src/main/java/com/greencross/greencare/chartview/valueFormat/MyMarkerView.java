
package com.greencross.greencare.chartview.valueFormat;

import android.content.Context;
import android.widget.TextView;

import com.greencross.greencare.R;
import com.greencross.greencare.charting.components.MarkerView;
import com.greencross.greencare.charting.utils.MPPointF;


/**
 * Custom implementation of the MarkerView.
 * 
 * @author Philipp Jahoda
 */
public class MyMarkerView extends MarkerView {

    private TextView tvContent;

    public MyMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);

        tvContent = (TextView) findViewById(R.id.tvContent);
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}
