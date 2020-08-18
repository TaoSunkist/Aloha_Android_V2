package com.wealoha.social.push.notification;


/**
 * 新消息
 * 
 * @author javamonk
 * @see
 * @since
 * @date 2014-11-11 下午11:19:27
 */
public class InboxMessageNewNotification extends Notification {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String TYPE = "InboxMessageNew";

	public String messageId;
	public String sessionId;
}
