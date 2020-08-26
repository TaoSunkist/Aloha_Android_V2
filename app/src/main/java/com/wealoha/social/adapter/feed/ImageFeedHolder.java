package com.wealoha.social.adapter.feed;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.wealoha.social.R;
import com.wealoha.social.beans.UserTag;
import com.wealoha.social.beans.Post;
import com.wealoha.social.fragment.SingletonFeedFragment;
import com.wealoha.social.inject.Injector;
import com.wealoha.social.utils.OnClickListenerUtil;
import com.wealoha.social.utils.UiUtils;
import com.wealoha.social.utils.XL;
import com.wealoha.social.view.custom.AtOnePopup2;

public class ImageFeedHolder extends BaseFeedHolder implements OnClickListener {

	@Inject
	Picasso picasso;
	private ViewGroup rootView;
	private ImageView feedPhoto;
	private ImageView praiseImg;
	private ImageView mTagsVisibIv;
	private Fragment mFrag;
	private final int imgWidth;
	private Post mPost;
	/** 存储当前post上的所有tag */
	private final List<AtOnePopup2> atOnesList;

	// private ImageFeedHandler mHandler = new ImageFeedHandler(this);
	//
	// public static class ImageFeedHandler extends Handler {
	//
	// public WeakReference<ImageFeedHolder> holder;
	//
	// public ImageFeedHandler(ImageFeedHolder fiHolder) {
	// holder = new WeakReference<ImageFeedHolder>(fiHolder);
	// }
	// }

	public ImageFeedHolder(LayoutInflater inflater, ViewGroup parent, Fragment frag) {
		super();
		Injector.inject(this);
		mFrag = frag;
		imgWidth = UiUtils.getScreenWidth(mFrag.getActivity().getBaseContext());
		rootView = (ViewGroup) inflater.inflate(R.layout.holder_feed_img, parent, false);
		atOnesList = new ArrayList<AtOnePopup2>();
		findView();
	}

	public View resetViewData(final Post post) {
		mPost = post;
		feedPhoto.getLayoutParams().height = imgWidth;
		picasso.load(post.getCommonImage().getUrlSquare(imgWidth)).into(feedPhoto);
		if (getFeedType() == FeedHolder.TAGS_HOLDER) {
			initAtOnePopup(post);// 直接显示tag
		} else {
			initTagSwitchBtn(post);// 初始化 tag 开关
			clearTags();// 清空视图复用过来的tag
			initDoubleClickListener(feedPhoto, 300);
		}
		return rootView;
	}

	public void findView() {
		feedPhoto = (ImageView) rootView.findViewById(R.id.feed_photo);
		praiseImg = (ImageView) rootView.findViewById(R.id.item_feed_parise_iv);
		mTagsVisibIv = (ImageView) rootView.findViewById(R.id.tags_visibility);
		mTagsVisibIv.setOnClickListener(this);
	}

	@Override
	public View getView() {
		return rootView;
	}

	@Override
	public void praisePost() {
		praiseImg.startAnimation(AnimationUtils.loadAnimation(mFrag.getActivity(), R.anim.feed_like));
	}

	/***
	 * 初始化单击和双击事件
	 * 
	 * @param view
	 * @param clickInterval
	 *            区分单击和双击事件的时间间隔
	 * @return void
	 */
	private void initDoubleClickListener(View view, long clickInterval) {
		if (view != null) {
			view.setOnClickListener(new OnClickListenerUtil(clickInterval) {

				@Override
				public void OnDoubleClickEvent(View v) {
					// ToastUtil.longToast(mFrag.getActivity(), "double click");
					if (parentHolder != null) {
						parentHolder.praisePost();
					}
				}

				@Override
				public void OnClickEvent(View v) {
					// ToastUtil.longToast(mFrag.getActivity(), "click");
					tagSwitch();
				}
			});
		}
	}

