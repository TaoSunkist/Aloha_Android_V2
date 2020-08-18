package com.wealoha.social.adapter.profile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wealoha.social.R;

public class Profile2BlackHolder {

	private ViewGroup blackRootView;

	public Profile2BlackHolder(LayoutInflater inflater, ViewGroup parent) {
		blackRootView = (ViewGroup) inflater.inflate(R.layout.item_profile_block, parent, false);
	}

	public View getView() {
		return blackRootView;
	}

}
