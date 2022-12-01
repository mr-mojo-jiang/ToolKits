package com.example.aarmodel.test;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mojo.toolkit.utils.PaintUtil;

public class Text extends androidx.appcompat.widget.AppCompatTextView {
    private int colorBgOff = 0x0ffCCCCCC;

    public Text(@NonNull Context context) {
        super(context);
    }

    public Text(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(colorBgOff);
        getPaint().setStrokeWidth(2);
        float base = PaintUtil.getBaseline(getPaint());
        //canvas.drawLine(0,base,getWidth(),base,getPaint());
        getPaint().setColor(Color.BLUE);
        canvas.drawLine(0, getBaseline(), getWidth(), getBaseline(), getPaint());
        canvas.drawLine(0, getHeight(), getWidth(), getHeight(), getPaint());
        super.onDraw(canvas);
        canvas.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2, new Paint());
    }
}
