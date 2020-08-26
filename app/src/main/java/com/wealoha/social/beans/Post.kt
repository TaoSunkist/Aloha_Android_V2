package com.wealoha.social.beans

import com.wealoha.social.beans.User.Companion.fromDTO
import com.wealoha.social.beans.feed.UserTags
import java.io.Serializable
import java.util.*

/**
 *
 *
 * @author javamonk
 * @createTime 2015年2月25日 上午10:49:44
 */
class Post(
    val postId: String?,
    val type: FeedType?,
    val description: String?,
    val createTimeMillis: Long,
    val isMine: Boolean,
    var isLiked: Boolean,
    var isTagMe: Boolean,
    val venue: String?,
    var venueId: String?,
    val latitude: Double?,
    val longitude: Double?,
    val venueAbroad: Boolean?,
    val user: User?,
    val userTags: List<UserTag>?,
    val commonImage: CommonImage?,
    val commonVideo: CommonVideo?,
    /***
     * 留言增加后数据刷新
     *
     * @return void
     */
    var commentCount: Int,
    var praiseCount: Int,
    val recentComment: List<PostComment>?,
    val hashTag: HashTag?,
    private val hasMoreComment: Boolean
) : Serializable {

    /***
     * 移除当前用户的tag后数据刷新
     *
     * @return void
     */
    fun removeTagMe() {
        isTagMe = false
    }

    /***
     * 赞后数据刷新
     *
     * @return void
     */
    fun praise() {
        praiseCount = praiseCount + 1
        isLiked = true
    }

    /***
     * 取消赞后数据刷新
     *
     * @return void
     */
    fun dislike() {
        if (praiseCount != 0) {
            praiseCount -= 1
            isLiked = false
        }
    }

    fun setVenueid(venueid: String?) {
        venueId = venueid
    }

    fun hasMoreComment(): Boolean {
        return hasMoreComment
    }

    companion object {
        @kotlin.jvm.JvmField
        val TAG = Post::class.java.name

        /**
         *
         */
        private const val serialVersionUID = 2689425306398055435L
        fun fromDTO(
            postdto: PostDTO,
            commonImage: CommonImage?,
            commonVideo: CommonVideo?,
            user2: User?,
            userTags: List<UserTag>?,
            commentCount: Int,
            praiseCount: Int,
            tagMe: Boolean,
            recentComment: List<PostComment>?,
            hashtag: HashTag?
        ): Post {
            return Post( //
                postdto.postId,  //
                FeedType.Companion.fromValue(postdto.type),  //
                postdto.description,  //
                postdto.createTimeMillis,  //
                postdto.mine,  //
                postdto.liked,  //
                tagMe,  //
                postdto.venue,  //
                postdto.venueId,  //
                postdto.latitude,  //
                postdto.longitude,  //
                postdto.venueAbroad,  //
                user2,  //
                userTags,  //
                commonImage,  //
                commonVideo,  //
                commentCount,  //
                praiseCount,  //
                recentComment,  //
                hashtag,  //
                postdto.hasMoreComment
            )
        }

        @kotlin.jvm.JvmStatic
        fun transTagsToOldVer(
            tags: List<UserTag>,
            tagme: Boolean
        ): ArrayList<UserTags> {
            val userTags = ArrayList<UserTags>(tags.size)
            for (tag in tags) {
                val userTag = UserTags()
                userTag.tagAnchorX = tag.tagAnchorX?.toFloat()
                userTag.tagAnchorY = tag.tagAnchorY?.toFloat()
                userTag.tagCenterX = tag.tagCenterX?.toFloat()
                userTag.tagCenterY = tag.tagCenterY?.toFloat()
                userTag.tagMe = tagme
                userTag.tagUserId = tag.user?.id
                userTag.username = tag.user?.name
                userTags.add(userTag)
            }
            return userTags
        }

        /***
         * 这个feed 是否有当前用户的标签
         *
         * @param userTagList
         * 圈人列表
         * @param userId
         * 当前用户id
         * @return 如果任意参数为空，那么返回false
         */
        @kotlin.jvm.JvmStatic
        fun hasTagForMe(
            userTagList: List<UserTag>?,
            userId: String?
        ): Boolean {
            if (userTagList == null) {
                return false
            }

            // if (TextUtils.isEmpty(userId)) {
            for (userTag in userTagList) {
                if (userTag.user?.me == true) {
                    return true
                }
            }
            // }
            return false
        }

        @kotlin.jvm.JvmStatic
        fun fromDTO(
            postDTO: PostDTO,
            userMap: Map<String?, UserDTO?>,
            imageMap: Map<String?, ImageCommonDto?>,
            video: Map<String?, VideoCommonDTO?>,
            commentCountMap: Map<String?, Int?>,
            praiseCountMap: Map<String?, Int?>
        ): Post {
            val userTagList: List<UserTag>? =
                UserTag.Companion.fromDTOList(postDTO.userTags, userMap, imageMap)
            val userDTO = userMap[postDTO.userId]
            var commonImage: CommonImage? = null
            var user2: User? = null
            if (userDTO != null) {
                commonImage = CommonImage.Companion.fromDTO(imageMap[userDTO.avatarImageId])
                user2 = fromDTO(userDTO, commonImage)
            }
            return Post( //
                postDTO.postId,  //
                FeedType.Companion.fromValue(postDTO.type),  //
                postDTO.description,  //
                postDTO.createTimeMillis,  //
                postDTO.mine,  //
                postDTO.liked,  //
                hasTagForMe(userTagList, postDTO.userId),  //
                postDTO.venue,  //
                postDTO.venueId,  //
                postDTO.latitude,  //
                postDTO.longitude,  //
                postDTO.venueAbroad,  //
                user2,  //
                userTagList,  //
                CommonImage.Companion.fromDTO(imageMap[postDTO.imageId]),  //
                CommonVideo.Companion.fromDTO(video[postDTO.videoId]),  //
                commentCountMap[postDTO.postId]!!,  //
                praiseCountMap[postDTO.postId]!!,  //
                PostComment.Companion.fromDTOV2List(postDTO.recentComments),  //
                HashTag.Companion.fromDTO(postDTO.hashtag),  //
                postDTO.hasMoreComment
            )
        }

        @kotlin.jvm.JvmStatic
        fun fromPostDTOList(
            dtoList: List<PostDTO>,
            userMap: Map<String?, UserDTO?>,
            imageMap: Map<String?, ImageCommonDto?>,
            videoMap: Map<String?, VideoCommonDTO?>,
            commentCountMap: Map<String?, Int?>,
            praiseCountMap: Map<String?, Int?>
        ): List<Post> {
            val postList: MutableList<Post> = ArrayList()
            for (postDTO in dtoList) {
                val post = fromDTO(
                    postDTO,
                    userMap,
                    imageMap,
                    videoMap,
                    commentCountMap,
                    praiseCountMap
                )
                postList.add(post)
            }
            return postList
        }
    }

}