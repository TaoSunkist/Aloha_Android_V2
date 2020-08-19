package com.wealoha.social.activity;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import butterknife.InjectView;
import butterknife.OnClick;

import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.wealoha.social.BaseFragAct;
import com.wealoha.social.R;
import com.wealoha.social.beans.AuthData;
import com.wealoha.social.beans.Result;
import com.wealoha.social.api.UserService;
import com.wealoha.social.commons.GlobalConstants;
import com.wealoha.social.commons.JsonController;
import com.wealoha.social.store.SyncEntProtocol;
import com.wealoha.social.utils.ContextUtil;
import com.wealoha.social.utils.StringUtil;
import com.wealoha.social.utils.ToastUtil;

public class NewPasswordAct extends BaseFragAct implements OnClickListener {

	@Inject
	ContextUtil contextUtil;
	@Inject
	UserService userService;

	/** 保存并登录 */
	@InjectView(R.id.login_btn)
	Button loginBtn;

	/** 新密码 */
	@InjectView(R.id.new_password)
	EditText newWord;

	/** 重复新密码 */
	@InjectView(R.id.new_password_again)
	EditText newWordAgain;
	/** 取消 */
	@InjectView(R.id.cancle)
	Button cancle;

	/** 用户账号（电话号） */
	private String mUserName;
	/** 验证码 */
	private String mCode;

	private String newPassword;
	private InputMethodManager imm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_newpassword);
		initData();
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	}

	public void initData() {

		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			mUserName = bundle.getString("username");
			mCode = bundle.getString("code");
		} else {
			return;
		}
	}

	@OnClick({ R.id.login_btn, R.id.cancle })
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login_btn:
			resetToServer();
			break;
		case R.id.cancle:
			finish();
			break;
		default:
			break;
		}
	}

	/**
	 * @Title: saveLogin
	 * @Description: 先向服务器发送新密码
	 * @param 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public void resetToServer() {
		newPassword = newWord.getText().toString().trim();
		String newPasswordAgain = newWordAgain.getText().toString().trim();

		if (TextUtils.isEmpty(newPassword)) {
			ToastUtil.longToast(this, R.string.config_pwd_new_empty);
			return;
		}
		if (newPassword.length() < 6 || newPassword.length() > 16) {
			ToastUtil.longToast(this, R.string.login_error_pwd_to_short);
			return;
		}
		// 验证密码和重复密码
		if (TextUtils.isEmpty(newPasswordAgain)) {
			ToastUtil.longToast(this, R.string.config_pwd_again_empty);
			return;
		}
		if (newPasswordAgain.length() < 6 || newPasswordAgain.length() > 16) {
			ToastUtil.longToast(this, R.string.login_error_pwd_to_short);
			return;
		}
		if (!newPasswordAgain.equals(newPassword)) {
			ToastUtil.longToast(this, R.string.login_error_pwd_to_diff);
			return;
		}
		// md5
		newPassword = StringUtil.md5(newPassword);
		RequestParams params = new RequestParams();
		params.addBodyParameter("number", mUserName);
		params.addBodyParameter("code", mCode);
		params.addBodyParameter("passwordMd5", newPassword);
		SyncEntProtocol.getInstance().send(HttpMethod.POST, GlobalConstants.ServerUrl.RESET_PASSWORD, params, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {

				ToastUtil.longToast(NewPasswordAct.this, R.string.get_code_error);
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				Result<AuthData> result = JsonController.parseJson(arg0.result, new TypeToken<Result<AuthData>>() {
				}.getType());
				if (result != null && result.isOk()) {
					// ToastUtil.longToast(NewPasswordAct.this,
					// R.string.signin);
					login(newPassword);
					// afterMobileLoginSuccess(mUserName, result.data.user,
					// result.data.t);
				} else {
				}
				// ToastUtil.longToast(mContext, arg0.result);
				Log.i("PASSWORD_RESET", arg0.result);
			}
		});
	}

	/**
	 * @Title: login
	 * @Description: 登录
	 * @param 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public void login(String password) {
		if (TextUtils.isEmpty(mUserName) || TextUtils.isEmpty(password)) {
			ToastUtil.shortToast(this, R.string.failed);
			return;
		}
		if (popup != null) {
			popup.show(container);
		}
		// showLoadingDialog(getString(R.string.being_sign_in_please_wait));
		userService.login(mUserName, password, new Callback<Result<AuthData>>() {

			@Override
			public void success(Result<AuthData> result, Response arg1) {
				if (result != null) {
					if (result.isOk()) {
						// TODO 需要清楚本地的其他用户数据
						ToastUtil.shortToast(NewPasswordAct.this, getString(R.string.login_success));
						afterMobileLoginSuccess(mUserName, result.data.user, result.data.t);
					} else if (result.data.error == 451) {
						ToastUtil.shortToast(NewPasswordAct.this, getString(R.string.login_proscribe));
					} else if (result.data.error == 401) {
						ToastUtil.shortToast(NewPasswordAct.this, getString(R.string.login_failed));
					} else {
						ToastUtil.shortToast(NewPasswordAct.this, R.string.Unkown_Error);
					}
				}

				if (popup != null) {
					popup.hide();
				}
			}

			@Override
			public void failure(RetrofitError arg0) {
				ToastUtil.shortToast(NewPasswordAct.this, getString(R.string.login_failed));
			}
		});
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (imm != null && newWord != null && newWordAgain != null) {
			imm.hideSoftInputFromWindow(newWord.getWindowToken(), 0);
			imm.hideSoftInputFromWindow(newWordAgain.getWindowToken(), 0);
		}
	}

}
