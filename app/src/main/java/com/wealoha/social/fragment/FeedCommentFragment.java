package com.wealoha.social.fragment;

import java.util.List;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;

import com.squareup.picasso.Picasso;
import com.wealoha.social.BaseFragAct;
import com.wealoha.social.ContextConfig;
import com.wealoha.social.R;
import com.wealoha.social.adapter.FeedCommentAdapter;
import com.wealoha.social.adapter.FeedCommentAdapter.FeedCommentAdapterCallback;
import com.wealoha.social.adapter.feed.BaseFeedHolder;
import com.wealoha.social.adapter.feed.BaseFeedHolder.Holder2FragCallback;
import com.wealoha.social.adapter.feed.FeedHolder;
import com.wealoha.social.adapter.feed.ImageFeedHolder;
import com.wealoha.social.adapter.feed.VideoFeedHolder;
import com.wealoha.social.api.ServerApi;
import com.wealoha.social.beans.Comment2GetData;
import com.wealoha.social.beans.PostComment;
import com.wealoha.social.api.Comment2ListApiService;
import com.wealoha.social.beans.ApiErrorCode;
import com.wealoha.social.api.BaseListApiService.ApiCallback;
import com.wealoha.social.beans.Direct;
import com.wealoha.social.beans.FeedGetData;
import com.wealoha.social.beans.FeedType;
import com.wealoha.social.api.Feed2ListApiService;
import com.wealoha.social.beans.Post;
import com.wealoha.social.beans.IResultDataErrorCode;
import com.wealoha.social.beans.ApiResponse;
import com.wealoha.social.beans.ResultData;
import com.wealoha.social.beans.User;
import com.wealoha.social.commons.GlobalConstants;
import com.wealoha.social.utils.FontUtil;
import com.wealoha.social.utils.ToastUtil;
import com.wealoha.social.utils.UiUtils;
import com.wealoha.social.utils.XL;
import com.wealoha.social.utils.FontUtil.Font;
import com.wealoha.social.view.custom.CircleImageView;
import com.wealoha.social.view.custom.dialog.ListItemDialog;
import com.wealoha.social.view.custom.dialog.ListItemDialog.ListItemCallback;
import com.wealoha.social.view.custom.dialog.ListItemDialog.ListItemType;
import com.wealoha.social.widget.BaseListApiAdapter.LoadCallback;

