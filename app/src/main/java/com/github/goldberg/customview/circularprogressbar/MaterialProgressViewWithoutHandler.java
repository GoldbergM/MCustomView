package com.github.goldberg.customview.circularprogressbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.github.goldberg.customview.R;

/**
 * Created by mengzhun on 2016/11/21.
 */

public class MaterialProgressViewWithoutHandler extends View {

    private String TAG = "MaterialProgressViewWithoutHandler";
    private Paint mPaint;
    private float mBorderWidth;
    private static final int BORDER_WIDTH_DEFAULT = 2;

    private int[] mPaintColors;
    private static final int[] COLORS_DEFAULT = new int[]{
            Color.BLACK, Color.RED, Color.GREEN, Color.BLUE
    };

    private int mAngleDuration;
    private static final int ANGLE_DURATION_DEFAULT = 2000;

    private int mSweepDuration;
    private static final int SWEEP_DURATION_DEFAULT = 2000;


    private int mMinSweepAngle;//保留的最小角度
    private int mMinGapAngle;//最小间隙角度
    private static final int MIN_SWEEP_ANGLE_DEFAULT = 20;
    private static final int MIN_GAP_ANGLE_DEFAULT = 50;

    private int mCurrentAngle;//旋转的角度
    private int mCurrentSweepAngle;//draw的角度
    private int angleOffset;//每次sweep完毕往后的offset
    private boolean angleIncreasing;//draw角度增加，


    private int mWidth;
    private int mHeight;
    private int mContentWidth;
    private int mContentHeight;
    private int mRadius;
    private Point centrePoint;
    private int mPaddingLeft;
    private int mPaddingTop;
    private int mPaddingRight;
    private int mPaddingBottom;
    private RectF mRectF;

    private int currentColorIndex;
    private int nextColorIndex;

    private ValueAnimator angleValueAnimator;
    private ValueAnimator sweepValueAnimator;

