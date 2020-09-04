package com.wealoha.social.beans

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

/**
 * @author javamonk
 * @createTime 14-10-16 PM12:02
 */
@Parcelize
data class PromotionGetData(
    var promotionCode: String? = null,
    var quotaReset: Int = 0,
    var inviteNewUserCount: Int = 0,
    var alohaGetLocked: Boolean = false,
    var alohaGetUnlockInviteNeed: Int = 0,
    var alohaGetUnlockMoreInviteNeed: Int = 0,
    var alohaGetUnlockMax: Int = 0,
    var quotaPerPerson: Int = 0
) : ResultData(),  Parcelable {

    companion object {
        @kotlin.jvm.JvmField
        val TAG = PromotionGetData::class.java.simpleName
    }
}