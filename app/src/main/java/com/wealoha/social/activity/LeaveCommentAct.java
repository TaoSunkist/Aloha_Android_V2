package com.wealoha.social.activity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.InjectView;
import butterknife.OnClick;

import com.squareup.picasso.Picasso;
import com.wealoha.social.BaseFragAct;
import com.wealoha.social.R;
import com.wealoha.social.adapter.CommentAdapter;
import com.wealoha.social.beans.Comment;
import com.wealoha.social.beans.CommentResult;
import com.wealoha.social.beans.Feed;
import com.wealoha.social.beans.IResultDataErrorCode;
import com.wealoha.social.beans.Result;
import com.wealoha.social.beans.ResultData;
import com.wealoha.social.beans.User;
import com.wealoha.social.beans.comment.CommentService;
import com.wealoha.social.commons.GlobalConstants;
import com.wealoha.social.utils.ContextUtil;
import com.wealoha.social.utils.FontUtil.Font;
import com.wealoha.social.utils.ToastUtil;
import com.wealoha.social.utils.UiUtils;
import com.wealoha.social.utils.XL;
import com.wealoha.social.view.custom.dialog.ListItemDialog;
import com.wealoha.social.view.custom.dialog.ListItemDialog.ListItemCallback;
import com.wealoha.social.view.custom.dialog.ListItemDialog.ListItemType;
import com.wealoha.social.view.custom.listitem.FeedItemHolder;

/**
 * @author superman
 * @author sunkist
 * @author javamonk
 * @see
 * @since
 */
public class LeaveCommentAct extends BaseFragAct implements OnItemClickListener, SwipeRefreshLayout.OnRefreshListener, OnItemLongClickListener, OnTouchListener, OnClickListener, ListItemCallback {

    @Inject
    ContextUtil contextUtil;
    @Inject
    Picasso picasso;
    @Inject
    CommentService mCommentService;
    public static final String TAG = LeaveCommentAct.class.getSimpleName();
    @InjectView(R.id.comments_send_tv)
    TextView mSend;
    // 内容
    @InjectView(R.id.comments_content_et)
    EditText mCommentEt;

