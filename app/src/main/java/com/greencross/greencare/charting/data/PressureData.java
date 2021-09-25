package com.greencross.greencare.charting.data;

import com.greencross.greencare.charting.interfaces.datasets.IPresureDataSet;

import java.util.List;

public class PressureData extends BarLineScatterCandleBubbleData<IPresureDataSet> {

    public PressureData() {
        super();
    }

    public PressureData(List<IPresureDataSet> dataSets) {
        super(dataSets);
    }

    public PressureData(IPresureDataSet... dataSets) {
        super(dataSets);
    }
}
