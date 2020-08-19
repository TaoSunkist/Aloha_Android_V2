package com.wealoha.social.push;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.otto.Bus;
import com.wealoha.social.AppApplication;
import com.wealoha.social.ContextConfig;
import com.wealoha.social.api.ProfileService;
import com.wealoha.social.commons.GlobalConstants;
import com.wealoha.social.event.push.NewNotifyEvent;
import com.wealoha.social.inject.Injector;
import com.wealoha.social.interfaces.NotifyPush;
import com.wealoha.social.push.model.AlohaTimePush;
import com.wealoha.social.push.model.InboxMessageNewPush;
import com.wealoha.social.push.model.InstagramSyncPush;
import com.wealoha.social.push.model.NewHashtagPush;
import com.wealoha.social.push.model.NewMatchPush;
import com.wealoha.social.push.model.PostCommentPush;
import com.wealoha.social.push.model.PostCommentReplyOnMyPostPush;
import com.wealoha.social.push.model.PostCommentReplyOnOthersPostPush;
import com.wealoha.social.push.model.PostLikePush;
import com.wealoha.social.push.notification.Notification;
import com.wealoha.social.utils.ContextUtil;
import com.wealoha.social.utils.PushUtil;
import com.wealoha.social.utils.XL;
import com.xiaomi.mipush.sdk.ErrorCode;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaomi.mipush.sdk.MiPushCommandMessage;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageReceiver;

public class XmPushBroadcast extends PushMessageReceiver {

	private static final String TAG = XmPushBroadcast.class.getSimpleName();
	@Inject
	PushUtil pushUtil;
	private String mRegId;
	private String mMessage;
	private String mTopic;
	private String mAlias;
	@Inject
	ContextUtil contextUtil;
	@Inject
	Gson gson;
	@Inject
	ProfileService mProfileService;

	@Inject
	Bus bus;
	private Map<Push2Type, NotifyPush> notifycationMap;

	public static InboxMessageNewPush inboxMessageNewPush = new InboxMessageNewPush(AppApplication.getInstance());
	public static NewMatchPush newMatchPush = new NewMatchPush(AppApplication.getInstance());
	public static PostLikePush postLikePush = new PostLikePush(AppApplication.getInstance());
	public static InstagramSyncPush value = new InstagramSyncPush(AppApplication.getInstance());
	public static AlohaTimePush alohaTimePush = new AlohaTimePush(AppApplication.getInstance());
	public static PostCommentPush postCommentPush = new PostCommentPush(AppApplication.getInstance());
	public static NewHashtagPush newHashtagPush = new NewHashtagPush(AppApplication.getInstance());
	public static PostCommentReplyOnMyPostPush postCommentReplyOnMyPostPush = new PostCommentReplyOnMyPostPush(AppApplication.getInstance());
	public static PostCommentReplyOnOthersPostPush postCommentReplyOnOthersPostPush = new PostCommentReplyOnOthersPostPush(AppApplication.getInstance());

	public XmPushBroadcast() {
		super();
		Injector.inject(this);
		// 注册所有的push类型
		notifycationMap = new HashMap<>();
		notifycationMap.put(Push2Type.InboxMessageNew, inboxMessageNewPush);
		notifycationMap.put(Push2Type.NewMatch, newMatchPush);
		notifycationMap.put(Push2Type.PostLike, postLikePush);
		notifycationMap.put(Push2Type.InstagramSyncDone, value);
		notifycationMap.put(Push2Type.AlohaTime, alohaTimePush);
		notifycationMap.put(Push2Type.PostComment, postCommentPush);
		notifycationMap.put(Push2Type.NewHashtag, newHashtagPush);
		notifycationMap.put(Push2Type.PostCommentReplyOnMyPost, postCommentReplyOnMyPostPush);
		notifycationMap.put(Push2Type.PostCommentReplyOnOthersPost, postCommentReplyOnOthersPostPush);
	}

	@Override
	public void onReceiveMessage(Context context, MiPushMessage message) {
		mMessage = message.getContent();
		XL.i("XM_PUSHBROAD_CAST", "message:" + message.getContent());
		if (mMessage != null) {
			Notification notification = null;
			try {
				notification = gson.fromJson(mMessage, new TypeToken<Notification>() {
				}.getType());
				XL.i("XM_PUSHBROAD_CAST", "notification:" + notification);
				if (notification == null) {
					return;
				}
			} catch (Throwable e) {
				return;
			}
			
			NotifyPush<Notification> notifyPush = notifycationMap.get(notification.getType());
			XL.i("XM_PUSHBROAD_CAST", "notifyPush:" + notifyPush);
			if (notifyPush == null) {
				return;
			} else {
				notifyPush.sendNotification(notification);
			}
			if (notification.notifyCount > 0) {
				bus.post(new NewNotifyEvent(notification.notifyCount));
			}
		}

		if (!TextUtils.isEmpty(message.getTopic())) {
			setTopic(message.getTopic());
		} else if (!TextUtils.isEmpty(message.getAlias())) {
			setAlias(message.getAlias());
		}
	}

	@Override
	public void onCommandResult(Context context, MiPushCommandMessage message) {
		String command = message.getCommand();
		List<String> arguments = message.getCommandArguments();
		String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
		// XL.d("cmdArg", "cmdArg1" + cmdArg1 + "/cmdArg2:" + cmdArg2);
		if (MiPushClient.COMMAND_REGISTER.equals(command)) {
			if (message.getResultCode() == ErrorCode.SUCCESS) {
				mRegId = cmdArg1;
				bindUserToServer(mRegId);
			}
		} else if (MiPushClient.COMMAND_SET_ALIAS.equals(command)) {
			if (message.getResultCode() == ErrorCode.SUCCESS) {
				setAlias(cmdArg1);
			}
		} else if (MiPushClient.COMMAND_UNSET_ALIAS.equals(command)) {
			if (message.getResultCode() == ErrorCode.SUCCESS) {
				setAlias(cmdArg1);
			}
		} else if (MiPushClient.COMMAND_SUBSCRIBE_TOPIC.equals(command)) {
			if (message.getResultCode() == ErrorCode.SUCCESS) {
				setTopic(cmdArg1);
			}
		} else if (MiPushClient.COMMAND_UNSUBSCRIBE_TOPIC.equals(command)) {
			if (message.getResultCode() == ErrorCode.SUCCESS) {
				setTopic(cmdArg1);
			}
		} else if (MiPushClient.COMMAND_SET_ACCEPT_TIME.equals(command)) {
			if (message.getResultCode() == ErrorCode.SUCCESS) {
			}
		}
	}

	public void bindUserToServer(String regId) {
		ContextConfig.getInstance().putStringWithFilename(GlobalConstants.AppConstact.GETUI_PUSH_TOKEN, regId);
		pushUtil.tryBindGetuiPushToken(regId);
	}

	public String getTopic() {
		return mTopic;
	}

	public void setTopic(String mTopic) {
		this.mTopic = mTopic;
	}

	public String getAlias() {
		return mAlias;
	}

	public void setAlias(String mAlias) {
		this.mAlias = mAlias;
	}

	public void dispensePush() {

	}
}
