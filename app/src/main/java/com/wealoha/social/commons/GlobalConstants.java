package com.wealoha.social.commons;

import java.io.File;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

import android.net.Uri;
import android.os.Environment;

import com.google.gson.reflect.TypeToken;
import com.wealoha.social.beans.FeedResult;
import com.wealoha.social.beans.User;
import com.wealoha.social.beans.message.InboxSessionResult;
import com.wealoha.social.impl.ServerUrlImpl;
import com.wealoha.social.utils.ContextHolder;

/**
 * @author:sunkist
 * @see:
 * @since:
 * @description 全局常量
 * @copyright wealoha.com
 * @Date:2014-10-27
 */
public class GlobalConstants {

	// 公共的日期时间格式化对象，都要使用以下现成的方法
	public static final SimpleDateFormat DateFormatHMS = new SimpleDateFormat("HH:mm:ss");
	public static final SimpleDateFormat DateFormatYMDHMS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final SimpleDateFormat DateFormatMDHM = new SimpleDateFormat("MM-dd HH:mm");
	public static final SimpleDateFormat DateFormatYMD = new SimpleDateFormat("yyyyMMdd");
	public static final SimpleDateFormat DateFormatYYMMDDHHMMSS = new SimpleDateFormat("yyyyMMddHHmmss");
	public static final SimpleDateFormat DateFormatEEE = new SimpleDateFormat("EEEE");
	public static final SimpleDateFormat DateFormatHM = new SimpleDateFormat("HH:mm");
	public static final SimpleDateFormat DataFormatCHINESYYMMDD = new SimpleDateFormat("yyyy年MM月dd日");
	public static final SimpleDateFormat DataFormatENYYMMDD = new SimpleDateFormat("yyyy-MM-dd");
	public static final Pattern COMPILE = Pattern.compile("((?:https?://){0,1}[0-9a-z\\-_\\.]+\\.(?:com|net|gov|org|cn|me)(?:/[0-9a-z\\#\\?\\.\\=\\-\\&\\%_/]*)*)", Pattern.CASE_INSENSITIVE);
	public static class PromptInfo {

		/** 网络连接失败 */
		public static final int NETWORK_LINK_FAILURE = 1 << 1;
		/** 网络连接错误 */
		public static final int NETWORK_LINK_ERROR = NETWORK_LINK_FAILURE + 1;
		/** 没有更多数据 */
		public static final int NO_MORE_DATA = NETWORK_LINK_ERROR + 1;
	}

	public static class AppConstact {

		public static String mDelPostId;
		public static int position;
		public static final String IS_FIRST_NOPE = "is_first_nope";
		public static final String REFRESH_RESULT_KEY = "is_first_nope";
		public static final String TRENDS_SCHEMA = "devdiv://sina_profile1";
		public static final String MENTIONS_SCHEMA = "com.youku.paike://message_private_url";
		public static final String PARAM_UID = "uid";
		public static final String CHECK_UPDATE_TIMESTAMP = "check_update_timestamp";
		/** 通知主线程展示头像 */
		public static final int DISPLAY_HAND_PIC = 0x000004;
		/** 是否第一次进入APP的Sharepreference的key */
		public static final String IS_FIRST_ENTER = "isFirstEnter";
		public static final String SHOW_INVITATION_CODE_INPUT = "showInvitationCodeInput";
		public static final String USERNAME = "username";
		public static final String PASSWORD = "password";
		public static boolean IS_UPDATE_HANDPIC = false;
		public static final int SHOW_PROGRESS = 0x000001;
		public static final int REMOVE_PROGRESS = SHOW_PROGRESS + 1;
		public static final int START_TIME = REMOVE_PROGRESS + 1;
		public static final String UPLOAD_FEED_PIC = "upload_feed_pic.jpg";
		public static final File FILE_SDCARD = Environment.getExternalStorageDirectory();
		public static final String CACHE_FOLDER_NAME = Environment.getExternalStorageDirectory() + "wealoha/";
		public static final String CRASH_LOG_FOLDER = CACHE_FOLDER_NAME + "crashAloha/";
		public static final String IMAGE_PATH = "aloha/";
		public static final File FILE_LOCAL = new File(FILE_SDCARD, IMAGE_PATH);
		// 存放聊天的临时图片
		public static final String CHAT_IMG_PATH = FILE_LOCAL + "/chat_temp/";
		/** 截图的图片路径 */
		public static final File FILE_PIC_SCREENSHOT = new File(FILE_LOCAL + "images/screenshots/");
		public static String localTempImageFileName = "user_head_icon.jpg";
		public static String chatSendTempImageFileName = "chat_img.jpg";
		public static String APK_NAME;
		public static String APK_VERSION_CODE;
		/* 网络请求的接口常量 */
		public static final String SINA_APP_KEY = "2988273878";
		public static final String SINA_APP_SECRET = "73624042b781b21a68190be94a176a2a";
		public static final String SINA_REDIRECT_URL = "api.wealoha.com/connect/weibo/";

