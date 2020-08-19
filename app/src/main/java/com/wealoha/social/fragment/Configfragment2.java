package com.wealoha.social.fragment;

import javax.inject.Inject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wealoha.social.R;
import com.wealoha.social.beans.User;
import com.wealoha.social.commons.GlobalConstants.ImageSize;
import com.wealoha.social.inject.Injector;
import com.wealoha.social.utils.ContextUtil;
import com.wealoha.social.utils.ImageUtil;
import com.wealoha.social.utils.ImageUtil.CropMode;
import com.wealoha.social.utils.ToastUtil;
import com.wealoha.social.view.custom.preferenceitem.ProfileBlockItem;
import com.wealoha.social.view.custom.preferenceitem.SimpleBlockItem;

public class Configfragment2 extends BasePreferenceFragment {

	@Inject
	ContextUtil mContextUtil;
	private User mUser;

	private ProfileBlockItem mProItem;
	private SimpleBlockItem mAdvanceItem;
	private SimpleBlockItem mInsItem;
	private SimpleBlockItem mPhoneItem;
	private SimpleBlockItem mPswItem;
	private SimpleBlockItem mNotifyItem;
	private SimpleBlockItem mBlackListItem;
	private SimpleBlockItem mCacheItem;
	private SimpleBlockItem mFeedBackItem;
	private SimpleBlockItem mFaqItem;
	private SimpleBlockItem mUpdateItem;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Injector.inject(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mUser = mContextUtil.getCurrentUser();
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void initBlockItems() {
		mProItem = new ProfileBlockItem(this, R.string.set_profile, null,//
		mUser.getName(), ImageUtil.getImageUrl(mUser.getAvatarImage().getId(), ImageSize.AVATAR_ROUND_SMALL, CropMode.ScaleCenterCrop),//
		true, false);

		mAdvanceItem = new SimpleBlockItem(this, R.string.advanced_features, getString(R.string.Invite_friend_to_open), true, true);
		mInsItem = new SimpleBlockItem(this, R.string.instagram, null, true, true);
		mPhoneItem = new SimpleBlockItem(this, R.string.phone_number, "xxxxxx", true, false);
		mPswItem = new SimpleBlockItem(this, R.string.change_password, null, true, false);
		mNotifyItem = new SimpleBlockItem(this, R.string.new_message_notice, null, true, false);
		mBlackListItem = new SimpleBlockItem(this, R.string.black_list, null, true, false);
		mCacheItem = new SimpleBlockItem(this, R.string.cleanup_cache, null, false, false);
		mFeedBackItem = new SimpleBlockItem(this, R.string.feedback, null, true, false);
		mFaqItem = new SimpleBlockItem(this, R.string.faq, null, true, false);
		mUpdateItem = new SimpleBlockItem(this, R.string.check_new_version, null, true, false);

		initBlockView(null, mProItem);
		initBlockView(null, mAdvanceItem, mInsItem);
		initBlockView(null, mPhoneItem, mPswItem, mNotifyItem, mBlackListItem);
		initBlockView(null, mCacheItem, mFeedBackItem, mFaqItem, mUpdateItem);

	}

	@Override
	public void onclickCallback(int itemid) {
		ToastUtil.longToast(getActivity(), "item:" + itemid);
		switch (itemid) {
		case R.string.set_profile:
			break;
		case R.string.advanced_features:
			break;
		case R.string.instagram:
			break;
		case R.string.phone_number:
			break;
		case R.string.change_password:
			break;
		case R.string.new_message_notice:
			break;
		case R.string.black_list:
			break;
		case R.string.cleanup_cache:
			break;
		case R.string.feedback:
			break;
		case R.string.faq:
			break;
		case R.string.check_new_version:
			break;

		default:
			break;
		}
	}

}
