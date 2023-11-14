package com.lihao.rxjavademo.ox_game;

import com.lihao.rxjavademo.ox_game.model.GameGrid;
import com.lihao.rxjavademo.ox_game.model.SymbolType;

public class GameUtils {

    /**
     * 计算游戏胜利者。
     * @param gameGrid 目前棋盘情况。
     * @return 胜利者信息。
     */
    public static SymbolType calculateWinnerForGrid(GameGrid gameGrid) {
        for (int r = 0; r < GameGrid.GRID_SIZE; r++) {
            for (int c = 0; c < GameGrid.GRID_SIZE; c++) {
                SymbolType player = gameGrid.getSymbolAt(r, c);
                if (player == SymbolType.EMPTY) {
                    continue;
                }
                // 找到三个连续的水平符号。
                if (c + 2 < GameGrid.GRID_SIZE
                        && player == gameGrid.getSymbolAt(r, c + 1)
                        && player == gameGrid.getSymbolAt(r, c + 2)) {
                    return player;
                }
                if (r + 2 < GameGrid.GRID_SIZE) {
                    // 找到三个连续的垂直符号。
                    if (player == gameGrid.getSymbolAt(r + 1, c)
                            && player == gameGrid.getSymbolAt(r + 2, c)) {
                        return player;
                    }
                    // 找到三个连续的对角线符号。
                    if (c + 2 < GameGrid.GRID_SIZE
                            && player == gameGrid.getSymbolAt(r + 1, c + 1)
                            && player == gameGrid.getSymbolAt(r + 2, c + 2)) {
                        return player;
                    }
                    // 在另一个方向上找到三个连续的对角线符号。
                    if (c - 2 >= 0
                            && player == gameGrid.getSymbolAt(r + 1, c - 1)
                            && player == gameGrid.getSymbolAt(r + 2, c - 2)) {
                        return player;
                    }
                }
            }
        }
        return null; // 还没有确定的赢家。
    }
}
