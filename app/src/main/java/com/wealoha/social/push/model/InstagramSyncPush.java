package com.wealoha.social.push.model;

import android.app.Notification;
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
import com.wealoha.social.push.notification.InstagramNotification;
import com.wealoha.social.receive.MonitorNoticeColumnClearBroadcast;
import com.wealoha.social.ui.topic.TopicDetailActivity;
import com.wealoha.social.utils.Utils;
import com.wealoha.social.utils.XL;

public class InstagramSyncPush extends BasePush implements NotifyPush<InstagramNotification> {

	private int mNoticeId;

	public InstagramSyncPush(Context ctx) {
		mNoticeId = R.string.instagram_synchronization_is_complete;
		mCtx = ctx;
	}

	@Override
	public void sendNotification(InstagramNotification notification) {
		if (!Utils.isAppForground(AppApplication.getInstance())) {
			int currentNoticeId = getNoticeId();

			android.app.NotificationManager notificationManager = (android.app.NotificationManager) AppApplication.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
			// 每一条Push必须对应一个PushModel
			NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(mCtx);
			mNotifyBuilder.setSmallIcon(R.drawable.title_message);
			mNotifyBuilder.setContentText(mCtx.getString(R.string.instagram_synchronization_is_complete));
			mNotifyBuilder.setContentTitle(mCtx.getString(R.string.app_name));
			if (ContextConfig.getInstance().getBooleanWithDefValue(GlobalConstants.AppConstact.PUSH_SOUND, true)) {
				mNotifyBuilder.setSound(Uri.parse("android.resource://com.wealoha.social/raw/complete"));
			}
			if (ContextConfig.getInstance().getBooleanWithDefValue(GlobalConstants.AppConstact.PUSH_VIBRATION, true)) {
				mNotifyBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
			}
			mNotifyBuilder.setAutoCancel(true);// 点击消失
			Intent intent = new Intent(mCtx, MainAct.class);
			Bundle bundle = new Bundle();
			bundle.putString("openTab", "profile");
			intent.putExtras(bundle);
			PendingIntent pendingIntent = PendingIntent.getActivity(mCtx, currentNoticeId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
			mNotifyBuilder.setContentIntent(pendingIntent);
			Notification noti = mNotifyBuilder.build();
			notificationManager.notify(currentNoticeId, noti);
		}
	}

	private int getNoticeId() {
		return mNoticeId++;
	}
}
