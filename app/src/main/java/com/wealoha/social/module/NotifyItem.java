package com.wealoha.social.module;

import com.wealoha.social.impl.DynamicType;

public class NotifyItem implements DynamicType {

	// public String postId;Feed中存在PostId
	public String fromUser;
	public String type;
	public long createTimeMillis;
//	public String comment;
//	public List<User> user;
	public long updateTimeMillis;
	public boolean unread;
	public boolean replyMe;

	/**
	 * @Description:获取需要显示的视图类型
	 * @see:
	 * @since:
	 * @description
	 * @author: sunkist
	 * @date:2015-2-6
	 */
	public int getType() {
		if (TYPE_NOTIFY_POST_LIKE.equals(type)) {// feed被点赞
			return FEED_PRAISE_VIEW_TYPE;
		} else if (TYPE_NOTIFY_POST_COMMENT.equals(type)) {// Feed被评论的Item显示
			return FEED_COMMENT_VIEW_TYPE;
		} else if (TYPE_NOTIFY_POST_NEWALOHA.equals(type)) {// 新人气的通知
			return FEED_NEWALOHA_VIEW_TYPE;
		} else if (TYPE_NOTIFY_POST_TAG.equals(type)) {// 圈人的通知
			return FEED_POST_TAG_TYPE;
		} else {
			return -1;
		}
	}
}
