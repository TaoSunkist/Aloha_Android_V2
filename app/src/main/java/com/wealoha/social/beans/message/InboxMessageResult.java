package com.wealoha.social.beans.message;

import java.util.List;
import java.util.Map;

import com.wealoha.social.beans.Image;
import com.wealoha.social.beans.ResultData;
import com.wealoha.social.beans.User;
import com.wealoha.social.beans.imagemap.HasImageMap;

/**
 * Match调用结果，下一批待匹配的用户或者错误
 * 
 * @author javamonk
 * @see
 * @since
 * @date 2014-10-29 下午4:44:17
 */
public class InboxMessageResult extends ResultData implements HasImageMap {

	public String nextCursorId;
	public String messageId;
	public User toUser;
	public User user;
	public List<Message> list;
	public Map<String, Image> imageMap;

	@Override
	public Map<String, Image> getImageMap() {
		return imageMap;
	}
}
