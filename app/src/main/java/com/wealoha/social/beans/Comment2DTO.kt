package com.wealoha.social.beans

class Comment2DTO {
    var createTimeMillis: Long = 0
    @kotlin.jvm.JvmField
    var user: UserDTO? = null
    var mine = false
    var id: String? = null
    var type: String? = null
    var comment: String? = null
    @kotlin.jvm.JvmField
    var replyUser: UserDTO? = null
    var whisper = false
}