package com.wealoha.social.beans.location;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

import com.wealoha.social.beans.Result;
import com.wealoha.social.beans.ResultData;
import com.wealoha.social.impl.ServerUrlImpl;

public interface LocationService {

	@FormUrlEncoded
	@POST(ServerUrlImpl.LOCATION)
	public void location(//
	@Field("name") String uid, //
			@Field("coordinate") String coordinate,//
			@Field("latitude") Double latitude, //
			@Field("longitude") Double longitude,//
			@Field("count") Integer count,//
			@Field("cursor") String cursor,//
			Callback<Result<LocationResult>> callback);

	@FormUrlEncoded
	@POST(ServerUrlImpl.LOCATION_RECORD)
	public void locationRecord(//
	@Field("latitude") Double latitude, //
			@Field("longitude") Double longitude, Callback<Result<ResultData>> callback);
}
