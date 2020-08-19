package com.wealoha.social.module;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.wealoha.social.beans.Feed;
import com.wealoha.social.beans.Result;
import com.wealoha.social.beans.User;
import com.wealoha.social.beans.Notify;
import com.wealoha.social.beans.NotifyResult;

/**
 * @author:sunkist
 * @see:
 * @since:
 * @description
 * @copyright wealoha.com
 * @Date:2015-2-12
 */
public class DynamicNotice {

	public List<NotifyItem> mNotifyItems = new ArrayList<NotifyItem>();

	/**
	 * @Description:根据不同的视图拼装不同的Item用来填充Item
	 * @param result
	 * @see:
	 * @since:
	 * @description
	 * @author: sunkist
	 * @date:2015-2-12
	 */
	public void assemblyDynamicItem(Result<NotifyResult> result) {

		List<Notify> notifies = result.data.list;
		Map<String, Feed> feedMap = result.data.postMap;
		Map<String, User> userMap = result.data.userMap;
		Map<String, Integer> likeCountMap = result.data.likeCountMap;
		Map<String, Integer> commentCountMap = result.data.commentCountMap;
		// 循环拼装数据
		for (int i = 0; i < notifies.size(); i++) {
			Notify notify = notifies.get(i);
			NotifyItem notifyItem = new NotifyItem();
			notifyItem.createTimeMillis = notify.createTimeMillis;
			notifyItem.updateTimeMillis = notify.updateTimeMillis;
			notifyItem.type = notify.type;

			int type = notifyItem.getType();
			// 这里面的for循环可以抽出来;
			switch (type) {
			// feed评论
			case Notify.FEED_COMMENT_VIEW_TYPE: {
				FeedCommentItem feedCommentItem = (FeedCommentItem) notifyItem;
				// 设置：回复feed的评论内容
				feedCommentItem.commentContent = notify.comment;
				// 设置：回复的replyUser
				List<String> userIds = notify.userIds;
				int userIdsSize = userIds.size();
				for (int j = 0; j < userIdsSize; j++) {
					String userId = userIds.get(j);
					feedCommentItem.replyUser = userMap.get(userId);
				}
				// 设置：根据PostId从Map中找到Feed对象
				String postId = notify.postId;
				feedCommentItem.mFeed = feedMap.get(postId);
				feedCommentItem.mFeed.commentCount = commentCountMap.get(postId);
				mNotifyItems.add(feedCommentItem);
			}
				break;
			// 新人气的通知
			case Notify.FEED_NEWALOHA_VIEW_TYPE: {
				AlohaItem alohaItem = (AlohaItem) notifyItem;
				// 添加喜欢的
				List<String> userIds = notify.userIds;
				int userIdsSize = userIds.size();
				for (int j = 0; j < userIdsSize; j++) {
					String userId = userIds.get(j);
					alohaItem.mAlohaUsers.add(userMap.get(userId));
				}
				// 新人气的个数;
				alohaItem.count = notify.count;
				mNotifyItems.add(alohaItem);
			}
				break;
			// 圈人的通知
			case Notify.FEED_POST_TAG_TYPE: {
				TagItem tagItem = (TagItem) notifyItem;

				mNotifyItems.add(tagItem);
			}
				break;
			// feed被点赞
			case Notify.FEED_PRAISE_VIEW_TYPE: {
				PraiseFeedItem praiseFeedItem = (PraiseFeedItem) notifyItem;
				// 被点赞的次数;
				praiseFeedItem.count = notify.count;
				// 填充攒的User
				List<String> userIds = notify.userIds;
				int userIdsSize = notify.userIds.size();
				for (int j = 0; j < userIdsSize; j++) {
					String userId = userIds.get(j);
					User user = userMap.get(userId);
					praiseFeedItem.mUsers.add(user);
				}
				// 找到被赞的Feed
				String postId = notify.postId;
				praiseFeedItem.mFeed = feedMap.get(postId);
				// 设置被评论的个数,添加在以前的Feed对象中;
				praiseFeedItem.mFeed.commentCount = commentCountMap.get(postId);
				praiseFeedItem.mFeed.likeCount = likeCountMap.get(postId);
				mNotifyItems.add(praiseFeedItem);
			}
				break;
			default:
				break;
			}

		}

	}

	/**
	 * @Description:清除所有的Item数据
	 * @see:
	 * @since:
	 * @description
	 * @author: sunkist
	 * @date:2015-2-13
	 */
	public void clearAllData() {

	}

	public Feed byPostIdGetFeed(String postId) {
		Feed feed = null;

		return feed;
	}

	public void byCommentIdGetCount() {

	}

}
