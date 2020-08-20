package com.wealoha.social.beans;

/**
 * 
 * 
 * @author javamonk
 * @createTime 2015年2月25日 下午12:25:04
 */
public class PostTagNotify2 extends AbsNotify2 {

	private static final long serialVersionUID = -346708852583028562L;
	private final Post post;
	private final User2 fromUser2;

	public PostTagNotify2(Notify2Type type, boolean unread, String notifyid, long updateTimeMillis, User2 fromUser2, Post post) {
		super(type, unread, notifyid, updateTimeMillis);
		this.fromUser2 = fromUser2;
		this.post = post;
	}

	public User2 getFromUser2() {
		return fromUser2;
	}

	public Post getPost() {
		return post;
	}
}
