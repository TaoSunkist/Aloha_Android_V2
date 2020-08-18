package com.wealoha.social.activity;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.wealoha.social.ActivityManager;
import com.wealoha.social.BaseFragAct;
import com.wealoha.social.R;
import com.wealoha.social.api.user.MatchSettingData;
import com.wealoha.social.api.user.User2API;
import com.wealoha.social.api.user.User2Service;
import com.wealoha.social.beans.AuthData;
import com.wealoha.social.beans.Result;
import com.wealoha.social.beans.ResultData;
import com.wealoha.social.beans.user.UserRegisterService;
import com.wealoha.social.beans.user.UserService;
import com.wealoha.social.commons.GlobalConstants;
import com.wealoha.social.fragment.PhoneAreaFragment;
import com.wealoha.social.utils.ContextUtil;
import com.wealoha.social.utils.FontUtil;
import com.wealoha.social.utils.StringUtil;
import com.wealoha.social.utils.ToastUtil;
import com.wealoha.social.utils.XL;

/**
 * @author:sunkist
 * @see:
 * @since:
 * @description 登录和找回密码
 * @copyright wealoha.com
 * @Date:2014-10-27
 */
public class LoginAct extends BaseFragAct implements OnClickListener {

	@Inject
	UserService mUserService;
	@Inject
	User2Service mUser2Service;
	@Inject
	User2API mUserAPI;
	private static final String TAG = LoginAct.class.getSimpleName();

	/** 區號 */
	@ViewInject(R.id.areacode)
	private Button login;
	/** 账号 */
	@ViewInject(R.id.login_account)
	private EditText mLoginAccount;

	/** 密码 */
	@ViewInject(R.id.login_password)
	private EditText mLoginPassword;

	/** 点击登录 */
	@ViewInject(R.id.login_btn)
	private Button mLogin;

	// ViewInject失效 报Undeclared
	private TextView forgotPw;
	private String mUserName;
	private PhoneAreaFragment paf;
	private int mAreaPosition = -1;
	private InputMethodManager imm;
	@Inject
	ContextUtil contextUtil;
	@Inject
	Context context;
	@Inject
	UserRegisterService userRegisterService;

