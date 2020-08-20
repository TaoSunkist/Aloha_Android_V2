package com.wealoha.social.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PostComment implements Serializable {

	private static final long serialVersionUID = -4770259070158706513L;

	private final long createTimeMillis;
	private final String id;
	private final String comment;
	private final User2 replyUser2;
	private final User2 user2;
	private final boolean whisper;

	public PostComment(long createTimeMillis, String id, String comment, User2 replyUser2, User2 user2, boolean whisper) {
		this.createTimeMillis = createTimeMillis;
		this.id = id;
		this.comment = comment;
		this.replyUser2 = replyUser2;
		this.user2 = user2;
		this.whisper = whisper;
	}

	public static PostComment fromCommentDTO(CommentDTO commentDTO, User2 replyUser2, User2 user2) {
		return new PostComment(//
		commentDTO.createTimeMillis,//
		commentDTO.id,//
		commentDTO.comment,//
				replyUser2,//
				user2,//
		commentDTO.whisper);
	}

	public static PostComment fromComment2DTO(Comment2DTO comment2DTO, User2 replyUser2, User2 user2) {
		return new PostComment(comment2DTO.createTimeMillis, comment2DTO.id, comment2DTO.comment, replyUser2, user2, comment2DTO.whisper);
	}

	public static PostComment fromCommentDTO(CommentDTO commentDTO) {
		return new PostComment(commentDTO.createTimeMillis,//
		commentDTO.id, //
		commentDTO.comment,//
		User2.fromDTO(commentDTO.replyUser),//
		User2.fromDTO(commentDTO.user),//
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
			User2 user2 = null;
			User2 replyUser2 = null;
			if (dto.user != null) {
				user2 = User2.fromDTO(dto.user, CommonImage.fromDTO(dto.user.avatarImage));
			}
			if (dto.replyUser != null) {
				replyUser2 = User2.fromDTO(dto.replyUser, CommonImage.fromDTO(dto.replyUser.avatarImage));
			}

			PostComment postComment = PostComment.fromComment2DTO(dto, replyUser2, user2);
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

	public User2 getReplyUser2() {
		return replyUser2;
	}

	public User2 getUser2() {
		return user2;
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
