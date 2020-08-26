package com.wealoha.social.api

import android.text.TextUtils
import com.wealoha.social.api.BaseService.ServiceResultCallback
import com.wealoha.social.beans.FeedGetData
import com.wealoha.social.beans.Post
import com.wealoha.social.beans.Post.Companion.fromPostDTOList
import com.wealoha.social.beans.ApiResponse
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.client.Response
import javax.inject.Inject

class PostService : AbsBaseService<Post?>() {
    @JvmField
	@Inject
    var feed2API: ServerApi? = null
    override fun getList(
        callback: ServiceResultCallback<Post?>,
        cursor: String?,
        vararg args: Any?
    ) {
        feed2API!!.getPosts(
            cursor!!,
            AbsBaseService.COUNT,
            object : Callback<ApiResponse<FeedGetData>> {
                override fun failure(arg0: RetrofitError) {
                    callback.failer()
                }

                override fun success(apiResponse: ApiResponse<FeedGetData>?, arg1: Response) {
                    if (apiResponse != null && apiResponse.isOk) {
                        list.addAll( //
                            fromPostDTOList(
                                apiResponse.data!!.list,  //
                                apiResponse.data!!.userMap,  //
                                apiResponse.data!!.imageMap,  //
                                apiResponse.data!!.videoMap,  //
                                apiResponse.data!!.commentCountMap,  //
                                apiResponse.data!!.likeCountMap
                            )
                        )
                        callback.success(list)
                        cursorId = apiResponse.data!!.nextCursorId
                        if (TextUtils.isEmpty(cursorId)) {
                            callback.nomore()
                        }
                    } else {
                        callback.failer()
                    }
                }
            })
    }
}