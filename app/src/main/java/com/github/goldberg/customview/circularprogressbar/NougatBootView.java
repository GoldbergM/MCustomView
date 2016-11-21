package com.github.goldberg.customview.circularprogressbar;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created by mengzhun on 2016/11/16.
 */

public class NougatBootView extends View {

    private static final String TAG = "NougatBootView";

    private int color = Color.parseColor("#DB4437");
    private int color1 = Color.parseColor("#F4B400");
    private int color2 = Color.parseColor("#4285F4");
    private int color3 = Color.parseColor("#0F9D58");
    private Paint mPaint;
    private Path path;
    private Path path1;
    private Path path2;
    private Path path3;
    private ValueAnimator valueAnimator;
    private int mWidth;
    private int mHeight;
    private int mContentWidth;
    private int mContentHeight;
    private float[] p0, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11;
    private float currentAngle;
    private static final int DURATION = 10000;
    private Point centrePoint;
    private int mRadius;
    private int mStrokeWidth;
    private static final int STROKE_WIDTH_DEFAULT = 8;


    private int widthDefault;
    private int heightDefault;

    private PathMeasure pathMeasure;
    private float[] currentPoint;
    private ValueAnimator pathAnimator;
    private float currentPathLength;

    public NougatBootView(Context context) {
        this(context, null);
    }

    public NougatBootView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NougatBootView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributeSet) {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SCREEN));
        path = new Path();
        path1 = new Path();
        path2 = new Path();
        path3 = new Path();
        valueAnimator = ValueAnimator.ofFloat(0, 360);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setDuration(DURATION);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setRepeatMode(ValueAnimator.RESTART);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentAngle = (float) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        Log.e(TAG, "onSizeChanged");
        mWidth = w;
        mHeight = h;
        mContentWidth = mWidth - getPaddingLeft() - getPaddingRight();
        mContentHeight = mHeight - getPaddingTop() - getPaddingBottom();
        centrePoint = new Point();
        centrePoint.x = getPaddingLeft() + mContentWidth / 2;
        centrePoint.y = getPaddingTop() + mContentHeight / 2;
        mRadius = Math.min(mWidth, mHeight) / 3;
        mStrokeWidth = mContentWidth / 30;
        mPaint.setStrokeWidth(mStrokeWidth);
        p0 = new float[]{centrePoint.x + mRadius, centrePoint.y};
        p1 = new float[]{centrePoint.x + mRadius, centrePoint.y + mRadius / 2};
        p2 = new float[]{centrePoint.x + mRadius / 2, centrePoint.y + mRadius};
        p3 = new float[]{centrePoint.x, centrePoint.y + mRadius};
        p4 = new float[]{centrePoint.x - mRadius * 3 / 4, centrePoint.y + mRadius};
        p5 = new float[]{centrePoint.x - mRadius, centrePoint.y + mRadius / 2};
        p6 = new float[]{centrePoint.x - mRadius, centrePoint.y};
        p7 = new float[]{centrePoint.x - mRadius, centrePoint.y - mRadius * 7 / 9};
        p8 = new float[]{centrePoint.x - mRadius / 2, centrePoint.y - mRadius};
        p9 = new float[]{centrePoint.x, centrePoint.y - mRadius};
        p10 = new float[]{centrePoint.x + mRadius / 2, centrePoint.y - mRadius};
        p11 = new float[]{centrePoint.x + mRadius, centrePoint.y - mRadius * 7 / 9};
        path.moveTo(p0[0], p0[1]);
        path.cubicTo(p1[0], p1[1], p2[0], p2[1], p3[0], p3[1]);
        path.cubicTo(p4[0], p4[1], p5[0], p5[1], p6[0], p6[1]);
        path.cubicTo(p7[0], p7[1], p8[0], p8[1], p9[0], p9[1]);
        path.cubicTo(p10[0], p10[1], p11[0], p11[1], p0[0], p0[1]);
        pathMeasure = new PathMeasure(path, true);
        currentPoint = new float[2];
        pathAnimator = ValueAnimator.ofFloat(0, pathMeasure.getLength());
        pathAnimator.setDuration(10000);
        pathAnimator.setInterpolator(new LinearInterpolator());
        pathAnimator.setRepeatCount(ValueAnimator.INFINITE);
        pathAnimator.setRepeatMode(ValueAnimator.RESTART);
        pathAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentPathLength = (float) animation.getAnimatedValue();
                pathMeasure.getPosTan(currentPathLength, currentPoint, null);
                invalidate();
            }
        });
        valueAnimator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        canvas.drawCircle(currentPoint[0], currentPoint[1], 15, mPaint);
        for (int i = 0; i < 4; i++) {
            int count = canvas.save();
            switch (i) {
                case 0:
                    mPaint.setColor(color);
                    canvas.rotate(currentAngle, centrePoint.x, centrePoint.y);
                    canvas.drawPath(path, mPaint);
                    break;
                case 1:
                    mPaint.setColor(color1);
                    canvas.rotate(-currentAngle + 70, centrePoint.x, centrePoint.y);
                    canvas.drawPath(path, mPaint);
                    break;
                case 2:
                    mPaint.setColor(color2);
                    canvas.rotate(currentAngle - 100, centrePoint.x, centrePoint.y);
                    canvas.drawPath(path, mPaint);
                    break;
                case 3:
                    mPaint.setColor(color3);
                    canvas.rotate(-currentAngle + 200, centrePoint.x, centrePoint.y);
                    canvas.drawPath(path, mPaint);
                    break;
            }

            canvas.restoreToCount(count);
        }

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.e(TAG, "onMeasure");
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {
            widthDefault = widthSize / 2;
            heightDefault = heightSize / 2;
            setMeasuredDimension(widthDefault, heightDefault);
        } else if (widthMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthDefault, heightSize);
        } else if (heightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSize, heightDefault);
        }
    }
}
