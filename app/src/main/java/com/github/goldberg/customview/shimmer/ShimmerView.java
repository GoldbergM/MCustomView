package com.github.goldberg.customview.shimmer;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.widget.TextView;

import com.github.goldberg.customview.R;

/**
 * Created by mengzhun on 2016/11/4.
 */

public class ShimmerView extends TextView {

    private Paint mPaint;
    private LinearGradient linearGradient;
    private int dx;
    private Matrix matrix;
    private long duration = 2000;
    private int reflectionColor;
    private int defaultReflectionColor = 0xffffffff;

    public ShimmerView(Context context) {
        this(context, null);
    }

    public ShimmerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShimmerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs,
                    R.styleable.ShimmerView);
            reflectionColor = typedArray.getColor(R.styleable.ShimmerView_reflectionColor, defaultReflectionColor);
            typedArray.recycle();
        }
        mPaint = getPaint();
        matrix = new Matrix();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        linearGradient = new LinearGradient(0, 0, getWidth(), 0,
                new int[]{getCurrentTextColor(), reflectionColor, getCurrentTextColor()},
                new float[]{0.0f, 0.5f, 1.0f}, Shader.TileMode.CLAMP);
        mPaint.setShader(linearGradient);

        ValueAnimator valueAnimator = ValueAnimator.ofInt(-getMeasuredWidth(), getMeasuredWidth());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                dx = (int) animation.getAnimatedValue();

                matrix.reset();
                matrix.setTranslate(dx, 0);
                linearGradient.setLocalMatrix(matrix);
                invalidate();
            }
        });
        valueAnimator.setDuration(duration);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.start();


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
