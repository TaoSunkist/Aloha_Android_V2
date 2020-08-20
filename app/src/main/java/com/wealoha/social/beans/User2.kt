package com.wealoha.social.beans

import android.os.Parcelable
import android.text.TextUtils
import com.wealoha.social.api.common.bean.Image
import kotlinx.android.parcel.Parcelize
import org.apache.commons.lang3.builder.ToStringBuilder
import org.apache.commons.lang3.builder.ToStringStyle
import java.io.Serializable

/**
 * @author javamonk
 * @createTime 2015年2月25日 上午10:57:45
 */
@Parcelize
class User2(
    val id: String?,
    val name: String?,
    val birthday: String?,
    val age: Int,
    val height: Int,
    val weight: Int,
    val isMe: Boolean,  //
    val regionCode: String?,
    val region: List<String?>?,
    val zodiac: String?,
    val summary: String?,
    val selfPurposes: List<String?>?,
    val selfTag: String?,  //
    val avatarImage: Image?,
    val isProfileIncomplete: Boolean,
    val alohaCount: Int,
    var alohaGetCount: Int,
    var isAloha: Boolean,  //
    val isMatch: Boolean,
    val postCount: Int,
    var isBlock: Boolean,
    private val hasPrivacy: Boolean
) : Serializable, Parcelable {

    fun hasPrivacy(): Boolean {
        return hasPrivacy
    }

    override fun toString(): String {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE)
    }

    /***
     * 加入黑名单
     *
     * @return void
     */
    fun block() {
        isBlock = true
    }

    /***
     * 移出黑名单
     *
     * @return void
     */
    fun removeBlock() {
        isBlock = false
    }

    /***
     * 被aloha,被aloha 数加1
     *
     * @return void
     */
    fun alohaed() {
        if (!isAloha) {
            isAloha = true
            alohaGetCount++
        }
    }

    fun dislike() {
        if (isAloha) {
            isAloha = false
            alohaGetCount--
        }
    }

    companion object {
        @JvmField
        val TAG = User2::class.java.name

        /**
         *
         */
        private const val serialVersionUID = -4646233984981667550L

        @JvmStatic
        fun fromDTO(
            dto: UserDTO?,
            avatarImage: Image?
        ): User2? {
            var user2: User2? = null
            if (dto != null) {
                user2 = User2(
                    dto.id, dto.name, dto.birthday, dto.age, dto.height,  //
                    dto.weight, dto.me, dto.regionCode, dto.region, dto.zodiac,  //
                    dto.summary, dto.selfPurposes, dto.selfTag, avatarImage,  //
                    dto.profileIncomplete, dto.alohaCount, dto.alohaGetCount,  //
                    dto.aloha, dto.match, dto.postCount, dto.block, dto.hasPrivacy
                )
            }
            return user2
        }

        @JvmStatic
        fun fromDTO(dto: UserDTO): User2 {
            //com.wealoha.social.beans.User()
//        user.age = userDto.age.toString()
//        user.aloha = userDto.aloha
//        user.alohaCount = userDto.alohaCount
//        user.alohaGetCount = userDto.alohaGetCount
//        user.avatarImageId = userDto.avatarImageId
//        user.birthday = userDto.birthday
//        user.block = userDto.block
//        user.hasPrivacy = userDto.hasPrivacy
//        user.height = userDto.height.toString()
//        user.id = userDto.id
//        user.match = userDto.match
//        user.me = userDto.me
//        User.getName() = userDto.name
//        user.postCount = userDto.postCount
//        user.profileIncomplete = userDto.profileIncomplete
//        user.regionCode = userDto.regionCode
//        user.region = userDto.region
//        user.selfPurposes = userDto.selfPurposes
//        user.selfTag = userDto.selfTag
//        user.summary = userDto.summary
//        user.weight = userDto.weight.toString()
//        user.zodiac = userDto.zodiac
//        val image = com.wealoha.social.beans.Image()
//        image.id = userDto.avatarImageId
//        image.height = userDto.avatarImage!!.height
//        image.width = userDto.avatarImage!!.width
//        image.urlPatternWidth = userDto.avatarImage!!.urlPatternWidth
//        image.urlPatternWidthHeight = userDto.avatarImage!!.urlPatternWidthHeight
//        image.type = userDto.avatarImage!!.type
//        user.avatarImage = image
            return User2(
                dto.id,
                dto.name,
                dto.birthday,
                dto.age,
                dto.height,  //
                dto.weight,
                dto.me,
                dto.regionCode,
                dto.region,
                dto.zodiac,  //
                dto.summary,
                dto.selfPurposes,
                dto.selfTag,
                Image.fromDTO(dto.avatarImage),  //
                dto.profileIncomplete,
                dto.alohaCount,
                dto.alohaGetCount,  //
                dto.aloha,
                dto.match,
                dto.postCount,
                dto.block,
                dto.hasPrivacy
            )
        }

        @JvmStatic
        fun formOldUser(oldUser: User): User2 {
            return User2(
                oldUser.id,
                oldUser.name,
                oldUser.birthday,
                parseInt(oldUser.age),
                parseInt(oldUser.height),  //
                parseInt(oldUser.weight),
                oldUser.me,
                oldUser.regionCode,
                oldUser.region,
                oldUser.zodiac,  //
                oldUser.summary,
                oldUser.selfPurposes,
                oldUser.selfTag,
                Image.fromOld(oldUser.avatarImage),  //
                oldUser.profileIncomplete,
                oldUser.alohaCount,
                oldUser.alohaGetCount,  //
                oldUser.aloha,
                oldUser.match,
                oldUser.postCount,
                oldUser.block,
                oldUser.hasPrivacy
            )
        }

        fun parseInt(str: String?): Int {
            var temp = 0
            if (!TextUtils.isEmpty(str)) {
                temp = str?.toInt() ?: 0
            }
            return temp
        }
    }

}