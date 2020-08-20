package com.wealoha.social.beans;


public class PostCommentReplyOnOthersPost extends AbsNotify2{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3241745037614905208L;
	
	private User2 postAuthor;
	private User2 fromUser2;
	private String commentId;
	private String comment;
	private Post post;
	
	public PostCommentReplyOnOthersPost(Notify2Type type, boolean unread, String notifyid, long updateTimeMillis,//
										User2 postAuthor, User2 fromUser2, String commentId, String comment, Post post) {
		super(type, unread, notifyid, updateTimeMillis);
		this.post = post;
		this.postAuthor = postAuthor;
		this.fromUser2 = fromUser2;
		this.comment = comment;
		this.commentId = commentId;
	}

	
	public User2 getPostAuthor() {
		return postAuthor;
	}

	
	public void setPostAuthor(User2 postAuthor) {
		this.postAuthor = postAuthor;
	}

	
	public User2 getFromUser2() {
		return fromUser2;
	}

	
	public void setFromUser2(User2 fromUser2) {
		this.fromUser2 = fromUser2;
	}

	
	public String getCommentId() {
		return commentId;
	}

	
	public void setCommentId(String commentId) {
		this.commentId = commentId;
	}

	
	public String getComment() {
		return comment;
	}

	
	public void setComment(String comment) {
		this.comment = comment;
	}

	
	public Post getPost() {
		return post;
	}

	
	public void setPost(Post post) {
		this.post = post;
	}
	
	

}
