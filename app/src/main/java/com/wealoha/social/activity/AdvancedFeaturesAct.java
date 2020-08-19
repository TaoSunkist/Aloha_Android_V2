package com.wealoha.social.activity;

import javax.inject.Inject;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;

import com.wealoha.social.AsyncLoader;
import com.wealoha.social.BaseFragAct;
import com.wealoha.social.R;
import com.wealoha.social.beans.Result;
import com.wealoha.social.beans.PromotionGetData;
import com.wealoha.social.api.UserPromotionService;
import com.wealoha.social.utils.FontUtil;
import com.wealoha.social.utils.FontUtil.Font;
import com.wealoha.social.utils.ToastUtil;

public class AdvancedFeaturesAct extends BaseFragAct implements LoaderManager.LoaderCallbacks<Result<PromotionGetData>> {

	@Inject
	UserPromotionService userPromotionService;
	@Inject
	FontUtil fontUtil;

	@InjectView(R.id.text_invite_quota_reset_rules)
	TextView textQuotaResetRules;
	@InjectView(R.id.title)
	TextView mTitle;

	@InjectView(R.id.text_promotion_code)
	TextView textPromotionCode;

	@InjectView(R.id.advanced_back_tv)
	ImageView backBtn;

	@InjectView(R.id.quota_left)
	TextView textQuotaResetLeft;
	@InjectView(R.id.speed_up_tv)
	TextView mSpeedUp;

	@InjectView(R.id.wechat_tv)
	TextView mWechatTv;
	@InjectView(R.id.wechatmoment_tv)
	TextView mWechatMomentTv;
	@InjectView(R.id.message_tv)
	TextView mMessageTv;

	@InjectView(R.id.advance_features_share_wechat_rl)
	RelativeLayout weChatFriend;
	@InjectView(R.id.advanced_feadtures_wx_friends_rl)
	RelativeLayout advanced_feadtures_wx_friends_rl;
	@InjectView(R.id.send_sms_share_to_friends_rl)
	RelativeLayout send_sms_share_to_friends_rl;

	private static final int REQUEST_CODE_CODE_LOAD = 0;

	private PromotionGetData proData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_advanced_features);

		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			proData = (PromotionGetData) bundle.get(PromotionGetData.TAG);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		// getLoaderManager().initLoader(REQUEST_CODE_CODE_LOAD, null, this);
		if (proData != null) {
			updateData(proData);
		}
	}

	@Override
	protected void initTypeFace() {
		fontUtil.changeViewFont(mTitle, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
		fontUtil.changeViewFont(mSpeedUp, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
		fontUtil.changeViewFont(mWechatTv, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
		fontUtil.changeViewFont(mWechatMomentTv, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
		fontUtil.changeViewFont(mMessageTv, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
	}

	@Override
	public Loader<Result<PromotionGetData>> onCreateLoader(int i, Bundle bundle) {

		Log.d(TAG, "onCreateLoader....");
		if (i == REQUEST_CODE_CODE_LOAD) {
			return new AsyncLoader<Result<PromotionGetData>>(this) {

				@Override
				public Result<PromotionGetData> loadInBackground() {
					try {
						return userPromotionService.get();
					} catch (Exception e) {
						Log.d(TAG, e.getMessage() + ": " + e.getStackTrace().toString());
						e.printStackTrace();
					}
					return null;
				}
			};
		}
		return null;
	}

	@OnClick({ R.id.advanced_back_tv, R.id.advance_features_share_wechat_rl, R.id.advanced_feadtures_wx_friends_rl, R.id.send_sms_share_to_friends_rl })
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.advanced_back_tv:
			onBackPressed();
			break;
		case R.id.advance_features_share_wechat_rl:
		case R.id.advanced_feadtures_wx_friends_rl:
			shareWechat(v.getId());
			break;
		case R.id.send_sms_share_to_friends_rl:
			Uri smsToUri = Uri.parse("smsto:");
			Intent sendIntent = new Intent(Intent.ACTION_VIEW, smsToUri);
			sendIntent.putExtra("sms_body", getResources().getString(R.string.advance_features_gay_aloha_Invitation, //
					data.promotionCode, "http://wealoha.com/get"));
			startActivity(sendIntent);
			break;
		default:
			break;
		}
	}

	boolean isTimelineCb;

	private void shareWechat(long i) {

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {

		case 0x101: {

			break;
		}
		default:
			break;
		}
	}

	private String buildTransaction(String string) {
		return (string == null) ? String.valueOf(System.currentTimeMillis()) : string + System.currentTimeMillis();
	}

	PromotionGetData data;

	private void updateData(PromotionGetData data) {
		this.data = data;
		textQuotaResetLeft.setText(getString(R.string.label_invite_quota_reset_left, data.quotaReset));

		textQuotaResetRules.setText(getString(R.string.label_invite_quota_reset_get, data.quotaPerPerson));
		// 邀请码
		textPromotionCode.setText(getString(R.string.label_invite_promotion_code, data.promotionCode));
		// TODO 第三方邀请使用的文案
	}

	@Override
	public void onLoadFinished(Loader<Result<PromotionGetData>> resultLoader, Result<PromotionGetData> result) {
		if (result == null) {
			return;
		}
		int loaderId = resultLoader.getId();

		if (loaderId == REQUEST_CODE_CODE_LOAD) {
			PromotionGetData r = (PromotionGetData) result.data;
			if (result.isOk()) {
				updateData(r);
			} else {
				ToastUtil.shortToast(this, getString(R.string.network_error));
			}
		}
	}

	@Override
	public void onLoaderReset(Loader<Result<PromotionGetData>> resultLoader) {

	}

}