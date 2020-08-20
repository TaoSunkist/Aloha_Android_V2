package com.wealoha.social.beans

class PostCommentReplyOnMyPost(
    type: Notify2Type,
    unread: Boolean,
    notifyid: String?,  //
    updateTimeMillis: Long,
    private var replyUser2: User2,
    private var fromUser2: User2,
    private var commentId: String,  //
    private var comment: String,
    private var post: Post
) : AbsNotify2(type, unread, notifyid, updateTimeMillis) {
    fun getReplyUser2(): User2 {
        return replyUser2
    }

    fun setReplyUser2(replyUser2: User2) {
        this.replyUser2 = replyUser2
    }

    fun getFromUser2(): User2 {
        return fromUser2
    }

    fun setFromUser2(fromUser2: User2) {
        this.fromUser2 = fromUser2
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