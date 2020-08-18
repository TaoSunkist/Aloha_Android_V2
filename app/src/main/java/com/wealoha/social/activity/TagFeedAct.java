package com.wealoha.social.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnTouch;

import com.squareup.picasso.Picasso;
import com.wealoha.social.BaseFragAct;
import com.wealoha.social.R;
import com.wealoha.social.activity.FindYouAct.FindYouActBundleKey;
import com.wealoha.social.beans.User;
import com.wealoha.social.beans.feed.UserTags;
import com.wealoha.social.commons.GlobalConstants;
import com.wealoha.social.utils.FontUtil;
import com.wealoha.social.utils.FontUtil.Font;
import com.wealoha.social.utils.ImageUtil;
import com.wealoha.social.utils.ImageUtil.CropMode;
import com.wealoha.social.utils.UiUtils;
import com.wealoha.social.view.custom.popup.AtOnePopup;
import com.wealoha.social.view.custom.popup.AtOnePopup.AtOnePopupCallback;

public class TagFeedAct extends BaseFragAct implements OnTouchListener, OnClickListener, AtOnePopupCallback {

	@Inject
	Picasso picasso;
	@Inject
	FontUtil fontUtil;
	@InjectView(R.id.photo)
	ImageView mPhoto;
	@InjectView(R.id.photo_container)
	FrameLayout mPhotoContainer;
	@InjectView(R.id.tags_container)
	FrameLayout mTagsContainer;
	@InjectView(R.id.back_tv)
	ImageView mBack;
	@InjectView(R.id.save_tags)
	TextView mSaveTag;
	@InjectView(R.id.tag_lead_iv)
	ImageView mTagLeadImg;
	@InjectView(R.id.tag_lead_tv)
	TextView mTagLeadText;
	@InjectView(R.id.menu_bar)
	RelativeLayout mTitle;

	private Bundle mBundle;
	private Intent intent;
	private List<AtOnePopup> atOnePops;
	private ArrayList<UserTags> userTagsList;

