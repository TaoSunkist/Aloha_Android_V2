package com.wealoha.social.adapter;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.wealoha.social.BaseFragAct;
import com.wealoha.social.R;
import com.wealoha.social.activity.WebActivity;
import com.wealoha.social.api.ServerApi;
import com.wealoha.social.beans.Result;
import com.wealoha.social.beans.ResultData;
import com.wealoha.social.beans.User;
import com.wealoha.social.beans.message.ImageMessage;
import com.wealoha.social.beans.message.InboxMessageResult;
import com.wealoha.social.beans.message.Message;
import com.wealoha.social.beans.message.TextMessage;
import com.wealoha.social.commons.CacheManager;
import com.wealoha.social.commons.GlobalConstants;
import com.wealoha.social.commons.GlobalConstants.ImageSize;
import com.wealoha.social.inject.Injector;
import com.wealoha.social.ui.dialogue.DialogueActivity;
import com.wealoha.social.utils.ContextUtil;
import com.wealoha.social.utils.ConversionUtil;
import com.wealoha.social.utils.FontUtil;
import com.wealoha.social.utils.FontUtil.Font;
import com.wealoha.social.utils.ImageUtil;
import com.wealoha.social.utils.ImageUtil.CropMode;
import com.wealoha.social.utils.ImageUtil.Dimension;
import com.wealoha.social.utils.NetworkUtil;
import com.wealoha.social.utils.TimeUtil;
import com.wealoha.social.utils.ToastUtil;
import com.wealoha.social.utils.UiUtils;
import com.wealoha.social.utils.XL;
import com.wealoha.social.view.custom.CircleImageView;
import com.wealoha.social.view.custom.XListView;
import com.wealoha.social.view.custom.dialog.ReportBlackAlohaPopup;
import com.wealoha.social.view.custom.dialog.ReportBlackAlohaPopup.PopupType;
import com.wealoha.social.view.custom.dialog.ReportBlackAlohaPopup.PostType;

/**
 * 聊天会话的详细聊天内容
 *
 * @author sunkist
 * @author superman
 * @author javamonk
 * @date 2014-11-17 下午10:37:28
 * @see
 * @since
 */
public class ChatMsgViewAdapter extends BaseAdapter implements OnClickListener {

    @Inject
    Picasso picasso;
    @Inject
    ContextUtil contextUtil;
    @Inject
    CacheManager cacheManager;
    @Inject
    FontUtil fontUtil;
    private XListView mListView;
    // Bitmap bitmap;
    private static final String TAG = ChatMsgViewAdapter.class.getSimpleName();
    // index越大，数据越新
    private List<Message> mMessageList;
    private Context mContext;
    private LayoutInflater mInflater;
    // private Message mSendingSms;
    // private File mSendingFile;
    // private ViewHolder mSendingViewHolder;
    // 不包括失败的，缓存这么多
    private final int CACHE_SIZE = 20;
    private final String CACHE_KEY_LOCAL_MESAGE_PREFIX = "ChatLocalMessageWith_";
    private final String CACHE_KEY_FIRST_PAGE_MESAGE_PREFIX = "ChatFirstPageMessageWith_";
    private BaseFragAct mAct;
    private User toUser;// You
    private User mUser;// Myself
    private String mSessionId;
    @Inject
    ServerApi mMessageService;
    // state计数器
    private static AtomicInteger mStateCounter = new AtomicInteger(new Random(System.currentTimeMillis()).nextInt(10000));

    public static interface IMsgViewType {

        int IMVT_COM_MSG = 0;
        int IMVT_TO_MSG = 1;
    }

    /**
     * 计算 chatImageTransformation 转换以后的视图尺寸
     *
     * @param imageWidth
     * @param imageHeight
     * @return in dip
     */
    private android.widget.RelativeLayout.LayoutParams getImageLayoutSize(int imageWidth, int imageHeight) {

        float scale = imageWidth / (float) imageHeight;

        // 注意！修改这里的代码必须保持和chatImageTransformation比例一致
        int height;
        int width;
        if (scale > 1.0) {
            height = UiUtils.dip2px(mContext, 50);
            width = Math.min(UiUtils.dip2px(mContext, 65), imageWidth);
        } else if (scale < 1.0) {
            width = UiUtils.dip2px(mContext, 50);
            height = Math.min(UiUtils.dip2px(mContext, 65), imageWidth);
        } else {
            width = UiUtils.dip2px(mContext, 50);
            ;
            height = UiUtils.dip2px(mContext, 65);
            ;
        }

        // 根据屏幕比例放大下
        int screenWidth = UiUtils.getScreenWidth(mContext);
        int zoom = Math.max((int) (screenWidth / (float) 120 * 0.7), 4);

        width *= zoom;
        height *= zoom;
        // XL.d(TAG, "计算图像尺寸: " + imageWidth + "x" + imageHeight + " -> " +
        // width + "x" + height);
        return new RelativeLayout.LayoutParams(ConversionUtil.px2dip(mContext, width), ConversionUtil.px2dip(mContext, height));
    }

