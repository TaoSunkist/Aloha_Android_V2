package com.wealoha.social.activity;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.AlertDialog;
import android.app.Dialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;

import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Picasso.LoadedFrom;
import com.squareup.picasso.Target;
import com.wealoha.social.ActivityManager;
import com.wealoha.social.AppApplication;
import com.wealoha.social.BaseFragAct;
import com.wealoha.social.ContextConfig;
import com.wealoha.social.R;
import com.wealoha.social.api.ServerApi;
import com.wealoha.social.beans.MatchSettingData;
import com.wealoha.social.beans.ApiResponse;
import com.wealoha.social.beans.User;
import com.wealoha.social.beans.common.ConstantsData;
import com.wealoha.social.api.ConstantsService;
import com.wealoha.social.beans.common.CountData;
import com.wealoha.social.api.CountService;
import com.wealoha.social.beans.message.UnreadData;
import com.wealoha.social.beans.PushSettingResult;
import com.wealoha.social.commons.GlobalConstants;
import com.wealoha.social.event.ClearFeedNotifyEvent;
import com.wealoha.social.event.NewCommentPushEvent;
import com.wealoha.social.event.RefreshFeedListEvent;
import com.wealoha.social.event.RefreshFilterBtnEvent;
import com.wealoha.social.event.TicketInvalidEvent;
import com.wealoha.social.fragment.AlohaFragment;
import com.wealoha.social.fragment.BaseFragment;
import com.wealoha.social.fragment.ChatFragment;
import com.wealoha.social.fragment.FeedFragment;
import com.wealoha.social.fragment.LoadingFragment;
import com.wealoha.social.fragment.Profile2Fragment;
import com.wealoha.social.fragment.SwipeMenuListFragment;
import com.wealoha.social.impl.Listeners;
import com.wealoha.social.push.Push2Type;
import com.wealoha.social.push.notification.NotificationCount;
import com.wealoha.social.services.DownloadService;
import com.wealoha.social.store.PopupStore;
import com.wealoha.social.ui.feeds.Feed2Fragment;
import com.wealoha.social.utils.ContextUtil;
import com.wealoha.social.utils.FileTools;
import com.wealoha.social.utils.FontUtil;
import com.wealoha.social.utils.ImageUtil;
import com.wealoha.social.utils.PushUtil;
import com.wealoha.social.utils.ToastUtil;
import com.wealoha.social.utils.UiUtils;
import com.wealoha.social.utils.XL;
import com.wealoha.social.view.custom.SlideView.OnSlideListener;
import com.xiaomi.mipush.sdk.MiPushClient;

/**
 * 
 * @author:sunkist
 * @see:
 * @since:
 * @description
 * @copyright wealoha.com
 * @Date:2014-10-23
 */
public class MainAct extends BaseFragAct implements OnClickListener, OnSlideListener, OnTouchListener {

	/** 是否允许Fragment的后退，当前Android版本有bug，不能用。。 */
	private static final boolean FRAGMENT_BACKSTACK_ENABLED = false;
	public static final String TAG = MainAct.class.getSimpleName();
	public static final File FILE_SDCARD = Environment.getExternalStorageDirectory();
	public FragmentManager mFm;
	public FragmentTransaction transaction;
	public List<BaseFragment> mFragments = new ArrayList<BaseFragment>();
	@InjectView(R.id.tab)
	LinearLayout radioGroup;
	@InjectView(R.id.aloha_frag_rb)
	CheckBox mAlohaFragRb;
	@InjectView(R.id.chat_frag_rb)
	CheckBox mChatFragRb;
	@InjectView(R.id.feed_frag_rb)
	CheckBox mFeedFragRb;
	@InjectView(R.id.profile_frag_rb)
	CheckBox mProfileFragRb;
	@Inject
	ServerApi settingService;
	/** 角标 */
	@InjectView(R.id.profile_frag_rb_subscript)
	TextView profileSub;
	@InjectView(R.id.chat_frag_rb_subscript)
	TextView chatSub;
	@InjectView(R.id.feed_frag_rb_subscript)
	TextView feedSub;
	@InjectView(R.id.feed_frag_rb_subscript_secound)
	TextView feedSubSecound;
	@Inject
	ServerApi mMessageService;
	@InjectView(R.id.aloha_frag_rb_subscript)
	TextView alohaSub;

	@InjectView(R.id.main_menu_bar)
	RelativeLayout mMenuBar;
	@InjectView(R.id.aloha_add)
	ImageView mAdd;
	@InjectView(R.id.aloha_filtrate)
	TextView mOpenFilter;

	@Inject
	PushUtil pushUtil;
	@Inject
	ContextUtil contextUtil;
	public String userid;
	// 默认打开哪个tab
	private String openTab;
	public static final int HANDLER_LOAD_MORE = 9999;
	public static final int HANDLER_RELOAD = 10002;
	public static final int HANDLER_LOAD_MORE_ALOHA = 10000;
	public static final int HANDLER_PUSH = 10001;
	public static final String CLEAR_ACTION = "CLEAR_ACTION";
	private LoadingFragment mLoadingFragment;
	private AlohaFragment mAlohaFragment;
	private SparseArray<Fragment> mTabsFirstFragments = new SparseArray<Fragment>();
	private Map<String, String> mRecommendSourceMap;
	private long lastBackPressedMillis;
	private Dialog updateAlert;
	/**
	 * 取Feed计数的
	 */
	private Timer counterRetrieveTask;

	@Inject
	CountService countService;
	@Inject
	ConstantsService constantsService;

	@Inject
	ServerApi mUserAPI;
	private Context mContext;
	private Handler mHandler;
	private Dialog mIsFirstEnterFeedsDialog;
	private View popView;

