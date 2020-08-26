package com.wealoha.social.api

import com.wealoha.social.beans.AuthData
import com.wealoha.social.beans.ApiResponse
import io.reactivex.Single
import kotlin.random.Random

class RxUserService {
    companion object {
        val shared = RxUserService()
    }

    fun login(mUserName: String, password: String): Single<ApiResponse<AuthData>> {
        return Single.create<ApiResponse<AuthData>> {
            Thread.sleep(500)
            if (Random.nextInt(0, 50) > 10) {
                /* success */
                it.onSuccess(
                    ApiResponse.success(AuthData.fake())
                )
            } else {
                it.onError(Throwable("我就让你出错你能咋地"))
            }
        }
    }
}