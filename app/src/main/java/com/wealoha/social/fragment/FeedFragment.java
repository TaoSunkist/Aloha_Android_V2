package com.wealoha.social.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.animation.LayoutTransition;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;

import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.wealoha.social.BaseFragAct;
import com.wealoha.social.R;
import com.wealoha.social.activity.FragmentWrapperActivity;
import com.wealoha.social.activity.MainAct;
import com.wealoha.social.activity.PicSendActivity;
import com.wealoha.social.activity.PicSendActivity.PicSendActivityBundleKey;
import com.wealoha.social.adapter.ProfileListAdapter;
import com.wealoha.social.adapter.ProfileListAdapter.ViewType;
import com.wealoha.social.api.notify2.service.Notify2Service;
import com.wealoha.social.beans.Feed;
import com.wealoha.social.beans.FeedResult;
import com.wealoha.social.beans.Result;
import com.wealoha.social.beans.ResultData;
import com.wealoha.social.beans.User;
import com.wealoha.social.api.FeedService;
import com.wealoha.social.beans.feed.UserTags;
import com.wealoha.social.commons.GlobalConstants;
import com.wealoha.social.commons.GlobalConstants.CacheKey;
import com.wealoha.social.commons.GlobalConstants.ImageSize;
import com.wealoha.social.event.ClearFeedNotifyEvent;
import com.wealoha.social.event.NotifyCountEvent;
import com.wealoha.social.event.RefreshFeedListEvent;
import com.wealoha.social.impl.Listeners;
import com.wealoha.social.push.notification.NotificationCount;
import com.wealoha.social.utils.ContextUtil;
import com.wealoha.social.utils.FontUtil;
import com.wealoha.social.utils.FontUtil.Font;
import com.wealoha.social.utils.ImageUtil;
import com.wealoha.social.utils.ImageUtil.CropMode;
import com.wealoha.social.utils.RemoteLogUtil;
import com.wealoha.social.utils.ToastUtil;
import com.wealoha.social.utils.UiUtils;
import com.wealoha.social.utils.XL;

/**
 * 用户收到的Feed
 * 
 * 
 * 
 * @author superman
 * @author Hongwei
 * @author sunkist
 * @author javamonk
 * @see
 * @since
 * @date 2014-10-24 18:38:38
 */
