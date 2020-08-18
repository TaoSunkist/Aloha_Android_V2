package com.wealoha.social.commons;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;

import org.apache.commons.lang3.StringUtils;

import android.content.Context;
import android.net.Uri;

import com.squareup.picasso.OkHttpDownloader;
import com.wealoha.social.utils.ContextUtil;
import com.wealoha.social.utils.XL;

/**
 * 给图片请求增加用户身份信息
 * 
 * @author javamonk
 * @see
 * @since
 * @date 2015-1-19 下午12:15:31
 */
public class CustomHttpDownloader extends OkHttpDownloader {

	private ContextUtil contextUtil;

	private final String TAG = getClass().getName();

	public CustomHttpDownloader(Context context, ContextUtil contextUtil) {
		super(context);
		this.contextUtil = contextUtil;
	}

	public CustomHttpDownloader(File cacheDir, ContextUtil contextUtil) {
		super(cacheDir);
		this.contextUtil = contextUtil;
	}

	@Override
	protected HttpURLConnection openConnection(final Uri uri) throws IOException {
		HttpURLConnection connection = super.openConnection(uri);
		String url = uri.toString();
		if (StringUtils.contains(url, "/v1/file/image")) {
			String t = contextUtil.getCurrentTicket();
			if (t != null) {
				XL.d(TAG, "请求增加用户身份信息");
				try {
					connection.addRequestProperty("Cookie", "t=" + t);
					connection.setInstanceFollowRedirects(false);
					connection.setUseCaches(false);
					connection.setReadTimeout(30 * 1000);
					connection.setConnectTimeout(30 * 1000);
					connection.addRequestProperty("Connection", "close");
					connection.connect();

					String location = connection.getHeaderField("Location");
					XL.d(TAG, "Location: " + location);
					if (location != null) {
						return super.openConnection(Uri.parse(location));
					} else {
						return super.openConnection(uri);
					}
				} finally {
					connection.disconnect();
				}
			}
		}
		return connection;
	}
}
