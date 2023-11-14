package com.lihao.rxjavademo.ox_game.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.lihao.rxjavademo.R;
import com.lihao.rxjavademo.ox_game.viewmodel.GameViewModel;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

public class OXGameActivity extends AppCompatActivity {

    private InteractiveGameGridView mInteractiveGameGridView;
    private PlayerView mPlayerView;
    private TextView mWinnerView;
    private Button mResetBtn;

    private final CompositeDisposable mSubscriptions = new CompositeDisposable();
    private GameViewModel mGameViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_ox);

        mInteractiveGameGridView = findViewById(R.id.game_grid_view);
        mPlayerView = findViewById(R.id.player_view);
        mWinnerView = findViewById(R.id.winner_view);
        mResetBtn = findViewById(R.id.reset_game);

        mGameViewModel = new GameViewModel(mInteractiveGameGridView.getTouchesOnGrid(), RxView.clicks(mResetBtn));
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
                    if (gameStatus.isEnded && gameStatus.winner != null) {
                        mWinnerView.setVisibility(View.VISIBLE);
                        mWinnerView.setText("Winner:".concat("\n").concat(gameStatus.winner.name()));
                    } else {
                        mWinnerView.setVisibility(View.GONE);
                    }
                }));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSubscriptions.clear();
        mGameViewModel.unsubscribe();
    }
}
