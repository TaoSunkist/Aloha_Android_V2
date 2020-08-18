package com.wealoha.social.impl;

public interface DynamicType {
	/**
	 * feed点赞的通知.
	 */
	int FEED_PRAISE_VIEW_TYPE = 0;
	/**
	 * Feed被评论的通知
	 */
	int FEED_COMMENT_VIEW_TYPE = FEED_PRAISE_VIEW_TYPE + 1;
	/**
	 * 圈人的通知
	 */
	int FEED_POST_TAG_TYPE = FEED_COMMENT_VIEW_TYPE + 1;
	/**
	 * 新人气的通知
	 */
	int FEED_NEWALOHA_VIEW_TYPE = FEED_POST_TAG_TYPE + 1;
	/**
	 * View的类型数量
	 */
	int VIEW_TYPE_COUNT = FEED_NEWALOHA_VIEW_TYPE + 1;

	/**
	 * feed被点赞的通知
	 */
	String TYPE_NOTIFY_POST_LIKE = "PostLike";
	/**
	 * feed被评论的通知
	 */
	String TYPE_NOTIFY_POST_COMMENT = "PostComment";
	/**
	 * 圈人
	 */
	String TYPE_NOTIFY_POST_TAG = "PostTag";
	/**
	 * tag:Aloha的人气
	 */
	String TYPE_NOTIFY_POST_NEWALOHA = "NewAloha";
}
