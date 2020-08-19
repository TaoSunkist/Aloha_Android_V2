package com.wealoha.social.view.custom.listitem;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import com.squareup.picasso.Picasso;
import com.wealoha.social.AppApplication;
import com.wealoha.social.BaseFragAct;
import com.wealoha.social.R;
import com.wealoha.social.beans.User;
import com.wealoha.social.commons.GlobalConstants;
import com.wealoha.social.inject.Injector;
import com.wealoha.social.utils.ContextUtil;
import com.wealoha.social.utils.FontUtil;
import com.wealoha.social.utils.FontUtil.Font;
import com.wealoha.social.utils.RegionNodeUtil;
import com.wealoha.social.utils.StringUtil;
import com.wealoha.social.utils.ToastUtil;

public class ProfileInfoHolder {

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
	private User mUser;
	private boolean mIsMe;

	public ProfileInfoHolder(User user, boolean isMe, ViewGroup parent) {
		Injector.inject(this);
		this.mContainer = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.item_profile_info, parent, false);
		ToastUtil.shortToast(AppApplication.getInstance(), "AppApplication");
		ButterKnife.inject(this, mContainer);
		mIsMe = isMe;

		// 保持数据是最新的
		if (mIsMe) {
			this.mUser = contextUtil.getCurrentUser();
		} else {
			this.mUser = user;
		}

		// 字体
		fontUtil.changeFonts(mContainer, Font.ENCODESANSCOMPRESSED_400_REGULAR);
		initData();
	}

	public void initData() {
		// mUsername.setText("" + mUser.getName());
		mWord.setText(mUser.getSummary());
		List<String> regionNames = regionNodeUtil.getRegionNames(mUser.getRegionCode(), 2);
		// FIXME 地區
		Collections.reverse(regionNames);
		if (regionNames.size() > 2) {
			regionNames.remove(0);
		}
		mFind.setText(StringUtil.join(", ", regionNames));
		String brith = mUser.getAge() + " · " + mUser.getHeight() + " · " + mUser.getWeight();
		String userTag = contextUtil.getUserTag(mUser.getSelfTag());
		if (userTag != null) {
			brith += " · " + userTag;
		}
		mBrith.setText(brith);
		// 寻找
		String purposes = contextUtil.formatPurposes(mUser.getSelfPurposes());
		if (StringUtil.isNotEmpty(purposes)) {
			mFindWoman.setVisibility(View.VISIBLE);
			mFindWoman.setText(context.getResources().getString(R.string.seek_for, purposes));
		} else {
			mFindWoman.setVisibility(View.GONE);
		}

		if (mIsMe) {
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
			((BaseFragAct) contextUtil.getForegroundAct()).startActivity(GlobalConstants.IntentAction.INTENT_URI_CONFIG_DETAILS);
			break;
		}
	}
}
