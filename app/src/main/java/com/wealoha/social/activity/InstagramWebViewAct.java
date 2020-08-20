package com.wealoha.social.activity;

import java.net.URI;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;

import com.wealoha.social.BaseFragAct;
import com.wealoha.social.R;
import com.wealoha.social.api.ServerApi;
import com.wealoha.social.beans.AuthData;
import com.wealoha.social.beans.Result;
import com.wealoha.social.beans.instagram.AccessToken;
import com.wealoha.social.commons.GlobalConstants;
import com.wealoha.social.utils.ContextUtil;
import com.wealoha.social.utils.FontUtil;
import com.wealoha.social.utils.ToastUtil;
import com.wealoha.social.utils.XL;

public class InstagramWebViewAct extends BaseFragAct implements OnClickListener {

	@Inject
	ServerApi mOauthService;
	@Inject
	ServerApi mInstagramService;
	@Inject
	ContextUtil contextUtil;

	@Inject
	FontUtil mFont;

	@InjectView(R.id.instagram_progress)
	ProgressBar pb;
	@InjectView(R.id.instagram_back)
	ImageView mBack;
	@InjectView(R.id.instagram_title)
	TextView mTitle;

	private URI mUrl;
	private WebView webView;

	private final static String REDIRECT_URI = "http://api.wealoha.com/connect/instagram/";
	public final static String DEFAULT_GRANT_TYPE = "authorization_code";

	private boolean isAll = false;
	private int progressTemp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_instagram_webview);

		mFont.changeViewFont(mTitle, FontUtil.Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
		CookieSyncManager.createInstance(this);
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.removeAllCookie();
		// cookieManager.setAcceptCookie(false);

		Bundle isAllBundle = getIntent().getExtras();
		if (isAllBundle != null) {
			isAll = isAllBundle.getBoolean("is_all");
		}

		webView = (WebView) findViewById(R.id.instagram_web_view);
		WebSettings mWebSettings = webView.getSettings();
		mWebSettings.setJavaScriptEnabled(true);
		mWebSettings.setBuiltInZoomControls(true);
		mWebSettings.setSupportZoom(false);
		mWebSettings.setDefaultTextEncodingName("UTF-8");
		mWebSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
		// mWebSettings.setAppCacheEnabled(false);
		mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		mWebSettings.setSaveFormData(false);
		mWebSettings.setSavePassword(false);

		webView.setHapticFeedbackEnabled(false);

		StringBuilder sb = new StringBuilder();
		sb.append("https://api.instagram.com/oauth/authorize/?client_id=");
		sb.append(getString(R.string.ins_client_id));
		sb.append("&redirect_uri=");
		sb.append(REDIRECT_URI);
		sb.append("&response_type=code");

		XL.i("WEB_INFO_TEST", sb.toString());
		webView.loadUrl(sb.toString());
		webView.setWebChromeClient(new WebChromeClient() {

			public void onProgressChanged(WebView view, int progress) {
				// Log.i("WEB_VIEW", "progress:" + progress);

				if (progress >= 100) {
					pb.setVisibility(View.GONE);
				} else if (progress > progressTemp) {
					pb.setProgress(progress);
					progressTemp = progress;
				}
			}
		});
		webView.setWebViewClient(new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// view.loadUrl(urls);
				return false;
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				try {
					mUrl = new URI(url);
					String code = mUrl.getQuery();
					// XL.i("WEB_VIEW", "URL url:" + mUrl.getQuery());
					if (TextUtils.isEmpty(code)) {
						return;
					}
					String[] codeTemp = code.split("=");
					if (codeTemp.length < 2 || !"code".equals(codeTemp[0]) || TextUtils.isEmpty(codeTemp[1])) {
						return;
					}
					reqInstagramTokenInfo(codeTemp[1]);
					view.clearCache(true);
				} catch (Exception e) {
					e.printStackTrace();
					ToastUtil.longToast(InstagramWebViewAct.this, R.string.instagram_error);
					finish();
				}
			}
		});

	}

	@OnClick(R.id.instagram_back)
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.instagram_back:
			finish();
			break;
		default:
			break;
		}
	}

	/**
	 * @Description: 请求sinatoken
	 * @see:
	 * @since:
	 * @description
	 * @author: sunkist
	 * @date:2014-11-24
	 */
	private void reqInstagramTokenInfo(String codeStr) {
		if (popup != null && container != null) {
			popup.show(container);
		}
		mOauthService.accessToken(getString(R.string.ins_client_id), getResources().getString(R.string.client_secret), DEFAULT_GRANT_TYPE, REDIRECT_URI, codeStr, new Callback<AccessToken>() {

			@Override
			public void success(AccessToken at, Response arg1) {
				postInstagramToken(at.access_token, at.user.id, at.user.username);
			}

			@Override
			public void failure(RetrofitError failer) {
				if (popup != null) {
					popup.hide();
				}
				failer.getUrl();
				ToastUtil.longToast(InstagramWebViewAct.this, R.string.instagram_error);
			}
		});
	}

	private void postInstagramToken(String accessToken, String uid, final String username) {
		// com.wealoha.social.beans.User user = contextUtil.getCurrentUser();
		// if (user != null) {
		Integer month = null;
		if (!isAll) {
			month = 6;
		}
		mInstagramService.postToken(uid, accessToken, month, new Callback<Result<AuthData>>() {

			@Override
			public void success(Result<AuthData> result, Response arg1) {
				if (result != null) {
					if (result != null && result.isOk()) {
						XL.i("POST_TOKEN", "result" + result.getData().getT());
						if (popup != null) {
							popup.hide();
						}

						Bundle bundle = new Bundle();
						bundle.putBoolean("isfirst", true);
						bundle.putString("name", username);
						// bundle.putBoolean("autoSync", isAll);
						startActivity(GlobalConstants.IntentAction.INTENT_URI_CONFIG_HAVE_INSTAGRAM, bundle);
					} else if (200524 == result.getData().error) {
						ToastUtil.longToast(InstagramWebViewAct.this, R.string.instagram_token_overdue);
					} else if (200530 == result.getData().error) {
						ToastUtil.longToast(InstagramWebViewAct.this, R.string.instagram_have_token);
					} else if (451 == result.getData().error) {
						ToastUtil.longToast(InstagramWebViewAct.this, R.string.user_was_fucked);
					} else {
						ToastUtil.longToast(InstagramWebViewAct.this, R.string.Unkown_Error);
					}
				} else {
					ToastUtil.longToast(InstagramWebViewAct.this, R.string.Unkown_Error);
				}

				finish();
			}

			@Override
			public void failure(RetrofitError arg0) {
				if (popup != null) {
					popup.hide();
				}
				ToastUtil.longToast(InstagramWebViewAct.this, R.string.instagram_error);
			}
		});
		// }
	}

	@Override
	protected void onPause() {
		super.onPause();
		clearCache();
	}

	public void clearCache() {
		// File file = CacheManager.getCacheFileBaseDir();
		// if (file != null && file.exists() && file.isDirectory()) {
		// for (File item : file.listFiles()) {
		// item.delete();
		// }
		// file.delete();
		// }
		//
		// this.deleteDatabase("webview.db");
		// this.deleteDatabase("webviewCache.db");
	}
}
