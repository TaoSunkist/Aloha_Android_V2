package com.wealoha.social.api

import android.text.TextUtils
import com.wealoha.social.R
import com.wealoha.social.api.BaseService.*
import com.wealoha.social.beans.*
import com.wealoha.social.beans.CommonImage.Companion.fromDTO
import com.wealoha.social.beans.CommonVideo.Companion.fromDTO
import com.wealoha.social.beans.HashTag.Companion.fromDTO
import com.wealoha.social.beans.PostComment.Companion.fromComment2DTO
import com.wealoha.social.beans.User.Companion.fromDTO
import com.wealoha.social.utils.XL
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.client.Response
import java.util.*
import javax.inject.Inject

class TopicPostService : AbsBaseService<TopicPost?>() {
    @JvmField
    @Inject
    var postAPI: ServerApi? = null
    private var mTopicPosts: TopicPosts? = null
    fun getHashTagResult(serviceListResultCallback: ServiceListResultCallback<HashTag?>) {
        postAPI!!.getHashTag(object : Callback<Result<HashTagResultData>> {
            override fun failure(retrofitError: RetrofitError) {
                serviceListResultCallback.failer()
            }

            override fun success(hashTagResult: Result<HashTagResultData>?, arg1: Response) {
                if (hashTagResult != null && hashTagResult.isOk) {
                    val hashTags = transHashTagDTO2HashTag(
                        hashTagResult.data!!.list
                    )
                    serviceListResultCallback.success(hashTags)
                } else {
                    serviceListResultCallback.failer()
                }
            }
        })
    }

    fun getResult(tagname: String, callback: ServiceObjResultCallback<TopicPosts?>) {
        if (loading) {
            return
        }
        loading = true
        var tempCursor = cursorId
        if (FIRST_PAGE == tempCursor) {
            tempCursor = ""
            list.clear()
        } else if (TextUtils.isEmpty(tempCursor)) {
            loading = false
            callback.nomore()
            return
        }
        XL.d("failure", "tagname$tagname-tempCursor=$tempCursor")
        postAPI!!.getTopicPosts(
            tagname,
            tempCursor,
            AbsBaseService.Companion.COUNT,
            object : Callback<Result<TopicPostResultData>> {
                override fun failure(arg0: RetrofitError) {
                    XL.d("Topic_Refresh", "failure")
                    callback.failer()
                    loading = false
                }

                override fun success(result: Result<TopicPostResultData>, arg1: Response) {
                    if (mTopicPosts == null) {
                        mTopicPosts = TopicPosts()
                    }
                    if (result.isOk) {
                        // TopicPosts topicPosts = new TopicPosts();
                        callback.beforeSuccess()
                        val newPostsList = transTopicPosts2Posts(
                            result.data!!.list!!
                        )
                        val hotPostsList = transTopicPosts2Posts(
                            result.data!!.hot!!
                        )
                        if (FIRST_PAGE == cursorId) { // 首次拉取数据
                            if (hotPostsList.isNotEmpty()) {
                                list.add(topicTitleItem4Hot)
                                list.addAll(transPostsList2GridList(hotPostsList))
                            }
                            if (newPostsList.isNotEmpty()) {
                                list.add(topicTitleItem4New)
                                list.addAll(transPostsList2GridList(newPostsList))
                            }
                            val hashTag = fromDTO(result.data!!.hashtag)
                            mTopicPosts!!.hashTag = hashTag
                        } else { // 最新数据翻页
                            list.addAll(transPostsList2GridList(newPostsList))
                        }
                        mTopicPosts!!.posts.addAll(list.filterNotNull().map { it }.toList())
                        callback.success(mTopicPosts)
                        if (TextUtils.isEmpty(result.data!!.nextCursorId)) {
                            callback.nomore()
                        }
                        cursorId = result.data!!.nextCursorId!!
                    } else {
                        callback.failer()
                    }
                    loading = false
                }
            })
    }

    private val topicTitleItem4Hot: TopicPost
        private get() = getTopicItem(R.string.top_detail_hot_photo_str, TopicPost.TITLE_TYPE)
    private val topicTitleItem4New: TopicPost
        private get() = getTopicItem(R.string.topic_detail_new_photo_str, TopicPost.TITLE_TYPE)

