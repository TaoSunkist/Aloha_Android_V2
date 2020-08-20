package com.wealoha.social.beans

data class AuthData(
    var t: String,
    var user: User
) : ResultData() {
    companion object {
        fun fake(): AuthData {
            val user = User.fake()
            return AuthData(t = System.currentTimeMillis().toString(), user = user)
        }
    }
}