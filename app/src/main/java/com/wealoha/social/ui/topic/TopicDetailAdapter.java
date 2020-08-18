package com.wealoha.social.ui.topic;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.squareup.picasso.Picasso;
import com.wealoha.social.AppApplication;
import com.wealoha.social.BaseFragAct;
import com.wealoha.social.R;
import com.wealoha.social.api.feed.bean.FeedType;
import com.wealoha.social.api.post.bean.Post;
import com.wealoha.social.api.post.bean.TopicPost;
import com.wealoha.social.api.post.bean.TopicPosts;
import com.wealoha.social.commons.GlobalConstants;
import com.wealoha.social.fragment.FeedCommentFragment;
import com.wealoha.social.fragment.Profile2Fragment;
import com.wealoha.social.inject.Injector;
import com.wealoha.social.utils.FontUtil;
import com.wealoha.social.utils.FontUtil.Font;
import com.wealoha.social.utils.UiUtils;
import com.wealoha.social.utils.XL;

public class TopicDetailAdapter extends BaseAdapter implements OnClickListener {

	private Context mCtx;
	// private TopicPosts mTopicPosts;
	private TopicPosts mTopicPosts;
	@Inject
	Picasso mPicasso;
	public static int IMG_WIDTH;
	private static int color;
	private BaseFragAct baseFragAct;
	private int mWidth;

	public TopicDetailAdapter(Context ctx, BaseFragAct baseFragAct, TopicPosts topicPosts) {
		mCtx = ctx.getApplicationContext();
		this.baseFragAct = baseFragAct;

		mWidth = UiUtils.getScreenWidth(mCtx);
		int divider = (int) mCtx.getResources().getDimension(R.dimen.profile_list_divider);
		IMG_WIDTH = (int) Math.floor((mWidth - divider * 2) / 3.0);

		mTopicPosts = new TopicPosts();
		mTopicPosts.setPosts(new ArrayList<TopicPost>());
		if (topicPosts != null) {
			mTopicPosts.getPosts().addAll(topicPosts.getPosts());
		}
		color = mCtx.getResources().getColor(R.color.light_gray);
		Injector.inject(this);
	}

	@Inject
	FontUtil fontUtil;

	@Override
	public int getCount() {
		return mTopicPosts != null && mTopicPosts.getPosts() != null ? mTopicPosts.getPosts().size() : 0;
	}

