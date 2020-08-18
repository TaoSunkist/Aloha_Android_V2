package com.wealoha.social.beans.user;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

import com.wealoha.social.beans.Result;
import com.wealoha.social.beans.ResultData;
import com.wealoha.social.impl.ServerUrlImpl;

/**
 * 
 * 
 * @author javamonk
 * @see
 * @since
 * @date 2014-11-16 下午2:11:35
 */
public interface AuthService {

	/**
	 * 
	 * @param callback
	 */
	@POST(ServerUrlImpl.LOGOUT_URL)
	public void unauth(Callback<Result<ResultData>> callback);

	// @FormUrlEncoded
	// @POST(GlobalConstants.ServerUrlPath.PUSH_UNBINDING)
	// Result<ResultData> bind(@Field("tokenGetui") String tokenGetui);

	@FormUrlEncoded
	@POST(ServerUrlImpl.PUSH_UNBINDING)
	public void unbind(@Field("token") String token, Callback<Result<ResultData>> callback);
}
