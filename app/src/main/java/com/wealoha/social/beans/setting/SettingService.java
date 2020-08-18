package com.wealoha.social.beans.setting;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;

import com.wealoha.social.beans.Result;
import com.wealoha.social.beans.ResultData;
import com.wealoha.social.impl.ServerUrlImpl;

/**
 * @author javamonk
 * @createTime 14-10-11 PM5:50
 */
public interface SettingService {

	/**
	 * 读取当前设置
	 * 
	 * @return
	 */
	// TODO
	@GET(ServerUrlImpl.URL_USER_SETTING_PUSH)
	public Result<PushSettingResult> getPushSetting();

	@GET(ServerUrlImpl.URL_USER_SETTING_PUSH)
	public void getPushSetting(Callback<Result<PushSettingResult>> callback);

	/**
	 * 保存设置
	 * 
	 * @param pushSound
	 * @param pushVibration
	 * @param pushShowDetail
	 * @return
	 */
	@FormUrlEncoded
	@POST(ServerUrlImpl.URL_USER_SETTING_PUSH)
	public Result<ResultData> savePushSetting(@Field("pushSound") Boolean pushSound,//
			@Field("pushVibration") Boolean pushVibration, //
			@Field("pushShowDetail") Boolean pushShowDetail, //
			@Field("pushPostLike") String pushPostLike, //
			@Field("pushPostComment") String pushPostComment);

	@FormUrlEncoded
	@POST(ServerUrlImpl.URL_USER_SETTING_PUSH)
	public void savePushSetting(@Field("pushSound") Boolean pushSound,//
			@Field("pushVibration") Boolean pushVibration, //
			@Field("pushShowDetail") Boolean pushShowDetail, //
			@Field("pushPostLike") String pushPostLike, //
			@Field("pushPostComment") String pushPostComment,//
			@Field("pushPostTag") String pushPostTag,//
			Callback<Result<ResultData>> callback);

	@FormUrlEncoded
	@POST(ServerUrlImpl.URL_USER_SETTING_PUSH)
	public void savePushSetting(@Field("pushAloha") String pushAloha,//
			Callback<Result<ResultData>> callback);

	@FormUrlEncoded
	@POST(ServerUrlImpl.URL_USER_SETTING_PUSH)
	public Result<PushSettingResult> savePushSetting(@Field("pushSound") Boolean pushSound,//
			@Field("pushVibration") Boolean pushVibration,//
			@Field("pushShowDetail") Boolean pushShowDetail);
}
