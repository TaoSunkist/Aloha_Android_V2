package com.wealoha.social.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.wealoha.social.api.common.bean.Image;
import com.wealoha.social.api.common.dto.ImageDTO;
import com.wealoha.social.api.user.bean.User;

public class UserTag implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6072731508136652565L;
	public final Double tagAnchorX;
	public final Double tagAnchorY;
	public final Double tagCenterX;
	public final Double tagCenterY;
	public final User user;

	public UserTag(Double tagAnchorX, Double tagAnchorY, Double tagCenterX, Double tagCenterY, User user) {
		super();
		this.tagAnchorX = tagAnchorX;
		this.tagAnchorY = tagAnchorY;
		this.tagCenterX = tagCenterX;
		this.tagCenterY = tagCenterY;
		this.user = user;
	}

	public static List<UserTag> fromDTOList(List<UserTagsDTO> userTagDTOList, Map<String, UserDTO> userMap, Map<String, ImageDTO> imageMap) {
		if (userTagDTOList == null || userMap == null || imageMap == null) {
			return null;
		}
		List<UserTag> userTagList = new ArrayList<UserTag>(userTagDTOList.size());
		UserDTO userDto;
		Image image;
		for (UserTagsDTO userTagDto : userTagDTOList) {
			userDto = userMap.get(userTagDto.tagUserId);
			image = Image.fromDTO(imageMap.get(userDto.avatarImageId));
			userTagList.add(new UserTag(userTagDto.tagAnchorX, userTagDto.tagAnchorY,//
			userTagDto.tagCenterX, userTagDto.tagCenterY, User.fromDTO(userDto, image)));
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

	public User getUser() {
		return user;
	}

}
