package com.github.goldberg.customview.montage;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.github.goldberg.customview.R;

/**
 * Created by mengzhun on 2016/9/7.
 */
public class MontageView extends View {

    private static final String TAG = "MontageView";
    private Paint mPaint;
    private int textSize;
    private int backGroundColor;
    private int textColor;
    private int gapColor;
    private int borderRadius;
    private float minGapWidth;
    private float maxGapWidth;
    private int animDuration;
    private int mWidth, mHeight;
    private float dx;//滑动距离
    private float fraction;

    private int paddingLeft;
    private int paddingTop;
    private int paddingRight;
    private int paddingBottom;
    private int contentWidth;
    private int contentHeight;

    private static final int DURATION_DEFAULT = 1000;

    private static final int BACKGROUND_COLOR_DEFAULT = Color.WHITE;
    private static final int TEXT_SIZE_DEFAULT = 16;
    private static final int TEXT_COLOR_DEFAULT = Color.BLACK;
    private static final int GAP_COLOR_DEFAULT = Color.WHITE;
    private static final int BORDER_RADIUS_DEFAULT = 8;

    private String newString;
    private String oldString;
    private Rect newStringBound;
    private Rect oldStringBound;
    private int newStringSize;
    private int oldStringSize;


    public MontageView(Context context) {
        this(context, null);
    }

    public MontageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MontageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context
                .obtainStyledAttributes(attrs, R.styleable.MontageView);
        backGroundColor = typedArray
                .getColor(R.styleable.MontageView_backGroundColor, BACKGROUND_COLOR_DEFAULT);
        textColor = typedArray.getColor(R.styleable.MontageView_textColor, TEXT_COLOR_DEFAULT);
        textSize = typedArray.getDimensionPixelSize(R.styleable.MontageView_textSize, TEXT_SIZE_DEFAULT);
        gapColor = typedArray.getColor(R.styleable.MontageView_gapColor, GAP_COLOR_DEFAULT);
        minGapWidth = typedArray.getDimension(R.styleable.MontageView_gapMinWidth, MIN_GAP_WIDTH);
        maxGapWidth = typedArray.getDimension(R.styleable.MontageView_gapMaxWidth, MIN_GAP_WIDTH);
        animDuration = typedArray.getInt(R.styleable.MontageView_animDuration, DURATION_DEFAULT);
        borderRadius = typedArray.getDimensionPixelSize(R.styleable.MontageView_borderRadius, BORDER_RADIUS_DEFAULT);
        typedArray.recycle();
        mPaint = new Paint();
        mPaint.setColor(textColor);
        mPaint.setTextSize(textSize);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        mWidth = getRight() - getLeft();
        mHeight = getBottom() - getTop();
        paddingLeft = getPaddingLeft();
        paddingTop = getPaddingTop();
        paddingRight = getPaddingRight();
        paddingBottom = getPaddingBottom();
        contentWidth = mWidth - paddingLeft - paddingRight;
        contentHeight = mHeight - paddingTop - paddingBottom;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    private Rect gapRect;
    private int gapRectWidth;
    private int gapRectHeight;
    private static final float MIN_GAP_WIDTH = 4;
    private double degree;//旋转的角度
    private int distanceToMove;//移动的距离

    private Rect coverRect;
    private int coverRectWidth;
    private int coverRectHeight;

    public void setText(String text) {
        if (null == text)
            return;
        oldString = newString;
        newString = text;

        oldStringBound = newStringBound;
        oldStringSize = newStringSize;
        newStringBound = new Rect();
        mPaint.setTextSize(textSize);
        mPaint.getTextBounds(newString, 0, newString.length(), newStringBound);
        if (newStringBound.width() > contentWidth) {
            float scale = contentWidth * 1.0f / newStringBound.width();
            newStringSize = (int) (textSize * scale);
            mPaint.setTextSize(newStringSize);
            mPaint.getTextBounds(newString, 0, newString.length(), newStringBound);
        } else {
            newStringSize = textSize;
        }

        post(new Runnable() {
            @Override
            public void run() {
                calculateText();
                anim();
            }
        });
    }


    private void calculateText() {
        gapRectHeight = (int) Math.sqrt(mWidth * mWidth + mHeight * mHeight);
        distanceToMove = (int) (2.0f * mWidth * mHeight / gapRectHeight);
        gapRectWidth = (int) maxGapWidth;
        gapRect = new Rect(0, 0, gapRectWidth, gapRectHeight);
        degree = Math.asin(mWidth * 1.0f / gapRectHeight) * 180 / Math.PI;//canvas 倾斜角度
        coverRectWidth = distanceToMove;
        coverRectHeight = gapRectHeight;
        coverRect = new Rect(0, 0, coverRectWidth, coverRectHeight);
    }


    private void anim() {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, distanceToMove);
        valueAnimator.setDuration(animDuration).setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                dx = (float) animation.getAnimatedValue();
                fraction = animation.getAnimatedFraction();
                invalidate();
            }
        });
        valueAnimator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (newString != null) {

            //draw the background
            canvas.save();
            mPaint.setColor(backGroundColor);
            canvas.drawRect(0, 0, mWidth, mHeight, mPaint);

            //draw the text
            mPaint.setColor(textColor);
            mPaint.setTextSize(newStringSize);
            canvas.drawText(newString, paddingLeft + contentWidth / 2 - newStringBound.width() / 2,
                    paddingTop + contentHeight / 2 + newStringBound.height() / 2, mPaint);

            //draw the gap
            canvas.rotate((float) degree);
            canvas.translate(0, -gapRectHeight / 2);
            canvas.translate(dx, 0);
            mPaint.setColor(gapColor);
            gapRect.right = (int) ((1 - fraction) * (gapRectWidth - minGapWidth) + (int) minGapWidth);
            canvas.drawRect(gapRect, mPaint);

            //draw the coverRect
            mPaint.setColor(backGroundColor);
            coverRect.left = gapRect.right - 1;
            canvas.drawRect(coverRect, mPaint);
            canvas.restore();

            //draw the coverText
            canvas.rotate((float) degree);
            canvas.translate(0, -gapRectHeight / 2);
            canvas.clipRect((int) (dx + gapRect.right), 0, distanceToMove, gapRectHeight);
            if (oldString != null) {
                mPaint.setColor(textColor);
                mPaint.setTextSize(oldStringSize);
                canvas.translate(0, gapRectHeight / 2);
                canvas.rotate(-(float) degree);
                canvas.drawText(oldString, paddingLeft + contentWidth / 2 - oldStringBound.width() / 2,
                        paddingTop + contentHeight / 2 + oldStringBound.height() / 2, mPaint);
            }

        }
    }



}
