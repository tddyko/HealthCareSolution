package com.greencross.greencare.sample;

/**
 * Created by MrsWin on 2017-03-06.
 */


/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposePathEffect;
import android.graphics.CornerPathEffect;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathDashPathEffect;
import android.graphics.PathEffect;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.greencross.greencare.base.BaseFragment;
import com.greencross.greencare.base.IBaseFragment;

/*
 * 액티비티 화면에 여러개의 선들을 그려서, 각 선들에 효과(PathEffect)를 주고 있습니다.
 * 커스텀 뷰에 선(path)을 그리고 선에 효과를 주는 처리를 정의하고 있습니다.
 * 꼭 선에만 효과를 줄 수 있는것은 아니구요, 사각형(도형) 이나 글자 출력  등의 선이외의
 * 다른 그리기 처리시에도 그 효과를 적용할 수 있습니다.
 * 페인트객체(Paint)의 setPathEffect 메소드에 효과 객체를 넘겨서, 해당 효과를 적용할
 * 수 있습니다. 그리고 캔버스(canvas)객체에 페인트 객체를 적용하여, 선(drawPath메소드)
 * 뿐만이 아니라 여러가지 그리기를 수행할때도 해당 그리기에 페인트객체에 적용된 효과를
 * 적용할 수 있습니다.
 */
public class PathEffectsFragment extends BaseFragment implements IBaseFragment {

    public static Fragment newInstance() {
        PathEffectsFragment fragment = new PathEffectsFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return new SampleView(getContext());
    }


    /*
     * 선을 그리는 커스텀뷰 클래스 입니다.
     */
    private class SampleView extends View {
        private Paint mPaint;
        private Path mPath;
        private PathEffect[] mEffects; //여러가지 효과객체를 저장하는 배열변수
        private int[] mColors;
        private float mPhase;

        // 점선효과를 주는 효과객체를 반환하는 메소드입니다.
        private PathEffect makeDash(float phase) {
            return new DashPathEffect(new float[]{15, 5, 8, 5}, phase);
        }

        //phase : 점선일때 점선간격을 조정하는 변수입니다.
        private void makeEffects(PathEffect[] e, float phase) {
            //Paint.setPathEffect 에 널값을 넘기면, 현재 설정된 효과를 제거합니다.
            e[0] = null;     // no effect
            //끝이 둥근 선을 그리고, 인자는 반지름을 의미합니다.
            e[1] = new CornerPathEffect(10);
            e[2] = new DashPathEffect(new float[]{10, 5, 5, 5}, phase);
            e[3] = new PathDashPathEffect(makePathDash(), 12, phase,
                    PathDashPathEffect.Style.ROTATE);
            e[4] = new ComposePathEffect(e[2], e[1]);
            e[5] = new ComposePathEffect(e[3], e[1]);
        }

        /*
         * 생성자에서 페인트 객체를 초기화 처리하고 있습니다.
         */
        public SampleView(Context context) {
            super(context);
            setFocusable(true);
            setFocusableInTouchMode(true);
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(6);

            mPath = makeFollowPath();

            //여섯개의 효과객체를 설정하고 있습니다.
            mEffects = new PathEffect[6];

            mColors = new int[]{Color.BLACK, Color.RED, Color.BLUE,
                    Color.GREEN, Color.MAGENTA, Color.BLACK
            };
        }

        /*
         * (non-Javadoc)
         * @see android.view.View#onDraw(android.graphics.Canvas)
         * 실제 그리기를 수행할때 시스템에서 호출하는 메소드입니다.
         * invalidate 를 호출하여, 실시간으로 화면을 갱신하게 됩니다.
         * 점선이 계속해서 움직이는 효과를 처리하게 됩니다.
         */
        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawColor(Color.WHITE);

            RectF bounds = new RectF();
            mPath.computeBounds(bounds, false);
            canvas.translate(10 - bounds.left, 10 - bounds.top);

            makeEffects(mEffects, mPhase);
            mPhase += 1;
            invalidate();
            for (int i = 0; i < mEffects.length; i++) {
                //페인트 객체에 효과를 적용합니다.
                mPaint.setPathEffect(mEffects[i]);
                //페인트 객체에  그리기 색상값을 설정합니다.
                mPaint.setColor(mColors[i]);
                //위의 페인트에 설정된 내용대로, 화면(canvas)에 그리기를
                //수행합니다. drawPath는 선을 그립니다.
                //선의 경로는 위의 makeEffects(mEffects, mPhase); 에서
                //설정하고 있습니다. 그리고 선의 간격값을 mPhase 값으로 조정하여,
                //invalidate 메소드로 화면을 갱신하면서 계속해서 바뀌게 되어,
                //점섬이 계속해서 움직이게 보이게 됩니다.
                canvas.drawPath(mPath, mPaint);
                canvas.translate(0, 28);
            }
        }

        /*
         * (non-Javadoc)
         * @see android.view.View#onKeyDown(int, android.view.KeyEvent);
         * DPad 의 가운데 버튼을 눌렀을때, 새로운 선분을 생성합니다.
         */
        @Override
        public boolean onKeyDown(int keyCode, KeyEvent event) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_CENTER:
                    mPath = makeFollowPath();
                    return true;
            }
            return super.onKeyDown(keyCode, event);
        }

        //그리기할 선의 점들을 정의하여, 각 점들을 라인으로 그리고 있습니다.
        private Path makeFollowPath() {
            Path p = new Path();
            p.moveTo(0, 0);
            for (int i = 1; i <= 15; i++) {
                p.lineTo(i * 20, (float) Math.random() * 35);
            }
            return p;
        }

        // 점선을 그리는 Path 객체를 반환합니다.
        private Path makePathDash() {
            Path p = new Path();
            p.moveTo(4, 0);
            p.lineTo(0, -4);
            p.lineTo(8, -4);
            p.lineTo(12, 0);
            p.lineTo(8, 4);
            p.lineTo(0, 4);
            return p;
        }
    }
}

