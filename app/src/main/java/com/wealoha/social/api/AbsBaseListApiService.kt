package com.wealoha.social.api

import android.text.TextUtils
import com.wealoha.social.api.BaseListApiService.AdapterListDataCallback
import com.wealoha.social.api.BaseListApiService.ListContextCallback
import com.wealoha.social.beans.*
import com.wealoha.social.beans.CommonImage.Companion.fromDTO
import com.wealoha.social.beans.CommonVideo.Companion.fromDTO
import com.wealoha.social.beans.Post.Companion.fromDTO
import com.wealoha.social.beans.Post.Companion.hasTagForMe
import com.wealoha.social.beans.PostComment.Companion.fromComment2DTO
import com.wealoha.social.beans.User.Companion.fromDTO
import org.apache.commons.collections4.CollectionUtils
import java.util.*

/**
 * @author javamonk
 * @createTime 2015年2月25日 下午12:13:33
 */
abstract class AbsBaseListApiService<E, P> : BaseListApiService<E, P> {
    override fun appendToHeader(): Boolean {
        return false
    }

    override fun needReverse(): Boolean {
        return false
    }

    override fun supportContextByCursor(): Boolean {
        return false
    }

    override fun supportPrev(): Boolean {
        return false
    }

    protected fun getPost(
        postDto: PostDTO, userMap: Map<String, UserDTO>,  //
        imageMap: Map<String, ImageCommonDto>,  //
        videoMap: Map<String, VideoCommonDTO>,  //
        commentCount: Map<String, Int>,  //
        praiseCount: Map<String, Int>
    ): Post {
        return getPost(postDto, userMap, imageMap, videoMap, commentCount, praiseCount, "")
    }

    /***
     * 得到帶有“是否含有當前用戶的標籤”的post
     *
     * @param postDto
     * @param userMap
     * @param imageMap
     * @param videoMap
     * commentCount
     * praiseCount
     * @param currentUserid
     * 當前用戶id
     * @see {@link .getPost
     * @return Post
     */
    protected fun getPost(
        postDto: PostDTO, userMap: Map<String, UserDTO>,  //
        imageMap: Map<String, ImageCommonDto>,  //
        videoMap: Map<String, VideoCommonDTO>,  //
        commentCountMap: Map<String, Int>,  //
        praiseCountMap: Map<String, Int>, currentUserid: String
    ): Post {
        val type = FeedType.fromValue(postDto.type)
        val user2 = getUser(postDto.userId, userMap, imageMap)
        val userTagList = gerUserTagList(postDto.userTags, userMap, imageMap)
        val commonImage = fromDTO(imageMap[postDto.imageId]!!)
        val commonVideo = fromDTO(videoMap[postDto.videoId]!!)
        val recentCommentList =
            transCommentDTOList2PostCommentList(postDto.recentComments, userMap, imageMap)
        var commentCount: Int = 0
        var praiseCount: Int = 0
        if (!TextUtils.isEmpty(postDto.userId)) {
            commentCount = commentCountMap[postDto.userId]!!
        }
        if (!TextUtils.isEmpty(postDto.userId)) {
            praiseCount = praiseCountMap[postDto.userId]!!
        }
        return fromDTO(
            postDto = postDto,
            commonImage = commonImage,
            commonVideo = commonVideo,
            user2 = user2,
            userTagList = userTagList,
            commentCount = commentCount,
            praiseCount = praiseCount,
            hasTagForMe = hasTagForMe(userTagList, currentUserid),
            recentCommentList = recentCommentList
        )
    }

    private fun fromDTO(
        postDto: PostDTO,
        commonImage: CommonImage,
        commonVideo: CommonVideo,
        user2: User,
        userTagList: List<UserTag>,
        commentCount: Int,
        praiseCount: Int,
        hasTagForMe: Boolean,
        recentCommentList: List<PostComment>
    ): Post {
//        postdto: PostDTO,
//        commonImage: CommonImage?,
//        commonVideo: CommonVideo?,
//        user2: User?,
//        userTags: List<UserTag>?,
//        commentCount: Int,
//        praiseCount: Int,
//        tagMe: Boolean,
//        recentComment: List<PostComment>?,
//        hashtag: HashTag?
        return fromDTO(
            postdto = postDto,
            commonImage = commonImage,
            commonVideo = commonVideo,
            user2 = user2,
            userTags = userTagList,
            commentCount = commentCount,
            praiseCount = praiseCount,
            tagMe = hasTagForMe,
            recentComment = recentCommentList,
            hashtag = null
        )
    }

