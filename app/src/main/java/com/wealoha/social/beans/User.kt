package com.wealoha.social.beans

import android.os.Parcelable
import com.mooveit.library.Fakeit
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
data class User(
    var id: String,
    var type: String? = null,
    var name: String,
    // yyyy-MM-dd
    var birthday: String? = null,
    var age: String? = null,
    var height: String? = null,
    var weight: String? = null,
    /** 我当前所在的页面是否指向我自己  */
    var me: Boolean = false,
    /** 地区  */
    var regionCode: String? = null,
    var region: List<String>? = null,
    /** 星座  */
    var zodiac: String? = null,
    /** 摘要简介  */
    var summary: String? = null,
    /** 感兴趣的类型  */
    var selfPurposes: List<String>? = null,
    /** 我自己的类型  */
    var selfTag: String? = null,
    /** 头像  */
    var avatarImage: Image,
    var avatarImageId: String,
    var createTimeMillis: Long = 0,
    /** 资料不完整 强制跳  */
    var profileIncomplete: Boolean = false,
    /** 他喜欢多少人  */
    var alohaCount: Int = 0,
    /** 他被多少人喜欢  */
    var alohaGetCount: Int = 0,
    /** 是否喜欢过他  */
    var aloha: Boolean,
    /** 匹配  */
    var match: Boolean = false,
    var postCount: Int = 0,
    @Deprecated("")
    var t: String? = null,
    var phoneNum: String,
    var block: Boolean = false,
    var accessToken: String,
    var isUpdate: Boolean = false,
    /** 是否显示首次aloha time over后的提示  */
    var isShowAlohaTimeDialog: Boolean = false,
    /** 是否显示首次feed的引导  */
    var isShowFeedDialog: Boolean = false,
    var hasPrivacy: Boolean = false
) : Parcelable, Serializable {
    companion object {

        @kotlin.jvm.JvmField
        val TAG = User::class.java.simpleName

        fun fake(): User {
            val avatarImage = Image.fake();
            return User(
                id = System.currentTimeMillis().toString(),
                type = System.currentTimeMillis().toString(),
                name = System.currentTimeMillis().toString(),
                birthday = System.currentTimeMillis().toString(),
                age = System.currentTimeMillis().toString(),
                height = System.currentTimeMillis().toString(),
                weight = System.currentTimeMillis().toString(),
                me = (0..1).random() == 1,
                regionCode = System.currentTimeMillis().toString(),
                region = (0..4).map { Fakeit.app().name() },
                zodiac = System.currentTimeMillis().toString(),
                summary = System.currentTimeMillis().toString(),
                selfPurposes = (0..4).map { Fakeit.app().name() },
                selfTag = System.currentTimeMillis().toString(),
                avatarImage = avatarImage,
                avatarImageId = avatarImage.id,
                createTimeMillis = System.currentTimeMillis(),
                profileIncomplete = (0..1).random() == 1,
                alohaCount = (0..1000).random(),
                alohaGetCount = (0..1000).random(),
                aloha = (0..1).random() == 1,
                match = (0..1).random() == 1,
                postCount = (0..1000).random(),
                t = System.currentTimeMillis().toString(),
                phoneNum = System.currentTimeMillis().toString(),
                accessToken = System.currentTimeMillis().toString(),
                isUpdate = true,
                isShowAlohaTimeDialog = (0..1).random() == 1,
                isShowFeedDialog = (0..1).random() == 1,
                hasPrivacy = (0..1).random() == 1
            )
        }

        fun init(userDto: UserDTO): User {
            val user = fake()
            user.age = userDto.age.toString()
            user.aloha = userDto.aloha
            user.alohaCount = userDto.alohaCount
            user.alohaGetCount = userDto.alohaGetCount
            user.avatarImageId = userDto.avatarImageId
            user.birthday = userDto.birthday
            user.block = userDto.block
            user.hasPrivacy = userDto.hasPrivacy
            user.height = userDto.height.toString()
            user.id = userDto.id
            user.match = userDto.match
            user.me = userDto.me
            user.name = userDto.name
            user.postCount = userDto.postCount
            user.profileIncomplete = userDto.profileIncomplete
            user.regionCode = userDto.regionCode
            user.region = userDto.region
            user.selfPurposes = userDto.selfPurposes
            user.selfTag = userDto.selfTag
            user.summary = userDto.summary
            user.weight = userDto.weight.toString()
            user.zodiac = userDto.zodiac
            val image = Image.fake()
            image.id = userDto.avatarImageId
            image.height = userDto.avatarImage!!.height
            image.width = userDto.avatarImage!!.width
            image.urlPatternWidth = userDto.avatarImage!!.urlPatternWidth
            image.urlPatternWidthHeight = userDto.avatarImage!!.urlPatternWidthHeight
            image.type = userDto.avatarImage!!.type
            user.avatarImage = image
            return user
        }
    }
}