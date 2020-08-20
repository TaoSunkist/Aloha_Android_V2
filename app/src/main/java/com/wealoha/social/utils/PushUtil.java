package com.wealoha.social.utils;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.text.TextUtils;

import com.wealoha.social.ContextConfig;
import com.wealoha.social.api.ServerApi;
import com.wealoha.social.beans.Result;
import com.wealoha.social.beans.ResultData;
import com.wealoha.social.commons.GlobalConstants;
import com.wealoha.social.inject.Injector;

/**
 * 
 * 
 * @author javamonk
 * @see
 * @since
 * @date 2014-11-11 下午6:40:30
 */
public class PushUtil {

	private String pushToken;

	private boolean bindSuccess = false;

	@Inject
	ContextUtil contextUtil;
	@Inject
	ServerApi mMessageService;

	public PushUtil() {
		Injector.inject(this);
	}

	/**
	 * 拿到PushToken后调用
	 * 
	 * @param token
	 */
	public void tryBindGetuiPushToken(String token) {
		if (bindSuccess && !token.equals(this.pushToken)) {
			bindSuccess = false;
		}
		this.pushToken = token;
		tryXmGetuiPushToken();
	}

	static int tryBindPushCount = 3;

	/**
	 * 用户登录后调用
	 */
	public void tryXmGetuiPushToken() {
		if (bindSuccess) {
			// XL.d(TAG, "Token已经绑定成功过，忽略");
			return;
		}

		// TODO 改成使用正确方式去取
		if (contextUtil.getCurrentUser() != null && pushToken != null) {
			mMessageService.bindPushToUser(pushToken, new Callback<Result<ResultData>>() {

				@Override
				public void success(Result<ResultData> result, Response arg1) {
					if (result != null && result.isOk()) {
						ContextConfig.getInstance().putStringWithFilename(GlobalConstants.AppConstact.TOKEN_GETUI, pushToken);
						bindSuccess = true;
					} else {
						tryBindPush();
					}

				}

				public void tryBindPush() {
					if (tryBindPushCount <= 0 || TextUtils.isEmpty(contextUtil.getCurrentTicket())) {
						return;
					}
					tryBindPushCount -= 1;
					tryXmGetuiPushToken();
				}

				@Override
				public void failure(RetrofitError arg0) {
					tryBindPush();
				}

			});

		}
	}

	public String getPushToken() {
		return pushToken;
	}

	public void setTokenUnbind() {
		bindSuccess = false;
	}
}
