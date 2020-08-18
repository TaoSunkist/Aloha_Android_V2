package com.wealoha.social.view.custom;

import javax.inject.Inject;

import android.annotation.TargetApi;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.squareup.picasso.Picasso;
import com.wealoha.social.BaseFragAct;
import com.wealoha.social.ContextConfig;
import com.wealoha.social.R;
import com.wealoha.social.beans.User;
import com.wealoha.social.commons.GlobalConstants;
import com.wealoha.social.fragment.Profile2Fragment;
import com.wealoha.social.inject.Injector;
import com.wealoha.social.render.BlurRenderTest;
import com.wealoha.social.render.BlurRendererLite;
import com.wealoha.social.utils.ChatUtil;
import com.wealoha.social.utils.ContextUtil;
import com.wealoha.social.utils.FontUtil;
import com.wealoha.social.utils.FontUtil.Font;
import com.wealoha.social.utils.ImageUtil;
import com.wealoha.social.utils.ImageUtil.CropMode;
import com.wealoha.social.utils.MusicPlayer;
import com.wealoha.social.utils.UiUtils;

public class MatchPopupWindow extends PopupWindow {

	@Inject
	Picasso picasso;
	@Inject
	ContextUtil contextUtil;
	@Inject
	Context mContext;
	@Inject
	FontUtil font;
	@Inject
	BlurRendererLite blurRendererLite;

	@InjectView(R.id.layout)
	ViewGroup layout;
	@InjectView(R.id.match_not_now)
	TextView notNow;

	@InjectView(R.id.match_user_photo)
	CircleImageView mUserPhoto;
	@InjectView(R.id.match_my_photo)
	CircleImageView mMyPhoto;
	@InjectView(R.id.match_begin)
	TextView mBegin;
	@InjectView(R.id.match_username)
	TextView mUserName;
	@InjectView(R.id.match_goto_profile)
	TextView mGotoHisProfile;

	private View view;
	private User mUser;
	private MusicPlayer musicPlayer;

	public MatchPopupWindow(Context context) {
		mContext = context;
		Injector.inject(this);
		LayoutInflater lInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = (ViewGroup) lInflater.inflate(R.layout.act_match, new LinearLayout(context), false);
		ButterKnife.inject(this, view);
		font.changeFonts((ViewGroup) view, Font.ENCODESANSCOMPRESSED_400_REGULAR);
		musicPlayer = new MusicPlayer(mContext);
		initView();
	}

	private void initView() {

		notNow.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
		});

		mBegin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new ChatUtil().chatWith(mUser.id);
				dismiss();
			}
		});
		mGotoHisProfile.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
				Bundle bundle = new Bundle();
				bundle.putParcelable(User.TAG, mUser);

				if (contextUtil.getForegroundAct() != null) {
					((BaseFragAct) contextUtil.getForegroundAct()).startFragmentHasAnim(Profile2Fragment.class, bundle, true, R.anim.left_in, R.anim.stop);
				}
			}
		});
	}

	public void init(Bitmap b, User user) {
		if (user == null) {
			user = new User();
			return;
		}
		mUser = user;
		setContentView(layout);
		setHeight(LayoutParams.MATCH_PARENT);
		setWidth(LayoutParams.MATCH_PARENT);
		drawBackground(layout, b);
		// Log.i("BASE_FRAG", "time:" + (System.currentTimeMillis() - time));
		// setFocusable(true);
		// setOutsideTouchable(false);
		setAnimationStyle(android.R.style.Animation_InputMethod);

		// Log.i("ALOHA_MATCHPOPUP", userphoto);
		int px = UiUtils.dip2px(mContext, 130);
		picasso.load(ImageUtil.getImageUrl(mUser.avatarImage.id, px, CropMode.ScaleCenterCrop))//
				.placeholder(R.color.gray_text)//
				.into(mUserPhoto);
		if (contextUtil.getCurrentUser() != null) {
			picasso.load(ImageUtil.getImageUrl(contextUtil.getCurrentUser().avatarImage.id, px, CropMode.ScaleCenterCrop))//
					.placeholder(R.color.gray_text)//
					.into(mMyPhoto);
		}

		mUserName.setText(mContext.getString(R.string.someone_aloha_you, mUser.name));
	}

	public void show(View view) {
		playMusic();
		// Log.i("TIME_LONG", "TIME:" + (System.currentTimeMillis() - time));
		if (view == null) {
			super.showAtLocation(layout, Gravity.BOTTOM, 0, 0);
		} else {
			super.showAtLocation(view, Gravity.BOTTOM, 0, 0);
		}

	}

	private void playMusic() {
		if (ContextConfig.getInstance().getBooleanWithDefValue(GlobalConstants.AppConstact.PUSH_SOUND, true)) {
			musicPlayer.playBgSound(R.raw.popcorn);
		}
		if (ContextConfig.getInstance().getBooleanWithDefValue(GlobalConstants.AppConstact.PUSH_VIBRATION, true)) {
			Vibrator vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
			vibrator.vibrate(500);
		}
	}

	int fuzzyRatio = 50;// 模糊比率

	private void drawBackground(ViewGroup layout, Bitmap bitmap) {
		// Bitmap b = blurRendererLite.blurBitmap(bitmap, 25, true);

		final View blurView = layout;
		BlurRenderTest bt = new BlurRenderTest();
		Bitmap blurBit = null;
		try {
			blurBit = bt.fastblur(bitmap, fuzzyRatio);
		} catch (OutOfMemoryError e) {
			blurBit = bt.fastblur(bitmap, fuzzyRatio -= 5);
		}
		if (bt != null) {
			// sdk兼容处理
			if (android.os.Build.VERSION.SDK_INT >= 16) {
				setBackgroundV16Plus(blurView, blurBit);
			} else {
				setBackgroundV16Minus(blurView, blurBit);
			}
		}

	}

	@TargetApi(16)
	private void setBackgroundV16Plus(View v, Bitmap backimg) {
		v.setBackground(new BitmapDrawable(mContext.getResources(), backimg));
	}

	@SuppressWarnings("deprecation")
	private void setBackgroundV16Minus(View v, Bitmap backimg) {
		v.setBackgroundDrawable(new BitmapDrawable(mContext.getResources(), backimg));
	}

}
