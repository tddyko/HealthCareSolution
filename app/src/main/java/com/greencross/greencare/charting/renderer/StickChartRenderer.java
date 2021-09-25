
package com.greencross.greencare.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;

import com.greencross.greencare.charting.animation.ChartAnimator;
import com.greencross.greencare.charting.data.SticData;
import com.greencross.greencare.charting.data.SticEntry;
import com.greencross.greencare.charting.highlight.Highlight;
import com.greencross.greencare.charting.interfaces.dataprovider.SticDataProvider;
import com.greencross.greencare.charting.interfaces.datasets.ISticDataSet;
import com.greencross.greencare.charting.utils.ColorTemplate;
import com.greencross.greencare.charting.utils.MPPointD;
import com.greencross.greencare.charting.utils.Transformer;
import com.greencross.greencare.charting.utils.ViewPortHandler;

public class StickChartRenderer extends LineScatterCandleRadarRenderer {

    protected SticDataProvider mChart;

    private float[] mShadowBuffers = new float[8];
    private float[] mBodyBuffers = new float[4];
    private float[] mRangeBuffers = new float[4];
    private float[] mOpenBuffers = new float[4];
    private float[] mCloseBuffers = new float[4];

    public StickChartRenderer(SticDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(animator, viewPortHandler);
        mChart = chart;
    }

    @Override
    public void initBuffers() {
    }

    @Override
    public void drawData(Canvas c) {

        SticData candleData = mChart.getCandleData();

        for (ISticDataSet set : candleData.getDataSets()) {

            if (set.isVisible())
                drawDataSet(c, set);
        }
    }

