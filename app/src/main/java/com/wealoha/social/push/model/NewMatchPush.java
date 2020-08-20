package com.wealoha.social.push.model;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;

import com.wealoha.social.AppApplication;
import com.wealoha.social.BaseFragAct;
import com.wealoha.social.ContextConfig;
import com.wealoha.social.R;
import com.wealoha.social.api.ServerApi;
import com.wealoha.social.beans.Result;
import com.wealoha.social.beans.ProfileData;
import com.wealoha.social.commons.GlobalConstants;
import com.wealoha.social.interfaces.NotifyPush;
import com.wealoha.social.push.notification.NewMatchNotification;
import com.wealoha.social.receive.MonitorNoticeColumnClearBroadcast;
import com.wealoha.social.ui.dialogue.DialogueActivity;
import com.wealoha.social.utils.ContextUtil;
import com.wealoha.social.utils.Utils;

public class NewMatchPush extends BasePush implements NotifyPush<NewMatchNotification> {
	@Inject
	ServerApi mProfileService;
	@Inject
	ContextUtil contextUtil;
	private String ALOHA_PUSH_Notification_New_Match = "ALOHA_PUSH_Notification_New_Match";

	public NewMatchPush(AppApplication instance) {
		mCtx = instance;
		mNotificationManager = (NotificationManager) mCtx.getSystemService(Context.NOTIFICATION_SERVICE);
	}

	static int noticeId = R.string.aloha_push_notification_new_match;

	@Override
	public void sendNotification(NewMatchNotification notification) {
		mNotificationManager = (NotificationManager) AppApplication.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
		if (Utils.isApplicationBroughtToBackground(mCtx)) {
			String msg = mCtx.getString(R.string.aloha_push_notification_new_match);

			mNotifyBuilder = new NotificationCompat.Builder(mCtx);
			Class actClass = DialogueActivity.class;
			Bundle bundle = new Bundle();

			bundle.putBoolean("forceBackToSessionList", true); // 强制回Session
			bundle.putString("sessionId", ((NewMatchNotification) notification).userId);
			mNotifyBuilder.setSmallIcon(R.drawable.title_message);
			Intent deleteIntent = new Intent(mCtx, MonitorNoticeColumnClearBroadcast.class);
			deleteIntent.setAction(GlobalConstants.AppConstact.MONITOR_NOTICE_COLUMN_CLEAR_BROADCAST);

			mNotifyBuilder.setTicker(msg == null ? "" : msg);
			mNotifyBuilder.setContentTitle("Aloha");
			mNotifyBuilder.setContentText(msg == null ? "" : msg);

			Intent resultIntent = new Intent(mCtx, actClass);
			resultIntent.putExtras(bundle);
			PendingIntent resultPendingIntent = PendingIntent.getActivity(mCtx, i++, resultIntent,//
					Intent.FILL_IN_ACTION);
			mNotifyBuilder.setContentIntent(resultPendingIntent);
			mNotifyBuilder.setDeleteIntent(PendingIntent.getBroadcast(mCtx, i++, deleteIntent, //
					PendingIntent.FLAG_CANCEL_CURRENT));
			if (ContextConfig.getInstance().getBooleanWithDefValue(GlobalConstants.AppConstact.PUSH_SOUND, true)) {
				if (ALOHA_PUSH_Notification_New_Match.equals(notification.getAlertLocKey())) {
					mNotifyBuilder.setSound(Uri.parse("android.resource://com.wealoha.social/raw/popcorn"));
				} else {
					mNotifyBuilder.setSound(Uri.parse("android.resource://com.wealoha.social/raw/complete"));
				}
			}
			if (ContextConfig.getInstance().getBooleanWithDefValue(GlobalConstants.AppConstact.PUSH_VIBRATION, true)) {
				mNotifyBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
			}

			mNotifyBuilder.setContentText(msg);
			mNotifyBuilder.setSmallIcon(R.drawable.title_message);
			Notification noti = mNotifyBuilder.build();
			noti.flags = Notification.FLAG_AUTO_CANCEL;
			mNotificationManager.notify(noticeId++, noti);
		} else {
			mProfileService.getUserProfile(notification.userId, new Callback<Result<ProfileData>>() {

				@Override
				public void success(Result<ProfileData> result, Response arg1) {
					if (result == null) {
						return;
					}
					if (result.isOk()) {
						BaseFragAct act = (BaseFragAct) contextUtil.getForegroundAct();
						if (act != null) {
							act.showMatchPopup(result.getData().user);
						}
					}

				}

				@Override
				public void failure(RetrofitError arg0) {
				}
			});

		}
	}

}
