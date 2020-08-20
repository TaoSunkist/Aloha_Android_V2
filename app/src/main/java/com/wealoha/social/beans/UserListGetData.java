package com.wealoha.social.beans;

import java.util.List;
import java.util.Map;

import com.wealoha.social.api.common.dto.ImageDTO;

public class UserListGetData extends ResultData {

	public List<UserDTO> list;
	public Map<String, ImageDTO> imageMap;
	public String nextCursorId;
	public boolean alohaGetLocked;
	public int alohaGetUnlockCount;

}
