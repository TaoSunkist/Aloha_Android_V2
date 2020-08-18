package com.wealoha.social.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.wealoha.social.beans.feed.Feed;
import com.wealoha.social.beans.imagemap.HasImageMap;

/**
 * Created by walker on 14-3-25.
 */
public class ProfileViewResult extends ResultData implements HasImageMap {

	public String nextCursorId;
	public HashMap<String, Integer> commentCountMap;
	public HashMap<String, Integer> likCountMap;
	public HashMap<String, Object> userMap;
	public ArrayList<Feed> list;
	public Map<String, Image> imageMap;

	@Override
	public Map<String, Image> getImageMap() {
		return imageMap;
	}
}
