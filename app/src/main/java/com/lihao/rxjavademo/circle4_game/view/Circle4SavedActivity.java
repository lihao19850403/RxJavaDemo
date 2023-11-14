package com.lihao.rxjavademo.circle4_game.view;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.LinearLayout;

import com.lihao.rxjavademo.R;
import com.lihao.rxjavademo.circle4_game.Circle4SavedBrowserAdapter;
import com.lihao.rxjavademo.circle4_game.GameUtils;
import com.lihao.rxjavademo.circle4_game.model.Constants;

import java.io.File;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;

public class Circle4SavedActivity extends AppCompatActivity {

    private final int ROW_COUNT = 3;
    private final int PADDING = 10;

    private RecyclerView infoList;
    private LinearLayout emptyTipZone;
    private Circle4SavedBrowserAdapter circle4SavedBrowserAdapter;

    private final CompositeDisposable mSubscriptions = new CompositeDisposable();
    private Observable<String> listViewItemClickObservable;
    private Observable<String> listViewItemLongClickObservable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_circle4_browser);

        infoList = findViewById(R.id.info_list);
        emptyTipZone = findViewById(R.id.empty_tip_zone);
        circle4SavedBrowserAdapter = new Circle4SavedBrowserAdapter();
        GridLayoutManager infoListLayoutManager = new GridLayoutManager(this, ROW_COUNT);
        infoList.setLayoutManager(infoListLayoutManager);
        infoList.setAdapter(circle4SavedBrowserAdapter);
        infoList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                int itemIndex = parent.getChildLayoutPosition(view);
                int line = itemIndex / ROW_COUNT;
                int number = itemIndex % ROW_COUNT;
                int totalLine = circle4SavedBrowserAdapter.getItemCount() / ROW_COUNT + (circle4SavedBrowserAdapter.getItemCount() % ROW_COUNT == 0 ? 0 : 1);
                outRect.top = line == 0 ? PADDING : PADDING / 2;
                outRect.bottom = line == totalLine - 1 ? PADDING : PADDING / 2;
                outRect.left = number == 0 ? PADDING : PADDING / 2;
                outRect.right = number == ROW_COUNT - 1 ? PADDING : PADDING / 2;
            }
        });

        // 获取存档。
        File savedGamesPath = new File(getFilesDir().getAbsolutePath().concat(File.separator).concat(Constants.SAVED_GAMES_PATH).concat(File.separator));
        final BehaviorSubject<File> savedGamesSubject = BehaviorSubject.createDefault(savedGamesPath);
        mSubscriptions.add(savedGamesSubject
                .map(GameUtils::getSavedGameNames)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::updateList));

        // 单击使用存档。
        listViewItemClickObservable = Observable.create(emitter -> circle4SavedBrowserAdapter
                .setOnItemClickListener((view, savedGameName) -> emitter.onNext(savedGameName)));
        mSubscriptions.add(listViewItemClickObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(savedGameName -> {
                    Intent intent = new Intent();
                    intent.putExtra(Constants.LOADED_SAVED_GAME_NAME, savedGameName);
                    setResult(RESULT_OK, intent);
                    finish();
                }));

        // 长按删除存档。
        listViewItemLongClickObservable = Observable.create(emitter -> circle4SavedBrowserAdapter
                .setOnItemLongClickListener((view, savedGameName) -> new AlertDialog.Builder(this)
                        .setTitle(R.string.tip)
                        .setMessage(getString(R.string.delete_saved_game).concat(savedGameName).concat("？"))
                        .setCancelable(false)
                        .setNegativeButton(R.string.cancel, (dialog, which) -> emitter.onComplete())
                        .setPositiveButton(R.string.confirm, (dialog, which) -> emitter.onNext(savedGameName))
                        .create()
                        .show()));
        mSubscriptions.add(listViewItemLongClickObservable
                .map(savedGameName -> GameUtils.deleteSavedGame(savedGameName))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(savedGameName -> savedGamesSubject.onNext(savedGamesPath)));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSubscriptions.clear();
    }

    private void updateList(List<String> savedGamesNames) {
        circle4SavedBrowserAdapter.setDataList(savedGamesNames);
        circle4SavedBrowserAdapter.notifyDataSetChanged();
        if (savedGamesNames.size() == 0) {
            infoList.setVisibility(View.GONE);
            emptyTipZone.setVisibility(View.VISIBLE);
        } else {
            infoList.setVisibility(View.VISIBLE);
            emptyTipZone.setVisibility(View.GONE);
        }
    }
}