package com.wealoha.social.api.post.dto;

import java.util.List;

import com.wealoha.social.api.comment.dto.Comment2DTO;
import com.wealoha.social.beans.UserTagsDTO;
import com.wealoha.social.api.topic.dto.HashTagDTO;

/**
 * 
 * 
 * @author javamonk
 * @createTime 2015年2月25日 上午11:33:11
 */
public class PostDTO {

	public long createTimeMillis;
	public String description;
	public String imageId;
	public String videoId;
	public String postId;
	public boolean mine;
	public boolean liked;
	public String type;
	public String userId;
	public Double latitude;
	public Double longitude;
	public String venue;
	public List<UserTagsDTO> userTags;
	public Boolean venueAbroad;
	public String venueId;
	public int count;
	public List<Comment2DTO> recentComments;
	public HashTagDTO hashtag;
	public boolean hasMoreComment;
}
