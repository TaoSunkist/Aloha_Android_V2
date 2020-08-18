package com.wealoha.social.api.user.dto

import com.wealoha.social.api.common.dto.ImageDTO

/**
 *
 *
 * @author javamonk
 * @createTime 2015年2月25日 上午11:21:56
 */
class UserDTO {
    var id: String? = null
    var name: String? = null
    var birthday // yyyy-MM-dd
            : String? = null
    var age = 0
    var height // 1+
            = 0
    var weight // 1+
            = 0

    /** 我当前所在的页面是否指向我自己  */
    var me = false

    /** 地区  */
    var regionCode: String? = null
    var region: List<String>? = null

    /** 星座  */
    var zodiac: String? = null

    /** 摘要简介  */
    var summary: String? = null

    /** 感兴趣的类型  */
    var selfPurposes: List<String>? = null

    /** 我自己的类型  */
    var selfTag: String? = null
    @JvmField
    var avatarImageId: String? = null

    /** 资料不完整 强制跳  */
    var profileIncomplete = false

    /** 他喜欢多少人  */
    var alohaCount = 0

    /** 他被多少人喜欢  */
    var alohaGetCount = 0

    /** 是否喜欢过他  */
    var aloha = false

    /** 匹配  */
    var match = false
    var postCount = 0

    // 是否屏蔽
    var block = false
    var hasPrivacy = false
    @JvmField
    var avatarImage: ImageDTO? = null
}