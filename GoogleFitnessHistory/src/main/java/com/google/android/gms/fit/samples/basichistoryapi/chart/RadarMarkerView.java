
package com.google.android.gms.fit.samples.basichistoryapi.chart;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

import com.google.android.gms.fit.samples.basichistoryapi.R;
import com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.components.MarkerView;
import com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.data.ChartEntry;
import com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.highlight.Highlight;
import com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.utils.MPPointF;

import java.text.DecimalFormat;

/**
 * Custom implementation of the MarkerView.
 * 
 * @author Philipp Jahoda
 */
public class RadarMarkerView extends MarkerView {

    private TextView tvContent;
    private DecimalFormat format = new DecimalFormat("##0");

    public RadarMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);

        tvContent = (TextView) findViewById(R.id.tvContent);
        tvContent.setTypeface(Typeface.createFromAsset(context.getAssets(), "OpenSans-Light.ttf"));
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(ChartEntry e, Highlight highlight) {
        tvContent.setText(format.format(e.getY()) + " %");

        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight() - 10);
    }
}
