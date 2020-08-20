package com.wealoha.social.activity;

import java.util.ArrayList;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.OnClick;

import com.wealoha.social.BaseFragAct;
import com.wealoha.social.R;
import com.wealoha.social.beans.Profeature;
import com.wealoha.social.beans.PromotionGetData;
import com.wealoha.social.utils.FontUtil;
import com.wealoha.social.view.custom.ShareDialogFragment;

public class ProFeatureAct extends BaseFragAct implements OnClickListener {

	// @InjectView(R.id.list_view)
	// ListView listview;
	private PromotionGetData mPromotionGetData;

	private LinearLayout mCardViewRoot;
	private ArrayList<Profeature> features;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.frag_pro_feature);
		mCardViewRoot = (LinearLayout) findViewById(R.id.cardview_root);
		mPromotionGetData = (PromotionGetData) getIntent().getSerializableExtra(PromotionGetData.TAG);
		features = new ArrayList<Profeature>();

		initFeatureListData(mPromotionGetData);
		initView(mPromotionGetData);
		initProFeatureLayout(features);
		// initProFeatureLayout(listview);
		// profeatureAdt = new ProfeatureAdapter(initFeatureListData(), this);
		// listview.setAdapter(profeatureAdt);
	}

	/**
	 * 初始化列表ui意外的ui
	 * 
	 * @return void
	 */
	private void initView(PromotionGetData data) {
		TextView ruleTv = (TextView) findViewById(R.id.rule_of_profeature);
		ruleTv.setText(getString(R.string.invite_a_friend_to_get_the_invitation_code, data.quotaPerPerson));
		FontUtil.setSemiBoldTypeFace(mContext, findViewById(R.id.title_tv));// 标题字体
	}

	/**
	 * 初始化高级界面布局
	 * 
	 * @param listview
	 * @return void
	 */
	private void initProFeatureLayout(ArrayList<Profeature> features) {
		int layoutId = R.layout.frag_pro_feature_item;
		View rootView;
		for (int i = 0; i < features.size(); i++) {
			Profeature pf = features.get(i);
			rootView = getLayoutInflater().inflate(layoutId, mCardViewRoot, false);
			setViewText(rootView, R.id.profeature_title_tv, pf.getTitle());
			setViewText(rootView, R.id.profeature_item_title_tv, pf.getExplain());
			setViewText(rootView, R.id.profeature_item_content_tv, pf.getState());
			setViewIcon(rootView, R.id.profeature_icon_iv, pf.getHeaderResId());

			FontUtil.setSemiBoldTypeFace(mContext, rootView.findViewById(R.id.profeature_title_tv));
			FontUtil.setSemiBoldTypeFace(mContext, rootView.findViewById(R.id.profeature_item_title_tv));
			FontUtil.setRegulartypeFace(mContext, rootView.findViewById(R.id.profeature_item_content_tv));
			// 隐藏最后一个item的底线
			if (i == features.size() - 1) {
				rootView.findViewById(R.id.line).setVisibility(View.GONE);
			}

			// 长短线。。。。
			if (i == 1) {
				pf = features.get(2);
				if (!TextUtils.isEmpty(pf.getTitle())) {
					rootView.findViewById(R.id.line).setVisibility(View.GONE);
					rootView.findViewById(R.id.line_long).setVisibility(View.VISIBLE);
				} else {
					rootView.findViewById(R.id.line).setVisibility(View.VISIBLE);
					rootView.findViewById(R.id.line_long).setVisibility(View.GONE);
				}
			}
			mCardViewRoot.addView(rootView);
		}

		Button btn = (Button) getLayoutInflater().inflate(R.layout.frag_pro_feature_btn, mCardViewRoot, false);
		FontUtil.setSemiBoldTypeFace(mContext, btn);
		if (mPromotionGetData.alohaGetLocked) {
			if (mPromotionGetData.quotaReset > 0) {
				btn.setText(R.string.immediately_open_all_functions);
			} else {
				btn.setText(R.string.enable_rightnow);
			}
		} else {
			btn.setText(R.string.continue_to_invite);
		}

		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog();
			}
		});
		mCardViewRoot.addView(btn);
	}

	private void setViewText(View view, int viewId, String string) {
		TextView tv = (TextView) view.findViewById(viewId);
		if (!TextUtils.isEmpty(string)) {
			tv.setText(Html.fromHtml(string));
		} else {
			tv.setVisibility(View.GONE);
		}
	}

	private void setViewIcon(View view, int viewId, int iconId) {
		ImageView iv = (ImageView) view.findViewById(viewId);
		if (iconId != 0) {
			iv.setImageResource(iconId);
		} else {
			iv.setVisibility(View.GONE);
		}
	}

	/**
	 * 配置高级功能的内容
	 * 
	 * @param proGetData
	 * @return void
	 */
	private void initFeatureListData(PromotionGetData proGetData) {
		int titleId;
		if (proGetData.alohaGetLocked) {
			titleId = R.string.invite_someone_deblocking_profeature_str;
		} else {
			titleId = R.string.have_enabled;
		}

		addProfeature(titleId,//
						R.string.filter_location,//
						R.string.filter_location_rules,//
						R.drawable.pro_location);
		addProfeature(0,//
						R.string.filter_age,//
						R.string.filter_age_rules,//
						R.drawable.pro_age);

		String title = null;
		if (proGetData.quotaReset != 0) {
			title = getString(R.string.label_invite_quota_reset_str, proGetData.quotaReset);
		}
		addProfeature(title,//
						getString(R.string.match_rightnow),//
						getString(R.string.match_rightnow_rules),//
						R.drawable.pro_skip);
	}

	private void addProfeature(int titleId, int itemTitleId, int itemContentId, int headerResId) {
		features.add(new Profeature(this, titleId, itemTitleId, itemContentId, headerResId));
	}

	private void addProfeature(String title, String itemTitle, String itemContent, int headerResId) {
		features.add(new Profeature(title, itemTitle, itemContent, headerResId));
	}

	@OnClick({ R.id.pro_feature_back_tv })
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.deblock_btn:
			showDialog();
			break;
		case R.id.pro_feature_back_tv:
			overridePendingTransition(R.anim.right_out, R.anim.stop);
			finish();
			break;
		default:
			break;
		}
	}

	ShareDialogFragment shareDialog;

	private void showDialog() {
		FragmentManager fm = getFragmentManager();
		shareDialog = new ShareDialogFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable(PromotionGetData.TAG, mPromotionGetData);
		shareDialog.setArguments(bundle);
		FragmentTransaction ft = fm.beginTransaction();
		// ft.setTransition(transit)
		// ft.setCustomAnimations(R.animator.slide_fragment_horizontal_right_in,
		// R.animator.slide_out,
		// R.animator.slide_fragment_horizontal_right_in, R.animator.slide_out);
		ft.add(android.R.id.content, shareDialog).addToBackStack(null).commit();
	}

	@Override
	public void onBackPressed() {
		// super.onBackPressed();
		if (shareDialog != null && shareDialog.isVisible()) {
			shareDialog.closeFragment();
			// getFragmentManager().popBackStack();
		} else {
			super.onBackPressed();
		}
	}

}
