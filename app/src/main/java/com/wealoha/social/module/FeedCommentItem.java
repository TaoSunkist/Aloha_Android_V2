package com.wealoha.social.module;

import com.wealoha.social.beans.Feed;
import com.wealoha.social.beans.User;

/**
 * @author:sunkist
 * @see:
 * @since:
 * @description 评论你的Feed的Item
 * @copyright wealoha.com
 * @Date:2015-2-13
 */
public class FeedCommentItem extends NotifyItem {
	/**
	 * 新feed的描述.
	 */
	public String commentContent;
	/**
	 * 回复你的User
	 */
	public User replyUser;
	/**
	 * 回复对应的Feed.
	 */
	public Feed mFeed;
}