		public static final String SINA_DEFAULT_REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";
		public static final String SINA_SCOPE = "email,direct_messages_read,direct_messages_write," + "friendships_groups_read,friendships_groups_write,statuses_to_me_read," + "follow_app_official_microblog," + "invitation_write";
		static public final String SINA_WB_NICKNAME = "screen_name";
		public static final String GETUI_KEY = "VVJ7ohdRrK9G287VRHb4S";
		public static final String MONITOR_NOTICE_COLUMN_CLEAR_BROADCAST = "monitor_notice_column_clear_broadcast";
		public static final String IMG_SAVE_SUCCESS = "";
		/**
		 * 文件夹迁移
		 */
		public static final String APP_ROOT_FOLDER = Environment.getExternalStorageDirectory() + "";
		public static final int LOADING_MATCH_DATA_SUCCESS = 1 << 2;
		public static final String QQ_WX_APPID = "wx9b2e625494ba4518";
		public static final String QQ_WX_APP_SECRET = "1934b00f8a5fda5087076ca242d4540c";
		public static final String FACEBOOK_SECRET = "13b3f23c669befeced3cb24c0d85b3ea";
		public static final int FLAG_MODIFY_FINISH = 0x000007;
		public static final int FLAG_CHOOSE_PHONE = 6;
		public static final int FLAG_CHOOSE_IMG = 0x000005;
		public static final int OPEN_COMPOSE_FEED = 0x000008;
		public static final int is_refresh_chat_list = 0x000009;
		public static final int IS_REFRESH_PROFILE_HEAD_ICON = 222;
		public static final int RETURN_NEXT_USER_ALOHA = 333;
		public static final int DISPLAY_ADD_ALOHA = 444;
		/* App内部错误码 所有的请求应该只有三个结果 */
		public static final int CONTROL_SUCCESS = 10000000;
		public static final String IMG_PATH_FROM_PHOTO_PICKED_WITH_DATA = "PHOTO_PICKED_WITH_DATA";
		public static final String IMG_PATH_FROM_CAMERA_WITH_DATA = "CAMERA_WITH_DATA";
		public static final int CONTROL_FAILURE = CONTROL_SUCCESS + 1;
		public static final int SERVER_ERROR = CONTROL_FAILURE + 1;
		public static final int LOADING_SEND_TIMING_NOTICE = 9000;
		public static final String ALARM_REQUEST_CODE = "requestCode";
		public static final String ALARM_TITLE = "title";
		public static final String ALARM_TICKER_TEXT = "tickerText";
		public static final String ALARM_DESCRIPTION = "description";
		public static final String EWM_SIZE = "EWM_SIZE";
		// public static final String
		// SINA_REDIRECT_URL="http://api.wealoha.com/connect/weibo/revoke"
		// 网络请求的参数常量，可用于传值的key，
		/*------------------第三方注册登录------------------*/
		/** 微博用户id */
		public static final String SINA_LOGIN_UID = "uid";
		/** 授权登录的令牌 */
		public static final String SINA_ACCESSTOKEN = "accessToken";
		/* [Feed]发评论 */
		public static final String POST_ID = "postId";
		/* [Feed] */
		public static final String FEED = "FEED";
		public static final String USER_LIST = "USER_LIST";
		/** 可选，回复的用户 */
		public static final String REPLY_USERID = "replyUserId";
		public static final String COMMENT = "comment";
		public static final String SESSION_ID = "id";
		public static final String PUSH_ENABLE = "pushEnable";
		public static final String PUSH_SHOW_DETAIL = "pushShowDetail";
		public static final String PUSH_SOUND = "pushSound";
		public static final String PUSH_VIBRATION = "pushVibration";
		public static final String TOKEN_GETUI = "token";
		public static final String GETUI_PUSH_TOKEN = "pushToken";
		// 包名： com.wealoha.social

