package com.wealoha.social.activity;

import androidx.fragment.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;

import com.wealoha.social.ActivityManager;
import com.wealoha.social.BaseFragAct;
import com.wealoha.social.R;
import com.wealoha.social.utils.XL;

public class AlerPhoneNumActivity extends BaseFragAct implements OnClickListener {

	public static final String BUNDLE_KEY_FRAGMENT_CLASS = "fragmentClass";

	private Fragment mFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActivityManager.push(this);
		if (savedInstanceState != null) {

		} else {
			Bundle bundle = getIntent().getExtras();

			try {
				Class<?> clazz = (Class<?>) bundle.get(BUNDLE_KEY_FRAGMENT_CLASS);
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
			getSupportFragmentManager().beginTransaction().add(R.id.act_frag_wrapper_root_rl, mFragment).commit();
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			this.finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onPause() {
		super.onPause();
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

	// @Override
	// protected void onActivityResult(int requestcode, int resultcode, Intent
	// result) {
	// super.onActivityResult(requestcode, resultcode, result);
	// XL.i("CONFIG_FRAG_RESULT", "onActivityResult");
	// if (mActivityResultCallback != null) {
	// mActivityResultCallback.onActivityResultCallback(requestcode, resultcode,
	// result);
	// }
	// }

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
}
