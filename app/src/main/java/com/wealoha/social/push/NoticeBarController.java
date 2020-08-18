package com.wealoha.social.push;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.inject.Inject;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;

import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import com.wealoha.social.AppApplication;
import com.wealoha.social.ContextConfig;
import com.wealoha.social.R;
import com.wealoha.social.activity.MainAct;
import com.wealoha.social.commons.GlobalConstants;
import com.wealoha.social.inject.Injector;
import com.wealoha.social.push.model.PostCommentPush;
import com.wealoha.social.push.notification.AlohaPushNotification;
import com.wealoha.social.push.notification.InboxMessageNewNotification;
import com.wealoha.social.push.notification.NewMatchNotification;
import com.wealoha.social.push.notification.PostCommentNotification;
import com.wealoha.social.push.notification.PostCommentReplyOnMyPostNotification;
import com.wealoha.social.push.notification.PostCommentReplyOnOthersPostNotification;
import com.wealoha.social.push.notification.PostLikeNotification;
import com.wealoha.social.receive.AlarmReceiver;
import com.wealoha.social.receive.MonitorNoticeColumnClearBroadcast;
import com.wealoha.social.ui.dialogue.DialogueActivity;
import com.wealoha.social.utils.ContextUtil;
import com.wealoha.social.utils.XL;

/**
 * @author:sunkist
 * @see:
 * @since:
 * @description 通知栏管理者
 * @copyright wealoha.com
 * @Date:2014-11-13
 */
public class NoticeBarController {

    @Inject
    ContextUtil contextUtil;
    // public static final PushSettingResult mPushSettingResult = new
    // PushSettingResult();
    private static NoticeBarController mNoticeBarController = new NoticeBarController();
    public static final String TAG = NoticeBarController.class.getSimpleName();
    private static Context mContext;
    /**
     * 相同用户发送过来的信息数量
     */
    public Map<String, Integer> mSessionMap = new HashMap<String, Integer>();
    /**
     * 维护相同session的通知Id
     */
    public Map<String, Integer> mNoticeMap = new HashMap<String, Integer>();
    private NotificationManager mNotificationManager = (NotificationManager) AppApplication.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
    private AlarmManager alarmMgr;

    /**
     * 不显示详情用的
     */
    public static final String FAKE_NODETAIL_SESSION_ID = "union-session";
    private int noDetailMessageCount;
    private Set<String> noDetailMessageSessions = new HashSet<String>();

    private PendingIntent pendIntent;
    private NotificationCompat.Builder mNotifyBuilder;

    // 声音控制
    private final static String SOUND = "complete.m4r";// 声音
    private final static String BLANK = "blank.wav";// 震动

    private NoticeBarController() {
        Injector.inject(this);
    }

    public synchronized static NoticeBarController getInstance(Context context) {
        mContext = context;
        return mNoticeBarController;
    }

    /**
     * @param context
     * @param msg
     * @param username
     * @Description: 添加一个新的会话通知
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2014-11-13
     */
    public void showSessionNotice(Context context, InboxMessageNewNotification notification, String msg, String username) {
        String sessionId = notification.sessionId;
        boolean toSessionList = false;
        if ("ALOHA_PUSH_Notification_New_Message_NoDetail".equals(notification.aps.alert.locKey)) {
            // 如果不显示详情，合并下
            noDetailMessageCount++;
            noDetailMessageSessions.add(notification.sessionId);
            sessionId = FAKE_NODETAIL_SESSION_ID;
            msg = context.getString(R.string.push_no_detail_summary, noDetailMessageSessions.size(), noDetailMessageCount);
            toSessionList = true;
        }
        // 拼通知
        designNotice(notification, msg, username, toSessionList);
        Notification noti = mNotifyBuilder.build();
        noti.flags = Notification.FLAG_AUTO_CANCEL;
        if (!mSessionMap.containsKey(sessionId) && mSessionMap.size() <= 0) {
            mSessionMap.put(sessionId, 1);
            mNoticeMap.put(sessionId, 1);
            mNotificationManager.notify(mNoticeMap.get(sessionId), noti);
            return;
        }
        Integer noticeCurrentId = mNoticeMap.get(sessionId);
        /* 当前会话是新建的session,但是通知栏中已经存在通知了,不进行任何删除通知的操作 */
        if (noticeCurrentId == null || noticeCurrentId == 0) {
            /* 第二次获取的sessionId就从100开始加 */
            String lastSessionKey = getLastSessionKey(mSessionMap);
            noticeCurrentId = mSessionMap.get(lastSessionKey) + 100;
            mSessionMap.put(sessionId, 1);
            mNoticeMap.put(sessionId, noticeCurrentId);
            mNotificationManager.notify(mNoticeMap.get(sessionId), noti);
            return;
        } else {
            // 取消该sessionId的notice
            mNotificationManager.cancel(mNoticeMap.get(sessionId));
            // 从集合中获取被取消的sessionId的noticeId
            mSessionMap.put(sessionId, mSessionMap.get(sessionId) + 1);
            mNoticeMap.put(sessionId, mNoticeMap.get(sessionId) + 1);
            mNotificationManager.notify(mNoticeMap.get(sessionId), noti);
            return;
        }
    }

