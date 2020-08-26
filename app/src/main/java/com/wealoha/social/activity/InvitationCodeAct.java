package com.wealoha.social.activity;

import javax.inject.Inject;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.InjectView;
import butterknife.OnClick;

import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.wealoha.social.ActivityManager;
import com.wealoha.social.BaseFragAct;
import com.wealoha.social.ContextConfig;
import com.wealoha.social.R;
import com.wealoha.social.beans.CommentResult;
import com.wealoha.social.beans.ApiResponse;
import com.wealoha.social.commons.GlobalConstants;
import com.wealoha.social.commons.JsonController;
import com.wealoha.social.store.SyncEntProtocol;
import com.wealoha.social.utils.ContextUtil;
import com.wealoha.social.utils.ToastUtil;

public class InvitationCodeAct extends BaseFragAct implements OnClickListener {

	@InjectView(R.id.invitation_code_et)
	EditText mCodeEt;

	@InjectView(R.id.invitation_save)
	TextView mSave;

	@Inject
	ContextUtil contextUtil;
	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_invitation);
		ActivityManager.popAll();
		ActivityManager.push(this);
		mContext = this;
		initView();
	}

	@OnClick(R.id.invitation_save)
	@Override
	public void onClick(View v) {
		// super.onClick(v);
		switch (v.getId()) {
		case R.id.invitation_save:
			postInvitation();
			break;
		default:
			break;
		}
	}

	/**
	 * @Title: postInvitation
	 * @Description: 发送邀请码
	 * @param 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public void postInvitation() {
		if (container != null) {
			// PromptPopup.showPrompt(mContext, container);
			if (popup != null) {
				popup.show(container);
			}
		}

		String code = mCodeEt.getText().toString().trim();
		if (TextUtils.isEmpty(code)) {
			next();
			return;
		}
		RequestParams params = new RequestParams();
		params.addBodyParameter("code", code);
		contextUtil.addGeneralHttpHeaders(params);
		SyncEntProtocol.getInstance().send(HttpMethod.POST, GlobalConstants.ServerUrl.POST_INVITATION, params, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				Toast.makeText(mContext, R.string.network_error, Toast.LENGTH_LONG).show();
				if (popup != null) {
					popup.hide();
				}
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				ApiResponse<CommentResult> apiResponse = JsonController.parseJson(arg0.result, new TypeToken<ApiResponse<CommentResult>>() {
				}.getType());
				Log.i("invitation", arg0.result);
				if (apiResponse != null && apiResponse.isOk()) {
					ToastUtil.shortToast(mContext, R.string.is_work);
					// 不用再显示了
					dismissInvitationInput();
					startActivityAndCleanTask(GlobalConstants.IntentAction.INTENT_URI_MAIN, null);
					finish();
					return;
				}
				ToastUtil.shortToast(InvitationCodeAct.this, R.string.invalid_code_please_check_and_retry);
				// showSingleAlohaDialog(mContext, R.string.invalid_code_please_check_and_retry, null);
				if (popup != null) {
					popup.hide();
				}
			}
		});
	}

	private void dismissInvitationInput() {
		ContextConfig.getInstance().putBooleanWithFilename(GlobalConstants.AppConstact.SHOW_INVITATION_CODE_INPUT, false);
	}

	/**
	 * @Title: next
	 * @Description: 如果没有邀请码，进入main
	 * @param 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public void next() {
		// ActivityManager.pop(InvitationCodeAct.this);
		// save(CacheKey.InvitationFlag, true);
		dismissInvitationInput();
		startActivity(GlobalConstants.IntentAction.INTENT_URI_MAIN);
		finish();
	}

	public void initView() {
		
		// super.initView();
		mCodeEt.addTextChangedListener(new com.wealoha.social.impl.Listeners.TextWatcherAdapter() {

			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() <= 0) {
					mSave.setText(getResources().getString(R.string.skip));
				} else {
					mSave.setText(getResources().getString(R.string.continued));
				}
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			closeCurrentAct();
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}

	}

	void closeCurrentAct() {
		overridePendingTransition(R.anim.right_in, R.anim.stop);
		finish();
		ActivityManager.popAll();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		mCodeEt.requestFocus();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}
