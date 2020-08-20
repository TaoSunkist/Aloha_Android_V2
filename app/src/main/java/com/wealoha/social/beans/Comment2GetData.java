package com.wealoha.social.beans;

import java.util.List;
import java.util.Map;

/**
 * @author:sunkist
 * @see:
 * @since:
 * @description
 * @copyright wealoha.com
 * @Date:2015年3月11日
 */
public class Comment2GetData extends ResultData {

	public String nextCursorId;
	public Map<String, UserDTO> userMap;
	public Map<String, ImageCommonDto> imageMap;
	public List<CommentDTO> list;
	public String lateCursorId;
}
