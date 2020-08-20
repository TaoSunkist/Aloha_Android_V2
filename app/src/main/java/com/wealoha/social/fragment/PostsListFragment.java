package com.wealoha.social.fragment;

import javax.inject.Inject;

import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;

import com.wealoha.social.R;
import com.wealoha.social.adapter.profile.Profile2Adapter;
import com.wealoha.social.adapter.profile.Profile2ImagesAdapter;
import com.wealoha.social.beans.ApiErrorCode;
import com.wealoha.social.api.PraisedPostService;
import com.wealoha.social.api.TagedPostService;
import com.wealoha.social.utils.ContextUtil;
import com.wealoha.social.utils.FontUtil.Font;
import com.wealoha.social.utils.UiUtils;
import com.wealoha.social.utils.XL;
import com.wealoha.social.view.custom.popup.LoadingPopup;
import com.wealoha.social.widget.BaseListApiAdapter.LoadCallback;
import com.wealoha.social.widget.ScrollToLoadMoreListener;
import com.wealoha.social.widget.ScrollToLoadMoreListener.Callback;

public class PostsListFragment extends BaseFragment implements OnClickListener, SwipeRefreshLayout.OnRefreshListener {

	@Inject
	TagedPostService tagedService;
	@Inject
	PraisedPostService praisedService;
	@Inject
	ContextUtil contextUtil;

	@InjectView(R.id.post_list)
	ListView listView;
	@InjectView(R.id.back_tv)
	ImageView backTv;
	@InjectView(R.id.refresh_list)
	SwipeRefreshLayout refreshList;
	@InjectView(R.id.title_tv)
	TextView titleTv;
	private Profile2Adapter pro2Adt;
	private Profile2ImagesAdapter pro2ImgAdt;
	public final static int TAGED = 0;
	public final static int PRAISED = 1;
	public final static String TYPED_KEY = "TYPED_KEY";
	private int listType = TAGED;

	private View rootView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.frag_postlist, container, false);
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		refreshList.setOnRefreshListener(this);// 下拉刷新
		refreshList.setColorSchemeResources(R.color.light_red);
		refreshList.setProgressViewOffset(false, 0, UiUtils.dip2px(context, 70));// 下拉刷新控件的位置
		popup = new LoadingPopup(getActivity());
		listType = getArguments().getInt(TYPED_KEY, TAGED);
		fontUtil.changeViewFont(titleTv, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
		if (listType == TAGED) {
			pro2Adt = new Profile2Adapter(this, tagedService);
			titleTv.setText(R.string.taged_post);
		} else if (listType == PRAISED) {
			pro2Adt = new Profile2Adapter(this, praisedService);
			titleTv.setText(R.string.praised_post);
		}

		listView.setOnScrollListener(new ScrollToLoadMoreListener(6, new Callback() {

			@Override
			public void loadMore() {
				loadNextPage(false);
			}
		}));
		switchRefreshView(true);
		loadNextPage(false);
	}

	private void loadNextPage(boolean reset) {
		if (reset) {
			pro2Adt.resetState();
		}
		pro2Adt.loadEarlyPage(50, contextUtil.getCurrentUser().getId(), new LoadCallback() {

			@Override
			public void success(boolean hasEarly, boolean hasLate) {
				if (!isVisible()) {
					return;
				}
				switchRefreshView(false);
				if (pro2ImgAdt == null) {
					pro2ImgAdt = new Profile2ImagesAdapter(pro2Adt.getListData(), PostsListFragment.this);
					listView.setAdapter(pro2ImgAdt);
				} else {
					pro2ImgAdt.notifyDataSetChanged();
				}

				for (int i = 0; i < pro2Adt.getListData().size(); i++) {
					XL.i("post_info", "video:" + pro2Adt.getListData().get(i).getCommonVideo());
				}
			}

			@Override
			public void fail(ApiErrorCode code, Exception exception) {
				// showDialog(false);
				switchRefreshView(false);
			}

			@Override
			public void dataState(int size, boolean isnull) {
			}
		});
	}

	@OnClick({ R.id.back_tv })
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_tv:
			getActivity().finish();
			break;

		default:
			break;
		}
	}

	@Override
	public void onRefresh() {
		loadNextPage(true);
	}

	private synchronized void switchRefreshView(boolean show) {
		if (refreshList != null) {
			if (show) {
				refreshList.setRefreshing(true);
			} else {
				// 延时一会再关闭，好看
				refreshList.postDelayed(new Runnable() {

					@Override
					public void run() {
						if (refreshList != null) {
							refreshList.setRefreshing(false);
						}
					}
				}, 500);
			}
		}
	}
}
