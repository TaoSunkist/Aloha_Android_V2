package com.wealoha.social.beans;

import java.util.List;
import java.util.Map;

import com.wealoha.social.api.common.dto.ImageDTO;
import com.wealoha.social.api.common.dto.VideoDTO;

public class FeedGetData extends ResultData {

	public List<PostDTO> list;

	public Map<String, ImageDTO> imageMap;
	public Map<String, VideoDTO> videoMap;

	public Map<String, Integer> commentCountMap;

	public Map<String, Integer> likeCountMap;

	public Map<String, UserDTO> userMap;

	public String nextCursorId;

}
