package com.wealoha.social.fragment;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;

import com.wealoha.social.AsyncLoader;
import com.wealoha.social.R;
import com.wealoha.social.api.ServerApi;
import com.wealoha.social.beans.Result;
import com.wealoha.social.beans.ResultData;
import com.wealoha.social.beans.PushSettingResult;
import com.wealoha.social.utils.FontUtil.Font;
import com.wealoha.social.utils.ToastUtil;

public class PreferSettingNotificationFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Result<PushSettingResult>>, OnClickListener, OnTouchListener {

	@Inject
	ServerApi settingService;

	@InjectView(R.id.setting_notification_back_tv)
	ImageView back_tv;

	/** 贊的checkbox */
	@InjectView(R.id.like_close)
	CheckBox mLikeClose;
	@InjectView(R.id.like_everyone)
	CheckBox mLikeEveryone;
	@InjectView(R.id.like_matcher)
	CheckBox mLikeMatcher;
	@InjectView(R.id.like_close_rl)
	RelativeLayout mLikeCloseRl;
	@InjectView(R.id.like_everyone_rl)
	RelativeLayout mLikeEveryoneRl;
	@InjectView(R.id.like_matcher_rl)
	RelativeLayout mLikeMatcherRl;
	/** 留言的checkbox */
	@InjectView(R.id.comment_close)
	CheckBox mCommentClose;
	@InjectView(R.id.comment_everyone)
	CheckBox mCommentEveryone;
	@InjectView(R.id.comment_matcher)
	CheckBox mCommentMatcher;
	@InjectView(R.id.comment_close_rl)
	RelativeLayout mCommentCloseRl;
	@InjectView(R.id.comment_everyone_rl)
	RelativeLayout mCommentEveryoneRl;
	@InjectView(R.id.comment_matcher_rl)
	RelativeLayout mCommentMatcherRl;
	/** aloha的checkbox */
	@InjectView(R.id.aloha_close)
	CheckBox mAlohaClose;
	@InjectView(R.id.aloha_everyone)
	CheckBox mAlohaEveryone;
	@InjectView(R.id.aloha_close_rl)
	RelativeLayout mAlohaCloseRl;
	@InjectView(R.id.aloha_everyone_rl)
	RelativeLayout mAlohaEveryoneRl;
	@InjectView(R.id.menu_bar)
	RelativeLayout mMenuBar;
	@InjectView(R.id.items_container)
	LinearLayout mItemContainer;
	/** tag的checkbox */
	@InjectView(R.id.tag_close)
	CheckBox mTagClose;
	@InjectView(R.id.tag_everyone)
	CheckBox mTagEveryone;
	@InjectView(R.id.tag_matcher)
	CheckBox mTagMatcher;
	@InjectView(R.id.tag_close_rl)
	RelativeLayout mTagCloseRl;
	@InjectView(R.id.tag_everyone_rl)
	RelativeLayout mTagEveryoneRl;
	@InjectView(R.id.tag_matcher_rl)
	RelativeLayout mTagMatcherRl;

	@InjectView(R.id.aloha_noty_tv)
	TextView mAlohaNotyTv;
	@InjectView(R.id.praise_noty_tv)
	TextView mPraiseNotyTv;
	@InjectView(R.id.comment_noty_tv)
	TextView mCommentNotyTv;
	@InjectView(R.id.tag_noty_tv)
	TextView mTagNotyTv;
	@Inject
	Context context;

	private static final int REQUEST_CODE_LOAD_SETTING = 0;

	Boolean pushSound;
	Boolean pushVibration;
	Boolean pushShowDetail; // 接口里是show，界面是notShow，注意哟

	private static final String ALL = "All";
	private static final String NO = "No";
	private static final String MATCH = "Match";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_prefer_setting_notification, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mLikeClose.setOnTouchListener(this);
		mLikeEveryone.setOnTouchListener(this);
		mLikeMatcher.setOnTouchListener(this);
		mCommentClose.setOnTouchListener(this);
		mCommentEveryone.setOnTouchListener(this);
		mCommentMatcher.setOnTouchListener(this);
		mTagClose.setOnTouchListener(this);
		mTagEveryone.setOnTouchListener(this);
		mTagMatcher.setOnTouchListener(this);
		mAlohaClose.setOnTouchListener(this);
		mAlohaEveryone.setOnTouchListener(this);