    @InjectView(R.id.comment_list_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @InjectView(R.id.comments_content_lv)
    ListView mCommentListView;

    @InjectView(R.id.config_back_tv)
    ImageView mBack;
    @InjectView(R.id.container_layout)
    LinearLayout containerLayout;
    @InjectView(R.id.menu_bar)
    RelativeLayout mTitle;

    // 获取postid
    private Bundle mBundle;
    private Feed mFeed;
    private CommentAdapter mCommentAdapter;

    // 评论s
    private List<Comment> mComments;
    private Map<String, User> mUserMap;

    private String SURPER_CURSOR = "cursor";
    private String mCursor = SURPER_CURSOR;
    /**
     * false = 回覆 true = 評論
     */
    private boolean commentType = true;

    private InputMethodManager imm;

    private String mCount = "15";
    private String fromUser = null;

    private Handler refreshViewHandler;
    private Runnable refreshViewRunable;

    private Context mContext;
    private String mDeleteCommentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_leave_comment);
        mContext = this;
        mComments = new ArrayList<Comment>();
        mUserMap = new HashMap<String, User>();
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        initView();
    }

    public void initView() {
        Intent intent = getIntent();
        mBundle = (intent == null ? null : intent.getExtras());
        if (mBundle != null) {
            mFeed = (Feed) mBundle.getParcelable(Feed.TAG);
            loadComment(mFeed);
        } else {
            return;
        }
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setProgressViewOffset(false, -UiUtils.dip2px(mContext, 70), UiUtils.dip2px(mContext, 70));
        mSwipeRefreshLayout.setColorSchemeResources(R.color.red);
        mCommentListView.setOnItemClickListener(this);
        mCommentListView.setOnTouchListener(this);
        mCommentListView.setOnItemLongClickListener(this);
    }

    @Override
    protected void initTypeFace() {
        fontUtil.changeFonts(mTitle, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
    }

    /**
     * @return void 返回类型
     * @throws
     * @Title: loadComment
     * @Description: 加载评论列表
     */
    public void loadComment(Feed feed) {
        if (feed == null) {
            return;
        }
        if (TextUtils.isEmpty(mCursor) || "null".equals(mCursor)) {
            mSwipeRefreshLayout.setRefreshing(false);
            return;
        }
        if (SURPER_CURSOR.equals(mCursor)) {
            mCursor = null;
        }
        mCommentService.comments(feed.postId, mCursor, mCount, new Callback<Result<CommentResult>>() {

            @Override
            public void failure(RetrofitError arg0) {
                ToastUtil.longToast(mContext, R.string.network_error);
            }

            @Override
            public void success(Result<CommentResult> result, Response arg1) {
                if (result != null && result.isOk()) {
                    mCursor = result.data.nextCursorId;
                    refreshView(result);
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    /**
     * @return void 返回类型
     * @throws
     * @Title: refreshView
     * @Description: 创建评论信息的控件
     */
    public void refreshView(Result<CommentResult> result) {
        if (result.data.userMap == null || result.data.list == null) {
            // ToastUtil.longToast(mContext, "這張照片已經被刪除了");
            ToastUtil.shortToast(mContext, R.string.failed);
            return;
        }
        if (mCommentAdapter == null) {
            mUserMap.putAll(result.data.userMap);
            mCommentAdapter = new CommentAdapter(this, result.data.list, result.data.userMap, mFeed);
            mCommentListView.setAdapter(mCommentAdapter);
            mComments.addAll(result.data.list);

            final int endPositino = result.data.list.size();
            refreshViewHandler = new Handler();
            refreshViewRunable = new Runnable() {

                @Override
                public void run() {
                    mCommentListView.setSelection(endPositino - 1);
                }
            };
            refreshViewHandler.postDelayed(refreshViewRunable, 200);
            // Log.i("REFRESHVIEW", "null");
        } else {
            mComments.addAll(result.data.list);
            mUserMap.putAll(result.data.userMap);
            mCommentAdapter.notifyDataSetChanged(mComments, mUserMap);
            if (mCommentAdapter.getCount() > 0) {
                mCommentListView.setSelection(mCommentAdapter.getCount() - 1);
            } else {
                mCommentListView.setSelection(mCommentAdapter.getCount());
            }
        }
    }

    @OnClick({R.id.comments_send_tv, R.id.config_back_tv})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.comments_send_tv:
                sendComment(mCommentEt.getText().toString());
                break;
            case R.id.config_back_tv:
                finish();
                break;
        }
    }

    /**
     * @param @param comment 评论内容
     * @return void 返回类型
     * @throws
     * @Title: sendComment
     * @Description: 发送评论
     */
    public void sendComment(String comment) {
        if (TextUtils.isEmpty(comment.trim())) {
            return;
        }
        String replyUserId = null;
        if (!commentType) {
            if (mFeed.isMine() && !TextUtils.isEmpty(fromUser)) {
                replyUserId = fromUser;
            } else {
                replyUserId = mFeed.userId;
            }
        }
        mCommentService.postCommentForOld(mFeed != null ? mFeed.postId : "", replyUserId, comment, new Callback<Result<CommentResult>>() {

            @Override
            public void success(Result<CommentResult> result, Response response) {
                if (isFinishing()) {
                    return;
                }
                if (result != null) {
                    if (result.isOk()) {
                        // 评论数 + 1
                        Integer commentCount = 0;
                        if (FeedItemHolder.commentMap != null && FeedItemHolder.commentMap.size() > 0) {
                            XL.i("LEAVE_COMMENT_BUG", "feed:" + FeedItemHolder.commentMap);
                            commentCount = FeedItemHolder.commentMap.get(mFeed.postId);
                            if (commentCount == null) {
                                commentCount = 0;
                            }
                            FeedItemHolder.commentMap.put(mFeed.postId, commentCount + 1);
                        }
                        mCommentEt.setText("");// 重置
                        mCommentEt.setHint(R.string.leave_hint);
                        // 收回輸入法
                        containerLayout.setFocusable(true);
                        containerLayout.requestFocus();
                        imm.hideSoftInputFromWindow(mCommentEt.getWindowToken(), 0);

                        // Log.i("SEND_COMMENT", "result:" +
                        // result.data.list.get(0).replyUserId);
                        // feed.
                        refreshView(result);// 更新评论列表
                    } else if (result.data.error == IResultDataErrorCode.ERROR_INVALID_COMMENT) {
                        ToastUtil.shortToast(LeaveCommentAct.this, getString(R.string.comment_has_illegalword));
                    } else if (result.data.error == IResultDataErrorCode.ERROR_BLOCK_BY_OTHER) {
                        ToastUtil.shortToastCenter(mContext, getString(R.string.otherside_black_current_user));
                    } else {
                        ToastUtil.shortToastCenter(mContext, getString(R.string.Unkown_Error));
                    }
                } else {
                    ToastUtil.shortToastCenter(mContext, getString(R.string.Unkown_Error));
                }
            }

            @Override
            public void failure(RetrofitError arg0) {
                ToastUtil.longToast(mContext, R.string.network_error);
            }
        });
    }

    /**
     * @param @param commentId 评论id
     * @return void 返回类型
     * @throws
     * @Title: deleteMineComment
     * @Description: 删除评论，只能删除自己的啦啦啦
     */
    public void deleteMineComment(String commentId) {

    }

    public static class LeaveCommentHandler extends Handler {

        public WeakReference<LeaveCommentAct> act;

        public LeaveCommentHandler(LeaveCommentAct lcAct) {
            act = new WeakReference<LeaveCommentAct>(lcAct);
        }

        @Override
        public void handleMessage(Message msg) {
            LeaveCommentAct lcAct = act.get();
            if (lcAct != null) {
                lcAct.handlerService(msg);
            }
        }
    }

    public void handlerService(Message msg) {
        switch (msg.what) {
            case GlobalConstants.AppConstact.SERVER_ERROR:
                ToastUtil.shortToast(LeaveCommentAct.this, getString(R.string.network_error));
                break;
            case GlobalConstants.AppConstact.CONTROL_FAILURE:

                break;
            case GlobalConstants.AppConstact.CONTROL_SUCCESS:
                if (msg.obj != null) {
                    int position = (Integer) msg.obj;
                    mComments.remove(position);
                }
                mCommentAdapter.notifyDataSetChanged();
                break;
        }
    }

    private Handler mHandler = new LeaveCommentHandler(this);

    /**
     * @return void 返回类型
     * @throws
     * @Title: popInputMethod
     * @Description: 自動彈出輸入法
     */
    public void popInputMethod() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {

                // imm = (InputMethodManager)
                // getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null && mCommentEt != null) {
                    imm.showSoftInput(mCommentEt, 0);
                }
            }
        }, 500);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        super.onWindowFocusChanged(hasFocus);
        // 彈出輸入法
        popInputMethod();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Comment comment = (Comment) mCommentListView.getItemAtPosition(position);
        if (comment == null) {
            return;
        }

        if (!comment.mine) {
            // popInputMethod();
            // imm = (InputMethodManager)
            // getSystemService(Context.INPUT_METHOD_SERVICE);
            // 別人的評論，输入法是开关式的弹出方式
            // imm.toggleSoftInput(0, 0);
            mCommentEt.setFocusable(true);
            mCommentEt.requestFocus();
            imm.showSoftInput(mCommentEt, 0);
            mCommentEt.setText("");
            mCommentEt.setHint(mResources.getString(R.string.report_hint) + mUserMap.get(comment.userId).getName() + ":");
            commentType = false;
            fromUser = mUserMap.get(comment.userId).getId();

            final int fp = position;
            final View v = view;
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    int height = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getHeight() - UiUtils.dip2px(mContext, (48 + 45)) - v.getHeight();
                    // Log.i("NOTIFY_HEIGHT", "height:" + height);
                    // Log.i("NOTIFY_HEIGHT", "edittext:" +
                    // mCommentEt.getVisibility());
                    // mCommentListView.smoothScrollToPositionFromTop(fp,
                    // height, 100);
                    // mCommentListView.smoothScrollByOffset(10);
                    mCommentListView.setSelectionFromTop(fp, height);
                }
            }, 300);
        } else {
            mCommentEt.setText("");
            mCommentEt.setHint(R.string.leave_comment);
            commentType = true;

            containerLayout.setFocusable(true);
            containerLayout.requestFocus();

            imm.hideSoftInputFromWindow(mCommentEt.getWindowToken(), 0);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        XL.d(TAG, "onTouchEvent");
        return true;
    }

    @Override
    public void onRefresh() {
        // loadComment();
        if (TextUtils.isEmpty(mCursor) || "null".equals(mCursor)) {
            mSwipeRefreshLayout.setRefreshing(false);
            return;
        }
        if (SURPER_CURSOR.equals(mCursor)) {
            mCursor = null;
        }
        mCommentService.comments(mFeed.postId, mCursor, mCount, new Callback<Result<CommentResult>>() {

            @Override
            public void failure(RetrofitError arg0) {

            }

            @Override
            public void success(Result<CommentResult> result, Response arg1) {
                if (result != null && result.isOk()) {
                    mCursor = result.data.nextCursorId;
                    // refreshView(result);
                    mComments.addAll(0, result.data.list);
                    mCommentAdapter.notifyDataSetChangedOnTop(result);
                    mCommentListView.setSelection(0);
                    Log.i("REFRESHVIEW", "pullRefresh");
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        Comment comment = (Comment) parent.getItemAtPosition(position);
        // 为什么要这样写
        if (mFeed.isMine() || comment.mine) {
            // new ReportBlackAlohaPopup().showPopup(PopupType.COMMENT_DELETE,
            // PostType.COMMENT_DEL,
            // comment.id, null, null);
            mDeleteCommentId = comment.id;
            new ListItemDialog(this, (ViewGroup) view).showListItemPopup(this, null, ListItemType.DELETE);
        }

        // if (!(feed.mine || comment.mine)) {
        // new ReportBlackAlohaPopup().showPopup(PopupType.COMMENT_DELETE,
        // PostType.COMMENT_DEL, comment.id,
        // null, null);
        // }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        // AppApplication.sessionId = null;
        if (refreshViewHandler != null && refreshViewRunable != null) {
            refreshViewHandler.removeCallbacks(refreshViewRunable);
        }
        super.onPause();
    }

    private float currentY = 0;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int eventId = event.getAction();
        switch (eventId) {
            case MotionEvent.ACTION_DOWN:
                currentY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (currentY != -1 && (event.getY() - currentY) > 200) {
                    containerLayout.setFocusable(true);
                    containerLayout.requestFocus();
                    imm.hideSoftInputFromWindow(mCommentEt.getWindowToken(), 0);
                    currentY = -1;
                }
                break;
            case MotionEvent.ACTION_UP:
                currentY = 0;
                break;
            default:
                break;
        }
        v.performClick();
        return false;
    }

    @Override
    public void itemCallback(int listItemType) {
        switch (listItemType) {
            case ListItemType.DELETE:
                deleteComment(mDeleteCommentId);
                break;

            default:
                break;
        }
    }

    private void deleteComment(final String commentId) {
        mCommentService.deleteComment(mFeed != null ? mFeed.postId : "", commentId, new Callback<Result<ResultData>>() {

            @Override
            public void success(Result<ResultData> result, Response arg1) {
                if (result != null && result.isOk()) {
                    // FIXME 评论数更新，有更好的方法？
                    Comment s = new Comment();
                    s.id = commentId;
                    int index = mComments.indexOf(s);
                    if (index != -1) {
                        mComments.remove(index);
                        FeedItemHolder.commentMap.put(mFeed.postId, FeedItemHolder.commentMap.get(mFeed.postId) - 1);
                    }
                    mCommentAdapter.notifyDataSetChangedByDelete(mComments);
                    return;
                }
            }

            @Override
            public void failure(RetrofitError arg0) {
                mHandler.sendEmptyMessage(GlobalConstants.AppConstact.SERVER_ERROR);
            }
        });
    }
}
