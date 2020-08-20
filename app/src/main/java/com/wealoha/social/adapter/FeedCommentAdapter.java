package com.wealoha.social.adapter;

import java.util.Date;

import javax.inject.Inject;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.InjectView;

import com.squareup.picasso.Picasso;
import com.wealoha.social.R;
import com.wealoha.social.adapter.feed.AbsViewHolder;
import com.wealoha.social.api.comment.bean.PostComment;
import com.wealoha.social.api.comment.service.Comment2Service;
import com.wealoha.social.beans.User2;
import com.wealoha.social.commons.GlobalConstants.ImageSize;
import com.wealoha.social.utils.FontUtil;
import com.wealoha.social.utils.TimeUtil;
import com.wealoha.social.view.custom.CircleImageView;
import com.wealoha.social.widget.BaseAdapterHolder;
import com.wealoha.social.widget.BaseListApiAdapter;
import com.wealoha.social.widget.MultiListViewType;

public class FeedCommentAdapter extends BaseListApiAdapter<PostComment, String> {

	private Context mCtx;
	@Inject
	Picasso picasso;
	@Inject
	FontUtil fontUtil;

	protected FeedCommentAdapterCallback callback;
	private Drawable whisperDrawable;

	public interface FeedCommentAdapterCallback {

		public void openSomeoneProfile(User2 user2);

		public void showmPrivacyCommentSign();
	}

	public FeedCommentAdapter(Context ctx, Comment2Service mComment2Service, FeedCommentAdapterCallback callback) {
		super(mComment2Service);
		mCtx = ctx;
		this.callback = callback;
		whisperDrawable = mCtx.getResources().getDrawable(R.drawable.message_lock);
		whisperDrawable.setBounds(0, 0, whisperDrawable.getMinimumWidth(), whisperDrawable.getMinimumHeight());
	}

	@Override
	protected BaseAdapterHolder newViewHolder(MultiListViewType type, PostComment item, LayoutInflater inflater, ViewGroup parent) {
		View view = inflater.inflate(R.layout.item_feed_comment, parent, false);
		FeedCommentViewHolder feedCommentViewHolder = new FeedCommentViewHolder(view);
		return feedCommentViewHolder;
	}

	@Override
	protected <V extends AbsViewHolder> void fillView(V holder, PostComment item, int position, View convertView) {
		FeedCommentViewHolder feedCommentViewHolder = (FeedCommentViewHolder) holder;

		FontUtil.setSemiBoldTypeFace(mCtx, feedCommentViewHolder.mTimeStamp);
		FontUtil.setSemiBoldTypeFace(mCtx, feedCommentViewHolder.mUserName);
		FontUtil.setRegulartypeFace(mCtx, feedCommentViewHolder.mCommentBody);
		// fontUtil.changeViewFont(feedCommentViewHolder.mTimeStamp,
		// Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
		// fontUtil.changeViewFont(feedCommentViewHolder.mUserName,
		// Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
		// fontUtil.changeViewFont(feedCommentViewHolder.mCommentBody,
		// Font.ENCODESANSCOMPRESSED_500_MEDIUM);
		picasso.load(item.getUser2().getAvatarCommonImage().getUrl(ImageSize.AVATAR_ROUND_SMALL, ImageSize.AVATAR_ROUND_SMALL)).placeholder(R.drawable.default_photo).into(feedCommentViewHolder.mUserHead);

		TextView timeStampTv = feedCommentViewHolder.mTimeStamp;
		if (item.isWhisper()) {
			timeStampTv.setCompoundDrawables(whisperDrawable, null, null, null);
			if (callback != null) {// 显示 仅双方可见的留言提醒
				callback.showmPrivacyCommentSign();
			}
		} else {
			timeStampTv.setCompoundDrawables(null, null, null, null);
			timeStampTv.refreshDrawableState();
		}
		timeStampTv.setText(TimeUtil.getDistanceTimeForApp(mCtx, //
															new Date().getTime(), item.getCreateTimeMillis()));

		feedCommentViewHolder.mUserName.setText(item.getUser2().getName());

		String commentHint = null;
		if (item.getReplyUser2() != null) {
			commentHint = mCtx.getString(R.string.comment_in_reply, item.getReplyUser2().getName(), item.getComment());
		} else {
			commentHint = item.getComment();
		}
		feedCommentViewHolder.mCommentBody.setText(commentHint);

		final PostComment finalItem = item;
		feedCommentViewHolder.mUserHead.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				callback.openSomeoneProfile(finalItem.getUser2());
			}
		});
	}

	static class FeedCommentViewHolder extends BaseAdapterHolder {

		@InjectView(R.id.item_feed_comment_userhead_cv)
		CircleImageView mUserHead;
		@InjectView(R.id.item_feed_comment_time_stamp_tv)
		TextView mTimeStamp;
		@InjectView(R.id.item_feed_comment_username_tv)
		TextView mUserName;
		@InjectView(R.id.item_feed_notice_comment_body_tv)
		TextView mCommentBody;

		public FeedCommentViewHolder(View view) {
			super(view);
		}

	}

}
