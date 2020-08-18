package com.wealoha.social.beans;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

import com.wealoha.social.beans.feed.UserTags;

/**
 * @author javamonk
 * @createTime 14-10-12 AM11:15
 */
public class Feed implements Parcelable {

	/**
	 * @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么)
	 */

	public static final String TAG = Feed.class.getSimpleName();

	public long createTimeMillis;
	public String description;
	public String imageId;
	public boolean mine;
	public String postId;
	public String type;
	public String userId;
	public boolean liked;
	public Double latitude;
	public Double longitude;
	public String venue;
	public List<UserTags> userTags;
	public Boolean tagMe;
	public Boolean venueAbroad;
	public String venueId;
	public int commentCount;
	public int likeCount;
	public String videoId;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((postId == null) ? 0 : postId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Feed other = (Feed) obj;
		if (postId == null) {
			if (other.postId != null)
				return false;
		} else if (!postId.equals(other.postId))
			return false;
		return true;
	}

	public Feed() {
	}

	public Feed(Parcel source) {
		// 必须按成员变量声明的顺序读取数据，不然会出现获取数据出错
		setCreateTimeMillis(source.readLong());
		setDescription(source.readString());
		setImageId(source.readString());
		setMine(source.readByte() != 0);
		setPostId(source.readString());
		setType(source.readString());
		setUserId(source.readString());
		setLiked(source.readByte() != 0);
		setLatitude((Double) source.readValue(Double.class.getClassLoader()));
		setLongitude((Double) source.readValue(Double.class.getClassLoader()));
		setVenue(source.readString());
		if (userTags == null) {
			userTags = new ArrayList<UserTags>();
		}
		source.readTypedList(userTags, UserTags.CREATOR);
		setTagMe((Boolean) source.readValue(Boolean.class.getClassLoader()));
		setVenueAbroad((Boolean) source.readValue(Boolean.class.getClassLoader()));
		setVenueId(source.readString());
		setCommentCount(source.readInt());
		setLikeCount(source.readInt());
	}

	public static final Parcelable.Creator<Feed> CREATOR = new Creator<Feed>() {

		@Override
		public Feed createFromParcel(Parcel source) {
			return new Feed(source);
		}

		@Override
		public Feed[] newArray(int size) {
			return new Feed[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(createTimeMillis);
		dest.writeString(description);
		dest.writeString(imageId);
		dest.writeByte((byte) (mine ? 1 : 0));
		dest.writeString(postId);
		dest.writeString(type);
		dest.writeString(userId);
		dest.writeByte((byte) (liked ? 1 : 0));
		dest.writeValue(latitude);
		dest.writeValue(longitude);

		dest.writeString(venue);
		dest.writeTypedList(userTags);
		dest.writeValue(tagMe);
		dest.writeValue(venueAbroad);
		dest.writeString(venueId);
		dest.writeInt(commentCount);
		dest.writeInt(likeCount);
	}

	public long getCreateTimeMillis() {
		return createTimeMillis;
	}

	public void setCreateTimeMillis(long createTimeMillis) {
		this.createTimeMillis = createTimeMillis;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImageId() {
		return imageId;
	}

	public void setImageId(String imageId) {
		this.imageId = imageId;
	}

	public boolean isMine() {
		return mine;
	}

	public void setMine(boolean mine) {
		this.mine = mine;
	}

	public String getPostId() {
		return postId;
	}

	public void setPostId(String postId) {
		this.postId = postId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public boolean isLiked() {
		return liked;
	}

	public void setLiked(boolean liked) {
		this.liked = liked;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public String getVenue() {
		return venue;
	}

	public void setVenue(String venue) {
		this.venue = venue;
	}

	public List<UserTags> getUserTags() {
		return userTags;
	}

	public void setUserTags(List<UserTags> userTags) {
		this.userTags = userTags;
	}

	public Boolean getTagMe() {
		return tagMe;
	}

	public void setTagMe(Boolean tagMe) {
		this.tagMe = tagMe;
	}

	public Boolean getVenueAbroad() {
		return venueAbroad;
	}

	public void setVenueAbroad(Boolean venueAbroad) {
		this.venueAbroad = venueAbroad;
	}

	public String getVenueId() {
		return venueId;
	}

	public void setVenueId(String venueId) {
		this.venueId = venueId;
	}

	public int getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}

	public int getLikeCount() {
		return likeCount;
	}

	public void setLikeCount(int likeCount) {
		this.likeCount = likeCount;
	}

}
