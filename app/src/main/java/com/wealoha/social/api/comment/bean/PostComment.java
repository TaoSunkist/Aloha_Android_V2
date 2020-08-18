package com.wealoha.social.api.comment.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.wealoha.social.api.comment.dto.Comment2DTO;
import com.wealoha.social.api.comment.dto.CommentDTO;
import com.wealoha.social.api.common.bean.Image;
import com.wealoha.social.api.user.bean.User;

public class PostComment implements Serializable {

	private static final long serialVersionUID = -4770259070158706513L;

	private final long createTimeMillis;
	private final String id;
	private final String comment;
	private final User replyUser;
	private final User user;
	private final boolean whisper;

	public PostComment(long createTimeMillis, String id, String comment, User replyUser, User user, boolean whisper) {
		this.createTimeMillis = createTimeMillis;
		this.id = id;
		this.comment = comment;
		this.replyUser = replyUser;
		this.user = user;
		this.whisper = whisper;
	}

	public static PostComment fromCommentDTO(CommentDTO commentDTO, User replyUser, User user) {
		return new PostComment(//
		commentDTO.createTimeMillis,//
		commentDTO.id,//
		commentDTO.comment,//
		replyUser,//
		user,//
		commentDTO.whisper);
	}

	public static PostComment fromComment2DTO(Comment2DTO comment2DTO, User replyUser, User user) {
		return new PostComment(comment2DTO.createTimeMillis, comment2DTO.id, comment2DTO.comment, replyUser, user, comment2DTO.whisper);
	}

	public static PostComment fromCommentDTO(CommentDTO commentDTO) {
		return new PostComment(commentDTO.createTimeMillis,//
		commentDTO.id, //
		commentDTO.comment,//
		User.fromDTO(commentDTO.replyUser),//
		User.fromDTO(commentDTO.user),//
		commentDTO.whisper);
	};

	public static List<PostComment> fromCommentDTOList(List<CommentDTO> commentList) {
		if (commentList == null) {
			return null;
		}
		List<PostComment> posts = new ArrayList<>();
		for (CommentDTO commentDTO : commentList) {
			posts.add(fromCommentDTO(commentDTO));
		}
		return posts;
	}

	public static List<PostComment> fromDTOV2List(List<Comment2DTO> commentList) {
		if (commentList == null) {
			return null;
		}
		List<PostComment> postCommentList = new ArrayList<>();
		for (Comment2DTO dto : commentList) {
			User user = null;
			User replyUser = null;
			if (dto.user != null) {
				user = User.fromDTO(dto.user, Image.fromDTO(dto.user.avatarImage));
			}
			if (dto.replyUser != null) {
				replyUser = User.fromDTO(dto.replyUser, Image.fromDTO(dto.replyUser.avatarImage));
			}

			PostComment postComment = PostComment.fromComment2DTO(dto, replyUser, user);
			postCommentList.add(postComment);
		}
		return postCommentList;
	}

	public long getCreateTimeMillis() {
		return createTimeMillis;
	}

	public String getId() {
		return id;
	}

	public String getComment() {
		return comment;
	}

	public User getReplyUser() {
		return replyUser;
	}

	public User getUser() {
		return user;
	}

	public boolean isWhisper() {
		return whisper;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PostComment other = (PostComment) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
