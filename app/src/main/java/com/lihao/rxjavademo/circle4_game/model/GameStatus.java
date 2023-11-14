package com.lihao.rxjavademo.circle4_game.model;

public class GameStatus {

    /** 游戏是否结束。 */
    public boolean isEnded;

    /** 获胜者。 */
    public SymbolType winner;

    /** 胜利者的开始位置。 */
    public GridPosition winningPositionStart;

    /** 胜利者的结束位置。 */
    public GridPosition winningPositionEnd;

    public GameStatus(boolean isEnded, SymbolType winner) {
        this.isEnded = isEnded;
        this.winner = winner;
    }
}
