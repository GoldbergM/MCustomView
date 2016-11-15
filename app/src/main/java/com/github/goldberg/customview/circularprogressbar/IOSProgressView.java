package com.github.goldberg.customview.circularprogressbar;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
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
    private boolean mGradient;
    private static final boolean GRADIENT_DEFUALT = false;
    private int mGradientDuration;
    private static final int DURATION_GRADIENT_DEFAULT = DURATION_DEFAULT * 2;

    private int rotateAngle;
    private ValueAnimator valueAnimator;
    private ValueAnimator valueAnimator_Shader;
    private int mCurrentValue;
    private int[] alphas;

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

    private LinearGradient linearGradient;
    private Matrix matrix_gradient;
    private int dx_gradient;
    private RadialGradient radialGradient;

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
        mGradient = typedArray.getBoolean(R.styleable.IOSProgressView_iGradient, GRADIENT_DEFUALT);
        mGradientDuration = typedArray.getInteger(R.styleable.IOSProgressView_iGradientDuration, DURATION_GRADIENT_DEFAULT);
        mPaint = new Paint();
        if (!mGradient) {
            mPaint.setColor(mColor);
        }
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
        initAlphas();
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
        if (mGradient)
            initGradient();
        Log.e(TAG,"onSizeChanged");
    }

    private void initAlphas() {
        alphas = new int[mLeafCount];
        int alpha = Color.alpha(mColor);
        for (int i = 0; i < mLeafCount; i++) {
            alphas[i] = alpha * (i + 1) / mLeafCount;
        }

    }

    private void initGradient() {
        linearGradient = new LinearGradient(0, 0, mWidth, mHeight,
                new int[]{getResources().getColor(R.color.colorPrimary),
                        getResources().getColor(R.color.colorAccent),
                        getResources().getColor(R.color.colorPrimary)},
                new float[]{0.0f, 0.5f, 1.0f}, Shader.TileMode.CLAMP);
        mPaint.setShader(linearGradient);
        int length = (int) (Math.sqrt(mWidth * mWidth + mHeight * mHeight));
        valueAnimator_Shader = ValueAnimator.ofInt(-length, length);
        valueAnimator_Shader.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator_Shader.setInterpolator(new LinearInterpolator());
        valueAnimator_Shader.setRepeatMode(ValueAnimator.RESTART);
        valueAnimator_Shader.setDuration(mGradientDuration);
        valueAnimator_Shader.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                dx_gradient = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
        valueAnimator_Shader.start();
        matrix_gradient = new Matrix();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < mLeafCount; i++) {
            canvas.rotate(rotateAngle, centrePoint.x, centrePoint.y);
            mPaint.setAlpha(alphas[(i + mCurrentValue) % mLeafCount]);
            if (mGradient) {
                matrix_gradient.setTranslate(dx_gradient, 0);
                matrix_gradient.postRotate(-rotateAngle * (i + 1), centrePoint.x, centrePoint.y);
                mPaint.getShader().setLocalMatrix(matrix_gradient);
            }
            canvas.drawLine(centrePoint.x, centrePoint.y - mRadius / 2,
                    centrePoint.x, centrePoint.y - mRadius,
                    mPaint);
        }
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == VISIBLE) {
            Log.e(TAG, "onVisibilityChanged:" + visibility);
            startAnim();
        } else {
            Log.e(TAG, "onVisibilityChanged:" + visibility);
            cancelAnim();
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

    private void startAnim() {
        Log.e(TAG, "startAnim");
        resetState();
        if (valueAnimator != null && !valueAnimator.isRunning())
            valueAnimator.start();
        if (mGradient) {
            if (valueAnimator_Shader != null && !valueAnimator_Shader.isRunning()) {
                valueAnimator_Shader.start();
            }
        }
    }

    private void cancelAnim() {
        Log.e(TAG, "cancelAnim");
        if (valueAnimator != null && valueAnimator.isRunning())
            valueAnimator.cancel();
        if (mGradient) {
            if (valueAnimator_Shader != null && valueAnimator_Shader.isRunning()) {
                valueAnimator_Shader.cancel();
            }
        }
    }

    private void resetState() {
        mCurrentValue = mLeafCount;
        if (mGradient) {
            dx_gradient = -mWidth;
        }
    }


}
