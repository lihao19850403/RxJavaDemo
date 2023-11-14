package com.lihao.rxjavademo.circle4_game.viewmodel;

import com.lihao.rxjavademo.circle4_game.GameUtils;
import com.lihao.rxjavademo.circle4_game.model.GameState;
import com.lihao.rxjavademo.circle4_game.model.GameStatus;
import com.lihao.rxjavademo.circle4_game.model.GridPosition;
import com.lihao.rxjavademo.circle4_game.model.SymbolType;

import androidx.core.util.Pair;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.BehaviorSubject;

public class GameViewModel {

    private final CompositeDisposable mSubscriptions = new CompositeDisposable();
    private final BehaviorSubject<GameState> mGameStateSubject = BehaviorSubject.createDefault(new GameState());
    private final Observable<GridPosition> mFilteredTouchEventObservable;
    private final Observable<SymbolType> mPlayerInTurnObservable;
    private final Observable<GameStatus> mGameStatusObservable;
    private final Observable<GameState> mResetObservable;
    private final Observable<Boolean> mSaveObservable;

    public GameViewModel(Observable<GridPosition> touchEventObservable, Observable<Object> resetObservable, Observable<Object> saveObservable) {
        mPlayerInTurnObservable = mGameStateSubject.map(gameState -> gameState.lastPlayedSymbol == SymbolType.BLACK ? SymbolType.RED : SymbolType.BLACK);
        mGameStatusObservable = mGameStateSubject.map(gameState -> GameUtils.calculateWinnerForGrid(gameState.gameGrid));
        mFilteredTouchEventObservable = touchEventObservable.withLatestFrom(mGameStateSubject, (gridPosition, gameState) -> new Pair(gridPosition, gameState))
                .filter(pair -> GameUtils.calculateValidTouch((GridPosition) pair.first, (GameState) pair.second))
                .withLatestFrom(mGameStatusObservable, (pair, gameStatus) -> new Pair(pair, gameStatus))
                .filter(pair -> !((GameStatus) pair.second).isEnded)
                .map(pair -> GameUtils.dropToNewPosition((GridPosition) ((Pair) pair.first).first, (GameState) ((Pair) pair.first).second));
        mResetObservable = resetObservable.withLatestFrom(mGameStateSubject, (object, gameState) -> new Pair(object, gameState))
                .map(pair -> ((GameState) pair.second).reset());
        mSaveObservable = saveObservable.withLatestFrom(mGameStateSubject, (object, gameState) -> new Pair(object, gameState))
                .map(pair -> GameUtils.saveGame((GameState) pair.second));
    }

    public Observable<GameState> getGameState() {
        return mGameStateSubject.hide();
    }

    public void updateGameState(GameState newGameState) {
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
