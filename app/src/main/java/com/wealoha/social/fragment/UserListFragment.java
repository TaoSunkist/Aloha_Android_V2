package com.wealoha.social.fragment;

import javax.inject.Inject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import com.wealoha.social.BaseFragAct;
import com.wealoha.social.R;
import com.wealoha.social.adapter.UserListAdapter;
import com.wealoha.social.api.common.ApiErrorCode;
import com.wealoha.social.api.notify2.service.UserListService;
import com.wealoha.social.api.user.bean.User;
import com.wealoha.social.inject.Injector;
import com.wealoha.social.utils.DockingBeanUtils;
import com.wealoha.social.utils.XL;
import com.wealoha.social.widget.BaseListApiAdapter.LoadCallback;
import com.wealoha.social.widget.ScrollToLoadMoreListener;

public class UserListFragment extends BaseFragment implements OnItemClickListener, OnClickListener {

	@Inject
	UserListService mUserListService;
	@InjectView(R.id.swipe_list)
	ListView mListView;
	@InjectView(R.id.back)
	ImageView mBackImg;

	private UserListAdapter mUserListAdapter;
	private View mRootView;
	private static final int PAGE_NUM = 20;
	private String notifyid;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.frag_swipemenu_list, container, false);
		Injector.inject(this);
		ButterKnife.inject(mRootView);
		return mRootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		notifyid = getActivity().getIntent().getStringExtra("notifyid");

		mListView.setOnScrollListener(new ScrollToLoadMoreListener(4, new ScrollToLoadMoreListener.Callback() {

			@Override
			public void loadMore() {
				loadNextPage(false);
			}
		}));
		mListView.setOnItemClickListener(this);
		mUserListAdapter = new UserListAdapter((BaseFragAct) getActivity(), mUserListService);
		mListView.setAdapter(mUserListAdapter);
		loadNextPage(false);
	}

	public void loadNextPage(boolean reset) {
		if (mUserListAdapter != null) {
			if (reset) {
				mUserListAdapter.resetState();
			}
			XL.i("USER_LIST_FRAG", "loadNextPage:");
			mUserListAdapter.loadEarlyPage(PAGE_NUM, notifyid, new LoadCallback() {

				@Override
				public void fail(ApiErrorCode code, Exception exception) {
					XL.i("USER_LIST_FRAG", "fail:");
				}

				@Override
				public void success(boolean hasPrev, boolean hasNext) {

				}

				@Override
				public void dataState(int size, boolean isnull) {
					// TODO Auto-generated method stub

				}
			});
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		User user = (User) mListView.getItemAtPosition(position);
		Bundle bundle = new Bundle();
		bundle.putParcelable(User.TAG, DockingBeanUtils.transUser(user));
		((BaseFragAct) getActivity()).startFragment(Profile2Fragment.class, bundle, true);
	}

	@OnClick(R.id.back)
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			getActivity().finish();
			break;
		default:
			break;
		}
	}

}
