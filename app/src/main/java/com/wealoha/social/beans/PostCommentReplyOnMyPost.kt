package com.wealoha.social.beans

class PostCommentReplyOnMyPost(
    type: Notify2Type,
    unread: Boolean,
    notifyid: String?,  //
    updateTimeMillis: Long,
    private var replyUser: User,
    private var fromUser: User,
    private var commentId: String,  //
    private var comment: String,
    private var post: Post
) : AbsNotify2(type, unread, notifyid, updateTimeMillis) {
    fun getReplyUser(): User {
        return replyUser
    }

    fun setReplyUser(replyUser: User) {
        this.replyUser = replyUser
    }

    fun getFromUser(): User {
        return fromUser
    }

    fun setFromUser(fromUser: User) {
        this.fromUser = fromUser
    }

    fun getComment(): String {
        return comment
    }

    fun setComment(comment: String) {
        this.comment = comment
    }

    fun getCommentId(): String {
        return commentId
    }

    fun setCommentId(commentId: String) {
        this.commentId = commentId
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
        private const val serialVersionUID = 1L
    }

}