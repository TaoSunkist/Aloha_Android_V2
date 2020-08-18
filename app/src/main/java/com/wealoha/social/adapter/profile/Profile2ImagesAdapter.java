package com.wealoha.social.adapter.profile;

import java.util.Collections;
import java.util.List;

import android.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.wealoha.social.api.post.bean.Post;

public class Profile2ImagesAdapter extends BaseAdapter {

	List<Post> mPostList;
	private Fragment mFrag;

	public Profile2ImagesAdapter(List<Post> postList, Fragment frag) {
		mPostList = postList;
		mFrag = frag;
	}

	@Override
	public int getCount() {
		if (mPostList == null) {
			return 0;
		}
		int count = mPostList.size() / 3;
		return mPostList.size() % 3 != 0 ? ++count : count;
	}

	@Override
	public List<Post> getItem(int position) {
		if (mPostList == null) {
			return null;
		}
		// 三个一行的模式
		if (mPostList.size() == 0) {
			return Collections.emptyList();
		}
		int start = position * 3;
		int to = start + 3;
		if (to > mPostList.size()) {
			to = mPostList.size();
		}
		return mPostList.subList(start, to);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Profile2ImageHolder imageHolder;
		List<Post> item = getItem(position);
		if (convertView == null) {
			imageHolder = new Profile2ImageHolder(item, parent, mFrag, position);
			convertView = imageHolder.getView();
			convertView.setTag(imageHolder);
		} else {
			imageHolder = (Profile2ImageHolder) convertView.getTag();
			imageHolder.updateData(item, position);
		}
		return convertView;
	}

}
