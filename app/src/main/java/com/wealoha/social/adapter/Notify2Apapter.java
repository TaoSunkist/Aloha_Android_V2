package com.wealoha.social.adapter;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.InjectView;

import com.squareup.picasso.Picasso;
import com.wealoha.social.R;
import com.wealoha.social.activity.FeedNoticeAct;
import com.wealoha.social.adapter.feed.AbsViewHolder;
import com.wealoha.social.api.common.BaseListApiService;
import com.wealoha.social.beans.NewAlohaNotify2;
import com.wealoha.social.beans.Notify2;
import com.wealoha.social.beans.Notify2Type;
import com.wealoha.social.beans.PostCommentNotify2;
import com.wealoha.social.beans.PostCommentReplyOnMyPost;
import com.wealoha.social.beans.PostCommentReplyOnOthersPost;
import com.wealoha.social.beans.PostLikeNotify2;
import com.wealoha.social.beans.PostTagNotify2;
import com.wealoha.social.beans.Post;
import com.wealoha.social.beans.User;
import com.wealoha.social.commons.GlobalConstants;
import com.wealoha.social.commons.GlobalConstants.ImageSize;
import com.wealoha.social.fragment.FeedCommentFragment;
import com.wealoha.social.fragment.Profile2Fragment;
import com.wealoha.social.utils.DockingBeanUtils;
import com.wealoha.social.utils.FontUtil;
import com.wealoha.social.utils.FontUtil.Font;
import com.wealoha.social.utils.TimeUtil;
import com.wealoha.social.utils.UiUtils;
import com.wealoha.social.utils.XL;
import com.wealoha.social.view.custom.CircleImageView;
import com.wealoha.social.widget.BaseAdapterHolder;
import com.wealoha.social.widget.BaseListApiAdapter;
import com.wealoha.social.widget.MultiListViewType;

/**
 * 通知2
 * 
 * @author javamonk
 * @createTime 2015年3月5日 上午10:20:08
 */
public class Notify2Apapter extends BaseListApiAdapter<Notify2, Boolean> {

	private final FeedNoticeAct mParent;
	@Inject
	FontUtil mFontUtil;
	@Inject
	Picasso picasso;

	@Inject
	FontUtil fontUtil;

	// 多个头像类型的通知，一行最多多少个
	private int oneRowMaxUserCount;

	// 多个头像类型的通知，用户头像布局
	private final LayoutParams userIconLayoutParams;
	private final LayoutParams oneIconLayoutParams;

	public Notify2Apapter(FeedNoticeAct parent, BaseListApiService<Notify2, Boolean> service) {
		super(service);
		mParent = parent;
		// int userIconSize = (int) (mParent.mScreenWidth * 0.11 + 0.5f);
		int userIconSize = UiUtils.dip2px(mParent, 40);
		XL.d("userIconSize", "" + userIconSize);
		oneRowMaxUserCount = (mParent.mScreenWidth - 150) / userIconSize;
		if (oneRowMaxUserCount >= 6) {
			oneRowMaxUserCount = 6;
		}
		userIconLayoutParams = new LayoutParams(userIconSize, userIconSize);
		oneIconLayoutParams = new LayoutParams(userIconSize, userIconSize);
		// userIconLayoutParams.setMargins(
		userIconLayoutParams.setMargins(0, 0, UiUtils.dip2px(parent, 4), 0);
		oneIconLayoutParams.setMargins(0, 0, UiUtils.dip2px(parent, 4), 0);
	}

	@Override
	protected boolean isMultiListView() {
		return true;
	}

	@Override
	protected int getMultiListViewTypeCount() {
		return Notify2Type.values().length;
	}

	@Override
	protected MultiListViewType getItemMultiViewType(Notify2 item) {
		return item.getType();
	}

