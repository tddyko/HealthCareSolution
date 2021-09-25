
package com.greencross.greencare.charting.renderer;


import com.greencross.greencare.charting.utils.ViewPortHandler;
import com.greencross.greencare.util.ChartTimeUtil;

/**
 * Abstract baseclass of all Renderers.
 * 
 * @author Philipp Jahoda
 */
public abstract class Renderer {

    /**
     * the component that handles the drawing area of the chart and it's offsets
     */
    protected ViewPortHandler mViewPortHandler;

    public Renderer(ViewPortHandler viewPortHandler) {
        this.mViewPortHandler = viewPortHandler;
    }

    public void setTimeClass(ChartTimeUtil timeClass) {
    }
}
