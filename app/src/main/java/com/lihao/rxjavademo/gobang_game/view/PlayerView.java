package com.lihao.rxjavademo.gobang_game.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;

import com.lihao.rxjavademo.gobang_game.model.SymbolType;

import androidx.annotation.Nullable;

public class PlayerView extends SquareView {

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
        canvas.drawColor(getResources().getColor(android.R.color.darker_gray));
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