	private Context mContext;
	private Dialog forgotDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_login);
		ActivityManager.push(this);
		ViewUtils.inject(this); // 注入view和事件
		mContext = this;
		initView();
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	}

	@OnClick({ R.id.forgot_pw, R.id.login_btn, R.id.areacode })
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login_btn:
			login();
			break;
		case R.id.forgot_pw:
			forgot();
			break;
		case R.id.areacode:
			paf = new PhoneAreaFragment(mAreaPosition);
			paf.show(getFragmentManager(), "phone_area_dialog");
			break;
		}
	}

	@Override
	public void changePhoneArea(int position) {
		String area = paf.getCurrentArea(position);
		if (!TextUtils.isEmpty(area)) {
			login.setText(area);
			mAreaPosition = position;
		}

	}

	/**
	 * @Description:點擊登錄操作
	 * @see:
	 * @since:
	 * @description
	 * @author: sunkist
	 * @date:2014-10-27
	 */
	private void login() {
		String callingCode = getCallingCode();
		if (login.getText().toString() == null) {
			XL.d(TAG, "country_code_is_selected_by_default");
			return;
		}
		String username = mLoginAccount.getText().toString().trim();
		String password = mLoginPassword.getText().toString().trim();
		if (TextUtils.isEmpty(username)) {
			ToastUtil.shortToast(this, R.string.login_username_is_invalid_text);
			// showSingleAlohaDialog(mContext,
			// R.string.login_username_is_invalid_title,
			// R.string.login_username_is_invalid_text);
			return;
		}
		if (TextUtils.isEmpty(password) || password.length() < 4) {
			ToastUtil.shortToast(this, R.string.login_password_is_invalid_text);
			// showSingleAlohaDialog(mContext,
			// R.string.login_password_is_invalid_title,
			// R.string.login_password_is_invalid_text);
			mLoginPassword.requestFocus();
			return;
		}

		if (popup != null) {
			popup.show(container);
		}
		mUserName = StringUtil.appendPhoneNum(callingCode, username);
		password = StringUtil.md5(password);

		mUserService.login(mUserName, password, new Callback<Result<AuthData>>() {

			@Override
			public void failure(RetrofitError arg0) {
				if (popup != null) {
					popup.hide();
				}

				showSingleAlohaDialog(mContext, R.string.network_error, null);
			}

			@Override
			public void success(Result<AuthData> result, Response arg1) {
				if (popup != null) {
					popup.hide();
				}
				if (result != null) {
					if (result.isOk()) {
						afterMobileLoginSuccess(mUserName, result.data.user, result.data.t);
						// getProfeatureSetting();
						mUser2Service.getProfeatureSetting();
					} else if (result.data.error == 451) {// "用戶被封禁"
						ToastUtil.shortToast(LoginAct.this, R.string.failed);
					} else if (result.data.error == 401) {
						ToastUtil.shortToast(LoginAct.this, R.string.login_failed);
					} else {
						ToastUtil.shortToast(LoginAct.this, R.string.login_failed);
					}
				} else {
					ToastUtil.shortToast(LoginAct.this, R.string.Unkown_Error);
				}

			}
		});
	}

	/**
	 * 获取高级功能设置,并保存到本地
	 * 
	 */
	private void getProfeatureSetting() {
		mUserAPI.userMatchSetting(new Callback<Result<MatchSettingData>>() {

			@Override
			public void success(Result<MatchSettingData> result, Response response) {
				if (result != null && result.isOk()) {
					contextUtil.setProfeatureEnable(result.data.filterEnable);
					ArrayList<String> regions = (ArrayList<String>) result.data.selectedRegion;
					if (regions != null && regions.size() > 0) {
						contextUtil.setFilterRegion(regions.get(regions.size() - 1));
					}
				}
			}

			@Override
			public void failure(RetrofitError arg0) {
			}
		});
	}

	private String getCallingCode() {
		String str = login.getText().toString();
		int s = str.indexOf("(");
		String area = (String) str.subSequence(s, str.length());
		return StringUtil.getCallingCode(area);
	}

	/**
	 * @Description:找回密码
	 * @see:
	 * @since:
	 * @description
	 * @author: sunkist
	 * @date:2014-10-27
	 */
	private void forgot() {
		// dialog样式
		View dialogView = getLayoutInflater().inflate(R.layout.dialog_forget_password, null);
		final EditText phone = (EditText) dialogView.findViewById(R.id.forget_password_et);
		TextView title = (TextView) dialogView.findViewById(R.id.forget_title_tv);
		TextView forget_description_tv = (TextView) dialogView.findViewById(R.id.forget_description_tv);
		TextView closeLeft = (TextView) dialogView.findViewById(R.id.close_tv);
		TextView closeRight = (TextView) dialogView.findViewById(R.id.close_tv_02);
		FontUtil.setSemiBoldTypeFace(this, title);
		FontUtil.setRegulartypeFace(this, forget_description_tv);
		FontUtil.setRegulartypeFace(this, phone);
		FontUtil.setRegulartypeFace(this, closeLeft);
		FontUtil.setRegulartypeFace(this, closeRight);
		closeRight.setText(R.string.confirm);
		closeLeft.setText(R.string.cancel);
		// dialog号码输入框中前缀 ：+86 等
		phone.setText(getCallingCode() + " ");
		phone.setSelection(getCallingCode().length() + 1);
		// 弹出dialog
		closeLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				forgotDialog.dismiss();
			}
		});
		closeRight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mUserName = phone.getText().toString();
				forgotToServer();
			}
		});

		forgotDialog = new AlertDialog.Builder(this)//
		.setView(dialogView).create();
		forgotDialog.show();
	}

	/**
	 * @Title: forgotToServer
	 * @Description: 向服务器请求验证码
	 * @param 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	private void forgotToServer() {
		// mUserName:+XX XXXXXXXXX,去掉区号
		String userName = mUserName.substring(mUserName.indexOf(" ") + 1, mUserName.length());
		if (TextUtils.isEmpty(mUserName) || TextUtils.isEmpty(mUserName)) {
			ToastUtil.longToast(mContext, R.string.login_error_invalid_account);
			return;
			/* 手機號格式判斷 */
		} else if (!StringUtil.matchPhone(userName)) {
			ToastUtil.longToast(mContext, R.string.login_error_invalid_account);
			return;
		}

		userRegisterService.getResetPasswordCode(mUserName, new Callback<Result<ResultData>>() {

			@Override
			public void success(Result<ResultData> result, Response response) {
				// mLoadingDialog.dismiss();
				// PromptPopup.hidePrompt();
				if (popup != null) {
					popup.hide();
				}
				if (result != null) {
					if (result.isOk()) {
						Bundle bundle = new Bundle();
						bundle.putInt("from", 1);
						bundle.putString(GlobalConstants.AppConstact.USERNAME, mUserName);
						// 请求成功跳转
						startActivity(GlobalConstants.IntentAction.INTENT_URI_VERIFY, bundle);
						ToastUtil.shortToast(mContext, getString(R.string.verification_code_has_been_sent));
					} else if (result.status == Result.STATUS_CODE_THRESHOLD_HIT) {
						ToastUtil.shortToast(mContext, getString(R.string.register_frequent_request));
						// showSingleAlohaDialog(mContext,
						// R.string.register_frequent_request, null);
					} else if (result.data.error == ResultData.ERROR_USER_NOT_FOUND) {
						ToastUtil.shortToast(mContext, getString(R.string.register_mobile_error_noregister));
						// showSingleAlohaDialog(mContext,
						// R.string.register_mobile_error_noregister, null);
					}
				} else {
					ToastUtil.shortToast(mContext, getString(R.string.get_code_error));
					// showSingleAlohaDialog(mContext, R.string.get_code_error,
					// null);
				}
			}

			@Override
			public void failure(RetrofitError error) {
				// mLoadingDialog.dismiss();
				// PromptPopup.hidePrompt();
				if (popup != null) {
					popup.hide();
				}
				ToastUtil.shortToast(mContext, getString(R.string.get_code_error));
				// showSingleAlohaDialog(mContext, R.string.get_code_error,
				// null);
			}
		});
	}

	public void initView() {
		forgotPw = (TextView) findViewById(R.id.forgot_pw);
		forgotPw.setOnClickListener(this);
	}

	/**
	 * @Title: popInputMethod
	 * @Description: 自動彈出輸入法
	 * @param 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public void popInputMethod() {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {

				// imm = (InputMethodManager)
				// getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(mLoginAccount, 0);
			}
		}, 500);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {

		super.onWindowFocusChanged(hasFocus);
		// 彈出輸入法
		popInputMethod();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { // 按下的如果是BACK，同时没有重复
			openWelcomeAct();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void cancel(View view) {
		openWelcomeAct();
	}

	void openWelcomeAct() {
		ActivityManager.pop(this);

	}

	@Override
	protected void onStop() {
		super.onStop();
		if (imm != null && mLoginAccount != null && mLoginPassword != null) {
			imm.hideSoftInputFromWindow(mLoginAccount.getWindowToken(), 0);
			imm.hideSoftInputFromWindow(mLoginPassword.getWindowToken(), 0);
			// imm.hideSoftInputFromInputMethod(mEditTextContent.getWindowToken(),
			// 0);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
	}
}
