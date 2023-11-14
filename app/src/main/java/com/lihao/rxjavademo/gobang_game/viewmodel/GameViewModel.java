package com.lihao.rxjavademo.gobang_game.viewmodel;

import com.lihao.rxjavademo.gobang_game.GameUtils;
import com.lihao.rxjavademo.gobang_game.model.GameState;
import com.lihao.rxjavademo.gobang_game.model.GameStatus;
import com.lihao.rxjavademo.gobang_game.model.GridPosition;
import com.lihao.rxjavademo.gobang_game.model.SymbolType;

import java.util.concurrent.TimeUnit;

import androidx.core.util.Pair;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.BehaviorSubject;

public class GameViewModel {

    private long mBlackTimeCount = 1;
    private long mRedTimeCount = 1;

    private final CompositeDisposable mSubscriptions = new CompositeDisposable();
    private final BehaviorSubject<GameState> mGameStateSubject = BehaviorSubject.createDefault(new GameState());
    private final Observable<GridPosition> mFilteredTouchEventObservable;
    private final Observable<SymbolType> mPlayerInTurnObservable;
    private final Observable<GameStatus> mGameStatusObservable;
    private final Observable<GameState> mResetObservable;
    private final Observable<Boolean> mSaveObservable;
    private final Observable<String> mBlackTimerObservable;
    private final Observable<String> mRedTimerObservable;

    public GameViewModel(Observable<GridPosition> touchEventObservable, Observable<Object> resetObservable, Observable<Object> saveObservable) {
        mPlayerInTurnObservable = mGameStateSubject.map(gameState -> gameState.lastPlayedSymbol == SymbolType.BLACK ? SymbolType.RED : SymbolType.BLACK);
        mGameStatusObservable = mGameStateSubject.map(gameState -> GameUtils.calculateWinnerForGrid(gameState.gameGrid));
        mFilteredTouchEventObservable = touchEventObservable.withLatestFrom(mGameStateSubject, (gridPosition, gameState) -> new Pair(gridPosition, gameState))
                .filter(pair -> ((GameState) pair.second).isEmpty((GridPosition) pair.first))
                .map(pair -> (GridPosition) pair.first)
                .withLatestFrom(mGameStatusObservable, (gridPosition, gameStatus) -> new Pair(gridPosition, gameStatus))
                .filter(pair -> !((GameStatus) pair.second).isEnded)
                .map(pair -> (GridPosition) pair.first);
        mResetObservable = resetObservable.withLatestFrom(mGameStateSubject, (object, gameState) -> new Pair(object, gameState))
                .map(pair -> {
                    mBlackTimeCount = mRedTimeCount = 0;
                    return ((GameState) pair.second).reset();
                });
        mSaveObservable = saveObservable.withLatestFrom(mGameStateSubject, (object, gameState) -> new Pair(object, gameState))
                .map(pair -> GameUtils.saveGame((GameState) pair.second, new long[]{ mBlackTimeCount, mRedTimeCount }));
        // 计时器。
        mBlackTimerObservable = Observable.interval(mBlackTimeCount, TimeUnit.MILLISECONDS)
                .withLatestFrom(mGameStatusObservable, (count, gameStatus) -> new Pair(count, gameStatus))
                .filter(pair -> !((GameStatus) pair.second).isEnded)
                .map(pair -> (long) pair.first)
                .withLatestFrom(mGameStateSubject, (count, gameState) -> new Pair(count, gameState))
                .map(pair -> {
                    SymbolType lastPlayer = ((GameState) pair.second).lastPlayedSymbol;
                    return GameUtils.calculateTimeStr(lastPlayer == SymbolType.EMPTY ? 0 : lastPlayer == SymbolType.RED ? mBlackTimeCount++ : mBlackTimeCount);
                });
        mRedTimerObservable = Observable.interval(mRedTimeCount, TimeUnit.MILLISECONDS)
                .withLatestFrom(mGameStatusObservable, (count, gameStatus) -> new Pair(count, gameStatus))
                .filter(pair -> !((GameStatus) pair.second).isEnded)
                .map(pair -> (long) pair.first)
                .withLatestFrom(mGameStateSubject, (count, gameState) -> new Pair(count, gameState))
                .map(pair -> {
                    SymbolType lastPlayer = ((GameState) pair.second).lastPlayedSymbol;
                    return GameUtils.calculateTimeStr(lastPlayer == SymbolType.EMPTY ? 0 : lastPlayer == SymbolType.BLACK ? mRedTimeCount++ : mRedTimeCount);
                });
    }

    public Observable<GameState> getGameState() {
        return mGameStateSubject.hide();
    }

    public void updateGameState(GameState newGameState, long newBlackTimeCount, long newRedTimeCount) {
        mBlackTimeCount = newBlackTimeCount;
        mRedTimeCount = newRedTimeCount;
        mGameStateSubject.onNext(newGameState);
    }

    public Observable<SymbolType> getPlayerInTurnObservable() {
        return mPlayerInTurnObservable;
    }

    public Observable<GameStatus> getGameStatusObservable() {
        return mGameStatusObservable;
    }

    public Observable<Boolean> getSaveObservable() {
        return mSaveObservable;
    }

    public Observable<String> getBlackTimerObservable() {
        return mBlackTimerObservable;
    }

    public Observable<String> getRedTimerObservable() {
        return mRedTimerObservable;
    }

    public void subscribe() {
        Observable<Pair<GameState, SymbolType>> gameInfoObservable = Observable.combineLatest(mGameStateSubject, mPlayerInTurnObservable, Pair::new);
        mSubscriptions.add(mFilteredTouchEventObservable
                .withLatestFrom(gameInfoObservable, (gridPosition, gameInfoPair) -> gameInfoPair.first.setSymbolAt(gameInfoPair.second, gridPosition))
                .subscribe(mGameStateSubject::onNext));
        mSubscriptions.add(mResetObservable
                .subscribe(mGameStateSubject::onNext));
    }

    public void unsubscribe() {
        mSubscriptions.clear();
    }
}
