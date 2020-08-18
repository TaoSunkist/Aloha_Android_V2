package com.wealoha.social.api.notify2.bean;

import com.wealoha.social.api.post.bean.Post;
import com.wealoha.social.api.user.bean.User;

/**
 * 
 * 
 * @author javamonk
 * @createTime 2015年2月25日 下午12:25:04
 */
public class PostCommentNotify2 extends AbsNotify2 {

	private static final long serialVersionUID = 6159518661888749040L;
	private final boolean replyMe;
	private final String comment;
	private final String commentId;
	private final User fromUser;
	private final Post post;
	private int count;

	public PostCommentNotify2(Notify2Type type, boolean unread, String notifyid, long updateTimeMillis, boolean replyMe, String comment, String commentid, User fromUser, Post post, int count) {
		super(type, unread, notifyid, updateTimeMillis);
		this.replyMe = replyMe;
		this.comment = comment;
		this.commentId = commentid;
		this.fromUser = fromUser;
		this.post = post;
		this.count = count;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public boolean isReplyMe() {
		return replyMe;
	}

	public String getCommentId() {
		return commentId;
	}

	public String getComment() {
		return comment;
	}

	public User getFromUser() {
		return fromUser;
	}

	public Post getPost() {
		return post;
	}

}
