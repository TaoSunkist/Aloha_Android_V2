package com.wealoha.social.ui.privacy;

import javax.inject.Inject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;

import com.wealoha.social.BaseFragAct;
import com.wealoha.social.ContextConfig;
import com.wealoha.social.R;
import com.wealoha.social.commons.GlobalConstants;
import com.wealoha.social.fragment.SwipeMenuListFragment;
import com.wealoha.social.presenters.PrivacyPresenter;
import com.wealoha.social.ui.attestation.AttestationActivity;
import com.wealoha.social.ui.lock.GestureLockAct;
import com.wealoha.social.utils.ContextUtil;
import com.wealoha.social.utils.FontUtil;
import com.wealoha.social.utils.NetworkUtil;
import com.wealoha.social.utils.ToastUtil;
import com.wealoha.social.utils.XL;
import com.wealoha.social.utils.FontUtil.Font;
import com.wealoha.social.view.custom.SlideSwitch;

/**
 * @author:sunkist
 * @description:用户的隐私界面
 * @Date:2015年7月30日
 */
public class PrivacyActivity extends BaseFragAct implements IPrivacyView {

	@InjectView(R.id.switch_gesture_password_ss)
	SlideSwitch mSwitchGesturePw;
	@InjectView(R.id.switch_stealth_mode_ss)
	SlideSwitch mSwitchStealthMode;
	@InjectView(R.id.alert_gesture_password_rl)
	RelativeLayout mAlertGesturePwWrap;
	@InjectView(R.id.privacy_range_wrap_rl)
	LinearLayout mInvisibleRangeWrap;
	@InjectView(R.id.privacy_range_sb)
	SeekBar mInvisiableRangeBar;
	@InjectView(R.id.dynamic_prompt_tv)
	TextView mRangePrompt;
	@InjectView(R.id.open_gesture_des_tv)
	TextView mOpenGestureDes;
	@InjectView(R.id.switch_gesture_password_wrap)
	RelativeLayout mSwitchGesturePwWrap;
	@Inject
	ContextUtil mContextUtil;
	PrivacyPresenter mPrivacyP;
	@InjectView(R.id.stealth_mode_des_tv)
	TextView mStealthModeDes;
	@InjectView(R.id.switch_ring_tv)
	TextView switch_ring_tv;
	int mRedColor;
	int mGrayColor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_privacy);
		FontUtil.setRegulartypeFace(this, mOpenGestureDes);
		FontUtil.setRegulartypeFace(this, mStealthModeDes);

		mPrivacyP = new PrivacyPresenter(this);
		mSwitchGesturePw.setOnSwitchChangedListener(this);
		mSwitchStealthMode.setOnSwitchChangedListener(this);
		mInvisiableRangeBar.setOnSeekBarChangeListener(this);
		mRedColor = getResources().getColor(R.color.light_red);
		mGrayColor = getResources().getColor(R.color.black_text);
		if (mContextUtil.isPassportSinaWeibo()
				|| mContextUtil.isPassportFacebook()) {
			mOpenGestureDes.setVisibility(View.GONE);
			mSwitchGesturePwWrap.setVisibility(View.GONE);
		}
		// 从服务器获取隐身范围
		boolean isOpenGesturePw = ContextConfig.getInstance()
				.getBooleanWithFilename(GlobalConstants.TAGS.IS_GESTURE_PW);
		mSwitchGesturePw.setChecked(isOpenGesturePw);
		mAlertGesturePwWrap
				.setVisibility(mSwitchGesturePw.isChecked() ? View.VISIBLE
						: View.GONE);
		// 获取本地的隐身设置信息,如果有网络会从服务器拉去之后再显示.
		if (NetworkUtil.isNetworkAvailable()) {
			mPrivacyP.getPrivacy();
		} else {
			setPrivacyRange(ContextConfig.getInstance().getIntWithFilename(
					GlobalConstants.TAGS.PRIVACY_RANGE));
		}
	}

	@Override
	protected void changeFont(ViewGroup root) {
		fontUtil.changeFonts(root, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
		initTypeFace();
	}

	/**
	 * @author: sunkist
	 * @description:配置默认隐身参数
	 * @param isOpenGesturePw
	 * @return
	 * @date:2015年8月5日
	 */
	private void setDefaultPrivacyRange(int privacyRange) {
		if (privacyRange < 1) {// 为关闭状态
			mInvisibleRangeWrap.setVisibility(View.GONE);
		} else if (privacyRange >= 1 && privacyRange < 201) {
			mRangePrompt.setText(getString(R.string.dynamic_prompt_range_str,
					privacyRange));
			mRangePrompt.setTextColor(mGrayColor);
		} else {
			mRangePrompt.setText(R.string.all_account_dont_look_str);
			mRangePrompt.setTextColor(mRedColor);
		}
		mInvisiableRangeBar.setProgress(privacyRange == 0 ? 1 : privacyRange);
		mSwitchStealthMode.setChecked(privacyRange == 0 ? false : true);
		mInvisibleRangeWrap.setVisibility(privacyRange == 0 ? View.GONE
				: View.VISIBLE);
		isCheckStealth(mSwitchStealthMode.isChecked());
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (checkGestureLockEnable()) {
			mSwitchGesturePw.setChecked(true);
			mAlertGesturePwWrap.setVisibility(View.VISIBLE);
		} else {
			mSwitchGesturePw.setChecked(false);
			mAlertGesturePwWrap.setVisibility(View.GONE);
		}
	}

	@OnClick({ R.id.privacy_back_tv, R.id.alert_gesture_password_rl,
			R.id.black_list_wrap_rl })
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.privacy_back_tv:
			onBackKeyPressed();
			break;
		case R.id.alert_gesture_password_rl:
			startCloseGestureLock(GestureLockAct.LOCK_CHANGE);
			break;
		case R.id.black_list_wrap_rl:
			Bundle bundle = new Bundle();
			SwipeMenuListFragment userList = new SwipeMenuListFragment();
			bundle.putInt("listtype", SwipeMenuListFragment.LISTTYPE_BLACK);
			userList.setArguments(bundle);
			if (mContextUtil.getForegroundAct() != null) {
				((BaseFragAct) mContextUtil.getForegroundAct()).startFragment(
						SwipeMenuListFragment.class, bundle, true,
						R.anim.left_in, R.anim.stop);
			}
			break;
		}
	}

	@Override
	public void onSwitchChanged(SlideSwitch obj, boolean isChecked) {
		switch (obj.getId()) {
		case R.id.switch_gesture_password_ss:
			if (isChecked) {
				startAttestation();
			} else if (checkGestureLockEnable()) {
				startCloseGestureLock(GestureLockAct.LOCK_CLEAR);
			}
			break;
		case R.id.switch_stealth_mode_ss:
			if (isChecked) {
				mInvisibleRangeWrap.setVisibility(View.VISIBLE);
				mInvisiableRangeBar.setProgress(1);
			} else {
				mInvisiableRangeBar.setProgress(0);
				mInvisibleRangeWrap.setVisibility(View.GONE);

			}
			isCheckStealth(isChecked);
			break;
		}
	}

	public void isCheckStealth(boolean isOpen) {
		if (isOpen) {
			mStealthModeDes
					.setText(R.string.stealth_mode_close_description_str);
		} else {
			mStealthModeDes.setText(R.string.stealth_mode_description_str);
		}
	}

	/**
	 * 开启验证密码界面
	 * 
	 * @return void
	 */
	private void startAttestation() {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(GlobalConstants.IntentAction.INTENT_URI_ATTESTATION);
		intent.putExtra(AttestationActivity.ATTESTATION_TYPE_KEY,
				AttestationActivity.DEFAULT_ATTESTATION);
		startActivity(intent);
		overridePendingTransition(R.anim.left_in, R.anim.stop);
	}

	/**
	 * 开启手势锁界面
	 * 
	 * @param lockType
	 *            {@link GestureLockAct#LOCK_CHANGE},
	 *            {@link GestureLockAct#LOCK_CLEAR},
	 *            {@link GestureLockAct#LOCK_CREATE},
	 *            {@link GestureLockAct#LOCK_UNLOCK}
	 * @return void
	 */
	private void startCloseGestureLock(int lockType) {
		Intent lockIntent = null;
		lockIntent = new Intent(this, GestureLockAct.class);
		lockIntent.putExtra(GestureLockAct.LOCK_TYPE, lockType);
		startActivity(lockIntent);
	}

	private boolean checkGestureLockEnable() {
		XL.i("CREATE_LOCK_PASSWORD",
				"get:"
						+ ContextConfig.getInstance().getStringWithFilename(
								GestureLockAct.LOCK_PASSWORD, "null"));
		if (TextUtils.isEmpty(ContextConfig.getInstance()
				.getStringWithFilename(GestureLockAct.LOCK_PASSWORD, null))) {
			return false;
		}
		return true;
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		switch (seekBar.getId()) {
		case R.id.privacy_range_sb:
			if (progress >= 1 && progress < 201) {
				mRangePrompt.setText(getString(
						R.string.dynamic_prompt_range_str, progress));
				mRangePrompt.setTextColor(mGrayColor);
			} else if (progress >= 201) {
				mRangePrompt.setText(R.string.all_account_dont_look_str);
				mRangePrompt.setTextColor(mRedColor);
			} else {
				seekBar.setProgress(0);
			}
			break;
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
	}

	@Override
	protected void onDestroy() {
		saveConfig();
		super.onDestroy();
	}

	public void saveConfig() {
		ContextConfig.getInstance().putBooleanWithFilename(
				GlobalConstants.TAGS.IS_GESTURE_PW,//
				mSwitchGesturePw.isChecked());
		ContextConfig.getInstance().putIntWithFilename(
				GlobalConstants.TAGS.PRIVACY_RANGE,//
				mSwitchStealthMode.isChecked() ? mInvisiableRangeBar
						.getProgress() : 0);
		mPrivacyP
				.setPrivacy(mSwitchStealthMode.isChecked() ? mInvisiableRangeBar
						.getProgress() : null);
	}

	@Override
	public void getPrivacyRange(boolean isSuccess, int invisibleRange) {
		if (isSuccess) {// 获取成功
			setDefaultPrivacyRange(invisibleRange);
		} else {// 获取失败,设置默认值
			ToastUtil.shortToast(this, R.string.network_error);
		}
	}

	@Override
	public void setPrivacyRange(int privacyRange) {
		setDefaultPrivacyRange(privacyRange);
	}

}
