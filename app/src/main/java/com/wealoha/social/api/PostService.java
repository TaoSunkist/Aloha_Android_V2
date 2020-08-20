package com.wealoha.social.api;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.text.TextUtils;

import com.wealoha.social.api.ServerApi;
import com.wealoha.social.api.common.service.AbsBaseService;
import com.wealoha.social.beans.FeedGetData;
import com.wealoha.social.beans.Post;
import com.wealoha.social.beans.Result;

public class PostService extends AbsBaseService<Post> {

	@Inject
	ServerApi feed2API;

	@Override
	public void getList(final ServiceResultCallback<Post> callback, final String cursor, Object... args) {
		feed2API.getPosts(cursor, COUNT, new Callback<Result<FeedGetData>>() {

			@Override
			public void failure(RetrofitError arg0) {
				callback.failer();
			}

			@Override
			public void success(Result<FeedGetData> result, Response arg1) {
				if (result != null && result.isOk()) {
					list.addAll(//
					Post.fromPostDTOList(result.data.list, //
											result.data.userMap, //
											result.data.imageMap,//
											result.data.videoMap, //
											result.data.commentCountMap,//
											result.data.likeCountMap));
					callback.success(list);

					cursorId = result.data.nextCursorId;
					if (TextUtils.isEmpty(cursorId)) {
						callback.nomore();
					}
				} else {
					callback.failer();
				}
			}
		});
	}
}
