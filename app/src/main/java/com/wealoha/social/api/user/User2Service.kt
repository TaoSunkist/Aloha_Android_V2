package com.wealoha.social.api.user

import com.squareup.otto.Bus
import com.wealoha.social.api.common.ApiErrorCode
import com.wealoha.social.api.common.BaseListApiService.ApiCallback
import com.wealoha.social.api.common.BaseListApiService.NoResultCallback
import com.wealoha.social.api.common.bean.Image
import com.wealoha.social.api.user.bean.User2
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

class User2Service {
    @JvmField
    @Inject
    var user2Api: User2API? = null

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
    fun dislike(userid: String?, callback: NoResultCallback) {
        user2Api!!.dislikeUser(
            userid,
            object : Callback<Result<ResultData?>?> {
                override fun success(
                    result: Result<ResultData?>?,
                    arg1: Response
                ) {
                    if (result == null || !result.isOk) {
                        callback.fail(ApiErrorCode.fromResult(result), null)
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
        userid: String?,
        refer: String?,
        callback: NoResultCallback
    ) {
        user2Api!!.aloha(
            userid,
            refer,
            object : Callback<Result<ResultData?>?> {
                override fun success(
                    result: Result<ResultData?>?,
                    arg1: Response
                ) {
                    if (result == null || !result.isOk) {
                        callback.fail(ApiErrorCode.fromResult(result), null)
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
        userid: String?,
        callback: ApiCallback<User2?>
    ) {
        user2Api!!.userProfile(userid, object : Callback<Result<Profile2Data>?> {
            override fun success(
                result: Result<Profile2Data>?,
                arg1: Response
            ) {
                if (result == null || !result.isOk) {
                    callback.fail(ApiErrorCode.fromResult(result), null)
                } else {
                    saveCurrentUser(result.data?.user!!)
                    callback.success(transPro2DataToUser(result.data))
                }
            }

            override fun failure(error: RetrofitError) {
                callback.fail(null, error)
            }
        })
    }

    private fun transPro2DataToUser(proData: Profile2Data?): User2? {
        val userDto = proData?.user
        // userDto.aloha = proData.liked;
        // userDto.match = proData.friend;
        val img =
            Image.fromDTO(proData?.imageMap!![userDto!!.avatarImageId])
        return User2.Companion.fromDTO(userDto, img)
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
            user2Api!!.userMatchSetting(object : Callback<Result<MatchSettingData>> {
                override fun success(
                    result: Result<MatchSettingData>?,
                    response: Response
                ) {
                    if (result != null && result.isOk) {
                        contextUtil!!.profeatureEnable = result.data?.filterEnable == true
                        val regions =
                            result.data?.selectedRegion as ArrayList<String?>
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
        pw: String?,
        callbackImpl: CallbackImpl
    ) {
        user2Api!!.verifyPassword(
            pw,
            object : Callback<Result<ResultData>?> {
                override fun failure(retrofitError: RetrofitError) {
                    // ToastUtil.shortToast(AppApplication.getInstance(), R.string.Unkown_Error);
                    callbackImpl.failure()
                }

                override fun success(
                    result: Result<ResultData>?,
                    response: Response
                ) {
                    if (result != null && result.isOk) {
                        if (result.isOk) {
                            callbackImpl.success()
                        } else if (result.data?.error == IResultDataErrorCode.ERROR_INVALID_PASSWORD) {
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