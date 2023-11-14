package com.lihao.rxjavademo.gobang_game.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public abstract class SquareView extends View {

    public SquareView(Context context) {
        super(context);
    }

    public SquareView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int wSize = MeasureSpec.getSize(widthMeasureSpec);
        int hSize = MeasureSpec.getSize(heightMeasureSpec);
        int min = wSize == 0 && hSize == 0 ? 0
                : wSize == 0 && hSize != 0 ? hSize
                : wSize != 0 && hSize == 0 ? wSize
                : Math.min(wSize, hSize);
        setMeasuredDimension(min, min);
    }
}
