package com.wealoha.social.api

import com.wealoha.social.api.BaseListApiService.ApiListCallback
import com.wealoha.social.beans.ApiErrorCode.Companion.fromResult
import com.wealoha.social.beans.Direct
import com.wealoha.social.beans.MergeUsersGetData
import com.wealoha.social.beans.ApiResponse
import com.wealoha.social.beans.User
import com.wealoha.social.inject.Injector
import com.wealoha.social.utils.XL
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.client.Response
import javax.inject.Inject

/**
 *
 *
 * @author superman
 * @createTime 2015-03-12 11:20:31
 */
class UserListListApiService : AbsBaseListApiService<User, String>() {
    @JvmField
	@Inject
    var notify2api: ServerApi? = null

    override fun getList(
        cursor: String,
        count: Int,
        direct: Direct,
        notifyId: String,
        callback: ApiListCallback<User?>
    ) {
        notify2api!!.getMergeUsers(
            notifyId!!,
            count,
            cursor!!,
            object : Callback<ApiResponse<MergeUsersGetData>> {
                override fun failure(error: RetrofitError) {
                    callback.fail(null, error)
                    XL.i("USER_LIST_FRAG", "failure:" + error.message)
                }

                override fun success(apiResponse: ApiResponse<MergeUsersGetData>?, arg1: Response) {
                    XL.i("USER_LIST_FRAG", "success")
                    if (apiResponse != null && apiResponse.isOk) {
                        callback.success(
                            getUsers(apiResponse.data!!.list!!, apiResponse.data!!.imageMap),
                            apiResponse.data!!.nextCursorId
                        )
                    } else {
                        callback.fail(fromResult(apiResponse), null)
                    }
                }
            })
    }

    init {
        Injector.inject(this)
    }
}