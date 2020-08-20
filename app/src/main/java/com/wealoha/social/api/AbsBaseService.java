package com.wealoha.social.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;

import android.text.TextUtils;

import com.wealoha.social.beans.PostComment;
import com.wealoha.social.beans.Comment2DTO;
import com.wealoha.social.beans.CommentDTO;
import com.wealoha.social.beans.CommonImage;
import com.wealoha.social.beans.CommonVideo;
import com.wealoha.social.beans.ImageCommonDto;
import com.wealoha.social.beans.VideoCommonDTO;
import com.wealoha.social.beans.FeedType;
import com.wealoha.social.beans.UserTag;
import com.wealoha.social.beans.UserTagsDTO;
import com.wealoha.social.beans.Post;
import com.wealoha.social.beans.PostDTO;
import com.wealoha.social.beans.User2;
import com.wealoha.social.beans.UserDTO;

/**
 * @author javamonk
 * @createTime 2015年2月25日 下午12:13:33
 */
public abstract class AbsBaseService<E, P> implements BaseListApiService<E, P> {

    @Override
    public boolean appendToHeader() {
        return false;
    }

    @Override
    public boolean needReverse() {
        return false;
    }

    @Override
    public boolean supportContextByCursor() {
        return false;
    }

    @Override
    public boolean supportPrev() {
        return false;
    }

    protected Post getPost(PostDTO postDto, Map<String, UserDTO> userMap,//
                           Map<String, ImageCommonDto> imageMap,//
                           Map<String, VideoCommonDTO> videoMap,//
                           Map<String, Integer> commentCount,//
                           Map<String, Integer> praiseCount) {
        return getPost(postDto, userMap, imageMap, videoMap, commentCount, praiseCount, null);
    }

    /***
     * 得到帶有“是否含有當前用戶的標籤”的post
     *
     * @param postDto
     * @param userMap
     * @param imageMap
     * @param videoMap
     *  commentCount
     *  praiseCount
     * @param currentUserid
     *            當前用戶id
     * @see {@link #getPost(PostDTO, Map, Map, Map, Map, Map)}
     * @return Post
     */
    protected Post getPost(PostDTO postDto, Map<String, UserDTO> userMap,//
                           Map<String, ImageCommonDto> imageMap,//
                           Map<String, VideoCommonDTO> videoMap,//
                           Map<String, Integer> commentCountMap,//
                           Map<String, Integer> praiseCountMap, String currentUserid) {

        FeedType type = FeedType.fromValue(postDto.type);

        if (type == null) {
            // XL.d(TAG, "不支持的通知类型: " + postDto.type);
            return null;
        }
        User2 user2 = getUser(postDto.userId, userMap, imageMap);
        List<UserTag> userTagList = gerUserTagList(postDto.userTags, userMap, imageMap);
        CommonImage commonImage = CommonImage.fromDTO(imageMap.get(postDto.imageId));
        CommonVideo commonVideo = null;
        if (videoMap != null) {
            commonVideo = CommonVideo.fromDTO(videoMap.get(postDto.videoId));
        }

        List<PostComment> recentCommentList = transCommentDTOList2PostCommentList(postDto.recentComments, userMap, imageMap);

        Integer commentCount = 0;
        Integer praiseCount = 0;
        if (commentCountMap != null && !TextUtils.isEmpty(postDto.userId)) {
            commentCount = commentCountMap.get(postDto.userId);
        }
        if (praiseCountMap != null && !TextUtils.isEmpty(postDto.userId)) {
            praiseCount = praiseCountMap.get(postDto.userId);
        }

        return Post.fromDTO(postDto, commonImage, commonVideo, user2, userTagList,//
                commentCount == null ? 0 : commentCount,//
                praiseCount == null ? 0 : praiseCount, Post.hasTagForMe(userTagList, currentUserid),//
                recentCommentList, null);
    }

