package com.lihao.rxjavademo.gobang_game.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding2.view.RxView;
import com.lihao.rxjavademo.R;
import com.lihao.rxjavademo.gobang_game.GameUtils;
import com.lihao.rxjavademo.gobang_game.model.Constants;
import com.lihao.rxjavademo.gobang_game.model.GameState;
import com.lihao.rxjavademo.gobang_game.model.SymbolType;
import com.lihao.rxjavademo.gobang_game.viewmodel.GameViewModel;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

public class GobangGameActivity extends AppCompatActivity {

    private InteractiveGameGridView mInteractiveGameGridView;
    private PlayerView mPlayerView;
    private TextView mWinnerView;
    private Button mResetBtn;
    private Button mSaveBtn;
    private Button mLoadBtn;
    private TextView mBlackTimerView;
    private TextView mRedTimerView;
    private PlayerView mBlackPlayerView;
    private PlayerView mRedPlayerView;

    private final CompositeDisposable mSubscriptions = new CompositeDisposable();
    private GameViewModel mGameViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_gobang);

        mInteractiveGameGridView = findViewById(R.id.game_grid_view);
        mPlayerView = findViewById(R.id.player_view);
        mWinnerView = findViewById(R.id.winner_view);
        mResetBtn = findViewById(R.id.reset_game);
        mSaveBtn = findViewById(R.id.save_game);
        mLoadBtn = findViewById(R.id.load_game);
        mBlackTimerView = findViewById(R.id.black_timer);
        mRedTimerView = findViewById(R.id.red_timer);
        mBlackPlayerView = findViewById(R.id.black_player_view);
        mBlackPlayerView.setData(SymbolType.BLACK);
        mRedPlayerView = findViewById(R.id.red_player_view);
        mRedPlayerView.setData(SymbolType.RED);

        mGameViewModel = new GameViewModel(mInteractiveGameGridView.getTouchesOnGrid(), RxView.clicks(mResetBtn), RxView.clicks(mSaveBtn));
        mGameViewModel.subscribe();
        mSubscriptions.add(mGameViewModel.getGameState()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(gameState -> mInteractiveGameGridView.setData(gameState.gameGrid)));
        mSubscriptions.add(mGameViewModel.getPlayerInTurnObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(symbolType -> mPlayerView.setData(symbolType)));
        mSubscriptions.add(mGameViewModel.getGameStatusObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(gameStatus -> {
                    mInteractiveGameGridView.setGameStatus(gameStatus);
                    if (gameStatus.isEnded && gameStatus.winner != null) {
                        mWinnerView.setVisibility(View.VISIBLE);
                        mWinnerView.setText("Winner:".concat("\n").concat(gameStatus.winner.name()));
                        mSaveBtn.setEnabled(false);
                    } else {
                        mWinnerView.setVisibility(View.GONE);
                        mSaveBtn.setEnabled(true);
                    }
                }));
        mSubscriptions.add(mGameViewModel.getSaveObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> Toast.makeText(this, getString(R.string.save_result) + result, Toast.LENGTH_LONG).show()));
        mSubscriptions.add(RxView.clicks(mLoadBtn)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> {
                    Intent intent = new Intent(this, GobangSavedActivity.class);
                    startActivityForResult(intent, Constants.LOAD_ONE_SAVED_GAME_REQUEST);
                }));
        // 计时器。
        mSubscriptions.add(mGameViewModel.getBlackTimerObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mBlackTimerView::setText));
        mSubscriptions.add(mGameViewModel.getRedTimerObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mRedTimerView::setText));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSubscriptions.clear();
        mGameViewModel.unsubscribe();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case Constants.LOAD_ONE_SAVED_GAME_REQUEST: {
                String savedGameData = data.getStringExtra(Constants.LOADED_SAVED_GAME_NAME);
                Pair<GameState, long[]> gameInfoPair = GameUtils.loadGame(savedGameData);
                GameState newGameState = gameInfoPair.first;
                mGameViewModel.updateGameState(newGameState, gameInfoPair.second[0], gameInfoPair.second[1]);
            }
            break;
            default:
                break;
        }
    }
}
