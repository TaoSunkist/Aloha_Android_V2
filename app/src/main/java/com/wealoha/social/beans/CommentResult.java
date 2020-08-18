package com.wealoha.social.beans;

import java.util.List;
import java.util.Map;

public class CommentResult extends ResultData {

	public String nextCursorId;
	public Map<String, User> userMap;
	public List<Comment> list;
}
