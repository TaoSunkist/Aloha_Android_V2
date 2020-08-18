package com.wealoha.social.push.model;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.core.app.NotificationCompat;

import com.wealoha.social.AppApplication;
import com.wealoha.social.ContextConfig;
import com.wealoha.social.R;
import com.wealoha.social.activity.NaviIntroActivity;
import com.wealoha.social.activity.WelcomeAct;
import com.wealoha.social.commons.GlobalConstants;
import com.wealoha.social.interfaces.NotifyPush;
import com.wealoha.social.push.notification.AlohaTimeNotification;

public class AlohaTimePush extends BasePush implements NotifyPush<AlohaTimeNotification> {
	private int noticeId = 0;

	public AlohaTimePush(Context ctx) {
		mCtx = ctx;
		noticeId = R.string.aloha_time_photo_count;
	}

	@Override
	public void sendNotification(AlohaTimeNotification notification) {
		int currentNoticeId = getNoticeId();
		android.app.NotificationManager notificationManager = (android.app.NotificationManager) AppApplication.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
		// 每一条Push必须对应一个PushModel
		NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(mCtx);
		mNotifyBuilder.setSmallIcon(R.drawable.title_message);
		mNotifyBuilder.setContentTitle(mCtx.getString(R.string.aloha_time_now_local_title));
		mNotifyBuilder.setContentText(mCtx.getString(R.string.aloha_time_now_local_content));
		if (ContextConfig.getInstance().getBooleanWithDefValue(GlobalConstants.AppConstact.PUSH_SOUND, true)) {
			mNotifyBuilder.setSound(Uri.parse("android.resource://com.wealoha.social/raw/complete"));
		}
		if (ContextConfig.getInstance().getBooleanWithDefValue(GlobalConstants.AppConstact.PUSH_VIBRATION, true)) {
			mNotifyBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
		}
		mNotifyBuilder.setAutoCancel(true);// 点击消失
		Intent intent = new Intent(mCtx, NaviIntroActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(mCtx, currentNoticeId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		mNotifyBuilder.setContentIntent(pendingIntent);
		Notification noti = mNotifyBuilder.build();
		notificationManager.notify(currentNoticeId, noti);
	}

	private int getNoticeId() {
		return noticeId++;
	}
}
