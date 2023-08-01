package com.momo.sdk.update.custom;

import static android.content.Context.DOWNLOAD_SERVICE;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.widget.Toast;


import com.u8.sdk.SDKTools;
import com.u8.sdk.utils.LogUtil;

import java.io.File;
import java.text.NumberFormat;

/**
 * Created by andim on 2019/10/22.
 */

public class UpdateNew_background_Version {
    private DownloadManager mDownloadManager;
    private Context mContext;
    private long downloadId;
    private String apkName;
    private DownloadManager.Request request;

    public UpdateNew_background_Version(Context context) {
        mContext = context;
    }

    UpdateCallBack callBack;
    ProgressCallBack progCallback;

    public void setUpdateCallback(UpdateCallBack callBack) {
        this.callBack = callBack;
    }

    public UpdateCallBack getUpdateCallback() {
        return callBack;
    }

    public void setProgressCallback(ProgressCallBack progCallback) {
        this.progCallback = progCallback;
    }

    public ProgressCallBack getProgCallback() {
        return progCallback;
    }

    /**
     * @param url 下载路径
     */
    public void download(String url) {
        File path = new File(Environment.DIRECTORY_DOWNLOADS);         //通过变量文件来获取需要创建的文件夹名字
        if (!path.exists()) { //如果该文件夹不存在，则进行创建
            path.mkdirs();//创建文件夹
        }

        Uri uri = Uri.parse(url);
        LogUtil.d("uri:" + uri + "---path:" + getDownloadFileName(mContext));
        request = new DownloadManager.Request(uri);

        //正常下载流程
        request.setAllowedOverRoaming(false);

        String updateTips = mContext.getString(UpdateTools.getIdByName(mContext, "string", "u8_notification_update_tips"));
        String updating = mContext.getString(UpdateTools.getIdByName(mContext, "string", "u8_notification_update_download"));
        //通知栏显示
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setTitle(updateTips);
        request.setDescription(updating);
        request.setVisibleInDownloadsUi(true);

        // 设置下载路径和文件名
        int idx = url.lastIndexOf("/");
        apkName = url.substring(idx + 1);
        LogUtil.d("apkName:" + apkName + "--renameTo:" + getApkName(mContext));
        request.setDestinationInExternalFilesDir(mContext, Environment.DIRECTORY_DOWNLOADS,
                getApkName(mContext));

        //获取DownloadManager
        if (mDownloadManager == null) {
            mDownloadManager = (DownloadManager) mContext.getSystemService(DOWNLOAD_SERVICE);
        }

        if (mDownloadManager != null) {
            downloadId = mDownloadManager.enqueue(request);
        }
        LogUtil.d("downloadId:" + downloadId);
        startQuery();
        mContext.registerReceiver(mReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            checkStatus(context);
        }
    };


