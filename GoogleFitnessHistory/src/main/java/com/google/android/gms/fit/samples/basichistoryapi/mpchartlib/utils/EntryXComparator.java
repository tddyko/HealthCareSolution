package com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.utils;

import com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.data.ChartEntry;

import java.util.Comparator;

/**
 * Comparator for comparing ChartEntry-objects by their x-value.
 * Created by philipp on 17/06/15.
 */
public class EntryXComparator implements Comparator<ChartEntry> {
    @Override
    public int compare(ChartEntry chartEntry1, ChartEntry chartEntry2) {
        float diff = chartEntry1.getX() - chartEntry2.getX();

        if (diff == 0f) return 0;
        else {
            if (diff > 0f) return 1;
            else return -1;
        }
    }
}
