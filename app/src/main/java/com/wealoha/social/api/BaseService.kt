package com.wealoha.social.api

interface BaseService<T> {
    interface ServiceCallback {
        fun success()
        fun failer()
    }

    interface ServiceResultCallback<T> {
        fun success(list: List<T>?)
        fun failer()
        fun nomore()
        fun beforeSuccess()
    }

    interface ServiceObjResultCallback<T> {
        fun success(obj: T)
        fun failer()
        fun nomore()
        fun beforeSuccess()
    }

    interface ServiceListResultCallback<T> {
        fun success(t: List<T>?)
        fun failer()
    }
}