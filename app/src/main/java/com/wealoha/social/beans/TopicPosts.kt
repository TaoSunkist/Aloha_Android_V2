package com.wealoha.social.beans

/**
 * 话题页下获取的最新和最热的post集合
 *
 * @author dell-pc
 */
class TopicPosts {
    private var posts: MutableList<TopicPost>? = null
    var cursorId: String? = null
    var hashTag: HashTag? = null
    fun getPosts(): List<TopicPost>? {
        return posts
    }

    fun setPosts(posts: MutableList<TopicPost>?) {
        this.posts = posts
    }

    fun clear() {
        posts!!.clear()
        cursorId = null
    }

}