    private Transformation chatImageTransformation = new Transformation() {

        @Override
        public Bitmap transform(Bitmap bitmap) {
            float scale = bitmap.getWidth() / (float) bitmap.getHeight();

            // 注意！修改这里的代码必须保持和getImageLayoutSize比例一致
            int height;
            int width;

            if (scale > 1.0) {
                height = UiUtils.dip2px(mContext, 50);
                width = Math.min(UiUtils.dip2px(mContext, 65), bitmap.getWidth());
            } else if (scale < 1.0) {
                width = UiUtils.dip2px(mContext, 50);
                height = Math.min(UiUtils.dip2px(mContext, 65), bitmap.getHeight());
            } else {
                width = UiUtils.dip2px(mContext, 50);
                height = UiUtils.dip2px(mContext, 65);
            }

            width *= 2;
            height *= 2;
            Bitmap tmpBitmap = ImageUtil.cropImageByLayoutRatio(width, height, bitmap);
            if (tmpBitmap != bitmap) {
                // 不是原图，回收
                bitmap.recycle();
                bitmap = tmpBitmap;
            }

            if (bitmap.getWidth() > width || bitmap.getHeight() > height) {
                tmpBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
                if (tmpBitmap != bitmap) {
                    // 不是原图，回收
                    bitmap.recycle();
                    bitmap = tmpBitmap;
                }
            }
            return bitmap;
        }

        @Override
        public String key() {
            return "chat_scale()";
        }
    };
    private ReportBlackAlohaPopup reportBlackAlohaPopup;
    private Dialog mPopupWindow;

    public ChatMsgViewAdapter(Context context, User user, XListView xListView, BaseFragAct act, String sessionId) {
        Injector.inject(this);
        mUser = contextUtil.getCurrentUser();
        mContext = context;
        mMessageList = new ArrayList<Message>();
        mInflater = LayoutInflater.from(context);
        toUser = user;
        mListView = xListView;
        mAct = act;
        mSessionId = sessionId;
    }

    public int getCount() {
        if (mMessageList != null && mMessageList.size() > 0) {
            return mMessageList.size();
        } else {
            return 0;
        }
    }

