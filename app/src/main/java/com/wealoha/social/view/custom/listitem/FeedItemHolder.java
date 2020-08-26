package com.wealoha.social.view.custom.listitem;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import com.squareup.picasso.Picasso;
import com.wealoha.social.BaseFragAct;
import com.wealoha.social.R;
import com.wealoha.social.activity.GDMapAct;
import com.wealoha.social.adapter.ProfileListAdapter;
import com.wealoha.social.api.ServerApi;
import com.wealoha.social.beans.Feed;
import com.wealoha.social.beans.ApiResponse;
import com.wealoha.social.beans.ResultData;
import com.wealoha.social.beans.User;
import com.wealoha.social.beans.feed.UserTags;
import com.wealoha.social.commons.GlobalConstants;
import com.wealoha.social.commons.GlobalConstants.ImageSize;
import com.wealoha.social.fragment.FeedFragment;
import com.wealoha.social.fragment.Profile2Fragment;
import com.wealoha.social.fragment.SwipeMenuListFragment;
import com.wealoha.social.inject.Injector;
import com.wealoha.social.utils.ContextUtil;
import com.wealoha.social.utils.FontUtil;
import com.wealoha.social.utils.FontUtil.Font;
import com.wealoha.social.utils.ImageUtil;
import com.wealoha.social.utils.ImageUtil.CropMode;
import com.wealoha.social.utils.NetworkUtil;
import com.wealoha.social.utils.OnClickListenerUtil;
import com.wealoha.social.utils.TimeUtil;
import com.wealoha.social.utils.ToastUtil;
import com.wealoha.social.utils.UiUtils;
import com.wealoha.social.utils.XL;
import com.wealoha.social.view.custom.CircleImageView;
import com.wealoha.social.view.custom.dialog.ListItemDialog;
import com.wealoha.social.view.custom.dialog.ListItemDialog.ListItemCallback;
import com.wealoha.social.view.custom.dialog.ListItemDialog.ListItemType;
import com.wealoha.social.view.custom.popup.AtOnePopup;

public class FeedItemHolder implements OnClickListener, ListItemCallback {

    public static final int RESULT_LEAVE_COMMENT = 10;

    private String TAG = getClass().getSimpleName();

    @Inject
    Context context;

    @Inject
    Picasso picasso;

    @Inject
    ContextUtil contextUtil;

    @Inject
    FontUtil fontUtil;
    @Inject
    ServerApi mFeedService;

    @InjectView(R.id.item_feed_load_error_rl)
    RelativeLayout mFeedLoadErrorRl;

    /**
     * 小头像
     */
    @InjectView(R.id.item_feed_head_icon)
    CircleImageView mUserPhoto;

    /**
     * 大照片
     */
    @InjectView(R.id.item_feed_first_photo)
    public ImageView mFeedImage;
    @InjectView(R.id.item_feed_first_photo_rl)
    RelativeLayout mWrapFeedPhoto;
    /**
     * 昵称
     */
    @InjectView(R.id.item_feed_nickname)
    TextView mUsername;
    /**
     * 位置
     */
    @InjectView(R.id.item_feed_location)
    TextView mLocation;

    /**
     * 赞
     */
    @InjectView(R.id.item_feed_praise)
    LinearLayout mPraiseContainer;

    @InjectView(R.id.item_feed_load_error)
    ImageView mFeedImgLoadError;

    /**
     * 赞
     */
    @InjectView(R.id.item_feed_praise_tv)
    TextView mPraise;

    /**
     * 赞
     */
    @InjectView(R.id.item_feed_praise_iv)
    ImageView mPraiseImg;

    /**
     * 留言
     */
    @InjectView(R.id.item_feed_leave_a_message_tv)
    TextView mMessage;

    @InjectView(R.id.item_feed_leave_a_message)
    LinearLayout item_feed_leave_a_message;

    /**
     * 更多
     */
    @InjectView(R.id.item_feed_more)
    LinearLayout item_feed_more;

    @InjectView(R.id.item_feed_more_tv)
    TextView mMore;

