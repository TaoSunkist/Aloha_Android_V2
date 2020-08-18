package com.wealoha.social.api.user;

import java.util.Map;

import com.wealoha.social.api.common.dto.ImageDTO;
import com.wealoha.social.api.user.dto.UserDTO;
import com.wealoha.social.beans.ResultData;

public class Profile2Data extends ResultData {

	public UserDTO user;
	public Map<String, ImageDTO> imageMap;
	public boolean friend;
	public boolean liked;
}
