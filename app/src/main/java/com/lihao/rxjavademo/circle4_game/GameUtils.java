package com.lihao.rxjavademo.circle4_game;

import com.lihao.rxjavademo.MApplication;
import com.lihao.rxjavademo.R;
import com.lihao.rxjavademo.Utils;
import com.lihao.rxjavademo.circle4_game.model.Constants;
import com.lihao.rxjavademo.circle4_game.model.GameGrid;
import com.lihao.rxjavademo.circle4_game.model.GameState;
import com.lihao.rxjavademo.circle4_game.model.GameStatus;
import com.lihao.rxjavademo.circle4_game.model.GridPosition;
import com.lihao.rxjavademo.circle4_game.model.SymbolType;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class GameUtils {

    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat(MApplication.getApp().getResources().getString(R.string.time_format));

    /**
     * 计算游戏胜利者。
     * @param gameGrid 目前棋盘情况。
     * @return 胜利者信息。
     */
    public static GameStatus calculateWinnerForGrid(GameGrid gameGrid) {
        for (int r = 0; r < GameGrid.GRID_NUMBERS; r++) {
            for (int c = 0; c < GameGrid.GRID_LINES; c++) {
                SymbolType player = gameGrid.getSymbolAt(r, c);
                if (player == SymbolType.EMPTY) {
                    continue;
                }
                // 找到四个连续的水平符号。
                if (c + 3 < GameGrid.GRID_NUMBERS
                        && player == gameGrid.getSymbolAt(r, c + 1)
                        && player == gameGrid.getSymbolAt(r, c + 2)
                        && player == gameGrid.getSymbolAt(r, c + 3)) {
                    GameStatus result = new GameStatus(true, player);
                    result.winningPositionStart = new GridPosition(r, c);
                    result.winningPositionEnd = new GridPosition(r, c + 3);
                    return result;
                }
                if (r + 3 < GameGrid.GRID_LINES) {
                    // 找到四个连续的垂直符号。
                    if (player == gameGrid.getSymbolAt(r + 1, c)
                            && player == gameGrid.getSymbolAt(r + 2, c)
                            && player == gameGrid.getSymbolAt(r + 3, c)) {
                        GameStatus result = new GameStatus(true, player);
                        result.winningPositionStart = new GridPosition(r, c);
                        result.winningPositionEnd = new GridPosition(r + 3, c);
                        return result;
                    }
                    // 找到四个连续的对角线符号。
                    if (c + 3 < GameGrid.GRID_NUMBERS
                            && player == gameGrid.getSymbolAt(r + 1, c + 1)
                            && player == gameGrid.getSymbolAt(r + 2, c + 2)
                            && player == gameGrid.getSymbolAt(r + 3, c + 3)) {
                        GameStatus result = new GameStatus(true, player);
                        result.winningPositionStart = new GridPosition(r, c);
                        result.winningPositionEnd = new GridPosition(r + 3, c + 3);
                        return result;
                    }
                    // 在另一个方向上找到四个连续的对角线符号。
                    if (c - 3 >= 0
                            && player == gameGrid.getSymbolAt(r + 1, c - 1)
                            && player == gameGrid.getSymbolAt(r + 2, c - 2)
                            && player == gameGrid.getSymbolAt(r + 3, c - 3)) {
                        GameStatus result = new GameStatus(true, player);
                        result.winningPositionStart = new GridPosition(r, c);
                        result.winningPositionEnd = new GridPosition(r + 3, c - 3);
                        return result;
                    }
                }
            }
        }
        GameStatus result = new GameStatus(false, null);
        return result; // 还没有确定的赢家。
    }

    /**
     * 检测触摸点是否有效（可以放下一枚棋子）。
     * @param touchPosition 触摸到的网格。
     * @param currentState 当前棋盘状态。
     * @return 检测结果。
     */
    public static boolean calculateValidTouch(GridPosition touchPosition, GameState currentState) {
        int checkLine = GameGrid.GRID_LINES - 1;
        GridPosition checkPosition = new GridPosition(checkLine, touchPosition.number);
        for (; checkLine >= -1; checkLine--) {
            if (checkLine == -1) {
                break;
            }
            checkPosition.line = checkLine;
            if (currentState.isEmpty(checkPosition)) {
                break;
            }
        }
        return checkLine != -1;
    }

    /**
     * 棋子坠落到新的棋盘格子中。
     * @param touchPosition 触摸到的网格。
     * @param currentState 当前棋盘状态。
     * @return 棋子新的位置。
     */
    public static GridPosition dropToNewPosition(GridPosition touchPosition, GameState currentState) {
        int checkLine = GameGrid.GRID_LINES - 1;
        GameGrid gameGrid = currentState.gameGrid;
        for (; checkLine >= -1; checkLine--) {
            if (checkLine == -1) {
                break;
            }
            SymbolType symbol = gameGrid.getSymbolAt(checkLine, touchPosition.number);
            if (symbol == SymbolType.EMPTY) {
                break;
            }
        }
        return new GridPosition(checkLine, touchPosition.number);
    }

    /**
     * 存档。
     * @param currentState 当前游戏状态。
     * @return 存档是否成功。
     */
    public static boolean saveGame(GameState currentState) {
        String time = TIME_FORMAT.format(Calendar.getInstance().getTime());
        return Utils.storeObject(MApplication.getApp(), time, currentState, Constants.SAVED_GAMES_PATH);
    }

    /**
     * 加载游戏。
     * @param fileName 游戏文件名。
     * @return 棋盘状况。
     */
    public static GameState loadGame(String fileName) {
        return (GameState) Utils.restoreObject(MApplication.getApp(), fileName, Constants.SAVED_GAMES_PATH);
    }

    /**
     * 删除游戏存档。
     * @param fileName 游戏文件名。
     * @return 操作是否成功。
     */
    public static boolean deleteSavedGame(String fileName) {
        return Utils.deleteObject(MApplication.getApp(), fileName, Constants.SAVED_GAMES_PATH);
    }

    /**
     * 获取已存档的记录列表。
     * @param savedGamesPath 存档文件夹。
     * @return 已存档的记录列表。
     */
    public static List<String> getSavedGameNames(File savedGamesPath) {
        List<String> savedGamesNames = new ArrayList<String>();
        try {
            File[] savedGames = savedGamesPath.listFiles();
            for (File savedGame : savedGames) {
                savedGamesNames.add(savedGame.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return savedGamesNames;
    }
}
