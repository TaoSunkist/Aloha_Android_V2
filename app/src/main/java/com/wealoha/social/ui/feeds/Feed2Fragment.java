package com.wealoha.social.ui.feeds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.InjectView;
import butterknife.OnClick;

import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.wealoha.social.BaseFragAct;
import com.wealoha.social.R;
import com.wealoha.social.activity.MainAct;
import com.wealoha.social.adapter.feed.BaseFeedHolder;
import com.wealoha.social.adapter.feed.Feed2Adapter;
import com.wealoha.social.adapter.feed.FeedHolder;
import com.wealoha.social.adapter.feed.VideoFeedHolder;
import com.wealoha.social.api.common.ApiErrorCode;
import com.wealoha.social.api.Feed2Service;
import com.wealoha.social.beans.HashTag;
import com.wealoha.social.commons.GlobalConstants;
import com.wealoha.social.event.feed.MediaPlayEvent;
import com.wealoha.social.fragment.BaseFragment;
import com.wealoha.social.presenters.FeedsPresenter;
import com.wealoha.social.push.notification.NotificationCount;
import com.wealoha.social.utils.ContextUtil;
import com.wealoha.social.utils.FontUtil.Font;
import com.wealoha.social.utils.NetworkUtil;
import com.wealoha.social.utils.UiUtils;
import com.wealoha.social.utils.XL;
import com.wealoha.social.view.custom.dialog.ListItemDialog;
import com.wealoha.social.view.custom.dialog.ListItemDialog.ListItemType;
import com.wealoha.social.widget.BaseListApiAdapter.LoadCallback;

public class Feed2Fragment extends BaseFragment implements IFeedsView {

    @Inject
    Feed2Service feedService;
    @Inject
    ContextUtil contextUtil;
    private View mRootView;
    private Feed2Adapter feedAdapter;
    private View listFooterView;
    private ProgressBar loadProgress;
    private TextView reloadImg;
    private FrameLayout headerRoot;
    /**
     * 进入feed后是否执行加载数据
     */
    private boolean isLoad = true;
    private final static int PAGE_COUNT = 20;
    /**
     * 开启这个fragment 的组件指定frag是否刷新
     */
    public final static String IS_REFRESH_KEY = Feed2Fragment.class.getSimpleName() + "_IS_REFRESH_KEY";

    public final static int HANDLER_KEY_SUCCESS = 1;
    public final static int HANDLER_KEY_FAIL = 2;
    public final static int HANDLER_KEY_DATASTATE = 3;
    public static final int OPEN_NOTIFY_REQUESTCODE = 1003;

    @InjectView(R.id.feed_listview)
    ListView mListView;
    @InjectView(R.id.refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @InjectView(R.id.start_upload)
    ImageView mStartUpload;
    @InjectView(R.id.notify_root)
    RelativeLayout mNotifyRoot;
    @InjectView(R.id.no_feed_cover)
    RelativeLayout mNullFeedCover;
    /**
     * 显示新通知提醒的数目控件
     */
    @InjectView(R.id.notify_subscript)
    TextView mNotifySub;
    @InjectView(R.id.feed_title)
    TextView mTitleText;
    @SuppressLint("UseSparseArrays")
    public static Map<Integer, View> views = new HashMap<Integer, View>();
    FeedsPresenter mFeedsP;
    @Inject
    Picasso mPicasso;
    private static List<HashTag> hashtag = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.frag_feed2, container, false);
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFeedsP = new FeedsPresenter(getActivity(), this);
        initHeadView();
        mSwipeRefreshLayout.setOnRefreshListener(this);// 下拉刷新
        mSwipeRefreshLayout.setColorSchemeResources(R.color.light_red);
        initListFooter();
        if (feedAdapter == null) {
            feedAdapter = new Feed2Adapter(this, feedService);
        }
        if (hashtag != null && hashtag.size() > 0) {
            showTopic(hashtag);
        } else {
            mFeedsP.showHeadView();
        }
        fontUtil.changeViewFont(mTitleText, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
        mListView.setAdapter(feedAdapter);
        mListView.setTextFilterEnabled(true);
        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mListView.setItemChecked(0, true);
        mListView.setCacheColorHint(Color.TRANSPARENT);
        mListView.setOnScrollListener(this);
        if (isLoad || getArguments().getBoolean(IS_REFRESH_KEY, false)) {// 数据为空
            loadNextPage(true);
        }
        mNotifyRoot.setOnTouchListener(this);// 进入通知的按钮
        mTitleText.setOnTouchListener(this);
        // 从通知栏启动时
        if ("auto_open_notify".equals(getArguments().getString("notificationbar"))) {
            openNotifyAct();
        }
    }

