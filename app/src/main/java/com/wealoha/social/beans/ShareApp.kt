package com.wealoha.social.beans

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View

class ShareApp(
    context: Context,
    val iconId: Int,
    titleId: Int,
    clickListener: View.OnClickListener
) {
    val id: Int
    val title: String
    val icon: Drawable
    val clickListener: View.OnClickListener

    init {
        id = iconId
        icon = context.resources.getDrawable(iconId)
        title = context.getString(titleId)
        this.clickListener = clickListener
    }
}