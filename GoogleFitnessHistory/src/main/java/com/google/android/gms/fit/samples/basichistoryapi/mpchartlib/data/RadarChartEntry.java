package com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.data;

import android.annotation.SuppressLint;

/**
 * Created by philipp on 13/06/16.
 */
@SuppressLint("ParcelCreator")
public class RadarChartEntry extends ChartEntry {

    public RadarChartEntry(float value) {
        super(0f, value);
    }

    public RadarChartEntry(float value, Object data) {
        super(0f, value, data);
    }

    /**
     * This is the same as getY(). Returns the value of the RadarChartEntry.
     *
     * @return
     */
    public float getValue() {
        return getY();
    }

    public RadarChartEntry copy() {
        RadarChartEntry e = new RadarChartEntry(getY(), getData());
        return e;
    }

    @Deprecated
    @Override
    public void setX(float x) {
        super.setX(x);
    }

    @Deprecated
    @Override
    public float getX() {
        return super.getX();
    }
}
