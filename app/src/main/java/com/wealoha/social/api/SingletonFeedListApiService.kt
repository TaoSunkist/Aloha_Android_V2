package com.wealoha.social.api

import com.wealoha.social.api.BaseListApiService.ApiListCallback
import com.wealoha.social.beans.Direct
import com.wealoha.social.beans.FeedGetData
import com.wealoha.social.beans.Post
import com.wealoha.social.beans.ApiResponse
import com.wealoha.social.extension.observeOnMainThread
import com.wealoha.social.utils.XL
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
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
        AlohaService.shared.singleFeed(postId).observeOnMainThread(onSuccess = {
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
        }, onError = {
            callback.fail(null, null)
        }).addTo(compositeDisposable = CompositeDisposable())
    }

    companion object {
        val TAG = SingletonFeedListApiService::class.java.simpleName
    }
}