package com.wealoha.social.module;

import java.util.ArrayList;
import java.util.List;

import com.wealoha.social.beans.Feed;
import com.wealoha.social.beans.User;

public class PraiseFeedItem extends NotifyItem {
	/**
	 * 赞你的User集合
	 */
	public final List<User> mUsers = new ArrayList<User>();
	/**
	 * 被赞的Feed图片.
	 */
	public Feed mFeed;
	/**
	 * 点赞的人数
	 */
	public int count;
}
