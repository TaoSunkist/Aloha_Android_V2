package com.wealoha.social.beans.instagram

import com.wealoha.social.beans.AuthData
import com.wealoha.social.beans.Result
import com.wealoha.social.impl.ServerUrlImpl
import retrofit.Callback
import retrofit.http.Field
import retrofit.http.FormUrlEncoded
import retrofit.http.POST

interface InstagramService {
    @FormUrlEncoded
    @POST(ServerUrlImpl.POST_INSTAGRAM_TOKEN)
    fun postToken(
        @Field("uid") uid: String?,  //
        @Field("accessToken") accessToken: String?,  //
        @Field("month") month: Int?,  //
        callback: Callback<Result<AuthData?>?>?
    )

    @POST(ServerUrlImpl.UNBIND_INSTAGRAM)
    fun unbind(callback: Callback<Result<AuthData?>?>?)

    @FormUrlEncoded
    @POST(ServerUrlImpl.SET_AUTO)
    fun setAuto(
        @Field("autoSync") autoSync: Boolean,  //
        callback: Callback<Result<AuthData?>?>?
    )
}