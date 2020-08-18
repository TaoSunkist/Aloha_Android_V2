package com.wealoha.social.ui.lock;

import javax.inject.Inject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;

import com.wealoha.social.BaseFragAct;
import com.wealoha.social.ContextConfig;
import com.wealoha.social.R;
import com.wealoha.social.commons.GlobalConstants;
import com.wealoha.social.ui.attestation.AttestationActivity;
import com.wealoha.social.utils.ContextUtil;
import com.wealoha.social.utils.StringUtil;
import com.wealoha.social.utils.ToastUtil;
import com.wealoha.social.utils.UiUtils;
import com.wealoha.social.utils.XL;
import com.wealoha.social.view.custom.LockView;
import com.wealoha.social.view.custom.LockView.LockViewListener;
import com.wealoha.social.view.custom.dialog.LogoutAndClearActDialogBuilder;

public class GestureLockAct extends BaseFragAct implements LockViewListener, OnClickListener {

	@Inject
	ContextUtil contextUtil;
	@InjectView(R.id.text_view)
	TextView textView;

	@InjectView(R.id.user_phone_num)
	TextView userPhoneNumView;

	@InjectView(R.id.lock_view)
	LockView lockView;

	@InjectView(R.id.forget_psw)
	TextView forgetPswView;

	@InjectView(R.id.other_account)
	TextView otherAccountLoginView;

	@InjectView(R.id.menu_bar)
	RelativeLayout menuBarLayout;

	@InjectView(R.id.back)
	ImageView backView;

	@InjectView(R.id.title)
	TextView titleView;

	String tempPassword;

	public static final String LOCK_PASSWORD = "LOCK_PASSWORD";
	public static final String LOCK_TYPE = "LOCK_TYPE";
	/**
	 * 解锁失败的次数
	 */
	public static final String LOCK_PASSWORD_COUNTER_KEY = "LOCK_PASSWORD_COUNTER";
	/**
	 * 关闭前一个act
	 */
	public static final int START_ATTESTATION_REQUESTCODE = 0x5555;

	public static final int LOCK_CLEAR = 0x0001;
	public static final int LOCK_CREATE = 0x0002;
	public static final int LOCK_UNLOCK = 0x0003;
	public static final int LOCK_CHANGE = 0x0004;

	public int lockType = LOCK_UNLOCK;
	private int lockPasswordCounter;

