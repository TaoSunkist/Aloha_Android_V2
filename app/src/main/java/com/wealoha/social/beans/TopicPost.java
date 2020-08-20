package com.wealoha.social.beans;

import java.util.ArrayList;
import java.util.List;

import com.wealoha.social.api.topic.bean.HashTag;

public class TopicPost {

	/**
	 * 适应gridview 样式实图，三个post一行
	 */
	private List<Post> postsItem;
	/** List<TopicPost> 是否满足一行三个 */
	private boolean isItemFull = true;
	/** List<TopicPost> 有几个空位 */
	private int vacancyCount;

	private boolean isTitleItem;
	private int titleId;
	private int itemType;
	private HashTag hashTag;
	public static final int RELODE_TYPE = 0x0001;
	public static final int NOMORE_TYPE = 0x0002;
	public static final int TITLE_TYPE = 0x0003;
	public static final int NORMAL_TYPE = 0x0004;

	public TopicPost() {
		postsItem = new ArrayList<>();
	}

	public List<Post> getPostsItem() {
		return postsItem;
	}

	public void setPostsItem(List<Post> postsItem) {
		this.postsItem = postsItem;
	}

	public boolean isItemFull() {
		return isItemFull;
	}

	public void setItemFull(boolean isItemFull) {
		this.isItemFull = isItemFull;
	}

	public int isVacancyCount() {
		return vacancyCount;
	}

	public void setVacancyCount() {
		this.vacancyCount++;
	}

	public boolean isTitleItem() {
		return isTitleItem;
	}

	public void setTitleItem(boolean isTitleItem) {
		this.isTitleItem = isTitleItem;
	}

	public int getTitleId() {
		return titleId;
	}

	public void setTitleId(int titleId) {
		this.titleId = titleId;
	}

	public int getItemType() {
		return itemType;
	}

	public void setItemType(int itemType) {
		this.itemType = itemType;
	}

	public HashTag getHashTag() {
		return hashTag;
	}

	public void setHashTag(HashTag hashTag) {
		this.hashTag = hashTag;
	}

}
