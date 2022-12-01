package com.example.aarmodel.test;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.mojo.toolkit.utils.DecimalUtil;
import com.mojo.toolkit.utils.DensityUtil;
import com.mojo.toolkit.utils.MathUtil;
import com.mojo.toolkit.utils.PaintUtil;

public class DialPlateView1 extends View {
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
    private int textSize = 12;
    /**
     * 刻度盘值的字体大小
     */
    private int progressSize = 14;
    /**
     * 刻度盘刻度字体大小
     */
    private int scaleSize = 10;
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
     * 每个图形间隔角度
     */
    private float angleUtil = 180f / (scaleNumber * 2 - 2);
    /**
     * 值的单位
     */
    private String progressUnit = "%";
    /**
     * 刻度比例
     */
    private int scaleUnit = 10;
    /**
     * 刻度比例
     */
    private int progress = 1;
    private boolean isInit = true;


    public DialPlateView1(Context context) {
        this(context, null);
    }

    public DialPlateView1(Context context, @Nullable AttributeSet attrs) {
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
        refreshTextSize();
    }

    public void setTextSize(int tagSize, int progressSize, int scaleSize) {
        this.textSize = tagSize;
        this.progressSize = progressSize;
        this.scaleSize = scaleSize;
        refreshTextSize();
    }

    private void refreshTextSize() {
        if (viewHeight == 0) return;
        textSize = Math.min(DensityUtil.dip2px(getContext(), textSize), viewHeight / 12);
        progressSize = Math.min(DensityUtil.dip2px(getContext(), progressSize), viewHeight / 10);
        scaleSize = Math.min(DensityUtil.dip2px(getContext(), scaleSize), viewHeight / 14);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawDialPlate(canvas);
        drawProgress(canvas);
    }

    /**
     * 绘制表盘，包括起始结束标志，刻度等
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

        float scaleX = dialPlateCenterX - dialPlateRadius + viewWidth / 20f;
        float scaleLineWidth = viewWidth / 25f;
        float progressRadius = dialPlateCenterX - scaleX - scaleLineWidth - viewWidth / 25f;
        textPaint.setTextSize(scaleSize);
        textPaint.setColor(scaleColor);
        for (int i = 0; i < scaleNumber * 2 - 1; i++) {
            canvas.save();
            matrix.setRotate(angleUtil * i, dialPlateCenterX, dialPlateCenterY);
            canvas.setMatrix(matrix);
            scalePaint.setColor(i % 2 == 0 ? 0x0ff222222 : 0x0ff555555);
            canvas.drawLine(scaleX, dialPlateCenterY, scaleX + (i % 2 == 0 ? scaleLineWidth : scaleLineWidth * 0.6f), dialPlateCenterY, scalePaint);
            canvas.restore();
            if (i % 2 == 0) {
                float x = (int) (dialPlateCenterX - progressRadius * DecimalUtil.value2Value(MathUtil.cos(angleUtil * i), 2));
                float y = (int) (dialPlateCenterY - progressRadius * DecimalUtil.value2Value(MathUtil.sin(angleUtil * i), 2));
                textPaint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText(String.valueOf(i / 2 * scaleUnit), x, PaintUtil.getBaselineByCenterY(textPaint, y), textPaint);
            }
        }
    }

    private void drawProgress(Canvas canvas) {
        if (progressGradient == null) {
            progressPaint.setColor(progressColor);
        } else {
            progressPaint.setShader(progressGradient);
        }
        canvas.drawArc(dialPlateCenterX - dialPlateRadius, dialPlateCenterY - dialPlateRadius, dialPlateCenterX + dialPlateRadius,
                dialPlateCenterY + dialPlateRadius, 180, angleUtil*progress*2, false, progressPaint);
        drawArrow(canvas);
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setColor(progressValueColor);
        textPaint.setTextSize(progressSize);
        canvas.drawText(progress * scaleUnit + progressUnit, viewWidth / 2f, PaintUtil.getBaselineByBottom(textPaint, viewHeight), textPaint);
    }

    private void drawArrow(Canvas canvas) {
        float w = viewWidth / 30f;
        canvas.drawCircle(viewWidth / 2f, dialPlateCenterY, w, arrowsPaint);
        path.reset();
        float arrowPoint1X = viewWidth / 2f - dialPlateRadius / 1.6f;
        path.moveTo(arrowPoint1X, dialPlateCenterY);
        path.lineTo(viewWidth / 2f - w / 2, dialPlateCenterY + w / 2f);
        path.lineTo(viewWidth / 2f - w / 2, dialPlateCenterY - w / 2f);
        path.moveTo(arrowPoint1X, dialPlateCenterY);
        canvas.save();
        matrix.setRotate(angleUtil * progress * 2, dialPlateCenterX, dialPlateCenterY);
        canvas.setMatrix(matrix);
        canvas.drawPath(path, arrowsPaint);
        canvas.restore();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int ws = DensityUtil.dip2px(getContext(), 300);
        widthSize = widthMode == MeasureSpec.AT_MOST ? ws : widthSize;
        int heightSize = (int) (widthSize / 1.7f);
        this.setMeasuredDimension(widthSize, heightSize);
        viewWidth = widthSize;
        viewHeight = heightSize;
        dialPlateCenterX = viewWidth / 2f;
        dialPlateCenterY = viewHeight - PaintUtil.getTextHeight(textSize) - dialPlateCenterX / 12f;
        dialPlateRadius = dialPlateCenterY - dialPlateCenterX / 20f;
        if (progressGradient == null) {
            progressGradient = new SweepGradient(dialPlateCenterX, dialPlateCenterY,
                    Color.parseColor("#02D2FF"), Color.parseColor("#5ABDFB"));
        }
        progressPaint.setStrokeWidth(viewWidth / 23f);
        refreshTextSize();

    }

    public void setScaleNumber(int scaleNumber) {
        this.scaleNumber = scaleNumber;
        this.angleUtil = 180f / (scaleNumber * 2 - 2);
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

    public void setScaleUnit(int scaleUnit) {
        this.scaleUnit = scaleUnit;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
