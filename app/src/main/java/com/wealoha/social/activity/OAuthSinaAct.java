package com.wealoha.social.activity;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.wealoha.social.ActivityManager;
import com.wealoha.social.BaseFragAct;
import com.wealoha.social.R;
import com.wealoha.social.beans.AuthData;
import com.wealoha.social.beans.Result;
import com.wealoha.social.beans.SinaTokenBean;
import com.wealoha.social.beans.User;
import com.wealoha.social.beans.UsersAPI;
import com.wealoha.social.beans.sina.AccessTokenKeeper;
import com.wealoha.social.beans.user.ConnectService;
import com.wealoha.social.commons.AlohaThreadPool;
import com.wealoha.social.commons.AlohaThreadPool.ENUM_Thread_Level;
import com.wealoha.social.commons.GlobalConstants;
import com.wealoha.social.commons.JsonController;
import com.wealoha.social.utils.ContextUtil;
import com.wealoha.social.utils.ToastUtil;
import com.wealoha.social.utils.XL;
import com.wealoha.social.view.custom.dialog.FlippingLoadingDialog;

public class OAuthSinaAct extends BaseFragAct {

	private static final String TAG = OAuthSinaAct.class.getSimpleName();
	WebView webView;
	private LinearLayout layout = null;
	/* sina的用户信息封装bean */
	private Oauth2AccessToken oauth2AccessToken;
	/* 用户请求token */
	private final int REQ_TOKEN_INFO = 0x000001;
	/* 获取授权后的code，需要这个去请求token */
	private String codeStr;
	private StringBuffer pathBuf;
	@Inject
	ContextUtil contextUtil;
	@Inject
	ConnectService connectService;