		public static final String XIAOMI_PUSH_APPID = "2882303761517280603";
		public static final String XIAOMI_PUSH_APPKEY = "5461728046603";
		public static final String APK_UPDATE_ADDR = "http://wealoha.com/asset/aloha-android.apk";
		public static final int CAMERA_WITH_DATA = 40; // 照相的requestCode;
		public static final int PHOTO_PICKED_WITH_DATA = 41; // 相册的requestCode;

		public static final int SWIPE_MENU_LIST_FRAG_TO_PROFILE_FRAG = 100; // SwipeMenuListFrag
																			// 跳转到
																			// ProfileTestFrag
																			// 的requestCode;
		/** 赞和留言 push的数目 缓存 */
		public static final String POST_PUSH_COUNT_CACHE = "post_push_count";
		public static boolean IS_LAST_USER = false;
		/** 1.6.0 */
		public static final String IS_FIRST_ENTER_160V = "is_first_enter_160v";// 是否第一次进入1.6.0v

	}

	public static class IntentAction {

		public static final Uri INTENT_URI_LOGIN = Uri.parse("aloha://wealoha.com/login/");
		public static final Uri INTENT_URI_REGISTER = Uri.parse("aloha://wealoha.com/register/");
		public static final Uri INTENT_URI_VERIFY = Uri.parse("aloha://wealoha.com/verify/");
		public static final Uri INTENT_URI_USER_DATA = Uri.parse("aloha://wealoha.com/userdata/");
		public static final Uri INTENT_URI_MAIN = Uri.parse("aloha://wealoha.com/main/");
		public static final Uri INTENT_URI_ADVANCEDFEATURED = Uri.parse("aloha://wealoha.com/advancedfeatured/");
		public static final Uri INTENT_URI_CONFIG_DETAILS = Uri.parse("aloha://wealoha.com/configdetails/");
		public static final Uri INTENT_URI_CONFIG_PASSWORD = Uri.parse("aloha://wealoha.com/configpassword/");
		public static final Uri INTENT_URI_LEAVE_COMMENT = Uri.parse("aloha://wealoha.com/leavecomment/");
		public static final Uri INTENT_URI_PIC_SEND = Uri.parse("aloha://wealoha.com/picsend/");
		public static final Uri INTENT_URI_DIALOGUE = Uri.parse("aloha://wealoha.com/dialogue/");
		public static final Uri INTENT_URI_NEWPASSWORD = Uri.parse("aloha://wealoha.com/newpassword/");
		public static final Uri INTENT_URI_INTRODUCTION = Uri.parse("aloha://wealoha.com/introduction/");
		public static final Uri INTENT_URI_LOCATION = Uri.parse("aloha://wealoha.com/location/");
		public static final Uri INTENT_URI_CROPIMAGE = Uri.parse("aloha://wealoha.com/cropimage/");
		public static final Uri INTENT_URI_TOPIC = Uri.parse("aloha://wealoha.com/topic/");
		public static final Uri INTENT_URI_INVITATION = Uri.parse("aloha://wealoha.com/invitation/");
		public static final Uri INTENT_URI_CONFIG_NEWS = Uri.parse("aloha://wealoha.com/notification/");
		public static final Uri INTENT_URI_BLACK_LIST = Uri.parse("aloha://wealoha.com/blacklist/");
		public static final Uri INTENT_URI_FAQ = Uri.parse("aloha://wealoha.com/faq/");
		public static final Uri INTENT_URI_FRAGMENT_TO_ACTIVITY = Uri.parse("aloha://wealoha.com/fragment-to-activity/");
		public static final Uri INTENT_URI_OAUTH_SINA = Uri.parse("aloha://wealoha.com/o_auth_sina/");
		public static final Uri INTENT_URI_FEED_NOTICE = Uri.parse("aloha://wealoha.com/feed_notice/");
		public static final Uri INTENT_URI_CHAT_BIG_IMG = Uri.parse("aloha://wealoha.com/chat_big_img/");
		public static final Uri INTENT_URI_NAVIINTRO = Uri.parse("aloha://wealoha.com/navintro/");
		public static final Uri INTENT_URI_WELCOME = Uri.parse("aloha://wealoha.com/welcome/");
		public static final Uri INTENT_URI_CONFIG_INSTAGRAM = Uri.parse("aloha://wealoha.com/config_instagram/");
		public static final Uri INTENT_URI_INSTAGRAM_WEB = Uri.parse("aloha://wealoha.com/instagram_web/");
		public static final Uri INTENT_URI_CONFIG_HAVE_INSTAGRAM = Uri.parse("aloha://wealoha.com/config_have_instagram/");
		public static final Uri INTENT_URI_WEB = Uri.parse("aloha://wealoha.com/webactivity/");
		public static final Uri INTENT_URI_LOCATION_FOR_FEED = Uri.parse("aloha://wealoha.com/location_for_feed/");
		public static final Uri INTENT_URI_FIND_YOU = Uri.parse("aloha://wealoha.com/find_you/");
		public static final Uri INTENT_URI_TWODIMENSIONALCODEACTIVITY = Uri.parse("aloha://wealoha.com/twoDimensionalcodeactivity/");
		public static final Uri INTENT_URI_CAPTUREACTIVITY = Uri.parse("aloha://wealoha.com/CaptureActivity/");
		public static final Uri INTENT_URI_TAGS_FEED = Uri.parse("aloha://wealoha.com/tag_feed/");
		public static final Uri INTENT_URI_NEWALOHAACTVITY = Uri.parse("aloha://wealoha.com/NewAlohaActivity/");
		public static final Uri INTENT_URI_FEEDCOMMENTACTIVITY = Uri.parse("aloha://wealoha.com/FeedCommentActivity/");
		public static final Uri INTENT_URI_ALERTPHONENUMACT = Uri.parse("aloha://wealoha.com/AlerPhoneNumActivity-activity/");
		public static final Uri INTENT_URI_PRIVACY = Uri.parse("aloha://wealoha.com/privacy_act/");
		public static final Uri INTENT_URI_ATTESTATION = Uri.parse("aloha://wealoha.com/attestation_act/");
	}

