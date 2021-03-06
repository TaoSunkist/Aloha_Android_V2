package com.wealoha.social.api

import android.util.Log
import com.wealoha.social.beans.*
import com.wealoha.social.beans.message.InboxSessionResult
import io.reactivex.Single
import retrofit.Callback
import retrofit.http.Field
import retrofit.http.FormUrlEncoded
import retrofit.http.POST

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

    fun sessions(mNextCursorId: String, i: Int): Single<ApiResponse<InboxSessionResult>> {
        return Single.create<ApiResponse<InboxSessionResult>> {
            it.onSuccess(ApiResponse.success(InboxSessionResult.fake()))
        }
    }

    fun getUserPosts(
        userId: String,
        cursor: String,
        count: Int
    ): Single<ApiResponse<FeedGetData>> {
        return Single.create<ApiResponse<FeedGetData>> {
            it.onSuccess(
                ApiResponse.success(
                    FeedGetData.fake(
                        userID = userId,
                        cursor = cursor,
                        count = count,
                        direct = Direct.Early
                    )
                )
            )
        }
    }

    fun likeFeedPersons(
        postId: String,
        cursor: String,
        s: String
    ): Single<ApiResponse<UserListResult>> {
        return Single.create<ApiResponse<UserListResult>> {
            it.onSuccess(ApiResponse.success(UserListResult.fake()))
        }
    }

    fun singleFeed(
        postId: String
    ): Single<ApiResponse<FeedGetData>> {
        return Single.create<ApiResponse<FeedGetData>> {
            it.onSuccess(
                ApiResponse.success(
                    FeedGetData.fake(
                        cursor = "",
                        count = 1,
                        direct = Direct.Early,
                        userID = ""
                    )
                )
            )
        }
    }

    fun postComment(
        postId: String,
        replyUserId: String,
        comment: String,
        callback: Callback<ApiResponse<Comment2GetData>>
    ): Single<ApiResponse<Comment2GetData>> {
        return Single.create<ApiResponse<Comment2GetData>> {
            it.onSuccess(
                ApiResponse.success(
                    Comment2GetData.fake()
                )
            )
        }
    }

    fun deleteComment(
        postId: String,
        commentId: String
    ): Single<ApiResponse<ResultData>> {
        return Single.create<ApiResponse<ResultData>> {
            it.onSuccess(
                ApiResponse.success(
                    ResultData(error = 200)
                )
            )
        }
    }
}