	public static final String OPEN_GESTURE_FROM_PUSH_KEY = "OPEN_GESTURE_FROM_PUSH_KEY";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_main);

		// 如果是1，那么不开启MainAct，开启MainAct只为清空Act栈
		if (getIntent().getIntExtra(CLEAR_ACTION, 0) == 1) {
			finish();
		}

		ActivityManager.popAll();
		ActivityManager.push(this);
		initXiaomiPushConfig();
		mContext = this;
		mFm = getSupportFragmentManager();
		Intent intent = getIntent();
		if (intent != null) {
			openTab = intent.getStringExtra("openTab");
			intent.putExtra("openTab", "");
		}
		// 角标数字字体
		chatSub.setTypeface(Typeface.DEFAULT);
		feedSub.setTypeface(Typeface.DEFAULT);
		initHandler();
		initData();
		// initPopupView();

		registerReceiver(mHomeKeyEventReceiver, new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));

	}

	private void initPopupView() {
		View view = getLayoutInflater().inflate(R.layout.main_join_ewm_popup, new LinearLayout(mContext), false);
		popupJoinEwm = new PopupWindow(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		popupJoinEwm.setOutsideTouchable(true);
		popupJoinEwm.setBackgroundDrawable(new ColorDrawable());
		popupJoinEwm.setContentView(view);
		popupJoinEwm.showAtLocation(view, Gravity.CENTER, 0, 0);
		popupJoinEwm.update();
	}

	public static User mLastUser;
	public User mToUser;

	private static class MainHandler extends Handler {

		public WeakReference<MainAct> act;

		public MainHandler(MainAct mainact) {
			act = new WeakReference<MainAct>(mainact);
		}

		@Override
		public void handleMessage(Message msg) {
			MainAct mainact = act.get();
			if (mainact != null) {
				mainact.handlerService(msg);
			}
		}
	}

	public void handlerService(Message msg) {
		switch (msg.what) {
		case GlobalConstants.PromptInfo.NETWORK_LINK_FAILURE:
			ToastUtil.longToast(mContext, R.string.network_error);
			break;
		case EXIT_BACK_NOTICE:
			break;
		case GlobalConstants.AppConstact.LOADING_MATCH_DATA_SUCCESS:
			if (mLoadingFragment.isResumed()) {
				if (AppApplication.mUserList != null && AppApplication.mUserList.size() > 0) {
					if (mAlohaFragment == null) {
						mAlohaFragment = new AlohaFragment();
					}
					transaction = mFm.beginTransaction();
					transaction.setCustomAnimations(R.animator.aloha_in, R.animator.frag_left_null);
					transaction.replace(R.id.content, mAlohaFragment, AlohaFragment.TAG);
					transaction.commit();
					break;
				}
			}
			break;
		case HANDLER_LOAD_MORE:
			doAlohaLoading(HANDLER_LOAD_MORE);
			break;
		case HANDLER_LOAD_MORE_ALOHA:
			doAlohaLoading(HANDLER_LOAD_MORE_ALOHA);
			break;
		case HANDLER_RELOAD:
			doAlohaLoading(HANDLER_RELOAD);
			break;
		case GlobalConstants.AppConstact.RETURN_NEXT_USER_ALOHA:
			returnNextFrag(msg);
			break;
		default:
			break;
		}
	}

	private void initHandler() {
		mHandler = new MainHandler(this);
	}

	public boolean isDestory = true;

	private void doAlohaLoading(int flag) {
		// flag: true nope动画；false aloha动画
		boolean f = false;
		if (flag == HANDLER_LOAD_MORE) {
			f = true;
		} else {
			f = false;
		}
		mLoadingFragment = new LoadingFragment();
		mFm = getSupportFragmentManager();
		// }
		GlobalConstants.AppConstact.IS_LAST_USER = true;
		Bundle bundle = new Bundle();
		bundle.putBoolean("bool", f);
		mLoadingFragment.setArguments(bundle);
		if (flag == HANDLER_LOAD_MORE) {
			mFm.beginTransaction().setCustomAnimations(R.animator.frag_left_null, R.animator.frag_left_out).replace(R.id.content, mLoadingFragment).commitAllowingStateLoss();
		} else if (flag == HANDLER_LOAD_MORE_ALOHA) {
			mFm.beginTransaction().setCustomAnimations(R.animator.frag_left_null, R.animator.frag_right_out).replace(R.id.content, mLoadingFragment).commitAllowingStateLoss();
		} else {
			mFm.beginTransaction().setCustomAnimations(R.animator.frag_left_null, R.animator.frag_out).replace(R.id.content, mLoadingFragment).commitAllowingStateLoss();
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		closeAddPop();
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			XL.i("TOUCH_TEST", "EVENT:" + event.getAction());
			transaction = mFm.beginTransaction();
			CheckBox c = (CheckBox) v;
			if (c.isChecked() && c.getId() != R.id.feed_frag_rb) {
				XL.i("TOUCH_TEST", "EVENT true:" + event.getAction());
				return true;
			}
			c.setChecked(true);
			// 如果通知未读，那么切换tab的时候角标要继续显示
			int newNotifyCount = NotificationCount.getCommentCount();
			if (newNotifyCount > 0) {
				feedSub.setVisibility(View.VISIBLE);
				feedSub.setText(newNotifyCount > 99 ? "···" : newNotifyCount + "");
			}
			Bundle bundle = new Bundle();

			switch (c.getId()) {
			case R.id.aloha_frag_rb:
				mFeedFragRb.setChecked(false);
				mProfileFragRb.setChecked(false);
				mChatFragRb.setChecked(false);
				if (AppApplication.mUserList != null && AppApplication.mUserList.size() > 0) {
					if (mAlohaFragment == null) {
						mAlohaFragment = new AlohaFragment();
					}
					if (!mAlohaFragment.isAdded()) {
						transaction.replace(R.id.content, mAlohaFragment).commit();
					}
					break;
				} else if (mLoadingFragment == null) {
					mLoadingFragment = new LoadingFragment();
				}
				if (!mLoadingFragment.isAdded()) {
					transaction.replace(R.id.content, mLoadingFragment).commit();
				}
				break;
			case R.id.chat_frag_rb:
				mAlohaFragRb.setChecked(false);
				mFeedFragRb.setChecked(false);
				mProfileFragRb.setChecked(false);
				ChatFragment chatFrag = (ChatFragment) mTabsFirstFragments.get(R.id.chat_frag_rb);
				if (chatFrag == null) {
					chatFrag = new ChatFragment();
					contextUtil.setChatFragment(chatFrag);
					mTabsFirstFragments.put(R.id.chat_frag_rb, chatFrag);
				}
				if (!chatFrag.isAdded()) {
					transaction.replace(R.id.content, chatFrag).commit();
				}
				break;
			case R.id.feed_frag_rb:
				mAlohaFragRb.setChecked(false);
				mProfileFragRb.setChecked(false);
				mChatFragRb.setChecked(false);
				if (contextUtil.getForegroundFrag() != null && contextUtil.getForegroundFrag() instanceof Feed2Fragment) {
					((Feed2Fragment) contextUtil.getForegroundFrag()).listViewScrollTop();
					break;
				}
				Fragment feedFrag = mTabsFirstFragments.get(R.id.feed_frag_rb);
				// 防止头像数据
				if (feedFrag == null) {
					feedFrag = new Feed2Fragment();
					bundle.putParcelable(User.TAG, contextUtil.getCurrentUser());
					mTabsFirstFragments.put(R.id.feed_frag_rb, feedFrag);
					XL.i("FEED_REFRESH_BUS", "feedFrag=null");
				}
				if (feedSubSecound != null && feedSubSecound.getVisibility() == View.VISIBLE) {
					bundle.putBoolean(Feed2Fragment.IS_REFRESH_KEY, true);
				} else {
					bundle.putBoolean(Feed2Fragment.IS_REFRESH_KEY, false);
				}
				if (!feedFrag.isAdded()) {
					feedFrag.setArguments(bundle);
					transaction.replace(R.id.content, feedFrag).commit();
				}
				break;
			case R.id.profile_frag_rb:
				mAlohaFragRb.setChecked(false);
				mFeedFragRb.setChecked(false);
				mChatFragRb.setChecked(false);

				Fragment fragProf = mTabsFirstFragments.get(R.id.profile_frag_rb);
				if (fragProf == null) {
					fragProf = new Profile2Fragment();
					bundle = new Bundle();
					bundle.putParcelable(User.TAG, contextUtil.getCurrentUser());
					fragProf.setArguments(bundle);
					mTabsFirstFragments.put(R.id.profile_frag_rb, fragProf);
				}
				if (!fragProf.isAdded()) {
					transaction.replace(R.id.content, fragProf).commit();
				}
			}
		}
		// v.performClick();
		return true;
	}

	public void initData() {
		mAlohaFragRb.setOnTouchListener(this);
		mChatFragRb.setOnTouchListener(this);
		mFeedFragRb.setOnTouchListener(this);
		mProfileFragRb.setOnTouchListener(this);

		// if(!TextUtils.isEmpty(openTab)){
		// ContextConfig.getInstance().putBooleanWithFilename(OPEN_GESTURE_FROM_PUSH_KEY, true);
		// }else {
		// ContextConfig.getInstance().putBooleanWithFilename(OPEN_GESTURE_FROM_PUSH_KEY, false);
		// }
		XL.i("IS_ACTOPEN_BY_PUSH", "open tab:" + openTab);

		if ("chat".equals(openTab)) {
			ChatFragment mChatFrag = new ChatFragment();
			contextUtil.setChatFragment(mChatFrag);
			mTabsFirstFragments.put(R.id.chat_frag_rb, mChatFrag);
			mFm.beginTransaction().replace(R.id.content, mChatFrag).commit();
			mChatFragRb.setChecked(true);
		} else if ("profile".equals(openTab)) {
			Profile2Fragment mProfileFrag = new Profile2Fragment();
			Bundle bundle = new Bundle();
			bundle.putParcelable(User.TAG, contextUtil.getCurrentUser());
			mProfileFrag.setArguments(bundle);
			mTabsFirstFragments.put(R.id.profile_frag_rb, mProfileFrag);
			mFm.beginTransaction().replace(R.id.content, mProfileFrag).commit();
			mProfileFragRb.setChecked(true);
			// push 进入要开启手势锁
			openLock = true;
			openLaunchImg = true;
		} else if ("feednotify".equals(openTab)) {
			// push 进入要开启手势锁
			ContextConfig.getInstance().putBooleanWithFilename(OPEN_GESTURE_FROM_PUSH_KEY, true);
			// 从通知栏开启notify：1.打开mainact 2.切换到feed 3.开启notify
			Bundle bundle = new Bundle();
			bundle.putString("notificationbar", "auto_open_notify");

			Feed2Fragment mFeedFrag = new Feed2Fragment();
			mFeedFrag.setArguments(bundle);
			// mTabsFirstFragments.put(R.id.feed_frag_rb, mFeedFrag);
			mFm.beginTransaction().replace(R.id.content, mFeedFrag).commit();
			mFeedFragRb.setChecked(true);
		} else if ("popularity".equals(openTab)) {
			// push 进入要开启手势锁
			ContextConfig.getInstance().putBooleanWithFilename(OPEN_GESTURE_FROM_PUSH_KEY, true);
			Bundle bundle = new Bundle();
			bundle.putInt("listtype", SwipeMenuListFragment.LISTTYPE_POPULARITY);
			bundle.putParcelable(User.TAG, contextUtil.getCurrentUser());
			Profile2Fragment mProfileTestFrag = new Profile2Fragment();
			mProfileTestFrag.setArguments(bundle);
			mFm.beginTransaction().replace(R.id.content, mProfileTestFrag).commit();
			mFeedFragRb.setChecked(true);
			startFragment(SwipeMenuListFragment.class, bundle, true);
		} else if ("topic".equals(openTab)) {
			Bundle bundle = new Bundle();
			bundle.putBoolean(Feed2Fragment.IS_REFRESH_KEY, true);
			Feed2Fragment mFeedFrag = new Feed2Fragment();
			mFeedFrag.setArguments(bundle);
			mFm.beginTransaction().replace(R.id.content, mFeedFrag).commit();
			mFeedFragRb.setChecked(true);
		} else {
			mLoadingFragment = new LoadingFragment();
			mFm.beginTransaction().replace(R.id.content, mLoadingFragment).commit();
			mAlohaFragRb.setChecked(true);
		}
		// if(!TextUtils.isEmpty(openTab)){
		// openLock = true;
		// openLaunchImg = true;
		// openGestureLock();
		//
		// }else {
		// mLoadingFragment = new LoadingFragment();
		// mFm.beginTransaction().replace(R.id.content, mLoadingFragment).commit();
		// mAlohaFragRb.setChecked(true);
		// }
		pushUtil.tryXmGetuiPushToken();
		getNoticeSetting();

	}

	private void getNoticeSetting() {
		// XL.d(TAG, "取Push设置");
		settingService.getPushSetting(new Callback<ApiResponse<PushSettingResult>>() {

			@Override
			public void success(ApiResponse<PushSettingResult> arg0, Response arg1) {
				if (arg0 != null && arg0.isOk()) {
					ContextConfig.getInstance().putPushSetConfig(arg0.getData());
				} else {
				}
			}

			@Override
			public void failure(RetrofitError arg0) {

			}
		});
	}

	/**
	 * @Description:进入计时界面
	 * @param view
	 * @see:
	 * @since:
	 * @description
	 * @author: sunkist
	 * @date:2014-10-31
	 */
	public void Accelerate(View view, Bundle bundle) {
		startActivity(GlobalConstants.IntentAction.INTENT_URI_ADVANCEDFEATURED);
		overridePendingTransition(R.anim.left_in, R.anim.act_out);
	}

	public Object clearNotify = new Object() {

		@Subscribe
		public void clearFeedNotify(ClearFeedNotifyEvent event) {
			if (event.isClear()) {
				feedSub.setVisibility(View.GONE);
				feedSubSecound.setVisibility(View.GONE);
			}
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		popUpdateDescription();
		refreshFilterBtnText();
		try {
			registerReceiver(mBroadcastReceiver, new IntentFilter(Push2Type.InboxMessageNew.getType()));
		} catch (Exception e) {
		}
		if (contextUtil.getMainAct() == null) {
			contextUtil.setMainAct(this);
		}
		isDestory = false;
		ActivityManager.retain(MainAct.class);
		// Feed和Notify计数
		initFeedAndNotifyCounterTimer();
		// 读取server返回的更新，开机画面等信息，下次用
		fetchUpdateAndStartup();
		// TODO 此处存在纰漏，另外不推荐使用onWindowFocus方法
		mHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				Log.i("UPDATE_LOG", "---======--");
				long timeStamp = ContextConfig.getInstance().getLongWithFilename(GlobalConstants.AppConstact.CHECK_UPDATE_TIMESTAMP, 0);
				long currentTime = System.currentTimeMillis();
				// TimeUnit.DAYS.toMillis(1))
				// {
				if (currentTime - timeStamp > TimeUnit.DAYS.toMillis(1)) {
					if (contextUtil.hasNewVersion()) {
						ContextConfig.getInstance().putLongWithFilename(GlobalConstants.AppConstact.CHECK_UPDATE_TIMESTAMP, currentTime);
						try {
							updateDialog(contextUtil.getNewVersionDetails());
						} catch (Exception e) {
							XL.d(TAG, "提示有新版本失败", e);
						}
					}
				}
			}
		}, 1000);

		// bus.register(getNewMsgCount);
		bus.register(clearNotify);
		bus.register(newCommentPushEventListener);
		bus.register(mListenerBottomTagPostion);
		// XL.d(TAG, "注册票无效事件");
		bus.register(ticketInvalidEventListener);
		bus.register(refreshFilterBtnEventListener);
		refreshChatSub();
		showOrHideTabFeedNotice(false, NotificationCount.getCommentCount());

		if (contextUtil.isTestingApi()) {
			ToastUtil.longToast(this, "您正在使用测试环境");
		}
	}

	private void popUpdateDescription() {
		// TODO 1.6.2更新提示，如果下版本不使用，删除
		if (mIsFirstEnterFeedsDialog == null && ContextConfig.getInstance().getBooleanWithDefValue(GlobalConstants.AppConstact.IS_FIRST_ENTER_160V, true)) {
			mIsFirstEnterFeedsDialog = new PopupStore().showFirstEnterFeedsPopDialog(this);
		}
	}

	/**
	 * 刷新顾虑按钮上显示的过滤地区
	 * 
	 * @return void
	 */
	private void refreshFilterBtnText() {
		// 开启过滤界面控件上显示当前过滤地区，没有则为默认
		String region = contextUtil.getFilterRegion();
		if (TextUtils.isEmpty(region)) {
			region = getString(R.string.system_filtr);
		}
		mOpenFilter.setText(region);
	}

	private void updateDialog(String updateDetails) {
		View view = getLayoutInflater().inflate(R.layout.dialog_update, new LinearLayout(mContext), false);
		TextView titleTv = (TextView) view.findViewById(R.id.update_title);
		titleTv.setText(R.string.update);
		TextView messageTv = (TextView) view.findViewById(R.id.update_message);
		messageTv.setText(updateDetails);
		updateAlert = new AlertDialog.Builder(this)//
		.setView(view)//
		.setPositiveButton(R.string.confirm_updata_version, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				startDownloadService();
				if (updateAlert != null && updateAlert.isShowing()) {
					updateAlert.dismiss();
				}
			}
		}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (updateAlert != null && updateAlert.isShowing()) {
					updateAlert.dismiss();
				}
			}
		}).create();
		updateAlert.show();
	}

	private void startDownloadService() {
		Intent intent = new Intent(mContext, DownloadService.class);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			startForegroundService(intent);
		}else{
			startService(intent);
		}

		// 正式版使用这个地址
		DownloadService.downNewFile(mContext, "http://wealoha.com/get/android", 351, "Aloha");
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (mIsFirstEnterFeedsDialog != null && mIsFirstEnterFeedsDialog.isShowing()) {
			mIsFirstEnterFeedsDialog.dismiss();
			mIsFirstEnterFeedsDialog = null;
		}
	}

	@Override
	protected void onPause() {

		isDestory = true;
		if (popWin != null && popWin.isShowing()) {
			popWin.dismiss();
		}
		super.onPause();
		if (counterRetrieveTask != null) {
			// Log.d(TAG, "干掉Feed定时");
			counterRetrieveTask.cancel();
			counterRetrieveTask = null;
		}
		try {
			unregisterReceiver(mHomeKeyEventReceiver);
			// bus.unregister(getNewMsgCount);
			unregisterReceiver(mBroadcastReceiver);
		} catch (Throwable e) {
			XL.w(TAG, "取消注册失败", e);
		}
		try {
			bus.unregister(clearNotify);
			bus.unregister(ticketInvalidEventListener);
			bus.unregister(newCommentPushEventListener);
			bus.unregister(mListenerBottomTagPostion);
			bus.unregister(refreshFilterBtnEventListener);
		} catch (Throwable e) {
			XL.w(TAG, "取消注册失败", e);
		}

	}

	/**
	 * 进入Feed的时候要清理下
	 */
	public void newFeedTagDotCleared() {
		showOrHideTabFeedNotice(false, 0);
	}

	public void newNotifyCountCleared() {
		// 触发Notify清理的时候，Feed肯定已经看过了，红点消失
		showOrHideTabFeedNotice(false, 0);
	}

	/**
	 * 加载开机画面的target
	 */
	private Target picTarget = new Target() {

		@Override
		public void onBitmapLoaded(Bitmap bitmap, LoadedFrom arg1) {
			String cachePaht = FileTools.getDiskCacheDir(mContext, "launcherimg.png").getAbsolutePath();
			String path = ImageUtil.saveToLocal(bitmap, cachePaht);
			contextUtil.setStartupImagePath(path);// 预加载，不显示
		}

		@Override
		public void onBitmapFailed(Exception e, Drawable errorDrawable) {

		}

		@Override
		public void onPrepareLoad(Drawable placeHolderDrawable) {

		}
	};

	/**
	 * 开机画面和升级等信息
	 */
	private void fetchUpdateAndStartup() {
		constantsService.get(new Callback<ApiResponse<ConstantsData>>() {

			@Override
			public void success(ApiResponse<ConstantsData> apiResponse, Response response) {
				if (apiResponse == null) {
					return;
				}
				if (apiResponse.isOk()) {
					XL.d(TAG, "有新版本可以更新");
					contextUtil.setHasNewestVersion(apiResponse.getData().hasUpdateVersion);
					contextUtil.setNewVersionDetails(apiResponse.getData().updateDetails);
					contextUtil.setStartImageIntervalMinutes(apiResponse.getData().startupImageShowIntervalMinutes);
					Map<String, String> startupImageMap = apiResponse.getData().startupImageMap;
					// 下面两行测试数据，可以测试开机画面
					if (startupImageMap != null && startupImageMap.size() > 0) {
						// 有开机画面了，根据手机尺寸取一张下载，下次显示
						XL.i(TAG, "有开机画面，设置下次使用: " + startupImageMap);
						int screenWidth = mScreenWidth;
						String imageId = null;
						if (screenWidth > 720) {
							imageId = startupImageMap.get(ConstantsData.STARTUP_IMAGE_1080P);
						}
						if (imageId == null) {
							imageId = startupImageMap.get(ConstantsData.STARTUP_IMAGE_720P);
						}

						if (imageId != null) {
							// contextUtil.setStartupImageId(imageId);// 预加载，不显示
							// 注意，这里预加载的图和显示的尺寸要一致！
							String imageUrl = ImageUtil.getImageUrl(imageId, 0, null);
							Picasso.get().load(imageUrl).into(picTarget);
						}
					} else {
						XL.i(TAG, "清理开机画面");
						if (contextUtil.getStartupImageId() != null) {
							contextUtil.setStartupImageId(null);
						}

						if (contextUtil.getStartupImagePath() != null) {
							contextUtil.setStartupImagePath(null);
						}
					}
				}
			}

			@Override
			public void failure(RetrofitError error) {
			}
		});
	}

	private void initFeedAndNotifyCounterTimer() {
		// 主界面后马上取一次，然后定时刷新
		if (counterRetrieveTask == null) {
			counterRetrieveTask = new Timer();
			counterRetrieveTask.scheduleAtFixedRate(new TimerTask() {

				@Override
				public void run() {
					countService.count(new Callback<ApiResponse<CountData>>() {

						@Override
						public void success(ApiResponse<CountData> apiResponse, Response response) {
							// 触发tab的红点
							if (apiResponse != null && apiResponse.isOk()) {
								NotificationCount.setCommentCount(apiResponse.getData().newNotifyCount);
								if (apiResponse.getData().newFeed) {
									showOrHideTabFeedNotice(apiResponse.getData().newFeed, apiResponse.getData().newNotifyCount);
								}
								// 发通知
								// busSentEvent(new
								// NotifyCountEvent(result.getData().newNotifyCount));
								// newPostNotify();
							}
						}

						public void failure(RetrofitError error) {
							Log.w(TAG, "取计数出错", error);
						};
					});
				}
			}, 300, TimeUnit.MINUTES.toMillis(2));
		}
	}

	private void showOrHideTabFeedNotice(boolean showFeed, int newNotifyCount) {
		if (feedSub == null || feedSubSecound == null) {
			return;
		}
		if (newNotifyCount > 0) {
			feedSub.setVisibility(View.VISIBLE);
			feedSub.setText(newNotifyCount + "");
			feedSubSecound.setVisibility(View.GONE);
		} else if (showFeed) {
			feedSub.setVisibility(View.GONE);
			feedSubSecound.setVisibility(View.VISIBLE);
		} else {
			feedSub.setVisibility(View.GONE);
			feedSubSecound.setVisibility(View.GONE);
		}

		// FIXME 逻辑冗余
		if (contextUtil.getForegroundFrag() != null && contextUtil.getForegroundFrag() instanceof Feed2Fragment) {
			// 如果当前页是feed 那么只显示feed上的角标，主界面的tab栏不显示
			feedSub.setVisibility(View.GONE);
			((Feed2Fragment) contextUtil.getForegroundFrag()).setNewNoticeSub(NotificationCount.getCommentCount(), "main:showOrHideTabFeedNotice");
		} else if (mFeedFragRb != null && mFeedFragRb.isChecked()) {
			feedSub.setVisibility(View.GONE);
		}
	}

	/**
	 * @Description:
	 * @see:
	 * @since:
	 * @description
	 * @author: sunkist
	 * @date:2014-11-5
	 */
	public void switchFrag(Fragment hide, Fragment add) {
		if (mFm.popBackStackImmediate()) {

		}
	}

	private static final int EXIT_BACK_NOTICE = 111;

	public void switchContent(Fragment fragment) {
		getSupportFragmentManager().beginTransaction().replace(R.id.content, fragment).commit();
	}

	public void switchContentAllowStateLoss(Fragment fragment) {
		getSupportFragmentManager().beginTransaction().replace(R.id.content, fragment).commitAllowingStateLoss();
	}

	protected void onRestart() {
		super.onRestart();
	}

	// 更新角标
	public void setAlohaSub(int count) {
		alohaSub.setVisibility(View.VISIBLE);
		alohaSub.setText(count + "");
	}

	/**
	 * 设置聊天的数量
	 * 
	 * @param count
	 */
	public void setChatSub(int count) {
		if (chatSub == null) {
			return;
		}
		XL.d(TAG, "更新聊天未读: " + count);
		XL.d("NOTIFY_COUNT_TAG", "contextUtil.getForegroundFrag(): " + contextUtil.getForegroundFrag() + "----" + (contextUtil.getForegroundFrag() instanceof FeedFragment));
		if (count > 0) {
			chatSub.setVisibility(View.VISIBLE);
			chatSub.setText(count > 99 ? "···" : count + "");
		} else if (chatSub.getVisibility() == View.VISIBLE) {
			chatSub.setVisibility(View.GONE);
		}
	}

	public void setProfileSub(int count) {
		profileSub.setVisibility(View.VISIBLE);
		profileSub.setText(count + "");
	}

	@Override
	public void onSlide(View view, int status) {

	}

	/**
	 * 当前哪个Tab被选中了？
	 * 
	 * @return
	 */
	private Integer getCurrentChechBox() {

		for (CheckBox cb : new CheckBox[] { mAlohaFragRb, mChatFragRb, mFeedFragRb, mProfileFragRb }) {
			if (cb.isChecked()) {
				return cb.getId();
			}
		}
		return null;
	}

	@SuppressWarnings("unused")
	@Override
	public boolean onBackKeyPressed() {
		if (FRAGMENT_BACKSTACK_ENABLED && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			// 只有jelly可用
			BaseFragment fragment = (BaseFragment) mTabsFirstFragments.get(R.id.feed_frag_rb);
			if (fragment != null) {
				XL.d(TAG, "可以后退指定fragment");

				int childCount = fragment.getChildFragmentManager().getBackStackEntryCount();

				if (childCount == 0) {
					// it has no child Fragment
					// can not handle the onBackPressed task by itself
					XL.d(TAG, "没有更多可以退出了");
					// return false;

					// 上传处理
					super.onBackPressed();
					return true;
				} else {
					// get the child Fragment
					FragmentManager childFragmentManager = fragment.getChildFragmentManager();

					childFragmentManager.popBackStack();
					XL.d(TAG, "退出");
					super.onBackPressed();
					return false;
				}
			}
		}
		// 连按两次退出
		if (lastBackPressedMillis + 3000 > System.currentTimeMillis()) {
			// 退出
			super.onBackPressed();
			android.os.Process.killProcess(android.os.Process.myPid());
			ActivityManager.popAll();
			android.app.ActivityManager manager = (android.app.ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
			manager.restartPackage(getPackageName());
			System.exit(0);
		} else {
			// 第一次按，只提示
			lastBackPressedMillis = System.currentTimeMillis();
			ToastUtil.shortToast(this, getString(R.string.back_to_exit_notice));
			return true;
		}
		return true;

	}

	private boolean shouldInit() {
		android.app.ActivityManager am = ((android.app.ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
		List<RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
		String mainProcessName = getPackageName();
		int myPid = android.os.Process.myPid();
		for (RunningAppProcessInfo info : processInfos) {
			if (info.pid == myPid && mainProcessName.equals(info.processName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @Description: 默认情况下，我们会将日志内容写入SDCard/Android/data/app pkgname/files/MiPushLog目录下的文件
	 *               。如果app需要关闭写日志文件功能（不建议关闭），只需要调用Logger .disablePushFileLog(context)即可。
	 * @see:
	 * @since:
	 * @description
	 * @author: sunkist
	 * @date:2014-12-4
	 */
	private void initXiaomiPushConfig() {

		if (shouldInit()) {
			// XL.d(TAG, "注册push服务");
			MiPushClient.registerPush(AppApplication.getInstance(), GlobalConstants.AppConstact.XIAOMI_PUSH_APPID, GlobalConstants.AppConstact.XIAOMI_PUSH_APPKEY);
		}
	}

	/**
	 * 向总线发送事件
	 * 
	 * @param event
	 */
	public void busSentEvent(final Object event) {
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				bus.post(event);
			}
		});
	}

	String openFrom = null;

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent result) {
		super.onActivityResult(requestCode, resultCode, result);
		// isOpenCamera = false;
		if (resultCode == RESULT_OK) {

			switch (requestCode) {
			case GlobalConstants.AppConstact.PHOTO_PICKED_WITH_DATA: {
				if (result == null) {// NullPointerException
					return;
				}
				openFrom = GlobalConstants.AppConstact.IMG_PATH_FROM_PHOTO_PICKED_WITH_DATA;
				getSelectedImgPath(result.getData(), GlobalConstants.AppConstact.IMG_PATH_FROM_PHOTO_PICKED_WITH_DATA, TAG);
			}
				break;
			case GlobalConstants.AppConstact.CAMERA_WITH_DATA: {
				if (mCameraImgFile == null) {// NullPointerException
					return;
				}
				openFrom = GlobalConstants.AppConstact.IMG_PATH_FROM_CAMERA_WITH_DATA;
				goImgCropAct(mCameraImgFile.getAbsolutePath(), GlobalConstants.AppConstact.IMG_PATH_FROM_CAMERA_WITH_DATA, TAG);
			}
				break;
			case GlobalConstants.AppConstact.FLAG_MODIFY_FINISH: {// 进入裁剪界面
				if (result == null) {// NullPointerException
					return;
				}
				goPicSendAct(result);
			}
				break;
			case GlobalConstants.AppConstact.IS_REFRESH_PROFILE_HEAD_ICON: {// 刷新头像
				refreshFeedList(result);
			}
				break;
			case GlobalConstants.AppConstact.OPEN_COMPOSE_FEED: // 刷新feed列表
				break;
			case FeedFragment.OPEN_NOTIFY_REQUESTCODE:
				bus.post(new RefreshFeedListEvent(0));
				break;
			}
		}
	}

	private void refreshFeedList(Intent result) {
		if (TextUtils.isEmpty(userid)) {
			userid = contextUtil.getCurrentUser().getId();
		}
		if (result != null) {
			isRefreshHeadIcon = result.getBooleanExtra("isRefreshHeadIcon", false);
		}
	}

	private void goPicSendAct(Intent result) {
		String path = result.getStringExtra("path");
		if (TextUtils.isEmpty(path)) {
			return;
		}
		Intent intent = new Intent(this, PicSendActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("path", path);
		bundle.putString("openType", MainAct.TAG);
		bundle.putString("openFrom", openFrom);// 需要从发送feed界面回来到图片的获取方式：相机或者图库
		intent.putExtras(bundle);
		startActivityForResult(intent, GlobalConstants.AppConstact.OPEN_COMPOSE_FEED);
		overridePendingTransition(R.anim.left_in, 0);
	}

	/**
	 * @Title: refreshChatSub
	 * @Description: 刷新未读消息数目
	 * @return void 返回类型
	 */
	private void refreshChatSub() {
		mMessageService.unread(new Callback<ApiResponse<UnreadData>>() {

			@Override
			public void failure(RetrofitError arg0) {
				// 不做任何请求
			}

			@Override
			public void success(ApiResponse<UnreadData> apiResponse, Response arg1) {
				if (apiResponse != null && apiResponse.isOk()) {
					setChatSub(apiResponse.getData().count);
				}
			}
		});
	}

	private Object ticketInvalidEventListener = new Object() {

		@Subscribe
		public void onEvent(TicketInvalidEvent e) {
			// 登出当前用户的数据，清理登录状态
			// XL.d(TAG, "票无效，请求401了，回到未登录状态");
			contextUtil.cleanLoginStatus();
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setClass(MainAct.this, WelcomeAct.class);
			MainAct.this.startActivity(intent);
			// 不能后退了
			MainAct.this.finish();
		}
	};

	private Object newCommentPushEventListener = new Object() {

		@Subscribe
		public void onEvent(NewCommentPushEvent e) {
			// 登出当前用户的数据，清理登录状态
			// XL.d(TAG, "票无效，请求401了，回到未登录状态");
			XL.i("XM_PUSH_BROADCAST", "handleMessage:" + NotificationCount.getCommentCount());
			showOrHideTabFeedNotice(false, NotificationCount.getCommentCount());
		}
	};

	private Object refreshFilterBtnEventListener = new Object() {

		@Subscribe
		public void onEvent(RefreshFilterBtnEvent e) {
			// ToastUtil.longToast(mContext, "refreshFilterBtnEventListener");
			refreshFilterBtnText();
		}
	};

	public Handler getHandler() {
		if (mHandler == null) {
			mHandler = new Handler();
		}
		return mHandler;
	}

	/**
	 * @Description:返回上一个Fragment的操作
	 * @param msg
	 * @see:
	 * @since:
	 * @description
	 * @author: sunkist
	 * @date:2015-1-9
	 */
	private void returnNextFrag(Message msg) {
		Bundle bundle = (Bundle) msg.obj;
		AlohaFragment fragment = null;
		if (bundle != null) {
			mToUser = (User) bundle.getParcelable(User.TAG);
			AppApplication.mUserList.add(0, mToUser);
			fragment = new AlohaFragment();
			getSupportFragmentManager().beginTransaction().setCustomAnimations(R.animator.slide_fragment_horizontal_right_in, R.animator.slide_fragment_horizontal_left_out).replace(R.id.content, fragment).commit();
		} else {
			mToUser = null;
		}
		if (fragment != null && fragment.isAdded()) {
			AlohaFragment.mExcuteOver = true;
		}
	}

	/**
	 * @Description:设置User的Tag集合.
	 * @param recommendSourceMap
	 * @see:
	 * @since:
	 * @description
	 * @author: sunkist
	 * @date:2015-1-13
	 */
	public void setRecommendSourceMap(Map<String, String> recommendSourceMap) {
		mRecommendSourceMap = recommendSourceMap;
	}

	/**
	 * @Description:查询User的Tag信息
	 * @param userId
	 * @return
	 * @see:
	 * @since:
	 * @description
	 * @author: sunkist
	 * @date:2015-1-13
	 */
	public String getUserTag(String userId) {
		if (mRecommendSourceMap != null) {
			String tag = null;
			int resId = 0;
			Set<Entry<String, String>> entries = mRecommendSourceMap.entrySet();
			for (Entry<String, String> entry : entries) {
				if (userId.equals(entry.getKey())) {
					tag = entry.getValue();
				}
			}
			if ("Random".equals(tag)) {
				resId = R.string.select_user_type_random;
			} else if ("Online".equals(tag)) {
				resId = R.string.select_user_type_online;
			} else if ("Near".equals(tag)) {
				resId = R.string.select_user_type_near;
			} else if ("Personalized".equals(tag)) {
				resId = R.string.select_user_type_personalized;
			} else if ("New".equals(tag)) {
				resId = R.string.select_user_type_new;
			} else if ("Hot".equals(tag)) {
				resId = R.string.select_user_type_hot;
			} else if ("Local".equals(tag)) {
				resId = R.string.select_user_type_local;
			}
			if (resId == 0) {
				return null;
			} else {
				tag = mContext.getString(resId);
			}
			return tag;
		} else {
			return null;
		}
	}

	public void hideMenuBar(boolean isHide) {
		if (mMenuBar != null) {
			if (!isHide) {
				mMenuBar.setVisibility(View.VISIBLE);
			} else {
				mMenuBar.setVisibility(View.GONE);
			}
		}
	}

	@Override
	@OnClick({ R.id.aloha_add, R.id.aloha_filtrate })
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.aloha_add:
			openAddPop();
			break;
		case R.id.aloha_filtrate:
			// Intent intent = new Intent(this, ProFeatureAct.class);
			getUserMatchSetting();
			break;
		default:
			break;
		}
	}

	/**
	 * 获取个人设置界面信息，成功则开启个人设置界面
	 * 
	 * @return void
	 */
	public void getUserMatchSetting() {
		mOpenFilter.setEnabled(false);
		mUserAPI.userMatchSetting(new Callback<ApiResponse<MatchSettingData>>() {

			@Override
			public void success(ApiResponse<MatchSettingData> apiResponse, Response response) {
				mOpenFilter.setEnabled(true);
				if (apiResponse != null && apiResponse.isOk()) {
					Intent intent = new Intent(MainAct.this, FilterSettingAct.class);
					intent.putExtra(MatchSettingData.TAG, apiResponse.getData());
					startActivity(intent);
					overridePendingTransition(R.anim.sliding_in_down2up, R.anim.stop);
				} else {
					ToastUtil.shortToast(mContext, R.string.network_error);
				}
			}

			@Override
			public void failure(RetrofitError arg0) {
				mOpenFilter.setEnabled(true);
				ToastUtil.shortToast(mContext, R.string.Unkown_Error);
			}
		});
	}

	private PopupWindow popWin;

	public void openAddPop() {
		popView = getLayoutInflater().inflate(R.layout.popup_add, new LinearLayout(this), false);

		if (popWin != null && popWin.isShowing()) {
			popWin.dismiss();
		} else {
			LinearLayout findyou = (LinearLayout) popView.findViewById(R.id.find_you_container);
			LinearLayout openEws = (LinearLayout) popView.findViewById(R.id.ews_container);
			FontUtil.setSemiBoldTypeFace(this, findyou);
			FontUtil.setSemiBoldTypeFace(this, openEws);
			openEws.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					openEsc();
				}
			});
			findyou.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					openFindYouAct();
				}
			});
			popWin = new PopupWindow(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			popWin.setOutsideTouchable(true);
			popWin.setBackgroundDrawable(new ColorDrawable());
			popWin.setContentView(popView);
			popWin.showAsDropDown(mAdd, -UiUtils.dip2px(mContext, 100), 0);
		}
	}

	/**
	 * @Description:打开二维码扫描
	 * @see:
	 * @since:
	 * @description
	 * @author: sunkist
	 * @date:2015-1-22
	 */
	protected void openEsc() {
		closeAddPop();
		initPopupView();
		startActivityHasAnim(GlobalConstants.IntentAction.INTENT_URI_CAPTUREACTIVITY, null, R.anim.bottom_in, R.anim.stop);
	}

	private void openFindYouAct() {
		closeAddPop();
		startActivityNotFinish(GlobalConstants.IntentAction.INTENT_URI_FIND_YOU, null, R.anim.push_bottom_in, R.anim.stop);
	}

	private void closeAddPop() {
		if (popWin != null && popWin.isShowing()) {
			popWin.dismiss();
		}
	}

	private BroadcastReceiver mHomeKeyEventReceiver = new BroadcastReceiver() {

		String SYSTEM_REASON = "reason";
		String SYSTEM_HOME_KEY = "homekey";

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(action)) {
				String reason = intent.getStringExtra(SYSTEM_REASON);
				if (TextUtils.equals(reason, SYSTEM_HOME_KEY)) {
					if (AppApplication.mUserList != null) {
						mToUser = null;
						AppApplication.mUserList.clear();
					}
				}
			}
		}
	};
	public Object mListenerBottomTagPostion = new Object() {

		@Subscribe
		public void onEvent(Listeners.MonitorMainUiBottomTagPostion monitorMainUiBottomTagPostion) {
			if (monitorMainUiBottomTagPostion == null || mAlohaFragRb == null || mFeedFragRb == null || mProfileFragRb == null || mChatFragRb == null) {
				return;
			}
			int position = monitorMainUiBottomTagPostion.getPostion();
			switch (position) {
			case 0:
				mAlohaFragRb.setChecked(true);
				mFeedFragRb.setChecked(false);
				mProfileFragRb.setChecked(false);
				mChatFragRb.setChecked(false);
				break;
			case 1:
				mAlohaFragRb.setChecked(false);
				mFeedFragRb.setChecked(false);
				mProfileFragRb.setChecked(false);
				mChatFragRb.setChecked(true);
				break;
			case 2:
				mAlohaFragRb.setChecked(false);
				mFeedFragRb.setChecked(true);
				mProfileFragRb.setChecked(false);
				mChatFragRb.setChecked(false);
				break;
			case 3:
				mAlohaFragRb.setChecked(false);
				mFeedFragRb.setChecked(false);
				mProfileFragRb.setChecked(true);
				mChatFragRb.setChecked(false);
				break;
			}
		}
	};

	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Push2Type.InboxMessageNew.getType())) {
				if (getCurrentChechBox() != mChatFragRb.getId()) {
					refreshChatSub();
				}
			}
		}
	};
}
