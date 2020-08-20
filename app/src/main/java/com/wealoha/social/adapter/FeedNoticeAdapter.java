package com.wealoha.social.adapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import butterknife.ButterKnife;

import com.squareup.picasso.Picasso;
import com.wealoha.social.BaseFragAct;
import com.wealoha.social.R;
import com.wealoha.social.activity.FeedNoticeAct;
import com.wealoha.social.beans.NewAlohaNotify2;
import com.wealoha.social.beans.Notify2;
import com.wealoha.social.beans.Notify2Type;
import com.wealoha.social.beans.PostCommentNotify2;
import com.wealoha.social.beans.PostLikeNotify2;
import com.wealoha.social.beans.PostTagNotify2;
import com.wealoha.social.beans.User;
import com.wealoha.social.beans.User2;
import com.wealoha.social.fragment.FeedFragment;
import com.wealoha.social.fragment.Profile2Fragment;
import com.wealoha.social.inject.Injector;
import com.wealoha.social.utils.ContextUtil;
import com.wealoha.social.utils.DockingBeanUtils;
import com.wealoha.social.utils.FontUtil;
import com.wealoha.social.utils.FontUtil.Font;
import com.wealoha.social.utils.TimeUtil;
import com.wealoha.social.view.custom.CircleImageView;

@Deprecated
// 切换到了 Notify2Adapter
public class FeedNoticeAdapter extends BaseAdapter {

	private LayoutInflater mLayoutInflater;
	private FeedNoticeAct mParent;
	@Inject
	Picasso picasso;
	@Inject
	ContextUtil contextUtil;
	@Inject
	Context context;
	@Inject
	FontUtil fontUtil;
	private List<Notify2> mNotify2s = new ArrayList<Notify2>();

	public FeedNoticeAdapter(FeedNoticeAct mContext, List<Notify2> list) {
		this.mParent = mContext;
		mNotify2s.addAll(list);
		this.mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		Injector.inject(this);
		mUserIconSize = (int) (mParent.mScreenWidth * 0.15 + 0.5f);
		count = (mParent.mScreenWidth - 200) / mUserIconSize;
		layoutParams2 = new LayoutParams(mUserIconSize, mUserIconSize);
		layoutParams2.setMargins(34, 0, 0, 0);
	}

	public void notifyDataSetLoadMore(List<Notify2> list) {
		if (list != null) {
			mNotify2s.addAll(list);
			notifyDataSetChanged();
		}
	}

	public void notifyDataSetHaveNewPush(List<Notify2> list) {
		mNotify2s.addAll(list);
		notifyDataSetChanged();
	}

	public void notifyDataSetAllChanged(List<Notify2> list) {
		if (list != null) {
			mNotify2s.addAll(list);
			notifyDataSetChanged();
		}
	}

	@Override
	public int getCount() {
		if (mNotify2s.size() > 0)
			return mNotify2s.size();
		else
			return 0;
	}

