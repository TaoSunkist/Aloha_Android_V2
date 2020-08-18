package com.wealoha.social.fragment;

import java.util.Locale;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.os.Handler;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.InjectView;
import butterknife.OnClick;

import com.squareup.otto.Bus;
import com.squareup.picasso.Picasso;
import com.wealoha.social.ActivityManager;
import com.wealoha.social.AsyncLoader;
import com.wealoha.social.BaseFragAct;
import com.wealoha.social.ContextConfig;
import com.wealoha.social.R;
import com.wealoha.social.activity.FragmentWrapperActivity;
import com.wealoha.social.activity.MainAct;
import com.wealoha.social.adapter.ProfileListAdapter;
import com.wealoha.social.adapter.ProfileListAdapter.ViewType;
import com.wealoha.social.beans.Feed;
import com.wealoha.social.beans.FeedResult;
import com.wealoha.social.beans.Result;
import com.wealoha.social.beans.User;
import com.wealoha.social.beans.feed.FeedService;
import com.wealoha.social.beans.match.MatchService;
import com.wealoha.social.beans.user.ProfileData;
import com.wealoha.social.beans.user.ProfileService;
import com.wealoha.social.beans.user.UserService;
import com.wealoha.social.commons.GlobalConstants;
import com.wealoha.social.impl.Listeners;
import com.wealoha.social.store.PopupStore;
import com.wealoha.social.ui.config.ConfigFragment;
import com.wealoha.social.utils.FontUtil;
import com.wealoha.social.utils.FontUtil.Font;
import com.wealoha.social.utils.RegionNodeUtil;
import com.wealoha.social.utils.UiUtils;
import com.wealoha.social.utils.XL;
import com.wealoha.social.view.custom.dialog.ReportBlackAlohaPopup;
import com.wealoha.social.view.custom.listitem.ProfileHeaderHolder;

public class ProfileTestFragment extends BaseFragment implements OnClickListener, LoaderCallbacks<Result<ProfileData>>, SwipeRefreshLayout.OnRefreshListener, ReportBlackAlohaPopup.RefreshData, OnScrollListener {

	@Inject
	RegionNodeUtil regionNodeUtil;
	@Inject
	MatchService matchService;
	@Inject
	UserService userService;
	@Inject
	ProfileService profileService;
	@Inject
	Context context;
	@Inject
	FeedService mFeedService;
	@Inject
	Picasso picasso;
	@Inject
	FontUtil fontUtil;
	@Inject
	Bus bus;
	@InjectView(R.id.profile_back)
	ImageView mBack;
	@InjectView(R.id.profile_username)
	TextView mUsername;
	/** 设置or举报 */
	// @InjectView(R.id.profile_setup_rep)
	// FrameLayout mTools;
	@InjectView(R.id.profile_setup)
	ImageView mSetup;
	@InjectView(R.id.profile_setup_fl)
	FrameLayout mSetupFl;
	// @InjectView(R.id.profile_rep)
	// TextView mReport;
	@InjectView(R.id.profile_list_refresh)
	SwipeRefreshLayout refreshLayout;
	@InjectView(R.id.content_layout)
	ViewGroup mContentView;
	@InjectView(R.id.profile_title_bar)
	RelativeLayout mTitleBar;
	@InjectView(R.id.title_blur)
	FrameLayout mTitleBlur;
	@InjectView(R.id.profile_popup_fl)
	FrameLayout profile_popup_fl;
	// @InjectView(R.id.profile_other_pop_count)
	// TextView mOtherProfilePop;
	private ListView list;
	private ProfileListAdapter profileAdapter;
	private ProfileHeaderHolder profileHeader;
	private View layoutView;
	public User currentUser;
	private static final int LOADER_REFRESH_ONRESUME = 3;
	private static final int LOADER_REFRESH = 2;

	// 从profile开启单例feed
	public static final int OPEN_SINGLETO_FEED_REQUESTCODE = 0x000001;

