package com.wealoha.social.beans.comment;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

import com.wealoha.social.api.comment.Comment2GetData;
import com.wealoha.social.beans.CommentResult;
import com.wealoha.social.beans.Result;
import com.wealoha.social.beans.ResultData;
import com.wealoha.social.impl.ServerUrlImpl;

public interface CommentService {

	/**
	 * æŸ¥çœ‹ç”¨æˆ·feed
	 * 
	 * @param userId
	 *            ä¼ nullçœ‹ç”¨æˆ·è‡ªå·±çš„
	 * @param cursor
	 * @param count
	 * @return
	 */
	@GET(ServerUrlImpl.GET_COMMENT)
	public void comments(@Query("postId") String postId, @Query("cursor") String cursor, @Query("count") String count, Callback<Result<CommentResult>> callback);

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
	public void postComment(@Field("postId") String postId, @Field("replyUserId") String userid, @Field("comment") String comment, Callback<Result<Comment2GetData>> callback);

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
	public void postCommentV2(@Field("postId") String postId, @Field("replyCommentId") String replyCommentId, @Field("comment") String comment, Callback<Result<Comment2GetData>> callback);

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
	public void postCommentForOld(@Field("postId") String postId, @Field("replyUserId") String userid, @Field("comment") String comment, Callback<Result<CommentResult>> callback);

	@FormUrlEncoded
	@POST(ServerUrlImpl.DELETE_COMMENT)
	public void deleteComment(@Field("postId") String postId, @Field("commentId") String comment, Callback<Result<ResultData>> callback);
}