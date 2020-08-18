package com.wealoha.social.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

import com.wealoha.social.R;
import com.wealoha.social.ui.base.ScrollToLoadView;

public class ScrollToLoadMoreListView extends ListView implements OnScrollListener {

	private Context mContext;
	private final int loadMoreThreshold = 5;

	private ScrollToLoadView mScrollToLoadView;

	public ScrollToLoadMoreListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		View view = LayoutInflater.from(mContext).inflate(R.layout.list_loader_footer, this, false);
		addFooterView(view);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if (firstVisibleItem + visibleItemCount + loadMoreThreshold == totalItemCount) {
			if (mScrollToLoadView != null) {
				mScrollToLoadView.loadMore();
			}
		}
	}

	public void setScrollToLoadView(ScrollToLoadView scrollToLoadView) {
		mScrollToLoadView = scrollToLoadView;
	}

}
