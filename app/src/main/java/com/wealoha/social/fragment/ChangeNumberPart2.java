package com.wealoha.social.fragment;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;

import com.wealoha.social.BaseFragAct;
import com.wealoha.social.R;
import com.wealoha.social.api.user.User2API;
import com.wealoha.social.beans.Result;
import com.wealoha.social.beans.ResultData;
import com.wealoha.social.fragment.PhoneAreaFragment.ChangePhoneAreaCallback;
import com.wealoha.social.utils.FontUtil;
import com.wealoha.social.utils.FontUtil.Font;
import com.wealoha.social.utils.ToastUtil;
import com.wealoha.social.view.custom.popup.LoadingPopup;

public class ChangeNumberPart2 extends BaseFragment implements OnClickListener, OnTouchListener, ChangePhoneAreaCallback {

	@Inject
	User2API user2Api;

	@InjectView(R.id.phone_num_et)
	EditText phoneNumEdit;
	@InjectView(R.id.next_tv)
	TextView nextTv;
	@InjectView(R.id.phone_code_root)
	RelativeLayout phoneCodeRoot;
	@InjectView(R.id.phone_code_tv)
	TextView phoneCodeTv;
	@InjectView(R.id.back_imgv)
	ImageView backImg;
	@InjectView(R.id.title_tv)
	TextView mTitle;
	@InjectView(R.id.phone_code_title_tv)
	TextView mPhoneCodeTitle;
	private ViewGroup rootView;

	private PhoneAreaFragment phoneAreaFrag;
	private String areaCode;
	protected LoadingPopup popup;
	private Dialog dialog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = (ViewGroup) inflater.inflate(R.layout.frag_changenum_part2, container, false);
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		phoneCodeRoot.setOnTouchListener(this);
		popup = new LoadingPopup(getActivity());
		phoneCodeTv.setText(R.string.china_86);
		areaCode = getString(R.string.code_china_86);
		fontUtil.changeViewFont(mTitle, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
		FontUtil.setRegulartypeFace(getActivity(), nextTv);
		FontUtil.setRegulartypeFace(getActivity(), mPhoneCodeTitle);
		FontUtil.setRegulartypeFace(getActivity(), phoneNumEdit);
	}

	@OnClick({ R.id.next_tv, R.id.back_imgv })
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.next_tv:
			postNewPhoneNum(areaCode + " " + phoneNumEdit.getText().toString());
			break;
		case R.id.back_imgv:
			getActivity().finish();
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() != MotionEvent.ACTION_UP) {
			return true;
		}

		switch (v.getId()) {
		case R.id.phone_code_root:
			chancePhoneCode();
			break;

		default:
			break;
		}
		v.performClick();
		return false;
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
		// 密码是否为空
		final String password;
		if (getArguments() != null) {
			password = getArguments().getString("password");
		} else {
			password = null;
		}

		if (TextUtils.isEmpty(password)) {
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
				int resid = -1;
				if (result.isOk()) {
					Bundle bundle = new Bundle();// 将密码和电话号码发送给下一个页面
					bundle.putString("password", password);
					bundle.putString("phonenum", phonenum);
					((BaseFragAct) getActivity()).startFragmentForResult(ChangeNumberPart3.class, bundle, true, ChangeNumberPart1.CHANGE_NUM_REQUEST_CODE, 0, 0);
				} else if (result.data.error == 200516) {
					resid = R.string.the_phone_had_registed;
				} else if (result.data.error == 200520) {
					resid = R.string.phone_num_incorrect_format;
				} else if (result.status == 503) {
					resid = R.string.register_too_multifarious;
				}
				if (resid != -1)
					ToastUtil.shortToast(getActivity(), resid);
			}

			@Override
			public void failure(RetrofitError arg0) {
				showDialog(false);
				if (!isVisible()) {
					return;
				}
				ToastUtil.longToast(getActivity(), R.string.network_error);
			}
		});

	}

	/***
	 * 
	 * 密码错误提示
	 * 
	 * @return void
	 */
	private void openPasswordErrorDialog(int stringid) {
		if (stringid == -1) {
			return;
		}
		View view = LayoutInflater.from(context).inflate(R.layout.dialog_first_aloha_time, new LinearLayout(context), false);
		TextView title = (TextView) view.findViewById(R.id.first_aloha_title);
		TextView message = (TextView) view.findViewById(R.id.first_aloha_message);
		message.setVisibility(View.GONE);
		title.setText(stringid);

		TextView closeLeft = (TextView) view.findViewById(R.id.close_tv);
		closeLeft.setText(R.string.confirm);
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
		dialog = new AlertDialog.Builder(getActivity())//
				.setView(view).create();
		dialog.show();
	}

	/***
	 * 选择地区码
	 * 
	 * @return void
	 */
	private void chancePhoneCode() {
		phoneAreaFrag = new PhoneAreaFragment(-1, this);
		phoneAreaFrag.show(getFragmentManager(), "phone_area_dialog");
	}

	@Override
	public void changeAreaCallback(int position) {
		phoneCodeTv.setText(phoneAreaFrag.getCurrentArea(position));
		areaCode = phoneAreaFrag.getCurrentAreaCode(getActivity(), position);
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

	@Override
	public void onActivityResultCallback(int requestcode, int resultcode, Intent result) {
		switch (requestcode) {
		case ChangeNumberPart1.CHANGE_NUM_REQUEST_CODE:
			if (resultcode == Activity.RESULT_OK) {
				Intent intent = new Intent();
				intent.putExtra(ChangeNumberPart1.NEW_PHONE_NUM_KEY, areaCode + " " + phoneNumEdit.getText().toString());
				getActivity().setResult(Activity.RESULT_OK, intent);
				getActivity().finish();
			}
			break;

		default:
			break;
		}
	}
}
