package com.wealoha.social.view.custom.listitem;

import java.util.Iterator;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import android.app.ActionBar.LayoutParams;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Picasso.LoadedFrom;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;
import com.wealoha.social.AppApplication;
import com.wealoha.social.BaseFragAct;
import com.wealoha.social.R;
import com.wealoha.social.adapter.ProfileListAdapter;
import com.wealoha.social.adapter.ProfileListAdapter.ViewType;
import com.wealoha.social.beans.Result;
import com.wealoha.social.beans.ResultData;
import com.wealoha.social.beans.User;
import com.wealoha.social.beans.match.MatchService;
import com.wealoha.social.beans.message.InboxSession;
import com.wealoha.social.beans.message.InboxSessionResult;
import com.wealoha.social.beans.message.MessageService;
import com.wealoha.social.beans.user.ProfileData;
import com.wealoha.social.beans.user.ProfileService;
import com.wealoha.social.beans.user.UserService;
import com.wealoha.social.commons.GlobalConstants;
import com.wealoha.social.commons.GlobalConstants.ImageSize;
import com.wealoha.social.fragment.ProfileTestFragment;
import com.wealoha.social.fragment.SwipeMenuListFragment;
import com.wealoha.social.inject.Injector;
import com.wealoha.social.render.BlurRendererLite;
import com.wealoha.social.utils.ContextUtil;
import com.wealoha.social.utils.FontUtil;
import com.wealoha.social.utils.FontUtil.Font;
import com.wealoha.social.utils.ImageUtil;
import com.wealoha.social.utils.ImageUtil.CropMode;
import com.wealoha.social.utils.UiUtils;
import com.wealoha.social.utils.XL;
import com.wealoha.social.view.custom.CircleImageView;

public class ProfileHeaderHolder {

    private final String TAG = getClass().getSimpleName();

    @InjectView(R.id.layout)
    ViewGroup mLayout;

    /**
     * 人气
     */
    @InjectView(R.id.profile_popularity_ll)
    LinearLayout mPopularity;
    @InjectView(R.id.profile_popularity_count_tv)
    TextView mPopCountTv;
    /**
     * 头像
     */
    @InjectView(R.id.me_circleimg_v)
    CircleImageView mUserPhoto;
    /**
     * aloha
     */
    @InjectView(R.id.profile_aloha_ll)
    LinearLayout mAloha;
    @InjectView(R.id.profile_aloha_tv)
    TextView mAlohaTv;
    @InjectView(R.id.profile_aloha_count_tv)
    TextView mAlohaCountTv;
    /**
     * 相册
     */
    @InjectView(R.id.profile_grid_pic_radio)
    CheckBox mGridPic;
    /**
     * FEED
     */
    @InjectView(R.id.profile_list_pic_radio)
    CheckBox mFeed;
    /**
     * PROFILE
     */
    @InjectView(R.id.profile_info_radio)
    CheckBox mProfile;
    /**
     * 相册
     */
    @InjectView(R.id.profile_grid_pic_radio_container)
    LinearLayout mGridPicCont;
    /**
     * FEED
     */
    @InjectView(R.id.profile_list_pic_radio_container)
    LinearLayout mFeedCont;
    /**
     * PROFILE
     */
    @InjectView(R.id.profile_info_radio_container)
    LinearLayout mProfileCont;
    /**
     * PROFILE OTHER
     */
    @InjectView(R.id.profile_other)
    LinearLayout mOther;

    /**
     * PROFILE OTHER
     */
    @InjectView(R.id.profile_other_chat)
    LinearLayout mOtherChat;
    /**
     * PROFILE OTHER
     */
    @InjectView(R.id.profile_other_chat_iv)
    ImageView mOtherChatImg;
    /**
     * PROFILE OTHER
     */
    @InjectView(R.id.profile_other_chat_tv)
    TextView mOtherChatText;

    /**
     * PROFILE OTHER
     */
    @InjectView(R.id.profile_other_match)
    LinearLayout mOtherMatch;
    /**
     * PROFILE OTHER
     */
    @InjectView(R.id.profile_other_match_iv)
    ImageView mOtherMatchImg;
    /**
     * PROFILE OTHER
     */
    @InjectView(R.id.profile_other_match_tv)
    TextView mOtherMatchText;

