package com.wealoha.social.beans.user

import com.wealoha.social.beans.ResultData
import java.io.Serializable

/**
 * @author javamonk
 * @createTime 14-10-16 PM12:02
 */
class PromotionGetData : ResultData(), Serializable {
    @kotlin.jvm.JvmField
    var promotionCode: String? = null
    @kotlin.jvm.JvmField
    var quotaReset = 0
    var inviteNewUserCount = 0
    @kotlin.jvm.JvmField
    var alohaGetLocked = false
    var alohaGetUnlockInviteNeed = 0
    var alohaGetUnlockMoreInviteNeed = 0
    var alohaGetUnlockMax = 0
    @kotlin.jvm.JvmField
    var quotaPerPerson = 0

    companion object {
        private const val serialVersionUID = -6138532041290176835L
        @kotlin.jvm.JvmField
        val TAG = PromotionGetData::class.java.simpleName
    }
}