package com.wealoha.social.api;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.content.Context;
import android.util.Log;

import com.squareup.picasso.Picasso;
import com.wealoha.social.api.comment.bean.PostComment;
import com.wealoha.social.api.common.AbsBaseService;
import com.wealoha.social.api.common.ApiErrorCode;
import com.wealoha.social.api.common.Direct;
import com.wealoha.social.api.common.bean.Image;
import com.wealoha.social.api.common.bean.Video;
import com.wealoha.social.beans.FeedGetData;
import com.wealoha.social.beans.FeedType;
import com.wealoha.social.beans.UserListGetData;
import com.wealoha.social.beans.UserTag;
import com.wealoha.social.beans.Post;
import com.wealoha.social.beans.PostDTO;
import com.wealoha.social.api.topic.bean.HashTag;
import com.wealoha.social.api.user.bean.User;
import com.wealoha.social.beans.Result;
import com.wealoha.social.beans.ResultData;
import com.wealoha.social.commons.GlobalConstants.ImageSize;
import com.wealoha.social.inject.Injector;
import com.wealoha.social.utils.XL;

public class Feed2Service extends AbsBaseService<Post, String> {

	@Inject
	ServerApi feed2Api;
	@Inject
	Context context;
	@Inject
	Picasso picasso;

	public final static String TAG = Feed2Service.class.getSimpleName();
	protected List<Post> postList;

	public Feed2Service() {
		Injector.inject(this);
	}

	@Override
	public void getList(String cursor, int count, Direct direct, final String userid, final com.wealoha.social.api.common.BaseListApiService.ApiListCallback<Post> callback) {
		feed2Api.getPosts(cursor, count, new Callback<Result<FeedGetData>>() {

			@Override
			public void failure(RetrofitError error) {
				XL.i("Feed2Fragment", "service: faile--" + error.getMessage());
				callback.fail(null, error);
			}

			@Override
			public void success(Result<FeedGetData> result, Response arg1) {
				XL.i("Feed2Fragment", "service: success--");
				if (result == null || !result.isOk()) {
					callback.fail(ApiErrorCode.fromResult(result), null);
				} else {
					callback.success(transResult2List(result.data, userid), result.data.nextCursorId);
				}
			}
		});
	}

	/***
	 * 赞post 的用户列表
	 * 
	 * @param cursor
	 * @param count
	 * @param postid
	 * @param callback
	 * @return void
	 */
	public void getPraiseList(String cursor, int count, String postid, final ApiCallback<List<User>> callback) {
		feed2Api.getPraiseList(postid, cursor, count, new Callback<Result<UserListGetData>>() {

			@Override
			public void failure(RetrofitError error) {
				callback.fail(null, error);
			}

			@Override
			public void success(Result<UserListGetData> result, Response arg1) {
				if (result == null || !result.isOk()) {
					callback.fail(ApiErrorCode.fromResult(result), null);
				} else {
					callback.success(transUserListGetData2List(result.data));
				}
			}
		});
	}

	private List<User> transUserListGetData2List(UserListGetData userListGetData) {
		if (userListGetData == null || userListGetData.list == null) {
			return null;
		}
		List<User> userList = new ArrayList<User>(userListGetData.list.size());
		userList = getUsers(userListGetData.list, userListGetData.imageMap);
		return userList;
	}

	protected List<Post> transResult2List(FeedGetData result, String currentUserid) {
		if (result == null || result.list == null) {
			XL.i(TAG, "post list is null");
			return null;
		}
		List<Post> postList = new ArrayList<Post>(result.list.size());
		for (PostDTO postDto : result.list) {

			FeedType type = FeedType.fromValue(postDto.type);
			if (type == null) {
				XL.d(TAG, "不支持的通知类型: " + postDto.type);
				continue;
			}

			User user = getUser(postDto.userId, result.userMap, result.imageMap);
			List<UserTag> userTagList = gerUserTagList(postDto.userTags, result.userMap, result.imageMap);
			Image image = Image.fromDTO(result.imageMap.get(postDto.imageId));
			Video video = null;
			if (result.videoMap != null) {
				video = Video.fromDTO(result.videoMap.get(postDto.videoId));
			}

			List<PostComment> recentCommentList = transCommentDTOList2PostCommentList(postDto.recentComments, result.userMap, result.imageMap);
			Post post = new Post(//
			postDto.postId,//
			type,//
			postDto.description,//
			postDto.createTimeMillis, //
			postDto.mine,//
			postDto.liked,//
			Post.hasTagForMe(userTagList, currentUserid),//
			postDto.venue,//
			postDto.venueId,//
			postDto.latitude, //
			postDto.longitude,//
			postDto.venueAbroad, //
			user, //
			userTagList,//
			image, //
			video, //
			result.commentCountMap.get(postDto.postId),//
			result.likeCountMap.get(postDto.postId),//
			recentCommentList,//
			HashTag.fromDTO(postDto.hashtag),//
			postDto.hasMoreComment);

			postList.add(post);
		}
		return postList;
	}

