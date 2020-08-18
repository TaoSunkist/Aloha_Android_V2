package com.wealoha.social.interfaces;

import com.wealoha.social.push.notification.Notification;

public interface NotifyPush<T extends Notification> {
	/**
	 * @Description:发送通知
	 * @see:
	 * @since:
	 * @description
	 * @author: sunkist
	 * @date:2015年4月16日
	 */
	void sendNotification(T notification);
}
