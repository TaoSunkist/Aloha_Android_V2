package com.wealoha.social.adapter.profile;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.wealoha.social.beans.User2;

public class Profile2InfoAdapter extends BaseAdapter {

	private User2 mUser2;

	public Profile2InfoAdapter(User2 user2) {
		mUser2 = user2;
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
		Profile2InfoHolder holder = new Profile2InfoHolder(mUser2, parent);
		convertView = holder.getView();
		return convertView;
	}
}
