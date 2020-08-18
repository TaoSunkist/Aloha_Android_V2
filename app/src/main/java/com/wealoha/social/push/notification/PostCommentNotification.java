package com.wealoha.social.push.notification;

public class PostCommentNotification extends Notification {

	/**
	 * @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么)
	 */
	private static final long serialVersionUID = 4927307057208417269L;

	public static final String TYPE = "PostComment";

	public String userId;
	public String postId;
	public String commentId;
	public boolean reply;
}
