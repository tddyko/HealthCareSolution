package com.greencross.greencare.charting.utils;

import com.greencross.greencare.charting.data.CEntry;

import java.util.Comparator;

/**
 * Comparator for comparing CEntry-objects by their x-value.
 * Created by philipp on 17/06/15.
 */
public class EntryXComparator implements Comparator<CEntry> {
    @Override
    public int compare(CEntry entry1, CEntry entry2) {
        float diff = entry1.getX() - entry2.getX();

        if (diff == 0f) return 0;
        else {
            if (diff > 0f) return 1;
            else return -1;
        }
    }
}
