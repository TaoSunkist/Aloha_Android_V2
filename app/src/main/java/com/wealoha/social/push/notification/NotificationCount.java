package com.wealoha.social.push.notification;

/**
 * 全局的通知数
 * 
 * @author superman
 * @createTime 2014-12-30 16:48:11
 */
public class NotificationCount {

	private static int NOTIFY_COUNT = 0;

	public static int getCommentCount() {
		return NOTIFY_COUNT;
	}

	public static synchronized void setCommentCount(int commencCount) {

		NOTIFY_COUNT = commencCount;

	}

	public static synchronized void setCommentCount() {

		NOTIFY_COUNT++;

	}

}
