package com.wealoha.social.view.custom.listitem;

import java.util.List;

import javax.inject.Inject;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.wealoha.social.beans.Feed;
import com.wealoha.social.fragment.FeedFragment;
import com.wealoha.social.fragment.ProfileTestFragment;
import com.wealoha.social.inject.Injector;
import com.wealoha.social.utils.ContextUtil;
import com.wealoha.social.utils.ImageUtil;
import com.wealoha.social.utils.ImageUtil.CropMode;
import com.wealoha.social.utils.UiUtils;

public class ProfileImagesHolder implements OnClickListener {

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
	private List<Feed> mItems;
	private int mWidth;
	private int imgWidth;
	public static final int IMG_COUNT = 3;

	public ProfileImagesHolder(List<Feed> items, ViewGroup parent) {
		Injector.inject(this);
		container = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.profile_pics_content, parent, false);
		ButterKnife.inject(this, container);
		this.mItems = items;
		mWidth = UiUtils.getScreenWidth(context);
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
	public void updateData(List<Feed> oneRowFeed) {
		this.mItems = oneRowFeed;
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
		for (int i = 0; i < (IMG_COUNT > mItems.size() ? mItems.size() : IMG_COUNT); i++) {
			if (mItems.get(i) != null) {

				if (i == 0) {
					loadPhotos(image01, profile_pics_image_rl01, i);
				} else if (i == 1) {
					loadPhotos(image02, profile_pics_image_rl02, i);
				} else if (i == 2) {
					loadPhotos(image03, profile_pics_image_rl03, i);
				}

			} else {
				continue;
			}
		}
	}

	private void loadPhotos(ImageView image, RelativeLayout profile_pics_image_rl, int position) {
		// Log.i("PROFILE_IMAGES", "POSITION:" + position);
		Feed feed = mItems.get(position);
		if ("VideoType".equals(feed.getType())) {
			
		}
		profile_pics_image_rl.setVisibility(ViewStub.VISIBLE);
		picasso.load(ImageUtil.getImageUrl(feed.imageId, 240, CropMode.ScaleCenterCrop)).resize(imgWidth, imgWidth).placeholder(R.color.gray_text).into(image);
		profile_pics_image_rl.setTag(mItems.get(position).postId);
		profile_pics_image_rl.setOnClickListener(this);
	}

	public ViewGroup getView() {
		return container;
	}

	@Override
	public void onClick(View v) {
		Bundle bundle = new Bundle();
		String postid = v.getTag().toString();
		if (!TextUtils.isEmpty(postid)) {
			bundle.putInt(FeedFragment.FEED_TYPE, FeedFragment.FEED_TYPE_SINGLE);
			bundle.putString(FeedFragment.SINGLE_FEED_POST_ID, postid);
		}
		if (contextUtil.getForegroundAct() != null) {
			// ((BaseFragAct)
			// contextUtil.getForegroundAct()).startFragment(FeedFragment.class,
			// bundle, true);
			((BaseFragAct) contextUtil.getForegroundAct()).startFragmentForResult(FeedFragment.class,//
					bundle,//
					true,//
					ProfileTestFragment.OPEN_SINGLETO_FEED_REQUESTCODE,//
					R.anim.left_in, R.anim.stop);
		}
	}
}
