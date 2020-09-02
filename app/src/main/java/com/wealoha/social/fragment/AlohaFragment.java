package com.wealoha.social.fragment;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.DialogInterface;
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
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.bitmap.callback.DefaultBitmapLoadCallBack;
import com.squareup.picasso.Picasso;
import com.wealoha.social.AppApplication;
import com.wealoha.social.AsyncLoader;
import com.wealoha.social.ContextConfig;
import com.wealoha.social.R;
import com.wealoha.social.activity.MainAct;
import com.wealoha.social.api.ServerApi;
import com.wealoha.social.beans.ApiResponse;
import com.wealoha.social.beans.ResultData;
import com.wealoha.social.beans.User;
import com.wealoha.social.commons.GlobalConstants;
import com.wealoha.social.commons.GlobalConstants.ImageSize;
import com.wealoha.social.commons.GlobalConstants.WhereIsComeFrom;
import com.wealoha.social.impl.Listeners;
import com.wealoha.social.utils.ContextUtil;
import com.wealoha.social.utils.FontUtil;
import com.wealoha.social.utils.FontUtil.Font;
import com.wealoha.social.utils.ImageUtil;
import com.wealoha.social.utils.ImageUtil.CropMode;
import com.wealoha.social.utils.RegionNodeUtil;
import com.wealoha.social.utils.StringUtil;
import com.wealoha.social.utils.ToastUtil;
import com.wealoha.social.utils.XL;
import com.wealoha.social.view.custom.dialog.BaseDialog;
import com.wealoha.social.view.custom.dialog.ListItemDialog;
import com.wealoha.social.view.custom.dialog.ListItemDialog.ListItemCallback;
import com.wealoha.social.view.custom.dialog.ListItemDialog.ListItemType;

/**
 * Aloha展示界面<br/>
 * <p>
 * 注意：userList是展示完再删除的，避免视图切换以后乱跳人
 *
 * @author:sunkist
 * @see:
 * @since:
 * @description
 * @copyright wealoha.com
 * @Date:2014-10-31
 */
public class AlohaFragment extends BaseFragment implements LoaderCallbacks<ApiResponse<ResultData>>, ListItemCallback {

    public static final String TAG = AlohaFragment.class.getSimpleName();
    static public final int nope = 1111;
    static public final int aloha = 2222;
    @Inject
    ServerApi mUserService;
    @InjectView(R.id.ms_report_iv_container)
    RelativeLayout mReportContainerl;
    @InjectView(R.id.nope_btn_container)
    LinearLayout mNopeContainer;
    @InjectView(R.id.aloha_btn_container)
    LinearLayout mAlohaContainer;
    @InjectView(R.id.nope_btn)
    Button mNope;
    @InjectView(R.id.aloha_btn)
    Button mAloha;
    /**
     * 点击进入相册
     */
    @InjectView(R.id.ms_des_tv)
    TextView mDes;
    @InjectView(R.id.ms_tag_tv)
    TextView mTag;
    @InjectView(R.id.textUserName)
    TextView mUsername;
    @InjectView(R.id.ms_find_tv)
    TextView mFind;
    @InjectView(R.id.ms_brith_tv)
    TextView mBrith;
    @InjectView(R.id.ms_find_woman_tv)
    TextView mFindWoman;
    @InjectView(R.id.ms_word_tv)
    TextView mWord;
    @InjectView(R.id.ms_report_iv)
    ImageView mReport;
    @InjectView(R.id.img_view)
    ImageView img;
    @InjectView(R.id.ms_head_photo_iv)
    ImageView mHeadPhoto;
    @InjectView(R.id.ms_fl)
    FrameLayout mMsFl;
    @InjectView(R.id.img_pb)
    ProgressBar img_pb;
    @Inject
    ContextUtil ContextUtil;

    @Inject
    RegionNodeUtil regionNodeUtil;
    @Inject
    ServerApi matchService;
    Context mContext;
    @Inject
    FontUtil fontUtil;
    private View view;
    public User user;
    private int mWidth;

    private final int LOADER_ALOHA = 0;
    private final int LOADER_NOPE = 1;
    /**
     * 當前手勢是點擊還是滑動
     */
    private boolean clickFlag = true;
    private boolean moveFlag = true;
    private float moveX = 0;

    private Dialog alertDialog;
    private MainAct mBaseFragAct;

