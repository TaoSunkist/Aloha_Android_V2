package com.wealoha.social.utils;

import java.util.Locale;
import java.util.UUID;

import javax.inject.Inject;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.wealoha.social.beans.IResultDataErrorCode;
import com.wealoha.social.beans.ApiResponse;
import com.wealoha.social.beans.ResultData;
import com.wealoha.social.commons.GlobalConstants;
import com.wealoha.social.commons.JsonController;
import com.wealoha.social.inject.Injector;
import com.wealoha.social.store.SyncEntProtocol;
import com.wealoha.social.store.UserAgentProvider;

/**
 * 
 * 
 * @author javamonk
 * @see
 * @since
 * @date 2014-11-12 下午3:20:08
 */
public class GuidUtil {

	private static final String KEY_GUID = "ALOHA_GUID";

	private String TAG = getClass().getSimpleName();

	@Inject
	SharedPreferences sharedPreferences;

	@Inject
	ContextUtil contextUtil;

	@Inject
	UserAgentProvider userAgentProvider;

	public GuidUtil() {
		Injector.inject(this);
	}

	/**
	 * 返回已注册的guid，如果没有，生成一个，并且在服务端注册
	 * 
	 * @return
	 */
	public String getGuid() {
		String guid = sharedPreferences.getString(KEY_GUID, null);
		if (StringUtil.isNotEmpty(guid)) {
			XL.d(TAG, "GUID: " + guid);
			return guid;
		}

		guid = UUID.randomUUID().toString().toUpperCase(Locale.ENGLISH);
		XL.d(TAG, "生成GUID: " + guid);
		Editor editor = sharedPreferences.edit();
		editor.putString(KEY_GUID, guid);
		editor.commit();

		RequestParams params = new RequestParams();
		params.addBodyParameter("guid", guid);
		String androidStore = userAgentProvider.getAndroidStore();
		if (StringUtil.isNotEmpty(androidStore)) {
			params.addHeader("Android-Store", androidStore);
		}

		contextUtil.addGeneralHttpHeaders(params, false);

		SyncEntProtocol.getInstance().send(HttpMethod.POST, GlobalConstants.ServerUrl.GUID_REGISTER, params, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				XL.d(TAG, "注册GUID失败: " + arg1, arg0);
			}

			@Override
			public void onSuccess(ResponseInfo<String> resp) {
				ApiResponse<ResultData> apiResponse = JsonController.parseJson(resp.result, new TypeToken<ApiResponse<ResultData>>() {
				}.getType());
				if (apiResponse.isOk()) {
					XL.d(TAG, "注册GUID成功");
				} else if (apiResponse.getData().getError() == IResultDataErrorCode.ERROR_GUID_DUPLICATE) {
					XL.w(TAG, "注册GUID失败，重复，清理掉本地的，下次再生成一个");
					Editor editor = sharedPreferences.edit();
					editor.remove(KEY_GUID);
					editor.commit();
				}
			}
		});

		return guid;
	}
}
