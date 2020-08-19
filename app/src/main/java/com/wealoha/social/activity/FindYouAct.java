package com.wealoha.social.activity;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnEditorAction;

import com.squareup.picasso.Picasso;
import com.wealoha.social.BaseFragAct;
import com.wealoha.social.R;
import com.wealoha.social.adapter.FindYouAdapter;
import com.wealoha.social.beans.Result;
import com.wealoha.social.beans.User;
import com.wealoha.social.beans.FindYouResult;
import com.wealoha.social.api.FindYouService;
import com.wealoha.social.commons.GlobalConstants;
import com.wealoha.social.fragment.Profile2Fragment;
import com.wealoha.social.utils.FontUtil;
import com.wealoha.social.utils.FontUtil.Font;
import com.wealoha.social.utils.ImageUtil;
import com.wealoha.social.utils.ImageUtil.CropMode;
import com.wealoha.social.utils.StringUtil;
import com.wealoha.social.utils.XL;
import com.wealoha.social.view.custom.CircleImageView;

public class FindYouAct extends BaseFragAct implements TextWatcher, OnItemClickListener, OnClickListener,
		OnTouchListener, OnEditorActionListener {

	@Inject
	FindYouService findyouService;
	@Inject
	Picasso picasso;
	@Inject
	Context context;
	@Inject
	FontUtil font;

	@InjectView(R.id.find_you_search_view)
	EditText mSearchView;
	@InjectView(R.id.find_you_canceled)
	TextView mCanceled;

	@InjectView(R.id.find_you_list)
	ListView mList;

	@InjectView(R.id.container)
	LinearLayout parentContainer;
	@InjectView(R.id.find_you_delete_edit)
	ImageView mDeleteEdit;
	@InjectView(R.id.find_you_edit_progress)
	ProgressBar mEditProgress;

	private FindYouAdapter mFindYouAdapter;
	private User currentUser;
	private InputMethodManager imm;

	// list header
	private LinearLayout headerContainer;
	private int headerContainerHeight;
	private CircleImageView headerUserPhoto;
	private TextView headerUserName;
	private RelativeLayout headerAlohaListTitle;
	private RelativeLayout headerFindResult;

	private String mKeyword;
	private boolean syncLastPageBool;
	private String mFindYouActType;
	private FindYouResult mDefUsersResult;
	private FindYouResult mFindYouResult;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_find_you);
		mFindYouAdapter = new FindYouAdapter();
		mSearchView.addTextChangedListener(this);
		mList.setOnItemClickListener(this);
		mList.setOnTouchListener(this);
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		mSearchView.postDelayed(new Runnable() {

			@Override
			public void run() {
				imm.showSoftInput(mSearchView, 0);
			}
		}, 500);

		initListHeader(mList);
		getBundleData();
		getDefaultList();
		mList.setAdapter(mFindYouAdapter);
		// initAdapterWithDefUsers();
	}

	/**
	 * @Title: getBundleData
	 * @Description: 获取bundle 信息，标记这个搜索界面时哪个功能开启的，不同界面开启的搜索 ，功能和视图渲染不同
	 */
	private void getBundleData() {
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			mFindYouActType = bundle.getString(FindYouActBundleKey.FIND_YOU_TYPE);
		}
	}

	/**
	 * @Title: getDefaultList
	 * @Description: 寻找要圈人目标时的系统推荐圈人列表
	 */
	private void getDefaultList() {
		findyouService.defaultTagUsers(new Callback<Result<FindYouResult>>() {

			@Override
			public void success(Result<FindYouResult> result, Response arg1) {
				if (result != null && result.isOk()) {
					mDefUsersResult = result.data;
					initAdapterWithDefUsers();
				}
			}

			@Override
			public void failure(RetrofitError arg0) {
				XL.i("FIND_YOU_TEST", "error:" + arg0.getMessage());
			}
		});
	}

	/**
	 * @Title: initAdapterWithDefUsers
	 * @Description: 装载默认用户
	 */
	private void initAdapterWithDefUsers() {
		if (FindYouActBundleKey.TAGS_SOMEONE.equals(mFindYouActType)) {
			mFindYouAdapter.clearData();
			mFindYouAdapter.setData(mDefUsersResult);
			mList.setPadding(0, -headerContainerHeight, 0, 0);
		} else {
			headerContainer.setVisibility(View.GONE);
			mFindYouAdapter.clearData();
			mFindYouAdapter.setData(null);
		}
		mFindYouAdapter.notifyDataSetChanged();
	}

	private void initListHeader(ListView parent) {
		if (headerContainer != null) {
			return;
		}

		LayoutInflater inflater = getLayoutInflater();
		headerContainer = (LinearLayout) inflater.inflate(R.layout.act_find_you_header, parent, false);
		headerUserPhoto = (CircleImageView) headerContainer.findViewById(R.id.find_you_pic_left);
		headerUserName = (TextView) headerContainer.findViewById(R.id.find_you_username_tv);
		font.changeViewFont(headerUserName, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
		headerAlohaListTitle = (RelativeLayout) headerContainer.findViewById(R.id.find_you_list_title_rl);
		headerFindResult = (RelativeLayout) headerContainer.findViewById(R.id.find_you_user_rl);
		headerFindResult.setOnClickListener(this);
		headerContainer.setVisibility(View.GONE);
		headerContainer.post(new Runnable() {

			@Override
			public void run() {
				headerContainerHeight = headerContainer.getHeight();
			}
		});
		parent.addHeaderView(headerContainer);
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		// XL.i("FIND_YOU", "-s:" + s);
		// XL.i("FIND_YOU", "-start:" + start);
		// XL.i("FIND_YOU", "-count:" + count);
		// XL.i("FIND_YOU", "-after:" + after);
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		XL.i("FIND_YOU", "--s:" + s);
		XL.i("FIND_YOU", "--start:" + start);
		XL.i("FIND_YOU", "--count:" + count);
		XL.i("FIND_YOU", "--before:" + before);
	}

	@Override
	public void afterTextChanged(Editable s) {
		XL.i("FIND_YOU", s.toString());
		findYou(s.toString(), null);
	}

	private Callback<Result<FindYouResult>> findYouCallback = new Callback<Result<FindYouResult>>() {

		@Override
		public void failure(RetrofitError arg0) {
			syncLastPageBool = false;
			XL.i("FIND_YOU", "failure:" + arg0.getMessage());
			if (!TextUtils.isEmpty(mKeyword)) {
				if (mEditProgress != null && mDeleteEdit != null) {
					// mEditProgress.setVisibility(View.GONE);
					mDeleteEdit.setVisibility(View.VISIBLE);
				}
			}
			if (headerAlohaListTitle != null) {
				headerAlohaListTitle.setVisibility(View.INVISIBLE);
			}
		}

		@Override
		public void success(Result<FindYouResult> result, Response arg1) {
			syncLastPageBool = false;
			if (result == null || !result.isOk() || mList == null) {
				return;
			}
			if (mList == null || headerAlohaListTitle == null || headerUserName == null || headerUserPhoto == null) {
				return;
			}

			// 返回的关键字和当前关键字是否一样，不一样则说明返回的结果已经过期
			if (!TextUtils.isEmpty(result.data.keyword)) {
				if (!result.data.keyword.equals(mKeyword)) {
					return;
				}
			} else {
				return;
			}

			// 初始化aloha list 列表
			if (result.data.list.size() > 0) {
				headerAlohaListTitle.setVisibility(View.VISIBLE);
				mFindYouResult = result.data;
				mFindYouAdapter.clearData();
				mFindYouAdapter.setData(mFindYouResult);
				mFindYouAdapter.notifyDataSetChanged();

				XL.i("FIND_YOU_TEST", "size:" + result.data.list.size());
			} else {
				// 隐藏aloha list
				if (mFindYouAdapter != null) {
					mFindYouAdapter.clearData();
					mFindYouAdapter.notifyDataSetChanged();
					headerAlohaListTitle.setVisibility(View.INVISIBLE);
				}
			}

			// 初始化搜寻结果
			User user = result.data.user;
			if (user != null) {
				currentUser = user;
				headerUserName.setText(StringUtil.foregroundHight(user.getName(), result.data.keyword));
				picasso.load(ImageUtil.getImageUrl(user.getAvatarImage().getId(),//
													100, CropMode.ScaleCenterCrop))//
				.placeholder(R.drawable.search_persion)//
				.into(headerUserPhoto);
			} else {
				currentUser = null;
				if (!TextUtils.isEmpty(result.data.keyword)) {
					headerUserName.setText(StringUtil.foregroundHight(context.getResources().getString(R.string.not_find_the_person) + result.data.keyword, result.data.keyword));
				}
			}

			if (mEditProgress != null && mDeleteEdit != null) {
				// mEditProgress.setVisibility(View.GONE);
				mDeleteEdit.setVisibility(View.VISIBLE);
			}
		}
	};

	private void findYou(String keyword, Integer count) {
		mKeyword = keyword;
		if (TextUtils.isEmpty(keyword)) {
			initAdapterWithDefUsers();
			headerContainer.setVisibility(View.GONE);
			mDeleteEdit.setVisibility(View.GONE);
			// mEditProgress.setVisibility(View.GONE);
		} else {
			mList.setPadding(0, 0, 0, 0);
			headerContainer.setVisibility(View.VISIBLE);
			headerUserName.setText(StringUtil.foregroundHight(keyword, keyword));
			picasso.load(R.drawable.search_persion).into(headerUserPhoto);
			// 针对键盘上的搜索键给搜索请求加一把锁
			syncLastPageBool = true;
			findyouService.findYou(keyword, count, findYouCallback);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		User user = (User) mList.getItemAtPosition(position);
		startFragment(user);
	}

	@OnClick({ R.id.find_you_canceled, R.id.find_you_delete_edit })
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.find_you_user_rl:
			if (currentUser != null) {
				startFragment(currentUser);
			}
			break;
		case R.id.find_you_delete_edit:
			findYou(null, null);
			mSearchView.setText("");
			break;
		case R.id.find_you_canceled:
			finish(R.anim.stop, R.anim.push_bottom_out);
			break;
		default:
			break;
		}

	}

	/**
	 * @Title: startFragment
	 * @Description: 点击搜索的用户之后，执行哪种操作
	 * @param user
	 *            设定文件
	 */
	private void startFragment(User user) {
		Bundle bundle = new Bundle();
		bundle.putSerializable(User.TAG, user);
		bundle.putString("refer_key", GlobalConstants.WhereIsComeFrom.SEARCH_TO_PROFILE);
		if (!TextUtils.isEmpty(mFindYouActType) && FindYouActBundleKey.TAGS_SOMEONE.equals(mFindYouActType)) {
			Intent intent = new Intent();
			intent.putExtras(bundle);
			setResult(RESULT_OK, intent);
			finish(R.anim.stop, R.anim.push_bottom_out);
		} else {
			startFragment(Profile2Fragment.class, bundle, true);
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (imm != null && mSearchView != null) {
			imm.hideSoftInputFromWindow(mSearchView.getWindowToken(), 0);
		}
		v.performClick();
		return false;
	}

	@Override
	@OnEditorAction(R.id.find_you_search_view)
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (actionId == KeyEvent.KEYCODE_HOME && !syncLastPageBool) {
			imm.hideSoftInputFromWindow(mSearchView.getWindowToken(), 0);
			// findYou(mKeyword, null);
		}
		return true;
	}

	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.stop, R.anim.push_bottom_out);
	}

	public interface FindYouActBundleKey {

		public final static String TAGS_SOMEONE = "TAGS_SOMEONE";
		public final static String FIND_YOU_TYPE = "FIND_YOU_TYPE";
	}
}
