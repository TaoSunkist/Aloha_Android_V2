package com.wealoha.social.beans;

import com.wealoha.social.api.user.bean.User;


public class PostCommentReplyOnMyPost extends AbsNotify2{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private User replyUser;
	private User fromUser;
	private String comment;
	private String commentId;
	private Post post;
	public PostCommentReplyOnMyPost(Notify2Type type, boolean unread, String notifyid,//
			long updateTimeMillis,User replyUser, User fromUser, String commentId, //
			String comment, Post post) {
		super(type, unread, notifyid, updateTimeMillis);
		this.replyUser = replyUser;
		this.fromUser = fromUser;
		this.comment = comment;
		this.commentId = commentId;
		this.post = post;
	}
	
	public User getReplyUser() {
		return replyUser;
	}
	
	public void setReplyUser(User replyUser) {
		this.replyUser = replyUser;
	}
	
	public User getFromUser() {
		return fromUser;
	}
	
	public void setFromUser(User fromUser) {
		this.fromUser = fromUser;
	}
	
	public String getComment() {
		return comment;
	}
	
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public String getCommentId() {
		return commentId;
	}
	
	public void setCommentId(String commentId) {
		this.commentId = commentId;
	}
	
	public Post getPost() {
		return post;
	}
	
	public void setPost(Post post) {
		this.post = post;
	}
	
	
}
