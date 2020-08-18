package com.wealoha.social.fragment;

import javax.inject.Inject;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import butterknife.ButterKnife;

import com.squareup.otto.Bus;
import com.wealoha.social.BaseFragAct;
import com.wealoha.social.R;
import com.wealoha.social.activity.FragmentWrapperActivity;
import com.wealoha.social.activity.FragmentWrapperActivity.ActivityResultCallback;
import com.wealoha.social.activity.MainAct;
import com.wealoha.social.api.topic.bean.HashTag;
import com.wealoha.social.beans.remotelog.ClientLogService;
import com.wealoha.social.commons.CacheManager;
import com.wealoha.social.commons.GlobalConstants;
import com.wealoha.social.commons.GlobalConstants.CacheKey;
import com.wealoha.social.commons.HasCache;
import com.wealoha.social.commons.JsonController;
import com.wealoha.social.inject.Injector;
import com.wealoha.social.utils.ContextUtil;
import com.wealoha.social.utils.FontUtil;
import com.wealoha.social.utils.FontUtil.Font;
import com.wealoha.social.utils.PushUtil;
import com.wealoha.social.view.custom.popup.LoadingPopup;

/**
 * Created by walker on 14-4-2.
 */
public abstract class BaseFragment extends Fragment implements HasCache, ActivityResultCallback {

	@Inject
	public ClientLogService mClientLogService;
	protected String TAG = getClass().getSimpleName();
	@Inject
	public JsonController mJsonController;
	@Inject
	CacheManager cacheManager;
	@Inject
	protected ContextUtil contextUtil;
	@Inject
	PushUtil pushUtil;
	@Inject
	protected Context context;
	@Inject
	protected FontUtil fontUtil;
	@Inject
	protected Bus bus;
	protected boolean fragmentVisible = false;
	protected LoadingPopup popup;
	protected Frag2HolderCallback frag2HolderCallback;
	protected ActivityResultCallback mActivityResultCallback;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Injector.inject(this);
		hideMainBar();
		((BaseFragAct) getActivity()).setActivityResultCallback(this);// 让fragment
																		// 也响应act
																		// 的onactivityresult
																		// 方法
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		ButterKnife.inject(this, view);
		super.onViewCreated(view, savedInstanceState);

		fontUtil.changeFonts((ViewGroup) view, Font.ENCODESANSCOMPRESSED_400_REGULAR);
		popup = new LoadingPopup(context);

