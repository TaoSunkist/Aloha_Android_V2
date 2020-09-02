package com.wealoha.social.widget.feed;

import javax.inject.Inject;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.InjectView;

import com.squareup.picasso.Picasso;
import com.wealoha.social.R;
import com.wealoha.social.beans.FeedType;
import com.wealoha.social.beans.Post;
import com.wealoha.social.ui.feeds.IFeedsViewV2;
import com.wealoha.social.utils.UiUtils;
import com.wealoha.social.view.custom.CircleImageView;
import com.wealoha.social.widget.BaseLayout;

public class PostLayout extends BaseLayout {


	@InjectView(R.id.content_root)
	RelativeLayout contentRootView;
	@InjectView(R.id.post_image)
	ImageView postImageView;
	@InjectView(R.id.post_video)
	SurfaceView postSurfaceView;
	@InjectView(R.id.user_photo)
	CircleImageView userPhotoView;
	@InjectView(R.id.user_name)
	TextView userNameView;
	@InjectView(R.id.time_stamp)
	TextView timeStampView;
	@InjectView(R.id.user_location)
	TextView userLocationView;
	@InjectView(R.id.introduction)
	TextView introductionView;

	private int screenWidth;
	private Post mPost;
	private Context mContext;
	private IFeedsViewV2 mFeedView;

	public PostLayout(Context context, IFeedsViewV2 feedView, Post post) {
		super(context, R.layout.post_view_layout);
		screenWidth = UiUtils.getScreenWidth(context);
		mPost = post;
		mFeedView = feedView;
		// initView(mPost);
		initViewData(mPost);
	}

	public PostLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public PostLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	private void initView(Post post) {
		if (post.getType() == FeedType.ImagePost) {

			postSurfaceView.setVisibility(View.GONE);
			postImageView.setVisibility(View.VISIBLE);
			postImageView.getLayoutParams().height = screenWidth;

		} else if (post.getType() == FeedType.VideoPost) {

			postImageView.setVisibility(View.GONE);
			postSurfaceView.setVisibility(View.VISIBLE);
			postSurfaceView.getLayoutParams().height = screenWidth;
		}

	}

	public void initViewData(Post post) {
		Picasso.get().load(post.getCommonImage().getUrlSquare(screenWidth)).into(postImageView);
		Picasso.get().load(post.getUser().getAvatarImage().getUrlSquare(screenWidth)).into(userPhotoView);

		// userNameView.setText(post.getUser().getName());
		// timeStampView.setText(TimeUtil.howLong(mContext, post.getCreateTimeMillis()));
		// setTextorGone(mPost.getVenue(), userLocationView);
		// mPost.getHashTag().getName() + " " + mPost.getDescription();
		// setTextorGone(mPost.getDescription(), introductionView);
	}

	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		String id = "";
		if (mPost != null) {
			id = mPost.getPostId();
		}
		mFeedView.postViewScroll(top, bottom, id);
	};
}
