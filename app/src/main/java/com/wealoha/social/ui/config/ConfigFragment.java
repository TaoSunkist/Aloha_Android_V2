package com.wealoha.social.ui.config;

import java.lang.ref.WeakReference;
import java.util.Map;

import javax.inject.Inject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;

import com.squareup.picasso.Picasso;
import com.wealoha.social.AsyncLoader;
import com.wealoha.social.BaseFragAct;
import com.wealoha.social.ContextConfig;
import com.wealoha.social.R;
import com.wealoha.social.activity.FaqStatmentAct;
import com.wealoha.social.activity.ProFeatureAct;
import com.wealoha.social.api.ServerApi;
import com.wealoha.social.beans.Result;
import com.wealoha.social.beans.User;
import com.wealoha.social.beans.instagram.InstagramResult;
import com.wealoha.social.beans.PromotionGetData;
import com.wealoha.social.commons.GlobalConstants;
import com.wealoha.social.commons.GlobalConstants.ImageSize;
import com.wealoha.social.fragment.BaseFragment;
import com.wealoha.social.fragment.ChangeNumberPart1;
import com.wealoha.social.fragment.PostsListFragment;
import com.wealoha.social.fragment.SettingNotificationFragment;
import com.wealoha.social.fragment.SwipeMenuListFragment;
import com.wealoha.social.presenters.ConfigPresenter;
import com.wealoha.social.services.DownloadService;
import com.wealoha.social.store.UserAgentProvider;
import com.wealoha.social.utils.ChatUtil;
import com.wealoha.social.utils.ContextUtil;
import com.wealoha.social.utils.FileTools;
import com.wealoha.social.utils.FontUtil;
import com.wealoha.social.utils.FontUtil.Font;
import com.wealoha.social.utils.ImageUtil;
import com.wealoha.social.utils.ImageUtil.CropMode;
import com.wealoha.social.utils.NetworkUtil;
import com.wealoha.social.utils.ToastUtil;
import com.wealoha.social.view.custom.CircleImageView;
import com.wealoha.social.view.custom.dialog.ListItemDialog;
import com.wealoha.social.view.custom.dialog.ListItemDialog.ListItemType;

/**
 * Profile->设置
 * 
 * @author javamonk
 * @see
 * @since
 * @date 2014-11-20 下午4:36:34
 */
public class ConfigFragment extends BaseFragment implements IConfigView {

	public static final String TAG = ConfigFragment.class.getSimpleName();
	private View thisView;
	@Inject
	ContextUtil mContextUtil;
	@Inject
	FontUtil fontUtil;
	@Inject
	ServerApi userPromotionService;
	// 资料详细
	@InjectView(R.id.config_detals_rl)
	RelativeLayout mDetals;
	@InjectView(R.id.config_username_tv)
	TextView mUserName;
	// 高级功能
	@InjectView(R.id.config_advanced_rl)
	RelativeLayout mAdvanced;
	// 手机号码
	@InjectView(R.id.config_phone_rl)
	RelativeLayout mPhone;
	// instagram同步
	@InjectView(R.id.config_instagram_rl)
	RelativeLayout mInstagram;
	// 修改密码
	@InjectView(R.id.config_password_rl)
	RelativeLayout mPassword;
	// 新消息通知
	@InjectView(R.id.config_news_rl)
	RelativeLayout mNews;
	@InjectView(R.id.cleancache_iv)
	TextView cleancache_iv;
	// 黑名单
	@InjectView(R.id.config_blacklist_rl)
	RelativeLayout mBlacklist;
	// 清理缓存
	@InjectView(R.id.config_cleancache_rl)
	RelativeLayout mCleanCache;
	// 意见反映
	@InjectView(R.id.config_opinion_rl)
	RelativeLayout mOpinion;
	// 常见问题
	@InjectView(R.id.config_question_rl)
	RelativeLayout mQuestion;
	// 隐私设置
	@InjectView(R.id.private_set_rl)
	RelativeLayout mPrivateSet;
	@InjectView(R.id.config_logout_rl)
	RelativeLayout mLogout;
	@Inject
	UserAgentProvider mUserAgentProvider;
	@InjectView(R.id.config_userphoto_civ)
	CircleImageView mUserPhoto;
	@InjectView(R.id.config_phone_tv)
	TextView mUserPhotoNum;
	@InjectView(R.id.config_back_tv)
	ImageView configBackTv;
	@InjectView(R.id.config_version_tv)
	TextView mVersionTv;
	@InjectView(R.id.config_instagram_tv)
	TextView mInstagramTv;
	@InjectView(R.id.config_insta_sub)
	View mInstagramSub;
	@InjectView(R.id.height_config_sub)
	View mHightSub;
	@InjectView(R.id.config_newversion_rl)
	RelativeLayout mNewVersion;
	@InjectView(R.id.praise_post_list)
	RelativeLayout mPraisedList;
	@InjectView(R.id.tag_post_list)
	RelativeLayout mTagedList;
	@InjectView(R.id.config_advanced_tv)
	TextView mAdvancedTv;
	@Inject
	Picasso picasso;
	@Inject
	ChatUtil chatUtil;
	@Inject
	PackageInfo packageInfo;
	private User mUser;
	private static final int REQUEST_CODE_CODE_LOAD = 0;
	protected static final int CLEAR_CACHE_OVER = 0x00000001;
	private BaseFragAct mBaseFragAct;
	private Dialog updateAlert;

