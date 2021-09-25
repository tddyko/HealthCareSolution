package com.greencross.greencare.sample;

/**
 * Created by MrsWin on 2017-03-06.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.greencross.greencare.base.BaseFragment;
import com.greencross.greencare.base.IBaseFragment;

public class DrawPathFragment extends BaseFragment implements IBaseFragment {

    public static Fragment newInstance() {
        DrawPathFragment fragment = new DrawPathFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView vm = new myView(getContext());
        return vm;
    }


    protected class myView extends View {

        public myView(Context context) {
            super(context);
            // TODO Auto-generated constructor stub
        }

        public void onDraw(Canvas canvas)
        {
            canvas.drawColor(Color.WHITE);
            Path path = new Path();

            Paint pnt = new Paint();
            pnt.setStrokeWidth(5);
            pnt.setColor(Color.RED);
            pnt.setStyle(Paint.Style.STROKE);

            // 원, 사각형을 패스로 정의한 후 출력
            path.addCircle(50, 50, 40, Path.Direction.CW);
            path.addRect(100, 10, 150, 90, Path.Direction.CW);
            canvas.drawPath(path, pnt);

            // 직선 곡선을 패스로 정의한 후 출력
            path.reset();
            path.moveTo(10,110);
            path.lineTo(50, 150);
            path.rLineTo(50, -30);
            path.quadTo(120, 170, 200, 110);
            pnt.setStrokeWidth(3);
            pnt.setColor(Color.GRAY);
            canvas.drawPath(path, pnt);

            // 곡선 패스 출력
            path.reset();
            path.moveTo(10,220);
            path.cubicTo(80,150,150,220,220,180);
            pnt.setStrokeWidth(2);
            pnt.setColor(Color.BLACK);
            canvas.drawPath(path, pnt);

            // 곡선 패스 위에 텍스트 출력
            pnt.setTextSize(20);
            pnt.setStyle(Paint.Style.FILL);
            pnt.setAntiAlias(true);
            canvas.drawTextOnPath("curved Text", path, 0, 0, pnt);
        }
    }
}