package com.example.aarmodel.test;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class CircleView extends View {

    Paint progressPaint;

    public CircleView(Context context) {
        super(context);
    }

    public CircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        progressPaint = new Paint();
        progressPaint = new Paint();
        progressPaint.setAntiAlias(true);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(20);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int[] colors = new int[]{Color.parseColor("#5ABDFB"),Color.parseColor("#1670FA")};
        float[] pos = new float[]{0.1f,0.6f};
        progressPaint.setShader( new SweepGradient(getWidth()/2f, getHeight()/2f, colors, pos));
        canvas.drawLine(0,getHeight()/2f,getWidth(),getHeight()/2f,new Paint());

        Matrix matrix = new Matrix();
        canvas.save();
        matrix.setRotate(170, getWidth()/2f, getHeight()/2f);
        canvas.setMatrix(matrix);
        canvas.drawArc(20, 20, getWidth() - 20, getHeight() - 20, 10, 180, false, progressPaint);
        canvas.restore();
    }
}
