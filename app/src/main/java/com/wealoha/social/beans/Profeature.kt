package com.wealoha.social.beans

import android.content.Context

class Profeature {
    var title: String?
        private set
    var explain: String?
        private set
    var state: String?
        private set
    var headerResId: Int
        private set

    constructor(
        title: String?,
        itemTitle: String?,
        itemContent: String?,
        headerResId: Int
    ) : super() {
        this.title = title
        explain = itemTitle
        state = itemContent
        this.headerResId = headerResId
    }

    constructor(
        context: Context,
        itemTitleId: Int,
        itemContentId: Int,
        stateid: Int,
        headerResId: Int
    ) {
        title = getString(context, itemTitleId)
        explain = getString(context, itemContentId)
        state = getString(context, stateid)
        this.headerResId = headerResId
    }

    private fun getString(context: Context, stringId: Int): String? {
        return if (stringId == 0) {
            null
        } else {
            context.resources.getString(stringId)
        }
    }

}