    static int i = 0;

    private void designNotice(com.wealoha.social.push.notification.Notification notification, String msg, String username, boolean toSessionList) {
        mNotifyBuilder = new NotificationCompat.Builder(mContext);
        Class actClass = DialogueActivity.class;
        Bundle bundle = new Bundle();
        if (toSessionList) {
            // 去聊天列表界面
            bundle.putString("openTab", "chat");
            actClass = MainAct.class;
        }
        if (notification instanceof InboxMessageNewNotification) {
            bundle.putString("sessionId", ((InboxMessageNewNotification) notification).sessionId);
            bundle.putBoolean("forceBackToSessionList", true); // 强制回Session
        } else if (notification instanceof NewMatchNotification) {
            notification = (NewMatchNotification) notification;
            bundle.putBoolean("forceBackToSessionList", true); // 强制回Session
            bundle.putString("sessionId", ((NewMatchNotification) notification).userId);
        }
        mNotifyBuilder.setSmallIcon(R.drawable.title_message);
        Intent deleteIntent = new Intent(mContext, MonitorNoticeColumnClearBroadcast.class);
        deleteIntent.setAction(GlobalConstants.AppConstact.MONITOR_NOTICE_COLUMN_CLEAR_BROADCAST);
        if (username != null) {
            mNotifyBuilder.setContentTitle(username);
            if (mSessionMap.containsKey(((InboxMessageNewNotification) notification).sessionId) && mSessionMap.size() > 0) {
                mNotifyBuilder.setTicker("[" + (mSessionMap.get(((InboxMessageNewNotification) notification).sessionId) + 1) + "条] " + username + " : " + msg);
                mNotifyBuilder.setContentText(/* username + " : " + */"[" + (mSessionMap.get(((InboxMessageNewNotification) notification).sessionId) + 1) + "条] " + msg);
            } else {
                mNotifyBuilder.setTicker("" + username + " : " + msg);
                mNotifyBuilder.setContentText(msg);
            }
        } else {
            mNotifyBuilder.setTicker(msg == null ? "" : msg);
            mNotifyBuilder.setContentTitle("Aloha");
            mNotifyBuilder.setContentText(msg == null ? "" : msg);
        }

        Intent resultIntent = new Intent(mContext, actClass);
        resultIntent.putExtras(bundle);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(mContext, i++, resultIntent,//
                Intent.FILL_IN_ACTION);
        mNotifyBuilder.setContentIntent(resultPendingIntent);
        mNotifyBuilder.setDeleteIntent(PendingIntent.getBroadcast(mContext, i++, deleteIntent, //
                PendingIntent.FLAG_CANCEL_CURRENT));

        if (ContextConfig.getInstance().getBooleanWithDefValue(GlobalConstants.AppConstact.PUSH_SOUND, true)) {
            if ("ALOHA_PUSH_Notification_New_Match".equals(notification.getAlertLocKey())) {
                mNotifyBuilder.setSound(Uri.parse("android.resource://com.wealoha.social/raw/popcorn"));
            } else {
                mNotifyBuilder.setSound(Uri.parse("android.resource://com.wealoha.social/raw/complete"));
            }
        }
        if (ContextConfig.getInstance().getBooleanWithDefValue(GlobalConstants.AppConstact.PUSH_VIBRATION, true)) {
            mNotifyBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
        }
    }

