package com.wealoha.social.beans.notify;

import java.util.List;

import com.wealoha.social.adapter.FeedNoticeAdapter;
import com.wealoha.social.beans.User;
import com.wealoha.social.impl.DynamicType;

/**
 * 
 * @author:sunkist
 * @see:
 * @since:
 * @description
 * @copyright wealoha.com
 * @Date:2014-11-20
 * @deprecated 切换到Notify2
 */
public class Notify implements DynamicType {

	public String postId;
	public String fromUser;
	public String type;
	public long createTimeMillis;
	public String comment;
	public User replyUser;
	public List<String> userIds;
	public long updateTimeMillis;
	public boolean unread;
	public int count;
	public boolean replyMe;

	@Override
	public String toString() {
		return "Notify [postId=" + postId + ", fromUser=" + fromUser + ", type=" + type + ", createTimeMillis=" + createTimeMillis + ", comment=" + comment + ", replyUser=" + replyUser + ", userIds=" + userIds + ", updateTimeMillis=" + updateTimeMillis + ", unread=" + unread + ", count=" + count + ", replyMe=" + replyMe + "]";
	}

	/**
	 * @Description:获取需要显示的视图类型
	 * @see:
	 * @since:
	 * @description 暂时不能删除，涉及到 {@link FeedNoticeAdapter}
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
