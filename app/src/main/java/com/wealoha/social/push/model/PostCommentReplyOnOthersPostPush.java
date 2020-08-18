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
import com.wealoha.social.push.notification.PostCommentReplyOnOthersPostNotification;
import com.wealoha.social.utils.Utils;
import com.wealoha.social.utils.XL;

public class PostCommentReplyOnOthersPostPush extends BasePush implements NotifyPush<PostCommentReplyOnOthersPostNotification> {

	public static final String ALOHA_PUSH_Notification_Post_Comment_Reply_On_Others_Post = "ALOHA_PUSH_Notification_Post_Comment_Reply_On_My_Post";

	public PostCommentReplyOnOthersPostPush(Context ctx) {
		mCtx = ctx;
	}

	@Override
	public void sendNotification(PostCommentReplyOnOthersPostNotification notification) {
		NotificationCount.setCommentCount();
		if (Utils.isApplicationBroughtToBackground(AppApplication.getInstance())) {// 在后台
			
			XL.i("NoticeBarController", "key:" + notification.getAlertLocKey());
			String msg = null;
			if(ALOHA_PUSH_Notification_Post_Comment_Reply_On_Others_Post.equals(notification.getAlertLocKey())){
				
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
