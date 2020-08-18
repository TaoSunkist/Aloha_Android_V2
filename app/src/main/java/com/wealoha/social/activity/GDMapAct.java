package com.wealoha.social.activity;

import javax.inject.Inject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.squareup.picasso.Picasso;
import com.wealoha.social.R;
import com.wealoha.social.inject.Injector;

public class GDMapAct extends Activity {

	@Inject
	Picasso picasso;
	private ProgressDialog progDialog = null;
	private AMap aMap;
	private MapView mapView;
	private LatLng latLonPoint;

	ImageView mBack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_gd_map);
		Injector.inject(this);
		mBack = (ImageView) findViewById(R.id.location_back_iv);
		mBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);// 此方法必须重写
		init();
		initLocation();
	}

	/**
	 * 初始化AMap对象
	 */
	private void init() {
		if (aMap == null) {
			aMap = mapView.getMap();
		}
		progDialog = new ProgressDialog(this);

	}

	private void initLocation() {
		Intent intent = getIntent();
		if (intent != null) {
			Double latitude = intent.getDoubleExtra("latitude", 0.0);
			Double longitude = intent.getDoubleExtra("longitude", 0.0);
			Boolean venueAbroad = intent.getBooleanExtra("venueAbroad", false);
			latLonPoint = new LatLng(latitude, longitude);
			Marker marker = aMap.addMarker(new MarkerOptions().position(latLonPoint).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).draggable(true));
			if (venueAbroad) {
				aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLonPoint, 9));
			} else {
				aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLonPoint, 15));
			}
			marker.showInfoWindow();// 设置默认显示一个infowinfow
		}
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}

	/**
	 * 显示进度条对话框
	 */
	public void showDialog() {
		progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progDialog.setIndeterminate(false);
		progDialog.setCancelable(true);
		// progDialog.setMessage("正在获取地址");
		progDialog.show();
	}

	/**
	 * 隐藏进度条对话框
	 */
	public void dismissDialog() {
		if (progDialog != null) {
			progDialog.dismiss();
		}
	}

}
