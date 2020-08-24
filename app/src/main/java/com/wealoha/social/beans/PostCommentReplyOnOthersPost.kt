package com.wealoha.social.beans

class PostCommentReplyOnOthersPost(
    type: Notify2Type,
    unread: Boolean,
    notifyid: String?,
    updateTimeMillis: Long,  //
    private var postAuthor: User,
    private var fromUser: User,
    private var commentId: String,
    private var comment: String,
    private var post: Post
) : AbsNotify2(type, unread, notifyid, updateTimeMillis) {
    fun getPostAuthor(): User {
        return postAuthor
    }

    fun setPostAuthor(postAuthor: User) {
        this.postAuthor = postAuthor
    }

    fun getFromUser(): User {
        return fromUser
    }

    fun setFromUser(fromUser: User) {
        this.fromUser = fromUser
    }

    fun getCommentId(): String {
        return commentId
    }

    fun setCommentId(commentId: String) {
        this.commentId = commentId
    }

    fun getComment(): String {
        return comment
    }

    fun setComment(comment: String) {
        this.comment = comment
    }

    fun getPost(): Post {
        return post
    }

    fun setPost(post: Post) {
        this.post = post
    }

    companion object {
        /**
         *
         */
        private const val serialVersionUID = 3241745037614905208L
    }

}