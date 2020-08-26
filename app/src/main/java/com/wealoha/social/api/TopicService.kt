package com.wealoha.social.api

import android.content.Context
import com.wealoha.social.AppApplication
import com.wealoha.social.api.BaseService.ServiceResultCallback
import com.wealoha.social.beans.HashTag
import com.wealoha.social.beans.HashTagDTO
import com.wealoha.social.beans.Result
import com.wealoha.social.beans.TopicData
import com.wealoha.social.inject.Injector
import com.wealoha.social.utils.AMapUtil
import com.wealoha.social.utils.AMapUtil.LocationCallback
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.client.Response
import javax.inject.Inject

class TopicService : BaseService<HashTagDTO?> {
    @JvmField
    @Inject
    var topicAPI: ServerApi? = null

    @JvmField
    @Inject
    var context: Context? = null
    var app: AppApplication
    fun getTopic(callback: ServiceResultCallback<HashTag?>?) {
        AMapUtil().getLocation(context, object : LocationCallback {
            override fun locaSuccess() {
                getTopicFromServer(app.locationXY[0], app.locationXY[1], callback)
            }

            override fun locaError() {
                getTopicFromServer(app.locationXY[0], app.locationXY[1], callback)
            }
        })
    }

    fun getTopicFromServer(
        latitude: Double?,
        longitude: Double?,
        callback: ServiceResultCallback<HashTag?>?
    ) {
        topicAPI!!.getTopic(latitude, longitude, object : Callback<Result<TopicData>> {
            override fun success(result: Result<TopicData>, arg1: Response) {
                transHashTagDTO2HashTag(result.data!!.tag)
                // callback.
            }

            override fun failure(arg0: RetrofitError) {}
        })
    }

    private fun transHashTagDTO2HashTag(htd: List<HashTagDTO>?) {}

    init {
        Injector.inject(this)
        app = AppApplication.getInstance()
    }
}