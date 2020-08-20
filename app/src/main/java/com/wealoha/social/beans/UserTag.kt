package com.wealoha.social.beans

import com.wealoha.social.beans.User2.Companion.fromDTO
import java.io.Serializable
import java.util.*

class UserTag(
    val tagAnchorX: Double?,
    val tagAnchorY: Double?,
    val tagCenterX: Double?,
    val tagCenterY: Double?,
    val user2: User2?
) : Serializable {

    companion object {
        /**
         *
         */
        private const val serialVersionUID = 6072731508136652565L
        fun fromDTOList(
            userTagDTOList: List<UserTagsDTO?>?,
            userMap: Map<String?, UserDTO?>?,
            imageMap: Map<String?, ImageCommonDto?>?
        ): List<UserTag>? {
            if (userTagDTOList == null || userMap == null || imageMap == null) {
                return null
            }
            val userTagList: MutableList<UserTag> =
                ArrayList(userTagDTOList.size)
            var userDto: UserDTO?
            var commonImage: CommonImage
            for (userTagDto in userTagDTOList) {
                userDto = userMap[userTagDto!!.tagUserId]
                commonImage = CommonImage.Companion.fromDTO(imageMap[userDto!!.avatarImageId])!!
                userTagList.add(
                    UserTag(
                        userTagDto.tagAnchorX,
                        userTagDto.tagAnchorY,  //
                        userTagDto.tagCenterX,
                        userTagDto.tagCenterY,
                        fromDTO(userDto, commonImage)
                    )
                )
            }
            return userTagList
        }
    }

}