    public Message getItem(int position) {
        return mMessageList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = mMessageList.get(position);
        if (message.mine) {
            return IMsgViewType.IMVT_TO_MSG;
        } else {
            return IMsgViewType.IMVT_COM_MSG;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        Message message = mMessageList.get(position);
        boolean isComMsg = message.mine;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            if (isComMsg) {
                convertView = mInflater.inflate(R.layout.chatting_item_msg_text_right, mListView, false);
                viewHolder.mUser = mUser;
            } else {
                convertView = mInflater.inflate(R.layout.chatting_item_msg_text_left, mListView, false);
                viewHolder.mUser = this.toUser;
            }
            findById(parent, convertView, viewHolder, isComMsg, position);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.setMessage(message);
        picasso.load(ImageUtil.getImageUrl(viewHolder.mUser.getAvatarImage().getId(), 120, CropMode.ScaleCenterCrop)).placeholder(R.drawable.default_photo).into(viewHolder.userhead_iv);

        if (message instanceof TextMessage) {
            viewHolder.iv_chatcontent_fl.setVisibility(View.GONE);
            viewHolder.tv_chatcontent_fl.setVisibility(View.VISIBLE);
            dealTvLink(viewHolder.tv_chatcontent, ((TextMessage) message).text);
            if (isComMsg) {
                viewHolder.resend_iv.setTag(message.state);
                if (message.sending) {
                    viewHolder.right_pb_tv.setVisibility(View.VISIBLE);
                    viewHolder.right_pb.setVisibility(View.GONE);
                } else {
                    viewHolder.right_pb_tv.setVisibility(View.GONE);
                    viewHolder.right_pb.setVisibility(View.GONE);
                }
                if (message.sendFail) {
                    viewHolder.resend_tv.setVisibility(View.VISIBLE);
                    viewHolder.resend_iv.setVisibility(View.GONE);
                } else {
                    viewHolder.resend_tv.setVisibility(View.GONE);
                    viewHolder.resend_iv.setVisibility(View.GONE);
                }
            }
        } else if (message instanceof ImageMessage) {
            if (isComMsg) {// 如果是自己的
                viewHolder.resend_iv.setTag(message.state);
                if (message.sending) {
                    viewHolder.right_pb.setVisibility(View.VISIBLE);
                    viewHolder.right_pb_tv.setVisibility(View.GONE);
                } else {
                    viewHolder.right_pb.setVisibility(View.GONE);
                    viewHolder.right_pb_tv.setVisibility(View.GONE);
                }
                if (message.sendFail) {// 失败
                    viewHolder.resend_iv.setVisibility(View.VISIBLE);
                    viewHolder.resend_tv.setVisibility(View.GONE);
                } else {
                    viewHolder.resend_iv.setVisibility(View.GONE);
                    viewHolder.resend_tv.setVisibility(View.GONE);
                }
            }
            viewHolder.tv_chatcontent_fl.setVisibility(View.GONE);
            viewHolder.iv_chatcontent_fl.setVisibility(View.VISIBLE);
            final ImageMessage imageMessage = (ImageMessage) message;
            viewHolder.iv_chatcontent_box.setOnClickListener(this);
            viewHolder.iv_chatcontent_box.setTag(message);
            if (imageMessage.isLocal && !(TextUtils.isEmpty(imageMessage.image.getUrl()))) {// 如果是本地的图片
                Dimension size = ImageUtil.getImageSize(imageMessage.image.getUrl());
                RelativeLayout.LayoutParams fLayoutParams = getImageLayoutSize(size.width, size.height);
                viewHolder.iv_chatcontent_box.setLayoutParams(fLayoutParams);
                viewHolder.iv_chatcontent.setLayoutParams(fLayoutParams);
                File file = new File(imageMessage.image.getUrl());
                picasso.load(file).transform(chatImageTransformation).into(viewHolder.iv_chatcontent);
            } else {
                RelativeLayout.LayoutParams fLayoutParams = getImageLayoutSize(imageMessage.image.getWidth(), imageMessage.image.getHeight()); // new
                viewHolder.iv_chatcontent_box.setLayoutParams(fLayoutParams);
                viewHolder.iv_chatcontent.setLayoutParams(fLayoutParams);
                String imageUrl = ImageUtil.getImageUrl(imageMessage.image.getId(), ImageSize.CHAT_THUMB, null);
                picasso.load(imageUrl).transform(chatImageTransformation).into(viewHolder.iv_chatcontent);
            }

        }
        if (message.showTimestamp) { // 控制时间戳的现显示
            viewHolder.tvSendTime.setVisibility(View.VISIBLE);
            viewHolder.tvSendTime.setText(TimeUtil.getStrdateyearmonthday(message.createTimeMillis));
        } else {
            viewHolder.tvSendTime.setVisibility(View.GONE);
        }
        return convertView;
    }

    SpannableString msp = null;
    private AlertDialog superDialog;

    /**
     * @param tv_chatcontent
     * @param text
     * @Description:处理超链接效果
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2015-1-6
     */
    @SuppressLint("InlinedApi")
    private void dealTvLink(TextView tv_chatcontent, String text) {

        tv_chatcontent.setText(text);
        Linkify.addLinks(tv_chatcontent, GlobalConstants.COMPILE, "");
        tv_chatcontent.setLinksClickable(true);

        tv_chatcontent.setMovementMethod(LinkMovementMethod.getInstance());
        int end = text.length();
        Spannable sp = new SpannableStringBuilder(tv_chatcontent.getText());
        URLSpan[] urls = sp.getSpans(0, end, URLSpan.class);
        SpannableStringBuilder style = new SpannableStringBuilder(text);
        style.clearSpans();// should clear old spans
        for (URLSpan url : urls) {
            MyURLSpan myURLSpan = new MyURLSpan(url.getURL());
            style.setSpan(myURLSpan, sp.getSpanStart(url), sp.getSpanEnd(url), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        tv_chatcontent.setText(style);
    }

    private class MyURLSpan extends ClickableSpan {

        private String mUrl;

        MyURLSpan(String url) {
            mUrl = url;
        }

        @Override
        public void onClick(View widget) {
            Matcher m = GlobalConstants.COMPILE.matcher(mUrl == null ? "" : mUrl);
            if (m.find()) {
                if (mPopupWindow == null || !mPopupWindow.isShowing()) {

                    Intent intent = new Intent(mContext, WebActivity.class);
                    Bundle bundle = new Bundle();
                    String string = mUrl.toString();
                    bundle.putString("url", string);
                    bundle.putParcelable(User.TAG, toUser);
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
                }
            }

        }
    }

    /**
     * @param file mUser
     * @Description: 用户发送图片
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2014-11-17
     */
    public void uploadSms(Message message, File file, final ViewHolder viewHolder) {
        final String state = message.state;
        if (message instanceof TextMessage) {
            try {
                mMessageService.sendMsgText(((TextMessage) message).text, toUser.getId(), state, new SmsSendDealCallBack(viewHolder, state));
            } catch (Exception e) {
                // TODO 國際化
                // ToastUtil.shortToast(mContext, "消息發送失敗");
                ToastUtil.shortToast(mContext, R.string.Unkown_Error);
            }
        } else {
            try {
                mMessageService.sendMsgImage(new TypedFile("application/octet-stream", file), toUser.getId(), state, new SmsSendDealCallBack(viewHolder, state));
            } catch (Exception e) {
                // TODO 國際化
                // ToastUtil.shortToast(mContext, "消息發送失敗");
                ToastUtil.shortToast(mContext, R.string.Unkown_Error);
            }
        }
    }

    /**
     * 添加时间戳
     */
    private void tagTimestamp() {
        synchronized (mMessageList) {
            Message previousMessage = null;
            for (int i = mMessageList.size() - 1; i >= 0; i--) {
                Message m = mMessageList.get(i);
                if (previousMessage != null) {
                    if (Math.abs(previousMessage.createTimeMillis - m.createTimeMillis) < 90 * 1000) {
                        // 不打时间戳
                        previousMessage.showTimestamp = false;
                    } else {
                        previousMessage.showTimestamp = true;
                    }
                }
                previousMessage = m;
            }

            if (previousMessage != null) {
                // 最早的一条永远显示
                previousMessage.showTimestamp = true;
            }
        }
    }

    /**
     * 删除本地的重复消息
     *
     * @param messageList
     * @return 有删除
     */
    private boolean removeDuplicateLocalMessages(List<Message> messageList) {
        // 合并消息
        // state, type相同，时间相差不多
        if (messageList == null || messageList.size() == 0) {
            return false;
        }
        if (mMessageList == null || mMessageList.size() == 0) {
            return false;
        }

        synchronized (mMessageList) {
            // XL.d(TAG, "开始清理本地消息");
            // 本地消息
            Map<String, Message> localMessageMap = new HashMap<String, Message>();
            for (Message m : mMessageList) {
                if (m.mine && m.isLocal) {
                    localMessageMap.put(m.state, m);
                }
            }

            if (localMessageMap.size() > 0) {
                XL.d(TAG, "有本地消息");
                // 合并
                Set<String> removeMessages = new HashSet<String>();
                for (Message m : messageList) {
                    if (!m.mine) {
                        continue;
                    }

                    Message mLocal = localMessageMap.get(m.state);
                    if (mLocal != null) {
                        // XL.d(TAG, "找到本地消息: " + mLocal);
                    }
                    if (mLocal != null //
                            && mLocal.state.equals(m.state) //
                            && mLocal.getClass().equals(m.getClass())) {
                        // 不判断时间了，有可能很久才点重发
                        // XL.d(TAG, "取回Server发送成功的，丢弃本地的: " + m.state);
                        removeMessages.add(m.state);
                    }
                }

                // 删除
                if (removeMessages.size() > 0) {
                    Iterator<Message> it = mMessageList.iterator();
                    while (it.hasNext()) {
                        Message m = it.next();
                        if (m.mine && m.isLocal && removeMessages.contains(m.state)) {
                            it.remove();
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 最新一条消息的位置(新消息在最后)
     *
     * @return
     */
    public int getBottomPosition() {
        return Math.max(mMessageList.size() - 1, 0);
    }

    /**
     * @Description: 获取到了聊天记录，更新
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2014-11-13
     */
    public void gotChatHistory(boolean firstPage, List<Message> messages, XListView mListView) {
        synchronized (mMessageList) {
            // 把发送失败的消息留下来
            if (firstPage) {
                List<Message> localMessage = new ArrayList<Message>();
                for (Message m : mMessageList) {
                    if (m.isLocal) {
                        localMessage.add(m);
                    }
                }
                // FIXME 此处修改排序规则
                mMessageList.clear();
                mMessageList.addAll(0, messages);

                if (localMessage.size() > 0) {
                    mMessageList.addAll(localMessage);
                }
            } else {
                mMessageList.addAll(0, messages);
            }
            // mListView.setSelection(0);

            removeDuplicateLocalMessages(messages);
            tagTimestamp();
            this.notifyDataSetChanged();
        }

        if (firstPage) {
            cacheSave();
        }
    }

    /**
     * 保存Cache里的数据
     */
    private void cacheSave() {
        synchronized (mMessageList) {
            List<Message> localMessage = new ArrayList<Message>();
            List<Message> firstPageMessage = new ArrayList<Message>();

            // 倒序排，缓存最新的数据
            for (int i = mMessageList.size() - 1; i >= 0; i--) {
                Message m = mMessageList.get(i);
                if (m.mine && (m.sending || m.sendFail || m.isLocal)) {
                    localMessage.add(m);
                } else if (firstPageMessage.size() < CACHE_SIZE) {
                    firstPageMessage.add(m);
                }
            }

            String key = CACHE_KEY_LOCAL_MESAGE_PREFIX + contextUtil.getCurrentUser().getId() + "_" + toUser.getId();
            Collections.reverse(localMessage);
            cacheManager.globalSave(key, localMessage);

            key = CACHE_KEY_FIRST_PAGE_MESAGE_PREFIX + contextUtil.getCurrentUser().getId() + "_" + toUser.getId();
            Collections.reverse(firstPageMessage);
            cacheManager.globalSave(key, firstPageMessage);
        }
    }

    /**
     * 加载缓存的数据，本地消息在后面
     */
    public void cacheRestore() {
        String key = CACHE_KEY_LOCAL_MESAGE_PREFIX + contextUtil.getCurrentUser().getId() + "_" + toUser.getId();
        Type type = new TypeToken<List<Message>>() {
        }.getType();
        List<Message> localMessage = cacheManager.globalRestore(key, type);
        key = CACHE_KEY_FIRST_PAGE_MESAGE_PREFIX + contextUtil.getCurrentUser().getId() + "_" + toUser.getId();
        List<Message> firstPageMessage = cacheManager.globalRestore(key, type);
        // XL.d(TAG, "缓存的本地消息: " + localMessage);
        // XL.d(TAG, "缓存的第一页消息: " + firstPageMessage);
        if (firstPageMessage == null) {
            firstPageMessage = new ArrayList<Message>();
        }
        if (localMessage != null) {
            firstPageMessage.addAll(localMessage);
        }

        mMessageList = firstPageMessage;
        notifyDataSetChanged();
    }

    private void sendingMessage(Message message, File file) {
        message.mine = true;
        message.isLocal = true;
        message.sendFail = false;
        message.sending = true;
        message.createTimeMillis = System.currentTimeMillis();
        message.state = Integer.toHexString(mStateCounter.getAndIncrement());

        // 放到最尾
        synchronized (mMessageList) {
            mMessageList.add(message);
            // 更新界面
            notifyDataSetChanged();
            mListView.setSelection(mListView.getBottom());
        }
        // 发送
        uploadSms(message, file, null);
    }

    /**
     * 发送成功，找到发送中的，替换掉
     *
     * @param message
     */
    private void sendingMessageSuccess(final Message message) {
        synchronized (mMessageList) {
            for (int i = mMessageList.size() - 1; i >= 0; i--) {
                Message m = mMessageList.get(i);
                if (m.mine && message.state.equals(m.state)) {
                    if (message instanceof ImageMessage) {
                        // 图片需要暂时保持不变，避免抖动
                        m.sendFail = false;
                        m.sending = false;
                        m.isLocal = true;
                        m.id = message.id;
                        m.createTimeMillis = message.createTimeMillis;
                    } else {
                        mMessageList.set(i, message);
                    }
                    notifyDataSetChanged();
                    mListView.setSelection(mListView.getBottom());
                    break;
                }
            }

        }
    }

    /**
     * 消息发送失败，更新状态
     *
     * @param state
     */
    private void sendingMessageFail(final String state) {
        for (int i = mMessageList.size() - 1; i >= 0; i--) {
            Message m = mMessageList.get(i);
            if (m.mine && state.equals(m.state)) {
                // XL.d(TAG, "标记发送失败的消息: " + state);
                m.sendFail = true;
                m.sending = false;
                this.notifyDataSetChanged();
                // 失败时缓存下
                cacheSave();
                break;
            }
        }
    }

    private void resendFailMessage(String state) {
        for (Message m : mMessageList) {
            if (m.isLocal && !m.sending && m.sendFail && state.equals(m.state)) {
                m.sendFail = false;
                m.sending = true;
                // 发送
                File file = m instanceof ImageMessage ? new File(((ImageMessage) m).image.getUrl()) : null;
                uploadSms(m, file, null);
                this.notifyDataSetChanged();
                break;
            }
        }
    }

    public void addSendText(Message message) {
        sendingMessage(message, null);
    }

    public void addSendImg(ImageMessage message, File file) {
        sendingMessage(message, file);
    }

    public void resend(String state) {
        View view = mInflater.inflate(R.layout.pupop_sms_show_time, null);
        TextView message = (TextView) view.findViewById(R.id.popup_sms_message);
        TextView ok = (TextView) view.findViewById(R.id.pupop_agin_get_sms_tv);
        ok.setOnClickListener(this);
        ok.setText(mContext.getResources().getString(R.string.confirm));
        ok.setTag(state); // 继续传给重发
        message.setText(mContext.getResources().getString(R.string.retry));
        superDialog = new AlertDialog.Builder(mContext)//
                .setView(view)//
                .create();
        superDialog.show();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.userhead_iv:
                ViewHolder viewHolder = (ViewHolder) v.getTag();
                if (viewHolder != null && viewHolder.isComMsg) {
                    ((DialogueActivity) mAct).toProfileLogin(viewHolder.mUser);
                } else {
                    ((DialogueActivity) mAct).toProfileLogin(viewHolder.mUser);
                }

                break;
            case R.id.resend_tv:
                resendSms(v.getTag());
                break;
            case R.id.resend_iv:
                // 点了失败消息旁的叹号
                resend((String) v.getTag());
                break;
            case R.id.pupop_agin_get_sms_tv:
                // 点了失败消息的叹号，然后确认重发
                if (NetworkUtil.isNetworkAvailable()) {
                    ToastUtil.shortToast(mContext, R.string.posting);
                    // 获取对应的消息id，重发指定的
                    String state = (String) v.getTag();
                    resendFailMessage(state);
                } else {
                    ToastUtil.shortToast(mContext, R.string.network_error);
                }
                // uploadSms(mSendingSms, mSendingFile, mSendingViewHolder);
                if (superDialog != null && superDialog.isShowing()) {
                    superDialog.dismiss();
                }
                break;
            case R.id.iv_chatcontent:
            case R.id.iv_chatcontent_fl:
                break;
            case R.id.iv_chatcontent_box:
                Message message = (Message) v.getTag();
                if (message != null && message instanceof ImageMessage) {
                    ImageMessage imageMessage = (ImageMessage) message;
                    PopupWindow popupWindow = readyPopUpWindow(imageMessage);
                    ((DialogueActivity) mAct).hidenInputMedthod();
                    if (popupWindow.isShowing()) {
                        popupWindow.dismiss();
                    } else {
                        popupWindow.setAnimationStyle(R.style.popwindow_avactor_anim_style);
                        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
                    }
                }
                break;

            default:
                break;
        }
    }

    /**
     * @param tag
     * @Description:重发消息
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2014-12-30
     */
    private void resendSms(Object tag) {
        ViewHolder viewHolder = (ViewHolder) tag;
        if (viewHolder != null) {
            new ReportBlackAlohaPopup().showPopup(PopupType.RESEND_CHAT_TEXT, PostType.RESEND_CHAT_TEXT, null, mContext.getResources().getString(R.string.retry), null, null, viewHolder);
        }
    }

    public class ViewHolder {

        public TextView tvSendTime;
        public TextView tv_chatcontent;
        public boolean isComMsg = true;
        public CircleImageView userhead_iv;

        public ImageView iv_chatcontent;
        public RelativeLayout iv_chatcontent_fl;
        public ImageView iv_chatcontent_box;

        public RelativeLayout tv_chatcontent_fl;
        public FrameLayout fl_rechat;
        public ImageView iv_rechat;
        // 发送中
        public ProgressBar right_pb, right_pb_tv;
        // 重发
        public ImageView resend_iv, resend_tv;

        /**
         * 如果是文本 = 1、如果是图片=0、如果是语音=-1
         */
        @Deprecated
        public int smsType;
        public User mUser;
        private Message mMessage;

        public void setMessage(Message message) {
            mMessage = message;
        }

        public Message getMessage() {
            return mMessage;
        }

    }

    private void findById(final ViewGroup parent, View convertView, final ViewHolder viewHolder, final boolean isComMsg, final int position) {
        viewHolder.iv_chatcontent_box = (ImageView) convertView.findViewById(R.id.iv_chatcontent_box);
        viewHolder.iv_chatcontent_fl = (RelativeLayout) convertView.findViewById(R.id.iv_chatcontent_fl);
        viewHolder.tv_chatcontent_fl = (RelativeLayout) convertView.findViewById(R.id.tv_chatcontent_fl);
        viewHolder.userhead_iv = (CircleImageView) convertView.findViewById(R.id.userhead_iv);
        viewHolder.tvSendTime = (TextView) convertView.findViewById(R.id.tv_sendtime);
        viewHolder.iv_chatcontent = (ImageView) convertView.findViewById(R.id.iv_chatcontent);
        viewHolder.tv_chatcontent = (TextView) convertView.findViewById(R.id.tv_chatcontent);
        viewHolder.isComMsg = isComMsg;
        if (isComMsg) {
            viewHolder.right_pb = (ProgressBar) convertView.findViewById(R.id.right_pb);
            viewHolder.resend_iv = (ImageView) convertView.findViewById(R.id.resend_iv);
            viewHolder.resend_tv = (ImageView) convertView.findViewById(R.id.resend_tv);
            viewHolder.right_pb_tv = (ProgressBar) convertView.findViewById(R.id.right_pb_tv);
            viewHolder.resend_iv.setOnClickListener(this);
            viewHolder.resend_tv.setOnClickListener(this);
            viewHolder.resend_tv.setTag(viewHolder);
        }
        fontUtil.changeViewFont(viewHolder.tv_chatcontent, Font.ENCODESANSCOMPRESSED_400_REGULAR);
        viewHolder.userhead_iv.setTag(viewHolder);
        viewHolder.userhead_iv.setOnClickListener(this);
        // FIXME 使用Bundle
        viewHolder.iv_chatcontent_box.setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                contorlSinglSms(viewHolder, position);
                return true;
            }
        });
        viewHolder.tv_chatcontent.setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                contorlSinglSms(viewHolder, position);
                return true;
            }
        });
    }

    /**
     * 打开看大图
     *
     * @param imageMessage
     * @return
     */
    private PopupWindow readyPopUpWindow(ImageMessage imageMessage) {

        View popwin_layout = LayoutInflater.from(mContext).inflate(R.layout.popwin_avactor, null);
        ImageView avactor = (ImageView) popwin_layout.findViewById(R.id.pop_imgview);
        LinearLayout ll = (LinearLayout) popwin_layout.findViewById(R.id.container_layout);
        final PopupWindow popUpWindow = new PopupWindow(popwin_layout);
        popUpWindow.setOutsideTouchable(true);
        popUpWindow.setFocusable(true);
        popUpWindow.setBackgroundDrawable(new ColorDrawable());
        popUpWindow.setTouchable(true);
        popUpWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popUpWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        if (imageMessage.isLocal) {
            picasso.load(new File(imageMessage.image.getUrl())).into(avactor);
        } else {
            // 屏幕寬度
            String url = ImageUtil.getImageUrl(imageMessage.image.getId(), UiUtils.getScreenWidth(mContext), null);
            picasso.load(url).into(avactor);
        }
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

    /**
     * @param cViewHolder
     * @Description:再次发送消息
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2014-12-30
     */
    public void anginSendSms(ViewHolder cViewHolder) {
        if (cViewHolder.mMessage instanceof TextMessage) {
            uploadSms(cViewHolder.mMessage, null, cViewHolder);
        } else {
            ImageMessage message = (ImageMessage) cViewHolder.mMessage;
            if (message.isLocal) {
                try {
                    File file = new File(message.image.getUrl());
                    uploadSms(cViewHolder.mMessage, file, cViewHolder);
                } catch (Throwable e) {
                }
            }
        }
    }

    /**
     * @author:sunkist
     * @see:
     * @since:
     * @description 消息發送后的回调處理.
     * @copyright wealoha.com
     * @Date:2015-1-4
     */
    class SmsSendDealCallBack implements Callback<Result<InboxMessageResult>> {

        private ViewHolder mViewHolder;
        private String mState;

        public SmsSendDealCallBack(ViewHolder viewHolder, String state) {
            mViewHolder = viewHolder;
            mState = state;
        }

        @Override
        public void failure(RetrofitError arg0) {
            sendingMessageFail(mState);
        }

        @Override
        public void success(Result<InboxMessageResult> result, Response arg1) {
            if (result != null && result.isOk()) {
                final Message newMessage = result.getData().getList().get(0);
                sendingMessageSuccess(newMessage);
                // changeUI(View.GONE, View.GONE);
            } else if (result.getData().getError() == ResultData.ERROR_BLOCK_BY_OTHER) {
                // 發送失敗
                ToastUtil.shortToast(mContext, mContext.getString(R.string.failed_to_send_message_cause_blocked));
                sendingMessageFail(mState);
            } else {
                ToastUtil.shortToast(mContext, mContext.getString(R.string.send_message_error));
                sendingMessageFail(mState);
            }
        }

        void changeUI(int pbDisplay, int errorIv) {
            mViewHolder.resend_iv.setVisibility(errorIv);
            mViewHolder.right_pb.setVisibility(pbDisplay);
        }
    }

    /**
     * @param position object
     *                 message
     * @Description: 单条消息的操作
     * tag
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2015-1-5
     */
    private void contorlSinglSms(final ViewHolder viewHolder, final int position) {
        Bundle bundle = new Bundle();
        reportBlackAlohaPopup = new ReportBlackAlohaPopup(mContext);
        mPopupWindow = reportBlackAlohaPopup.showPopup(viewHolder, PopupType.CONTROL_CHAT_SMS, viewHolder.mUser.getName(), bundle, new OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.popup_box_one:
                        isTextCopy(viewHolder);
                        break;
                    case R.id.popup_box_two:
                        delSingleSms(viewHolder, position);
                        break;
                    default:
                        break;
                }
                reportBlackAlohaPopup.closeDialog();
            }
        });
    }

