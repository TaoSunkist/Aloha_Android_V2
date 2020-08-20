package com.wealoha.social.adapter.profile;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.app.ActionBar.LayoutParams;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Picasso.LoadedFrom;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;
import com.wealoha.social.AppApplication;
import com.wealoha.social.BaseFragAct;
import com.wealoha.social.R;
import com.wealoha.social.beans.ApiErrorCode;
import com.wealoha.social.api.BaseListApiService.ApiCallback;
import com.wealoha.social.api.BaseListApiService.NoResultCallback;
import com.wealoha.social.api.User2Service;
import com.wealoha.social.beans.User2;
import com.wealoha.social.beans.Result;
import com.wealoha.social.beans.message.InboxSession;
import com.wealoha.social.beans.message.InboxSessionResult;
import com.wealoha.social.api.MessageService;
import com.wealoha.social.commons.GlobalConstants;
import com.wealoha.social.commons.GlobalConstants.ImageSize;
import com.wealoha.social.commons.GlobalConstants.WhereIsComeFrom;
import com.wealoha.social.fragment.BaseFragment;
import com.wealoha.social.fragment.Profile2Fragment;
import com.wealoha.social.fragment.SwipeMenuListFragment;
import com.wealoha.social.inject.Injector;
import com.wealoha.social.render.BlurRendererLite;
import com.wealoha.social.utils.FontUtil;
import com.wealoha.social.utils.FontUtil.Font;
import com.wealoha.social.utils.ImageUtil;
import com.wealoha.social.utils.UiUtils;
import com.wealoha.social.utils.XL;
import com.wealoha.social.view.custom.CircleImageView;

public class Profile2HeaderHolder implements OnTouchListener {

	@Inject
	Picasso picasso;
	@Inject
	User2Service userService;;
	@Inject
	FontUtil fontUtil;
	@Inject
	BlurRendererLite blurRendererLite;
	@Inject
	MessageService mMessageService;

	@InjectView(R.id.layout)
	ViewGroup mLayout;
	/** 人气 */
	@InjectView(R.id.profile_popularity_ll)
	LinearLayout mPopularity;
	@InjectView(R.id.profile_popularity_count_tv)
	TextView mPopCountTv;
	/** 头像 */
	@InjectView(R.id.me_circleimg_v)
	CircleImageView mUserPhoto;
	/** aloha */
	@InjectView(R.id.profile_aloha_ll)
	LinearLayout mAloha;
	@InjectView(R.id.profile_aloha_tv)
	TextView mAlohaTv;
	@InjectView(R.id.profile_aloha_count_tv)
	TextView mAlohaCountTv;
	/** 相册 */
	@InjectView(R.id.profile_grid_pic_radio)
	ImageView mGridPic;
	/** FEED */
	@InjectView(R.id.profile_list_pic_radio)
	ImageView mFeed;
	/** PROFILE */
	@InjectView(R.id.profile_info_radio)
	ImageView mProfile;
	/** 相册 */
	@InjectView(R.id.profile_grid_pic_radio_container)
	LinearLayout mGridPicCont;
	/** FEED */
	@InjectView(R.id.profile_list_pic_radio_container)
	LinearLayout mFeedCont;
	/** PROFILE */
	@InjectView(R.id.profile_info_radio_container)
	LinearLayout mProfileCont;
	/** PROFILE OTHER */
	@InjectView(R.id.profile_other)
	LinearLayout mOther;

	/** PROFILE OTHER */
	@InjectView(R.id.profile_other_chat)
	LinearLayout mOtherChat;
	/** PROFILE OTHER */
	@InjectView(R.id.profile_other_chat_iv)
	ImageView mOtherChatImg;
	/** PROFILE OTHER */
	@InjectView(R.id.profile_other_chat_tv)
	TextView mOtherChatText;

