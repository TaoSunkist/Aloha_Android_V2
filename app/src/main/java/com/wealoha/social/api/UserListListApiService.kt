package com.wealoha.social.api

import com.wealoha.social.api.BaseListApiService.ApiListCallback
import com.wealoha.social.beans.ApiErrorCode.Companion.fromResult
import com.wealoha.social.beans.Direct
import com.wealoha.social.beans.MergeUsersGetData
import com.wealoha.social.beans.Result
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
            object : Callback<Result<MergeUsersGetData>> {
                override fun failure(error: RetrofitError) {
                    callback.fail(null, error)
                    XL.i("USER_LIST_FRAG", "failure:" + error.message)
                }

                override fun success(result: Result<MergeUsersGetData>?, arg1: Response) {
                    XL.i("USER_LIST_FRAG", "success")
                    if (result != null && result.isOk) {
                        callback.success(
                            getUsers(result.data!!.list!!, result.data!!.imageMap),
                            result.data!!.nextCursorId
                        )
                    } else {
                        callback.fail(fromResult(result), null)
                    }
                }
            })
    }

    init {
        Injector.inject(this)
    }
}