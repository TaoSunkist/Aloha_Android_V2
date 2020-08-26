package com.wealoha.social.adapter.profile;

import java.util.List;

import javax.inject.Inject;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.squareup.picasso.Picasso;
import com.wealoha.social.BaseFragAct;
import com.wealoha.social.R;
import com.wealoha.social.adapter.feed.AbsViewHolder;
import com.wealoha.social.beans.FeedType;
import com.wealoha.social.beans.Post;
import com.wealoha.social.commons.GlobalConstants;
import com.wealoha.social.fragment.FeedCommentFragment;
import com.wealoha.social.fragment.Profile2Fragment;
import com.wealoha.social.inject.Injector;
import com.wealoha.social.utils.ContextUtil;
import com.wealoha.social.utils.UiUtils;

public class Profile2ImageHolder extends AbsViewHolder implements OnClickListener {

	@Inject
	Picasso picasso;
	@Inject
	Context context;
	@Inject
	ContextUtil contextUtil;

	@InjectView(R.id.profile_pics_image01)
	ImageView image01;
	@InjectView(R.id.profile_pics_image02)
	ImageView image02;
	@InjectView(R.id.profile_pics_image03)
	ImageView image03;
	@InjectView(R.id.profile_pics_image_rl01)
	RelativeLayout profile_pics_image_rl01;
	@InjectView(R.id.profile_pics_image_rl02)
	RelativeLayout profile_pics_image_rl02;
	@InjectView(R.id.profile_pics_image_rl03)
	RelativeLayout profile_pics_image_rl03;
	@InjectView(R.id.profile_play_image01)
	ImageView profile_play_image01;
	@InjectView(R.id.profile_play_image02)
	ImageView profile_play_image02;
	@InjectView(R.id.profile_play_image03)
	ImageView profile_play_image03;

	private ViewGroup container;
	private List<Post> mPostList;
	private int mWidth;
	private int imgWidth;
	private Fragment mFrag;
	public static final int IMG_COUNT = 3;
	private int holderPosition;
	public static final String HOLDER_POSITION_KEY = "HOLDER_POSITION_KEY";

	public Profile2ImageHolder(List<Post> items, ViewGroup parent, Fragment frag, int position) {
		Injector.inject(this);
		holderPosition = position;
		container = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.profile_pics_content, parent, false);
		ButterKnife.inject(this, container);
		this.mPostList = items;
		mWidth = UiUtils.getScreenWidth(context);
		mFrag = frag;
		// 初始化图片大小
		int divider = (int) context.getResources().getDimension(R.dimen.profile_list_divider);
		imgWidth = (int) Math.floor((mWidth - divider * 2) / 3.0);
		initView();
		initData();
	}

	/**
	 * 更新数据
	 * 
	 * @param oneRowFeed
	 */
	public void updateData(List<Post> oneRowFeed, int position) {
		this.mPostList = oneRowFeed;
		holderPosition = position;
		initData();
	}

	private void initView() {
		profile_pics_image_rl01.getLayoutParams().height = imgWidth;
		profile_pics_image_rl01.getLayoutParams().width = imgWidth;
		profile_pics_image_rl02.getLayoutParams().height = imgWidth;
		profile_pics_image_rl02.getLayoutParams().width = imgWidth;
		profile_pics_image_rl03.getLayoutParams().height = imgWidth;
		profile_pics_image_rl03.getLayoutParams().width = imgWidth;
	}

	private void initData() {
		// 先藏起来，避免不够三个多出来以前的
		profile_pics_image_rl01.setVisibility(ViewStub.GONE);
		profile_pics_image_rl02.setVisibility(ViewStub.GONE);
		profile_pics_image_rl03.setVisibility(ViewStub.GONE);
		for (int i = 0; i < (IMG_COUNT > mPostList.size() ? mPostList.size() : IMG_COUNT); i++) {
			if (mPostList.get(i) != null) {

				if (i == 0) {
					loadPhotos(image01, profile_play_image01, profile_pics_image_rl01, i);
				} else if (i == 1) {
					loadPhotos(image02, profile_play_image02, profile_pics_image_rl02, i);
				} else if (i == 2) {
					loadPhotos(image03, profile_play_image03, profile_pics_image_rl03, i);
				}

			} else {
				continue;
			}
		}
	}

	private void loadPhotos(ImageView image, ImageView profile_play_image, RelativeLayout profile_pics_image_rl, int position) {
		Post post = mPostList.get(position);
		profile_play_image.setVisibility(FeedType.VideoPost==post.getType()?View.VISIBLE:View.GONE);
		profile_pics_image_rl.setVisibility(ViewStub.VISIBLE);
		picasso.load(post.getUser().getAvatarImage().getUrl()).resize(imgWidth, imgWidth).placeholder(R.color.gray_text).into(image);
		profile_pics_image_rl.setTag(post);
		profile_pics_image_rl.setOnClickListener(this);
	}

	// private void loadPhotos(ImageView image, int position) {
	// // Log.i("PROFILE_IMAGES", "POSITION:" + position);
	// image.setVisibility(ViewStub.VISIBLE);
	// picasso.load(mPostList.get(position).getImage().getUrlSquare(imgWidth)).resize(imgWidth,
	// imgWidth).placeholder(R.color.gray_text).into(image);
	// image.setTag(mPostList.get(position));
	// image.setOnClickListener(this);
	// }

	public ViewGroup getView() {
		return container;
	}

	@Override
	public void onClick(View v) {
		Bundle bundle = new Bundle();
		Post post = (Post) v.getTag();
		if (post != null) {
			bundle.putSerializable(GlobalConstants.TAGS.POST_TAG, post);
		}
		int position = holderPosition * 3;
		if (v.getId() == R.id.profile_pics_image_rl01) {
			position += 0;
		} else if (v.getId() == R.id.profile_pics_image_rl02) {
			position += 1;
		} else if (v.getId() == R.id.profile_pics_image_rl03) {
			position += 2;
		}
		bundle.putString(GlobalConstants.TAGS.TOPIC_DETAIL_TAG, GlobalConstants.TAGS.TOPIC_DETAIL_TAG);//控制留言列表刷新
		bundle.putInt(HOLDER_POSITION_KEY, position);
		((BaseFragAct) mFrag.getActivity()).startFragmentForResult(FeedCommentFragment.class,//
																	bundle,//
																	true,//
																	Profile2Fragment.OPEN_SINGLETO_FEED_REQUESTCODE,//
																	R.anim.left_in, R.anim.stop);
	}
}
