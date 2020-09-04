package com.wealoha.social.api

import com.wealoha.social.api.BaseListApiService.ApiListCallback
import com.wealoha.social.beans.Direct
import com.wealoha.social.beans.FeedGetData
import com.wealoha.social.beans.Post
import com.wealoha.social.beans.ApiResponse
import com.wealoha.social.utils.XL
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.client.Response
import javax.inject.Inject

class SingletonFeedListApiService : Feed2ListApiService() {

    override fun getList(
        cursor: String,
        count: Int,
        direct: Direct,
        postId: String,
        callback: ApiListCallback<Post?>
    ) {
        feed2Api!!.singleFeed(postId, object : Callback<ApiResponse<FeedGetData>> {
            override fun failure(error: RetrofitError) {
                XL.i("Feed2Fragment", error.message)
                callback.fail(null, error)
            }

            override fun success(apiResponse: ApiResponse<FeedGetData>, arg1: Response) {
                val _result = ApiResponse.success(
                    FeedGetData.fake(
                        cursor = cursor,
                        direct = direct,
                        userID = postId,
                        count = count
                    )
                )
                callback.success(
                    transResult2List(_result.data!!, ""),
                    _result.data!!.nextCursorId
                )
            }
        })
    }

    companion object {
        val TAG = SingletonFeedListApiService::class.java.simpleName
    }
}