	private ConfigPresenter mConfigP;
	private Bundle instagramBundle;

	// 测试
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		thisView = inflater.inflate(R.layout.frag_config, container, false);
		return thisView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mConfigP = new ConfigPresenter(getActivity(), this);
		fontUtil.changeFonts((ViewGroup) thisView, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
		mBaseFragAct = (BaseFragAct) getActivity();
		mUser = contextUtil.getCurrentUser();
		initView();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (BaseFragAct.isRefreshHeadIcon) {
			mUser = contextUtil.getCurrentUser();
			initView();
		}
		mConfigP.refreshInstagram();
		refreshProFeatureUI();
	}

	public void initView() {
		// 英文版隐藏常见问题选项
		String localLanguage = getResources().getConfiguration().locale.getCountry();
		if ("UK".equals(localLanguage) || "US".equals(localLanguage)) {
			mQuestion.setVisibility(View.GONE);
		}
		mConfigP.clacCacheSize();
		if (mUser != null) {
			picasso.load(ImageUtil.getImageUrl(mUser.getAvatarImage().getId(), ImageSize.AVATAR_ROUND_SMALL, CropMode.ScaleCenterCrop)).placeholder(R.drawable.default_photo).into(mUserPhoto);
			mUserName.setText(mUser.getName());
			mVersionTv.setText(packageInfo.versionName);
		}

		if (contextUtil.isPassportPhoneNumber()) {
			mPhone.setVisibility(View.VISIBLE);
			mPassword.setVisibility(View.VISIBLE);
			mUserPhotoNum.setText(contextUtil.getAccountPhoneNumber());
		} else {
			mPhone.setVisibility(View.GONE);
			mPassword.setVisibility(View.GONE);
		}

		if (!ContextConfig.getInstance().getBooleanWithFilename("instagram_dot")) {
			mInstagramSub.setVisibility(View.VISIBLE);
		} else {
			mInstagramSub.setVisibility(View.GONE);
		}
		refreshProFeatureUI();
		mHightSub.setVisibility(ContextConfig.getInstance().getBooleanWithFilename("advanced_dot") == true ? View.GONE : View.VISIBLE);
	}

	private void refreshProFeatureUI() {
		// 高级功能是否开通
		if (contextUtil.getProfeatureEnable()) {
			mAdvancedTv.setText(R.string.have_enabled);
		} else {
			mAdvancedTv.setText(R.string.have_disabled);
		}
	}

