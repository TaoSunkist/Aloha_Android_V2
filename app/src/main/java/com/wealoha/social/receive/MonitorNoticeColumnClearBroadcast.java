package com.wealoha.social.receive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.wealoha.social.commons.GlobalConstants;
import com.wealoha.social.push.NoticeBarController;
import com.wealoha.social.utils.XL;

/**
 * @author:sunkist
 * @see:
 * @since:
 * @description 监听清除通知栏的动作
 * @copyright wealoha.com
 * @Date:2014-11-14
 */
public class MonitorNoticeColumnClearBroadcast extends BroadcastReceiver {
	private static final String TAG = MonitorNoticeColumnClearBroadcast.class.getSimpleName();

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		// 当用户清除所有通知栏时，清空NoticeBarController集合内的所有数据
		if (action != null && GlobalConstants.AppConstact.MONITOR_NOTICE_COLUMN_CLEAR_BROADCAST.equals(action)) {
			XL.d(TAG, "MONITOR_NOTICE_COLUMN_CLEAR_BROADCAST");
			NoticeBarController.getInstance(context).cleanAllSession();
		}
	}
}
