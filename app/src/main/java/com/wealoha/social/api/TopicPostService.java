package com.wealoha.social.api;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.text.TextUtils;

import com.wealoha.social.R;
import com.wealoha.social.api.comment.bean.PostComment;
import com.wealoha.social.api.comment.dto.Comment2DTO;
import com.wealoha.social.api.comment.dto.CommentDTO;
import com.wealoha.social.api.common.bean.Image;
import com.wealoha.social.api.common.bean.Video;
import com.wealoha.social.api.common.service.AbsBaseService;
import com.wealoha.social.beans.HashTagResultData;
import com.wealoha.social.beans.TopicPostResultData;
import com.wealoha.social.beans.UserTag;
import com.wealoha.social.beans.Post;
import com.wealoha.social.beans.TopicPost;
import com.wealoha.social.beans.TopicPosts;
import com.wealoha.social.beans.TopicPostDTO;
import com.wealoha.social.beans.HashTag;
import com.wealoha.social.beans.HashTagDTO;
import com.wealoha.social.beans.TopicPostTagDTO;
import com.wealoha.social.beans.User2;
import com.wealoha.social.beans.Result;
import com.wealoha.social.utils.XL;

public class TopicPostService extends AbsBaseService<TopicPost> {

	@Inject
	ServerApi postAPI;
	private TopicPosts mTopicPosts;

	public void getHashTagResult(final ServiceListResultCallback<HashTag> serviceListResultCallback) {
		postAPI.getHashTag(new Callback<Result<HashTagResultData>>() {

			@Override
			public void failure(RetrofitError retrofitError) {
				serviceListResultCallback.failer();
			}

			@Override
			public void success(Result<HashTagResultData> hashTagResult, Response arg1) {
				if (hashTagResult != null && hashTagResult.isOk()) {
					List<HashTag> hashTags = transHashTagDTO2HashTag(hashTagResult.data.list);
					serviceListResultCallback.success(hashTags);
				} else {
					serviceListResultCallback.failer();
				}
			}
		});
	}
	public void getResult(String tagname, final ServiceObjResultCallback<TopicPosts> callback) {
		if (loading) {
			return;
		}
		loading = true;

		String tempCursor = cursorId;
		if (FIRST_PAGE.equals(tempCursor)) {
			tempCursor = null;
			list.clear();
		} else if (TextUtils.isEmpty(tempCursor)) {
			loading=false;
			callback.nomore();
			return;
		}

		XL.d("failure", "tagname" + tagname + "-tempCursor=" + tempCursor);
		postAPI.getTopicPosts(tagname, tempCursor, COUNT, new Callback<Result<TopicPostResultData>>() {

			@Override
			public void failure(RetrofitError arg0) {
				XL.d("Topic_Refresh", "failure");
				callback.failer();
				loading = false;
			}

			@Override
			public void success(Result<TopicPostResultData> result, Response arg1) {
				if (mTopicPosts == null) {
					mTopicPosts = new TopicPosts();
				}
				if (result != null && result.isOk()) {
					// TopicPosts topicPosts = new TopicPosts();
					callback.beforeSuccess();
					List<Post> newPostsList = transTopicPosts2Posts(result.data.list);
					List<Post> hotPostsList = transTopicPosts2Posts(result.data.hot);
					if (FIRST_PAGE.equals(cursorId)) {// 首次拉取数据
						if (hotPostsList != null && hotPostsList.size() > 0) {
							list.add(getTopicTitleItem4Hot());
							list.addAll(transPostsList2GridList(hotPostsList));
						}

						if (newPostsList != null && newPostsList.size() > 0) {
							list.add(getTopicTitleItem4New());
							list.addAll(transPostsList2GridList(newPostsList));
						}
						HashTag hashTag = HashTag.fromDTO(result.data.hashtag);
						mTopicPosts.setHashTag(hashTag);
					} else {// 最新数据翻页
						list.addAll(transPostsList2GridList(newPostsList));
					}
					mTopicPosts.setPosts(list);
					callback.success(mTopicPosts);

					if (TextUtils.isEmpty(result.data.nextCursorId)) {
						callback.nomore();
					}
					cursorId = result.data.nextCursorId;
				} else {
					callback.failer();
				}
				loading = false;
			}
		});
	}

	private TopicPost getTopicTitleItem4Hot() {
		return getTopicItem(R.string.top_detail_hot_photo_str, TopicPost.TITLE_TYPE);
	}

	private TopicPost getTopicTitleItem4New() {
		return getTopicItem(R.string.topic_detail_new_photo_str, TopicPost.TITLE_TYPE);
	}

	private TopicPost getTopicItem(int strId, int itemtype) {
		TopicPost item = new TopicPost();
		// titleItem.setTitleItem(true);
		item.setItemType(itemtype);
		item.setTitleId(strId);
		return item;
	}

