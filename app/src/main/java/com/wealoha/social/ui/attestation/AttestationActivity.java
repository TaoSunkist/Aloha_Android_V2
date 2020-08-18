package com.wealoha.social.ui.attestation;

import javax.inject.Inject;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;

import com.wealoha.social.BaseFragAct;
import com.wealoha.social.ContextConfig;
import com.wealoha.social.R;
import com.wealoha.social.presenters.AttestationPresenter;
import com.wealoha.social.ui.lock.GestureLockAct;
import com.wealoha.social.utils.ContextUtil;
import com.wealoha.social.utils.FontUtil;
import com.wealoha.social.utils.StringUtil;
import com.wealoha.social.utils.ToastUtil;
import com.wealoha.social.utils.UiUtils;
import com.wealoha.social.utils.XL;
import com.wealoha.social.view.custom.dialog.LogoutAndClearActDialogBuilder;

/**
 * @author:sunkist
 * @description:密码验证界面
 * @Date:2015年7月30日
 */
public class AttestationActivity extends BaseFragAct implements IAttestationView {

	@Inject
	ContextUtil contextUtil;

	@InjectView(R.id.password_et)
	EditText mPasswordBox;

	@InjectView(R.id.attestation_next_tv)
	TextView mNextView;

	@InjectView(R.id.user_phone_num)
	TextView mPhoneNumView;

	@InjectView(R.id.password_state)
	TextView mPasswordStateView;

	@InjectView(R.id.other_account_login)
	TextView mOtherAccountLoginView;

	@InjectView(R.id.attestation_title)
	TextView mTitleView;

	@InjectView(R.id.menu_bar)
	RelativeLayout mMenuBar;

	@InjectView(R.id.root_view)
	RelativeLayout mRootView;

	@InjectView(R.id.input_view_layout)
	LinearLayout mInputViewLayout;

	@InjectView(R.id.loading_popup)
	RelativeLayout mLoadingLayout;

	/**
	 * 解锁状态下的标题栏，在其他模式下时隐藏的
	 */
	@InjectView(R.id.unlock_menu_bar)
	RelativeLayout mUnlockTypeMenubar;

	private AttestationPresenter mAttestationP;

	/**
	 * 本地保存密码错误次数的key
	 */
	public static final String ATTESTATION_PASSWORD_COUNTER_KEY = "attestation_password_counter_key";
	public static final int CHANGE_GESTURE_ATTESTATION = 0x0001;
	/**
	 * 删除手势锁时忘记密码的验证
	 */
	public static final int DELETE_GESTURE_ATTESTATION = 0x0003;
	/**
	 * 设置手势锁之前的验证
	 */
	public static final int DEFAULT_ATTESTATION = 0x0002;
	/**
	 * 解锁时忘记手势密码，进行登录密码验证
	 */
	public static final int UNLOCK_ATTESTATION = 0x0004;
	private int attestationType;
	public static final String ATTESTATION_TYPE_KEY = "ATTESTATION_TYPE_KEY";

	private int passwordCounter;
	private Dialog updateAlert;

