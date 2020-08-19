package com.wealoha.social.ui.dialogue;

import java.io.File;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.InjectView;
import butterknife.OnClick;

import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.wealoha.social.ActivityManager;
import com.wealoha.social.AsyncLoader;
import com.wealoha.social.BaseFragAct;
import com.wealoha.social.R;
import com.wealoha.social.adapter.ChatMsgViewAdapter;
import com.wealoha.social.beans.Image;
import com.wealoha.social.beans.Result;
import com.wealoha.social.beans.ResultData;
import com.wealoha.social.beans.User;
import com.wealoha.social.beans.message.ImageMessage;
import com.wealoha.social.beans.message.InboxMessageResult;
import com.wealoha.social.beans.message.Message;
import com.wealoha.social.api.MessageService;
import com.wealoha.social.beans.message.TextMessage;
import com.wealoha.social.beans.message.UnreadData;
import com.wealoha.social.beans.user.ProfileData;
import com.wealoha.social.api.ProfileService;
import com.wealoha.social.commons.GlobalConstants;
import com.wealoha.social.event.AnginSendSmsEvent;
import com.wealoha.social.event.SessionNewMessageEvent;
import com.wealoha.social.fragment.Profile2Fragment;
import com.wealoha.social.push.NoticeBarController;
import com.wealoha.social.push.Push2Type;
import com.wealoha.social.push.model.InboxMessageNewPush;
import com.wealoha.social.utils.ContextUtil;
import com.wealoha.social.utils.FileTools;
import com.wealoha.social.utils.FontUtil;
import com.wealoha.social.utils.FontUtil.Font;
import com.wealoha.social.utils.ImageUtil;
import com.wealoha.social.utils.ImageUtil.ExifOrientation;
import com.wealoha.social.utils.ImageUtil.SizeGetCallback;
import com.wealoha.social.utils.StringUtil;
import com.wealoha.social.utils.ToastUtil;
import com.wealoha.social.utils.UiUtils;
import com.wealoha.social.utils.XL;
import com.wealoha.social.view.custom.XListView;
import com.wealoha.social.view.custom.XListView.IXListViewListener;

/**
 * @author:sunkist
 * @see:
 * @since:
 * @description 对话界面的容器
 * @copyright wealoha.com
 * @Date:2014-11-16
 */
public class DialogueActivity extends BaseFragAct implements IDialogueView {

