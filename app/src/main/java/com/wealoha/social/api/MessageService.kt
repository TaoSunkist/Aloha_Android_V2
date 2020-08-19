package com.wealoha.social.api

import com.wealoha.social.beans.Result
import com.wealoha.social.beans.ResultData
import com.wealoha.social.beans.message.InboxMessageResult
import com.wealoha.social.beans.message.InboxSessionResult
import com.wealoha.social.beans.message.UnreadData
import com.wealoha.social.impl.ServerUrlImpl
import retrofit.Callback
import retrofit.http.*
import retrofit.mime.TypedFile

/**
 *
 *
 * @author javamonk
 * @see
 * @since
 * @date 2014-11-16 下午4:47:39
 */
interface MessageService {
    @GET(ServerUrlImpl.GET_THE_SESSION_LIST)
    fun sessions(
        @Query("cursor") cursor: String?,
        @Query("count") count: Int
    ): Result<InboxSessionResult?>?

    @GET(ServerUrlImpl.GET_THE_SESSION_LIST)
    fun sessions(
        @Query("cursor") cursor: String?,
        @Query("count") count: Int,
        callback: Callback<Result<InboxSessionResult?>?>?
    )

    @GET(ServerUrlImpl.GET_SMS_LIST)
    fun sessionMessages(
        @Query("sessionId") sessionId: String?,
        @Query("cursor") cursor: String?,
        @Query("count") count: Int
    ): Result<InboxMessageResult?>?

    /**
     * 获取单个会话
     *
     * @param sessionId
     * 对方用户id
     * @return
     */
    @GET(ServerUrlImpl.GET_SINGLE_SESSION)
    fun getInboxSession(
        @Query("sessionId") sessionId: String?,
        callback: Callback<Result<InboxSessionResult?>?>?
    )

    /**
     * 创建一个会话
     *
     * @param sessionId
     * 要建立聊天的用户
     */
    @POST(ServerUrlImpl.CREATE_SESSION)
    @FormUrlEncoded
    fun post(
        @Field("sessionId") sessionId: String?,
        callback: Callback<Result<InboxSessionResult?>?>?
    )

    /**
     * 清理会话未读数
     *
     * @param sessionId
     * @param callback
     */
    @POST(ServerUrlImpl.INBOX_SESSION_CLEAR_UNREAD)
    @FormUrlEncoded
    fun clearUnread(
        @Field("sessionId") sessionId: String?,
        callback: Callback<Result<ResultData?>?>?
    )

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
    fun clearUnread(@Field("sessionId") sessionId: String?): Result<ResultData?>?

    /**
     * 清理会话未读数
     *
     * @param sessionId
     * @param callback
     */
    @POST(ServerUrlImpl.PUSH_BINDING)
    @FormUrlEncoded
    fun bindPushToUser(
        @Field("token") token: String?,
        callback: Callback<Result<ResultData?>?>?
    )

    /**
     * 取未读会话数
     *
     * @param callback
     */
    @GET(ServerUrlImpl.INBOX_SESSION_UNREAD)
    fun unread(callback: Callback<Result<UnreadData?>?>?)

    /**
     * 获取单个session
     *
     * @param sessionId
     * @param callback
     */
    @GET(ServerUrlImpl.INBOX_SESSION_GET)
    fun sessionGet(
        @Query("sessionId") sessionId: String?,
        callback: Callback<Result<InboxSessionResult?>?>?
    )

    /**
     * @Description: Multipart 中的Part使用 RestAdapter 的转换器来转换，也可以实现 TypedOutput
     * 来自己处理序列化。
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
    fun sendMsgImage(
        @Part("image") file: TypedFile?,
        @Part("toUserId") toUserId: String?,
        @Part("state") state: String?,
        callback: Callback<Result<InboxMessageResult?>?>?
    )

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
    fun sendMsgText(
        @Field("text") text: String?,
        @Field("toUserId") toUserId: String?,
        @Field("state") state: String?,
        callback: Callback<Result<InboxMessageResult?>?>?
    )

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
    fun delSingleSms(
        @Field("sessionId") sessionId: String?,
        @Field("messageId") messageId: String?,
        callback: Callback<Result<ResultData?>?>?
    )
}