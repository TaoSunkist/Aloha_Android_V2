package com.wealoha.social.adapter.feed;

import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;

import javax.inject.Inject;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import com.squareup.picasso.Picasso;
import com.wealoha.social.BaseFragAct;
import com.wealoha.social.R;
import com.wealoha.social.activity.GDMapAct;
import com.wealoha.social.activity.WebActivity;
import com.wealoha.social.api.Feed2ListApiService;
import com.wealoha.social.beans.PostComment;
import com.wealoha.social.beans.ApiErrorCode;
import com.wealoha.social.api.BaseListApiService.NoResultCallback;
import com.wealoha.social.beans.UserTag;
import com.wealoha.social.beans.Post;
import com.wealoha.social.beans.User;
import com.wealoha.social.commons.GlobalConstants;
import com.wealoha.social.commons.GlobalConstants.ImageSize;
import com.wealoha.social.fragment.BaseFragment;
import com.wealoha.social.fragment.BaseFragment.Frag2HolderCallback;
import com.wealoha.social.fragment.FeedCommentFragment;
import com.wealoha.social.fragment.Profile2Fragment;
import com.wealoha.social.fragment.SwipeMenuListFragment;
import com.wealoha.social.inject.Injector;
import com.wealoha.social.store.PopupStore;
import com.wealoha.social.utils.ContextUtil;
import com.wealoha.social.utils.FontUtil;
import com.wealoha.social.utils.RegionNodeUtil;
import com.wealoha.social.utils.SpannableUtil;
import com.wealoha.social.utils.StringUtil;
import com.wealoha.social.utils.TimeUtil;
import com.wealoha.social.utils.ToastUtil;
import com.wealoha.social.utils.UiUtils;
import com.wealoha.social.utils.XL;
import com.wealoha.social.view.custom.CircleImageView;
import com.wealoha.social.view.custom.dialog.ListItemDialog;
import com.wealoha.social.view.custom.dialog.ListItemDialog.ListItemCallback;
import com.wealoha.social.view.custom.dialog.ListItemDialog.ListItemType;
import com.wealoha.social.widget.NoUnderlineClickableSpan;

public class FeedHolder extends BaseFeedHolder implements OnClickListener, ListItemCallback, OnTouchListener, Frag2HolderCallback {

	@Inject
    Feed2ListApiService feed2Service;
	@Inject
	ContextUtil contextUtil;
	@Inject
	RegionNodeUtil regionNodeUtil;
	@Inject
	FontUtil fontUtil;
	@Inject
	Picasso picasso;
	@Inject
	Context mContext;

	@InjectView(R.id.content_root)
	FrameLayout feedContentRoot;
	@InjectView(R.id.user_photo)
	CircleImageView userPhoto;
	@InjectView(R.id.user_name)
	TextView username;
	@InjectView(R.id.user_location)
	TextView userLocation;
	@InjectView(R.id.time_stamp)
	TextView timeStamp;
	@InjectView(R.id.praise_tv)
	TextView praistText;
	@InjectView(R.id.share_tv)
	TextView shareText;
	@InjectView(R.id.more_iv)
	ImageView moreImage;
	@InjectView(R.id.introduction)
	TextView introduction;
	@InjectView(R.id.message_tv)
	TextView commentRoot;
	@InjectView(R.id.menu_bar)
	RelativeLayout mMenuBar;
	@InjectView(R.id.comment_list)
	LinearLayout mCommentListLl;
	@InjectView(R.id.praise_count)
	TextView mCommentListPraiseCountTv;
	@InjectView(R.id.open_comment_list)
	TextView mOpenCommentListTv;
	@InjectView(R.id.content_container)
	LinearLayout mContentContainerLayout;
	@InjectView(R.id.post_interaction)
	LinearLayout mPostInteractionLl;

	private int screenWidth;
	private final View rootView;
	private final Fragment mFrag;
	private Post mPost;
	public final static int START_FEEDCOMMENT_REQUESTCODE = 101;
	public final static String COMMENT_COUNT_DATA_KEY = "comment_count_data_key";

