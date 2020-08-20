package com.wealoha.social.beans;

import java.util.List;

/**
 * 话题页下获取的最新和最热的post集合
 * 
 * @author dell-pc
 *
 */
public class TopicPosts {

	private List<TopicPost> posts;
	private String cursorId;
	private HashTag hashTag;

	public List<TopicPost> getPosts() {
		return posts;
	}

	public void setPosts(List<TopicPost> posts) {
		this.posts = posts;
	}

	public String getCursorId() {
		return cursorId;
	}

	public void setCursorId(String cursorId) {
		this.cursorId = cursorId;
	}

	public void clear() {
		posts.clear();
		cursorId = null;
	}

	public HashTag getHashTag() {
		return hashTag;
	}

	public void setHashTag(HashTag hashTag) {
		this.hashTag = hashTag;
	}
}
