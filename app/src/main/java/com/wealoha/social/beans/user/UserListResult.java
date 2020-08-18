package com.wealoha.social.beans.user;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.wealoha.social.beans.Image;
import com.wealoha.social.beans.ResultData;
import com.wealoha.social.beans.User;
import com.wealoha.social.beans.imagemap.HasImageMap;

public class UserListResult extends ResultData implements Serializable, HasImageMap {

	/**
	 * @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么)
	 */
	private static final long serialVersionUID = 8456520717379285862L;
	public static final String TAG = UserListResult.class.getSimpleName();
	public List<User> list;
	public Map<String, Image> imageMap;
	public String nextCursorId;
	public boolean alohaGetLocked;
	public int alohaGetUnlockCount;

	@Override
	public Map<String, Image> getImageMap() {
		return imageMap;
	}
}
