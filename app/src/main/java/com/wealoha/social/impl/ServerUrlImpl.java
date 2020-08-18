package com.wealoha.social.impl;

/**
 * 这是给rest风格的api请求用的
 */
public interface ServerUrlImpl {

	public String GET_TIME_LINE_FEED = "/v1/feed";
	public String LOAD_USER_FEED = "/v1/feed/user";
	public String FEED_DETAIL = "/v1/feed/detail";
	public String LOAD_OTHER_USER_DATA = "/v1/user/match";
	public String ALOHA_LIKE = "/v1/user/match/like";
	public String ALOHA_DISLIKE = "/v1/user/match/dislike";
	public String GET_USER_PROMOTION = "/v1/user/promotion";
	public String URL_USER_SETTING_PUSH = "/v1/user/setting/push";
	public String PUSH_UNBINDING = "/v1/user/push/unbind/android/xiaomi";
	public String LOGOUT_URL = "/v1/user/unauth";
	public String CONNECT_WEIBO = "/v1/connect/weibo";
	public String GET_THE_SESSION_LIST = "/v1/inbox/session";
	public String GET_SMS_LIST = "/v1/inbox/session/message/";
	public String CONSTANTS_API_ENDPOINT = "/v1/constants/api/endpoint";
	public String CONSTANTS_API_IMAGE_ENDPOINT = "/v1/user/setting/image/endpoint";
	public String CONSTANTS = "/v1/constants";
	public String REPORT_FEED = "/v1/feed/report";
	public String REPORT_USER = "/v1/user/report";
	public String BLACK_USER = "/v1/user/blacklist/block";
	public String CANCEL_ALOHA_USER = "/v1/user/match/dislike";
	// 记录名片点击
	public String RECORD_CARD_CLICK = "/v1/user/link/click";
	public String USER_PROFILE_VIEW = "/v1/user/profile/view";
	// 获取单个会话
	public String GET_SINGLE_SESSION = "/v1/inbox/session/get";
	// 创建一个会话
	public String CREATE_SESSION = "/v1/inbox/session/create";
	public String INBOX_SESSION_CLEAR_UNREAD = "/v1/inbox/session/clearUnread";
	public String PUSH_BINDING = "/v1/user/push/bind/android/xiaomi";
	public String INBOX_SESSION_GET = "/v1/inbox/session/get";
	public String INBOX_SESSION_UNREAD = "/v1/inbox/unread";
	public String BLACK_LIST = "/v1/user/blacklist";
	public String LIKE_LIST = "/v1/user/match/liked";
	public String POPULARITY_LIST = "/v1/user/match/likedme";
	public String UNBLOCK = "/v1/user/blacklist/unblock";
	// 赞
	public String PRAISE_USER_FEED = "/v1/feed/like";
	// 取消赞
	public String DISLIKE_USER_FEED = "/v1/feed/like/cancel";
	// 删除feed
	public String DELETE_USER_FEED = "/v1/feed/delete";
	public String DELETE_TAG = "/v1/feed/delete/tag";
	// 喜欢这个feed的人们
	public String GET_FEED_LIKE = "/v1/feed/like";
	// 获取评论
	public String GET_COMMENT = "/v1/feed/comment";
	// 通知列表
	public String GET_NOTIFY = "/v1/feed/notify";
	public String GET_NOTIFY_TWO = "/v1/feed/notify2";
	@Deprecated
	public String NOTIFY_CLEAR_UNREAD = "/v1/feed/notify/clearUnread";
	public String POST_COMMENT = "/v1/feed/comment";
	public String POST_COMMENT_V2 = "/feed/comment/v2";
	public String DELETE_COMMENT = "/v1/feed/comment/delete";
	public String COUNT = "/v1/count";
	/** 注册登录 */
	public String REGISTER_LOGIN = "/v1/user/register/mobile/verify";
	/** 登录 */
	public String LOGIN = "/v1/user/auth";

	public String SEND_TO_USER_IMG = "/v1/inbox/send/image";
	public String SEND_TEXT_SMS = "/v1/inbox/send/text";
	/** 登录方式 */
	public String LOGIN_TYPE = "/v1/user/passport/info";

	/** instagram同步 */
	public String INSTAGRAM = "/oauth/access_token";

	/** 向服务器提交instagram token */
	public String POST_INSTAGRAM_TOKEN = "/v1/connect/bind/instagram";
	public String UNBIND_INSTAGRAM = "/v1/connect/unbind/instagram";
	public String SET_AUTO = "/v1/connect/instagram/autoSync";
	public String DEL_SINGLE_SMS = "/v1/inbox/message/delete";
	/** 向服务器提交Log信息 */
	public String CLIENT_LOG = "/v1/stat/client/log";
	/** 搜索 */
	public String FIND_YOU = "/v1/match/aloha/search";
	public String TAG_SUGGEST = "/v1/feed/publish/tag/suggest";
	/** 定位 */
	public String LOCATION = "/v1/venue";
	/** 注册位置 */
	public String LOCATION_RECORD = "/v1/lbs/register";
	/** 发送feed */
	public String UPLOAD_FEED = "/v1/feed/publish/image";
	public String UPLOAD_FEED_JPG = "/v1/file/upload/image";
	/**
	 * 请求修改密码的API
	 */
	public static String RESET_PASSWORD_USER = "/v1/user/password/change/mobile";
	/**
	 * 请求修改用户的profile信息;
	 */
	public static String CHANGE_PROFILE = "/v1/user/profile/modify";
	/**
	 * 分享Feed发送的请求
	 */
	public static String FEED_SHARE = "/v1/post/link/share";
	/**
	 * feedWeb頁分享的點擊呼氣APP時，調用的接口
	 */
	public static String FEED_WEB_CALL_ALOHAAPP = "/v1/post/link/click";
}
