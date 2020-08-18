package com.wealoha.social.beans;

import java.util.List;
import java.util.Map;

import com.wealoha.social.beans.feed.UserTags;
import com.wealoha.social.beans.feed.Video;
import com.wealoha.social.beans.imagemap.HasImageMap;

/**
 * @author javamonk
 * @createTime 14-10-13 AM10:29
 */
public class FeedResult extends ResultData implements HasImageMap {

	public List<Feed> list;

	public Map<String, Image> imageMap;

	public Map<String, Integer> commentCountMap;

	public Map<String, Integer> likeCountMap;

	public Map<String, User> userMap;
	public Map<String, Video> videoMap;
	public String nextCursorId;

	/**
	 * @Title: buildUserTags
	 * @Description: 重新组装usertags 把user 对象加入usertags中, 判断feed中是否有我的tag
	 * @param users
	 * @param feeds
	 * @return void
	 * @throws
	 */
	public void resetFeed(User currentUser) {
		if (list == null || userMap == null) {
			return;
		}
		for (Feed feed : list) {
			if (feed.userTags == null || feed.userTags.size() <= 0) {
				continue;
			}
			for (UserTags userTag : feed.userTags) {
				// user 对象加入usertags中
				userTag.tagUserName = userMap.get(userTag.tagUserId).name;
				userTag.tagUser = userMap.get(userTag.tagUserId);
				// 是否有我的tag
				if (currentUser != null && userTag.tagUserId.equals(currentUser.id)) {
					feed.tagMe = true;
					userTag.tagMe = true;
				}
			}
		}
	}

	@Override
	public Map<String, Image> getImageMap() {
		return imageMap;
	}
}
