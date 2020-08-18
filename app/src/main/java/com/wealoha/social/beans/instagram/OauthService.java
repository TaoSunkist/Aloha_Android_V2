package com.wealoha.social.beans.instagram;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

public interface OauthService {

	@POST("/oauth/access_token")
	@FormUrlEncoded
	public AccessToken accessToken(@Field("client_id") String clientId, //
			@Field("client_secret") String clientSecret, //
			@Field("grant_type") String grant_type, //
			@Field("redirect_uri") String redirectUri, //
			@Field("code") String code);

	@POST("/oauth/access_token")
	@FormUrlEncoded
	public void accessToken(@Field("client_id") String clientId, //
			@Field("client_secret") String clientSecret, //
			@Field("grant_type") String grant_type, //
			@Field("redirect_uri") String redirectUri, //
			@Field("code") String code,//
			Callback<AccessToken> callback);
}