    /**
     * @return
     * @Description:
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2014-11-14
     */
    private String getLastSessionKey(Map<String, Integer> sessionMap) {
        String sessionKey = null;
        Set<Entry<String, Integer>> entries = sessionMap.entrySet();
        for (Entry<String, Integer> entry : entries) {
            sessionKey = entry.getKey();
        }
        return sessionKey;
    }

    public void cleanAllSession() {
        if (mSessionMap != null && mSessionMap.size() > 0 && mNoticeMap != null && mNoticeMap.size() > 0) {
            mSessionMap.clear();
            mNoticeMap.clear();
            noDetailMessageCount = 0;
            if (noDetailMessageSessions != null) {
                noDetailMessageSessions.clear();
            }
            if (mNotificationManager != null) {
                mNotificationManager.cancelAll();
            }
        }

        if (commentMap != null) {
            commentMap.clear();
        }
        if (notifyMap != null) {
            notifyMap.clear();
        }
        notifyCount = 0;

        if (mNotificationManager != null) {
            mNotificationManager.cancelAll();
        }
    }

    /**
     * @return void 返回类型
     * @throws
     * @Title: removeSession
     * @Description: 清理已经读过的消息
     */
    public void removeSession(String sessionId) {
        // XL.d(TAG, "清理Session记数: " + sessionId);
        mSessionMap.remove(sessionId);
        mNoticeMap.remove(sessionId);
        if (FAKE_NODETAIL_SESSION_ID.equals(sessionId)) {
            // 合并的消息，清空
            noDetailMessageSessions.clear();
            noDetailMessageCount = 0;
        }
        // 更新角标
        // setChatSub();
    }

    // /**
    // * @Title: setChatSub
    // * @Description: mainact导航栏角标，chatfragment头像上的角标，feed提醒和角标还没有做
    // * @param 设定文件
    // * @return void 返回类型
    // * @throws
    // */
    // private void setChatSub() {
    // if (contextUtil.getForegroundAct() instanceof MainAct) {
    // MainAct a = (MainAct) contextUtil.getForegroundAct();
    // a.setChatSub();
    // }
    // if (contextUtil.getChatFragment() instanceof ChatFragment) {
    // ChatFragment cf = contextUtil.getChatFragment();
    // cf.setChatSub();
    // }
    // }

    public Map<String, Integer> getmSessionMap() {
        return mSessionMap;
    }