	@OnClick({ //
	R.id.config_cleancache_rl,//
	R.id.config_newversion_rl,//
	R.id.config_instagram_rl, //
	R.id.config_blacklist_rl,//
	R.id.config_detals_rl,//
	R.id.config_password_rl,//
	R.id.config_opinion_rl, //
	R.id.config_advanced_rl,//
	R.id.config_news_rl,//
	R.id.config_logout_rl,//
	R.id.config_question_rl, //
	R.id.config_phone_rl,//
	R.id.tag_post_list,//
	R.id.praise_post_list,//
	R.id.config_back_tv,//
//	R.id.config_lock_rl, //
	R.id.private_set_rl })
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.config_back_tv:
			backCurrentUI();
			break;
		// 资料设定
		case R.id.config_detals_rl:
			startActivityForResult(GlobalConstants.IntentAction.INTENT_URI_CONFIG_DETAILS, //
									GlobalConstants.AppConstact.IS_REFRESH_PROFILE_HEAD_ICON, //
									null, R.anim.left_in, R.anim.stop);
			break;
		// 赞过的照片
		case R.id.tag_post_list:
			Bundle bundle = new Bundle();
			bundle.putInt(PostsListFragment.TYPED_KEY, PostsListFragment.TAGED);
			((BaseFragAct) getActivity()).startFragment(PostsListFragment.class, //
														bundle, //
														true,//
														R.anim.left_in, R.anim.stop);
			break;
		// 圈过的照片
		case R.id.praise_post_list:
			Bundle bundle2 = new Bundle();
			bundle2.putInt(PostsListFragment.TYPED_KEY, PostsListFragment.PRAISED);
			((BaseFragAct) getActivity()).startFragment(PostsListFragment.class,//
														bundle2,//
														true,//
														R.anim.left_in, R.anim.stop);
			break;
		// 高级功能
		case R.id.config_advanced_rl:
			ContextConfig.getInstance().putBooleanWithFilename("advanced_dot", true);
			mHightSub.setVisibility(View.GONE);
			if (isAdded()) {
//				popup.show();
				getLoaderManager().initLoader(REQUEST_CODE_CODE_LOAD, null, this);
			}
			break;
		// 电话号码
		case R.id.config_phone_rl:
			changeNumberDialog();
			break;
		// 密码
		case R.id.config_password_rl:
			startActivity(GlobalConstants.IntentAction.INTENT_URI_CONFIG_PASSWORD);
			break;
		// 手势锁
//		case R.id.config_lock_rl:
//			((BaseFragAct) getActivity()).startFragment(Feed3Fragment.class, null, true);
//			break;
		// 消息通知
		case R.id.config_news_rl:
			if (mContextUtil.getForegroundAct() != null) {
				((BaseFragAct) mContextUtil.getForegroundAct()).startFragment(SettingNotificationFragment.class, null, true);
			}
			break;
		// 黑名单
		case R.id.config_blacklist_rl:
			bundle = new Bundle();
			SwipeMenuListFragment userList = new SwipeMenuListFragment();
			bundle.putInt("listtype", SwipeMenuListFragment.LISTTYPE_BLACK);
			userList.setArguments(bundle);
			if (mContextUtil.getForegroundAct() != null) {
				((BaseFragAct) mContextUtil.getForegroundAct()).startFragment(SwipeMenuListFragment.class, bundle, true);
			}
			break;
		// 清理缓存
		case R.id.config_cleancache_rl:
			cleancache_iv.setText(R.string.cleaning);
			mConfigP.clearCache();
			break;
		// 意见反映
		case R.id.config_opinion_rl:
			chatUtil.bySessionIdToDialogue("YUZzqAXuczM");
			break;
		// 常见问题
		case R.id.config_question_rl:
			if (mContextUtil.getForegroundAct() != null) {
				((BaseFragAct) mContextUtil.getForegroundAct()).startFragment(FaqStatmentAct.class, null, true);
			}
			break;
		case R.id.config_logout_rl:
			if (NetworkUtil.isNetworkAvailable()) {
				new ListItemDialog(mBaseFragAct, (ViewGroup) thisView).//
				showListItemPopup(this, getString(R.string.logout_confirm), ListItemType.LOGOUT);

			} else {
				ToastUtil.shortToast(getActivity(), R.string.please_check_your_network);
			}
			break;
		case R.id.config_instagram_rl:
			ContextConfig.getInstance().putBooleanWithFilename("instagram_dot", true);
			mInstagramSub.setVisibility(View.GONE);
			if (instagramBundle == null || !instagramBundle.getBoolean("bind", false)) {
				((BaseFragAct) getActivity()).startActivity(GlobalConstants.IntentAction.INTENT_URI_CONFIG_INSTAGRAM);
			} else {
				// instagramBundle.get("name");
				((BaseFragAct) getActivity()).startActivity(GlobalConstants.IntentAction.INTENT_URI_CONFIG_HAVE_INSTAGRAM, instagramBundle);
			}

