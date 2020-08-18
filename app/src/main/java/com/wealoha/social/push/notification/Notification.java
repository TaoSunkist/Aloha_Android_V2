package com.wealoha.social.push.notification;

import java.io.Serializable;
import java.util.List;

import android.content.Context;

import com.google.gson.annotations.SerializedName;
import com.wealoha.social.push.Push2Type;
import com.wealoha.social.utils.Utils;

/**
 * Push消息
 * 
 * @author javamonk
 * @see
 * @since
 * @date 2014-11-11 下午11:10:49
 */
public abstract class Notification implements Serializable {

	private static final long serialVersionUID = -8845501456166092483L;

	public static final String TYPE = Notification.class.getSimpleName();

	public int notifyCount;
	public int messageCount;

	public static class Alert implements Serializable {

		private static final long serialVersionUID = 5917541401051290849L;

		@SerializedName("loc-key")
		public String locKey;
		@SerializedName("loc-args")
		public List<String> locArgs;
	}

	public static class Aps implements Serializable {

		private static final long serialVersionUID = -3869919705652473656L;

		public Alert alert;
		public int badge;
		public String sound;

	}

	public Aps aps;
	public String type;

	/**
	 * 未读消息数
	 */
	public int getBadge() {
		return aps.badge;
	}

	/**
	 * 声音
	 * 
	 * @return
	 */
	public String getSound() {
		return aps.sound;
	}

	/**
	 * 提醒的resource-key
	 * 
	 * @return
	 */
	public String getAlertLocKey() {
		return aps.alert.locKey;
	}

	/**
	 * 提醒的resource-key文本替换参数
	 * 
	 * @return
	 */
	public List<String> getAlertLocArgs() {
		return (List<String>) aps.alert.locArgs;
	}

	/** 前台展示：默认true */
	public boolean mDisableForeground = true;

	public boolean isMsgNeedToHandle(Context ctx) {
		// 客户端在前台不执行push消息
		if (Utils.isAppForground(ctx) && mDisableForeground) {
			return false;
		} else {
			return true;
		}
	}

	public Push2Type getType() {
		if (Push2Type.Aloha.getType().equals(type)) {
			return Push2Type.Aloha;
		} else if (Push2Type.AlohaTime.getType().equals(type)) {
			return Push2Type.AlohaTime;
		} else if (Push2Type.InboxMessageNew.getType().equals(type)) {
			return Push2Type.InboxMessageNew;
		} else if (Push2Type.InstagramSyncDone.getType().equals(type)) {
			return Push2Type.InstagramSyncDone;
		} else if (Push2Type.NewMatch.getType().equals(type)) {
			return Push2Type.NewMatch;
		} else if (Push2Type.PostComment.getType().equals(type)) {
			return Push2Type.PostComment;
		} else if (Push2Type.PostLike.getType().equals(type)) {
			return Push2Type.PostLike;
		} else if (Push2Type.NewHashtag.getType().equals(type)) {
			return Push2Type.NewHashtag;
		} else if(Push2Type.PostCommentReplyOnMyPost.getType().equals(type)){
			return Push2Type.PostCommentReplyOnMyPost;
		} else if(Push2Type.PostCommentReplyOnOthersPost.getType().equals(type)){
			return Push2Type.PostCommentReplyOnOthersPost;
		}else {
			return null;
		}
	}
}
