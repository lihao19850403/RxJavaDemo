package com.lihao.rxjavademo.circle4_game.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;

import com.lihao.rxjavademo.circle4_game.model.GameGrid;
import com.lihao.rxjavademo.circle4_game.model.GameStatus;
import com.lihao.rxjavademo.circle4_game.model.GridPosition;
import com.lihao.rxjavademo.circle4_game.model.SymbolType;

import androidx.annotation.Nullable;

public class GameGridView extends View {

    /** 游戏网格。 */
    private GameGrid mGameGrid = new GameGrid();

    /** 游戏状态。 */
    private GameStatus mGameStatus;

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
        try {
            // 通过循环绘制符号。
            for (int line = 0; line < GameGrid.GRID_LINES; line++) {
                for (int number = 0; number < GameGrid.GRID_NUMBERS; number++) {
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
            // 绘制游戏结果。
            drawResult(canvas);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setData(GameGrid gameGrid) {
        mGameGrid = gameGrid;
        invalidate();
    }

    public void setGameStatus(GameStatus gameStatus) {
        mGameStatus = gameStatus;
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
        float padding = rectWidth * 1.0f / GameGrid.GRID_NUMBERS;
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
        canvas.drawColor(getResources().getColor(android.R.color.darker_gray));
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
        float linePadding = rectWidth * 1.0f / GameGrid.GRID_NUMBERS;
        for (int line = 0; line < GameGrid.GRID_LINES + 1; line++) {
            float startX = hMargin;
            float startY = vMargin + linePadding * line;
            float endX = width - hMargin;
            float endY = vMargin + linePadding * line;
            canvas.drawLine(startX, startY, endX, endY, PAINT);
        }
        for (int number = 0; number < GameGrid.GRID_NUMBERS + 1; number++) {
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
        float padding = getWidth() * 2 / 3.0f / GameGrid.GRID_NUMBERS;
        float centerX = startX + padding * number + padding / 2;
        float centerY = startY + padding * line + padding / 2;
        canvas.drawCircle(centerX, centerY, padding * 3 / 8, PAINT);
    }

    /**
     * 绘制游戏结果。
     * @param canvas 画布。
     */
    private void drawResult(Canvas canvas) {
        if (mGameStatus == null || !mGameStatus.isEnded || mGameStatus.winner == null) {
            return;
        }
        PAINT.setColor(mGameStatus.winner == SymbolType.BLACK ? Color.BLACK : Color.RED);
        PAINT.setStrokeWidth(12.0f);
        float startX = getWidth() / 6.0f;
        float startY = (getHeight() - getWidth() * 2 / 3.0f) / 2;
        float padding = getWidth() * 2 / 3.0f / GameGrid.GRID_NUMBERS;
        boolean isVertical = mGameStatus.winningPositionStart.number == mGameStatus.winningPositionEnd.number;
        boolean isHorizontal = mGameStatus.winningPositionStart.line == mGameStatus.winningPositionEnd.line;
        boolean isLeftLean = mGameStatus.winningPositionStart.number > mGameStatus.winningPositionEnd.number;
        boolean isRightLean = mGameStatus.winningPositionStart.number < mGameStatus.winningPositionEnd.number;
        float lineStartX = startX + padding * mGameStatus.winningPositionStart.number + (isVertical ? padding / 2.0f : isLeftLean ? padding : 0);
        float lineStartY = startY + padding * mGameStatus.winningPositionStart.line + (isHorizontal ? padding / 2.0f : 0);
        float lineEndX = startX + padding * mGameStatus.winningPositionEnd.number + (isVertical ? padding / 2.0f : isHorizontal || isRightLean ? padding : 0);
        float lineEndY = startY + padding * mGameStatus.winningPositionEnd.line + (isHorizontal ? padding / 2.0f : padding);
        canvas.drawLine(lineStartX, lineStartY, lineEndX, lineEndY, PAINT);
    }
}
