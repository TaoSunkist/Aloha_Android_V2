package com.wealoha.social.ui.feeds;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.wealoha.social.api.post.bean.Post;
import com.wealoha.social.widget.feed.PostLayout;

public class FeedAdapterV3 extends BaseAdapter {

	List<Post> postList;
	Context mContext;
	IFeedsViewV2 mFeedView;

	public FeedAdapterV3(Context context, IFeedsViewV2 feedView, List<Post> postList) {
		this.postList = postList;
		mContext = context;
		mFeedView = feedView;
	}

	@Override
	public int getCount() {
		return postList == null ? 0 : postList.size();
	}

	@Override
	public Post getItem(int position) {
		return postList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		PostLayout postView = null;
		if (convertView == null) {
			postView = new PostLayout(mContext, mFeedView, getItem(position));
			convertView = postView;
		} else {
			postView = (PostLayout) convertView;
		}
		postView.initViewData(getItem(position));
		return convertView;
	}

}
