package com.wealoha.social.api.comment;

import java.util.List;
import java.util.Map;

import com.wealoha.social.api.comment.dto.CommentDTO;
import com.wealoha.social.api.common.dto.ImageDTO;
import com.wealoha.social.beans.UserDTO;
import com.wealoha.social.beans.ResultData;

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
	public Map<String, ImageDTO> imageMap;
	public List<CommentDTO> list;
	public String lateCursorId;
}
