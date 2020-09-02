package com.wealoha.social.view.custom;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;
import androidx.fragment.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.wealoha.social.R;
import com.wealoha.social.api.ServerApi;
import com.wealoha.social.beans.ShareApp;
import com.wealoha.social.beans.ImageUploadResult;
import com.wealoha.social.beans.ApiResponse;
import com.wealoha.social.beans.PromotionGetData;
import com.wealoha.social.callback.CallbackImpl;
import com.wealoha.social.commons.GlobalConstants;
import com.wealoha.social.impl.ShareCallbackImpl;
import com.wealoha.social.inject.Injector;
import com.wealoha.social.store.ShareStore;
import com.wealoha.social.utils.ContextUtil;
import com.wealoha.social.utils.FontUtil;
import com.wealoha.social.utils.ImageUtil;
import com.wealoha.social.utils.ImageUtil.CropMode;
import com.wealoha.social.utils.ToastUtil;
import com.wealoha.social.utils.UiUtils;
import com.wealoha.social.view.custom.popup.LoadingPopup;

public class ShareDialogFragment extends Fragment implements IWeiboHandler.Response, OnClickListener {

	private View rootView;
	private TextView cancleTv;
	private TextView contentTv;
	private TextView titleTv;
	private TextView ruleTv;
	private TextView shareFuncTitle;
	private ViewGroup appsRoot;
	private PromotionGetData data;
	private IWXAPI iwxapi;
	boolean isTimelineCb;
	private RelativeLayout contentRoot;
	private FrameLayout coverRoot;
	@Inject
	ContextUtil contextUtil;
	@Inject
	ServerApi mUserService;
	@Inject
	Context mContext;
	LoadingPopup popup;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.dialog_share_frag, container, false);
		popup = new LoadingPopup(getActivity());
		Injector.inject(this);
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		data = (PromotionGetData) getArguments().get(PromotionGetData.TAG);
		findViews();
		fillAppsRoot();
		openAnimation();
	}

	@Override
	public void onResume() {
		super.onResume();
		popup.hide();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	/**
	 * 开启动画，分两部分, 主体部分由屏幕下方升起，类似输入法，背景遮罩层渐暗
	 * 
	 * @return void
	 */
	private void openAnimation() {
		// contentRoot.startAnimation(AnimationUtils.loadAnimation(getActivity(),
		// R.anim.alpha_in));

		Animation dialogClose = AnimationUtils.loadAnimation(getActivity(), R.anim.inputmethod_type_dialog);
		Animation coverClose = AnimationUtils.loadAnimation(getActivity(), R.anim.inputmethod_type_dialog_cover);
		coverClose.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				coverRoot.setEnabled(false);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				coverRoot.setEnabled(true);
			}
		});
		contentRoot.startAnimation(dialogClose);
		coverRoot.startAnimation(coverClose);
	}

	/**
	 * 开启动画的逆动画
	 * 
	 * @return void
	 */
	private void closeAnimation() {
		Animation dialogClose = AnimationUtils.loadAnimation(getActivity(), R.anim.inputmethod_type_dialog_close);
		Animation coverClose = AnimationUtils.loadAnimation(getActivity(), R.anim.inputmethod_type_dialog_cover_close);
		dialogClose.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				coverRoot.setEnabled(false);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				getFragmentManager().popBackStack();
			}
		});

		contentRoot.startAnimation(dialogClose);
		coverRoot.startAnimation(coverClose);
	}

	private void findViews() {
		appsRoot = (ViewGroup) rootView.findViewById(R.id.apps_root);
		cancleTv = (TextView) rootView.findViewById(R.id.cancle_tv);
		contentRoot = (RelativeLayout) rootView.findViewById(R.id.content_root);
		coverRoot = (FrameLayout) rootView.findViewById(R.id.cover_root);
		contentTv = (TextView) rootView.findViewById(R.id.content_tv);
		titleTv = (TextView) rootView.findViewById(R.id.title_tv);
		ruleTv = (TextView) rootView.findViewById(R.id.rule_tv);
		shareFuncTitle = (TextView) rootView.findViewById(R.id.share_func_title);

		FontUtil.setSemiBoldTypeFace(mContext, titleTv);
		FontUtil.setRegulartypeFace(mContext, contentTv);
		FontUtil.setRegulartypeFace(mContext, ruleTv);
		FontUtil.setRegulartypeFace(mContext, cancleTv);
		FontUtil.setRegulartypeFace(mContext, shareFuncTitle);
		// ((TextView)
		// rootView.findViewById(R.id.title_tv)).setOnClickListener(new
		// OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// shareSina();
		// }
		// });

		cancleTv.setOnClickListener(this);
		coverRoot.setOnClickListener(this);
		initViews();
	}

	private void initViews() {
		contentTv.setText(Html.fromHtml(getString(R.string.introduction_of_advanced_functions_str, data.promotionCode)));
	}

	private void fillAppsRoot() {
		List<ShareApp> apps = initShareAppsData();
		int iconWidth = getResources().getDrawable(R.drawable.wechat_02).getMinimumWidth();
		int width = UiUtils.getScreenWidth(mContext) - iconWidth * apps.size();
		// int margin = width / (apps.size() + 1);
		int horMargin = width / (apps.size() * 2);

		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.setMargins(horMargin, 0, horMargin, 0);

		for (ShareApp shareApp : apps) {
			TextView iconView = new TextView(getActivity());
			iconView.setLayoutParams(params);
			iconView.setText(shareApp.getTitle());
			iconView.setTextSize(10);
			iconView.setGravity(Gravity.CENTER_HORIZONTAL);
			iconView.setCompoundDrawablePadding(UiUtils.dip2px(mContext, 6));
			Drawable d = shareApp.getIcon();
			d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicWidth());
			iconView.setCompoundDrawables(null, d, null, null);
			iconView.setId(shareApp.getId());
			iconView.setOnClickListener(shareApp.getClickListener());
			FontUtil.setRegulartypeFace(mContext, iconView);
			appsRoot.addView(iconView);
		}
	}

	/***
	 * 配置icon 的信息，包括图标和标题
	 * 
	 * @return
	 * @return List<ShareApp>
	 */
	public List<ShareApp> initShareAppsData() {
		ArrayList<ShareApp> apps = new ArrayList<ShareApp>();
		apps.add(new ShareApp(getActivity(), R.drawable.wechat_02, R.string.wechat, this));
		apps.add(new ShareApp(getActivity(), R.drawable.moments_02, R.string.wechat_friend_, this));
		apps.add(new ShareApp(getActivity(), R.drawable.facebook_02, R.string.facebook, this));
		apps.add(new ShareApp(getActivity(), R.drawable.weibo_02, R.string.sina_weibo, this));
		apps.add(new ShareApp(getActivity(), R.drawable.messages_02, R.string.message, this));
		return apps;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.cancle_tv:
		case R.id.cover_root:
			closeFragment();
			break;
		case R.drawable.wechat_02:
			shareWechat(v.getId());
			break;
		case R.drawable.moments_02:
			shareWechat(v.getId());
			break;
		case R.drawable.facebook_02:
			shareFacebook();
			break;
		case R.drawable.weibo_02:
			shareSina();
			break;
		case R.drawable.messages_02:
			shareToSms();
			break;
		default:
			break;
		}
	}

	/**
	 * @Description:
	 * @see:
	 * @since:
	 * @description
	 * @author: sunkist
	 * @date:2015年7月10日
	 */
	public void shareSina() {
		popup.show();
		Bundle bundle = new Bundle();
		bundle.putString("permalink", "http://wealoha.com/get");
		bundle.putString("code", data.promotionCode);
		bundle.putString("imgUrl", ImageUtil.getImageUrl(contextUtil.getCurrentUser().getAvatarImage().getId(), UiUtils.dip2px(//
		getActivity(), GlobalConstants.ImageSize.AVATAR_ROUND_SMALL), CropMode.ScaleCenterCrop));
		ShareStore.shareSina(getActivity(), bundle, this, new ShareCallbackImpl() {

			@Override
			public void failure() {
				popupDismiss();
			}

			private void popupDismiss() {
				getHandler().post(new Runnable() {

					@Override
					public void run() {
						popup.hide();
						closeFragment();
					}
				});
			}

			@Override
			public void success() {
				popupDismiss();
			}

		});
	}

	/**
	 * @Description:
	 * @see:
	 * @since:
	 * @description
	 * @author: sunkist
	 * @date:2015年7月10日
	 */
	public void shareFacebook() {
		if (popup != null) {
			popup.show();
		}
		// FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
		// 获取分享到Facebook的图片
		ImageUtil.getFacebookShareImage(contextUtil.getCurrentUser(), ImageUtil.getImageUrl(contextUtil.getCurrentUser().getAvatarImage().getId(), UiUtils.dip2px(//
		getActivity(), GlobalConstants.ImageSize.AVATAR_ROUND_SMALL), CropMode.ScaleCenterCrop), new CallbackImpl() {

			@Override
			public void success(final String string) {
				getHandler().post(new Runnable() {

					@Override
					public void run() {
						if (TextUtils.isEmpty(string)) {
							popup.hide();
							ToastUtil.shortToast(getActivity(), R.string.is_not_work);
							return;
						}
						// 上传图片到服务器
						File shareFile = new File(string);
						mUserService.sendSingleFeed(new TypedFile("application/octet-stream", shareFile), new Callback<ApiResponse<ImageUploadResult>>() {

							@Override
							public void failure(RetrofitError arg0) {
								popup.hide();
							}

							@Override
							public void success(ApiResponse<ImageUploadResult> apiResponse, Response arg1) {
								popup.hide();
								if (apiResponse != null && apiResponse.isOk()) {
								}
							}
						});
					}
				});
			}

			@Override
			public void failure() {
			}

		}, getActivity());

	}

	public void closeFragment() {
		closeAnimation();
	}

	private void shareWechat(long i) {
		iwxapi = WXAPIFactory.createWXAPI(getActivity(), GlobalConstants.AppConstact.QQ_WX_APPID, true);
		iwxapi.registerApp(GlobalConstants.AppConstact.QQ_WX_APPID);
		if (i == R.drawable.wechat_02) {
			isTimelineCb = false;
		} else if (i == R.drawable.moments_02) {
			isTimelineCb = true;
		}
		WXTextObject textObj = new WXTextObject();
		textObj.text = getString(R.string.advance_features_gay_aloha_Invitation, data.promotionCode, "http://wealoha.com/get");

		WXMediaMessage msg = new WXMediaMessage();
		msg.mediaObject = textObj;
		msg.description = getString(R.string.advance_features_gay_aloha_Invitation, data.promotionCode, "http://wealoha.com/get");

		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.message = msg;
		req.scene = isTimelineCb ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
		iwxapi.sendReq(req);
	}

	private void shareToSms() {
		Uri smsToUri = Uri.parse("smsto:");
		Intent sendIntent = new Intent(Intent.ACTION_VIEW, smsToUri);
		// FIXME 分享邀请码的东西
		// try {
		sendIntent.putExtra("sms_body", getResources().getString(R.string.advance_features_gay_aloha_Invitation, data.promotionCode, "http://wealoha.com/get"));
		startActivity(sendIntent);
	}

	@Override
	public void onResponse(BaseResponse response) {

	}

	private Handler mHandler;

	public Handler getHandler() {
		if (mHandler != null) {
			return mHandler;
		} else {
			return (mHandler = new Handler(getActivity().getMainLooper()));
		}
	}
}
