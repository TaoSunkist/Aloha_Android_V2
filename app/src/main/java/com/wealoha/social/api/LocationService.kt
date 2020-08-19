package com.wealoha.social.api

import com.wealoha.social.beans.Result
import com.wealoha.social.beans.ResultData
import com.wealoha.social.beans.location.LocationResult
import com.wealoha.social.impl.ServerUrlImpl
import retrofit.Callback
import retrofit.http.Field
import retrofit.http.FormUrlEncoded
import retrofit.http.POST

interface LocationService {
    @FormUrlEncoded
    @POST(ServerUrlImpl.LOCATION)
    fun location( //
        @Field("name") uid: String?,  //
        @Field("coordinate") coordinate: String?,  //
        @Field("latitude") latitude: Double?,  //
        @Field("longitude") longitude: Double?,  //
        @Field("count") count: Int?,  //
        @Field("cursor") cursor: String?,  //
        callback: Callback<Result<LocationResult?>?>?
    )

    @FormUrlEncoded
    @POST(ServerUrlImpl.LOCATION_RECORD)
    fun locationRecord( //
        @Field("latitude") latitude: Double?,  //
        @Field("longitude") longitude: Double?,
        callback: Callback<Result<ResultData?>?>?
    )
}