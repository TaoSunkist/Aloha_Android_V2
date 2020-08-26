package com.wealoha.social.api

import com.squareup.otto.Bus
import com.wealoha.social.beans.ApiErrorCode
import com.wealoha.social.api.BaseListApiService.ApiCallback
import com.wealoha.social.api.BaseListApiService.NoResultCallback
import com.wealoha.social.beans.CommonImage
import com.wealoha.social.beans.UserDTO
import com.wealoha.social.beans.*
import com.wealoha.social.callback.CallbackImpl
import com.wealoha.social.event.RefreshFilterBtnEvent
import com.wealoha.social.inject.Injector
import com.wealoha.social.utils.ContextUtil
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.client.Response
import java.util.*
import javax.inject.Inject

class UserService {

    @JvmField
    @Inject
    var user2Api: ServerApi? = null

    @JvmField
    @Inject
    var contextUtil: ContextUtil? = null

    @JvmField
    @Inject
    var bus: Bus? = null

    /***
     * 解除aloha
     *
     * @param userid
     * 用户id
     * @param callback
     * @return void
     */
    fun dislike(userid: String, callback: NoResultCallback) {
        user2Api!!.dislikeUser(userid, object : Callback<ApiResponse<ResultData>> {
            override fun success(
                apiResponse: ApiResponse<ResultData>,
                arg1: Response
            ) {
                if (!apiResponse.isOk) {
                    callback.fail(ApiErrorCode.fromResult(apiResponse), null)
                } else {
                    callback.success()
                }
            }

            override fun failure(error: RetrofitError) {
                callback.fail(null, error)
            }
        })
    }

    fun aloha(
        userid: String,
        refer: String,
        callback: NoResultCallback
    ) {
        user2Api!!.aloha(
            userid,
            refer,
            object : Callback<ApiResponse<ResultData>> {
                override fun success(
                    apiResponse: ApiResponse<ResultData>,
                    arg1: Response
                ) {
                    if (apiResponse == null || !apiResponse.isOk) {
                        callback.fail(ApiErrorCode.fromResult(apiResponse), null)
                    } else {
                        callback.success()
                    }
                }

                override fun failure(error: RetrofitError) {
                    callback.fail(null, error)
                }
            })
    }

    /***
     * 获取用户的profile， callback 返回的值就是一个user,如果刷新的用户是当前用户， 那么跟新本地的用户信息 [.saveCurrentUser]
     *
     * @param userid
     * 用户id
     * @param callback
     * @return void
     */
    fun userProfile(
        userid: String,
        callback: ApiCallback<User>
    ) {
        user2Api!!.userProfile(userid, object : Callback<ApiResponse<Profile2Data>?> {
            override fun success(
                apiResponse: ApiResponse<Profile2Data>?,
                arg1: Response
            ) {
                if (apiResponse == null || !apiResponse.isOk) {
                    callback.fail(ApiErrorCode.fromResult(apiResponse), null)
                } else {
                    saveCurrentUser(apiResponse.data?.user!!)
                    callback.success(transPro2DataToUser(apiResponse.data!!))
                }
            }

            override fun failure(error: RetrofitError) {
                callback.fail(null, error)
            }
        })
    }

    private fun transPro2DataToUser(proData: Profile2Data): User {
        val userDto = proData.user
        // userDto.aloha = proData.liked;
        // userDto.match = proData.friend;
        val img =
            CommonImage.fromDTO(proData.imageMap[userDto.avatarImageId]!!)
        return User.fromDTO(userDto, img)
    }

    /***
     * 更新user后， 将当前用户的信息刷新并保存
     *
     * @param userDto
     * @return void
     */
    private fun saveCurrentUser(userDto: UserDTO) {
        if (!userDto!!.me || userDto == null) {
            return
        }
        val user = com.wealoha.social.beans.User.init(userDto)
        contextUtil!!.currentUser = user
    }

    /**
     * 获取高级功能设置,并保存到本地
     *
     */
    val profeatureSetting: Unit
        get() {
            user2Api!!.userMatchSetting(object : Callback<ApiResponse<MatchSettingData>> {
                override fun success(
                    apiResponse: ApiResponse<MatchSettingData>?,
                    response: Response
                ) {
                    if (apiResponse != null && apiResponse.isOk) {
                        contextUtil!!.profeatureEnable = apiResponse.data?.filterEnable == true
                        val regions =
                            apiResponse.data?.selectedRegion as ArrayList<String?>
                        if (regions != null && regions.size > 0) {
                            contextUtil!!.filterRegion = regions[regions.size - 1]
                        } else {
                            contextUtil!!.filterRegion = null
                        }
                        bus!!.post(RefreshFilterBtnEvent())
                    }
                }

                override fun failure(arg0: RetrofitError) {}
            })
        }

    /**
     * @author: sunkist
     * @date:2015年7月30日
     */
    fun verifyPassword(
        pw: String,
        callbackImpl: CallbackImpl
    ) {
        user2Api!!.verifyPassword(
            pw,
            object : Callback<ApiResponse<ResultData>?> {
                override fun failure(retrofitError: RetrofitError) {
                    // ToastUtil.shortToast(AppApplication.getInstance(), R.string.Unkown_Error);
                    callbackImpl.failure()
                }

                override fun success(
                    apiResponse: ApiResponse<ResultData>?,
                    response: Response
                ) {
                    if (apiResponse != null && apiResponse.isOk) {
                        if (apiResponse.isOk) {
                            callbackImpl.success()
                        } else if (apiResponse.data?.error == IResultDataErrorCode.ERROR_INVALID_PASSWORD) {
                            callbackImpl.failure()
                            // ToastUtil.shortToast(AppApplication.getInstance(),
                            // R.string.login_password_is_invalid_title);
                        }
                    } else {
                        callbackImpl.failure()
                        // ToastUtil.shortToast(AppApplication.getInstance(), R.string.Unkown_Error);
                    }
                }
            })
    }

    init {
        Injector.inject(this)
    }
}