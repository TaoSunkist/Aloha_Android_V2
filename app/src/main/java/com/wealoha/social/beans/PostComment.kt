package com.wealoha.social.beans

import android.os.Parcelable
import com.wealoha.social.beans.User.Companion.fromDTO
import kotlinx.android.parcel.Parcelize
import java.io.Serializable
import java.util.*

@Parcelize
data class PostComment(
    val createTimeMillis: Long,
    val id: String?,
    val comment: String,
    val replyUser: User,
    val user: User,
    val whisper: Boolean
) : Serializable, Parcelable {


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

        fun fromCommentDTO(commentDTO: CommentDTO, replyUser: User, user: User): PostComment {
            return PostComment( //
                commentDTO.createTimeMillis,  //
                commentDTO.id,  //
                commentDTO.comment?:"",  //
                replyUser,  //
                user,  //
                commentDTO.whisper
            )
        }

        fun fromComment2DTO(
            comment2DTO: Comment2DTO,
            replyUser: User,
            user: User
        ): PostComment {
            return PostComment(
                comment2DTO.createTimeMillis,
                comment2DTO.id,
                comment2DTO.comment,
                replyUser,
                user,
                comment2DTO.whisper
            )
        }

        fun fromCommentDTO(commentDTO: CommentDTO): PostComment {
            return PostComment(
                commentDTO.createTimeMillis,  //
                commentDTO.id,  //
                commentDTO.comment?:"",  //
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

        fun fromDTOV2List(commentList: List<Comment2DTO>?): List<PostComment>? {
            if (commentList == null) {
                return null
            }
            val postCommentList: MutableList<PostComment> =
                ArrayList()
            for (dto in commentList) {
                val user2: User = fromDTO(dto.user, CommonImage.fromDTO(dto.user.avatarImage))
                val replyUser: User = fromDTO(
                    dto.replyUser,
                    CommonImage.fromDTO(dto.replyUser.avatarImage)
                )
                val postComment = fromComment2DTO(dto, replyUser, user2)
                postCommentList.add(postComment)
            }
            return postCommentList
        }
    }

}