package com.wealoha.social.beans;

import java.util.List;
import java.util.Map;

import com.wealoha.social.api.common.dto.ImageDTO;
import com.wealoha.social.api.common.dto.VideoDTO;

/**
 * 话题功能部分请求新版的post 接口，新版post接口返回的数据和旧版中由postdto 转换后的post结构一致
 * 
 * @author dell-pc
 */
public class TopicPostResultData extends ResultData {

	/** 最新 */
	public List<TopicPostDTO> list;
	/** 最热 */
	public List<TopicPostDTO> hot;
	public Map<String, ImageDTO> imageMap;
	public Map<String, VideoDTO> videoMap;
	public Map<String, UserDTO> userMap;
	/** 最新的cursor */
	public String nextCursorId;
	public HashTagDTO hashtag;
}
