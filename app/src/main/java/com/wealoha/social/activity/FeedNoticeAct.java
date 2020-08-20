package com.wealoha.social.activity;

import javax.inject.Inject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;

import com.squareup.otto.Subscribe;
import com.wealoha.social.ActivityManager;
import com.wealoha.social.BaseFragAct;
import com.wealoha.social.R;
import com.wealoha.social.adapter.Notify2Apapter;
import com.wealoha.social.adapter.feed.FeedHolder;
import com.wealoha.social.api.common.ApiErrorCode;
import com.wealoha.social.beans.NewAlohaNotify2;
import com.wealoha.social.beans.Notify2;
import com.wealoha.social.beans.PostCommentNotify2;
import com.wealoha.social.beans.PostCommentReplyOnMyPost;
import com.wealoha.social.beans.PostCommentReplyOnOthersPost;
import com.wealoha.social.beans.PostLikeNotify2;
import com.wealoha.social.beans.PostTagNotify2;
import com.wealoha.social.api.notify2.service.Notify2Service;
import com.wealoha.social.beans.Post;
import com.wealoha.social.beans.User;
import com.wealoha.social.api.CommentService;
import com.wealoha.social.api.FeedService;
import com.wealoha.social.commons.GlobalConstants;
import com.wealoha.social.event.push.NewNotifyEvent;
import com.wealoha.social.fragment.FeedCommentFragment;
import com.wealoha.social.fragment.Profile2Fragment;
import com.wealoha.social.fragment.SingletonFeedFragment;
import com.wealoha.social.push.notification.NotificationCount;
import com.wealoha.social.utils.ContextUtil;
import com.wealoha.social.utils.DockingBeanUtils;
import com.wealoha.social.utils.FontUtil.Font;
import com.wealoha.social.utils.XL;
import com.wealoha.social.view.custom.dialog.ReplyNoticeDialog;
import com.wealoha.social.widget.BaseListApiAdapter.LoadCallback;
import com.wealoha.social.widget.BaseListApiAdapter.LoadNewCallback;
import com.wealoha.social.widget.ScrollToLoadMoreListener;

/**
 * Feed -> 通知列表
 * 
 * @author sunkist
 * @author superman
 * @author javamonk
 * @see
 * @since
 * @date 2014-11-20 13:56:51
 */