	private Animation crazyShakeAnim;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_gesture_lock);
		lockView.setLockViewListener(this);
		lockType = getIntent().getIntExtra(LOCK_TYPE, LOCK_UNLOCK);
		lockPasswordCounter = ContextConfig.getInstance().getIntWithFilename(LOCK_PASSWORD_COUNTER_KEY, 4);
		initTextView();

		crazyShakeAnim = AnimationUtils.loadAnimation(mContext, R.anim.crazy_shake);
	}

	/**
	 * 初始化锁屏界面显示文案
	 * 
	 * @return void
	 */
	public void initTextView() {
		setDefaultText(textView, getString(R.string.enter_gestrue_lock_password));
		switch (lockType) {
		case LOCK_CLEAR:
		case LOCK_CHANGE:
			// 隐藏其他账户登录，将忘记密码至于底部的中间位置
			otherAccountLoginView.setVisibility(View.GONE);
			LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			params.setMargins(0, 0, 0, UiUtils.dip2px(mContext, 25));
			forgetPswView.setLayoutParams(params);
			forgetPswView.setGravity(Gravity.CENTER);
			titleView.setText(R.string.pattern_reset);
			break;
		case LOCK_CREATE:
			forgetPswView.setVisibility(View.GONE);
			otherAccountLoginView.setVisibility(View.GONE);
			titleView.setText(R.string.pattern_reset);
			setDefaultText(textView, getString(R.string.enter_new_gestrue_lock_password));
			break;
		case LOCK_UNLOCK:
			menuBarLayout.setVisibility(View.GONE);
			userPhoneNumView.setVisibility(View.VISIBLE);
			break;
		default:
			break;
		}
		userPhoneNumView.setText(StringUtil.encryptNum(contextUtil.getAccountPhoneNumber()));
	}

	@Override
	public void finish(String passwords) {
		if (TextUtils.isEmpty(passwords)) {
			return;
		}

		if (passwords.length() < 4) {
			tempPassword = null;
			setErrorText(textView, getString(R.string.gestrue_lock_password_lenght_error));
			delayResetLockView();
			return;
		}

		if (lockType == LOCK_CREATE) {
			createGesture(passwords);
			return;
		}

		if (!checkGesturePassword(passwords)) {
			return;
		}

		switch (lockType) {
		case LOCK_CLEAR:
			clearGesture();
			break;
		case LOCK_UNLOCK:
			checkGesture(passwords);
			break;
		case LOCK_CHANGE:
			changeGesture(passwords);
		default:
			break;
		}
	}

	/**
	 * 创建手势锁
	 * 
	 * @return void
	 */
	public void createGesture(String passwords) {
		XL.i("CREATE_LOCK_PASSWORD", "password:" + passwords);
		XL.i("CREATE_LOCK_PASSWORD", "tempPassword:" + tempPassword);
		if (!TextUtils.isEmpty(tempPassword)) {
			if (tempPassword.equals(passwords)) {
				// textView.setText("");
				save(tempPassword);
			} else {
				setErrorText(textView, getString(R.string.login_error_pwd_to_diff));
				tempPassword = null;
				delayResetLockView();
			}
		} else {
			setDefaultText(textView, getString(R.string.enter_gestrue_lock_password_again));
			tempPassword = passwords;
			lockView.clearDrowed();
		}
	}

	/**
	 * 已经设置了手势锁的情况下，验证手势锁
	 * 
	 * @return void
	 */
	public void checkGesture(String password) {
		// textView.setText("");
		finishAction();
	}

	/**
	 * 关闭手势锁
	 * 
	 * @return void
	 */
	public void clearGesture() {
		ContextConfig.getInstance().putStringWithFilename(LOCK_PASSWORD, null);
		// textView.setText("");
		finishAction();
	}

	public void changeGesture(String password) {
		// ContextConfig.getInstance().putStringWithFilename(LOCK_PASSWORD, null);
		Intent intent = new Intent(this, GestureLockAct.class);
		intent.putExtra(GestureLockAct.LOCK_TYPE, GestureLockAct.LOCK_CREATE);
		startActivity(intent);
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
		finish();
		// lockType = LOCK_CREATE;
		// tempPassword = null;
		// initTextView();
		// setDefaultText(textView, getString(R.string.enter_new_gestrue_lock_password));
		// lockView.clearDrowed();
	}

	/**
	 * 检验输入的手势锁密码和本地保存的密码是否一直
	 * 
	 * @param password
	 * @return boolean
	 */
	private boolean checkGesturePassword(String password) {
		tempPassword = ContextConfig.getInstance().getStringWithFilename(LOCK_PASSWORD, null);

		if (md5Password(password).equals(tempPassword)) {
			ContextConfig.getInstance().putIntWithFilename(LOCK_PASSWORD_COUNTER_KEY, 4);
			ContextConfig.getInstance().putIntWithFilename(AttestationActivity.ATTESTATION_PASSWORD_COUNTER_KEY, 4);
			return true;
		}
		String error = null;
		if (lockType == LOCK_UNLOCK) {
			savePasswordCounter();
			checkPasswordCounter();
			error = getString(R.string.gestrue_lock_password_error, lockPasswordCounter);
		} else {
			error = getString(R.string.password_error_try_again);
		}
		setErrorText(textView, error);
		delayResetLockView();
		return false;
	}

	public void save(String password) {
		lockView.setEnable(false);
		ContextConfig.getInstance().putStringWithFilename(LOCK_PASSWORD, md5Password(password));
		finishAction();
	}

	/**
	 * 将 密码字段+附加字段 做md5加密
	 * 
	 * @param password
	 * @return String
	 */
	private String md5Password(String password) {
		return StringUtil.md5(password + getString(R.string.gesture_password_addition));
	}

	/**
	 * 解锁次数计数器，每次存储都减一
	 * 
	 * @return void
	 */
	private void savePasswordCounter() {
		if (lockPasswordCounter <= 0) {
			return;
		}
		ContextConfig.getInstance().putIntWithFilename(LOCK_PASSWORD_COUNTER_KEY, --lockPasswordCounter);
	}

	private void checkPasswordCounter() {
		if (lockPasswordCounter <= 0) {
			ToastUtil.longToast(mContext, R.string.password_error_to_comeout);
			doLogout(this);
			closeAllAct(this);
		}
	}

	/**
	 * 自动关闭本act
	 * 
	 * @return void
	 */
	public void finishAction() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					setResult(RESULT_OK);
					finish();
				}
			}
		}).start();
	}

	/**
	 * 如果点击忘记密码在，则调用此方法开启密码验证界面，如果密码验证通过， 则关闭密码验证界面，同时设置返回给该界面的结果，在{@link #onActivityResult(int, int, Intent)}
	 * 该界面也关闭
	 * 
	 * @return void
	 */
	private void startAttestation() {
		Intent intent = new Intent(this, AttestationActivity.class);
		intent.setData(GlobalConstants.IntentAction.INTENT_URI_ATTESTATION);
		int attestationType = 0;
		int requestCode = START_ATTESTATION_REQUESTCODE;
		switch (lockType) {
		case LOCK_CLEAR:
			attestationType = AttestationActivity.DELETE_GESTURE_ATTESTATION;
			break;
		case LOCK_UNLOCK:
			attestationType = AttestationActivity.UNLOCK_ATTESTATION;
			break;
		case LOCK_CHANGE:
			attestationType = AttestationActivity.CHANGE_GESTURE_ATTESTATION;
			break;
		default:
			break;
		}
		intent.putExtra(AttestationActivity.ATTESTATION_TYPE_KEY, attestationType);
		startActivityForResult(intent, requestCode);
		overridePendingTransition(R.anim.left_in, R.anim.stop);
	}

	@Override
	protected void onActivityResult(int requestcode, int resultcode, Intent result) {
		if (resultcode == RESULT_OK && requestcode == START_ATTESTATION_REQUESTCODE) {
			setResult(RESULT_OK);
			finish();
		}else {
			if(lockType != LOCK_UNLOCK){
				finish();
				startGestureLock();
			}
		}
	}

	private void setTextAndColor(TextView textView, String str, int color) {
		textView.setTextColor(color);
		textView.setText(str);
	}

	/**
	 * 将text view的样式修改为输入错误的样式，并开启错误提示动画
	 * 
	 * @param textView
	 * @param content
	 */
	private void setErrorText(TextView textView, String content) {
		textView.startAnimation(crazyShakeAnim);
		setTextAndColor(textView, content, getResources().getColor(R.color.light_red));
	}

	private void setDefaultText(TextView textView, String content) {
		setTextAndColor(textView, content, getResources().getColor(R.color.black_text));
	}

	@OnClick({ R.id.back, R.id.forget_psw, R.id.other_account })
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.forget_psw:
			startAttestation();
			break;
		case R.id.other_account:
			new LogoutAndClearActDialogBuilder(this, //
			getString(R.string.use_other_account_logon),//
			getString(R.string.prompt_need_logoff_current_account)//
			).create().show();
			break;
		default:
			break;
		}

	}

	public int getGestureActType() {
		return lockType;
	}

	/**
	 * 
	 */
	private void delayResetLockView() {
		lockView.changeToErrorState();
		lockView.setEnable(false);
		lockView.postDelayed(new Runnable() {

			@Override
			public void run() {
				if (!isFinishing() && lockView != null) {
					lockView.clearDrowed();
					lockView.setEnable(true);
				}
			}
		}, 1000);
	}

	@Override
	public void startCreateGestureLock() {
		switch (lockType) {
		case LOCK_CREATE:
			if(!TextUtils.isEmpty(tempPassword)){
				setDefaultText(textView, getString(R.string.enter_gestrue_lock_password_again));
			}else {
				setDefaultText(textView, getString(R.string.enter_new_gestrue_lock_password));
			}
			break;
		default:
			setDefaultText(textView, getString(R.string.enter_gestrue_lock_password));
			break;
		}
	}
	
	@Override
	public boolean onBackKeyPressed() {
		if(lockType == LOCK_UNLOCK){
			backToBeck();
		}else {
			return super.onBackKeyPressed();
		}
		return true;
	}
}
