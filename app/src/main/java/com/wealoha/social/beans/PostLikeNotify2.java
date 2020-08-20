package com.wealoha.social.beans;

import java.util.List;

/**
 * 
 * 
 * @author javamonk
 * @createTime 2015年2月25日 上午10:57:11
 */
public class PostLikeNotify2 extends AbsNotify2 {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1517780633471613601L;
	private final Post post;
	private final List<User2> user2s;
	private int count;
	public PostLikeNotify2(Notify2Type type, boolean unread, String notifyid, long updateTimeMillis, Post post, List<User2> user2s, int count) {
		super(type, unread, notifyid, updateTimeMillis);
		this.post = post;
		this.user2s = user2s;
		this.count = count;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public Post getPost() {
		return post;
	}

	public List<User2> getUser2s() {
		return user2s;
	}
}