	/** PROFILE OTHER */
	@InjectView(R.id.profile_other_match)
	LinearLayout mOtherMatch;
	/** PROFILE OTHER */
	@InjectView(R.id.profile_other_match_iv)
	ImageView mOtherMatchImg;
	/** PROFILE OTHER */
	@InjectView(R.id.profile_other_match_tv)
	TextView mOtherMatchText;

	/** 为了调整header高度 */
	@InjectView(R.id.profile_header_container)
	RelativeLayout mHeaderContainer;
	/** 为了调整header高度 */
	@InjectView(R.id.profile_photo_container)
	RelativeLayout mPhotoContainer;
	/** RadioGroup */
	@InjectView(R.id.me_content_rg)
	LinearLayout mTabContainer;
	/** RadioGroup */
	@InjectView(R.id.profile_other_pop_count)
	TextView mOtherPopCount;
	protected Fragment mFrag;
	protected User2 mUser2;
	protected View rootView;
	protected ProfileHeader2FragCallback mCallback;

	private int layoutH;
	private int layoutW;
	private Bitmap mBlurBitmap;
	private float mBorderHeight;
	private float mHeaderAlpha;
	private Target blurTarget; // 避免被解引用 @see
	private final static String TAG = Profile2HeaderHolder.class.getSimpleName();

	private Dialog areYouSureDialog;
	private PopupWindow popUpWindow;

	public interface ProfileHeader2FragCallback {

		/***
		 * 切换到多宫格模式
		 */
		public void changeToGridImg();

		/***
		 * 切换到列表模式
		 */
		public void changeToListImg();

		/***
		 * 切换到个人信息
		 */
		public void changeToInfo();

		/***
		 * header 更新了aloha 的状态
		 */
		public void alohaCallback(User2 user2);

		public void refreshUserData(User2 user2);

		public int getProfileType();
	}

