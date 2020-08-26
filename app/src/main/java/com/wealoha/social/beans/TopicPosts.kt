package com.wealoha.social.beans

/**
 * 话题页下获取的最新和最热的post集合
 *
 * @author dell-pc
 */
data class TopicPosts(
    val posts: MutableList<TopicPost> = arrayListOf(),
    var cursorId: String? = null,
    var hashTag: HashTag? = null

) {

    fun clear() {
        posts.clear()
        cursorId = null
    }
}