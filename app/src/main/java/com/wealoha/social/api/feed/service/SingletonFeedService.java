package com.wealoha.social.api.feed.service;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import com.wealoha.social.api.common.ApiErrorCode;
import com.wealoha.social.api.common.Direct;
import com.wealoha.social.api.feed.Feed2API;
import com.wealoha.social.api.feed.FeedGetData;
import com.wealoha.social.api.post.bean.Post;
import com.wealoha.social.beans.Result;
import com.wealoha.social.utils.XL;

public class SingletonFeedService extends Feed2Service {

	@Inject
	Feed2API feed2Api;
	public final static String TAG = SingletonFeedService.class.getSimpleName();

	@Override
	public void getList(String cursor, int count, Direct direct, String postId, final com.wealoha.social.api.common.BaseListApiService.ApiListCallback<Post> callback) {
		feed2Api.singleFeed(postId, new Callback<Result<FeedGetData>>() {

			@Override
			public void failure(RetrofitError error) {
				XL.i("Feed2Fragment", error.getMessage());
				callback.fail(null, error);
			}

			@Override
			public void success(Result<FeedGetData> result, Response arg1) {
				if (result == null || !result.isOk()) {
					callback.fail(ApiErrorCode.fromResult(result), null);
				} else {
					callback.success(transResult2List(result.data, null), result.data.nextCursorId);
				}
			}
		});
	}

}
