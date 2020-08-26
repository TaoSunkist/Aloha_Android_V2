package com.wealoha.social.api

import com.wealoha.social.api.BaseListApiService.ApiListCallback
import com.wealoha.social.beans.ApiErrorCode.Companion.fromResult
import com.wealoha.social.beans.Direct
import com.wealoha.social.beans.FeedGetData
import com.wealoha.social.beans.Post
import com.wealoha.social.beans.Result
import com.wealoha.social.utils.XL
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.client.Response
import javax.inject.Inject

class PraisedPostListApiService : Feed2ListApiService() {
    @JvmField
	@Inject
    var feed2API: ServerApi? = null
    override fun getList(
        cursor: String,
        count: Int,
        direct: Direct,
        userid: String,
        callback: ApiListCallback<Post?>
    ) {
        feed2API!!.getPraisedList(cursor, count, object : Callback<Result<FeedGetData>> {
            override fun failure(error: RetrofitError) {
                XL.i("Feed2Fragment", "service: faile--" + error.message)
                callback.fail(null, error)
            }

            override fun success(result: Result<FeedGetData>?, arg1: Response) {
                XL.i("Feed2Fragment", "service: success--")
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