		// 加载配置
		getLoaderManager().restartLoader(REQUEST_CODE_LOAD_SETTING, null, PreferSettingNotificationFragment.this);

	}

	@Override
	protected void initTypeFace() {
		fontUtil.changeFonts(mMenuBar, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
		fontUtil.changeFonts(mItemContainer, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
		fontUtil.changeViewFont(mAlohaNotyTv, Font.ENCODESANSCOMPRESSED_400_REGULAR);
		fontUtil.changeViewFont(mCommentNotyTv, Font.ENCODESANSCOMPRESSED_400_REGULAR);
		fontUtil.changeViewFont(mPraiseNotyTv, Font.ENCODESANSCOMPRESSED_400_REGULAR);
		fontUtil.changeViewFont(mTagNotyTv, Font.ENCODESANSCOMPRESSED_400_REGULAR);

	}

	@Override
	public Loader<Result<PushSettingResult>> onCreateLoader(int i, Bundle bundle) {
		if (i == REQUEST_CODE_LOAD_SETTING) {
			return new AsyncLoader<Result<PushSettingResult>>(context) {

				@Override
				public Result<PushSettingResult> loadInBackground() {
					return settingService.getPushSetting();
				}
			};
		}
		return null;
	}

	private void initCheckBox(PushSettingResult r) {
		if (!TextUtils.isEmpty(r.pushPostLike) && mLikeClose != null && mLikeEveryone != null && mLikeMatcher != null) {
			String likeSetting = r.pushPostLike;
			if (ALL.equals(likeSetting)) {
				mLikeClose.setChecked(false);
				mLikeEveryone.setChecked(true);
				mLikeMatcher.setChecked(false);
			} else if (NO.equals(likeSetting)) {
				mLikeClose.setChecked(true);
				mLikeEveryone.setChecked(false);
				mLikeMatcher.setChecked(false);
			} else if (MATCH.equals(likeSetting)) {
				mLikeClose.setChecked(false);
				mLikeEveryone.setChecked(false);
				mLikeMatcher.setChecked(true);
			}

		}

		if (!TextUtils.isEmpty(r.pushPostComment)) {
			String commentSetting = r.pushPostComment;
			if (ALL.equals(commentSetting)) {
				mCommentClose.setChecked(false);
				mCommentEveryone.setChecked(true);
				mCommentMatcher.setChecked(false);
			} else if (NO.equals(commentSetting)) {
				mCommentClose.setChecked(true);
				mCommentEveryone.setChecked(false);
				mCommentMatcher.setChecked(false);
			} else if (MATCH.equals(commentSetting)) {
				mCommentClose.setChecked(false);
				mCommentEveryone.setChecked(false);
				mCommentMatcher.setChecked(true);
			}
		}
		if (!TextUtils.isEmpty(r.pushPostTag)) {
			String tagSetting = r.pushPostTag;
			if (ALL.equals(tagSetting)) {
				mTagClose.setChecked(false);
				mTagEveryone.setChecked(true);
				mTagMatcher.setChecked(false);
			} else if (NO.equals(tagSetting)) {
				mTagClose.setChecked(true);
				mTagEveryone.setChecked(false);
				mTagMatcher.setChecked(false);
			} else if (MATCH.equals(tagSetting)) {
				mTagClose.setChecked(false);
				mTagEveryone.setChecked(false);
				mTagMatcher.setChecked(true);
			}
		}

		if (!TextUtils.isEmpty(r.pushAloha)) {
			if (ALL.equals(r.pushAloha)) {
				mAlohaClose.setChecked(false);
				mAlohaEveryone.setChecked(true);
			} else if (NO.equals(r.pushAloha)) {
				mAlohaClose.setChecked(true);
				mAlohaEveryone.setChecked(false);
			}

		}

	}

	@OnClick({ R.id.tag_close_rl, R.id.tag_everyone_rl, R.id.tag_matcher_rl, R.id.aloha_close_rl, R.id.aloha_everyone_rl, R.id.setting_notification_back_tv, R.id.like_close_rl, R.id.like_everyone_rl, R.id.like_matcher_rl, R.id.comment_close_rl, R.id.comment_everyone_rl, R.id.comment_matcher_rl })
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.setting_notification_back_tv:
			getActivity().finish();
			break;
		case R.id.like_close_rl:
			if (!mLikeClose.isChecked()) {
				mLikeClose.setChecked(true);
				mLikeEveryone.setChecked(false);
				mLikeMatcher.setChecked(false);

				savePraiseAndCommentSetting(NO, null, null);
			}
			break;
		case R.id.like_everyone_rl:
			if (!mLikeEveryone.isChecked()) {
				mLikeEveryone.setChecked(true);
				mLikeClose.setChecked(false);
				mLikeMatcher.setChecked(false);
				savePraiseAndCommentSetting(ALL, null, null);
			}
			break;
		case R.id.like_matcher_rl:
			if (!mLikeMatcher.isChecked()) {
				mLikeMatcher.setChecked(true);
				mLikeEveryone.setChecked(false);
				mLikeClose.setChecked(false);
				savePraiseAndCommentSetting(MATCH, null, null);
			}
			break;
		case R.id.comment_close_rl:
			if (!mCommentClose.isChecked()) {
				mCommentClose.setChecked(true);
				mCommentEveryone.setChecked(false);
				mCommentMatcher.setChecked(false);

				savePraiseAndCommentSetting(null, NO, null);
			}
			break;
		case R.id.comment_everyone_rl:
			if (!mCommentEveryone.isChecked()) {
				mCommentEveryone.setChecked(true);
				mCommentClose.setChecked(false);
				mCommentMatcher.setChecked(false);

				savePraiseAndCommentSetting(null, ALL, null);
			}
			break;
		case R.id.comment_matcher_rl:
			if (!mCommentMatcher.isChecked()) {
				mCommentMatcher.setChecked(true);
				mCommentClose.setChecked(false);
				mCommentEveryone.setChecked(false);

				savePraiseAndCommentSetting(null, MATCH, null);
			}
			break;
		case R.id.tag_close_rl:
			if (!mTagClose.isChecked()) {
				mTagClose.setChecked(true);
				mTagEveryone.setChecked(false);
				mTagMatcher.setChecked(false);

				savePraiseAndCommentSetting(null, null, NO);
			}
			break;
		case R.id.tag_everyone_rl:
			if (!mTagEveryone.isChecked()) {
				mTagEveryone.setChecked(true);
				mTagClose.setChecked(false);
				mTagMatcher.setChecked(false);

				savePraiseAndCommentSetting(null, null, ALL);
			}
			break;
		case R.id.tag_matcher_rl:
			if (!mTagMatcher.isChecked()) {
				mTagMatcher.setChecked(true);
				mTagClose.setChecked(false);
				mTagEveryone.setChecked(false);

				savePraiseAndCommentSetting(null, null, MATCH);
			}
			break;
		case R.id.aloha_everyone_rl:
			if (!mAlohaEveryone.isChecked()) {
				mAlohaEveryone.setChecked(true);
				mAlohaClose.setChecked(false);
				saveAlohaPushSetting(ALL);
				// savePraiseAndCommentSetting(null, ALL);
			}
			break;
		case R.id.aloha_close_rl:
			if (!mAlohaClose.isChecked()) {
				mAlohaClose.setChecked(true);
				mAlohaEveryone.setChecked(false);
				saveAlohaPushSetting(NO);
				// savePraiseAndCommentSetting(null, MATCH);
			}
			break;

		default:
			break;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		CheckBox cb = (CheckBox) v;
		switch (v.getId()) {
		case R.id.like_close:
			if (!cb.isChecked()) {
				cb.setChecked(true);
				mLikeEveryone.setChecked(false);
				mLikeMatcher.setChecked(false);
				// savePraiseAndCommentSetting(NO, null, null);
			}
			break;
		case R.id.like_everyone:
			if (!cb.isChecked()) {
				cb.setChecked(true);
				mLikeClose.setChecked(false);
				mLikeMatcher.setChecked(false);
				// savePraiseAndCommentSetting(ALL, null, null);
			}
			break;
		case R.id.like_matcher:
			if (!cb.isChecked()) {
				cb.setChecked(true);
				mLikeEveryone.setChecked(false);
				mLikeClose.setChecked(false);
				// savePraiseAndCommentSetting(MATCH, null, null);
			}
			break;
		// 留言設置
		case R.id.comment_close:
			if (!cb.isChecked()) {
				cb.setChecked(true);
				mCommentEveryone.setChecked(false);
				mCommentMatcher.setChecked(false);
				// savePraiseAndCommentSetting(null, NO, null);
			}
			break;
		case R.id.comment_everyone:
			if (!cb.isChecked()) {
				cb.setChecked(true);
				mCommentClose.setChecked(false);
				mCommentMatcher.setChecked(false);
				// savePraiseAndCommentSetting(null, ALL, null);
			}
			break;
		case R.id.comment_matcher:
			if (!cb.isChecked()) {
				cb.setChecked(true);
				mCommentEveryone.setChecked(false);
				mCommentClose.setChecked(false);

				// savePraiseAndCommentSetting(null, MATCH, null);
			}
			break;
		case R.id.tag_close:
			if (!cb.isChecked()) {
				cb.setChecked(true);
				mTagEveryone.setChecked(false);
				mTagMatcher.setChecked(false);
				// savePraiseAndCommentSetting(null, null, NO);
			}
			break;
		case R.id.tag_everyone:
			if (!cb.isChecked()) {
				cb.setChecked(true);
				mTagClose.setChecked(false);
				mTagMatcher.setChecked(false);
				// savePraiseAndCommentSetting(null, null, ALL);
			}
			break;
		case R.id.tag_matcher:
			if (!cb.isChecked()) {
				cb.setChecked(true);
				mTagEveryone.setChecked(false);
				mTagClose.setChecked(false);

				// savePraiseAndCommentSetting(null, null, MATCH);
			}
			break;
		// aloha
		case R.id.aloha_everyone:
			if (!cb.isChecked()) {
				cb.setChecked(true);
				mAlohaClose.setChecked(false);
				saveAlohaPushSetting(ALL);
				// savePraiseAndCommentSetting(null, ALL);
			}
			break;
		case R.id.aloha_close:
			if (!cb.isChecked()) {
				cb.setChecked(true);
				mAlohaEveryone.setChecked(false);
				saveAlohaPushSetting(NO);
				// savePraiseAndCommentSetting(null, MATCH);
			}
			break;
		default:
			break;
		}
		v.performClick();
		return true;
	}

	private void savePraiseAndCommentSetting(String like, String comment, String tag) {
		settingService.savePushSetting(null, null, null, like, comment, tag, new Callback<Result<ResultData>>() {

			@Override
			public void success(Result<ResultData> arg0, Response arg1) {
				// ToastUtil.longToast(context, R.string.successfully_saved);
				getLoaderManager().restartLoader(REQUEST_CODE_LOAD_SETTING, null, PreferSettingNotificationFragment.this);
			}

			@Override
			public void failure(RetrofitError arg0) {
				ToastUtil.longToast(context, R.string.is_not_work);
			}
		});
	}

	private void saveAlohaPushSetting(String off) {
		settingService.savePushSetting(off, new Callback<Result<ResultData>>() {

			@Override
			public void success(Result<ResultData> arg0, Response arg1) {
				// ToastUtil.longToast(context, R.string.successfully_saved);
				getLoaderManager().restartLoader(REQUEST_CODE_LOAD_SETTING, null, PreferSettingNotificationFragment.this);
			}

			@Override
			public void failure(RetrofitError arg0) {
				ToastUtil.longToast(context, R.string.is_not_work);
			}
		});
	}

	@Override
	public void onLoadFinished(Loader<Result<PushSettingResult>> resultLoader, Result<PushSettingResult> result) {
		if (result == null || !result.isOk()) {
			return;
		}
		int loader = resultLoader.getId();
		if (loader == REQUEST_CODE_LOAD_SETTING) {
			// 加载完，更新按钮状态
			PushSettingResult r = (PushSettingResult) result.getData();
			initCheckBox(r);
		}

	}

	@Override
	public void onLoaderReset(Loader<Result<PushSettingResult>> loader) {

	}

}
