package com.wealoha.social.fragment;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;

import com.wealoha.social.R;
import com.wealoha.social.api.user.User2API;
import com.wealoha.social.beans.Result;
import com.wealoha.social.beans.ResultData;
import com.wealoha.social.utils.FontUtil;
import com.wealoha.social.utils.FontUtil.Font;
import com.wealoha.social.utils.StringUtil;
import com.wealoha.social.utils.ToastUtil;
import com.wealoha.social.view.custom.popup.LoadingPopup;

public class ChangeNumberPart3 extends BaseFragment implements OnClickListener {

	@Inject
	User2API user2Api;
	@InjectView(R.id.security_code_et)
	EditText codeEdit;
	@InjectView(R.id.next_tv)
	TextView nextTv;
	@InjectView(R.id.phone_code_tv)
	TextView titleTv;
	@InjectView(R.id.title_tv)
	TextView mTitle;
	@InjectView(R.id.security_error_tv)
	TextView securityCodeError;
	@InjectView(R.id.reget_security_code_tv)
	TextView regetCodeTv;
	@InjectView(R.id.back_imgv)
	ImageView backImg;
	@InjectView(R.id.phone_num_tv)
	TextView phoneNumTv;

	private ViewGroup rootView;
	private CodeCountDownTimer countTimer;
	protected LoadingPopup popup;
	private boolean isReset;
	private Dialog dialog;
	private Dialog forgetDialog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = (ViewGroup) inflater.inflate(R.layout.frag_changenum_part3, container, false);
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		Bundle bundle = getArguments();
		if (bundle != null) {
			phoneNumTv.setText(bundle.getString("phonenum"));
		}
		fontUtil.changeViewFont(mTitle, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
		FontUtil.setRegulartypeFace(getActivity(), nextTv);
		FontUtil.setRegulartypeFace(getActivity(), phoneNumTv);
		FontUtil.setRegulartypeFace(getActivity(), securityCodeError);
		FontUtil.setRegulartypeFace(getActivity(), regetCodeTv);
		FontUtil.setRegulartypeFace(getActivity(), titleTv);
		FontUtil.setRegulartypeFace(getActivity(), codeEdit);

		codeEdit.addTextChangedListener(new TextWatcher() {// 隐藏验证码错误提示

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				securityCodeError.setVisibility(View.INVISIBLE);
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		popup = new LoadingPopup(getActivity());
		countTimer = new CodeCountDownTimer(60000, 200);
		countTimer.start();
	}

	@OnClick({ R.id.next_tv, R.id.reget_security_code_tv, R.id.back_imgv })
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.next_tv:
			checkPassword(codeEdit.getText().toString());
			break;
		case R.id.reget_security_code_tv:
			securityCodeCountDownTimer();
			break;
		case R.id.back_imgv:
			confirmBackDialog();
			break;
		default:
			break;
		}
	}

