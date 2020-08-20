package com.wealoha.social.fragment;

import java.util.Locale;

import javax.inject.Inject;

import android.app.Activity;
import android.content.Intent;
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
import butterknife.InjectView;
import butterknife.OnClick;

import com.wealoha.social.BaseFragAct;
import com.wealoha.social.ContextConfig;
import com.wealoha.social.R;
import com.wealoha.social.activity.MainAct;
import com.wealoha.social.adapter.feed.BaseFeedHolder;
import com.wealoha.social.adapter.feed.FeedHolder;
import com.wealoha.social.adapter.feed.VideoFeedHolder;
import com.wealoha.social.adapter.profile.Profile2Adapter;
import com.wealoha.social.adapter.profile.Profile2BlackAdapter;
import com.wealoha.social.adapter.profile.Profile2HeaderHolder;
import com.wealoha.social.adapter.profile.Profile2HeaderHolder.ProfileHeader2FragCallback;
import com.wealoha.social.adapter.profile.Profile2ImageHolder;
import com.wealoha.social.adapter.profile.Profile2ImagesAdapter;
import com.wealoha.social.adapter.profile.Profile2InfoAdapter;
import com.wealoha.social.api.common.ApiErrorCode;
import com.wealoha.social.api.profile.service.Profile2Service;
import com.wealoha.social.beans.User2;
import com.wealoha.social.commons.GlobalConstants;
import com.wealoha.social.store.PopupStore;
import com.wealoha.social.ui.config.ConfigFragment;
import com.wealoha.social.utils.DockingBeanUtils;
import com.wealoha.social.utils.FontUtil;
import com.wealoha.social.utils.FontUtil.Font;
import com.wealoha.social.utils.RegionNodeUtil;
import com.wealoha.social.utils.UiUtils;
import com.wealoha.social.utils.XL;
import com.wealoha.social.widget.BaseListApiAdapter.LoadCallback;

