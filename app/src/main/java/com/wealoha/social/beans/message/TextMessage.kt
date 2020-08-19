package com.wealoha.social.beans.message

/**
 * Created by walker on 14-4-27.
 */
class TextMessage : Message() {
    @kotlin.jvm.JvmField
    var text: String? = null
    override fun toString(): String {
        return text!!
    }

    companion object {
        private const val serialVersionUID = -567229340829512956L
        const val TYPE = "inboxMessageText"
    }

    init {
        type = TYPE
    }
}