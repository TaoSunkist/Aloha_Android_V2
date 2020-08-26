package com.wealoha.social.beans

data class Comment2DTO(
    var createTimeMillis: Long = 0,
    var user: UserDTO,
    var mine: Boolean = false,
    var id: String? = null,
    var type: String? = null,
    var comment: String,
    var replyUser: UserDTO,
    var whisper: Boolean = false
) {

}