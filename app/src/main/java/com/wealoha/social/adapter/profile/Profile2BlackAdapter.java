package com.wealoha.social.adapter.profile;

import androidx.fragment.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class Profile2BlackAdapter extends BaseAdapter {

	private Fragment mFrag;

	public Profile2BlackAdapter(Fragment frag) {
		mFrag = frag;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return new Profile2BlackHolder(mFrag.getActivity().getLayoutInflater(), parent).getView();
	}

}