    /**
     * Called when the activity is first created.
     */
    public static final String TAG = DialogueActivity.class.getSimpleName();
    @InjectView(R.id.dialogue_act_et_sendmessage)
    EditText mEditTextContent;
    @InjectView(R.id.listview)
    XListView mListView;
    @InjectView(R.id.dialogue_back_iv)
    TextView mDialogueBack;
    @InjectView(R.id.btn_send)
    TextView mSendMes;
    @InjectView(R.id.toUserProfile)
    TextView mToUserProfile;
    @InjectView(R.id.btn_send_img)
    ImageView mSendImg;
    @InjectView(R.id.session_list_username_tv)
    TextView mUsername;
    @InjectView(R.id.dialogue_title_rl)
    RelativeLayout mMenuBar;
    @InjectView(R.id.first_chat_hi)
    LinearLayout mFirstChatHi;
    private Handler mHandler;
    @Inject
    ContextUtil mDialogueActivityUtil;
    @Inject
    Picasso mPicasso;
    @Inject
    FontUtil mFont;
    @Inject
    ContextUtil mContextUtil;
    @Inject
    MessageService mMessageService;
    private static final int LOADER_LOAD_MESSAGES = 0;
    @Inject
    ProfileService mProfileService;
    private ChatMsgViewAdapter mAdapter;
    private String mNextCursorId;
    private boolean mHasMore = true;
    private static final int PAGE_COUNT = 20;
    private Context mContext;
    private Dialog mOpenCarmeraDialog;
    /* 强制回到会话列表，push跳过来以后要回去的 */
    private InputMethodManager mImm;
    private int mWidth;
    private int mHeight;
    private DialoguePresenter mDialogueP;
    /**
     * @author Sunkist
     */
    private Object mAnginSendSmsEventListener = new Object() {

        @Subscribe
        public void onEvent(AnginSendSmsEvent anginSendSmsEvent) {
            ChatMsgViewAdapter.ViewHolder cViewHolder = anginSendSmsEvent.getViewHolder();
            if (mAdapter != null && cViewHolder != null) {
                mAdapter.anginSendSms(cViewHolder);
            }
        }
    };

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Push2Type.InboxMessageNew.getType())) {
                String sessionId = intent.getStringExtra(InboxMessageNewPush.INBOX_MSG_SESSION_ID);
                // 如果當前Push過來的消息和當前聊天的用戶的sessionId是不一樣的，
                if (mDialogueP.getDialogueHolder().getToUser() != null && //
                        !mDialogueP.getDialogueHolder().getToUser().getId().equals(sessionId)) {
                    mMessageService.unread(new Callback<Result<UnreadData>>() {

                        @Override
                        public void success(Result<UnreadData> result, Response arg1) {
                            changeBackKeyUI(result.data.count);
                        }

                        public void failure(RetrofitError e) {
                        }

                        ;
                    });
                } else {
                    mNextCursorId = null;
                    getLoaderManager().restartLoader(LOADER_LOAD_MESSAGES, null, DialogueActivity.this);
                    if (mDialogueP.getDialogueHolder().getToUser() != null) {
                        mMessageService.clearUnread(mDialogueP.getDialogueHolder().getToUser().getId(), new Callback<Result<ResultData>>() {

                            @Override
                            public void success(Result<ResultData> arg0, Response arg1) {
                            }

                            @Override
                            public void failure(RetrofitError e) {
                            }
                        });
                    }
                }

            }
        }
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_dialogue);
        mDialogueP = new DialoguePresenter(this);
        mContext = this;
        ActivityManager.push(this);
        Intent intent = getIntent();
        mImm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        Bundle bundle = intent.getExtras();
        mDialogueP.initData(bundle);
    }

    @Override
    public void initData() {
        mFont.changeViewFont(mUsername, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
    }

    @Override
    public void initDataError() {

    }

    @Override
    protected void initTypeFace() {
        mFont.changeViewFont(mMenuBar, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
    }

    /**
     * @param sessionId
     * @Description: 清理未读数，需要更换为不关心回调的请求;
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2015-2-11
     */
    @Override
    public void clearUnreadCount(String sessionId) {
        mMessageService.clearUnread(mDialogueP.getDialogueHolder().getSessionId(), new Callback<Result<ResultData>>() {

            @Override
            public void failure(RetrofitError arg0) {

            }

            @Override
            public void success(Result<ResultData> arg0, Response arg1) {

            }
        });
    }

    @Override
    public void byNoticeJoinDialog() {

        mProfileService.view(mDialogueP.getDialogueHolder().getSessionId(), new Callback<Result<ProfileData>>() {

            @Override
            public void success(Result<ProfileData> arg0, Response arg1) {
                mDialogueP.getDialogueHolder().setToUser(arg0.data.user);
                bus.post(new SessionNewMessageEvent(null, null));
                designUi();
            }

            @Override
            public void failure(RetrofitError arg0) {

            }
        });
    }

    @Override
    public void designUi() {
        initView();
        NoticeBarController.getInstance(mContext).removeSession(mDialogueP.getDialogueHolder().getSessionId());
        mUsername.setText(mDialogueP.getDialogueHolder().getToUser().getName());
    }

    @Inject
    ContextUtil contextUtil;

    @Override
    public void onLoaderReset(Loader<Result<InboxMessageResult>> arg0) {
        if (mListView != null) {
            mListView.stopRefresh();
        }
    }

    @Override
    public Loader<Result<InboxMessageResult>> onCreateLoader(int id, Bundle arg1) {
        hideFirstChatView();
        switch (id) {
            case LOADER_LOAD_MESSAGES:
                return new AsyncLoader<Result<InboxMessageResult>>(mContext) {

                    @Override
                    public Result<InboxMessageResult> loadInBackground() {
                        try {
                            return mMessageService.sessionMessages(mDialogueP.getDialogueHolder().getSessionId(), mNextCursorId, PAGE_COUNT);
                        } catch (Exception e) {
                            return null;
                        }
                    }
                };

            default:
                break;
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Result<InboxMessageResult>> loader, Result<InboxMessageResult> result) {
        mListView.stopRefresh();
        if (result == null) {
            return;
        }
        switch (loader.getId()) {
            case LOADER_LOAD_MESSAGES:
                boolean firstPage = false;
                if (mNextCursorId == null) {
                    firstPage = true;
                }
                mNextCursorId = result.data.nextCursorId;
                if (mNextCursorId == null) {
                    mHasMore = false;
                }
                List<Message> list = (List<Message>) result.data.list;
                Collections.reverse(list);
                // if (list != null && list.size() > 0) {
                // mMessage = list.get(list.size() - 1);
                // }
                // XL.d("getLastNewMessage", "" + mMessage.toString());
                mAdapter.gotChatHistory(firstPage, list, mListView);
                // 调整位置到开始
                mListView.setSelection(Math.max(list.size() - 1, 0));
                break;

            default:
                break;
        }
    }

    public static final String ACTION = "cn.etzmico.broadcastreceiverregister.SENDBROADCAST";

    /**
     * 修改后退按钮旁边显示的消息数量
     *
     * @param count
     */
    @Override
    public void changeBackKeyUI(int count) {
        if (count > 0) {
            mDialogueBack.setText("(" + count + ")");
        } else {
            mDialogueBack.setText("");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent result) {
        if (requestCode == GlobalConstants.AppConstact.PHOTO_PICKED_WITH_DATA && resultCode == RESULT_OK && result != null) {
            Uri uri = result.getData();
            String[] filePathColumn = new String[]{MediaStore.Images.Media.DATA};
            Cursor cursor = mContext.getContentResolver().query(uri, filePathColumn, null, null, null);
            String path = null;
            if (cursor != null) {
                cursor.moveToFirst();
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                cursor.close();
            } else {
                if (uri != null) {
                    String tmpPath = uri.getPath();
                    if (tmpPath != null && (tmpPath.endsWith(".jpg") || tmpPath.endsWith(".png") || tmpPath.endsWith(".gif"))) {
                        path = tmpPath;
                    }
                }
            }
            sendImgToUser(path);
        } else if (requestCode == GlobalConstants.AppConstact.CAMERA_WITH_DATA && resultCode == RESULT_OK) {
            String path = mCameraImgFile.getAbsolutePath();
            sendImgToUser(path);
        }
    }

    void sendImgToUser(String path) {
        if (TextUtils.isEmpty(path)) {
            ToastUtil.shortToast(this, R.string.cant_find_image);
            return;
        }
        Bitmap bitmap3 = new ImageUtil().createThumbImage(path, UiUtils.getScreenWidth(mContext), UiUtils.getScreenHeight(mContext), new SizeGetCallback() {

            @Override
            public void getSize(int srcW, int srcH, int thumbW, int thumbH, float scaleRatio, ExifOrientation orientation) {
                mWidth = thumbW;
                mHeight = thumbH;
            }
        });
        if (bitmap3 == null) {
            ToastUtil.shortToast(this, R.string.cant_find_image);
            return;
        }
        ImageMessage message = new ImageMessage();
        message.image = Image.Companion.fake();
        message.image.setUrl(ImageUtil.saveToLocal(bitmap3, FileTools.getFileImgNameHasDir(mContextUtil.getCurrentUser())));
        if (TextUtils.isEmpty(message.image.getUrl())) {
            ToastUtil.shortToast(DialogueActivity.this, R.string.upload_failure_please_retry);
            return;
        }

        message.image.setWidth(mWidth);
        message.image.setHeight(mHeight);
        message.mine = true;
        message.isLocal = true;
        message.smsStatus = 0;
        if (mAdapter != null) {
            mAdapter.addSendImg(message, new File(message.image.getUrl()));
        }
    }

    /**
     * @Description: 请求发送文本消息到服务器
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2014-11-8
     */
    private void reqSendTextSms(final String contString) {
        if (StringUtil.isEmpty(contString)) {
            return;
        }

        TextMessage textMessage = new TextMessage();
        textMessage.smsStatus = 0;
        textMessage.isLocal = true;
        textMessage.text = contString;
        // mMessage = textMessage;
        mAdapter.addSendText(textMessage);
    }

    private void chageSmsUI() {
        Editable text = mEditTextContent.getText();
        if (text != null) {
            String contString = text.toString();
            mEditTextContent.setText("");
            reqSendTextSms(contString);
        } else {
            return;
        }
    }

    @OnClick({R.id.dialogue_act_et_sendmessage, R.id.dialogue_back_iv, R.id.toUserProfile, R.id.btn_send_img, R.id.btn_send})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialogue_act_et_sendmessage:
                UiUtils.showKeyBoard(DialogueActivity.this, mEditTextContent, InputMethodManager.HIDE_NOT_ALWAYS);
                mListView.setSelection(mListView.getBottom());
                break;
            case R.id.dialogue_back_iv:
                // 后退
                // onBackPressed();
                onBackKeyPressed();
                break;
            case R.id.btn_send:
                try {
                    hideFirstChatView();
                    chageSmsUI();
                } catch (NullPointerException e) {
                    XL.d(TAG, e.getMessage());
                }
                break;
            case R.id.toUserProfile:
                toProfileLogin(mDialogueP.getDialogueHolder().getToUser());
                break;
            case R.id.btn_send_img:
                UiUtils.hideKeyBoard(this);
                openHeadSelect();
                break;
        }
    }

    /**
     * @Description:
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2014-12-5
     */
    public void toProfileLogin(User user) {
        if (mImm != null && mEditTextContent != null) {
            mImm.hideSoftInputFromWindow(mEditTextContent.getWindowToken(), 0);
            mImm.hideSoftInputFromInputMethod(mEditTextContent.getWindowToken(), 0);
        }
        Bundle bundle = new Bundle();
        bundle.putParcelable(User.TAG, user);
        startFragment(Profile2Fragment.class, bundle, true);
    }

    @SuppressLint("ClickableViewAccessibility")
    public void initView() {
        // back键显示未读数
        mListView.setPullLoadEnable(false);
        mListView.setPullRefreshEnable(true);
        mListView.setXListViewListener(new IXListViewListener() {

            @Override
            public void onRefresh() {
                if (mHasMore) {
                    getLoaderManager().restartLoader(LOADER_LOAD_MESSAGES, null, DialogueActivity.this);
                } else {
                    mListView.stopRefresh();
                }
            }

            @Override
            public void onLoadMore() {

            }
        });
        mHandler = new Handler();
        mAdapter = new ChatMsgViewAdapter(this, mDialogueP.getDialogueHolder().getToUser(), mListView, this, mDialogueP.getDialogueHolder().getSessionId());
        mListView.setAdapter(mAdapter);
        mListView.setSelection(mListView.getBottom());
        mListView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent event) {

                if (mImm.isActive()) {
                    mImm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
                }
                return false;
            }
        });
        mAdapter.cacheRestore();
        getLoaderManager().restartLoader(LOADER_LOAD_MESSAGES, null, this);
        mEditTextContent.setOnTouchListener(this);
        mListView.setSelection(mListView.getBottom());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    public String userid;

    // private Message mMessage;

    @Override
    public void onResume() {
        super.onResume();
        try {
            IntentFilter myIntentFilter = new IntentFilter();
            myIntentFilter.addAction(Push2Type.InboxMessageNew.getType());
            registerReceiver(mBroadcastReceiver, myIntentFilter);
        } catch (Exception e) {
        }
    }

    @Override
    public void onStop() {
        hidenInputMedthod();
        try {
            unregisterReceiver(mBroadcastReceiver);
        } catch (Throwable e) {
        }
        try {
            bus.unregister(mAnginSendSmsEventListener);
        } catch (Throwable e) {
        }
        super.onStop();
    }

    private void openHeadSelect() {
        View view = getLayoutInflater().inflate(R.layout.open_carmera_dialog, new LinearLayout(mContext), false);
        TextView openCarmera = (TextView) view.findViewById(R.id.open_carmera);
        TextView openLocaPics = (TextView) view.findViewById(R.id.open_location_photo);

        FontUtil.setRegulartypeFace(this, openCarmera);
        FontUtil.setRegulartypeFace(this, openLocaPics);
        openCarmera.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    openCamera(DialogueActivity.this);
                } catch (ActivityNotFoundException e) {
                }
                if (mOpenCarmeraDialog != null && mOpenCarmeraDialog.isShowing()) {
                    mOpenCarmeraDialog.dismiss();
                }
            }
        });
        openLocaPics.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                openImgPick(DialogueActivity.this);
                if (mOpenCarmeraDialog != null && mOpenCarmeraDialog.isShowing()) {
                    mOpenCarmeraDialog.dismiss();
                }
            }
        });
        mOpenCarmeraDialog = new AlertDialog.Builder(this).setView(view).show();
    }

    @Override
    public boolean onBackKeyPressed() {
        if (mImm != null && mEditTextContent != null) {
            mImm.hideSoftInputFromWindow(mEditTextContent.getWindowToken(), 0);
            mImm.hideSoftInputFromInputMethod(mEditTextContent.getWindowToken(), 0);
        }
        if (mDialogueP.getDialogueHolder().isForceBackToSessionList()) {
            // 从push跳过来，回到会话列表，不是退出");
            Bundle bundle = new Bundle();
            bundle.putString("openTab", "chat");
            startActivity(GlobalConstants.IntentAction.INTENT_URI_MAIN, bundle, 0, 0);
            // 关闭自身，避免死循环
        }
        return super.onBackKeyPressed();

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.dialogue_act_et_sendmessage:
                mListView.setSelection(mListView.getBottom());
                return false;
        }
        v.performClick();
        return false;
    }

    public void hidenInputMedthod() {
        if (mImm != null && mEditTextContent != null && mUsername != null) {
            mUsername.setFocusable(true);
            mUsername.setFocusableInTouchMode(true);
            mUsername.requestFocus();
            mImm.hideSoftInputFromWindow(mEditTextContent.getWindowToken(), 0);
        }
    }

    @Override
    protected void onDestroy() {
        try {
            unregisterReceiver(mBroadcastReceiver);
        } catch (Exception e) {
        }
        ActivityManager.pop(this);
        super.onDestroy();
    }

    private void hideFirstChatView() {
        if (mFirstChatHi == null || isFinishing()) {
            return;
        }
        if (mFirstChatHi.getVisibility() == View.VISIBLE) {
            mFirstChatHi.setVisibility(View.GONE);
        }
    }

    @Override
    public void showMatchHint() {
        fontUtil.changeFonts(mFirstChatHi, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
        mFirstChatHi.setVisibility(View.VISIBLE);
    }
}