	@Override
	public void setAdapterListCallback(com.wealoha.social.api.common.BaseListApiService.AdapterListDataCallback<Post> callback) {
		super.setAdapterListCallback(callback);
		postList = callback.getListData();
	}

	@Override
	public void fetchPhoto(int firstVisibleItem, int totalItemCount, boolean direction, int mScreenWidth) {
		if (postList == null || postList.size() == 0) {
			return;
		}
		int first = 0;
		int end = 0;
		// 正向
		if (direction) {
			first = firstVisibleItem;
			if ((firstVisibleItem + 10) > totalItemCount) {
				end = totalItemCount;
			} else {
				end = firstVisibleItem + 10;
			}
		} else {
			end = firstVisibleItem;
			if ((firstVisibleItem - 10) < 0) {
				first = 0;
			} else {
				first = firstVisibleItem - 10;
			}
		}
		Log.i("LOAD_MEMORY", first + "=====" + end + "=====" + firstVisibleItem);
		end = end > postList.size() - 1 ? postList.size() - 1 : end;
		for (int i = first; i < end - 2; i++) {
			Log.i("LOAD_MEMORY", "++++++" + i);
			Post post = postList.get(i);
			picasso.load(post.getImage().getUrlSquare(mScreenWidth)).fetch();
			picasso.load(post.getUser().getAvatarImage().getUrlSquare(ImageSize.AVATAR_ROUND_SMALL)).fetch();
		}
	}

	/***
	 * 赞
	 * 
	 * @param postId
	 * @param callback
	 * @return void
	 */
	public void praiseFeed(String postId, final com.wealoha.social.api.common.BaseListApiService.NoResultCallback callback) {
		feed2Api.praisePost(postId, new Callback<Result<ResultData>>() {

			@Override
			public void success(Result<ResultData> result, Response arg1) {
				if (result == null || !result.isOk()) {
					callback.fail(ApiErrorCode.fromResult(result), null);
				} else {
					callback.success();
				}
			}

			@Override
			public void failure(RetrofitError error) {
				callback.fail(null, error);
			}
		});
	}

	/***
	 * 取消赞
	 * 
	 * @param postid
	 * @param callback
	 * @return void
	 */
	public void canclePraiseFeed(String postid, final com.wealoha.social.api.common.BaseListApiService.NoResultCallback callback) {
		feed2Api.dislikePost(postid, new Callback<Result<ResultData>>() {

			@Override
			public void success(Result<ResultData> result, Response arg1) {
				if (result == null || !result.isOk()) {
					callback.fail(ApiErrorCode.fromResult(result), null);
				} else {
					callback.success();
				}
			}

			@Override
			public void failure(RetrofitError error) {
				callback.fail(null, error);
			}
		});
	}

	/***
	 * 删除post
	 * 
	 * @param postId
	 * @param callback
	 * @return void
	 */
	public void deletePost(String postId, final com.wealoha.social.api.common.BaseListApiService.NoResultCallback callback) {
		feed2Api.deletePost(postId, new Callback<Result<ResultData>>() {

			@Override
			public void success(Result<ResultData> result, Response arg1) {
				if (result == null || !result.isOk()) {
					callback.fail(ApiErrorCode.fromResult(result), null);
				} else {
					callback.success();
				}
			}

			@Override
			public void failure(RetrofitError error) {
				callback.fail(null, error);
			}
		});
	}

	/***
	 * 删除post
	 * 
	 * @param postId
	 * @param callback
	 * @return void
	 */
	public void reportPost(String postId, final com.wealoha.social.api.common.BaseListApiService.NoResultCallback callback) {
		feed2Api.reportFeed(postId, null, null, new Callback<Result<ResultData>>() {

			@Override
			public void success(Result<ResultData> result, Response arg1) {
				if (result == null || !result.isOk()) {
					callback.fail(ApiErrorCode.fromResult(result), null);
				} else {
					callback.success();
				}
			}

			@Override
			public void failure(RetrofitError error) {
				callback.fail(null, error);
			}
		});
	}

	/***
	 * 移除标签
	 * 
	 * @param postId
	 * @param callback
	 * @return void
	 */
	public void removeTag(String postId, String tagUserId, final com.wealoha.social.api.common.BaseListApiService.NoResultCallback callback) {
		feed2Api.removeTag(postId, tagUserId, new Callback<Result<ResultData>>() {

			@Override
			public void success(Result<ResultData> result, Response arg1) {
				if (result == null || !result.isOk()) {
					callback.fail(ApiErrorCode.fromResult(result), null);
				} else {
					callback.success();
				}
			}

			@Override
			public void failure(RetrofitError error) {
				callback.fail(null, error);
			}
		});
	}

	/***
	 * 判断数据是否为空
	 * 
	 * @return
	 * @return boolean
	 */
	public boolean isDataEmpty() {
		if (postList == null || postList.size() == 0) {
			return true;
		} else {
			return false;
		}
	}

}
