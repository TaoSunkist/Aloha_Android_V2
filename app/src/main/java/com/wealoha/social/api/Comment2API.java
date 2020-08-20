package com.wealoha.social.api;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

import com.wealoha.social.beans.Comment2GetData;
import com.wealoha.social.beans.Result;
import com.wealoha.social.impl.ServerUrlImpl;

public interface Comment2API {

	/**
	 * @Description:从通知列表进入的Comment(第一次请求), 客户端传入参数postId, cursor, count, needCtx=true：
	 *                                      返回cursor上下各一半count的数据list, preCursorId(向前|上)游标,
	 *                                      nextCursorId(向后|下)游标
	 * @param postId
	 * @param cursor
	 * @param count
	 * @param callback
	 * @see:
	 * @since:
	 * @description
	 * @author: sunkist
	 * @date:2015年3月11日
	 */
	@GET(ServerUrlImpl.GET_COMMENT)
	public void getFirstComment2s(@Query("postId") String postId, @Query("cursor") String cursor, //
			@Query("count") int count,//
			@Query("needCtx") boolean needCtx, Callback<Result<Comment2GetData>> callback);

	/**
	 * @Description:客户端继续取数据， 传入postId, cursor, count, direct=[desc(向下)|asc(向上)]
	 *                        返回cursor在direct方向上的count条记录list和在次方向上的nextCursorId
	 * @param postId
	 * @param cursor
	 * @param count
	 * @param direct
	 * @param callback
	 * @see:
	 * @since:
	 * @description
	 * @author: sunkist
	 * @date:2015年3月11日
	 */
	@GET(ServerUrlImpl.GET_COMMENT)
	public void byDirectGetComment2s(@Query("postId") String postId, @Query("cursor") String cursor, //
			@Query("count") int count,//
			@Query("direct") String direct, Callback<Result<Comment2GetData>> callback);

	/**
	 * @Title: postComment
	 * @Description: 发送评论
	 * @param @param postId
	 * @param @param userid
	 * @param @param comment
	 * @param @param callback 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	@FormUrlEncoded
	@POST(ServerUrlImpl.POST_COMMENT)
	public void sendComment(@Field("postId") String postId, //
			@Field("replyUserId") String userid,//
			@Field("comment") String comment,//
			Callback<Result<Comment2GetData>> callback);

}
