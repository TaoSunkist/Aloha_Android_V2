package com.wealoha.social.push.notification;

public class PostLikeNotification extends Notification {

	/**
	 * @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么)
	 */
	private static final long serialVersionUID = -5262273310999178274L;
	public static final String TYPE = "PostLike";

	public String userId;
	public String postId;
}
