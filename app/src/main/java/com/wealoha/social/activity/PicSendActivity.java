package com.wealoha.social.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.InjectView;
import butterknife.OnClick;

import com.squareup.picasso.Picasso;
import com.wealoha.social.ActivityManager;
import com.wealoha.social.BaseFragAct;
import com.wealoha.social.ContextConfig;
import com.wealoha.social.R;
import com.wealoha.social.activity.TagFeedAct.TagFeedActBundleKey;
import com.wealoha.social.api.post.bean.Post;
import com.wealoha.social.beans.ImageUploadResult;
import com.wealoha.social.beans.Result;
import com.wealoha.social.beans.ResultData;
import com.wealoha.social.api.FeedService;
import com.wealoha.social.beans.feed.UserTags;
import com.wealoha.social.beans.Location;
import com.wealoha.social.commons.GlobalConstants;
import com.wealoha.social.inject.Injector;
import com.wealoha.social.utils.ContextUtil;
import com.wealoha.social.utils.ConversionUtil;
import com.wealoha.social.utils.FontUtil;
import com.wealoha.social.utils.FontUtil.Font;
import com.wealoha.social.utils.ImageUtil;
import com.wealoha.social.utils.ImageUtil.CropMode;
import com.wealoha.social.utils.ToastUtil;
import com.wealoha.social.utils.UiUtils;
import com.wealoha.social.utils.XL;
import com.wealoha.social.view.custom.dialog.ListItemDialog;
import com.wealoha.social.view.custom.dialog.ListItemDialog.ListItemCallback;
import com.wealoha.social.view.custom.dialog.ListItemDialog.ListItemType;

/**
 * 
 * @author sunkist
 * @author superman
 * @see
 * @since
 * @date 2014-11-21 19:04:16
 */
public class PicSendActivity extends BaseFragAct implements OnClickListener, ListItemCallback {

	@Inject
	ContextUtil contextUtil;
	@Inject
	FeedService feedService;
	@Inject
	Picasso picasso;
	@Inject
	FontUtil fontUtil;
	@InjectView(R.id.pic_feed_summary)
	EditText mPicSummary;
	@InjectView(R.id.pic_feed_pic)
	ImageView mPic;
	@InjectView(R.id.pic_des_back)
	ImageView mBack;
	@InjectView(R.id.pic_des_send)
	TextView mSend;
	@InjectView(R.id.layout)
	ViewGroup layout;
	@InjectView(R.id.send_pic_location_ll)
	LinearLayout mLocation;
	@InjectView(R.id.send_pic_tag_ll)
	LinearLayout mTag;
	@InjectView(R.id.tag_tv)
	TextView mTagTv;
	@InjectView(R.id.tag_iv)
	ImageView mTagImg;
	@InjectView(R.id.location_tv)
	TextView mLocationTv;
	@InjectView(R.id.location_iv)
	ImageView mLocationImg;
	@InjectView(R.id.title)
	RelativeLayout mTitle;

	private InputMethodManager imm;
	private Bundle mInitBundle;
	private Bitmap mBitmap;
	// private String mPicPath;

	private String mPicPath;
	private String mImageId;

	private String openFrom;
	private String openType;
	private String hashtagId;
	private Location mLocationResult;

	private int openLocationRequestCode = 1;
	private int openTagRequestCode = 2;
	private Float[] mAnchorX;
	private Float[] mAnchorY;
	private Float[] mCenterX;
	private Float[] mCenterY;
	private String[] mUserids;

