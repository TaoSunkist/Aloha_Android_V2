package com.wealoha.social.beans

import android.os.Parcel
import android.os.Parcelable
import com.wealoha.social.utils.Debug
import com.wealoha.social.utils.Dimens
import com.wealoha.social.utils.ImageUtil
import kotlinx.android.parcel.Parcelize

@Parcelize
class Image(
    var id: String,
    var type: String? = null,
    var width: Int = 0,
    var height: Int = 0,
    var url: String,
    var urlPatternWidth: String? = null,
    var urlPatternWidthHeight: String? = null,
    var path: String? = null,
    var mimeType: String? = null
) : Parcelable {

    companion object {
        fun fake(): Image {
            return Image(
                id = System.currentTimeMillis().toString(),
                type = System.currentTimeMillis().toString(),
                url = Debug.images.random(),
                width = Dimens.purchaseableItemWidth,
                height = Dimens.purchaseableItemHeight
            )
        }
    }
}