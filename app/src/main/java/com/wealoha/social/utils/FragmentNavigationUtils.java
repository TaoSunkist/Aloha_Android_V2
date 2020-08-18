package com.wealoha.social.utils;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;

import com.wealoha.social.fragment.BaseFragment;

/**
 * Fragment 在 R.id.content 区域导航使用
 * 
 * @author javamonk
 * @createTime 14-10-13 PM4:36
 */
public class FragmentNavigationUtils {

	private static FragmentNavigationUtils instance;

	private List<BaseFragment> items = new ArrayList<BaseFragment>();

	// 暂时不用管底部的导航
	public static class Item {
		public Fragment fragment;
		public int menuId;
	}

	public static FragmentNavigationUtils getInstance() {
		if (instance == null) {
			instance = new FragmentNavigationUtils();
		}
		return instance;
	}

	/**
	 * 取最后保存进来的视图
	 * 
	 * @return 没有就返回null
	 */
	public BaseFragment getLastFragment() {
		if (items.size() > 0) {
			return items.remove(items.size() - 1);
		}
		return null;
	}

	/**
	 * 切换Fragment前，把当前的视图保存好
	 * 
	 * @param fragment
	 */
	public void saveLastFragment(BaseFragment fragment) {
		items.add(fragment);
	}
}
