package com.example.aarmodel.test;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.aarmodel.R;
import com.mojo.toolkit.utils.DensityUtil;
import com.mojo.toolkit.utils.PaintUtil;

import java.util.Timer;
import java.util.TimerTask;

public class SwitchButton extends View {
    String TAG = "ToggleButton";
    private RectF rectBg;
    private RectF rectOn;
    private Paint paintBg;
    private Paint paintCircle;
    private Paint paintText;
    /**
     * 开关两个状态下，圆点的圆心距离
     */
    private float spotOff;
    /**
     * 两边圆角大小
     */
    private float radius;
    /**
     * 圆点的偏移量
     */
    private float offset = 0;
    /**
     * 默认文字的x左边
     */
    private float defaultTextX;
    /**
     * 关闭时背景色颜色
     */
    private int offColor = 0x0ffCCCCCC;
    /**
     * 开启时背景色颜色
     */
    private int onColor = 0x0ff3E93F7;
    /**
     * 圆点颜色
     */
    private int spotColor = Color.WHITE;
    /**
     * 是否开启
     */
    private boolean isOn = false;
    /**
     * 状态改变时，是否以动画改变状态
     */
    private boolean animate = true;
    /**
     * 圆点和背景的缝隙宽度
     */
    private float gapSize = 5;
    /**
     * 开关状态下，文字间距和白色圆间距之比
     */
    private float scale;
    /**
     * 开状态下显示的文字
     */
    private String onText = "全开";
    /**
     * 关状态下显示的文字
     */
    private String offText = "全关";

    public SwitchButton(Context context) {
        this(context, null, 0);
    }

    public SwitchButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwitchButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) initView(attrs);
        init();
    }

    private void initView(@NonNull AttributeSet attrs) {
        //获取自定义属性。
        @SuppressLint({"Recycle", "CustomViewStyleable"})
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.SwitchButton);
        onColor = ta.getColor(R.styleable.SwitchButton_onColor, 0x0ff3E93F7);
        offColor = ta.getColor(R.styleable.SwitchButton_offColor, 0x0ffCCCCCC);
        spotColor = ta.getColor(R.styleable.SwitchButton_spotColor, Color.WHITE);
        animate = ta.getBoolean(R.styleable.SwitchButton_animate, false);
        isOn = ta.getBoolean(R.styleable.SwitchButton_asDefaultOn, false);
    }

    private void init() {
        paintBg = PaintUtil.getFillPaint();
        paintBg.setColor(offColor);

        paintCircle = PaintUtil.getFillPaint();
        paintCircle.setColor(spotColor);

        paintText = PaintUtil.getTextPaint();
        paintText.setColor(Color.WHITE);
        rectBg = new RectF();
        rectOn = new RectF();
    }

    public void setOffColor(int offColor) {
        this.offColor = offColor;
    }

    public void setOnColor(int onColor) {
        this.onColor = onColor;
    }

    public void setGapSize(float gapSize) {
        this.gapSize = gapSize;
    }

    public String getOnText() {
        return onText;
    }

    public String getOffText() {
        return offText;
    }

    public void setState(boolean on) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (spotOff != 0) {
                    Message msg = new Message();
                    msg.obj = on;
                    handler.sendMessage(msg);
                    this.cancel();
                }
            }
        }, 0, 1);
    }

    public boolean getState() {
        return isOn;
    }

    Handler handler = new Handler(message -> {
        refreshState((Boolean) message.obj);
        return false;
    });

    /**
     * 刷新状态
     *
     * @param on 状态值，true=开
     */
    private void refreshState(boolean on) {
        if (isOn == on) return;
        this.isOn = on;
        if (animate) {
            ValueAnimator animator = ValueAnimator.ofFloat(on ? 0 : spotOff, on ? spotOff : 0);
            animator.setRepeatCount(0);
            animator.setDuration(500);
            animator.addUpdateListener(valueAnimator -> {
                offset = (float) valueAnimator.getAnimatedValue();
                rectOn.right = offset + radius * 2;
                invalidate();
            });
            animator.start();
        } else {
            offset = on ? 0 : spotOff;
            rectOn.right = offset + radius * 2;
            invalidate();
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        paintBg.setColor(offColor);
        canvas.drawRoundRect(rectBg, radius, radius, paintBg);
        if (offset > 0) {
            paintBg.setColor(onColor);
            canvas.drawRoundRect(rectOn, radius, radius, paintBg);
        }
        canvas.drawCircle(offset + radius, radius, radius - gapSize, paintCircle);
        canvas.drawText(offset > spotOff / 2 ? offText : onText, defaultTextX - offset * scale, PaintUtil.getBaselineByCenterY(paintText, radius), paintText);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) performClick();
        return true;
    }

    @Override
    public boolean performClick() {
        refreshState(!isOn);
        return super.performClick();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int width = widthMode == MeasureSpec.AT_MOST ? DensityUtil.dip2px(getContext(), 80) : MeasureSpec.getSize(widthMeasureSpec);
        int height = (int) (width / 2.3);
        setMeasuredDimension(width, height);
        rectBg.right = width;
        rectBg.bottom = height;
        rectOn.bottom = height;
        radius = height / 2f;
        float textSize = (int) ((width - height) / 2 - radius / 4);
        paintText.setTextSize((int) textSize);
        defaultTextX = height + textSize;
        scale = ((height + textSize) * 2 - width) / (width - height);
        spotOff = width - height;
    }


}
