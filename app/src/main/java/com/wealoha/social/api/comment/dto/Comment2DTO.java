package com.wealoha.social.api.comment.dto;

import com.wealoha.social.api.user.dto.UserDTO;

public class Comment2DTO {

	public long createTimeMillis;
	public UserDTO user;
	public boolean mine;
	public String id;
	public String type;
	public String comment;
	public UserDTO replyUser;
	public boolean whisper;
}
