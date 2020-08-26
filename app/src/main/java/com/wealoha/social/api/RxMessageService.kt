package com.wealoha.social.api

class RxMessageService {
    companion object {
        val shared = RxMessageService()
    }

//    fun sessions(
//        cursor: String,
//        count: Int
//    ): Single<Result<InboxSessionResult>> {
//        return Single.create<Result<InboxSessionResult>> {
//            Thread.sleep(500)
//            if (Random.nextInt(0, 50) > 10) {
//                /* success */
//                it.onSuccess(
//                    Result.success(InboxSessionResult.fake())
//                )
//            } else {
//                it.onError(Throwable("我就让你出错你能咋地"))
//            }
//        }
//    }

}