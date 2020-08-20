package com.wealoha.social.activity;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;

import com.wealoha.social.AppApplication;
import com.wealoha.social.BaseFragAct;
import com.wealoha.social.LocationActivity;
import com.wealoha.social.R;
import com.wealoha.social.api.ServerApi;
import com.wealoha.social.beans.MatchSettingData;
import com.wealoha.social.beans.Result;
import com.wealoha.social.beans.ResultData;
import com.wealoha.social.beans.PromotionGetData;
import com.wealoha.social.utils.AMapUtil;
import com.wealoha.social.utils.AMapUtil.LocationCallback;
import com.wealoha.social.utils.ContextUtil;
import com.wealoha.social.utils.FontUtil;
import com.wealoha.social.utils.ToastUtil;
import com.wealoha.social.view.custom.SuperRangerBar;
import com.wealoha.social.view.custom.SuperRangerBar.OnMoveListener;

public class FilterSettingAct extends BaseFragAct implements OnClickListener, OnTouchListener {

	@Inject
	ServerApi mUserAPI;
	@Inject
	ContextUtil mContextUtil;

	@InjectView(R.id.system_filter_cb)
	CheckBox mSystemFilterCb;
	@InjectView(R.id.location_filter_tv)
	TextView mLocationFilterTv;
	@InjectView(R.id.location_filter_cb)
	CheckBox mLocationFilterCb;
	@InjectView(R.id.location_filter_rl)
	RelativeLayout mLocationFilterRl;
	@InjectView(R.id.system_filter_rl)
	RelativeLayout mSystemFilterRl;
	@InjectView(R.id.age_filter_rangbar)
	SuperRangerBar mAgaRangeBar;
	@InjectView(R.id.start_age)
	TextView mStartAgeTv;
	@InjectView(R.id.end_age)
	TextView mEndAgeTv;
	@InjectView(R.id.content_cover_fl)
	RelativeLayout mContentCoverFl;
	@InjectView(R.id.disenble_filter_ll)
	LinearLayout mDisenbleContainer;
	@InjectView(R.id.filter_setting_save_tv)
	TextView mSaveTv;
	@InjectView(R.id.progress_bar)
	ProgressBar mProgressBar;
	@InjectView(R.id.filter_setting_back)
	TextView mCloseTv;
	@InjectView(R.id.pro_icon_tv)
	TextView mProIconTv;
	@InjectView(R.id.check_detail_tv)
	TextView mCheckDetailTv;
	@InjectView(R.id.menu_bar)
	RelativeLayout mMenuBar;
	@InjectView(R.id.age_filter_rl)
	LinearLayout mAgeFilterLl;

