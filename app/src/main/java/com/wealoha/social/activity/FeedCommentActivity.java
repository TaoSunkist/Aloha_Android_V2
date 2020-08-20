package com.wealoha.social.activity;

import java.util.List;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;

import com.wealoha.social.BaseFragAct;
import com.wealoha.social.R;
import com.wealoha.social.adapter.FeedCommentAdapter;
import com.wealoha.social.adapter.FeedCommentAdapter.FeedCommentAdapterCallback;
import com.wealoha.social.adapter.feed.BaseFeedHolder.Holder2FragCallback;
import com.wealoha.social.adapter.feed.FeedHolder;
import com.wealoha.social.api.comment.Comment2GetData;
import com.wealoha.social.api.comment.bean.PostComment;
import com.wealoha.social.api.comment.service.Comment2Service;
import com.wealoha.social.api.common.ApiErrorCode;
import com.wealoha.social.api.common.Direct;
import com.wealoha.social.beans.Post;
import com.wealoha.social.beans.IResultDataErrorCode;
import com.wealoha.social.beans.Result;
import com.wealoha.social.beans.ResultData;
import com.wealoha.social.beans.User;
import com.wealoha.social.api.CommentService;
import com.wealoha.social.commons.GlobalConstants;
import com.wealoha.social.fragment.Profile2Fragment;
import com.wealoha.social.utils.DockingBeanUtils;
import com.wealoha.social.utils.FontUtil.Font;
import com.wealoha.social.utils.ToastUtil;
import com.wealoha.social.utils.UiUtils;
import com.wealoha.social.utils.XL;
import com.wealoha.social.view.custom.dialog.ListItemDialog;
import com.wealoha.social.view.custom.dialog.ListItemDialog.ListItemCallback;
import com.wealoha.social.view.custom.dialog.ListItemDialog.ListItemType;
import com.wealoha.social.widget.BaseListApiAdapter.LoadCallback;
import com.wealoha.social.widget.ScrollToLoadMoreListener;

