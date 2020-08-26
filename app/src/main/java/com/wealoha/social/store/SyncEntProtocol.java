package com.wealoha.social.store;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import android.os.Handler;

import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.wealoha.social.beans.ImageUploadResult;
import com.wealoha.social.beans.ApiResponse;
import com.wealoha.social.commons.GlobalConstants;
import com.wealoha.social.commons.JsonController;
import com.wealoha.social.utils.ContextUtil;
import com.wealoha.social.utils.XL;

public class SyncEntProtocol extends HttpUtils {

	private static final int TIME_OUT = 10000;// 30000

	private static final String CHARSET = "UTF-8";
	private static SyncEntProtocol mHttpSingleObj;

	public static SyncEntProtocol getInstance() {
		if (mHttpSingleObj == null) {
			mHttpSingleObj = new SyncEntProtocol();
		}
		return mHttpSingleObj;
	}

	private String postMethod(String url, List<Object> params) throws IOException {

		HttpURLConnection conn = getConnection(url);
		if (conn == null) {
			return "";
		}
		conn.setRequestMethod("POST");
		// Post 请求不能使用缓存
		conn.setUseCaches(false);

		conn.setRequestProperty(" Content-Type ", " application/x-www-form-urlencoded ");
		// 连接，从postUrl.openConnection()至此的配置必须要在 connect之前完成，
		// 要注意的是connection.getOutputStream()会隐含的进行调用 connect()，所以这里可以省略
		// connection.connect();
		DataOutputStream out = new DataOutputStream(conn.getOutputStream());
		// 正文内容其实跟get的URL中'?'后的参数字符串一致
		StringBuffer buffer = new StringBuffer();
		int i = 0;

		// DataOutputStream.writeBytes将字符串中的16位的 unicode字符以8位的字符形式写道流里面
		out.writeBytes(buffer.toString());
		out.flush();
		out.close(); // flush and close

		String result = null;
		try {
			if (conn.getResponseCode() == 200) {
				if ("gzip".equalsIgnoreCase(conn.getContentEncoding())) {
					result = requestResult(conn.getInputStream(), true);
				} else {
					result = requestResult(conn.getInputStream(), false);
				}
			}
			conn.disconnect();
		} catch (java.net.SocketTimeoutException e) {
		} catch (Exception e) {
		}
		return result;
	}

	/**
	 * 提交GET请求，不用httpurlconnection是为了和4.0兼容
	 * 
	 * @Description:
	 * @param url
	 * @param params
	 * @return
	 * @throws IOException
	 * @see:
	 * @since:
	 * @author: huangyongxing
	 * @date:2012-8-8
	 */
	private String getMethod(String url, List<Object> params) throws IOException {
		String result = "";
		return result;
	}

	/**
	 * 解析服务器返回数据
	 * 
	 * @Description:
	 * @param is
	 * @param isGzip
	 * @return
	 * @throws IOException
	 * @see:
	 * @since:
	 * @author: huangyongxing
	 * @date:2012-8-8
	 */
	private String requestResult(InputStream is, boolean isGzip) throws IOException {
		int i = -1;
		if (isGzip) {
			is = new GZIPInputStream(is);
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		while ((i = is.read()) != -1) {
			baos.write(i);
		}
		baos.close();
		System.gc();
		return baos.toString(CHARSET);
	}

	/**
	 * 发送请求
	 * 
	 * @Description:
	 * @param url
	 *            目标url http://prm.35.com:9012/system/get_prm_base_url
	 * @param params
	 *            参数 [account=zhonggy@35.cn]
	 * @param isPost
	 *            是否为Post提交
	 * @return
	 * @see:
	 * @since:
	 * @author: huangyongxing
	 * @date:2012-6-29
	 */
	public String sendRequest(String url, List<Object> params, boolean isPost) {
		String result = null;
		try {
			if (isPost) {
				result = postMethod(url, params);
			} else {
				result = getMethod(url, params);
			}
		} catch (UnsupportedEncodingException e) {
		} catch (IOException e) {
		} finally {

		}

		return result;
	}

	private HttpURLConnection getConnection(String uri) {
		HttpURLConnection httpConn = null;
		URL url = null;
		try {
			url = new URL(uri);
			httpConn = (HttpURLConnection) url.openConnection();
			httpConn.setConnectTimeout(TIME_OUT);
			httpConn.setReadTimeout(TIME_OUT);
			// 打开读写属性，默认均为false
			httpConn.setDoOutput(true);
			httpConn.setDoInput(true);
			httpConn.setInstanceFollowRedirects(true);
		} catch (Exception e) {
		}
		return httpConn;
	}

	public void postByReadSessionList(String url, RequestParams requestParams) {

		mHttpSingleObj.send(HttpMethod.POST, url, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {

			}

			@Override
			public void onSuccess(ResponseInfo arg0) {

			}

		});
	}

