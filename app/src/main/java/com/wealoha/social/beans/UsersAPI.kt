/*
 * Copyright (C) 2010-2013 The SINA WEIBO Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wealoha.social.beans

import android.content.Context
import android.util.SparseArray
import com.sina.weibo.sdk.auth.Oauth2AccessToken
import com.sina.weibo.sdk.net.RequestListener
import com.sina.weibo.sdk.net.WeiboParameters

/**
 * 该类封装了用户接口。 详情请参考[用户接口](http://t.cn/8F1n1eF)
 *
 * @author SINA
 * @since 2014-03-03
 */
class UsersAPI(context: Context?, appKey: String?, accessToken: Oauth2AccessToken?) :
    AbsOpenAPI(context, appKey, accessToken) {
    companion object {
        private const val READ_USER = 0
        private const val READ_USER_BY_DOMAIN = 1
        private const val READ_USER_COUNT = 2
        private val API_BASE_URL: String = AbsOpenAPI.API_SERVER + "/users"
        private val sAPIList = SparseArray<String>()

        init {
            sAPIList.put(
                READ_USER,
                "$API_BASE_URL/show.json"
            )
            sAPIList.put(
                READ_USER_BY_DOMAIN,
                "$API_BASE_URL/domain_show.json"
            )
            sAPIList.put(
                READ_USER_COUNT,
                "$API_BASE_URL/counts.json"
            )
        }
    }

    /**
     * 根据用户ID获取用户信息。
     *
     * @param uid
     * 需要查询的用户ID
     * @param listener
     * 异步请求回调接口
     */
    fun show(uid: Long, listener: RequestListener?) {
        // WeiboParameters params = new WeiboParameters(mAppKey);
        val params = WeiboParameters()
        params.put("uid", uid)
        requestAsync(
            sAPIList[READ_USER],
            params,
            HTTPMETHOD_GET,
            listener
        )
    }

    /**
     * 根据用户昵称获取用户信息。
     *
     * @param screen_name
     * 需要查询的用户昵称
     * @param listener
     * 异步请求回调接口
     */
    fun show(screen_name: String?, listener: RequestListener?) {
        // WeiboParameters params = new WeiboParameters(mAppKey);
        val params = WeiboParameters()
        params.put("screen_name", screen_name)
        requestAsync(
            sAPIList[READ_USER],
            params,
            HTTPMETHOD_GET,
            listener
        )
    }
}