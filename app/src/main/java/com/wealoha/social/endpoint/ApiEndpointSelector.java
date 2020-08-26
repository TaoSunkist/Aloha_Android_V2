package com.wealoha.social.endpoint;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

import javax.inject.Inject;

import org.apache.commons.collections4.MapUtils;

import com.wealoha.social.beans.ApiResponse;
import com.wealoha.social.beans.common.ApiEndpointData;
import com.wealoha.social.api.ConstantsService;
import com.wealoha.social.commons.GlobalConstants;
import com.wealoha.social.inject.Injector;
import com.wealoha.social.utils.ContextUtil;
import com.wealoha.social.utils.RemoteLogUtil;
import com.wealoha.social.utils.XL;

/**
 * 自动选择api的ip，参见 http://redmine.wealoha.com/projects/backend/wiki/ServiceEndpoint
 * 
 * @author javamonk * @since
 * @date 2015-1-13 上午10:55:32
 */
public class ApiEndpointSelector {

	@Inject
	ApiEndpoint apiEndpoint;
	@Inject
	ContextUtil contexUtil;
	@Inject
	RemoteLogUtil remoteLogUtil;

	@Inject
	ConstantsService constantsService;

	private String TAG = getClass().getSimpleName();

	private ExecutorService executorService = Executors.newSingleThreadExecutor();

	private List<String> hostList = Arrays.asList("api.wealoha.com", //
													"121.201.7.28", //
													"api-pek2.wealoha.com", //
													"119.254.111.236", //
													"api-ap1.wealoha.com", //
													"207.226.142.55");

	private long lastTest;

	public ApiEndpointSelector() {
		Injector.inject(this);
	}

	private List<String> getHostList() {
		if (contexUtil.isTestingApi()) {
			return Arrays.asList("aloha-api.cuapp.me");
		}
		return hostList;
	}

	public void testAndSelectEndpoint() {
		long millis = System.currentTimeMillis() - lastTest;
		if (millis < 60 * 1000) {
			XL.d(TAG, "上次测试: " + millis);
			return;
		}
		millis = lastTest;

		executorService.execute(new Runnable() {

			@Override
			public void run() {
				List<String> hostList = getHostList();
				for (int i = 0; i < hostList.size(); i++) {
					String host = hostList.get(i);
					apiEndpoint.setEndpoint("http://" + host);

					XL.i(TAG, "尝试使用'" + host + "'获取ip..");
					log("尝试使用'" + host + "'获取ip..", null);
					try {
						ApiResponse<ApiEndpointData> apiResponse = constantsService.apiEndpoing();
						if (apiResponse != null) {
							if (apiResponse.isOk()) {
								if (i == 0) {
									// 如果第一个能用，不再修改
									XL.i(TAG, "使用默认IP: " + host);
									// log("使用IP: " + host, null);
									// 修改GlobalConsants的所有url
									changeXUtilsUrl(host);
									return;
								}

								ApiEndpointData data = apiResponse.getData();
								if (data.ip != null && data.ip.size() > 0) {
									for (String ip : data.ip) {
										XL.i(TAG, "测试'" + host + "'返回的ip'" + ip + "'..");
										log("测试'" + host + "'返回的ip'" + ip + "'..", null);
										if (setAndTestIp(ip)) {
											// 测试ip是否可用
											XL.i(TAG, "使用IP: " + ip);
											log("使用IP: " + ip, null);
											// 修改GlobalConsants的所有url
											changeXUtilsUrl(ip);
											return;
										}
									}
								}
							}
						}
					} catch (Throwable e) {
						// try next
						log("获取ip失败: " + host, e);
						XL.w(TAG, "获取ip失败: " + host, e);
					}

				}
			}
		});
	}

	private void log(String message, Throwable t) {
		// 暂时不写远程log remoteLogUtil.log(message, t);
	}

	public void testImageEndpoint() {
		XL.i(TAG, "开始测试图片地址可用性");
		executorService.execute(new Runnable() {

			@Override
			public void run() {

				try {
					ApiResponse<ApiEndpointData> apiResponse = constantsService.apiEndpoing();
					if (apiResponse != null) {
						if (apiResponse.isOk()) {
							ApiEndpointData data = apiResponse.getData();
							testImageEndpoint(data.imageTestUrlMap);
						}
					}
				} catch (Throwable e) {
					log("测试图片地址失败: ", e);
					// try next
					XL.w(TAG, "设置图片地址失败: ", e);
				}
			}
		});
	}

	private void testImageEndpoint(Map<String, String> urlMap) {
		if (MapUtils.isNotEmpty(urlMap)) {
			List<String> okList = new ArrayList<String>();
			List<String> failList = new ArrayList<String>();
			for (Entry<String, String> entry : urlMap.entrySet()) {
				String id = entry.getKey();
				String url = entry.getValue();
				XL.i(TAG, "测试'" + url + "'..");
				try {
//					com.wealoha.libcurldroid.Result result = CurlHttp.newInstance() //
//					.setTimeoutMillis(30 * 1000) //
//					.setConnectionTimeoutMillis(20 * 1000) //
//					.getUrl(url).perform();
//					if (result.getStatus() == HttpURLConnection.HTTP_OK) {
//						XL.i(TAG, "请求图片地址成功，设定url=" + url);
//						log("请求图片地址成功，设定url=" + url, null);
//						okList.add(id);
//					} else {
//						failList.add(id);
//					}
				} catch (Throwable e) {
					failList.add(id);
					XL.w(TAG, "请求图片地址时异常：url=" + url, e);
					log("请求图片地址时异常：url=" + url, e);
				}
			}
			apiEndpoint.setUrlFail(failList);
			apiEndpoint.setUrlOk(okList);
			XL.i(TAG, "图片测试结果: ok=" + okList + ", fail=" + failList);
		}
	}

	private boolean setAndTestIp(String ip) {
		try {
			apiEndpoint.setEndpoint("http://" + ip);

			ApiResponse<ApiEndpointData> apiResponse = constantsService.apiEndpoing();
			return apiResponse != null && apiResponse.isOk();
		} catch (Throwable e) {
			XL.w(TAG, "测试ip失败: " + ip, e);
			log("测试ip失败: " + ip, e);
		}
		return false;
	}

	private final Pattern p = Pattern.compile("http://[^/]+");

	private void changeXUtilsUrl(String ip) {
		Field[] declaredFields = GlobalConstants.ServerUrl.class.getDeclaredFields();
		for (Field field : declaredFields) {
			if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
				try {
					Object value = field.get(null);
					if (value instanceof String) {
						String oldValue = (String) value;
						String newValue = p.matcher(oldValue).replaceFirst("http://" + ip);
						field.set(null, newValue);
						XL.i(TAG, "修改属性: " + field.getName() + " " + oldValue + " -> " + newValue);
						log("修改属性: " + field.getName() + " " + oldValue + " -> " + newValue, null);
					}
				} catch (Throwable e) {
					XL.w(TAG, "修改属性失败", e);
					log("修改属性失败", e);
				}
			}
		}
	}
}