public class FeedCommentFragment extends BaseFragment implements OnClickListener,//
		OnItemClickListener, OnItemLongClickListener, ListItemCallback, FeedCommentAdapterCallback,//
		OnScrollListener, OnTouchListener, Holder2FragCallback {

	@Inject
	Picasso picasso;
	@Inject
	Feed2ListApiService feedService;
	@Inject
	ServerApi mCommentService;
	@Inject
	Comment2ListApiService mComment2Service;
	@Inject
	Context mcontext;
	@Inject
	ServerApi mFeed2API;

	@InjectView(R.id.feed_comment_back_iv)
	ImageView mBack;

	@InjectView(R.id.feed_comment_content_lv)
	ListView mContentListView;

	@InjectView(R.id.comments_content_et)
	EditText mContentEdit;

	@InjectView(R.id.comments_send_tv)
	TextView mSendTv;

	@InjectView(R.id.menu_bar)
	RelativeLayout mTitle;

	TextView mPrivacyLinkView;
	InputMethodManager imm;
	TextView mPrivacyCommentSignLayout;

	/** “加载之前的信息” 的父视图 */
	private RelativeLayout loadRootView;
	/** “加载之前的信息” 的loading */
	private ProgressBar mProgressBar;
	/** fragment 的父视图 */
	private View rootView;
	private FeedCommentAdapter mFeedCommentAdapter;
	private final int pageSize = 15;
	private Post mPost;
	private int removePosition;
	private String removeCommentId;
	private User mUser;

	private FeedHolder feedHolder;
	private VideoFeedHolder videoHolder;
	private RelativeLayout userListLayout;
	private boolean isPopSoftInputKey;
	public static final String IS_POP_SOFTINPUT_KEY = "isPopSoftInputKey";
	/**
	 * 私密留言时，在留言框中出现的私密标志
	 **/
	private Drawable wisperCommentDrawable;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.activity_feed_comment, container, false);
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		wisperCommentDrawable = getResources().getDrawable(R.drawable.message_lock);
		wisperCommentDrawable.setBounds(0, 0, wisperCommentDrawable.getMinimumWidth(), wisperCommentDrawable.getMinimumHeight());
		getData();
	}

	/***
	 * 从bundle 中获取post 和user数据
	 * 
	 * @return boolean 如果post 和user 都不为空那么返回true
	 */
	private void getData() {
		final Bundle bundle = getArguments();
		if (bundle == null) {
			return;
		}
		String tag = bundle.getString(GlobalConstants.TAGS.TOPIC_DETAIL_TAG);
		if (GlobalConstants.TAGS.TOPIC_DETAIL_TAG.equals(tag)) {
			mContentEdit.setFocusableInTouchMode(false);
			mPost = (Post) bundle.getSerializable(GlobalConstants.TAGS.POST_TAG);
			mFeed2API.singleFeed(mPost.getPostId(), new Callback<ApiResponse<FeedGetData>>() {

				@Override
				public void failure(RetrofitError arg0) {
				}

				@Override
				public void success(ApiResponse<FeedGetData> apiResponse, Response arg1) {
					if (apiResponse != null && apiResponse.isOk()) {
						if (apiResponse.getData().getList().get(0) != null) {
							if(!isVisible()){
								return;
							}
							mPost = Post.Companion.fromDTO(apiResponse.getData().getList().get(0), apiResponse.getData().getUserMap(), apiResponse.getData().getImageMap(), apiResponse.getData().getVideoMap(), apiResponse.getData().getCommentCountMap(), apiResponse.getData().getLikeCountMap());
							initView(bundle);
						}
					}
				}
			});
		} else {
			mPost = (Post) bundle.getSerializable(GlobalConstants.TAGS.POST_TAG);
			isPopSoftInputKey = bundle.getBoolean(IS_POP_SOFTINPUT_KEY);
			if (isPopSoftInputKey) {
				mContentEdit.requestFocus();
			}
			initView(bundle);
		}
	}

	private void initView(final Bundle bundle) {
		mUser = (User) bundle.getSerializable(User.TAG);
		initHeadView();
		mFeedCommentAdapter = new FeedCommentAdapter(getActivity(), mComment2Service, this);
		if (isPopSoftInputKey) {
			mContentListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		}
		mContentListView.setAdapter(mFeedCommentAdapter);
		mContentListView.setOnScrollListener(this);
		mContentListView.setOnItemClickListener(this);
		mContentListView.setOnItemLongClickListener(this);
		mContentListView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent arg1) {
				if (imm.isActive()) {
					imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
				}
				return false;
			}
		});
		loadFirstPage(getActivity().getIntent().getStringExtra(GlobalConstants.TAGS.COMMENT_ID));
		if (mUser != null) {// 从通知过来，要在输入框中加上回复人的名字
			mContentEdit.setHint(getResources().getString(R.string.comment_in_reply_hint, mUser.getName()));
		}
	}

	@Override
	protected void initTypeFace() {
		FontUtil.setSemiBoldTypeFace(getActivity(), mTitle);
	}

	/**
	 * 初始化listview 头部布局中的post视图
	 * 
	 *  listview
	 * @return void
	 */
	private void initHeadView() {
		View headView = getActivity().getLayoutInflater().inflate(R.layout.item_feed_comment_head, mContentListView, false);
		findHeaderViewById(headView);
		mContentListView.addHeaderView(headView);
	}

	/***
	 * 实例化 listview 头布局中的控件
	 * 
	 *  header
	 * @return void
	 */
	private void findHeaderViewById(View header) {
		mPrivacyCommentSignLayout = (TextView) header.findViewById(R.id.privacy_comment_sign_view);
		FrameLayout headerFeedRoot = (FrameLayout) header.findViewById(R.id.header_feed_root);
		userListLayout = (RelativeLayout) header.findViewById(R.id.user_list);
		userListLayout.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (userListLayout.getChildCount() > 2) {
						openUserList();
					} else {
						openSomeoneProfile((User) userListLayout.getChildAt(1).getTag());
					}
					return true;
				}
				v.performClick();
				return false;
			}
		});// 跳转赞列表
		RelativeLayout headerRoot = (RelativeLayout) header.findViewById(R.id.header_root);
		headerRoot.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				v.performClick();
				loadEarlyPage(false);
				return false;
			}
		});
		loadRootView = (RelativeLayout) header.findViewById(R.id.title_wrap_rl);
		mProgressBar = (ProgressBar) header.findViewById(R.id.item_feed_comment_pb);

		// 跳转到隐私设定说明网页
		// PS:该功能暂时未启用
		mPrivacyLinkView = (TextView) header.findViewById(R.id.privacy_link);
		String str = getString(R.string.add_blacklist);
		SpannableStringBuilder ssb = new SpannableStringBuilder(str);
		ssb.setSpan(new URLSpan("https://www.baidu.com"), 0, str.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		mPrivacyLinkView.setText(ssb);
		mPrivacyLinkView.setMovementMethod(LinkMovementMethod.getInstance());

		initFeedHolder(headerFeedRoot);
		loadPraiseList(userListLayout);
	}

	/***
	 * 加载赞过这个post 的用户的信息并根据屏幕和将要显示的视图大小计算应该显示的用户数量和视图参数
	 * 
	 *  userListLayout
	 *            装载这些用户信息视图的父容器
	 * @return void
	 */
	private void loadPraiseList(final ViewGroup userListLayout) {

		// 创建 视图最右端的 “xxx赞>”视图，并测量出它的宽度
		int padding = UiUtils.dip2px(getActivity(), 3);
		TextView praiseCount = new TextView(mcontext);
		Drawable arrowDrawble = getResources().getDrawable(R.drawable.gray_arrow);
		arrowDrawble.setBounds(0, 0, arrowDrawble.getMinimumWidth(), arrowDrawble.getMinimumHeight());
		praiseCount.setCompoundDrawables(null, null, arrowDrawble, null);
		praiseCount.setCompoundDrawablePadding(padding);
		praiseCount.setGravity(Gravity.CENTER);
		FontUtil.setSemiBoldTypeFace(context, praiseCount);
		praiseCount.setPadding(padding, 0, padding, 0);
		if (mPost.getPraiseCount() > 0) {
			praiseCount.setText(getString(R.string.count_praise, mPost.getPraiseCount()));
		}
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		praiseCount.setLayoutParams(params);
		((RelativeLayout) userListLayout).addView(praiseCount);
		praiseCount.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
		// ------------------------------------

		int arrowWidth = praiseCount.getMeasuredWidth();// 赞数 视图的宽度
		final int radiu = UiUtils.dip2px(getActivity(), 34);
		final int margin = UiUtils.dip2px(getActivity(), 3);
		int rootPadding = UiUtils.dip2px(mcontext, 9);
		// 屏幕宽度减去一个头像所占的大小（为了给布局末尾的箭头视图留出位置）,除以每个头像的大小得出头像的最大个数
		final int count = (UiUtils.getScreenWidth(getActivity().getApplicationContext()) - arrowWidth - rootPadding * 2) / (radiu + margin * 2);
		feedService.getPraiseList(null, count, mPost.getPostId(), new ApiCallback<List<User>>() {

			@Override
			public void success(List<User> data) {
				XL.i(TAG, "init user list success");
				if (isVisible()) {
					initUserList(userListLayout, data, radiu, margin);
				}
			}

			@Override
			public void fail(ApiErrorCode code, Exception exception) {
				XL.i(TAG, "init user list error");
			}
		});
	}

	/***
	 * 加载赞过这个post 的用户的信息并渲染视图，循环添加到父容器中
	 * 
	 *  parent
	 *            装载这些用户信息视图的父容器
	 *  users
	 *            用户的数据
	 *  radiu
	 *            圆形头像的半径
	 *  margin
	 *            圆形头像的margin值
	 * @return void
	 */
	private void initUserList(ViewGroup parent, List<User> user2s, int radiu, int margin) {
		if (user2s == null || user2s.size() == 0 || parent == null) {
			parent.setVisibility(View.GONE);
			return;
		}

		for (int i = 0; i < user2s.size(); i++) {
			LayoutParams params = new LayoutParams(radiu, radiu);
			params.setMargins(margin, margin, margin, margin);
			params.addRule(RelativeLayout.CENTER_VERTICAL);
			if (i != 0) {
				params.addRule(RelativeLayout.RIGHT_OF, i);
			} else {
				params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			}
			CircleImageView circleImage = new CircleImageView(mcontext);
			String url = user2s.get(i).getAvatarImage().getUrlSquare(radiu);
			picasso.load(url).into(circleImage);
			circleImage.setTag(user2s.get(i));// 保存这个头像的用户信息
			XL.i("user_type", "user--:" + user2s.get(i).getClass().getName());
			circleImage.setLayoutParams(params);
			circleImage.setId(i + 1);
			parent.addView(circleImage);
		}
	}

	/***
	 * listview 头部布局中要显示当前的post视图，这个方法用来初始化这个post视图的holder
	 * 
	 *  parent
	 *            post 视图的父容器
	 * @return void
	 */
	private void initFeedHolder(ViewGroup parent) {
		if (mPost != null && parent != null) {
			XL.i(TAG, "init feed holder");
			feedHolder = new FeedHolder(this, getActivity().getLayoutInflater(), parent);
			View contentView = feedHolder.getView();
			BaseFeedHolder contentHolder = null;
			if (mPost.getType() == FeedType.ImagePost) {
				contentHolder = new ImageFeedHolder(getActivity().getLayoutInflater(), (ViewGroup) contentView, this);
			} else if (mPost.getType() == FeedType.VideoPost) {
				contentHolder = new VideoFeedHolder(getActivity().getLayoutInflater(), parent, this);
			}
			feedHolder.setHolder2FragCallback(this);
			feedHolder.addChildHolder(contentHolder);
			feedHolder.resetViewData(mPost);
			feedHolder.hideCommentListView();
			parent.addView(feedHolder.getView());
		}
	}

	/**
	 * 判断是否显示“加载更在数据”视图
	 * 
	 *  isLoading
	 * @return void
	 */
	private void loadingHeader(boolean hasNext) {
		if (!hasNext) {
			loadRootView.setVisibility(View.GONE);
			return;
		}

		if (mProgressBar.getVisibility() == View.VISIBLE) {
			mProgressBar.setVisibility(View.GONE);
		} else {
			mProgressBar.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 滚到选中的评论，只在第一次加载数据的时候执行
	 * 
	 *  commentid
	 * @return void
	 */
	private void smoothToTheComment(String commentid) {
		if (TextUtils.isEmpty(commentid)) {
			checkItemStartPlay();// 不滚动 就播放视频。如果是视频的话
			return;
		}
		for (int i = 0; i < mFeedCommentAdapter.getCount(); i++) {
			PostComment postComment = (PostComment) mFeedCommentAdapter.getItem(i);
			if (commentid.equals(postComment.getId())) {
				mContentListView.smoothScrollToPosition(i + 1);// listview
																// 头布局占一个位置
				mContentEdit.setHint(getString(R.string.comment_in_reply_hint, postComment.getUser().getName()));
				mContentEdit.setTag(postComment);
				break;
			}
		}
	}

	private void loadFirstPage(String commentid) {
		mFeedCommentAdapter.loadContextByCursor(commentid, pageSize, mPost.getPostId(), new LoadCallback() {

			@Override
			public void success(boolean hasPrev, boolean hasNext) {
				if (!isVisible()) {
					return;
				}
				loadingHeader(hasPrev);
				mContentListView.postDelayed(new Runnable() {

					@Override
					public void run() {
						if (isVisible()) {
							smoothToTheComment(getActivity().getIntent().getStringExtra(GlobalConstants.TAGS.COMMENT_ID));
						}
					}
				}, 500);
			}

			@Override
			public void fail(ApiErrorCode code, Exception exception) {
			}

			@Override
			public void dataState(int size, boolean isnull) {

			}
		});
	}

	/**
	 * @Title: loadLatePage
	 * @Description: 加载以前的数据
	 */
	private void loadEarlyPage(boolean reset) {
		XL.i("COMMENT_TEST", "loadNextPage");
		if (reset) {
			mFeedCommentAdapter.resetState();
		}
		loadingHeader(true);
		// 这里不需要判断
		mFeedCommentAdapter.loadEarlyPage(pageSize, mPost.getPostId(), new LoadCallback() {

			@Override
			public void success(boolean hasPrev, boolean hasNext) {
				loadingHeader(hasPrev);
			}

			public void fail(ApiErrorCode error, Exception exception) {
				ToastUtil.shortToast(getActivity(), R.string.Unkown_Error);
			}

			@Override
			public void dataState(int size, boolean isnull) {
			}
		});
	}

	/**
	 * @Title: loadLatePage
	 * @Description: 加载新数据
	 */
	private void loadLatePage() {
		XL.i("COMMENT_TEST", "loadLatePage");
		mFeedCommentAdapter.loadLatePage(pageSize, mPost.getPostId(), new LoadCallback() {

			@Override
			public void success(boolean hasPrev, boolean hasNext) {
				if (isVisible()) {
					return;
				}
			}

			public void fail(ApiErrorCode error, Exception exception) {
			}

			@Override
			public void dataState(int size, boolean isnull) {

			}
		});
	}

	@OnClick({ R.id.feed_comment_back_iv, R.id.comments_send_tv, R.id.comments_content_et })
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.feed_comment_back_iv:
			// setResult();
			getActivity().finish();
			getActivity().overridePendingTransition(R.anim.stop, R.anim.right_out);
			break;
		case R.id.comments_send_tv:
			sendComment(mContentEdit.getText().toString(), (PostComment) mContentEdit.getTag());
			break;
		case R.id.comments_content_et:
			mContentListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
			mContentEdit.setFocusableInTouchMode(true);
			mContentEdit.setFocusable(true);
			mContentEdit.requestFocus();
			UiUtils.showKeyBoard(getActivity(), mContentEdit, 0);
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() != MotionEvent.ACTION_UP) {
			return false;
		}
		switch (v.getId()) {
		case R.id.header_feed_root:
			openUserList();
			return true;

		default:
			break;
		}
		v.performClick();
		return false;
	}

	/***
	 * 开启赞列表
	 * 
	 * @return void
	 */
	public void openUserList() {
		Bundle bundle = new Bundle();
		bundle.putInt("listtype", SwipeMenuListFragment.LISTTYPE_PRAISE);
		bundle.putInt("feedLikeCount", mPost.getPraiseCount());
		bundle.putString("postId", mPost.getPostId());
		((BaseFragAct) getActivity()).startFragment(SwipeMenuListFragment.class, bundle, true);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		XL.i(TAG, "position:" + position);

		if (mContentListView.getHeaderViewsCount() != 0) {
			position -= 1;// 要减去header view所占用的position
		}
		PostComment postComment = (PostComment) mFeedCommentAdapter.getItem(position);// 要减去header
																						// view所占用的position
		if (postComment == null) {
			return;
		}
		// 保存当前选中的评论信息，在发送评论的时候取出，省一个全局的变量
		mContentEdit.setTag(postComment);
		if (!postComment.getUser().getMe()) {
			mContentEdit.setFocusable(true);
			mContentEdit.requestFocus();
			UiUtils.showKeyBoard(getActivity(), mContentEdit, 0);

			if (postComment.isWhisper()) {
				mContentEdit.setCompoundDrawables(wisperCommentDrawable, null, null, null);
			} else {
				mContentEdit.setCompoundDrawables(null, null, null, null);
				mContentEdit.refreshDrawableState();
			}
			mContentEdit.setText("");
			mContentEdit.setHint(getString(R.string.report_hint) + postComment.getUser().getName() + ":");

			final int fp = position + 1;
			final View v = view;
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					int height = getActivity().getWindow().findViewById(Window.ID_ANDROID_CONTENT).getHeight() - UiUtils.dip2px(getActivity(), (48 + 45)) - v.getHeight();
					mContentListView.setSelectionFromTop(fp, height);
				}
			}, 350);
		} else {
			mContentEdit.setCompoundDrawables(null, null, null, null);
			mContentEdit.refreshDrawableState();
			mContentEdit.setText("");
			mContentEdit.setHint(R.string.comment);
			UiUtils.hideKeyBoard(getActivity());
		}
	}

	private Callback<ApiResponse<Comment2GetData>> sendCommentCallback = new Callback<ApiResponse<Comment2GetData>>() {

		@Override
		public void failure(RetrofitError arg0) {
			ToastUtil.longToast(getActivity(), R.string.network_error);
		}

		@Override
		public void success(ApiResponse<Comment2GetData> apiResponse, Response response) {
			changeViewInSendTime(false);
			if (!isVisible()) {
				return;
			}
			if (apiResponse != null) {
				if (apiResponse.isOk()) {
					mContentEdit.setText("");// 重置
					mContentEdit.setHint(R.string.leave_hint);
					mContentEdit.setTag(null);
					UiUtils.hideKeyBoard(getActivity());
					List<PostComment> postcommentlist = null;
					postcommentlist = PostComment.Companion.fromCommentDTOList(apiResponse.getData().getList());
					mFeedCommentAdapter.appendListItem(Direct.Late, postcommentlist);
					mFeedCommentAdapter.notifyDataSetChanged();
					mContentListView.smoothScrollToPosition(mFeedCommentAdapter.getCount());// 因为listview
					// 有头布局，所以这个地方不用-1
				} else if (apiResponse.getData().getError() == IResultDataErrorCode.ERROR_INVALID_COMMENT) {
					ToastUtil.shortToast(getActivity(), getString(R.string.comment_has_illegalword));
				} else if (apiResponse.getData().getError() == IResultDataErrorCode.ERROR_BLOCK_BY_OTHER) {
					ToastUtil.shortToastCenter(getActivity(), getString(R.string.otherside_black_current_user));
				} else {
					ToastUtil.shortToastCenter(getActivity(), getString(R.string.Unkown_Error));
				}
			} else {
				ToastUtil.shortToastCenter(getActivity(), getString(R.string.Unkown_Error));
			}

		}
	};

	/**
	 * @Title: sendComment
	 * @Description: 发送评论
	 *   comment 评论内容
	 * @return void 返回类型
	 * @throws
	 */
	public void sendComment(String comment, PostComment postComment) {
		if (TextUtils.isEmpty(comment.trim()) || mPost == null) {
			return;
		}

		String replyCommentId = null;
		if (postComment != null) {
			if (!postComment.getUser().getMe()) {
				replyCommentId = postComment.getId();
			}
		}
		changeViewInSendTime(true);
		mCommentService.postCommentV2(mPost.getPostId(), replyCommentId, comment, sendCommentCallback);
	}

	/***
	 * 正在發送的時候，send 禁用send 按鈕
	 * 
	 * @return void
	 */
	public void changeViewInSendTime(boolean isSending) {
		if (isSending) {
			mSendTv.setBackgroundResource(R.drawable.chat_send_btn_down);
		} else {
			mSendTv.setBackgroundResource(R.drawable.chat_send_btn_selector);
		}
		mSendTv.setClickable(!isSending);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		if (mContentListView.getHeaderViewsCount() != 0) {
			position -= 1;// 要减去header view所占用的position
		}
		PostComment postComment = (PostComment) mFeedCommentAdapter.getItem(position);
		if (postComment != null) {
			new ListItemDialog(getActivity(), (ViewGroup) view).showListItemPopup(this, null, ListItemType.DELETE);
			removePosition = position;
			removeCommentId = postComment.getId();
		}
		return false;
	}

	private void deleteComment(final String commentId) {
		mCommentService.deleteComment(mPost != null ? mPost.getPostId() : "", commentId, new Callback<ApiResponse<ResultData>>() {

			@Override
			public void success(ApiResponse<ResultData> apiResponse, Response arg1) {
				if (apiResponse != null && apiResponse.isOk()) {
					mFeedCommentAdapter.removeItem(removePosition);
					return;
				}
			}

			@Override
			public void failure(RetrofitError arg0) {
				// mHandler.sendEmptyMessage(GlobalConstants.AppConstact.SERVER_ERROR);
			}
		});
	}

	@Override
	public void itemCallback(int listItemType) {
		switch (listItemType) {
		case ListItemType.DELETE:
			deleteComment(removeCommentId);
			break;

		default:
			break;
		}
	}

	/**
	 * 进入这个人的主页
	 * 
	 *  user
	 *            被开启主页的用户
	 */
	@Override
	public void openSomeoneProfile(User user2) {
		Bundle bundle = new Bundle();
		bundle.putSerializable(User.TAG, user2);
		// bundle.putSerializable(User.TAG, DockingBeanUtils.transUser(user));
		((BaseFragAct) getActivity()).startFragment(Profile2Fragment.class, bundle, true);
	}

	@Override
	public void onPause() {
		super.onPause();
		Intent intent = new Intent();
		intent.putExtra(FeedHolder.COMMENT_COUNT_DATA_KEY, mFeedCommentAdapter == null ? null : mFeedCommentAdapter.getCount());
		getActivity().setResult(Activity.RESULT_OK, intent);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (scrollState == SCROLL_STATE_IDLE) {
			checkItemStartPlay();
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if (firstVisibleItem + visibleItemCount + 5 == totalItemCount) {
			loadLatePage();
		}

		checkItemStopPlay();
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
		if (feedHolder != null) {
			BaseFeedHolder holder = feedHolder.getContentHolder();
			if (holder != null && holder instanceof VideoFeedHolder) {
				videoHolder = (VideoFeedHolder) holder;
				if (videoHolder.isPlayer()) {
					videoHolder.startMediaPlayer();
				}
			}
		}
	}

	private void checkItemStopPlay() {
		if (videoHolder != null && !videoHolder.isPlayer()) {
			videoHolder.stopPlayer();
		}
	}

	/***
	 * 播放视频
	 * 
	 * @return void
	 */
	public synchronized void startVideo() {
		new Handler().postDelayed(new Runnable() {// 如果是视频 ，那么直接播放

			@Override
			public void run() {
				if (!isVisible() || feedHolder == null || !(feedHolder.getContentHolder() instanceof VideoFeedHolder)) {
					return;
				}
				videoHolder = ((VideoFeedHolder) feedHolder.getContentHolder());
				videoHolder.startMediaPlayer();
			}
		}, 500);
	}

	@Override
	public int getHolderType() {
		return FeedHolder.COMMENT_HOLDER;
	}

	@Override
	public void praiseCallback() {
		if (userListLayout != null) {
			userListLayout.setVisibility(View.VISIBLE);
			userListLayout.removeAllViews();
			loadPraiseList(userListLayout);
		}
	}

	@Override
	public void deleteCallback() {
		Intent intent = new Intent();
		intent.putExtras(getArguments());
		getActivity().setResult(Profile2Fragment.OPEN_SINGLETO_FEED_REQUESTCODE, intent);
		getActivity().finish();
	}

	/***
	 * 在留言界面点击留言按钮，呼起监盘
	 * 
	 * @return
	 */
	@Override
	public void commentClickCallback() {
		mContentEdit.requestFocus();
		UiUtils.showKeyBoard(getActivity(), mContentEdit, 0);
	}

	private Dialog wisperDialog;
	private static final String WISPER_GUIDE_KEY = "WISPER_GUIDE_KEY";

	@Override
	public void showmPrivacyCommentSign() {
		if (ContextConfig.getInstance().getBooleanWithFilename(WISPER_GUIDE_KEY)) {
			return;
		}
		ContextConfig.getInstance().putBooleanWithFilename(WISPER_GUIDE_KEY, true);
		if (wisperDialog != null) {
			return;
		}
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_for_wisper_guide, (ViewGroup) rootView, false);
		Button confirm = (Button) view.findViewById(R.id.cancel_btn);
		fontUtil.changeViewFont(((TextView) view.findViewById(R.id.title)), Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
		FontUtil.setRegulartypeFace(context, ((TextView) view.findViewById(R.id.content)));
		FontUtil.setRegulartypeFace(context, confirm);
		confirm.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				wisperDialog.cancel();
			}
		});
		wisperDialog = new AlertDialog.Builder(getActivity())//
		.setView(view)//
		.create();
		wisperDialog.show();

	}

}
