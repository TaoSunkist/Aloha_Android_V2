package com.wealoha.social.beans;

import java.util.List;
import java.util.Map;

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
	public Map<String, ImageCommonDto> imageMap;
	public Map<String, PostDTO> postMap;
	public List<AbsNotify2DTO> list;
	public String nextCursorId;
}
