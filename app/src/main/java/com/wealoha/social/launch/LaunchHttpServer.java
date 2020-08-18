package com.wealoha.social.launch;

import java.util.Map;

import android.app.IntentService;
import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.wealoha.social.utils.StringUtil;
import com.wealoha.social.utils.XL;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response.Status;

/**
 * 
 * 
 * @author javamonk
 * @see <a
 *      href="http://stackoverflow.com/questions/16560285/how-to-create-nanohttpd-server-in-android">http://stackoverflow.com/questions/16560285/how-to-create-nanohttpd-server-in-android</a>
 * @since
 * @date 2015-1-7 下午3:09:28
 */
public class LaunchHttpServer extends NanoHTTPD {

	private final String TAG = getClass().getSimpleName();
	private final IntentService service;

	public LaunchHttpServer(IntentService service, int port) {
		super("127.0.0.1", port);
		this.service = service;
	}

	@Override
	public Response serve(IHTTPSession session) {
		String uri = session.getUri();
		String queryString = session.getQueryParameterString();
		Map<String, String> params = session.getParms();
		// Referer，请求里暂时没有
		// 未来有了referer可以检测，来自[www.]wealoha.com才能呼起，否则只返回状态
		String referer = session.getHeaders().get("Referer");
		XL.d(TAG, "请求: " + uri + "?" + queryString + " Referer: " + referer);
		String callback = params.get("callback"); // jsonp
		if ("/user".equals(uri)) {
			// 发送
			String url = uri;
			if (StringUtil.isNotEmpty(queryString)) {
				url += "?" + queryString;
			}
			Intent localIntent = new Intent(LaunchService.BROADCAST_ACTION)
			// Puts the url into the Intent
					.putExtra(LaunchService.EXTENDED_DATA_URL, url);
			// Broadcasts the Intent to receivers in this app.
			XL.d(TAG, "广播收到的请求: " + url);
			LocalBroadcastManager.getInstance(service).sendBroadcast(localIntent);

			return success(callback);
		} else if ("/post".equals(uri)) {
			//
			String url = uri;
			if (StringUtil.isNotEmpty(queryString)) {
				url += "?" + queryString;
			}
			Intent localIntent = new Intent(LaunchService.BROADCAST_ACTION).putExtra(LaunchService.EXTENDED_DATA_URL, url);
			XL.d(TAG, "post广播收到的请求: " + url);
			LocalBroadcastManager.getInstance(service).sendBroadcast(localIntent);

			return success(callback);
		} else {
			StringBuilder msg = new StringBuilder();
			msg.append("<h1>Aloha!</h1>");
			return new NanoHTTPD.Response(Status.OK, "text/html", msg.toString());
		}
	}

	private NanoHTTPD.Response success(String callback) {
		String msg = callback + "({\"success\":\"true\"})";
		return new NanoHTTPD.Response(Status.OK, "text/json", msg);
	}
}
