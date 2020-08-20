package com.wealoha.social.fragment;

import javax.inject.Inject;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import com.wealoha.social.BaseFragAct;
import com.wealoha.social.R;
import com.wealoha.social.activity.PicSendActivity;
import com.wealoha.social.activity.PicSendActivity.PicSendActivityBundleKey;
import com.wealoha.social.adapter.feed.BaseFeedHolder;
import com.wealoha.social.adapter.feed.BaseFeedHolder.Holder2FragCallback;
import com.wealoha.social.adapter.feed.Feed2Adapter;
import com.wealoha.social.adapter.feed.Feed2Adapter.Adapter2FragmentCallback;
import com.wealoha.social.adapter.feed.FeedHolder;
import com.wealoha.social.adapter.feed.VideoFeedHolder;
import com.wealoha.social.api.common.ApiErrorCode;
import com.wealoha.social.api.common.BaseListApiService.NoResultCallback;
import com.wealoha.social.api.SingletonFeedService;
import com.wealoha.social.beans.Post;
import com.wealoha.social.utils.XL;
import com.wealoha.social.widget.BaseListApiAdapter.LoadCallback;

public class SingletonFeedFragment extends BaseFragment implements Holder2FragCallback, OnClickListener,
		Adapter2FragmentCallback {

	@Inject
	SingletonFeedService feedService;
	@InjectView(R.id.list)
	ListView postList;
	@InjectView(R.id.tags_method_container)
	LinearLayout tagsMethodLayout;
	@InjectView(R.id.remove_tag)
	TextView mRemoveTagTv;
	@InjectView(R.id.take_to_mine)
	TextView mTakeToMineTv;
	@InjectView(R.id.config_details_back)
	ImageView mBackTv;

	private View rootView;
	private Post mPost;
	private Feed2Adapter feedAdapter;
	public final static String TAG = SingletonFeedFragment.class.getName();
	public static final int OPEN_PIC_SEND_REQUESTCODE = 1002;
	private int feedType;
	private Dialog alertDialog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.frag_singleton_feed, container, false);
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		ButterKnife.inject(this, rootView);
		if (getPostData()) {
			initMenuViewAnim();
			feedAdapter = new Feed2Adapter(this, feedService);
			feedAdapter.setAdt2FragCallback(this);
			feedAdapter.setHolder2FragCallback(this);// 设置holder 的回调
			postList.setAdapter(feedAdapter);
			loadNextPage(false);
		}
	}

	/***
	 * 初始化移除和分享按钮的出现和隐藏动画
	 * 
	 * @return void
	 */
	private void initMenuViewAnim() {
		LayoutTransition transition = new LayoutTransition();
		transition.setDuration(300);
		transition.setAnimator(LayoutTransition.APPEARING, transition.getAnimator(LayoutTransition.APPEARING));
		transition.setAnimator(LayoutTransition.DISAPPEARING, transition.getAnimator(LayoutTransition.DISAPPEARING));
		tagsMethodLayout.setLayoutTransition(transition);
	}

	protected void loadNextPage(boolean resetdata) {
		if (resetdata) {
			feedAdapter.resetState();
		}
		feedAdapter.loadEarlyPage(1, mPost.getPostId(), new LoadCallback() {

			@Override
			public void success(boolean hasEarly, boolean hasLate) {
				if (!isVisible()) {
					return;
				}
				if (feedAdapter.getListData() != null && feedAdapter.getListData().size() > 0) {
					mPost = feedAdapter.getListData().get(0);
				}
				initView();
			}

			@Override
			public void fail(ApiErrorCode code, Exception exception) {
				XL.i(TAG, "load fail");
			}

			@Override
			public void dataState(int size, boolean isnull) {
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		startVideo();
	}

	/***
	 * 根据feed 类型来渲染不同的视图：是否包含分享功能
	 * 
	 * @return void
	 */
	public void initView() {
		if (feedType == FeedHolder.TAGS_HOLDER) {
			tagsMethodLayout.setVisibility(View.VISIBLE);
			changeViewByTags(mPost.isTagMe());
		} else {
			tagsMethodLayout.setVisibility(View.GONE);
		}
		startVideo();
	}

	private VideoFeedHolder videoHolder;

	/***
	 * 播放视频
	 * 
	 * @return void
	 */
	public synchronized void startVideo() {
		postList.postDelayed(new Runnable() {// 如果是视频 ，那么直接播放

			@Override
			public void run() {
				if (!isVisible() || postList == null) {
					return;
				}
				View view = (View) postList.getChildAt(0);
				if (view != null) {
					BaseFeedHolder bfh = (BaseFeedHolder) view.getTag();
					if (bfh != null && bfh.getContentHolder() instanceof VideoFeedHolder) {
						videoHolder = (VideoFeedHolder) bfh.getContentHolder();
						if (videoHolder != null) {
							((VideoFeedHolder) bfh.getContentHolder()).startMediaPlayer();
						}
					}
				}
			}
		}, 200);
	}

	@Override
	public void onPause() {
		super.onPause();
		if (videoHolder != null) {
			videoHolder.stopPlayer();// 停掉视频
		}
	}

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
			mRemoveTagTv.setBackgroundColor(getResources().getColor(R.color.light_gray));
			mRemoveTagTv.setClickable(false);
		}
	}

	/***
	 * 從開啟這個frag的組件那裡得到post數據
	 * 
	 * @return void
	 */
	public boolean getPostData() {
		feedType = getArguments().getInt(SingletonFeedFragment.TAG, 2);
		mPost = (Post) getArguments().get(Post.TAG);
		if (mPost != null) {
			return true;
		}
		return false;
	}

	@Override
	public int getHolderType() {
		return feedType;
	}

	@OnClick({ R.id.remove_tag, R.id.take_to_mine, R.id.config_details_back })
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.remove_tag:
			// ((FeedHolder) postList.getChildAt(0).getTag()).removeMyTag();
			openAlertDialog((BaseFragAct) getActivity());
			break;
		case R.id.take_to_mine:
			shareFeed();
			break;
		case R.id.config_details_back:
			getActivity().finish();
			break;
		default:
			break;
		}
	}

	/**
	 * @Title: copyTheFeedToMe
	 * @Description: 转发
	 */
	private void shareFeed() {
		Post post = (Post) feedAdapter.getItem(0);
		if (post == null) {
			return;
		}
		Intent intent = new Intent(getActivity(), PicSendActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString(PicSendActivityBundleKey.PIC_SEND_TYPE, PicSendActivity.SHARE_FEED);
		bundle.putSerializable(Post.TAG, post);
		intent.putExtras(bundle);
		getActivity().startActivityForResult(intent, OPEN_PIC_SEND_REQUESTCODE);
	}

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
				removeCurrentUserTag();
			}
		});

		alertDialog = new AlertDialog.Builder(baseAct)//
		.setView(view)//
		.setCancelable(false) //
		.create();
		alertDialog.show();

	}

	private void removeCurrentUserTag() {
		final Post post = (Post) feedAdapter.getItem(0);
		if (post != null) {
			feedService.removeTag(post.getPostId(), contextUtil.getCurrentUser().getId(), new NoResultCallback() {

				@Override
				public void success() {
					loadNextPage(true);
					mPost.removeTagMe();
					changeViewByTags(false);
				}

				@Override
				public void fail(ApiErrorCode code, Exception exception) {

				}
			});
		}
	}

	/***
	 * 单例feed 也删除post后，要关闭当前feed页，并刷新profile
	 * 
	 * @return
	 */
	@Override
	public void deletePostCallback() {
		if (isVisible()) {
			getActivity().setResult(Activity.RESULT_OK);
			getActivity().finish();
		}
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
}
