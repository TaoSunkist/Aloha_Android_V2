package com.wealoha.social.activity;

import javax.inject.Inject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
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
import com.wealoha.social.beans.Result;
import com.wealoha.social.beans.ProfileData;
import com.wealoha.social.commons.GlobalConstants;
import com.wealoha.social.commons.JsonController;
import com.wealoha.social.store.SyncEntProtocol;
import com.wealoha.social.utils.ContextUtil;
import com.wealoha.social.utils.FontUtil;
import com.wealoha.social.utils.FontUtil.Font;
import com.wealoha.social.utils.ToastUtil;

public class ConfigIntroductionAct extends BaseFragAct implements OnClickListener {

	@Inject
	ContextUtil contextUtil;
	@Inject
	FontUtil fontUtil;

	@InjectView(R.id.config_introduction_et)
	EditText mIntroductionEt;
	@InjectView(R.id.config_introduction_save_tv)
	TextView mSave;
	@InjectView(R.id.config_introduction_back)
	ImageView mBack;
	@InjectView(R.id.config_introduction_container)
	ScrollView mEditContainer;
	@InjectView(R.id.layout_container)
	LinearLayout mContainer;
	@InjectView(R.id.title)
	TextView mTitle;

	private String mIntroductionContent;
	private InputMethodManager imm;

	/**
	 * 当前activity的识别码 注意：为了在各个act之间取得返回值，应当为每个act设置识别码并整合 但是现在还没有这么做
	 * */
	private static int FLAG = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_config_introduction);
		initData();
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		FontUtil.setSemiBoldTypeFace(this, mSave);
	}

	@Override
	protected void initTypeFace() {
		fontUtil.changeViewFont(mTitle, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
	}

	@OnClick({ R.id.config_introduction_back, R.id.config_introduction_save_tv, R.id.config_introduction_container, R.id.layout_container })
	@Override
	public void onClick(View v) {
		// super.onClick(v);
		switch (v.getId()) {
		case R.id.config_introduction_save_tv:
			saveIntroduction();
			break;
		case R.id.config_introduction_back:
			// FIXME提示没有保存
			finish();
			break;
		case R.id.config_introduction_container:
		case R.id.layout_container:
			Log.i("ONCLICK_CONTAINER", "-----------------------");
			mIntroductionEt.requestFocus();
			imm.showSoftInput(mIntroductionEt, 0);
			break;
		default:
			break;
		}
	}

	/**
	 * @Title: saveIntroduction
	 * @Description: 保存介绍内容
	 * @param 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public void saveIntroduction() {
		mIntroductionContent = mIntroductionEt.getText().toString();
		if (TextUtils.isEmpty(mIntroductionContent)) {
			// ToastUtil.longToast(mContext,
			// getString(R.string.message_is_empty));
			Intent intent = new Intent();
			intent.putExtra("introduction", "");
			setResult(FLAG, intent);
			finish();
			return;
		}
		RequestParams params = new RequestParams();
		// params.addBodyParameter("name", AppApplication.User.getName());
		params.addBodyParameter("summary", mIntroductionContent);
		contextUtil.addGeneralHttpHeaders(params);
		SyncEntProtocol.getInstance().send(HttpMethod.POST, GlobalConstants.ServerUrl.CHANGE_PROFILE, params, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				// ToastUtil.shortToast(mContext, "Failure");
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// XL.i("ALOHA_CONFIGDETAIL", "introduction:" + arg0.result);
				if (arg0 == null) {
					return;
				}
				Result<ProfileData> result = JsonController.parseJson(arg0.result, new TypeToken<Result<ProfileData>>() {
				}.getType());
				if (result.isOk()) {
					ToastUtil.longToast(ConfigIntroductionAct.this, getString(R.string.successfully_saved));
					contextUtil.setCurrentUser(result.getData().user);
					Intent intent = new Intent();
					intent.putExtra("introduction", mIntroductionContent);
					setResult(FLAG, intent);
					finish();
				} else if (result.getData().getError() == 200503) {
					ToastUtil.longToast(ConfigIntroductionAct.this, getString(R.string.username_unavailable));
				} else if (result.getData().getError() == 200522) {
					ToastUtil.longToast(ConfigIntroductionAct.this, getString(R.string.intro_has_illegalword));
				} else {
					ToastUtil.longToast(ConfigIntroductionAct.this, getString(R.string.Unkown_Error));
				}
			}
		});
	}

	public void initData() {
		String introduction = getIntent().getStringExtra("content");
		mIntroductionEt.setText(introduction);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (imm != null && mIntroductionEt != null) {
			imm.hideSoftInputFromWindow(mIntroductionEt.getWindowToken(), 0);
		}
	}

}