    /**
     * {@link CommentDTO} list 转换为渲染视图的 {@link PostComment} List
     *
     * @param dtoList
     * @param userMap
     * @param imageMap
     * @return void
     */
    protected List<PostComment> transCommentDTOList2PostCommentList(List<Comment2DTO> dtoList, Map<String, UserDTO> userMap, Map<String, ImageCommonDto> imageMap) {
        if (dtoList == null) {
            return null;
        }
        List<PostComment> postCommentList = new ArrayList<>();
        for (Comment2DTO dto : dtoList) {
            User2 user2 = null;
            User2 replyUser2 = null;
            if (dto.user != null) {
                user2 = User2.fromDTO(dto.user, CommonImage.fromDTO(dto.user.avatarImage));
            }
            if (dto.replyUser != null) {
                replyUser2 = User2.fromDTO(dto.replyUser, CommonImage.fromDTO(dto.replyUser.avatarImage));
            }

            PostComment postComment = PostComment.fromComment2DTO(dto, replyUser2, user2);
            postCommentList.add(postComment);
        }
        return postCommentList;
    }

    protected List<User2> getUsers(List<String> userIds, Map<String, UserDTO> userMap, Map<String, ImageCommonDto> imageMap) {
        if (CollectionUtils.isEmpty(userIds)) {
            return Collections.emptyList();
        }
        List<User2> result = new ArrayList<User2>();
        for (String userId : userIds) {
            result.add(getUser(userId, userMap, imageMap));
        }
        return result;
    }

    protected List<User2> getUsers(List<UserDTO> userList, Map<String, ImageCommonDto> imageMap) {
        if (CollectionUtils.isEmpty(userList) || imageMap == null) {
            return Collections.emptyList();
        }
        ArrayList<User2> user2s = new ArrayList<User2>(userList.size());
        for (UserDTO userDTO : userList) {
            user2s.add(User2.fromDTO(userDTO, getImage(userDTO.avatarImageId, imageMap)));
        }
        return user2s;
    }

    protected User2 getUser(String userId, Map<String, UserDTO> userMap, Map<String, ImageCommonDto> imageMap) {
        UserDTO userDTO = userMap.get(userId);
        if (userDTO == null) {
            return null;
        }

        return User2.fromDTO(userDTO, getImage(userDTO.avatarImageId, imageMap));
    }

    protected CommonImage getImage(String imageId, Map<String, ImageCommonDto> imageMap) {
        ImageCommonDto imageCommonDto = imageMap.get(imageId);
        if (imageCommonDto == null) {
            return null;
        }
        return CommonImage.fromDTO(imageCommonDto);
    }

    protected CommonVideo getVideo(String Videoid, Map<String, VideoCommonDTO> videoMap) {
        VideoCommonDTO videoCommonDTO = videoMap.get(Videoid);
        if (videoCommonDTO == null) {
            return null;
        }
        return CommonVideo.fromDTO(videoCommonDTO);
    }

    @Override
    public void getListWithContext(String cursor, int count, P param, BaseListApiService.ListContextCallback<E> callback) {
        throw new UnsupportedOperationException("不支持从中间取数据");
    }

    /***
     * 返回适合填充视图的 usertag list
     *
     * @param userTagDTOList
     *            原始圈人数据
     * @param userMap
     *            原始用户列表
     * @param imageMap
     *            原始的图片列表
     * @return 如果任意一个参数 为空 ， 那么返回值也为空
     */
    public List<UserTag> gerUserTagList(List<UserTagsDTO> userTagDTOList, Map<String, UserDTO> userMap, Map<String, ImageCommonDto> imageMap) {
        if (userTagDTOList == null || userMap == null || imageMap == null) {
            return null;
        }
        List<UserTag> userTagList = new ArrayList<UserTag>(userTagDTOList.size());
        UserDTO userDto;
        CommonImage commonImage;
        for (UserTagsDTO userTagDto : userTagDTOList) {
            userDto = userMap.get(userTagDto.tagUserId);
            commonImage = CommonImage.fromDTO(imageMap.get(userDto.avatarImageId));
            userTagList.add(new UserTag(userTagDto.tagAnchorX, userTagDto.tagAnchorY,//
                    userTagDto.tagCenterX, userTagDto.tagCenterY, User2.fromDTO(userDto, commonImage)));
        }
        return userTagList;
    }

    @Override
    public void setAdapterListCallback(BaseListApiService.AdapterListDataCallback<E> callback) {
        // 子类实现具体方法
    }

    @Override
    public void fetchPhoto(int firstVisibleItem, int totalItemCount, boolean direction, int mScreenWidth) {
        // 子类实现具体方法
    }
}