public class Profile2Fragment extends BaseFragment implements ProfileHeader2FragCallback, SwipeRefreshLayout.OnRefreshListener,
		OnTouchListener, OnClickListener, OnScrollListener {

	@Inject
	Profile2Service pro2Service;
	@Inject
	RegionNodeUtil regionNodeUtil;
	@Inject
	FontUtil fontUtil;

	@InjectView(R.id.profile_list)
	ListView mProList;
	@InjectView(R.id.title_blur)
	FrameLayout mTitleLayout;
	@InjectView(R.id.refresh_layout)
	SwipeRefreshLayout mRefreshLayout;
	@InjectView(R.id.config_root)
	FrameLayout mConfigLayout;
	@InjectView(R.id.setup)
	ImageView mConfig;
	@InjectView(R.id.back)
	ImageView mBack;
	@InjectView(R.id.username)
	TextView mUsername;
	@InjectView(R.id.popup_fl)
	FrameLayout mStartMenuPopup;
	@InjectView(R.id.profile_title_bar)
	RelativeLayout mTitleRootView;

	private View rootView;
	private PopupWindow instaSub;

	private Profile2Adapter pro2Adt;
	private Profile2ImagesAdapter pro2ImgAdt;
	private Profile2InfoAdapter pro2InfoAdt;
	private Profile2BlackAdapter pro2BlackAdt;
	private Profile2HeaderHolder proHeadHld;
	private User2 mUser2;

	private static final int PAGE_SIZE = 30;

	/** profile 的 类型 */
	public static final int GRID_IMG_PROFILE = 1;
	public static final int LIST_IMG_PROFILE = 2;
	public static final int INFO_PROFILE = 3;
	public static final int BLACK_PROFILE = 4;
	/****************/

	private int viewtype = GRID_IMG_PROFILE;
	public static final int PROFILE_REFRESH_ICON = 4;
	// 从profile开启单例feed
	public static final int OPEN_SINGLETO_FEED_REQUESTCODE = 1;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.frag_profile, container, false);
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		getUserData();
		initHeaderView();
		// 这样做可以先把视图显示出来，等网络请求完毕再重绘视图，不会出现空白
		if (pro2ImgAdt == null) {
			mProList.setAdapter(new Profile2ImagesAdapter(null, this));// 首次初始化的时候
		}
		if (pro2Adt == null) {
			XL.i("PROFILE_TEST_CHANGE", "NOT FIRST");
			pro2Adt = new Profile2Adapter(this, pro2Service);
			loadFirstPage();
		} else {
			XL.i("PROFILE_TEST_CHANGE", "NOT FIRST");
			initView();
			if (ContextConfig.getInstance().getBooleanWithFilename(GlobalConstants.GlobalCacheKeys.POST_NEW_FEED)) {
				loadFirstPage();
			}
		}

		mProList.setOnScrollListener(this);
		mRefreshLayout.setOnRefreshListener(this);// 下拉刷新
		mRefreshLayout.setProgressViewOffset(false, 0, UiUtils.dip2px(context, 70));// 下拉刷新控件的位置
		mRefreshLayout.setColorSchemeResources(R.color.light_red);// 下拉刷新的颜色
		initTitlebar();
		initOnTouchEvent();
		openInstagramSub();

		if (mUser2.hasPrivacy()) {
			blackUserCallback();
		}
	}

	/***
	 * 根据当前profile 的类型来填充listview 的数据
	 * 
	 * @return void
	 */
	public void initView() {
		XL.i("PROFILE_TEST_CHANGE", "initView");
		if (viewtype == GRID_IMG_PROFILE) {
			changeToGridImg();
		} else if (viewtype == LIST_IMG_PROFILE) {
			changeToListImg();
		} else if (viewtype == INFO_PROFILE) {
			changeToInfo();
		}
	}

	/***
	 * 初始化复合控件的单击事件
	 * 
	 * @return void
	 */
	private void initOnTouchEvent() {
		mConfigLayout.setOnTouchListener(this);
		mStartMenuPopup.setOnClickListener(this);
		mUsername.setOnTouchListener(this);
	}

	/***
	 * 初始化标题栏上的按钮事件和文本信息
	 * 
	 */
	private void initTitlebar() {
		// 字体
		fontUtil.changeFonts(mTitleRootView, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
		if (mUser2.isMe()) {// 按钮
			if (getActivity() instanceof MainAct) {
				mConfigLayout.setVisibility(View.VISIBLE);
				mBack.setVisibility(View.GONE);
			} else {
				mConfigLayout.setVisibility(View.GONE);
				mBack.setVisibility(View.VISIBLE);
			}
		} else {
			mConfigLayout.setVisibility(View.GONE);
			mBack.setVisibility(View.VISIBLE);
		}
		// 名字转成风骚的大写~~
		String upper = mUser2.getName();
		if (!TextUtils.isEmpty(upper)) {
			upper = upper.toUpperCase(Locale.getDefault());
		}
		mUsername.setText(upper);// 文本
	}

	/***
	 * ins同步功能引导层
	 * 
	 */
	public void openInstagramSub() {

		if (!mUser2.isMe() || !(getActivity() instanceof MainAct)) {
			return;
		}
		if (ContextConfig.getInstance().getBooleanWithFilename("instagram_sub")) {
			return;
		}
		View view = getActivity().getLayoutInflater().inflate(R.layout.pop_insta_sub, new LinearLayout(context), false);
		ImageView sub = (ImageView) view.findViewById(R.id.insta_rectangle);

		float margin = (UiUtils.dip2px(context, 48) - getResources().getDrawable(R.drawable.insta_rectangle).getIntrinsicWidth()) / 2 - UiUtils.dip2px(context, 10);

		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.setMargins((int) margin, 0, 0, 0);
		params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		sub.setLayoutParams(params);
		instaSub = new PopupWindow(view, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		if (instaSub == null || mConfigLayout == null) {
			return;
		}

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				if (isVisible()) {
					instaSub.showAsDropDown(mConfig, -250, 0);
					instaSub.update();
				}
			}
		}, 400);
	}

	/***
	 * 关闭ins同步功能引导层
	 * 
	 */
	private void closeInsPopup() {
		if (instaSub != null && instaSub.isShowing()) {
			instaSub.dismiss();
			ContextConfig.getInstance().putBooleanWithFilename("instagram_sub", true);
		}
	}

	/***
	 * 接收从开启这个frag的组件那里传来的User 数据
	 * 
	 * @return void
	 */
	private void getUserData() {
		if (mUser2 == null) {
			Object userobj = getArguments().get(com.wealoha.social.beans.User.TAG);
			if (userobj == null) {
				userobj = getArguments().get(User2.TAG);
			}

			if (userobj instanceof User2) {
				mUser2 = (User2) userobj;
			} else {
				mUser2 = User2.formOldUser((com.wealoha.social.beans.User) userobj);
			}

			if (mUser2.getPostCount() == 0) {
				viewtype = INFO_PROFILE;
			}
		}
		
	}

	private void loadNextPage(boolean reset) {
		if (reset) {
			pro2Adt.resetStateDelay();
		}
		pro2Adt.loadEarlyPage(PAGE_SIZE, mUser2.getId(), new LoadCallback() {

			@Override
			public void success(boolean hasEarly, boolean hasLate) {
				if (isVisible()) {
					fillImageAdtData();
					// initView();
					closeRefreshLayout();
					ContextConfig.getInstance().putBooleanWithFilename(GlobalConstants.GlobalCacheKeys.POST_NEW_FEED, false);
				}
			}

			@Override
			public void fail(ApiErrorCode code, Exception exception) {
				if (isVisible()) {
					closeRefreshLayout();
				}
			}

			@Override
			public void dataState(int size, boolean isnull) {
			}
		});
	}

	private void loadFirstPage() {
		pro2Adt.resetStateDelay();
		pro2Adt.loadEarlyPage(PAGE_SIZE, mUser2.getId(), new LoadCallback() {

			@Override
			public void success(boolean hasEarly, boolean hasLate) {
				if (isVisible()) {
					// fillImageAdtData();
					closeRefreshLayout();
					ContextConfig.getInstance().putBooleanWithFilename(GlobalConstants.GlobalCacheKeys.POST_NEW_FEED, false);
				}
			}

			@Override
			public void fail(ApiErrorCode code, Exception exception) {
				if (isVisible()) {
					closeRefreshLayout();
				}
			}

			@Override
			public void dataState(int size, boolean isnull) {
				if (!isVisible()) {
					return;
				}
				if (size == 0 || isnull) {
					viewtype = INFO_PROFILE;
				}
				initView();
			}
		});
	}

	/***
	 * 初始化 多宫格图片模式的adapter， 因为这个adt 的数据来自 {@link #pro2Adt}, 所以应该在 {#loadNextPage(User)}的success 回调中执行
	 * 
	 * @return void
	 */
	private void fillImageAdtData() {
		if (pro2Adt == null) {
			return;
		}
		if (pro2ImgAdt == null) {
			pro2ImgAdt = new Profile2ImagesAdapter(pro2Adt.getListData(), this);
			// mProList.setAdapter(pro2ImgAdt);
		}
		pro2ImgAdt.notifyDataSetChanged();

		if (viewtype == INFO_PROFILE) {
			changeToInfo();
		}
	}

	/***
	 * 初始化 profile 的个人信息和操作视图, 4.4以前必须要在list.setadapter 之前调用，每次进入profile都刷新
	 * 
	 *  user
	 *            当前profile 的用户
	 * @return void
	 */
	private void initHeaderView() {
		if (proHeadHld == null) {
			proHeadHld = new Profile2HeaderHolder(this, this, mProList);
			mProList.addHeaderView(proHeadHld.resetViewData(mUser2));
		} else {
			mProList.addHeaderView(proHeadHld.resetViewData(mUser2));
		}
		proHeadHld.refreshHeader(mUser2.getId());
	}

	/***
	 * 随着listview 的滑动，title bar 渐渐改变透明度
	 * 
	 * @return void
	 */
	private void changeTitleBarAlpha() {
		if (proHeadHld != null) {
			mTitleLayout.setAlpha(proHeadHld.getParentTitleAlpha());
		}
	}

	@Override
	public void changeToGridImg() {
		if (pro2Adt == null) {
			return;
		}
		mProList.setDividerHeight(getResources().getDimensionPixelSize(R.dimen.profile_list_divider));
		viewtype = GRID_IMG_PROFILE;
		if (pro2ImgAdt != null) {
			pro2ImgAdt.notifyDataSetChanged();
		} else {
			pro2ImgAdt = new Profile2ImagesAdapter(pro2Adt.getListData(), this);
		}
		mProList.setAdapter(pro2ImgAdt);
	}

	@Override
	public void changeToListImg() {
		viewtype = LIST_IMG_PROFILE;
		mProList.setDividerHeight(getResources().getDimensionPixelSize(R.dimen.profile_list_divider_feed_offset));
		mProList.setAdapter(pro2Adt);
	}

	@Override
	public void changeToInfo() {
		viewtype = INFO_PROFILE;
		// if (pro2InfoAdt == null) {
		pro2InfoAdt = new Profile2InfoAdapter(mUser2);
		// } else {
		// pro2InfoAdt.notifyDataSetChanged();
		// }
		mProList.setAdapter(pro2InfoAdt);
	}

	/***
	 * 
	 * 被加入黑名单模式
	 * 
	 */
	public void blackUserCallback() {
		viewtype = BLACK_PROFILE;
		if (pro2BlackAdt == null) {
			pro2BlackAdt = new Profile2BlackAdapter(this);
		}
		mProList.setAdapter(pro2BlackAdt);
	}

	@Override
	public void onRefresh() {
		checkItemStopPlay();
		refreshUserIco();
		loadNextPage(true);
	}

	/**
	 * 关闭下拉控件动画
	 * 
	 * @return void
	 */
	public void closeRefreshLayout() {
		if (mRefreshLayout != null) {
			mRefreshLayout.setRefreshing(false);
		}
	}

	/***
	 * 因为界面可点击的按钮多为复合控件实现，所以为了保证点击事件能够灵敏的呗反应，在复合控件的父容器上监听touch 事件
	 * 
	 * @param v
	 * @param event
	 */
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() != MotionEvent.ACTION_UP) {
			return true;
		}

		switch (v.getId()) {
		case R.id.config_root:// 设置
			openConfigFrag();
			break;
		case R.id.username:
			listViewScrollTop();
			return true;
		default:
			break;
		}
		v.performClick();
		return false;
	}

	/***
	 * 非复合控件都响应单击事件
	 * 
	 * @param v
	 * @return
	 */
	@OnClick({ R.id.back })
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			getActivity().finish();
			break;
		case R.id.popup_fl:
			startMenuPopup();
			break;
		default:
			break;
		}
	}

	/***
	 * 開啟個人設置頁面
	 * 
	 */
	private void openConfigFrag() {
		closeInsPopup();
		((BaseFragAct) getActivity()).startFragmentForResult(ConfigFragment.class, null, true, //
																PROFILE_REFRESH_ICON,//
																R.anim.left_in, R.anim.stop);
	}

	/***
	 * 开启多功能弹出层
	 * 
	 */
	private void startMenuPopup() {
		if (mUser2.isMe()) {
			new PopupStore(regionNodeUtil).showShareProfilePopup((BaseFragAct) getActivity(), DockingBeanUtils.transUser(mUser2), null);
		} else {
			new PopupStore(regionNodeUtil).showShareProfilePopup((BaseFragAct) getActivity(), contextUtil.getCurrentUser(), DockingBeanUtils.transUser(mUser2));
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		closeInsPopup();
	}

	@Override
	public void alohaCallback(User2 user2) {
		XL.i("UNALOHA_SOMEONE", "------userid:" + mUser2.getId());
		mUser2 = user2;
		Intent resultIntent = new Intent();
		resultIntent.putExtra("userid", user2.getId());
		if (getActivity() != null && !getActivity().isFinishing()) {
			getActivity().setResult(Activity.RESULT_OK, resultIntent);
		}
		initTitlebar();
	}

	@Override
	public void onResume() {
		super.onResume();
		// refreshUserIco();
	}

	@Override
	public void onActivityResultCallback(int requestcode, int resultcode, Intent result) {
		XL.i("CONFIG_FRAG_RESULT", "profile2 fragment");
		switch (requestcode) {
		case PROFILE_REFRESH_ICON:
			if (resultcode != Activity.RESULT_OK) {
				return;
			}
			refreshUserIco();
			break;
		case OPEN_SINGLETO_FEED_REQUESTCODE:// 从多宫格页进入单例照片页后，在单例照片页删除feed返回profile，
			if (resultcode != OPEN_SINGLETO_FEED_REQUESTCODE) {
				return;
			}
			if (pro2Adt != null && pro2ImgAdt != null) {
				pro2Adt.removeItem(result.getIntExtra(Profile2ImageHolder.HOLDER_POSITION_KEY, -1));
				pro2ImgAdt.notifyDataSetChanged();
			}
			break;
		case FeedHolder.START_FEEDCOMMENT_REQUESTCODE:
			if (frag2HolderCallback != null && result != null) {
				frag2HolderCallback.commentCallback(result.getIntExtra(FeedHolder.COMMENT_COUNT_DATA_KEY, 0));
			}
			break;
		default:
			break;
		}
	}

	public void refreshUserIco() {
		// XL.i("CONFIG_FRAG_RESULT", "refreshUserIco");
		if (proHeadHld != null) {
			proHeadHld.refreshHeader(mUser2.getId());
		}
	}

	private int visibleItemCount;

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (scrollState == SCROLL_STATE_IDLE) {
			// 手指擡起的時候開始遍歷當前所有顯示的Item，包括ImageHolder
			// FIXME 播放的控制逻辑应该提取 ，feed 和profile是一样的
			checkItemStartPlay();
		}
	}

	/**
	 * @Description:遍历显示的所有顯示的Item中可以被播放的Item
	 * @see:
	 * @since:
	 * @description
	 * @author: sunkist
	 * @date:2015年4月7日
	 */
	private void checkItemStartPlay() {
		for (int i = 0; i < visibleItemCount; i++) {
			View listItem = mProList.getChildAt(i);
			if (listItem == null) {
				return;
			}
			// 获取当前的Holder
			if (listItem.getTag() != null && listItem.getTag() instanceof BaseFeedHolder) {
				BaseFeedHolder baseFeedHolder = (BaseFeedHolder) listItem.getTag();
				FeedHolder feedHolder = (FeedHolder) baseFeedHolder;
				// 此处的条件：
				if (feedHolder != null && feedHolder.getContentHolder().isPlayer()) {
					mVideoFeedHolder = (VideoFeedHolder) feedHolder.getContentHolder();
					VideoFeedHolder videoFeedHolder = (VideoFeedHolder) mVideoFeedHolder;
					videoFeedHolder.startMediaPlayer();
					break;
				}
			} else {
				continue;
			}
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		this.visibleItemCount = visibleItemCount;
		checkItemStopPlay();
		// 预加载
		if (firstVisibleItem + visibleItemCount + 3 == totalItemCount) {
			loadNextPage(false);
		}
		changeTitleBarAlpha();
	}

	private BaseFeedHolder mVideoFeedHolder;

	private void checkItemStopPlay() {
		if (mVideoFeedHolder != null && !mVideoFeedHolder.isPlayer()) {
			VideoFeedHolder videoFeedHolder = (VideoFeedHolder) mVideoFeedHolder;
			videoFeedHolder.stopPlayer();
			mVideoFeedHolder = null;
		}
	}

	/**
	 * @Title: listViewScrollTop
	 * @Description: 双击标题返回顶部并刷新
	 * @param 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public void listViewScrollTop() {
		if (mProList != null) {
			if (mProList.getLastVisiblePosition() == 1 && mProList.getChildCount() < 3) {
				// refreshLayout.setRefreshing(true);
				// onRefresh();
				// return;
				mProList.smoothScrollToPosition(0);
				mRefreshLayout.setRefreshing(true);
				onRefresh();
			} else if (mProList.getLastVisiblePosition() < 50) {
				mProList.smoothScrollToPosition(0);
				mRefreshLayout.postDelayed(new Runnable() {

					@Override
					public void run() {
						if (mRefreshLayout != null) {
							mRefreshLayout.setRefreshing(true);
							onRefresh();
						}
					}
				}, 1000);
			} else if (mProList.getLastVisiblePosition() > 49) {
				mProList.setSelection(0);
				mRefreshLayout.postDelayed(new Runnable() {

					@Override
					public void run() {
						if (mRefreshLayout != null) {
							mRefreshLayout.setRefreshing(true);
							onRefresh();
						}
					}
				}, 500);
			}
		}
	}

	@Override
	public void refreshUserData(User2 user2) {
		mUser2 = user2;
		initTitlebar();

		if (viewtype == INFO_PROFILE) {
			changeToInfo();
		}
	}

	@Override
	public int getProfileType() {
		return viewtype;
	}

}