	private MatchSettingData mMatchSettingData;
	private int mLocationCode = 0x2222;
	private String mRegionKey;
	private Integer mStartAge;
	private Integer mEndAge;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_filter_setting);
		mLocationFilterRl.setOnTouchListener(this);
		mSystemFilterRl.setOnTouchListener(this);

		FontUtil.setSemiBoldTypeFace(mContext, mLocationFilterRl);
		FontUtil.setSemiBoldTypeFace(mContext, mSystemFilterRl);
		FontUtil.setSemiBoldTypeFace(mContext, mMenuBar);
		FontUtil.setSemiBoldTypeFace(mContext, mAgeFilterLl);

		FontUtil.setSemiBoldTypeFace(mContext, mProIconTv);
		Paint paint = mCheckDetailTv.getPaint();
		paint.setFlags(Paint.UNDERLINE_TEXT_FLAG);
		paint.setAntiAlias(true);
		initData();
	}

	/**
	 * 初始化筛选的设置数据
	 * 
	 */
	private void initData() {
		mMatchSettingData = (MatchSettingData) getIntent().getSerializableExtra(MatchSettingData.TAG);
		if (mMatchSettingData == null) {
			return;
		}
		if (mMatchSettingData.filterEnable) {// 有筛选设置
			if (TextUtils.isEmpty(mMatchSettingData.filterRegion)) {
				mSystemFilterCb.setChecked(true);
			} else {
				mRegionKey = mMatchSettingData.filterRegion;
				mLocationFilterCb.setChecked(true);
				mLocationFilterTv.setText(mMatchSettingData.selectedRegion.get(mMatchSettingData.selectedRegion.size() - 1));
			}
			// 禁用遮罩，开启过滤操作
			mDisenbleContainer.setVisibility(View.GONE);
			mContentCoverFl.setVisibility(View.GONE);
			mLocationFilterRl.setEnabled(true);
			mSystemFilterRl.setEnabled(true);
			mAgaRangeBar.setEnabled(true);
			mSaveTv.setVisibility(View.VISIBLE);

			// 年龄区间
			Integer startAge = mMatchSettingData.filterAgeRangeStart;
			Integer endAge = mMatchSettingData.filterAgeRangeEnd;
			if (startAge == null) {
				startAge = 17;
			}
			if (endAge == null) {
				endAge = 51;
			}
			mStartAge = mMatchSettingData.filterAgeRangeStart;
			mEndAge = mMatchSettingData.filterAgeRangeEnd;
			mAgaRangeBar.setScale(34, startAge - 17, endAge - 17);
			mAgaRangeBar.setOnMoveListener(new OnMoveListener() {

				@Override
				public void moveScale(int leftScale, int rightScale) {
					if (leftScale == 0) {
						mStartAge = null;
						mStartAgeTv.setText(R.string.min_age);
					} else {
						mStartAge = leftScale + 17;
						mStartAgeTv.setText(mStartAge.toString());
					}

					if (rightScale == 34) {
						mEndAge = null;
						mEndAgeTv.setText(R.string.max_age);
					} else {
						mEndAge = rightScale + 17;
						mEndAgeTv.setText(mEndAge.toString());
					}

				}
			});

			mStartAgeTv.setText(startAge == 17 ? getString(R.string.min_age) : startAge.toString());
			mEndAgeTv.setText(endAge == 51 ? getString(R.string.max_age) : endAge.toString());
		} else {
			// 过滤功能禁用，开启遮罩
			mDisenbleContainer.setVisibility(View.VISIBLE);
			mDisenbleContainer.setOnClickListener(this);
			mContentCoverFl.setVisibility(View.VISIBLE);
			mLocationFilterRl.setEnabled(false);
			mSystemFilterCb.setChecked(true);
			mSystemFilterRl.setEnabled(false);
			mAgaRangeBar.setEnabled(false);
			mSaveTv.setVisibility(View.GONE);
		}
	}

	@Override
	@OnClick({ R.id.filter_setting_back,//
	R.id.filter_setting_save_tv, R.id.disenble_filter_ll })
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.filter_setting_save_tv:
			save();
			break;
		case R.id.filter_setting_back:
			finish();
			overridePendingTransition(R.anim.stop, R.anim.sliding_out_up2down);
			break;
		case R.id.disenble_filter_ll:
			openProFeatrue();
			break;
		default:
			break;
		}
	}

	/**
	 * 开启高级功能界面
	 * 
	 * @return void
	 */
	private void openProFeatrue() {
		mUserAPI.getUserPromotionSetting(new Callback<Result<PromotionGetData>>() {

			@Override
			public void success(Result<PromotionGetData> result, Response arg1) {
				if (result != null) {
					if (result.isOk()) {
						Intent intent = new Intent(FilterSettingAct.this, ProFeatureAct.class);
						intent.putExtra(PromotionGetData.TAG, result.getData());
						startActivity(intent);
					} else {
						ToastUtil.shortToast(mContext, R.string.network_error);
					}
				}
			}

			@Override
			public void failure(RetrofitError arg0) {
				ToastUtil.shortToast(mContext, R.string.network_error);
			}
		});
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() != MotionEvent.ACTION_UP) {
			return false;
		}
		switch (v.getId()) {
		case R.id.location_filter_rl:
			mLocationFilterCb.setChecked(true);
			mSystemFilterCb.setChecked(false);
			Intent intent = new Intent(FilterSettingAct.this, LocationActivity.class);
			intent.putExtra(MatchSettingData.TAG, mMatchSettingData);
			// intent.putExtra(LocationAct.FROM_SERVER, true);
			startActivityForResult(intent, mLocationCode);
			return true;
		case R.id.system_filter_rl:
			mSystemFilterCb.setChecked(true);
			mLocationFilterCb.setChecked(false);
			mRegionKey = null;
			break;
		default:
			break;
		}
		return false;
	}

	@Override
	protected void onActivityResult(int requestcode, int resultcode, Intent result) {
		super.onActivityResult(requestcode, resultcode, result);
		if (resultcode == RESULT_OK && requestcode == mLocationCode) {
			mLocationFilterCb.setChecked(true);
			mLocationFilterTv.setText(result.getStringExtra("lastRegionName"));
			mRegionKey = result.getStringExtra("lastRegionNameKey");
			// ToastUtil.shortToast(this, mRegionKey + "-" +
			// mLocationFilterTv.getText());
		}
	}

	/**
	 * 保存数据
	 */
	private void save() {
		showProgressBar(true);
		new AMapUtil().getLocation(mContext, new LocationCallback() {

			@Override
			public void locaSuccess() {
				// ToastUtil.longToast(mContext, "success");
				saveToServer();
			}

			@Override
			public void locaError() {
				ToastUtil.longToast(mContext, R.string.network_error);
				saveToServer();
			}
		});

	}

	/**
	 * loading 控件，禁用所有操作
	 * 
	 * @param isShow
	 * @return void
	 */
	private void showProgressBar(boolean isShow) {
		if (isShow) {
			mSaveTv.setEnabled(false);
			mCloseTv.setEnabled(false);
			popup.show();
		} else {
			mSaveTv.setEnabled(true);
			mCloseTv.setEnabled(true);
			popup.hide();
		}
		// if (isShow) {
		// mLocationFilterRl.setEnabled(false);
		// mSystemFilterRl.setEnabled(false);
		// mAgaRangeBar.setEnabled(false);
		// mSaveTv.setEnabled(false);
		// mCloseTv.setEnabled(false);
		// mContentCoverFl.setVisibility(View.VISIBLE);
		// mProgressBar.setVisibility(View.VISIBLE);
		// return;
		// }
		// mLocationFilterRl.setEnabled(true);
		// mSystemFilterRl.setEnabled(true);
		// mAgaRangeBar.setEnabled(true);
		// mSaveTv.setEnabled(true);
		// mCloseTv.setEnabled(true);
		// mContentCoverFl.setVisibility(View.GONE);
		// mProgressBar.setVisibility(View.GONE);
	}

	private void saveToServer() {
		Double[] locations = AppApplication.getInstance().locationXY;
		mUserAPI.saveMatchSetting(mRegionKey, mStartAge, mEndAge, locations[0], locations[1], new Callback<Result<ResultData>>() {

			@Override
			public void failure(RetrofitError arg0) {
				showProgressBar(false);
				ToastUtil.shortToast(FilterSettingAct.this, R.string.network_error);
			}

			@Override
			public void success(Result<ResultData> result, Response arg1) {
				showProgressBar(false);
				if (result != null && result.isOk()) {
					if (AppApplication.mUserList != null) {// 清空原有的翻牌子数据
						AppApplication.mUserList.clear();
					}
					// ToastUtil.shortToast(FilterSettingAct.this, "success");
					String region = (String) mLocationFilterTv.getText();
					// 保存所选地区，用户更新主界面控件信息
					if (mSystemFilterCb.isChecked() || TextUtils.isEmpty(region)) {
						mContextUtil.setFilterRegion(null);
					} else {
						mContextUtil.setFilterRegion(region);
					}
					finish();
				}
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK: {
			finish();
			overridePendingTransition(R.anim.stop, R.anim.sliding_out_up2down);
		}
			return true;
		}
		return false;
	}
}