    private int startAngle;
    private int sweepAngle;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            sweepValueAnimator.start();
        }
    };


    public MaterialProgressViewWithoutHandler(Context context) {
        this(context, null);
    }

    public MaterialProgressViewWithoutHandler(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaterialProgressViewWithoutHandler(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributeSet) {

        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.MaterialProgressView);
        mBorderWidth = typedArray.getDimension(R.styleable.MaterialProgressView_border_Width, BORDER_WIDTH_DEFAULT);
        int resourceId = typedArray.getResourceId(R.styleable.MaterialProgressView_paintColors, -1);
        if (resourceId != -1) {
            mPaintColors = getResources().getIntArray(resourceId);
        } else {
            mPaintColors = COLORS_DEFAULT;
        }
        mMinSweepAngle = typedArray.getInt(R.styleable.MaterialProgressView_minSweepAngle_, MIN_SWEEP_ANGLE_DEFAULT);
        mMinGapAngle = typedArray.getInt(R.styleable.MaterialProgressView_minGapAngle, MIN_GAP_ANGLE_DEFAULT);
        mAngleDuration = typedArray.getInt(R.styleable.MaterialProgressView_angleDuration, ANGLE_DURATION_DEFAULT);
        mSweepDuration = typedArray.getInt(R.styleable.MaterialProgressView_sweepDuration, SWEEP_DURATION_DEFAULT);
        typedArray.recycle();

        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mBorderWidth);
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        resetState();
        setUpAnimator();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.e(TAG, "onSizeChanged");
        mWidth = w;
        mHeight = h;
        mPaddingLeft = getPaddingLeft();
        mPaddingTop = getPaddingTop();
        mPaddingRight = getPaddingRight();
        mPaddingBottom = getPaddingBottom();
        mContentWidth = mWidth - mPaddingLeft - mPaddingRight;
        mContentHeight = mHeight - mPaddingTop - mPaddingBottom;
        centrePoint = new Point();
        mRectF = new RectF();
        if (mContentWidth > mContentHeight) {
            mRadius = mContentHeight / 2;
            centrePoint.x = mPaddingLeft + mContentWidth / 2;
            centrePoint.y = mPaddingTop + mRadius;
        } else {
            mRadius = mContentWidth / 2;
            centrePoint.x = mPaddingLeft + mRadius;
            centrePoint.y = mPaddingTop + mContentHeight / 2;
        }
        mRectF.left = centrePoint.x - mRadius;
        mRectF.top = centrePoint.y - mRadius;
        mRectF.right = centrePoint.x + mRadius;
        mRectF.bottom = centrePoint.y + mRadius;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawArc(mRectF, startAngle, sweepAngle, false, mPaint);
    }

    private void setUpAnimator() {
        Log.e(TAG, "setUpAnimator");
        angleValueAnimator = ValueAnimator.ofInt(0, 360);
        angleValueAnimator.setDuration(mAngleDuration);
        angleValueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        angleValueAnimator.setInterpolator(new LinearInterpolator());
        angleValueAnimator.setRepeatMode(ValueAnimator.RESTART);
        angleValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCurrentAngle = (int) animation.getAnimatedValue();
            }
        });
        sweepValueAnimator = ValueAnimator.ofInt(0, 360 - mMinSweepAngle - mMinGapAngle);
        sweepValueAnimator.setDuration(mSweepDuration);
        sweepValueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        sweepValueAnimator.setRepeatMode(ValueAnimator.RESTART);
        sweepValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCurrentSweepAngle = (int) animation.getAnimatedValue();
                if (angleIncreasing) {
                    startAngle = mCurrentAngle - angleOffset;
                    sweepAngle = mMinSweepAngle + mCurrentSweepAngle;
                    mPaint.setColor(getPaintColor(mPaintColors[currentColorIndex], mPaintColors[nextColorIndex],
                            animation.getAnimatedFraction()));
                } else {
                    startAngle = mCurrentAngle + mCurrentSweepAngle - angleOffset;
                    sweepAngle = 360 - mMinGapAngle - mCurrentSweepAngle;
                }
                invalidate();
            }
        });
        sweepValueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                angleIncreasing = !angleIncreasing;
                if (angleIncreasing) {
                    currentColorIndex = ++currentColorIndex % mPaintColors.length;
                    nextColorIndex = ++nextColorIndex % mPaintColors.length;
                    angleOffset = (angleOffset + mMinSweepAngle + mMinGapAngle) % 360;
                }
                post(runnable);
            }
        });

    }

    /**
     * get过度颜色
     *
     * @param color1
     * @param color2
     * @param p
     * @return
     */
    private static int getPaintColor(int color1, int color2, float p) {
        int r1 = (color1 & 0xff0000) >> 16;
        int g1 = (color1 & 0xff00) >> 8;
        int b1 = color1 & 0xff;
        int r2 = (color2 & 0xff0000) >> 16;
        int g2 = (color2 & 0xff00) >> 8;
        int b2 = color2 & 0xff;
        int r = (int) (r2 * p + r1 * (1 - p));
        int g = (int) (g2 * p + g1 * (1 - p));
        int b = (int) (b2 * p + b1 * (1 - p));
        return Color.argb(255, r, g, b);
    }


    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == VISIBLE) {
            startAnim();
            Log.e(TAG, "startAnim");
        } else {
            cancelAnim();
            Log.e(TAG, "cancelAnim");
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.e(TAG, "onAttachedToWindow");
        startAnim();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.e(TAG, "onDetachedFromWindow");
        cancelAnim();
    }

    /**
     * 状态重置
     */
    private void resetState() {
        currentColorIndex = 0;
        nextColorIndex = 1;
        mCurrentAngle = 0;
        mCurrentSweepAngle = 0;
        angleOffset = 0;
        angleIncreasing = true;
    }

    private void startAnim() {
        resetState();
        if (angleValueAnimator != null && !angleValueAnimator.isRunning())
            angleValueAnimator.start();
        if (sweepValueAnimator != null && !sweepValueAnimator.isRunning())
            sweepValueAnimator.start();
    }

    private void cancelAnim() {
        if (angleValueAnimator != null && angleValueAnimator.isRunning())
            angleValueAnimator.cancel();
        if (sweepValueAnimator != null && sweepValueAnimator.isRunning())
            sweepValueAnimator.cancel();
        removeCallbacks(runnable);
    }

    private int widthDefault;
    private int heightDefault;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
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
