package com.wealoha.social.beans

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Image : Parcelable {
    @kotlin.jvm.JvmField
    var id: String? = null
    @kotlin.jvm.JvmField
    var type: String? = null
    @kotlin.jvm.JvmField
    var width = 0
    @kotlin.jvm.JvmField
    var height = 0
    @kotlin.jvm.JvmField
    var url: String? = null
    @kotlin.jvm.JvmField
    var urlPatternWidth: String? = null
    @kotlin.jvm.JvmField
    var urlPatternWidthHeight: String? = null
    var path: String? = null
    var mimeType: String? = null

    companion object {
    }
}