	/** feed comment 页中的feed holder */
	public final static int COMMENT_HOLDER = 1;
	/** tag 页中的feed holder */
	public final static int TAGS_HOLDER = 2;
	/** feed 单例页中的feed holder */
	public final static int SINGLETON_HOLDER = 3;
	private int blackTextColor;
	private Resources resources;

	public FeedHolder(Fragment frag, LayoutInflater inflater, ViewGroup parent) {
		super();
		resources = frag.getResources();
		rootView = inflater.inflate(R.layout.frag_feed_root, parent, false);
		mFrag = frag;
		// 返回由构造函数指定或setBaseContext()设置的上下文
		screenWidth = UiUtils.getScreenWidth(mFrag.getActivity().getBaseContext());
		blackTextColor = resources.getColor(R.color.black_text);
		ButterKnife.inject(this, rootView);
		Injector.inject(this);

	}

	/**
	 * 保留子控件的holder，并且将子控件填装到父控件中
	 * 
	 * @return void
	 */
	public void addChildHolder(BaseFeedHolder feedcontentHolder) {
		super.addChildHolder(feedcontentHolder);
		feedContentRoot.addView(feedcontentHolder.getView());
	}

	public void addParentHolder(BaseFeedHolder parentHolder) {
		this.parentHolder = parentHolder;
	}

	/***
	 * 赞的数量以及显示规则
	 * 
	 *  feedItemHolder
	 *            包含赞的控件的holder
	 *  mPost
	 *            当前feed
	 * @return void
	 */
	private void showLikeBar() {
		if (holder2FragCallback != null && holder2FragCallback.getHolderType() == FeedHolder.COMMENT_HOLDER) {
			mCommentListPraiseCountTv.setVisibility(View.GONE);
			return;
		}
		FontUtil.setSemiBoldTypeFace(mContext, mCommentListPraiseCountTv);
		int praiseCount = mPost.getPraiseCount();
		if (praiseCount > 0) {
			mCommentListPraiseCountTv.setVisibility(View.VISIBLE);
			mCommentListPraiseCountTv.setText(StringUtil.getPluralformString(praiseCount, R.string.count_praise, R.string.count_praises, mContext));
		} else {
			mCommentListPraiseCountTv.setVisibility(View.GONE);
		}
	}

