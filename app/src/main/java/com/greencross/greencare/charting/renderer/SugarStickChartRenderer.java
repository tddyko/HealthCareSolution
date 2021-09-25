
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
import com.greencross.greencare.base.value.Define;
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
import com.greencross.greencare.database.DBHelper;
import com.greencross.greencare.database.DBHelperSugar;
import com.greencross.greencare.base.value.TypeDataSet;
import com.greencross.greencare.util.CDateUtil;
import com.greencross.greencare.util.ChartTimeUtil;
import com.greencross.greencare.util.Logger;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class SugarStickChartRenderer extends LineScatterCandleRadarRenderer {
    private final String TAG = SugarStickChartRenderer.class.getSimpleName();

    protected SticDataProvider mChart;
    private Context mContext;
    private ChartTimeUtil mTimeClass;

    private float[] mShadowBuffers = new float[8];
    private float[] mBodyBuffers = new float[4];
    private float[] mRangeBuffers = new float[4];
    private float[] mOpenBuffers = new float[4];
    private float[] mCloseBuffers = new float[4];

    public SugarStickChartRenderer(SticDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler, Context context,  ChartTimeUtil timeClass) {
        super(animator, viewPortHandler);
        mContext = context;
        mChart = chart;
        mTimeClass = timeClass;
    }

    public ChartAnimator getChartAnimator() {
        return mAnimator;
    }

    public void setChartAnimator(ChartAnimator mAnimator) {
        this.mAnimator = mAnimator;
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

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();
        float barSpace = dataSet.getBarSpace();
        boolean showCandleBar = dataSet.getShowCandleBar();

        TypeDataSet.EatState EatState = mTimeClass.getEatType();
        if(EatState == TypeDataSet.EatState.TYPE_ALL){

        }else{
            fillBackGroundColor(c, phaseY, trans);
        }


        mXBounds.set(mChart, dataSet);

        mRenderPaint.setStrokeWidth(dataSet.getShadowWidth());

        // draw the body
        for (int j = mXBounds.min; j <= mXBounds.range + mXBounds.min; j++) {

            // get the entry
            SticEntry e = dataSet.getEntryForIndex(j);

            if (e == null)
                continue;

            final float xPos = e.getX() + 1;

            final float beforeAndAfterType = e.getOpen();
            final float high = e.getHigh();
            final float low = e.getLow();

            Logger.i(TAG, TAG+"["+j+"] open="+beforeAndAfterType+", high="+high+", low="+low);

            if (showCandleBar) {
                // calculate the shadow

                mShadowBuffers[0] = xPos;
                mShadowBuffers[2] = xPos;
                mShadowBuffers[4] = xPos;
                mShadowBuffers[6] = xPos;

                mShadowBuffers[1] = high * phaseY;
                mShadowBuffers[3] = low * phaseY;
                mShadowBuffers[5] = beforeAndAfterType;//open * phaseY;
                mShadowBuffers[7] = mShadowBuffers[3];

                trans.pointValuesToPixel(mShadowBuffers);

                // draw the shadows

                mRenderPaint.setStyle(Paint.Style.STROKE);

                // 고가, 저가 라인
                // calculate the body

                mBodyBuffers[0] = xPos - 0.5f + barSpace;
                mBodyBuffers[2] = (xPos + 0.5f - barSpace);
                mBodyBuffers[3] = beforeAndAfterType * phaseY;

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
                // 식후
                if (Define.SUGAR_TYPE_BEFORE != beforeAndAfterType) {
                    mRenderPaint.setColor(Color.parseColor("#ff9902"));
                    c.drawCircle(mShadowBuffers[0], mShadowBuffers[1], circleSize, mRenderPaint);
                }

                // 식전
                if (Define.SUGAR_TYPE_AFTER != beforeAndAfterType) {
                    mRenderPaint.setColor(Color.parseColor("#8fc31f"));
                    c.drawCircle(mShadowBuffers[0], mShadowBuffers[3], circleSize, mRenderPaint);
                }

            } else {
                mRangeBuffers[0] = xPos;
                mRangeBuffers[1] = high * phaseY;
                mRangeBuffers[2] = xPos;
                mRangeBuffers[3] = low * phaseY;

                mOpenBuffers[0] = xPos - 0.5f + barSpace;
                mOpenBuffers[1] = beforeAndAfterType * phaseY;
                mOpenBuffers[2] = xPos;
                mOpenBuffers[3] = beforeAndAfterType * phaseY;

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
        }

        dotLinesVertical(c, trans, 200);
    }

    /**
     * 세로라인그리기
     * @param c
     * @param phaseY
     * @param trans
     */
    private void dotLinesVertical(Canvas c, Transformer trans, float... phaseY) {
        if (mTimeClass == null)
            return;

        String format = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String yyyy_MM_dd1 = sdf.format(mTimeClass.getStartTime());
        String yyyy_MM_dd2 = sdf.format(mTimeClass.getEndTime());

        TypeDataSet.Period tp = mTimeClass.getPeriodType();
        Logger.i(TAG, "TypeDataSet.Period:"+tp);


        // mrsohn 투약시간 그리기
        DBHelper db = new DBHelper(mContext);
        DBHelperSugar sugDb = db.getSugarDb();
        List<DBHelperSugar.MedicenData> arrayData = sugDb.getMedicenTime(yyyy_MM_dd1, yyyy_MM_dd2);

        int cur=-1;
        int bef=-1;
        for(int i = 0 ; i < arrayData.size() ; i++){
            Calendar cal = CDateUtil.getCalendar_yyyy_MM_dd_HH_mm(arrayData.get(i).getMedi_regdate());


            if (mTimeClass.getPeriodType() == TypeDataSet.Period.PERIOD_DAY) {
                cur = cal.get(java.util.Calendar.HOUR_OF_DAY);     //일
            }else if (mTimeClass.getPeriodType() == TypeDataSet.Period.PERIOD_WEEK) {
                cur    = cal.get(java.util.Calendar.DAY_OF_WEEK);  //주
            }else if (mTimeClass.getPeriodType() == TypeDataSet.Period.PERIOD_MONTH) {
                cur = cal.get(java.util.Calendar.DAY_OF_MONTH);    //월
            }
            if (bef==cur) return;
            bef = cur;


            Logger.i(TAG, "dotLinesVertical cur["+cur+"]");
            Logger.i(TAG, "mXBounds.max="+ (cur / mXBounds.max));
            MPPointD pointD = trans.getPixelForValues(cur, 0);

            float xTimeLine = (float) pointD.x; //800;  // width / 총시간

            Path path = new Path();
            Paint paint = new Paint();

            path.moveTo(xTimeLine, 0);
            path.quadTo(xTimeLine, 0, xTimeLine, c.getHeight());
            paint.setColor(Color.parseColor("#ff0000"));
            paint.setStrokeWidth(2f); //선의 굵기
            paint.setStyle(Paint.Style.STROKE);
            paint.setPathEffect(new DashPathEffect(new float[]{10, 10, 10, 10}, 0));
            c.drawPath(path, paint);

            mShadowBuffers[1] = 240.0f * 1.0f;
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
        Path path = new Path();
        Paint paint = new Paint();
        float[] values = new float[2];
        int j = 1;
        for (int i = 0; i <= phaseY.length-1; i++) {
            values[1] = phaseY[i];
            trans.pointValuesToPixel(values);

            float yPos = values[1];
            path.moveTo(0, yPos);
            path.quadTo(0, yPos, c.getWidth(), yPos);
            paint.setColor(Color.parseColor("#00ff00"));
            paint.setStrokeWidth(2f); //선의 굵기
            paint.setStyle(Paint.Style.STROKE);
            paint.setPathEffect(new DashPathEffect(new float[]{10, 10, 10, 10}, 0));
            c.drawPath(path, paint);
        }
    }


    /**
     * MrSohn BackGround 색깔 채우기
     * @return
     */
    public void fillBackGroundColor(Canvas c, float phaseY, Transformer trans)  {
        float points[] = new float[10];

        TypeDataSet.EatState EatState = mTimeClass.getEatType();
        if(EatState == TypeDataSet.EatState.TYPE_BEFORE) {
            points[1] = phaseY * 300;
            points[3] = phaseY * 125;   // 126이상 주황
            points[5] = phaseY * 100;   // 100이상 노랑
            points[7] = phaseY * 10;    // 100미만 하늘
        }else{
            points[1] = phaseY * 300;
            points[3] = phaseY * 200;   // 이상 주황
            points[5] = phaseY * 140;   // 100이상 노랑
            points[7] = phaseY * 10;    // 100미만 하늘
        }


        // 짝수(0,2,4) x축 계산, 홀수(1,3,5..) Y축 계산
        trans.pointValuesToPixel(points);

        Paint circlePaint = new Paint();
        circlePaint.setColor(Color.RED);
        c.drawCircle(150, points[1], 20f, circlePaint);

        Paint paint = new Paint();
        float left = mViewPortHandler.contentLeft();//mViewPortHandler.offsetLeft();
        float right = mViewPortHandler.contentRight();
        // 126 이상 주황
        paint.setColor(Color.rgb(248, 225, 209));
        c.drawRect(left, points[1] , right, points[3], paint);
        // 100~126 노랑
        paint.setColor(Color.rgb(253, 240, 208));
        c.drawRect(left, points[3] , right, points[5], paint);
        // 100 미만 하늘색
        paint.setColor(Color.rgb(218, 230, 244));
        c.drawRect(left, points[5] , right, points[7], paint);
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
