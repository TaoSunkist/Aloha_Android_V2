package com.wealoha.social.beans;

import java.util.List;
import java.util.Map;

public class FeedGetData extends ResultData {

	public List<PostDTO> list;

	public Map<String, ImageCommonDto> imageMap;
	public Map<String, VideoCommonDTO> videoMap;

	public Map<String, Integer> commentCountMap;

	public Map<String, Integer> likeCountMap;

	public Map<String, UserDTO> userMap;

	public String nextCursorId;

}
