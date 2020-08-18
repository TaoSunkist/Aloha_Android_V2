package com.wealoha.social.beans.instagram;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

import com.wealoha.social.beans.AuthData;
import com.wealoha.social.beans.Result;
import com.wealoha.social.impl.ServerUrlImpl;

public interface InstagramService {

	@FormUrlEncoded
	@POST(ServerUrlImpl.POST_INSTAGRAM_TOKEN)
	public void postToken(@Field("uid") String uid, //
			@Field("accessToken") String accessToken, //
			@Field("month") Integer month,//
			Callback<Result<AuthData>> callback);

	@POST(ServerUrlImpl.UNBIND_INSTAGRAM)
	public void unbind(Callback<Result<AuthData>> callback);

	@FormUrlEncoded
	@POST(ServerUrlImpl.SET_AUTO)
	public void setAuto(@Field("autoSync") boolean autoSync, //
			Callback<Result<AuthData>> callback);

}
