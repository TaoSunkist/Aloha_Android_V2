package com.wealoha.social.activity;

import java.util.List;

import javax.inject.Inject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.viewpager.widget.ViewPager;

import com.wealoha.social.BaseFragAct;
import com.wealoha.social.R;
import com.wealoha.social.adapter.ViewPagerAdapter;
import com.wealoha.social.commons.GlobalConstants;
import com.wealoha.social.utils.ContextUtil;
import com.wealoha.social.utils.ToastUtil;

/**
 * @author:sunkist
 * @see:
 * @since:
 * @description新手导航
 * @copyright wealoha.com
 * @Date:2014-12-10
 */
public class WebLaunchActivity extends BaseFragAct implements ViewPager.OnPageChangeListener {

	private static final String TAG = "ViewPager";
	@Inject
	ContextUtil contextUtil;
	private ViewPager vp;
	private ViewPagerAdapter vpAdapter;
	private List<ImageView> views;
	private boolean bool = false;
	private ImageView[] dots;
	private int currentIndex;
	private static final int[] pics = { R.drawable.guide_one, R.drawable.guide_two, R.drawable.guide_three, R.drawable.guide_four, R.drawable.guide_five };

	/**
	 * @Description: 跳到导航页
	 * @param context
	 * @see:
	 * @since:
	 * @description
	 * @author: sunkist
	 * @date:2014-12-10
	 */
	public static void actionNavi(Context context) {
		Intent i = new Intent(context, WebLaunchActivity.class);
		context.startActivity(i);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_web_launch);
		ToastUtil.longToast(this, "-------------------------");
		// mHandler = new Handler();
		// boolean bool =
		// ContextConfig.getInstance().getBooleanWithDefValue(GlobalConstants.AppConstact.IS_FIRST_ENTER,
		// false);
		// if (bool) {
		// User currentUser = contextUtil.getCurrentUser();
		// String ticket = contextUtil.getCurrentTicket();
		// if (currentUser != null && StringUtil.isNotEmpty(ticket)) {
		//
		// setCrashlyticesInfo(currentUser);
		// // 如果用户已登录
		// Uri uri = null;
		// if (currentUser.profileIncomplete) {
		// // 资料不完整，跳到完善资料
		// uri = GlobalConstants.IntentAction.INTENT_URI_USER_DATA;
		// } else {
		// boolean showInvitation =
		// ContextConfig.getInstance().getBooleanWithDefValue(GlobalConstants.AppConstact.SHOW_INVITATION_CODE_INPUT,
		// false);
		// if (showInvitation) {
		// // 显示邀请码界面
		// uri = GlobalConstants.IntentAction.INTENT_URI_INVITATION;
		// } else {
		// uri = GlobalConstants.IntentAction.INTENT_URI_MAIN;
		// }
		// }
		// Bundle bundle = new Bundle();
		// bundle.putString(TAG, TAG);
		// startActivity(uri, bundle);
		// finish();
		// return;
		// } else {
		// startActivityAndCleanTask(GlobalConstants.IntentAction.INTENT_URI_WELCOME, null);
		// return;
		// }
		// } else {
		// // 第一次进入
		// ContextConfig.getInstance().putBooleanWithFilename(GlobalConstants.AppConstact.IS_FIRST_ENTER,
		// true);
		// setContentView(R.layout.slidepage_navi);
		// LinearLayout.LayoutParams mParams = new
		// LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
		// LinearLayout.LayoutParams.MATCH_PARENT);
		// views = new ArrayList<ImageView>();
		// // 初始化引导图片列表
		// ImageView guideImg;
		// for (int i = 0; i < pics.length; i++) {
		// guideImg = new ImageView(this);
		// guideImg.setLayoutParams(mParams);
		// guideImg.setScaleType(ScaleType.FIT_XY);
		// guideImg.setImageBitmap(ImageUtil.readBitMap(pics[i], this));
		// views.add(guideImg);
		// }
		// vpAdapter = new ViewPagerAdapter(views, this);
		// vp = (ViewPager) findViewById(R.id.viewpager);
		// vp.setAdapter(vpAdapter);
		// vp.setOnPageChangeListener(this);
		// initDots();
		// return;
		// }
	}

	private void initDots() {
		LinearLayout ll = (LinearLayout) findViewById(R.id.ll);

		dots = new ImageView[views.size()];
		// 循环取得小点图片
		for (int i = 0; i < views.size(); i++) {
			dots[i] = (ImageView) ll.getChildAt(i);
			dots[i].setEnabled(true);// 都设为灰色
		}
		currentIndex = 0;
		dots[currentIndex].setEnabled(false);// 设置为白色，即选中状态
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		if (arg0 == views.size() - 1) {
			if (bool) {
				bool = false;
				goHome();
			} else {
				bool = true;
			}
		}
	}

	/**
	 * @Description:引导完成后,再次滑动最后一页进入应用
	 * @see:
	 * @since:
	 * @description
	 * @author: sunkist
	 * @date:2014-12-17
	 */
	private void goHome() {
		startActivityAndCleanTask(GlobalConstants.IntentAction.INTENT_URI_WELCOME, null, R.anim.left_in, R.anim.stop);
	}

	@Override
	public void onPageSelected(int arg0) {
		setCurrentDot(arg0);
	}

	private void setCurrentDot(int position) {
		if (position < 0 || position > views.size() - 1 || currentIndex == position) {
			return;
		}
		dots[position].setEnabled(false);
		dots[currentIndex].setEnabled(true);
		currentIndex = position;
	}
}
