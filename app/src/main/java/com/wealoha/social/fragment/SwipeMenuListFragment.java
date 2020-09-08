package com.wealoha.social.fragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.InjectView;
import butterknife.OnClick;

import com.squareup.otto.Subscribe;
import com.wealoha.social.BaseFragAct;
import com.wealoha.social.R;
import com.wealoha.social.adapter.SwipeMenuAdapter;
import com.wealoha.social.api.ServerApi;
import com.wealoha.social.beans.ApiResponse;
import com.wealoha.social.beans.ResultData;
import com.wealoha.social.beans.User;
import com.wealoha.social.beans.ProfileData;
import com.wealoha.social.beans.UserListResult;
import com.wealoha.social.commons.GlobalConstants;
import com.wealoha.social.event.ControlUserEvent;
import com.wealoha.social.utils.FontUtil;
import com.wealoha.social.utils.FontUtil.Font;
import com.wealoha.social.utils.ToastUtil;
import com.wealoha.social.utils.XL;
import com.wealoha.social.view.custom.dialog.ListItemDialog;
import com.wealoha.social.view.custom.dialog.ListItemDialog.ListItemCallback;
import com.wealoha.social.view.custom.dialog.ListItemDialog.ListItemType;

public class SwipeMenuListFragment extends BaseFragment implements ListItemCallback, OnItemClickListener,
        OnClickListener, OnScrollListener, OnItemLongClickListener {

    @Inject
    ServerApi mUserService;
    @Inject
    ServerApi mFeedService;
    @Inject
    ServerApi mProfileService;
    @Inject
    FontUtil fontUtil;
    @InjectView(R.id.swipe_list)
    ListView mSwipeList;
    @InjectView(R.id.back)
    ImageView mBackImg;
    @InjectView(R.id.title)
    TextView mTitle;
    BaseFragAct mBaseFragAct;
    @InjectView(R.id.layout)
    ViewGroup layout;
    private View footerView;

    private Bundle bundle;
    private int mListType;
    private SwipeMenuAdapter mSwipeAdapter;
    private String TAG = getClass().getName();
    private List<User> mUsers;
    private User mCurrentUser;

    public static final int LISTTYPE_BLACK = 1;
    public static final int LISTTYPE_LIKE = 2;
    public static final int LISTTYPE_POPULARITY = 3;
    public static final int LISTTYPE_PRAISE = 4;
    public static final int PROFIEL_REQUESTCODE = 5;
    private boolean syncLastPageBool;
    private String SUPER_CURSOR = "cursor";
    private String cursor = SUPER_CURSOR;
    private int pageCount = 50;

    // type==LISTTYPE_PRAISE
    private int feedLikeCount;

    private TextView unlockCount;

    private final static int CREAT_ADAPTER = 1;
    private final static int ADAPTER_NOTIFY = 2;
    private ApiResponse<UserListResult> mApiResponse;
    private ProgressBar footerProgress;
    private TextView footerImg;
    private int positionTemp;

    /**
     * 当前被点击的用户
     */
    public User user;

    // private LoadingPopupWindow loading;

    public static enum ListType {
        BLACK, LIKE, Popularity;
    }

    public static class MenuListHandler extends Handler {

        public WeakReference<SwipeMenuListFragment> frag;

        public MenuListHandler(SwipeMenuListFragment smlFrag) {
            frag = new WeakReference<SwipeMenuListFragment>(smlFrag);
        }

        @Override
        public void handleMessage(Message msg) {
            SwipeMenuListFragment smlFrag = frag.get();
            if (smlFrag != null) {
                smlFrag.handlerService(msg);
            }
        }
    }

    public void handlerService(Message msg) {
        switch (msg.what) {
            case CREAT_ADAPTER:
                boolean showMatch = mListType == LISTTYPE_LIKE || mListType == LISTTYPE_POPULARITY;
                if (mApiResponse != null && mSwipeList != null) {
                    mSwipeAdapter = new SwipeMenuAdapter(mApiResponse.getData(), showMatch);
                    mSwipeList.setAdapter(mSwipeAdapter);
                }

                break;
            case ADAPTER_NOTIFY:
                if (mApiResponse != null && mSwipeList != null) {
                    mSwipeAdapter.notifyDataSetChanged(mApiResponse);
                    XL.i("LOADER_TEST", "ADAPTER_NOTIFY-----");
                }
                break;
            default:
                break;
        }
        syncLastPageBool = true;
    }

    Handler myHandler = new MenuListHandler(this);

    Callback<ApiResponse<UserListResult>> userlistCallback = new Callback<ApiResponse<UserListResult>>() {

        @Override
        public void failure(RetrofitError arg0) {
            syncLastPageBool = true;
            addFooterView(false);
            ToastUtil.longToast(context, R.string.network_error);
        }

        @Override
        public void success(ApiResponse<UserListResult> apiResponse, Response arg1) {
            if (!isVisible()) {
                return;
            }
            if (apiResponse != null && apiResponse.isOk()) {
                addFooterView(true);
                Message msg = new Message();
                if (mSwipeAdapter == null) {
                    msg.what = CREAT_ADAPTER;
                } else {
                    msg.what = ADAPTER_NOTIFY;
                }
                mApiResponse = apiResponse;
                myHandler.sendMessage(msg);
                if (mUsers == null) {
                    mUsers = new ArrayList<User>();
                }
                mUsers.addAll(apiResponse.getData().getList());
                cursor = apiResponse.getData().getNextCursorId();
                if (TextUtils.isEmpty(cursor) || "null".equals(cursor)) {
                    removeFooterView();
                }
                // 更新人氣列表表頭
                if (mListType == LISTTYPE_POPULARITY && unlockCount != null) {
                    unlockCount.setText(context.getResources().getString(R.string.showing_latest_followers, apiResponse.getData().getAlohaGetUnlockCount()));
                }
            } else {
                XL.i(TAG, apiResponse.getData().getError() + "");
                addFooterView(false);
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // loading = new LoadingPopupWindow();
        mBaseFragAct = (BaseFragAct) getActivity();
        return inflater.inflate(R.layout.frag_swipemenu_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getData();
        initView();
    }

    @Override
    protected void initTypeFace() {
        fontUtil.changeViewFont(mTitle, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
    }

    private void getData() {
        bundle = getArguments();
        if (bundle != null) {
            mListType = bundle.getInt("listtype");
            if (mListType == LISTTYPE_PRAISE) {
                feedLikeCount = bundle.getInt("feedLikeCount");
            }
        }

        mCurrentUser = contextUtil.getCurrentUser();
    }

    private void initData() {
        // 處理title
        if (mListType == LISTTYPE_LIKE) {
            mTitle.setText(getResources().getString(R.string.profile_aloha_title, contextUtil.getCurrentUser().getAlohaCount()));
            loadLiksList();
        } else if (mListType == LISTTYPE_BLACK) {
            mTitle.setText(R.string.profile_blacklist_title);
            loadBlackList();
        } else if (mListType == LISTTYPE_POPULARITY) {
            // 处理标题栏显示的人气数量
            mTitle.setText(getResources().getString(R.string.profile_aloha_get_title, contextUtil.getCurrentUser().getAlohaGetCount()));
            mSwipeList.setOnItemClickListener(this);
            loadPopularityList();
        } else if (mListType == LISTTYPE_PRAISE) {
            mTitle.setText(getResources().getString(R.string.feed_like_title, feedLikeCount));
            mSwipeList.setOnItemClickListener(this);
            loadPraiseList();
        }
    }

    private void initView() {

        if (mListType != LISTTYPE_POPULARITY && mListType != LISTTYPE_PRAISE) {
            loadSwipeDeleteUI();
        }
        initData();

        mSwipeList.setOnScrollListener(this);

        footerView = LayoutInflater.from(context).inflate(R.layout.list_loader_footer, new ListView(context), false);
        footerProgress = (ProgressBar) footerView.findViewById(R.id.reload_progress);
        footerImg = (TextView) footerView.findViewById(R.id.reload_img);
        footerView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                syncLastPageBool = false;
                XL.i("USER_LIST_CURSOR", "loader:" + cursor);

                if (mListType == LISTTYPE_LIKE) {
                    loadLiksList();
                } else if (mListType == LISTTYPE_BLACK) {
                    loadBlackList();
                } else if (mListType == LISTTYPE_POPULARITY) {
                    loadPopularityList();
                } else if (mListType == LISTTYPE_PRAISE) {
                    loadPraiseList();
                }
            }
        });
        mSwipeList.addFooterView(footerView);
        addFooterView(true);

    }

    private void removeFooterView() {
        if (footerView != null) {
            footerView.setVisibility(View.GONE);
        }
    }

    private void addFooterView(boolean isloading) {
        if (footerImg == null || footerProgress == null) {
            return;
        }
        if (isloading) {
            footerProgress.setVisibility(View.VISIBLE);
            footerImg.setVisibility(View.GONE);
        } else {
            footerProgress.setVisibility(View.GONE);
            footerImg.setVisibility(View.VISIBLE);
        }

    }

    public void loadLiksList() {
        if (TextUtils.isEmpty(cursor) || "null".equals(cursor)) {
            return;
        }
        if (SUPER_CURSOR.equals(cursor)) {
            cursor = null;
        }
        mUserService.likeList(cursor, pageCount + "", userlistCallback);
    }

    public void loadBlackList() {
        if (TextUtils.isEmpty(cursor) || "null".equals(cursor)) {
            return;
        }
        if (SUPER_CURSOR.equals(cursor)) {
            cursor = null;
        }
        mUserService.blackList(cursor, pageCount + "", userlistCallback);
    }

    public void loadPopularityList() {
        if (TextUtils.isEmpty(cursor) || "null".equals(cursor)) {
            return;
        }
        if (SUPER_CURSOR.equals(cursor)) {
            cursor = null;
        }
        mUserService.popularityList(cursor, pageCount + "", userlistCallback);
    }

    public void loadPraiseList() {
        if (bundle == null) {
            return;
        }
        if (TextUtils.isEmpty(cursor) || "null".equals(cursor)) {
            return;
        }
        if (SUPER_CURSOR.equals(cursor)) {
            cursor = null;
        }
        XL.i(TAG, "loadPopularityList");
        String postId = bundle.getString("postId");
        mFeedService.likeFeedPersons(postId, cursor, pageCount + "", userlistCallback);
    }

    private void loadSwipeDeleteUI() {
        mSwipeList.setOnItemClickListener(this);
        mSwipeList.setOnItemLongClickListener(this);
    }

    /**
     * @Title: deleteFromBlackList
     * @Description: 移出黑名单
     */
    private void deleteFromBlackList(final int position) {
        User user = mUsers.get(position);
        mUserService.unblock(user.getId(), new Callback<ApiResponse<ResultData>>() {

            @Override
            public void failure(RetrofitError arg0) {
                ToastUtil.longToast(mBaseFragAct, R.string.is_not_work);
                syncLastPageBool = true;
            }

            @Override
            public void success(ApiResponse<ResultData> apiResponse, Response arg1) {
                if (apiResponse == null) {
                    return;
                }
                if (apiResponse.isOk()) {

                    if (mUsers != null && mUsers.size() > position) {
                        mUsers.remove(position);
                        mSwipeAdapter.notifyDataSetChangedByList(mUsers);
                    }
                    syncLastPageBool = true;
                    return;
                }
            }
        });
    }

    /**
     * @Title: deleteFromBlackList
     * @Description: 移出喜欢列表
     */
    private void deleteFromLikeList(final int position) {
        XL.i("UNALOHA_SOMEONE", "deleteFromLikeList" + position);
        mUserService.dislikeUser(mUsers.get(position).getId(), new Callback<ApiResponse<ResultData>>() {

            @Override
            public void failure(RetrofitError arg0) {
                syncLastPageBool = true;
            }

            @Override
            public void success(ApiResponse<ResultData> apiResponse, Response arg1) {
                try {
                    mUsers.remove(position);
                } catch (IndexOutOfBoundsException e) {
                    XL.d(TAG, e.getMessage());
                    return;
                }

                if (mSwipeAdapter != null) {
                    mSwipeAdapter.notifyDataSetChangedByList(mUsers);
                }
                if (mCurrentUser.getAlohaCount() > 0) {
                    mCurrentUser.setAlohaCount(mCurrentUser.getAlohaCount() - 1);
                    mTitle.setText(getResources().getString(R.string.profile_aloha_title, mCurrentUser.getAlohaCount()));
                    contextUtil.setCurrentUser(mCurrentUser);
                }
                syncLastPageBool = true;
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Bundle bundle = new Bundle();

        user = mUsers.get(position);
        bundle.putParcelable(User.TAG, user);
        int requestcode = 0;
        if (mListType == LISTTYPE_POPULARITY) {
            // 记录这个profile 界面时从人气界面跳转来的
            bundle.putString("refer_key", GlobalConstants.WhereIsComeFrom.FOLLOWER_TO_PROFILE);
        } else if (mListType == LISTTYPE_LIKE) {
            requestcode = PROFIEL_REQUESTCODE;
        }
        ((BaseFragAct) getActivity()).startFragmentForResult(Profile2Fragment.class, bundle, true, requestcode,//
                R.anim.left_in, R.anim.stop);

    }

    @Override
    public void onActivityResultCallback(int requestcode, int resultcode, Intent result) {
        if (resultcode == Activity.RESULT_OK) {
            switch (requestcode) {
                case PROFIEL_REQUESTCODE:// 进入人气列表解除aloha后返回，刷新界面
                    XL.i("UNALOHA_SOMEONE", "userid" + result.getStringExtra("userid"));
                    String userid = result.getStringExtra("userid");
                    if (mUsers != null) {
                        for (int i = 0; i < mUsers.size(); i++) {
                            User user = mUsers.get(i);
                            if (user.getId().equals(userid)) {
                                mUsers.remove(i);
                                break;
                            }
                        }
                    }
                    mSwipeAdapter.notifyDataSetChangedByList(mUsers);
                    if (mCurrentUser.getAlohaCount() > 0) {
                        mCurrentUser.setAlohaCount(mCurrentUser.getAlohaCount() - 1);
                        mTitle.setText(getResources().getString(R.string.profile_aloha_title, mCurrentUser.getAlohaCount()));
                        contextUtil.setCurrentUser(mCurrentUser);
                    }
                    break;
            }
        }
    }

    @OnClick(R.id.back)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                getActivity().finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (firstVisibleItem + visibleItemCount + 15 > (totalItemCount - 5) && syncLastPageBool) {
            syncLastPageBool = false;

            if (mListType == LISTTYPE_LIKE) {
                loadLiksList();
            } else if (mListType == LISTTYPE_BLACK) {
                loadBlackList();
            } else if (mListType == LISTTYPE_POPULARITY) {
                loadPopularityList();
            } else if (mListType == LISTTYPE_PRAISE) {
                loadPraiseList();
            }
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        // if (mListType == LISTTYPE_LIKE) {
        // // new ReportBlackAlohaPopup().showPopup(PopupType.LISTTYPE_LIKE, PostType.LISTTYPE_LIKE,
        // // String.valueOf(position), null, null);
        // new ListItemDialog(getActivity(), parent).showListItemPopup(this, null, ListItemType.DELETE);
        // } else if (mListType == LISTTYPE_BLACK) {
        new ListItemDialog(getActivity(), parent).showListItemPopup(this, null, ListItemType.DELETE);
        positionTemp = position;
        getActivity().setResult(Activity.RESULT_OK);
        // }
        return true;
    }

    Object controlUserEvent = new Object() {

        @Subscribe
        public void refreshUI(ControlUserEvent event) {
            controlUser(event.getPostion());
        }

        private void controlUser(final int position) {
            syncLastPageBool = false;
            if (mListType == LISTTYPE_LIKE) {
                deleteFromLikeList(position);
            } else if (mListType == LISTTYPE_BLACK) {
                deleteFromBlackList(position);
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        try {
            bus.register(controlUserEvent);
        } catch (Throwable e) {
        }
        if (user != null) {
            XL.i(TAG, "user: not null");
            refreshUser(user.getId());
            user = null;
        }
    }

    private void refreshUser(final String userid) {
        if (mProfileService == null) {
            return;
        }
        mProfileService.view(userid, new Callback<ApiResponse<ProfileData>>() {

            @Override
            public void success(ApiResponse<ProfileData> apiResponse, Response arg1) {
                if (apiResponse == null || !apiResponse.isOk() || mUsers == null || mSwipeAdapter == null) {
                    return;
                }
                for (User user : mUsers) {
                    if (user.getId().equals(userid)) {
                        user.setMatch(apiResponse.getData().user.getMatch());
                        mSwipeAdapter.notifyDataSetChangedByList(mUsers);
                        break;
                    }
                }
            }

            @Override
            public void failure(RetrofitError arg0) {

            }
        });
    }

    @Override
    public void onPause() {
        try {
            bus.unregister(controlUserEvent);
        } catch (Throwable e) {
        }
        super.onPause();
        // 返回profile 刷新界面
        getActivity().setResult(Activity.RESULT_OK);
    }

    public boolean getListType(int type) {
        return type == mListType;
    }

    @Override
    public void itemCallback(int listItemType) {
        switch (listItemType) {
            case ListItemType.DELETE:
                if (mListType == LISTTYPE_LIKE) {
                    deleteFromLikeList(positionTemp);
                } else if (mListType == LISTTYPE_BLACK) {
                    deleteFromBlackList(positionTemp);
                }
                break;

            default:
                break;
        }
    }

}
