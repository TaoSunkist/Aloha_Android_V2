package com.wealoha.social.api.feed;

import java.util.List;
import java.util.Map;

import com.wealoha.social.api.common.dto.ImageDTO;
import com.wealoha.social.api.common.dto.VideoDTO;
import com.wealoha.social.api.post.dto.PostDTO;
import com.wealoha.social.api.user.dto.UserDTO;
import com.wealoha.social.beans.ResultData;

public class FeedGetData extends ResultData {

	public List<PostDTO> list;

	public Map<String, ImageDTO> imageMap;
	public Map<String, VideoDTO> videoMap;

	public Map<String, Integer> commentCountMap;

	public Map<String, Integer> likeCountMap;

	public Map<String, UserDTO> userMap;

	public String nextCursorId;

}
