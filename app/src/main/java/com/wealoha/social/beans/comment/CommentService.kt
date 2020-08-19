package com.wealoha.social.beans.comment

import com.wealoha.social.api.comment.Comment2GetData
import com.wealoha.social.beans.CommentResult
import com.wealoha.social.beans.Result
import com.wealoha.social.beans.ResultData
import com.wealoha.social.impl.ServerUrlImpl
import retrofit.Callback
import retrofit.http.*

interface CommentService {
    /**
     * æŸ¥çœ‹ç”¨æˆ·feed
     *
     * @param userId
     * ä¼ nullçœ‹ç”¨æˆ·è‡ªå·±çš„
     * @param cursor
     * @param count
     * @return
     */
    @GET(ServerUrlImpl.GET_COMMENT)
    fun comments(
        @Query("postId") postId: String?,
        @Query("cursor") cursor: String?,
        @Query("count") count: String?,
        callback: Callback<Result<CommentResult?>?>?
    )

    /**
     * 发表评论(旧)
     *
     * @param userId
     * @param cursor
     * @param count
     * @return
     */
    @FormUrlEncoded
    @POST(ServerUrlImpl.POST_COMMENT)
    fun postComment(
        @Field("postId") postId: String?,
        @Field("replyUserId") userid: String?,
        @Field("comment") comment: String?,
        callback: Callback<Result<Comment2GetData?>?>?
    )

    /**
     * 发表评论(新)
     *
     * @param userId
     * @param cursor
     * @param count
     * @return
     */
    @FormUrlEncoded
    @POST(ServerUrlImpl.POST_COMMENT_V2)
    fun postCommentV2(
        @Field("postId") postId: String?,
        @Field("replyCommentId") replyCommentId: String?,
        @Field("comment") comment: String?,
        callback: Callback<Result<Comment2GetData?>?>?
    )

    /**
     * 旧版本的发表评论接口
     *
     * @param userId
     * @param cursor
     * @param count
     * @return
     */
    @FormUrlEncoded
    @POST(ServerUrlImpl.POST_COMMENT)
    fun postCommentForOld(
        @Field("postId") postId: String?,
        @Field("replyUserId") userid: String?,
        @Field("comment") comment: String?,
        callback: Callback<Result<CommentResult?>?>?
    )

    @FormUrlEncoded
    @POST(ServerUrlImpl.DELETE_COMMENT)
    fun deleteComment(
        @Field("postId") postId: String?,
        @Field("commentId") comment: String?,
        callback: Callback<Result<ResultData?>?>?
    )
}