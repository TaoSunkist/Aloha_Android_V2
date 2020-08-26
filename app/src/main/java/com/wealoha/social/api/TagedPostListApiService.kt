package com.wealoha.social.api

import com.wealoha.social.api.BaseListApiService.ApiListCallback
import com.wealoha.social.beans.ApiErrorCode.Companion.fromResult
import com.wealoha.social.beans.Direct
import com.wealoha.social.beans.FeedGetData
import com.wealoha.social.beans.Post
import com.wealoha.social.beans.ApiResponse
import com.wealoha.social.utils.XL
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.client.Response

class TagedPostListApiService : Feed2ListApiService() {
    override fun getList(
        cursor: String,
        count: Int,
        direct: Direct,
        userid: String,
        callback: ApiListCallback<Post?>
    ) {
        feed2Api!!.getTagedList(cursor!!, count, object : Callback<ApiResponse<FeedGetData>> {
            override fun failure(error: RetrofitError) {
                XL.i("Feed2Fragment", "service: faile--" + error.message)
                callback.fail(null, error)
            }

            override fun success(apiResponse: ApiResponse<FeedGetData>?, arg1: Response) {
                XL.i("Feed2Fragment", "service: success--")
                if (apiResponse == null || !apiResponse.isOk) {
                    callback.fail(fromResult(apiResponse), null)
                } else {
                    callback.success(
                        transResult2List(apiResponse.data!!, userid),
                        apiResponse.data!!.nextCursorId
                    )
                }
            }
        })
    }
}