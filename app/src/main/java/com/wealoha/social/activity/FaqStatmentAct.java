package com.wealoha.social.activity;

import javax.inject.Inject;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;

import com.wealoha.social.R;
import com.wealoha.social.fragment.BaseFragment;
import com.wealoha.social.utils.ToastUtil;

/**
 * 设置-常见问题Act
 * 
 * @author hongwei
 * 
 * @date Nov 17, 2014
 */
public class FaqStatmentAct extends BaseFragment implements OnClickListener {

	@InjectView(R.id.faq_statement_web)
	WebView content;
	@InjectView(R.id.faq_canceled)
	ImageView canceled;

	@InjectView(R.id.faq_statement_title)
	TextView mTitle;
	@Inject
	Context context;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.act_setting_faq, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		String url = "http://wealoha.com/faq-android.html";
		Bundle bundle = getArguments();
		if (bundle != null && "about".equals(bundle.getString("about"))) {
			url = "http://wealoha.com/faq-android.html";
			mTitle.setText(getResources().getString(R.string.wechat));
		}

		WebSettings mWebSettings = content.getSettings();
		mWebSettings.setJavaScriptEnabled(true);
		mWebSettings.setBuiltInZoomControls(true);
		mWebSettings.setSupportZoom(false);
		mWebSettings.setDefaultTextEncodingName("UTF-8");
		mWebSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
		mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		content.setHapticFeedbackEnabled(false);
		// 暂时不能确定是否存在问提
		if (isAdded()) {
			content.setWebViewClient(new WebViewClient() {

				@Override
				public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
					ToastUtil.shortToast(context, getString(R.string.network_error));
				}
			});
			content.loadUrl(url);
		}
	}

	@OnClick({ R.id.faq_canceled })
	@Override
	public void onClick(View v) {
		getActivity().finish();
	}

}