public class FeedFragment extends BaseFragment implements OnClickListener,
		SwipeRefreshLayout.OnRefreshListener, OnScrollListener {

	public static final String TAG = FeedFragment.class.getSimpleName();
	@Inject
	Context context;
	@Inject
	ContextUtil contextUtil;
	@Inject
	FeedService feedService;
	@Inject
	RemoteLogUtil logUtil;
	@Inject
	Picasso picasso;
	@Inject
	Notify2Service notify2Service;
	@Inject
	FontUtil fontUtil;
	TextView feed_sms_subscript;
	@InjectView(R.id.news_view_pager_listview)
	ListView mListView;
	@InjectView(R.id.frag_feed_msg_tv)
	RelativeLayout openSendSms;
	@InjectView(R.id.open_feed_upload_setup_tv)
	ImageView open_feed_upload_setup_tv;
	@InjectView(R.id.frag_feed_msg_img)
	ImageView mMsgImg;
	@InjectView(R.id.profile_list_refresh)
	SwipeRefreshLayout refreshLayout;
	@InjectView(R.id.feed_title)
	TextView mTitle;
	@InjectView(R.id.feed_title_container)
	RelativeLayout mTitleContainer;
	@InjectView(R.id.no_feed_cover)
	RelativeLayout mNoFeedCover;
	@InjectView(R.id.tags_method_container)
	LinearLayout mTagsMethodContainer;

	TextView mRemoveTagTv;
	TextView mTakeToMineTv;

	private boolean isRefresh;
	private View view;
	private ProfileListAdapter mProfileAdapter;
	private String mNextCursor = "notnull";
	private int mCount = 20;
	public User mCurrentUser;
	private String mPostId;
	// true = 單例
	private boolean singleFeedFlag = false;
	// 防止视图抖动加载数据
	private boolean syncLastPageBool = true;
	private FeedResult mResult;
	private List<Feed> mList; // 数据
	private Map<String, User> mUserMap;
	private Map<String, Integer> mCommentCountMap;
	private Map<String, Integer> mLikeCountMap;
	BaseFragAct mBaseFragAct;
	public FeedFragment mFeedFragment;
	private Dialog openCarmeraDialog;
	private View footerview;
	private ProgressBar footerProgress;
	private TextView footerImage;
	private Bundle mBundle;
	private int mImageWidth;
	private int mFeedItemType = 0;

	private View viewHeader;

	public static final String FEED_TYPE = "feedtype";
	public static final String SINGLE_FEED_POST_ID = "postid";
	public static final int FEED_TYPE_SINGLE = 1000;
	public static final int FEED_TYPE_TAGS = 1001;
	public static final int FEED_TYPE_DEFAULT = 0;
	public static final int OPEN_PIC_SEND_REQUESTCODE = 1002;
	public static final int OPEN_NOTIFY_REQUESTCODE = 1003;
	public static final String REFRESH_CODE_KEY = "isrefresh";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mFeedFragment = this;
		mImageWidth = Math.min(ImageSize.FEED_MAX, UiUtils.getScreenWidth(context));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.frag_feed, container, false);
		mBaseFragAct = (BaseFragAct) getActivity();
		if (mBaseFragAct instanceof MainAct) {
			mBaseFragAct = (MainAct) getActivity();
		} else if (mBaseFragAct instanceof FragmentWrapperActivity) {
			mBaseFragAct = (FragmentWrapperActivity) getActivity();
		}
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		findView();
		initData();
		initView();
	}

	protected void initTypeFace() {
		fontUtil.changeViewFont(mTitle, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
	}

	private void findView() {
		feed_sms_subscript = (TextView) view.findViewById(R.id.feed_sms_subscript);
		feed_sms_subscript.setTypeface(Typeface.DEFAULT);

		mTakeToMineTv = (TextView) view.findViewById(R.id.take_to_mine);
		mRemoveTagTv = (TextView) view.findViewById(R.id.remove_tag);
		mRemoveTagTv.setOnClickListener(this);
		mTakeToMineTv.setOnClickListener(this);
	}

	public void initData() {
		mBundle = getArguments();
		if (mBundle != null) {
			mCurrentUser = (User) mBundle.get(User.TAG);
			if (mBundle.getInt(FEED_TYPE, FEED_TYPE_DEFAULT) != FEED_TYPE_DEFAULT) {
				mPostId = mBundle.getString(SINGLE_FEED_POST_ID);
				// 开启单例模式
				changeView(mBundle.getInt(FEED_TYPE));
			}
			// 从通知栏启动时
			if ("auto_open_notify".equals(mBundle.getString("notificationbar"))) {
				openNotifyAct();
			}

		}
		if (mCurrentUser == null) {
			mCurrentUser = contextUtil.getCurrentUser();
		}

		openGuideDialog();

		if (mList == null) {
			mList = new ArrayList<Feed>();
		}
		if (mCommentCountMap == null) {
			mCommentCountMap = new HashMap<String, Integer>();
		}
		if (mLikeCountMap == null) {
			mLikeCountMap = new HashMap<String, Integer>();
		}

		if (mUserMap == null) {
			mUserMap = new HashMap<String, User>();
		}
	}

	/**
	 * @Title: changeView
	 * @Description: 改变视图
	 * @return void 返回类型
	 * @throws
	 */
	public void changeView(int feedtype) {

		mListView.setDividerHeight(0);
		mTitle.setText(R.string.photo);
		open_feed_upload_setup_tv.setVisibility(View.GONE);
		mMsgImg.setImageResource(R.drawable.nav_back);
		singleFeedFlag = true;
		if (feedtype == FEED_TYPE_SINGLE) {
		} else if (feedtype == FEED_TYPE_TAGS) {
			mTagsMethodContainer.setVisibility(View.VISIBLE);
			LayoutTransition transition = new LayoutTransition();
			transition.setDuration(200);
			transition.setAnimator(LayoutTransition.APPEARING, transition.getAnimator(LayoutTransition.APPEARING));
			transition.setAnimator(LayoutTransition.DISAPPEARING, transition.getAnimator(LayoutTransition.DISAPPEARING));
			mTagsMethodContainer.setLayoutTransition(transition);
		}
		mFeedItemType = feedtype;

	}

	/**
	 * @Title: openGuideDialog
	 * @Description: 第一次登陆出现的引导弹层
	 */
	public void openGuideDialog() {
		if (!singleFeedFlag && contextUtil.getCurrentUser() != null && !contextUtil.getCurrentUser().isShowFeedDialog()) {
			viewHeader = LayoutInflater.from(context).inflate(R.layout.item_feed_header, new ListView(context), false);
			TextView title = (TextView) viewHeader.findViewById(R.id.first_aloha_title);

			fontUtil.changeViewFont(title, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
			title.setText(getResources().getString(R.string.new_feature));
			TextView message = (TextView) viewHeader.findViewById(R.id.first_aloha_message);
			message.setText(getResources().getString(R.string.new_feature_content));
			TextView close = (TextView) viewHeader.findViewById(R.id.close_tv);
			close.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					removeHeader();
				}

			});
			mListView.addHeaderView(viewHeader);

		}
	}

	private void removeHeader() {
		if (mListView == null) {
			return;
		}
		mListView.removeHeaderView(viewHeader);
		User user = contextUtil.getCurrentUser();
		user.setShowFeedDialog(true);
		contextUtil.setCurrentUser(user);
		removeNofeedCover();
	}

	private void removeNofeedCover() {
		// XL.i("REMOVE_COVER", syncLastPageBool + "----MSG:" +
		// mResult.list.size());
		if (!singleFeedFlag && contextUtil.getCurrentUser() != null && !contextUtil.getCurrentUser().isShowFeedDialog()) {
			return;
		}
		if (mResult == null) {
			return;
		}

		int viewVisblity = 0;
		if (syncLastPageBool && mResult.list.size() == 0) {
			viewVisblity = View.VISIBLE;
		} else {
			viewVisblity = View.GONE;
		}

		if (mNoFeedCover != null) {
			mNoFeedCover.setVisibility(viewVisblity);
		}
	}

	/**
	 * @Title: initView
	 * @Description: 初始化控件
	 */
	public void initView() {
		footerview = LayoutInflater.from(context).inflate(R.layout.list_loader_footer, new ListView(context), false);
		footerProgress = (ProgressBar) footerview.findViewById(R.id.reload_progress);
		footerImage = (TextView) footerview.findViewById(R.id.reload_img);

		footerview.setOnClickListener(this);
		// 4.4以前 addFooter要在setAdapter前执行
		if (!singleFeedFlag) {
			mListView.addFooterView(footerview);
			addFooterView(false);
		}
		mProfileAdapter = new ProfileListAdapter(mListView, this, mCurrentUser, null, ViewType.Single, mFeedItemType);
		mListView.setAdapter(mProfileAdapter);

		// 下拉刷新监听
		refreshLayout.setOnRefreshListener(this);
		refreshLayout.setProgressViewOffset(false, -UiUtils.dip2px(context, 70), UiUtils.dip2px(context, 70));
		refreshLayout.setColorSchemeResources(R.color.red);
		mListView.setOnScrollListener(this);

		if (singleFeedFlag) {
			loadSingleFeed();// 单个feed
			feed_sms_subscript.setVisibility(View.GONE);
		} else {
			initFragmentdatas();// 一坨feed
		}
	}

	/**
	 * @Title: loadData
	 * @Description: 请求数据
	 * @param @param cursor
	 * @param @param count
	 * @param @param isRefresh 设定文件
	 */
	private void loadData(String cursor, int count, boolean isRefresh) {
		this.isRefresh = isRefresh;
		if (isRefresh) {
			cursor = null;
			mNextCursor = "notnull";
		}

		if (TextUtils.isEmpty(mNextCursor) || "null".equals(mNextCursor)) {
			removeFooterView();
			return;
		} else if (syncLastPageBool) {
			syncLastPageBool = false;
			feedService.feed(cursor, count, loadCallBack);
		} else if (!syncLastPageBool) {
			// refreshLayout.setRefreshing(false);
		}
		addFooterView(false);
	}

	private Callback<Result<FeedResult>> loadCallBack = new Callback<Result<FeedResult>>() {

		@Override
		public void failure(RetrofitError arg0) {
			closeRefresh();
			ToastUtil.longToast(context, R.string.network_error);
			addFooterView(true);
			syncLastPageBool = true;
		}

		@Override
		public void success(Result<FeedResult> result, Response arg1) {
			closeRefresh();
			if (result == null || !result.isOk()) {
				addFooterView(true);
				return;
			}
			// 为了渲染视图，重新组装usertags
			result.data.resetFeed(mCurrentUser);
			setNextCursor(result.data.nextCursorId);
			notifyAdapterDataChanged(isRefresh, result.data);
			syncLastPageBool = true;
			if (isAdded())
				removeNofeedCover();
		}
	};

	private boolean setNextCursor(String nextCursor) {
		mNextCursor = nextCursor;
		XL.i("NUL_NEWTCURSOR", "===" + nextCursor);
		if (TextUtils.isEmpty(nextCursor) || "null".equals(nextCursor)) {
			removeFooterView();
			XL.i("NUL_NEWTCURSOR", "---" + nextCursor);
			return false;
		} else {
			return true;
		}

	}

	/**
	 * @Title: notifyAdapterDataChanged
	 * @Description: listview 数据更新
	 * @param isRefresh
	 *            false:追加数据； true:将数据重置，重新开始添加数据
	 * @param result
	 *            设定文件
	 */
	private void notifyAdapterDataChanged(boolean isRefresh, FeedResult result) {
		if (result == null) {
			return;
		}
		if (isRefresh) {
			mProfileAdapter.clearData();
			mProfileAdapter.notifyTopDataSetChanged(result);
		} else {
			mProfileAdapter.notifyDataSetChanged(result);
		}
		saveFeed(result, this.isRefresh);
	}

	/**
	 * @Title: loadSingleFeed
	 * @Description: 加载单个feed
	 */
	private void loadSingleFeed() {
		feedService.feedDetail(mPostId, new Callback<Result<FeedResult>>() {

			@Override
			public void success(Result<FeedResult> result, Response arg1) {
				if (getActivity() != null && getActivity().isFinishing()) {
					return;
				}
				try {
					if (refreshLayout != null) {
						refreshLayout.setRefreshing(false);
					}
				} catch (Throwable e) {
				}

				if (result != null) {
					if (result.isOk()) {
						// cacheFlag = false;
						// syncLastPageBool = false;
						result.data.resetFeed(mCurrentUser);
						mResult = result.data;
						mProfileAdapter.clearData();
						mProfileAdapter.notifyTopDataSetChanged(mResult);

						if (mResult.list.get(0).tagMe != null && mResult.list.get(0).tagMe) {
							changeViewByTags(true);
						} else {
							changeViewByTags(false);
						}

					} else if (result.data.error == 404) {
						ToastUtil.longToast(mBaseFragAct, R.string.deleted_or_nohave_user_by_service);
					} else if (result.data.error == 451) {
						ToastUtil.longToast(mBaseFragAct, R.string.deleted_user_by_service);
					} else {
						ToastUtil.longToast(mBaseFragAct, R.string.Unkown_Error);
					}
				} else {
					ToastUtil.longToast(mBaseFragAct, R.string.Unkown_Error);
				}
			}

			@Override
			public void failure(RetrofitError e) {
				XL.d(TAG, "读取失败", e);
				if (refreshLayout != null) {
					refreshLayout.setRefreshing(false);
				}
			}
		});
	}

	/**
	 * @Title: initFragmentdatas
	 * @Description: 加载feed
	 */
	private void initFragmentdatas() {
		if (mResult != null) {
			XL.i("RESULT_FEED", "mresult:" + mResult.list.size());
			mProfileAdapter.clearData();
			mProfileAdapter.notifyDataSetChanged(mResult);
			if (!setNextCursor(mNextCursor)) {
				// removeFooterView();
				removeNofeedCover();
			}
		} else {
			restoreFirstPage();
			loadData(null, mCount, true);
		}
	}

	public void restoreFirstPage() {
		FeedResult firstPageFeed = restore(CacheKey.FirstPageFeed);
		if (firstPageFeed != null) {
			mProfileAdapter.clearData();
			mProfileAdapter.notifyDataSetChanged(firstPageFeed);
		}
	}

	private Runnable refreshRunnable = new Runnable() {

		@Override
		public void run() {
			if (refreshLayout != null) {
				refreshLayout.setRefreshing(false);
			}

		}
	};

	private void closeRefresh() {
		if (refreshLayout != null && refreshRunnable != null) {
			refreshLayout.postDelayed(refreshRunnable, 500);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (refreshLayout != null && refreshRunnable != null) {
			refreshLayout.removeCallbacks(refreshRunnable);
		}
	}

	/**
	 * @Title saveFeed
	 * @Description 保存当前数据，包括本地缓存和内存
	 * @param result
	 * @param init
	 *            false:追加数据； true:将数据重置，重新开始添加数据
	 */
	private void saveFeed(FeedResult result, boolean init) {
		if (mResult == null || init) {
			mResult = new FeedResult();
			mResult.list = new ArrayList<Feed>();
			mResult.userMap = new HashMap<String, User>();
			mResult.likeCountMap = new HashMap<String, Integer>();
			mResult.commentCountMap = new HashMap<String, Integer>();
			// clearAppend(CacheKey.AllPageFeed);
			this.isRefresh = false;
			save(CacheKey.FirstPageFeed, result);
		}
		mResult.list.addAll(result.list);
		mResult.userMap.putAll(result.userMap);
		mResult.likeCountMap.putAll(result.likeCountMap);
		mResult.commentCountMap.putAll(result.commentCountMap);
	}

	private int mFirstVisibleItem;
	private int mTotalItemCount;

	/**
	 * @Description: 监听滚动,完成预加载
	 * @see:
	 * @since:
	 * @description
	 * @author: sunkist
	 * @date:2014-11-1
	 */
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		mTotalItemCount = totalItemCount;
		mFirstVisibleItem = firstVisibleItem;
		if (mResult != null && mResult.list != null && mResult.list.size() < mCount) {
			removeFooterView();
		}
		if (firstVisibleItem + visibleItemCount + 12 == totalItemCount) {
			if (syncLastPageBool) {
				loadData(mNextCursor, mCount, false);
			}
		}

	}

	private int mTemtVisibleItem;

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
			Log.i("LOAD_MEMORY", "===" + ((mFirstVisibleItem - mTemtVisibleItem) > 0));
			boolean direction = (mFirstVisibleItem - mTemtVisibleItem) >= 0;
			fetchPhoto(mFirstVisibleItem, mTotalItemCount, direction);
			mTemtVisibleItem = mFirstVisibleItem;
		}
	}

	private void fetchPhoto(int firstVisibleItem, int totalItemCount, boolean direction) {
		if (mResult == null || mResult.list == null || mResult.list.size() == 0) {
			return;
		}
		int first = 0;
		int end = 0;
		// 正向
		if (direction) {
			first = firstVisibleItem;
			if ((firstVisibleItem + 10) > totalItemCount) {
				end = totalItemCount;
			} else {
				end = firstVisibleItem + 10;
			}
		} else {
			end = firstVisibleItem;
			if ((firstVisibleItem - 10) < 0) {
				first = 0;
			} else {
				first = firstVisibleItem - 10;
			}
		}
		Log.i("LOAD_MEMORY", first + "=====" + end + "=====" + firstVisibleItem);
		end = end > mResult.list.size() - 1 ? mResult.list.size() - 1 : end;
		for (int i = first; i < end - 2; i++) {
			Log.i("LOAD_MEMORY", "++++++" + i);
			Feed feed = mResult.list.get(i);
			picasso.load(ImageUtil.getImageUrl(feed.imageId, mImageWidth, CropMode.ScaleCenterCrop)).fetch();
			picasso.load(ImageUtil.getImageUrl(mResult.userMap.get(feed.userId).getAvatarImageId(), GlobalConstants.ImageSize.AVATAR_ROUND_SMALL, CropMode.ScaleCenterCrop)).fetch();
		}
	}

	private void addFooterView(boolean isreloadimg) {
		if (footerview == null) {
			return;
		}
		if (isreloadimg) {
			footerImage.setVisibility(View.VISIBLE);
			footerProgress.setVisibility(View.GONE);
		} else {
			footerImage.setVisibility(View.GONE);
			footerProgress.setVisibility(View.VISIBLE);
		}
		footerview.setVisibility(View.VISIBLE);
	}

	private void removeFooterView() {
		// XL.i("RESULT_FEED", "removeFooterView");
		XL.i("NUL_NEWTCURSOR", "+++++" + footerview);
		if (footerview != null) {
			footerview.setVisibility(View.GONE);
		}
	}

	// public void openLocalPhoto() {
	// Intent i = new Intent(Intent.ACTION_PICK,
	// android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
	// startActivityForResult(i, GlobalConstants.AppConstact.FLAG_CHOOSE_IMG);
	// }

	// public void openCamera() {
	// String status = Environment.getExternalStorageState();
	// if (status.equals(Environment.MEDIA_MOUNTED)) {
	// try {
	// File filePath = GlobalConstants.AppConstact.FILE_PIC_SCREENSHOT;
	// if (!filePath.exists()) {
	// filePath.mkdirs();
	// }
	// Intent intent = new
	// Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
	// File f = new File(filePath,
	// GlobalConstants.AppConstact.localTempImageFileName);
	// // f.getAbsolutePath()).show();
	// // localTempImgDir和localTempImageFileName是自己定义的名字
	// Log.i("CAMRA_LOG", "--------");
	// Uri u = Uri.fromFile(f);
	// intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
	// intent.putExtra(MediaStore.EXTRA_OUTPUT, u);
	// startActivityForResult(intent, FLAG_CHOOSE_PHONE);
	// } catch (ActivityNotFoundException e) {
	// }
	// } else {
	// // ToastUtil.shortToastCenter(this, "未检测到SD卡");
	// }
	// }

	@Override
	public void onRefresh() {
		if (singleFeedFlag) {
			loadSingleFeed();
			return;
		}
		loadData(null, mCount, true);
		// 清红点
		if (contextUtil.getMainAct() != null) {
			contextUtil.getMainAct().newFeedTagDotCleared();
		}
		bus.post(new ClearFeedNotifyEvent(true));
		addFooterView(false);
	}

	@Override
	public void onResume() {
		try {
			bus.post(new Listeners.MonitorMainUiBottomTagPostion(2));
		} catch (Throwable e) {
		}
		if (mBaseFragAct != null && mBaseFragAct instanceof MainAct) {
			MainAct baseFragAct = (MainAct) mBaseFragAct;
			baseFragAct.newFeedTagDotCleared();
			judgeNotice(baseFragAct);
			bus.register(notifyCountChangeListener);
			bus.register(refreshFeedListEvent);
			bus.post(new ClearFeedNotifyEvent(true));
		}

		if (mBundle != null && mBundle.getBoolean(REFRESH_CODE_KEY, false)) {
			XL.i("FEED_FRAG_RESULT", "refresh:" + mBundle.getBoolean(REFRESH_CODE_KEY, false));
			mBundle.putBoolean(REFRESH_CODE_KEY, false);
			listViewScrollTop();
		}
		super.onResume();
	}

	@Override
	public void onStop() {
		super.onStop();
		// 不再监听变更
		if (mBaseFragAct != null && mBaseFragAct instanceof MainAct) {
			try {
				bus.unregister(notifyCountChangeListener);
			} catch (Throwable e) {
				XL.w(TAG, "取消注册失败", e);
			}
		}
	}

	/**
	 * @Description: 判断Notice的小圆球是否显示
	 * @see:
	 * @since:
	 * @description
	 * @author: sunkist
	 * @date:2014-11-25
	 */
	private void judgeNotice(MainAct baseFragAct) {
		int newNoticeCount = NotificationCount.getCommentCount();
		setNewNoticeSub(newNoticeCount, "judgeNotice");
	}

	public void setNewNoticeSub(int newNoticeCount, String form) {
		XL.i("XM_PUSH_BROADCAST_TEST", "setNewNoticeSub:" + newNoticeCount);
		XL.i("XM_PUSH_BROADCAST_TEST", "from:" + form);
		if (singleFeedFlag) {
			XL.d(TAG, "不显示提醒数");
			feed_sms_subscript.setVisibility(View.GONE);
			return;
		}

		if (newNoticeCount > 0) {
			feed_sms_subscript.setVisibility(View.VISIBLE);
			feed_sms_subscript.setText(newNoticeCount > 99 ? "···" : newNoticeCount + "");
		} else {
			feed_sms_subscript.setVisibility(View.GONE);
		}
	}

	@OnClick({ R.id.remove_tag, R.id.take_to_mine, R.id.frag_feed_msg_tv, R.id.open_feed_upload_setup_tv, R.id.feed_title })
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.frag_feed_msg_tv:
			if (singleFeedFlag) {
				getActivity().finish();
			} else {
				// 清理Notify未读数
				// 无论是否为0，都清空，避免新的通知被定时刷到还显示数字
				openNotifyAct();
			}

			break;
		case R.id.open_feed_upload_setup_tv:
			removeHeader();
			openHeadSelect();
			break;
		case R.id.feed_title:
			if (isDoubleClick()) {
				listViewScrollTop();
			}
			break;
		case R.id.listview_footer_container:
			loadData(mNextCursor, mCount, false);
			break;
		case R.id.remove_tag:
			openAlertDialog((BaseFragAct) getActivity());

			break;
		case R.id.take_to_mine:
			shareFeed();
			break;
		}
	}

	/**
	 * @Title: removeTagInSingleFeedType
	 * @Description: 移除tag
	 */
	private void removeTagInSingleFeedType() {
		Feed tagFeed = null;
		// String tagId = null;
		if (mResult != null) {
			// 只有一个feed
			tagFeed = mResult.list.get(0);
		}
		if (tagFeed != null) {
			if (tagFeed.tagMe != null && tagFeed.tagMe) {
				feedService.removeTag(tagFeed.postId, mCurrentUser.getId(), removeTagCallback);
			}
		}
	}

	private Dialog alertDialog;

	/**
	 * @Title: openGuideDialog
	 * @Description: 移除你的標籤
	 */
	public void openAlertDialog(BaseFragAct baseAct) {
		View view = LayoutInflater.from(baseAct).inflate(R.layout.dialog_first_aloha_time, new LinearLayout(baseAct), false);
		TextView title = (TextView) view.findViewById(R.id.first_aloha_title);
		title.setText(R.string.remove_tag_dialog);
		TextView message = (TextView) view.findViewById(R.id.first_aloha_message);
		message.setGravity(Gravity.CENTER);
		message.setText(R.string.remove_tag_dialog_message);
		TextView close = (TextView) view.findViewById(R.id.close_tv);
		close.setText(R.string.cancel);
		close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (alertDialog != null && alertDialog.isShowing()) {
					alertDialog.dismiss();
				}
			}
		});
		TextView close02 = (TextView) view.findViewById(R.id.close_tv_02);
		close02.setVisibility(View.VISIBLE);
		close02.setText(R.string.delete);
		close02.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (alertDialog != null && alertDialog.isShowing()) {
					alertDialog.dismiss();
				}
				removeTagInSingleFeedType();
			}
		});

		alertDialog = new AlertDialog.Builder(baseAct)//
		.setView(view)//
		.setCancelable(false) //
		.create();
		alertDialog.show();

	}

	/**
	 * @Title: copyTheFeedToMe
	 * @Description: 转发
	 */
	private void shareFeed() {

		if (mResult == null) {
			return;
		}
		Intent intent = new Intent(getActivity(), PicSendActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString(PicSendActivityBundleKey.PIC_SEND_TYPE, PicSendActivity.SHARE_FEED);
		bundle.putParcelable(Feed.TAG, mResult.list.get(0));
		// bundle.putString(PicSendActivityBundleKey.PHOTO_ID,
		// mResult.list.get(0).imageId);
		// ArrayList<UserTags> userTags = (ArrayList<UserTags>)
		// mResult.list.get(0).userTags;
		// bundle.putParcelableArrayList(UserTags.TAG, userTags);
		intent.putExtras(bundle);
		getActivity().startActivityForResult(intent, OPEN_PIC_SEND_REQUESTCODE);
	}

	private Callback<ResultData> removeTagCallback = new Callback<ResultData>() {

		@Override
		public void success(ResultData result, Response arg1) {
			if (mProfileAdapter != null) {
				ArrayList<UserTags> usertags = (ArrayList<UserTags>) mResult.list.get(0).userTags;
				for (int i = 0; i < usertags.size(); i++) {
					UserTags tag = usertags.get(i);
					if (tag.tagUserId.equals(mCurrentUser.getId())) {
						usertags.remove(i);
						i--;
					}
				}

				mProfileAdapter.clearData();
				mProfileAdapter.notifyDataSetChanged(mResult);
				mListView.setAdapter(mProfileAdapter);
			}

			changeViewByTags(false);
		}

		@Override
		public void failure(RetrofitError error) {
			XL.i("REMOVE_TAG", "FAILURE:" + error.getMessage());
		}
	};

	/**
	 * @Title: changeViewByTags
	 * @Description: 根据当前feed有没有自己的标签来渲染不同的视图
	 */
	private void changeViewByTags(boolean isDoubleView) {
		if (isDoubleView) {
			mTakeToMineTv.setVisibility(View.VISIBLE);
			mRemoveTagTv.setVisibility(View.VISIBLE);
		} else {
			// 改变视图，转发和移除tag的两个textview
			mTakeToMineTv.setVisibility(View.GONE);
			mRemoveTagTv.setVisibility(View.VISIBLE);
			mRemoveTagTv.setText(R.string.tag_removed);
			mRemoveTagTv.setBackgroundColor(0xcdcdcd);// 取資源時崩潰，改造後的feed不會有這個問題
			mRemoveTagTv.setClickable(false);
		}
	}

	private void openNotifyAct() {
		// 清空tab的数字显示, TODO 改成事件通知
		MainAct mact = (MainAct) getActivity();

		if (mact != null) {
			mact.newNotifyCountCleared();
		}
		if (isAdded())
			setNewNoticeSub(0, "openNotifyAct");

		// 打开通知界面
		((BaseFragAct) getActivity()).startActivityForResult(GlobalConstants.IntentAction.INTENT_URI_FEED_NOTICE,//
																null, 0, 0, OPEN_NOTIFY_REQUESTCODE);
		// startActivity(GlobalConstants.IntentAction.INTENT_URI_FEED_NOTICE);
		if (mBaseFragAct != null && mBaseFragAct instanceof MainAct) {
			mBaseFragAct.overridePendingTransition(R.anim.left_in, R.anim.stop);
		}
	}

	private void openHeadSelect() {
		// 用dialog 不用popupwindow了 方法原樣複製過來的，沒有改動
		View view = getActivity().getLayoutInflater().inflate(R.layout.open_carmera_dialog, new ListView(context), false);
		TextView openCarmera = (TextView) view.findViewById(R.id.open_carmera);
		TextView openLocaPics = (TextView) view.findViewById(R.id.open_location_photo);
		openCarmera.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (/* mBaseFragAct != null && */mBaseFragAct instanceof MainAct) {
					MainAct baseFragAct = (MainAct) mBaseFragAct;
					baseFragAct.openCamera(baseFragAct);
					if (openCarmeraDialog != null && openCarmeraDialog.isShowing()) {
						openCarmeraDialog.dismiss();
					}
				}
			}

		});
		openLocaPics.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (/* mBaseFragAct != null && */mBaseFragAct instanceof MainAct) {
					MainAct baseFragAct = (MainAct) mBaseFragAct;
					baseFragAct.openImgPick(baseFragAct);
					if (openCarmeraDialog != null && openCarmeraDialog.isShowing()) {
						openCarmeraDialog.dismiss();
					}
				}
			}
		});
		openCarmeraDialog = new AlertDialog.Builder(getActivity()).setView(view).show();
	}

	public void refreshFeedList() {
		XL.i("FEED_REFRESH_BUS", "refreshFeedList");
		if (isAdded())
			loadData(null, mCount, true);
		try {
			bus.unregister(refreshFeedListEvent);
		} catch (Throwable e) {
			XL.w(TAG, "取消注册失败", e);
		}
	}

	/**
	 * @Title: listViewScrollTop
	 * @Description: 双击标题返回顶部并刷新
	 * @return void 返回类型
	 * @throws
	 */
	public void listViewScrollTop() {
		if (mListView != null) {
			if (mListView.getLastVisiblePosition() == 1 && mListView.getChildCount() < 3) {
				// refreshLayout.setRefreshing(true);
				// onRefresh();
				// return;
				mListView.smoothScrollToPosition(0);
				refreshLayout.setRefreshing(true);
				onRefresh();
			} else if (mListView.getLastVisiblePosition() < 50) {
				mListView.smoothScrollToPosition(0);
				refreshLayout.postDelayed(new Runnable() {

					@Override
					public void run() {
						if (refreshLayout != null) {
							refreshLayout.setRefreshing(true);
							onRefresh();
						}
					}
				}, 1000);
			} else if (mListView.getLastVisiblePosition() > 49) {
				mListView.setSelection(0);
				refreshLayout.postDelayed(new Runnable() {

					@Override
					public void run() {
						if (refreshLayout != null) {
							refreshLayout.setRefreshing(true);
							onRefresh();
						}
					}
				}, 500);
			}
		}
	}

	private long lastTapTimeMillis;
	private static final long DOUBLE_TIME = 500;

	private boolean isDoubleClick() {
		boolean isDoubleClick = false;
		if (System.currentTimeMillis() - lastTapTimeMillis < DOUBLE_TIME) {
			isDoubleClick = true;
		}
		lastTapTimeMillis = System.currentTimeMillis();
		return isDoubleClick;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	public void deleteCallback(List<Feed> feeds) {
		XL.i("FEED_FRAMGNET_LIFE", "deleteCallback");
		if (singleFeedFlag) {
			getActivity().setResult(Activity.RESULT_OK);
			getActivity().finish();
			return;
		} else if (mResult != null && feeds != null) {
			XL.i("FEED_FRAMGNET_LIFE", "mResult:" + mResult);
			XL.i("FEED_FRAMGNET_LIFE", "feeds:" + feeds);
			mResult.list = feeds;
		}
	}

	private Object notifyCountChangeListener = new Object() {

		@Subscribe
		public void onEvent(NotifyCountEvent event) {
			// XL.d(TAG, "通知数变更，更新界面");
			setNewNoticeSub(event.getNewNotifyCount(), "onEvent");
		}
	};
	private Object refreshFeedListEvent = new Object() {

		@Subscribe
		public void refreshFeed(RefreshFeedListEvent event) {
			XL.i("FEED_REFRESH_BUS", "EVENT");
			refreshFeedList();
		}
	};

	@Override
	public void onActivityResultCallback(int requestcode, int resultcode, Intent result) {
		if (resultcode != Activity.RESULT_OK) {
			return;
		}
		switch (requestcode) {
		case OPEN_PIC_SEND_REQUESTCODE:
			getActivity().setResult(Activity.RESULT_OK);
			getActivity().finish();
			break;
		}
	}
}
