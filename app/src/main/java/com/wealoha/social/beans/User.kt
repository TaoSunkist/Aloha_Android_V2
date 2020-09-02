package com.wealoha.social.beans

import android.os.Parcelable
import com.mooveit.library.Fakeit
import com.wealoha.social.utils.ImageUtil
import kotlinx.android.parcel.Parcelize
import org.apache.commons.lang3.StringUtils
import java.io.Serializable

@Parcelize
data class User(
    var id: String,
    var type: String? = null,
    var name: String,
    // yyyy-MM-dd
    var birthday: String,
    var age: Int,
    var height: Int,
    var weight: Int,
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

        fun fake(me: Boolean = false, isAuthentication: Boolean = true): User {
            val avatarImage = Image.fake();
            return User(
                id = System.currentTimeMillis().toString(),
                type = System.currentTimeMillis().toString(),
                name = Fakeit.app().author(),
                birthday = Fakeit.dateTime().dateFormatter(),
                age = System.currentTimeMillis().toInt(),
                height = System.currentTimeMillis().toInt(),
                weight = System.currentTimeMillis().toInt(),
                me = me,
                regionCode = System.currentTimeMillis().toString(),
                region = (0..4).map { Fakeit.app().name() },
                zodiac = System.currentTimeMillis().toString(),
                summary = System.currentTimeMillis().toString(),
                selfPurposes = (0..4).map { Fakeit.app().name() },
                selfTag = System.currentTimeMillis().toString(),
                avatarImage = avatarImage,
                avatarImageId = avatarImage.id,
                createTimeMillis = System.currentTimeMillis(),
                profileIncomplete = isAuthentication,
                alohaCount = (0..1000).random(),
                alohaGetCount = (0..1000).random(),
                aloha = (0..1).random() == 1,
                match = (0..1).random() == 1,
                postCount = (0..1000).random(),
                phoneNum = System.currentTimeMillis().toString(),
                accessToken = System.currentTimeMillis().toString(),
                isUpdate = true,
                isShowAlohaTimeDialog = (0..1).random() == 1,
                isShowFeedDialog = (0..1).random() == 1,
                hasPrivacy = (0..1).random() == 1
            )
        }

        fun init(userDto: UserDTO): User {
            val user = fake(true, false)
            user.age = userDto.age
            user.aloha = userDto.aloha
            user.alohaCount = userDto.alohaCount
            user.alohaGetCount = userDto.alohaGetCount
            user.avatarImageId = userDto.avatarImageId
            user.birthday = userDto.birthday
            user.block = userDto.block
            user.hasPrivacy = userDto.hasPrivacy
            user.height = userDto.height
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
            user.weight = userDto.weight
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

        fun fromDTO(
            dto: UserDTO,
            avatarCommonImage: CommonImage
        ): User {
            return User(
                id = dto.id,
                name = dto.name,
                birthday = dto.birthday,
                age = dto.age,
                height = dto.height,  //
                weight = dto.weight,
                me = dto.me,
                regionCode = dto.regionCode,
                region = dto.region,
                zodiac = dto.zodiac,  //
                summary = dto.summary,
                selfPurposes = dto.selfPurposes,
                selfTag = dto.selfTag,
                avatarImage = Image.init(avatarCommonImage),  //
                profileIncomplete = dto.profileIncomplete,
                alohaCount = dto.alohaCount,
                alohaGetCount = dto.alohaGetCount,  //
                aloha = dto.aloha,
                match = dto.match,
                postCount = dto.postCount,
                block = dto.block,
                hasPrivacy = dto.hasPrivacy,
                accessToken = "",
                avatarImageId = dto.avatarImage?.imageId ?: "",
                phoneNum = ""
            )
        }

        fun fromDTO(dto: UserDTO): User {
            return User(
                id = dto.id,
                name = dto.name,
                birthday = dto.birthday,
                age = dto.age,
                height = dto.height,  //
                weight = dto.weight, me = dto.me,
                regionCode = dto.regionCode,
                region = dto.region,
                zodiac = dto.zodiac,  //
                summary = dto.summary,
                selfPurposes = dto.selfPurposes,
                selfTag = dto.selfTag,
                avatarImage = Image.init(dto.avatarImage!!),  //
                profileIncomplete = dto.profileIncomplete,
                alohaCount = dto.alohaCount,
                alohaGetCount = dto.alohaGetCount,  //
                aloha = dto.aloha,
                match = dto.match,
                postCount = dto.postCount,
                block = dto.block,
                hasPrivacy = dto.hasPrivacy,
                accessToken = "",
                avatarImageId = dto.avatarImage?.imageId ?: "",
                phoneNum = ""
            )
        }

    }

    fun hasPrivacy(): Boolean {
        return hasPrivacy
    }

    /***
     * 加入黑名单
     *
     * @return void
     */
    fun block() {
        block = true
    }

    /***
     * 移出黑名单
     *
     * @return void
     */
    fun removeBlock() {
        block = false
    }

    /**
     * 获取方图地址
     *
     * @param width
     * @return
     */
    fun getUrlSquare(width: Int): String {
        return if (StringUtils.isNotBlank(avatarImage.urlPatternWidth)) {
            replacePattern(avatarImage.urlPatternWidth, width.toString() + "")
        } else ImageUtil.getImageUrl(id, width, ImageUtil.CropMode.ScaleCenterCrop)
    }

    private fun replacePattern(target: String?, vararg args: Any): String {
        var target = target
        target = StringUtils.replace(target, "%@", "%s")
        return String.format(target, *args)
    }
}