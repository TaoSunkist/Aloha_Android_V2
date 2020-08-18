package com.wealoha.social.push.model;

import java.util.HashMap;
import java.util.Map;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;

import com.wealoha.social.AppApplication;
import com.wealoha.social.ContextConfig;
import com.wealoha.social.R;
import com.wealoha.social.activity.MainAct;
import com.wealoha.social.commons.GlobalConstants;
import com.wealoha.social.interfaces.NotifyPush;
import com.wealoha.social.push.NoticeBarController;
import com.wealoha.social.push.Push2Type;
import com.wealoha.social.push.notification.InboxMessageNewNotification;
import com.wealoha.social.receive.MonitorNoticeColumnClearBroadcast;
import com.wealoha.social.ui.dialogue.DialogueActivity;
import com.wealoha.social.utils.Utils;

public class InboxMessageNewPush extends BasePush implements NotifyPush<InboxMessageNewNotification> {
    /**
     * 消息合并開啓
     */
    public static final String ALOHA_PUSH_Notification_New_Message_NoDetail = "ALOHA_PUSH_Notification_New_Message_NoDetail";
    /**
     * 图片消息
     */
    public static final String ALOHA_PUSH_Notification_New_Message_Photo = "ALOHA_PUSH_Notification_New_Message_Photo";
    /**
     * 正常消息
     */
    public static final String ALOHA_PUSH_Notification_New_Message_Text = "ALOHA_PUSH_Notification_New_Message_Text";
    public static final String INBOX_MSG_SESSION_ID = "notification.sessionId";
    public static final String INBOX_MSG_MESSAGE_ID = "notification.messageId";

    public InboxMessageNewPush(Context ctx) {
        noticeId = R.string.chat;
        mCtx = ctx;
        mNotificationManager = (NotificationManager) mCtx.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    /**
     * 相同用户发送过来的信息数量
     */
    protected static Map<String, Integer> mSessionMap = new HashMap<String, Integer>();
    /**
     * 维护相同session的通知Id
     */
    protected static Map<String, Integer> mNoticeMap = new HashMap<String, Integer>();
    public static final String FAKE_NODETAIL_SESSION_ID = "union-session";
    private int noticeId;

    @Override
    public void sendNotification(InboxMessageNewNotification notification) {
        if (Utils.isAppForground(AppApplication.getInstance())) {// 不在后台
            Intent mIntent = new Intent(Push2Type.InboxMessageNew.getType());
            mIntent.putExtra(INBOX_MSG_SESSION_ID, notification.sessionId);
            mIntent.putExtra(INBOX_MSG_MESSAGE_ID, notification.messageId);
            mCtx.sendBroadcast(mIntent);
        } else {
            String msg = null;
            String username = null;
            if (ALOHA_PUSH_Notification_New_Message_Text.equals(notification.getAlertLocKey())) {
                // 文本
                msg = mCtx.getString(R.string.aloha_push_notification_new_message_text,//
                        notification.getAlertLocArgs().get(0), notification.getAlertLocArgs().get(1));
                username = notification.getAlertLocArgs().get(0);
            } else if (ALOHA_PUSH_Notification_New_Message_Photo.equals(notification.getAlertLocKey())) {
                msg = mCtx.getString(R.string.aloha_push_notification_new_message_photo)
                        + notification.getAlertLocArgs().get(0);
                username = notification.getAlertLocArgs().get(0);
            } else if (ALOHA_PUSH_Notification_New_Message_NoDetail.equals(notification.getAlertLocKey())) {
                // 不显示详情
                msg = mCtx.getString(R.string.aloha_push_notification_new_message_nodetail);
            }
            NoticeBarController.getInstance(mCtx).showSessionNotice(mCtx, notification, msg, username);
        }
    }

    /**
     * @return
     * @Description:自增的通知ID
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2015年4月21日
     */
    private int getNoticeID() {
        return noticeId++;
    }

    public void testInboxMsg(InboxMessageNewNotification notification) {
        String msg = null;
        String username = null;
        /* 每次获取一次当前的通知ID */

        if (ALOHA_PUSH_Notification_New_Message_Text.equals(notification.getAlertLocKey())) {
            msg = mCtx.getString(R.string.aloha_push_notification_new_message_text,//
                    notification.getAlertLocArgs().get(0), notification.getAlertLocArgs().get(1));
            username = notification.getAlertLocArgs().get(0);
        } else if (ALOHA_PUSH_Notification_New_Message_Photo.equals(notification.getAlertLocKey())) {
            msg = mCtx.getString(R.string.aloha_push_notification_new_message_photo) +
                    notification.getAlertLocArgs().get(0);
            username = notification.getAlertLocArgs().get(0);
        } else if (ALOHA_PUSH_Notification_New_Message_NoDetail.equals(notification.getAlertLocKey())) {
            msg = mCtx.getString(R.string.aloha_push_notification_new_message_nodetail);
        } else {
            return;
        }

        int currentNoticeId = getNoticeID();
        mNotifyBuilder = new NotificationCompat.Builder(mCtx);
        Bundle bundle = new Bundle();
        bundle.putString("sessionId", notification.sessionId);
        Class actClass = null;
        if (username != null) { // 合并消息
            bundle.putBoolean("forceBackToSessionList", true); // 强制回Session
            actClass = DialogueActivity.class;
            // mNotifyBuilder.setContentTitle(username);
            // if (mSessionMap.containsKey((notification).sessionId) &&
            // mSessionMap.size() > 0) {
            // mNotifyBuilder.setTicker("[" +
            // (mSessionMap.get((notification).sessionId) + 1) + "条] " +
            // username + " : " + msg);
            // mNotifyBuilder.setContentText("[" +
            // (mSessionMap.get((notification).sessionId) + 1) + "条] " + msg);
            // } else {
            // mNotifyBuilder.setTicker("" + username + " : " + msg);
            // mNotifyBuilder.setContentText(msg);
            // }
        } else {// 合并消息
            bundle.putString("openTab", "chat");
            actClass = MainAct.class;
            // mNotifyBuilder.setTicker(msg == null ? "" : msg);
            // mNotifyBuilder.setContentTitle("Aloha");
            // mNotifyBuilder.setContentText(msg == null ? "" : msg);
        }
        mNotifyBuilder.setSmallIcon(R.drawable.title_message);
        Intent resultIntent = new Intent(mCtx, actClass).putExtras(bundle);
        Intent deleteIntent = new Intent(mCtx, MonitorNoticeColumnClearBroadcast.class).//
                setAction(GlobalConstants.AppConstact.MONITOR_NOTICE_COLUMN_CLEAR_BROADCAST);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(mCtx, currentNoticeId, //
                resultIntent, PendingIntent.FLAG_ONE_SHOT);
        PendingIntent deletePendingIntent = PendingIntent.getBroadcast(mCtx, currentNoticeId, //
                deleteIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        mNotifyBuilder.setDeleteIntent(deletePendingIntent);
        mNotifyBuilder.setContentIntent(resultPendingIntent);

        if (ContextConfig.getInstance().getBooleanWithDefValue(GlobalConstants.AppConstact.PUSH_SOUND, true)) {
            mNotifyBuilder.setSound(Uri.parse("android.resource://com.wealoha.social/raw/complete"));
        }
        if (ContextConfig.getInstance().getBooleanWithDefValue(GlobalConstants.AppConstact.PUSH_VIBRATION, true)) {
            mNotifyBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
        }

        Notification noti = mNotifyBuilder.build();
        noti.flags = Notification.FLAG_AUTO_CANCEL;
        mNotificationManager.notify(currentNoticeId, noti);
    }
}