	private float mInitX;
	private float mInitY;
	private final int mOpenFindYouAct = 1;
	private Dialog alertDialog;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_tag_feed);

		// mPhotoContainer.getLayoutParams().width = UiUtils.getScreenPoint(this).x;
		// mPhotoContainer.getLayoutParams().height = UiUtils.getScreenPoint(this).y;

		mPhotoContainer.getLayoutParams().width = UiUtils.getScreenWidth(mContext);
		mPhotoContainer.getLayoutParams().height = UiUtils.getScreenWidth(mContext);

		getDataFromBundle();
		atOnePops = new ArrayList<AtOnePopup>();
	}

	@Override
	protected void initTypeFace() {
		fontUtil.changeFonts(mTitle, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
	}

	/**
	 * @Title: getDataFromBundle
	 * @Description: 获取上一个界面传来的值
	 */
	private void getDataFromBundle() {
		intent = getIntent();
		mBundle = intent.getExtras();
		if (mBundle != null) {
			String imgPath = mBundle.getString(TagFeedActBundleKey.IMG_PATH);
			String imgId = mBundle.getString(TagFeedActBundleKey.IMG_ID);
			userTagsList = mBundle.getParcelableArrayList(UserTags.TAG);

			if (userTagsList != null && userTagsList.size() > 0) {
				changeLeadImgView(true);
			}
			mTagsContainer.post(new CreatPopupRunnable());
			if (TextUtils.isEmpty(imgId)) {
				picasso.load(new File(imgPath)).into(mPhoto);
			} else {
				String imgTempId = ImageUtil.getImageUrl(imgId, 360, CropMode.ScaleCenterCrop);
				picasso.load(imgTempId).into(mPhoto);
			}
		}
	}

	@Override
	@OnTouch(R.id.tags_container)
	public boolean onTouch(View v, MotionEvent event) {
		if (removeDeleteView()) {
			return false;
		}
		// 最多15个
		if (atOnePops != null && atOnePops.size() < 15) {
			mInitX = event.getX();
			mInitY = event.getY();
			Bundle bundle = new Bundle();
			bundle.putString(FindYouActBundleKey.FIND_YOU_TYPE, FindYouActBundleKey.TAGS_SOMEONE);
			startActivityForResult(GlobalConstants.IntentAction.INTENT_URI_FIND_YOU, bundle, R.anim.push_bottom_in, R.anim.stop, mOpenFindYouAct);
		} else {
			openAlertDialog(this);
		}
		v.performClick();
		return false;
	}

	/**
	 * @Title: openGuideDialog
	 * @Description: 最多能标注15个人
	 */
	public void openAlertDialog(BaseFragAct baseAct) {
		View view = LayoutInflater.from(baseAct).inflate(R.layout.dialog_first_aloha_time, new LinearLayout(baseAct), false);
		TextView title = (TextView) view.findViewById(R.id.first_aloha_title);
		fontUtil.changeViewFont(title, Font.ENCODESANSCOMPRESSED_700_BOLD);
		title.setText(R.string.tags_up_to);
		TextView message = (TextView) view.findViewById(R.id.first_aloha_message);
		message.setVisibility(View.GONE);
		TextView close = (TextView) view.findViewById(R.id.close_tv);
		close.setText(R.string.ok);
		close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (alertDialog != null && alertDialog.isShowing()) {
					alertDialog.dismiss();
				}
			}
		});

		alertDialog = new AlertDialog.Builder(baseAct)//
		.setView(view)//
		.setCancelable(false) //
		.create();
		alertDialog.show();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent result) {
		super.onActivityResult(requestCode, resultCode, result);
		if (requestCode == mOpenFindYouAct && resultCode == RESULT_OK && result != null) {
			if (result.getParcelableExtra(User.TAG) != null) {
				UserTags userTags = new UserTags();
				User user = (User) result.getParcelableExtra(User.TAG);
				userTags.tagUserName = user.name;
				userTags.tagUserId = user.id;
				userTags.tagAnchorX = mInitX / mTagsContainer.getWidth();
				userTags.tagAnchorY = mInitY / mTagsContainer.getHeight();
				checkSingleTag(userTags.tagUserId);
				changeLeadImgView(true);
				creatPopup(userTags);
			}
		}
	}

	/**
	 * @Title: checkSingleTag
	 * @Description: 同一个人的标签只能有一个，关闭旧的重复标签
	 * @param onePopup
	 * @return boolean 返回类型
	 */
	private void checkSingleTag(String userid) {
		if (atOnePops == null || atOnePops.size() <= 0) {
			return;
		}

		for (int i = 0; i < atOnePops.size(); i++) {
			AtOnePopup popup = atOnePops.get(i);
			if (popup.getTagUserId().equals(userid)) {
				popup.closePopupByGoneNoCallback();
				atOnePops.remove(i);
				break;
			}
		}
		if (atOnePops == null || atOnePops.size() <= 0) {
			changeLeadImgView(false);
		}
	}

	private void creatPopups(List<UserTags> tags) {
		if (tags != null && tags.size() > 0) {
			for (UserTags userTag : tags) {
				creatPopup(userTag);
			}
		}
	}

	private AtOnePopup creatPopup(UserTags userTags) {
		AtOnePopup atone = new AtOnePopup(TagFeedAct.this, mTagsContainer, userTags);
		atOnePops.add(atone);
		atone.initAtPopup(true, this);
		return atone;
	}

	@Override
	@OnClick({ R.id.back_tv, R.id.save_tags })
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_tv:
			finish(R.anim.stop, R.anim.push_bottom_out);
			break;
		case R.id.save_tags:
			getTagsCoordinateList();
			setResult();
			finish(R.anim.stop, R.anim.push_bottom_out);
			break;
		default:
			break;
		}
	}

	/**
	 * @Title: getTagsCoordinate
	 * @Description: 拼装服务器能接受的坐标数据
	 */
	private void getTagsCoordinateList() {

		if (userTagsList == null) {
			userTagsList = new ArrayList<UserTags>();
		} else {
			userTagsList.clear();
		}
		for (int i = 0; i < atOnePops.size(); i++) {
			userTagsList.add(atOnePops.get(i).getPopupInfo());
			mTagsContainer.getChildAt(i);
		}
	}

	/**
	 * @Title: setResult
	 * @Description: 设置返回结果
	 */
	private void setResult() {
		Bundle resultBundle = new Bundle();
		resultBundle.putParcelableArrayList(UserTags.TAG, userTagsList);
		Intent intent = new Intent();
		intent.putExtras(resultBundle);
		setResult(RESULT_OK, intent);
	}

	/**
	 * @Title: changeLeadImgView
	 * @Description: 改变使用引导的视图
	 */
	private void changeLeadImgView(boolean isHaveTags) {
		if (isHaveTags) {
			mTagLeadImg.setImageResource(R.drawable.tag_guide_move);
			mTagLeadText.setText(R.string.move_tags_lead);
		} else {
			mTagLeadImg.setImageResource(R.drawable.tag_guide);
			mTagLeadText.setText(R.string.creat_tags_lead);
		}
	}

	/**
	 * 避免 tags 父容器空指针
	 * 
	 * @see
	 * @since
	 * @date 2015年2月10日 下午12:21:11
	 */
	public class CreatPopupRunnable implements Runnable {

		@Override
		public void run() {
			creatPopups(userTagsList);
		}

	}

	public interface TagFeedActBundleKey {

		public final static String IMG_PATH = "IMG_PATH";
		public final static String IMG_ID = "IMG_ID";
	}

	@Override
	public void closePopupByGoneCallback(AtOnePopup popup) {
		if (atOnePops == null || atOnePops.size() <= 0) {
			return;
		}

		for (int i = 0; i < atOnePops.size(); i++) {
			AtOnePopup onepopup = atOnePops.get(i);
			if (onepopup.equals(popup)) {
				atOnePops.remove(i);
				break;
			}
		}
		if (atOnePops == null || atOnePops.size() <= 0) {
			changeLeadImgView(false);
		}
	}

	@Override
	public void addDeleteViewCallback() {
		if (atOnePops == null || atOnePops.size() <= 0) {
			return;
		}
		for (AtOnePopup onepopup : atOnePops) {
			onepopup.addDeleteView();
		}

	}

	@Override
	public void removeDeleteViewCallback() {
		removeDeleteView();
	}

	/**
	 * @Title: removeDeleteView
	 * @Description: 先关闭tag 控件的删除视图
	 * @return boolean 当前点击操作是关闭 删除视图那么返回 true
	 */
	private boolean removeDeleteView() {
		boolean flag = false;
		if (atOnePops == null || atOnePops.size() <= 0) {
			return flag;
		}
		for (AtOnePopup onepopup : atOnePops) {
			if (onepopup.isDeleteViewVisibility()) {
				flag = true;
				onepopup.removeDeleteView();
			}
		}
		return flag;
	}
}
