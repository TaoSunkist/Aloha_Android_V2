package com.wealoha.social.beans

import com.mooveit.library.Fakeit

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

    companion object {
        fun fake(): Comment2DTO {
            return Comment2DTO(
                createTimeMillis = System.currentTimeMillis(),
                user = UserDTO.fake(),
                mine = (0..1).random() == 1,
                id = System.currentTimeMillis().toString(),
                type = "",
                comment = Fakeit.book().title(),
                replyUser = UserDTO.fake(),
                whisper = (0..1).random() == 1
            )
        }
    }
}