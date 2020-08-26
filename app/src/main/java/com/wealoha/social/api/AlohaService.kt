package com.wealoha.social.api

import android.util.Log
import com.wealoha.social.beans.MatchData
import io.reactivex.Single

class AlohaService {
    companion object {
        val shared = AlohaService()
    }


    fun findRandom(
        latitude: Double = 0.0,
        longitude: Double = 0.0
    ): Single<com.wealoha.social.beans.ApiResponse<MatchData>> {
        Log.i("taohui","111")
        return Single.create<com.wealoha.social.beans.ApiResponse<MatchData>> {
            it.onSuccess(com.wealoha.social.beans.ApiResponse.success(MatchData.fake()))
        }
    }
}