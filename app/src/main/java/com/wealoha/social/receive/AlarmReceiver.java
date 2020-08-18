package com.wealoha.social.receive;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import com.wealoha.social.ContextConfig;
import com.wealoha.social.R;
import com.wealoha.social.activity.MainAct;
import com.wealoha.social.commons.GlobalConstants;
import com.wealoha.social.ui.dialogue.DialogueActivity;

public class AlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		if (bundle != null) {
			String title = bundle.getString(GlobalConstants.AppConstact.ALARM_TITLE);
			String tickerText = bundle.getString(GlobalConstants.AppConstact.ALARM_TICKER_TEXT);
			String description = bundle.getString(GlobalConstants.AppConstact.ALARM_DESCRIPTION);
			sendNotice(context, tickerText, title, description, GlobalConstants.AppConstact.LOADING_SEND_TIMING_NOTICE);
		}
	}

	private static NotificationManager mNotificationManager;

	/**
	 * @Description: 发送计时已经完毕的广播
	 * @see:
	 * @since:
	 * @description
	 * @author: sunkist
	 * @param context
	 * @date:2014-11-25
	 */
	private void sendNotice(Context context, String tickerText, String title, String des, int noticeId) {
		mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(context);
		mNotifyBuilder.setContentTitle(context.getString(R.string.aloha_time_now_local_title));
		mNotifyBuilder.setContentText(context.getString(R.string.aloha_time_now_local_content));
		mNotifyBuilder.setSmallIcon(R.drawable.title_message);
		if (ContextConfig.getInstance().getBooleanWithDefValue(GlobalConstants.AppConstact.PUSH_SOUND, true)) {
			mNotifyBuilder.setSound(Uri.parse("android.resource://com.wealoha.social/raw/complete"));
		}
		if (ContextConfig.getInstance().getBooleanWithDefValue(GlobalConstants.AppConstact.PUSH_VIBRATION, true)) {
			mNotifyBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
		}
		// 发送本地Push，跳回MainAct, 默认Loading
		Intent resultIntent = new Intent(context, MainAct.class);

		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		stackBuilder.addParentStack(DialogueActivity.class);
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

		// PendingIntent resultPendingIntent =
		// PendingIntent.getActivity(context, 0, resultIntent,
		// PendingIntent.FLAG_UPDATE_CURRENT, null);

		mNotifyBuilder.setContentIntent(resultPendingIntent);
		Notification noti = mNotifyBuilder.build();
		// noti.defaults = Notification.DEFAULT_SOUND;
		noti.tickerText = context.getString(R.string.aloha_time_now_local_title);
		noti.flags = Notification.FLAG_AUTO_CANCEL;
		Intent deleteIntent = new Intent(context, MonitorNoticeColumnClearBroadcast.class);
		deleteIntent.setAction(GlobalConstants.AppConstact.MONITOR_NOTICE_COLUMN_CLEAR_BROADCAST);
		noti.deleteIntent = PendingIntent.getBroadcast(context, 0, deleteIntent, 0);
		mNotificationManager.notify(noticeId, noti);
	}

	public static void clearNotice() {
		if (mNotificationManager != null) {
			mNotificationManager.cancelAll();
		}
	}
}