public class FeedCommentActivity extends BaseFragAct implements OnClickListener, OnItemClickListener,
		OnItemLongClickListener, ListItemCallback, FeedCommentAdapterCallback, Holder2FragCallback {

	@Inject
	CommentService mCommentService;
	@Inject
	Comment2Service mComment2Service;
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
	RelativeLayout mHeaderRoot;
	TextView loadMoreComment;
	ProgressBar mProgressBar;
	private FeedCommentAdapter mFeedCommentAdapter;
	private final int pageSize = 15;
	private Post mPost;
	private View headView;
	private int removePosition;
	private String removeCommentId;
	private com.wealoha.social.api.user.bean.User2 mUser2;
	private Handler mHandler;
	private static final String TAG = FeedCommentActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feed_comment);
		if (getData()) {// 获取bundle中的值
			initHeadView();
		} else {
			return;// 没有数据无法渲染视图
		}

		mFeedCommentAdapter = new FeedCommentAdapter(this, mComment2Service, this);
		mContentListView.setAdapter(mFeedCommentAdapter);
		mContentListView.setOnScrollListener(new ScrollToLoadMoreListener(4, new ScrollToLoadMoreListener.Callback() {

			@Override
			public void loadMore() {
				loadLatePage();
			}
		}));
		mContentListView.setOnItemClickListener(this);
		mContentListView.setOnItemLongClickListener(this);
		loadFirstPage(getIntent().getStringExtra(GlobalConstants.TAGS.COMMENT_ID));

		if (mUser2 != null) {
			mContentEdit.setHint(getResources().getString(R.string.comment_in_reply_hint, mUser2.getName()));
		}
	}

	/***
	 * 从bundle 中获取post 和user数据
	 * 
	 * @return boolean 如果post 和user 都不为空那么返回true
	 */
	private boolean getData() {
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			mPost = (Post) bundle.getSerializable(GlobalConstants.TAGS.POST_TAG);
			mUser2 = (com.wealoha.social.api.user.bean.User2) bundle.getSerializable(com.wealoha.social.api.user.bean.User2.TAG);
			return true;
		}
		return false;
	}

	@Override
	protected void initTypeFace() {
		fontUtil.changeFonts(mTitle, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
	}

	/**
	 * 加载 “加载之前的留言” 视图
	 * 
	 * @param listview
	 * @return void
	 */
	private void initHeadView() {
		headView = getLayoutInflater().inflate(R.layout.item_feed_comment_head, mContentListView, false);

		mHeaderRoot = (RelativeLayout) headView.findViewById(R.id.header_root);
		mHeaderRoot.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				v.performClick();
				loadEarlyPage(false);
				return false;
			}
		});
		loadMoreComment = (TextView) headView.findViewById(R.id.item_feed_comment_head_load_tv);
		mProgressBar = (ProgressBar) headView.findViewById(R.id.item_feed_comment_pb);
		headView.setVisibility(View.INVISIBLE);
		mContentListView.addHeaderView(headView);
	}

	/**
	 * 是否显示正在加载
	 * 
	 * @param isLoading
	 * @return void
	 */
	private void loadingHeader(boolean isLoading) {
		if (isLoading) {
			mProgressBar.setVisibility(View.VISIBLE);
		} else {
			mProgressBar.setVisibility(View.GONE);
		}
	}

	/**
	 * 移除加载更早的视图
	 * 
	 * @return void
	 */
	private void removeHeadView() {
		mContentListView.removeHeaderView(headView);
	}

	/**
	 * 滚到选中的评论，只在第一次加载数据的时候执行
	 * 
	 * @param commentid
	 * @return void
	 */
	private void smoothToTheComment(String commentid) {
		if (TextUtils.isEmpty(commentid)) {
			return;
		}
		for (int i = 0; i < mFeedCommentAdapter.getCount(); i++) {
			PostComment postComment = (PostComment) mFeedCommentAdapter.getItem(i);
			if (commentid.equals(postComment.getId())) {
				mContentListView.smoothScrollToPosition(i);
				mContentEdit.setHint(getString(R.string.comment_in_reply_hint, postComment.getUser2().getName()));
				mContentEdit.setTag(postComment);
				break;
			}
		}
	}

	private void loadFirstPage(String commentid) {
		mFeedCommentAdapter.loadContextByCursor(commentid, pageSize, mPost.getPostId(), new LoadCallback() {

			@Override
			public void success(boolean hasPrev, boolean hasNext) {
				if (isFinishing()) {
					return;
				}

				if (!hasPrev) {
					removeHeadView();
				} else if (headView != null) {
					headView.setVisibility(View.VISIBLE);
				}
				mContentListView.postDelayed(new Runnable() {

					@Override
					public void run() {
						smoothToTheComment(getIntent().getStringExtra(GlobalConstants.TAGS.COMMENT_ID));
					}
				}, 500);
			}

			@Override
			public void fail(ApiErrorCode code, Exception exception) {
				ToastUtil.shortToast(FeedCommentActivity.this, R.string.Unkown_Error);
			}

			@Override
			public void dataState(int size, boolean isnull) {
				// TODO Auto-generated method stub

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
				if (!hasPrev) {
					removeHeadView();
				} else {
					loadingHeader(false);
				}
			}

			public void fail(ApiErrorCode error, Exception exception) {
				ToastUtil.shortToast(FeedCommentActivity.this, R.string.Unkown_Error);
			}

			@Override
			public void dataState(int size, boolean isnull) {
				// TODO Auto-generated method stub

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
				if (isFinishing()) {
					return;
				}
				// if (!hasPrev || hasNext) {
				// }
			}

			public void fail(ApiErrorCode error, Exception exception) {
			}

			@Override
			public void dataState(int size, boolean isnull) {
				// TODO Auto-generated method stub

			}
		});
	}

	@OnClick({ R.id.feed_comment_back_iv, R.id.comments_send_tv })
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.feed_comment_back_iv:
			overridePendingTransition(R.anim.right_out, R.anim.stop);

			Intent intent = new Intent();
			intent.putExtra(FeedHolder.COMMENT_COUNT_DATA_KEY, mFeedCommentAdapter.getCount());
			setResult(RESULT_OK, intent);
			finish();
			break;
		case R.id.comments_send_tv:
			sendComment(mContentEdit.getText().toString(), (PostComment) mContentEdit.getTag());
			break;
		default:
			break;
		}
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent();
		intent.putExtra(FeedHolder.COMMENT_COUNT_DATA_KEY, mFeedCommentAdapter.getCount());
		setResult(RESULT_OK, intent);
		finish();
		super.onBackPressed();
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
		XL.i(TAG, "user:" + postComment.getUser2());
		XL.i(TAG, "reply user:" + postComment.getReplyUser2());
		// 保存当前选中的评论信息，在发送评论的时候取出，省一个全局的变量
		mContentEdit.setTag(postComment);
		if (!postComment.getUser2().isMe()) {
			mContentEdit.setFocusable(true);
			mContentEdit.requestFocus();
			UiUtils.showKeyBoard(this, mContentEdit, 0);
			mContentEdit.setText("");
			mContentEdit.setHint(mResources.getString(R.string.report_hint) + postComment.getUser2().getName() + ":");

			final int fp = position;
			final View v = view;
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					int height = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getHeight() - UiUtils.dip2px(FeedCommentActivity.this, (48 + 45)) - v.getHeight();
					mContentListView.setSelectionFromTop(fp, height);
				}
			}, 300);
		} else {
			mContentEdit.setText("");
			mContentEdit.setHint(R.string.comment);
			UiUtils.hideKeyBoard(this);
		}
	}

	/**
	 * @Title: sendComment
	 * @Description: 发送评论
	 * @param @param comment 评论内容
	 * @return void 返回类型
	 * @throws
	 */
	public void sendComment(String comment, PostComment postComment) {
		if (TextUtils.isEmpty(comment.trim()) || mPost == null) {
			return;
		}

		String replyUserId = null;
		if (postComment != null) {
			if (!postComment.getUser2().isMe()) {
				replyUserId = postComment.getUser2().getId();
			}
		}
		changeViewInSendTime(true);
		mCommentService.postComment(mPost.getPostId(), replyUserId, comment, new Callback<Result<Comment2GetData>>() {

			@Override
			public void failure(RetrofitError arg0) {
				ToastUtil.longToast(FeedCommentActivity.this, R.string.network_error);
			}

			@Override
			public void success(Result<Comment2GetData> result, Response response) {
				changeViewInSendTime(false);
				if (isFinishing()) {
					return;
				}
				if (result != null) {
					if (result.isOk()) {
						mContentEdit.setText("");// 重置
						mContentEdit.setHint(R.string.leave_hint);
						mContentEdit.setTag(null);
						UiUtils.hideKeyBoard(FeedCommentActivity.this);

						List<PostComment> postcommentlist = mComment2Service.trans(result.data);
						mFeedCommentAdapter.appendListItem(Direct.Late, postcommentlist);

						mContentListView.smoothScrollToPosition(mFeedCommentAdapter.getCount() - 1);
					} else if (result.data.error == IResultDataErrorCode.ERROR_INVALID_COMMENT) {
						ToastUtil.shortToast(FeedCommentActivity.this, getString(R.string.comment_has_illegalword));
					} else if (result.data.error == IResultDataErrorCode.ERROR_BLOCK_BY_OTHER) {
						ToastUtil.shortToastCenter(FeedCommentActivity.this, getString(R.string.otherside_black_current_user));
					} else {
						ToastUtil.shortToastCenter(FeedCommentActivity.this, getString(R.string.Unkown_Error));
					}
				} else {
					ToastUtil.shortToastCenter(FeedCommentActivity.this, getString(R.string.Unkown_Error));
				}

			}
		});
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
			new ListItemDialog(this, (ViewGroup) view).showListItemPopup(this, null, ListItemType.DELETE);
			removePosition = position;
			removeCommentId = postComment.getId();
		}
		return false;
	}

	private void deleteComment(final String commentId) {
		mCommentService.deleteComment(mPost != null ? mPost.getPostId() : "", commentId, new Callback<Result<ResultData>>() {

			@Override
			public void success(Result<ResultData> result, Response arg1) {
				if (result != null && result.isOk()) {
					mFeedCommentAdapter.removeItem(removePosition);
					return;
				}
			}

			@Override
			public void failure(RetrofitError arg0) {
				mHandler.sendEmptyMessage(GlobalConstants.AppConstact.SERVER_ERROR);
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
	 * @param user2
	 *            被开启主页的用户
	 */
	@Override
	public void openSomeoneProfile(com.wealoha.social.api.user.bean.User2 user2) {
		Bundle bundle = new Bundle();
		bundle.putSerializable(User.TAG, DockingBeanUtils.transUser(user2));
		startFragment(Profile2Fragment.class, bundle, true);
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public int getHolderType() {
		return 0;
	}

	@Override
	public void praiseCallback() {

	}

	@Override
	public void deleteCallback() {
		// TODO Auto-generated method stub

	}

	@Override
	public void commentClickCallback() {
		// TODO Auto-generated method stub

	}

	@Override
	public void showmPrivacyCommentSign() {
		// TODO Auto-generated method stub

	}

}
