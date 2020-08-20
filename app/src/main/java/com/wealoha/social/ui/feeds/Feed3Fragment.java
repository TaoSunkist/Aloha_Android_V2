package com.wealoha.social.ui.feeds;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.InjectView;

import com.wealoha.social.R;
import com.wealoha.social.beans.Post;
import com.wealoha.social.fragment.BaseFragment;
import com.wealoha.social.presenters.FeedsPresenter;
import com.wealoha.social.utils.XL;
import com.wealoha.social.widget.ScrollToLoadMoreListView;

public class Feed3Fragment extends BaseFragment implements IFeedsViewV2 {

	@InjectView(R.id.feed_listview)
	ScrollToLoadMoreListView feedListView;

	private FeedsPresenter feedPresenter;
	private View rootView;
	private FeedAdapterV3 feedAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = LayoutInflater.from(context).inflate(R.layout.frag_feed3, container, false);
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		feedListView.setScrollToLoadView(this);
		feedPresenter = new FeedsPresenter(this);
		feedPresenter.getFeedsData();
	}

	@Override
	public void loadMore() {
		feedPresenter.getFeedsData();
	}

	@Override
	public void setData(List<Post> postList) {
		if (feedAdapter == null) {
			feedAdapter = new FeedAdapterV3(getActivity(), this, postList);
			feedListView.setAdapter(feedAdapter);
		} else {
			feedAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void postViewScroll(int x, int y, String id) {
		XL.i("FEED_FRAGMENT_LAYOUT", "X:" + x);
		XL.i("FEED_FRAGMENT_LAYOUT", "Y:" + y);
		XL.i("FEED_FRAGMENT_LAYOUT", "ID:" + id);
		XL.i("FEED_FRAGMENT_LAYOUT", "=============");
	}

}
