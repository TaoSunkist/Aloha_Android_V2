package com.wealoha.social.presenters;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import com.wealoha.social.ContextConfig;
import com.wealoha.social.api.ServerApi;
import com.wealoha.social.beans.PrivacyData;
import com.wealoha.social.beans.Result;
import com.wealoha.social.commons.GlobalConstants;
import com.wealoha.social.ui.privacy.IPrivacyView;

/**
 * @author:sunkist
 * @description:
 * @Date:2015年8月4日
 */
public class PrivacyPresenter extends AbsPresenter {
	IPrivacyView mIPrivacyView;
	@Inject
	ServerApi mUserSettingPrivacyAPI;

	public PrivacyPresenter(IPrivacyView iPrivacyView) {
		mIPrivacyView = iPrivacyView;
	}

	public void getPrivacy() {
		mUserSettingPrivacyAPI.getPrivacy(new Callback<Result<PrivacyData>>() {

			@Override
			public void success(Result<PrivacyData> result, Response arg1) {
				if (result != null && result.isOk()) {
					mIPrivacyView.setPrivacyRange(result.data.matchExcludeDistanceKm);
				} else {
					mIPrivacyView.setPrivacyRange(ContextConfig.getInstance().getIntWithFilename(GlobalConstants.TAGS.PRIVACY_RANGE));
				}
			}

			@Override
			public void failure(RetrofitError arg0) {
				mIPrivacyView.setPrivacyRange(ContextConfig.getInstance().getIntWithFilename(GlobalConstants.TAGS.PRIVACY_RANGE));
			}
		});
	}

	/**
	 * @author: sunkist
	 * @description:请求隐身范围
	 * @date:2015年8月4日
	 */
	public void setPrivacy(Integer rangeValue) {
		mUserSettingPrivacyAPI.setPrivacy(rangeValue == null ? null : rangeValue, new Callback<Result<PrivacyData>>() {

			@Override
			public void success(Result<PrivacyData> result, Response arg1) {
			}

			@Override
			public void failure(RetrofitError arg0) {
			}
		});
	}
}
