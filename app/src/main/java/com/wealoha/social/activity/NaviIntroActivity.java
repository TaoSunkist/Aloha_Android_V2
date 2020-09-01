package com.wealoha.social.activity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import androidx.viewpager.widget.ViewPager;

import com.squareup.picasso.Picasso;
import com.wealoha.social.BaseFragAct;
import com.wealoha.social.ContextConfig;
import com.wealoha.social.R;
import com.wealoha.social.adapter.ViewPagerAdapter;
import com.wealoha.social.api.ServerApi;
import com.wealoha.social.api.UserService;
import com.wealoha.social.beans.ApiResponse;
import com.wealoha.social.beans.ResultData;
import com.wealoha.social.beans.User;
import com.wealoha.social.api.ConstantsService;
import com.wealoha.social.commons.GlobalConstants;
import com.wealoha.social.ui.lock.GestureLockAct;
import com.wealoha.social.utils.ContextUtil;
import com.wealoha.social.utils.GuidUtil;
import com.wealoha.social.utils.ImageUtil;
import com.wealoha.social.utils.StringUtil;

/**
 * @author:sunkist
 * @see:
 * @since:
 * @description新手导航
 * @copyright wealoha.com
 * @Date:2014-12-10
 */
public class NaviIntroActivity extends BaseFragAct implements ViewPager.OnPageChangeListener {

    private static final String TAG = "ViewPager";
    @Inject
    ContextUtil contextUtil;
    @Inject
    ConstantsService constantsService;
    @Inject
    Picasso picasso;
    @Inject
    ServerApi mUserAPI;
    @Inject
    UserService mUserService;

    private ViewPager vp;
    private ViewPagerAdapter vpAdapter;
    private List<ImageView> views;
    private boolean bool = false;
    private ImageView[] dots;
    private int currentIndex;
    public Handler mHandler;
    private static final int[] pics = {R.drawable.guide_one, R.drawable.guide_two, R.drawable.guide_three, R.drawable.guide_four, R.drawable.guide_five};

    private int launchImgRequstCode = 0x0001;
    private int lockViewRequstCode = 0x0002;

    /**
     * @param context
     * @Description: 跳到导航页
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2014-12-10
     */
    public static void actionNavi(Context context) {
        Intent i = new Intent(context, NaviIntroActivity.class);
        context.startActivity(i);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // fetchUpdateAndStartup();
        mHandler = new Handler();
        boolean bool = ContextConfig.getInstance().getBooleanWithDefValue(GlobalConstants.AppConstact.IS_FIRST_ENTER, false);
        if (bool) {
            User currentUser = contextUtil.getCurrentUser();
            String ticket = contextUtil.getCurrentTicket();
            if (currentUser != null && StringUtil.isNotEmpty(ticket)) {

                setCrashlyticesInfo(currentUser);
                // 如果用户已登录
                Uri uri = null;
                // -----------------test------------------
                // uri = GlobalConstants.IntentAction.INTENT_URI_USER_DATA;
                // Bundle bundletest = new Bundle();
                // bundletest.putString(TAG, TAG);
                // startActivity(uri, bundletest);
                // finish();
                // -------------------------------------
                if (currentUser.getProfileIncomplete()) {
                    // 资料不完整，跳到完善资料
                    uri = GlobalConstants.IntentAction.INTENT_URI_USER_DATA;
                    startActivityAndCleanTask(uri, null);
                } else {
                    boolean showInvitation = ContextConfig.getInstance().getBooleanWithDefValue(GlobalConstants.AppConstact.SHOW_INVITATION_CODE_INPUT, false);
                    if (showInvitation) {
                        // 显示邀请码界面
                        uri = GlobalConstants.IntentAction.INTENT_URI_INVITATION;
                        Bundle bundle = new Bundle();
                        bundle.putString(TAG, TAG);
                        startActivity(uri, bundle);
                        finish();
                    } else {
                        uri = GlobalConstants.IntentAction.INTENT_URI_MAIN;
                        mUserService.getProfeatureSetting();// 获取高级界面设置信息
                    }
                }
                // Bundle bundle = new Bundle();
                // bundle.putString(TAG, TAG);
                // startActivity(uri, bundle);

                if (uri == GlobalConstants.IntentAction.INTENT_URI_MAIN) {
                    Intent intent = null;
                    if (checkLockEnable()) {// 是否进入手势解锁页面
                        intent = new Intent(NaviIntroActivity.this, GestureLockAct.class);
                        intent.putExtra(GestureLockAct.LOCK_TYPE, GestureLockAct.LOCK_UNLOCK);
                        startActivityForResult(intent, lockViewRequstCode);
                    } else {
                        intent = new Intent(NaviIntroActivity.this, LauncherImgAct.class);
                        // startActivity(intent);
                        startActivityForResult(intent, launchImgRequstCode);
                    }
                }
                // finish();
                return;
            } else {
                startActivityAndCleanTask(GlobalConstants.IntentAction.INTENT_URI_WELCOME, null);
                return;
            }
        } else {
            // 第一次进入
            ContextConfig.getInstance().putBooleanWithFilename(GlobalConstants.AppConstact.IS_FIRST_ENTER, true);
            setContentView(R.layout.slidepage_navi);
            LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            views = new ArrayList<ImageView>();
            // 初始化引导图片列表
            ImageView guideImg;
            for (int i = 0; i < pics.length; i++) {
                guideImg = new ImageView(this);
                guideImg.setLayoutParams(mParams);
                guideImg.setScaleType(ScaleType.FIT_XY);
                guideImg.setImageBitmap(ImageUtil.readBitMap(pics[i], this));
                views.add(guideImg);
            }
            vpAdapter = new ViewPagerAdapter(views, this);
            vp = (ViewPager) findViewById(R.id.viewpager);
            vp.setAdapter(vpAdapter);
            vp.setOnPageChangeListener(this);
            initDots();
            return;
        }
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
        mUserAPI.startLand(new GuidUtil().getGuid(), new Callback<ApiResponse<ResultData>>() {

            @Override
            public void success(ApiResponse<ResultData> arg0, Response arg1) {
            }

            @Override
            public void failure(RetrofitError arg0) {
            }
        });
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

    @Override
    protected void onActivityResult(int requestcode, int resultcode, Intent result) {
        // super.onActivityResult(requestcode, resultcode, result);
        if (resultcode == RESULT_OK) {
            if (requestcode == launchImgRequstCode) {
                openMainAct();
                finish();
            } else if (requestcode == lockViewRequstCode) {
                if (checkLaunchImagePage()) {
                    Intent intent = new Intent(this, LauncherImgAct.class);
                    startActivityForResult(intent, launchImgRequstCode);
                } else {
                    openMainAct();
                }
            }
        } else {
            finish();
        }
    }

    private void openMainAct() {
        Intent intent = new Intent(this, MainAct.class);
        intent.putExtra(TAG, TAG);
        startActivity(intent);
    }

}