	private boolean mIsMe;
	private float mTitleAlphaTemp;
	/** 下一页 */
	private final String firstCursor = "notnull";
	private String mCursor = firstCursor;
	private boolean firstPage;
	private boolean feedLoading = true;
	// private boolean scrollLoadMore = false; // 默认不要让滚动触发加载下一页，数据会错乱
	/** 默认加载数量。注意：取太少在三个一排模式下会连发两个请求 */
	private int mCount = 21;
	/** 防止重複加载数据 */

	private BaseFragAct mBaseFragAct;
	private PopupWindow instaSub;

	private Handler instaPopSubHandler;
	private Runnable instaPopSubRunnable;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		layoutView = inflater.inflate(R.layout.profile_list_test, container, false);
		list = (ListView) layoutView.findViewById(R.id.profile_list);
		return layoutView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mBaseFragAct = (BaseFragAct) getActivity();
		if (mBaseFragAct instanceof FragmentWrapperActivity) {
			ActivityManager.push(mBaseFragAct);
		}

		mCursor = firstCursor;
		feedLoading = false;

		initData();
		initView();
		// XL.d(TAG, "加載第一頁數據");
		loadFeedList("onViewCreated");

	}

	private User getUser() {
		if (currentUser == null || currentUser.me) {
			return contextUtil.getCurrentUser();
		}
		return currentUser;
	}

	public void initData() {
		Bundle bundle = getArguments();
		if (bundle != null) {
			currentUser = (User) bundle.getParcelable(User.TAG);
		}
		if (currentUser == null) {
			currentUser = contextUtil.getCurrentUser();
		}
		mIsMe = getUser().me;
		// mIsMe = contextUtil.getCusrrentUser().id.equals(currentUser.id);
		if (mIsMe && getActivity() instanceof MainAct) {
			currentUser = contextUtil.getCurrentUser();
			mSetup.setImageResource(R.drawable.profile_setting);
		} else {
			// FIXME 国际化
			mSetupFl.setVisibility(View.GONE);
		}
		// Log.i("PROFILE_TEST", "----------" + contextUtil.getForegroundAct());
		if (contextUtil.getForegroundAct() instanceof MainAct) {
			mBack.setVisibility(View.GONE);
		} else {
			mBack.setVisibility(View.VISIBLE);
		}
		mSetup.setOnClickListener(this);

		// 名字转成风骚的大写~~
		String upper = null;
		if (!TextUtils.isEmpty(currentUser.name)) {
			upper = currentUser.name.toUpperCase(Locale.getDefault());
		}
		mUsername.setText(upper);
	}

	public void initView() {

		// 字体
		fontUtil.changeFonts((ViewGroup) layoutView, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
		// title 背景色
		// mTitleBarBg = new ColorDrawable(0xf9f9f9);
		// mTitleBarBg.setAlpha(255);
		// mTitleBlur.setBackground(mTitleBarBg);

		list.setOnScrollListener(this);
		mIsMe = contextUtil.getCurrentUser().id.equals(getUser().id);
		// 下拉刷新控件
		refreshLayout.setOnRefreshListener(this);
		refreshLayout.setProgressViewOffset(false, 0, UiUtils.dip2px(context, 70));
		refreshLayout.setColorSchemeResources(R.color.light_red);
		profileHeader = new ProfileHeaderHolder(mBaseFragAct, this);
		profileAdapter = new ProfileListAdapter(list, this, getUser(), profileHeader, ViewType.Triple, FeedFragment.FEED_TYPE_DEFAULT);
		// list.addHeaderView(profileHeader.getView(profileAdapter,
		// getUser(), true));
		list.setAdapter(profileAdapter);

		mTitleBar.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				v.performClick();
				return true;
			}
		});
		openInstagramSub();
	}

	@Override
	public void onResume() {
		byPostIdDelFeed();
		try {
			bus.post(new Listeners.MonitorMainUiBottomTagPostion(3));
		} catch (Throwable e) {
		}
		Bundle bundle = new Bundle();
		User user = getUser();
		if (mBaseFragAct != null) {
			boolean isRefreshHeadIcon = BaseFragAct.isRefreshHeadIcon;
			if (isRefreshHeadIcon) {
				BaseFragAct.isRefreshHeadIcon = !isRefreshHeadIcon;
				bundle.putString("userId", user.id);
				getLoaderManager().restartLoader(LOADER_REFRESH_ONRESUME, bundle, ProfileTestFragment.this);
				profileHeader.loadHeadCache(1);
			}
		}
		if (user != null) {
			bundle.putString("userId", user.id);
			getLoaderManager().restartLoader(LOADER_REFRESH, bundle, this);
		}
		// if (profileAdapter != null) {
		// profileAdapter.notifyDataSetChanged();
		// }
		if (mIsMe && mUsername != null && contextUtil.getCurrentUser() != null) {
			mUsername.setText(contextUtil.getCurrentUser().name.toUpperCase(Locale.getDefault()));
		}
		super.onResume();
	}

	/**
	 * @Description:通过POSTID刷新删除自己的Feed.
	 * @see:
	 * @since:
	 * @description
	 * @author: sunkist
	 * @date:2015-2-9
	 */
	private void byPostIdDelFeed() {
		if (currentUser != null && currentUser.me && !TextUtils.isEmpty(GlobalConstants.AppConstact.mDelPostId)) {
			if (mResult != null) {
				Feed feed = new Feed();
				feed.postId = GlobalConstants.AppConstact.mDelPostId;
				int position = mResult.list.indexOf(feed);
				if (position != -1) {
					mResult.list.remove(position);
					profileAdapter.clearData();
					profileAdapter.notifyDataSetChanged(mResult);
				}
			}
			GlobalConstants.AppConstact.mDelPostId = null;
		}
	}

	public void openInstagramSub() {

		if (!mIsMe || !(getActivity() instanceof MainAct)) {
			return;
		}
		if (ContextConfig.getInstance().getBooleanWithFilename("instagram_sub")) {
			return;
		}
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.pop_insta_sub, new LinearLayout(context), false);
		ImageView sub = (ImageView) view.findViewById(R.id.insta_rectangle);

		float margin = (UiUtils.dip2px(context, 48) - getResources().getDrawable(R.drawable.insta_rectangle).getIntrinsicWidth()) / 2 - UiUtils.dip2px(context, 10);

		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.setMargins((int) margin, 0, 0, 0);
		params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		sub.setLayoutParams(params);
		instaSub = new PopupWindow(view, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		if (instaSub == null || mSetup == null) {
			return;
		}

		// 不要这样写
		try {

			instaPopSubHandler = new Handler();
			instaPopSubRunnable = new Runnable() {

				@Override
				public void run() {
					if (!isAdded() || instaSub == null || mSetup == null) {
						return;
					}
					instaSub.showAsDropDown(mSetup, -250, 0);
					instaSub.update();
				}
			};
			instaPopSubHandler.postDelayed(instaPopSubRunnable, 350);
		} catch (NullPointerException e) {
			// 如果初始化失败,就下一次在现实;
		}
	}

	@Override
	public void onPause() {
		super.onPause();

		// scrollLoadMore = false;
		if (instaSub != null && instaSub.isShowing()) {
			instaSub.dismiss();
		}

		if (instaPopSubHandler != null && instaPopSubRunnable != null) {
			instaPopSubHandler.removeCallbacks(instaPopSubRunnable);
		}
	}

	public FeedResult mResult;

	public void loadFeedList(String from) {
		// 锁
		if (feedLoading || profileAdapter == null) {
			return;
		} else {
			feedLoading = true;
		}
		// 初始化cursor
		if (TextUtils.isEmpty(mCursor)) {
			return;
		} else if (firstCursor.equals(mCursor)) {
			mCursor = null;
			firstPage = true;
		}

		XL.i("CURSOR_TEST", "from:" + from);
		mFeedService.userFeed(getUser().id, mCursor, mCount, new Callback<Result<FeedResult>>() {

			@Override
			public void success(Result<FeedResult> result, Response arg1) {

				if (result != null && result.isOk()) {
					// XL.d("mFeedService", mResult)
					result.data.resetFeed(currentUser);
					if (firstPage) {
						mResult = result.data;
						profileAdapter.notifyTopDataSetChanged(result.data);
						// 允許下拉刷新
						// scrollLoadMore = true;
						firstPage = false;
					} else {
						mResult.commentCountMap.putAll(result.data.commentCountMap);
						mResult.imageMap.putAll(result.data.imageMap);
						mResult.likeCountMap.putAll(result.data.likeCountMap);
						mResult.userMap.putAll(result.data.userMap);
						mResult.list.addAll(result.data.list);
						// mResult.videoMap.putAll(result.data.videoMap);
						profileAdapter.notifyDataSetChanged(result.data);
					}
					XL.i("CURSOR_TEST", "current:" + mCursor);
					mCursor = result.data.nextCursorId;
					XL.i("CURSOR_TEST", "next:" + mCursor);
				}
				feedLoading = false;

				if (refreshLayout != null) {
					refreshLayout.setRefreshing(false);
				}
			}

			@Override
			public void failure(RetrofitError e) {
				XL.i("loadFeedList", "失败", e);
				feedLoading = false;
				if (refreshLayout != null) {
					refreshLayout.setRefreshing(false);
				}
			}
		});
	}

	/**
	 * @Title: onClick</p>
	 * @Description: profile三种模式的切换
	 * @param v
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@OnClick({ R.id.profile_back, R.id.profile_setup, R.id.profile_setup_fl, R.id.profile_popup_fl })
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.profile_popup_fl:
			controlOtherUser();
			break;
		case R.id.profile_setup_fl:
		case R.id.profile_setup:
			// FIXME 逻辑提取成方法
			if (mIsMe && getActivity() instanceof MainAct) {
				ContextConfig.getInstance().putBooleanWithFilename("instagram_sub", true);
				BaseFragAct bt = (BaseFragAct) contextUtil.getForegroundAct();
				if (bt != null) {
					bt.startFragmentForResult(ConfigFragment.class, null, true, //
							GlobalConstants.AppConstact.IS_REFRESH_PROFILE_HEAD_ICON,//
							R.anim.left_in, R.anim.stop);
				}
			}
			break;
		case R.id.profile_back:
			backToGoal();
			break;
		}
	}

	private void backToGoal() {
		if (!(mBaseFragAct instanceof MainAct)) {
			mBack.setVisibility(View.GONE);
			getActivity().finish();
		} else if (mBaseFragAct instanceof FragmentWrapperActivity) {
			ActivityManager.pop();
		}
	}

	private AsyncLoader<Result<ProfileData>> loaderWapper(final String userId) {
		return new AsyncLoader<Result<ProfileData>>(context) {

			@Override
			public Result<ProfileData> loadInBackground() {
				try {
					return profileService.view(userId);
				} catch (Exception e) {
					// XL.w(TAG, "Aloha失败", e);
					return null;
				}
			}
		};
	}

	@Override
	public Loader<Result<ProfileData>> onCreateLoader(int id, Bundle bundle) {
		final String userId = bundle.getString("userId");
		if (id == LOADER_REFRESH_ONRESUME || id == LOADER_REFRESH) {
			return loaderWapper(userId);
		}
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Result<ProfileData>> loader, Result<ProfileData> result) {
		if (result == null) {
			refreshLayout.setRefreshing(false);
			Toast.makeText(context, getResources().getString(R.string.network_error), Toast.LENGTH_LONG).show();
			return;
		}

		if (loader.getId() == LOADER_REFRESH_ONRESUME) {
			if (result.data != null && result.data.user != null) {
				if (result.data.user.me) {
					contextUtil.setCurrentUser(result.data.user);
				}
				currentUser = result.data.user;
				// 更新profile header
				if (profileHeader != null) {
					profileHeader.refresh(getUser());
				}
			}
			// profileAdapter.notifyHeaderDataSetChanged(new
			// ProfileHeaderHolder().getView(profileAdapter,
			// result.data.user, mIsMe));
		} else if (loader.getId() == LOADER_REFRESH) {
			if (result.data != null && result.data.user != null) {
				if (result.data.user.me) {
					contextUtil.setCurrentUser(result.data.user);
				}
				currentUser = result.data.user;
				// 更新profile header
				if (profileHeader != null) {
					profileHeader.refreshExceptIcon(getUser());
				}
			}
		}
		// 延时关闭
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				if (refreshLayout != null) {
					refreshLayout.setRefreshing(false);
				}
			}
		}, 500);

	}

	/**
	 * @Title: refreshData
	 * @Description: ReportBlackAlohaPopup回调 ，修改视图
	 * @see com.wealoha.social.view.custom.dialog.ReportBlackAlohaPopup.RefreshData#refreshData()
	 */
	@Override
	public void refreshData() {
		profileHeader.refreshHeader(getUser().id);
	}

	@Override
	public void onLoaderReset(Loader<Result<ProfileData>> loader) {
	}

	/**
	 * @Title: onRefresh
	 * @Description: 下拉刷新
	 *  androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener#onRefresh()
	 */
	@Override
	public void onRefresh() {
		Bundle bundle = new Bundle();
		bundle.putString("userId", getUser().id);
		// getLoaderManager().restartLoader(LOADER_REFRESH, bundle, this);
		firstPage = true;
		feedLoading = false;
		mCursor = "notnull";
		mResult = null;
		if (profileHeader != null) {
			profileHeader.refreshIcon(getUser().id);
		}

		if (profileAdapter != null) {
			profileAdapter.clearData();
		}
		// 加把鎖，避免重入
		loadFeedList("onViewCreated");
		// XL.d(TAG, "onRefresh()");
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// view.getc
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		// header渐变效果
		if (profileHeader != null && mTitleBar != null && mTitleBlur != null) {
			mTitleAlphaTemp = profileHeader.headerAlpha();
			mTitleBlur.setAlpha(mTitleAlphaTemp);
		}

		if (!feedLoading && firstVisibleItem + visibleItemCount + 3 > totalItemCount) {
			// 當前顯示的還差3個就到底了才加載
			loadFeedList("onScroll");
		}
	}

	/**
	 * @Title: changeListDividerHeight
	 * @Description: 改变list item 的间距
	 * @return void 返回类型
	 * @throws
	 */
	public void changeListDividerHeight(int divider) {
		list.setDividerHeight(divider);
	}

	/**
	 * @Description:对其他用户进行操作.
	 * @see:
	 * @since:
	 * @description
	 * @author: sunkist
	 * @date:2015-1-12
	 */
	private void controlOtherUser() {
		if (currentUser.me) {
			new PopupStore(regionNodeUtil).showShareProfilePopup(mBaseFragAct, currentUser, null);
		} else {
			new PopupStore(regionNodeUtil).showShareProfilePopup(mBaseFragAct, contextUtil.getCurrentUser(), currentUser);
		}
	}

	/**
	 * @Title: setResultForUnaloha
	 * @Description: 由profile页进入喜欢列表， 在喜欢列表中进入某人profile后解除aloha， 返回人气列表要刷新人气列表
	 * @param userid
	 *            设定文件
	 */
	public void setResultForUnaloha(String userid) {
		Intent resultIntent = new Intent();
		resultIntent.putExtra("userid", userid);
		Activity activity = getActivity();
		if (activity != null && !activity.isFinishing()) {
			activity.setResult(Activity.RESULT_OK, resultIntent);
		}
	}

}
