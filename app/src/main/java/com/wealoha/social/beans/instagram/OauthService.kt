package com.wealoha.social.beans.instagram

import retrofit.Callback
import retrofit.http.Field
import retrofit.http.FormUrlEncoded
import retrofit.http.POST

interface OauthService {
    @POST("/oauth/access_token")
    @FormUrlEncoded
    fun accessToken(
        @Field("client_id") clientId: String?,  //
        @Field("client_secret") clientSecret: String?,  //
        @Field("grant_type") grant_type: String?,  //
        @Field("redirect_uri") redirectUri: String?,  //
        @Field("code") code: String?
    ): AccessToken?

    @POST("/oauth/access_token")
    @FormUrlEncoded
    fun accessToken(
        @Field("client_id") clientId: String?,  //
        @Field("client_secret") clientSecret: String?,  //
        @Field("grant_type") grant_type: String?,  //
        @Field("redirect_uri") redirectUri: String?,  //
        @Field("code") code: String?,  //
        callback: Callback<AccessToken?>?
    )
}