	private ArrayList<UserTags> mUserTagsList;
	/** 要分享的feed */
	// private Feed mShareFeed;
	private Post mSharePost;
	// 当前act 的两种模式
	public final static String SHARE_FEED = "SHARE_FEED";// 分享
	public final static String PIC_SEND = "PIC_SEND";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_pic_send);
		Injector.inject(this);
		findViewById();
		Intent intent = getIntent();
		mInitBundle = intent.getExtras();
		initData(mInitBundle);
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	}

	@Override
	protected void initTypeFace() {
		fontUtil.changeFonts(mTitle, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
	}

	private void resetImageView(Bitmap mBitmap2) {
		mPic.setImageBitmap(mBitmap2);
	}

	public Bitmap createBitmap(String path, int w, int h) {
		try {
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(path, opts);
			int srcWidth = opts.outWidth;
			int srcHeight = opts.outHeight;
			int destWidth = 0;
			int destHeight = 0;
			double ratio = 0.0;
			if (srcWidth < w || srcHeight < h) {
				ratio = 0.0;
				destWidth = srcWidth;
				destHeight = srcHeight;
			} else if (srcWidth > srcHeight) {
				ratio = (double) srcWidth / w;
				destWidth = w;
				destHeight = (int) (srcHeight / ratio);
			} else {
				ratio = (double) srcHeight / h;
				destHeight = h;
				destWidth = (int) (srcWidth / ratio);
			}
			BitmapFactory.Options newOpts = new BitmapFactory.Options();
			newOpts.inSampleSize = (int) ratio + 1;
			newOpts.inJustDecodeBounds = false;
			newOpts.outHeight = destHeight;
			newOpts.outWidth = destWidth;
			return BitmapFactory.decodeFile(path, newOpts);
		} catch (Exception e) {
			return null;
		}
	}

	@OnClick({ R.id.send_pic_tag_ll, R.id.send_pic_location_ll, R.id.pic_des_back, R.id.pic_des_send })
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.pic_des_back:
			if (GlobalConstants.AppConstact.IMG_PATH_FROM_PHOTO_PICKED_WITH_DATA.equals(openFrom)) {// ç›¸å†Œ
				backLogic();
			} else if (GlobalConstants.AppConstact.IMG_PATH_FROM_CAMERA_WITH_DATA.equals(openFrom)) {// ç›¸æœº
				backLogic();
			}
			finish();
			overridePendingTransition(0, R.anim.right_out);
			break;
		case R.id.pic_des_send:
			if (TextUtils.isEmpty(mPicPath)) {
				publishFeed(mImageId);
			} else {
				uploadMethod();
			}
			break;
		case R.id.send_pic_location_ll:
			startLocation();
			break;
		case R.id.send_pic_tag_ll:
			final Bundle bundle = new Bundle();
			if (TextUtils.isEmpty(mPicPath)) {
				bundle.putString(TagFeedActBundleKey.IMG_ID, mImageId);
			} else {
				bundle.putString(TagFeedActBundleKey.IMG_PATH, mPicPath);
			}
			bundle.putParcelableArrayList(UserTags.TAG, mUserTagsList);

			startActivityForResultDelayed(GlobalConstants.IntentAction.INTENT_URI_TAGS_FEED, bundle,//
					R.anim.push_bottom_in, R.anim.stop, openTagRequestCode);
			break;
		}
	}

	/**
	 * @Title: startLocation
	 * @Description: 开启定位
	 */
	public void startLocation() {
		if (isThereVenue()) {
			new ListItemDialog(this, (ViewGroup) container).showListItemPopup(this, getString(R.string.location_str),//
					ListItemType.REMOVE_OLD_VENUES,//
					ListItemType.USER_NEW_VENUES);
		} else {
			startActivityForResultDelayed(GlobalConstants.IntentAction.INTENT_URI_LOCATION_FOR_FEED, null,//
					R.anim.push_bottom_in, R.anim.stop, openLocationRequestCode);

		}
	}

	/**
	 * @Title: startActivityForResultDelayed
	 * @Description: 延迟开启其他的act，让输入法收回去
	 * @param @param uri
	 * @param @param bundle
	 * @param @param enterAnimId
	 * @param @param outAnimId
	 * @param @param requestCode 设定文件
	 */
	public void startActivityForResultDelayed(final Uri uri, final Bundle bundle,//
			final int enterAnimId, final int outAnimId, final int requestCode) {

		UiUtils.hideKeyBoard(this);
		// 延迟执行，让输入法先收回去
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				startActivityForResult(uri, bundle,//
						enterAnimId, outAnimId, requestCode);
			}
		}, 300);
	}

	/**
	 * @Title: isThereVenue
	 * @Description: 当前是否有地理位置信息
	 */
	public boolean isThereVenue() {
		if (mSharePost != null && !TextUtils.isEmpty(mSharePost.getVenue())//
				|| mLocationResult != null && !mLocationResult.isEmpty()) {
			return true;
		}
		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent result) {
		super.onActivityResult(requestCode, resultCode, result);

		if (requestCode == openLocationRequestCode && resultCode == RESULT_OK && result != null) {
			mLocationResult = (Location) result.getExtras().get("location");
			changeLocationView(mLocationResult.name);
		} else if (requestCode == openTagRequestCode && resultCode == RESULT_OK && result != null) {
			Bundle bundle = result.getExtras();
			mUserTagsList = bundle.getParcelableArrayList(UserTags.TAG);
			tagPositionListToArray(mUserTagsList);
			changeTagView(mUserTagsList == null ? 0 : mUserTagsList.size());
		}

	}

	/**
	 * @Title: tagPositionListToArray
	 * @Description: 初始化tag坐标
	 */
	private void tagPositionListToArray(ArrayList<UserTags> tagsList) {
		if (tagsList != null && tagsList.size() > 0) {
			initTagsCoordinateList(tagsList.size());
			for (int i = 0; i < tagsList.size(); i++) {
				UserTags userTag = tagsList.get(i);
				mUserids[i] = userTag.tagUserId;
				mAnchorX[i] = userTag.tagAnchorX;
				mAnchorY[i] = userTag.tagAnchorY;
				mCenterX[i] = userTag.tagCenterX;
				mCenterY[i] = userTag.tagCenterY;
			}
		}
	}

	private void initTagsCoordinateList(int tagsCount) {
		mUserids = new String[tagsCount];
		mAnchorX = new Float[tagsCount];
		mAnchorY = new Float[tagsCount];
		mCenterX = new Float[tagsCount];
		mCenterY = new Float[tagsCount];
	}

	public void backLogic() {
		MainAct mainAct = (MainAct) ActivityManager.isSaveStack(MainAct.class);
		if (mainAct != null) {
			if (GlobalConstants.AppConstact.IMG_PATH_FROM_CAMERA_WITH_DATA.equals(openFrom)) {
				mainAct.openCamera(mainAct);
			} else if (GlobalConstants.AppConstact.IMG_PATH_FROM_PHOTO_PICKED_WITH_DATA.equals(openFrom)) {
				mainAct.openImgPick(mainAct);
			}
		}
		finish();
		overridePendingTransition(0, R.anim.right_out);
	}

	public void publishFeed(String imgId) {
		String venue = null;
		Double latitude = null;
		Double longitude = null;
		if (mLocationResult != null) {
			XL.i("PIC_SEND_TAG", ":" + mLocationResult.latitude);
			XL.i("PIC_SEND_TAG", ":" + mLocationResult.longitude);
			XL.i("PIC_SEND_TAG", ":" + mLocationResult.id);
			venue = mLocationResult.id;
			latitude = mLocationResult.latitude;
			longitude = mLocationResult.longitude;
		}
		feedService.uploadFeed(imgId,//
				mPicSummary.getText().toString().trim(), //
				null,//
				venue,//
				hashtagId, //
				latitude,//
				longitude,//
				mAnchorX,//
				mAnchorY,//
				mCenterX, //
				mCenterY, //
				mUserids,//
				new Callback<Result<ResultData>>() {

					@Override
					public void failure(RetrofitError failureResult) {
						// PromptPopup.hidePrompt();
						if (popup != null) {
							popup.hide();
						}
						ToastUtil.showCustomToast(PicSendActivity.this, R.string.network_error);
					}

					@Override
					public void success(Result<ResultData> result, Response arg1) {
						if (popup != null) {
							popup.hide();
						}
						if (result != null && result.isOk()) {
							// 发布新feed，做一个标记，进入profile时要刷新profile
							ContextConfig.getInstance().putBooleanWithFilename(GlobalConstants.GlobalCacheKeys.POST_NEW_FEED, true);
							ToastUtil.longToast(PicSendActivity.this, getString(R.string.published_success));
							setResult(Activity.RESULT_OK);
							finish();
						} else if (result.data.error == 200526) {
							ToastUtil.longToast(PicSendActivity.this, getString(R.string.describe_has_illegalword));
							finish();
						} else {
						}
					}
				});
	}

	public void uploadMethod() {

		if (popup != null) {
			popup.show(container);
		}

		feedService.sendSingleFeed(new TypedFile("application/octet-stream", new File(mPicPath)), new Callback<Result<ImageUploadResult>>() {

			@Override
			public void failure(RetrofitError arg0) {
				if (popup != null) {
					popup.hide();
				}
				ToastUtil.longToast(PicSendActivity.this, R.string.Unkown_Error);
				finish();
			}

			@Override
			public void success(Result<ImageUploadResult> result, Response arg1) {
				if (result != null && result.isOk()) {
					publishFeed(result.data.imageId);
				} else {
					ToastUtil.longToast(PicSendActivity.this, getString(R.string.image_upload_failed));
					setResult(GlobalConstants.AppConstact.OPEN_COMPOSE_FEED);
					// PromptPopup.hidePrompt();
					finish();
				}
			}
		});
	}

	protected void findViewById() {
		mPicSummary = (EditText) findViewById(R.id.pic_feed_summary);
		mPic = (ImageView) findViewById(R.id.pic_feed_pic);
		mBack = (ImageView) findViewById(R.id.pic_des_back);
		mSend = (TextView) findViewById(R.id.pic_des_send);

		mSend.setOnClickListener(this);
		mBack.setOnClickListener(this);
	}

	/**
	 * @Title: initPhoto
	 */
	public void initData(Bundle bundle) {
		if (bundle != null) {
			openFrom = bundle.getString("openFrom");
			openType = bundle.getString("openType");
			hashtagId = bundle.getString("hashtagId", null);
			
			String actType = bundle.getString(PicSendActivityBundleKey.PIC_SEND_TYPE, PicSendActivity.PIC_SEND);
			// 发 feed
			if (PicSendActivity.PIC_SEND.equals(actType)) {
				initPhotoByPicSend(bundle);
			} else {// 分享feed
				mSharePost = (Post) bundle.getSerializable(Post.TAG);
				mUserTagsList = Post.transTagsToOldVer(mSharePost.getUserTags(), mSharePost.isTagMe());

				// 初始化地理信息
				mLocationResult = new Location();
				mLocationResult.id = mSharePost.getVenueId();
				mLocationResult.latitude = mSharePost.getLatitude();
				mLocationResult.longitude = mSharePost.getLongitude();
				
				if(TextUtils.isEmpty(hashtagId)){
					hashtagId = mSharePost.getHashTag().getItemId();
				}
				
				tagPositionListToArray(mUserTagsList);// 初始化tag 坐标
				initPhotoByShared(mSharePost.getImage().getImageId());
				changeTagView(mUserTagsList == null ? 0 : mUserTagsList.size());
				changeLocationView(mSharePost.getVenue());
			}
		} else {
			ToastUtil.shortToast(PicSendActivity.this, getString(R.string.image_get_failed_try_later));
			return;
		}

	}

	/**
	 * @Title: initPhotoByPicSend
	 * @Description: 从发feed 进入发送界面时，显示的缩略图
	 */
	private void initPhotoByPicSend(Bundle bundle) {
		mPicPath = bundle.getString("path");
		try {
			mBitmap = createBitmap(mPicPath, ConversionUtil.px2dip(PicSendActivity.this, 640), ConversionUtil.px2dip(PicSendActivity.this, 640));
			if (mBitmap == null) {
				Toast.makeText(PicSendActivity.this, R.string.cant_find_image, Toast.LENGTH_SHORT).show();
			} else {
				resetImageView(mBitmap);
			}
		} catch (Exception e) {
			Toast.makeText(PicSendActivity.this, R.string.cant_find_image, Toast.LENGTH_SHORT).show();
			finish();
		}
	}

	/**
	 * @Title: initPhotoByPicSend
	 * @Description: 从分享 进入发送界面时，显示的缩略图
	 */
	private void initPhotoByShared(String imageId) {
		mImageId = imageId;
		picasso.load(ImageUtil.getImageUrl(imageId, 60, CropMode.ScaleCenterCrop)).into(mPic);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			backLogic();
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		popInputMethod();
	}

	public void popInputMethod() {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {

				// imm = (InputMethodManager)
				// getSystemService(Context.INPUT_METHOD_SERVICE);
				if (imm != null && mPicSummary != null) {
					imm.showSoftInput(mPicSummary, 0);
				}
			}
		}, 500);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (imm != null && mPicSummary != null) {
			imm.hideSoftInputFromWindow(mPicSummary.getWindowToken(), 0);
		}
	}

	public void changeTagView(int count) {
		String title = getString(R.string.tag_your_friend);
		int imgid = R.drawable.start_tag;
		int colorid = R.color.gray_text;
		if (mTagTv != null && count > 0) {
			title = getString(R.string.tag_x_friends, mUserTagsList.size());
			imgid = R.drawable.start_tag_black;
			colorid = R.color.black_text;
		}
		mTagImg.setImageResource(imgid);
		mTagTv.setTextColor(getResources().getColor(colorid));
		mTagTv.setText(title);
	}

	public void changeLocationView(String locationname) {
		String title = getString(R.string.add_your_location);
		int imgid = R.drawable.start_location;
		int colorid = R.color.gray_text;
		if (!TextUtils.isEmpty(locationname)) {
			title = locationname;
			imgid = R.drawable.start_location_black;
			colorid = R.color.black_text;
		}
		mLocationTv.setText(title);
		mLocationTv.setTextColor(getResources().getColor(colorid));
		mLocationImg.setImageResource(imgid);
	}

	public interface PicSendActivityBundleKey {

		public final static String PHOTO_ID = "PHOTO_ID";
		public final static String PIC_SEND_TYPE = "PIC_SEND_TYPE";
	}

	@Override
	public void itemCallback(int listItemType) {
		switch (listItemType) {
		case ListItemType.REMOVE_OLD_VENUES:
			if (mSharePost != null) {
				mSharePost.setVenueid(null);
				changeLocationView(null);
			}
			if (mLocationResult != null) {
				mLocationResult = null;
				changeLocationView(null);
			}
			break;
		case ListItemType.USER_NEW_VENUES:
			startActivityForResult(GlobalConstants.IntentAction.INTENT_URI_LOCATION_FOR_FEED, null,//
					R.anim.push_bottom_in, R.anim.stop, openLocationRequestCode);
			break;
		default:
			break;
		}
	}

}