	public String makeParams(Map<String, String> paramsVal) {
		StringBuilder builder = new StringBuilder("");
		if (paramsVal != null) {
			int size = paramsVal.size();
			int i = 0;
			for (Map.Entry<String, String> entry : paramsVal.entrySet()) {
				i++;
				if (i < size) {
					builder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
				} else {
					builder.append(entry.getKey()).append("=").append(entry.getValue());
				}
			}
		}

		return builder.toString();
	}

	/**
	 * @Description: 用户更改头像之后上传头像
	 * @see:
	 * @since:
	 * @description
	 * @author: sunkist
	 * @date:2014-11-19
	 */
	public static String byPostUploadImg(String url, String filePath, RequestParams params, Handler handler) {
		SyncEntProtocol.getInstance().send(HttpMethod.POST, GlobalConstants.ServerUrl.UPLOAD_FEED_JPG, params, new RequestCallBack<String>() {

			@Override
			public void onStart() {
				// msgTextview.setText("conn...");
			}

			@Override
			public void onLoading(long total, long current, boolean isUploading) {
				if (isUploading) {
					// msgTextview.setText("upload: " + current + "/"+ total);
				} else {
					// msgTextview.setText("reply: " + current + "/"+ total);
				}
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				// msgTextview.setText("reply: " + responseInfo.result);
				ApiResponse<ImageUploadResult> apiResponse = JsonController.parseJson(responseInfo.result, new TypeToken<ApiResponse<ImageUploadResult>>() {
				}.getType());
				XL.d("onSuccess", "" + responseInfo.result);
				if (apiResponse != null && apiResponse.isOk()) {
					XL.d("onSuccess", "" + responseInfo.result);
					// 接收上传成功的图片的id
				} else {
				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				// msgTextview.setText(error.getExceptionCode() + ":" + msg);
			}
		});
		return filePath;
	}

	public void reqSinaTokenRecove(String string) {

		RequestParams requestParams = new RequestParams();
		requestParams.addBodyParameter("access_token", string);
		HttpUtils httpUtils = new HttpUtils();
		httpUtils.send(HttpMethod.POST, "https://api.weibo.com/oauth2/revokeoauth2", requestParams, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				XL.d(CHARSET, arg1);
				XL.d(CHARSET, arg1);
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				XL.d(CHARSET, arg0.result);
				XL.d(CHARSET, arg0.result);
			}
		});
	}

	/**
	 * @Description: 请求删除未读数
	 * @see:
	 * @since:
	 * @description
	 * @author: sunkist
	 * @date:2014-11-26
	 */
	public static void reqClearNoticeCount(RequestParams requestParams) {
		HttpUtils httpUtils = new HttpUtils();
		httpUtils.send(HttpMethod.POST, GlobalConstants.ServerUrl.CLEAR_UNREAD_NOTICE_COUNT, requestParams, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {

			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {

			}
		});

	}

	public static void reqClearChatCount(String sessionId, ContextUtil contextUtil) {
		RequestParams requestParams = new RequestParams();
		requestParams.addBodyParameter("sessionId", sessionId);
		contextUtil.addGeneralHttpHeaders(requestParams);
		new HttpUtils().send(HttpMethod.POST, GlobalConstants.ServerUrl.INBOX_SESSION_CLEARUNREAD, requestParams, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {

			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
			}
		});

	}

}
