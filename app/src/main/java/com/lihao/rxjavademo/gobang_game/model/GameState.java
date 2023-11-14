package com.lihao.rxjavademo.gobang_game.model;

import java.io.Serializable;

public class GameState implements Serializable {

    /** 网格信息。 */
    public GameGrid gameGrid = new GameGrid();

    /** 最后一个游戏符号。 */
    public SymbolType lastPlayedSymbol = SymbolType.EMPTY;

    public GameState() {}

    public GameState(GameGrid gameGrid, SymbolType lastPlayedSymbol) {
        this.gameGrid = gameGrid;
        this.lastPlayedSymbol = lastPlayedSymbol;
    }

    public GameState setSymbolAt(SymbolType symbolType, GridPosition gridPosition) {
        return new GameState(this.gameGrid.setSymbolAt(symbolType, gridPosition), symbolType);
    }

    /**
     * 判断给定棋盘格子是否为空。
     * @param gridPosition 给定棋盘格子是否为空。
     * @return 判断结果。
     */
    public boolean isEmpty(GridPosition gridPosition) {
        if (gridPosition == null || gridPosition.line == -1 || gridPosition.number == -1) {
            return false;
        }
        boolean result = true;
        try {
            result = gameGrid.getSymbolAt(gridPosition.line, gridPosition.number) == SymbolType.EMPTY;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public GameState reset() {
        return new GameState(this.gameGrid.resetGrid(), SymbolType.EMPTY);
    }
}
