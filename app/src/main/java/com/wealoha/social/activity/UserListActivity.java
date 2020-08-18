package com.wealoha.social.activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.InjectView;

import com.wealoha.social.BaseFragAct;
import com.wealoha.social.R;
import com.wealoha.social.adapter.UserListAdapter;

/**
 * @author:sunkist
 * @see:
 * @since:
 * @description 用户列表清单
 * @copyright wealoha.com
 * @Date:2015年3月10日
 */
public class UserListActivity extends BaseFragAct {

	@InjectView(R.id.user_list_title_tv)
	TextView mTitle;
	@InjectView(R.id.user_list_back_iv)
	ImageView mBack;
	@InjectView(R.id.user_list_lv)
	ListView mListView;
	private UserListAdapter mUserListAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_list);
		// 获取数据
	}
}
