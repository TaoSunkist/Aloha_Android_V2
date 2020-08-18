package com.wealoha.social.activity;

import java.lang.reflect.Method;
import java.util.Collections;

import javax.inject.Inject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;

import com.wealoha.social.BaseFragAct;
import com.wealoha.social.R;
import com.wealoha.social.beans.User;
import com.wealoha.social.store.PopupStore;
import com.wealoha.social.store.UserAgentProvider;
import com.wealoha.social.utils.ContextUtil;
import com.wealoha.social.utils.RegionNodeUtil;
import com.wealoha.social.utils.ToastUtil;
import com.wealoha.social.view.custom.AlohaWebView;

/**
 * @author:sunkist
 * @see:
 * @since:
 * @description 以后在统一成一个Activity
 * @copyright wealoha.com
 * @Date:2015-1-6
 */
public class WebActivity extends BaseFragAct implements OnClickListener {

	private BaseFragAct mBaseFragAct;
	@InjectView(R.id.web_root_rl)
	LinearLayout layout;
	@InjectView(R.id.web_view)
	AlohaWebView webView;
	@InjectView(R.id.faq_statement_title)
	TextView faq_statement_title;
	@InjectView(R.id.faq_canceled)
	ImageView mCanceled;
	@InjectView(R.id.faq_share)
	ImageView mFaqShare;
	@Inject
	UserAgentProvider mUserAgentProvider;
	private User mToUser;
	String mUrl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web);
		Intent intent = getIntent();
		mBaseFragAct = this;
		if (intent != null) {
			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				mUrl = bundle.getString("url");
				mToUser = (User) bundle.getParcelable(User.TAG);
				initLayout(mUrl, mToUser);
			}
		}

	}

	private void initLayout(String uid, User mToUser2) {

		RelativeLayout.LayoutParams fillParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);
		layout = new LinearLayout(this);
		layout.setLayoutParams(fillParams);
		layout.setOrientation(LinearLayout.VERTICAL);

		if (Build.VERSION.SDK_INT >= 11) {
			Class[] name = new Class[] { String.class };
			Object[] rmMethodName = new Object[] { "searchBoxJavaBridge_" };
			Method rji;
			try {
				rji = webView.getClass().getDeclaredMethod(
						"removeJavascriptInterface", name);
				rji.invoke(webView, rmMethodName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		webView.setBackgroundColor(Color.WHITE);
		webView.setVerticalScrollBarEnabled(false);
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setLoadWithOverviewMode(false);
		// webSettings.
		webSettings.setUseWideViewPort(true);
		/* 设置网页支持缩放 */
		webSettings.setSupportZoom(true);
		/* 设置网页自适应高宽 */
		webView.loadUrl(uid.startsWith("htt")?uid:"http://"+uid, Collections.singletonMap("AlohaWebUserAgent",
				mUserAgentProvider.getUserAgent()));
		webView.setWebViewClient(new WebViewClient() {

			@Override
			public void onPageFinished(WebView view, String url) {
				// mLoadingDialog.dismiss();
				if (TextUtils.isEmpty(webView.getTitle())) {
				} else {
					faq_statement_title.setText(webView.getTitle());
				}
			}
		});
	}

	@Inject
	RegionNodeUtil regionNodeUtil;
	@Inject
	ContextUtil mContextUtil;

	@OnClick({ R.id.faq_canceled, R.id.faq_share })
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.faq_share:
			if(TextUtils.isEmpty(mUrl)){
				ToastUtil.shortToast(mContext, R.string.Unkown_Error);
				return;
			}
			if (!mUrl.startsWith("htt")) {
				mUrl = "http://" + mUrl;
			}
			new PopupStore(regionNodeUtil).showShareProfileUrl(
					getString(R.string.web_act_share), mBaseFragAct,
					webView != null ? webView.getTitle() : mUrl, mUrl);
			break;
		case R.id.faq_canceled:
			onBackKeyPressed();
			break;
		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
			mLoadingDialog.dismiss();
		}
		if (webView != null) {
			webView.clearCache(true);
			webView.removeView(webView);
			webView.removeAllViews();
		}
	}
}