	private Context mContext;
	private FlippingLoadingDialog mFlippingLoadingDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Window mWindow = getWindow();
		mWindow.setWindowAnimations(R.style.dialog_alert_usernick_enter_anim);
		ActivityManager.push(this);
		mContext = this;
		mFlippingLoadingDialog = new FlippingLoadingDialog(mContext, R.layout.popup_prompt, new RelativeLayout.LayoutParams(android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
		initData();
		initLayout();
	}

	/**
	 * @Description:
	 * @see:
	 * @since:
	 * @description
	 * @author: sunkist
	 * @date:2014-8-6
	 */
	public void initData() {
		oauth2AccessToken = new Oauth2AccessToken();
		pathBuf = new StringBuffer();
		pathBuf.append("https://api.weibo.com/oauth2/authorize");
		pathBuf.append("?client_id=");
		pathBuf.append(GlobalConstants.AppConstact.SINA_APP_KEY);
		pathBuf.append("&redirect_uri=");
		pathBuf.append(GlobalConstants.AppConstact.SINA_REDIRECT_URL);
		XL.d(TAG, pathBuf.toString());
	}

	/**
	 * @Description: 初始化布局
	 * @see:
	 * @since:
	 * @description
	 * @author: sunkist
	 * @date:2014-8-6
	 */
	@SuppressLint("SetJavaScriptEnabled")
	@SuppressWarnings("deprecation")
	public void initLayout() {
		RelativeLayout.LayoutParams fillParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
		layout = new LinearLayout(mContext);
		layout.setLayoutParams(fillParams);
		layout.setOrientation(LinearLayout.VERTICAL);

		webView = new WebView(mContext);
		if (Build.VERSION.SDK_INT >= 11) {
			Class[] name = new Class[] { String.class };
			Object[] rmMethodName = new Object[] { "searchBoxJavaBridge_" };
			Method rji;
			try {
				rji = webView.getClass().getDeclaredMethod("removeJavascriptInterface", name);
				rji.invoke(webView, rmMethodName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		RelativeLayout.LayoutParams wvParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
		webView.setLayoutParams(wvParams);
		webView.setBackgroundColor(Color.WHITE);
		webView.setVerticalScrollBarEnabled(false);
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setLoadWithOverviewMode(false);
		webSettings.setUseWideViewPort(true);
		webSettings.setSavePassword(false);
		webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
		/* 设置网页支持缩放 */
		webSettings.setSupportZoom(true);
		/* 设置网页自适应高宽 */
		webView.clearHistory();
		webView.clearCache(true);
		webView.loadUrl(pathBuf.toString());
		mFlippingLoadingDialog.show();
		webView.setWebViewClient(new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				codeStr = url.split("=")[1];
				if (codeStr == null || "".equals(codeStr)) {
					ToastUtil.shortToast(mContext, R.string.login_failed_tlease_try_again);
					// ToastUtil.shortToastCenter(mContext,
					// "微博授权认证失败,请联系Aloha客服");
					return true;
				} else {
					handle.sendEmptyMessage(REQ_TOKEN_INFO);
					return true;
				}
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				mFlippingLoadingDialog.dismiss();
				super.onPageFinished(view, url);
			}

		});
		layout.addView(webView);
		setContentView(layout, new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}

	private final static class OAuthSinaHandler extends Handler {

		public WeakReference<OAuthSinaAct> mAct;

		public OAuthSinaHandler(OAuthSinaAct act) {
			mAct = new WeakReference<OAuthSinaAct>(act);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			OAuthSinaAct act = mAct.get();
			if (act != null) {
				act.handlerService(msg);
			}
		}
	}

	private void handlerService(Message msg) {
		switch (msg.what) {
		case REQ_TOKEN_INFO:
			reqSinaTokenInfo();
			break;
		}
	}

	Handler handle = new OAuthSinaHandler(this);

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			ActivityManager.pop(this);
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * @Description: 请求sinatoken
	 * @see:
	 * @since:
	 * @description
	 * @author: sunkist
	 * @date:2014-11-24
	 */
	private void reqSinaTokenInfo() {
		
		String urlStr = "https://api.weibo.com/oauth2/access_token";
		HttpUtils httpUtils = new HttpUtils();
		RequestParams requestParams = new RequestParams();
		requestParams.addBodyParameter("grant_type", "authorization_code");
		requestParams.addBodyParameter("code", codeStr);
		requestParams.addBodyParameter("client_id", GlobalConstants.AppConstact.SINA_APP_KEY);
		requestParams.addBodyParameter("client_secret", GlobalConstants.AppConstact.SINA_APP_SECRET);
		requestParams.addBodyParameter("redirect_uri", GlobalConstants.AppConstact.SINA_REDIRECT_URL);
		
		httpUtils.send(HttpMethod.POST, urlStr, requestParams, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				SinaTokenBean result = JsonController.parseSinaJson(arg0.result, new TypeToken<SinaTokenBean>() {
				}.getType());
				if (!TextUtils.isEmpty(result.accessToken) && !TextUtils.isEmpty(result.uid)) {
					oauth2AccessToken.setToken(result.accessToken);
					oauth2AccessToken.setUid(result.uid);
					oauth2AccessToken.setExpiresIn(result.expiresIn);
					AccessTokenKeeper.writeAccessToken(mContext, oauth2AccessToken);
					getSinaUserData(result);
				} else {
					ToastUtil.shortToast(mContext, R.string.login_failed_tlease_try_again);
				}
			}

			@Override
			public void onFailure(com.lidroid.xutils.exception.HttpException arg0, String arg1) {
				webView.loadUrl(pathBuf.toString());
			}
		});
		
	}

	protected void getSinaUserData(final SinaTokenBean result) {
		if (oauth2AccessToken != null && oauth2AccessToken.isSessionValid()) {
			UsersAPI mUsersAPI = new UsersAPI(mContext, codeStr, oauth2AccessToken);
			long uid = Long.parseLong(oauth2AccessToken.getUid());

			mUsersAPI.show(uid, new RequestListener() {

				@Override
				public void onComplete(final String response) {
					if (!TextUtils.isEmpty(response)) {
						AlohaThreadPool.getInstance(ENUM_Thread_Level.TL_AtOnce).execute(new Runnable() {

							@Override
							public void run() {
								SinaTokenBean sinaTokenBean = JsonController.parseSinaJson(response, new TypeToken<SinaTokenBean>() {
								}.getType());
								result.name = sinaTokenBean.name;
								openUserDataUI(result);
							}
						});
					} else {
						// ToastUtil.shortToastCenter(mContext, "获取用户昵称失败");
					}
				}

				@Override
				public void onWeiboException(WeiboException e) {
				}
			});
		}
	}

	/**
	 * @Description:
	 * @see:
	 * @since:
	 * @description
	 * @author: sunkist
	 * @date:2014-11-18
	 */
	private void openUserDataUI(final SinaTokenBean sinaTokenBean) {

		connectService.connectWeibo(sinaTokenBean.uid, sinaTokenBean.accessToken, new Callback<Result<AuthData>>() {

			@Override
			public void success(Result<AuthData> result, Response response) {
				if (result != null) {
					if (result.isOk()) {
						Bundle bundle = new Bundle();
						result.data.user.setAccessToken(sinaTokenBean.accessToken);
						bundle.putParcelable(User.TAG, result.data.user);
						contextUtil.setCurrentSinaWbToken(result.data.user.getAccessToken());
						afterSinaLoginSuccess(result.data.user, result.data.t);
						if (result.data.user.getProfileIncomplete()) {
							startActivityAndCleanTask(GlobalConstants.IntentAction.INTENT_URI_USER_DATA, null);
						} else {
							startActivityAndCleanTask(GlobalConstants.IntentAction.INTENT_URI_MAIN, null);
						}
						finish();
					} else if (451 == result.status) {
						// ToastUtil.shortToast(mContext, "該賬號已被封停");
						ToastUtil.shortToast(mContext, R.string.failed);
						finish();
					}
				}
			}

			@Override
			public void failure(RetrofitError error) {
				// XL.w(TAG, "绑定新浪异常", error);
				// ToastUtil.shortToast(mContext, "賬號授權失敗");
				ToastUtil.shortToast(mContext, R.string.failed);
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		webView.clearCache(true);
		if (webView != null) {
			webView.removeAllViews();
		}
	}

}