	public static class Http {

		public static final String CONTENT_TYPE_JSON = "application/json";
	}

	/**
	 * 这是给XUtil的api请求用的<br/>
	 * 注意：新加的接口都使用retrofit的接口，xUtils不再推荐使用
	 */
	public static class ServerUrl {

		// public static final String SERVER_URL = "http://10.0.1.223";
		public static String SERVER_URL = "http://api.wealoha.com";
		public static String LOGIN_URL = SERVER_URL + "/v1/user/auth";
		// public static final String REGISTER_URL = SERVER_URL +
		// "/v1/user/register/mobile/verify";
		// public static final String REGISTER_URL_VALID = SERVER_URL +
		// "/v1/user/register/mobile/verify/valid";
		// public static final String REGISTER_END_URL = SERVER_URL +
		// "/v1/user/register/mobile/";
		public static String LOAD_OTHER_USER_DATA = SERVER_URL + ServerUrlImpl.LOAD_OTHER_USER_DATA;
		public static String ALOHA_LIKE = SERVER_URL + ServerUrlImpl.ALOHA_LIKE;
		public static String ALOHA_DISLIKE = SERVER_URL + ServerUrlImpl.ALOHA_DISLIKE;
		public static String ALOHA_REPORT_USER = SERVER_URL + "/v1/user/report";
		public static String LOAD_USER_PROFILE = SERVER_URL + "/v1/feed/user";
		public static String LOAD_USER_FEED = SERVER_URL + ServerUrlImpl.LOAD_USER_FEED;
		public static String GET_OTHER_USER_FEED = SERVER_URL + ServerUrlImpl.GET_TIME_LINE_FEED;
		public static String GET_OTHER_USER_FEED_DETAIL = SERVER_URL + "/v1/feed/detail";
		public static String UPLOAD_FEED_JPG = SERVER_URL + "/v1/file/upload/image";
		public static String UPLOAD_FEED_JPG_FOR_CHAT = SERVER_URL + "/v1/inbox/send/image";
		public static String UPLOAD_FEED = SERVER_URL + "/v1/feed/publish/image";
		public static String PRAISE_USER_FEED = SERVER_URL + "/v1/feed/like";
		public static String DISLIKE_USER_FEED = SERVER_URL + "/v1/feed/like/cancel";
		public static String FEED_COMMENTS = SERVER_URL + "/v1/feed/comment";
		public static String DELETE_FEED_COMMENT = SERVER_URL + "/v1/feed/comment/delete";
		// public static String RESET_PASSWORD_VERIFY = SERVER_URL +
		// "/v1/user/password/mobile/reset/verify";
		public static String RESET_PASSWORD_VERIFY_CODE = SERVER_URL + "/v1/user/password/mobile/reset/verify/valid";
		public static String RESET_PASSWORD = SERVER_URL + "/v1/user/password/mobile/reset";
		public static String RESET_PASSWORD_USER = SERVER_URL + "/v1/user/password/change/mobile";
		public static String CHANGE_PROFILE = SERVER_URL + "/v1/user/profile/modify";
		public static String CREATE_SESSION = SERVER_URL + "/v1/inbox/session/create";
		public static String SEND_TEXT_SMS = SERVER_URL + "/v1/inbox/send/text";
		public static String JOIN_BLACKLIST = SERVER_URL + "/v1/user/blacklist/block";
		public static String REPORT_USER = SERVER_URL + "/v1/user/report";
		public static String REPORT_FEED = SERVER_URL + "/v1/feed/report";
		public static String NOPE = SERVER_URL + "/v1/user/match/dislike";
		public static String PUSH_BINDING = SERVER_URL + "/v1/user/push/bind/android/xiaomi";
		public static String POST_INVITATION = SERVER_URL + "/v1/user/promotion/";
		public static String GET_PROFILE = SERVER_URL + "/v1/user/profile/view";
		public static String GUID_REGISTER = SERVER_URL + "/v1/user/guid/register";
		public static String GET_BLACKLIST = SERVER_URL + "/v1/user/blacklist";
		public static String REMOVE_BLACKLIST = SERVER_URL + "/v1/user/blacklist/unblockst";
		public static String SEND_TO_USER_IMG = SERVER_URL + "/v1/inbox/send/image";
		// public static String REQ_SINA_USER_REG_ACCOUNT = SERVER_URL +
		// "/v1/connect/weibo";
		public static String REQ_FEED_NOTIFY_INFO = SERVER_URL + "/v1/feed/notify";
		public static String BLACK_LIST = SERVER_URL + "/v1/user/blacklist";
		public static String LIKE_LIST = SERVER_URL + "/v1/user/match/liked";
		public static String POPULARITY_LIST = SERVER_URL + "/v1/user/match/likedme";
		public static String DELETE_SESSION = SERVER_URL + "/v1/inbox/session/delete";
		public static String CLEAR_UNREAD_NOTICE_COUNT = SERVER_URL + "/v1/feed/notify/clearUnread";
		public static String GET_SINGLE_SESSION = SERVER_URL + "/v1/inbox/session/get";
		public static String GET_MSG_UNREAD_COUNT = SERVER_URL + "/v1/inbox/unread";
		public static String INBOX_SESSION_CLEARUNREAD = SERVER_URL + "/v1/inbox/session/clearUnread";
		// 注意：新加的接口都使用retrofit的接口，xUtils不再推荐使用
	}

