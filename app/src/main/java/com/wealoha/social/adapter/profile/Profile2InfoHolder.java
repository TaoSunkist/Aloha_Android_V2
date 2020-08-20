package com.wealoha.social.adapter.profile;

import javax.inject.Inject;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import com.squareup.picasso.Picasso;
import com.wealoha.social.BaseFragAct;
import com.wealoha.social.R;
import com.wealoha.social.activity.ConfigDetailsAct;
import com.wealoha.social.api.user.bean.User2;
import com.wealoha.social.fragment.Profile2Fragment;
import com.wealoha.social.inject.Injector;
import com.wealoha.social.utils.ContextUtil;
import com.wealoha.social.utils.FontUtil;
import com.wealoha.social.utils.FontUtil.Font;
import com.wealoha.social.utils.RegionNodeUtil;
import com.wealoha.social.utils.StringUtil;

public class Profile2InfoHolder {

	@Inject
	Context context;
	@Inject
	Picasso picasso;
	@Inject
	RegionNodeUtil regionNodeUtil;
	@Inject
	ContextUtil contextUtil;
	@Inject
	FontUtil fontUtil;

	@InjectView(R.id.ms_find_tv)
	TextView mFind;
	@InjectView(R.id.ms_brith_tv)
	TextView mBrith;
	@InjectView(R.id.ms_find_woman_tv)
	TextView mFindWoman;
	@InjectView(R.id.ms_word_tv)
	TextView mWord;
	@InjectView(R.id.ms_setup_tv)
	TextView mSetupTv;
	@InjectView(R.id.ms_report_iv)
	ImageView mReport;
	private ViewGroup mContainer;
	private User2 mUser2;

	public Profile2InfoHolder(User2 user2, ViewGroup parent) {
		Injector.inject(this);
		this.mContainer = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.item_profile_info, parent, false);
		ButterKnife.inject(this, mContainer);
		// 保持数据是最新的
		this.mUser2 = user2;

		// 字体
		fontUtil.changeFonts(mContainer, Font.ENCODESANSCOMPRESSED_400_REGULAR);
		initData();
	}

	public void initData() {
		// mUsername.setText("" + mUser.getName());
		mWord.setText(mUser2.getSummary());
		// List<String> regionNames = regionNodeUtil.getRegionNames(mUser.getRegionCode(), 2);
		// // FIXME 地區
		// Collections.reverse(regionNames);
		// if (regionNames.size() > 2) {
		// regionNames.remove(0);
		// }
		// mFind.setText(StringUtil.join(", ", regionNames));

		if (mUser2.getRegion() == null || mUser2.getRegion().size() == 0) {
			mFind.setText(R.string.location_invisible);
		} else {
			String region = "";
			for (String r : mUser2.getRegion()) {
				region += r + ", ";
			}
			mFind.setText(region.substring(0, region.length() - 2));
		}
		String brith = mUser2.getAge() + " · " + mUser2.getHeight() + " · " + mUser2.getWeight();
		String userTag = contextUtil.getUserTag(mUser2.getSelfTag());
		if (userTag != null) {
			brith += " · " + userTag;
		}
		mBrith.setText(brith);
		// 寻找
		String purposes = contextUtil.formatPurposes(mUser2.getSelfPurposes());
		if (StringUtil.isNotEmpty(purposes)) {
			mFindWoman.setVisibility(View.VISIBLE);
			mFindWoman.setText(context.getResources().getString(R.string.seek_for, purposes));
		} else {
			mFindWoman.setVisibility(View.GONE);
		}

		if (mUser2.isMe()) {
			mReport.setVisibility(View.VISIBLE);
			mSetupTv.setVisibility(View.VISIBLE);
		} else {
			mReport.setVisibility(View.GONE);
			mSetupTv.setVisibility(View.GONE);
		}

	}

	public ViewGroup getView() {
		return mContainer;
	}

	@OnClick({ R.id.ms_report_iv, R.id.ms_setup_tv })
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ms_setup_tv:
		case R.id.ms_report_iv:
			Intent intent = new Intent(contextUtil.getForegroundAct(), ConfigDetailsAct.class);
			((BaseFragAct) contextUtil.getForegroundAct()).startActivityForResult(intent, Profile2Fragment.PROFILE_REFRESH_ICON);
			// ((BaseFragAct)
			// contextUtil.getForegroundAct()).startActivity(GlobalConstants.IntentAction.INTENT_URI_CONFIG_DETAILS);
			break;
		}
	}
}
