package com.u8.sdk.update;


import static com.u8.sdk.update.UpdateNew_background_Version.getDownloadFileName;
import static com.u8.sdk.update.UpdateNew_background_Version.isCacheApkFileExists;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Created by wjy on 2017/11/18.
 */

public class UpdateTipsDialog extends Dialog implements View.OnClickListener {
    //    private TextView tvClose;
    private Button btnUse;
    private Button btnUsCancel;
    private Button btnUsUpdate;
    private TextView tvTips;
    private TextView tvTitle;
    private Context context;
    private String tipsMsg;
    private String titleMsg;
    private boolean isForced;
    private String updateUrl;
    private Handler updateGameHandler;
    private int intMargin = 60;
    public static UpdateTipsDialog instance;
    private boolean clickable;

    public UpdateTipsDialog(Context context, String title, String tips, boolean isForced, String url) {
        super(context);
        this.context = context;
        this.tipsMsg = tips;
        this.titleMsg = title;
        this.isForced = isForced;
        this.updateUrl = url;
        updateGameHandler = new Handler();
    }

    public UpdateTipsDialog(Context context) {
        super(context);
        this.context = context;
        updateGameHandler = new Handler();
    }

    public void show(String title, String tips, boolean isForced, String url) {
        this.tipsMsg = tips;
        this.titleMsg = title;
        this.isForced = isForced;
        this.updateUrl = url;
        this.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = View.inflate(context, UpdateTools.getIdByName(context, "layout", "layout_update_dialog_u8"), null);
//        tvClose = (TextView) view.findViewById(UpdateTools.getIdByName(context, "id", "tv_dialog_close_yfjh"));
        btnUse = (Button) view.findViewById(UpdateTools.getIdByName(context, "id", "btn_use_u8"));
        btnUsCancel = (Button) view.findViewById(UpdateTools.getIdByName(context, "id", "btn_u8_cancel"));
        btnUsUpdate = (Button) view.findViewById(UpdateTools.getIdByName(context, "id", "btn_u8_update"));
        tvTips = (TextView) view.findViewById(UpdateTools.getIdByName(context, "id", "tv_dialog_tips_u8"));
        tvTitle = (TextView) view.findViewById(UpdateTools.getIdByName(context, "id", "tv_dialog_title_u8"));
        tvTitle.setText(titleMsg);
        tvTips.setText(tipsMsg);
        btnUse.setOnClickListener(this);
        btnUsCancel.setOnClickListener(this);
        btnUsUpdate.setOnClickListener(this);
//        tvClose.setOnClickListener(this);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        setContentView(view);
//        if (isCacheApkFileExists(context)) {
//            btnUsUpdate.setText("应用包已存在，点击安装");
//            android.widget.RelativeLayout.LayoutParams layoutParams = (android.widget.RelativeLayout.LayoutParams) this.btnUsCancel.getLayoutParams();
//            layoutParams.width = -1;
//            layoutParams.leftMargin = this.intMargin;
//            layoutParams.rightMargin = this.intMargin;
//            this.btnUsCancel.setLayoutParams(layoutParams);
//        }
    }


    @Override
    public void onClick(View v) {
        // Tag Here：后续可能做国际化
        String downloadnewv = context.getString(UpdateTools.getIdByName(context, "string", "u8_download_new"));
        String startdwonload = context.getString(UpdateTools.getIdByName(context, "string", "u8_start_download"));
//        String downloadnewv = "请下载更新版本！";
//        String startdwonload = "开始后台下载更新…";
        if (v.getId() == UpdateTools.getIdByName(context, "id", "btn_u8_cancel")) {
            Toast.makeText(context, downloadnewv, Toast.LENGTH_LONG).show();
            if (isForced) { //true 强更 false 不是强更
                //执行退出程序操作
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            Thread.sleep(2000);
                            dismiss();
                            System.exit(0);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            } else {
                dismiss();
            }
        } else if (v.getId() == UpdateTools.getIdByName(context, "id", "btn_use_u8")) {
            btnUse.setVisibility(View.GONE);
            btnUsCancel.setVisibility(View.GONE);
            btnUsUpdate.setVisibility(View.VISIBLE);

            //执行更新操作(后台下载)
            final UpdateNew_background_Version updateNew_background_Version = new UpdateNew_background_Version(context);
            String downloadFinish = context.getString(UpdateTools.getIdByName(context, "string", "u8_download_finish"));
            String downloadPercentage = context.getString(UpdateTools.getIdByName(context, "string", "u8_download_percentage"));
            updateNew_background_Version.setUpdateCallback(new UpdateCallBack() {
                @Override
                public void callback(boolean isDownLoaded) {
                    if (isDownLoaded) {
                        btnUsUpdate.setText(downloadFinish);
                        clickable = true;
                    }
                }
            });
            updateNew_background_Version.setProgressCallback(new ProgressCallBack() {
                @Override
                public void progCallback(int progress) {
                    btnUsUpdate.setText(downloadPercentage + progress + "%");
                }
            });
            if (!isCacheApkFileExists(context)) {
                Toast.makeText(context, startdwonload, Toast.LENGTH_SHORT).show();
                updateNew_background_Version.download(updateUrl);
            } else {
                clickable = true;
                btnUsUpdate.setText(downloadFinish);
            }
        } else if (v.getId() == UpdateTools.getIdByName(context, "id", "btn_u8_update")) {
            if (clickable) {
                UpdateNew_background_Version.openApkNew(context, getDownloadFileName(context));
            } else {
                Toast.makeText(context, "很快下载完了哦~", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