    /**
     *  mrsohn Draw Candle Stick Chart
     * @param c
     * @param dataSet
     */
    @SuppressWarnings("ResourceAsColor")
    protected void drawDataSet(Canvas c, ISticDataSet dataSet) {

        Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

        float phaseY = mAnimator.getPhaseY();
        float barSpace = dataSet.getBarSpace();
        boolean showCandleBar = dataSet.getShowCandleBar();

        mXBounds.set(mChart, dataSet);

        mRenderPaint.setStrokeWidth(dataSet.getShadowWidth());

        // draw the body
        for (int j = mXBounds.min; j <= mXBounds.range + mXBounds.min; j++) {

            // get the entry
            SticEntry e = dataSet.getEntryForIndex(j);

            if (e == null)
                continue;

            final float xPos = e.getX();

            final float open = e.getOpen();
            final float high = e.getHigh();
            final float low = e.getLow();

            if (showCandleBar) {
                // calculate the shadow

                mShadowBuffers[0] = xPos;
                mShadowBuffers[2] = xPos;
                mShadowBuffers[4] = xPos;
                mShadowBuffers[6] = xPos;

                mShadowBuffers[1] = high * phaseY;
                mShadowBuffers[3] = open * phaseY;
                mShadowBuffers[5] = low * phaseY;
                mShadowBuffers[7] = mShadowBuffers[3];

                trans.pointValuesToPixel(mShadowBuffers);

                // draw the shadows

                if (dataSet.getShadowColorSameAsCandle()) {

                        mRenderPaint.setColor(
                                dataSet.getNeutralColor() == ColorTemplate.COLOR_NONE ?
                                        dataSet.getColor(j) :
                                        dataSet.getNeutralColor()
                        );

                } else {
                    mRenderPaint.setColor(
                            dataSet.getShadowColor() == ColorTemplate.COLOR_NONE ?
                                    dataSet.getColor(j) :
                                    dataSet.getShadowColor()
                    );
                }

                mRenderPaint.setStyle(Paint.Style.STROKE);

                // 고가, 저가 라인
                c.drawLines(mShadowBuffers, mRenderPaint);

                // calculate the body

                mBodyBuffers[0] = xPos - 0.5f + barSpace;
                mBodyBuffers[2] = (xPos + 0.5f - barSpace);
                mBodyBuffers[3] = open * phaseY;

                trans.pointValuesToPixel(mBodyBuffers);

                // draw body differently for increasing and decreasing entry
                if (dataSet.getDecreasingColor() == ColorTemplate.COLOR_NONE) {
                    mRenderPaint.setColor(dataSet.getColor(j));
                } else {
                    mRenderPaint.setColor(dataSet.getDecreasingColor());
                }

                mRenderPaint.setStyle(dataSet.getDecreasingPaintStyle());

                // 상승, 고가, 저가 네모

                float circleSize = 10f;

                // 고가
                mRenderPaint.setColor(Color.GRAY);
                c.drawCircle(mShadowBuffers[0], mShadowBuffers[1], circleSize, mRenderPaint);
                // 저가
                mRenderPaint.setColor(Color.GREEN);
                c.drawCircle(mShadowBuffers[0], mShadowBuffers[5], circleSize, mRenderPaint);

                mRenderPaint.setColor(Color.MAGENTA);
                c.drawCircle(mShadowBuffers[0], mShadowBuffers[3], circleSize, mRenderPaint);


                mRenderPaint.setColor(Color.BLUE);
                c.drawCircle(mShadowBuffers[0], mShadowBuffers[7], circleSize, mRenderPaint);

            } else {

                mRangeBuffers[0] = xPos;
                mRangeBuffers[1] = high * phaseY;
                mRangeBuffers[2] = xPos;
                mRangeBuffers[3] = low * phaseY;

                mOpenBuffers[0] = xPos - 0.5f + barSpace;
                mOpenBuffers[1] = open * phaseY;
                mOpenBuffers[2] = xPos;
                mOpenBuffers[3] = open * phaseY;

                mCloseBuffers[0] = xPos + 0.5f - barSpace;
                mCloseBuffers[2] = xPos;

                trans.pointValuesToPixel(mRangeBuffers);
                trans.pointValuesToPixel(mOpenBuffers);
                trans.pointValuesToPixel(mCloseBuffers);

                // draw the ranges
                int barColor;
                    barColor = dataSet.getNeutralColor() == ColorTemplate.COLOR_NONE
                            ? dataSet.getColor(j)
                            : dataSet.getNeutralColor();

                mRenderPaint.setColor(barColor);
                c.drawLine(
                        mRangeBuffers[0], mRangeBuffers[1],
                        mRangeBuffers[2], mRangeBuffers[3],
                        mRenderPaint);
                c.drawLine(
                        mOpenBuffers[0], mOpenBuffers[1],
                        mOpenBuffers[2], mOpenBuffers[3],
                        mRenderPaint);
                c.drawLine(
                        mCloseBuffers[0], mCloseBuffers[1],
                        mCloseBuffers[2], mCloseBuffers[3],
                        mRenderPaint);
            }

            // mrsohn 투약시간 그리기
            float xTimeLine = c.getWidth() / 25.5f; //800;  // width / 총시간
            xTimeLine = xTimeLine * 20; // 나눈시간 / 표시할 시간
            Path mPath;
            mPath = new Path();
            mPath.moveTo(xTimeLine, 0);
            mPath.quadTo(xTimeLine, 0, xTimeLine, c.getHeight());
            Paint mPaint = new Paint();
            mPaint.setColor(Color.parseColor("#ff0000"));
            mPaint.setStrokeWidth(5f); //선의 굵기
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setPathEffect(new DashPathEffect(new float[]{10, 10, 10, 10}, 0));
            c.drawPath(mPath, mPaint);

            mPaint = new Paint();
            mPaint.setTextSize(50f);
            mPaint.setColor(Color.parseColor("#ff0000"));
            c.drawText("투약시간", xTimeLine + 10, c.getHeight()/6, mPaint);
        }
    }

    /**
     * 캔들 상단 값
     * @param c
     */
    @Override
    public void drawValues(Canvas c) {
    }

    @Override
    public void drawExtras(Canvas c) {
    }

    @Override
    public void drawHighlighted(Canvas c, Highlight[] indices) {

        SticData candleData = mChart.getCandleData();

        for (Highlight high : indices) {

            ISticDataSet set = candleData.getDataSetByIndex(high.getDataSetIndex());

            if (set == null || !set.isHighlightEnabled())
                continue;

            SticEntry e = set.getEntryForXValue(high.getX(), high.getY());

            if (!isInBoundsX(e, set))
                continue;

            float lowValue = e.getLow() * mAnimator.getPhaseY();
            float highValue = e.getHigh() * mAnimator.getPhaseY();
            float y = (lowValue + highValue) / 2f;

            MPPointD pix = mChart.getTransformer(set.getAxisDependency()).getPixelForValues(e.getX(), y);

            high.setDraw((float) pix.x, (float) pix.y);

            // draw the lines
            drawHighlightLines(c, (float) pix.x, (float) pix.y, set);
        }
    }
}
