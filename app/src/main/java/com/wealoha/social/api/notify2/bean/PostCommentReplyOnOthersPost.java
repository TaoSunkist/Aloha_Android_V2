package com.wealoha.social.api.notify2.bean;

import com.wealoha.social.api.post.bean.Post;
import com.wealoha.social.api.user.bean.User;


public class PostCommentReplyOnOthersPost extends AbsNotify2{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3241745037614905208L;
	
	private User postAuthor;
	private User fromUser;
	private String commentId;
	private String comment;
	private Post post;
	
	public PostCommentReplyOnOthersPost(Notify2Type type, boolean unread, String notifyid, long updateTimeMillis,//
			User postAuthor, User fromUser, String commentId, String comment, Post post) {
		super(type, unread, notifyid, updateTimeMillis);
		this.post = post;
		this.postAuthor = postAuthor;
		this.fromUser = fromUser;
		this.comment = comment;
		this.commentId = commentId;
	}

	
	public User getPostAuthor() {
		return postAuthor;
	}

	
	public void setPostAuthor(User postAuthor) {
		this.postAuthor = postAuthor;
	}

	
	public User getFromUser() {
		return fromUser;
	}

	
	public void setFromUser(User fromUser) {
		this.fromUser = fromUser;
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