	@Override
	public TopicPost getItem(int position) {
		return mTopicPosts.getPosts().get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemViewType(int position) {
		return mTopicPosts.getPosts().get(position).getItemType();
	}

	@Override
	public int getViewTypeCount() {
		return 5;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TopicPost topicPost = getItem(position);
		ViewHolder viewHolder = null;
		TitleViewHolder titleHolder = null;
		if (convertView == null) {
			switch (getItemViewType(position)) {
			case TopicPost.TITLE_TYPE:
				convertView = View.inflate(mCtx, R.layout.textview_title, null);
				fontUtil.changeViewFont(convertView, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
				titleHolder = new TitleViewHolder(convertView);
				convertView.setTag(titleHolder);
				break;
			case TopicPost.NORMAL_TYPE:
				convertView = View.inflate(mCtx, R.layout.item_topic_detail, null);
				viewHolder = new ViewHolder(convertView);
				convertView.setTag(viewHolder);
				break;
			}
		} else {
			switch (getItemViewType(position)) {
			case TopicPost.TITLE_TYPE:
				titleHolder = (TitleViewHolder) convertView.getTag();
				break;
			case TopicPost.NORMAL_TYPE:
				viewHolder = (ViewHolder) convertView.getTag();
				break;
			}
		}

		switch (getItemViewType(position)) {
		case TopicPost.TITLE_TYPE:
			titleHolder.mTitleColumn.setText(topicPost.getTitleId());
			break;
		case TopicPost.NORMAL_TYPE:
			shwoColumn(topicPost, viewHolder, convertView);
			break;
		}
		return convertView;
	}

	/**
	 * @Description:展示普通的Column
	 * @param topicPost
	 * @param viewHolder
	 * @see:
	 * @since:
	 * @description
	 * @author: sunkist
	 * @param convertView
	 * @date:2015年7月28日
	 */
	private void shwoColumn(TopicPost topicPost, ViewHolder viewHolder, View convertView) {
		List<Post> posts = topicPost.getPostsItem();
		for (int i = 0; i < posts.size(); i++) {
			Post post = posts.get(i);
			ImageView iv = null;
			if (i == 0) {
				iv = viewHolder.mTopicPhotoBefore;
				viewHolder.mTopicPhotoBefore.setTag(post);
				iv.setVisibility(View.VISIBLE);
				viewHolder.mTopicPlayBefore.setVisibility(FeedType.VideoPost == (post == null ? null : post.getType()) ? View.VISIBLE : View.GONE);
			} else if (i == 1) {
				iv = viewHolder.mTopicPhotoMiddle;
				viewHolder.mTopicPhotoMiddle.setTag(post);
				iv.setVisibility(View.VISIBLE);
				viewHolder.mTopicPlayMiddle.setVisibility(FeedType.VideoPost == (post == null ? null : post.getType()) ? View.VISIBLE : View.GONE);
			} else if (i == 2) {
				iv = viewHolder.mTopicPhotoAfter;
				viewHolder.mTopicPhotoAfter.setTag(post);
				iv.setVisibility(View.VISIBLE);
				viewHolder.mTopicPlayAfter.setVisibility(FeedType.VideoPost == (post == null ? null : post.getType()) ? View.VISIBLE : View.GONE);
			}
			if (post != null) {
				mPicasso.load(post.getImage().getUrlSquare(IMG_WIDTH)).resize(IMG_WIDTH, IMG_WIDTH).placeholder(R.color.gray_text).into(iv);
				iv.setOnClickListener(this);
			} else {
				iv.setVisibility(View.INVISIBLE);
			}
		}
	}

	public void fillSingleImg(ImageView topicContentImg, ImageView topicPlayIcon, Post post) {
		mPicasso.load(post.getImage().getUrlSquare(IMG_WIDTH)).resize(IMG_WIDTH, IMG_WIDTH).placeholder(R.color.gray_text).into(topicContentImg);
		topicContentImg.setOnClickListener(this);
	}

	public static class ViewHolder {

		@InjectView(R.id.item_topic_detail_before)
		public ImageView mTopicPhotoBefore;
		@InjectView(R.id.item_topic_detail_middle)
		public ImageView mTopicPhotoMiddle;
		@InjectView(R.id.item_topic_detail_after)
		public ImageView mTopicPhotoAfter;

		@InjectView(R.id.topic_detail_play_before)
		public ImageView mTopicPlayBefore;
		@InjectView(R.id.topic_detail_play_middle)
		public ImageView mTopicPlayMiddle;
		@InjectView(R.id.topic_detail_play_after)
		public ImageView mTopicPlayAfter;

		@InjectView(R.id.item_topic_detail_wrap_before)
		public RelativeLayout mTopicPhotoWrapBefore;
		@InjectView(R.id.item_topic_detail_wrap_middle)
		public RelativeLayout mTopicPhotoWrapMiddle;
		@InjectView(R.id.item_topic_detail_wrap_after)
		public RelativeLayout mTopicPhotoWrapAfter;

		public ViewHolder(View view) {
			ButterKnife.inject(this, view);
			mTopicPhotoWrapBefore.getLayoutParams().width = IMG_WIDTH;
			mTopicPhotoWrapBefore.getLayoutParams().height = IMG_WIDTH;
			mTopicPhotoWrapMiddle.getLayoutParams().width = IMG_WIDTH;
			mTopicPhotoWrapMiddle.getLayoutParams().height = IMG_WIDTH;
			mTopicPhotoWrapAfter.getLayoutParams().width = IMG_WIDTH;
			mTopicPhotoWrapAfter.getLayoutParams().height = IMG_WIDTH;
		}
	}

	public static class TitleViewHolder {

		public TextView mTitleColumn;

		public TitleViewHolder(View convertView) {
			mTitleColumn = (TextView) convertView.findViewById(R.id.titile_tv);
		}
	}

	@Override
	public void onClick(View v) {
		Post post = null;
		switch (v.getId()) {
		case R.id.item_topic_detail_before:
			post = (Post) v.getTag();
			break;
		case R.id.item_topic_detail_middle:
			post = (Post) v.getTag();
			break;
		case R.id.item_topic_detail_after:
			post = (Post) v.getTag();
			break;
		default:
			break;
		}
		Bundle bundle = new Bundle();
		post = (Post) v.getTag();
		if (post == null) {
			return;
		}
		bundle.putString(GlobalConstants.TAGS.TOPIC_DETAIL_TAG, GlobalConstants.TAGS.TOPIC_DETAIL_TAG);
		bundle.putSerializable(GlobalConstants.TAGS.POST_TAG, post);
		// bundle.putSerializable(com.wealoha.social.api.user.bean.User.TAG,post.getUser());
		baseFragAct.startFragmentForResult(FeedCommentFragment.class,//
											bundle,//
											true,//
											Profile2Fragment.OPEN_SINGLETO_FEED_REQUESTCODE,//
											R.anim.left_in, R.anim.stop);
	}

	public void notifyDataSetChanged(TopicPosts topicPosts) {
		if (topicPosts.getPosts() == null) {
			mTopicPosts.setPosts(new ArrayList<TopicPost>());
		}
		mTopicPosts.getPosts().clear();
		mTopicPosts.getPosts().addAll(topicPosts.getPosts());
		notifyDataSetChanged();
	}
}
