package com.wealoha.social.beans;

import java.util.List;
import java.util.Map;

import com.wealoha.social.beans.imagemap.HasImageMap;

public class BlackListResult extends ResultData implements HasImageMap {

	public List<User> list;
	public String nextCursorId;
	private Map<String, Image> imageMap;

	@Override
	public Map<String, Image> getImageMap() {
		return imageMap;
	}
}
