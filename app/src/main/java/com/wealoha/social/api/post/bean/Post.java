package com.wealoha.social.api.post.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.wealoha.social.api.comment.bean.PostComment;
import com.wealoha.social.api.common.bean.Image;
import com.wealoha.social.api.common.bean.Video;
import com.wealoha.social.api.common.dto.ImageDTO;
import com.wealoha.social.api.common.dto.VideoDTO;
import com.wealoha.social.beans.FeedType;
import com.wealoha.social.beans.UserTag;
import com.wealoha.social.api.post.dto.PostDTO;
import com.wealoha.social.api.topic.bean.HashTag;
import com.wealoha.social.api.user.bean.User;
import com.wealoha.social.api.user.dto.UserDTO;
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
	private final User user;
	private final List<UserTag> userTags;
	private final Image image;
	private final Video video;
	private boolean tagMe;
	private boolean liked;
	private int commentCount;
	private int praiseCount;
	private List<PostComment> recentComment;
	private HashTag hashtag;
	private boolean hasMoreComment;

	public Post(String postId, FeedType type, String description, long createTimeMillis, boolean mine, boolean liked, boolean tagMe, String venue, String venueId, Double latitude, Double longitude, Boolean venueAbroad, User user, List<UserTag> userTags, Image image, Video video, int commentCount, int praiseCount, List<PostComment> recentComment, HashTag hashtag, boolean hasMoreComment) {
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
		this.user = user;
		this.userTags = userTags;
		this.image = image;
		this.video = video;
		this.commentCount = commentCount;
		this.praiseCount = praiseCount;
		this.recentComment = recentComment;
		this.hashtag = hashtag;
		this.hasMoreComment = hasMoreComment;
	}

	public static Post fromDTO(PostDTO postdto, Image image, Video video, User user, List<UserTag> userTags, int commentCount, int praiseCount, boolean tagMe, List<PostComment> recentComment, HashTag hashtag) {
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
		user,//
		userTags,//
		image,//
		video,//
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
			userTag.tagUserId = tag.getUser().getId();
			userTag.setUsername(tag.getUser().getName());
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
			if (userTag.getUser().isMe()) {
				return true;
			}
		}
		// }
		return false;
	}

	public static Post fromDTO(PostDTO postDTO, Map<String, UserDTO> userMap, Map<String, ImageDTO> imageMap, Map<String, VideoDTO> video, Map<String, Integer> commentCountMap, Map<String, Integer> praiseCountMap) {
		List<UserTag> userTagList = UserTag.fromDTOList(postDTO.userTags, userMap, imageMap);

		UserDTO userDTO = userMap.get(postDTO.userId);
		Image image = null;
		User user = null;
		if (userDTO != null) {
			image = Image.fromDTO(imageMap.get(userDTO.avatarImageId));
			user = User.fromDTO(userDTO, image);
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
		user,//
		userTagList,//
		Image.fromDTO(imageMap.get(postDTO.imageId)),//
		Video.fromDTO(video.get(postDTO.videoId)),//
		commentCountMap.get(postDTO.postId),//
		praiseCountMap.get(postDTO.postId),//
		PostComment.fromDTOV2List(postDTO.recentComments),//
		HashTag.fromDTO(postDTO.hashtag),//
		postDTO.hasMoreComment);
	}

	public static List<Post> fromPostDTOList(List<PostDTO> dtoList, Map<String, UserDTO> userMap, Map<String, ImageDTO> imageMap, Map<String, VideoDTO> videoMap, Map<String, Integer> commentCountMap, Map<String, Integer> praiseCountMap) {
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

	public User getUser() {
		return user;
	}

	public List<UserTag> getUserTags() {
		return userTags;
	}

	public Image getImage() {
		return image;
	}

	public Video getVideo() {
		return video;
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
