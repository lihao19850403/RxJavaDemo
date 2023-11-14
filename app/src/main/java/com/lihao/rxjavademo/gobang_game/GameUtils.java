package com.lihao.rxjavademo.gobang_game;

import com.lihao.rxjavademo.MApplication;
import com.lihao.rxjavademo.R;
import com.lihao.rxjavademo.Utils;
import com.lihao.rxjavademo.gobang_game.model.Constants;
import com.lihao.rxjavademo.gobang_game.model.GameGrid;
import com.lihao.rxjavademo.gobang_game.model.GameState;
import com.lihao.rxjavademo.gobang_game.model.GameStatus;
import com.lihao.rxjavademo.gobang_game.model.GridPosition;
import com.lihao.rxjavademo.gobang_game.model.SymbolType;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.core.util.Pair;

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
                // 找到五个连续的水平符号。
                if (c + 4 < GameGrid.GRID_NUMBERS
                        && player == gameGrid.getSymbolAt(r, c + 1)
                        && player == gameGrid.getSymbolAt(r, c + 2)
                        && player == gameGrid.getSymbolAt(r, c + 3)
                        && player == gameGrid.getSymbolAt(r, c + 4)) {
                    GameStatus result = new GameStatus(true, player);
                    result.winningPositionStart = new GridPosition(r, c);
                    result.winningPositionEnd = new GridPosition(r, c + 4);
                    return result;
                }
                if (r + 4 < GameGrid.GRID_LINES) {
                    // 找到五个连续的垂直符号。
                    if (player == gameGrid.getSymbolAt(r + 1, c)
                            && player == gameGrid.getSymbolAt(r + 2, c)
                            && player == gameGrid.getSymbolAt(r + 3, c)
                            && player == gameGrid.getSymbolAt(r + 4, c)) {
                        GameStatus result = new GameStatus(true, player);
                        result.winningPositionStart = new GridPosition(r, c);
                        result.winningPositionEnd = new GridPosition(r + 4, c);
                        return result;
                    }
                    // 找到五个连续的对角线符号。
                    if (c + 4 < GameGrid.GRID_NUMBERS
                            && player == gameGrid.getSymbolAt(r + 1, c + 1)
                            && player == gameGrid.getSymbolAt(r + 2, c + 2)
                            && player == gameGrid.getSymbolAt(r + 3, c + 3)
                            && player == gameGrid.getSymbolAt(r + 4, c + 4)) {
                        GameStatus result = new GameStatus(true, player);
                        result.winningPositionStart = new GridPosition(r, c);
                        result.winningPositionEnd = new GridPosition(r + 4, c + 4);
                        return result;
                    }
                    // 在另一个方向上找到五个连续的对角线符号。
                    if (c - 4 >= 0
                            && player == gameGrid.getSymbolAt(r + 1, c - 1)
                            && player == gameGrid.getSymbolAt(r + 2, c - 2)
                            && player == gameGrid.getSymbolAt(r + 3, c - 3)
                            && player == gameGrid.getSymbolAt(r + 4, c - 4)) {
                        GameStatus result = new GameStatus(true, player);
                        result.winningPositionStart = new GridPosition(r, c);
                        result.winningPositionEnd = new GridPosition(r + 4, c - 4);
                        return result;
                    }
                }
            }
        }
        GameStatus result = new GameStatus(false, null);
        return result; // 还没有确定的赢家。
    }

    /**
     * 存档。
     * @param currentState 当前游戏状态。
     * @param timers 玩家耗时信息。
     * @return 存档是否成功。
     */
    public static boolean saveGame(GameState currentState, long[] timers) {
        String time = TIME_FORMAT.format(Calendar.getInstance().getTime());
        boolean stateSaveResult = Utils.storeObject(MApplication.getApp(), time, currentState, Constants.SAVED_GAMES_PATH);
        boolean timersSaveResult = Utils.storeObject(MApplication.getApp(), time.concat("VS"), timers, Constants.SAVED_GAMES_TIMER_PATH);
        return stateSaveResult && timersSaveResult;
    }

    /**
     * 加载游戏。
     * @param fileName 游戏文件名。
     * @return 棋盘状况与耗时情况。
     */
    public static Pair<GameState, long[]> loadGame(String fileName) {
        GameState gameState = (GameState) Utils.restoreObject(MApplication.getApp(), fileName, Constants.SAVED_GAMES_PATH);
        long[] timers = (long[]) Utils.restoreObject(MApplication.getApp(), fileName.concat("VS"), Constants.SAVED_GAMES_TIMER_PATH);
        return new Pair(gameState, timers);
    }

    /**
     * 删除游戏存档。
     * @param fileName 游戏文件名。
     * @return 操作是否成功。
     */
    public static boolean deleteSavedGame(String fileName) {
        boolean deleteStateResult = Utils.deleteObject(MApplication.getApp(), fileName, Constants.SAVED_GAMES_PATH);
        boolean deleteTimersResult = Utils.deleteObject(MApplication.getApp(), fileName.concat("VS"), Constants.SAVED_GAMES_TIMER_PATH);
        return deleteStateResult && deleteTimersResult;
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

    /**
     * 根据计数器数据算出当前耗时。
     * @param timeCount 计数器数据。
     * @return 当前耗时。
     */
    public static String calculateTimeStr(long timeCount) {
        int hours = (int) (timeCount / 3600000);
        int minutes = (int) ((timeCount / 60000) % 60);
        int seconds = (int) ((timeCount / 1000) % 60);
        int ms = (int) (timeCount % 1000);
        return String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, ms);
    }
}
