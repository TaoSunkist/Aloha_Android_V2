package com.wealoha.social.view.custom.listitem;

import javax.inject.Inject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.squareup.picasso.Picasso;
import com.wealoha.social.R;
import com.wealoha.social.beans.User;
import com.wealoha.social.inject.Injector;
import com.wealoha.social.utils.ImageUtil;
import com.wealoha.social.utils.ImageUtil.CropMode;
import com.wealoha.social.view.custom.CircleImageView;

public class UserListItem {

	@Inject
	Context context;


	@InjectView(R.id.user_photo)
	CircleImageView mUserPhoto;

	private ViewGroup container;
	private User mUser;

	public UserListItem() {
		Injector.init(this);
		container = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.item_swipe_userlist, null);
		ButterKnife.inject(this, container);
	}

	public ViewGroup initView(User user) {
		if (user == null) {
			return null;
		}
		mUser = user;
		Picasso.get().load(ImageUtil.getImageUrl(mUser.getAvatarImage().getId(), 100, CropMode.ScaleCenterCrop)).placeholder(R.drawable.default_photo).into(mUserPhoto);

		return container;
	}

	public ViewGroup getView(User user) {
		return container;
	}
}
