package com.github.goldberg.customview.circularprogressbar;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.github.goldberg.customview.R;

/**
 * Created by mengzhun on 2016/11/14.
 */

public class IOSProgressView extends View {


    private static final String TAG = "IOSProgressView";

    private int mLeafCount;
    private static final int LEAF_COUNT_DEFAULT = 12;
    private int mColor;
    private static final int COLOR_DEFAULT = Color.BLACK;
    private int mRadius;
    private static final int RADIUS_DEFAULT = 36;
    private int mDuration;
    private static final int DURATION_DEFAULT = 800;


    private int rotateAngle;
    private ValueAnimator valueAnimator;
    private int mCurrentValue;
    private int[] colors;

    private Paint mPaint;
    private int mStrokeWidth = 6;
    private int mWidth;
    private int mHeight;
    private int mContentWidth;
    private int mContentHeight;
    private int mPaddingLeft;
    private int mPaddingTop;
    private int mPaddingRight;
    private int mPaddingBottom;
    private Point centrePoint;

    public IOSProgressView(Context context) {
        this(context, null);
    }

    public IOSProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IOSProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributeSet) {
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet,
                R.styleable.IOSProgressView);
        mColor = typedArray.getColor(R.styleable.IOSProgressView_iColor, COLOR_DEFAULT);
        mRadius = typedArray.getDimensionPixelSize(R.styleable.IOSProgressView_iRadius, RADIUS_DEFAULT);
        mLeafCount = typedArray.getInteger(R.styleable.IOSProgressView_iLeafCount, LEAF_COUNT_DEFAULT);
        mDuration = typedArray.getInteger(R.styleable.IOSProgressView_iDuration, DURATION_DEFAULT);
        mPaint = new Paint();
        mPaint.setColor(mColor);
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        mWidth = w;
        mHeight = h;
        mPaddingLeft = getPaddingLeft();
        mPaddingTop = getPaddingTop();
        mPaddingRight = getPaddingRight();
        mPaddingBottom = getPaddingBottom();
        mContentWidth = mWidth - mPaddingLeft - mPaddingRight;
        mContentHeight = mHeight - mPaddingTop - mPaddingBottom;
        centrePoint = new Point();
        if (mContentWidth > mContentHeight) {
            if (mRadius > mContentHeight / 2) {
                mRadius = mContentHeight / 2;
            }
        } else {
            if (mRadius > mContentWidth / 2) {
                mRadius = mContentWidth / 2;
            }
        }
        centrePoint.x = mPaddingLeft + mContentWidth / 2;
        centrePoint.y = mPaddingTop + mContentHeight / 2;
        rotateAngle = 360 / mLeafCount;
        initColors();
        valueAnimator = ValueAnimator.ofInt(mLeafCount, 0);
        valueAnimator.setDuration(mDuration);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setRepeatMode(ValueAnimator.RESTART);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCurrentValue = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
        valueAnimator.start();
    }

    private void initColors() {
        colors = new int[mLeafCount];
        int red = Color.red(mColor);
        int green = Color.green(mColor);
        int blue = Color.blue(mColor);
        int alpha = Color.alpha(mColor);
        for (int i = 0; i < mLeafCount; i++) {
            colors[i] = Color.argb(alpha * (i + 1) / mLeafCount, red, green, blue);
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (int i = 0; i < mLeafCount; i++) {
            canvas.rotate(rotateAngle, centrePoint.x, centrePoint.y);
            mPaint.setColor(colors[(i + mCurrentValue) % mLeafCount]);
            canvas.drawLine(centrePoint.x, centrePoint.y - mRadius / 2,
                    centrePoint.x, centrePoint.y - mRadius,
                    mPaint);
        }

    }


}
