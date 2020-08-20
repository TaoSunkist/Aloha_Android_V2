package com.wealoha.social.beans;

import java.util.List;

public class TopicPostDTO {

	public String postId;
	public FeedType type;
	public String description;
	public long createTimeMillis;
	public boolean mine;
	public String venue;
	public String venueId;
	public Double latitude;
	public Double longitude;
	public Boolean venueAbroad;
	public UserDTO user;
	public List<TopicPostTagDTO> userTags;
	public ImageCommonDto image;
	public VideoCommonDTO video;
	public boolean tagMe;
	public boolean liked;
	public int commentCount;
	public int praiseCount;
	public List<Comment2DTO> recentComment;
	public HashTagDTO hashtag;
	public boolean hasMoreComment;
}