			break;
		case R.id.config_newversion_rl:
			mConfigP.checkNewVersion();
			break;
		case R.id.private_set_rl:
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(GlobalConstants.IntentAction.INTENT_URI_PRIVACY);
			getActivity().startActivity(intent);
			getActivity().overridePendingTransition(R.anim.left_in, R.anim.stop);
			break;
		default:
			break;
		}
	}

	/***
	 * 修改电话号码dialog
	 * 
	 * @param updateDetails
	 * @return void
	 */
	private void changeNumberDialog() {
		View view = LayoutInflater.from(context).inflate(R.layout.dialog_first_aloha_time, new LinearLayout(context), false);
		TextView title = (TextView) view.findViewById(R.id.first_aloha_title);
		TextView message = (TextView) view.findViewById(R.id.first_aloha_message);
		message.setVisibility(View.GONE);
		title.setText(R.string.confirm_change_phone_num);
		TextView closeLeft = (TextView) view.findViewById(R.id.close_tv);
		closeLeft.setText(R.string.cancel);
		closeLeft.setVisibility(View.VISIBLE);
		TextView closeRight = (TextView) view.findViewById(R.id.close_tv_02);
		closeRight.setText(R.string.confirm);
		closeRight.setVisibility(View.VISIBLE);
		FontUtil.setSemiBoldTypeFace(getActivity(), title);
		FontUtil.setRegulartypeFace(getActivity(), closeRight);
		FontUtil.setRegulartypeFace(getActivity(), closeLeft);
		closeLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (updateAlert.isShowing()) {
					updateAlert.dismiss();
				}
			}
		});
		closeRight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (updateAlert.isShowing()) {
					updateAlert.dismiss();
				}
				((BaseFragAct) getActivity()).startFragmentForResult(//
				GlobalConstants.IntentAction.INTENT_URI_ALERTPHONENUMACT, ChangeNumberPart1.class, //
																		null, true, ChangeNumberPart1.CHANGE_NUM_REQUEST_CODE, R.anim.left_in, R.anim.stop);
			}
		});
		updateAlert = new AlertDialog.Builder(getActivity())//
		.setView(view).create();
		updateAlert.show();
	}

	/***
	 * 开启升级提示dialog
	 * 
	 * @param updateDetails
	 * @return void
	 */
	@Override
	public void updateDialog(String updateDetails) {
		View view = LayoutInflater.from(context).inflate(R.layout.dialog_update, new LinearLayout(context), false);
		TextView titleTv = (TextView) view.findViewById(R.id.update_title);
		titleTv.setText(R.string.update);
		TextView messageTv = (TextView) view.findViewById(R.id.update_message);
		messageTv.setText(updateDetails);
		updateAlert = new AlertDialog.Builder(getActivity())//
		.setView(view)//
		.setPositiveButton(R.string.confirm_updata_version, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				startUpdateApp();
				if (updateAlert != null && updateAlert.isShowing()) {
					updateAlert.dismiss();
				}
			}
		}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (updateAlert != null && updateAlert.isShowing()) {
					updateAlert.dismiss();
				}
			}
		}).create();
		updateAlert.show();
	}

	private void backCurrentUI() {
		getActivity().finish();
	}

	@Override
	public Loader<Result<PromotionGetData>> onCreateLoader(int i, Bundle bundle) {

		if (i == REQUEST_CODE_CODE_LOAD) {
			return new AsyncLoader<Result<PromotionGetData>>(getActivity()) {

				@Override
				public Result<PromotionGetData> loadInBackground() {
					try {
						return userPromotionService.get();
					} catch (Exception e) {
						e.printStackTrace();
					}
					return null;
				}
			};
		}
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Result<PromotionGetData>> resultLoader, Result<PromotionGetData> result) {
		if (result == null) {
			return;
		}
		int loaderId = resultLoader.getId();
		if (loaderId == REQUEST_CODE_CODE_LOAD) {
			PromotionGetData r = (PromotionGetData) result.getData();
			if (result.isOk()) {
				contextUtil.setProfeatureEnable(!r.alohaGetLocked);
				Intent intent = new Intent(getActivity(), ProFeatureAct.class);
				intent.putExtra(PromotionGetData.TAG, r);
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.left_in, R.anim.stop);
			} else {
				ToastUtil.shortToast(mBaseFragAct, getString(R.string.network_error));
			}
		}
	}

	@Override
	public void onLoaderReset(Loader<Result<PromotionGetData>> loader) {
	}

	public void clearCache() {
		try {
			cleancache_iv.setText(FileTools.FormetFileSize(FileTools.byFileGetFileSize(FileTools.ROOT_PATH)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onActivityResultCallback(int requestcode, int resultcode, Intent result) {
		if (resultcode != Activity.RESULT_OK) {
			return;
		}
		switch (requestcode) {
		case GlobalConstants.AppConstact.IS_REFRESH_PROFILE_HEAD_ICON:
			getActivity().setResult(Activity.RESULT_OK);
			break;
		default:
			break;
		}
	}

	private Handler mHandler = new ConfigFragHandler(this);

	private static class ConfigFragHandler extends Handler {

		private WeakReference<ConfigFragment> mFrag;

		public ConfigFragHandler(ConfigFragment frag) {
			mFrag = new WeakReference<ConfigFragment>(frag);
		}

		@Override
		public void handleMessage(Message msg) {
			ConfigFragment frag = mFrag.get();
			if (frag != null) {
				switch (msg.what) {
				case CLEAR_CACHE_OVER:
					frag.clearCache();
					break;
				}
			}
		}
	}

	@Override
	public void startUpdateApp() {
		Intent intent = new Intent(getActivity(), DownloadService.class);
		getActivity().startService(intent);
		DownloadService.downNewFile(getActivity(), "http://wealoha.com/get/android", 351, "Aloha");
	}

	/**
	 * 登出
	 */
	@Override
	public void itemCallback(int listItemType) {
		((BaseFragAct) getActivity()).doLogout(context);
	}

	@Override
	public void clearCacheSuccess() {
		if (ConfigFragment.this.isAdded())
			mHandler.sendEmptyMessage(CLEAR_CACHE_OVER);
	}

	@Override
	public void setUiShowCacheSize(final String cacheSize) {
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				if (cleancache_iv != null) {
					cleancache_iv.setText(cacheSize);
				}
			}
		});
	}

	@Override
	public void refreshInstagramSuccess(Result<InstagramResult> result) {
		if (result != null) {
			if (result.isOk() && isAdded()) {
				Map<String, Object> instagram = (Map<String, Object>) result.getData().instagram;
				// bundle.
				if (instagram == null && mInstagramTv != null) {
					instagramBundle = null;
					// 可能是Fragmentdestroy后,变量被回收了.
					mInstagramTv.setText("");
					return;
				}
				if (instagramBundle == null) {
					instagramBundle = new Bundle();
				}
				if (instagram.get("bind") != null) {
					instagramBundle.putBoolean("bind", (Boolean) instagram.get("bind"));
				}
				if (instagram.get("name") != null) {
					instagramBundle.putString("name", instagram.get("name").toString());
				}
				if (instagram.get("autoSync") != null) {
					instagramBundle.putBoolean("autoSync", (Boolean) instagram.get("autoSync"));
				}
				if (instagram.get("name") != null && mInstagramTv != null) {
					mInstagramTv.setText(String.valueOf(instagram.get("name")));
				}
			}
		}
		popup.hide();
	}
}
