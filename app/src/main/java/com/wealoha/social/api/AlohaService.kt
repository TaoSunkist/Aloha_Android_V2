package com.wealoha.social.api

import android.util.Log
import com.wealoha.social.beans.ApiResponse
import com.wealoha.social.beans.MatchData
import io.reactivex.Single

class AlohaService {
    companion object {
        val shared = AlohaService()
    }


    fun findRandom(
        latitude: Double = 0.0,
        longitude: Double = 0.0
    ): Single<ApiResponse<MatchData>> {
        return Single.create<ApiResponse<MatchData>> {
            it.onSuccess(ApiResponse.success(MatchData.fake()))
        }
    }

    fun findWithResetQuota(
        latitude: Double,
        longitude: Double,
        b: Boolean
    ): Single<ApiResponse<MatchData>> {
        return Single.create<ApiResponse<MatchData>> {
            it.onSuccess(ApiResponse.success(MatchData.fake()))
        }
    }
}