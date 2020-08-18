package com.wealoha.social.beans.match;

import java.io.Serializable;
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
public class MatchData extends ResultData implements Serializable, HasImageMap {

	private static final long serialVersionUID = -7473243341042126058L;
	public static final String TAG = MatchData.class.getSimpleName();
	public List<User> list;
	/** 还需要多少秒才能刷下一批 */
	public int quotaResetSeconds;
	/** 每次刷一批的时间段长度 */
	public int quotaDurationSeconds;
	/** 可用的重置配额次数 */
	public int quotaReset;
	public Map<String, Image> imageMap;
	public Map<String, String> recommendSourceMap;

	@Override
	public Map<String, Image> getImageMap() {
		return imageMap;
	}
}
