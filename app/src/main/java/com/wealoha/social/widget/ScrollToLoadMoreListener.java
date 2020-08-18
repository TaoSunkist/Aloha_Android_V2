package com.wealoha.social.widget;

import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

import com.wealoha.social.api.feed.service.Feed2Service;

/**
 * 通过滚动加载更多
 * 
 * @author javamonk
 * @createTime 2015年3月6日 下午1:07:10
 */
public class ScrollToLoadMoreListener implements OnScrollListener {

	private final int loadMoreThreshold;
	private Callback callback;
	private final Feed2Service feedService;
	private final int mFeedWidth;

	public interface Callback {

		public void loadMore();
	}

	public interface ScrollCallback {

		public void loadMore();
	}

	public interface ScrollStateCallback extends Callback {

		public void scrollState();
	}

	/**
	 * 
	 * @param loadMoreThreshold
	 *            到达多少个元素时加载
	 * @param callback
	 */
	public ScrollToLoadMoreListener(int loadMoreThreshold, Callback callback) {
		this.loadMoreThreshold = loadMoreThreshold;
		this.callback = callback;
		mFeedWidth = 0;
		feedService = null;
	}

	public ScrollToLoadMoreListener(int loadMoreThreshold, ScrollStateCallback callback) {
		this.loadMoreThreshold = loadMoreThreshold;
		this.callback = callback;
		mFeedWidth = 0;
		feedService = null;
	}

	public ScrollToLoadMoreListener(int loadMoreThreshold, Callback callback, Feed2Service service, int feedWidth) {
		this.loadMoreThreshold = loadMoreThreshold;
		this.callback = callback;
		this.feedService = service;
		mFeedWidth = feedWidth;
	}

	private int mTemtVisibleItem;
	private int mFirstVisibleItem;
	private int mTotalItemCount;

	@Override
	public void onScrollStateChanged(AbsListView arg0, int scrollState) {
		if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && feedService != null) {
			boolean direction = (mFirstVisibleItem - mTemtVisibleItem) >= 0;
			feedService.fetchPhoto(mFirstVisibleItem, mTotalItemCount, direction, mFeedWidth);
			mTemtVisibleItem = mFirstVisibleItem;
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		mTotalItemCount = totalItemCount;
		mFirstVisibleItem = firstVisibleItem;
		if (firstVisibleItem + visibleItemCount + loadMoreThreshold == totalItemCount) {
			callback.loadMore();
		}

		if (callback instanceof ScrollStateCallback) {
			((ScrollStateCallback) callback).scrollState();
		}
	}
};
