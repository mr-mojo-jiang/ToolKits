package com.example.aarmodel;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

import com.mojo.toolkit.utils.DensityUtil;

public class LoadingBar extends View {
    String TAG = "LoadingBar";
    private Paint paint;
    private Paint groundPaint;//底色
    private float viewRadius;//该View的半径
    private final int ovalNumber = 12;//图形数量
    private int deepNumber = 0;
    private RectF rect;
    private Matrix matrix;


    public LoadingBar(Context context) {
        this(context, null);
    }

    public LoadingBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        paint = new Paint();
        paint.setAntiAlias(true);
        groundPaint = new Paint(paint);
        groundPaint.setColor(Color.WHITE);
        paint.setColor(0x0ff1670FA);
        matrix = new Matrix();
        rect = new RectF();
        initAnimation();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < ovalNumber; i++) {
            canvas.save();
            //每个图形间隔角度
            float angleUtil = 360f / ovalNumber;
            matrix.setRotate(angleUtil * i - 90, viewRadius, viewRadius);
            canvas.setMatrix(matrix);
            int index = (i > deepNumber ? ovalNumber + 1 : 0) + deepNumber - i;
            int alpha = 255 - 20 * Math.abs(index);
            alpha = Math.max(alpha, 0);
            paint.setAlpha(alpha);
            canvas.drawOval(rect, groundPaint);
            canvas.drawOval(rect, paint);
            canvas.restore();
        }
    }

    /**
     * 波形动画
     */
    private void initAnimation() {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, ovalNumber);
        valueAnimator.setDuration(2000);
        valueAnimator.setRepeatCount(-1);
        valueAnimator.setInterpolator(new MyInterpolator());

        valueAnimator.addUpdateListener(animation -> {
            deepNumber = (int) animation.getAnimatedValue();
            invalidate();
        });
        valueAnimator.start();
    }

    static class MyInterpolator implements TimeInterpolator {
        @Override
        public float getInterpolation(float v) {
            return 0.5f * (float) Math.sin(Math.PI * v - Math.PI * 0.5)+0.5f;
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int size = DensityUtil.dip2px(getContext(), 60);
        widthSize = widthMode == MeasureSpec.AT_MOST ? size : widthSize;
        heightSize = heightMode == MeasureSpec.AT_MOST ? size : heightSize;
        if (widthSize != heightSize) {
            widthSize = heightSize = Math.min(widthSize, heightSize);
        }
        this.setMeasuredDimension(widthSize, heightSize);
        viewRadius = widthSize / 2f;
        //图形宽度（长）
        float width = widthSize / 6f;
        //图形高度（短）
        float height = width / 1.5f;

        //图形近点距中心的的距离
        float radius = viewRadius - width - 10;
        rect.left = viewRadius + radius;
        rect.top = viewRadius - height / 2f;
        rect.right = viewRadius + radius + width;
        rect.bottom = viewRadius + height / 2f;
    }
}
