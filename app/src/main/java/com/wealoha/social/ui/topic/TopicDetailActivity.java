package com.wealoha.social.ui.topic;

import javax.inject.Inject;

import android.content.Intent;
import android.os.Bundle;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import butterknife.InjectView;
import butterknife.OnClick;

import com.squareup.picasso.Picasso;
import com.wealoha.social.ActivityManager;
import com.wealoha.social.BaseFragAct;
import com.wealoha.social.ContextConfig;
import com.wealoha.social.R;
import com.wealoha.social.activity.MainAct;
import com.wealoha.social.activity.PicSendActivity;
import com.wealoha.social.activity.WebActivity;
import com.wealoha.social.beans.TopicPosts;
import com.wealoha.social.beans.HashTag;
import com.wealoha.social.beans.User;
import com.wealoha.social.commons.GlobalConstants;
import com.wealoha.social.presenters.TopicDetailPresenter;
import com.wealoha.social.utils.UiUtils;
import com.wealoha.social.view.custom.dialog.ListItemDialog;
import com.wealoha.social.view.custom.dialog.ListItemDialog.ListItemType;
import com.wealoha.social.widget.ScrollToLoadListener;
import com.wealoha.social.widget.ScrollToLoadListener.Callback;

/**
 * @author:sunkist
 * @see:
 * @since:
 * @description
 * @copyright wealoha.com
 * @Date:2015年7月21日
 */
public class TopicDetailActivity extends BaseFragAct implements ITopicDetailView {

	@InjectView(R.id.topic_detail_list_lv)
	ListView mListView;
	@InjectView(R.id.topic_detail_back_iv)
	ImageView mBack;
	@InjectView(R.id.refresh_layout)
	SwipeRefreshLayout mRefreshLayout;
	@InjectView(R.id.topic_detail_send_photo_tv)
	TextView mSendImgTop;
	TopicDetailPresenter mTopicDetailP;
	TopicDetailAdapter mTopicDetailAdapter;
	@Inject
	Picasso mPicasso;
	private ImageView mHeadImage;
	private View mHeadView;
	private View mFootView;
	@InjectView(R.id.topic_detail_title_tv)
	TextView mTopicDetailTitle;
	
	private boolean mForceBackToSessionList;

	/**
	 * 监听 {@link #mListView} 的加载更多
	 */
	private ScrollToLoadListener toloadMoreListener = new ScrollToLoadListener(3, new Callback() {


		@Override
		public void loadMore() {
			mTopicDetailP.getTopicData(hashtagId);

		}

		@Override
		public void changeTitleBar() {
		}

	});
	private ProgressBar mProgressBar;
	private TextView footText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActivityManager.push(this);
		setContentView(R.layout.activity_topic_detail);
		mRefreshLayout.setOnRefreshListener(this);// 下拉刷新
		mListView.setOnScrollListener(toloadMoreListener);
		mRefreshLayout.setColorSchemeResources(R.color.light_red);
		mTopicDetailP = new TopicDetailPresenter(this);
		Bundle bundle = getIntent().getExtras();
		tag = bundle.getString(GlobalConstants.TAGS.OPEN_HASH_TAG_TYPE);
		// 优先初始化头部和脚步UI
		initListHeader();
		initListFooter();
		showLoadingFooterView();// 正在加载
		mTopicDetailAdapter = new TopicDetailAdapter(this, this, null);
		mListView.setAdapter(mTopicDetailAdapter);
		if (GlobalConstants.TAGS.IS_FEED_HEAD_HASHTAG.equals(tag)) {// 完整的数据
			HashTag hashTagObj = (HashTag) bundle.getSerializable(GlobalConstants.TAGS.IS_HASHTAG_OBJ);
			hashtagId = hashTagObj.getItemId();
			initSummary(hashTagObj);
			mTopicDetailP.getTopicData(hashTagObj.getItemId());
		} else if (GlobalConstants.TAGS.IS_PUSH_HASHTAG.equals(tag)) {// 只有ID
			ContextConfig.getInstance().putBooleanWithFilename(MainAct.OPEN_GESTURE_FROM_PUSH_KEY, true);
			mForceBackToSessionList = true;
			hashtagId = bundle.getString(GlobalConstants.TAGS.IS_HASHTAG_ID);
			mTopicDetailP.getTopicData(hashtagId);
		} else {
			// 没有任何数据
		}
	}

	@OnClick({ R.id.topic_detail_back_rl, R.id.topic_detail_back_iv, R.id.topic_detail_send_photo_tv, R.id.topic_detail_send_photo_rl })
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.topic_detail_back_rl:
		case R.id.topic_detail_back_iv:
