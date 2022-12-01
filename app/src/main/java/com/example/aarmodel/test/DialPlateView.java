package com.example.aarmodel.test;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

import com.mojo.toolkit.utils.DecimalUtil;
import com.mojo.toolkit.utils.DensityUtil;
import com.mojo.toolkit.utils.MathUtil;
import com.mojo.toolkit.utils.PaintUtil;

public class DialPlateView extends View {
    String TAG = "DialPlateView";

    /**
     * 刻度盘中心X坐标值
     */
    private float dialPlateCenterX;
    /**
     * 刻度盘中心Y坐标值
     */
    private float dialPlateCenterY;
    /**
     * 刻度盘半径
     */
    private float dialPlateRadius;
    /**
     * View的宽高
     */
    private int viewWidth, viewHeight;
    /**
     * 标志资源的字体大小
     */
    private int textSize = 14;
    /**
     * 刻度盘值的字体大小
     */
    private int progressSize = 16;
    /**
     * 刻度盘刻度字体大小
     */
    private int scaleSize = 12;
    /**
     * 画笔
     */
    private Paint textPaint, scalePaint, progressPaint, arrowsPaint;
    /**
     * 用于绘制刻度的矩阵
     */
    private Matrix matrix;
    /**
     * 刻度盘指针绘制路径
     */
    private Path path;
    /**
     * 刻度盘刻度数量
     */
    private int scaleNumber = 11;
    /**
     * 刻度盘开始标志
     */
    private String startTag = "全关";
    /**
     * 刻度盘结束标志
     */
    private String endTag = "全开";//刻度数量
    /**
     * 刻度盘结束标志
     */
    private String contentTag = "开度";//刻度数量
    /**
     * 主刻度颜色
     */
    private int mainScaleColor = 0x0ff222222;
    /**
     * 结束标志文字颜色
     */
    private int halfScaleColor = 0x0ff555555;
    /**
     * 开始标志文字颜色
     */
    private int startTagColor = 0x0ffFF0101;
    /**
     * 结束标志文字颜色
     */
    private int endTagColor = 0x0ff58C755;
    /**
     * 刻度盘显示内容文字颜色
     */
    private int contentTagColor = 0x0ff222222;
    /**
     * 刻度盘刻度值文字颜色
     */
    private int scaleColor = 0x0ff666666;
    /**
     * 具体值的文字颜色
     */
    private int progressValueColor = 0x0ffFE630E;
    /**
     * 刻度盘进度条的颜色
     */
    private int progressBgColor = 0x0ffE9ECF5;
    /**
     * 刻度盘进度条的颜色
     */
    private int progressColor = 0x0ff222222;
    /**
     * 刻度盘进度条的颜色
     */
    private int arrowColor = 0x0ff02D2FF;
    /**
     * 刻度盘进度条的颜色
     */
    private SweepGradient progressGradient = null;
    /**
     * 值的单位
     */
    private String progressUnit = "%";
    /**
     * 是否显示刻度的中间值
     */
    private boolean showHalfScale = true;
    /**
     * 进度
     */
    private int progress = 0;
    /**
     * 总量
     */
    private int maxProgress = 100;
    /**
     * 刻度比例
     */
    private int scaleUnit = maxProgress / (scaleNumber - 1);


    public DialPlateView(Context context) {
        this(context, null);
    }

    public DialPlateView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    void init() {
        textPaint = PaintUtil.getTextPaint();
        textPaint.setTextSize(textSize);
        scalePaint = PaintUtil.getTextPaint();

        progressPaint = new Paint();
        progressPaint.setAntiAlias(true);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(20);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);