	public static class GlobalCacheKeys {

		/** Match背景，byte[] png */
		public static final String MATCH_BACKGROUND_SCREENSHOT_KEY = "match-background";

		/** 手机号注册 */
		public static final String REGISTER_NUMBER_AND_PASSWORD = "register-info";
		/** 发布新feed，做一个标记，进入profile时要刷新profile */
		public static final String POST_NEW_FEED = "POST_NEW_FEED";
	}

	/**
	 * 图片尺寸(px)
	 */
	public static class ImageSize {

		public static final int AVATAR = 240;

		/** Feed最大 */
		public static final int FEED_MAX = 720;
		/** 圆形小头像 */
		public static final int AVATAR_ROUND_SMALL = 120;
		/** 聊天里的小图 */
		public static final int CHAT_THUMB = 320;

		public static final int MIN_FEED_SIZE = 640;

		public static final int MAX_FEED_SIZE_IF_OOM = 760;
		/** 上传的feed被裁减的大小不能小于1242 理想尺寸 */
		public static final int MAX_FEED_IDEAL_SIZE = 1242;
	}

	/**
	 * 全局使用的cachekey
	 * 
	 * @author javamonk
	 * @see HasCache#globalSave(String, Object)
	 * @since
	 * @date 2014-11-16 下午12:34:23
	 */
	public static enum CacheKey {
		// // 以下是全局的，注意！！目前只有全局的支持base64加密
		/** 当前用户的数据 */
		ContextHolder("ContextHolder", new TypeToken<ContextHolder>() {
		}.getType(), true), //
		/** 当前用户 */
		CurrentUser("CurrentUser", new TypeToken<User>() {
		}.getType(), false), //
		/** 当前用户的票 */
		CurrentTiket("CurrentTiket", new TypeToken<String>() {
		}.getType(), true), //

