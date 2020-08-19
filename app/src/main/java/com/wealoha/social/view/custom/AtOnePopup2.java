package com.wealoha.social.view.custom;

import javax.inject.Inject;

import android.animation.LayoutTransition;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.wealoha.social.BaseFragAct;
import com.wealoha.social.R;
import com.wealoha.social.activity.TagFeedAct;
import com.wealoha.social.beans.UserTag;
import com.wealoha.social.beans.feed.UserTags;
import com.wealoha.social.fragment.Profile2Fragment;
import com.wealoha.social.inject.Injector;
import com.wealoha.social.utils.ContextUtil;
import com.wealoha.social.utils.FontUtil;
import com.wealoha.social.utils.FontUtil.Font;
import com.wealoha.social.utils.UiUtils;
import com.wealoha.social.utils.XL;
import com.wealoha.social.view.custom.popup.AtOnePopup;

public class AtOnePopup2 implements OnTouchListener, OnClickListener, OnGestureListener {

	@Inject
	ContextUtil contextUtil;
	@Inject
	FontUtil fontUtil;
	private Context mContext;

	private int mPopupWidth;
	private int mPopupHeight;
	private int mAnchorImgWidth;
	private int mContainerWidth;
	private int mContainerHeight;
	private boolean mCanMove;

	// 以下 7 个变量为 pop 跟随手机移动的计算所需要的变量
	private float onTouchFirtDownX;
	private float onTouchFritDownY;
	private float popupStartMoveFromX;
	private float popupStartMoveFromY;
	private float mPopupStopMoveInLeft;
	private float mPopupStopMoveInRight;
	/** 为了在锚点变向后 让pop 扔能够正常的跟随手指移动而设置的偏移补偿，在手抬起时一定要清零 */
	private float mAnchorChangedOffsetY;
	// private boolean mAchorChange

	private TextView mContent;
	private ImageView mAnchorImgTop;
	private ImageView mAnchorImgBottom;
	private RelativeLayout mPopupView;
	private LinearLayout mContentContainer;

	private LayoutInflater inflater;
	private ValueAnimator slideAnimator;
	private ImageView mDeleteImg;
	private ViewGroup mParentContainer;
	private LayoutTransition mLayoutTransition;
	private AlphaAnimation mAlphaAnim;

	private UserTag mUserTag;
	private AtOnePopupCallback mCallback;

	private GestureDetector mGesture = new GestureDetector(mContext, this);

	private final float PADDING_FOR_POPCONTAINER = 20f;
	private final int DELETEIMG_ID = 0x000000;

	public AtOnePopup2(Context context, ViewGroup container, UserTag userTag) {
		Injector.inject(this);
		mContext = context;
		mParentContainer = container;
		mUserTag = userTag;
		inflater = LayoutInflater.from(mContext);
		// 默认宽高
		if (container == null) {
			getDefaultWidth();
		} else {
			mContainerWidth = container.getWidth();
			mContainerHeight = container.getHeight();
			if (mContainerWidth <= 0 || mContainerHeight <= 0) {
				getDefaultWidth();
			}
		}
		initAnimationFields();
	}

	private void getDefaultWidth() {

		mContainerWidth = UiUtils.getScreenWidth(mContext);
		mContainerHeight = UiUtils.getScreenWidth(mContext);
	}

	/**
	 * @Title: initFiels
	 * @Description: 初始化全局变量
	 */
	private void initFiels() {
		mPopupWidth = mPopupView.getWidth();
		mPopupHeight = mPopupView.getHeight();
		mAnchorImgWidth = mAnchorImgTop.getWidth();
	}

