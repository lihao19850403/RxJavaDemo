package com.lihao.rxjavademo.circle4_game.model;

import java.io.Serializable;

import androidx.annotation.NonNull;

public class GameGrid implements Cloneable, Serializable {

    public static final int GRID_LINES = 7;
    public static final int GRID_NUMBERS = 7;

    /** 游戏状态。 */
    private SymbolType[][] mSymbols = initSymbolTypes();

    public SymbolType getSymbolAt(int line, int number) {
        return mSymbols[line][number];
    }

    public GameGrid setSymbolAt(SymbolType symbol, int line, int number) {
        GameGrid copy;
        try {
            copy = (GameGrid) clone();
            copy.mSymbols[line][number] = symbol;
            return copy;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return this;
    }

    public GameGrid setSymbolAt(SymbolType symbol, GridPosition gridPosition) {
        if (gridPosition != null && gridPosition.line >= 0 && gridPosition.number >= 0) {
            return setSymbolAt(symbol, gridPosition.line, gridPosition.number);
        }
        return this;
    }

    public GameGrid resetGrid() {
        GameGrid copy;
        try {
            copy = (GameGrid) clone();
            for (int line = 0; line < copy.mSymbols.length; line++) {
                for (int number = 0; number < copy.mSymbols[line].length; number++) {
                    copy.mSymbols[line][number] = SymbolType.EMPTY;
                }
            }
            return copy;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        for (int line = 0; line < mSymbols.length; line++) {
            for (int number = 0; number < mSymbols[line].length; number++) {
                mSymbols[line][number] = SymbolType.EMPTY;
            }
        }
        return this;
    }

    @NonNull
    @Override
    protected Object clone() throws CloneNotSupportedException {
        GameGrid newGameGrid = (GameGrid) super.clone();
        for (int line = 0; line < this.mSymbols.length; line++) {
            System.arraycopy(newGameGrid.mSymbols[line], 0, this.mSymbols[line], 0, this.mSymbols[line].length);
        }
        return newGameGrid;
    }

    private SymbolType[][] initSymbolTypes() {
        SymbolType[][] symbolTypes = new SymbolType[GRID_LINES][GRID_NUMBERS];
        for (int line = 0; line < symbolTypes.length; line++) {
            for (int number = 0; number < symbolTypes[line].length; number++) {
                symbolTypes[line][number] = SymbolType.EMPTY;
            }
        }
        return symbolTypes;
    }
}
