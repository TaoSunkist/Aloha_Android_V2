package com.wealoha.social.api;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import com.wealoha.social.beans.ApiErrorCode;
import com.wealoha.social.beans.Direct;
import com.wealoha.social.beans.Post;
import com.wealoha.social.beans.FeedGetData;
import com.wealoha.social.beans.Result;
import com.wealoha.social.utils.XL;

public class TagedPostService extends Feed2Service {

    @Inject
    ServerApi feed2API;

    @Override
    public void getList(String cursor, int count, Direct direct, final String userid, final BaseListApiService.ApiListCallback<Post> callback) {
        feed2API.getTagedList(cursor, count, new Callback<Result<FeedGetData>>() {

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
                    callback.success(transResult2List(result.getData(), userid), result.getData().getNextCursorId());
                }
            }
        });
    }

}
