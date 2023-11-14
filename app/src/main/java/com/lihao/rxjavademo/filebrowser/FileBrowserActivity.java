package com.lihao.rxjavademo.filebrowser;

import android.Manifest;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.jakewharton.rxbinding2.view.RxView;
import com.lihao.rxjavademo.MApplication;
import com.lihao.rxjavademo.R;
import com.lihao.rxjavademo.Utils;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

public class FileBrowserActivity extends AppCompatActivity {

    final private RxPermissions rxPermissions = new RxPermissions(this);

    private Button previousBtn;
    private Button rootBtn;
    private RecyclerView infoList;
    private LinearLayout emptyTipZone;
    private FileBrowserAdapter fileBrowserAdapter;
    private final File root = Environment.getExternalStorageDirectory();
    private final CompositeDisposable cleanableSubscriptions = new CompositeDisposable();

    private Observable<File> listViewObservable;
    private Observable<Object> previousBtnObservable;
    private Observable<Object> rootBtnObservable;
    private FileBrowserViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_browser);

        // 申请权限。
        Observable<Permission> po = rxPermissions.requestEach(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        cleanableSubscriptions.add(po.observeOn(AndroidSchedulers.mainThread())
                .subscribe(permission -> {
                    if (permission.granted) { // 授权成功。
                        continueInit();
                        viewModel.subscribe();
                    } else if (permission.shouldShowRequestPermissionRationale) { // 拒绝。
                        tipToExit();
                    } else { // 永久拒绝。
                        tipToSettingsPage();
                    }
                }));
    }

    @Override
    public void onBackPressed() {
        if (root.getAbsolutePath().equals(getTitle())) {
            super.onBackPressed();
        } else {
            previousBtn.performClick();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cleanableSubscriptions.clear();
        if (viewModel != null) {
            viewModel.unsubscribe();
        }
    }

    /**
     * 继续进行初始化操作。
     */
    private void continueInit() {
        previousBtn = findViewById(R.id.previous_btn);
        rootBtn = findViewById(R.id.root_btn);
        infoList = findViewById(R.id.info_list);
        emptyTipZone = findViewById(R.id.empty_tip_zone);
        fileBrowserAdapter = new FileBrowserAdapter();

        RecyclerView.LayoutManager infoListLayoutManager = new LinearLayoutManager(this);
        infoList.setLayoutManager(infoListLayoutManager);
        infoList.setAdapter(fileBrowserAdapter);
        infoList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        listViewObservable = Observable.create(emitter -> fileBrowserAdapter
                .setOnItemClickListener((view, file) -> {
                    if (file != null && file.isDirectory()) { // 显示文件列表。
                        emitter.onNext(file);
                    } else { // 直接打开文件。
                        FBUtils.openFile(view.getContext(), file);
                    }
                }));
        previousBtnObservable = RxView.clicks(previousBtn);
        rootBtnObservable = RxView.clicks(rootBtn);

        viewModel = new FileBrowserViewModel(listViewObservable, previousBtnObservable, rootBtnObservable, root, FBUtils::createFileObservable, this::rootPathWarning);

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            cleanableSubscriptions.add(
                    viewModel.getFilesObservable()
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(this::updateList));
        }
    }

    private Void rootPathWarning(Void aVoid) {
        Toast.makeText(MApplication.getApp(), R.string.root_directory_tip, Toast.LENGTH_SHORT).show();
        return null;
    }

    private void updateList(Map<File, List<File>> param) {
        Set<Map.Entry<File, List<File>>> set = param.entrySet();
        Map.Entry<File, List<File>> entry = set.iterator().next();
        File pathFile = entry.getKey();
        List<File> list = entry.getValue();
        setTitle(pathFile.getAbsolutePath());
        fileBrowserAdapter.setDataList(list);
        fileBrowserAdapter.notifyDataSetChanged();
        if (list.size() == 0) {
            infoList.setVisibility(View.GONE);
            emptyTipZone.setVisibility(View.VISIBLE);
        } else {
            infoList.setVisibility(View.VISIBLE);
            emptyTipZone.setVisibility(View.GONE);
        }
    }

    private void tipToExit() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.tip)
                .setMessage(R.string.storage_permissions_denied)
                .setPositiveButton(R.string.confirm, (dialog, which) -> finish())
                .setCancelable(false)
                .create()
                .show();
    }

    private void tipToSettingsPage() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.tip)
                .setMessage(R.string.storage_permissions_denied_permanently)
                .setPositiveButton(R.string.confirm, (dialog, which) -> Utils.gotoSettingAppDetail(this))
                .setNegativeButton(R.string.cancel, (dialog, which) -> finish())
                .setCancelable(false)
                .create()
                .show();
    }
}