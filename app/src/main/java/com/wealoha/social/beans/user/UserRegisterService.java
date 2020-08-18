package com.wealoha.social.beans.user;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

import com.wealoha.social.beans.AuthData;
import com.wealoha.social.beans.Result;
import com.wealoha.social.beans.ResultData;

/**
 * 用户注册
 * 
 * @author javamonk
 * @see
 * @since
 * @date 2014-12-21 上午11:11:13
 */
public interface UserRegisterService {

	/**
	 * 获得短信验证码
	 * 
	 * @param number
	 * @param callback
	 */
	@POST("/v1/user/register/mobile/verify")
	@FormUrlEncoded
	public void getCode(@Field("number") String number, Callback<Result<ResultData>> callback);

	/**
	 * 活得重设密码的短信验证码
	 * 
	 * @param number
	 * @param callback
	 */
	@POST("/v1/user/password/mobile/reset/verify")
	@FormUrlEncoded
	public void getResetPasswordCode(@Field("number") String number, Callback<Result<ResultData>> callback);

	/**
	 * 提交注册
	 * 
	 * @param number
	 * @param code
	 * @param passwordMd5
	 * @param callback
	 */
	@POST("/v1/user/register/mobile")
	@FormUrlEncoded
	public void register(@Field("number") String number, @Field("code") String code, //
			@Field("passwordMd5") String passwordMd5, Callback<Result<AuthData>> callback);
}
