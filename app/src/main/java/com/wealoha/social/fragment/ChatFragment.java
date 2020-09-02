package com.wealoha.social.fragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import butterknife.InjectView;

import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.squareup.otto.Subscribe;
import com.wealoha.social.AsyncLoader;
import com.wealoha.social.R;
import com.wealoha.social.activity.MainAct;
import com.wealoha.social.adapter.ChatListAdapter;
import com.wealoha.social.api.ServerApi;
import com.wealoha.social.beans.ApiResponse;
import com.wealoha.social.beans.ResultData;
import com.wealoha.social.beans.message.InboxSession;
import com.wealoha.social.beans.message.InboxSessionResult;
import com.wealoha.social.beans.message.Message;
import com.wealoha.social.commons.GlobalConstants;
import com.wealoha.social.commons.GlobalConstants.CacheKey;
import com.wealoha.social.commons.JsonController;
import com.wealoha.social.event.DeleteSessionEvent;
import com.wealoha.social.impl.Listeners;
import com.wealoha.social.interfaces.SmsNoticeCallBack;
import com.wealoha.social.push.NoticeBarController;
import com.wealoha.social.push.Push2Type;
import com.wealoha.social.push.model.InboxMessageNewPush;
import com.wealoha.social.utils.ContextUtil;
import com.wealoha.social.utils.FontUtil;
import com.wealoha.social.utils.FontUtil.Font;
import com.wealoha.social.utils.ToastUtil;
import com.wealoha.social.utils.XL;
import com.wealoha.social.view.custom.dialog.ListItemDialog;
import com.wealoha.social.view.custom.dialog.ListItemDialog.ListItemCallback;
import com.wealoha.social.view.custom.dialog.ListItemDialog.ListItemType;

/**
 * 聊天会话
 *
 * @author sunkist
 * @author superman
 * @author javamonk
 * @date 2014-11-16 下午4:38:03
 * @see
 * @since
 */
public class ChatFragment extends BaseFragment implements ListItemCallback, OnItemClickListener, LoaderManager.LoaderCallbacks<ApiResponse<InboxSessionResult>>, SmsNoticeCallBack {

    public static final String TAG = ChatFragment.class.getSimpleName();
    public static final int DELETE_SESSION_SUCCESS = 100;
    public static final int DELETE_SESSION_FAILURE = DELETE_SESSION_SUCCESS + 1;
    public static final int NO_CHAT_LIST_DATA = DELETE_SESSION_FAILURE + 1;
    protected static final int CHAT_LIST_THERE_ARE_DATA = NO_CHAT_LIST_DATA + 1;
    private static final int LOADER_GET_SESSION_LIST = 0;
    private View view;
    private MainAct mMainAct;
    @InjectView(R.id.frag_chat_obj_list)
    ListView frag_chat_obj_list;
    @InjectView(R.id.title_bar)
    TextView title;
    @InjectView(R.id.chat_bg_no_chat_data_rl)
    RelativeLayout chat_bg_no_chat_data_rl;
    @Inject
    ContextUtil mContextUtil;
    @Inject
    FontUtil fontUtil;

    @Inject
    ServerApi messageService;

    /**
     * 是否还有最新数据
     */
    private String mNextCursorId;
    private List<InboxSession> inboxSessions;
    private Map<String, Message> newMessagMap;
    private boolean nextPageLoading;
    private boolean hasNextPage;
    private String sessionIdTemp;
    @InjectView(R.id.chat_tips_page_rl)
    RelativeLayout chat_tips_page_rl;
    private ChatListAdapter mChatListAdapter;
    @Inject
    JsonController jsonController;

    private Object sessionDeleteEventListenerObject = new Object() {

        @Subscribe
        public void onCountChange(DeleteSessionEvent event) {
            deleteSession(event.getSessionId());
        }

        /**
         * @Description: 删除会话
         * @see:
         * @since:
         * @description
         * @author: sunkist
         * @date:2014-11-22
         */
        private void deleteSession(final String sessionId) {
            RequestParams requestParams = new RequestParams();
            requestParams.addBodyParameter(GlobalConstants.AppConstact.SESSION_ID, sessionId);

            contextUtil.addGeneralHttpHeaders(requestParams);
            HttpUtils httpUtils = new HttpUtils();
            httpUtils.send(HttpMethod.POST, GlobalConstants.ServerUrl.DELETE_SESSION, requestParams, new RequestCallBack<String>() {

                @Override
                public void onFailure(HttpException arg0, String arg1) {
                    mHandler.sendEmptyMessage(DELETE_SESSION_FAILURE);
                }

                @Override
                public void onSuccess(ResponseInfo<String> arg0) {
                    ApiResponse<ResultData> apiResponse = JsonController.parseJson(arg0.result, new TypeToken<ApiResponse<ResultData>>() {
                    }.getType());
                    if (apiResponse != null && apiResponse.isOk()) {
                        mHandler.sendEmptyMessage(DELETE_SESSION_SUCCESS);
                        InboxSession s = new InboxSession();
                        s.id = sessionId;
                        int index = inboxSessions.indexOf(s);
                        if (index != -1) {
                            inboxSessions.remove(index);
                        }
                        mChatListAdapter.notifyDataChage(inboxSessions, newMessagMap);
                        return;
                    }
                    mHandler.sendEmptyMessage(DELETE_SESSION_FAILURE);
                }
            });
        }
    };

