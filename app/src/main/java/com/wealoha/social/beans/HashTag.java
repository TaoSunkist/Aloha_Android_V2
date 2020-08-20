package com.wealoha.social.beans;

import java.io.Serializable;

public class HashTag implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 8090400637875293423L;
	private String itemId;
	private String name;
	private CommonImage commonImage;
	private String summary;
	private int postCount;
	private String url;
	private String type;
	private int userCount;

	public HashTag(String itemId, String name, CommonImage commonImage, String summary, int postCount, String url, String type, int userCount) {
		super();
		this.itemId = itemId;
		this.name = name;
		this.commonImage = commonImage;
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
		return new HashTag(dto.itemId, dto.name, CommonImage.fromDTO(dto.backgroundImage), dto.summary, dto.postCount, dto.url, dto.type, dto.userCount);
	}

	public String getItemId() {
		return itemId;
	}

	public String getName() {
		return name;
	}

	public CommonImage getBackgroundImage() {
		return commonImage;
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
