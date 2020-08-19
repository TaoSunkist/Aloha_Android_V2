package com.wealoha.social.beans.message

import com.wealoha.social.beans.Image

/**
 * Created by walker on 14-4-27.
 */
class ImageMessage : Message() {
    @kotlin.jvm.JvmField
    var image: Image? = null

    companion object {
        private const val serialVersionUID = 1025221798212317198L
        const val TYPE = "inboxMessageImage"
    }

    init {
        type = TYPE
    }
}