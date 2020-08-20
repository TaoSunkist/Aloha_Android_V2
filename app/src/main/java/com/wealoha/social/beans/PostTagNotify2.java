package com.wealoha.social.beans;

import com.wealoha.social.api.user.bean.User;

/**
 * 
 * 
 * @author javamonk
 * @createTime 2015年2月25日 下午12:25:04
 */
public class PostTagNotify2 extends AbsNotify2 {

	private static final long serialVersionUID = -346708852583028562L;
	private final Post post;
	private final User fromUser;

	public PostTagNotify2(Notify2Type type, boolean unread, String notifyid, long updateTimeMillis, User fromUser, Post post) {
		super(type, unread, notifyid, updateTimeMillis);
		this.fromUser = fromUser;
		this.post = post;
	}

	public User getFromUser() {
		return fromUser;
	}

	public Post getPost() {
		return post;
	}
}
