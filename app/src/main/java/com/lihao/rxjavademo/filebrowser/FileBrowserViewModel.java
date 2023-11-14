package com.lihao.rxjavademo.filebrowser;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;

public class FileBrowserViewModel {

    private Observable<File> mListItemClickObservable;
    private Observable<Object> mPreviousClickObservable;
    private Observable<Object> mRootClickObservable;
    private File mFileSystemRoot;
    private Function<File, Observable<List<File>>> mGetFiles;
    private Function<Void, Void> mWarning;

    private final BehaviorSubject<Map<File, List<File>>> mFilesOutput = BehaviorSubject.create();
    private final CompositeDisposable mSubscriptions = new CompositeDisposable();

    public FileBrowserViewModel(Observable<File> listItemClickObservable,
                                Observable<Object> previousClickObservable,
                                Observable<Object> rootClickObservable,
                                File fileSystemRoot,
                                Function<File, Observable<List<File>>> getFiles,
                                Function<Void, Void> warning) {
        this.mListItemClickObservable = listItemClickObservable;
        this.mPreviousClickObservable = previousClickObservable;
        this.mRootClickObservable = rootClickObservable;
        this.mFileSystemRoot = fileSystemRoot;
        this.mGetFiles = getFiles;
        this.mWarning = warning;
    }

    public Observable<Map<File, List<File>>> getFilesObservable() {
        return mFilesOutput.hide(); // 该Subject是最终Subject，仅需初始化一次。确保订阅该Subject的任何用户都坚信只要次视图模型存在，这个Subject的值就不会改变。
    }

    /**
     * 订阅。
     */
    public void subscribe() {
        final BehaviorSubject<File> selectedFile = BehaviorSubject.createDefault(mFileSystemRoot); // 默认选中根目录。
        Observable<File> previousFileObservable = mPreviousClickObservable
                .map(event -> {
                    if (mFileSystemRoot.getAbsolutePath().equals(selectedFile.getValue().getAbsolutePath())) {
                        mWarning.apply(null);
                        return selectedFile.getValue();
                    } else {
                        return selectedFile.getValue().getParentFile();
                    }
                });
        Observable<File> rootClickObservable = mRootClickObservable
                .map(event -> {
                    if (mFileSystemRoot.getAbsolutePath().equals(selectedFile.getValue().getAbsolutePath())) {
                        mWarning.apply(null);
                    }
                    return mFileSystemRoot;
                });
        mSubscriptions.add(Observable.merge(mListItemClickObservable, previousFileObservable, rootClickObservable)
                .subscribe(selectedFile::onNext));
        mSubscriptions.add(
                selectedFile.switchMap(file -> mGetFiles.apply(file))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(files -> {
                            Map<File, List<File>> param = new HashMap<File, List<File>>(1);
                            param.put(selectedFile.getValue(), files);
                            mFilesOutput.onNext(new HashMap<File, List<File>>(param));
                        }));
    }

    public void unsubscribe() {
        mSubscriptions.clear();
    }
}










