package com.wealoha.social.api.notify2.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.collections4.CollectionUtils;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.content.Context;

import com.wealoha.social.R;
import com.wealoha.social.api.common.AbsBaseService;
import com.wealoha.social.api.common.ApiErrorCode;
import com.wealoha.social.api.common.Direct;
import com.wealoha.social.api.notify2.Notify2API;
import com.wealoha.social.api.notify2.NotifyGetData;
import com.wealoha.social.api.notify2.bean.NewAlohaNotify2;
import com.wealoha.social.api.notify2.bean.Notify2;
import com.wealoha.social.api.notify2.bean.Notify2Type;
import com.wealoha.social.api.notify2.bean.PostCommentNotify2;
import com.wealoha.social.api.notify2.bean.PostCommentReplyOnMyPost;
import com.wealoha.social.api.notify2.bean.PostCommentReplyOnOthersPost;
import com.wealoha.social.api.notify2.bean.PostLikeNotify2;
import com.wealoha.social.api.notify2.bean.PostTagNotify2;
import com.wealoha.social.api.notify2.dto.AbsNotify2DTO;
import com.wealoha.social.api.notify2.dto.NewAlohaNotify2DTO;
import com.wealoha.social.api.notify2.dto.PostCommentNotify2DTO;
import com.wealoha.social.api.notify2.dto.PostCommentReplyOnMyPost2DTO;
import com.wealoha.social.api.notify2.dto.PostCommentReplyOnOthersPost2DTO;
import com.wealoha.social.api.notify2.dto.PostLikeNotify2DTO;
import com.wealoha.social.api.notify2.dto.PostTagNotify2DTO;
import com.wealoha.social.api.post.bean.Post;
import com.wealoha.social.api.user.bean.User;
import com.wealoha.social.beans.Result;
import com.wealoha.social.beans.ResultData;
import com.wealoha.social.inject.Injector;
import com.wealoha.social.push.notification.NotificationCount;
import com.wealoha.social.utils.XL;

/**
 * 取通知数据
 * 
 * @author javamonk
 * @createTime 2015年2月25日 上午10:34:56
 */
public class Notify2Service extends AbsBaseService<Notify2, Boolean> {

	private static final String TAG = Notify2Service.class.getSimpleName();

	@Inject
	Notify2API notify2api;
	@Inject
	Context mContext;

	public Notify2Service() {
		Injector.inject(this);
	}

	protected List<Notify2> trans(NotifyGetData data) {
		if (CollectionUtils.isEmpty(data.list)) {
			return Collections.emptyList();
		}

		List<Notify2> result = new ArrayList<Notify2>(data.list.size());
		for (AbsNotify2DTO dto : data.list) {
			Notify2Type type = Notify2Type.fromValue(dto.type);
			if (type == null) {
				continue;
			}

			if (type == Notify2Type.PostLike) {
				PostLikeNotify2DTO d = (PostLikeNotify2DTO) dto;
				Post post = getPost(data.postMap.get(d.postId), data.userMap, data.imageMap, null, data.commentCountMap, data.likeCountMap);
				List<User> users = getUsers(d.userIds, data.userMap, data.imageMap);
				result.add(new PostLikeNotify2(type, dto.unread, dto.notifyId, dto.updateTimeMillis, post, users,dto.count));
			} else if (type == Notify2Type.NewAloha) {
				NewAlohaNotify2DTO d = (NewAlohaNotify2DTO) dto;
				List<User> users = getUsers(d.userIds, data.userMap, data.imageMap);
				result.add(new NewAlohaNotify2(type, dto.unread, dto.notifyId, dto.updateTimeMillis, dto.count, users));
			} else if (type == Notify2Type.PostComment) {
				PostCommentNotify2DTO d = (PostCommentNotify2DTO) dto;
				User fromUser = getUser(d.fromUser, data.userMap, data.imageMap);
				Post post = getPost(data.postMap.get(d.postId), data.userMap, data.imageMap, null, data.commentCountMap, data.likeCountMap);
				result.add(new PostCommentNotify2(type, d.unread, dto.notifyId, d.updateTimeMillis, d.replyMe, d.comment, d.commentId, fromUser, post,d.count));
			} else if (type == Notify2Type.PostTag) {
				PostTagNotify2DTO d = (PostTagNotify2DTO) dto;
				User fromUser = getUser(d.fromUser, data.userMap, data.imageMap);
				Post post = getPost(data.postMap.get(d.postId), data.userMap, data.imageMap, null, data.commentCountMap, data.likeCountMap);
				result.add(new PostTagNotify2(type, d.unread, dto.notifyId, d.updateTimeMillis, fromUser, post));
			}else if(type == Notify2Type.PostCommentReplyOnMyPost){
				PostCommentReplyOnMyPost2DTO d = (PostCommentReplyOnMyPost2DTO) dto;
				User fromUser = getUser(d.fromUser, data.userMap, data.imageMap);
				User replyUser = getUser(d.replyUser, data.userMap, data.imageMap);
				Post post = getPost(data.postMap.get(d.postId), data.userMap, data.imageMap, null, data.commentCountMap, data.likeCountMap);
				String comment = mContext.getString(R.string.aloha_post_comment_reply_on_my_post, replyUser.getName(),d.comment);
				result.add(new PostCommentReplyOnMyPost(type, d.unread, d.notifyId, d.updateTimeMillis, //
				                                        replyUser, fromUser, d.commentId, comment, post));
			}else if(type == Notify2Type.PostCommentReplyOnOthersPost){
				PostCommentReplyOnOthersPost2DTO d = (PostCommentReplyOnOthersPost2DTO)dto;
				User postAuthor = getUser(d.fromUser, data.userMap, data.imageMap);
				User replyUser = getUser(d.postAuthor, data.userMap, data.imageMap);
				Post post = getPost(data.postMap.get(d.postId), data.userMap, data.imageMap, null, data.commentCountMap, data.likeCountMap);
				result.add(new PostCommentReplyOnOthersPost(type, d.unread, d.notifyId, d.updateTimeMillis, //
				                                        replyUser, postAuthor, d.commentId, d.comment, post));
			}
			// 在这里添加其它类型
		}
		return result;
	}

	@Override
	public void getList(String cursor, int count, Direct direct, Boolean param, final com.wealoha.social.api.common.BaseListApiService.ApiListCallback<Notify2> callback) {
		notify2api.getNotifies(cursor, count, param, new retrofit.Callback<Result<NotifyGetData>>() {

			@Override
			public void success(Result<NotifyGetData> result, Response response) {
				XL.d(TAG, "加载数据成功");
				if (result != null && result.isOk()) {
					// 成功，拼装数据
					callback.success(trans(result.data), result.data.nextCursorId);
				} else {
					callback.fail(ApiErrorCode.fromResult(result), null);
				}
			}

			@Override
			public void failure(RetrofitError error) {
				callback.fail(null, error);
			}
		});
	}

	/**
	 * 清理未读数
	 */
	public void clearUnread() {

		notify2api.clearUnread(new Callback<Result<ResultData>>() {

			@Override
			public void success(Result<ResultData> arg0, Response arg1) {
				XL.d(TAG, "clear成功");
				// 清理本地的未读数
				NotificationCount.setCommentCount(0);
			}

			@Override
			public void failure(RetrofitError e) {
				XL.w(TAG, "clearUnread失败", e);
			}
		});
	}

	public void changeReadState(Notify2 notify, boolean isread) {
		notify.changeReadState(isread);
	}
}
