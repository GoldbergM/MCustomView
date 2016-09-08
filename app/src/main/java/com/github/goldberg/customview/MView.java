package com.github.goldberg.customview;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * Created by mengzhun on 2016/9/7.
 */
public class MView extends View {
    public MView(Context context) {
        super(context);
    }

    public MView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private Paint mPaint;
    private Context mContext;
    private int mWidth, mHeight;
    private float offset;
    private float fraction;
    private static final int DURATION = 1000;

    private String newString;
    private String oldString;
    private Rect newStringBound;
    private Rect oldStringBound;
    private int newStringSize;
    private int oldStringSize;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void init(Context context, AttributeSet attrs) {
        mContext = context;
        mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = getRight() - getLeft();
        mHeight = getBottom() - getTop();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (newString != null) {
            canvas.save();
            mPaint.setColor(Color.BLACK);
            mPaint.setTextSize(newStringSize);
            canvas.drawText(newString, mWidth / 2 - newStringBound.width() / 2, mHeight / 2 + newStringBound.height() / 2, mPaint);

            canvas.rotate((float) degree);
            canvas.translate(0, -coverRectHeight / 2);
            canvas.translate(offset, 0);
            mPaint.setColor(Color.WHITE);
            coverRect.right = (int) ((1 - fraction) * coverRectWidth) + gapMinWidth;
            canvas.drawRect(coverRect, mPaint);

            mPaint.setColor(Color.WHITE);
            transparentRect.left = coverRect.right - 1;
            canvas.drawRect(transparentRect, mPaint);
            canvas.restore();

            canvas.rotate((float) degree);
            canvas.translate(0, -coverRectHeight / 2);
            canvas.clipRect((int) (offset + coverRect.right), 0, move, coverRectHeight);
            if (oldString != null) {
                mPaint.setColor(Color.BLACK);
                mPaint.setTextSize(oldStringSize);
                canvas.translate(0, coverRectHeight / 2);
                canvas.rotate(-(float) degree);
                canvas.drawText(oldString, mWidth / 2 - oldStringBound.width() / 2, mHeight / 2 + oldStringBound.height() / 2, mPaint);
            }

        }
    }

    private void anim() {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, move);
        valueAnimator.setDuration(DURATION).setInterpolator(new AccelerateDecelerateInterpolator());
//        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
//        valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                offset = (float) animation.getAnimatedValue();
                fraction = animation.getAnimatedFraction();
                postInvalidate();
            }
        });
        valueAnimator.start();
    }


    private Rect coverRect;//gapRect
    private int coverRectWidth;
    private int coverRectHeight;
    private int gapMinWidth = 4;
    private double degree;//旋转的角度
    private int move;

    private Rect transparentRect;
    private int transparentRectWidth;
    private int transparentRectHeight;


    private void calculateText() {
        coverRectHeight = (int) Math.sqrt(mWidth * mWidth + mHeight * mHeight);
        move = (int) (2 * mWidth * mHeight * 1.0f / coverRectHeight);
        coverRectWidth = gapMinWidth * 4;
        coverRect = new Rect(0, 0, coverRectWidth, coverRectHeight);
        degree = Math.asin(mWidth * 1.0f / coverRectHeight) * 180 / Math.PI;
        transparentRectHeight = coverRectHeight;
        transparentRectWidth = move;
        transparentRect = new Rect(0, 0, transparentRectWidth, transparentRectHeight);
    }


    public void setText(String text) {
        if (null == text)
            return;
        oldString = newString;
        newString = text;

        oldStringBound = newStringBound;
        oldStringSize = newStringSize;
        mPaint.setTextSize(mHeight);
        newStringBound = new Rect();
        mPaint.getTextBounds(newString, 0, newString.length(), newStringBound);
        if (newStringBound.width() > mWidth) {
            float scale = mWidth * 1.0f / newStringBound.width();
            newStringSize = (int) (mHeight * scale);
            mPaint.setTextSize(newStringSize);
            mPaint.getTextBounds(newString, 0, newString.length(), newStringBound);
        } else {
            newStringSize = mHeight;
        }

        mPaint.setColor(Color.BLACK);
        post(new Runnable() {
            @Override
            public void run() {
                calculateText();
                anim();
            }
        });
    }


}
