package com.wealoha.social.view.custom.swipe;

import android.content.Context;
import android.os.Handler;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.wealoha.social.R;
import com.wealoha.social.utils.UiUtils;

public class SwipeRefreshLayoutWaper {

	private SwipeRefreshLayout mRefreshLayout;

	public SwipeRefreshLayoutWaper(Context context, SwipeRefreshLayout refreshLayout) {
		mRefreshLayout = refreshLayout;
		mRefreshLayout.setProgressViewOffset(false, 0, UiUtils.dip2px(context, 70));
		mRefreshLayout.setColorSchemeResources(R.color.light_red);
	}

	public void setRefreshing(boolean arg0, Handler handler) {
		final boolean refresh = arg0;
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				mRefreshLayout.setRefreshing(refresh);
			}
		}, 500);

	}

	public SwipeRefreshLayout getSwipeRefreshLayout() {
		return mRefreshLayout;
	}

}
