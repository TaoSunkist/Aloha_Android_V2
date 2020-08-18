package com.wealoha.social.activity;

import java.util.List;

import javax.inject.Inject;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.InjectView;

import com.wealoha.social.BaseFragAct;
import com.wealoha.social.R;
import com.wealoha.social.adapter.NewAlohaAdapter;
import com.wealoha.social.api.notify2.bean.NewAlohaNotify2;
import com.wealoha.social.api.notify2.bean.Notify2;
import com.wealoha.social.api.notify2.bean.PostLikeNotify2;
import com.wealoha.social.api.user.bean.User;
import com.wealoha.social.utils.ContextUtil;
import com.wealoha.social.utils.FontUtil.Font;
import com.wealoha.social.utils.ToastUtil;

public class NewAlohaActivity extends BaseFragAct {

	@Inject
	ContextUtil contextUtil;

	private ListView mNewAlohaLv;
	private NewAlohaAdapter mNewAlohaAdapter;
	private List<User> mUsers;
	@InjectView(R.id.aloha_title_tv)
	TextView mTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_aloha);
		findViewById();
		Bundle bundle = getIntent().getExtras();

		if (bundle != null) {
			Notify2 notify2 = (Notify2) bundle.getSerializable("Users");
			if (notify2 != null) {
				initData(notify2);
			} else {
				ToastUtil.shortToast(this, R.string.Unkown_Error);
				finish();
			}

		}

	}

	// private int getCurrentUserAlohaCount() {
	// if (contextUtil.getCurrentUser() != null) {
	// return contextUtil.getCurrentUser().alohaCount;
	// } else {
	// return 0;
	// }
	// }

	private void findViewById() {
		mNewAlohaLv = (ListView) findViewById(R.id.new_aloha_lv);
	}

	public void initData(Notify2 notify2) {
		fontUtil.changeViewFont(mTitle, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
		if (notify2 instanceof NewAlohaNotify2) {
			NewAlohaNotify2 mNewAlohaNotify2 = (NewAlohaNotify2) notify2;
			mUsers = mNewAlohaNotify2.getUsers();
			mTitle.setText(mResources.getString(R.string.new_aloha_title_aloha, mUsers.size()));
			mNewAlohaAdapter = new NewAlohaAdapter(mUsers, mNewAlohaNotify2.getCount(), this, true);
		} else if (notify2 instanceof PostLikeNotify2) {
			PostLikeNotify2 mPostLikeNotify2 = (PostLikeNotify2) notify2;
			mUsers = mPostLikeNotify2.getUsers();
			mTitle.setText(mResources.getString(R.string.feed_like_title, mUsers.size()));
			mNewAlohaAdapter = new NewAlohaAdapter(mUsers, mPostLikeNotify2.getCount(), this, true);
		}
		mNewAlohaLv.setAdapter(mNewAlohaAdapter);
		mNewAlohaLv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				User user = (User) parent.getItemAtPosition(position);
				NewAlohaActivity.this.toProfileLogin(user);
			}
		});
	}

	public void closeCurrent(View view) {
		finish();
	}

	public interface NewAlohaActBundleKey {

		public static final String TYPE = "type";
		public static final int PRAISE_TYPE = 0;
		public static final int PRAISE_COUNT_KEY = 1;
		public static final int ALOHA_TYPE = 2;
	}
}