    /***
     * listview 底部的loading 控件, 注意，4.4以前 addFooter要在setAdapter前执行
     */
    private void initListFooter() {
        listFooterView = LayoutInflater.from(context).inflate(R.layout.list_loader_footer, new ListView(context), false);
        loadProgress = (ProgressBar) listFooterView.findViewById(R.id.reload_progress);
        reloadImg = (TextView) listFooterView.findViewById(R.id.reload_img);
        // FontUtil.setSemiBoldTypeFace(getActivity(), reloadImg);
        fontUtil.changeViewFont(reloadImg, Font.ENCODESANSCOMPRESSED_500_MEDIUM);
        headerRoot = (FrameLayout) listFooterView.findViewById(R.id.listview_footer_container);
        mListView.addFooterView(listFooterView);
        reloadImg.setOnClickListener(new OnClickListener() {// 重载数据

            @Override
            public void onClick(View v) {
                loadNextPage(false);
            }
        });
        changeFooterView(true);
    }

    /***
     * 渲染不同状态下 liseview 的loading控件
     *
     * @param hasMore 如果是true,那么是正在加载状态，false则是重新加载,如果是空值，那么把load控件移除
     * @return void
     */
    private void changeFooterView(Boolean hasMore) {
        if (!isVisible() || headerRoot.getVisibility() == View.GONE) {
            return;
        }
        XL.i(TAG, "hasMore:" + hasMore);
        if (hasMore == null) {
            headerRoot.setVisibility(View.VISIBLE);
            loadProgress.setVisibility(View.GONE);
            reloadImg.setVisibility(View.VISIBLE);
        } else if (hasMore) {
            headerRoot.setVisibility(View.VISIBLE);
            loadProgress.setVisibility(View.VISIBLE);
            reloadImg.setVisibility(View.GONE);
        } else {
            headerRoot.setVisibility(View.GONE);
            // mListView.removeFooterView(listFooterView);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    /***
     * 加载下一页
     *
     * @param reset 是否重置数据
     * @return void
     */
    protected void loadNextPage(boolean reset) {
        if (NetworkUtil.isNetworkAvailable()) {
            XL.i(TAG, "net work");
            mSwipeRefreshLayout.setRefreshing(false);
            changeFooterView(true);
        } else {
            changeFooterView(null);
            XL.i(TAG, "not work");
        }
        if (reset) {
            feedAdapter.resetStateDelay();// 防止重置数据的时候 ，数据全部清空导致的视图空白
        }

        feedAdapter.loadEarlyPage(PAGE_COUNT, gerCurrentUserId(), new LoadCallback() {

            @Override
            public void success(boolean hasEarly, boolean hasLate) {
                if (isVisible()) {
                    closeRefreshView();
                    setAdapter();
                    changeFooterView(hasEarly);
                }
                XL.i(TAG, "success");
            }

            @Override
            public void fail(ApiErrorCode code, Exception exception) {
                if (isVisible()) {
                    changeFooterView(null);
                }
                XL.i(TAG, "fail");
            }

            @Override
            public void dataState(int size, boolean isnull) {
                if (isVisible()) {
                    openFeedCover(size, isnull);
                }
            }
        });
    }

    /***
     * post 数目为0时，feed的引导层
     *
     * @param size
     * @param isnull
     * @return void
     */
    private void openFeedCover(int size, boolean isnull) {
        // if (mNullFeedCover == null || feedAdapter == null) {
        // return;
        // }
        isLoad = (isnull || size == 0);
        if (isLoad) {
            mNullFeedCover.setVisibility(View.VISIBLE);
            feedAdapter.resetState();// 因为服务器没有数据，所以下一次刷新的时候应该将cursor重置，否则不会向服务器请求数据
        } else {
            mNullFeedCover.setVisibility(View.GONE);
        }
    }

    /***
     * listview set adapter,如果listview 已经有adapter那么就放弃
     */
    private void setAdapter() {
        if (mListView == null || feedAdapter == null || mListView.getAdapter() != null) {
            return;
        }
        mListView.setAdapter(feedAdapter);
    }

    /***
     * 获取当前用户的id，用于判断当前feed 是否有当前用户的标签
     *
     * @return String 用户id
     * @see {@link }
     */
    private String gerCurrentUserId() {
        String currentId = null;
        if (contextUtil.getCurrentUser() != null) {
            currentId = contextUtil.getCurrentUser().getId();// 当前用户的userid，用来判feed
            // 是否有自己的标签
        }
        return currentId;
    }

    /***
     * 关闭下拉刷新的loading视图
     */
    public void closeRefreshView() {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    /***
     * 下拉刷新
     *
     * @return void
     */
    @Override
    public void onRefresh() {
        clearMainTabdDot();
        stopVideo();
        loadNextPage(true);
        if (isAdded()) {
            mFeedsP.showHeadView();
        }
    }

    /**
     * @param
     * @return void 返回类型
     * @throws
     * @Title: listViewScrollTop
     * @Description: 双击标题返回顶部并刷新
     */
    public void listViewScrollTop() {
        if (mListView != null) {
            if (mListView.getLastVisiblePosition() == 1 && mListView.getChildCount() < 3) {
                // refreshLayout.setRefreshing(true);
                // onRefresh();
                // return;
                mListView.smoothScrollToPosition(0);
                mSwipeRefreshLayout.setRefreshing(true);
                onRefresh();
            } else if (mListView.getLastVisiblePosition() < 50) {
                mListView.smoothScrollToPosition(0);
                mSwipeRefreshLayout.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        if (mSwipeRefreshLayout != null) {
                            mSwipeRefreshLayout.setRefreshing(true);
                            onRefresh();
                        }
                    }
                }, 1000);
            } else if (mListView.getLastVisiblePosition() > 49) {
                mListView.setSelection(0);
                mSwipeRefreshLayout.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        if (mSwipeRefreshLayout != null) {
                            mSwipeRefreshLayout.setRefreshing(true);
                            onRefresh();
                        }
                    }
                }, 500);
            }
        }
    }

    @Override
    public void itemCallback(int listItemType) {
        switch (listItemType) {
            case ListItemType.CAMERA:
                if (getActivity() instanceof MainAct) {
                    MainAct baseFragAct = (MainAct) getActivity();
                    baseFragAct.openCamera(baseFragAct);
                }
                break;
            case ListItemType.CHOSE_PHOTOS:
                if (getActivity() instanceof MainAct) {
                    MainAct baseFragAct = (MainAct) getActivity();
                    baseFragAct.openImgPick(baseFragAct);
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void onActivityResultCallback(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        XL.i("COMMENT_ACT_CALLBACK", "REQUESTCODE---:" + requestCode);
        XL.i("COMMENT_ACT_CALLBACK", "RESULTCODE----:" + resultCode);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case GlobalConstants.AppConstact.OPEN_COMPOSE_FEED:// 发 feed 后刷新feed
                listViewScrollTop();
                break;
            case FeedHolder.START_FEEDCOMMENT_REQUESTCODE:
                if (frag2HolderCallback != null && data != null) {
                    frag2HolderCallback.commentCallback(data.getIntExtra(FeedHolder.COMMENT_COUNT_DATA_KEY, 0));
                }
                break;
            case OPEN_NOTIFY_REQUESTCODE:
                onRefresh();
                break;
            default:
                break;
        }
    }

    private Object ControlMedia = new Object() {

        @Subscribe
        public void onEvent(MediaPlayEvent mediaPlayEvent) {

        }
    };

    public class StartPlayMedia {

        @Subscribe
        public void controlMedia() {

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            bus.register(ControlMedia);
        } catch (Throwable e) {
        }
        refreshNotifyCount(NotificationCount.getCommentCount());// 更新通知数
        clearMainTabdDot();
    }

    /***
     * 清除主界面上的红点
     */
    private void clearMainTabdDot() {
        if (getActivity() instanceof MainAct) {// 隐藏主界面的通知数
            ((MainAct) getActivity()).newFeedTagDotCleared();
            ((MainAct) getActivity()).newNotifyCountCleared();
        }
    }

    /***
     * 刷新新的通知数目，如果
     *
     * @param count 通知的数目，如果count 大于0，那么显示通知视图及数量，反之则不显示
     */
    public void refreshNotifyCount(int count) {
        if (mNotifySub != null) {
            if (count > 0) {
                mNotifySub.setVisibility(View.VISIBLE);
            } else {
                mNotifySub.setVisibility(View.INVISIBLE);
            }
            mNotifySub.setText(String.valueOf(count));
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_UP) {
            return false;
        }
        switch (v.getId()) {
            case R.id.notify_root:
                openNotifyAct();
                return true;
            case R.id.feed_title:
                listViewScrollTop();
                return true;
            default:
                break;
        }
        v.performClick();
        return false;
    }

    /***
     * 开启通知界面
     *
     * @return void
     */
    private void openNotifyAct() {
        setNewNoticeSub(0, "openNotifyAct");
        // 打开通知界面
        ((BaseFragAct) getActivity()).startActivityForResult(GlobalConstants.IntentAction.INTENT_URI_FEED_NOTICE,//
                null, 0, 0, OPEN_NOTIFY_REQUESTCODE);
    }

    @Override
    public void onStop() {
        if (mVideoFeedHolder != null) {
            checkItemStopPlay();
        }
        try {
            bus.unregister(ControlMedia);
        } catch (Throwable e) {
        }
        super.onStop();
    }

    public void setNewNoticeSub(int newNoticeCount, String form) {
        XL.i("XM_PUSH_BROADCAST_TEST", "setNewNoticeSub:" + newNoticeCount);
        XL.i("XM_PUSH_BROADCAST_TEST", "from:" + form);

        if (newNoticeCount > 0) {
            mNotifySub.setVisibility(View.VISIBLE);
            mNotifySub.setText(newNoticeCount > 99 ? "···" : newNoticeCount + "");
        } else {
            mNotifySub.setVisibility(View.GONE);
        }
    }

    private int visibleItemCount;

    private int mTemtVisibleItem;
    private int mFirstVisibleItem;
    private int mTotalItemCount;

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_IDLE) {
            // 手指擡起的時候開始遍歷當前所有顯示的Item，包括ImageHolder
            checkItemStartPlay();
        }

        if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && feedService != null) {
            boolean direction = (mFirstVisibleItem - mTemtVisibleItem) >= 0;
            feedService.fetchPhoto(mFirstVisibleItem, mTotalItemCount, direction, UiUtils.getScreenWidth(getActivity().getApplicationContext()));
            mTemtVisibleItem = mFirstVisibleItem;
        }
    }

    /**
     * @Description:遍历显示的所有顯示的Item中可以被播放的Item
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2015年4月7日
     */
    private void checkItemStartPlay() {
        for (int i = 0; i < visibleItemCount; i++) {
            View listItem = mListView.getChildAt(i);
            // 获取当前的Holder
            if (listItem.getTag() != null && listItem.getTag() instanceof BaseFeedHolder) {
                BaseFeedHolder baseFeedHolder = (BaseFeedHolder) listItem.getTag();
                FeedHolder feedHolder = (FeedHolder) baseFeedHolder;
                // 此处的条件：
                if (feedHolder != null && feedHolder.getContentHolder().isPlayer()) {
                    mVideoFeedHolder = (VideoFeedHolder) feedHolder.getContentHolder();
                    VideoFeedHolder videoFeedHolder = (VideoFeedHolder) mVideoFeedHolder;
                    videoFeedHolder.startMediaPlayer();
                    break;
                }
            } else {
                continue;
            }

        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.visibleItemCount = visibleItemCount;
        checkItemStopPlay();
        // 预加载
        if (firstVisibleItem + visibleItemCount + 3 == totalItemCount) {
            loadNextPage(false);
        }

        mTotalItemCount = totalItemCount;
        mFirstVisibleItem = firstVisibleItem;
    }

    BaseFeedHolder mVideoFeedHolder;
    private View mHeadView;
    private RelativeLayout mAdvertWrap;
    private RelativeLayout mTopicWrap;

    /***
     * 启动播放器
     *
     * @return void
     */
    private void checkItemStopPlay() {
        if (mVideoFeedHolder != null && !mVideoFeedHolder.isPlayer()) {
            VideoFeedHolder videoFeedHolder = (VideoFeedHolder) mVideoFeedHolder;
            videoFeedHolder.stopPlayer();
            mVideoFeedHolder = null;
        }
    }

    private void stopVideo() {
        if (mVideoFeedHolder != null) {
            ((VideoFeedHolder) mVideoFeedHolder).stopPlayer();
        }
    }

    @OnClick({R.id.start_upload})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_upload:
                new ListItemDialog(getActivity(), (ViewGroup) mRootView)//
                        .showListItemPopup(this, null, ListItemType.CAMERA, ListItemType.CHOSE_PHOTOS);
                break;
            default:
                break;
        }
    }

    private void initHeadView() {
        mHeadView = View.inflate(getActivity(), R.layout.item_feed_header, null);
        mAdvertWrap = (RelativeLayout) mHeadView.//
                findViewById(R.id.show_advertisement_wrap_rl);
        mTopicWrap = (RelativeLayout) mHeadView.//
                findViewById(R.id.show_topic_wrap_rl);
        mListView.addHeaderView(mHeadView);
    }

    // 控制头部显示逻辑的代码.
    public void isShowHeadWidget(int topicId, int advertId) {
        mAdvertWrap.setVisibility(advertId == mAdvertWrap.getId() ? View.VISIBLE : View.GONE);
        mTopicWrap.setVisibility(topicId == mTopicWrap.getId() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showAdvert(boolean isSuccess) {
        isShowHeadWidget(0, mAdvertWrap.getId());
        ImageView advertImg = (ImageView) mAdvertWrap.findViewById(R.id.advertisement_img_iv);
        ImageView closeAdvert = (ImageView) mAdvertWrap.findViewById(R.id.close_advert_iv);
        closeAdvert.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });
        advertImg.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("tagname", "");
                startActivityHasAnim(GlobalConstants.IntentAction.INTENT_URI_TOPIC,//
                        bundle, R.anim.left_in, R.anim.stop);
            }
        });
    }

    @Override
    public void showTopic(List<HashTag> t) {
        if (isAdded()) {
            if (t != null) {
                hashtag = t;
                final HashTag hashtagObj = hashtag.get(0);
                if (hashtagObj != null) {
                    isShowHeadWidget(mTopicWrap.getId(), 0);
                    TextView mTopicBody = (TextView) mTopicWrap.findViewById(R.id.topic_des_tv);
                    TextView mTopicNum = (TextView) mTopicWrap.findViewById(R.id.topic_num_tv);
                    fontUtil.changeViewFont(mTopicBody, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
                    fontUtil.changeViewFont(mTopicNum, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
                    mTopicBody.setText(hashtagObj.getName());
                    mTopicNum.setText(getString(R.string.aloha_time_photo_count, hashtagObj.getPostCount()));
                    mTopicWrap.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            openTopic(hashtagObj);
                        }
                    });
                    return;
                }
            }
            if (mTopicWrap != null) {
                mTopicWrap.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void loadMore() {

    }
}