    /**
     * @Description: 定时发送广播
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2014-11-25
     */
    public void timmingSendNotice(int sec, Bundle bundle) {
        alarmMgr = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Intent it = new Intent(mContext, AlarmReceiver.class);
        it.putExtras(bundle);
        pendIntent = PendingIntent.getBroadcast(mContext, 1, it, PendingIntent.FLAG_UPDATE_CURRENT);
        // 时间到呼起设备
        long triggerAtTime = SystemClock.elapsedRealtime() + sec * 1000;
        alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pendIntent);
    }

    public void stopCancenTiming() {
    }

    // /**
    // * @Description: 获得未读消息的数量
    // * @see:
    // * @since:
    // * @description
    // * @author: sunkist
    // * @date:2014-12-9
    // */
    // public int getUnreadSmsCount() {
    // int smsCount = 0;
    // if (mSessionMap.size() > 0) {
    // Set<Entry<String, Integer>> entries = mSessionMap.entrySet();
    //
    // for (Entry<String, Integer> entry : entries) {
    // System.out.println(entry.getKey() + ":" + entry.getValue());
    // smsCount += mSessionMap.get(entry.getKey());
    //
    // }
    // }
    // XL.d("smsCount", "" + smsCount);
    // return smsCount;
    // }

    /**
     * @Description: 如果在应用程序内，不需要精心通知栏的通知，直接显示在底部角标，并且播放声音
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2014-12-11
     */
    public void sendChatSubBroad() {

    }

    public void byMatchToDialogue(NewMatchNotification notification, String msg) {
        designNotice(notification, msg, null, false);
        mNotifyBuilder.setContentText(msg);
        mNotifyBuilder.setSmallIcon(R.drawable.title_message);
        Notification noti = mNotifyBuilder.build();
        noti.flags = Notification.FLAG_AUTO_CANCEL;
        mNotificationManager.notify(0, noti);
    }

    public void instagramHaveFinished() {
        mNotifyBuilder = new NotificationCompat.Builder(mContext);
        mNotifyBuilder.setSmallIcon(R.drawable.title_message);
        Intent deleteIntent = new Intent(mContext, MonitorNoticeColumnClearBroadcast.class);
        deleteIntent.setAction(GlobalConstants.AppConstact.MONITOR_NOTICE_COLUMN_CLEAR_BROADCAST);
        mNotifyBuilder.setContentTitle("Aloha");
        mNotifyBuilder.setContentText(mContext.getString(R.string.aloha_push_notification_instagramsyncdone));
        Class<MainAct> actClass = MainAct.class;
        Bundle bundle = new Bundle();
        bundle.putString("openTab", "profile");
        Intent resultIntent = new Intent(mContext, actClass);
        // speedNotification(mNotifyBuilder);
        resultIntent.putExtras(bundle);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
        stackBuilder.addNextIntent(resultIntent);
        // 这里注意
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(i++, PendingIntent.FLAG_CANCEL_CURRENT);
        mNotifyBuilder.setContentIntent(resultPendingIntent);
        mNotifyBuilder.setDeleteIntent(PendingIntent.getBroadcast(mContext, 0, deleteIntent, 0));

        if (ContextConfig.getInstance().getBooleanWithDefValue(GlobalConstants.AppConstact.PUSH_SOUND, true)) {
            mNotifyBuilder.setSound(Uri.parse("android.resource://com.wealoha.social/raw/complete"));
        }

        if (ContextConfig.getInstance().getBooleanWithDefValue(GlobalConstants.AppConstact.PUSH_VIBRATION, true)) {
            mNotifyBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
        }
        Notification noti = mNotifyBuilder.build();
        noti.flags = Notification.FLAG_AUTO_CANCEL;
        mNotificationManager.notify(1, noti);

    }

    private Map<String, Integer> notifyMap = new HashMap<String, Integer>();
    private Map<String, Integer> commentMap = new HashMap<String, Integer>();
    private int commentId = 0;
    private int notifyCount;
    private int postCommentReplyOnMyPostNotifyCount;
    private final static int NO_DETAIL_NOTIFY_ID = 10000;
    private final static int NO_DETAIL_ALOHA_ID = 10001;

    public void showNewCommentNotice(Context context, PostCommentNotification notification, String msg) {
        notifyCount++;
        mNotifyBuilder = new NotificationCompat.Builder(mContext);
        mNotifyBuilder.setSmallIcon(R.drawable.title_message);
        // 如果不显示详情，合并下
        String message = context.getResources().getString(R.string.aloha_push_notification_post_nodetail, notifyCount);
        // 判断是否显示详情
        boolean nodetail = showNoDetail(context, notification, message, "feednotify");
        if (nodetail) {
            return;
        }

        String userName = notification.aps.alert.locArgs.get(0);
        String content = notification.aps.alert.locArgs.get(1);

        int countTemp = commentMap.get(userName) == null ? 0 : commentMap.get(userName);
        String count = (countTemp == 0 ? "" : countTemp + "");

        // 通知栏："\[x条]\aaa 给你的照片留言了：xxxxxxx" ------ \ \内为可选内容
        // String notifyContent = count + userName + " " +
        // context.getResources().getString(R.string.aloha_push_notification_post_comment)
        // + "：" + content;
        String notifyContent = "";
        if (notification.reply) {
            notifyContent = userName + " " + mContext.getString(R.string.aloha_push_notification_post_comment_reply) + content;
        } else {
            notifyContent = String.format(mContext.getString(R.string.leaven_push_notice_str), countTemp == 0 ? "1" : String.valueOf(countTemp), userName, content);
        }
        mNotifyBuilder.setTicker(notifyContent);

        // 通知栏（下拉后）：
        // "aaa 给你的照片留言了"
        // "\[x条]\ xxxxxxx"
        String notifyMenuTitle = "";

        if (notification.reply) {
            notifyMenuTitle = userName + " " + mContext.getString(R.string.aloha_push_notification_post_comment_reply);
        } else {
            notifyMenuTitle = userName + " " + mContext.getResources().getString(R.string.aloha_push_notification_post_comment);
        }
        String notifyMenuContent = (TextUtils.isEmpty(count) ? "" : "[ " + count + "条 ]") + content;
        mNotifyBuilder.setContentTitle(notifyMenuTitle);
        mNotifyBuilder.setContentText(notifyMenuContent);
        XL.i(TAG, "content:" + notifyMenuContent);
        // 设置跳转
        openNotifyAct("feednotify");
        // 设置铃声
        setNotifySound(notification.getSound());
        putMap(userName);
        if (countTemp != 0) {
            mNotificationManager.cancel(notifyMap.get(userName));
        }

        mNotifyBuilder.setAutoCancel(true);
        Notification noti = mNotifyBuilder.build();
        // noti.flags = Notification.FLAG_AUTO_CANCEL;
        mNotificationManager.notify((Integer) notifyMap.get(userName), noti);

    }

    public void showNewCommentNotice(Context context, PostCommentReplyOnMyPostNotification notification, String msg) {
        postCommentReplyOnMyPostNotifyCount++;
        mNotifyBuilder = new NotificationCompat.Builder(mContext);
        mNotifyBuilder.setSmallIcon(R.drawable.title_message);
        // 如果不显示详情，合并下
        String message = context.getResources().getString(R.string.aloha_push_notification_post_nodetail, postCommentReplyOnMyPostNotifyCount);
        // 判断是否显示详情
        boolean nodetail = showNoDetail(context, notification, message, "feednotify");
        if (nodetail) {
            return;
        }

        String userName = notification.aps.alert.locArgs.get(0);
        String replyName = notification.aps.alert.locArgs.get(1);
        String content = notification.aps.alert.locArgs.get(2);

        int countTemp = commentMap.get(userName) == null ? 0 : commentMap.get(userName);
        String count = countTemp == 0 ? "" : String.valueOf(countTemp);

        // 通知栏："\[x条]\aaa 在你的照片回复了留言：xxxxxxx" ------ \ \内为可选内容
        // String notifyContent = count + userName + " " +
        // context.getResources().getString(R.string.aloha_push_notification_post_comment)
        // + "：" + content;
        String notifyContent = String.format(mContext.getString(R.string.aloha_push_post_comment_reply_on_my_post), countTemp == 0 ? "1" : String.valueOf(countTemp), userName, replyName, content);
        mNotifyBuilder.setTicker(notifyContent);

        // 通知栏（下拉后）：
        // "aaa 在你的照片回复了留言"
        // "\[x条]\ xxxxxxx"
        String notifyMenuTitle = context.getResources().getString(R.string.aloha_push_post_comment_reply_on_my_post_02, userName, replyName);
        String notifyMenuContent = "";
        if (!TextUtils.isEmpty(count)) {
            notifyMenuContent = context.getResources().getString(R.string.aloha_push_post_comment_reply_on_my_post_03, count, content);
        } else {
            notifyMenuContent = content;
        }
        mNotifyBuilder.setContentTitle(notifyMenuTitle);
        mNotifyBuilder.setContentText(notifyMenuContent);
        XL.i(TAG, "content:" + notifyMenuContent);
        // 设置跳转
        openNotifyAct("feednotify");
        // 设置铃声
        setNotifySound(notification.getSound());
        putMap(userName);
        if (countTemp != 0) {
            mNotificationManager.cancel(notifyMap.get(userName));
        }

        mNotifyBuilder.setAutoCancel(true);
        Notification noti = mNotifyBuilder.build();
        // noti.flags = Notification.FLAG_AUTO_CANCEL;
        mNotificationManager.notify((Integer) notifyMap.get(userName), noti);

    }

    public void showNewCommentNotice(Context context, PostCommentReplyOnOthersPostNotification notification, String msg) {
        notifyCount++;
        mNotifyBuilder = new NotificationCompat.Builder(mContext);
        mNotifyBuilder.setSmallIcon(R.drawable.title_message);
        // 如果不显示详情，合并下
        String message = context.getResources().getString(R.string.aloha_push_notification_post_nodetail, notifyCount);
        // 判断是否显示详情
        boolean nodetail = showNoDetail(context, notification, message, "feednotify");
        if (nodetail) {
            return;
        }

        String userName = "";
        String content = "";

        userName = notification.aps.alert.locArgs.get(0);
        content = notification.aps.alert.locArgs.get(2);

        int countTemp = commentMap.get(userName) == null ? 0 : commentMap.get(userName);
        String count = countTemp == 0 ? "" : String.valueOf(countTemp);

        // 通知栏："\[x条]\aaa 给你的照片留言了：xxxxxxx" ------ \ \内为可选内容
        // String notifyContent = count + userName + " " +
        // context.getResources().getString(R.string.aloha_push_notification_post_comment)
        // + "：" + content;
        String notifyContent = String.format(mContext.getString(R.string.leaven_push_notice_str), countTemp == 0 ? "1" : String.valueOf(countTemp), userName, content);
        mNotifyBuilder.setTicker(notifyContent);

        // 通知栏（下拉后）：
        // "aaa 给你的照片留言了"
        // "\[x条]\ xxxxxxx"
        String notifyMenuTitle = userName + " " + context.getResources().getString(R.string.aloha_push_notification_post_comment);
        String notifyMenuContent = count + content;
        mNotifyBuilder.setContentTitle(notifyMenuTitle);
        mNotifyBuilder.setContentText(notifyMenuContent);
        XL.i(TAG, "content:" + notifyMenuContent);
        // 设置跳转
        openNotifyAct("feednotify");
        // 设置铃声
        setNotifySound(notification.getSound());
        putMap(userName);
        if (countTemp != 0) {
            mNotificationManager.cancel(notifyMap.get(userName));
        }

        mNotifyBuilder.setAutoCancel(true);
        Notification noti = mNotifyBuilder.build();
        // noti.flags = Notification.FLAG_AUTO_CANCEL;
        mNotificationManager.notify((Integer) notifyMap.get(userName), noti);

    }

    private void openNotifyAct(String whereAreYouGo) {
        Intent deleteIntent = new Intent(mContext, MonitorNoticeColumnClearBroadcast.class);
        deleteIntent.setAction(GlobalConstants.AppConstact.MONITOR_NOTICE_COLUMN_CLEAR_BROADCAST);
        Intent resultIntent = new Intent(mContext, MainAct.class);
        Bundle bundle = new Bundle();
        bundle.putString("openTab", whereAreYouGo);
        resultIntent.putExtras(bundle);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(i++, PendingIntent.FLAG_CANCEL_CURRENT);
        mNotifyBuilder.setContentIntent(resultPendingIntent);
        mNotifyBuilder.setDeleteIntent(PendingIntent.getBroadcast(mContext, 0, deleteIntent, 0));
    }

    private void putMap(String key) {
        Integer count = (Integer) commentMap.get(key);
        if (count == null) {
            // 为什么要+2呢，因为这个数字是为了给下一条消息用的，消息为1的时候 1不显示
            commentMap.put(key, 2);
        } else {
            commentMap.put(key, count + 1);
        }
        Integer notifyId = notifyMap.get(key);
        if (notifyId == null) {
            commentId++;
            notifyMap.put(key, commentId);
        }
    }

    public void showNewLikeNotice(Context context, PostLikeNotification notification, String msg) {
        notifyCount++;
        mNotifyBuilder = new NotificationCompat.Builder(mContext);
        mNotifyBuilder.setSmallIcon(R.drawable.title_message);

        setNotifySound(notification.getSound());
        // 判断是否显示详情
        String noDetail = context.getResources().getString(R.string.aloha_push_notification_post_nodetail, notifyCount);
        boolean nodetail = showNoDetail(context, notification, noDetail, "feednotify");
        if (nodetail) {
            return;
        }
        XL.i("XM_PUSH_SOUND", "notify:" + notification.getSound());
        // 设置铃声
        String userName = notification.aps.alert.locArgs.get(0);
        mNotifyBuilder.setTicker(userName + " " + msg);
        mNotifyBuilder.setContentTitle(userName);
        mNotifyBuilder.setContentText(msg);
        mNotifyBuilder.setAutoCancel(true);

        openNotifyAct("feednotify");
        Notification noti = mNotifyBuilder.build();
        commentId++;
        mNotificationManager.notify(commentId, noti);
    }

    private int alohaPushCount;

    public void showAlohaPushNotice(Context context, AlohaPushNotification notification, String msg) {
        alohaPushCount++;
        mNotifyBuilder = new NotificationCompat.Builder(mContext);
        mNotifyBuilder.setSmallIcon(R.drawable.title_message);

        // 判断是否显示详情
        String message = context.getResources().getString(R.string.aloha_push_notification_post_aloha_nodetail, alohaPushCount);
        boolean nodetail = showNoDetail(context, notification, message, "popularity");
        if (nodetail) {
            return;
        }

        // 设置铃声
        setNotifySound(notification.getSound());
        String userName = notification.aps.alert.locArgs.get(0);
        mNotifyBuilder.setTicker(userName + " " + msg);
        mNotifyBuilder.setContentTitle(userName);
        mNotifyBuilder.setContentText(msg);
        mNotifyBuilder.setAutoCancel(true);

        openNotifyAct("popularity");
        Notification noti = mNotifyBuilder.build();
        mNotificationManager.notify(commentId, noti);
    }

    private boolean showNoDetail(Context context, com.wealoha.social.push.notification.Notification notification,//
                                 String msg, String whereAreYouGo) {

        mNotifyBuilder.setTicker(msg);
        mNotifyBuilder.setContentTitle("Aloha");
        mNotifyBuilder.setContentText(msg);
        openNotifyAct(whereAreYouGo);
        Notification noti = mNotifyBuilder.build();
        noti.flags = Notification.FLAG_AUTO_CANCEL;
        XL.d("XM_PUSH_BROADCAST", "lockey: " + notification.aps.alert.locKey);
        if ("ALOHA_PUSH_Notification_Post_Like_NoDetail".equals(notification.aps.alert.locKey)//
                || "ALOHA_PUSH_Notification_Post_Comment_NoDetail".equals(notification.aps.alert.locKey)) {
            // 如果不显示详情，合并下
            mNotificationManager.cancel(NO_DETAIL_NOTIFY_ID);
            mNotificationManager.notify(NO_DETAIL_NOTIFY_ID, noti);
            return true;
        } else if ("ALOHA_PUSH_Notification_Aloha_NoDetail".equals(notification.aps.alert.locKey)) {
            mNotificationManager.cancel(NO_DETAIL_ALOHA_ID);
            mNotificationManager.notify(NO_DETAIL_ALOHA_ID, noti);
            return true;
        } else if ("ALOHA_PUSH_Notification_Post_Comment_Reply_On_My_Post_NoDetail".equals(notification.aps.alert.locKey)) {
            mNotificationManager.cancel(NO_DETAIL_ALOHA_ID);
            mNotificationManager.notify(NO_DETAIL_ALOHA_ID, noti);
            return true;
        } else if ("ALOHA_PUSH_Notification_Post_Comment_Reply_On_Others_Post_NoDetail".equals(notification.aps.alert.locKey)) {
            mNotificationManager.cancel(NO_DETAIL_ALOHA_ID);
            mNotificationManager.notify(NO_DETAIL_ALOHA_ID, noti);
            return true;
        }
        return false;
    }

    // private void setNotifySound() {
    // if (mPushSettingResult.pushSound) {
    // mNotifyBuilder.setSound(Uri.parse("android.resource://com.wealoha.social/raw/complete"));
    // }
    //
    // if (mPushSettingResult.pushVibration) {
    // mNotifyBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
    // }
    // }

    private void setNotifySound(String sound) {
        if (!TextUtils.isEmpty(sound)) {
            if (SOUND.equals(sound)) {
                mNotifyBuilder.setSound(Uri.parse("android.resource://com.wealoha.social/raw/complete"));
            } else if (BLANK.equals(sound)) {
                mNotifyBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
            }
        }

    }
}
