package com.wealoha.social.module;

import com.wealoha.social.beans.Feed;
import com.wealoha.social.beans.User;

/**
 * @author:sunkist
 * @see:
 * @since:
 * @description 圈人
 * @copyright wealoha.com
 * @Date:2015-2-13
 */
public class TagItem extends NotifyItem {
	/**
	 * tag只有一个User对象
	 */
	public User mUser;
	/**
	 * 对应被转发的Feed
	 */
	public Feed mFeed;
}