        arrowsPaint = new Paint();
        arrowsPaint.setAntiAlias(true);
        arrowsPaint.setColor(arrowColor);
        arrowsPaint.setStyle(Paint.Style.FILL);
        matrix = new Matrix();
        path = new Path();
        this.setProgress(progress);
        this.refreshTextSize();
    }

    public void setTextSize(int tagSize, int progressSize, int scaleSize) {
        this.textSize = tagSize;
        this.progressSize = progressSize;
        this.scaleSize = scaleSize;
        refreshTextSize();
    }

    private void refreshTextSize() {
        if (viewHeight == 0) return;
        textSize = Math.min(DensityUtil.dip2px(getContext(), textSize), viewHeight / 9);
        progressSize = Math.min(DensityUtil.dip2px(getContext(), progressSize), viewHeight / 8);
        scaleSize = Math.min(DensityUtil.dip2px(getContext(), scaleSize), viewHeight / 10);
    }

    float currentSweepAngle;

    public void setProgress(int progress) {
        this.progress = Math.min(progress, maxProgress);
        float endAngle = 180f * this.progress / maxProgress;
        startAnimator(currentSweepAngle,endAngle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawDialPlate(canvas);
        drawProgress(canvas);
    }

    /**
     * 绘制表盘，包括起始结束标志，刻度等
     *
     * @param canvas 画布
     */
    private void drawDialPlate(Canvas canvas) {
        textPaint.setTextSize(textSize);
        textPaint.setColor(startTagColor);
        textPaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(startTag, 0, PaintUtil.getBaselineByBottom(textPaint, viewHeight), textPaint);
        textPaint.setTextAlign(Paint.Align.RIGHT);
        textPaint.setColor(endTagColor);
        canvas.drawText(endTag, viewWidth, PaintUtil.getBaselineByBottom(textPaint, viewHeight), textPaint);
        textPaint.setColor(contentTagColor);
        canvas.drawText(contentTag + ": ", viewWidth / 2f, PaintUtil.getBaselineByBottom(textPaint, viewHeight), textPaint);
        textPaint.setTextSize(progressSize);

        progressPaint.setColor(progressBgColor);
        progressPaint.setShader(null);
        canvas.drawArc(dialPlateCenterX - dialPlateRadius, dialPlateCenterY - dialPlateRadius, dialPlateCenterX + dialPlateRadius,
                dialPlateCenterY + dialPlateRadius, 180, 180, false, progressPaint);

        textPaint.setTextSize(scaleSize);
        textPaint.setColor(scaleColor);
        float scaleX = dialPlateCenterX - dialPlateRadius + viewWidth / 20f;
        float scaleLineWidth = viewWidth / 25f;
        float progressRadius = dialPlateCenterX - scaleX - scaleLineWidth - textPaint.measureText(String.valueOf(maxProgress)) * 0.5f;
        //刻度数量
        int showScaleNum = showHalfScale ? scaleNumber * 2 - 1 : scaleNumber;
        float angleUnit = 180f / (showHalfScale ? (scaleNumber * 2 - 2) : (scaleNumber - 1));
        for (int i = 0; i < showScaleNum; i++) {
            int color = showHalfScale ? i % 2 == 0 ? mainScaleColor : halfScaleColor : mainScaleColor;
            scalePaint.setColor(color);
            float stopX = scaleX + (showHalfScale ? scaleLineWidth * (i % 2 == 0 ? 1 : 0.6f) : scaleLineWidth);
            canvas.save();
            matrix.setRotate(angleUnit * i, dialPlateCenterX, dialPlateCenterY);
            canvas.setMatrix(matrix);
            canvas.drawLine(scaleX, dialPlateCenterY, stopX, dialPlateCenterY, scalePaint);
            canvas.restore();
            if (i % 2 == 0) {
                float x = (int) (dialPlateCenterX - progressRadius * DecimalUtil.value2Value(MathUtil.cos(angleUnit * i), 2));
                float y = (int) (dialPlateCenterY - progressRadius * DecimalUtil.value2Value(MathUtil.sin(angleUnit * i), 2));
                textPaint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText(String.valueOf(i / 2 * scaleUnit), x, PaintUtil.getBaselineByCenterY(textPaint, y), textPaint);
            }
        }
    }

    /**
     * @param canvas 绘制当前刻度指针及箭头
     */
    private void drawProgress(Canvas canvas) {
        if (progressGradient == null) {
            progressPaint.setColor(progressColor);
        } else {
            progressPaint.setShader(progressGradient);
        }
        float degree = 170;
        canvas.save();
        matrix.setRotate(degree, dialPlateCenterX, dialPlateCenterY);
        canvas.setMatrix(matrix);
        canvas.drawArc(dialPlateCenterX - dialPlateRadius, dialPlateCenterY - dialPlateRadius, dialPlateCenterX + dialPlateRadius,
                dialPlateCenterY + dialPlateRadius, 180 - degree, currentSweepAngle, false, progressPaint);
        canvas.restore();
        drawArrow(canvas, currentSweepAngle);
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setColor(progressValueColor);
        textPaint.setTextSize(progressSize);
        canvas.drawText(progress + progressUnit, viewWidth / 2f, PaintUtil.getBaselineByBottom(textPaint, viewHeight), textPaint);
    }

    private void drawArrow(Canvas canvas, float sweepAngle) {
        float w = viewWidth / 30f;
        canvas.drawCircle(dialPlateCenterX, dialPlateCenterY, w, arrowsPaint);
        path.reset();
        float arrowPoint1X = viewWidth / 2f - dialPlateRadius / 1.9f;
        path.moveTo(arrowPoint1X, dialPlateCenterY);
        path.lineTo(viewWidth / 2f - w / 2, dialPlateCenterY + w / 2f);
        path.lineTo(viewWidth / 2f - w / 2, dialPlateCenterY - w / 2f);
        path.moveTo(arrowPoint1X, dialPlateCenterY);
        canvas.save();
        matrix.setRotate(sweepAngle, dialPlateCenterX, dialPlateCenterY);
        canvas.setMatrix(matrix);
        canvas.drawPath(path, arrowsPaint);
        canvas.restore();
    }

    private void startAnimator(final float startAngle,float endAngle) {
        ValueAnimator animator = ValueAnimator.ofFloat(endAngle - startAngle);
        animator.setDuration(800);
        animator.setRepeatCount(0);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(animation -> {
            currentSweepAngle = startAngle + (float) animation.getAnimatedValue();
            invalidate();
        });
        animator.start();
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int ws = DensityUtil.dip2px(getContext(), 300);
        widthSize = widthMode == MeasureSpec.AT_MOST ? ws : widthSize;
        int heightSize = (int) (widthSize / 1.6f);
        this.setMeasuredDimension(widthSize, heightSize);
        viewWidth = widthSize;
        viewHeight = heightSize;
        refreshTextSize();
        boolean changed = dialPlateCenterX != viewWidth / 2f || dialPlateCenterY != viewHeight - PaintUtil.getTextHeight(textSize) * 1.8f;
        dialPlateCenterX = viewWidth / 2f;
        dialPlateCenterY = viewHeight - PaintUtil.getTextHeight(textSize) * 1.8f;
        dialPlateRadius = dialPlateCenterY - dialPlateCenterX / 20f;
        if (progressGradient == null || changed) {
            progressGradient = new SweepGradient(dialPlateCenterX, dialPlateCenterY,
                    Color.parseColor("#02D2FF"), Color.parseColor("#1670FA"));
        }
        progressPaint.setStrokeWidth(viewWidth / 23f);

    }

    public void setScaleNumber(int scaleNumber) {
        this.scaleNumber = scaleNumber;
        refreshScale();
    }

    public void setStartTag(String startTag) {
        this.startTag = startTag;
    }

    public void setEndTag(String endTag) {
        this.endTag = endTag;
    }

    public void setContentTag(String contentTag) {
        this.contentTag = contentTag;
    }

    public void setStartTagColor(int startTagColor) {
        this.startTagColor = startTagColor;
    }

    public void setEndTagColor(int endTagColor) {
        this.endTagColor = endTagColor;
    }

    public void setContentTagColor(int contentTagColor) {
        this.contentTagColor = contentTagColor;
    }

    public void setScaleColor(int scaleColor) {
        this.scaleColor = scaleColor;
    }

    public void setProgressValueColor(int progressValueColor) {
        this.progressValueColor = progressValueColor;
    }

    public void setProgressColor(int progressColor) {
        this.progressColor = progressColor;
    }

    public void setArrowColor(int arrowColor) {
        this.arrowColor = arrowColor;
    }

    public void setProgressGradient(SweepGradient progressGradient) {
        this.progressGradient = progressGradient;
    }

    public void setProgressUnit(String progressUnit) {
        this.progressUnit = progressUnit;
    }

    public void setProgressBgColor(int progressBgColor) {
        this.progressBgColor = progressBgColor;
    }

    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
        refreshScale();
    }

    public void setShowHalfScale(boolean showHalfScale) {
        this.showHalfScale = showHalfScale;
    }

    private void refreshScale() {
        this.scaleUnit = maxProgress / (scaleNumber - 1);
    }
}
