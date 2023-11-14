package com.lihao.rxjavademo.ox_game.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;

import com.lihao.rxjavademo.ox_game.model.SymbolType;

import androidx.annotation.Nullable;

public class PlayerView extends View {

    /** 玩家状态。 */
    private SymbolType mPlayerType = SymbolType.EMPTY;

    private final Paint PAINT = new Paint(Paint.ANTI_ALIAS_FLAG);

    public PlayerView(Context context) {
        super(context);
    }

    public PlayerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PlayerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        clearCanvas(canvas);
        switch (mPlayerType) {
            case BLACK:
                drawBlack(canvas);
                break;
            case RED:
                drawRed(canvas);
                break;
            default:
                break;
        }
    }

    public void setData(SymbolType playerType) {
        mPlayerType = playerType;
        invalidate();
    }

    /* ---------- 画布内容的绘制。 ---------- */

    /**
     * 清除画布。不可在此方法中使用new创建对象。
     *
     * @param canvas 画布。
     */
    private void clearCanvas(Canvas canvas) {
        PAINT.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawPaint(PAINT);
        PAINT.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        canvas.drawColor(Color.GRAY);
    }

    /**
     * 绘制一个黑色棋子。不可在此方法中使用new创建对象。
     *
     * @param canvas 画布。
     */
    private void drawBlack(Canvas canvas) {
        PAINT.setColor(Color.BLACK);
        drawCircle(canvas);
    }

    /**
     * 绘制一个红色棋子。不可在此方法中使用new创建对象。
     *
     * @param canvas 画布。
     */
    private void drawRed(Canvas canvas) {
        PAINT.setColor(Color.RED);
        drawCircle(canvas);
    }

    private void drawCircle(Canvas canvas) {
        PAINT.setStrokeWidth(12.0f);
        float centerX = getWidth() / 2.0f;
        float centerY = getHeight() / 2.0f;
        float radius = getWidth() * 0.375f;
        canvas.drawCircle(centerX, centerY, radius, PAINT);
    }
}
