package com.wealoha.social.services;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import com.wealoha.social.R;
import com.wealoha.social.activity.WelcomeAct;
import com.wealoha.social.commons.GlobalConstants;

public class DownloadService extends Service {

    private static NotificationManager nm;
    private static Notification notification;
    private static boolean cancelUpdate = false;
    private static MyHandler myHandler;
    private static ExecutorService executorService = Executors.newFixedThreadPool(5); // 固定五个线程来执行任务
    public static Map<Integer, Integer> download = new HashMap<Integer, Integer>();
    public static Context context;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        myHandler = new MyHandler(Looper.myLooper(), DownloadService.this);
        context = this;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public static void downNewFile(Context context, final String url, final int notificationId, final String name) {
        nm = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        if (download.containsKey(notificationId))
            return;
        notification = new Notification();
        notification.icon = android.R.drawable.stat_sys_download;
        // notification.icon=android.R.drawable.stat_sys_download_done;
        notification.tickerText = name + context.getString(R.string.start_download_str);
        notification.when = System.currentTimeMillis();
        notification.defaults = Notification.DEFAULT_LIGHTS;
        // 显示在“正在进行中”
        notification.flags = Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
        PendingIntent contentIntent = PendingIntent.getActivity(context, notificationId, new Intent(context, WelcomeAct.class), 0);
        download.put(notificationId, 0);
        // 将下载任务添加到任务栏中
        nm.notify(notificationId, notification);
        // 启动线程开始执行下载任务
        downFile(url, notificationId, name);
    }

    /**
     * @param url
     * @param notificationId
     * @param name
     * @Description:下载更新文件
     * @see:App的名字是截取到URL后面的名字
     * @since:
     * @description
     * @author: sunkist
     * @date:2014-12-13
     */
    private static void downFile(final String url, final int notificationId, final String name) {
        executorService.execute(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                File tempFile = null;

            }
        });
    }

    // 安装下载后的apk文件
    private void Instanll(File file, Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /* 事件处理类 */
    class MyHandler extends Handler {

        private Context context;

        public MyHandler(Looper looper, Context c) {
            super(looper);
            this.context = c;
        }

        @Override
        public void handleMessage(Message msg) {
            PendingIntent contentIntent = null;
            super.handleMessage(msg);
            if (msg != null) {
                switch (msg.what) {
                    case 0:
                        Toast.makeText(context, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                        download.remove(msg.arg1);
                        break;
                    case 1:
                        break;
                    case 2:
                        contentIntent = PendingIntent.getActivity(DownloadService.this, msg.arg1, new Intent(DownloadService.this, WelcomeAct.class), 0);
//                        notification.setLatestEventInfo(DownloadService.this, msg.getData().getString("name") + context.getString(R.string.download_complete_str), "100%", contentIntent);
                        nm.notify(msg.arg1, notification);
                        // 下载完成后清除所有下载信息，执行安装提示
                        download.remove(msg.arg1);
                        nm.cancel(msg.arg1);
                        Instanll((File) msg.obj, context);
                        break;
                    case 3:
                        contentIntent = PendingIntent.getActivity(DownloadService.this, msg.arg1, new Intent(DownloadService.this, WelcomeAct.class), 0);
//                        notification.setLatestEventInfo(DownloadService.this, msg.getData().getString("name") + context.getString(R.string.downloading_str), download.get(msg.arg1) + "%", contentIntent);
                        nm.notify(msg.arg1, notification);
                        break;
                    case 4:
                        Toast.makeText(context, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                        download.remove(msg.arg1);
                        nm.cancel(msg.arg1);
                        break;
                }
            }
        }
    }

}
