package com.wealoha.social.api

import android.text.TextUtils
import com.wealoha.social.api.BaseService.ServiceResultCallback
import com.wealoha.social.inject.Injector
import java.util.*

abstract class AbsBaseService<T> : BaseService<T> {
    protected var list: MutableList<T>
    protected var cursorId = FIRST_PAGE
    protected var loading = false

    /**
     * 重置数据
     *
     * @return void
     */
    fun refreshAllData() {
        cursorId = FIRST_PAGE
        list.clear()
    }

    fun getDataList(callback: ServiceResultCallback<T>, vararg args: Any?) {
        var tempCursor: String? = cursorId
        if (FIRST_PAGE == tempCursor) {
            tempCursor = null
            list.clear()
        } else if (TextUtils.isEmpty(tempCursor)) {
            // list.add(getTopicReloadItem());
            callback.nomore()
            return
        }
        getList(callback, tempCursor, *args)
    }

    abstract fun getList(callback: ServiceResultCallback<T>, cursorId: String?, vararg args: Any?)

    companion object {
        const val FIRST_PAGE = "FIRST_PAGE"
        const val COUNT = 21
    }

    init {
        Injector.inject(this)
        list = ArrayList()
    }
}