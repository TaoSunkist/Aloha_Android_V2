package com.wealoha.social.push.model;

import android.app.Activity;
import android.content.Context;

import com.wealoha.social.AppApplication;
import com.wealoha.social.R;
import com.wealoha.social.activity.MainAct;
import com.wealoha.social.event.NewCommentPushEvent;
import com.wealoha.social.interfaces.NotifyPush;
import com.wealoha.social.push.NoticeBarController;
import com.wealoha.social.push.notification.NotificationCount;
import com.wealoha.social.push.notification.PostCommentNotification;
import com.wealoha.social.utils.Utils;
import com.wealoha.social.utils.XL;

public class PostCommentPush extends BasePush implements NotifyPush<PostCommentNotification> {

	public static final String ALOHA_PUSH_Notification_Post_Comment = "ALOHA_PUSH_Notification_Post_Comment";
	public static final String ALOHA_PUSH_Notification_Post_Comment_NoDetail = "ALOHA_PUSH_Notification_Post_Comment_NoDetail";
	public static final String ALOHA_PUSH_Notification_Post_Comment_Reply = "ALOHA_PUSH_Notification_Post_Comment_Reply";
	public static final String ALOHA_PUSH_Notification_Post_Comment_Reply_On_My_Post = "ALOHA_PUSH_Notification_Post_Comment_Reply_On_My_Post";
	public static final String ALOHA_PUSH_Notification_Post_Comment_Reply_On_Others_Post = "ALOHA_PUSH_Notification_Post_Comment_Reply_On_My_Post";

	public PostCommentPush(Context ctx) {
		mCtx = ctx;
	}

	@Override
	public void sendNotification(PostCommentNotification notification) {
		NotificationCount.setCommentCount();
		if (Utils.isApplicationBroughtToBackground(AppApplication.getInstance())) {// 在后台
			
			XL.i("NoticeBarController", "key:" + notification.getAlertLocKey());
			String msg = null;
			if (ALOHA_PUSH_Notification_Post_Comment.equals(notification.getAlertLocKey())) {
				// 留言的通知
				msg = mCtx.getString(R.string.aloha_push_notification_post_comment);
			} else if (ALOHA_PUSH_Notification_Post_Comment_NoDetail.equals(notification.getAlertLocKey())) {
				// 留言的通知：不顯示詳情
				msg = mCtx.getString(R.string.aloha_push_notification_post_nodetail);
			} else if (ALOHA_PUSH_Notification_Post_Comment_Reply.equals(notification.getAlertLocKey())) {
				// 留言被回覆的通知
				msg = mCtx.getString(R.string.aloha_push_notification_post_comment_reply);
			}else if(ALOHA_PUSH_Notification_Post_Comment_Reply_On_My_Post.equals(notification.getAlertLocKey())){
				msg = mCtx.getString(R.string.aloha_push_post_comment_reply_on_my_post);
			}else if(ALOHA_PUSH_Notification_Post_Comment_Reply_On_Others_Post.equals(notification.getAlertLocKey())){
				msg = mCtx.getString(R.string.aloha_push_post_comment_reply_on_others_post);
			}
			NoticeBarController.getInstance(mCtx).showNewCommentNotice(mCtx, notification, msg);
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