	/***
	 * 
	 * 密码错误提示
	 * 
	 * @return void
	 */
	private void confirmBackDialog() {
		View view = LayoutInflater.from(context).inflate(R.layout.dialog_first_aloha_time, new LinearLayout(context), false);
		TextView title = (TextView) view.findViewById(R.id.first_aloha_title);
		TextView message = (TextView) view.findViewById(R.id.first_aloha_message);
		TextView closeLeft = (TextView) view.findViewById(R.id.close_tv);
		FontUtil.setRegulartypeFace(getActivity(), title);
		FontUtil.setRegulartypeFace(getActivity(), message);
		FontUtil.setRegulartypeFace(getActivity(), closeLeft);

		title.setText(getString(R.string.cancel) + "?");
		message.setText(R.string.verification_code_has_delay_be_sure_to_restart);
		message.setGravity(Gravity.CENTER);
		closeLeft.setText(R.string.continue_to_wait);
		closeLeft.setTypeface(Typeface.DEFAULT_BOLD);
		closeLeft.setVisibility(View.VISIBLE);

		closeLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (dialog.isShowing()) {
					dialog.dismiss();
				}
			}
		});
		TextView closeRight = (TextView) view.findViewById(R.id.close_tv_02);
		closeRight.setText(R.string.cancel);
		closeRight.setVisibility(View.VISIBLE);
		closeRight.setTypeface(Typeface.DEFAULT);
		closeRight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (dialog.isShowing()) {
					dialog.dismiss();
				}
				getActivity().finish();
			}
		});
		dialog = new AlertDialog.Builder(getActivity())//
				.setView(view).create();
		dialog.show();
	}

	private void checkPassword(final String code) {
		final String password;
		final String phonenum;
		Bundle bundle = getArguments();
		if (bundle != null) {
			password = bundle.getString("password");
			phonenum = bundle.getString("phonenum");
		} else {
			password = null;
			phonenum = null;
		}

		if (TextUtils.isEmpty(phonenum) || TextUtils.isEmpty(password)) {
			return;
		}
		user2Api.changeMobile(phonenum, StringUtil.md5(password), code, new Callback<Result<ResultData>>() {

			@Override
			public void success(Result<ResultData> result, Response arg1) {
				if (!isVisible() || result == null) {
					return;
				}
				if (result.isOk()) {
					contextUtil.setAccountPhoneNumber(phonenum);
					getActivity().setResult(Activity.RESULT_OK);
					getActivity().finish();
				} else if (result.data.error == 200528) {
					securityCodeError.setVisibility(View.VISIBLE);
				} else {
					ToastUtil.longToast(getActivity(), R.string.Unkown_Error);
				}
			}

			@Override
			public void failure(RetrofitError arg0) {
				if (!isVisible()) {
					return;
				}
				ToastUtil.longToast(getActivity(), R.string.Unkown_Error);
			}
		});

	}

	private TextView timerTextView;
	private AlertDialog resetSecurityCodeDialog;

	public void securityCodeCountDownTimer() {
		if (isReset) {
			reSendSms();
			return;
		}
		if (resetSecurityCodeDialog == null) {
			View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_first_aloha_time, rootView, false);
			TextView resetTitle = (TextView) view.findViewById(R.id.first_aloha_title);
			TextView btnLeft = (TextView) view.findViewById(R.id.close_tv);
			timerTextView = (TextView) view.findViewById(R.id.first_aloha_message);
			timerTextView.setGravity(Gravity.CENTER);
			timerTextView.setText("");
			resetTitle.setText(R.string.resend_security_code);

			FontUtil.setRegulartypeFace(getActivity(), resetTitle);
			FontUtil.setRegulartypeFace(getActivity(), btnLeft);
			FontUtil.setRegulartypeFace(getActivity(), timerTextView);

			btnLeft.setText(R.string.confirm);
			btnLeft.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					resetSecurityCodeDialog.dismiss();
				}
			});
			// timerTextView.setText(i + "秒後重新發送驗證碼");
			resetSecurityCodeDialog = new AlertDialog.Builder(getActivity())//
					.setView(view).create();
		}
		resetSecurityCodeDialog.show();

	}

	/**
	 * @Title: reSendSms
	 * @Description: 参数用来判断重新获取的是忘记密码的验证码还是注册的验证码
	 * @param @param url 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	private void reSendSms() {
		Bundle bundle = getArguments();
		final String phonenum;
		if (bundle != null) {
			phonenum = bundle.getString("phonenum");
		} else {
			phonenum = null;
		}
		View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_first_aloha_time, rootView, false);
		TextView pn = (TextView) view.findViewById(R.id.first_aloha_title);
		TextView rt = (TextView) view.findViewById(R.id.first_aloha_message);
		rt.setText(R.string.resend_security_code);
		pn.setText(phonenum);

		TextView btnLeft = (TextView) view.findViewById(R.id.close_tv);
		btnLeft.setText(R.string.confirm);
		btnLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				forgetDialog.dismiss();
			}
		});
		TextView btnRight = (TextView) view.findViewById(R.id.close_tv_02);
		btnRight.setText(R.string.confirm);
		btnRight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				postNewPhoneNum(phonenum);
			}
		});
		forgetDialog = new AlertDialog.Builder(getActivity()).setView(view).create();
		forgetDialog.show();
	}

	/***
	 * 
	 * 验证新的手机号码，并获取验证码
	 * 
	 * @param phonenum
	 *            格式 +86 12345567890 (+国家码 空格 手机号)
	 * @return void
	 */
	private void postNewPhoneNum(final String phonenum) {
		// 手机号码是否正确
		if (TextUtils.isEmpty(phonenum.trim())) {
			return;
		}
		showDialog(true);
		user2Api.mobileVerify(phonenum, new Callback<Result<ResultData>>() {

			@Override
			public void success(Result<ResultData> result, Response arg1) {
				showDialog(false);
				if (!isVisible() || result == null) {
					return;
				}
				isReset = false;
				countTimer.start();
			}

			@Override
			public void failure(RetrofitError error) {
				showDialog(false);
				if (!isVisible()) {
					return;
				}
				ToastUtil.longToast(getActivity(), R.string.network_error);
			}
		});

	}

	/***
	 * loading 弹层
	 * 
	 * @param isShow
	 * @param where
	 * @return void
	 */
	public void showDialog(boolean isShow) {
		if (isShow) {
			if (rootView != null) {
				// PromptPopup.showPrompt(context, container);
				if (popup != null) {
					popup.show(rootView);
				}
			}
		} else {
			if (popup != null) {
				popup.hide();
			}
		}
	}

	/***
	 * 计时类
	 */
	private class CodeCountDownTimer extends CountDownTimer {

		String count;

		public CodeCountDownTimer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			if (resetSecurityCodeDialog == null) {
				return;
			}
			count = String.valueOf(millisUntilFinished / 1000);
			if (resetSecurityCodeDialog.isShowing() && timerTextView != null) {
				timerTextView.setText(getResources().getString(R.string.security_code_timer, count));
			}
		}

		@Override
		public void onFinish() {
			isReset = true;
			if (resetSecurityCodeDialog != null && resetSecurityCodeDialog.isShowing()) {
				resetSecurityCodeDialog.dismiss();
				reSendSms();
			}
		}

	}

	@Override
	public void onPause() {
		super.onPause();
		countTimer.cancel();
	}
}
