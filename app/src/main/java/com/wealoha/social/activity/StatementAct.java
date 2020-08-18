package com.wealoha.social.activity;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wealoha.social.BaseFragAct;
import com.wealoha.social.R;
import com.wealoha.social.utils.FontUtil.Font;
import com.wealoha.social.utils.ToastUtil;
import com.wealoha.social.utils.Utils;

public class StatementAct extends BaseFragAct {

	private TextView title;
	private WebView content;
	private ImageView canceled;
	RelativeLayout statement_wrap_title_rl;
	@Inject
	Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_statement);
		initView();
	}

	public void initView() {

		// TODO Auto-generated method stub
		title = (TextView) findViewById(R.id.statement_title);
		content = (WebView) findViewById(R.id.statement_web);
		canceled = (ImageView) findViewById(R.id.canceled);
		statement_wrap_title_rl = (RelativeLayout) findViewById(R.id.statement_wrap_title_rl);
		fontUtil.changeFonts((ViewGroup) statement_wrap_title_rl, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
		canceled.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				StatementAct.this.finish();
			}
		});

		String statement = getIntent().getData().getPath();
		WebSettings mWebSettings = content.getSettings();
		mWebSettings.setDefaultTextEncodingName("UTF-8");

		mWebSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
		mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		content.setHapticFeedbackEnabled(false);
		content.setWebViewClient(new WebViewClient() {

			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				ToastUtil.shortToast(mContext, getString(R.string.network_error));
			}
		});
		// 06-29 17:59:39.340: D/loadDataonResume(25025):
		// loadData():mWaitLayout->zh-CN
		// 06-29 18:00:25.945: D/loadDataonResume(25920):
		// loadData():mWaitLayout->zh-TW
		// 06-29 18:01:25.915: D/loadDataonResume(26408):
		// loadData():mWaitLayout->en-AU

		Map<String, String> extraHeaders = new HashMap<String, String>();
		String language = Utils.getLocaleLanguage();
		if (TextUtils.isEmpty(language)) {
			return;
		} else if (language.contains("CN")) {
			language = "zh-CN";
		} else if (language.contains("TW")) {
			language = "zh-TW";
		} else if (language.contains("en")) {
			language = "en_US";
		}
		extraHeaders.put("Accept-Language", language);
		if (statement.equals("/service/")) {
			title.setText(R.string.terms_of_service);
			content.loadUrl("http://www.wealoha.com/doc/tos", extraHeaders);
		} else if (statement.equals("/privacy/")) {
			title.setText(R.string.privacy_policy);
			content.loadUrl("http://www.wealoha.com/doc/privacy", extraHeaders);
		}
	}
}