	@Override
	protected BaseAdapterHolder newViewHolder(MultiListViewType type, Notify2 item, LayoutInflater inflater, ViewGroup parent) {
		View view;
		switch ((Notify2Type) type) {
		case PostLike:
			view = inflater.inflate(R.layout.item_feed_notice_post_like, parent, false);
			return new PostLikeViewHolder(view);
		case PostComment:
			view = inflater.inflate(R.layout.item_feed_notice_post_comment, parent, false);
			return new PostCommentViewHolder(view);
		case PostTag:
			view = inflater.inflate(R.layout.item_feed_notice_post_tag, parent, false);
			return new PostTagViewHolder(view);
		case NewAloha:
			view = inflater.inflate(R.layout.item_feed_notice_new_aloha, parent, false);
			return new NewAlohaViewHolder(view);
			// 这里加新类型支持
		case PostCommentReplyOnMyPost:
			view = inflater.inflate(R.layout.item_feed_notice_post_comment, parent, false);
			return new PostCommentViewHolder(view);
		case PostCommentReplyOnOthersPost:
			view = inflater.inflate(R.layout.item_feed_notice_post_comment, parent, false);
			return new PostCommentViewHolder(view);
		}

		throw new IllegalArgumentException("不支持的类型: " + type);
	}

	@Override
	protected void fillView(AbsViewHolder holder, Notify2 item, int position, View convertView) {
		switch (item.getType()) {
		case PostLike:
			PostLikeNotify2 postLikeNotify2 = (PostLikeNotify2) item;
			PostLikeViewHolder postLikeViewHolder = (PostLikeViewHolder) holder;
			if (holder.isNewHolder()) {
				// 字体初始化很慢
				fontUtil.changeFonts((ViewGroup) holder.getView(), Font.ENCODESANSCOMPRESSED_400_REGULAR);
				fontUtil.changeViewFont(postLikeViewHolder.mTimeStamp, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
				fontUtil.changeViewFont(postLikeViewHolder.mTitle, Font.ENCODESANSCOMPRESSED_500_MEDIUM);
			}

			if (!postLikeNotify2.isUnread()) {
				postLikeViewHolder.mRootView.setBackgroundResource(R.drawable.selector_notify_read);
			} else {
				postLikeViewHolder.mRootView.setBackgroundResource(R.drawable.layout_silde_01);
			}

			postLikeViewHolder.mTimeStamp.setText(TimeUtil.getDistanceTimeForApp(mParent, //
																					new Date().getTime(), postLikeNotify2.getUpdateTimeMillis()));
			picasso.load(postLikeNotify2.getPost().getImage().getUrl(80, 80))//
			.into(postLikeViewHolder.mFeedImg);

			if (postLikeNotify2.getUsers().size() == 1) {
				// * 說你的相片讃
				String format = String.format(mParent.getResources().getString(R.string.title_activity_post_like_title),//
												postLikeNotify2.getUsers().get(0).getName());
				postLikeViewHolder.mTitle.setText(format);
			} else {
				String format = mParent.getResources().getString(R.string.title_activity_post_like_title_more_end,//
				postLikeNotify2.getUsers().size());

				postLikeViewHolder.mTitle.setText(format);
			}
			wrapUsers(item, null, postLikeViewHolder);
			postLikeViewHolder.mFeedImg.setOnClickListener(new OnClickListener(postLikeNotify2));
			break;

		case PostComment:
			final PostCommentNotify2 postCommentNotify2 = (PostCommentNotify2) item;
			PostCommentViewHolder postCommentViewHolder = (PostCommentViewHolder) holder;
			if (holder.isNewHolder()) {
				fontUtil.changeFonts((ViewGroup) holder.getView(), Font.ENCODESANSCOMPRESSED_400_REGULAR);
				fontUtil.changeViewFont(postCommentViewHolder.mStamp, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
				fontUtil.changeViewFont(postCommentViewHolder.mUserName, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
				fontUtil.changeViewFont(postCommentViewHolder.mCommentBody, Font.ENCODESANSCOMPRESSED_500_MEDIUM);
			}
			if (!postCommentNotify2.isUnread()) {
				postCommentViewHolder.mRootView.setBackgroundResource(R.drawable.selector_notify_read);
			} else {
				postCommentViewHolder.mRootView.setBackgroundResource(R.drawable.layout_silde_01);
			}
			
			if(postCommentNotify2.isReplyMe()){
				postCommentViewHolder.mCommentBody.setText(postCommentNotify2.getComment());
			}else {
				postCommentViewHolder.mCommentBody.setText(postCommentNotify2.getComment());
			}
			postCommentViewHolder.mStamp.setText(TimeUtil.getDistanceTimeForApp(mParent, //
																				new Date().getTime(), postCommentNotify2.getUpdateTimeMillis()));
			picasso.load(postCommentNotify2.getFromUser().getAvatarImage().getUrlSquare(ImageSize.CHAT_THUMB)).//
			placeholder(R.drawable.default_photo).into(postCommentViewHolder.mUserHead);
			picasso.load(postCommentNotify2.getPost().getImage().getUrl(80, 80)).//
			into(postCommentViewHolder.mFeedImg);

			postCommentViewHolder.mUserName.setText(postCommentNotify2.getFromUser().getName());
			postCommentViewHolder.mUserHead.setOnClickListener(new OnClickListener(postCommentNotify2));
			postCommentViewHolder.mFeedImg.setOnClickListener(new OnClickListener(postCommentNotify2));
			break;

		case PostTag:
			PostTagNotify2 postTagNotify2 = (PostTagNotify2) item;
			PostTagViewHolder postTagViewHolder = (PostTagViewHolder) holder;
			if (holder.isNewHolder()) {
				fontUtil.changeFonts((ViewGroup) holder.getView(), Font.ENCODESANSCOMPRESSED_400_REGULAR);
				mFontUtil.changeViewFont(postTagViewHolder.mStamp, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
				mFontUtil.changeViewFont(postTagViewHolder.mUserName, Font.ENCODESANSCOMPRESSED_500_MEDIUM);
			}
			if (!postTagNotify2.isUnread()) {
				postTagViewHolder.mRootView.setBackgroundResource(R.drawable.selector_notify_read);
			} else {
				postTagViewHolder.mRootView.setBackgroundResource(R.drawable.layout_silde_01);
			}
			picasso.load(postTagNotify2.getFromUser().getAvatarImage().getUrl(ImageSize.AVATAR_ROUND_SMALL, ImageSize.AVATAR_ROUND_SMALL)).//
			placeholder(R.drawable.default_photo).into(postTagViewHolder.mUserHead);
			picasso.load(postTagNotify2.getPost().getImage().getUrl(100, 100)).//
			into(postTagViewHolder.mFeedImg);
			String format = String.format(mParent.getResources().getString(R.string.item_feed_notice_post_tag_body),//
											postTagNotify2.getFromUser().getName());
			postTagViewHolder.mUserName.setText(format);
			postTagViewHolder.mStamp.setText(TimeUtil.getDistanceTimeForApp(mParent, //
																			new Date().getTime(), postTagNotify2.getPost().getCreateTimeMillis()));
			postTagViewHolder.mFeedImg.setOnClickListener(new OnClickListener(postTagNotify2));
			postTagViewHolder.mUserHead.setOnClickListener(new OnClickListener(postTagNotify2));
			break;

		case NewAloha:
			NewAlohaNotify2 newAlohaNotify2 = (NewAlohaNotify2) item;
			NewAlohaViewHolder newAlohaViewHolder = (NewAlohaViewHolder) holder;
			if (holder.isNewHolder()) {
				long a = System.currentTimeMillis();
				fontUtil.changeFonts((ViewGroup) holder.getView(), Font.ENCODESANSCOMPRESSED_400_REGULAR);
				mFontUtil.changeViewFont(newAlohaViewHolder.mStamp, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
				mFontUtil.changeViewFont(newAlohaViewHolder.mTitle, Font.ENCODESANSCOMPRESSED_500_MEDIUM);
				XL.v("Perfs", "修改Font: " + (System.currentTimeMillis() - a));
			}

			if (!newAlohaNotify2.isUnread()) {
				newAlohaViewHolder.mRootView.setBackgroundResource(R.drawable.selector_notify_read);
			} else {
				newAlohaViewHolder.mRootView.setBackgroundResource(R.drawable.layout_silde_01);
			}
			int userNum = newAlohaNotify2.getUsers().size();
			String string = null;
			// SpannableString newAlohaSS = null;
			if (userNum == 1) {
				string = String.format(mParent.getResources().getString(R.string.item_feed_notice_new_aloha_body),//
										newAlohaNotify2.getUsers().get(0).getName());
				newAlohaViewHolder.mTitle.setText(string);
			} else {
				string = mParent.getResources().getString(R.string.item_feed_notice_new_aloha_body_more_one, userNum);
				newAlohaViewHolder.mTitle.setText(string);
			}
			newAlohaViewHolder.mStamp.setText(TimeUtil.getDistanceTimeForApp(mParent, //
																				new Date().getTime(), newAlohaNotify2.getUpdateTimeMillis()));
			wrapUsers(item, newAlohaViewHolder, null);
			break;
		case PostCommentReplyOnMyPost:
			final PostCommentReplyOnMyPost postCommentReplyOnMyPost = (PostCommentReplyOnMyPost) item;
			PostCommentViewHolder postCommentReplyOnMyPostViewHolder = (PostCommentViewHolder) holder;
			if (holder.isNewHolder()) {
				fontUtil.changeFonts((ViewGroup) holder.getView(), Font.ENCODESANSCOMPRESSED_400_REGULAR);
				fontUtil.changeViewFont(postCommentReplyOnMyPostViewHolder.mStamp, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
				fontUtil.changeViewFont(postCommentReplyOnMyPostViewHolder.mUserName, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
				fontUtil.changeViewFont(postCommentReplyOnMyPostViewHolder.mCommentBody, Font.ENCODESANSCOMPRESSED_500_MEDIUM);
			}
			if (!postCommentReplyOnMyPost.isUnread()) {
				postCommentReplyOnMyPostViewHolder.mRootView.setBackgroundResource(R.drawable.selector_notify_read);
			} else {
				postCommentReplyOnMyPostViewHolder.mRootView.setBackgroundResource(R.drawable.layout_silde_01);
			}
			postCommentReplyOnMyPostViewHolder.mCommentBody.setText(postCommentReplyOnMyPost.getComment());
			postCommentReplyOnMyPostViewHolder.mStamp.setText(TimeUtil.getDistanceTimeForApp(mParent, //
																				new Date().getTime(), postCommentReplyOnMyPost.getUpdateTimeMillis()));
			picasso.load(postCommentReplyOnMyPost.getFromUser().getAvatarImage().getUrlSquare(ImageSize.CHAT_THUMB)).//
			placeholder(R.drawable.default_photo).into(postCommentReplyOnMyPostViewHolder.mUserHead);
			picasso.load(postCommentReplyOnMyPost.getPost().getImage().getUrl(80, 80)).//
			into(postCommentReplyOnMyPostViewHolder.mFeedImg);

			postCommentReplyOnMyPostViewHolder.mUserName.setText(postCommentReplyOnMyPost.getFromUser().getName());
			postCommentReplyOnMyPostViewHolder.mUserHead.setOnClickListener(new OnClickListener(postCommentReplyOnMyPost));
			postCommentReplyOnMyPostViewHolder.mFeedImg.setOnClickListener(new OnClickListener(postCommentReplyOnMyPost));
			break;
		case PostCommentReplyOnOthersPost:
			final PostCommentReplyOnOthersPost postCommentReplyOnOthersPost = (PostCommentReplyOnOthersPost) item;
			PostCommentViewHolder postCommentReplyOnOthersPostViewHolder = (PostCommentViewHolder) holder;
			if (holder.isNewHolder()) {
				fontUtil.changeFonts((ViewGroup) holder.getView(), Font.ENCODESANSCOMPRESSED_400_REGULAR);
				fontUtil.changeViewFont(postCommentReplyOnOthersPostViewHolder.mStamp, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
				fontUtil.changeViewFont(postCommentReplyOnOthersPostViewHolder.mUserName, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
				fontUtil.changeViewFont(postCommentReplyOnOthersPostViewHolder.mCommentBody, Font.ENCODESANSCOMPRESSED_500_MEDIUM);
			}
			if (!postCommentReplyOnOthersPost.isUnread()) {
				postCommentReplyOnOthersPostViewHolder.mRootView.setBackgroundResource(R.drawable.selector_notify_read);
			} else {
				postCommentReplyOnOthersPostViewHolder.mRootView.setBackgroundResource(R.drawable.layout_silde_01);
			}
			postCommentReplyOnOthersPostViewHolder.mCommentBody.setText(postCommentReplyOnOthersPost.getComment());
			postCommentReplyOnOthersPostViewHolder.mStamp.setText(TimeUtil.getDistanceTimeForApp(mParent, //
																				new Date().getTime(), postCommentReplyOnOthersPost.getUpdateTimeMillis()));
			picasso.load(postCommentReplyOnOthersPost.getFromUser().getAvatarImage().getUrlSquare(ImageSize.CHAT_THUMB)).//
			placeholder(R.drawable.default_photo).into(postCommentReplyOnOthersPostViewHolder.mUserHead);
			picasso.load(postCommentReplyOnOthersPost.getPost().getImage().getUrl(80, 80)).//
			into(postCommentReplyOnOthersPostViewHolder.mFeedImg);

			postCommentReplyOnOthersPostViewHolder.mUserName.setText(postCommentReplyOnOthersPost.getFromUser().getName());
			postCommentReplyOnOthersPostViewHolder.mUserHead.setOnClickListener(new OnClickListener(postCommentReplyOnOthersPost));
			postCommentReplyOnOthersPostViewHolder.mFeedImg.setOnClickListener(new OnClickListener(postCommentReplyOnOthersPost));
			break;
		default:
			break;
		}
	}

	/**
	 * @author:sunkist 点击事件
	 * @see:
	 * @since:
	 * @description
	 * @copyright wealoha.com
	 * @Date:2015年3月2日
	 */
	private class OnClickListener implements View.OnClickListener {

		private Notify2 mNotify2;

		public OnClickListener(Notify2 notify2) {
			mNotify2 = notify2;
		}

		@Override
		public void onClick(View v) {
			Post post = null;
			switch (v.getId()) {
			case R.id.item_feed_notice_img_iv:// 被评论的Feed图片
				if(mNotify2 instanceof PostCommentNotify2){
					post = ((PostCommentNotify2) mNotify2).getPost();
				}else if(mNotify2 instanceof PostCommentReplyOnMyPost){
					post = ((PostCommentReplyOnMyPost) mNotify2).getPost();
				}else if(mNotify2 instanceof PostCommentReplyOnOthersPost){
					post = ((PostCommentReplyOnOthersPost) mNotify2).getPost();
				}
				openCommentFragment(post, false);
				return;
			case R.id.item_feed_notice_post_like_img_iv:// 被点赞的Feed图片
				post = ((PostLikeNotify2) mNotify2).getPost();
				openCommentFragment(post, false);
				return;
			case R.id.item_feed_notice_new_aloha_icon_iv:
				NewAlohaNotify2 newAlohaNotify = (NewAlohaNotify2) mNotify2;
				if (newAlohaNotify.getUsers() != null && newAlohaNotify.getUsers().size() == 1) {
					openSomeoneProfile(newAlohaNotify.getUsers().get(0));
				}
				return;
			case R.id.item_feed_notice_post_tag_img_iv:// 圈人被圈的Feed图片点击后跳转到圈人Feed
				post = ((PostTagNotify2) mNotify2).getPost();
				openCommentFragment(post, false);
				return;
			case R.id.item_feed_notice_comment_userhead_cv:// 评论Feed的User头像
				if(mNotify2 instanceof PostCommentNotify2){
					openSomeoneProfile(((PostCommentNotify2) mNotify2).getFromUser());
				}else if(mNotify2 instanceof PostCommentReplyOnMyPost){
					openSomeoneProfile(((PostCommentReplyOnMyPost) mNotify2).getFromUser());
				}else if(mNotify2 instanceof PostCommentReplyOnOthersPost){
					openSomeoneProfile(((PostCommentReplyOnOthersPost) mNotify2).getFromUser());
				}
//				openSomeoneProfile(((PostCommentNotify2) mNotify2).getFromUser());
				return;
			case R.id.item_feed_notice_post_tag_userhead_cv:// 评论Feed的User头像
				openSomeoneProfile(((PostTagNotify2) mNotify2).getFromUser());
				return;
			}
//			startSingleTagsFeed(post, feedtyep);
		}
	}
	
	private void openCommentFragment(Post post, boolean popInputMethod){
		Bundle bundle = new Bundle();
		bundle.putSerializable(GlobalConstants.TAGS.POST_TAG, post);
//		bundle.putString(GlobalConstants.TAGS.COMMENT_ID, commentId);
		bundle.putBoolean(FeedCommentFragment.IS_POP_SOFTINPUT_KEY, popInputMethod);
		mParent.startFragment(FeedCommentFragment.class, bundle, true);
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
		mParent.startFragment(Profile2Fragment.class, bundle, true);
	}

	/**
	 * @Title: startSingleTagsFeed
	 * @Description: 打开的单例feed
	 */
	private void startSingleTagsFeed(Post post, int feedFragType) {
		if (mParent != null) {
			mParent.startSingleTagsFeed(post, feedFragType);
		}
	}

	/**
	 * @Description:需要修改该代码逻辑
	 * @param notify2
	 * @param newAlohaViewHolder
	 * @param postLikeViewHolder
	 * @see:
	 * @since:
	 * @description
	 * @author: sunkist
	 * @date:2015年3月2日
	 */
	private void wrapUsers(final Notify2 notify2, NewAlohaViewHolder newAlohaViewHolder, PostLikeViewHolder postLikeViewHolder) {

		List<com.wealoha.social.api.user.bean.User> users = null;
		int size = 0;
		long t = System.currentTimeMillis();
		if (notify2 instanceof PostLikeNotify2) {
			postLikeViewHolder.mWrapUsers.removeAllViews();
			users = ((PostLikeNotify2) notify2).getUsers();
			size = users.size();
			for (int i = 0; i < Math.min(size, oneRowMaxUserCount); i++) {
				CircleImageView circleImageView = new CircleImageView(mParent);

				circleImageView.setLayoutParams(userIconLayoutParams);
				picasso.load(users.get(i).getAvatarImage().getUrlSquare(80)).placeholder(R.drawable.default_photo)//
				.into(circleImageView);
				postLikeViewHolder.mWrapUsers.addView(circleImageView);
			}
		} else if (notify2 instanceof NewAlohaNotify2) {
			newAlohaViewHolder.mWrapUsers.removeAllViews();
			users = ((NewAlohaNotify2) notify2).getUsers();
			size = users.size();

			for (int i = 0; i < Math.min(size, oneRowMaxUserCount); i++) {
				CircleImageView circleImageView = new CircleImageView(mParent);
				if (i <= 0) {
					circleImageView.setLayoutParams(oneIconLayoutParams);
				} else {
					circleImageView.setLayoutParams(userIconLayoutParams);
				}
				picasso.load(users.get(i).getAvatarImage().getUrlSquare(80)).//
				placeholder(R.drawable.default_photo).into(circleImageView);
				newAlohaViewHolder.mWrapUsers.addView(circleImageView);
			}
		}
		XL.d("Perfs", "渲染用户头像列表: " + (System.currentTimeMillis() - t));
	}

	/**
	 * @author:sunkist
	 * @see:
	 * @since:
	 * @description 评论通知
	 * @copyright wealoha.com
	 * @Date:2015年2月27日
	 */
	static class PostCommentViewHolder extends BaseAdapterHolder {

		@InjectView(R.id.item_feed_notice_comment_userhead_cv)
		public CircleImageView mUserHead;
		@InjectView(R.id.item_feed_notice_comment_username_tv)
		public TextView mUserName;
		@InjectView(R.id.item_feed_notice_comment_body_tv)
		public TextView mCommentBody;
		@InjectView(R.id.item_feed_notice_img_iv)
		public ImageView mFeedImg;
		@InjectView(R.id.item_feed_notice_time_stamp)
		public TextView mStamp;

		@InjectView(R.id.root_view)
		public RelativeLayout mRootView;

		public PostCommentViewHolder(View view) {
			super(view);
		}
	}

	/**
	 * @author:sunkist
	 * @see:
	 * @since:
	 * @description feed被喜欢
	 * @copyright wealoha.com
	 * @Date:2015-2-9
	 */
	static class PostLikeViewHolder extends BaseAdapterHolder {

		@InjectView(R.id.wrap_user_icon_ll)
		public LinearLayout mWrapUsers;
		@InjectView(R.id.root_view)
		public RelativeLayout mRootView;
		@InjectView(R.id.item_feed_notice_post_like_stamp_tv)
		public TextView mTimeStamp;
		@InjectView(R.id.item_feed_notice_post_like_title_tv)
		public TextView mTitle;
		@InjectView(R.id.item_feed_notice_post_like_img_iv)
		public ImageView mFeedImg;

		public PostLikeViewHolder(View convertView) {
			super(convertView);
			// mFontUtil.changeFonts((ViewGroup) convertView,
			// Font.ENCODESANSCOMPRESSED_400_REGULAR);
			// mFontUtil.changeViewFont(mTimeStamp,
			// Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
			// mFontUtil.changeViewFont(mTitle,
			// Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
		}
	}

	/**
	 * @author:sunkist
	 * @see:
	 * @since:
	 * @description 圈人
	 * @copyright wealoha.com
	 * @Date:2015年2月27日
	 */
	static class PostTagViewHolder extends BaseAdapterHolder {

		@InjectView(R.id.item_feed_notice_post_tag_userhead_cv)
		public CircleImageView mUserHead;
		@InjectView(R.id.item_feed_notice_post_tag_username_tv)
		public TextView mUserName;
		@InjectView(R.id.item_feed_notice_post_tag_stamp)
		public TextView mStamp;
		@InjectView(R.id.item_feed_notice_post_tag_img_iv)
		public ImageView mFeedImg;

		@InjectView(R.id.root_view)
		public RelativeLayout mRootView;

		public PostTagViewHolder(View convertView) {
			super(convertView);
			// mFontUtil.changeFonts((ViewGroup) convertView,
			// Font.ENCODESANSCOMPRESSED_400_REGULAR);
			// mFontUtil.changeViewFont(mStamp,
			// Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
			// mFontUtil.changeViewFont(mUserName,
			// Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
		}

	}

	/**
	 * @author:sunkist
	 * @see:
	 * @since:
	 * @description 新人气
	 * @copyright wealoha.com
	 * @Date:2015年2月27日
	 */
	static class NewAlohaViewHolder extends BaseAdapterHolder {

		@InjectView(R.id.item_feed_notice_new_aloha_title_tv)
		public TextView mTitle;
		@InjectView(R.id.item_feed_notice_new_aloha_stamp_tv)
		public TextView mStamp;
		@InjectView(R.id.item_feed_notice_new_aloha_wrap_users_ll)
		public LinearLayout mWrapUsers;
		@InjectView(R.id.root_view)
		public RelativeLayout mRootView;

		public NewAlohaViewHolder(View convertView) {
			super(convertView);
		}
	}
}
