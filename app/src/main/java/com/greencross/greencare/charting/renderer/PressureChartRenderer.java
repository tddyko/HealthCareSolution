
package com.greencross.greencare.charting.renderer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;

import com.greencross.greencare.R;
import com.greencross.greencare.base.value.TypeDataSet;
import com.greencross.greencare.bluetooth.model.PressureModel;
import com.greencross.greencare.charting.animation.ChartAnimator;
import com.greencross.greencare.charting.data.PressureData;
import com.greencross.greencare.charting.data.PressureEntry;
import com.greencross.greencare.charting.highlight.Highlight;
import com.greencross.greencare.charting.interfaces.dataprovider.PresureDataProvider;
import com.greencross.greencare.charting.interfaces.datasets.IPresureDataSet;
import com.greencross.greencare.charting.utils.ColorTemplate;
import com.greencross.greencare.charting.utils.MPPointD;
import com.greencross.greencare.charting.utils.Transformer;
import com.greencross.greencare.charting.utils.ViewPortHandler;
import com.greencross.greencare.database.DBHelper;
import com.greencross.greencare.database.DBHelperPresure;
import com.greencross.greencare.util.CDateUtil;
import com.greencross.greencare.util.ChartTimeUtil;
import com.greencross.greencare.util.Logger;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class PressureChartRenderer extends LineScatterCandleRadarRenderer {
    private final String TAG = PressureChartRenderer.class.getSimpleName();

    protected PresureDataProvider mChart;
    private Context mContext;
    private ChartTimeUtil mTimeClass;

    private float[] mShadowBuffers = new float[8];
    private float[] mBodyBuffers = new float[4];
    private float[] mRangeBuffers = new float[4];
    private float[] mOpenBuffers = new float[4];
    private float[] mCloseBuffers = new float[4];

    public PressureChartRenderer(PresureDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler, Context context) {
        super(animator, viewPortHandler);
        mContext = context;
        mChart = chart;

    }

    public void setTimeClass(ChartTimeUtil timeClass) {
        mTimeClass = timeClass;
    }

    @Override
    public void initBuffers() {

    }

    @Override
    public void drawData(Canvas c) {

        PressureData PressureData = mChart.getPresureData();

        for (IPresureDataSet set : PressureData.getDataSets()) {

            if (set.isVisible())
                drawDataSet(c, set);
        }
    }

    @SuppressWarnings("ResourceAsColor")
    protected void drawDataSet(Canvas c, IPresureDataSet dataSet) {

        Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();
        float barSpace = dataSet.getBarSpace();
        boolean showCandleBar = dataSet.getShowCandleBar();

        mXBounds.set(mChart, dataSet);

        mRenderPaint.setStrokeWidth(dataSet.getShadowWidth());

        // draw the body
        for (int j = mXBounds.min; j <= mXBounds.range + mXBounds.min; j++) {
            // get the entry
            PressureEntry e = dataSet.getEntryForIndex(j);

            if (e == null)
                continue;

            final float xPos = e.getX();

            final float open = e.getOpen();
            final float close = e.getClose();
            final float high = e.getHigh();
            final float low = e.getLow();

            if (showCandleBar) {
                // calculate the shadow

                // X축 값
                mShadowBuffers[0] = xPos;
                mShadowBuffers[2] = xPos;
                mShadowBuffers[4] = xPos;
                mShadowBuffers[6] = xPos;

                // Y축 값
                mShadowBuffers[1] = high * phaseY;
                mShadowBuffers[3] = low * phaseY;
                mShadowBuffers[5] = open * phaseY;
                mShadowBuffers[7] = close * phaseY;

                trans.pointValuesToPixel(mShadowBuffers);

                // 고가, 저가 라인
                mRenderPaint.setColor(Color.BLACK);
                // calculate the body
                mBodyBuffers[0] = xPos - 0.5f + barSpace;
                mBodyBuffers[1] = close * phaseY;
                mBodyBuffers[2] = (xPos + 0.5f - barSpace);
                mBodyBuffers[3] = open * phaseY;

                trans.pointValuesToPixel(mBodyBuffers);

                if (dataSet.getDecreasingColor() == ColorTemplate.COLOR_NONE) {
                    mRenderPaint.setColor(dataSet.getColor(j));
                } else {
                    mRenderPaint.setColor(dataSet.getDecreasingColor());
                }

                mRenderPaint.setStyle(dataSet.getDecreasingPaintStyle());
                float xPosition = mShadowBuffers[0];

                mRenderPaint.setColor(Color.BLACK);

                TypeDataSet.Period period = mTimeClass.getPeriodType();
                if(period == TypeDataSet.Period.PERIOD_DAY){
                    // 라인 부터 그리기
                    c.drawLine(xPosition, mShadowBuffers[1], xPosition, mShadowBuffers[5], mRenderPaint);
                    // 수축기
                    mRenderPaint.setColor(Color.parseColor("#1DB6AE"));
                    c.drawCircle(xPosition, mShadowBuffers[1], 10f, mRenderPaint);

                    // 이완기
                    mRenderPaint.setColor(Color.parseColor("#9E72DB"));
                    c.drawCircle(xPosition, mShadowBuffers[5], 10f, mRenderPaint);
                }else{
                    // 수축기
                    mRenderPaint.setColor(Color.parseColor("#1DB6AE"));
                    c.drawCircle(xPosition, mShadowBuffers[1], 10f, mRenderPaint);
                    c.drawCircle(xPosition, mShadowBuffers[3], 10f, mRenderPaint);

                    // 이완기
                    mRenderPaint.setColor(Color.parseColor("#9E72DB"));
                    c.drawCircle(xPosition, mShadowBuffers[5], 10f, mRenderPaint);
                    c.drawCircle(xPosition, mShadowBuffers[7], 10f, mRenderPaint);
                }

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
                mCloseBuffers[1] = close * phaseY;
                mCloseBuffers[2] = xPos;
                mCloseBuffers[3] = close * phaseY;

                trans.pointValuesToPixel(mRangeBuffers);
                trans.pointValuesToPixel(mOpenBuffers);
                trans.pointValuesToPixel(mCloseBuffers);
            }
        }
        dotLinesVertical(c, trans, 200);
        dotLinesHozontal(c, trans, 90,140);
    }

    /**
     * 세로라인그리기
     * @param c
     * @param phaseY
     * @param trans
     */
    private void dotLinesVertical(Canvas c, Transformer trans, float... phaseY) {
        if (mTimeClass == null) {
            Logger.e(TAG, "dotLinesVertical timeclass is null");
            return;
        } else {
            Logger.i(TAG, "dotLinesVertical timeclass="+mTimeClass);
        }

        String format = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String yyyy_MM_dd1 = sdf.format(mTimeClass.getStartTime());
        String yyyy_MM_dd2 = sdf.format(mTimeClass.getEndTime());


        TypeDataSet.Period tp = mTimeClass.getPeriodType();
        Logger.i(TAG, "TypeDataSet.Period:"+tp);


        // mrsohn 투약시간 그리기
        DBHelper db = new DBHelper(mContext);
        DBHelperPresure preDb = db.getPresureDb();
        List<PressureModel> arrayData = preDb.getMedicenTime(yyyy_MM_dd1, yyyy_MM_dd2);

        int cur=-1;
        int bef=-1;
        for(int i = 0 ; i < arrayData.size() ; i++){
            Calendar cal = CDateUtil.getCalendar_yyyy_MM_dd_HH_mm(arrayData.get(i).getRegdate());

            if (mTimeClass.getPeriodType() == TypeDataSet.Period.PERIOD_DAY) {
                cur = cal.get(java.util.Calendar.HOUR_OF_DAY);     //일
            }else if (mTimeClass.getPeriodType() == TypeDataSet.Period.PERIOD_WEEK) {
                cur    = cal.get(java.util.Calendar.DAY_OF_WEEK);  //주
            }else if (mTimeClass.getPeriodType() == TypeDataSet.Period.PERIOD_MONTH) {
                cur = cal.get(java.util.Calendar.DAY_OF_MONTH);    //월
            }
            if (bef==cur) return;
            bef = cur;

            Logger.i(TAG, "dotLinesVertical hour["+cur+"] ");
            Logger.i(TAG, "mXBounds.max="+ (cur / mXBounds.max));
            MPPointD pointD = trans.getPixelForValues(cur, 0);

            float xTimeLine = (float) pointD.x;
            Path path = new Path();
            Paint paint = new Paint();

            path.moveTo(xTimeLine, 0);
            path.quadTo(xTimeLine, 0, xTimeLine, c.getHeight());
            paint.setColor(Color.parseColor("#ff0000"));
            paint.setStrokeWidth(2f); //선의 굵기
            paint.setStyle(Paint.Style.STROKE);
            paint.setPathEffect(new DashPathEffect(new float[]{10, 10, 10, 10}, 0));
            c.drawPath(path, paint);

            mShadowBuffers[1] = 210.0f * 1.0f;
            trans.pointValuesToPixel(mShadowBuffers);

            Bitmap image1;
            image1 = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon_medi);
            Rect dst = new Rect((int)xTimeLine-15, (int)mShadowBuffers[1], (int)xTimeLine + 15, (int)mShadowBuffers[1]+30);
            c.drawBitmap(image1, null, dst, null);


        }
    }

    /**
     * 가로 라인 그리기
     * @param c
     * @param phaseY
     * @param trans
     */
    private void dotLinesHozontal(Canvas c, Transformer trans, float... phaseY) {

        float[] values = new float[2];
        for (int i = 0; i <= phaseY.length-1; i++) {
            Paint paint = new Paint();
            Path path = new Path();
            values[1] = phaseY[i];
            trans.pointValuesToPixel(values);

            float yPos = values[1];
            path.moveTo(0, yPos);
            path.quadTo(0, yPos, c.getWidth(), yPos);

            if(phaseY[i] == 90.0){
                paint.setColor(Color.parseColor("#1DB6EE")); // 이완기색
            }else{
                paint.setColor(Color.parseColor("#9E72AA")); // 수축기색
            }


            paint.setStrokeWidth(4f); //선의 굵기
            paint.setStyle(Paint.Style.STROKE);
            paint.setPathEffect(new DashPathEffect(new float[]{10, 10, 10, 10}, 0));
            c.drawPath(path, paint);
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

        PressureData PressureData = mChart.getPresureData();

        for (Highlight high : indices) {

            IPresureDataSet set = PressureData.getDataSetByIndex(high.getDataSetIndex());

            if (set == null || !set.isHighlightEnabled())
                continue;

            PressureEntry e = set.getEntryForXValue(high.getX(), high.getY());

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
