package com.wealoha.social.push;

public enum Push2Type {
	InboxMessageNew("InboxMessageNew"), // 聊天的
	NewMatch("NewMatch"), // 匹配的通知
	PostLike("PostLike"), // 喜歡的通知
	PostComment("PostComment"), // 評論的通知
	InstagramSyncDone("InstagramSyncDone"), // instagram的同步
	Aloha("Aloha"), // Aloha的通知
	AlohaTime("AlohaTime"), // Aloha时间到的通知
	NewHashtag("NewHashtagActivity"), // 话题的push
	PostCommentReplyOnMyPost("PostCommentReplyOnMyPost"), //
	PostCommentReplyOnOthersPost("PostCommentReplyOnOthersPost"), //
	;
	private final String type;

	Push2Type(String type) {
		this.type = type;
	}

	public String getType() {
		return this.type;
	}
}