    /**
     * 为了调整header高度
     */
    @InjectView(R.id.profile_header_container)
    RelativeLayout mHeaderContainer;
    /**
     * 为了调整header高度
     */
    @InjectView(R.id.profile_photo_container)
    RelativeLayout mPhotoContainer;
    /**
     * RadioGroup
     */
    @InjectView(R.id.me_content_rg)
    LinearLayout mTabContainer;
    /**
     * RadioGroup
     */
    @InjectView(R.id.profile_other_pop_count)
    TextView mOtherPopCount;

    @Inject
    BlurRendererLite blurRendererLite;
    @Inject
    Picasso picasso;
    @Inject
    Context mContext;
    @Inject
    ContextUtil contextUtil;
    @Inject
    MatchService mMatchService;
    @Inject
    ProfileService mProfileService;
    @Inject
    FontUtil fontUtil;
    @Inject
    UserService mUserService;

    public User mUser;
    private boolean mIsMe;
    private ViewGroup container;
    private Bundle bundle;

    public static final int PRO_PICS_RB = R.id.profile_grid_pic_radio_container;
    public static final int PRO_FEED_RB = R.id.profile_list_pic_radio_container;
    public static final int PRO_INFO_RB = R.id.profile_info_radio_container;
    private float mBorderHeight;
    private float mHeaderAlpha;
    private Bitmap mBlurBitmap;
    private ProfileListAdapter mBaseAdapter;
    private BaseFragAct bt;
    private ProfileTestFragment mParentFrag;
    private PopupWindow popUpWindow;

    private int layoutH;
    private int layoutW;
    private boolean isUseable;
    private String whereIsComeFrom;
    private Target blurTarget; // 避免被解引用 @see
    // http://stackoverflow.com/questions/20181491/use-picasso-to-get-a-callback-with-a-bitmap

    public ProfileHeaderHolder(BaseFragAct mBaseFragAct, ProfileTestFragment parentFragment) {
        bt = mBaseFragAct;
        mParentFrag = parentFragment;

        // 从哪里进入的当前profile
        if (mParentFrag != null) {
            whereIsComeFrom = mParentFrag.whereIsComeFrom();
        }
        Injector.inject(this);
        container = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.pro_header_layout, null);
        ButterKnife.inject(this, container);

