package com.github.goldberg.customview.telescope;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.github.goldberg.customview.R;


public class TelescopeView extends View {

    private Paint mPaint;
    private BitmapShader mBitmapShader;
    private Bitmap mBitmap, mBitmapBackGround;
    private int contentWidth;
    private int contentHeight;
    private int paddingLeft;
    private int paddingTop;
    private int paddingRight;
    private int paddingBottom;

    private ValueAnimator mValueAnimator;

    private int maxRadius = 300;
    private long duration = 200;
    private float currentX;
    private float currentY;
    private int currentRadius;

    public TelescopeView(Context context) {
        super(context);
        init(null, 0);
    }

    public TelescopeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public TelescopeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        mPaint = new Paint();
        mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        paddingLeft = getPaddingLeft();
        paddingTop = getPaddingTop();
        paddingRight = getPaddingRight();
        paddingBottom = getPaddingBottom();

        contentWidth = getWidth() - paddingLeft - paddingRight;
        contentHeight = getHeight() - paddingTop - paddingBottom;

        mBitmapBackGround = Bitmap.createBitmap(contentWidth, contentHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mBitmapBackGround);
        canvas.drawBitmap(mBitmap, null, new Rect(paddingLeft, paddingTop, contentWidth + paddingLeft, contentHeight + paddingTop), mPaint);
        mBitmapShader = new BitmapShader(mBitmapBackGround, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        mPaint.setShader(mBitmapShader);
    }

    private void animToShow() {
        if (mValueAnimator != null) {
            mValueAnimator.cancel();
        }
        mValueAnimator = ValueAnimator.ofInt(currentRadius, maxRadius);
        mValueAnimator.setDuration(duration);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentRadius = (int) animation.getAnimatedValue();
                invalidate();

            }
        });
        mValueAnimator.start();
    }

    private void animToHide() {
        if (mValueAnimator != null) {
            mValueAnimator.cancel();
        }
        mValueAnimator = ValueAnimator.ofInt(currentRadius, 0);
        mValueAnimator.setDuration(duration);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentRadius = (int) animation.getAnimatedValue();
                invalidate();

            }
        });
        mValueAnimator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        canvas.drawRect(0, 0, contentWidth, contentHeight, mPaint);
//        canvas.drawCircle(contentWidth / 2, contentHeight / 2, 100,mPaint);


        canvas.drawCircle(currentX, currentY, currentRadius, mPaint);


    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                currentX = x;
                currentY = y;
                animToShow();
                return true;
            case MotionEvent.ACTION_MOVE:
                currentX = x;
                currentY = y;
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                animToHide();
                break;

            default:
                break;

        }
        return super.onTouchEvent(event);
    }
}
