package com.wealoha.social.api.notify2.service;

import javax.inject.Inject;

import retrofit.RetrofitError;
import retrofit.client.Response;

import com.wealoha.social.api.common.AbsBaseService;
import com.wealoha.social.api.common.ApiErrorCode;
import com.wealoha.social.api.common.Direct;
import com.wealoha.social.beans.MergeUsersGetData;
import com.wealoha.social.api.notify2.Notify2API;
import com.wealoha.social.api.user.bean.User;
import com.wealoha.social.beans.Result;
import com.wealoha.social.inject.Injector;
import com.wealoha.social.utils.XL;

/**
 * 
 * 
 * @author superman
 * @createTime 2015-03-12 11:20:31
 */
public class UserListService extends AbsBaseService<User, String> {

	@Inject
	Notify2API notify2api;

	public UserListService() {
		Injector.inject(this);
	}

	@Override
	public void getList(String cursor, int count, Direct direct, String notifyId, final com.wealoha.social.api.common.BaseListApiService.ApiListCallback<User> callback) {
		notify2api.getMergeUsers(notifyId, count, cursor, new retrofit.Callback<Result<MergeUsersGetData>>() {

			@Override
			public void failure(RetrofitError error) {
				callback.fail(null, error);
				XL.i("USER_LIST_FRAG", "failure:" + error.getMessage());
			}

			@Override
			public void success(Result<MergeUsersGetData> result, Response arg1) {
				XL.i("USER_LIST_FRAG", "success");
				if (result != null && result.isOk()) {
					callback.success(getUsers(result.data.list, result.data.imageMap), result.data.nextCursorId);
				} else {
					callback.fail(ApiErrorCode.fromResult(result), null);
				}
			}

		});
	}
}
