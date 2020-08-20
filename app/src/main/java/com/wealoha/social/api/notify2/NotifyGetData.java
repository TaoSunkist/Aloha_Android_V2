package com.wealoha.social.api.notify2;

import java.util.List;
import java.util.Map;

import com.wealoha.social.api.common.dto.ImageDTO;
import com.wealoha.social.api.notify2.dto.AbsNotify2DTO;
import com.wealoha.social.api.post.dto.PostDTO;
import com.wealoha.social.beans.UserDTO;
import com.wealoha.social.beans.ResultData;

/**
 * 
 * 
 * @author javamonk
 * @createTime 2015年2月25日 上午10:33:05
 */
public class NotifyGetData extends ResultData {

	public Map<String, Integer> commentCountMap;
	public Map<String, Integer> likeCountMap;
	public Map<String, UserDTO> userMap;
	public Map<String, ImageDTO> imageMap;
	public Map<String, PostDTO> postMap;
	public List<AbsNotify2DTO> list;
	public String nextCursorId;
}