		// // 以下是非全局的
		FirstPageFeed("FirstPageFeed", new TypeToken<FeedResult>() {
		}.getType(), false), //
		AllPageFeed("FirstPageFeed", new TypeToken<FeedResult>() {
		}.getType(), false), //
		FirstPageInboxSession("FirstPageInboxSession", new TypeToken<InboxSessionResult>() {
		}.getType(), false), //
		// LoadTime("LoadTime", new TypeToken<MatchData>() {
		// }.getType(), false),
		// 注意！
		// 除了 CurrentUser, CurrentTicket以外，新加的缓存把清理放到这里去
		// ContextUtil.cleanAllCaches()
		;

		private final String name;
		private final Type type;
		private final boolean encrypt;

		private CacheKey(String name, Type type, boolean encrypt) {
			this.name = name;
			this.type = type;
			this.encrypt = encrypt;
		}

		public String getName() {
			return name;
		}

		public Type getType() {
			return type;
		}

		public boolean isEncrypt() {
			return encrypt;
		}

	}

	public static class WhereIsComeFrom {

		public static final String REFER_KEY = "refer_key";
		public static final String FOLLOWER_TO_PROFILE = "followerToProfile";
		public static final String ALOHA = "aloha";
		public static final String NOTIFY_LIKE_TO_PROFILE = "notifyLikeToProfile";
		public static final String NOTIFY_COMMENT_TO_PROFILE = "notifyCommentToProfile";
		public static final String FEED_LIKE_TO_PROFILE = "feedLikeToProfile";
		public static final String FEED_COMMENT_TO_PROFILE = "feedCommentToProfile";
		public static final String ALOHA_TO_PROFILE = "alohaToProfile";
		public static final String SCANNER_TO_PROFILE = "scannerToProfile";
		public static final String URL_SCHEME_TO_PROFILE = "URLSchemeToProfile";
		public static final String SEARCH_TO_PROFILE = "searchToProfile";
	}

	public static class TAGS {

		public static final String POST_ID = "post_id";
		public static final String POST_TAG = "post_tag";
		public static final String COMMENT_ID = "comment_id";
		public static String TOPIC_DETAIL_TAG = "topic_detail_tag";
		/** 是否开启了隐身的范围 */
		public static String IS_PRIVACY_RANGE = "is_privacy_range";
		/** 隐身的范围的值域 */
		public static String PRIVACY_RANGE = "privacy_range";
		/** 是否开启了手势密码 */
		public static String IS_GESTURE_PW = "is_gesture_pw";
		public static String IS_FEED_HEAD_HASHTAG = "is_feed_head_hashtag";
		public static String OPEN_HASH_TAG_TYPE = "open_hash_tag_type";
		public static String IS_PUSH_HASHTAG = "is_feed_head_tag";
		public static String IS_HASHTAG_OBJ = "is_hashtag_obj";
		public static String IS_HASHTAG_ID = "is_hashtag_id";
	}
}
