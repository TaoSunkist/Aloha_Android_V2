package com.wealoha.social.api.topic.bean;

import java.io.Serializable;

import com.wealoha.social.api.common.bean.Image;
import com.wealoha.social.api.topic.dto.HashTagDTO;

public class HashTag implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8090400637875293423L;
	private String itemId;
	private String name;
	private Image image;
	private String summary;
	private int postCount;
	private String url;
	private String type;
	private int userCount;

	public HashTag(String itemId, String name, Image image, String summary, int postCount, String url, String type, int userCount) {
		super();
		this.itemId = itemId;
		this.name = name;
		this.image = image;
		this.summary = summary;
		this.postCount = postCount;
		this.url = url;
		this.type = type;
		this.userCount = userCount;
	}

	public static HashTag fromDTO(HashTagDTO dto) {
		if (dto == null) {
			return null;
		}
		return new HashTag(dto.itemId, dto.name, Image.fromDTO(dto.backgroundImage), dto.summary, dto.postCount, dto.url, dto.type, dto.userCount);
	}

	public String getItemId() {
		return itemId;
	}

	public String getName() {
		return name;
	}

	public Image getBackgroundImage() {
		return image;
	}

	public String getSummary() {
		return summary;
	}

	public int getPostCount() {
		return postCount;
	}

	public String getUrl() {
		return url;
	}

	public String getType() {
		return type;
	}

	public int getUserCount() {
		return userCount;
	}

}
