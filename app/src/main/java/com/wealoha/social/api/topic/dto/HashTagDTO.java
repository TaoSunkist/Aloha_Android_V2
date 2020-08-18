package com.wealoha.social.api.topic.dto;

import com.wealoha.social.api.common.dto.ImageDTO;

public class HashTagDTO {

	// itemId: "",
	// name: "name",
	// bgImage:"背景图片",
	// summary: "描述",
	// url: "",
	// postCount: "post数量",
	// type: "HashTag"

	public String itemId;
	public String name;
	public ImageDTO backgroundImage;
	public String summary;
	public int postCount;
	public String url;
	public String type;
	public int userCount;
}
