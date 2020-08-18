package com.wealoha.social.beans.user;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

import com.wealoha.social.beans.AuthData;
import com.wealoha.social.beans.Result;
import com.wealoha.social.impl.ServerUrlImpl;

/**
 * 第三方登录
 * 
 * @author javamonk
 * @see
 * @since
 * @date 2014-11-16 下午3:14:23
 */
public interface ConnectService {

	/**
	 * 连接微博
	 * 
	 * @param uid
	 * @param accessToken
	 * @return
	 */
	@POST(ServerUrlImpl.CONNECT_WEIBO)
	@FormUrlEncoded
	public Result<AuthData> connectWeibo(@Field("uid") String uid, @Field("accessToken") String accessToken);

	/**
	 * 连接微博
	 * 
	 * @param uid
	 * @param accessToken
	 * @param callback
	 */
	@POST(ServerUrlImpl.CONNECT_WEIBO)
	@FormUrlEncoded
	public void connectWeibo(@Field("uid") String uid, @Field("accessToken") String accessToken, Callback<Result<AuthData>> callback);

	/**
	 * 连接微博
	 * 
	 * @param uid
	 * @param accessToken
	 * @param callback
	 */
	@POST("/v1/connect/facebook")
	@FormUrlEncoded
	public void connectFacebook(@Field("uid") String uid, @Field("accessToken") String accessToken, Callback<Result<AuthData>> callback);

}
