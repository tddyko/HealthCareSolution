
package com.google.android.gms.fit.samples.basichistoryapi.chart.valueFormat;

import android.content.Context;
import android.widget.TextView;

import com.google.android.gms.fit.samples.basichistoryapi.R;
import com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.components.MarkerView;
import com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.data.CandleChartEntry;
import com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.data.ChartEntry;
import com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.highlight.Highlight;
import com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.utils.MPPointF;
import com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.utils.Utils;

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
    public void refreshContent(ChartEntry e, Highlight highlight) {

        if (e instanceof CandleChartEntry) {

            CandleChartEntry ce = (CandleChartEntry) e;

            tvContent.setText("" + Utils.formatNumber(ce.getHigh(), 0, true));
        } else {

            tvContent.setText("" + Utils.formatNumber(e.getY(), 0, true));
        }

        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}
