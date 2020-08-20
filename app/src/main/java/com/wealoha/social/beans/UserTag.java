package com.wealoha.social.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserTag implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6072731508136652565L;
	public final Double tagAnchorX;
	public final Double tagAnchorY;
	public final Double tagCenterX;
	public final Double tagCenterY;
	public final User2 user2;

	public UserTag(Double tagAnchorX, Double tagAnchorY, Double tagCenterX, Double tagCenterY, User2 user2) {
		super();
		this.tagAnchorX = tagAnchorX;
		this.tagAnchorY = tagAnchorY;
		this.tagCenterX = tagCenterX;
		this.tagCenterY = tagCenterY;
		this.user2 = user2;
	}

	public static List<UserTag> fromDTOList(List<UserTagsDTO> userTagDTOList, Map<String, UserDTO> userMap, Map<String, ImageCommonDto> imageMap) {
		if (userTagDTOList == null || userMap == null || imageMap == null) {
			return null;
		}
		List<UserTag> userTagList = new ArrayList<UserTag>(userTagDTOList.size());
		UserDTO userDto;
		CommonImage commonImage;
		for (UserTagsDTO userTagDto : userTagDTOList) {
			userDto = userMap.get(userTagDto.tagUserId);
			commonImage = CommonImage.fromDTO(imageMap.get(userDto.avatarImageId));
			userTagList.add(new UserTag(userTagDto.tagAnchorX, userTagDto.tagAnchorY,//
			userTagDto.tagCenterX, userTagDto.tagCenterY, User2.fromDTO(userDto, commonImage)));
		}
		return userTagList;
	}

	public Double getTagAnchorX() {
		return tagAnchorX;
	}

	public Double getTagAnchorY() {
		return tagAnchorY;
	}

	public Double getTagCenterX() {
		return tagCenterX;
	}

	public Double getTagCenterY() {
		return tagCenterY;
	}

	public User2 getUser2() {
		return user2;
	}

}
