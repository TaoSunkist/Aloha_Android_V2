package com.wealoha.social.api.user;

import java.util.ArrayList;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import com.squareup.otto.Bus;
import com.wealoha.social.api.common.ApiErrorCode;
import com.wealoha.social.api.common.BaseListApiService.ApiCallback;
import com.wealoha.social.api.common.bean.Image;
import com.wealoha.social.api.user.bean.User;
import com.wealoha.social.api.user.dto.UserDTO;
import com.wealoha.social.beans.Result;
import com.wealoha.social.beans.ResultData;
import com.wealoha.social.callback.CallbackImpl;
import com.wealoha.social.event.RefreshFilterBtnEvent;
import com.wealoha.social.inject.Injector;
import com.wealoha.social.utils.ContextUtil;

public class User2Service {

	@Inject
	User2API user2Api;
	@Inject
	ContextUtil contextUtil;
	@Inject
	Bus bus;

	public User2Service() {
		Injector.inject(this);
	}

	/***
	 * 解除aloha
	 * 
	 * @param userid
	 *            用户id
	 * @param callback
	 * @return void
	 */
	public void dislike(String userid, final com.wealoha.social.api.common.BaseListApiService.NoResultCallback callback) {
		user2Api.dislikeUser(userid, new Callback<Result<ResultData>>() {

			@Override
			public void success(Result<ResultData> result, Response arg1) {
				if (result == null || !result.isOk()) {
					callback.fail(ApiErrorCode.fromResult(result), null);
				} else {
					callback.success();
				}
			}

			@Override
			public void failure(RetrofitError error) {
				callback.fail(null, error);
			}
		});
	}

	public void aloha(String userid, String refer, final com.wealoha.social.api.common.BaseListApiService.NoResultCallback callback) {
		user2Api.aloha(userid, refer, new Callback<Result<ResultData>>() {

			@Override
			public void success(Result<ResultData> result, Response arg1) {
				if (result == null || !result.isOk()) {
					callback.fail(ApiErrorCode.fromResult(result), null);
				} else {
					callback.success();
				}
			}

			@Override
			public void failure(RetrofitError error) {
				callback.fail(null, error);
			}
		});
	}

	/***
	 * 获取用户的profile， callback 返回的值就是一个user,如果刷新的用户是当前用户， 那么跟新本地的用户信息 {@link #saveCurrentUser(UserDTO)}
	 * 
	 * @param userid
	 *            用户id
	 * @param callback
	 * @return void
	 */
	public void userProfile(String userid, final ApiCallback<User> callback) {
		user2Api.userProfile(userid, new Callback<Result<Profile2Data>>() {

			@Override
			public void success(Result<Profile2Data> result, Response arg1) {
				if (result == null || !result.isOk()) {
					callback.fail(ApiErrorCode.fromResult(result), null);
				} else {
					saveCurrentUser(result.data.user);
					callback.success(transPro2DataToUser(result.data));
				}
			}

			@Override
			public void failure(RetrofitError error) {
				callback.fail(null, error);
			}
		});
	}

	private User transPro2DataToUser(Profile2Data proData) {
		UserDTO userDto = proData.user;
		// userDto.aloha = proData.liked;
		// userDto.match = proData.friend;
		Image img = Image.fromDTO(proData.imageMap.get(userDto.avatarImageId));
		return User.fromDTO(userDto, img);
	}

	/***
	 * 更新user后， 将当前用户的信息刷新并保存
	 * 
	 * @param userDto
	 * @return void
	 */
	private void saveCurrentUser(UserDTO userDto) {
		if (!userDto.me || userDto == null) {
			return;
		}
		com.wealoha.social.beans.User user = new com.wealoha.social.beans.User();
		user.age = String.valueOf(userDto.age);
		user.aloha = userDto.aloha;
		user.alohaCount = userDto.alohaCount;
		user.alohaGetCount = userDto.alohaGetCount;
		user.avatarImageId = userDto.avatarImageId;
		user.birthday = userDto.birthday;
		user.block = userDto.block;
		user.hasPrivacy = userDto.hasPrivacy;
		user.height = String.valueOf(userDto.height);
		user.id = userDto.id;
		user.match = userDto.match;
		user.me = userDto.me;
		user.name = userDto.name;
		user.postCount = userDto.postCount;
		user.profileIncomplete = userDto.profileIncomplete;
		user.regionCode = userDto.regionCode;
		user.region = userDto.region;
		user.selfPurposes = userDto.selfPurposes;
		user.selfTag = userDto.selfTag;
		user.summary = userDto.summary;
		user.weight = String.valueOf(userDto.weight);
		user.zodiac = userDto.zodiac;
		com.wealoha.social.beans.Image image = new com.wealoha.social.beans.Image();
		image.id = userDto.avatarImageId;
		image.height = userDto.avatarImage.height;
		image.width = userDto.avatarImage.width;
		image.urlPatternWidth = userDto.avatarImage.urlPatternWidth;
		image.urlPatternWidthHeight = userDto.avatarImage.urlPatternWidthHeight;
		image.type = userDto.avatarImage.type;
		user.avatarImage = image;
		contextUtil.setCurrentUser(user);
	}

	/**
	 * 获取高级功能设置,并保存到本地
	 * 
	 */
	public void getProfeatureSetting() {
		user2Api.userMatchSetting(new Callback<Result<MatchSettingData>>() {

			@Override
			public void success(Result<MatchSettingData> result, Response response) {
				if (result != null && result.isOk()) {
					contextUtil.setProfeatureEnable(result.data.filterEnable);
					ArrayList<String> regions = (ArrayList<String>) result.data.selectedRegion;
					if (regions != null && regions.size() > 0) {
						contextUtil.setFilterRegion(regions.get(regions.size() - 1));
					} else {
						contextUtil.setFilterRegion(null);
					}
					bus.post(new RefreshFilterBtnEvent());
				}
			}

			@Override
			public void failure(RetrofitError arg0) {
			}
		});
	}

	/**
	 * @author: sunkist
	 * @date:2015年7月30日
	 */
	public void verifyPassword(String pw, final CallbackImpl callbackImpl) {
		user2Api.verifyPassword(pw, new Callback<Result<ResultData>>() {

			@Override
			public void failure(RetrofitError retrofitError) {
				// ToastUtil.shortToast(AppApplication.getInstance(), R.string.Unkown_Error);
				callbackImpl.failure();
			}

			@Override
			public void success(Result<ResultData> result, Response response) {
				if (result != null && result.isOk()) {
					if (result.isOk()) {
						callbackImpl.success();
					} else if (result.data.error == ResultData.ERROR_INVALID_PASSWORD) {
						callbackImpl.failure();
						// ToastUtil.shortToast(AppApplication.getInstance(),
						// R.string.login_password_is_invalid_title);
					}
				} else {
					callbackImpl.failure();
					// ToastUtil.shortToast(AppApplication.getInstance(), R.string.Unkown_Error);
				}
			}
		});
	}
}
