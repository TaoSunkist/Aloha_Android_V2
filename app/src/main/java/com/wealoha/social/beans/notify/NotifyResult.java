package com.wealoha.social.beans.notify;

import java.util.List;
import java.util.Map;

import com.wealoha.social.beans.Feed;
import com.wealoha.social.beans.Image;
import com.wealoha.social.beans.ResultData;
import com.wealoha.social.beans.User;
import com.wealoha.social.beans.imagemap.HasImageMap;

public class NotifyResult extends ResultData implements HasImageMap {

	public String nextCursorId;
	public Map<String, Feed> postMap;
	public Map<String, Integer> commentCountMap;
	public Map<String, Integer> likeCountMap;
	public Map<String, User> userMap;
	public Map<String, Image> imageMap;
	public List<Notify> list;

	@Override
	public Map<String, Image> getImageMap() {
		return imageMap;
	}
}
