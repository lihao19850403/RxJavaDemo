package com.lihao.rxjavademo.ox_game.viewmodel;

import com.lihao.rxjavademo.ox_game.GameUtils;
import com.lihao.rxjavademo.ox_game.model.GameState;
import com.lihao.rxjavademo.ox_game.model.GameStatus;
import com.lihao.rxjavademo.ox_game.model.GridPosition;
import com.lihao.rxjavademo.ox_game.model.SymbolType;

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

    public GameViewModel(Observable<GridPosition> touchEventObservable, Observable<Object> resetObservable) {
        mPlayerInTurnObservable = mGameStateSubject.map(gameState -> gameState.lastPlayedSymbol == SymbolType.BLACK ? SymbolType.RED : SymbolType.BLACK);
        mGameStatusObservable = mGameStateSubject.map(gameState -> {
            SymbolType winner = GameUtils.calculateWinnerForGrid(gameState.gameGrid);
            return new GameStatus(winner != null, winner);
        });
        mFilteredTouchEventObservable = touchEventObservable.withLatestFrom(mGameStateSubject, (gridPosition, gameState) -> new Pair(gridPosition, gameState))
                .filter(pair -> ((GameState) pair.second).isEmpty((GridPosition) pair.first))
                .withLatestFrom(mGameStatusObservable, (pair, gameStatus) -> new Pair(pair, gameStatus))
                .filter(pair -> !((GameStatus) pair.second).isEnded)
                .map(pair -> (GridPosition) ((Pair) pair.first).first);
        mResetObservable = resetObservable.withLatestFrom(mGameStateSubject, (object, gameState) -> gameState.reset());
    }

    public Observable<GameState> getGameState() {
        return mGameStateSubject.hide();
    }

    public Observable<SymbolType> getPlayerInTurnObservable() {
        return mPlayerInTurnObservable;
    }

    public Observable<GameStatus> getGameStatusObservable() {
        return mGameStatusObservable;
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
