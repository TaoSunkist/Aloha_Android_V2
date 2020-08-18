package com.wealoha.social.api.notify2;

import java.util.List;
import java.util.Map;

import com.wealoha.social.api.common.dto.ImageDTO;
import com.wealoha.social.api.user.dto.UserDTO;
import com.wealoha.social.beans.ResultData;

public class MergeUsersGetData extends ResultData {

	public List<UserDTO> list;
	public Map<String, ImageDTO> imageMap;
	public String nextCursorId;
}
