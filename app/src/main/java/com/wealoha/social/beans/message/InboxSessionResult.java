package com.wealoha.social.beans.message;

import java.util.List;
import java.util.Map;

import com.wealoha.social.beans.Image;
import com.wealoha.social.beans.ResultData;
import com.wealoha.social.beans.imagemap.HasImageMap;

/**
 * Created by walker on 14-3-25.
 */
public class InboxSessionResult extends ResultData implements HasImageMap {

	// Success
	public String nextCursorId;
	public List<InboxSession> list;
	public Map<String, Message> newMessageMap;
	public Map<String, Image> imageMap;

	@Override
	public Map<String, Image> getImageMap() {
		return imageMap;
	}

}
