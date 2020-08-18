package com.wealoha.social.adapter.profile;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.wealoha.social.api.user.bean.User;

public class Profile2InfoAdapter extends BaseAdapter {

	private User mUser;

	public Profile2InfoAdapter(User user) {
		mUser = user;
	}

	@Override
	public int getCount() {
		return 1;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Profile2InfoHolder holder = new Profile2InfoHolder(mUser, parent);
		convertView = holder.getView();
		return convertView;
	}
}
