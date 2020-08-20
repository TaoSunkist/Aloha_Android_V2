package com.wealoha.social.adapter;

import javax.inject.Inject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.InjectView;

import com.squareup.picasso.Picasso;
import com.wealoha.social.BaseFragAct;
import com.wealoha.social.R;
import com.wealoha.social.adapter.feed.AbsViewHolder;
import com.wealoha.social.api.common.BaseListApiService;
import com.wealoha.social.beans.User2;
import com.wealoha.social.view.custom.CircleImageView;
import com.wealoha.social.widget.BaseAdapterHolder;
import com.wealoha.social.widget.BaseListApiAdapter;
import com.wealoha.social.widget.MultiListViewType;

public class UserListAdapter extends BaseListApiAdapter<User2, String> {

	@Inject
	Picasso picasso;
	private BaseFragAct mParent;

	public UserListAdapter(BaseFragAct parent, BaseListApiService<User2, String> service) {
		super(service);
		mParent = parent;
	}

	public static class UserListHodler extends BaseAdapterHolder {

		protected UserListHodler(View view) {
			super(view);
		}

		@InjectView(R.id.user_photo)
		CircleImageView userPhoto;
		@InjectView(R.id.user_name)
		TextView userName;
		@InjectView(R.id.user_photo_count)
		TextView userPhotoCount;
		@InjectView(R.id.macht_or_not)
		TextView matchOrNot;
		@InjectView(R.id.user_infos)
		TextView userInfos;
	}

	@Override
	protected BaseAdapterHolder newViewHolder(MultiListViewType type, User2 item, LayoutInflater inflater, ViewGroup parent) {
		View view = inflater.inflate(R.layout.item_swipe_userlist, parent, false);
		return new UserListHodler(view);
	}

	@Override
	protected void fillView(AbsViewHolder holder, User2 item, int position, View convertView) {
		UserListHodler userlistHolder = (UserListHodler) holder;
		picasso.load(item.getAvatarImage().getUrl(80, 80))//
		.into(userlistHolder.userPhoto);
		userlistHolder.userName.setText(item.getName());
		userlistHolder.userPhotoCount.setText(String.valueOf(item.getPostCount()) + mParent.getString(R.string.photo));
		userlistHolder.matchOrNot.setText(item.isMatch() ? mParent.getString(R.string.matched) : "");
		userlistHolder.userInfos.setText(item.getAge() + "·" + item.getHeight() + "·" + item.getWeight() + "·" + item.getZodiac());
	}
}
