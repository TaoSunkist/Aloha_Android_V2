package com.wealoha.social.beans

import android.os.Parcelable
import com.wealoha.social.beans.User.Companion.fromDTO
import kotlinx.android.parcel.Parcelize
import java.io.Serializable
import java.util.*

@Parcelize
data class UserTag(
    val tagAnchorX: Double?,
    val tagAnchorY: Double?,
    val tagCenterX: Double?,
    val tagCenterY: Double?,
    val user: User
) : Serializable, Parcelable {

    companion object {
        /**
         *
         */
        private const val serialVersionUID = 6072731508136652565L
        fun fromDTOList(
            userTagDTOList: List<UserTagsDTO>,
            userMap: Map<String, UserDTO>,
            imageMap: Map<String, ImageCommonDto>
        ): List<UserTag> {
            val userTagList: MutableList<UserTag> =
                ArrayList(userTagDTOList.size)
            var userDto: UserDTO?
            var commonImage: CommonImage
            for (userTagDto in userTagDTOList) {
                userDto = userMap[userTagDto!!.tagUserId]
                commonImage = CommonImage.fromDTO(imageMap[userDto!!.avatarImageId]!!)
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