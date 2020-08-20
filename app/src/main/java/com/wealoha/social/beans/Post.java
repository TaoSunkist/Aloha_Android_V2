package com.wealoha.social.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.wealoha.social.beans.feed.UserTags;

/**
 * 
 * 
 * @author javamonk
 * @createTime 2015年2月25日 上午10:49:44
 */
public class Post implements Serializable {

	public static final String TAG = Post.class.getName();

	/**
	 * 
	 */
	private static final long serialVersionUID = 2689425306398055435L;
	private final String postId;
	private final FeedType type;
	private final String description;
	private final long createTimeMillis;
	private final boolean mine;
	private final String venue;
	private String venueId;
	private final Double latitude;
	private final Double longitude;
	private final Boolean venueAbroad;
	private final User2 user2;
	private final List<UserTag> userTags;
	private final CommonImage commonImage;
	private final CommonVideo commonVideo;
	private boolean tagMe;
	private boolean liked;
	private int commentCount;
	private int praiseCount;
	private List<PostComment> recentComment;
	private HashTag hashtag;
	private boolean hasMoreComment;

	public Post(String postId, FeedType type, String description, long createTimeMillis, boolean mine, boolean liked, boolean tagMe, String venue, String venueId, Double latitude, Double longitude, Boolean venueAbroad, User2 user2, List<UserTag> userTags, CommonImage commonImage, CommonVideo commonVideo, int commentCount, int praiseCount, List<PostComment> recentComment, HashTag hashtag, boolean hasMoreComment) {
		super();
		this.postId = postId;
		this.type = type;
		this.description = description;
		this.createTimeMillis = createTimeMillis;
		this.mine = mine;
		this.liked = liked;
		this.tagMe = tagMe;
		this.venue = venue;
		this.venueId = venueId;
		this.latitude = latitude;
		this.longitude = longitude;
		this.venueAbroad = venueAbroad;
		this.user2 = user2;
		this.userTags = userTags;
		this.commonImage = commonImage;
		this.commonVideo = commonVideo;
		this.commentCount = commentCount;
		this.praiseCount = praiseCount;
		this.recentComment = recentComment;
		this.hashtag = hashtag;
		this.hasMoreComment = hasMoreComment;
	}

	public static Post fromDTO(PostDTO postdto, CommonImage commonImage, CommonVideo commonVideo, User2 user2, List<UserTag> userTags, int commentCount, int praiseCount, boolean tagMe, List<PostComment> recentComment, HashTag hashtag) {
		return new Post(//
		postdto.postId,//
		FeedType.fromValue(postdto.type),//
		postdto.description,//
		postdto.createTimeMillis,//
		postdto.mine, //
		postdto.liked,//
		tagMe,//
		postdto.venue,//
		postdto.venueId,//
		postdto.latitude,//
		postdto.longitude, //
		postdto.venueAbroad,//
				user2,//
		userTags,//
				commonImage,//
				commonVideo,//
		commentCount,//
		praiseCount, //
		recentComment,//
		hashtag,//
		postdto.hasMoreComment);
	}

	public static ArrayList<UserTags> transTagsToOldVer(List<UserTag> tags, boolean tagme) {
		ArrayList<UserTags> userTags = new ArrayList<UserTags>(tags.size());
		for (UserTag tag : tags) {
			UserTags userTag = new UserTags();
			userTag.tagAnchorX = tag.getTagAnchorX().floatValue();
			userTag.tagAnchorY = tag.getTagAnchorY().floatValue();
			userTag.tagCenterX = tag.getTagCenterX().floatValue();
			userTag.tagCenterY = tag.getTagCenterY().floatValue();
			userTag.tagMe = tagme;
			userTag.tagUserId = tag.getUser2().getId();
			userTag.setUsername(tag.getUser2().getName());
			userTags.add(userTag);
		}
		return userTags;
	}

	/***
	 * 这个feed 是否有当前用户的标签
	 * 
	 * @param userTagList
	 *            圈人列表
	 * @param userId
	 *            当前用户id
	 * @return 如果任意参数为空，那么返回false
	 */
	public static boolean hasTagForMe(List<UserTag> userTagList, String userId) {
		if (userTagList == null) {
			return false;
		}

		// if (TextUtils.isEmpty(userId)) {
		for (UserTag userTag : userTagList) {
			if (userTag.getUser2().isMe()) {
				return true;
			}
		}
		// }
		return false;
	}

