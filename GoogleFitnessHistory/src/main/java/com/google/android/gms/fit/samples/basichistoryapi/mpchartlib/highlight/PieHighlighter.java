package com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.highlight;

import com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.charts.PieChart;
import com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.data.ChartEntry;
import com.google.android.gms.fit.samples.basichistoryapi.mpchartlib.interfaces.datasets.IPieDataSet;

/**
 * Created by philipp on 12/06/16.
 */
public class PieHighlighter extends PieRadarHighlighter<PieChart> {

    public PieHighlighter(PieChart chart) {
        super(chart);
    }

    @Override
    protected Highlight getClosestHighlight(int index, float x, float y) {

        IPieDataSet set = mChart.getData().getDataSet();

        final ChartEntry chartEntry = set.getEntryForIndex(index);

        return new Highlight(index, chartEntry.getY(), x, y, 0, set.getAxisDependency());
    }
}
