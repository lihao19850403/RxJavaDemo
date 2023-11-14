package com.lihao.rxjavademo.ox_game.model;

public class GameStatus {

    /** 游戏是否结束。 */
    public boolean isEnded;

    /** 获胜者。 */
    public SymbolType winner;

    public GameStatus(boolean isEnded, SymbolType winner) {
        this.isEnded = isEnded;
        this.winner = winner;
    }
}
