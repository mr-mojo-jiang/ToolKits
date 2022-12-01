package com.example.aarmodel.test;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.mojo.toolkit.R;
import com.mojo.toolkit.utils.CompoundImageUtil;
import com.mojo.toolkit.utils.DensityUtil;


public class StateTextView extends TextView {
    private static final String TAG = "StateTextView";
    private int unSelectId = R.drawable.icon_down;
    private int selectId = R.drawable.icon_up;
    private int finishId = R.drawable.bg_finished;
    private boolean isSelected;
    private int iconW = 24;
    private int iconH = 24;
    private boolean enableImageChange;//是否允许图片自动点击
    private boolean isFinish;//是否允许图片自动点击
    private int paddingTB;

    public StateTextView(Context context) {
        this(context, null, 0);
    }

    public StateTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StateTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    void init() {
        this.setGravity(Gravity.END);
        this.setTextImage(unSelectId);
        //initPadding();
    }

    private void initPadding(){
        int lr = DensityUtil.dip2px(getContext(), 12);
        this.setPadding(lr, paddingTB, lr, paddingTB);
    }

    /*@Override
    public void setText(CharSequence text, BufferType type) {
        //initPadding();
        super.setText(text, type);
    }*/

    /*设置图片自动变化*/
    public void enableImageChange(boolean enable) {
        enableImageChange = enable;
    }

    /**
     * 设置右侧图片
     *
     * @param resId 图片资源Id
     */
    /*设置右侧图片*/
    private void setTextImage(int resId) {
        CompoundImageUtil.build(this)
                .setLocation(CompoundImageUtil.Location.Right)
                .setSize(iconW, iconH)
                .setCompoundImage(resId);
    }

    public int getSpace() {
        return DensityUtil.dip2px(getContext(), 52);
    }

    /**
     * 修改选中状态图片
     *
     * @param selectId 图片资源Id
     */
    public void setSelectImgId(int selectId) {
        this.selectId = selectId;
        invalidate();
    }

    /**
     * 修改选中状态图片
     *
     * @param dpWith   图标宽度
     * @param dpHeight 图片高度
     */
    public void refreshImgSize(int dpWith, int dpHeight) {
        this.iconW = dpWith;
        this.iconH = dpHeight;
        invalidate();
    }

    /**
     * 修改未选中状态图片
     *
     * @param unSelectId 未选中状态图片Id
     */
    public void setUnSelectImgId(int unSelectId) {
        this.unSelectId = unSelectId;
        invalidate();
        setTextImage(unSelectId);
    }

    /**
     * @param finishId 设置已完成状态图标Id
     */
    public void setFinishId(int finishId) {
        this.finishId = finishId;
        invalidate();
    }

    /**
     * @return 获取选中状态
     */
    public boolean getState() {
        return isSelected;
    }


    /**
     * @param isSelected 主动更改状态
     */
    public void setState(boolean isSelected) {
        this.isSelected = isSelected;
        if (isSelected) {
            setTextImage(selectId);
        } else {
            setTextImage(unSelectId);
        }
    }

    public void setFinished(boolean isFinish) {
        setTextImage(isFinish ? finishId : unSelectId);
    }

    @Override
    public boolean performClick() {
        //initPadding();
        if (enableImageChange) {
            isSelected = !isSelected;
            setTextImage(isSelected ? selectId : unSelectId);
        }
        return super.performClick();
    }

    @Override
    public void setGravity(int gravity) {
        super.setGravity(gravity | Gravity.CENTER_VERTICAL);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Paint paint = getPaint();
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        //获取文本高度
        float th = fontMetrics.bottom - fontMetrics.top;
        Log.e(TAG, "h: "+h+"--th:"+th );
        paddingTB = (int) ((h -th)/2);
        Log.e(TAG, "onSizeChanged: "+paddingTB );
    }
}
