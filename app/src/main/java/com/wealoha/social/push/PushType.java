package com.wealoha.social.push;

import com.wealoha.social.R;

public enum PushType {
	/** 聊天消息 */
	ALOHA_PUSH_Notification_New_Message_Text("ALOHA_PUSH_Notification_New_Message_Text",//
			R.string.aloha_push_notification_new_message_text), //
	ALOHA_PUSH_Notification_New_Message_Photo("ALOHA_PUSH_Notification_New_Message_Photo",//
			R.string.aloha_push_notification_new_message_photo), //
	ALOHA_PUSH_Notification_New_Message_NoDetail("ALOHA_PUSH_Notification_New_Message_NoDetail",//
			R.string.aloha_push_notification_new_message_nodetail), //
	ALOHA_PUSH_Notification_New_Match("ALOHA_PUSH_Notification_New_Match",//
			R.string.aloha_push_notification_new_match), //
	ALOHA_PUSH_Notification_Post_Like("ALOHA_PUSH_Notification_Post_Like",//
			R.string.aloha_push_notification_post_like), //
	ALOHA_PUSH_Notification_Post_Like_NoDetail("ALOHA_PUSH_Notification_Post_Like_NoDetail",//
			R.string.aloha_push_notification_post_nodetail), //
	ALOHA_PUSH_Notification_Post_Comment("ALOHA_PUSH_Notification_Post_Comment",//
			R.string.aloha_push_notification_post_comment), //
	ALOHA_PUSH_Notification_Post_Comment_NoDetail("ALOHA_PUSH_Notification_Post_Comment_NoDetail",//
			R.string.aloha_push_notification_post_nodetail), //
	ALOHA_PUSH_Notification_Instagram_Sync_Done("ALOHA_PUSH_Notification_Instagram_Sync_Done",//
			R.string.instagram_have_finshed), //
	ALOHA_PUSH_Notification_Post_Comment_Reply("ALOHA_PUSH_Notification_Post_Comment_Reply",//
			R.string.aloha_push_notification_post_comment_reply), //
	ALOHA_PUSH_Notification_Aloha("ALOHA_PUSH_Notification_Aloha",//
			R.string.aloha_push_notification_aloha), //
	ALOHA_PUSH_Notification_Aloha_NoDetail("ALOHA_PUSH_Notification_Aloha_NoDetail",//
			R.string.aloha_push_notification_aloha), //
	;
	private final String pushType;
	private final int pushResStr;

	private PushType(String pushType, int pushResStr) {
		this.pushType = pushType;
		this.pushResStr = pushResStr;
	}

	public String getPushType() {
		return pushType;
	}

	public int getPushResStr() {
		return pushResStr;
	}

}
