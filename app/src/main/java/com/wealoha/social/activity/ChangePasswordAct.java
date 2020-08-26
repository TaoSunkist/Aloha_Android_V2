package com.wealoha.social.activity;

import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;

import com.wealoha.social.BaseFragAct;
import com.wealoha.social.R;
import com.wealoha.social.api.ServerApi;
import com.wealoha.social.beans.AuthData;
import com.wealoha.social.beans.ApiResponse;
import com.wealoha.social.utils.ContextUtil;
import com.wealoha.social.utils.FontUtil;
import com.wealoha.social.utils.FontUtil.Font;
import com.wealoha.social.utils.StringUtil;
import com.wealoha.social.utils.ToastUtil;

public class ChangePasswordAct extends BaseFragAct implements OnClickListener {

	@Inject
	ServerApi mUserService;
	@Inject
	ContextUtil contextUtil;
	@Inject
	FontUtil fontUtil;
	@InjectView(R.id.config_change_password)
	EditText mPassword;
	@InjectView(R.id.config_change_password_new)
	EditText mNewPassword;
	@InjectView(R.id.config_change_password_again)
	EditText mAgainPassword;
	@InjectView(R.id.config_change_password_save)
	TextView mSave;
	@InjectView(R.id.menu_bar)
	RelativeLayout mMenuBar;
	@InjectView(R.id.change_pwd_back_tv)
	ImageView changePwdBackBtn;
	@InjectView(R.id.title)
	TextView mTitle;
	// @InjectView(R.id.profile_user_name_tv)
	// TextView mUserNameTextView;

	private String newPassword;
	private String password;
	private String newPasswordAgain;

	private InputMethodManager imm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_change_password);
		// FontUtil.setRegulartypeFace(this, mUserNameTextView);

		FontUtil.setRegulartypeFace(this, mPassword);
		FontUtil.setRegulartypeFace(this, mNewPassword);
		FontUtil.setRegulartypeFace(this, mAgainPassword);
		// fontUtil.changeFonts((ViewGroup) thisView,
		// Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
		// FontUtil.setRegulartypeFace(getActivity(), closeLeft);
	}

	@Override
	protected void initTypeFace() {
		fontUtil.changeFonts(mMenuBar, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
	}

	/**
	 * @Title: changePassword
	 * @Description: 修改密码
	 * @param 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public void changePassword() {
		// 登录密码不能为空
		password = mPassword.getText().toString().trim();
		if (TextUtils.isEmpty(password)) {
			// showSingleAlohaDialog(mContext, R.string.config_pwd_empty, null);
			ToastUtil.shortToast(this, R.string.config_pwd_empty);
			return;
		}
		if (password.length() < 6 || password.length() > 16) {
			ToastUtil.shortToast(this, R.string.config_pwd_new_empty);
			// showSingleAlohaDialog(mContext, R.string.config_pwd_new_empty,
			// null);
			return;
		}
		// 新密码不能为空
		newPassword = mNewPassword.getText().toString().trim();
		if (TextUtils.isEmpty(newPassword)) {
			ToastUtil.shortToast(this, R.string.config_pwd_new_empty);
			// showSingleAlohaDialog(mContext, R.string.config_pwd_new_empty,
			// null);
			return;
		}
		newPasswordAgain = mAgainPassword.getText().toString().trim();
		if (newPasswordAgain.length() < 6 || newPasswordAgain.length() > 16 || newPassword.length() > 16 || newPassword.length() < 6) {
			ToastUtil.shortToast(this, R.string.login_error_pwd_to_short);
			// showSingleAlohaDialog(mContext,
			// R.string.login_error_pwd_to_short, null);
			return;
		}
		// 两次密码要一致
		if (!newPassword.equals(newPasswordAgain)) {
			// showSingleAlohaDialog(mContext, R.string.login_error_pwd_to_diff,
			// null);
			ToastUtil.shortToast(this, R.string.login_error_pwd_to_diff);
			return;
		}
		if (TextUtils.isEmpty(newPasswordAgain)) {
			ToastUtil.shortToast(this, R.string.config_pwd_new_again);
			// showSingleAlohaDialog(mContext, R.string.config_pwd_new_again,
			// null);
			return;
		}

		mUserService.reqAlertPassWord(StringUtil.md5(password), StringUtil.md5(newPassword), new Callback<ApiResponse<AuthData>>() {

			@Override
			public void success(ApiResponse<AuthData> apiResponse, Response arg1) {
				if (apiResponse != null) {
					if (apiResponse.isOk()) {
						ToastUtil.shortToast(ChangePasswordAct.this, getString(R.string.modify_success));
						finish();
					} else if (apiResponse.getData().getError() == 200509) {
						// showSingleAlohaDialog(mContext,
						// R.string.old_password_is_not_correct, null);
						ToastUtil.shortToast(ChangePasswordAct.this, R.string.old_password_is_not_correct);
					}
				} else {
					ToastUtil.shortToast(ChangePasswordAct.this, R.string.Unkown_Error);
					// showSingleAlohaDialog(mContext, R.string.Unkown_Error,
					// null);
				}
			}

			@Override
			public void failure(RetrofitError arg0) {
				// showSingleAlohaDialog(mContext,
				// R.string.network_link_failure, null);
				ToastUtil.shortToast(mContext, R.string.network_error);
			}
		});
	}

	@OnClick({ R.id.config_change_password_save, R.id.change_pwd_back_tv })
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.config_change_password_save:
			changePassword();
			break;
		case R.id.change_pwd_back_tv:
			finish();
			break;
		default:
			break;
		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		// 彈出輸入法
		popInputMethod();
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
				imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(mPassword, 0);
			}
		}, 500);
	}

	// @Override
	// public void initView() {
	//
	// }

	// @Override
	// public void initData() {
	//
	// }

	@Override
	protected void onPause() {
		super.onPause();

		if (imm != null && mPassword != null && mAgainPassword != null && mNewPassword != null) {
			imm.hideSoftInputFromWindow(mPassword.getWindowToken(), 0);
			imm.hideSoftInputFromWindow(mAgainPassword.getWindowToken(), 0);
			imm.hideSoftInputFromWindow(mNewPassword.getWindowToken(), 0);
			// imm.hideSoftInputFromInputMethod(mEditTextContent.getWindowToken(),
			// 0);
		}
	}

}