    /**
     * 发表时间
     */
    @InjectView(R.id.item_feed_dispaly_time)
    TextView mTime;
    SurfaceView mPreview;
    /**
     * 赞图
     */
    @InjectView(R.id.item_feed_parise_iv)
    ImageView mPraiseImage;

    /**
     * 描述
     */
    @InjectView(R.id.item_feed_introduction)
    TextView mIntroductionTv;

    /**
     * 描述
     */
    @InjectView(R.id.item_feed_praise_list)
    TextView mPraiseList;

    /**
     * 最上面的线
     */
    @InjectView(R.id.item_feed_top_line)
    View mTopLine;

    /**
     * 赞列表最下面的线
     */
    @InjectView(R.id.item_feed_praise_list_line)
    View mPraiseLine;

    /**
     * list item间隔
     */
    @InjectView(R.id.item_feed_interval)
    View mFeedInterval;
    @InjectView(R.id.item_feed_first_photo_container)
    RelativeLayout mFeedPhotoContainer;
    @InjectView(R.id.method_container)
    LinearLayout mMethodContainer;
    @InjectView(R.id.item_feed_tags_visibility)
    ImageView mTagsVisibIv;
    private ViewGroup mContainer;

    private User mUser;

    private Feed mFeed;

    private Map<String, Integer> mLikedCountMap;

    private int mCommentCount;

    private int mFeedItemType;

    private List<AtOnePopup> atOnesList;
    /**
     * 有没有自己的tag
     */
    // private boolean tagMe;
    // /** 举报等功能的弹出框 */
    // private ListItemDialog mItemDialog;

    // 评论数都放入集合，用于渲染视图
    public static Map<String, Integer> commentMap;

    static {
        commentMap = new HashMap<String, Integer>();
    }

    public static void clearPraiseMap() {
        commentMap.clear();
    }

    private BaseAdapter baseAdapter;

    public static class FeedItemHandler extends Handler {

        public WeakReference<FeedItemHolder> holder;

        public FeedItemHandler(FeedItemHolder fiHolder) {
            holder = new WeakReference<FeedItemHolder>(fiHolder);
        }
    }

    FeedItemHandler handler = new FeedItemHandler(this);

    public FeedItemHolder(BaseAdapter baseAdapter, ViewGroup rootView) {
        long t = System.currentTimeMillis();
        Injector.inject(this);
        this.mContainer = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.item_feed, rootView, false);
        ButterKnife.inject(this, mContainer);

