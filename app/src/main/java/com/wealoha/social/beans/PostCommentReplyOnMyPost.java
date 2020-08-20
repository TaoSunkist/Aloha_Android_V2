package com.wealoha.social.beans;


public class PostCommentReplyOnMyPost extends AbsNotify2{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private User2 replyUser2;
	private User2 fromUser2;
	private String comment;
	private String commentId;
	private Post post;
	public PostCommentReplyOnMyPost(Notify2Type type, boolean unread, String notifyid,//
									long updateTimeMillis, User2 replyUser2, User2 fromUser2, String commentId, //
									String comment, Post post) {
		super(type, unread, notifyid, updateTimeMillis);
		this.replyUser2 = replyUser2;
		this.fromUser2 = fromUser2;
		this.comment = comment;
		this.commentId = commentId;
		this.post = post;
	}
	
	public User2 getReplyUser2() {
		return replyUser2;
	}
	
	public void setReplyUser2(User2 replyUser2) {
		this.replyUser2 = replyUser2;
	}
	
	public User2 getFromUser2() {
		return fromUser2;
	}
	
	public void setFromUser2(User2 fromUser2) {
		this.fromUser2 = fromUser2;
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
