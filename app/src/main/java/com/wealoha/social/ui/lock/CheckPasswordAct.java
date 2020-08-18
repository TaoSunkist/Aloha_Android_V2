package com.wealoha.social.ui.lock;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;

import com.wealoha.social.BaseFragAct;
import com.wealoha.social.R;
import com.wealoha.social.presenters.CheckPasswordPresenter;
import com.wealoha.social.utils.ToastUtil;

public class CheckPasswordAct extends BaseFragAct implements CheckPasswordView, OnClickListener {

	@InjectView(R.id.check_password)
	EditText pswEt;
	@InjectView(R.id.next)
	TextView nextTv;

	CheckPasswordPresenter cpPresenter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_checkpsw);
		// glPresenter = new GestureLockPresenter(this);
		cpPresenter = new CheckPasswordPresenter(this);
	}

	@OnClick(R.id.next)
	@Override
	public void onClick(View v) {
		Editable pswEditable = pswEt.getText();
		if (pswEditable != null) {
			cpPresenter.checkPassword(pswEditable.toString());
		}
	}

	@Override
	public void checkPasswordSuccess() {
		Intent intent = new Intent(this, GestureLockAct.class);
		intent.putExtra(GestureLockAct.LOCK_TYPE, GestureLockAct.LOCK_CREATE);
		startActivity(intent);
		finish();
	}

	@Override
	public void checkPasswordFaile() {
		ToastUtil.longToast(this, R.string.login_password_is_invalid_title);
	}
}
