package com.wealoha.social.activity;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.wealoha.social.ActivityManager;
import com.wealoha.social.BaseFragAct;
import com.wealoha.social.R;
import com.wealoha.social.commons.GlobalConstants;
import com.wealoha.social.utils.XL;

/**
 * Fragment的封装
 * 
 * @author javamonk
 * @see
 * @since
 * @date 2014-11-17 下午3:57:40
 */
public class FragmentWrapperActivity extends BaseFragAct implements OnClickListener {

	public static final String BUNDLE_KEY_FRAGMENT_CLASS = "fragmentClass";

	private Fragment mFragment;
	
	/**
	 * 是否是从web页跳转而来
	 */
	protected boolean isFromWebPage;
	public static final String IS_FROM_WEB_PAGE_KEY = "IS_FROM_WEB_PAGE_KEY";

	@SuppressWarnings("rawtypes")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActivityManager.push(this);
		isFromWebPage = getIntent().getExtras().getBoolean(IS_FROM_WEB_PAGE_KEY, false);
		if (savedInstanceState != null) {

		} else {
			Bundle bundle = getIntent().getExtras();

			try {
				Class clazz = (Class) bundle.get(BUNDLE_KEY_FRAGMENT_CLASS);
				mFragment = (Fragment) clazz.newInstance();
				if (bundle != null) {
					mFragment.setArguments(bundle);
				}
			} catch (Exception e) {
				XL.d("Xxx", "xxx", e);
			}
		}

		setContentView(R.layout.act_fragment_wrapper);
		if (mFragment != null) {
			// 使用指定的Fragment显示
			getFragmentManager().beginTransaction().add(R.id.act_frag_wrapper_root_rl, mFragment).commit();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			ActivityManager.pop(this);
		} catch (Exception e) {
			XL.d(TAG, "Activity is Finished.");
		}
	}

	public interface ActivityResultCallback {

		/**
		 * @Title: onActivityResultCallback
		 * @Description: 方便这个包装类返回activity result 的结果给它包装的frag
		 * @param @param requestcode
		 * @param @param responsecode
		 * @param @param result 设定文件
		 */
		public void onActivityResultCallback(int requestcode, int resultcode, Intent result);
	}

	@Override
	public void onClick(View v) {

	}
	
	@Override
	public boolean onBackKeyPressed() {
		startMainAct();
		return super.onBackKeyPressed();
	}
	
	@Override
	public void finish() {
		startMainAct();
		super.finish();
	}
	/**
	 * 从push 或 网页进入特定的页面要开启手势锁，关闭这个页面时回到主页，手势锁不开启
	 */
	private void startMainAct(){
		if (isFromWebPage) {
			// 从push跳过来，回到会话列表，不是退出");
			Bundle bundle = new Bundle();
			bundle.putString("openTab", "");
			// startActivity(GlobalConstants.IntentAction.INTENT_URI_MAIN,
			// bundle);
			startActivity(GlobalConstants.IntentAction.INTENT_URI_MAIN, bundle, 0, 0);
		}
	}

}