    private static enum Action {
        Nope, Aloha;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // view = inflater.inflate(R.layout.frag_match_show, container, false);
        view = inflater.inflate(R.layout.frag_match_show, container, false);
        ButterKnife.inject(this, view);
        mBaseFragAct = (MainAct) getActivity();
        mContext = mBaseFragAct;
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mIsFirstNope = ContextConfig.getInstance().getBooleanWithDefValue(GlobalConstants.AppConstact.IS_FIRST_NOPE, false);
        mWidth = mBaseFragAct.mScreenWidth;
        mHeadPhoto.getLayoutParams().height = mWidth;
        mHeadPhoto.getLayoutParams().width = mWidth;
        mHeadPhoto.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float x = event.getX();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        moveX = x;
                        break;
                    case MotionEvent.ACTION_UP:
                        if (clickFlag) {
                            Bundle bundle = new Bundle();
                            bundle.putParcelable(User.TAG, user);
                            bundle.putString(WhereIsComeFrom.REFER_KEY, WhereIsComeFrom.ALOHA_TO_PROFILE);
                            mBaseFragAct.startFragmentHasAnim(Profile2Fragment.class, bundle, true, R.anim.left_in, R.anim.stop);
                        } else if (x - moveX > 300) {
                            nopeLongClick();
                        }
                        moveX = 0;
                        clickFlag = true;
                        moveFlag = true;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (x - moveX < -10 || x - moveX > 10) {
                            clickFlag = false;
                        }
                        if (!moveFlag) {
                            return true;
                        }
                        if (x - moveX < -200) {
                            Bundle bundle = new Bundle();
                            bundle.putParcelable(User.TAG, user);
                            bundle.putString("refer_key", GlobalConstants.WhereIsComeFrom.ALOHA_TO_PROFILE);
                            mBaseFragAct.startFragmentHasAnim(Profile2Fragment.class, bundle, true, R.anim.left_in, R.anim.stop);
                            moveFlag = false;
                            return true;
                        }
                }
                v.performClick();
                return true;
            }
        });
        initViewFont();
        // initGestureDetector();
    }

    @Override
    public void onResume() {

        try {
            bus.post(new Listeners.MonitorMainUiBottomTagPostion(0));
        } catch (Throwable e) {
        }
        super.onResume();
        if (AppApplication.mUserList != null && AppApplication.mUserList.size() > 0) {
            user = AppApplication.mUserList.get(0);
            displayUser();
        } else {
            // 加载更多
            if (mBaseFragAct != null) {
                mBaseFragAct.getHandler().sendEmptyMessage(MainAct.HANDLER_RELOAD);
            }
            return;
        }
        XL.i(TAG, "onresume");
    }

    /**
     * @return void 返回类型
     * @throws
     * @Title: initViewFont
     * @Description: 字体修正
     */
    private void initViewFont() {
        fontUtil.changeFonts((ViewGroup) view, Font.ENCODESANSCOMPRESSED_400_REGULAR);
        fontUtil.changeViewFont(mAloha, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
        fontUtil.changeViewFont(mNope, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
    }

    @OnClick({R.id.aloha_btn_container, R.id.aloha_btn, R.id.nope_btn_container, R.id.nope_btn, R.id.ms_report_iv, R.id.ms_report_iv_container, R.id.ms_des_tv})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nope_btn_container:
            case R.id.nope_btn:
                if (!mIsFirstNope) {
                    nopeCancel(Action.Nope);
                    return;
                } else {
                    mNope.setClickable(false);
                    nopeOrAloha(Action.Nope);
                }
                break;
            case R.id.aloha_btn_container:
            case R.id.aloha_btn:
                mAloha.setClickable(false);
                nopeOrAloha(Action.Aloha);
                break;
            case R.id.ms_report_iv_container:
            case R.id.ms_report_iv:
                openPopReport();
                break;
            case R.id.ms_des_tv:
                // 跳转到用户profile
                Bundle bundle = new Bundle();
                bundle.putParcelable(User.TAG, user);
                mBaseFragAct.startFragment(Profile2Fragment.class, bundle, true);
                break;
        }
    }

    private void openPopReport() {
        // new ReportBlackAlohaPopup().showPopup(PopupType.REPORT,
        // PostType.USER, user.getId(), User.getName(), user);
        new ListItemDialog(getActivity(), (ViewGroup) view).showListItemPopup(this, user.getName(), ListItemType.REPORT_USER);
    }

    /**
     * @Description: 举报用户
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2014-10-31
     */
    void reportUser() {
        mUserService.reportUser(user.getId(), new Callback<ApiResponse<ResultData>>() {

            @Override
            public void success(ApiResponse<ResultData> apiResponse, Response arg1) {
                if (apiResponse != null) {
                    if (apiResponse.isOk()) {
                        // 举报完同时nope掉
                        nopeOrAloha(Action.Nope);
                        // XL.d(TAG, arg0.data.toString());
                    } else {
                        ToastUtil.showCustomToast(mBaseFragAct, R.string.report_user_failure);
                    }
                }

            }

            @Override
            public void failure(RetrofitError arg0) {
            }
        });
    }

    void nopeOrAloha(Action action) {
        mIsNope = true;// 点击Nope之后,就存在了可以取消Nope的基础了.
        // 禁用点击事件
        mNope.setEnabled(false);
        mNopeContainer.setEnabled(false);
        mAloha.setEnabled(false);
        mAlohaContainer.setEnabled(false);
        if (user == null || AppApplication.mUserList.size() == 0) {
            return;
        }
        Bitmap bitmap;
        bitmap = ImageUtil.getScreenshot(view);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bitmap);
        img.setImageDrawable(bitmapDrawable);
        img.setFocusable(true);
        int animResId = 0;
        mMsFl.setVisibility(View.VISIBLE);
        Bundle bundle = new Bundle();
        bundle.putString("userId", user.getId());
        switch (action) {
            case Aloha:
                animResId = R.anim.right_out;
                // 标记需要更新
                AppApplication.isUpdate = true;
                getLoaderManager().restartLoader(LOADER_ALOHA, bundle, this);
                User tempUser = AppApplication.mUserList.remove(0);

                if (toUser != null && toUser.getId().equals(tempUser.getId())) {
                    mIsNope = false;
                    returnNextUser(null);
                } else if (toUser == null) {
                    mIsNope = false;
                }
                break;
            case Nope:
                animResId = R.anim.left_out;
                getLoaderManager().restartLoader(LOADER_NOPE, bundle, this);
                returnNextUser(AppApplication.mUserList.remove(0));
                break;
        }
        // 删除当前这个人
        // 没有更多用户了，结束
        if (AppApplication.mUserList.size() == 0) {
            // 加载更多
            if (animResId == R.anim.right_out) {
                mBaseFragAct.getHandler().sendEmptyMessage(MainAct.HANDLER_LOAD_MORE_ALOHA);
            } else {
                mBaseFragAct.getHandler().sendEmptyMessage(MainAct.HANDLER_LOAD_MORE);
            }
            return;
        }
        // 下一个人
        user = AppApplication.mUserList.get(0);
        MainAct.mLastUser = AppApplication.mUserList.get(0);
        Animation animation = AnimationUtils.loadAnimation(mBaseFragAct, animResId);
        mMsFl.setAnimation(animation);
        animation.setFillAfter(true);
        animation.setDuration(500);
        animation.setInterpolator(new AccelerateInterpolator());
        animation.setAnimationListener(new Listeners.AnimationListeners() {

            @Override
            public void onAnimationStart(Animation animation) {
                displayUser();
                mUsername.setText(user.getName());
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mExcuteOver = true;
                if (isAdded()) {
                    mNope.setClickable(true);
                    mAloha.setClickable(true);
                    mAloha.setEnabled(true);
                    mNope.setEnabled(true);
                    mAlohaContainer.setEnabled(true);
                    mNopeContainer.setEnabled(true);
                    mMsFl.setVisibility(View.GONE);
                }
            }
        });
    }

    public void openGuideDialog() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_first_aloha_time, new LinearLayout(context), false);
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
                .create();
        alertDialog.show();
    }

    /**
     * @Description:展示用户数据
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2014-10-31
     */
    void displayUser() {
        mUsername.setText("" + user.getName());
        mWord.setText(user.getSummary());

        if (user.getRegion() == null || user.getRegion().size() == 0) {
            mFind.setText(R.string.location_invisible);
        } else {
            String region = "";
            for (String r : user.getRegion()) {
                region += r + ", ";
            }
            mFind.setText(region.substring(0, region.length() - 2));
        }
        if (user.getPostCount() > 0) {
            mDes.setVisibility(View.VISIBLE);
            mDes.setText(mContext.getString(R.string.aloha_time_photo_count, String.valueOf(user.getPostCount())));
        } else {
            mDes.setVisibility(View.INVISIBLE);
        }
        mTag.setVisibility(View.VISIBLE);
        mTag.setText(mBaseFragAct.getUserTag(user.getId()) == null ? "" : mContext.getString(R.string.aloha_user_profile_tag, mBaseFragAct.getUserTag(user.getId())));
        String brith = user.getAge() + " · " + user.getHeight() + " · " + user.getWeight();
        String userTag = ContextUtil.getUserTag(user.getSelfTag());
        if (userTag != null) {
            brith += " · " + userTag;
        }

        brith += " · " + StringUtil.getUserZodiac(user.getZodiac(), mContext);
        mBrith.setText(brith);
        // 寻找
        String purposes = ContextUtil.formatPurposes(user.getSelfPurposes());
        if (!TextUtils.isEmpty(purposes)) {
            mFindWoman.setVisibility(View.VISIBLE);
            purposes = getString(R.string.seek_for, purposes);
            mFindWoman.setText(purposes);
        } else {
            mFindWoman.setVisibility(View.GONE);
        }

        int imageWidth = Math.min(ImageSize.FEED_MAX, mWidth);
        String imageUrl = ImageUtil.getImageUrl(user.getAvatarImage().getId(), imageWidth, CropMode.ScaleCenterCrop);
        Picasso.get().load(imageUrl).into(mHeadPhoto);

        if (AppApplication.mUserList.size() > 1 && AppApplication.mUserList.get(1) != null) {
            // 预加载
            User nextUser = AppApplication.mUserList.get(1);
            String nextImageUrl = ImageUtil.getImageUrl(nextUser.getAvatarImage().getId(), imageWidth, CropMode.ScaleCenterCrop);
            Picasso.get().load(nextImageUrl).fetch();
        }
    }

    private static final ColorDrawable TRANSPARENT_DRAWABLE = new ColorDrawable(Color.TRANSPARENT);

    private void fadeInDisplay(ImageView imageView, Bitmap bitmap) {
        final TransitionDrawable transitionDrawable = new TransitionDrawable(new Drawable[]{TRANSPARENT_DRAWABLE, new BitmapDrawable(imageView.getResources(), bitmap)});
        imageView.setImageDrawable(transitionDrawable);
        transitionDrawable.startTransition(500);
    }

    public class CustomBitmapLoadCallBack extends DefaultBitmapLoadCallBack<ImageView> {

        @Override
        public void onLoading(ImageView container, String uri, BitmapDisplayConfig config, long total, long current) {
            img_pb.setVisibility(View.VISIBLE);
            img_pb.setProgress((int) (current * 100 / total));
        }

        @Override
        public void onLoadCompleted(ImageView container, String uri, Bitmap bitmap, BitmapDisplayConfig config, BitmapLoadFrom from) {
            img_pb.setVisibility(View.GONE);
            fadeInDisplay(container, bitmap);
            img_pb.setProgress(100);
        }
    }

    @Override
    public Loader<ApiResponse<ResultData>> onCreateLoader(int loader, Bundle bundle) {
        final String userId = bundle.getString("userId");
        if (loader == LOADER_ALOHA) {
            return new AsyncLoader<ApiResponse<ResultData>>(mContext) {

                @Override
                public ApiResponse<ResultData> loadInBackground() {
                    try {

                        return matchService.like(userId, GlobalConstants.WhereIsComeFrom.ALOHA);
                    } catch (Exception e) {
                        XL.w(TAG, "Aloha失败", e);
                        return null;
                    }
                }
            };
        } else if (loader == LOADER_NOPE) {
            return new AsyncLoader<ApiResponse<ResultData>>(mContext) {

                @Override
                public ApiResponse<ResultData> loadInBackground() {
                    try {
                        return matchService.dislike(userId);
                    } catch (Exception e) {
                        XL.w(TAG, "Nope失败", e);
                        return null;
                    }
                }
            };
        }
        return null;
    }

    @Override
    public void onLoaderReset(Loader<ApiResponse<ResultData>> arg0) {

    }

    @Override
    public void onLoadFinished(Loader<ApiResponse<ResultData>> loader, ApiResponse<ResultData> apiResponse) {
    }

    private static User toUser;
    private Handler mHandler;
    /**
     * 全程动画是否执行完毕
     */
    public static boolean mExcuteOver = true;
    /**
     * 是否是第一次Nope
     */
    private boolean mIsFirstNope = false;
    /**
     * 是否已经不能Nope了
     */
    private boolean mIsNope = false;
    private AlertDialog areYouSureDialog;

    /**
     * @Description:返回上一个User的信息页面
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2015-1-9
     */
    public void returnNextUser(User user) {
        toUser = user;
    }

    /**
     * @Description:界面执行操作
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2015-1-9
     */
    private void returnNextFrag() {
        mHandler = mBaseFragAct.getHandler();
        Bundle bundle = null;
        if (isAdded() && toUser != null) {
            bundle = new Bundle();
            bundle.putParcelable(User.TAG, toUser);
        } else if (toUser == null) {
            bundle = null;
        }
        Message message = mBaseFragAct.sendMsgToHandler(GlobalConstants.AppConstact.RETURN_NEXT_USER_ALOHA, bundle);
        mHandler.sendMessage(message);
    }

    /**
     * @param nope2
     * @Description: 取消NOPE的逻辑
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2015-1-9
     */
    private void nopeCancel(final Action nope2) {
        if (!mIsFirstNope) {
            if (nope2 != null) {
                if (areYouSureDialog == null || !areYouSureDialog.isShowing()) {
                    View view = LayoutInflater.from(mBaseFragAct).inflate(R.layout.dialog_nope, new LinearLayout(mContext), false);
                    TextView title = (TextView) view.findViewById(R.id.reset_title);
                    TextView reset_content = (TextView) view.findViewById(R.id.reset_content);

                    fontUtil.changeViewFont(title, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
                    FontUtil.setRegulartypeFace(getActivity(), reset_content);

                    title.setText(getString(R.string.nope) + "?");
                    reset_content.setText(R.string.nope_rules);
                    reset_content.setGravity(Gravity.CENTER);
                    title.setTextSize(20);
                    title.setTextColor(mBaseFragAct.getResources().getColor(R.color.black_text));

                    areYouSureDialog = new AlertDialog.Builder(mBaseFragAct)//
                            .setView(view)//
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    nopeOrAloha(Action.Nope);
                                    ContextConfig.getInstance().putBooleanWithFilename(GlobalConstants.AppConstact.IS_FIRST_NOPE, true);
                                    mIsFirstNope = true;
                                }
                            }).create();
                    areYouSureDialog.show();
                }
                return;
            }
        } else {
            nopeOrAloha(Action.Nope);
        }
    }

    private void nopeLongClick() {
        if (mIsNope) {// 是否是进App的第一次Nope
            if (!mIsFirstNope) {// 是否是第一次Nope，是就给出
                nopeCancel(null);
            } else {
                if (mExcuteOver) {
                    mExcuteOver = false;
                    returnNextFrag();
                }
            }
        } else {
            canNotContinueNope();
        }
    }

    BaseDialog mBaseDialog;

    /**
     * @Description:不能继续撤销的提示
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2015-1-12
     */
    public void canNotContinueNope() {
        View view = LayoutInflater.from(mBaseFragAct).inflate(R.layout.dialog_nope, new LinearLayout(mContext), false);
        TextView title = (TextView) view.findViewById(R.id.reset_title);
        TextView reset_content = (TextView) view.findViewById(R.id.reset_content);
        title.setText(R.string.cant_continue_nope);
        reset_content.setVisibility(View.GONE);
        reset_content.setGravity(Gravity.CENTER);

        fontUtil.changeViewFont(title, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
        FontUtil.setRegulartypeFace(getActivity(), reset_content);

        title.setTextSize(20);
        title.setTextColor(mBaseFragAct.getResources().getColor(R.color.black_text));
        areYouSureDialog = new AlertDialog.Builder(mBaseFragAct)//
                .setView(view)//
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        areYouSureDialog.dismiss();
                    }
                }).create();
        if (!areYouSureDialog.isShowing())
            areYouSureDialog.show();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void itemCallback(int listItemType) {
        switch (listItemType) {
            case ListItemType.REPORT_USER:
                report(user.getId());
                break;

            default:
                break;
        }

    }

    /**
     * @return void 返回类型
     * @throws
     * @Title: report
     * @Description: 举报用户或FEED
     */
    private void report(String userid) {
        mUserService.reportUser(userid, null, new Callback<ApiResponse<ResultData>>() {

            @Override
            public void success(ApiResponse<ResultData> arg0, Response arg1) {
                ToastUtil.shortToast(AppApplication.getInstance(), R.string.report_inappropriate_success);
            }

            @Override
            public void failure(RetrofitError arg0) {
                ToastUtil.shortToast(AppApplication.getInstance(), R.string.network_error);
            }
        });
    }
}
