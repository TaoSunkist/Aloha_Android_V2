package com.wealoha.social.beans.search;

import java.util.List;
import java.util.Map;

import com.wealoha.social.beans.ResultData;
import com.wealoha.social.beans.User;

public class FindYouResult extends ResultData {

	public Map<String, String> highlightMap;
	public List<User> list;
	public User user;
	public String keyword;
}
