package com.lihao.rxjavademo.ox_game.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;

import com.lihao.rxjavademo.ox_game.model.GameGrid;
import com.lihao.rxjavademo.ox_game.model.SymbolType;
import com.lihao.rxjavademo.ox_game.model.GridPosition;

import androidx.annotation.Nullable;

public abstract class GameGridView extends View {

    /** 游戏状态。 */
    private GameGrid mGameGrid = new GameGrid();

    private final Paint PAINT = new Paint(Paint.ANTI_ALIAS_FLAG);

    public GameGridView(Context context) {
        super(context);
    }

    public GameGridView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GameGridView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        clearCanvas(canvas);
        drawGridLines(canvas);
        // 通过循环绘制符号。
        try {
            for (int line = 0; line < GameGrid.GRID_SIZE; line++) {
                for (int number = 0; number < GameGrid.GRID_SIZE; number++) {
                    SymbolType symbol = mGameGrid.getSymbolAt(line, number);
                    switch (symbol) {
                        case BLACK:
                            drawBlack(canvas, line, number);
                            break;
                        case RED:
                            drawRed(canvas, line, number);
                            break;
                        default:
                            break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setData(GameGrid gameGrid) {
        mGameGrid = gameGrid;
        invalidate();
    }

    /**
     * 获取点击的棋盘格子位置。
     *
     * @param touchX 触摸点x坐标。
     * @param touchY 触摸点y坐标。
     * @return 点击的棋盘格子位置。
     */
    public GridPosition getGridPosition(float touchX, float touchY) {
        int width = getWidth();
        int height = getHeight();
        int rectWidth = width * 2 / 3;
        int rectHeight = rectWidth;
        int hMargin = (width - rectWidth) / 2;
        int vMargin = (height - rectHeight) / 2;
        float padding = rectWidth * 1.0f / GameGrid.GRID_SIZE;
        if (touchX <= hMargin || touchX >= width - hMargin
                || touchY <= vMargin || touchY >= height - vMargin) {
            return new GridPosition(-1, -1);
        }
        int line = (int) ((touchY - vMargin) / padding);
        int number = (int) ((touchX - hMargin) / padding);
        return new GridPosition(line, number);
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
     * 绘制网格线。不可在此方法中使用new创建对象。
     *
     * @param canvas 画布。
     */
    private void drawGridLines(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        // 绘制棋盘背景。
        PAINT.setColor(Color.WHITE);
        int rectWidth = width * 2 / 3;
        int rectHeight = rectWidth;
        int hMargin = (width - rectWidth) / 2;
        int vMargin = (height - rectHeight) / 2;
        canvas.drawRect(hMargin, vMargin, width - hMargin, height - vMargin, PAINT);
        // 绘制网格。
        PAINT.setColor(Color.BLACK);
        PAINT.setStrokeWidth(5.0f);
        float linePadding = rectWidth * 1.0f / GameGrid.GRID_SIZE;
        for (int line = 0; line < GameGrid.GRID_SIZE + 1; line++) {
            float startX = hMargin;
            float startY = vMargin + linePadding * line;
            float endX = width - hMargin;
            float endY = vMargin + linePadding * line;
            canvas.drawLine(startX, startY, endX, endY, PAINT);
        }
        for (int number = 0; number < GameGrid.GRID_SIZE + 1; number++) {
            float startX = hMargin + linePadding * number;
            float startY = vMargin;
            float endX = hMargin + linePadding * number;
            float endY = height - vMargin;
            canvas.drawLine(startX, startY, endX, endY, PAINT);
        }
    }

    /**
     * 绘制一个黑色棋子。不可在此方法中使用new创建对象。
     *
     * @param canvas 画布。
     * @param line   行数（从0开始）。
     * @param number 序号（从0开始）。
     */
    private void drawBlack(Canvas canvas, int line, int number) {
        PAINT.setColor(Color.BLACK);
        drawCircle(canvas, line, number);
    }

    /**
     * 绘制一个红色棋子。不可在此方法中使用new创建对象。
     *
     * @param canvas 画布。
     * @param line   行数（从0开始）。
     * @param number 序号（从0开始）。
     */
    private void drawRed(Canvas canvas, int line, int number) {
        PAINT.setColor(Color.RED);
        drawCircle(canvas, line, number);
    }

    private void drawCircle(Canvas canvas, int line, int number) {
        PAINT.setStrokeWidth(12.0f);
        float startX = getWidth() / 6.0f;
        float startY = (getHeight() - getWidth() * 2 / 3.0f) / 2;
        float padding = getWidth() * 2 / 3.0f / GameGrid.GRID_SIZE;
        float centerX = startX + padding * number + padding / 2;
        float centerY = startY + padding * line + padding / 2;
        canvas.drawCircle(centerX, centerY, padding * 3 / 8, PAINT);
    }
}