public class FeedNoticeAct extends BaseFragAct implements OnClickListener, OnItemClickListener,
		OnTouchListener {

	@Inject
	Notify2Service mNotify2Service;
	@Inject
	ContextUtil contextUtil;
	@Inject
	Context context;
	@Inject
	FeedService mFeedService;
	@Inject
	CommentService mCommentService;
	@InjectView(R.id.feed_notice_tv)
	TextView feed_notice_tv;
	@InjectView(R.id.frag_feed_msg_tv)
	ImageView frag_feed_msg_tv;
	@InjectView(R.id.feed_notify_lv)
	ListView mListView;
	@InjectView(R.id.feed_layout)
	View mContainerView;
	@InjectView(R.id.feed_notify_list_container)
	RelativeLayout mListViewContainer;
	@InjectView(R.id.feed_title)
	RelativeLayout mTitle;
	@InjectView(R.id.feed_notify_pb)
	ProgressBar mFeedNotifyLoading;
	@InjectView(R.id.menu_01)
	TextView mMenu01;
	@InjectView(R.id.menu_02)
	TextView mMenu02;
	@InjectView(R.id.menu01_root)
	LinearLayout mMenuRoot01;
	@InjectView(R.id.menu02_root)
	LinearLayout mMenuRoot02;
	@InjectView(R.id.menu01_bottom_line)
	View mMenuLine01;
	@InjectView(R.id.menu02_bottom_line)
	View mMenuLine02;

	MainAct mMainAct;
	private Notify2Apapter adapter;
	private Notify2Apapter adapter02;
	private ReplyNoticeDialog replyNoticeDialog;
	public final static int START_SINGLETON_FEED = 0x000001;
	private static final int FEED_NUM = 30;
	private static final int ALOHA_NOTIFY = 2;
	private static final int NOTIFY = 3;

	private int notifyType;

	/**
	 * 当Push来了之后，Notification.notifyCount>0的情况下，触发该事件
	 */
	private Object eventListenerNewNotify = new Object() {

		@Subscribe
		public void onEvent(NewNotifyEvent event) {
			// XL.d("NewNotifyEvent", "来通知了.");
			adapter.tryLoadNew(event.notifyCount, true, new LoadNewCallback() {

				@Override
				public void success(boolean hasNew) {
					// XL.d("NewNotifyEvent", "刷新数据成功，新数据: " + hasNew);
					if (hasNew) {
						mNotify2Service.clearUnread();
					}
				}

				public void fail(ApiErrorCode code, Exception exception) {
					// XL.w("NewNotifyEvent", "加载数据失败: " + code, exception);
				}
			});
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_feed_notice);
		// 通知计数器清0
		NotificationCount.setCommentCount(0);
		ActivityManager.push(this);
		mListView.setOnScrollListener(new ScrollToLoadMoreListener(20, new ScrollToLoadMoreListener.Callback() {

			@Override
			public void loadMore() {
				loadNextPage(false);
			}
		}));
		mListView.setOnItemClickListener(this);
		mFeedNotifyLoading.setVisibility(View.VISIBLE);
		// 适配器
		adapter = new Notify2Apapter(this, mNotify2Service);
		adapter02 = new Notify2Apapter(this, mNotify2Service);
		mListView.setAdapter(adapter02);
		adapter.resetState();
		adapter02.resetState();
		notifyType = NOTIFY;

		mMenuRoot01.setOnTouchListener(this);
		mMenuRoot02.setOnTouchListener(this);
		loadNextPage(true);
	}

	@Override
	protected void initTypeFace() {
		fontUtil.changeFonts(mTitle, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
	}

	/**
	 * 加载下一页数据
	 * 
	 * @param fromStart
	 *            是否从头开始
	 */
	private void loadNextPage(boolean fromStart) {
		if (fromStart) {
			adapter.resetState();
			adapter02.resetState();
		}

		if (notifyType == ALOHA_NOTIFY) {// 复合的notify
			adapter.loadEarlyPage(FEED_NUM, true, new LoadCallback() {

				@Override
				public void success(boolean hasPrev, boolean hasNext) {
					XL.i("FEED_NOTIFY_ACT_TEST", "success");
					mFeedNotifyLoading.setVisibility(View.GONE);
				}

				public void fail(ApiErrorCode error, Exception exception) {
					XL.i("FEED_NOTIFY_ACT_TEST", "fail:");
					mFeedNotifyLoading.setVisibility(View.GONE);
				}

				@Override
				public void dataState(int size, boolean isnull) {

				}
			});
		} else {// aloha 用户的notify
			adapter02.loadEarlyPage(FEED_NUM, null, new LoadCallback() {

				@Override
				public void success(boolean hasPrev, boolean hasNext) {
					XL.i("FEED_NOTIFY_ACT_TEST", "success");
					mFeedNotifyLoading.setVisibility(View.GONE);
				}

				public void fail(ApiErrorCode error, Exception exception) {
					XL.i("FEED_NOTIFY_ACT_TEST", "fail:");
					mFeedNotifyLoading.setVisibility(View.GONE);
				}

				@Override
				public void dataState(int size, boolean isnull) {

				}
			});
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		// 通知数清零
		clearUnread();

		// 注册push监听
		bus.register(eventListenerNewNotify);

		// 从头开始加载数据
	}

	@OnClick({ //
	R.id.feed_notice_tv,//
	R.id.frag_feed_msg_tv, //
	R.id.comments_send_tv,//
	})
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.feed_notice_tv:
			break;
		case R.id.frag_feed_msg_tv:
			finish();
			break;
		}
	}

	/***
	 * 切换通知内容
	 * 
	 * @param type
	 *            {@link #NOTIFY} 复合通知，{@link #ALOHA_NOTIFY} aloha 通知
	 * @return void
	 */
	private void changeNotifyType(int type) {
		notifyType = type;
		Notify2Apapter adapterTemp;
		if (type == ALOHA_NOTIFY) {
			mMenu01.setTextColor(getResources().getColor(R.color.black_text));
			mMenu02.setTextColor(getResources().getColor(R.color.red));

			mMenuLine01.setVisibility(View.INVISIBLE);
			mMenuLine02.setVisibility(View.VISIBLE);
			adapterTemp = adapter;
		} else {
			mMenu01.setTextColor(getResources().getColor(R.color.red));
			mMenu02.setTextColor(getResources().getColor(R.color.black_text));
			mMenuLine02.setVisibility(View.INVISIBLE);
			mMenuLine01.setVisibility(View.VISIBLE);
			adapterTemp = adapter02;
		}
		initAdapter(adapterTemp);
		mListView.setAdapter(adapterTemp);
	}

	/***
	 * 切换数据源的时候，如果当前数据源没有数据，那么要初始化数据
	 * 
	 * @param adapter
	 *            要切换到的数据源
	 * @return void
	 */
	private void initAdapter(Notify2Apapter adapter) {
		if (adapter.getListData() == null || adapter.getListData().size() == 0) {
			boolean alohatype = false;
			if (notifyType == ALOHA_NOTIFY) {
				alohatype = true;
			}
			adapter.loadEarlyPage(FEED_NUM, alohatype, new LoadCallback() {

				@Override
				public void success(boolean hasPrev, boolean hasNext) {
					XL.i("FEED_NOTIFY_ACT_TEST", "init adatpter success");
					mFeedNotifyLoading.setVisibility(View.GONE);
				}

				public void fail(ApiErrorCode error, Exception exception) {
					XL.i("FEED_NOTIFY_ACT_TEST", "init adatpter fail:");
					mFeedNotifyLoading.setVisibility(View.GONE);
				}

				@Override
				public void dataState(int size, boolean isnull) {

				}
			});
		}
	}

	/**
	 * @Title: clearUnread
	 * @Description: 清除服务器未读消息数
	 */
	private void clearUnread() {
		mNotify2Service.clearUnread();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
		Notify2 mNotify2 = (Notify2) mListView.getItemAtPosition(position);
		adapter.changeItemState(false, position);
		if (mNotify2 instanceof PostTagNotify2) {
			PostTagNotify2 postTagNotify2 = (PostTagNotify2) mNotify2;
			Bundle bundle = new Bundle();
			bundle.putSerializable(Post.TAG, postTagNotify2.getPost());
			bundle.putInt(SingletonFeedFragment.TAG, FeedHolder.TAGS_HOLDER);
			startFragment(SingletonFeedFragment.class, bundle, true);
		} else if (mNotify2 instanceof PostCommentNotify2) {
			PostCommentNotify2 mPostCommentNotify2 = (PostCommentNotify2) mNotify2;
			openFeedCommentFragment(mPostCommentNotify2.getPost(), mPostCommentNotify2.getCommentId());
		}else if(mNotify2 instanceof PostCommentReplyOnOthersPost){ 
			PostCommentReplyOnOthersPost postCommentReplyOnOthersPostNotify = (PostCommentReplyOnOthersPost) mNotify2;
			openFeedCommentFragment(postCommentReplyOnOthersPostNotify.getPost(), postCommentReplyOnOthersPostNotify.getCommentId());
		}else if(mNotify2 instanceof PostCommentReplyOnMyPost){
			PostCommentReplyOnMyPost postCommentReplyOnMyPostNotify = (PostCommentReplyOnMyPost) mNotify2;
			openFeedCommentFragment(postCommentReplyOnMyPostNotify.getPost(), postCommentReplyOnMyPostNotify.getCommentId());
		}else if (mNotify2 instanceof PostLikeNotify2) {
			PostLikeNotify2 postLikeNotify2 = (PostLikeNotify2) mNotify2;
			// 只有一个人的时候开启他的profile
			if (postLikeNotify2.getUsers() != null && postLikeNotify2.getUsers().size() == 1) {
				openSomeoneProfile(postLikeNotify2.getUsers().get(0));
			} else {
				Bundle bundle = new Bundle();
				bundle.putSerializable("Users", postLikeNotify2);
				Intent intent = new Intent(this, NewAlohaActivity.class);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		} else if (mNotify2 instanceof NewAlohaNotify2) {
			NewAlohaNotify2 newAlohaNotify2 = (NewAlohaNotify2) mNotify2;
			// 只有一个人的时候开启他的profile
			if (newAlohaNotify2.getUsers() != null && newAlohaNotify2.getUsers().size() == 1) {
				openSomeoneProfile(newAlohaNotify2.getUsers().get(0));
			} else {
				Bundle bundle = new Bundle();
				bundle.putSerializable("Users", newAlohaNotify2);
				Intent intent = new Intent(this, NewAlohaActivity.class);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		}
	}
	
	public void openFeedCommentFragment(Post post, String commentId){
		Bundle bundle = new Bundle();
		bundle.putSerializable(GlobalConstants.TAGS.POST_TAG, post);
		bundle.putString(GlobalConstants.TAGS.COMMENT_ID, commentId);
		startFragment(FeedCommentFragment.class, bundle, true);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			if (replyNoticeDialog != null && replyNoticeDialog.isShowing())
				replyNoticeDialog.dismiss();
			else {
				this.finish();
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected void onStop() {
		// 取消注册push监听
		bus.unregister(eventListenerNewNotify);
		super.onStop();

	}

	private PostCommentNotify2 mPostCommentNotify2;

	/**
	 * @Title: startSingleTagsFeed
	 * @Description: 打开的单例feed, 在FeedNoticeAdapter 中调用
	 */
	public void startSingleTagsFeed(Post post, int feedFragType) {
		Bundle bundle = new Bundle();
		bundle.putInt(SingletonFeedFragment.TAG, feedFragType);
		bundle.putSerializable(Post.TAG, post);
		startFragmentForResult(SingletonFeedFragment.class, bundle, true, START_SINGLETON_FEED, R.anim.left_in, R.anim.stop);
	}

	@Override
	protected void onActivityResult(int requestcode, int resultcode, Intent result) {
		if (resultcode == RESULT_OK) {
			if (requestcode == START_SINGLETON_FEED) {
				setResult(RESULT_OK);
				finish();
			}
		}
	}

	/**
	 * 进入这个人的主页
	 * 
	 * @param user
	 *            被开启主页的用户
	 */
	private void openSomeoneProfile(com.wealoha.social.api.user.bean.User user) {

		Bundle bundle = new Bundle();
		bundle.putSerializable(User.TAG, DockingBeanUtils.transUser(user));
		startFragment(Profile2Fragment.class, bundle, true);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() != MotionEvent.ACTION_UP) {
			return false;
		}
		switch (v.getId()) {
		case R.id.menu01_root:
			changeNotifyType(NOTIFY);
			break;
		case R.id.menu02_root:
			changeNotifyType(ALOHA_NOTIFY);
			break;
		}
		return true;
	}
}
