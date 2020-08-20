package com.wealoha.social.api.comment.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.collections4.CollectionUtils;

import retrofit.RetrofitError;
import retrofit.client.Response;

import com.wealoha.social.api.comment.Comment2API;
import com.wealoha.social.api.comment.Comment2GetData;
import com.wealoha.social.api.comment.bean.PostComment;
import com.wealoha.social.api.comment.dto.CommentDTO;
import com.wealoha.social.api.common.AbsBaseService;
import com.wealoha.social.api.common.Direct;
import com.wealoha.social.api.user.bean.User2;
import com.wealoha.social.beans.Result;
import com.wealoha.social.inject.Injector;

public class Comment2Service extends AbsBaseService<PostComment, String> {

	@Inject
	Comment2API comment2api;

	public Comment2Service() {
		Injector.inject(this);
	}

	@Override
	public boolean supportContextByCursor() {
		return true;
	}

	@Override
	public boolean supportPrev() {
		return true;
	}

	@Override
	public boolean appendToHeader() {
		return true;
	}

	/**
	 * 第一次定位的时候获取的数据.
	 */
	@Override
	public void getList(String cursor, int count, final Direct direct, String postId, final com.wealoha.social.api.common.BaseListApiService.ApiListCallback<PostComment> callback) {
		comment2api.byDirectGetComment2s(postId, cursor, count, direct.getValue(), new retrofit.Callback<Result<Comment2GetData>>() {

			@Override
			public void failure(RetrofitError arg0) {

			}

			@Override
			public void success(Result<Comment2GetData> result, Response response) {
				if (result != null && result.isOk()) {
					if (result != null && result.isOk()) {
						String cursorid;
						// 拼装数据
						if (direct == Direct.Early) {
							cursorid = result.data.nextCursorId;
						} else {
							cursorid = result.data.lateCursorId;
						}
						callback.success(trans(result.data), cursorid);
					} else {
						callback.fail(com.wealoha.social.api.common.ApiErrorCode.fromResult(result), null);
					}

				} else {
					callback.fail(com.wealoha.social.api.common.ApiErrorCode.fromResult(result), null);
				}
			}
		});
	}

	@Override
	public void getListWithContext(String cursor, int count, String postId, final com.wealoha.social.api.common.BaseListApiService.ListContextCallback<PostComment> callback) {
		comment2api.getFirstComment2s(postId, cursor, count, true, new retrofit.Callback<Result<Comment2GetData>>() {

			@Override
			public void failure(RetrofitError arg0) {

			}

			@Override
			public void success(Result<Comment2GetData> result, Response arg1) {
				if (result != null && result.isOk()) {
					// 拼装数据
					callback.success(trans(result.data), result.data.lateCursorId, result.data.nextCursorId);
				} else {
					callback.fail(com.wealoha.social.api.common.ApiErrorCode.fromResult(result), null);
				}
			}

		});
	}

	public List<PostComment> trans(Comment2GetData data) {
		if (CollectionUtils.isEmpty(data.list)) {
			return Collections.emptyList();
		}
		List<PostComment> result = new ArrayList<PostComment>(data.list.size());
		for (CommentDTO commentDTO : data.list) {
			User2 user2 = getUser(commentDTO.userId, data.userMap, data.imageMap);
			User2 replyUser2 = getUser(commentDTO.replyUserId, data.userMap, data.imageMap);
			result.add(PostComment.fromCommentDTO(commentDTO, replyUser2, user2));
		}
		return result;
	}

	// private User getUser(PostComment comment2, Map<String, UserDTO> userMap, Map<String, ImageDTO>
	// imageMap) {
	// UserDTO userDTO = userMap.get(comment2.getUserId());
	// return User.fromDTO(userDTO, Image.fromDTO(imageMap.get(userDTO.avatarImageId)));
	// }

	/**
	 * @Description:获取Comment2Data的数据
	 * @return
	 * @see:
	 * @since:
	 * @description
	 * @author: sunkist
	 * @date:2015年3月11日
	 */
	// protected PostComment getComment2Data(CommentDTO commentDTO) {
	// return new PostComment(commentDTO);
	// }
}