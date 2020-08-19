package com.wealoha.social.activity;

import java.util.List;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.OnFocusChange;

import com.wealoha.social.AppApplication;
import com.wealoha.social.BaseFragAct;
import com.wealoha.social.R;
import com.wealoha.social.adapter.LocationForFeedAdapter;
import com.wealoha.social.beans.Result;
import com.wealoha.social.beans.location.Location;
import com.wealoha.social.beans.location.LocationResult;
import com.wealoha.social.api.LocationService;
import com.wealoha.social.utils.AMapUtil;
import com.wealoha.social.utils.AMapUtil.LocationCallback;
import com.wealoha.social.utils.ToastUtil;
import com.wealoha.social.utils.XL;

public class LocationForFeedAct extends BaseFragAct implements OnClickListener, OnScrollListener,
		TextWatcher, OnItemClickListener, OnEditorActionListener, OnFocusChangeListener {

	@Inject
	LocationService locationService;
	@InjectView(R.id.location_list)
	ListView mList;
	@InjectView(R.id.location_feed_back_tv)
	ImageView mBack;
	@InjectView(R.id.location_search)
	EditText mSearch;
	@InjectView(R.id.location_delete_edit)
	ImageView mDeleteImg;
	@InjectView(R.id.location_canceled)
	TextView mCanceled;
	@InjectView(R.id.location_list_cover)
	FrameLayout mListCover;

	private LocationForFeedAdapter locationAdapter;
	private Integer mCount = 30;
	private String mCursor = "notnull";
	// 防止视图抖动加载数据
	private boolean syncLastPageBool = true;
	private String mKeyword;
	private Integer scrollType = 10000;
	private Integer searchType = 10001;

	private Integer callbackType;
	private ProgressDialog progressDialog;
	private InputMethodManager imm;
	private List<Location> firstResults;
	private AMapUtil mAmapUtil;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_location_for_feed);
		mList.setOnScrollListener(this);
		mList.setOnItemClickListener(this);
		mList.addFooterView(getLayoutInflater().inflate(R.layout.item_dzdp_icon, null));
		mSearch.addTextChangedListener(this);
		mSearch.setOnFocusChangeListener(this);
		mSearch.setFocusable(false);
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		progressDialog = new ProgressDialog(this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setIndeterminate(false);
		progressDialog.setCancelable(true);
		// progressDialog.setMessage("正在获取地址");
		progressDialog.setMessage(mResources.getString(R.string.loading));
		progressDialog.show();

		mListCover.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				clearSearchResult();
				mSearch.setText("");
				hidenInputMedthod();
				mListCover.setVisibility(View.GONE);
				return true;
			}
		});
		mAmapUtil = new AMapUtil();
		initLocation();
	}

	private Callback<Result<LocationResult>> callback = new Callback<Result<LocationResult>>() {

		@Override
		public void success(Result<LocationResult> result, Response arg1) {
			if (result != null) {
				if (result.isOk()) {
					mCursor = result.data.nextCursorId;
					if (locationAdapter == null) {
						locationAdapter = new LocationForFeedAdapter(LocationForFeedAct.this, result.data.list);
						mList.setAdapter(locationAdapter);
					} else if (callbackType == scrollType) {
						locationAdapter.notifyDataSetChangedByResult(result.data.list);
					} else if (callbackType == searchType) {
						locationAdapter.notifyDataSetChangedBySearch(result.data.list);
					}
					if (firstResults == null) {
						firstResults = result.data.list;
					}
				} else {
					ToastUtil.longToast(LocationForFeedAct.this, R.string.location_error);
				}
			} else {
				ToastUtil.longToast(LocationForFeedAct.this, R.string.location_error);
			}

			hideProgDialog();
			syncLastPageBool = true;
		}

		@Override
		public void failure(RetrofitError failureResult) {
			syncLastPageBool = true;
			ToastUtil.longToast(LocationForFeedAct.this, R.string.network_error);
			hideProgDialog();
		}
	};

	private void hideProgDialog() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();

		}

	}

	private void getLocationList(final String keyword, Integer type) {
		syncLastPageBool = false;
		if (TextUtils.isEmpty(mCursor) || "null".equals(mCursor)) {
			hideProgDialog();
			syncLastPageBool = true;
			return;
		}

		if ("notnull".equals(mCursor)) {
			mCursor = null;
		}

		final AppApplication application = ((AppApplication) getApplication());
		callbackType = type;
		mAmapUtil.getLocation(this, new LocationCallback() {

			@Override
			public void locaSuccess() {
				locationService.location(keyword, AMapUtil.COORDINATE_GCJ02, application.locationXY[0], application.locationXY[1], mCount, mCursor, callback);
			}

			@Override
			public void locaError() {
				locationService.location(keyword, AMapUtil.COORDINATE_GCJ02, application.locationXY[0], application.locationXY[1], mCount, mCursor, callback);
			}
		});
		// locationService.location(keyword, AMapUtil.COORDINATE_GCJ02,
		// 35.708974, 139.731920, mCount,
		// mCursor, callback);
	}

	@Override
	@OnClick({ R.id.location_search, R.id.location_canceled, R.id.location_delete_edit, R.id.location_feed_back_tv })
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.location_feed_back_tv:
			finish(R.anim.stop, R.anim.push_bottom_out);
			break;
		case R.id.location_delete_edit:
			clearSearchResult();
			mSearch.setText("");
			break;
		case R.id.location_canceled:
			clearSearchResult();
			mSearch.setText("");
			hidenInputMedthod();
			break;
		case R.id.location_search:
			mSearch.setFocusable(true);
			mSearch.setFocusableInTouchMode(true);
			mSearch.requestFocus();
			// mSearch.requestFocus();
			showInputMethod();
			break;
		default:
			break;
		}
	}

	@Override
	@OnFocusChange(R.id.location_search)
	public void onFocusChange(View v, boolean hasFocus) {
		switch (v.getId()) {
		case R.id.location_search:
			if (hasFocus) {
				XL.i("FOCUSE_TEST", "FOCUSE:" + hasFocus);
				if (TextUtils.isEmpty(mSearch.getText().toString())) {
					mListCover.setVisibility(View.VISIBLE);
				} else {
					mListCover.setVisibility(View.GONE);
				}
				mCanceled.setVisibility(View.VISIBLE);
			} else {
				XL.i("FOCUSE_TEST", "FOCUSE:" + hasFocus);
				mListCover.setVisibility(View.GONE);
				mCanceled.setVisibility(View.GONE);
			}
			break;

		default:
			break;
		}
	}

	private void clearSearchResult() {
		mCursor = "notnull";
		if (locationAdapter != null) {
			// locationAdapter.clear();
			locationAdapter.notifyDataSetChangedBySearch(firstResults);
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if ((firstVisibleItem + visibleItemCount + 10) == totalItemCount && syncLastPageBool) {
			getLocationList(mSearch.getText().toString(), scrollType);
		}
	}

	@Override
	public void afterTextChanged(Editable s) {
		mKeyword = s.toString();
		mCursor = "notnull";
		if (TextUtils.isEmpty(mKeyword)) {
			mDeleteImg.setVisibility(View.GONE);
			clearSearchResult();

			mListCover.setVisibility(View.VISIBLE);
		} else {
			mDeleteImg.setVisibility(View.VISIBLE);
			mList.setSelection(0);
			mListCover.setVisibility(View.GONE);
		}
		getLocationList(mKeyword, searchType);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent intent = new Intent();
		Location location = (Location) mList.getItemAtPosition(position);
		intent.putExtra("location", location);
		setResult(RESULT_OK, intent);
		finish(R.anim.stop, R.anim.push_bottom_out);
	}

	private void initLocation() {
		XL.i("LOCATION_SERVICE_TEST", "initLocation");
		getLocationList(null, scrollType);
		// new AMapUtil().getLocation(this, new LocationCallback() {
		//
		// @Override
		// public void locaSuccess(AMapLocation amaplocation) {
		// XL.i("LOCATION_SERVICE_TEST", "locaSuccess");
		// locationService.locationRecord(amaplocation.getLatitude(),
		// amaplocation.getLongitude(), new
		// Callback<Result<ResultData>>() {
		//
		// @Override
		// public void failure(RetrofitError error) {
		// XL.i("LOCATION_SERVICE_TEST", "failure:" + error.getMessage());
		// }
		//
		// @Override
		// public void success(Result<ResultData> arg0, Response arg1) {
		// XL.i("LOCATION_SERVICE_TEST", "success:");
		// }
		// });
		// // 定位成功会取得当前最新的地理位置坐标
		// getLocationList(null, scrollType);
		// }
		//
		// @Override
		// public void locaError() {
		// // hideProgDialog();
		// // 定位失败会用上一次的地理位置坐标来计算附近的信息
		// XL.i("LOCATION_SERVICE_TEST", "locaError");
		// getLocationList(null, scrollType);
		// }
		// });
	}

	public void hidenInputMedthod() {
		if (imm != null && mSearch != null && mList != null) {
			mList.setFocusable(true);
			mList.setFocusableInTouchMode(true);
			mList.requestFocus();
			imm.hideSoftInputFromWindow(mSearch.getWindowToken(), 0);
		}
	}

	public void showInputMethod() {
		if (imm != null && mSearch != null) {
			imm.showSoftInput(mSearch, 0);
			// imm.hideSoftInputFromWindow(mSearch.getWindowToken(), 0);
		}
	}

	@Override
	@OnEditorAction(R.id.location_search)
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (actionId == KeyEvent.KEYCODE_HOME) {
			imm.hideSoftInputFromWindow(mSearch.getWindowToken(), 0);
		}
		return true;
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {

	}

}
