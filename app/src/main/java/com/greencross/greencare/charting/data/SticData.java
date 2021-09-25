package com.greencross.greencare.charting.data;

import com.greencross.greencare.charting.interfaces.datasets.ISticDataSet;

import java.util.List;

public class SticData extends BarLineScatterCandleBubbleData<ISticDataSet> {

    public SticData() {
        super();
    }

    public SticData(List<ISticDataSet> dataSets) {
        super(dataSets);
    }

    public SticData(ISticDataSet... dataSets) {
        super(dataSets);
    }
}
