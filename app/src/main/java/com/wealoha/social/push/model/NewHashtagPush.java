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
import com.wealoha.social.commons.GlobalConstants;
import com.wealoha.social.interfaces.NotifyPush;
import com.wealoha.social.push.notification.NewHashtagNotification;
import com.wealoha.social.ui.topic.TopicDetailActivity;
import com.wealoha.social.utils.Utils;

/**
 * @author:sunkist
 * @description:话题的通知
 * @Date:2015年8月3日
 */
public class NewHashtagPush extends BasePush implements NotifyPush<NewHashtagNotification> {

	// private String ALOHA_PUSH_Notification_NewHashtag_Activity =
	// "ALOHA_PUSH_Notification_NewHashtag_Activity";
	private int noticeId = 0;

	public NewHashtagPush(AppApplication instance) {
		mCtx = instance;
		noticeId = R.string.aloha_time_photo_count;
	}

	@Override
	public void sendNotification(NewHashtagNotification notification) {
		if (notification.getAlertLocArgs() == null || notification.getAlertLocArgs().get(0) == null) {
			return;
		}
		if (Utils.isAppForground(AppApplication.getInstance())) {// 不在后台
			return;
		} else {
			int currentNoticeId = getNoticeId();
			android.app.NotificationManager notificationManager = (android.app.NotificationManager) AppApplication.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
			// 每一条Push必须对应一个PushModel
			NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(mCtx);
			mNotifyBuilder.setSmallIcon(R.drawable.title_message);
			mNotifyBuilder.setContentText(notification.getAlertLocArgs().get(0));
			if (notification.getAlertLocArgs().size() >= 2) {
				mNotifyBuilder.setContentTitle(notification.getAlertLocArgs().get(1));
			}
			if (ContextConfig.getInstance().getBooleanWithDefValue(GlobalConstants.AppConstact.PUSH_SOUND, true)) {
				mNotifyBuilder.setSound(Uri.parse("android.resource://com.wealoha.social/raw/complete"));
			}
			if (ContextConfig.getInstance().getBooleanWithDefValue(GlobalConstants.AppConstact.PUSH_VIBRATION, true)) {
				mNotifyBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
			}
			mNotifyBuilder.setAutoCancel(true);// 点击消失
			Intent intent = new Intent(mCtx, TopicDetailActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString(GlobalConstants.TAGS.OPEN_HASH_TAG_TYPE, GlobalConstants.TAGS.IS_PUSH_HASHTAG);
			bundle.putString(GlobalConstants.TAGS.IS_HASHTAG_ID, notification.hashtagId);
			intent.putExtras(bundle);
			PendingIntent pendingIntent = PendingIntent.getActivity(mCtx, currentNoticeId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
			mNotifyBuilder.setContentIntent(pendingIntent);
			Notification noti = mNotifyBuilder.build();
			notificationManager.notify(currentNoticeId, noti);
		}
	}

	private int getNoticeId() {
		return noticeId++;
	}
}
