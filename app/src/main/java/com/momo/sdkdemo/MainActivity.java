package com.momo.sdkdemo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.momo.sdk.update.custom.UpdateTipsDialog;
import com.momo.sdk.update.sysnative.UpdateUtil;
import com.u8.sdkdemo.R;

public class MainActivity extends Activity {

    private UpdateUtil updateUtil;
    private EditText editDownloadUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn_update = findViewById(R.id.btn_update);
        Button btn_update2 = findViewById(R.id.btn_update2);
        EditText editDownloadUrl = findViewById(R.id.et_downloadUrl);
        updateUtil = new UpdateUtil(this);

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(MainActivity.this, "ayo", Toast.LENGTH_SHORT).show();
                UpdateTipsDialog dialog = new UpdateTipsDialog(MainActivity.this);
                dialog.show("发现更新", "全新3.8版本 「清夏！乐园？大秘境！」现已推出！\n"
                        + "\n" + "【限时区域】琉形蜃境\n"
                        + "一切都是在小小的琉璃瓶子中发生的故事。传言在沙漠的中心有一处神奇的秘境，进入秘境的人就能实现他们的愿望。\n"
                        + "而事实真相又是如何呢，无人知悉，在那小小的瓶中世界之中，又装着多少旅人犹如蜃影般的热望？\n"
                        + "\n" + "【新活动】版本主题活动 「清夏！乐园？大秘境！」、阶段性活动「传心同视」、「险途勘探」、「冒险家试炼·进阶篇」\n"
                        + "炽烈的夏日阳光渐渐笼罩大地，在派蒙的建议下，你们回到了西风骑士团的总部，意外得知了某件奇妙之事，一场特别的冒险即将拉开帷幕…\n"
                        + "完成主题活动任务，可邀请四星角色「绮思晚星 · 莱依拉（冰）」。\n" + "\n"
                        + "【新剧情】邀约任务更新\n" + "邀约事件 · 凯亚 第一幕 「鬼话与甜酒」\n" + "\n"
                        + "【新衣装】可莉衣装「琪花星烛」、凯亚衣装「帆影游风」\n"
                        + "全新角色衣装「琪花星烛」是可莉出演关键剧目时所穿的精致服装，色彩明丽，如同点缀着奶油花朵和明红蜡烛的蛋糕，能将快乐与笑容带往冒险所至的每个角落。\n"
                        + "「帆影游风」是凯亚在舞台上饰演「短刀大盗」时的装扮，以「吸睛」为设计原则的华丽服装。不过衣服穿起来并不麻烦，最花时间打理的是饰品与短刀。\n"
                        + "\n" + "【七圣召唤更新】全新角色牌、全新行动牌\n" + "全新角色牌、行动牌开放获取。", false, editDownloadUrl.getText().toString());

                WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                DisplayMetrics displayMetrics = new DisplayMetrics();
                windowManager.getDefaultDisplay().getMetrics(displayMetrics);
                int screenHeight = displayMetrics.heightPixels;
                int maxHeight = (int) (screenHeight * 0.8); // 设置为屏幕高度的80%
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, maxHeight);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialog.getWindow().setLayout(layoutParams.WRAP_CONTENT, maxHeight);

            }
        });

        btn_update2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("hzmd momo u8sdk", editDownloadUrl.getText().toString());
//                updateUtil.DEBUGMODE = true;
                updateUtil.setOnUpdateStatusChangeListener(null)
                        .showDownloadNotification("正在更新", "正在下载更新包...")
                        .showDownloadProgressDialog("正在下载", "后台下载", "取消")
                        .start(editDownloadUrl.getText().toString(), "发现更新",
                                "全新3.8版本 「清夏！乐园？大秘境！」现已推出！\n" + "\n"
                                        + "【限时区域】琉形蜃境\n" + "一切都是在小小的琉璃瓶子中发生的故事。传言在沙漠的中心有一处神奇的秘境，进入秘境的人就能实现他们的愿望。\n"
                                        + "而事实真相又是如何呢，无人知悉，在那小小的瓶中世界之中，又装着多少旅人犹如蜃影般的热望？\n"
                                        + "\n" + "【新活动】版本主题活动 「清夏！乐园？大秘境！」、阶段性活动「传心同视」、「险途勘探」、「冒险家试炼·进阶篇」\n"
                                        + "炽烈的夏日阳光渐渐笼罩大地，在派蒙的建议下，你们回到了西风骑士团的总部，意外得知了某件奇妙之事，一场特别的冒险即将拉开帷幕…\n"
                                        + "完成主题活动任务，可邀请四星角色「绮思晚星 · 莱依拉（冰）」。\n" + "\n" + "【新剧情】邀约任务更新\n"
                                        + "邀约事件 · 凯亚 第一幕 「鬼话与甜酒」\n" + "\n" + "【新衣装】可莉衣装「琪花星烛」、凯亚衣装「帆影游风」\n"
                                        + "全新角色衣装「琪花星烛」是可莉出演关键剧目时所穿的精致服装，色彩明丽，如同点缀着奶油花朵和明红蜡烛的蛋糕，能将快乐与笑容带往冒险所至的每个角落。\n"
                                        + "「帆影游风」是凯亚在舞台上饰演「短刀大盗」时的装扮，以「吸睛」为设计原则的华丽服装。不过衣服穿起来并不麻烦，最花时间打理的是饰品与短刀。\n"
                                        + "\n" + "【七圣召唤更新】全新角色牌、全新行动牌\n" + "全新角色牌、行动牌开放获取。",
                                "开始", "取消", "", 2, "com.xiaomi.market", "点击安装", false);
            }
        });
    }
}
