package com.wealoha.social.widget;

import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

public class ScrollToLoadListener implements OnScrollListener {

	private Callback callback;

	private final int loadMoreThreshold;

	public interface Callback {

		public void loadMore();

		public void changeTitleBar();
	}

	public ScrollToLoadListener(int thresHold, Callback cb) {
		loadMoreThreshold = thresHold;
		callback = cb;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if (firstVisibleItem + visibleItemCount + loadMoreThreshold == totalItemCount) {
			callback.loadMore();
		}
		callback.changeTitleBar();
	}

}