	private void findViews(ViewGroup rootview) {
		mPopupView = (RelativeLayout) inflater.inflate(R.layout.pop_tag_sub, rootview, false);
		mContent = (TextView) mPopupView.findViewById(R.id.insta_sub_text);
		fontUtil.changeViewFont(mContent, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
		mAnchorImgTop = (ImageView) mPopupView.findViewById(R.id.insta_rectangle);
		mAnchorImgBottom = (ImageView) mPopupView.findViewById(R.id.insta_rectangle_bottom);
		mContentContainer = (LinearLayout) mPopupView.findViewById(R.id.content_container);
	}

	public void initAtPopup(boolean canMove, AtOnePopupCallback callback) {
		if (mParentContainer == null) {
			return;
		}
		float initX = (float) (mUserTag.getTagAnchorX() * mContainerWidth);
		float initY = (float) (mUserTag.getTagAnchorY() * mContainerHeight);

		initAtPopup(initX, initY, canMove, callback);
	}

	public void initAtPopup(final float x, final float y, boolean canMove, AtOnePopupCallback callback) {
		if (mUserTag == null) {
			return;
		}
		mCallback = callback;
		mCanMove = canMove;
		findViews(mParentContainer);
		mContent.setText(mUserTag.getUser().getName());
		// 先设置为隐藏，防止初始化位置的时候视图闪动
		mPopupView.setVisibility(View.INVISIBLE);
		mPopupView.post(new Runnable() {

			@Override
			public void run() {
				initFiels();
				// 初始化位置，以锚点所在垂线 为 pop 的中心线
				mPopupView.setX(x - mPopupWidth / 2f);
				mPopupView.setY(initAnchorToCurrectPostion(y));
				mAnchorImgTop.setX(mPopupWidth / 2f - mAnchorImgWidth / 2f);
				mAnchorImgBottom.setX(mPopupWidth / 2f - mAnchorImgWidth / 2f);
				mPopupView.setVisibility(View.VISIBLE);

			}
		});
		mPopupView.setOnTouchListener(this);
		// mPopupContainer.setOnLongClickListener(this);
		// initDeleteView();
		mParentContainer.addView(mPopupView);
		mParentContainer.postDelayed(new Runnable() {

			@Override
			public void run() {
				slideToCurrectPositionAtUp();
			}
		}, 100);
	}

	public void slideToCurrectPositionAtUp() {
		final float subx = mPopupView.getX();
		final float imgx = mAnchorImgTop.getX();
		float distance = 0;
		boolean isMaybeStartOutside = false;
		if (subx + mPopupWidth > mContainerWidth) {
			distance = mContainerWidth - (subx + mPopupWidth);
		} else if (subx < 0) {
			distance = -subx;
		} else {
			// 这种情况下视图的当前状态很可能是有一部分在屏幕外，所以做特殊处理
			isMaybeStartOutside = true;
			distance = imgx - (mPopupWidth - mAnchorImgWidth) / 2f;
		}
		// 锚点方向初始化
		float distancOffset = initAnchorToCurrectPostion(mPopupView.getY());
		mPopupView.setY(distancOffset);
		if (mCanMove) {
			startAnimation(distance, subx, imgx, isMaybeStartOutside);
		} else {
			mAnchorImgBottom.setX(imgx - distance);
			mAnchorImgTop.setX(imgx - distance);
			mPopupView.setX(subx + distance);
		}
	}

	/***
	 * @Title: initAnchorToCurrectPostion
	 * @Description: 初始化锚点方向
	 * @param distanceY
	 *            设定文件
	 * @return void 返回类型
	 * @throws
	 */
	private float initAnchorToCurrectPostion(float distanceY) {
		float initDistanceOffsetY = 0;
		if (distanceY + mPopupHeight + PADDING_FOR_POPCONTAINER >= mContainerHeight) {// 锚点转换方向
			if (mAnchorImgBottom.getVisibility() == View.INVISIBLE) {
				mAnchorImgBottom.setVisibility(View.VISIBLE);
				mAnchorImgTop.setVisibility(View.INVISIBLE);
				// startAnchorChangedAnim(startx);
				// mPopupView.setY(distanceY - mPopupHeight);
				initDistanceOffsetY = -mPopupHeight;
				mAnchorChangedOffsetY = -mPopupHeight;
			}
		} else if (distanceY <= mContainerHeight - mPopupHeight * 2 - PADDING_FOR_POPCONTAINER * 2) {
			if (mAnchorImgTop.getVisibility() == View.INVISIBLE) {
				mAnchorImgTop.setVisibility(View.VISIBLE);
				mAnchorImgBottom.setVisibility(View.INVISIBLE);
				// startAnchorChangedAnim(startx);
				// mPopupView.setY(distanceY + mPopupHeight);
				initDistanceOffsetY = mPopupHeight;
				XL.i("INIT_FIELS", "dis:" + distanceY + "---" + mPopupHeight);
				mAnchorChangedOffsetY = 0;
			}
		}
		return distanceY + initDistanceOffsetY;
	}

	private void startAnimation(float distance, final float containerStartPosition, final float anchorStartPosition,//
			final boolean isMaybeStartOutside) {
		slideAnimator = ValueAnimator.ofFloat(distance);
		slideAnimator.setDuration(200);
		slideAnimator.addUpdateListener(new AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				float f = (Float) animation.getAnimatedValue();

				// 如果视图的起始状态是有一部分在屏幕外的话，那么在适当的时候停止动画
				if (isMaybeStartOutside) {
					if (containerStartPosition + f < 0 || containerStartPosition + f + mPopupWidth > mContainerWidth) {
						animation.cancel();
					}
				}
				mAnchorImgBottom.setX(anchorStartPosition - f);
				mAnchorImgTop.setX(anchorStartPosition - f);
				mPopupView.setX(containerStartPosition + f);
			}
		});
		slideAnimator.start();
	}

	private void initAnimationFields() {
		// 删除控件出现动画
		mLayoutTransition = new LayoutTransition();
		mLayoutTransition.setDuration(100);
		mLayoutTransition.setAnimator(LayoutTransition.APPEARING, mLayoutTransition.getAnimator(LayoutTransition.APPEARING));
		mLayoutTransition.setAnimator(LayoutTransition.DISAPPEARING, mLayoutTransition.getAnimator(LayoutTransition.DISAPPEARING));

		// close pop 动画
		mAlphaAnim = new AlphaAnimation(1.0f, 0.0f);
		mAlphaAnim.setDuration(300);
		mAlphaAnim.setFillAfter(true);
		mAlphaAnim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				closePopupByGone();
			}
		});
	}

	public void closePopupByGone() {
		closePopupByGoneNoCallback();

		if (mCallback != null) {
			// mCallback.closePopupByGoneCallback(this);
		}
	}

	public void closePopupByGoneNoCallback() {
		if (mPopupView != null) {
			XL.i("REMOVE_TAG", "closePopupByGoneNoCallback");
			// 如果父容器给子空间加了动画，那么动画也应该关闭
			mPopupView.clearAnimation();
			XL.i("REMOVE_TAG", "closePopupByGoneNoCallback:" + mPopupView.getVisibility());
			mPopupView.setVisibility(View.INVISIBLE);
			XL.i("REMOVE_TAG", "closePopupByGoneNoCallback:" + mPopupView.getVisibility());
			XL.i("REMOVE_TAG", "closePopupByGoneNoCallback:" + mPopupView.getX());
		}
	}

	private void initDeleteView() {
		if (mLayoutTransition == null) {
			initAnimationFields();
		}
		mContentContainer.setLayoutTransition(mLayoutTransition);
		mDeleteImg = new ImageView(mContext);
		mDeleteImg.setId(DELETEIMG_ID);
		LayoutParams params = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, //
		android.view.ViewGroup.LayoutParams.MATCH_PARENT);
		mDeleteImg.setPadding(0, 0, UiUtils.dip2px(mContext, 8), 0);
		mDeleteImg.setLayoutParams(params);
		mDeleteImg.setScaleType(ScaleType.CENTER);
		mDeleteImg.setImageResource(R.drawable.close_tag);
		mDeleteImg.setOnClickListener(this);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		if (!mCanMove) {
			if (event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP) {
				openProFile();
			}
			return false;
		}
		// 监听长按事件
		mGesture.onTouchEvent(event);
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			onTouchFirtDownX = event.getRawX();// 获取触点的起始坐标
			onTouchFritDownY = event.getRawY();
			popupStartMoveFromX = v.getX();// pop 的起始坐标，通过触点的移动的相对距离来动态计算 pop 的位置
			popupStartMoveFromY = v.getY();
			mAnchorChangedOffsetY = 0;
			// 用于计算停止还是移动的临界值
			mPopupStopMoveInLeft = mAnchorImgTop.getX() - PADDING_FOR_POPCONTAINER;
			mPopupStopMoveInRight = mAnchorImgTop.getX() + mAnchorImgWidth + PADDING_FOR_POPCONTAINER;

			// 让父容器一起移除所有的删除视图
			if (mCallback != null) {
				mCallback.removeDeleteViewCallback();
			}
			break;
		case MotionEvent.ACTION_MOVE:
			XL.i("MOVE_TOUCH", "----MOVE----");
			slideToCurrectPositionAtMove(v, event.getRawX(), event.getRawY());
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			mPopupView.setLongClickable(true);
			slideToCurrectPositionAtUp();

			break;
		default:
			break;
		}
		// 一个神奇的方法
		v.performClick();
		return false;
	};

	/**
	 * @Title slideToCurrectPositionAtMove
	 * @Description 以 pop 容器的x坐标（相对于整个pop的父容器）为计算基础，distanceTempX 为 pop 移动和停止的临界值
	 * @param v
	 * @param x
	 * @param y
	 */
	private void slideToCurrectPositionAtMove(View v, float x, float y) {
		float distanceX = x - onTouchFirtDownX + popupStartMoveFromX;
		float distanceLeftTempX = distanceX + mPopupStopMoveInLeft;
		float distanceRightTempX = distanceX + mPopupStopMoveInRight;
		if (distanceLeftTempX <= 0) {// 左边界计算
			distanceX = -mPopupStopMoveInLeft;
		} else if (distanceRightTempX >= mContainerWidth) {// 右边界计算
			distanceX = mContainerWidth - mPopupStopMoveInRight;
		}
		v.setX(distanceX);

		float distanceY = y - onTouchFritDownY + popupStartMoveFromY + mAnchorChangedOffsetY;// 加上冒点转向后的便宜补偿

		initAnchorToCurrectPostion(distanceY);
		if (distanceY - PADDING_FOR_POPCONTAINER <= 0) {// 上边界
			distanceY = PADDING_FOR_POPCONTAINER;
		} else if (distanceY + mPopupHeight + PADDING_FOR_POPCONTAINER >= mContainerHeight) {
			distanceY = mContainerHeight - PADDING_FOR_POPCONTAINER - mPopupHeight;
		}
		v.setY(distanceY);

	}

	private void openProFile() {
		Bundle bundle = new Bundle();
		bundle.putSerializable(com.wealoha.social.api.user.bean.User.TAG, mUserTag.getUser());
		XL.i("Profile2HeaderHolder", "is null ====" + mUserTag.getUser().getAvatarImage());
		if (contextUtil.getForegroundAct() != null) {
			((BaseFragAct) contextUtil.getForegroundAct()).startFragmentHasAnim(Profile2Fragment.class, bundle, true, R.anim.left_in, R.anim.stop);
		}
	}

	public void stopAnimation() {
		if (slideAnimator != null && slideAnimator.isRunning()) {
			slideAnimator.cancel();
			slideAnimator = null;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case DELETEIMG_ID:
			closePopup();
			break;
		default:
			break;
		}
	}

	public void closePopup() {
		if (mContentContainer == null || mAnchorImgTop == null) {
			return;
		}
		if (mAlphaAnim == null) {
			initAnimationFields();
		}
		mContentContainer.startAnimation(mAlphaAnim);
		mAnchorImgTop.startAnimation(mAlphaAnim);
	}

	public void removePopupFromContainer() {
		mContentContainer.removeView(mPopupView);
	}

	public UserTags getPopupInfo() {
		// if (mUserTag == null) {
		// mUserTag = new UserTag(getAnchorX(), getAnchorY(), getCenterX(), getCenterY(), new User);
		// }
		// mUserTags.tagAnchorX = getAnchorX();
		// mUserTags.tagAnchorY = getAnchorY();
		// mUserTags.tagCenterX = getCenterX();
		// mUserTags.tagCenterY = getCenterY();
		// mUserTags.tagUserId = getTagUserId();
		return null;
	}

	public Float getAnchorX() {

		return (mPopupView.getX() + mAnchorImgTop.getX() + mAnchorImgWidth / 2) / mContainerWidth;
	}

	public Float getAnchorY() {
		// 锚点方向不同，坐标不同
		int offset = 0;
		if (mAnchorImgBottom.getVisibility() == View.VISIBLE) {
			offset = mPopupHeight;
		} else {
			offset = 0;
		}

		return (mPopupView.getY() + offset) / mContainerHeight;
	}

	public Float getCenterX() {
		return (mPopupView.getX() + mPopupWidth / 2) / mContainerWidth;
	}

	public Float getCenterY() {
		return (mPopupView.getY() + mPopupHeight / 2) / mContainerHeight;
	}

	public void getPopopPosition() {
		XL.i("INITATONEPOPUP", "X:" + mPopupView.getX() + "---Y:" + mPopupView.getY());
	}

	public String getTagUserId() {
		if (mUserTag == null) {
			return null;
		}
		return mUserTag.getUser().getId();
	}

	@Override
	public void onLongPress(MotionEvent e) {
		if (mCallback != null) {
			mCallback.addDeleteViewCallback();
		}
	}

	public void addDeleteView() {
		if (mDeleteImg == null) {
			initDeleteView();
			mContentContainer.addView(mDeleteImg);
		}
	}

	/**
	 * @Title: removeDeleteImg
	 * @Description: 移除删除视图
	 */
	public void removeDeleteView() {
		if (isDeleteViewVisibility()) {
			mContentContainer.removeView(mDeleteImg);
			mDeleteImg = null;
		}
	}

	public boolean isDeleteViewVisibility() {
		if (mDeleteImg == null) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distX, float distY) {
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		return false;
	}

	public interface AtOnePopupCallback {

		/**
		 * @Title: closePopupByGoneCallback
		 * @Description: 移除tag 的回调
		 */
		public void closePopupByGoneCallback(AtOnePopup popup);

		/**
		 * @Title: addDeleteViewCallback
		 * @Description: 加载移除视图，在{@link TagFeedAct} 中回调并执行{@link TagFeedAct#addDeleteView()}，让所有tag
		 *               一起加载移除视图的图标
		 */
		public void addDeleteViewCallback();

		/**
		 * @Title: addDeleteViewCallback
		 * @Description: 加载移除视图，在{@link TagFeedAct} 中回调并执行{@link TagFeedAct#removeDeleteView()}，让所有tag
		 *               一起加载移除视图的图标
		 */
		public void removeDeleteViewCallback();
	}

	public boolean equals(AtOnePopup onePopup) {
		String userid = getTagUserId();
		if (!TextUtils.isEmpty(userid) && userid.equals(onePopup.getTagUserId())) {
			return true;
		}
		return false;
	}
}
