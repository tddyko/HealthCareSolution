package com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.data;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.util.Log;

/**
 * @author Philipp Jahoda
 */
@SuppressLint("ParcelCreator")
public class PieChartEntry extends ChartEntry {

    private String label;

    public PieChartEntry(float value) {
        super(0f, value);
    }

    public PieChartEntry(float value, Object data) {
        super(0f, value, data);
    }

    public PieChartEntry(float value, Drawable icon) {
        super(0f, value, icon);
    }

    public PieChartEntry(float value, Drawable icon, Object data) {
        super(0f, value, icon, data);
    }

    public PieChartEntry(float value, String label) {
        super(0f, value);
        this.label = label;
    }

    public PieChartEntry(float value, String label, Object data) {
        super(0f, value, data);
        this.label = label;
    }

    public PieChartEntry(float value, String label, Drawable icon) {
        super(0f, value, icon);
        this.label = label;
    }

    public PieChartEntry(float value, String label, Drawable icon, Object data) {
        super(0f, value, icon, data);
        this.label = label;
    }

    /**
     * This is the same as getY(). Returns the value of the PieChartEntry.
     *
     * @return
     */
    public float getValue() {
        return getY();
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Deprecated
    @Override
    public void setX(float x) {
        super.setX(x);
        Log.i("DEPRECATED", "Pie entries do not have x values");
    }

    @Deprecated
    @Override
    public float getX() {
        Log.i("DEPRECATED", "Pie entries do not have x values");
        return super.getX();
    }

    public PieChartEntry copy() {
        PieChartEntry e = new PieChartEntry(getY(), label, getData());
        return e;
    }
}
