package com.wealoha.social.activity;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;

import com.wealoha.social.BaseFragAct;
import com.wealoha.social.R;
import com.wealoha.social.beans.AuthData;
import com.wealoha.social.beans.Result;
import com.wealoha.social.beans.instagram.InstagramService;
import com.wealoha.social.utils.FontUtil;
import com.wealoha.social.utils.FontUtil.Font;
import com.wealoha.social.utils.NetworkUtil;
import com.wealoha.social.utils.ToastUtil;
import com.wealoha.social.utils.XL;
import com.wealoha.social.view.custom.SlideSwitch;
import com.wealoha.social.view.custom.SlideSwitch.OnSwitchChangedListener;

public class ConfigHaveInstagramConfig extends BaseFragAct implements OnClickListener, OnTouchListener {

	@Inject
	InstagramService instagramService;
	@Inject
	FontUtil mFont;

	@InjectView(R.id.instagram_username_tv)
	TextView mUserNameTv;
	@InjectView(R.id.instagram_auto_switch)
	SlideSwitch mAutoSs;

	@InjectView(R.id.config_instagram_back)
	ImageView mBack;
	@InjectView(R.id.instagram_title)
	TextView mTitle;
	@InjectView(R.id.instagram_tv)
	TextView mInstagramTv;
	@InjectView(R.id.instagram_auto_tv)
	TextView mInstagramAutoTv;
	@InjectView(R.id.unbind_ins_tv)
	TextView mUnbind;

	private Bundle bundle;
	private Dialog alertDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_config_have_instagram);

		bundle = getIntent().getExtras();

		if (bundle == null) {
			return;
		}
		mUserNameTv.setText(bundle.getString("name", ""));

		if (bundle.getBoolean("isfirst", false)) {
			openDialog();
		}

		if (bundle.getBoolean("autoSync", true)) {
			mAutoSs.setChecked(true);
		} else {
			mAutoSs.setChecked(false);
		}
		mAutoSs.setOnSwitchChangedListener(new OnSwitchChangedListener() {

			@Override
			public void onSwitchChanged(SlideSwitch obj, boolean isChecked) {
				setAuto(isChecked);
			}
		});

		mFont.changeViewFont(mTitle, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
		mFont.changeViewFont(mInstagramTv, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
		mFont.changeViewFont(mInstagramAutoTv, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
		mFont.changeViewFont(mUnbind, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);

	}

	private void setAuto(boolean autoSync) {
		instagramService.setAuto(autoSync, new Callback<Result<AuthData>>() {

			@Override
			public void success(Result<AuthData> arg0, Response arg1) {
				XL.i("AUTO_SET", "SUCCESS");
			}

			@Override
			public void failure(RetrofitError arg0) {
				XL.i("AUTO_SET", "FAILURE:" + arg0.getMessage());
			}
		});
	}

	private void openUnbindAlertDialog() {
		View view = LayoutInflater.from(this).inflate(R.layout.dialog_first_aloha_time, new LinearLayout(this), false);

		TextView title = (TextView) view.findViewById(R.id.first_aloha_title);
		title.setText(getResources().getString(R.string.unbind_ins_title));
		TextView message = (TextView) view.findViewById(R.id.first_aloha_message);
		mFont.changeViewFont(message, Font.ENCODESANSCOMPRESSED_500_MEDIUM);
		message.setText(getResources().getString(R.string.unbind_ins_msg));
		message.setGravity(Gravity.CENTER);
		TextView close = (TextView) view.findViewById(R.id.close_tv);
		mFont.changeViewFont(close, Font.ENCODESANSCOMPRESSED_500_MEDIUM);
		close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (alertDialog != null && alertDialog.isShowing()) {
					alertDialog.dismiss();
				}
			}
		});
		close.setText(R.string.cancel);
		TextView close02 = (TextView) view.findViewById(R.id.close_tv_02);
		mFont.changeViewFont(close02, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
		close02.setVisibility(View.VISIBLE);
		close02.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (alertDialog != null && alertDialog.isShowing()) {
					alertDialog.dismiss();
				}
				unbind();
			}
		});
		close02.setText(R.string.unbind);
		alertDialog = new AlertDialog.Builder(this)//
		.setView(view)//
		.setCancelable(false) //
		.create();
		alertDialog.show();
	}

	private void openDialog() {
		View view = LayoutInflater.from(this).inflate(R.layout.dialog_first_aloha_time, new LinearLayout(this), false);

		TextView title = (TextView) view.findViewById(R.id.first_aloha_title);
		title.setText(getResources().getString(R.string.instagram_have_opened));
		TextView message = (TextView) view.findViewById(R.id.first_aloha_message);
		message.setText(getResources().getString(R.string.instagram_success));
		message.setGravity(Gravity.CENTER);
		TextView close = (TextView) view.findViewById(R.id.close_tv);
		close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (alertDialog != null && alertDialog.isShowing()) {
					alertDialog.dismiss();
					finish();
				}
			}
		});
		alertDialog = new AlertDialog.Builder(this)//
		.setView(view)//
		.setCancelable(false) //
		.create();
		alertDialog.show();

	}

	@OnClick({ R.id.config_instagram_back, R.id.unbind_ins_tv })
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.unbind_ins_tv:
			openUnbindAlertDialog();
			break;
		case R.id.config_instagram_back:
			finish();
			break;
		default:
			break;
		}
	}

	private void unbind() {
		instagramService.unbind(new Callback<Result<AuthData>>() {

			@Override
			public void success(Result<AuthData> arg0, Response arg1) {
				ToastUtil.longToast(ConfigHaveInstagramConfig.this, "unbind success");
				finish();
			}

			@Override
			public void failure(RetrofitError arg0) {
			}
		});
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		boolean netWork = NetworkUtil.isNetworkAvailable();
		if (MotionEvent.ACTION_DOWN == event.getAction() && !netWork) {
			ToastUtil.shortToast(this, getResources().getString(R.string.network_error));
		}
		v.performClick();
		return !netWork;
	}
}
