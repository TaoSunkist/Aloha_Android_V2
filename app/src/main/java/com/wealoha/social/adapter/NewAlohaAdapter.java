package com.wealoha.social.adapter;

import java.util.List;

import javax.inject.Inject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wealoha.social.BaseFragAct;
import com.wealoha.social.R;
import com.wealoha.social.beans.User2;
import com.wealoha.social.commons.GlobalConstants.ImageSize;
import com.wealoha.social.inject.Injector;
import com.wealoha.social.utils.FontUtil;
import com.wealoha.social.utils.FontUtil.Font;
import com.wealoha.social.utils.StringUtil;
import com.wealoha.social.view.custom.CircleImageView;

public class NewAlohaAdapter extends BaseAdapter {

	private List<User2> mUser2s;
	private BaseFragAct mParent;
	private LayoutInflater mLayoutInflater;
	@Inject
	FontUtil mFontUtil;
	@Inject
	Picasso picasso;
	private final boolean mIsMatcher;
	private int mCount;

	public NewAlohaAdapter(List<User2> user2s, BaseFragAct baseFragAct, boolean b) {
		Injector.inject(this);
		this.mUser2s = user2s;
		this.mParent = baseFragAct;
		mIsMatcher = b;
		mLayoutInflater = (LayoutInflater) baseFragAct.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public NewAlohaAdapter(List<User2> user2s, int count, BaseFragAct baseFragAct, boolean b) {
		Injector.inject(this);
		this.mUser2s = user2s;
		this.mParent = baseFragAct;
		mIsMatcher = b;
		this.mCount = count;
		mLayoutInflater = (LayoutInflater) baseFragAct.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		if (mUser2s != null) {
			return mUser2s.size();
		}
		return 0;
	}

	@Override
	public User2 getItem(int position) {
		return mUser2s.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		User2 user2 = mUser2s.get(position);
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.item_new_aloha, parent, false);
			viewHolder = new ViewHolder(convertView, mParent);
			mFontUtil.changeViewFont(viewHolder.mUserName, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
			mFontUtil.changeViewFont(viewHolder.mUserInfos, Font.ENCODESANSCOMPRESSED_400_REGULAR);
			mFontUtil.changeViewFont(viewHolder.mUserPhotoCount, Font.ENCODESANSCOMPRESSED_400_REGULAR);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		// XL.d("getUrlSquare", user.getAvatarImage().getUrl(100, 100));
		picasso.load(user2.getAvatarImage().getUrl(ImageSize.CHAT_THUMB, ImageSize.CHAT_THUMB)).placeholder(R.drawable.default_photo).resize(100, 100).into(viewHolder.mUserPhoto);
		viewHolder.mUserName.setText(user2.getName());
		if (mIsMatcher && user2.isMatch()) {
			viewHolder.mMatchOrNot.setVisibility(View.VISIBLE);
		} else {
			viewHolder.mMatchOrNot.setVisibility(View.GONE);
		}
		viewHolder.mUserPhotoCount.setText(mParent.getString(R.string.aloha_time_photo_count, user2.getPostCount()));
		viewHolder.mUserInfos.setText(user2.getAge() + "·" + user2.getHeight() + "·" + user2.getWeight() + "·" + StringUtil.getUserZodiac(user2.getZodiac(), mParent));
		viewHolder.mUserName.setText(user2.getName());
		return convertView;
	}

	class ViewHolder {

		public CircleImageView mUserPhoto;
		public TextView mUserName;
		public TextView mUserPhotoCount;
		public TextView mMatchOrNot;
		public TextView mUserInfos;

		public ViewHolder(View view, BaseFragAct mParent) {
			mUserPhoto = (CircleImageView) view.findViewById(R.id.item_new_aloha_user_photo);
			mUserName = (TextView) view.findViewById(R.id.item_new_aloha_user_name);
			mUserPhotoCount = (TextView) view.findViewById(R.id.item_new_aloha_user_photo_count);
			mMatchOrNot = (TextView) view.findViewById(R.id.item_new_aloha_macht_or_not);
			mUserInfos = (TextView) view.findViewById(R.id.user_infos);
		}
	}
}
