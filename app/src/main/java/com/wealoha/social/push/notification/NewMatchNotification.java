package com.wealoha.social.push.notification;

/**
 * 收到了新的Match
 * 
 * @author javamonk
 * @see
 * @since
 * @date 2014-11-12 上午8:36:25
 */
public class NewMatchNotification extends Notification {

	private static final long serialVersionUID = -8319268601097095775L;

	public static final String TYPE = "NewMatch";

	/** Match到的用户 */
	public String userId;
}
