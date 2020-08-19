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
class UsersAPI(
    context: Context?,
    appKey: String?,
    accessToken: Oauth2AccessToken?
) : AbsOpenAPI(context, appKey, accessToken) {
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

    /**
     * 通过个性化域名获取用户资料以及用户最新的一条微博。
     *
     * @param domain
     * 需要查询的个性化域名（请注意：是http://weibo.com/xxx后面的xxx部分）
     * @param listener
     * 异步请求回调接口
     */
    fun domainShow(domain: String?, listener: RequestListener?) {
        // WeiboParameters params = new WeiboParameters(mAppKey);
        val params = WeiboParameters()
        params.put("domain", domain)
        requestAsync(
            sAPIList[READ_USER_BY_DOMAIN],
            params,
            HTTPMETHOD_GET,
            listener
        )
    }

    /**
     * 批量获取用户的粉丝数、关注数、微博数。
     *
     * @param uids
     * 需要获取数据的用户UID，多个之间用逗号分隔，最多不超过100个
     * @param listener
     * 异步请求回调接口
     */
    fun counts(uids: LongArray, listener: RequestListener?) {
        val params = buildCountsParams(uids)
        requestAsync(
            sAPIList[READ_USER_COUNT],
            params,
            AbsOpenAPI.Companion.HTTPMETHOD_GET,
            listener
        )
    }
    /**
     * -----------------------------------------------------------------------
     * 请注意：以下方法匀均同步方法。如果开发者有自己的异步请求机制，请使用该函数。
     * -----------------------------------------------------------------------
     */
    /**
     * @see .show
     */
    fun showSync(uid: Long): String? {
        // WeiboParameters params = new WeiboParameters(mAppKey);
        val params = WeiboParameters()
        params.put("uid", uid)
        return requestSync(
            sAPIList[READ_USER],
            params,
            AbsOpenAPI.Companion.HTTPMETHOD_GET
        )
    }

    /**
     * @see .show
     */
    fun showSync(screen_name: String?): String? {
        // WeiboParameters params = new WeiboParameters(mAppKey);
        val params = WeiboParameters()
        params.put("screen_name", screen_name)
        return requestSync(
            sAPIList[READ_USER],
            params,
            AbsOpenAPI.Companion.HTTPMETHOD_GET
        )
    }

    /**
     * @see .domainShow
     */
    fun domainShowSync(domain: String?): String? {
        // WeiboParameters params = new WeiboParameters(mAppKey);
        val params = WeiboParameters()
        params.put("domain", domain)
        return requestSync(
            sAPIList[READ_USER_BY_DOMAIN],
            params,
            AbsOpenAPI.Companion.HTTPMETHOD_GET
        )
    }

    /**
     * @see .counts
     */
    fun countsSync(uids: LongArray): String? {
        val params = buildCountsParams(uids)
        return requestSync(
            sAPIList[READ_USER_COUNT],
            params,
            AbsOpenAPI.Companion.HTTPMETHOD_GET
        )
    }

    private fun buildCountsParams(uids: LongArray): WeiboParameters {
        // WeiboParameters params = new WeiboParameters(mAppKey);
        val params = WeiboParameters()
        val strb = StringBuilder()
        for (cid in uids) {
            strb.append(cid).append(",")
        }
        strb.deleteCharAt(strb.length - 1)
        params.put("uids", strb.toString())
        return params
    }
}