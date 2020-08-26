package com.wealoha.social.api

import com.wealoha.social.beans.ApiErrorCode
import com.wealoha.social.beans.Direct

/**
 * 所有和api交互的list类型
 *
 * @author javamonk
 * @createTime 2015年3月4日 下午11:02:05
 */
interface BaseListApiService<E, P> {
    /**
     * Api列表数据加载异步回调
     *
     * @author javamonk
     * @createTime 2015年3月6日 上午11:28:42
     */
    interface ApiListCallback<E> {
        /**
         * 加载数据成功
         *
         * @param list
         * @param nextCursorId
         */
        fun success(list: List<E>, nextCursorId: String)

        /**
         * 加载数据出错
         *
         * @param code      api的错误码
         * @param exception 异常，可能为null
         */
        fun fail(code: ApiErrorCode?, exception: Exception?)
    }

    /***
     * @author dell-pc
     *
     * @param
     */
    interface ApiCallback<E> {
        /**
         * 加载数据成功
         */
        fun success(data: E)

        /**
         * 加载数据出错
         *
         * @param code      api的错误码
         * @param exception 异常，可能为null
         */
        fun fail(code: ApiErrorCode?, exception: Exception?)
    }

    interface ListContextCallback<E> {
        /**
         * 加载数据成功
         *
         * @param list
         * @param preCursorId
         * @param nextCursorId
         */
        fun success(list: List<E>, preCursorId: String, nextCursorId: String)

        /**
         * 加载数据出错
         *
         * @param code      api的错误码
         * @param exception 异常，可能为null
         */
        fun fail(code: ApiErrorCode?, exception: Exception?)
    }

    interface NoResultCallback {
        fun success()
        fun fail(code: ApiErrorCode?, exception: Exception?)
    }

    interface AdapterListDataCallback<E> {
        /***
         * service 通过这个callback 获取adapter中的list数据
         *
         * @return
         * @return List<E>
        </E> */
        val listData: List<E>
    }

    /**
     * 是否需要把api取到的数据反转(不用自己反转 [BaseListApiAdapter] 会反转)
     *
     * @return
     */
    fun needReverse(): Boolean

    /**
     * 是否需要把数据放到开头而不是结尾
     *
     * @return
     */
    fun appendToHeader(): Boolean

    /**
     * 是否支持向前取数据
     *
     * @return
     */
    fun supportPrev(): Boolean

    /**
     * 是否支持根据数据定位
     *
     * @return
     */
    fun supportContextByCursor(): Boolean

    /**
     * 获取列表数据
     *
     * @param cursor
     * @param count
     * @param direct   加载数据的方向
     * @param param    参数，每个service自行定义
     * @param callback Notify2Service#getList(String, int, Void, ApiListCallback) 的错误处理
     */
    fun getList(
        cursor: String,
        count: Int,
        direct: Direct,
        param: P,
        callback: ApiListCallback<E?>
    )

    /**
     * 获取列表数据，以cursor为参照，上下各取一部分
     *
     * @param cursor
     * @param count
     * @param param
     * @param callback
     */
    fun getListWithContext(cursor: String, count: Int, param: P, callback: ListContextCallback<E>)

    /***
     * 设置能够获取adapter 中的listdata 的callback
     *
     * @param callback
     * @return void
     */
    fun setAdapterListCallback(callback: AdapterListDataCallback<E>)

    /***
     *
     * 加载list 中的图片到缓存中
     *
     * @return void
     */
    fun fetchPhoto(
        firstVisibleItem: Int,
        totalItemCount: Int,
        direction: Boolean,
        mScreenWidth: Int
    )
}