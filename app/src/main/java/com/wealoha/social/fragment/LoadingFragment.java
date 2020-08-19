package com.wealoha.social.fragment;

import java.util.Collections;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.bitmap.callback.DefaultBitmapLoadCallBack;
import com.squareup.picasso.Picasso;
import com.wealoha.social.ActivityManager;
import com.wealoha.social.AppApplication;
import com.wealoha.social.AsyncLoader;
import com.wealoha.social.BaseFragAct;
import com.wealoha.social.R;
import com.wealoha.social.activity.MainAct;
import com.wealoha.social.activity.ProFeatureAct;
import com.wealoha.social.beans.Result;
import com.wealoha.social.beans.ResultData;
import com.wealoha.social.beans.User;
import com.wealoha.social.beans.MatchData;
import com.wealoha.social.api.MatchService;
import com.wealoha.social.beans.PromotionGetData;
import com.wealoha.social.api.UserPromotionService;
import com.wealoha.social.commons.GlobalConstants;
import com.wealoha.social.impl.Listeners;
import com.wealoha.social.push.NoticeBarController;
import com.wealoha.social.utils.AMapUtil;
import com.wealoha.social.utils.AMapUtil.LocationCallback;
import com.wealoha.social.utils.ContextUtil;
import com.wealoha.social.utils.ImageUtil;
import com.wealoha.social.utils.ImageUtil.CropMode;
import com.wealoha.social.utils.UiUtils;
import com.wealoha.social.utils.XL;
import com.wealoha.social.view.custom.CircleImageView;
import com.wealoha.social.view.custom.WaterView;

/**
 * @author:sunkist
 * @see:
 * @since:
 * @description 加载数据，加载完后交给详情显示或者没有更多数据
 * @copyright wealoha.com
 * @Date:2014-10-30
 */
public class LoadingFragment extends BaseFragment implements LoaderCallbacks<com.wealoha.social.beans.Result<MatchData>> {

    private static final int LOADER_LOAD_MATCH = 0;

    @Inject
    Context context;
    @Inject
    MatchService matchService;
    @Inject
    ContextUtil contextUtil;
    @Inject
    Picasso picasso;
    @Inject
    UserPromotionService userPromotionService;
    // @InjectView(R.id.match_no_more_ll)
    // LinearLayout linearLayout;
    /* 及时界面显示 */
    // @InjectView(R.id.loading_search)
    // TextView mLoadingSearch;
    // @InjectView(R.id.photo_container)
    // RelativeLayout mPhotoContainer;

    public static final String TAG = LoadingFragment.class.getSimpleName();
    private boolean hasQuoraReset = false; // 是否可以重置配额
    @InjectView(R.id.circle1)
    WaterView circle1;
    // @InjectView(R.id.circle2)
    // MatchCircleView circle2;
    @InjectView(R.id.user_photo)
    CircleImageView userPhoto;
    @InjectView(R.id.btn_reset_quota)
    TextView mRestAloha;
    @InjectView(R.id.wait_layout)
    LinearLayout mWaitLayout;
    @InjectView(R.id.countdown_tv)
    TextView mCountdown;
    @InjectView(R.id.wait_text)
    TextView mWaitText;
    @InjectView(R.id.wait_text_01)
    TextView mWaitText01;
    @InjectView(R.id.wait_text_02)
    TextView mWaitText02;
    @InjectView(R.id.loading_container_layout)
    RelativeLayout container;
    @InjectView(R.id.net_error_tv)
    TextView mNetErrorTv;
    @InjectView(R.id.user_photo_layout)
    RelativeLayout mUserPhotoRoot;
    @InjectView(R.id.sign_icon)
    ImageView mSignIcon;

    MainAct mainAct;

    private int mBottomLocation = 0;

    private Handler mHandler = new Handler();
    private Animation exitAnim;
    private Animation fragexitAnim;
    private Dialog alertDialog;
    private Runnable endOfScheduleRunble;
    private Handler endOfScheduleHandler;
    private AMapUtil aMapUtil;
    private AppApplication appApplication;

