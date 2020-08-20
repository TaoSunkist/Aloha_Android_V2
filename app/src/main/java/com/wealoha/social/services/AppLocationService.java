package com.wealoha.social.services;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.wealoha.social.AppApplication;
import com.wealoha.social.api.ServerApi;
import com.wealoha.social.beans.Result;
import com.wealoha.social.beans.ResultData;
import com.wealoha.social.inject.Injector;
import com.wealoha.social.utils.XL;

public class AppLocationService extends Service implements AMapLocationListener {

	@Inject
	ServerApi locactionService;
	private LocationManagerProxy mLocationManagerProxy;
	private Double[] location = { null, null };

	// private long time = 60 * 30 * 1000;
	private long time = 15 * 60 * 1000;
	MyTimer timer = new MyTimer(time, time);

	private class MyTimer extends CountDownTimer {

		public MyTimer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onTick(long millisUntilFinished) {

		}

		@Override
		public void onFinish() {
			// init();
			timer.start();
		}

	}

	@Override
	public void onCreate() {
		super.onCreate();
		XL.i("LOCATION_SERVICE", "----creat----");
		Injector.inject(this);
	}

	/**
	 * 初始化定位
	 */
	private void init() {
		XL.i("LOCATION_SERVICE", "----INIT----");

		// 初始化定位，只采用网络定位
		mLocationManagerProxy = LocationManagerProxy.getInstance(this);
		mLocationManagerProxy.setGpsEnable(true);
		// 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
		// 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用removeUpdates()方法来取消定位请求
		// 在定位结束后，在合适的生命周期调用destroy()方法
		// 其中如果间隔时间为-1，则定位只定一次,
		// 在单次定位情况下，定位无论成功与否，都无需调用removeUpdates()方法移除请求，定位sdk内部会移除
		mLocationManagerProxy.requestLocationData(LocationProviderProxy.AMapNetwork, -1, 100, this);

	}

	@Override
	public void onLocationChanged(AMapLocation amapLocation) {
		XL.i("LOCATION_SERVICE", "----CHANGED----：" + amapLocation.getAMapException().getErrorCode());
		if (amapLocation != null && amapLocation.getAMapException().getErrorCode() == 0) {
			// location[0] = amapLocation.getLatitude();
			// location[1] = amapLocation.getLongitude();
			XL.i("LOCATION_SERVICE", "LOCATION_SERVICE x:" + amapLocation.getLatitude());
			XL.i("LOCATION_SERVICE", "LOCATION_SERVICE y:" + amapLocation.getLongitude());
			((AppApplication) getApplication()).locationXY[0] = amapLocation.getLatitude();
			((AppApplication) getApplication()).locationXY[1] = amapLocation.getLongitude();
			locactionService.locationRecord(amapLocation.getLatitude(), amapLocation.getLongitude(), new Callback<Result<ResultData>>() {

				@Override
				public void failure(RetrofitError error) {
					XL.i("LOCATION_SERVICE_TEST", "failure service:" + error.getMessage());
				}

				@Override
				public void success(Result<ResultData> arg0, Response arg1) {
					XL.i("LOCATION_SERVICE_TEST", "success service:");
				}
			});
		}

		mLocationManagerProxy.removeUpdates(this);
		// 销毁定位
		mLocationManagerProxy.destroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		init();
		timer.start();
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// 移除定位请求
		mLocationManagerProxy.removeUpdates(this);
		// 销毁定位
		mLocationManagerProxy.destroy();
		XL.i("LOCATION_SERVICE", "----DESTROY----");

	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	public class LocationBiner extends Binder {

		public Double[] getXY() {
			return location;
		}
	}

	@Override
	public void onLocationChanged(Location location) {

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	@Override
	public void onProviderEnabled(String provider) {

	}

	@Override
	public void onProviderDisabled(String provider) {

	}
}
