/**
 * 
 */
package com.wealoha.social.utils;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.google.gson.Gson;
import com.wealoha.social.AppApplication;
import com.wealoha.social.ContextConfig;
import com.wealoha.social.api.ServerApi;
import com.wealoha.social.beans.LocationServiceStatus;
import com.wealoha.social.beans.ApiResponse;
import com.wealoha.social.beans.ResultData;
import com.wealoha.social.inject.Injector;

public class AMapUtil implements AMapLocationListener {

	@Inject
	ServerApi locactionService;
	@Inject
	ServerApi locactionServiceAPI;
	@Inject
	ContextUtil mContextUt;
	public LocationCallback mLocaCallback;
	private LocationManagerProxy mLocationManagerProxy;
	private Context mContext;

	public static final String COORDINATE_GCJ02 = "gcj02";
	public static final String COORDINATE_WGS84 = "wgs84";
	private static final long CACHE_LIFE = 1000 * 60 * 5;
	private static final String CACHE_NAME = AMapUtil.class.getName() + "cache";

	public interface LocationCallback {

		public void locaSuccess();

		public void locaError();
	}

	public AMapUtil() {
		Injector.inject(this);
	}

	public void getLocation(Context context, LocationCallback locaCallback) {

		mLocaCallback = locaCallback;
		mContext = context;
		
		if (isCacheLifeEnd()) {
			mLocationManagerProxy = LocationManagerProxy.getInstance(mContext);
			mLocationManagerProxy.setGpsEnable(true);
			// 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
			// 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用removeUpdates()方法来取消定位请求
			// 在定位结束后，在合适的生命周期调用destroy()方法
			// 其中如果间隔时间为-1，则定位只定一次,
			// 在单次定位情况下，定位无论成功与否，都无需调用removeUpdates()方法移除请求，定位sdk内部会移除
			// provider：有三种定位Provider供用户选择，
			// 分别是:LocationManagerProxy.GPS_PROVIDER，代表使用手机GPS定位；
			// LocationManagerProxy.NETWORK_PROVIDER，代表使用手机网络定位；
			// LocationProviderProxy.AMapNetwork，代表高德网络定位服务，混合定位。
			mLocationManagerProxy.requestLocationData(LocationProviderProxy.AMapNetwork, -1, 15, this);
		} else {
			performCallback(true);
		}
	}
	/**
	 * 是否开启了位置服务
	 */
	private void getLocationServiceStatus(int errorCode){
//		String locationStatus = Settings.System.getString(mContext.getContentResolver(), Settings.System.LOCATION_PROVIDERS_ALLOWED);
//		XL.i("LOCATION_UTIL", "locationStatus:" + locationStatus);
		boolean localEnable = true;
		if(errorCode == 33){
			localEnable = false;
		}
		String json = new Gson().toJson(new LocationServiceStatus(localEnable, errorCode));
		XL.i("LOCATION_UTIL", "json:" + json);
		locactionServiceAPI.logData(json, new Callback<ApiResponse<ResultData>>() {
			
			@Override
			public void success(ApiResponse<ResultData> arg0, Response arg1) {
			}
			
			@Override
			public void failure(RetrofitError arg0) {
			}
		});
		
	}

	@Override
	public void onLocationChanged(AMapLocation amapLocation) {
		XL.i("LOCATION_UTIL", "amap errorx:" + amapLocation.getAMapException().getErrorCode());
		getLocationServiceStatus(amapLocation.getAMapException().getErrorCode());
		if (amapLocation != null && amapLocation.getAMapException().getErrorCode() == 0) {
			XL.i("LOCATION_UTIL", "latitude:" + amapLocation.getLatitude());
			XL.i("LOCATION_UTIL", "Longitude:" + amapLocation.getLongitude());
			// location[0] = amapLocation.getLatitude();
			// location[1] = amapLocation.getLongitude();
			// XL.i("LOCATION_UTIL", "LOCATION_UTIL x:" + amapLocation.getLatitude());
			// XL.i("LOCATION_UTIL", "LOCATION_UTIL y:" + amapLocation.getLongitude());
			((AppApplication) mContext.getApplicationContext()).locationXY[0] = amapLocation.getLatitude();
			((AppApplication) mContext.getApplicationContext()).locationXY[1] = amapLocation.getLongitude();
			// 获取地理位置信息的时间
			ContextConfig.getInstance().putLongWithFilename(CACHE_NAME, System.currentTimeMillis());

			locactionService.locationRecord(amapLocation.getLatitude(), amapLocation.getLongitude(), new Callback<ApiResponse<ResultData>>() {

				@Override
				public void failure(RetrofitError error) {
					XL.i("LOCATION_SERVICE_TEST", "failure service:" + error.getMessage());
				}

				@Override
				public void success(ApiResponse<ResultData> arg0, Response arg1) {
					XL.i("LOCATION_SERVICE_TEST", "success service:");
				}
			});
			performCallback(true);
		} else {
			AppApplication.getInstance().locationXY[0] = null;// 取不到位置时，将位置缓存至空
			AppApplication.getInstance().locationXY[1] = null;
			performCallback(false);
		}

		XL.i("LOCATION_UTIL", "end");
		// mLocationManagerProxy.removeUpdates(this);
		// 销毁定位
		// mLocationManagerProxy.destroy();
	}

	/**
	 * 计算地理位置的缓存是否过期，缓存生命周期为五分钟
	 * 
	 * @return boolean
	 */
	private boolean isCacheLifeEnd() {
		long endTime = System.currentTimeMillis();
		long startTime = ContextConfig.getInstance().getLongWithFilename(CACHE_NAME, endTime);
		long time = endTime - startTime;
		if (time > CACHE_LIFE || time == 0 || AppApplication.getInstance().locationXY[0] == null) {
			XL.i("LOCATION_UTIL", "start location");
			return true;
		}
		XL.i("LOCATION_UTIL", "start location for cache");
		return false;
	}

	private void performCallback(boolean isSuccess) {
		if (mLocaCallback == null) {
			// XL.i("LOCATION_UTIL", "mLocaCallback");
			return;
		}
		if (isSuccess) {
			// XL.i("LOCATION_UTIL", "mLocaCallback -- 1");
			mLocaCallback.locaSuccess();

		} else {
			// XL.i("LOCATION_UTIL", "mLocaCallback -- 2");
			mLocaCallback.locaError();
		}
	}

	@Override
	public void onLocationChanged(Location location) {
//		XL.i("LOCATION_UTIL", "onLocationChanged");
		// ToastUtil.longToast(mContext, "onLocationChanged");
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	@Override
	public void onProviderEnabled(String provider) {
		// ToastUtil.longToast(mContext, "onProviderEnabled");
		if (mLocaCallback != null) {
			XL.i("LOCATION_UTIL", "onProviderDisabled");
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
		// ToastUtil.longToast(mContext, "onProviderDisabled");
		if (mLocaCallback != null) {
			mLocaCallback.locaError();
		}
	}

}