	public static Post fromDTO(PostDTO postDTO, Map<String, UserDTO> userMap, Map<String, ImageCommonDto> imageMap, Map<String, VideoCommonDTO> video, Map<String, Integer> commentCountMap, Map<String, Integer> praiseCountMap) {
		List<UserTag> userTagList = UserTag.fromDTOList(postDTO.userTags, userMap, imageMap);

		UserDTO userDTO = userMap.get(postDTO.userId);
		CommonImage commonImage = null;
		User2 user2 = null;
		if (userDTO != null) {
			commonImage = CommonImage.fromDTO(imageMap.get(userDTO.avatarImageId));
			user2 = User2.fromDTO(userDTO, commonImage);
		}

		return new Post(//
		postDTO.postId,//
		FeedType.fromValue(postDTO.type),//
		postDTO.description,//
		postDTO.createTimeMillis,//
		postDTO.mine, //
		postDTO.liked,//
		hasTagForMe(userTagList, postDTO.userId),//
		postDTO.venue,//
		postDTO.venueId,//
		postDTO.latitude,//
		postDTO.longitude,//
		postDTO.venueAbroad,//
				user2,//
		userTagList,//
		CommonImage.fromDTO(imageMap.get(postDTO.imageId)),//
		CommonVideo.fromDTO(video.get(postDTO.videoId)),//
		commentCountMap.get(postDTO.postId),//
		praiseCountMap.get(postDTO.postId),//
		PostComment.fromDTOV2List(postDTO.recentComments),//
		HashTag.fromDTO(postDTO.hashtag),//
		postDTO.hasMoreComment);
	}

	public static List<Post> fromPostDTOList(List<PostDTO> dtoList, Map<String, UserDTO> userMap, Map<String, ImageCommonDto> imageMap, Map<String, VideoCommonDTO> videoMap, Map<String, Integer> commentCountMap, Map<String, Integer> praiseCountMap) {
		List<Post> postList = new ArrayList<>();
		for (PostDTO postDTO : dtoList) {
			Post post = Post.fromDTO(postDTO, userMap, imageMap, videoMap, commentCountMap, praiseCountMap);
			postList.add(post);
		}
		return postList;
	}

	public String getPostId() {
		return postId;
	}

	public FeedType getType() {
		return type;
	}

	public String getDescription() {
		return description;
	}

	public long getCreateTimeMillis() {
		return createTimeMillis;
	}

	public boolean isMine() {
		return mine;
	}

	public String getVenue() {
		return venue;
	}

	public String getVenueId() {
		return venueId;
	}

	public Double getLatitude() {
		return latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public Boolean getVenueAbroad() {
		return venueAbroad;
	}

	public User2 getUser2() {
		return user2;
	}

	public List<UserTag> getUserTags() {
		return userTags;
	}

	public CommonImage getCommonImage() {
		return commonImage;
	}

	public CommonVideo getCommonVideo() {
		return commonVideo;
	}

	public int getCommentCount() {
		return commentCount;
	}

	public int getPraiseCount() {
		return praiseCount;
	}

	public boolean isLiked() {
		return liked;
	}

	public boolean isTagMe() {
		return tagMe;
	}

	/***
	 * 移除当前用户的tag后数据刷新
	 * 
	 * @return void
	 */
	public void removeTagMe() {
		tagMe = false;
	}

	/***
	 * 赞后数据刷新
	 * 
	 * @return void
	 */
	public void praise() {
		this.praiseCount = praiseCount + 1;
		this.liked = true;
	}

	/***
	 * 取消赞后数据刷新
	 * 
	 * @return void
	 */
	public void dislike() {
		if (praiseCount != 0) {
			praiseCount = praiseCount - 1;
			liked = false;
		}
	}

	/***
	 * 留言增加后数据刷新
	 * 
	 * @return void
	 */
	public void setCommentCount(int count) {
		commentCount = count;
	}

	public void setVenueid(String venueid) {
		this.venueId = venueid;
	}

	public List<PostComment> getRecentComment() {
		return recentComment;
	}

	public HashTag getHashTag() {
		return hashtag;
	}
	
	public boolean hasMoreComment(){
		return hasMoreComment;
	}

}