        this.baseAdapter = baseAdapter;
        // 举报等功能的弹出框
        // mItemDialog = new ListItemDialog(context, mContainer);
        // 初始化图片容器尺寸
        mWrapFeedPhoto.getLayoutParams().width = UiUtils.getScreenWidth(context);
        mWrapFeedPhoto.getLayoutParams().height = UiUtils.getScreenWidth(context);
        // 初始化字体
        fontUtil.changeFonts(mContainer, Font.ENCODESANSCOMPRESSED_400_REGULAR);
        fontUtil.changeViewFont(mUsername, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
        fontUtil.changeViewFont(mTime, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
        XL.d(TAG, "初始化时间: " + (System.currentTimeMillis() - t));
    }

    private int getLikeCount() {
        Integer cnt = mLikedCountMap.get(mFeed.postId);
        if (cnt == null) {
            cnt = 0;
        }
        return cnt;
    }

    private void incLikeCount() {
        mLikedCountMap.put(mFeed.postId, getLikeCount() + 1);
    }

    private void decLikeCount() {
        mLikedCountMap.put(mFeed.postId, getLikeCount() - 1);
    }

    private MediaPlayer mMediaPlayer;
    private SurfaceHolder holder;

    public ViewGroup getView(User user, Feed feed, int commentCount, Map<String, Integer> likeCountMap, int feedItemType) {
        if (mContainer == null) {
            return null;
        }

        this.mUser = user;
        this.mFeed = feed;
        this.mCommentCount = commentCount;
        this.mLikedCountMap = likeCountMap;
        this.mFeedItemType = feedItemType;

        if ("VideoPost".equals(this.mFeed.type)) {

        }

        initData();
        return mContainer;
    }

    private void initData() {
        String imageId = mFeed.imageId;
        int feedWidth = Math.min(ImageSize.FEED_MAX, UiUtils.getScreenWidth(context));
        loadFeedImg(imageId, feedWidth);
        // Log.i("FEED_IMAGE", "image width:" +
        // mFeedImage.getDrawingCache().getWidth());
        String upperName = null;
        if (!TextUtils.isEmpty(mUser.getName())) {
            upperName = mUser.getName().toUpperCase(Locale.getDefault());
        }

        // 地理位置
        if (!TextUtils.isEmpty(mFeed.venue)) {
            mLocation.setVisibility(View.VISIBLE);
            mLocation.setText(mFeed.venue);
        } else {
            mLocation.setVisibility(View.GONE);
            mLocation.setText("");
        }
        // mLocation.setVisibility(View.VISIBLE);
        // mLocation.setText("999999");
        mUsername.setText(upperName);
        mTime.setText(TimeUtil.getDistanceTimeForApp(context, new Date().getTime(), mFeed.createTimeMillis));
        picasso.load(ImageUtil.getImageUrl(mUser.getAvatarImage().getId(), GlobalConstants.ImageSize.AVATAR_ROUND_SMALL, CropMode.ScaleCenterCrop)).noFade().into(mUserPhoto);
        mUserPhoto.setOnClickListener(this);
        mMore.setOnClickListener(this);

        // 评论
        if (commentMap.containsKey(mFeed.postId)) {
            mMessage.setText(commentMap.get(mFeed.postId) + "");
        } else {
            commentMap.put(mFeed.postId, mCommentCount);
            mMessage.setText(mCommentCount + "");
        }

        if (Integer.parseInt(mMessage.getText().toString()) == 0) {
            mMessage.setText(R.string.leave_comment);
        }

        mMessage.setOnClickListener(this);

        showLikeBar();

        // 描述
        if (!TextUtils.isEmpty(mFeed.description)) {
            mIntroductionTv.setVisibility(View.VISIBLE);
            mIntroductionTv.setText(mFeed.description);
        } else {
            mIntroductionTv.setVisibility(View.GONE);
        }
        // 赞动画
        mPraise.setOnClickListener(this);
        clearTags();
        initDoubleClick(mFeedImage);
        initTagSwitchBtn();

        // notify 进入的feed， 隐藏赞 留言 那一栏
        if (mFeedItemType == FeedFragment.FEED_TYPE_TAGS) {
            mMethodContainer.setVisibility(View.GONE);
            mPraiseList.setVisibility(View.GONE);

            mTagsVisibIv.setVisibility(View.GONE);
            handler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    initAtOneCurrentUserPopup();
                }
            }, 300);

        }
    }

    AlphaAnimation mAlphaAnim;

    /**
     * tag 是否显示的开关， 在第一次初始化后5秒渐渐消失
     */
    private Runnable tagSwitchRunable = new Runnable() {

        @Override
        public void run() {
            mAlphaAnim = new AlphaAnimation(1f, 0f);
            mAlphaAnim.setDuration(500);
            mAlphaAnim.setAnimationListener(new AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mTagsVisibIv.setVisibility(View.GONE);
                }
            });
            mTagsVisibIv.startAnimation(mAlphaAnim);
        }
    };

    /**
     * @Title: initTagSwitchBtn
     * @Description: 初始化显示tag 的开关控件
     */
    public void initTagSwitchBtn() {
        clearTagSwitchAnimation();
        // Tags
        if (mFeed.userTags != null && mFeed.userTags.size() > 0) {
            // initTagContainerTransitionAnim(mFeedPhotoContainer);
            mTagsVisibIv.setVisibility(View.VISIBLE);
            mTagsVisibIv.postDelayed(tagSwitchRunable, 5000);
        } else {
            mTagsVisibIv.setVisibility(View.GONE);
        }
    }

    /**
     * @Title: clearTagSwitchAnimation
     * @Description: 移除tag 开关的动画
     */
    public void clearTagSwitchAnimation() {
        mTagsVisibIv.removeCallbacks(tagSwitchRunable);
        mTagsVisibIv.clearAnimation();
    }

    /**
     * @Title: initTagContainerTransitionAnim
     * @Description: 设置tag 消失的动画 ，在{@link #initTagSwitchBtn()} 中调用
     * @param tagContainer
     *            设定文件
     */
    // private void initTagContainerTransitionAnim(ViewGroup tagContainer) {
    // LayoutTransition layoutTran = new LayoutTransition();
    // layoutTran.setDuration(100);
    // layoutTran.setAnimator(LayoutTransition.APPEARING,
    // layoutTran.getAnimator(LayoutTransition.APPEARING));
    // layoutTran.setAnimator(LayoutTransition.CHANGE_APPEARING,
    // layoutTran.getAnimator(LayoutTransition.CHANGE_APPEARING));
    // tagContainer.setLayoutTransition(layoutTran);
    // }

    /**
     * @param view 设定文件
     * @Title: initDoubleClick
     * @Description: 是否初始化双击和单击事件
     */
    private void initDoubleClick(View view) {
        if (mFeedItemType != FeedFragment.FEED_TYPE_TAGS) {
            view.setOnClickListener(new OnClickListenerUtil(300) {

                @Override
                public void OnDoubleClickEvent(View v) {
                    // ToastUtil.longToast(context, "DOUBLE_CLICK");
                    praise();
                }

                @Override
                public void OnClickEvent(View v) {
                    // 这个item 没有tag，所以屏蔽单击事件
                    if (mFeed.userTags == null || mFeed.userTags.size() <= 0) {
                        return;
                    }
                    // 先移除消失动画事件
                    clearTagSwitchAnimation();
                    if (atOnesList == null || atOnesList.size() <= 0) {
                        // tag 开关一起显示
                        mTagsVisibIv.setVisibility(View.VISIBLE);
                        initAtOnePopup();
                    } else {
                        // tag 开关一起隐藏
                        mTagsVisibIv.setVisibility(View.INVISIBLE);
                        clearTags();
                    }
                }
            });
        }
    }

    /**
     * @return void 返回类型
     * @throws
     * @Title: initAtOnePopup
     * @Description: 清空旧pop ，生成新pop
     */
    private void initAtOnePopup() {
        List<UserTags> userTags = mFeed.userTags;
        if (userTags != null && userTags.size() >= 0) {
            for (int i = 0; i < userTags.size(); i++) {
                createAtOnePopup(userTags.get(i));
            }
        }
    }

    /***
     * 只渲染当前用户的tag
     *
     * @return void
     */
    private void initAtOneCurrentUserPopup() {
        List<UserTags> userTags = mFeed.userTags;
        if (userTags != null && userTags.size() >= 0) {
            UserTags userTag;
            for (int i = 0; i < userTags.size(); i++) {
                userTag = userTags.get(i);
                if (userTag.tagMe) {
                    createAtOnePopup(userTag);
                    break;
                }
            }
        }
    }

    /**
     * @param userTag 设定文件
     * @Title: createAtOnePopup
     * @Description: 创建tag 控件
     */
    private void createAtOnePopup(UserTags userTag) {
        if (atOnesList == null) {
            atOnesList = new ArrayList<AtOnePopup>();
        }
        AtOnePopup atOne = new AtOnePopup(context, mFeedPhotoContainer, userTag);
        atOnesList.add(atOne);
        atOne.initAtPopup(false, null);
    }

    /**
     * @Title: clearTags
     * @Description: 清理圈人
     */
    private void clearTags() {
        if (atOnesList != null && atOnesList.size() > 0) {
            for (int j = 0; j < atOnesList.size(); j++) {
                atOnesList.get(j).closePopupByGone();
            }
            atOnesList.clear();
        }
    }

    private void clearMyTags() {
        if (atOnesList != null && atOnesList.size() > 0) {
            XL.i("CLEAR_MY_TAGS", "size:" + atOnesList.size());
            for (int j = 0; j < atOnesList.size(); j++) {
                if (atOnesList.get(j).getPopupInfo().tagMe) {
                    atOnesList.get(j).closePopup();
                }
            }
            // 显示 tag 的开关控件消失
            if (atOnesList.size() <= 0) {
                mTagsVisibIv.setVisibility(View.GONE);
            }
        }
    }

    /**
     * @param imageId
     * @param feedWidth
     * @Description:加载feed大图
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2015-1-7
     */
    private void loadFeedImg(final String imageId, final int feedWidth) {
        // final ViewGroup.LayoutParams layoutParams =
        // mFeedImage.getLayoutParams();
        mFeedImage.setScaleType(ScaleType.FIT_CENTER);
        picasso.load(ImageUtil.getImageUrl(imageId, feedWidth, CropMode.ScaleCenterCrop))//
                .placeholder(R.color.gray_text)//
                .into(mFeedImage, new com.squareup.picasso.Callback() {

                    @Override
                    public void onError() {
                        mFeedLoadErrorRl.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, mFeedImage.getWidth()));
                        mFeedLoadErrorRl.setVisibility(View.VISIBLE);
                        mFeedImgLoadError.setVisibility(View.VISIBLE);
                        mFeedLoadErrorRl.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                if (NetworkUtil.isNetworkAvailable()) {
                                    loadFeedImg(imageId, feedWidth);
                                } else {
                                    ToastUtil.shortToast(context, R.string.please_check_your_network);
                                }
                            }
                        });
                    }

                    @Override
                    public void onSuccess() {
                        mFeedImgLoadError.setVisibility(View.GONE);
                        mFeedLoadErrorRl.setVisibility(View.GONE);
                        mFeedImage.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void showLikeBar() {
        // 赞
        changeColor(mFeed.isLiked());
        if (getLikeCount() == 0) {
            // 没有赞过，显示『赞』
            mPraise.setText(R.string.praise);
        } else {
            // 显示赞的数量
            mPraise.setText(Integer.toString(getLikeCount()));
        }

        boolean mine = mFeed.isMine();
        if (!mine) {
            // 看不到別人的
            mPraiseList.setVisibility(View.GONE);
            mPraiseLine.setVisibility(View.GONE);
            return;
        }

        if (getLikeCount() == 0) {
            // 没人赞过
            mPraiseList.setVisibility(View.GONE);
            mPraiseLine.setVisibility(View.GONE);
        } else if (!mFeed.isLiked()) {
            // 只有别人赞了
            // xx 個人說贊
            mPraiseList.setText(context.getString(R.string.feed_like_n_people_like, String.valueOf(getLikeCount())));
            mPraiseList.setVisibility(View.VISIBLE);
            mPraiseLine.setVisibility(View.VISIBLE);
        } else if (getLikeCount() == 1 && mFeed.isLiked()) {
            // 只有自己贊了
            mPraiseList.setText(R.string.feed_like_only_you_like);
            mPraiseList.setVisibility(View.VISIBLE);
            mPraiseLine.setVisibility(View.VISIBLE);
        } else {
            // 你和別人贊了
            mPraiseList.setText(context.getString(R.string.feed_like_you_and_n_people_like, String.valueOf(getLikeCount() - 1)));
            mPraiseList.setVisibility(View.VISIBLE);
            mPraiseLine.setVisibility(View.VISIBLE);
        }
    }

    /**
     * @param @param v
     * @param @param img 设定文件
     * @return void 返回类型
     * @throws
     * @Title: praise
     * @Description: 赞的逻辑判断
     */
    private void praise() {
        // int praise = 0;
        // 点赞
        if (!mFeed.isLiked()) {
            // 点赞动画和字体变色
            mPraiseImage.startAnimation(AnimationUtils.loadAnimation(context, R.anim.feed_like));
            // 這裡先改一下即時反饋，最後調用返回會再改
            changeColor(true);
            // 显示赞的数量
            mPraise.setText(Integer.toString(getLikeCount() + 1));
            // 填充赞数到渲染视图的map
            praiseFeed();
            // 消除赞
        } else {
            // 這裡先改一下即時反饋，最後調用返回會再改
            changeColor(false);
            dislikeFeed();
        }
    }

    /**
     * @param @param v
     * @param @param flag 设定文件
     * @return void 返回类型
     * @throws
     * @Title: changeColor
     * @Description: 赞的颜色控制
     */
    private void changeColor(boolean flag) {
        int praiseImg;
        int praiseTxt;
        if (flag) {
            praiseImg = R.drawable.feed_like_r;
            praiseTxt = context.getResources().getColor(R.color.light_red);
        } else {
            praiseImg = R.drawable.feed_like;
            praiseTxt = context.getResources().getColor(R.color.medium_gray_text);
        }
        XL.i(TAG, "praise color:" + praiseTxt + "---" + android.R.color.darker_gray + "---" + android.R.color.holo_red_light);
        mPraiseImg.setImageResource(praiseImg);
        mPraise.setTextColor(praiseTxt);
    }

    @OnClick({//
            R.id.item_feed_more,//
            R.id.item_feed_praise,//
            R.id.item_feed_location,//
            R.id.item_feed_nickname,//
            R.id.item_feed_head_icon,//
            R.id.item_feed_praise_list,//
            // R.id.item_feed_first_photo, //
            R.id.item_feed_tags_visibility,//
            R.id.item_feed_leave_a_message,//
            R.id.item_feed_leave_a_message_iv,//
            R.id.item_feed_leave_a_message_tv,//
            R.id.item_feed_more_iv,//
            R.id.item_feed_more_tv //
    })
    @Override
    public void onClick(View v) {
        Bundle bundle = null;
        switch (v.getId()) {
            case R.id.item_feed_praise:
                praise();
                break;
            case R.id.item_feed_head_icon:
            case R.id.item_feed_nickname:
                bundle = new Bundle();
                bundle.putParcelable(User.TAG, mUser);
                if (contextUtil.getForegroundAct() != null) {
                    ((BaseFragAct) contextUtil.getForegroundAct()).startFragment(Profile2Fragment.class, bundle, true);
                }
                break;
            case R.id.item_feed_leave_a_message_iv:
            case R.id.item_feed_leave_a_message_tv:
            case R.id.item_feed_leave_a_message:
                bundle = new Bundle();
                bundle.putParcelable(Feed.TAG, mFeed);
                if (contextUtil.getForegroundAct() != null) {
                    ((BaseFragAct) contextUtil.getForegroundAct()).startActivity(GlobalConstants.IntentAction.INTENT_URI_LEAVE_COMMENT, bundle);
                }

                break;
            case R.id.item_feed_more:
            case R.id.item_feed_more_iv:
            case R.id.item_feed_more_tv:
                createListItemDialog();
                break;
            case R.id.item_feed_praise_list:
                if (mUser.getMe()) {
                    bundle = new Bundle();
                    bundle.putInt("listtype", SwipeMenuListFragment.LISTTYPE_PRAISE);
                    bundle.putInt("feedLikeCount", getLikeCount());
                    bundle.putString("postId", mFeed.postId);
                    if (contextUtil.getForegroundAct() != null) {
                        ((BaseFragAct) contextUtil.getForegroundAct()).startFragment(SwipeMenuListFragment.class, bundle, true);
                    }
                }
                break;
            case R.id.item_feed_location:
                Intent intent = new Intent(contextUtil.getForegroundAct(), GDMapAct.class);
                intent.putExtra("latitude", mFeed.latitude);
                intent.putExtra("longitude", mFeed.longitude);
                intent.putExtra("userphoto", mUser.getAvatarImageId());
                intent.putExtra("venueAbroad", mFeed.venueAbroad);
                contextUtil.getForegroundAct().startActivity(intent);
                break;
            case R.id.item_feed_tags_visibility:
                clearTagSwitchAnimation();
                if (atOnesList == null || atOnesList.size() <= 0) {
                    mTagsVisibIv.setVisibility(View.VISIBLE);
                    initAtOnePopup();
                } else {
                    mTagsVisibIv.setVisibility(View.INVISIBLE);
                    clearTags();
                }

                break;
            default:
                break;
        }
    }

    /**
     * @Title: createListItemDialog
     * @Description: 组装举报等功能的dialog，一个模块中可能同时需要几个功能 itemType的数组的大小就为几， 遇到值为 0
     * 的元素dialog会停止继续组装新的item
     */
    private void createListItemDialog() {
        // 这个模块中最多有两种不同的item 同时出现在dialog 上，所以数组大小为2
        int[] itemType = new int[2];
        int index = 0;
        String title = null;
        if (mFeed.tagMe != null && mFeed.tagMe) {
            itemType[index++] = ListItemType.DELETE_TAG_ITEM;
        }
        if (mUser.getMe()) {
            itemType[index++] = ListItemType.DELETE_FEED_ITEM;
            title = mUser.getName();
        } else {
            itemType[index++] = ListItemType.REPORT_FEED_ITEM;
            title = context.getResources().getString(R.string.report_inappropriate_content);
        }
        new ListItemDialog(contextUtil.getForegroundAct(), mContainer).showListItemPopup(this, title, itemType);
    }

    private void praiseFeed() {
        mFeedService.praiseFeed(mFeed.postId, new Callback<ApiResponse<ResultData>>() {

            @Override
            public void success(ApiResponse<ResultData> apiResponse, Response arg1) {
                // 视图状态
                // praise();
                XL.i(TAG, "赞成");
                if (!mFeed.isLiked()) {
                    mFeed.setLiked(true);
                    incLikeCount();
                }
                showLikeBar();
            }

            @Override
            public void failure(RetrofitError arg0) {
                Toast.makeText(context, R.string.Unkown_Error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void dislikeFeed() {
        mFeedService.dislikeFeed(mFeed.postId, new Callback<ApiResponse<ResultData>>() {

            @Override
            public void success(ApiResponse<ResultData> apiResponse, Response arg1) {
                XL.i(TAG, "没赞成");
                if (mFeed.isLiked()) {
                    mFeed.setLiked(false);
                    decLikeCount();
                }
                showLikeBar();
            }

            @Override
            public void failure(RetrofitError arg0) {
                Toast.makeText(context, R.string.Unkown_Error, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * <p>
     * Title: itemCallback
     * </p>
     * <p>
     * Description: listItemDialog 的回调函数，包括举报，删除等等操作的回调
     * </p>
     *
     * @param listItemType
     * @see com.wealoha.social.view.custom.dialog.ListItemDialog.ListItemCallback#itemCallback(int)
     */
    @Override
    public void itemCallback(int listItemType) {
        switch (listItemType) {
            case ListItemType.DELETE_FEED_ITEM:
                deleteFeed();
                break;
            case ListItemType.REPORT_FEED_ITEM:
                report();
                break;
            case ListItemType.DELETE_TAG_ITEM:
                removeMyTag();
                break;

            default:
                break;
        }
    }

    private void removeMyTag() {
        clearMyTags();
        ArrayList<UserTags> userTags = (ArrayList<UserTags>) mFeed.userTags;
        if (userTags == null) {
            return;
        }
        for (int i = 0; i < userTags.size(); i++) {
            if (userTags.get(i).tagMe) {
                userTags.remove(i);
                i--;
            }
        }
        if (contextUtil.getCurrentUser() != null) {
            mFeedService.removeTag(mFeed.postId, contextUtil.getCurrentUser().getId(), new Callback<ResultData>() {

                @Override
                public void success(ResultData result, Response arg1) {
                    mFeed.tagMe = false;
                }

                @Override
                public void failure(RetrofitError arg0) {
                    ToastUtil.longToast(context, R.string.network_error);
                }
            });
        }
        // initAtOnePopup();

    }

    /**
     * @Title: deleteFeed
     * @Description: 刪除feed
     */
    private void deleteFeed() {
        mFeedService.deleteFeed(mFeed.postId, new Callback<ApiResponse<ResultData>>() {

            @Override
            public void success(ApiResponse<ResultData> arg0, Response arg1) {
                // 更新feed list
                ((ProfileListAdapter) baseAdapter).notifyFeedDataSetChanged(mFeed.postId);
            }

            @Override
            public void failure(RetrofitError arg0) {
                XL.i("ALOHA_ALOHA", "delete feed:failure");
            }
        });
    }

    private void report() {
        mFeedService.reportFeed(mFeed.postId, null, null, new Callback<ApiResponse<ResultData>>() {

            @Override
            public void success(ApiResponse<ResultData> arg0, Response arg1) {
                ToastUtil.longToast(context, R.string.report_inappropriate_success);
            }

            @Override
            public void failure(RetrofitError arg0) {
            }
        });
    }

}