	@Override
	public Notify2 getItem(int arg0) {
		return mNotify2s.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public int getItemViewType(int position) {
		return getItem(position).getType().getViewType();
	}

	@Override
	public int getViewTypeCount() {
		return Notify2Type.values().length;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		PostLikeViewHolder postLikeViewHolder = null;// feed被点赞.
		PostCommentViewHolder postCommentViewHolder = null;
		PostTagViewHolder postTagViewHolder = null;
		NewAlohaViewHolder newAlohaViewHolder = null;
		Notify2 notify2 = getItem(position);
		if (convertView == null) {
			switch (notify2.getType().getViewType()) {
			case Notify2.POST_LIKE_VIEW_TYPE:// Feed被喜欢;
				convertView = mLayoutInflater.inflate(R.layout.item_feed_notice_post_like, parent, false);
				postLikeViewHolder = new PostLikeViewHolder(convertView, mParent);
				convertView.setTag(postLikeViewHolder);
				break;
			case Notify2.POST_COMMENT_TAG_TYPE:// feed被 评论
				convertView = mLayoutInflater.inflate(R.layout.item_feed_notice_post_comment, parent, false);
				postCommentViewHolder = new PostCommentViewHolder(convertView, mParent);
				convertView.setTag(postCommentViewHolder);
				break;
			case Notify2.POST_TAG_VIEW_TYPE:// 圈人的通知
				convertView = mLayoutInflater.inflate(R.layout.item_feed_notice_post_tag, parent, false);
				postTagViewHolder = new PostTagViewHolder(convertView, mParent);
				convertView.setTag(postTagViewHolder);
				break;
			case Notify2.NEW_ALOHA_VIEW_TYPE:// 人气的通知
				convertView = mLayoutInflater.inflate(R.layout.item_feed_notice_new_aloha, parent, false);
				newAlohaViewHolder = new NewAlohaViewHolder(convertView, mParent, notify2);
				convertView.setTag(newAlohaViewHolder);
				break;
			}
		} else {
			switch (notify2.getType().getViewType()) {
			case Notify2.POST_LIKE_VIEW_TYPE:// Feed被喜欢;
				postLikeViewHolder = (PostLikeViewHolder) convertView.getTag();
				break;
			case Notify2.POST_COMMENT_TAG_TYPE:// feed被 评论
				postCommentViewHolder = (PostCommentViewHolder) convertView.getTag();
				break;
			case Notify2.POST_TAG_VIEW_TYPE:// 圈人的通知
				postTagViewHolder = (PostTagViewHolder) convertView.getTag();
				break;
			case Notify2.NEW_ALOHA_VIEW_TYPE:// 人气的通知
				newAlohaViewHolder = (NewAlohaViewHolder) convertView.getTag();
				break;
			}
		}
		// --------------点击事件的绑定和数据的绑定
		switch (notify2.getType().getViewType()) {
		case Notify2.POST_LIKE_VIEW_TYPE:// Feed被喜欢;
			final PostLikeNotify2 postLikeNotify2 = (PostLikeNotify2) notify2;
			postLikeViewHolder.mTimeStamp.setText(TimeUtil.getDistanceTimeForApp(mParent, //
																					new Date().getTime(), postLikeNotify2.getPost().getCreateTimeMillis()));
			picasso.load(postLikeNotify2.getPost().getCommonImage().getUrl(80, 80))//
			.into(postLikeViewHolder.mFeedImg);

			if (postLikeNotify2.getUser2s().size() == 1) {
				// * 說你的相片讃
				String format = String.format(mParent.getResources().getString(R.string.title_activity_post_like_title),//
												postLikeNotify2.getUser2s().get(0).getName());
				SpannableString ss = new SpannableString(format);
				ss.setSpan(new StyleSpan(Typeface.BOLD), 0, postLikeNotify2.getUser2s().get(0).getName().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				postLikeViewHolder.mTitle.setText(ss);
			} else if (postLikeNotify2.getUser2s().size() == 2) {
				// * 以及 * 說你的相片讃
				String format = String.format(mParent.getResources().getString(R.string.title_activity_post_like_title_two),//
												postLikeNotify2.getUser2s().get(0).getName());
				// , postLikeNotify2.getUsers().get(1).getName()
				String format2 = mParent.getString(R.string.title_activity_post_like_title_middle);
				String format3 = String.format(mParent.getResources().getString(R.string.title_activity_post_like_title_end),//
												postLikeNotify2.getUser2s().get(1).getName());
				int oneNameLength = postLikeNotify2.getUser2s().get(0).getName().length();

				StringBuilder stringBuilder = new StringBuilder(format);
				stringBuilder.append(format2);
				stringBuilder.append(format3);

				SpannableString ss = new SpannableString(stringBuilder.toString());
				// 加粗第一个人的名字
				ss.setSpan(new StyleSpan(Typeface.BOLD), 0, oneNameLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				ss.setSpan(new StyleSpan(Typeface.BOLD), oneNameLength + format2.length(), oneNameLength + format2.length() + //
				postLikeNotify2.getUser2s().get(1).getName().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

				postLikeViewHolder.mTitle.setText(ss);
			} else {
				String format = String.format(mParent.getResources().getString(R.string.title_activity_post_like_title_more),//
												postLikeNotify2.getUser2s().get(0).getName());

				String format2 = String.format(mParent.getResources().getString(R.string.title_activity_post_like_title_more_end),//
												String.valueOf(postLikeNotify2.getUser2s().size() - 1));
				int oneNameLength = format.length();
				StringBuilder stringBuilder = new StringBuilder(format);
				stringBuilder.append(format2);

				SpannableString ss = new SpannableString(stringBuilder.toString());
				ss.setSpan(new StyleSpan(Typeface.BOLD), 0, postLikeNotify2.getUser2s().get(0).getName().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				ss.setSpan(new StyleSpan(Typeface.BOLD), oneNameLength, oneNameLength + String.valueOf(String.valueOf(postLikeNotify2.getUser2s().size() - 1)).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

				postLikeViewHolder.mTitle.setText(ss);
			}
			// postLikeViewHolder.mTitle.setText();
			wrapUsers(notify2, null, postLikeViewHolder);
			postLikeViewHolder.mFeedImg.setOnClickListener(new OnClickListener(postLikeNotify2));
			break;
		case Notify2.POST_COMMENT_TAG_TYPE:// feed被 评论
			final PostCommentNotify2 postCommentNotify2 = (PostCommentNotify2) notify2;
			postCommentViewHolder.mCommentBody.setText(postCommentNotify2.getComment());
			postCommentViewHolder.mUserName.setText(postCommentNotify2.getFromUser2().getName());
			postCommentViewHolder.mStamp.setText(TimeUtil.getDistanceTimeForApp(mParent, //
																				new Date().getTime(), postCommentNotify2.getPost().getCreateTimeMillis()));
			picasso.load(postCommentNotify2.getFromUser2().getAvatarCommonImage().getUrlSquare(120)).//
			placeholder(R.drawable.default_photo).into(postCommentViewHolder.mUserHead);
			picasso.load(postCommentNotify2.getPost().getCommonImage().getUrl(80, 80)).//
			into(postCommentViewHolder.mFeedImg);

			postCommentViewHolder.mUserHead.setOnClickListener(new OnClickListener(postCommentNotify2));
			postCommentViewHolder.mFeedImg.setOnClickListener(new OnClickListener(postCommentNotify2));
			fontUtil.changeFonts((ViewGroup) convertView, Font.ENCODESANSCOMPRESSED_400_REGULAR);
			fontUtil.changeViewFont(postCommentViewHolder.mStamp, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
			fontUtil.changeViewFont(postCommentViewHolder.mUserName, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
			break;
		case Notify2.POST_TAG_VIEW_TYPE:// 圈人的通知
			PostTagNotify2 postTagNotify2 = (PostTagNotify2) notify2;
			picasso.load(postTagNotify2.getFromUser2().getAvatarCommonImage().getUrl(100, 100)).//
			placeholder(R.drawable.default_photo).into(postTagViewHolder.mUserHead);
			picasso.load(postTagNotify2.getPost().getCommonImage().getUrl(100, 100)).//
			into(postTagViewHolder.mFeedImg);
			String format = String.format(mParent.getResources().getString(R.string.item_feed_notice_post_tag_body),//
											postTagNotify2.getFromUser2().getName());

			SpannableString ss = new SpannableString(format);
			ss.setSpan(new StyleSpan(Typeface.BOLD), 0, postTagNotify2.getFromUser2().getName().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

			postTagViewHolder.mUserName.setText(ss);
			postTagViewHolder.mStamp.setText(TimeUtil.getDistanceTimeForApp(mParent, //
																			new Date().getTime(), postTagNotify2.getPost().getCreateTimeMillis()));
			postTagViewHolder.mFeedImg.setOnClickListener(new OnClickListener(postTagNotify2));
			postTagViewHolder.mUserHead.setOnClickListener(new OnClickListener(postTagNotify2));
			break;
		case Notify2.NEW_ALOHA_VIEW_TYPE:// 人气的通知
			NewAlohaNotify2 newAlohaNotify2 = (NewAlohaNotify2) notify2;
			String string = String.format(mParent.getResources().getString(R.string.item_feed_notice_new_aloha_body),//
											newAlohaNotify2.getUser2s().size());
			SpannableString newAlohaSS = new SpannableString(string);
			newAlohaSS.setSpan(new StyleSpan(Typeface.BOLD), 0, String.valueOf(newAlohaNotify2.getUser2s().size()).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			newAlohaViewHolder.mTitle.setText(newAlohaSS);
			newAlohaViewHolder.mStamp.setText(TimeUtil.getDistanceTimeForApp(mParent, //
																				new Date().getTime(), newAlohaNotify2.getUpdateTimeMillis()));
			wrapUsers(notify2, newAlohaViewHolder, null);
			break;
		}

		return convertView;
	}

	/**
	 * @Description:計算界面
	 * @see:
	 * @since:
	 * @description
	 * @author: sunkist
	 * @param newAlohaViewHolder
	 * @param newAlohaNotify2
	 * @date:2015年2月27日
	 */
	public int mUserIconSize;
	public final int mUserIconPaddingLeft = 10;
	private int count;
	private LayoutParams layoutParams2;

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

		List<User2> user2s = null;
		int size = 0;
		if (notify2 instanceof PostLikeNotify2) {
			postLikeViewHolder.mWrapUsers.removeAllViews();
			user2s = ((PostLikeNotify2) notify2).getUser2s();
			size = user2s.size();
			for (int i = 0; i < size; i++) {
				if (i < count) {
					CircleImageView circleImageView = new CircleImageView(mParent);
					circleImageView.setLayoutParams(layoutParams2);
					picasso.load(user2s.get(i).getAvatarCommonImage().getUrlSquare(80)).placeholder(R.drawable.default_photo)//
					.into(circleImageView);
					postLikeViewHolder.mWrapUsers.addView(circleImageView);
					circleImageView.setClickable(true);
					final User2 user2 = user2s.get(i);
					circleImageView.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							Bundle bundle = new Bundle();
							bundle.putSerializable(User.TAG, DockingBeanUtils.transUser(user2));
							mParent.startFragment(Profile2Fragment.class, bundle, true);
						}
					});
				} else {
					break;
				}
			}
		} else if (notify2 instanceof NewAlohaNotify2) {
			newAlohaViewHolder.mWrapUsers.removeAllViews();
			user2s = ((NewAlohaNotify2) notify2).getUser2s();
			size = user2s.size();
			for (int i = 0; i < size; i++) {
				if (i < count) {
					CircleImageView circleImageView = new CircleImageView(mParent);
					circleImageView.setLayoutParams(layoutParams2);
					picasso.load(user2s.get(i).getAvatarCommonImage().getUrlSquare(80)).//
					placeholder(R.drawable.default_photo).into(circleImageView);
					newAlohaViewHolder.mWrapUsers.addView(circleImageView);
				} else {
					break;
				}
			}
		}
	}

	static class BaseViewHolder {

	}

	/**
	 * @author:sunkist
	 * @see:
	 * @since:
	 * @description 评论通知
	 * @copyright wealoha.com
	 * @Date:2015年2月27日
	 */
	static class PostCommentViewHolder extends BaseViewHolder {

		public CircleImageView mUserHead;
		public TextView mUserName;
		public TextView mCommentBody;
		public ImageView mFeedImg;
		public TextView mStamp;

		public PostCommentViewHolder(View view, FeedNoticeAct mParent) {
			mUserHead = (CircleImageView) view.findViewById(R.id.item_feed_notice_comment_userhead_cv);
			mUserName = (TextView) view.findViewById(R.id.item_feed_notice_comment_username_tv);
			mCommentBody = (TextView) view.findViewById(R.id.item_feed_notice_comment_body_tv);
			mFeedImg = (ImageView) view.findViewById(R.id.item_feed_notice_img_iv);
			mStamp = (TextView) view.findViewById(R.id.item_feed_notice_time_stamp);
			ButterKnife.inject(this, view);
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
	static class PostLikeViewHolder extends BaseViewHolder {

		public LinearLayout mWrapUsers;
		public int mImgCount;
		public int mUserIconSize = 80;
		BaseFragAct mBaseFragAct;
		public CircleImageView mUserHead;
		public TextView mTimeStamp;
		public TextView mTitle;
		public ImageView mFeedImg;

		public PostLikeViewHolder(View convertView, FeedNoticeAct mParent) {
			mFeedImg = (ImageView) convertView.findViewById(R.id.item_feed_notice_post_like_img_iv);
			mTitle = (TextView) convertView.findViewById(R.id.item_feed_notice_post_like_title_tv);
			mTimeStamp = (TextView) convertView.findViewById(R.id.item_feed_notice_post_like_stamp_tv);
			mWrapUsers = (LinearLayout) convertView.findViewById(R.id.wrap_user_icon_ll);
			mBaseFragAct = mParent;
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
	static class PostTagViewHolder extends BaseViewHolder {

		public CircleImageView mUserHead;
		public TextView mUserName;
		public TextView mStamp;
		public ImageView mFeedImg;

		public PostTagViewHolder(View view, FeedNoticeAct mParent) {
			mUserHead = (CircleImageView) view.findViewById(R.id.item_feed_notice_post_tag_userhead_cv);
			mUserName = (TextView) view.findViewById(R.id.item_feed_notice_post_tag_username_tv);
			mStamp = (TextView) view.findViewById(R.id.item_feed_notice_post_tag_stamp);
			mFeedImg = (ImageView) view.findViewById(R.id.item_feed_notice_post_tag_img_iv);
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
	static class NewAlohaViewHolder extends BaseViewHolder {

		public TextView mTitle;
		public TextView mStamp;
		public LinearLayout mWrapUsers;

		public NewAlohaViewHolder(View view, FeedNoticeAct mParent, Notify2 notify2) {
			mTitle = (TextView) view.findViewById(R.id.item_feed_notice_new_aloha_title_tv);
			mStamp = (TextView) view.findViewById(R.id.item_feed_notice_new_aloha_stamp_tv);
			mWrapUsers = (LinearLayout) view.findViewById(R.id.item_feed_notice_new_aloha_wrap_users_ll);
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
			Bundle bundle = null;
			String postid = null;
			int feedtyep = FeedFragment.FEED_TYPE_SINGLE;
			switch (v.getId()) {
			case R.id.item_feed_notice_img_iv:// 被评论的Feed图片
				postid = ((PostCommentNotify2) mNotify2).getPost().getPostId();
				break;
			case R.id.item_feed_notice_post_like_img_iv:// 被点赞的Feed图片
				postid = ((PostLikeNotify2) mNotify2).getPost().getPostId();
				break;
			case R.id.item_feed_notice_post_tag_img_iv:// 圈人被圈的Feed图片点击后跳转到圈人Feed
				postid = ((PostTagNotify2) mNotify2).getPost().getPostId();
				break;
			case R.id.item_feed_notice_comment_userhead_cv:// 评论Feed的User头像
				bundle = new Bundle();
				bundle.putSerializable(User.TAG, DockingBeanUtils.transUser(((PostCommentNotify2) mNotify2).getFromUser2()));
				mParent.startFragment(Profile2Fragment.class, bundle, true);
				return;
			case R.id.item_feed_notice_post_tag_userhead_cv:// 圈人的用户头像
				return;
			}
			startSingleTagsFeed(postid, feedtyep);
		}
	}

	/**
	 * @Title: startSingleTagsFeed
	 * @Description: 打开的单例feed
	 */
	private void startSingleTagsFeed(String postid, int feedFragType) {
		if (mParent != null) {
			// mParent.startSingleTagsFeed(postid, feedFragType);
		}
	}
}