	/**
	 * tag开关的动画线程， 在tag开关第一次初始化后5秒渐渐消失
	 */
	private Runnable tagSwitchRunable = new Runnable() {

		@Override
		public void run() {
			AlphaAnimation alphaAnim = new AlphaAnimation(1f, 0f);
			alphaAnim.setDuration(500);
			alphaAnim.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
				}

				@Override
				public void onAnimationEnd(Animation animation) {
					mTagsVisibIv.setVisibility(View.INVISIBLE);
				}
			});
			mTagsVisibIv.startAnimation(alphaAnim);
		}
	};

	/**
	 * @Title: initTagSwitchBtn
	 * @Description: 初始化显示tag开关控件，如果当前post有tag则显示tag开关，反之则不显示
	 */
	public void initTagSwitchBtn(Post post) {
		clearTagSwitchAnim();
		// Tags
		if (post.getUserTags() != null && post.getUserTags().size() > 0) {
			// initTagContainerTransitionAnim(mFeedPhotoContainer);
			mTagsVisibIv.setVisibility(View.VISIBLE);
			mTagsVisibIv.postDelayed(tagSwitchRunable, 5000);
		} else {
			mTagsVisibIv.setVisibility(View.GONE);
		}
	}

	/***
	 * 根据tag 的类型来决定加载全部tag还是加载当前用户的tag
	 * 
	 * @param post
	 *            post数据
	 * @return void
	 */
	private void initAtOnePopup(final Post post) {
		if (getFeedType() == FeedHolder.TAGS_HOLDER) {
			XL.d(SingletonFeedFragment.TAG, "init tags current");
			initCurrentUserTag(post);
		} else {
			initAllTags(post);
		}
	}

	/***
	 * 获取feed 类型
	 * 
	 * @return
	 * @return int
	 */
	private int getFeedType() {
		if (holder2FragCallback != null) {
			return holder2FragCallback.getHolderType();
		}
		return 0;
	}

	/***
	 * 初始化全部tag
	 * 
	 * @param post
	 * @return void
	 */
	private void initAllTags(Post post) {
		List<UserTag> userTagList = post.getUserTags();
		if (userTagList != null && userTagList.size() >= 0) {
			for (int i = 0; i < userTagList.size(); i++) {
				createAtOnePopup(userTagList.get(i));
			}
		}
	}

	/***
	 * 初始化全部当前用的tag
	 * 
	 * @param post
	 * @return void
	 */
	private void initCurrentUserTag(Post post) {
		List<UserTag> userTagList = post.getUserTags();
		if (userTagList != null && userTagList.size() >= 0) {
			for (int i = 0; i < userTagList.size(); i++) {
				if (userTagList.get(i).getUser().getMe()) {
					createAtOnePopup(userTagList.get(i));
					break;
				}
			}
		}
	}

	/**
	 * @Title: clearTags
	 * @Description: 当 {@link ImageFeedHandler#atOnesList}
	 *               不为空或者大于0的时候，关闭所有已经显示的tag，
	 */
	private void clearTags() {
		if (atOnesList != null && atOnesList.size() > 0) {
			for (int j = 0; j < atOnesList.size(); j++) {
				atOnesList.get(j).closePopupByGone();
			}
			atOnesList.clear();
		}
	}

	/**
	 * @Title: createAtOnePopup
	 * @Description: 创建tag 控件, 并将创建的tag 存入 {@link ImageFeedHandler#atOnesList} 中
	 * @param userTag
	 *            设定文件
	 */
	private void createAtOnePopup(UserTag userTag) {
		XL.i("Profile2HeaderHolder", "null---" + userTag.getUser().getAvatarImage());
		XL.i("Profile2HeaderHolder", "post null---" + mPost.getUserTags().get(0).getUser().getAvatarImage());
		AtOnePopup2 atOne = new AtOnePopup2(mFrag.getActivity(), rootView, userTag);
		atOnesList.add(atOne);
		atOne.initAtPopup(false, null);
	}

	/***
	 * tag 开关点击后的操作，包括取消动画，显示或者关闭tag
	 * 
	 * @return void
	 */
	private void tagSwitch() {
		clearTagSwitchAnim();
		if (atOnesList != null && atOnesList.size() > 0) {
			clearTags();
			mTagsVisibIv.setVisibility(View.INVISIBLE);
		} else {
			initAtOnePopup(mPost);
		}
	}

	/***
	 * 清除tag 开关上的动画效果
	 * 
	 * @return void
	 */
	private void clearTagSwitchAnim() {
		if (mTagsVisibIv != null) {
			mTagsVisibIv.clearAnimation();// 取消tag 开关的消失动画(动画正在执行)
			mTagsVisibIv.removeCallbacks(tagSwitchRunable);// 取消动画（动画尚未执行）
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tags_visibility:
			tagSwitch();// 开启或者关闭tag
			break;

		default:
			break;
		}
	}

	@Override
	public void removeTag(String userid) {
		if (atOnesList != null && atOnesList.size() > 0) {
			for (AtOnePopup2 tagHolder : atOnesList) {
				if (userid.equals(tagHolder.getTagUserId())) {
					XL.i("REMOVE_TAG", "removeTag");
					tagHolder.closePopupByGoneNoCallback();
					// tagHolder.removePopupFromContainer();
					break;
				}
			}
		}
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
		return getRawTopY() + imgWidth;
	}
}