    private static final int TIME_DOWN = 0x0001;
    private static final int OPEN_FILTER = 0x0002;
    private static final int OPEN_PRIVACY = 0x0003;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (AppApplication.mUserList != null && AppApplication.mUserList.size() > 0) {
            // 还有用户没看完，继续
            startingAloha();
        }

        aMapUtil = new AMapUtil();
        appApplication = (AppApplication) getActivity().getApplication();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_loading, container, false);
        // mPhotoContainer = (RelativeLayout)
        // view.findViewById(R.id.photo_container);
        mainAct = (MainAct) getActivity();
        ButterKnife.inject(this, view);
        Log.i("TIMER", "onCreateView:");
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setRetainInstance(true);
        userPhoto.setVisibility(View.VISIBLE);
        circle1.setVisibility(View.VISIBLE);
        // 計算居中的位置
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(UiUtils.dip2px(context, 120), UiUtils.dip2px(context, 120));
        int marginTop = (UiUtils.getScreenWidth(context) - UiUtils.dip2px(context, 120)) / 2;
        params.setMargins(marginTop, marginTop, 0, 0);
        mUserPhotoRoot.setLayoutParams(params);

        mBottomLocation = UiUtils.getScreenHeight(context) - UiUtils.getScreenWidth(context) - UiUtils.dip2px(context, 45);
        mWaitText.getLayoutParams().height = mBottomLocation;
        mWaitLayout.getLayoutParams().height = mBottomLocation;

    }

    @Override
    public void onStart() {
        super.onStart();
        // 头像缩小动画
        exitAnim = AnimationUtils.loadAnimation(context, R.anim.aloha_load_finish);
        // frag变淡动画
        fragexitAnim = AnimationUtils.loadAnimation(context, R.anim.loading_frag_out);
        loadUserPhoto();
    }

    private void loadUserPhoto() {
        if (contextUtil.getCurrentUser() != null) {
            picasso.load(ImageUtil.getImageUrl(contextUtil.getCurrentUser().getAvatarImage().getId(), UiUtils.dip2px(context, GlobalConstants.ImageSize.AVATAR_ROUND_SMALL), CropMode.ScaleCenterCrop))//
                    .placeholder(R.drawable.default_photo)//
                    .into(userPhoto);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            bus.post(new Listeners.MonitorMainUiBottomTagPostion(0));
        } catch (Throwable e) {
        }
        loadData();
    }

    public void loadData() {
        // FIXME 暂时解决倒计时闪动的问题不知道是否会带来其他问题.
        mWaitLayout.setVisibility(View.INVISIBLE);
        mWaitText.setVisibility(View.VISIBLE);
        mSignIcon.setVisibility(View.INVISIBLE);
        userPhoto.setVisibility(View.VISIBLE);

        circle1.startLoadingAnimation();
        try {
            final LoaderManager loaderManager = getLoaderManager();
            if (loaderManager != null && isAdded()) {
                final long start = System.currentTimeMillis();
                if (aMapUtil != null) {

                    aMapUtil.getLocation(context, new LocationCallback() {

                        @Override
                        public void locaSuccess() {
                            loaderManager.restartLoader(LOADER_LOAD_MATCH, null, LoadingFragment.this);
                        }

                        @Override
                        public void locaError() {
                            XL.d("resetQuotaLoad", "false");
                            XL.d("LOCATION_TIME", "TIME:" + (System.currentTimeMillis() - start));
                            loaderManager.restartLoader(LOADER_LOAD_MATCH, null, LoadingFragment.this);
                        }
                    });
                }
            }
        } catch (IllegalStateException e) {
            // 切换tab，Fragment隐藏了又调用会抛出异常，忽略
            XL.w(TAG, "加载数据时异常", e);
        } catch (NullPointerException e) {
            // 切换tab，Fragment隐藏了又调用会抛出异常，忽略
            XL.w(TAG, "加载数据时异常", e);
        }
    }

    public long random(int fromInclusive, int toExclusive) {
        Random r = new Random(System.currentTimeMillis());
        return fromInclusive + r.nextInt(toExclusive - fromInclusive);
    }

    public class CustomBitmapLoadCallBack extends DefaultBitmapLoadCallBack<ImageView> {

        @Override
        public void onLoading(ImageView container, String uri, BitmapDisplayConfig config, long total, long current) {
        }

        @Override
        public void onLoadCompleted(ImageView container, String uri, Bitmap bitmap, BitmapDisplayConfig config, BitmapLoadFrom from) {
            fadeInDisplay(container, bitmap);
        }
    }

    private static final ColorDrawable TRANSPARENT_DRAWABLE = new ColorDrawable(Color.TRANSPARENT);
    private View view;
    private MyCounter mMyCounter;
    private Timer mCountDownTimer;

    private void fadeInDisplay(ImageView imageView, Bitmap bitmap) {
        final TransitionDrawable transitionDrawable = new TransitionDrawable(new Drawable[]{TRANSPARENT_DRAWABLE, new BitmapDrawable(imageView.getResources(), ImageUtil.toRoundCorner(bitmap, 240))});
        imageView.setImageDrawable(transitionDrawable);
        transitionDrawable.startTransition(500);
    }

    /**
     * @Description: 开启及时界面
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2014-10-31
     */
    void startTimingInterface(MatchData matchData) {
        openGuideDialog();
        // FIXME 這個地方從服務器去回的剩餘時間會有跳動 就是不准 ，誤差在1分鐘以內
        // 调整布局
        mNetErrorTv.setVisibility(View.GONE);
        circle1.setVisibility(View.VISIBLE);
        mUserPhotoRoot.setVisibility(View.VISIBLE);
        circle1.stopAnimation();
        mWaitLayout.setVisibility(View.VISIBLE);
        mWaitLayout.setEnabled(true);
        mCountdown.setVisibility(View.VISIBLE);
        mWaitText.setVisibility(View.INVISIBLE);
        mRestAloha.setBackgroundResource(R.drawable.shape_aloha_filter_setting);
        Drawable clock = getResources().getDrawable(R.drawable.red_clock);
        clock.setBounds(0, 0, clock.getMinimumWidth(), clock.getMinimumHeight());
        mRestAloha.setCompoundDrawables(clock, null, null, null);
        mRestAloha.refreshDrawableState();
        mRestAloha.setText(R.string.speed_up);
        mRestAloha.setTag(TIME_DOWN);

        this.hasQuoraReset = matchData == null ? false : matchData.quotaReset > 0;

        if (matchData.error == ResultData.ERROR_MATCH_NO_MORE_TODAY) {
            // 有倒计时和加速
            sendTimingNotice(matchData);
            mCountdown.setVisibility(View.VISIBLE);
            returnLastUser();
            mCountdown.setText(formatTime(matchData.quotaResetSeconds));
            if (mCountDownTimer != null) {
                mCountDownTimer.cancel();
                mCountDownTimer = null;
            }
            mCountDownTimer = new Timer();
            mMyCounter = new MyCounter(matchData.quotaResetSeconds, false);
            mCountDownTimer.scheduleAtFixedRate(mMyCounter, 0, 1000);
        }
        // this.hasQuoraReset = matchData.quotaReset > 0;
    }

    private float moveX = 0;

    private void returnLastUser() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            boolean flag = bundle.getBoolean("bool");// 是被Nope掉还是被Aloha掉了
            if (flag)
                container.setOnTouchListener(new OnTouchListener() {

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        float x = event.getX();

                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                moveX = x;

                                break;
                            case MotionEvent.ACTION_UP:
                                if (x - moveX > 300) {
                                    if (isAdded() && GlobalConstants.AppConstact.IS_LAST_USER) {
                                        mHandler = mainAct.getHandler();
                                        Bundle bundle = new Bundle();
                                        bundle.putParcelable(User.TAG, MainAct.mLastUser);
                                        Message message = mainAct.sendMsgToHandler(GlobalConstants.AppConstact.RETURN_NEXT_USER_ALOHA, bundle);
                                        mHandler.sendMessage(message);
                                    }
                                }
                                break;
                            case MotionEvent.ACTION_MOVE:
                        }
                        v.performClick();
                        return true;
                    }
                });
        }
    }

    public void openGuideDialog() {
        if (contextUtil.getCurrentUser() != null && !contextUtil.getCurrentUser().isShowAlohaTimeDialog()) {
            View view = LayoutInflater.from(context).inflate(R.layout.dialog_first_aloha_time, new LinearLayout(context), false);
            TextView close = (TextView) view.findViewById(R.id.close_tv);
            close.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (alertDialog != null && alertDialog.isShowing()) {
                        alertDialog.dismiss();
                    }
                }
            });

            alertDialog = new AlertDialog.Builder(getActivity())//
                    .setView(view)//
                    .setCancelable(false) //
                    .create();
            alertDialog.show();

            User user = contextUtil.getCurrentUser();
            user.setShowAlohaTimeDialog(true);
            contextUtil.setCurrentUser(user);
        }

    }

    /**
     * @param matchData
     * @Description: 开启计时界面
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2014-11-25
     */
    private void sendTimingNotice(MatchData matchData) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(MatchData.TAG, matchData);
        bundle.putInt(GlobalConstants.AppConstact.ALARM_REQUEST_CODE, GlobalConstants.AppConstact.LOADING_SEND_TIMING_NOTICE);
        NoticeBarController.getInstance(mainAct).timmingSendNotice(matchData.quotaResetSeconds, bundle);
    }

    private String formatTime(long millis) {
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis - TimeUnit.HOURS.toMillis(hours));
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis - TimeUnit.HOURS.toMillis(hours) - TimeUnit.MINUTES.toMillis(minutes));
        if (seconds < 0) {
            seconds = 0;
        }
        return String.format(Locale.ENGLISH, "%02d:%02d:%02d", hours, minutes, seconds);
    }

    /**
     * 点了重置配额
     */
    private void resetQuota() {
        if (hasQuoraReset) {
            // 重置配额
            mCountDownTimer.cancel();
            int seconds = mMyCounter.getSeconds();
            mMyCounter = new MyCounter(seconds, true);
            mCountDownTimer = new Timer();
            mCountDownTimer.scheduleAtFixedRate(mMyCounter, 0, 20);
        } else {
            // 去高级设置在這裏先加載高級設置要用的數據
            // popup
            userPromotionService.get(new Callback<Result<PromotionGetData>>() {

                @Override
                public void success(Result<PromotionGetData> result, Response arg1) {
                    if (result == null) {
                        return;
                    }
                    if (result.isOk()) {
                        PromotionGetData r = (PromotionGetData) result.data;
                        contextUtil.setProfeatureEnable(!r.alohaGetLocked);
                        Intent intent = new Intent(getActivity(), ProFeatureAct.class);
                        intent.putExtra(PromotionGetData.TAG, r);
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.left_in, R.anim.stop);

                        // Bundle bundle = new Bundle();
                        // bundle.putSerializable(PromotionGetData.TAG, r);
                        // startActivity(GlobalConstants.IntentAction.INTENT_URI_ADVANCEDFEATURED, bundle);
                        // contextUtil.getMainAct().Accelerate(null, bundle);
                    }
                }

                @Override
                public void failure(RetrofitError arg0) {
                }
            });
        }
    }

    private void resetQuotaLoad() {
        if (hasQuoraReset) {// 是否可以重置配额？
            resetQuota();
            return;
        }
        if (aMapUtil != null) {
            aMapUtil.getLocation(context, new LocationCallback() {

                @Override
                public void locaSuccess() {
                    resetQuotaLoadResquest(appApplication.locationXY[0], appApplication.locationXY[1]);
                }

                @Override
                public void locaError() {
                    resetQuotaLoadResquest(appApplication.locationXY[0], appApplication.locationXY[0]);
                }
            });
        }
    }

    public void resetQuotaLoadResquest(Double latitude, Double longitude) {
        matchService.findRandom(latitude, longitude, new Callback<Result<MatchData>>() {

            @Override
            public void success(Result<MatchData> result, Response arg1) {
                hasQuoraReset = (result.data.quotaReset > 0);
                resetQuota();
            }

            @Override
            public void failure(RetrofitError arg0) {

            }
        });
    }

    public class MyCounter extends TimerTask {

        private int seconds;
        private boolean boost = false;

        public MyCounter(int seconds, boolean boost) {
            this.seconds = seconds;
            this.boost = boost;
        }

        public int getSeconds() {
            return seconds;
        }

        @Override
        public void run() {
            if (boost) {
                seconds -= Math.max(seconds / 10, 5);
            } else {
                seconds--;
            }
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    if (mCountdown != null) {
                        mCountdown.setText(formatTime(seconds * 1000));
                    }
                }
            });

            if (seconds <= 0) {
                mCountDownTimer.cancel();
                onFinish();
            }
        }

        public void onFinish() {
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    endOfSchedule();
                }
            });
        }
    }

    private void endOfSchedule() {
        circle1.startLoadingAnimation();
        mWaitLayout.setVisibility(View.INVISIBLE);
        mWaitLayout.setEnabled(false);
        mWaitText.setVisibility(View.VISIBLE);
        mWaitText01.setText(R.string.no_more_to_explore_now);
        mWaitText02.setText(R.string.next_aloha_time);
        mRestAloha.setBackgroundResource(R.drawable.shape_aloha_load);
        Drawable clockDrawble = getResources().getDrawable(R.drawable.red_clock);
        clockDrawble.setBounds(0, 0, clockDrawble.getMinimumWidth(), clockDrawble.getMinimumHeight());
        mRestAloha.setCompoundDrawables(clockDrawble, null, null, null);
        mRestAloha.setTag(TIME_DOWN);// 在点击事件中判断要执行的方法
        endOfScheduleRunble = new Runnable() {

            @Override
            public void run() {
                final Bundle bundle = new Bundle();
                bundle.putBoolean("reset", true);
                BaseFragAct baseFragAct = ActivityManager.current();
                // FIXME no attach activity
                if (isDetached())
                    return;
                final LoaderManager restartLoader = getLoaderManager();
                if (isAdded() && restartLoader != null && !((MainAct) baseFragAct).isDestory) {
                    // 获取位置，回调后拉取翻牌子数据
                    if (aMapUtil != null) {
                        aMapUtil.getLocation(context, new LocationCallback() {

                            @Override
                            public void locaSuccess() {
                                restartLoader.restartLoader(LOADER_LOAD_MATCH, bundle, LoadingFragment.this);
                            }

                            @Override
                            public void locaError() {
                                restartLoader.restartLoader(LOADER_LOAD_MATCH, bundle, LoadingFragment.this);
                            }
                        });
                    }
                }
            }
        };
        endOfScheduleHandler = new Handler();
        endOfScheduleHandler.postDelayed(endOfScheduleRunble, 2000);
    }

    @OnClick({R.id.btn_reset_quota})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_reset_quota:
                Integer btnType = (Integer) view.getTag();
                // 点了重置配额
                if (btnType == null || btnType == TIME_DOWN) {
                    resetQuotaLoad();
                } else if (btnType == OPEN_FILTER) {
                    openFilterSetting();
                } else if (btnType == OPEN_PRIVACY) {
                    openPrivacy();
                }
                // openGuideDialog();
                break;

            default:
                break;
        }
    }

    @Override
    public Loader<com.wealoha.social.beans.Result<MatchData>> onCreateLoader(int loaderId, final Bundle bundle) {
        if (!fragmentVisible) {
            XL.d(TAG, "视图不在了，忽略请求");
            return null;
        }

        final Double latitude = appApplication != null ? appApplication.locationXY[0] : null;
        final Double longitude = appApplication != null ? appApplication.locationXY[1] : null;
        if (loaderId == LOADER_LOAD_MATCH) {
            return new AsyncLoader<com.wealoha.social.beans.Result<MatchData>>(context) {

                @Override
                public com.wealoha.social.beans.Result<MatchData> loadInBackground() {
                    // 开始加载下一批数据..
                    try {
                        if (bundle != null && bundle.getBoolean("reset")) {
                            // 重置配额
                            return matchService.findWithResetQuota(latitude, longitude, true);
                        } else {
                            return matchService.findRandom(latitude, longitude);
                        }
                    } catch (Exception e) {
                        return null;
                    }
                }

            };
        }
        return null;
    }

    public void loaderRequest() {

    }

    @Override
    public void onLoaderReset(Loader<Result<MatchData>> arg0) {
    }

    public void onLoadFinished(Loader<com.wealoha.social.beans.Result<MatchData>> loader, com.wealoha.social.beans.Result<MatchData> result) {
        XL.d(TAG, "加载下一批数据完成: " + result);
        if (!isVisible()) {
            return;
        }
        if (!fragmentVisible) {
            XL.d(TAG, "视图不在了，忽略返回结果");
            return;
        }
        if (result == null) {
            // 失败了，继续显示loading
            showNetworkFucked();
            return;
        }

        if (loader.getId() == LOADER_LOAD_MATCH) {
            if (result != null) {
                if (result.isOk() && result.data.list != null && result.data.list.size() > 0) {
                    // 有下一批用户
                    AppApplication.mUserList = result.data.list;
                    XL.i("HOME_KEY", "list size:" + AppApplication.mUserList.size());
                    // 设置标签的数据
                    mainAct.setRecommendSourceMap(result.data.recommendSourceMap);
                    Collections.shuffle(AppApplication.mUserList);
                    exitAnim.setFillAfter(true);
                    fragexitAnim.setFillAfter(true);

                    container.startAnimation(fragexitAnim);
                    mUserPhotoRoot.startAnimation(exitAnim);
                    // circle1.stopAnimation();
                    startingAloha();
                } else if (result.data.error == 200518) {// 当前时段没有更多匹配了，需要重置配额或者等下个时段
                    startTimingInterface(result.data);
                    // ToastUtil.shortToast(getActivity(), "200518");
                } else if (result.data.error == 200531) {// 没有搜寻到过滤条件范围内的人
                    // ToastUtil.shortToast(getActivity(), "200531");
                    showFilterResult();
                } else if (result.data.error == 200519) {// 没有更多了
                    // ToastUtil.shortToast(getActivity(), "200519");
                    showFilterNoResult();
                } else if (result.data.error == 200532) {// 服务挂了，稍候再试
                    // ToastUtil.shortToast(getActivity(), "200532");
                    showServerFucked();
                } else {
                    showNetworkFucked();
                }
            }
        }
    }

    /**
     * 服务器挂了的ui
     *
     * @return void
     */
    private void showServerFucked() {
        // userPhoto.setVisibility(View.GONE);
        circle1.stopAnimation();
        userPhoto.setVisibility(View.GONE);
        mSignIcon.setVisibility(View.VISIBLE);
        mSignIcon.setImageResource(R.drawable.match_upgrading);
        circle1.setVisibility(View.GONE);
        mWaitLayout.setVisibility(View.VISIBLE);
        mCountdown.setVisibility(View.GONE);
        mWaitLayout.setEnabled(true);
        mWaitText.setVisibility(View.INVISIBLE);
        mWaitText01.setText(R.string.filter_result_200532_01);
        mWaitText02.setText(R.string.filter_result_200532_02);
        mRestAloha.setVisibility(View.GONE);
    }

    /**
     * 没有搜寻到过滤条件范围内的人时，显示提示界面
     */
    private void showFilterResult() {
        userPhoto.setVisibility(View.INVISIBLE);
        mSignIcon.setVisibility(View.VISIBLE);
        mSignIcon.setImageResource(R.drawable.big_search);
        circle1.stopAnimation();
        circle1.setVisibility(View.GONE);
        mWaitLayout.setVisibility(View.VISIBLE);
        mWaitLayout.setEnabled(true);
        mCountdown.setVisibility(View.GONE);
        mWaitText.setVisibility(View.INVISIBLE);
        mWaitText01.setText(R.string.filter_result_200531_01);
        mWaitText02.setText(R.string.filter_result_200531_02);
        mRestAloha.setBackgroundResource(R.drawable.shape_aloha_filter_setting);
        mRestAloha.refreshDrawableState();
        mRestAloha.setCompoundDrawables(null, null, null, null);
        mRestAloha.setText(R.string.change_setting);
        mRestAloha.setTag(OPEN_FILTER);
    }

    /**
     * 没有更多过滤条件范围内的人，显示提示界面
     */
    private void showFilterNoResult() {
        userPhoto.setVisibility(View.INVISIBLE);
        mSignIcon.setVisibility(View.VISIBLE);
        mSignIcon.setImageResource(R.drawable.big_search);
        circle1.setVisibility(View.VISIBLE);
        circle1.stopAnimation();
        mWaitLayout.setVisibility(View.VISIBLE);
        mWaitLayout.setEnabled(true);
        mCountdown.setVisibility(View.GONE);
        mWaitText.setVisibility(View.INVISIBLE);
        mWaitText01.setText(R.string.filter_result_200531_01);
        mWaitText02.setText(R.string.filter_result_200531_02);
        mRestAloha.setVisibility(View.GONE);
    }

    private void showNetworkFucked() {
        userPhoto.setVisibility(View.INVISIBLE);
        mSignIcon.setVisibility(View.VISIBLE);
        mSignIcon.setImageResource(R.drawable.match_network_disconnected);
        circle1.stopAnimation();
        circle1.setVisibility(View.GONE);
        mWaitLayout.setVisibility(View.VISIBLE);
        mWaitLayout.setEnabled(true);
        mCountdown.setVisibility(View.GONE);
        mWaitText.setVisibility(View.INVISIBLE);
        mWaitText01.setText(R.string.filter_result_network_01);
        mWaitText02.setText(R.string.filter_result_network_02);
        mRestAloha.setVisibility(View.GONE);
    }

    private void showLocationFucked() {
        userPhoto.setVisibility(View.INVISIBLE);
        mSignIcon.setVisibility(View.VISIBLE);
        mSignIcon.setImageResource(R.drawable.match_network_disconnected);
        circle1.stopAnimation();
        circle1.setVisibility(View.GONE);
        mWaitLayout.setVisibility(View.VISIBLE);
        mWaitLayout.setEnabled(true);
        mCountdown.setVisibility(View.GONE);
        mWaitText.setVisibility(View.INVISIBLE);
        mWaitText01.setText(R.string.location_service_down_result01);
        mWaitText02.setText(R.string.location_service_down_result02);
        mRestAloha.setVisibility(View.VISIBLE);
        mRestAloha.refreshDrawableState();
        mRestAloha.setCompoundDrawables(null, null, null, null);
        mRestAloha.setText(R.string.private_set_str);
        mRestAloha.setTag(OPEN_PRIVACY);
    }

    private void openFilterSetting() {
        if (mainAct != null) {
            mainAct.getUserMatchSetting();
        }
    }

    private void openPrivacy() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(GlobalConstants.IntentAction.INTENT_URI_PRIVACY);
        getActivity().startActivity(intent);
        getActivity().overridePendingTransition(R.anim.left_in, R.anim.stop);
    }

    /**
     * 加载到翻牌子数据，开启翻牌子界面
     */
    private void startingAloha() {
        if (mainAct != null) {
            // 打开Aloha界面
            mainAct.getHandler().sendEmptyMessageDelayed(GlobalConstants.AppConstact.LOADING_MATCH_DATA_SUCCESS, 750);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (endOfScheduleHandler != null && endOfScheduleRunble != null) {
            endOfScheduleHandler.removeCallbacks(endOfScheduleRunble);
        }
        // 记录计时数据
        if (mMyCounter != null) {
            mMyCounter.cancel();
        }
        circle1.stopAnimation();
        mWaitLayout.setVisibility(View.INVISIBLE);
        mWaitLayout.setEnabled(false);
        mWaitText.setVisibility(View.VISIBLE);

    }
}