	private Animation crazyShakeAnim;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_attestation);

		attestationType = getIntent().getIntExtra(ATTESTATION_TYPE_KEY, DEFAULT_ATTESTATION);
		initView();

		passwordCounter = ContextConfig.getInstance().getIntWithFilename(ATTESTATION_PASSWORD_COUNTER_KEY, 4);
		mOtherAccountLoginView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				updateDialog("确定么？");
			}
		});
		mAttestationP = new AttestationPresenter(this);
		mPasswordBox.requestFocus();
		mPasswordBox.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				mPasswordStateView.setTextColor(getResources().getColor(R.color.black_text));
				mPasswordStateView.setText(R.string.input_pw_attestation_str);
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		crazyShakeAnim = AnimationUtils.loadAnimation(mContext, R.anim.crazy_shake);
	}

	private void initView() {
		switch (attestationType) {
		case DEFAULT_ATTESTATION:
		case DELETE_GESTURE_ATTESTATION:
		case CHANGE_GESTURE_ATTESTATION:
			mPhoneNumView.setVisibility(View.GONE);
			mOtherAccountLoginView.setVisibility(View.GONE);
			((LinearLayout.LayoutParams) mPasswordStateView.getLayoutParams()).bottomMargin = UiUtils.dip2px(mContext, 25);
			break;
		case UNLOCK_ATTESTATION:
			mPhoneNumView.setText(StringUtil.encryptNum(contextUtil.getAccountPhoneNumber()));
			mMenuBar.setVisibility(View.GONE);
			mUnlockTypeMenubar.setVisibility(View.VISIBLE);
			mRootView.setBackgroundColor(getResources().getColor(R.color.white_color));

			break;
		default:
			break;
		}

		FontUtil.setSemiBoldTypeFace(mContext, mMenuBar);
	}

	@OnClick({ R.id.attestation_back_tv, //
	R.id.attestation_next_tv,//
	R.id.unlock_attestation_next_tv,//
	R.id.unlock_attestation_back_tv })
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.attestation_back_tv:
		case R.id.unlock_attestation_back_tv:
			finish();
			overridePendingTransition(R.anim.stop, R.anim.right_out);
			break;
		case R.id.attestation_next_tv:
		case R.id.unlock_attestation_next_tv:
			if (TextUtils.isEmpty(mPasswordBox.getText().toString())) {
				ToastUtil.shortToast(mContext, R.string.you_entered_psd_is_wrong);
			} else {
				mLoadingLayout.setVisibility(View.VISIBLE);
				view.setEnabled(false);// 请求结束前禁用这个按钮
				mAttestationP.verifyPassword(StringUtil.md5(mPasswordBox.getText().toString()));
			}
			break;
		}
	}

	/**
	 * 如果开启该界面的的是{@link GestureLockAct}中的忘记密码功能，那么在密码验证正确的情况下，要设置setResult，以便手势解锁界面能够在当前界面关闭的情况下也关闭
	 * 
	 * @return
	 */
	@Override
	public void passwordRight() {
		// ToastUtil.shortToast(this, "密码验证成功");
		mLoadingLayout.setVisibility(View.GONE);
		initPasswordCounter();
		setResult(RESULT_OK);
		mNextView.setEnabled(true);

		switch (attestationType) {
		case DELETE_GESTURE_ATTESTATION:
			ContextConfig.getInstance().putStringWithFilename(GestureLockAct.LOCK_PASSWORD, null);
			break;
		case UNLOCK_ATTESTATION:
		case CHANGE_GESTURE_ATTESTATION:
			ContextConfig.getInstance().putStringWithFilename(GestureLockAct.LOCK_PASSWORD, null);
		case DEFAULT_ATTESTATION:
			startGestureLockForCreate();
			break;
		default:
			break;
		}
		finish();
	}

	@Override
	public void passwordError() {
		mLoadingLayout.setVisibility(View.GONE);
		mPasswordStateView.setTextColor(getResources().getColor(R.color.light_red));
		mNextView.setEnabled(true);
		if (attestationType == UNLOCK_ATTESTATION) {
			savePasswordCounter();
			checkPasswordCounter();
			mPasswordStateView.setText(getString(R.string.gestrue_lock_password_error, passwordCounter));
		} else {
			mPasswordStateView.setText(R.string.password_error_try_again);
		}
		mPasswordStateView.startAnimation(crazyShakeAnim);
	}

	/**
	 * 保存密码输入错误的次数, 每次{@link #passwordCounter}都递减
	 * 
	 * @return void
	 */
	private void savePasswordCounter() {
		ContextConfig.getInstance().putIntWithFilename(ATTESTATION_PASSWORD_COUNTER_KEY, passwordCounter--);
	}

	private void checkPasswordCounter() {
		if (passwordCounter == 0) {
			doLogout(mContext);
			closeAllAct(this);
		}
	}

	/**
	 * 初始化本地保存的密码错误次数计数器的值，
	 * 
	 * @return void
	 */
	private void initPasswordCounter() {
		ContextConfig.getInstance().putIntWithFilename(GestureLockAct.LOCK_PASSWORD_COUNTER_KEY, 4);
		ContextConfig.getInstance().putIntWithFilename(AttestationActivity.ATTESTATION_PASSWORD_COUNTER_KEY, 4);
	}
	
	protected void startGestureLockForCreate() {
		XL.i("ATTESTATIION_START_GESTURELOCK", "startGestureLock");
		Intent lockIntent = null;
		lockIntent = new Intent(this, GestureLockAct.class);
		lockIntent.putExtra(GestureLockAct.LOCK_TYPE, GestureLockAct.LOCK_CREATE);
		startActivity(lockIntent);
	}

	private void updateDialog(String updateDetails) {
		if (updateAlert == null) {
			updateAlert = new LogoutAndClearActDialogBuilder(mContext,//
			getString(R.string.use_other_account_logon),//
			getString(R.string.prompt_need_logoff_current_account)).//
			create();
		}
		updateAlert.show();
	}
	
}