    private void computeUnread() {
        int sum = getUnreadCount();

        mMainAct.setChatSub(sum);
    }

    private int getUnreadCount() {
        int sum = 0;
        if (inboxSessions != null && inboxSessions.size() > 0) {
            for (InboxSession s : inboxSessions) {
                sum += s.unread;
            }
        }
        return sum;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_chat, container, false);
        mMainAct = (MainAct) getActivity();
        return view;
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        frag_chat_obj_list.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView arg0, int arg1) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (!nextPageLoading && hasNextPage && (totalItemCount - visibleItemCount) <= (firstVisibleItem + 5)) {
                    Log.i("CHAT_LOAD", "scroll-----");
                    if (isAdded())
                        getLoaderManager().restartLoader(LOADER_GET_SESSION_LIST, null, ChatFragment.this);
                }
            }
        });
    }

    public void initView() {
        frag_chat_obj_list.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                InboxSession inboxSession = (InboxSession) parent.getItemAtPosition(position);
                if (inboxSession != null) {
                    sessionIdTemp = inboxSession.id;
                    new ListItemDialog(getActivity(), (ViewGroup) view).showListItemPopup(ChatFragment.this, inboxSession.user.getName(), ListItemType.DELETE);
                    // new ReportBlackAlohaPopup().showPopup(PopupType.DELETE,
                    // PostType.SESSION,
                    // inboxSession.id, inboxSession.User.getName(),
                    // inboxSession.user);
                } else {
                    ToastUtil.shortToast(getActivity(), R.string.is_not_work);
                }
                return true;
            }
        });
        fontUtil.changeViewFont(title, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
    }

    /**
     * @Description: 获取可聊天的列表 1、no netWork 从本地获取 2、network good 从网络获取
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2014-11-6
     */
    public void initData() {
        /** 从网络获取数据 */
        boolean init = false;
        if (mNextCursorId == null) {
            InboxSessionResult cached = restore(CacheKey.FirstPageInboxSession);
            if (cached != null) {
                init = true;// 如果存在本地数据
                this.inboxSessions = cached.getList();
                this.newMessagMap = (Map<String, Message>) cached.getNewMessageMap();
                computeUnread();
            }
        }
        if (!init) {
            this.inboxSessions = new ArrayList<InboxSession>();
            this.newMessagMap = new HashMap<>();
        }
        initChatListView(inboxSessions, newMessagMap);

        // 默认认为还有下一页
        hasNextPage = true;
        // 加载第一页
        if (isAdded()) {
            getLoaderManager().restartLoader(LOADER_GET_SESSION_LIST, null, this);
        }
        // 加上一个比较,比较两个集合的数据是否一样;

    }

    private void initChatListView(List<InboxSession> mSessionList, Map<String, Message> mMessageMap) {
        if (mSessionList == null || mSessionList.size() <= 0 || mMessageMap == null || mMessageMap.size() <= 0) {
            mHandler.sendEmptyMessage(NO_CHAT_LIST_DATA);
        } else {
            mHandler.sendEmptyMessage(CHAT_LIST_THERE_ARE_DATA);
        }
        NoticeBarController nbc = NoticeBarController.getInstance(mMainAct);// 用来获取sessionmap，里面有消息的数量
        // if (mChatListAdapter != null && mSessionList != null && mMessageMap
        // != null && mSessionList.size() > 0 && mMessageMap.size() > 0) {
        // mChatListAdapter.notifyDataChage(mSessionList, mMessageMap);
        // } else {
        mChatListAdapter = new ChatListAdapter(mMainAct, mSessionList, mMessageMap, nbc.getmSessionMap(), frag_chat_obj_list, chat_bg_no_chat_data_rl);
        frag_chat_obj_list.setAdapter(mChatListAdapter);
        // }
        frag_chat_obj_list.setOnItemClickListener(this);
        initView();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // 获取当前的inboxSession
        final InboxSession inboxSession = (InboxSession) parent.getAdapter().getItem(position);
        // 清空通知栏中push过来的sessionID通知
        NoticeBarController.getInstance(mMainAct).removeSession(inboxSession.user.getId());
        // 通过bundle传递到对话界面
        Bundle bundle = new Bundle();
        bundle.putString("sessionId", inboxSession.id);
        bundle.putBoolean("showMatchHint", inboxSession.showMatchHint);
        bundle.putParcelable("toUser", inboxSession.user);
        int count = getUnreadCount() - inboxSession.unread;
        bundle.putInt("unreadCount", count); // 除了当前会话，还有多少未读
        // 先清除本地的未读会话
        startActivityHasAnim(mMainAct, GlobalConstants.IntentAction.INTENT_URI_DIALOGUE, bundle, R.anim.left_in, R.anim.stop);
    }

    @Override
    public Loader<ApiResponse<InboxSessionResult>> onCreateLoader(int id, Bundle args) {
        Log.i("CHAT_LOAD", "CREAT-----");
        switch (id) {
            case LOADER_GET_SESSION_LIST:
                return new AsyncLoader<ApiResponse<InboxSessionResult>>(context) {

                    @Override
                    public ApiResponse<InboxSessionResult> loadInBackground() {
                        try {
                            nextPageLoading = true;
                            return messageService.sessions(mNextCursorId, 10);
                        } catch (Exception e) {
                            // FIXME 提示错误
                            // XL.d(TAG, "加载inbox session失败", e);
                            return null;
                        }
                    }
                };
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<ApiResponse<InboxSessionResult>> loader, ApiResponse<InboxSessionResult> apiResponse) {
        if (apiResponse == null) {
            return;
        }
        switch (loader.getId()) {
            case LOADER_GET_SESSION_LIST:
                nextPageLoading = false;
                if (mNextCursorId == null) {
                    // 缓存第一页
                    save(CacheKey.FirstPageInboxSession, apiResponse.getData());
                    inboxSessions = apiResponse.getData().getList();
                    newMessagMap = (Map<String, Message>) apiResponse.getData().getNewMessageMap();
                } else {
                    inboxSessions.addAll(apiResponse.getData().getList());
                    newMessagMap.putAll(apiResponse.getData().getNewMessageMap());
                }
                mNextCursorId = apiResponse.getData().getNextCursorId();
                if (mNextCursorId == null) {
                    // 没有下一页了
                    hasNextPage = false;
                }
                // FIXME 如果数据完全一致,则不考虑去再次渲染界面;
                mChatListAdapter.notifyDataChage(inboxSessions, newMessagMap);
                computeUnread();
                mHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        if (!fragmentVisible) {
                            XL.d(TAG, "视图不在了，忽略返回结果");
                            return;
                        }

                        if (inboxSessions == null || inboxSessions.size() <= 0) {
                            frag_chat_obj_list.setVisibility(View.GONE);
                            chat_bg_no_chat_data_rl.setVisibility(View.VISIBLE);
                        } else {
                            frag_chat_obj_list.setVisibility(View.VISIBLE);
                            chat_bg_no_chat_data_rl.setVisibility(View.GONE);
                        }
                    }
                });
        }
    }

    @Override
    public void onLoaderReset(Loader<ApiResponse<InboxSessionResult>> arg0) {
        nextPageLoading = false;
    }

    @Subscribe
    public void sayGoodOnEvent(DeleteSessionEvent event) {

    }

    private static class ChatFragHandler extends Handler {

        public WeakReference<ChatFragment> frag;

        public ChatFragHandler(ChatFragment chatFrag) {
            frag = new WeakReference<ChatFragment>(chatFrag);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            ChatFragment chatFrag = frag.get();
            if (chatFrag != null) {
                chatFrag.handlerService(msg);
            }
        }
    }

    public void handlerService(android.os.Message msg) {
        switch (msg.what) {
            case DELETE_SESSION_FAILURE:
                ToastUtil.shortToast(context, R.string.is_not_work);
                break;
            case DELETE_SESSION_SUCCESS:
                ToastUtil.shortToast(context, R.string.is_work);
                break;
            case NO_CHAT_LIST_DATA:
                // LayoutInflater layoutInflater = (LayoutInflater)
                // context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                // View view =
                // layoutInflater.inflate(R.layout.view_chat_tips_page, null);
                // chat_tips_page_rl.addView(view);
                // chat_tips_page_rl.setVisibility(View.VISIBLE);
                break;
            case CHAT_LIST_THERE_ARE_DATA:
                if (chat_tips_page_rl == null) {
                    chat_tips_page_rl = (RelativeLayout) view.findViewById(R.id.chat_tips_page_rl);
                }
                chat_tips_page_rl.setVisibility(View.GONE);
                break;
        }
    }

    private Handler mHandler = new ChatFragHandler(this);

    @Override
    public void noticeTask() {

    }

    @Override
    public void onResume() {
        mNextCursorId = null;
        try {
            bus.post(new Listeners.MonitorMainUiBottomTagPostion(1));
        } catch (Throwable e) {
        }
        initData();
        // 清理push栏的记数，如果是多人提醒
        NoticeBarController.getInstance(context).removeSession(NoticeBarController.FAKE_NODETAIL_SESSION_ID);
        // 注册事件监听
        try {
            IntentFilter myIntentFilter = new IntentFilter();
            myIntentFilter.addAction(Push2Type.InboxMessageNew.getType());
            mMainAct.registerReceiver(mBroadcastReceiver, myIntentFilter);
        } catch (Exception e) {
        }
        bus.register(sessionDeleteEventListenerObject);
        super.onResume();
    }

    @Override
    public void onStop() {
        try {
            try {
                mMainAct.unregisterReceiver(mBroadcastReceiver);
            } catch (Exception e) {
            }
        } catch (Throwable e) {
        }
        try {
            bus.unregister(sessionDeleteEventListenerObject);
        } catch (Throwable e) {
        }
        super.onStop();
    }

    @Override
    public void itemCallback(int listItemType) {
        switch (listItemType) {
            case ListItemType.DELETE:
                deleteSession(sessionIdTemp);
                break;

            default:
                break;
        }
    }

    /**
     * @Description: 删除会话
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2014-11-22
     */
    private void deleteSession(final String sessionId) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter(GlobalConstants.AppConstact.SESSION_ID, sessionId);

        contextUtil.addGeneralHttpHeaders(requestParams);
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.send(HttpMethod.POST, GlobalConstants.ServerUrl.DELETE_SESSION, requestParams, new RequestCallBack<String>() {

            @Override
            public void onFailure(HttpException arg0, String arg1) {
                mHandler.sendEmptyMessage(DELETE_SESSION_FAILURE);
            }

            @Override
            public void onSuccess(ResponseInfo<String> arg0) {
                ApiResponse<ResultData> apiResponse = JsonController.parseJson(arg0.result, new TypeToken<ApiResponse<ResultData>>() {
                }.getType());
                if (apiResponse != null && apiResponse.isOk()) {
                    mHandler.sendEmptyMessage(DELETE_SESSION_SUCCESS);
                    InboxSession s = new InboxSession();
                    s.id = sessionId;
                    int index = inboxSessions.indexOf(s);
                    if (index != -1) {
                        inboxSessions.remove(index);
                    }
                    mChatListAdapter.notifyDataChage(inboxSessions, newMessagMap);
                    return;
                }
                mHandler.sendEmptyMessage(DELETE_SESSION_FAILURE);
            }
        });
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Push2Type.InboxMessageNew.getType())) {
                String sessionId = intent.getStringExtra(InboxMessageNewPush.INBOX_MSG_SESSION_ID);
                messageService.getInboxSession(sessionId, new Callback<ApiResponse<InboxSessionResult>>() {

                    @Override
                    public void success(ApiResponse<InboxSessionResult> apiResponse, Response arg1) {
                        if (apiResponse != null && apiResponse.isOk() && apiResponse.getData().getList() != null && apiResponse.getData().getList().size() > 0) {
                            InboxSession session = apiResponse.getData().getList().get(0);
                            if (inboxSessions == null) {
                                inboxSessions = new ArrayList<InboxSession>();
                                newMessagMap = new HashMap<>();
                            }
                            Iterator<InboxSession> it = inboxSessions.iterator();
                            while (it.hasNext()) {
                                InboxSession s = it.next();
                                if (s.id.equals(session.id)) {
                                    it.remove();
                                    break;
                                }
                            }
                            inboxSessions.add(0, session);
                            newMessagMap.putAll(apiResponse.getData().getNewMessageMap());

                            mChatListAdapter.notifyDataChage(inboxSessions, newMessagMap);

                            computeUnread();
                        }
                    }

                    @Override
                    public void failure(RetrofitError e) {
                    }
                });

            }
        }
    };

}
