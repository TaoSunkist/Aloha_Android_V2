package com.wealoha.social;

import android.app.Notification;
import android.content.Context;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;

public class NotificationManager {
	private static Context mCtx;
	/**
	 * Push-->回话页面.
	 */
	public static final int DIALOG_TYPE = 0x01;
	private static NotificationManager mNotificationManager;
	/**
	 * push过来的实体id,通知的实体集合.
	 */

	private NotificationManager() {
	}

	/**
	 * @Description:
	 * @param context
	 * @return
	 * @see:
	 * @since:
	 * @description
	 * @author: sunkist
	 * @date:2015年4月14日
	 */
	public synchronized static NotificationManager getInstance(Context context) {
		mCtx = context;
		if (mNotificationManager == null) {
			mNotificationManager = new NotificationManager();
		}
		return mNotificationManager;
	}

	/**
	 * @Description:发送Push
	 * @see:
	 * @since:
	 * @description
	 * @author: sunkist
	 * @date:2015年4月14日
	 */
	public void sendNotification(int pushType) {
		
		android.app.NotificationManager notificationManager = (android.app.NotificationManager) AppApplication.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
		// 每一条Push必须对应一个PushModel
		NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(mCtx);
		Class actClass = null;
		Bundle bundle = new Bundle();
		mNotifyBuilder.setSmallIcon(R.drawable.title_message);
		mNotifyBuilder.setContentTitle("Title");
		mNotifyBuilder.setTicker("Ticker");
		mNotifyBuilder.setContentText("ContentText");
		mNotifyBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
		Notification noti = mNotifyBuilder.build();
		noti.flags = Notification.FLAG_AUTO_CANCEL;
		notificationManager.notify(0, noti);
	}

	/**
	 * @Description: 获取Notification的Id
	 * @return
	 * @see:
	 * @since:
	 * @description
	 * @author: sunkist
	 * @date:2015年4月14日
	 */
	public static int getNotifyId(int pushType) {
		int groupId = pushType;
		// 把app更新相关的归为一类
		return R.id.app_name + groupId;
	}

	/**
	 * @Description:根据ID删除所有的PushId
	 * @param notificationId
	 * @see:
	 * @since:
	 * @description
	 * @author: sunkist
	 * @date:2015年4月14日
	 */
	public void clearNotification(int notificationId) {
		android.app.NotificationManager notificationManager = (android.app.NotificationManager) AppApplication.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
	}
}