        // 字体修正
        fontUtil.changeFonts(container, Font.ENCODESANSCOMPRESSED_400_REGULAR);
        fontUtil.changeViewFont(mAlohaCountTv, Font.ENCODESANSCOMPRESSED_200_EXTRALIGHT);
        fontUtil.changeViewFont(mPopCountTv, Font.ENCODESANSCOMPRESSED_200_EXTRALIGHT);
        fontUtil.changeViewFont(mOtherMatchText, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);

    }

    /**
     * @param @return 设定文件
     * @return ViewGroup 返回类型
     * @throws
     * @Title: getView
     * @Description: 返回header布局实例
     */
    public ViewGroup getView(BaseAdapter baseAdapter, User user, boolean isMe) {
        // 默认选项
        // mGridPic.setChecked(true);
        if (user == null) {
            mUser = new User();
        }
        mUser = user;
        mIsMe = isMe;
        mBaseAdapter = (ProfileListAdapter) baseAdapter;
        initView();
        loadUserHeader();
        return container;
    }

    /**
     * @return void 返回类型
     * @throws
     * @Title: changeView
     * @Description: 改变视图（自己or别人的）
     */
    private void initView() {
        if (mIsMe) {

            mOther.setVisibility(View.GONE);
            mPopularity.setVisibility(View.VISIBLE);
            mAloha.setVisibility(View.VISIBLE);
            mOtherPopCount.setVisibility(View.GONE);

            mAlohaCountTv.setText("" + mUser.alohaCount);
            mPopCountTv.setText("" + mUser.alohaGetCount);

            // 为透明渐变效果做准备
            mBorderHeight = UiUtils.dip2px(mContext, 160);

        } else if (mUser.hasPrivacy) {
            // 被对方拉黑
            // 为透明渐变效果做准备
            mBorderHeight = UiUtils.dip2px(mContext, 160);
            mPopularity.setVisibility(View.GONE);
            mAloha.setVisibility(View.GONE);
        } else {
            // 调整header高度
            // FIXME 提取控件高度数值
            mBorderHeight = UiUtils.dip2px(mContext, 180);
            mPhotoContainer.getLayoutParams().height = UiUtils.dip2px(mContext, 240);

            mPopularity.setVisibility(View.GONE);
            mAloha.setVisibility(View.GONE);
            mOther.setVisibility(View.VISIBLE);
            mOtherPopCount.setVisibility(View.VISIBLE);
            mOtherPopCount.setText(mUser.alohaGetCount + mContext.getString(R.string.profile_aloha_get));
            // 已匹配
            if (mUser.match) {
                mOtherMatch.setVisibility(View.VISIBLE);
                mOtherChat.setVisibility(View.VISIBLE);

                mOtherMatchText.setTextSize(13);
                mOtherMatchText.setText(R.string.matched);
                mOtherMatchText.setTextColor(mContext.getResources().getColor(R.color.black_text));
                mOtherMatchImg.setImageResource(R.drawable.profile_liked);

                mOtherChatText.setText(R.string.chat);
                mOtherChatText.setTextSize(13);
                mOtherChatImg.setImageResource(R.drawable.profile_chat);
                // 已经喜欢
            } else if (mUser.aloha) {
                mOtherChat.setVisibility(View.VISIBLE);
                mOtherMatch.setVisibility(View.GONE);

                mOtherChatText.setText(R.string.liked);
                mOtherChatImg.setImageResource(R.drawable.profile_liked);

                Log.i("FONT_SIZE", "px2sp:" + UiUtils.px2sp(mContext, 100));
                Log.i("FONT_SIZE", "text size:" + mOtherChatText.getTextSize());
                // 没喜欢过aloha
            } else {
                Log.i("FONT_SIZE", "sp2px:" + UiUtils.sp2px(mContext, 13));
                mOtherChat.setVisibility(View.GONE);
                mOtherMatch.setVisibility(View.VISIBLE);

                mOtherMatchText.setTextSize(15);
                mOtherMatchText.setText("Aloha");
                mOtherMatchText.setTextColor(mContext.getResources().getColor(R.color.red));
                mOtherMatchImg.setImageResource(R.drawable.profile_heart);
            }
        }
        mPopCountTv.setText(mUser.alohaGetCount + "");

    }

    private void loadUserHeader() {
        if (picasso == null || mUser == null || mUserPhoto == null || mLayout == null) {
            return;
        }

        loadHeadCache(0);

        if (mIsMe) {
            layoutH = UiUtils.dip2px(mContext, 226);
            layoutW = UiUtils.getScreenWidth(mContext);
        } else {
            layoutH = UiUtils.dip2px(mContext, 240);
            layoutW = UiUtils.getScreenWidth(mContext);
        }
        loadBlur(mLayout);
    }

    public void loadHeadCache(int tag) {
        RequestCreator requestCreator = null;
        switch (tag) {
            case 0:
                requestCreator = picasso//
                        .load(ImageUtil.getImageUrl(mUser.avatarImage.id, UiUtils.dip2px(mContext, ImageSize.AVATAR_ROUND_SMALL), CropMode.ScaleCenterCrop));
                break;
            case 1:
                requestCreator = picasso//
                        .load(ImageUtil.getImageUrl(mUser.avatarImage.id, UiUtils.dip2px(mContext, ImageSize.AVATAR_ROUND_SMALL), CropMode.ScaleCenterCrop)).skipMemoryCache();
                break;
        }
        //
        if (requestCreator != null) {
            requestCreator.placeholder(R.drawable.default_photo).into(mUserPhoto, new com.squareup.picasso.Callback() {

                @Override
                public void onError() {
                    XL.i("USER_PHOTO", "error");
                }

                @Override
                public void onSuccess() {
                    isUseable = true;
                }

            });
        }
    }

    // 加载模糊背景的图片
    private void loadBlur(final ViewGroup layout) {
        if (mBlurBitmap != null) {
            // 避免重复加载
            return;
        }
        // FIXME 数值提取
        final String url = ImageUtil.getImageUrl(mUser.avatarImage.id, 320, CropMode.ScaleCenterCrop);
        XL.d(TAG, "头像: " + url);
        blurTarget = new Target() {

            @Override
            public void onBitmapLoaded(final Bitmap origBitmap, LoadedFrom arg1) {
                // 这里要按照比例裁切一块儿下来，避免比例不对，被拉伸了
                // int layoutH;
                // int layoutW;
                // if (mIsMe) {
                // layoutH = UiUtils.dip2px(mContext, 226);
                // layoutW = layout.getWidth();
                // } else {
                // layoutH = layout.getHeight();
                // layoutW = layout.getWidth();
                // }

                float ratioLayout = layoutH / (float) layoutW;
                float ratioBitmap = origBitmap.getHeight() / (float) origBitmap.getWidth();
                Bitmap bitmap = null;
                if (ratioLayout > ratioBitmap) {
                    // 图片高度不够，裁切左右边
                    int w = (int) Math.ceil(origBitmap.getHeight() / ratioLayout);
                    int gap = (origBitmap.getWidth() - w) / 2;
                    bitmap = Bitmap.createBitmap(origBitmap, gap, 0, w, origBitmap.getHeight());
                } else if (ratioLayout < ratioBitmap) {
                    // 图片宽度不够，裁切上下边
                    int h = (int) Math.ceil(origBitmap.getWidth() * ratioLayout);
                    int gap = (origBitmap.getHeight() - h) / 2;
                    // XL.d(TAG, "裁切: " + 0 + " " + gap + " " +
                    // origBitmap.getWidth() + " " + h);
                    bitmap = Bitmap.createBitmap(origBitmap, 0, gap, origBitmap.getWidth(), h);
                } else {
                    bitmap = origBitmap;
                }

                // XL.d(TAG, "应用Blur: " + url);
                // 先缩放和裁切，变暗，blur
                Matrix matrix = new Matrix();
                matrix.postScale(1.5f, 1.5f);
                // OOM
                // Bitmap source, int x, int y, int width, int height, Matrix m,
                // boolean filter
                Bitmap croppedBitmap = null;
                try {
                    // FIXME 放大图片
                    croppedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                } catch (Throwable e) {
                    // FIXME 如果OOM取出原图
                    croppedBitmap = bitmap;
                }
                Bitmap darkBitmap = blurRendererLite.changeBitmapContrastBrightness(croppedBitmap, 1, 0, true);
                mBlurBitmap = blurRendererLite.blurBitmap(darkBitmap, 25, true);
                if (mBlurBitmap != null) {
                    ImageUtil.drawBackground(mContext, layout, mBlurBitmap);
                }
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }

        };

        picasso.load(url).placeholder(R.color.gray_text).into(blurTarget);
    }

    @OnClick({R.id.me_circleimg_v, R.id.profile_popularity_ll, R.id.profile_aloha_ll, R.id.profile_other_match, R.id.profile_other_chat, R.id.profile_grid_pic_radio_container, R.id.profile_info_radio_container, R.id.profile_list_pic_radio_container})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.me_circleimg_v:
                // 点击头像放大
                PopupWindow popUpWindow = readyPopUpWindow();
                if (popUpWindow.isShowing()) {
                    popUpWindow.dismiss();
                } else {
                    popUpWindow.setAnimationStyle(R.style.popwindow_avactor_anim_style);
                    popUpWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
                }
                break;
            case R.id.profile_popularity_ll:
                // 人气列表
                openPopularityList();
                break;
            case R.id.profile_aloha_ll:
                // aloha过的列表
                bundle = new Bundle();
                bundle.putInt("listtype", SwipeMenuListFragment.LISTTYPE_LIKE);
                bt = (BaseFragAct) contextUtil.getForegroundAct();
                if (bt != null) {
                    bt.startFragment(SwipeMenuListFragment.class, bundle, true);
                }
                break;
            case R.id.profile_other_match:
                Log.i("PROFILE_TEST", "-----------");
                if (mUser.match) {
                    // match過，那麼就是取消aloha
                    openAreYouSureDialog();
                } else {
                    // 這時的match btn是aloha
                    aloho();
                }

                break;
            case R.id.profile_other_chat:
                if (mUser.match) {
                    getUserSessionId(mUser.id);
                } else {
                    openAreYouSureDialog();
                }
                break;
            // 三個tab切換
            case R.id.profile_grid_pic_radio_container:
                if (mGridPic.isChecked()) {
                    break;
                }
                changeViewType(ViewType.Triple);
                break;
            case R.id.profile_info_radio_container:
                if (mProfile.isChecked()) {
                    break;
                }
                changeViewType(ViewType.Profile);
                break;
            case R.id.profile_list_pic_radio_container:
                if (mFeed.isChecked()) {
                    break;
                }
                changeViewType(ViewType.Single);
                break;
        }
    }

    @Inject
    MessageService mMessageService;

    private void getUserSessionId(final String id) {
        mMessageService.getInboxSession(id, new Callback<Result<InboxSessionResult>>() {

            @Override
            public void success(Result<InboxSessionResult> result, Response arg1) {
                if (result != null && result.isOk()) {
                    if (result.data.list != null && result.data.list.size() != 0) {
                        InboxSession inboxSession = result.data.list.get(0);
                        Bundle inboxSessionBundle = new Bundle();
                        inboxSessionBundle.putString("sessionId", inboxSession.id);
                        if (contextUtil.getForegroundAct() != null) {
                            ((BaseFragAct) contextUtil.getForegroundAct()).startActivity(GlobalConstants.IntentAction.INTENT_URI_DIALOGUE, inboxSessionBundle);
                        }
                    } else {
                        mMessageService.post(id, new Callback<Result<InboxSessionResult>>() {

                            @Override
                            public void failure(RetrofitError arg0) {

                            }

                            @Override
                            public void success(Result<InboxSessionResult> arg0, Response arg1) {
                                mMessageService.post(id, new Callback<Result<InboxSessionResult>>() {

                                    @Override
                                    public void failure(RetrofitError arg0) {
                                    }

                                    @Override
                                    public void success(Result<InboxSessionResult> arg0, Response arg1) {
                                        if (arg0 != null && arg0.isOk()) {
                                            if (arg0.data.list != null && arg0.data.list.size() != 0) {
                                                InboxSession inboxSession = arg0.data.list.get(0);
                                                Bundle inboxSessionBundle = new Bundle();
                                                inboxSessionBundle.putString("sessionId", inboxSession.id);
                                                if (contextUtil.getForegroundAct() != null) {
                                                    ((BaseFragAct) contextUtil.getForegroundAct()).startActivity(GlobalConstants.IntentAction.INTENT_URI_DIALOGUE, inboxSessionBundle);
                                                }
                                            }
                                        }
                                    }
                                });
                            }
                        });
                    }
                }
            }

            @Override
            public void failure(RetrofitError arg0) {

            }
        });
    }

    public void openPopularityList() {
        bundle = new Bundle();
        bundle.putInt("listtype", SwipeMenuListFragment.LISTTYPE_POPULARITY);
        // bundle.putSerializable(UserListResult.TAG, result);
        bt = (BaseFragAct) contextUtil.getForegroundAct();
        if (bt != null) {
            bt.startFragment(SwipeMenuListFragment.class, bundle, true);
        }
    }

    public void changeViewType(ViewType viewType) {
        long time = System.currentTimeMillis();
        mBaseAdapter.changeViewType(viewType);
        Log.i("HEADER_TIME_TEST", "TIME:" + (System.currentTimeMillis() - time));
        int dividerId = 0;
        if (viewType == ViewType.Triple) {
            dividerId = R.dimen.profile_list_divider;
            mGridPic.setChecked(true);
            mProfile.setChecked(false);
            mFeed.setChecked(false);
        } else if (viewType == ViewType.Single) {
            dividerId = R.dimen.profile_list_divider_feed_offset;
            mGridPic.setChecked(false);
            mProfile.setChecked(false);
            mFeed.setChecked(true);
        } else if (viewType == ViewType.Profile) {
            dividerId = R.dimen.profile_list_divider_normal;
            mGridPic.setChecked(false);
            mProfile.setChecked(true);
            mFeed.setChecked(false);
        } else if (viewType == ViewType.Black) {
            // 在对方的黑名单中
            dividerId = R.dimen.profile_list_divider_feed;
            mGridPic.setChecked(false);
            mProfile.setChecked(false);
            mFeed.setChecked(false);
            mGridPic.setClickable(false);
            mGridPicCont.setClickable(false);
            mProfile.setClickable(false);
            mProfileCont.setClickable(false);
            mFeed.setClickable(false);
            mFeedCont.setClickable(false);

        } else {
            // 以防万一
            dividerId = R.dimen.profile_list_divider_feed;
        }

        mBaseAdapter.changeListDividerHeight((int) (mContext.getResources().getDimension(dividerId)));
    }

    private Dialog alertDialog;

    private AlertDialog areYouSureDialog;

    private void openAreYouSureDialog() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_sms_reset, new LinearLayout(mContext), false);
        TextView title = (TextView) view.findViewById(R.id.reset_title);
        title.setText(mContext.getResources().getString(R.string.unAloha));
        title.setTextSize(20);
        title.setTextColor(mContext.getResources().getColor(R.color.black_text));
        Log.i("OPENAREYOUSUREDIALOG", contextUtil.getForegroundAct() + "--------");
        areYouSureDialog = new AlertDialog.Builder(contextUtil.getForegroundAct())//
                .setView(view)//
                .setPositiveButton(mContext.getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dislike();
                        areYouSureDialog.dismiss();
                    }
                }).setNegativeButton(mContext.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (areYouSureDialog != null && areYouSureDialog.isShowing()) {
                            areYouSureDialog.dismiss();
                        }
                    }
                }).create();
        areYouSureDialog.show();
    }

    private void dislike() {
        mUserService.dislikeUser(mUser.id, new Callback<Result<ResultData>>() {

            @Override
            public void success(Result<ResultData> arg0, Response arg1) {
                XL.i("ALOHA_ALOHA", "dislike user:success");
                // 刷新数据
                // ((RefreshData) mFrag).refreshData();
                // popup.dismiss();
                refreshHeader(mUser.id);
                if (mParentFrag != null) {
                    mParentFrag.setResultForUnaloha(mUser.id);
                }
            }

            @Override
            public void failure(RetrofitError arg0) {
                XL.i("ALOHA_ALOHA", "dislike user:failure");
            }
        });
    }

    private void aloho() {
        mMatchService.like(mUser.id, whereIsComeFrom, new Callback<Result<ResultData>>() {

            @Override
            public void success(Result<ResultData> arg0, Response arg1) {
                refreshHeader(mUser.id);
                if (AppApplication.mUserList != null && AppApplication.mUserList.size() > 0) {
                    for (User user : AppApplication.mUserList) {
                        if (user.id.equals(mUser.id)) {
                            AppApplication.mUserList.remove(user);
                            break;
                        }
                    }
                }
            }

            @Override
            public void failure(RetrofitError arg0) {
            }
        });

        // 过滤掉Aloha列表里的
        if (AppApplication.mUserList != null && AppApplication.mUserList.size() > 0) {
            Iterator<User> it = AppApplication.mUserList.iterator();
            while (it.hasNext()) {
                User u = it.next();
                if (mUser.id.equals(u.id)) {
                    it.remove();
                    break;
                }
            }
        }
    }

    /**
     * @return void 返回类型
     * @throws
     * @Title: refreshHeader
     * @Description: 刷新header
     */
    public void refreshHeader(String userid) {
        mProfileService.view(userid, new Callback<Result<ProfileData>>() {

            @Override
            public void success(Result<ProfileData> result, Response arg1) {
                if (result != null && result.isOk()) {
                    mUser = result.data.user;
                    if (mUser.me) {
                        contextUtil.setCurrentUser(mUser);
                    }
                    initView();
                }

            }

            @Override
            public void failure(RetrofitError arg0) {
                XL.i(TAG, "get user profile:failure");
            }
        });
    }

    /**
     * @return void 返回类型
     * @throws
     * @Title: refreshHeader
     * @Description: 刷新header, 除了头像
     */
    public void refreshExceptIcon(User user) {
        mUser = user;
        initView();
    }

    public void refreshIcon(String userid) {
        mProfileService.view(userid, new Callback<Result<ProfileData>>() {

            @Override
            public void success(Result<ProfileData> result, Response arg1) {
                if (result == null || !result.isOk()) {
                    return;
                }
                XL.i("PROFILE_HEADER_HOLDER", "NEW:" + (mUser.avatarImage.id.equals(result.data.user.avatarImage.id)));
                if (!mUser.avatarImage.id.equals(result.data.user.avatarImage.id) || !isUseable) {
                    mUser = result.data.user;
                    isUseable = false;
                    loadUserHeader();
                }
            }

            @Override
            public void failure(RetrofitError arg0) {

            }
        });
        // mUserService.
    }

    /**
     * @return void 返回类型
     * @throws
     * @Title: refreshHeader
     * @Description: 刷新header
     */
    public void refresh(User user) {
        refreshExceptIcon(user);
        mBlurBitmap = null;
        loadUserHeader();
    }

    public void refreshHeaderByCurrent() {
        if (contextUtil.getCurrentUser() != null) {
            mUser = contextUtil.getCurrentUser();
            refreshHeader(mUser.id);
        }
    }

    public void setCurrentView(ViewType currentview) {
        if (currentview == ViewType.Single) {
            mFeed.setChecked(true);
            mGridPic.setChecked(false);
            mProfile.setChecked(false);
        } else if (currentview == ViewType.Triple) {
            mFeed.setChecked(false);
            mGridPic.setChecked(true);
            mProfile.setChecked(false);
        } else if (currentview == ViewType.Profile) {
            mFeed.setChecked(false);
            mGridPic.setChecked(false);
            mProfile.setChecked(true);
        }
    }

    /**
     * @param @param alohaType 设定文件
     * @return void 返回类型
     * @throws
     * @Title: changeAlohaView
     * @Description: 改变aloha的ui状态
     */
    public void changeAlohaView(String id) {
        refreshHeader(id);
    }

    private PopupWindow readyPopUpWindow() {
        View popwin_layout = LayoutInflater.from(mContext).inflate(R.layout.popwin_avactor, new LinearLayout(mContext), false);
        ImageView avactor = (ImageView) popwin_layout.findViewById(R.id.pop_imgview);
        LinearLayout ll = (LinearLayout) popwin_layout.findViewById(R.id.container_layout);
        popUpWindow = new PopupWindow(popwin_layout);
        popUpWindow.setOutsideTouchable(true);
        popUpWindow.setFocusable(true);
        popUpWindow.setBackgroundDrawable(new ColorDrawable());
        popUpWindow.setTouchable(true);
        popUpWindow.setWidth(LayoutParams.MATCH_PARENT);
        popUpWindow.setHeight(LayoutParams.MATCH_PARENT);
        String url = ImageUtil.getImageUrl(mUser.avatarImage.id, 640, CropMode.ScaleCenterCrop);
        // FIXME 应该先显示小图,大图家在完毕显示大图

        picasso.load(url).into(avactor);
        ll.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (popUpWindow.isShowing()) {
                    popUpWindow.dismiss();
                }
            }
        });
        return popUpWindow;
    }

    public User getuUser() {
        return this.mUser;
    }

    public float headerAlpha() {
        if (container != null) {
            mHeaderAlpha = container.getY() / -mBorderHeight;
            if (container.getY() > -mBorderHeight) {
                mHeaderAlpha = mHeaderAlpha > 1 ? 1 : mHeaderAlpha;
            } else {
                mHeaderAlpha = 1;
            }

            mHeaderContainer.setAlpha(1 - mHeaderAlpha);
            mOther.setAlpha(1 - mHeaderAlpha);
            mTabContainer.setAlpha(1 - mHeaderAlpha);

            return mHeaderAlpha;
        }
        return 0;
    }

}
