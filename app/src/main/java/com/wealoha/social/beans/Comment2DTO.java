package com.wealoha.social.beans;

import com.wealoha.social.beans.UserDTO;

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
