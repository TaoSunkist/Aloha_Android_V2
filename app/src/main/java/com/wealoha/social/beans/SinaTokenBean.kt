package com.wealoha.social.beans

import com.google.gson.annotations.SerializedName

class SinaTokenBean : ResultData() {
    @kotlin.jvm.JvmField
    @SerializedName("access_token")
    var accessToken: String? = null
    @kotlin.jvm.JvmField
    var uid: String? = null
    @kotlin.jvm.JvmField
    var name: String? = null

    @kotlin.jvm.JvmField
    @SerializedName("expires_in")
    var expiresIn: String? = null
}