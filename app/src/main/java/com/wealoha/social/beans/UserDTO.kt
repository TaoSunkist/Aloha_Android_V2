package com.wealoha.social.beans

import com.mooveit.library.Fakeit

/**
 *
 *
 * @author javamonk
 * @createTime 20,15年2月25日 上午11:21:56
 */
data class UserDTO(
    var id: String,
    var name: String,
    // yyyy-MM-dd
    var birthday: String,
    var age: Int = 0,
    var height: Int = 0,
    var weight: Int = 0,

    /** 我当前所在的页面是否指向我自己  */
    var me: Boolean = false,

    /** 地区  */
    var regionCode: String,
    var region: List<String>,

    /** 星座  */
    var zodiac: String? = null,

    /** 摘要简介  */
    var summary: String,

    /** 感兴趣的类型  */
    var selfPurposes: List<String>? = null,

    /** 我自己的类型  */
    var selfTag: String? = null,

    @JvmField
    var avatarImageId: String,

    /** 资料不完整 强制跳  */
    var profileIncomplete: Boolean = false,

    /** 他喜欢多少人  */
    var alohaCount: Int = 0,
    /** 他被多少人喜欢  */
    var alohaGetCount: Int = 0,

    /** 是否喜欢过他  */
    var aloha: Boolean = false,

    /** 匹配  */
    var match: Boolean = false,
    var postCount: Int = 0,

// 是否屏蔽
    var block: Boolean = false,
    var hasPrivacy: Boolean = false,

    var avatarImage: ImageCommonDto
) {
    companion object {
        fun fake(): UserDTO {
            val avatarImage = ImageCommonDto.fake();

            return UserDTO(
                id = System.currentTimeMillis().toString(),
                name = System.currentTimeMillis().toString(),
                birthday = System.currentTimeMillis().toString(),
                age = System.currentTimeMillis().toInt(),
                height = System.currentTimeMillis().toInt(),
                weight = System.currentTimeMillis().toInt(),
                me = (0..1).random() == 1,
                regionCode = System.currentTimeMillis().toString(),
                region = (0..4).map { Fakeit.app().name() },
                zodiac = System.currentTimeMillis().toString(),
                summary = System.currentTimeMillis().toString(),
                selfPurposes = (0..4).map { Fakeit.app().name() },
                selfTag = System.currentTimeMillis().toString(),
                avatarImage = avatarImage,
                avatarImageId = avatarImage.imageId,
                profileIncomplete = (0..1).random() == 1,
                alohaCount = (0..1000).random(),
                alohaGetCount = (0..1000).random(),
                aloha = (0..1).random() == 1,
                match = (0..1).random() == 1,
                postCount = (0..1000).random(),
                hasPrivacy = (0..1).random() == 1
            )
        }

        fun fake(userID: String): UserDTO {
            val avatarImage = ImageCommonDto.fake();

            return UserDTO(
                id = userID,
                name = System.currentTimeMillis().toString(),
                birthday = System.currentTimeMillis().toString(),
                age = System.currentTimeMillis().toInt(),
                height = System.currentTimeMillis().toInt(),
                weight = System.currentTimeMillis().toInt(),
                me = (0..1).random() == 1,
                regionCode = System.currentTimeMillis().toString(),
                region = (0..4).map { Fakeit.app().name() },
                zodiac = System.currentTimeMillis().toString(),
                summary = System.currentTimeMillis().toString(),
                selfPurposes = (0..4).map { Fakeit.app().name() },
                selfTag = System.currentTimeMillis().toString(),
                avatarImage = avatarImage,
                avatarImageId = avatarImage.imageId,
                profileIncomplete = (0..1).random() == 1,
                alohaCount = (0..1000).random(),
                alohaGetCount = (0..1000).random(),
                aloha = (0..1).random() == 1,
                match = (0..1).random() == 1,
                postCount = (0..1000).random(),
                hasPrivacy = (0..1).random() == 1
            )
        }
    }
}