	public Profile2HeaderHolder(Fragment frag, ProfileHeader2FragCallback callback, ViewGroup parent) {
		mFrag = frag;
		mCallback = callback;
		rootView = mFrag.getActivity().getLayoutInflater().inflate(R.layout.pro_header_layout, parent, false);
		Injector.inject(this);
		ButterKnife.inject(this, rootView);
		// 字体修正
		fontUtil.changeFonts((ViewGroup) rootView, Font.ENCODESANSCOMPRESSED_400_REGULAR);
		fontUtil.changeViewFont(mPopCountTv, Font.ENCODESANSCOMPRESSED_200_EXTRALIGHT);
		fontUtil.changeViewFont(mAlohaCountTv, Font.ENCODESANSCOMPRESSED_200_EXTRALIGHT);
		fontUtil.changeViewFont(mOtherMatchText, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
		fontUtil.changeViewFont(mOtherChatText, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);

	}

	public View resetViewData(User2 user2) {
		mUser2 = user2;
		initView();
		loadUserHeader();
		return rootView;
	}

	private void loadUserHeader() {
		if (picasso == null || mUser2 == null || mUserPhoto == null || mLayout == null) {
			// ToastUtil.longToast(mFrag.getActivity(), "null----:" + mUser);
			return;
		}

		loadHeadCache(0);

		if (mUser2.isMe()) {
			layoutH = UiUtils.dip2px(mFrag.getActivity(), 226);
			layoutW = UiUtils.getScreenWidth(mFrag.getActivity().getApplicationContext());
		} else {
			layoutH = UiUtils.dip2px(mFrag.getActivity(), 240);
			layoutW = UiUtils.getScreenWidth(mFrag.getActivity().getApplicationContext());
		}
		loadBlur(mLayout);
	}

	public void loadHeadCache(int tag) {
		RequestCreator requestCreator = null;
		switch (tag) {
		case 0:
			XL.i(TAG, "isnull----------------" + mUser2.getAvatarCommonImage());
			requestCreator = picasso//
			.load(mUser2.getAvatarCommonImage().getUrlSquare(ImageSize.CHAT_THUMB));
			break;
		case 1:
			requestCreator = picasso//
			.load(mUser2.getAvatarCommonImage().getUrlSquare(ImageSize.CHAT_THUMB)).skipMemoryCache();
			break;
		}
		//
		if (requestCreator != null) {
			requestCreator.placeholder(R.drawable.default_photo).into(mUserPhoto, new com.squareup.picasso.Callback() {

				@Override
				public void onError() {
					XL.i("USER_PHOTO", "error");
				}

				@Override
				public void onSuccess() {
				}

			});
		}
	}

	// 加载模糊背景的图片
	private void loadBlur(final ViewGroup layout) {
		if (mBlurBitmap != null) {
			// 避免重复加载
			return;
		}
		// FIXME 数值提取
		final String url = mUser2.getAvatarCommonImage().getUrlSquare(ImageSize.AVATAR_ROUND_SMALL);
		blurTarget = new Target() {


			@Override
			public void onBitmapLoaded(final Bitmap origBitmap, LoadedFrom arg1) {

				float ratioLayout = layoutH / (float) layoutW;
				float ratioBitmap = origBitmap.getHeight() / (float) origBitmap.getWidth();
				Bitmap bitmap = null;
				if (ratioLayout > ratioBitmap) {
					// 图片高度不够，裁切左右边
					int w = (int) Math.ceil(origBitmap.getHeight() / ratioLayout);
					int gap = (origBitmap.getWidth() - w) / 2;
					bitmap = Bitmap.createBitmap(origBitmap, gap, 0, w, origBitmap.getHeight());
				} else if (ratioLayout < ratioBitmap) {
					XL.i(TAG, "ratioLayout < ratioBitmap");
					// 图片宽度不够，裁切上下边
					int h = (int) Math.ceil(origBitmap.getWidth() * ratioLayout);
					int gap = (origBitmap.getHeight() - h) / 2;
					bitmap = Bitmap.createBitmap(origBitmap, 0, gap, origBitmap.getWidth(), h);
				} else {
					bitmap = origBitmap;
				}
				XL.i(TAG, "1：" + ratioLayout);
				// XL.d(TAG, "应用Blur: " + url);
				// 先缩放和裁切，变暗，blur
				Matrix matrix = new Matrix();
				matrix.postScale(1.5f, 1.5f);
				Bitmap croppedBitmap = null;
				try {
					croppedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
				} catch (Throwable e) {
					croppedBitmap = bitmap;
				}
				Bitmap darkBitmap = blurRendererLite.changeBitmapContrastBrightness(croppedBitmap, 1, 0, true);
				mBlurBitmap = blurRendererLite.blurBitmap(darkBitmap, 25, true);
				if (mBlurBitmap != null) {
					ImageUtil.drawBackground(mFrag.getActivity(), layout, mBlurBitmap);
				}
			}

			@Override
			public void onBitmapFailed(Drawable errorDrawable) {

			}

			@Override
			public void onPrepareLoad(Drawable placeHolderDrawable) {

			}


		};

		picasso.load(url).placeholder(R.color.gray_text).into(blurTarget);
	}

	/**
	 * @Title: changeView
	 * @Description: 改变视图（自己or别人的）
	 * @return void 返回类型
	 * @throws
	 */
	private void initView() {
		initTabState();
		if (mUser2.isMe()) {
			XL.i(TAG, "me");
			mOther.setVisibility(View.GONE);
			mPopularity.setVisibility(View.VISIBLE);
			mAloha.setVisibility(View.VISIBLE);
			mOtherPopCount.setVisibility(View.GONE);

			mAlohaCountTv.setText(String.valueOf(mUser2.getAlohaCount()));
			mPopCountTv.setText(String.valueOf(mUser2.getAlohaGetCount()));

			// 为透明渐变效果做准备
			mBorderHeight = UiUtils.dip2px(mFrag.getActivity(), 160);

		} else if (mUser2.hasPrivacy()) {
			// 被对方拉黑
			// 为透明渐变效果做准备
			mBorderHeight = UiUtils.dip2px(mFrag.getActivity(), 160);
			mPopularity.setVisibility(View.GONE);
			mAloha.setVisibility(View.GONE);
			changeToBlack();
		} else {
			// 调整header高度
			// FIXME 提取控件高度数值
			mBorderHeight = UiUtils.dip2px(mFrag.getActivity(), 180);
			mPhotoContainer.getLayoutParams().height = UiUtils.dip2px(mFrag.getActivity(), 240);

			mPopularity.setVisibility(View.GONE);
			mAloha.setVisibility(View.GONE);
			mOther.setVisibility(View.VISIBLE);
			mOtherPopCount.setVisibility(View.VISIBLE);
			mOtherPopCount.setText(mUser2.getAlohaGetCount() + mFrag.getString(R.string.profile_aloha_get));
			// 已匹配
			if (mUser2.isMatch()) {
				mOtherMatch.setVisibility(View.VISIBLE);
				mOtherChat.setVisibility(View.VISIBLE);

				mOtherMatchText.setTextSize(13);
				mOtherMatchText.setText(R.string.matched);
				mOtherMatchText.setTextColor(mFrag.getResources().getColor(R.color.black_text));
				mOtherMatchImg.setImageResource(R.drawable.profile_liked);

				mOtherChatText.setText(R.string.chat);
				mOtherChatText.setTextSize(13);
				mOtherChatImg.setImageResource(R.drawable.profile_chat);
				// 已经喜欢
			} else if (mUser2.isAloha()) {
				mOtherChat.setVisibility(View.VISIBLE);
				mOtherMatch.setVisibility(View.GONE);

				mOtherChatText.setText(R.string.liked);
				mOtherChatImg.setImageResource(R.drawable.profile_liked);

				// 没喜欢过aloha
			} else {
				mOtherChat.setVisibility(View.GONE);
				mOtherMatch.setVisibility(View.VISIBLE);

				mOtherMatchText.setTextSize(15);
				mOtherMatchText.setText("Aloha");
				mOtherMatchText.setTextColor(mFrag.getResources().getColor(R.color.red));
				mOtherMatchImg.setImageResource(R.drawable.profile_heart);
			}
		}
		mPopCountTv.setText(String.valueOf(mUser2.getAlohaGetCount()));
		initOnTouchEvent();
	}

	/***
	 * 初始化tab 切换的事件
	 * 
	 */
	private void initOnTouchEvent() {
		mGridPicCont.setOnTouchListener(this);
		mFeedCont.setOnTouchListener(this);
		mProfileCont.setOnTouchListener(this);
		mPopularity.setOnTouchListener(this);
		mAloha.setOnTouchListener(this);

		mOtherMatch.setOnTouchListener(this);
		mOtherChat.setOnTouchListener(this);
		mUserPhoto.setOnTouchListener(this);

	}

	/***
	 * 切换至被拉入黑名单的状态
	 * 
	 */
	private void changeToBlack() {
		// if (mCallback != null) {
		// mCallback.blackUserCallback();
		mGridPic.setImageResource(R.drawable.profile_grid_b);
		mFeed.setImageResource(R.drawable.profile_list_b);
		mProfile.setImageResource(R.drawable.profile_detail_b);
		// }
	}

	/***
	 * 初始化tab 键的状态，默认选中第一个
	 * 
	 * @return void
	 */
	public void initTabState() {
		switch (mCallback.getProfileType()) {
		case Profile2Fragment.GRID_IMG_PROFILE:
			mGridPic.setImageResource(R.drawable.profile_grid_d);
			mFeed.setImageResource(R.drawable.profile_list_b);
			mProfile.setImageResource(R.drawable.profile_detail_b);
			break;
		case Profile2Fragment.LIST_IMG_PROFILE:
			mGridPic.setImageResource(R.drawable.profile_grid_b);
			mFeed.setImageResource(R.drawable.profile_list_d);
			mProfile.setImageResource(R.drawable.profile_detail_b);
			break;
		case Profile2Fragment.INFO_PROFILE:
			mGridPic.setImageResource(R.drawable.profile_grid_b);
			mFeed.setImageResource(R.drawable.profile_list_b);
			mProfile.setImageResource(R.drawable.profile_detail_d);
			break;
		default:
			break;
		}

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (mCallback == null || event.getAction() != MotionEvent.ACTION_UP || mUser2.hasPrivacy()) {
			return true;
		}
		switch (v.getId()) {
		case R.id.profile_grid_pic_radio_container:
			mCallback.changeToGridImg();
			mGridPic.setImageResource(R.drawable.profile_grid_d);
			mFeed.setImageResource(R.drawable.profile_list_b);
			mProfile.setImageResource(R.drawable.profile_detail_b);
			return true;
		case R.id.profile_list_pic_radio_container:
			mCallback.changeToListImg();
			mGridPic.setImageResource(R.drawable.profile_grid_b);
			mFeed.setImageResource(R.drawable.profile_list_d);
			mProfile.setImageResource(R.drawable.profile_detail_b);
			return true;
		case R.id.profile_info_radio_container:
			mCallback.changeToInfo();
			mGridPic.setImageResource(R.drawable.profile_grid_b);
			mFeed.setImageResource(R.drawable.profile_list_b);
			mProfile.setImageResource(R.drawable.profile_detail_d);
			return true;
		case R.id.profile_popularity_ll:
			openUserList(SwipeMenuListFragment.LISTTYPE_POPULARITY);
			break;
		case R.id.profile_aloha_ll:
			openUserList(SwipeMenuListFragment.LISTTYPE_LIKE);
			break;
		case R.id.profile_other_match:
			if (mUser2.isMatch()) {
				// match過，那麼就是取消aloha
				openAreYouSureDialog();
			} else {
				// 這時的match btn是aloha
				aloha();
			}
			break;
		case R.id.profile_other_chat:// liked or chat
			if (mUser2.isMatch()) {
				getUserSessionId(mUser2.getId());
			} else {
				openAreYouSureDialog();
			}
			break;
		case R.id.me_circleimg_v:
			// 点击头像放大
			PopupWindow popUpWindow = readyPopUpWindow();
			if (popUpWindow.isShowing()) {
				popUpWindow.dismiss();
			} else {
				popUpWindow.setAnimationStyle(R.style.popwindow_avactor_anim_style);
				popUpWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
			}
			break;
		default:
			break;
		}
		v.performClick();
		return false;
	}

	/***
	 * 開啟人氣 or aloha過的人的列表
	 * 
	 * @param userListType
	 *            列表類型
	 * @return void
	 */
	private void openUserList(int userListType) {
		Bundle bundle = new Bundle();
		bundle.putInt("listtype", userListType);
		((BaseFragAct) mFrag.getActivity()).startFragmentForResult(SwipeMenuListFragment.class, bundle, true, Profile2Fragment.PROFILE_REFRESH_ICON, 0, 0);
	}

	/***
	 * 在这里通过list header 相对于屏幕的位置来计算父容器fragment的标题栏的透明度，达到随着list向上滑动 标题栏渐渐透明的效果。
	 * 父容器的fragment监听list滑动事件，每次滑动都调用header的此方法来获取透明度
	 * 
	 * @return float
	 */
	public float getParentTitleAlpha() {
		if (rootView != null) {
			mHeaderAlpha = rootView.getY() / -mBorderHeight;
			if (rootView.getY() > -mBorderHeight) {
				mHeaderAlpha = mHeaderAlpha > 1 ? 1 : mHeaderAlpha;
			} else {
				mHeaderAlpha = 1;
			}

			mHeaderContainer.setAlpha(1 - mHeaderAlpha);
			mOther.setAlpha(1 - mHeaderAlpha);
			mTabContainer.setAlpha(1 - mHeaderAlpha);

			return mHeaderAlpha;
		}
		return 0;
	}

	private Dialog alertDialog;

	/***
	 * 打开确认弹出层
	 * 
	 * @return void
	 */
	private void openAreYouSureDialog() {
		View view = LayoutInflater.from(mFrag.getActivity()).inflate(R.layout.dialog_first_aloha_time, new LinearLayout(mFrag.getActivity()), false);
		TextView title = (TextView) view.findViewById(R.id.first_aloha_title);
		TextView message = (TextView) view.findViewById(R.id.first_aloha_message);
		message.setVisibility(View.GONE);
		title.setText(R.string.unAloha);
		TextView closeLeft = (TextView) view.findViewById(R.id.close_tv);
		closeLeft.setText(R.string.cancel);
		closeLeft.setVisibility(View.VISIBLE);
		TextView closeRight = (TextView) view.findViewById(R.id.close_tv_02);
		closeRight.setText(R.string.confirm);
		closeRight.setVisibility(View.VISIBLE);
		FontUtil.setSemiBoldTypeFace(mFrag.getActivity(), title);
		FontUtil.setRegulartypeFace(mFrag.getActivity(), closeRight);
		FontUtil.setRegulartypeFace(mFrag.getActivity(), closeLeft);
		closeLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (alertDialog != null && alertDialog.isShowing()) {
					alertDialog.dismiss();
				}
			}
		});
		closeRight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (alertDialog != null && alertDialog.isShowing()) {
					dislike();
					alertDialog.dismiss();
				}

			}
		});
		alertDialog = new AlertDialog.Builder(mFrag.getActivity())//
		.setView(view).create();
		alertDialog.show();

	}

	/***
	 * 取消aloha
	 * 
	 * @return void
	 */
	private void dislike() {
		userService.dislike(mUser2.getId(), new NoResultCallback() {

			@Override
			public void success() {
				XL.i(TAG, "dislike success");
				refreshHeader(mUser2.getId());
				holderCallback();
			}

			@Override
			public void fail(ApiErrorCode code, Exception exception) {
				XL.i(TAG, "dislike fail");
			}
		});
	}

	/***
	 * aloha
	 * 
	 * @return void
	 */
	private void aloha() {
		Bundle referBundle = ((BaseFragment) mFrag).getWhereComeFromToProfile();
		String refer = null;
		if (referBundle != null) {
			refer = referBundle.getString(WhereIsComeFrom.REFER_KEY);
		}
		userService.aloha(mUser2.getId(), refer, new NoResultCallback() {

			@Override
			public void success() {
				XL.i(TAG, "dislike success");
				refreshHeader(mUser2.getId());
				holderCallback();
			}

			@Override
			public void fail(ApiErrorCode code, Exception exception) {
				XL.i(TAG, "dislike fail");
			}
		});
		// 移除翻牌子列表中的人
		if (AppApplication.mUserList != null && AppApplication.mUserList.size() > 0) {
			for (com.wealoha.social.beans.User user : AppApplication.mUserList) {
				if (user.getId().equals(mUser2.getId())) {
					AppApplication.mUserList.remove(user);
					XL.i(TAG, "AppApplication success:" + AppApplication.mUserList.contains(mUser2));
					break;
				}
			}
		}
	}

	/***
	 * 开启和某人的聊天界面
	 * 
	 * @param id
	 * @return void
	 */
	private void getUserSessionId(final String id) {
		mMessageService.getInboxSession(id, new Callback<Result<InboxSessionResult>>() {

			@Override
			public void success(Result<InboxSessionResult> result, Response arg1) {
				if (!mFrag.isVisible()) {
					return;
				}
				if (result != null && result.isOk()) {
					if (result.data.list != null && result.data.list.size() != 0) {
						InboxSession inboxSession = result.data.list.get(0);
						Bundle inboxSessionBundle = new Bundle();
						inboxSessionBundle.putString("sessionId", inboxSession.id);
						((BaseFragAct) mFrag.getActivity()).startActivity(GlobalConstants.IntentAction.INTENT_URI_DIALOGUE, inboxSessionBundle);
					} else {
						mMessageService.post(id, new Callback<Result<InboxSessionResult>>() {

							@Override
							public void failure(RetrofitError arg0) {

							}

							@Override
							public void success(Result<InboxSessionResult> arg0, Response arg1) {
								mMessageService.post(id, new Callback<Result<InboxSessionResult>>() {

									@Override
									public void failure(RetrofitError arg0) {
									}

									@Override
									public void success(Result<InboxSessionResult> arg0, Response arg1) {
										if (arg0 != null && arg0.isOk()) {
											if (arg0.data.list != null && arg0.data.list.size() != 0) {
												InboxSession inboxSession = arg0.data.list.get(0);
												Bundle inboxSessionBundle = new Bundle();
												inboxSessionBundle.putString("sessionId", inboxSession.id);
												((BaseFragAct) mFrag.getActivity()).startActivity(GlobalConstants.IntentAction.INTENT_URI_DIALOGUE, inboxSessionBundle);
											}
										}
									}
								});
							}
						});
					}
				}
			}

			@Override
			public void failure(RetrofitError arg0) {

			}
		});
	}

	/***
	 * 刷新用户信息并跟新视图，如果刷新的是当前用户，那么当前用户的本地信息也会被刷新保存 {@link User2Service#userProfile(String, ApiCallback)}
	 * 
	 * @param userid
	 *            用户id
	 * @return void
	 */
	public void refreshHeader(String userid) {
		XL.i(TAG, "refreshHeader==========");
		userService.userProfile(userid, new ApiCallback<User2>() {

			@Override
			public void success(User2 data) {
				// initView();
				XL.i(TAG, "success==========");
				if (!mFrag.isVisible()) {
					return;
				}
				refreshIco(data);
				mUser2 = data;
				initView();

				if (mCallback != null) {
					mCallback.refreshUserData(mUser2);
				}
			}

			@Override
			public void fail(ApiErrorCode code, Exception exception) {
				XL.i(TAG, "fail==========");
			}
		});
	}

	/***
	 * 刷新头像，如果头像没有更新那么不刷新
	 * 
	 * @return void
	 */
	private void refreshIco(User2 refreshUser2) {
		if (!mUser2.getAvatarCommonImage().getImageId().equals(refreshUser2.getAvatarCommonImage().getImageId())) {
			mUser2 = refreshUser2;
			loadUserHeader();
		}
	}

	/***
	 * 通知父组件 aloha 的状态改变
	 * 
	 * @return void
	 */
	private void holderCallback() {
		if (mCallback != null) {
			mCallback.alohaCallback(mUser2);
		}
	}

	/***
	 * 点击看大头像
	 * 
	 * @return PopupWindow
	 */
	private PopupWindow readyPopUpWindow() {
		View popwin_layout = mFrag.getActivity().getLayoutInflater().inflate(R.layout.popwin_avactor, new LinearLayout(mFrag.getActivity()), false);
		ImageView avactor = (ImageView) popwin_layout.findViewById(R.id.pop_imgview);
		LinearLayout ll = (LinearLayout) popwin_layout.findViewById(R.id.container_layout);
		popUpWindow = new PopupWindow(popwin_layout);
		popUpWindow.setOutsideTouchable(true);
		popUpWindow.setFocusable(true);
		popUpWindow.setBackgroundDrawable(new ColorDrawable());
		popUpWindow.setTouchable(true);
		popUpWindow.setWidth(LayoutParams.MATCH_PARENT);
		popUpWindow.setHeight(LayoutParams.MATCH_PARENT);
		String url = mUser2.getAvatarCommonImage().getUrlSquare(ImageSize.FEED_MAX);
		// FIXME 应该先显示小图,大图家在完毕显示大图

		picasso.load(url).into(avactor);
		ll.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (popUpWindow.isShowing()) {
					popUpWindow.dismiss();
				}
			}
		});
		return popUpWindow;
	}
}
