package com.wealoha.social.view.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wealoha.social.R;

/**
 * @author:sunkist
 * @see:
 * @since:
 * @description 加载更多的ListView
 * @copyright wealoha.com
 * @Date:2015年7月27日
 */
public class LoadMoreListView extends ListView implements android.widget.AbsListView.OnScrollListener {

	public static final int REFRESH = 0;
	public static final int LOAD = 1;
	private View mFootoerView;
	private OnLoadMoreListener onLoadMoreListener;
	private boolean mIsLoading = true;
	private LayoutInflater mInflater;
	private TextView mLoadFull;
	private TextView mNoMoreData;
	private TextView mMore;
	private ProgressBar mLoading;
	private boolean isLoadFull;
	public static final int LOAD_SUCCESS = 0x1;
	public static final int LOAD_FAULIRE = 0x2;
	public static final int LOAD_NOT_MORE = 0x3;

	public LoadMoreListView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initFooterView(context);
	}

	public LoadMoreListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initFooterView(context);
	}

	public LoadMoreListView(Context context) {
		super(context);
		initFooterView(context);
	}

	private void initFooterView(Context context) {
		mInflater = LayoutInflater.from(context);
		mFootoerView = mInflater.inflate(R.layout.listview_footer, this, false);
		mLoadFull = (TextView) mFootoerView.findViewById(R.id.loadFull);
		mNoMoreData = (TextView) mFootoerView.findViewById(R.id.noData);
		mMore = (TextView) mFootoerView.findViewById(R.id.more);
		mLoading = (ProgressBar) mFootoerView.findViewById(R.id.loading);
		this.addFooterView(mFootoerView);
		this.setOnScrollListener(this);
	}

	/**
	 * @Description:加载更多监听
	 * @param onLoadListener
	 * @see:
	 * @since:
	 * @description
	 * @author: sunkist
	 * @date:2015年7月27日
	 */
	public void setOnLoadMoreListener(OnLoadMoreListener onLoadListener) {
		this.onLoadMoreListener = onLoadListener;
	}

	/**
	 * @Description:加载更多结束后的回调
	 * @see:
	 * @since:
	 * @description
	 * @author: sunkist
	 * @date:2015年7月27日
	 */
	public void onLoadComplete() {
		mIsLoading = false;
	}

	/**
	 * @Description:加载更多
	 * @see:
	 * @since:
	 * @description
	 * @author: sunkist
	 * @date:2015年7月27日
	 */
	public void onLoad() {
		if (onLoadMoreListener != null) {
			onLoadMoreListener.onMoreLoad();
		}
	}

	public interface OnLoadMoreListener {
		public void onMoreLoad();
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		try {
			if (scrollState == OnScrollListener.SCROLL_STATE_IDLE //
					&& !mIsLoading //
					&& view.getLastVisiblePosition() == view.getPositionForView(mFootoerView) //
					&& !isLoadFull) {
				onLoad();
				mIsLoading = true;
			}
		} catch (Exception e) {
		}
	}

	private int pageSize = 10;

	public void complete(int resultSize) {
		if (LOAD_NOT_MORE == resultSize) {
			isLoadFull = true;
			mLoadFull.setVisibility(View.GONE);
			mLoading.setVisibility(View.GONE);
			mMore.setVisibility(View.GONE);
			mNoMoreData.setVisibility(View.VISIBLE);
		} else if (LOAD_SUCCESS == resultSize) {
			isLoadFull = true;
			mLoadFull.setVisibility(View.VISIBLE);
			mLoading.setVisibility(View.GONE);
			mMore.setVisibility(View.GONE);
			mNoMoreData.setVisibility(View.GONE);
		} else if (LOAD_FAULIRE == resultSize) {
			isLoadFull = false;
			mLoadFull.setVisibility(View.GONE);
			mLoading.setVisibility(View.VISIBLE);
			mMore.setVisibility(View.VISIBLE);
			mNoMoreData.setVisibility(View.GONE);
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

	}

}
