package com.wealoha.social.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.collections4.CollectionUtils;

import retrofit.RetrofitError;
import retrofit.client.Response;

import com.wealoha.social.beans.Comment2GetData;
import com.wealoha.social.beans.PostComment;
import com.wealoha.social.beans.CommentDTO;
import com.wealoha.social.beans.Direct;
import com.wealoha.social.beans.ApiErrorCode;
import com.wealoha.social.beans.User;
import com.wealoha.social.beans.Result;
import com.wealoha.social.inject.Injector;

public class Comment2Service extends AbsBaseService<PostComment, String> {

	@Inject
	ServerApi comment2api;

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
	public void getList(String cursor, int count, final Direct direct, String postId, final BaseListApiService.ApiListCallback<PostComment> callback) {
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
							cursorid = result.getData().nextCursorId;
						} else {
							cursorid = result.getData().lateCursorId;
						}
						callback.success(trans(result.getData()), cursorid);
					} else {
						callback.fail(ApiErrorCode.fromResult(result), null);
					}

				} else {
					callback.fail(ApiErrorCode.fromResult(result), null);
				}
			}
		});
	}

	@Override
	public void getListWithContext(String cursor, int count, String postId, final BaseListApiService.ListContextCallback<PostComment> callback) {
		comment2api.getFirstComment2s(postId, cursor, count, true, new retrofit.Callback<Result<Comment2GetData>>() {

			@Override
			public void failure(RetrofitError arg0) {

			}

			@Override
			public void success(Result<Comment2GetData> result, Response arg1) {
				if (result != null && result.isOk()) {
					// 拼装数据
					callback.success(trans(result.getData()), result.getData().lateCursorId, result.getData().nextCursorId);
				} else {
					callback.fail(ApiErrorCode.fromResult(result), null);
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
			User user2 = getUser(commentDTO.userId, data.userMap, data.imageMap);
			User replyUser = getUser(commentDTO.replyUserId, data.userMap, data.imageMap);
			result.add(PostComment.Companion.fromCommentDTO(commentDTO, replyUser, user2));
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