package com.lihao.rxjavademo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * 工具类。
 */
public class Utils {

    /**
     * 获取应用程序包名。
     * @param context 上下文。
     * @return 包名。
     */
    public static String getPackageName(Context context) {
        String packageName = "";
        if (context != null) {
            packageName = context.getPackageName();
        }
        return packageName;
    }

    /**
     * 跳转到系统应用详情页面，方便用户设定权限。
     * @param context 上下文。
     */
    public static void gotoSettingAppDetail(Context context) {
        if (context != null) {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName(context)));
            context.startActivity(intent);
        }
    }

    /**
     * 从持久化到磁盘上的数据中恢复对象。
     * @param context 应用程序上下文。
     * @param key 关键字。
     * @return 值。
     */
    public static Object restoreObject(Context context, String key) {
        return restoreObject(context, key, null);
    }

    /**
     * 从持久化到磁盘上的数据中恢复对象。
     * @param context 应用程序上下文。
     * @param key 关键字。
     * @param subDirectory 子目录名称。
     * @return 值。
     */
    public static Object restoreObject(Context context, String key, String subDirectory) {
        if (key == null) {
            return null;
        }
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            File appFileDir = context.getFilesDir();
            String objFilePath = appFileDir.getAbsolutePath().concat(File.separator)
                    .concat(TextUtils.isEmpty(subDirectory) ? "" : subDirectory.concat(File.separator))
                    .concat(Base64.encodeToString(key.getBytes(), Base64.DEFAULT));
            File objFile = new File(objFilePath);
            if (!objFile.exists()) {
                return null;
            }
            fis = new FileInputStream(objFile);
            ois = new ObjectInputStream(fis);
            Object obj = ois.readObject();
            return obj;
        } catch (Exception e) {
            /* 操作有误。 */
        } finally {
            try {
                if (ois != null) {
                    ois.close();
                }
                if (fis != null) {
                    fis.close();
                }
            } catch (Exception e) {
                /* 发生了异常。 */
            }
        }
        return null;
    }

    /**
     * 将对象持久化保存在磁盘上。
     * @param context 应用程序上下文。
     * @param key 关键字。
     * @param obj 值。
     * @return 操作结果。
     */
    public static boolean storeObject(Context context, String key, Object obj) {
        return storeObject(context, key, obj, null);
    }

    /**
     * 将对象持久化保存在磁盘上。
     * @param context 应用程序上下文。
     * @param key 关键字。
     * @param obj 值。
     * @param subDirectory 子目录名称。
     * @return 操作结果。
     */
    public static boolean storeObject(Context context, String key, Object obj, String subDirectory) {
        if (key == null || obj == null) {
            return false;
        }
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            File appFileDir = context.getFilesDir();
            String objFilePath = appFileDir.getAbsolutePath().concat(File.separator)
                    .concat(TextUtils.isEmpty(subDirectory) ? "" : subDirectory.concat(File.separator))
                    .concat(Base64.encodeToString(key.getBytes(), Base64.DEFAULT));
            File objFile = new File(objFilePath);
            if (!objFile.getParentFile().exists() || !objFile.getParentFile().isDirectory()) {
                objFile.getParentFile().mkdirs(); // 创建父层目录。
            }
            if (!objFile.exists()) {
                objFile.createNewFile(); // 创建文件。
            }
            fos = new FileOutputStream(objFile);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(obj); // 写入数据。

            return true;
        } catch (Exception e) {
            /* 操作有误。 */
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (oos != null) {
                    oos.flush();
                    oos.close();
                }
                if (fos != null) {
                    fos.flush();
                    fos.close();
                }
            } catch (Exception e) {
                /* 发生了异常。 */
            }
        }
    }

    /**
     * 删除持久化对象。
     * @param context 应用程序上下文。
     * @param key 关键字。
     * @return 操作结果。
     */
    public static boolean deleteObject(Context context, String key) {
        return deleteObject(context, key, null);
    }

    /**
     * 删除持久化对象。
     * @param context 应用程序上下文。
     * @param key 关键字。
     * @param subDirectory 子目录名称。
     * @return 操作结果。
     */
    public static boolean deleteObject(Context context, String key, String subDirectory) {
        try {
            File appFileDir = context.getFilesDir();
            String objFilePath = appFileDir.getAbsolutePath().concat(File.separator)
                    .concat(TextUtils.isEmpty(subDirectory) ? "" : subDirectory.concat(File.separator))
                    .concat(Base64.encodeToString(key.getBytes(), Base64.DEFAULT));
            File objFile = new File(objFilePath);
            return objFile.delete();
        } catch (Exception e) {
            /* 操作有误。 */
        }
        return false;
    }
}