    /**
     * 检查下载状态
     *
     * @param context
     */
    private void checkStatus(Context context) {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadId);
        Cursor cursor = mDownloadManager.query(query);
        if (cursor.moveToFirst()) {
            String fileName = cursor.getString(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_URI));
            int status = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS));
            switch (status) {
                //下载暂停
                case DownloadManager.STATUS_PAUSED:
                    break;
                //下载延迟
                case DownloadManager.STATUS_PENDING:
                    break;
                //正在下载
                case DownloadManager.STATUS_RUNNING:
                    break;
                //下载完成
                case DownloadManager.STATUS_SUCCESSFUL:
                    LogUtil.d("下载完成的路径:" + fileName + "\r\n我检查的路径:" + getDownloadFileName(context));
                    // 如果文件名不为空，说明文件已存在,则进行自动安装apk
                    if (fileName != null) {
//                        openAPK(context, fileName);
                        if (callBack != null) {
                            getUpdateCallback().callback(true);
                            openApkNew(context, fileName);
                            LogUtil.d("openAPK fileName:" + fileName);
                        }
                    }
                    break;
                //下载失败
                case DownloadManager.STATUS_FAILED:
                    LogUtil.d("下载失败！");
                    break;
            }
        }
        cursor.close();
    }

    //更新下载进度
    private void startQuery() {
        if (downloadId != 0) {
            mHandler.post(mQueryProgressRunnable);
        }
    }

    private final QueryRunnable mQueryProgressRunnable = new QueryRunnable();

    //查询下载进度
    private class QueryRunnable implements Runnable {
        @Override
        public void run() {
            queryState();
            mHandler.postDelayed(mQueryProgressRunnable, 100);
        }
    }

    //停止查询下载进度
    private void stopQuery() {
        mHandler.removeCallbacks(mQueryProgressRunnable);
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1001) {
                System.out.println(msg.arg1 + "--------usdkkkkk--------" + msg.arg2);
                NumberFormat numberFormat = NumberFormat.getInstance();
                // 设置精确到小数点后2位
                numberFormat.setMaximumFractionDigits(0);
                String result = numberFormat.format((float) msg.arg1 / (float) msg.arg2 * 100);
                System.out.println("diliverNum和queryMailNum的百分比为:" + result + "%");
                getProgCallback().progCallback(Integer.parseInt(result));
                if (msg.arg1 == msg.arg2) {
                    stopQuery();
                    getUpdateCallback().callback(true);
                }
            }
        }
    };

    //查询下载进度
    private void queryState() {
        // 通过ID向下载管理查询下载情况，返回一个cursor
        Cursor c = mDownloadManager.query(new DownloadManager.Query().setFilterById(downloadId));
        if (c == null) {
            Toast.makeText(mContext, "下载失败", Toast.LENGTH_SHORT).show();

        } else { // 以下是从游标中进行信息提取
            if (!c.moveToFirst()) {
                Toast.makeText(mContext, "下载失败", Toast.LENGTH_SHORT).show();
                if (!c.isClosed()) {
                    c.close();
                }
                return;
            }
            int mDownload_so_far = c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
            int mDownload_all = c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
            Message msg = Message.obtain();
            if (mDownload_all > 0) {
                msg.what = 1001;
                msg.arg1 = mDownload_so_far;
                msg.arg2 = mDownload_all;
                mHandler.sendMessage(msg);
            }
            if (!c.isClosed()) {
                c.close();
            }
        }
    }

    public static String getDownloadFileName(Context context) {
        return "file://" + context.getExternalFilesDir(DOWNLOAD_SERVICE) + "/" + getApkName(context);
    }

    public static String getApkName(Context context) {
//        return "game_" + U8SDK.getInstance().getAppID() + "_versioncode_" + SDKTools.getVersionCode(context) + ".apk";
        return "game_updateDemo_versioncode_" + SDKTools.getVersionCode(context) + ".apk";
    }

    private static boolean isGranted = false;

    // TagHere 这里处理检查apk的完整性
    public static boolean isCacheApkFileExists(Context context) {
        File apkfile = new File(getApkFolder(context), getApkName(context));
        if (apkfile.length() == 0 && apkfile.exists()) {
            LogUtil.d("apkfile.delete()==" + apkfile.delete() + "---apkfile:" + apkfile.getAbsolutePath());
            return false;
        }
        return apkfile.exists();
    }

    public static void deleteCacheApkFile(Context context){
        File apkfile = new File(getApkFolder(context), getApkName(context));
        if (apkfile.exists()){
            apkfile.delete();
        }
    }

    public static String getApkFolder(Context mContext) {
        String defalt_sdcard_folder = mContext.getExternalFilesDir(DOWNLOAD_SERVICE).getAbsolutePath();
        return defalt_sdcard_folder;
    }

    public static void openApkNew(Context context, String fileSavePath) {
        if (Build.VERSION.SDK_INT >= 26) {
            startInstallO(context, fileSavePath);
        } else if (Build.VERSION.SDK_INT >= 24) {
            startInstallN(context, fileSavePath);
        } else {
            startInstall(context, fileSavePath);
        }
    }

    private static void startInstall(Context context, String fileSavePath) {
        File file = new File(Uri.parse(fileSavePath).getPath());
        String filePath = file.getAbsolutePath();
        Intent install = new Intent("android.intent.action.VIEW");
        Uri data = Uri.fromFile(file);
        install.setDataAndType(data, "application/vnd.android.package-archive");
        install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(install);
    }

    private static void startInstallN(Context context, String fileSavePath) {
        File file = new File(Uri.parse(fileSavePath).getPath());
        String filePath = file.getAbsolutePath();
        try {
            Uri apkUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", new File(filePath));
            Intent install = new Intent("android.intent.action.VIEW");

            install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            install.setDataAndType(apkUri, "application/vnd.android.package-archive");
            context.startActivity(install);
        } catch (Exception e) {
            isGranted = false;
            e.printStackTrace();
        }
    }

    private static void startInstallO(Context context, String fileSavePath) {
        boolean canGranted = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            canGranted = context.getPackageManager().canRequestPackageInstalls();
        }
        if (canGranted || isGranted) {
            startInstallN(context, fileSavePath);
        } else {
            isGranted = true;
            Uri packageURI = Uri.parse("package:" + context.getPackageName());
            Intent intent = new Intent("android.settings.MANAGE_UNKNOWN_APP_SOURCES", packageURI);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }
}
