package com.wealoha.social.activity;

import javax.inject.Inject;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;

import com.wealoha.social.BaseFragAct;
import com.wealoha.social.R;
import com.wealoha.social.commons.GlobalConstants;
import com.wealoha.social.utils.FontUtil;
import com.wealoha.social.utils.FontUtil.Font;

public class ConfigInstagramAct extends BaseFragAct implements OnClickListener, OnTouchListener {

	@Inject
	FontUtil font;

	@InjectView(R.id.instagram_submit)
	Button mSbumit;
	@InjectView(R.id.instagram_first_cb)
	CheckBox mFirstCb;
	@InjectView(R.id.instagram_scound_cb)
	CheckBox mScoundCb;

	@InjectView(R.id.instagram_title)
	TextView mtitle;

	@InjectView(R.id.instagram_first_rl)
	RelativeLayout mFirstRl;
	@InjectView(R.id.instagram_first_tv)
	TextView mFirstTv;
	@InjectView(R.id.instagram_scound_tv)
	TextView mScoundTv;

	@InjectView(R.id.instagram_scound_rl)
	RelativeLayout mScoundRl;

	@InjectView(R.id.config_instagram_back)
	ImageView mBack;

	/** 默认6个月 */
	private boolean IS_ALL = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_config_instagram);
		mFirstCb.setOnTouchListener(this);
		mFirstCb.setChecked(true);
		mScoundCb.setOnTouchListener(this);

		font.changeViewFont(mtitle, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
		font.changeViewFont(mFirstTv, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
		font.changeViewFont(mScoundTv, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
	}

	@OnClick({ R.id.instagram_submit, R.id.config_instagram_back, R.id.instagram_first_rl, R.id.instagram_scound_rl })
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.instagram_submit:
			Bundle bundle = new Bundle();
			bundle.putBoolean("is_all", IS_ALL);
			startActivity(GlobalConstants.IntentAction.INTENT_URI_INSTAGRAM_WEB, bundle);
			finish();
			break;
		case R.id.config_instagram_back:
			finish();
			break;
		case R.id.instagram_first_rl:
			if (!mFirstCb.isChecked()) {
				mFirstCb.setChecked(true);
				mScoundCb.setChecked(false);
				IS_ALL = false;
			}
			break;
		case R.id.instagram_scound_rl:
			if (!mScoundCb.isChecked()) {
				mScoundCb.setChecked(true);
				mFirstCb.setChecked(false);
				IS_ALL = true;
			}
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (MotionEvent.ACTION_DOWN == event.getAction()) {
			CheckBox cb = (CheckBox) v;
			// ToastUtil.longToast(ConfigInstagramAct.this, "-------" + cb.isChecked());
			switch (v.getId()) {
			case R.id.instagram_first_cb:
				if (!cb.isChecked()) {
					cb.setChecked(true);
					mScoundCb.setChecked(false);
					IS_ALL = false;
				}
				break;
			case R.id.instagram_scound_cb:
				if (!cb.isChecked()) {
					cb.setChecked(true);
					mFirstCb.setChecked(false);
					IS_ALL = true;
				}
				break;
			default:
				break;
			}

		}
		v.performClick();
		return true;
	}
}
