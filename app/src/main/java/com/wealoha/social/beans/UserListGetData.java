package com.wealoha.social.beans;

import java.util.List;
import java.util.Map;

public class UserListGetData extends ResultData {

	public List<UserDTO> list;
	public Map<String, ImageCommonDto> imageMap;
	public String nextCursorId;
	public boolean alohaGetLocked;
	public int alohaGetUnlockCount;

}
