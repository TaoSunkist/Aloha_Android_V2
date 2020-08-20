package com.wealoha.social.beans

import com.wealoha.social.beans.User2.Companion.fromDTO
import java.io.Serializable
import java.util.*

data class PostComment(
    val createTimeMillis: Long,
    val id: String?,
    val comment: String?,
    val replyUser2: User2?,
    val user2: User2?,
    val whisper: Boolean
) : Serializable {


    fun isWhisper(): Boolean {
        return whisper
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + (id?.hashCode() ?: 0)
        return result
    }

    override fun equals(obj: Any?): Boolean {
        if (this === obj) return true
        if (obj == null) return false
        if (javaClass != obj.javaClass) return false
        val other = obj as PostComment
        if (id == null) {
            if (other.id != null) return false
        } else if (id != other.id) return false
        return true
    }

    companion object {

        fun fromCommentDTO(commentDTO: CommentDTO, replyUser2: User2?, user2: User2?): PostComment {
            return PostComment( //
                commentDTO.createTimeMillis,  //
                commentDTO.id,  //
                commentDTO.comment,  //
                replyUser2,  //
                user2,  //
                commentDTO.whisper
            )
        }

        fun fromComment2DTO(
            comment2DTO: Comment2DTO?,
            replyUser2: User2?,
            user2: User2?
        ): PostComment {
            return PostComment(
                comment2DTO!!.createTimeMillis,
                comment2DTO.id,
                comment2DTO.comment,
                replyUser2,
                user2,
                comment2DTO.whisper
            )
        }

        fun fromCommentDTO(commentDTO: CommentDTO): PostComment {
            return PostComment(
                commentDTO.createTimeMillis,  //
                commentDTO.id,  //
                commentDTO.comment,  //
                fromDTO(commentDTO.replyUser!!),  //
                fromDTO(commentDTO.user!!),  //
                commentDTO.whisper
            )
        }

        fun fromCommentDTOList(commentList: List<CommentDTO>?): List<PostComment>? {
            if (commentList == null) {
                return null
            }
            val posts: MutableList<PostComment> =
                ArrayList()
            for (commentDTO in commentList) {
                posts.add(fromCommentDTO(commentDTO))
            }
            return posts
        }

        fun fromDTOV2List(commentList: List<Comment2DTO?>?): List<PostComment>? {
            if (commentList == null) {
                return null
            }
            val postCommentList: MutableList<PostComment> =
                ArrayList()
            for (dto in commentList) {
                var user2: User2? = null
                var replyUser2: User2? = null
                if (dto!!.user != null) {
                    user2 =
                        fromDTO(dto.user, CommonImage.Companion.fromDTO(dto.user!!.avatarImage))
                }
                if (dto.replyUser != null) {
                    replyUser2 = fromDTO(
                        dto.replyUser,
                        CommonImage.Companion.fromDTO(dto.replyUser!!.avatarImage)
                    )
                }
                val postComment =
                    fromComment2DTO(dto, replyUser2, user2)
                postCommentList.add(postComment)
            }
            return postCommentList
        }
    }

}