    /**
     * [CommentDTO] list 转换为渲染视图的 [PostComment] List
     *
     * @param dtoList
     * @param userMap
     * @param imageMap
     * @return void
     */
    protected fun transCommentDTOList2PostCommentList(
        dtoList: List<Comment2DTO>,
        userMap: Map<String, UserDTO>,
        imageMap: Map<String, ImageCommonDto>
    ): List<PostComment> {
        val postCommentList: MutableList<PostComment> = ArrayList()
        for (dto in dtoList) {
            val user2 = fromDTO(
                dto.user, fromDTO(
                    dto.user!!.avatarImage
                )
            )
            val replyUser = fromDTO(
                dto.replyUser, fromDTO(
                    dto.replyUser!!.avatarImage
                )
            )
            val postComment = fromComment2DTO(dto, replyUser, user2)
            postCommentList.add(postComment)
        }
        return postCommentList
    }

    protected fun getUsers(
        userIds: List<String>,
        userMap: Map<String, UserDTO>,
        imageMap: Map<String, ImageCommonDto>
    ): List<User> {
        if (CollectionUtils.isEmpty(userIds)) {
            return emptyList()
        }
        val result: MutableList<User> = ArrayList()
        for (userId in userIds) {
            result.add(getUser(userId, userMap, imageMap))
        }
        return result
    }

    protected fun getUsers(
        userList: List<UserDTO>,
        imageMap: Map<String, ImageCommonDto>
    ): List<User> {
        if (CollectionUtils.isEmpty(userList) || imageMap == null) {
            return emptyList()
        }
        val user2s = ArrayList<User>(userList.size)
        for (userDTO in userList) {
            user2s.add(fromDTO(userDTO, getImage(userDTO!!.avatarImageId, imageMap)))
        }
        return user2s
    }

    protected fun getUser(
        userId: String,
        userMap: Map<String, UserDTO>,
        imageMap: Map<String, ImageCommonDto>
    ): User {
        val userDTO = userMap[userId]!!
        return fromDTO(userDTO, getImage(userDTO.avatarImageId, imageMap))
    }

    private fun getImage(
        avatarImageId: String,
        imageMap: Map<String, ImageCommonDto>
    ): CommonImage {
        val imageCommonDto = imageMap[avatarImageId]!!
        return fromDTO(imageCommonDto)
    }

    override fun getListWithContext(
        cursor: String,
        count: Int,
        param: P,
        callback: ListContextCallback<E>
    ) {
        throw UnsupportedOperationException("不支持从中间取数据")
    }

    /***
     * 返回适合填充视图的 usertag list
     *
     * @param userTagDTOList
     * 原始圈人数据
     * @param userMap
     * 原始用户列表
     * @param imageMap
     * 原始的图片列表
     * @return 如果任意一个参数 为空 ， 那么返回值也为空
     */
    fun gerUserTagList(
        userTagDTOList: List<UserTagsDTO>,
        userMap: Map<String, UserDTO>,
        imageMap: Map<String, ImageCommonDto>
    ): List<UserTag> {
        val userTagList: MutableList<UserTag> = ArrayList(userTagDTOList.size)
        var userDto: UserDTO
        var commonImage: CommonImage
        for (userTagDto in userTagDTOList) {
            userDto = userMap[userTagDto.tagUserId]!!
            commonImage = fromDTO(imageMap[userDto.avatarImageId]!!)
            userTagList.add(
                UserTag(
                    userTagDto.tagAnchorX, userTagDto.tagAnchorY,  //
                    userTagDto.tagCenterX, userTagDto.tagCenterY, fromDTO(userDto, commonImage)
                )
            )
        }
        return userTagList
    }

    override fun setAdapterListCallback(callback: AdapterListDataCallback<E>) {
        // 子类实现具体方法
    }

    override fun fetchPhoto(
        firstVisibleItem: Int,
        totalItemCount: Int,
        direction: Boolean,
        mScreenWidth: Int
    ) {
        // 子类实现具体方法
    }
}