	/**
	 * 将post列表转换成{@link TopicPost}的3个一行结构
	 * 
	 * @return List<TopicPost>
	 */
	public List<TopicPost> transPostsList2GridList(List<Post> postsList) {
		if (postsList == null) {
			return null;
		}
		int tempSzie = postsList.size() % 3 == 0 ? 0 : 1;
		int size = tempSzie + postsList.size() / 3;
		List<TopicPost> topicPostsList = new ArrayList<>();
		TopicPost topicPost = null;
		for (int i = 0; i < size; i++) {
			topicPost = new TopicPost();
			for (int j = 0; j < 3; j++) {
				int index = i * 3 + j;
				if (postsList.size() - 1 < index) {
					topicPost.getPostsItem().add(null);
					topicPost.setItemFull(false);
					topicPost.setVacancyCount();
				} else {
					topicPost.getPostsItem().add(postsList.get(index));
				}
			}
			topicPost.setItemType(TopicPost.NORMAL_TYPE);
			topicPostsList.add(topicPost);
		}

		return topicPostsList;
	}

	/**
	 * 话题页获取的{@link TopicPostDTO}集合转换为渲染实图用的{@link Post}
	 * 
	 * @return void
	 */
	public List<Post> transTopicPosts2Posts(List<TopicPostDTO> postsDTO) {
		Post post = null;
		List<Post> postlist = null;
		if (postsDTO != null) {
			postlist = new ArrayList<>(postsDTO.size());
			for (TopicPostDTO tpd : postsDTO) {
				User2 user2 = User2.fromDTO(tpd.user, Image.fromDTO(tpd.user.avatarImage));
				List<UserTag> userTags = transPostTagDTO2PostTag(tpd.userTags);
				List<PostComment> recentCommentList = transComment2DTOListToPostCommentList(tpd.recentComment);

				post = new Post(tpd.postId,//
				tpd.type,//
				tpd.description,//
				tpd.createTimeMillis, //
				tpd.mine, tpd.liked,//
				tpd.tagMe,//
				tpd.venue,//
				tpd.venueId,//
				tpd.latitude,//
				tpd.longitude,//
				tpd.venueAbroad,//
						user2, //
				userTags,//
				Image.fromDTO(tpd.image),//
				Video.fromDTO(tpd.video),//
				tpd.commentCount,//
				tpd.praiseCount,//
				recentCommentList,//
				HashTag.fromDTO(tpd.hashtag),//
				tpd.hasMoreComment);
				postlist.add(post);
			}
		}
		return postlist;
	}

	/**
	 * {@link CommentDTO} list 转换为渲染视图的 {@link PostComment} List
	 * 
	 * @param dtoList
	 * @param userMap
	 * @param imageMap
	 * @return void
	 */
	private List<PostComment> transComment2DTOListToPostCommentList(List<Comment2DTO> dtoList) {
		if (dtoList == null) {
			return null;
		}
		List<PostComment> postCommentList = new ArrayList<>();
		for (Comment2DTO dto : dtoList) {
			User2 user2 = User2.fromDTO(dto.user, Image.fromDTO(dto.user.avatarImage));
			User2 replyUser2 = User2.fromDTO(dto.replyUser, Image.fromDTO(dto.replyUser.avatarImage));
			PostComment postComment = PostComment.fromComment2DTO(dto, replyUser2, user2);
			postCommentList.add(postComment);
		}
		return postCommentList;
	}

	public List<UserTag> transPostTagDTO2PostTag(List<TopicPostTagDTO> tptag) {
		List<UserTag> tags = null;
		UserTag tag = null;
		if (tptag != null) {
			tags = new ArrayList<>(tptag.size());
			for (TopicPostTagDTO tpt : tptag) {
				tag = new UserTag(tpt.tagAnchorX,//
				tpt.tagAnchorY,//
				tpt.tagCenterX,//
				tpt.tagCenterY,//
				User2.fromDTO(tpt.tagUser, Image.fromDTO(tpt.tagUser.avatarImage)));
				tags.add(tag);
			}
		}
		return tags;
	}

	public List<HashTag> transHashTagDTO2HashTag(List<HashTagDTO> list) {
		if (list == null) {
			return null;
		}
		List<HashTag> hashTags = new ArrayList<>();
		for (HashTagDTO hashTagDTO : list) {
			HashTag hashTag = HashTag.fromDTO(hashTagDTO);
			hashTags.add(hashTag);
		}
		return hashTags;
	}

	@Override
	public void getList(ServiceResultCallback<TopicPost> callback, String cursorId, Object... args) {
		// TODO Auto-generated method stub

	}
}
