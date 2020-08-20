package com.wealoha.social.api

import com.wealoha.social.beans.AuthData
import com.wealoha.social.beans.Result
import io.reactivex.Single
import kotlin.random.Random

class RxUserService {
    companion object {
        val shared = RxUserService()
    }

    fun login(mUserName: String, password: String): Single<Result<AuthData>> {
        return Single.create<Result<AuthData>> {
            Thread.sleep(500)
            if (Random.nextInt(0, 50) > 10) {
                /* success */
                it.onSuccess(
                    Result.success(AuthData.fake())
                )
            } else {
                it.onError(Throwable("我就让你出错你能咋地"))
            }
        }
    }
}