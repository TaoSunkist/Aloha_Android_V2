package com.wealoha.social.api

import com.wealoha.social.api.BaseListApiService.ApiListCallback
import com.wealoha.social.beans.ApiErrorCode.Companion.fromResult
import com.wealoha.social.beans.Direct
import com.wealoha.social.beans.FeedGetData
import com.wealoha.social.beans.Post
import com.wealoha.social.beans.Result
import com.wealoha.social.inject.Injector
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.client.Response
import javax.inject.Inject

class Profile2ListApiService : Feed2ListApiService() {


    override fun getList(
        cursor: String,
        count: Int,
        direct: Direct,
        userid: String,
        callback: ApiListCallback<Post?>
    ) {
        feed2Api!!.getUserPosts(userid!!, cursor!!, count, object : Callback<Result<FeedGetData>> {
            override fun failure(error: RetrofitError) {
                callback.fail(null, error)
            }

            override fun success(result: Result<FeedGetData>?, arg1: Response) {
                if (result == null || !result.isOk) {
                    callback.fail(fromResult(result), null)
                } else {
                    callback.success(
                        transResult2List(result.data!!, userid),
                        result.data!!.nextCursorId
                    )
                }
            }
        })
    }
}