package com.lihao.rxjavademo.ox_game.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.jakewharton.rxbinding2.view.RxView;
import com.lihao.rxjavademo.ox_game.model.GridPosition;

import androidx.annotation.Nullable;
import io.reactivex.Observable;

public class InteractiveGameGridView extends GameGridView {

    public InteractiveGameGridView(Context context) {
        super(context);
    }

    public InteractiveGameGridView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public InteractiveGameGridView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public Observable<GridPosition> getTouchesOnGrid() {
        Observable<GridPosition> gridPositionObservable = RxView.touches(this, event -> true)
                .filter(event -> event.getAction() == MotionEvent.ACTION_UP)
                .map(event -> getGridPosition(event.getX(), event.getY()));
        return gridPositionObservable;
    }
}