	/**
	 * 拼装timeline中的评论列表视图
	 * 
	 * @return void
	 */
	private void showCommentList() {
		List<PostComment> recentComments = mPost.getRecentComment();
		mCommentListLl.setVisibility(View.VISIBLE);
		mCommentListLl.removeAllViews();
		if (mPost.hasMoreComment()) {
			mOpenCommentListTv.setVisibility(View.VISIBLE);
			mOpenCommentListTv.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					openComment(false);
				}
			});
			FontUtil.setSemiBoldTypeFace(mContext, mOpenCommentListTv);
		} else {
			mOpenCommentListTv.setVisibility(View.GONE);
		}
		if (recentComments != null && recentComments.size() > 0) {
			int foregroundColor = mContext.getResources().getColor(R.color.hashtag_in_feed_bg);
			for (int i = 0; i < recentComments.size(); i++) {
				final PostComment pc = recentComments.get(i);
				final String replyUserName = pc.getReplyUser() == null ? null : pc.getReplyUser().getName();
				final String userName = pc.getUser() == null ? null : pc.getUser().getName();

				// final String replyUserName = "徐八怪";
				// final String userName = "杨帅气";
				SpannableStringBuilder ssb = null;
				String content = null;
				if (pc.getReplyUser() != null) {

					content = mContext.getString(R.string.open_comment_user, replyUserName, userName, pc.getComment());
					ssb = SpannableUtil.buidlerClickableStr(content, new String[] { replyUserName, userName }, foregroundColor, new NoUnderlineClickableSpan() {

						@Override
						public void onClick(View widget) {
							startUserProfile(pc.getReplyUser());
						}

					}, new NoUnderlineClickableSpan() {

						@Override
						public void onClick(View widget) {
							startUserProfile(pc.getUser());
						}

					});
				} else {
					content = userName + " " + pc.getComment();
					ssb = SpannableUtil.buidlerClickableStr(content, new String[] { userName }, foregroundColor, new NoUnderlineClickableSpan() {

						@Override
						public void onClick(View widget) {
							startUserProfile(pc.getUser());
						}

					});
				}

				TextView comment = new TextView(mContext);
				LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				if (i != recentComments.size() - 1) {
					params.setMargins(0, 0, 0, UiUtils.dip2px(mContext, 8));
				}
				comment.setLayoutParams(params);
				comment.setText(ssb);
				comment.setTextSize(13);
				comment.setLineSpacing(3f, 1f);
				comment.setTextColor(mContext.getResources().getColor(R.color.black_text));
				comment.setMovementMethod(LinkMovementMethod.getInstance());
				comment.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						openComment(false);
					}
				});
				FontUtil.setMediumFace(mContext, comment);
				mCommentListLl.addView(comment);
			}
		} else {
			mCommentListLl.setVisibility(View.GONE);
		}

	}

	/**
	 * 开启某人的profile
	 * 
	 *  user2
	 * @return void
	 */
	public void startUserProfile(User user2) {
		Bundle bundle = new Bundle();
		bundle.putSerializable(User.TAG, user2);
		((BaseFragAct) mFrag.getActivity()).startFragment(Profile2Fragment.class, bundle, false);

	}

	/**
	 * 隐藏timeline 页面的留言列表
	 * 
	 * @return void
	 */
	public void hideCommentListView() {
		mPostInteractionLl.setVisibility(View.GONE);
		mPostInteractionLl.setTag(false);
	}

	/***
	 * 根据不同的单例feed 来渲染不同的视图
	 * 
	 *  type
	 *            SingletonFeedFragment.TAGS_HOLDER or SingletonFeedFragment.SINGLETON_HOLDER
	 * @return void
	 */
	private void initViewByFragType() {
		FontUtil.setRegulartypeFace(mContext, mMenuBar);
		if (holder2FragCallback != null) {
			if (holder2FragCallback.getHolderType() == FeedHolder.TAGS_HOLDER) {
				mMenuBar.setVisibility(View.GONE);
				mPostInteractionLl.setVisibility(View.GONE);
			} else if (holder2FragCallback.getHolderType() == FeedHolder.SINGLETON_HOLDER) {
				mPostInteractionLl.setVisibility(View.GONE);
			} else if (holder2FragCallback.getHolderType() == FeedHolder.COMMENT_HOLDER) {
				mPostInteractionLl.setVisibility(View.GONE);
				// 留言终端也，menu bar 要距离下面的控件15dp
				((LinearLayout.LayoutParams) mMenuBar.getLayoutParams()).bottomMargin = UiUtils.dip2px(mContext, 15);
			}
		}
	}

	/**
	 * @Title: changeColor
	 * @Description: 赞的颜色控制
	 *   v
	 *   flag 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	private void changeColor(boolean flag) {
		int praiseImg;
		int praiseTxt;
		int rootResId;
		if (flag) {
			praiseImg = R.drawable.feed_like_r;
			rootResId = R.drawable.shape_feedholder_menu_checked;
			praiseTxt = mFrag.getResources().getColor(R.color.light_red);
		} else {
			rootResId = R.drawable.shape_feedholder_menu;
			praiseImg = R.drawable.feed_like;
			praiseTxt = mFrag.getResources().getColor(R.color.gray_aaaaaa);
		}
		praistText.setBackgroundResource(rootResId);
		Drawable drawable = mFrag.getResources().getDrawable(praiseImg);
		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		praistText.setCompoundDrawables(drawable, null, null, null);
		praistText.setTextColor(praiseTxt);
	}

	/***
	 * 渲染视图，获取feed数据，装填子视图
	 * 
	 *  feed
	 *            当前feed 数据
	 * @return View feed的根视图
	 */
	@Override
	public View resetViewData(Post post) {
		mPost = post;
		if (childHodler != null) {
			childHodler.resetViewData(post);// 更新子视图
		}
		initView(post);
		return rootView;
	}

	/***
	 * 渲染视图
	 * 
	 *  post
	 *            当前feed
	 */
	private void initView(Post post) {
		mContentContainerLayout.setVisibility(View.VISIBLE);
		praistText.setOnTouchListener(this);
		commentRoot.setOnTouchListener(this);
		moreImage.setOnTouchListener(this);
		// -------------开始渲染视图
		if (isNewHolder()) {
			FontUtil.setSemiBoldTypeFace(mFrag.getActivity(), username);
			FontUtil.setRegulartypeFace(mFrag.getActivity(), introduction);
			FontUtil.setSemiBoldTypeFace(mFrag.getActivity(), userLocation);
			FontUtil.setSemiBoldTypeFace(mFrag.getActivity(), timeStamp);
			FontUtil.setRegulartypeFace(mFrag.getActivity(), commentRoot);
			FontUtil.setRegulartypeFace(mFrag.getActivity(), praistText);
			FontUtil.setRegulartypeFace(mFrag.getActivity(), shareText);
		}
		// 名字转成风骚的大写~~
		String upper = post.getUser().getName();
		if (!TextUtils.isEmpty(upper)) {
			upper = upper.toUpperCase(Locale.getDefault());
		}
		username.setText(upper);

		initIntroductionView(post);

		String time = TimeUtil.howLong(mFrag.getActivity(), post.getCreateTimeMillis());
		timeStamp.setText(time);

		if (!TextUtils.isEmpty(post.getVenue())) {
			userLocation.setText(post.getVenue());
			userLocation.setVisibility(View.VISIBLE);
		} else {
			userLocation.setVisibility(View.GONE);
		}
		picasso.load(post.getUser().getAvatarImage().getUrlSquare(ImageSize.AVATAR_ROUND_SMALL))//
		.placeholder(R.drawable.default_photo).into(userPhoto);
		changeColor(mPost.isLiked());

		showCommentList();
		showLikeBar();
		initViewByFragType();
		onlyHaveMenuBar();
		// -------------渲染视图结束
	}

	/**
	 * 视图中是否只有赞，留言等menubar视图，如果是，那么隐藏掉menubar上方的layout 视图
	 */
	private void onlyHaveMenuBar() {

		if (introduction.getVisibility() == View.VISIBLE) {
			XL.i("ONLY_SHOW_MENU_BAR", "introduction");
			return;
		}
		if (mCommentListPraiseCountTv.getVisibility() == View.VISIBLE) {
			XL.i("ONLY_SHOW_MENU_BAR", "mCommentListPraiseCountTv");
			return;
		}
		if (mOpenCommentListTv.getVisibility() == View.VISIBLE) {
			XL.i("ONLY_SHOW_MENU_BAR", "mOpenCommentListTv");
			return;
		}
		if (mCommentListLl.getVisibility() == View.VISIBLE) {
			XL.i("ONLY_SHOW_MENU_BAR", "mCommentListLl");
			return;
		}
		mContentContainerLayout.setVisibility(View.GONE);
	}

	/**
	 * 渲染post 描述和话题标签视图
	 * 
	 *  post
	 * @return void
	 */
	private void initIntroductionView(final Post post) {
		String introductionStr = post.getDescription();
		boolean isPostDesNull;
		if (TextUtils.isEmpty(introductionStr)) {
			isPostDesNull = true;
		} else {
			isPostDesNull = false;
		}
		introduction.setVisibility(View.VISIBLE);
		SpannableStringBuilder ssb = null;
		if (post.getHashTag() != null) {
			if (introductionStr == null) {
				introductionStr = "";
			}
			introductionStr = " " + post.getHashTag().getName() + "   " + introductionStr;
			BackgroundColorSpan bcs = new BackgroundColorSpan(resources.getColor(R.color.hashtag_in_feed_bg));
			ForegroundColorSpan fcs = new ForegroundColorSpan(Color.WHITE);
			ssb = SpannableUtil.//
			buidlerClickableStr(introductionStr, bcs, fcs, //
								new String[] { " " + post.getHashTag().getName() + " " }, //
								new NoUnderlineClickableSpan() {

									@Override
									public void onClick(View widget) {
										if (mFrag != null && mFrag.isVisible()) {
											((BaseFragment) mFrag).openTopic(post.getHashTag());
										}
									}
								});
			if (isPostDesNull) {
				introduction.setMovementMethod(LinkMovementMethod.getInstance());
				introduction.setText(ssb);
			} else {
				dealLink(ssb);
			}
		} else if (TextUtils.isEmpty(introductionStr)) {
			introduction.setVisibility(View.GONE);
		} else {
			ssb = new SpannableStringBuilder(introductionStr);
			if (isPostDesNull) {
				return;
			} else {
				dealLink(ssb);
			}
		}
	}

	private void dealLink(SpannableStringBuilder ssb) {
		introduction.setText(ssb);
		Linkify.addLinks(introduction, GlobalConstants.COMPILE, "");
		introduction.setLinksClickable(true);
		int end = ssb.length();
		SpannableStringBuilder sp = new SpannableStringBuilder(introduction.getText());
		URLSpan[] urls = sp.getSpans(0, end, URLSpan.class);
		for (final URLSpan url : urls) {
			ssb.setSpan(new ClickableSpan() {

				@Override
				public void onClick(View arg0) {
					String urlStr = url.getURL().toString();
					Matcher m = GlobalConstants.COMPILE.matcher(urlStr == null ? "" : urlStr);
					if (m.find()) {
						Intent intent = new Intent(mFrag.getActivity(), WebActivity.class);
						Bundle bundle = new Bundle();
						bundle.putString("url", urlStr);
						intent.putExtras(bundle);
						mFrag.getActivity().startActivity(intent);
					}
				}
			}, sp.getSpanStart(url), sp.getSpanEnd(url), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
			ssb.setSpan(new ForegroundColorSpan(blackTextColor), sp.getSpanStart(url), sp.getSpanEnd(url), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		}
		introduction.setMovementMethod(LinkMovementMethod.getInstance());
		introduction.setText(ssb);
	}

	@Override
	public View getView() {
		return rootView;
	}

	@OnClick({ R.id.user_photo,//
	R.id.user_name, //
	R.id.user_location,//
	R.id.share_tv,//
	R.id.praise_count,//
	R.id.comment_list })
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.user_photo:
		case R.id.user_name:
			openProfile();
			break;
		case R.id.user_location:
			openLocation();
			break;
		case R.id.share_tv:
			new PopupStore(regionNodeUtil).showShareProfileUrl02(mFrag.getActivity().getString(R.string.share_post),//
																	(BaseFragAct) mFrag.getActivity(), mPost.getUser().getName(), StringUtil.sharePostWebUrl(contextUtil.getCurrentUser().getId(), "", mPost, StringUtil.ME_PROFILE_COPY_LINK), mPost.getCommonImage().getUrl(100, 100));
			break;
		case R.id.praise_count:
			openUserList();
			break;
		case R.id.comment_list:
			openComment(false);
			break;
		default:
			break;
		}
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
		((BaseFragAct) mFrag.getActivity()).startFragment(SwipeMenuListFragment.class, bundle, true);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() != MotionEvent.ACTION_UP) {
			return false;
		}
		switch (v.getId()) {
		case R.id.more_iv:
			createListItemDialog();
			return true;
		case R.id.praise_tv:
			praisePost();
			return true;
		case R.id.message_tv:
			if (mFrag instanceof FeedCommentFragment) {// 以免循环进入留言界面
				if (holder2FragCallback != null) {
					holder2FragCallback.commentClickCallback();
				}
			} else {
				openComment(true);
			}
			return true;
		default:
			break;
		}
		v.performClick();
		return false;
	}

	/**
	 * 跳转留言页
	 * 
	 *  isPopSoftInput
	 *            是否弹出输入发
	 */
	private void openComment(boolean isPopSoftInput) {
		if (mFrag instanceof BaseFragment && mFrag.isVisible()) {
			((BaseFragment) mFrag).setFeed2FragCallback(this);// 评论成功后frag
		}

		Bundle bundle = new Bundle();
		bundle.putSerializable(GlobalConstants.TAGS.POST_TAG, mPost);
		bundle.putBoolean("isPopSoftInputKey", isPopSoftInput);
		((BaseFragAct) mFrag.getActivity()).startFragmentForResult(FeedCommentFragment.class,//
																	bundle, true, START_FEEDCOMMENT_REQUESTCODE, //
																	R.anim.left_in, R.anim.stop);
	}

	/***
	 * 开启用户的主页
	 * 
	 */
	public void openProfile() {
		Bundle bundle = new Bundle();
		bundle.putSerializable(User.TAG, mPost.getUser());
		((BaseFragAct) mFrag.getActivity()).startFragment(Profile2Fragment.class, bundle, true);
	}

	public void openLocation() {
		Intent intent = new Intent(mFrag.getActivity(), GDMapAct.class);
		intent.putExtra("latitude", mPost.getLatitude());
		intent.putExtra("longitude", mPost.getLongitude());
		intent.putExtra("userphoto", mPost.getUser().getAvatarImage());
		intent.putExtra("venueAbroad", mPost.getVenueAbroad());
		mFrag.getActivity().startActivity(intent);
	}

	public void praisePost() {
		if (mPost.isLiked()) {
			canclePraiseFeed();
		} else {
			praise();
		}
	}

	/***
	 * 赞post
	 */
	private void praise() {
		feed2Service.praiseFeed(mPost.getPostId(), new NoResultCallback() {

			@Override
			public void success() {
				if (!mFrag.isVisible()) {
					return;
				}
				mPost.praise();
				showLikeBar();
				changeColor(mPost.isLiked());
				if (childHodler != null) {
					childHodler.praisePost();
				}

				if (holder2FragCallback != null) {
					holder2FragCallback.praiseCallback();
				}
			}

			@Override
			public void fail(ApiErrorCode code, Exception exception) {
				ToastUtil.longToast(mContext, R.string.network_error);
			}
		});
	}

	/***
	 * 取消赞
	 * 
	 */
	private void canclePraiseFeed() {
		feed2Service.canclePraiseFeed(mPost.getPostId(), new NoResultCallback() {

			@Override
			public void success() {
				mPost.dislike();
				showLikeBar();
				changeColor(mPost.isLiked());
				if (holder2FragCallback != null) {
					holder2FragCallback.praiseCallback();
				}
			}

			@Override
			public void fail(ApiErrorCode code, Exception exception) {
				ToastUtil.longToast(mFrag.getActivity(), R.string.network_error);
			}
		});
	}

	private void deletePost() {
		feed2Service.deletePost(mPost.getPostId(), new NoResultCallback() {

			@Override
			public void success() {
				if (holder2AdtCallback != null) {
					holder2AdtCallback.deletePostCallback(holderPosition);
				}

				if (holder2FragCallback != null) {
					holder2FragCallback.deleteCallback();
				}
			}

			@Override
			public void fail(ApiErrorCode code, Exception exception) {
				ToastUtil.longToast(mFrag.getActivity(), R.string.network_error);
			}
		});
	}

	/**
	 * @Title: createListItemDialog
	 * @Description: 组装举报等功能的dialog，一个模块中可能同时需要几个功能 itemType的数组的大小就为几， 遇到值为 0 的元素dialog会停止继续组装新的item
	 */
	private void createListItemDialog() {
		// 这个模块中最多有两种不同的item 同时出现在dialog 上，所以数组大小为2
		int[] itemType = new int[2];
		int index = 0;
		String title = null;
		if (mPost.isTagMe()) {
			itemType[index++] = ListItemType.DELETE_TAG_ITEM;
		}
		if (mPost.getUser().getMe()) {
			itemType[index++] = ListItemType.DELETE_FEED_ITEM;
			title = mPost.getUser().getName();
		} else {
			itemType[index++] = ListItemType.REPORT_FEED_ITEM;
			title = mFrag.getString(R.string.report_inappropriate_content);
		}

		new ListItemDialog(mFrag.getActivity(), (ViewGroup) rootView).showListItemPopup(this, title, itemType);
	}

	/***
	 * 举报
	 * 
	 * @return void
	 */
	private void report() {
		feed2Service.reportPost(mPost.getPostId(), new NoResultCallback() {

			@Override
			public void success() {
				ToastUtil.longToast(mFrag.getActivity(), R.string.report_inappropriate_success);
			}

			@Override
			public void fail(ApiErrorCode code, Exception exception) {
				ToastUtil.longToast(mFrag.getActivity(), R.string.report_faile);
			}
		});
	}

	/***
	 * 移除当前用户的tag
	 * 
	 * @return void
	 */
	public void removeMyTag() {

		if (contextUtil.getCurrentUser() != null) {
			feed2Service.removeTag(mPost.getPostId(), contextUtil.getCurrentUser().getId(), new NoResultCallback() {

				@Override
				public void success() {
					removeTag(contextUtil.getCurrentUser().getId());
				}

				@Override
				public void fail(ApiErrorCode code, Exception exception) {
					ToastUtil.longToast(mFrag.getActivity(), R.string.report_faile);
				}
			});
		}
	}

	protected void sharePost() {
		new PopupStore(regionNodeUtil).showSharePostPopup((BaseFragAct) mFrag.getActivity(), mPost, contextUtil.getCurrentUser());
		// new PopupStore(regionNodeUtil).showShareProfilePopup((BaseFragAct)
		// mFrag.getActivity(),
		// contextUtil.getCurrentUser(), contextUtil.getCurrentUser());
	}

	@Override
	public void itemCallback(int listItemType) {
		switch (listItemType) {
		case ListItemType.DELETE_FEED_ITEM:
			deletePost();
			break;
		case ListItemType.REPORT_FEED_ITEM:
			report();
			break;
		case ListItemType.DELETE_TAG_ITEM:
			removeMyTag();
			break;
		case ListItemType.SHARE_POST:
			sharePost();
			break;

		default:
			break;
		}
	}

	/***
	 * 留言回调，更新留言数
	 * 
	 *  count
	 *            留言数
	 * @return
	 */
	@Override
	public void commentCallback(int count) {
		mPost.setCommentCount(count);
		commentRoot.setText(mPost.getCommentCount() == 0 ? mFrag.getString(R.string.comment) : String.valueOf(mPost.getCommentCount()));
		commentRoot.requestLayout();
	}

	@Override
	public void removeTag(String userid) {
		XL.i("REMOVE_TAG", "FEED:");
		if (childHodler != null) {
			XL.i("REMOVE_TAG", "FEED:CHILD NOT NULL");
			childHodler.removeTag(userid);// 子视图负责移除视图上的tag
		}

		// 父视图移除数据中的tag
		for (int i = 0; i < mPost.getUserTags().size(); i++) {
			UserTag userTag = mPost.getUserTags().get(i);
			if (userid.equals(userTag.getUser().getId())) {
				mPost.getUserTags().remove(i);
				mPost.removeTagMe();
				break;
			}
		}
		// resetViewData(mPost);
	}

	@Override
	public boolean isPlayer() {
		return false;
	}

	@Override
	public int getRawTopY() {
		int[] location = new int[2];
		rootView.getLocationOnScreen(location);
		return location[1];
	}

	@Override
	public int getRawBottomY() {
		return getRawTopY() + screenWidth;
	}
}
