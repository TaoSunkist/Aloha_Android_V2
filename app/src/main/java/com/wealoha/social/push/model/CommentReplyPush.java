package com.wealoha.social.push.model;

import android.app.Activity;
import android.content.Context;

import com.wealoha.social.AppApplication;
import com.wealoha.social.R;
import com.wealoha.social.activity.MainAct;
import com.wealoha.social.event.NewCommentPushEvent;
import com.wealoha.social.interfaces.NotifyPush;
import com.wealoha.social.push.NoticeBarController;
import com.wealoha.social.push.PushType;
import com.wealoha.social.push.notification.AlohaPushNotification;
import com.wealoha.social.push.notification.NotificationCount;
import com.wealoha.social.utils.Utils;

public class CommentReplyPush extends BasePush implements NotifyPush<AlohaPushNotification> {
	private String ALOHA_PUSH_Notification_Post_Comment_Reply = "ALOHA_PUSH_Notification_Post_Comment_Reply";
	private String ALOHA_PUSH_Notification_Aloha_NoDetail = "ALOHA_PUSH_Notification_Aloha_NoDetail";

	public CommentReplyPush(Context ctx) {
		mCtx = ctx;
	}

	@Override
	public void sendNotification(AlohaPushNotification notification) {
		String msg = null;
		NotificationCount.setCommentCount();
		if (Utils.isApplicationBroughtToBackground(AppApplication.getInstance())) {// 在后台
			if (ALOHA_PUSH_Notification_Post_Comment_Reply.equals(notification.getAlertLocKey())) {
				// 留言被回覆的通知
				msg = mCtx.getString(R.string.aloha_push_notification_post_comment_reply);
			} else if (PushType.ALOHA_PUSH_Notification_Aloha.getPushType().equals(notification.getAlertLocKey()) || //
					ALOHA_PUSH_Notification_Aloha_NoDetail.equals(notification.getAlertLocKey())) {
				// 留言被回覆的通知：不現實詳情
				msg = mCtx.getString(R.string.aloha_push_notification_aloha);
			}
			NoticeBarController.getInstance(mCtx).showAlohaPushNotice(mCtx, notification, msg);
		} else {
			Activity act = contextUtil.getForegroundAct();
			if (act == null) {
				return;
			}
			if (act instanceof MainAct) {
				((MainAct) act).busSentEvent(new NewCommentPushEvent());
			}
		}
	}
}
