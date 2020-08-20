package com.wealoha.social.activity;

import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
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
import com.wealoha.social.api.ServerApi;
import com.wealoha.social.beans.AuthData;
import com.wealoha.social.beans.Result;
import com.wealoha.social.beans.ResultData;
import com.wealoha.social.commons.GlobalConstants;
import com.wealoha.social.fragment.PhoneAreaFragment;
import com.wealoha.social.utils.FontUtil;
import com.wealoha.social.utils.StringUtil;
import com.wealoha.social.utils.ToastUtil;

public class RegisterAct extends BaseFragAct implements OnClickListener {

	public static final String TAG = RegisterAct.class.getSimpleName();
	@Inject
	Context context;
	@Inject
	ServerApi userService;
	@ViewInject(R.id.cancle)
	private Button canceled;

	@ViewInject(R.id.areacode)
	private Button areacode;

	@ViewInject(R.id.phonenumber)
	private EditText phoneNumber;

	@ViewInject(R.id.password)
	private EditText password;

	@ViewInject(R.id.url)
	private TextView url;

	@ViewInject(R.id.register)
	private Button register;
	public String username;
	private String pw;
	private PhoneAreaFragment paf;
	private int mAreaPosition = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_register);
		ViewUtils.inject(this);
		ActivityManager.push(RegisterAct.this);
		initView();
	}

	@OnClick({ R.id.cancle, R.id.areacode, R.id.register })
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.cancle:
			openWelcomeAct();
			break;
		case R.id.areacode:
			paf = new PhoneAreaFragment(mAreaPosition);
			paf.show(getFragmentManager(), "phone_area_dialog");
			break;
		case R.id.url:
			break;
		case R.id.register:
			registerUser();
			// alertTest();
			break;
		}
	}

	@Override
	public void changePhoneArea(int position) {
		String area = paf.getCurrentArea(position);
		if (!TextUtils.isEmpty(area)) {
			areacode.setText(area);
			paf.setCurrentArea(position);
			mAreaPosition = position;
		}
	}

	private void registerUser() {
		final String callingCode = StringUtil.getCallingCode(areacode.getText().toString());
		if (callingCode == null) {
			// XL.d(TAG, "手機區碼默認是選中的！！");
			return;
		}
		username = phoneNumber.getText().toString();
		pw = password.getText().toString();
		if (TextUtils.isEmpty(username)) {
			ToastUtil.shortToast(this, R.string.login_username_is_invalid_text);
			// showSingleAlohaDialog(mContext,
			// R.string.login_username_is_invalid_title,
			// R.string.login_username_is_invalid_text);
			return;
		}
		if (TextUtils.isEmpty(pw) || password.length() < 6) {
			ToastUtil.shortToast(this, R.string.login_password_is_invalid_text);
			// showSingleAlohaDialog(mContext,
			// R.string.login_password_is_invalid_title,
			// R.string.login_password_is_invalid_text);
			password.requestFocus();
			return;
		}
		View view = getLayoutInflater().inflate(R.layout.dialog_post_phone_num_to, null);
		TextView phoneNum = (TextView) view.findViewById(R.id.phone_num);
		FontUtil.setRegulartypeFace(this, phoneNum);
		phoneNum.setText(callingCode + " " + username);
		new AlertDialog.Builder(this).setView(view)//
				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (popup != null) {
							popup.show(container);
						}
						username = callingCode + " " + username;
						userService.login(username, new Callback<Result<AuthData>>() {

							@Override
							public void success(Result<AuthData> result, Response arg1) {
								if (result != null) {
									if (result.isOk()) {
										Bundle bundle = new Bundle();
										bundle.putInt("from", 0);
										bundle.putString(GlobalConstants.AppConstact.USERNAME, username);
										bundle.putString(GlobalConstants.AppConstact.PASSWORD, pw);
										startActivity(GlobalConstants.IntentAction.INTENT_URI_VERIFY, bundle);
										ToastUtil.longToast(RegisterAct.this, getString(R.string.verification_code_has_been_sent));
									} else if (ResultData.MOBILE_ERROR == result.data.error) {
										ToastUtil.shortToast(RegisterAct.this, R.string.mobile_error);
									} else if (ResultData.REGISTERED_ERROR == result.data.error) {
										ToastUtil.shortToast(RegisterAct.this, R.string.register_mobile_error);
									} else if ("503".equals(String.valueOf(result.status))) {
										ToastUtil.shortToast(RegisterAct.this, R.string.get_code_frequent_req);
									} else {
										ToastUtil.shortToast(RegisterAct.this, R.string.Unkown_Error);
									}
								} else {
									ToastUtil.shortToast(RegisterAct.this, R.string.Unkown_Error);
								}

								if (popup != null) {
									popup.hide();
								}
							}

							@Override
							public void failure(RetrofitError arg0) {
								ToastUtil.longToast(RegisterAct.this, R.string.get_code_error);
								if (popup != null) {
									popup.hide();
								}
							}
						});
					}

				}).setNegativeButton(mResources.getString(R.string.cancel), new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();
		return;

	}

	public void initView() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(mResources.getString(R.string.register_server_protocal_text_one));
		stringBuilder.append(mResources.getString(R.string.a_font_fff_href_aloha_wealoha_com_1000_service_));
		stringBuilder.append(mResources.getString(R.string.register_server_protocal_text_two));
		stringBuilder.append("</a>，<br />");
		stringBuilder.append(mResources.getString(R.string.register_server_protocal_text_three));
		stringBuilder.append(mResources.getString(R.string.a_font_fff_href_aloha_wealoha_com_1000_privacy_));
		stringBuilder.append(mResources.getString(R.string.register_server_protocal_text_four));
		stringBuilder.append("</a>");
		CharSequence cs = Html.fromHtml(stringBuilder.toString());
		url.setText(cs);
		url.setMovementMethod(LinkMovementMethod.getInstance());

	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {

		super.onWindowFocusChanged(hasFocus);
		// 彈出輸入法
		// popInputMethod();
	}

	@Override
	protected void onResume() {
		super.onResume();
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
				// TODO Auto-generated method stub
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(phoneNumber, 0);
			}
		}, 500);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { // 按下的如果是BACK，同时没有重复
			openWelcomeAct();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void regCancel(View view) {
		openWelcomeAct();
	}

	void openWelcomeAct() {
		RegisterAct.this.finish();
		overridePendingTransition(R.anim.stop, R.anim.right_out);
	}
}
