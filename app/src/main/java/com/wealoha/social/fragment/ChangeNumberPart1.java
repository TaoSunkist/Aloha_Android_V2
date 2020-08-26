package com.wealoha.social.fragment;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;

import com.wealoha.social.BaseFragAct;
import com.wealoha.social.R;
import com.wealoha.social.api.ServerApi;
import com.wealoha.social.beans.ApiResponse;
import com.wealoha.social.beans.ResultData;
import com.wealoha.social.utils.FontUtil;
import com.wealoha.social.utils.FontUtil.Font;
import com.wealoha.social.utils.StringUtil;
import com.wealoha.social.utils.ToastUtil;

public class ChangeNumberPart1 extends BaseFragment implements OnClickListener {

	@Inject
	ServerApi user2Api;
	@InjectView(R.id.password_et)
	EditText psdEdit;
	@InjectView(R.id.next_tv)
	TextView nextTv;
	@InjectView(R.id.title_tv)
	TextView title_tv;
	@InjectView(R.id.back_imgv)
	ImageView backImg;
	private ViewGroup rootView;
	private Dialog dialog;

	public final static int CHANGE_NUM_REQUEST_CODE = 0;
	public final static String NEW_PHONE_NUM_KEY = "NEW_PHONE_NUM_KEY";
	BaseFragAct baseFragAct;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = (ViewGroup) inflater.inflate(R.layout.frag_changenum_part1, container, false);
		baseFragAct = (BaseFragAct) getActivity();
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		psdEdit.setOnClickListener(this);
		fontUtil.changeViewFont(title_tv, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
		nextTv.setTypeface(Typeface.SANS_SERIF);
		FontUtil.setRegulartypeFace(getActivity(), psdEdit);
	}

	@OnClick({ R.id.next_tv, R.id.back_imgv})
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.next_tv:
			checkPassword(psdEdit.getText().toString());
			break;
		case R.id.back_imgv:
			getActivity().finish();
			break;
		default:
			break;
		}
	}

	/***
	 * 
	 * 验证密码
	 * 
	 * @param psd
	 *            密码，md5加密
	 * @return void
	 */
	private void checkPassword(final String psd) {
		if (TextUtils.isEmpty(psd)) {
			ToastUtil.shortToast(getActivity(), R.string.you_entered_psd_is_wrong);
			return;
		}
		user2Api.verifyPassword(StringUtil.md5(psd), new Callback<ApiResponse<ResultData>>() {

			@Override
			public void success(ApiResponse<ResultData> apiResponse, Response arg1) {
				if (apiResponse == null) {
					ToastUtil.longToast(getActivity(), R.string.network_error);
					return;
				}
				if (apiResponse.isOk()) {
					Bundle bundle = new Bundle();
					bundle.putString("password", psd);
					((BaseFragAct) getActivity()).startFragmentForResult(ChangeNumberPart2.class, bundle, true, CHANGE_NUM_REQUEST_CODE, 0, 0);
				} else if (apiResponse.getData().getError() == 200509) {
					ToastUtil.shortToast(getActivity(), R.string.you_entered_psd_is_wrong);
				}
			}

			@Override
			public void failure(RetrofitError arg0) {
				ToastUtil.longToast(getActivity(), R.string.network_error);
			}
		});
	}

	@Override
	public void onActivityResultCallback(int requestcode, int resultcode, Intent result) {
		switch (requestcode) {
		case CHANGE_NUM_REQUEST_CODE:
			if (resultcode == Activity.RESULT_OK) {
				getActivity().setResult(Activity.RESULT_OK, result);
				getActivity().finish();
			}
			break;

		default:
			break;
		}
	}
}