//			closeTopicDetailView();
			onBackKeyPressed();
			break;
		case R.id.topic_detail_send_photo_rl:
		case R.id.topic_detail_send_photo_tv:
			new ListItemDialog(TopicDetailActivity.this, null).showListItemPopup(TopicDetailActivity.this, null, ListItemType.CAMERA, ListItemType.CHOSE_PHOTOS);
			break;
		}
	}

	@Override
	public void closeTopicDetailView() {
		overridePendingTransition(R.anim.stop, R.anim.right_out);
		finish();
	}

	@Override
	public void itemCallback(int listItemType) {
		switch (listItemType) {
		case ListItemType.CAMERA:
			openCamera(this);
			break;
		case ListItemType.CHOSE_PHOTOS:
			openImgPick(this);
			break;
		}
	}

	public void initListHeader() {
		mHeadView = LayoutInflater.from(this).inflate(R.layout.item_head_topic_detail, null);
		mHeadImage = (ImageView) mHeadView.findViewById(R.id.topic_detail_photo_iv);
		mHeadText = (TextView) mHeadView.findViewById(R.id.topic_detail_sumrary_tv);
		mHeadProgress = (ProgressBar) mHeadView.findViewById(R.id.topic_detail_head_img_pb);
		mListView.addHeaderView(mHeadView);
	}

	private void initSummary(HashTag hashTag2) {
		if (hashTag2 == null) {
			return;
		}
		String summary = hashTag2.getSummary() + "  ";
		String urlStr = getString(R.string.url);
		SpannableStringBuilder ssb = new SpannableStringBuilder(summary);
		if (!TextUtils.isEmpty(hashTag2.getUrl())) {
			ssb.append(urlStr);
			ssb.setSpan(new MyURLSpan(hashTag2.getUrl()), summary.length(), summary.length() + urlStr.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
			ssb.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.light_red)), summary.length(), summary.length() + urlStr.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		}
		mHeadText.setMovementMethod(LinkMovementMethod.getInstance());
		mHeadText.setText(ssb);
		mPicasso.load(hashTag2.getBackgroundImage().getUrl(UiUtils.getScreenWidth(this), mHeadImage.getLayoutParams().height))
		//
		.resize(UiUtils.getScreenWidth(this), mHeadImage.getLayoutParams().height).//
		placeholder(R.color.gray_text).into(mHeadImage, new com.squareup.picasso.Callback() {

			@Override
			public void onError() {
				mHeadProgress.setVisibility(View.VISIBLE);
			}

			@Override
			public void onSuccess() {
				mHeadProgress.setVisibility(View.GONE);
			}

		});
		mTopicDetailTitle.setText(hashTag2.getName());
	}

	private void initListFooter() {
		mFootView = LayoutInflater.from(this).inflate(R.layout.xlistview_footer, null);
		mProgressBar = (ProgressBar) mFootView.findViewById(R.id.xlistview_footer_progressbar);
		footText = (TextView) mFootView.findViewById(R.id.xlistview_footer_hint_textview);
		mListView.addFooterView(mFootView);
	}

	@Override
	public void onRefresh() {
		mTopicDetailP.refreshTopicData(hashtagId);
	}

	@Override
	public void notifyDataChange(TopicPosts t) {
		if (t == null) {
			showReloadFooterView();
			return;
		}
		initSummary(t.getHashTag());
		mTopicDetailAdapter.notifyDataSetChanged(t);
	}

	@Override
	public void hideRefreshView() {
		mRefreshLayout.setRefreshing(false);
	}

	@Override
	public void showRefreshView() {
		mRefreshLayout.setRefreshing(true);
	}

	@Override
	public void showNomoreFooterView() {
		mFootView.setVisibility(View.GONE);
		mProgressBar.setVisibility(View.GONE);
		footText.setVisibility(View.GONE);
	}

	@Override
	public void showReloadFooterView() {
		mFootView.setVisibility(View.VISIBLE);
		mProgressBar.setVisibility(View.GONE);
		footText.setVisibility(View.VISIBLE);
		footText.setText(R.string.reload);
		mFootView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mTopicDetailP.getTopicData(hashtagId);
			}
		});
	}

	@Override
	public void showLoadingFooterView() {
		mFootView.setVisibility(View.VISIBLE);
		mProgressBar.setVisibility(View.VISIBLE);
		footText.setVisibility(View.VISIBLE);
		footText.setText(R.string.loading);
	}

	String openFrom = null;
	private TextView mHeadText;
	private String tag;
	private String hashtagId;
	private ProgressBar mHeadProgress;

	@Override
	protected void onActivityResult(int requestcode, int resultcode, Intent result) {
		super.onActivityResult(requestcode, resultcode, result);
		if (resultcode == RESULT_OK) {
			switch (requestcode) {
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
				reqSendImg(result);
			}
				break;
			case GlobalConstants.AppConstact.OPEN_COMPOSE_FEED: // 刷新feed列表
				onRefresh();
				mRefreshLayout.setRefreshing(true);
				break;
			}
		}
	}

	private void reqSendImg(Intent result) {
		String path = result.getStringExtra("path");
		if (TextUtils.isEmpty(path)) {
			return;
		}
		Intent intent = new Intent(this, PicSendActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("path", path);
		bundle.putString("hashtagId", hashtagId);
		bundle.putString("openType", TopicDetailActivity.TAG);
		bundle.putString("openFrom", openFrom);
		intent.putExtras(bundle);
		startActivityForResult(intent, GlobalConstants.AppConstact.OPEN_COMPOSE_FEED);
		overridePendingTransition(R.anim.left_in, 0);
	}

	@Override
	protected void onDestroy() {
		ActivityManager.pop(this);
		super.onDestroy();
	}

	@Override
	public void initView(TopicPosts topicPosts) {
	}

	private class MyURLSpan extends ClickableSpan {

		private String mUrl;

		MyURLSpan(String url) {
			mUrl = url;
		}

		@Override
		public void onClick(View widget) {
			if (TextUtils.isEmpty(mUrl)) {
				return;
			} else {
				Intent intent = new Intent(mContext, WebActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("url", mUrl);
				bundle.putParcelable(User.TAG, null);
				intent.putExtras(bundle);
				mContext.startActivity(intent);
			}
		}
	}
	
	@Override
	public boolean onBackKeyPressed() {
		if (mForceBackToSessionList) {
			// 从push跳过来，回到会话列表，不是退出");
			Bundle bundle = new Bundle();
			bundle.putString("openTab", "topic");
			// startActivity(GlobalConstants.IntentAction.INTENT_URI_MAIN,
			// bundle);
			startActivity(GlobalConstants.IntentAction.INTENT_URI_MAIN, bundle, 0, 0);
			// 关闭自身，避免死循环
		}
		return super.onBackKeyPressed();
	}
}
