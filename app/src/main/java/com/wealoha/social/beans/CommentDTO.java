package com.wealoha.social.beans;

/**
 * @author:sunkist
 * @see:
 * @since:
 * @description
 * @copyright wealoha.com
 * @Date:2015年3月11日
 */
public class CommentDTO {

	public long createTimeMillis;
	public String userId;
	public boolean mine;
	public String id;
	public String type;
	public String comment;
	/** 回复某人 */
	public String replyUserId;
	public boolean whisper;
	/** 新版评论 */
	public UserDTO user;
	public UserDTO replyUser;

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
			if (other.getId() != null)
				return false;
		} else if (!id.equals(other.getId()))
			return false;
		return true;
	}
}
