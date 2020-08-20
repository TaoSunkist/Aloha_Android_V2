package com.wealoha.social.beans

class PostCommentReplyOnOthersPost(
    type: Notify2Type,
    unread: Boolean,
    notifyid: String?,
    updateTimeMillis: Long,  //
    private var postAuthor: User2,
    private var fromUser2: User2,
    private var commentId: String,
    private var comment: String,
    private var post: Post
) : AbsNotify2(type, unread, notifyid, updateTimeMillis) {
    fun getPostAuthor(): User2 {
        return postAuthor
    }

    fun setPostAuthor(postAuthor: User2) {
        this.postAuthor = postAuthor
    }

    fun getFromUser2(): User2 {
        return fromUser2
    }

    fun setFromUser2(fromUser2: User2) {
        this.fromUser2 = fromUser2
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