    private fun getTopicItem(strId: Int, itemtype: Int): TopicPost {
        val item = TopicPost()
        // titleItem.setTitleItem(true);
        item.itemType = itemtype
        item.titleId = strId
        return item
    }

    /**
     * 将post列表转换成[TopicPost]的3个一行结构
     *
     * @return List<TopicPost>
    </TopicPost> */
    fun transPostsList2GridList(postsList: List<Post>): List<TopicPost> {
        val tempSzie = if (postsList.size % 3 == 0) 0 else 1
        val size = tempSzie + postsList.size / 3
        val topicPostsList: MutableList<TopicPost> = ArrayList()
        var topicPost: TopicPost? = null
        for (i in 0 until size) {
            topicPost = TopicPost()
            for (j in 0..2) {
                val index = i * 3 + j
                if (postsList.size - 1 < index) {
//                    topicPost.postsItem.add(null)
                    topicPost.isItemFull = false
                    topicPost.setVacancyCount()
                } else {
                    topicPost.postsItem.add(postsList[index])
                }
            }
            topicPost.itemType = TopicPost.NORMAL_TYPE
            topicPostsList.add(topicPost)
        }
        return topicPostsList
    }

    /**
     * 话题页获取的[TopicPostDTO]集合转换为渲染实图用的[Post]
     *
     * @return void
     */
    fun transTopicPosts2Posts(postsDTO: List<TopicPostDTO>): List<Post> {
        var post: Post? = null
        var postlist: MutableList<Post>? = null
        postlist = ArrayList(postsDTO.size)
        for (tpd in postsDTO) {
            val user2 = fromDTO(
                tpd.user, fromDTO(
                    tpd.user!!.avatarImage
                )
            )
            val userTags = transPostTagDTO2PostTag(tpd.userTags)
            val recentCommentList = transComment2DTOListToPostCommentList(tpd.recentComment)
            post = Post(
                tpd.postId,  //
                tpd.type,  //
                tpd.description,  //
                tpd.createTimeMillis,  //
                tpd.mine, tpd.liked,  //
                tpd.tagMe,  //
                tpd.venue,  //
                tpd.venueId,  //
                tpd.latitude,  //
                tpd.longitude,  //
                tpd.venueAbroad,  //
                user2,  //
                userTags,  //
                fromDTO(tpd.image),  //
                fromDTO(tpd.video),  //
                tpd.commentCount,  //
                tpd.praiseCount,  //
                recentCommentList,  //
                fromDTO(tpd.hashtag),  //
                tpd.hasMoreComment
            )
            postlist.add(post)
        }
        return postlist
    }

    /**
     * [CommentDTO] list 转换为渲染视图的 [PostComment] List
     *
     * dtoList
     * userMap
     * imageMap
     * @return void
     */
    private fun transComment2DTOListToPostCommentList(dtoList: List<Comment2DTO>?): List<PostComment>? {
        if (dtoList == null) {
            return null
        }
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

    fun transPostTagDTO2PostTag(tptag: List<TopicPostTagDTO>?): List<UserTag>? {
        var tags: MutableList<UserTag>? = null
        var tag: UserTag? = null
        if (tptag != null) {
            tags = ArrayList(tptag.size)
            for (tpt in tptag) {
                tag = UserTag(
                    tpt.tagAnchorX,  //
                    tpt.tagAnchorY,  //
                    tpt.tagCenterX,  //
                    tpt.tagCenterY,  //
                    fromDTO(tpt.tagUser, fromDTO(tpt.tagUser!!.avatarImage))
                )
                tags.add(tag)
            }
        }
        return tags
    }

    fun transHashTagDTO2HashTag(list: List<HashTagDTO?>?): List<HashTag?>? {
        if (list == null) {
            return null
        }
        val hashTags: MutableList<HashTag?> = ArrayList()
        for (hashTagDTO in list) {
            val hashTag = fromDTO(hashTagDTO)
            hashTags.add(hashTag)
        }
        return hashTags
    }

    override fun getList(
        callback: ServiceResultCallback<TopicPost?>,
        cursorId: String?,
        vararg args: Any?
    ) {
        // TODO Auto-generated method stub
    }
}