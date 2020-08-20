package com.wealoha.social.ui.feeds;

import java.util.List;

import android.view.View.OnTouchListener;
import android.widget.AbsListView.OnScrollListener;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.wealoha.social.activity.FragmentWrapperActivity.ActivityResultCallback;
import com.wealoha.social.beans.HashTag;
import com.wealoha.social.ui.base.ScrollToLoadView;
import com.wealoha.social.view.custom.dialog.ListItemDialog.ListItemCallback;

/**
 * @author:sunkist
 * @see:
 * @since:
 * @description
 * @copyright wealoha.com
 * @Date:2015年7月21日
 */
public interface IFeedsView extends ScrollToLoadView, SwipeRefreshLayout.OnRefreshListener,//
		ListItemCallback, ActivityResultCallback, OnTouchListener, OnScrollListener {

	public void showAdvert(boolean isSuccess);

	public void showTopic(List<HashTag> t);

}
