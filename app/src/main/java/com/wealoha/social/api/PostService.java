package com.wealoha.social.api;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.text.TextUtils;

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
					Post.fromPostDTOList(result.getData().getList(), //
											result.getData().getUserMap(), //
											result.getData().getImageMap(),//
											result.getData().getVideoMap(), //
											result.getData().getCommentCountMap(),//
											result.getData().getLikeCountMap()));
					callback.success(list);

					cursorId = result.getData().getNextCursorId();
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
