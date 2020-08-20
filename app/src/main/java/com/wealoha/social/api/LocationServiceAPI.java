package com.wealoha.social.api;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

import com.wealoha.social.beans.Result;
import com.wealoha.social.beans.ResultData;


public interface LocationServiceAPI {
	@FormUrlEncoded
	@POST("/v1/stat/logdata")
	public void logData(@Field("data") String dataStr, Callback<Result<ResultData>> callback);

}
