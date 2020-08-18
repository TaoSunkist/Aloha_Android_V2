package com.wealoha.social.beans.message;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Query;
import retrofit.mime.TypedFile;

import com.wealoha.social.beans.Result;
import com.wealoha.social.beans.ResultData;
import com.wealoha.social.impl.ServerUrlImpl;

/**
 * 
 * 
 * @author javamonk
 * @see
 * @since
 * @date 2014-11-16 下午4:47:39
 */
public interface MessageService {

	@GET(ServerUrlImpl.GET_THE_SESSION_LIST)
	Result<InboxSessionResult> sessions(@Query("cursor") String cursor, @Query("count") int count);

	@GET(ServerUrlImpl.GET_THE_SESSION_LIST)
	void sessions(@Query("cursor") String cursor, @Query("count") int count, Callback<Result<InboxSessionResult>> callback);

	@GET(ServerUrlImpl.GET_SMS_LIST)
	Result<InboxMessageResult> sessionMessages(@Query("sessionId") String sessionId, @Query("cursor") String cursor, @Query("count") int count);

	/**
	 * 获取单个会话
	 * 
	 * @param sessionId
	 *            对方用户id
	 * @return
	 */
	@GET(ServerUrlImpl.GET_SINGLE_SESSION)
	void getInboxSession(@Query("sessionId") String sessionId, Callback<Result<InboxSessionResult>> callback);

	/**
	 * 创建一个会话
	 * 
	 * @param sessionId
	 *            要建立聊天的用户
	 */
	@POST(ServerUrlImpl.CREATE_SESSION)
	@FormUrlEncoded
	void post(@Field("sessionId") String sessionId, Callback<Result<InboxSessionResult>> callback);

	/**
	 * 清理会话未读数
	 * 
	 * @param sessionId
	 * @param callback
	 */
	@POST(ServerUrlImpl.INBOX_SESSION_CLEAR_UNREAD)
	@FormUrlEncoded
	public void clearUnread(@Field("sessionId") String sessionId, Callback<Result<ResultData>> callback);

	/**
	 * @Description:清理未读数不需要
	 * @param sessionId
	 * @return
	 * @see:
	 * @since:
	 * @description
	 * @author: sunkist
	 * @date:2015-2-11
	 */
	@POST(ServerUrlImpl.INBOX_SESSION_CLEAR_UNREAD)
	@FormUrlEncoded
	Result<ResultData> clearUnread(@Field("sessionId") String sessionId);

	/**
	 * 清理会话未读数
	 * 
	 * @param sessionId
	 * @param callback
	 */
	@POST(ServerUrlImpl.PUSH_BINDING)
	@FormUrlEncoded
	public void bindPushToUser(@Field("token") String token, Callback<Result<ResultData>> callback);

	/**
	 * 取未读会话数
	 * 
	 * @param callback
	 */
	@GET(ServerUrlImpl.INBOX_SESSION_UNREAD)
	public void unread(Callback<Result<UnreadData>> callback);

	/**
	 * 获取单个session
	 * 
	 * @param sessionId
	 * @param callback
	 */
	@GET(ServerUrlImpl.INBOX_SESSION_GET)
	public void sessionGet(@Query("sessionId") String sessionId, Callback<Result<InboxSessionResult>> callback);

	/**
	 * @Description: Multipart 中的Part使用 RestAdapter 的转换器来转换，也可以实现 TypedOutput
	 *               来自己处理序列化。
	 * @param token
	 * @param callback
	 * @see:
	 * @since:
	 * @description 聊天发送信息[图片]
	 * @author: sunkist
	 * @date:2014-12-30
	 */
	@Multipart
	@POST(ServerUrlImpl.SEND_TO_USER_IMG)
	public void sendMsgImage(@Part("image") TypedFile file, @Part("toUserId") String toUserId, @Part("state") String state, Callback<Result<InboxMessageResult>> callback);

	/**
	 * @Description:
	 * @param token
	 * @param callback
	 * @see:
	 * @since:
	 * @description 聊天发送信息[信息]
	 * @author: sunkist
	 * @date:2014-12-30
	 */
	@FormUrlEncoded
	@POST(ServerUrlImpl.SEND_TEXT_SMS)
	public void sendMsgText(@Field("text") String text, @Field("toUserId") String toUserId, @Field("state") String state, Callback<Result<InboxMessageResult>> callback);

	/**
	 * @Description:删除单条消息
	 * @param text
	 * @param toUserId
	 * @param state
	 * @param callback
	 * @see:
	 * @since:
	 * @description
	 * @author: sunkist
	 * @date:2015-1-5
	 */
	@FormUrlEncoded
	@POST(ServerUrlImpl.DEL_SINGLE_SMS)
	public void delSingleSms(@Field("sessionId") String sessionId, @Field("messageId") String messageId, Callback<Result<ResultData>> callback);
}
