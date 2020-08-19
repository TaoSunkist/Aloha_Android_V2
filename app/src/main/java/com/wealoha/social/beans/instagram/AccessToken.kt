package com.wealoha.social.beans.instagram

import com.wealoha.social.beans.ResultData

class AccessToken : ResultData() {
    @kotlin.jvm.JvmField
    var access_token: String? = null

    @kotlin.jvm.JvmField
    var user: User? = null
}