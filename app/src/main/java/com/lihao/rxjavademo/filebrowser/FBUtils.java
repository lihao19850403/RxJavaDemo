package com.lihao.rxjavademo.filebrowser;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.webkit.MimeTypeMap;

import com.lihao.rxjavademo.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.core.content.FileProvider;
import io.reactivex.Observable;

/**
 * 文件浏览器工具类。
 */
public class FBUtils {

    /**
     * 创建一个发射器，用于向订阅者发送目录/文件列表。
     *
     * @param file 给定路径。
     * @return 发射器。
     */
    public static Observable<List<File>> createFileObservable(File file) {
        return Observable.create(emitter -> {
            try {
                final List<File> fileList = getFiles(file);
                emitter.onNext(fileList);
                emitter.onComplete();
            } catch (Exception e) {
                emitter.onError(e);
            }
        });
    }

    /**
     * 打开文件。
     *
     * @param context 上下文。
     * @param f       带打开的文件。
     */
    public static void openFile(Context context, File f) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        String extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(f).toString());
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        if (Build.VERSION.SDK_INT >= 24) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri uri = FileProvider.getUriForFile(context, Utils.getPackageName(context).concat(".provider"), f);
            intent.setDataAndType(uri, mimeType);
        } else {
            intent.setDataAndType(Uri.fromFile(f), mimeType);
        }
        context.startActivity(intent);
    }

    private static List<File> getFiles(File file) {
        List<File> filelist = new ArrayList<File>();
        if (file == null || !file.isDirectory()) {
            return filelist;
        }
        File[] files = file.listFiles();
        if (files == null || files.length == 0) {
            return filelist;
        }
        for (File fileItem : files) {
            if (!fileItem.isHidden() && fileItem.canRead()) {
                filelist.add(fileItem);
            }
        }
        return filelist;
    }
}