    /**
     * @param viewHolder
     * @param position
     *  isTextMsg
     * @Description:删除单条的文本信息
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2015-1-5
     */
    private void delSingleSms(final ViewHolder viewHolder, final int position) {
        if (viewHolder.getMessage().isLocal) {// 如果Message是本地的.
            mMessageList.remove(mMessageList.indexOf(viewHolder.getMessage()));
            cacheSave();
            cacheRestore();
            return;
        } else {
            mMessageService.delSingleSms(mSessionId, viewHolder.getMessage().id, new Callback<Result<ResultData>>() {

                @Override
                public void failure(RetrofitError arg0) {
                    ToastUtil.shortToast(mContext, R.string.is_work);// 找不到该条消息
                }

                @Override
                public void success(Result<ResultData> arg0, Response arg1) {
                    try {
                        mMessageList.remove(mMessageList.indexOf(viewHolder.getMessage()));
                        notifyDataSetChanged();
                    } catch (NullPointerException e) {
                        ToastUtil.shortToast(mContext, R.string.is_not_work);// 找不到该条消息
                    }
                }
            });
        }
    }

    /**
     * @param viewHolder
     * @Description:只有文本消息才能拷贝
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2015-1-5
     */
    private void isTextCopy(ViewHolder viewHolder) {
        try {
            TextMessage textMessage = (TextMessage) viewHolder.getMessage();
            String text = textMessage.text;
            ClipboardManager cmb = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
            ToastUtil.shortToast(mContext, R.string.is_work);
            cmb.setPrimaryClip(ClipData.newPlainText(null, text));
        } catch (Throwable e) {
            ToastUtil.shortToast(mContext, R.string.is_not_work);
            XL.d(TAG, e.getMessage());
        }
    }
}