		initTypeFace();
	}

	protected void initTypeFace() {
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ButterKnife.reset(this);
	}

	/**
	 * 通过Action跳转界面 *
	 */
	protected void startActivity(String action) {
		startActivity(action, null);
	}

	/**
	 * 含有Bundle通过Action跳转界面 *
	 */
	protected void startActivity(String action, Bundle bundle) {
		Intent intent = new Intent();
		intent.setAction(action);
		if (bundle != null) {
			intent.putExtras(bundle);
		}
		startActivity(intent);
	}

	/**
	 * 通过Uri跳转界面 *
	 */
	protected void startActivity(Uri uri) {
		startActivity(uri, null);
	}

	/**
	 * 通过Uri跳转界面 *
	 */
	protected void startActivityHasAnim(BaseFragAct baseFragAct, Uri uri, int enterAnim, int exitAnim) {
		startActivity(uri, null);
	}

	/**
	 * 含有Bundle通过Action跳转界面 *
	 */
	protected void startActivityHasAnim(Uri uri, Bundle bundle, int enterAnim, int exitAnim) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(uri);
		if (bundle != null) {
			intent.putExtras(bundle);
		}
		if (isAdded()) {
			getActivity().startActivity(intent);
			getActivity().overridePendingTransition(enterAnim, exitAnim);
		}
	}

	/**
	 * 含有Bundle通过Action跳转界面 *
	 */
	protected void startActivity(Uri uri, Bundle bundle) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(uri);
		if (bundle != null) {
			intent.putExtras(bundle);
		}
		if (isAdded()) {
			startActivity(intent);
		}
	}

	/**
	 * 含有Bundle通过Action跳转界面 *.
	 */
	protected void startActivityHasAnim(BaseFragAct baseFragAct, Uri uri, Bundle bundle, int enterAnim, int exitAnim) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(uri);
		if (bundle != null) {
			intent.putExtras(bundle);
		}
		baseFragAct.startActivity(intent);
		baseFragAct.overridePendingTransition(enterAnim, exitAnim);
	}

	/**
	 * @Description: 需要返回值的打开下一个界面
	 * @param uri
	 * @param requestCode
	 * @param bundle
	 * @see:
	 * @since:
	 * @description
	 * @author: sunkist
	 * @date:2014-12-22
	 */
	protected void startActivityForResult(Uri uri, int requestCode, Bundle bundle) {
		startActivityForResult(uri, requestCode, bundle, 0, 0);
	}
	protected void startActivityForResult(Uri uri, int requestCode, Bundle bundle, int animIn, int animOut) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(uri);
		if (bundle != null) {
			intent.putExtras(bundle);
		}
		getActivity().startActivityForResult(intent, requestCode/* , bundle */);
		getActivity().overridePendingTransition(animIn, animOut);
		
	}

	/**
	 * 切换Fragment替换当前内容
	 * 
	 * @param fragment
	 */
	protected void replaceFragment(Fragment fragment) {
		getFragmentManager().beginTransaction().replace(R.id.content, fragment).commitAllowingStateLoss();
	}

	/**
	 * 短暂显示Toast提示(来自res) *
	 */
	protected void showShortToast(int resId) {
		Toast.makeText(getActivity(), getString(resId), Toast.LENGTH_SHORT).show();
	}

	@Override
	public <T> void save(CacheKey key, T object) {
		cacheManager.save(getClass(), key, object);
	}

	@Override
	public <T> void saveAppend(CacheKey key, T object) {
		cacheManager.saveAppend(getClass(), key, object);
	}

	public <T> void clearAppend(CacheKey cacheKey) {
		// cacheManager.
		cacheManager.clearAppend(getClass(), cacheKey);
	};

	@Override
	public <T> T restore(CacheKey key) {
		return cacheManager.restore(getClass(), key);
	}

	@Override
	public <T> void globalSave(CacheKey key, T object) {
		cacheManager.globalSave(key, object);
	}

	@Override
	public <T> T globalRestore(CacheKey key) {
		return cacheManager.globalRestore(key);
	}

	public void turnToFrag() {

	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

	@Override
	public void onResume() {
		super.onResume();
		fragmentVisible = true;
		if (contextUtil.getForegroundFrag() == null) {
			contextUtil.setForegroundFrag(this);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		fragmentVisible = false;
		if (contextUtil.getForegroundFrag() != null) {
			contextUtil.deleteForegroundFrag();
		}
	}

	private void hideMainBar() {
		if (getActivity() instanceof MainAct) {
			if (this instanceof AlohaFragment || this instanceof LoadingFragment) {
				((MainAct) getActivity()).hideMenuBar(false);
			} else {
				((MainAct) getActivity()).hideMenuBar(true);
			}
		} else if (getActivity() instanceof FragmentWrapperActivity) {
			((FragmentWrapperActivity) getActivity()).setActivityResultCallback(this);
		}
	}

	/**
	 * @Title: isComeFromHere
	 * @Description: 判断开启这个frag的父容器是哪个
	 * @param isComeHere
	 * @return boolean 返回类型
	 */
	public boolean isComeFromHere(String isComeHere) {
		Bundle bundle = getArguments();
		if (bundle != null) {
			if (isComeHere.equals(bundle.getString("refer_key"))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @Title: whereIsComeFrom
	 * @Description: 同isComeFromHere();
	 * @return String 返回类型
	 */
	public String whereIsComeFrom() {
		Bundle bundle = getArguments();
		if (bundle != null) {
			return bundle.getString("refer_key");
		}
		return null;
	}

	public Bundle getWhereComeFromToProfile() {
		if (getArguments() != null && TextUtils.isEmpty(getArguments().getString("refer_key"))) {
			Bundle bundle = getArguments();
			return bundle;
		}
		return null;
	}

	@Override
	public void onActivityResultCallback(int requestcode, int resultcode, Intent result) {
	}

	/***
	 * feed的视图holder回调，例如变更留言数目等
	 *
	 */
	public interface Frag2HolderCallback {

		public void commentCallback(int count);
	}

	public void setFeed2FragCallback(Frag2HolderCallback callback) {
		frag2HolderCallback = callback;
	}

	/**
	 * @Title: onActivityResultCallback
	 * @Description: 方便这个包装类返回activity result 的结果给它包装的frag
	 */
	public void setActivityResultCallback(ActivityResultCallback activityResultCallback) {
		mActivityResultCallback = activityResultCallback;
	}

	/**
	 * 跳转到话题页
	 * 
	 * @return void
	 */
	public void openTopic(HashTag hashTag) {
		Bundle bundle = new Bundle();
		bundle.putString(GlobalConstants.TAGS.OPEN_HASH_TAG_TYPE, GlobalConstants.TAGS.IS_FEED_HEAD_HASHTAG);
		bundle.putSerializable(GlobalConstants.TAGS.IS_HASHTAG_OBJ, hashTag);
		startActivityHasAnim(GlobalConstants.IntentAction.INTENT_URI_TOPIC,//
				bundle, R.anim.left_in, R.anim.stop);
	}
	

}
