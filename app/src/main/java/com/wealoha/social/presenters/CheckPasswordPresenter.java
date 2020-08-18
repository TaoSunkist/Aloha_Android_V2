package com.wealoha.social.presenters;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.text.TextUtils;

import com.wealoha.social.api.user.User2API;
import com.wealoha.social.beans.Result;
import com.wealoha.social.beans.ResultData;
import com.wealoha.social.ui.lock.CheckPasswordView;
import com.wealoha.social.utils.StringUtil;

public class CheckPasswordPresenter extends AbsPresenter {

	@Inject
	User2API userAPI;
	private CheckPasswordView gestureLockView;

	public CheckPasswordPresenter(CheckPasswordView glv) {
		super();
		gestureLockView = glv;
	}

	public void checkPassword(String password) {

		if (!TextUtils.isEmpty(password)) {
			password = StringUtil.md5(password);
		} else {
			gestureLockView.checkPasswordFaile();
			return;
		}
		userAPI.verifyPassword(password, new Callback<Result<ResultData>>() {

			@Override
			public void success(Result<ResultData> result, Response arg1) {
				if (result != null) {
					if (result.isOk()) {
						gestureLockView.checkPasswordSuccess();
					} else {
						gestureLockView.checkPasswordFaile();
					}
				}
			}

			@Override
			public void failure(RetrofitError arg0) {
				gestureLockView.checkPasswordFaile();
			}
		});
	}
}
