package com.wealoha.social.beans.search

import com.wealoha.social.beans.Result
import com.wealoha.social.impl.ServerUrlImpl
import retrofit.Callback
import retrofit.http.Field
import retrofit.http.FormUrlEncoded
import retrofit.http.GET
import retrofit.http.POST

interface FindYouService {
    /**
     * 通知列表
     *
     * @param cursor
     * @param count
     * @return
     */
    @FormUrlEncoded
    @POST(ServerUrlImpl.FIND_YOU)
    fun findYou(
        @Field("keyword") keyword: String?,
        @Field("count") count: Int?,
        callback: Callback<Result<FindYouResult?>?>?
    )

    /**
     * 默认圈人列表
     *
     * @param cursor
     * @param count
     * @return
     */
    @GET(ServerUrlImpl.TAG_SUGGEST)
    fun defaultTagUsers(callback: Callback<Result<FindYouResult?>?>?)
}