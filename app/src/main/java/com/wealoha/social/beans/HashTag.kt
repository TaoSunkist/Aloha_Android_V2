package com.wealoha.social.beans

import java.io.Serializable

class HashTag(
    val itemId: String?,
    val name: String?,
    val backgroundImage: CommonImage?,
    val summary: String?,
    val postCount: Int,
    val url: String?,
    val type: String?,
    val userCount: Int
) : Serializable {

    companion object {
        /**
         *
         */
        private const val serialVersionUID = 8090400637875293423L
        @kotlin.jvm.JvmStatic
        fun fromDTO(dto: HashTagDTO?): HashTag? {
            return if (dto == null) {
                null
            } else HashTag(
                dto.itemId,
                dto.name,
                CommonImage.Companion.fromDTO(dto.backgroundImage),
                dto.summary,
                dto.postCount,
                dto.url,
                dto.type,
                dto.userCount
            )
        }
    }

}