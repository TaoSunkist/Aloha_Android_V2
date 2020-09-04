package com.wealoha.social.api

import com.wealoha.social.api.BaseListApiService.ApiListCallback
import com.wealoha.social.beans.ApiErrorCode.Companion.fromResult
import com.wealoha.social.beans.Direct
import com.wealoha.social.beans.FeedGetData
import com.wealoha.social.beans.Post
import com.wealoha.social.beans.ApiResponse
import com.wealoha.social.extension.observeOnMainThread
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.client.Response

class Profile2ListApiService : Feed2ListApiService() {


    override fun getList(
        cursor: String,
        count: Int,
        direct: Direct,
        userid: String,
        callback: ApiListCallback<Post?>
    ) {
        AlohaService.shared.getUserPosts(userid, cursor, count).observeOnMainThread(onSuccess = {
            if (it == null || !it.isOk) {
                callback.fail(fromResult(it), null)
            } else {
                callback.success(transResult2List(it.data!!, userid), it.data!!.nextCursorId)
            }
        }, onError = {
            callback.fail(null, RetrofitError.unexpectedError("", it))
        }, onTerminate = {
        }).addTo(compositeDisposable = CompositeDisposable())

        /*, object : Callback<ApiResponse<FeedGetData>> {
            override fun failure(error: RetrofitError) {
                callback.fail(null, error)
            }

            override fun success(apiResponse: ApiResponse<FeedGetData>?, arg1: Response) {
                if (apiResponse == null || !apiResponse.isOk) {
                    callback.fail(fromResult(apiResponse), null)
                } else {
                    callback.success(
                        transResult2List(apiResponse.data!!, userid),
                        apiResponse.data!!.nextCursorId
                    )
                }
            }
        }*/
    }
}