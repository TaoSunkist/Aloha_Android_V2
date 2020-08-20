package com.wealoha.social.api;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import com.wealoha.social.beans.ApiErrorCode;
import com.wealoha.social.beans.Direct;
import com.wealoha.social.beans.FeedGetData;
import com.wealoha.social.beans.Post;
import com.wealoha.social.beans.Result;

public class Profile2Service extends Feed2Service {

	@Inject
	ServerApi feed2Api;

	public Profile2Service() {
		super();
	}

	@Override
	public void getList(String cursor, int count, Direct direct, final String userid, final BaseListApiService.ApiListCallback<Post> callback) {
		feed2Api.getUserPosts(userid, cursor, count, new Callback<Result<FeedGetData>>() {

			@Override
			public void failure(RetrofitError error) {
				callback.fail(null, error);
			}

			@Override
			public void success(Result<FeedGetData> result, Response arg1) {
				if (result == null || !result.isOk()) {
					callback.fail(ApiErrorCode.fromResult(result), null);
				} else {
					callback.success(transResult2List(result.data, userid), result.data.nextCursorId);
				}
			}
		});
	}
}
