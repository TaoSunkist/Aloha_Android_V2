package com.wealoha.social.activity

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.lidroid.xutils.ViewUtils
import com.lidroid.xutils.view.annotation.ViewInject
import com.lidroid.xutils.view.annotation.event.OnClick
import com.wealoha.social.ActivityManager
import com.wealoha.social.BaseFragAct
import com.wealoha.social.R
import com.wealoha.social.api.ServerApi
import com.wealoha.social.api.UserService
import com.wealoha.social.api.RxUserService
import com.wealoha.social.beans.*
import com.wealoha.social.commons.GlobalConstants
import com.wealoha.social.extension.observeOnMainThread
import com.wealoha.social.fragment.PhoneAreaFragment
import com.wealoha.social.utils.*
import io.reactivex.rxkotlin.addTo
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.client.Response
import java.util.*
import javax.inject.Inject

/**
 * @author:sunkist
 * @see:
 * @since:
 * @description 登录和找回密码
 * @copyright wealoha.com
 * @Date:2014-10-27
 */
class LoginAct : BaseFragAct(), View.OnClickListener {
    @JvmField
    @Inject
    var mUserService: ServerApi? = null

    @JvmField
    @Inject
    var mUserAPI: ServerApi? = null

    /** 區號  */
    @ViewInject(R.id.areacode)
    private val login: Button? = null

    /** 账号  */
    @ViewInject(R.id.login_account)
    private val mLoginAccount: EditText? = null

    /** 密码  */
    @ViewInject(R.id.login_password)
    private val mLoginPassword: EditText? = null

    /** 点击登录  */
    @ViewInject(R.id.login_btn)
    private val mLogin: Button? = null

    // ViewInject失效 报Undeclared
    private var forgotPw: TextView? = null
    private var mUserName: String = ""
    private var paf: PhoneAreaFragment? = null
    private var mAreaPosition = -1
    private var imm: InputMethodManager? = null

    @JvmField
    @Inject
    var contextUtil: ContextUtil? = null

    @JvmField
    @Inject
    var userRegisterService: ServerApi? = null

    private val forgotDialog: Dialog by lazy {
        val dialogView =
            layoutInflater.inflate(R.layout.dialog_forget_password, null)
        val phone =
            dialogView.findViewById<View>(R.id.forget_password_et) as EditText
        val title =
            dialogView.findViewById<View>(R.id.forget_title_tv) as TextView
        val forget_description_tv =
            dialogView.findViewById<View>(R.id.forget_description_tv) as TextView
        val closeLeft =
            dialogView.findViewById<View>(R.id.close_tv) as TextView
        val closeRight =
            dialogView.findViewById<View>(R.id.close_tv_02) as TextView
        FontUtil.setSemiBoldTypeFace(this, title)
        FontUtil.setRegulartypeFace(this, forget_description_tv)
        FontUtil.setRegulartypeFace(this, phone)
        FontUtil.setRegulartypeFace(this, closeLeft)
        FontUtil.setRegulartypeFace(this, closeRight)
        closeRight.setText(R.string.confirm)
        closeLeft.setText(R.string.cancel)
        // dialog号码输入框中前缀 ：+86 等
        phone.setText("$callingCode ")
        phone.setSelection(callingCode.length + 1)
        // 弹出dialog
        closeLeft.setOnClickListener { forgotDialog!!.dismiss() }
        closeRight.setOnClickListener {
            mUserName = phone.text.toString()
            forgotToServer()
        }
        AlertDialog.Builder(mContext).setView(dialogView).create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_login)
        ActivityManager.push(this)
        ViewUtils.inject(this) // 注入view和事件
        mContext = this
        initView()
        imm =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    @OnClick(R.id.forgot_pw, R.id.login_btn, R.id.areacode)
    override fun onClick(v: View) {
        when (v.id) {
            R.id.login_btn -> login()
            R.id.forgot_pw -> forgot()
            R.id.areacode -> {
                paf = PhoneAreaFragment(mAreaPosition)
                paf!!.show(fragmentManager, "phone_area_dialog")
            }
        }
    }

    override fun changePhoneArea(position: Int) {
        val area = paf!!.getCurrentArea(position)
        if (!TextUtils.isEmpty(area)) {
            login!!.text = area
            mAreaPosition = position
        }
    }

    /**
     * @Description:點擊登錄操作
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2014-10-27
     */
    private fun login() {
        val callingCode = callingCode
        if (login!!.text.toString() == null) {
            XL.d(TAG, "country_code_is_selected_by_default")
            return
        }
        val username = mLoginAccount!!.text.toString().trim { it <= ' ' }
        var password = mLoginPassword!!.text.toString().trim { it <= ' ' }
        if (TextUtils.isEmpty(username)) {
            ToastUtil.shortToast(this, R.string.login_username_is_invalid_text)
            // showSingleAlohaDialog(mContext,
            // R.string.login_username_is_invalid_title,
            // R.string.login_username_is_invalid_text);
            return
        }
        if (TextUtils.isEmpty(password) || password.length < 4) {
            ToastUtil.shortToast(this, R.string.login_password_is_invalid_text)
            // showSingleAlohaDialog(mContext,
            // R.string.login_password_is_invalid_title,
            // R.string.login_password_is_invalid_text);
            mLoginPassword.requestFocus()
            return
        }
        if (popup != null) {
            popup.show(container)
        }
        mUserName = StringUtil.appendPhoneNum(callingCode, username)
        password = StringUtil.md5(password)
        RxUserService.shared.login(
            mUserName,
            password
        ).observeOnMainThread(onSuccess = { result ->
            if (popup != null) {
                popup.hide()
            }
            if (result != null) {
                if (result.isOk) {
                    afterMobileLoginSuccess(mUserName, result.data!!.user, result.data!!.t)
                } else if (result.data!!.error == 451) { // "用戶被封禁"
                    ToastUtil.shortToast(this@LoginAct, R.string.failed)
                } else if (result.data!!.error == 401) {
                    ToastUtil.shortToast(this@LoginAct, R.string.login_failed)
                } else {
                    ToastUtil.shortToast(this@LoginAct, R.string.login_failed)
                }
            } else {
                ToastUtil.shortToast(this@LoginAct, R.string.Unkown_Error)
            }
        }, onError = {
            if (popup != null) {
                popup.hide()
            }
            showSingleAlohaDialog(mContext, R.string.network_error, null)
        }).addTo(compositeDisposable = compositeDisposable)
    }

    /**
     * 获取高级功能设置,并保存到本地
     *
     */
    private val profeatureSetting: Unit
        private get() {
            mUserAPI!!.userMatchSetting(object :
                Callback<Result<MatchSettingData>> {
                override fun success(
                    result: Result<MatchSettingData>?,
                    response: Response
                ) {
                    if (result != null && result.isOk) {
                        contextUtil!!.profeatureEnable = result.data!!.filterEnable
                        val regions =
                            result.data!!.selectedRegion as ArrayList<String>?
                        if (regions != null && regions.size > 0) {
                            contextUtil!!.filterRegion = regions[regions.size - 1]
                        }
                    }
                }

                override fun failure(arg0: RetrofitError) {}
            })
        }

    private val callingCode: String
        private get() {
            val str = login!!.text.toString()
            val s = str.indexOf("(")
            val area = str.subSequence(s, str.length) as String
            return StringUtil.getCallingCode(area)
        }

    /**
     * @Description:找回密码
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2014-10-27
     */
    private fun forgot() {
        // dialog样式
        forgotDialog.show()
    }

    /**
     * @Title: forgotToServer
     * @Description: 向服务器请求验证码
     * @return void 返回类型
     * @throws
     */
    private fun forgotToServer() {
        // mUserName:+XX XXXXXXXXX,去掉区号
        val userName =
            mUserName!!.substring(mUserName!!.indexOf(" ") + 1, mUserName!!.length)
        if (TextUtils.isEmpty(mUserName) || TextUtils.isEmpty(mUserName)) {
            ToastUtil.longToast(mContext, R.string.login_error_invalid_account)
            return
            /* 手機號格式判斷 */
        } else if (!StringUtil.matchPhone(userName)) {
            ToastUtil.longToast(mContext, R.string.login_error_invalid_account)
            return
        }
        userRegisterService!!.getResetPasswordCode(
            mUserName!!,
            object : Callback<Result<ResultData>> {
                override fun success(
                    result: Result<ResultData>,
                    response: Response
                ) {
                    // mLoadingDialog.dismiss();
                    // PromptPopup.hidePrompt();
                    if (popup != null) {
                        popup.hide()
                    }
                    if (result != null) {
                        if (result.isOk) {
                            val bundle = Bundle()
                            bundle.putInt("from", 1)
                            bundle.putString(
                                GlobalConstants.AppConstact.USERNAME,
                                mUserName
                            )
                            // 请求成功跳转
                            startActivity(
                                GlobalConstants.IntentAction.INTENT_URI_VERIFY,
                                bundle
                            )
                            ToastUtil.shortToast(
                                mContext,
                                getString(R.string.verification_code_has_been_sent)
                            )
                        } else if (result.status == Result.STATUS_CODE_THRESHOLD_HIT) {
                            ToastUtil.shortToast(
                                mContext,
                                getString(R.string.register_frequent_request)
                            )
                            // showSingleAlohaDialog(mContext,
                            // R.string.register_frequent_request, null);
                        } else if (result.data!!.error == IResultDataErrorCode.ERROR_USER_NOT_FOUND) {
                            ToastUtil.shortToast(
                                mContext,
                                getString(R.string.register_mobile_error_noregister)
                            )
                            // showSingleAlohaDialog(mContext,
                            // R.string.register_mobile_error_noregister, null);
                        }
                    } else {
                        ToastUtil.shortToast(mContext, getString(R.string.get_code_error))
                        // showSingleAlohaDialog(mContext, R.string.get_code_error,
                        // null);
                    }
                }

                override fun failure(error: RetrofitError) {
                    // mLoadingDialog.dismiss();
                    // PromptPopup.hidePrompt();
                    if (popup != null) {
                        popup.hide()
                    }
                    ToastUtil.shortToast(mContext, getString(R.string.get_code_error))
                    // showSingleAlohaDialog(mContext, R.string.get_code_error,
                    // null);
                }
            })
    }

    fun initView() {
        forgotPw = findViewById<View>(R.id.forgot_pw) as TextView
        forgotPw!!.setOnClickListener(this)
    }

    /**
     * @Title: popInputMethod
     * @Description: 自動彈出輸入法
     * @param 设定文件
     * @return void 返回类型
     * @throws
     */
    fun popInputMethod() {
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {

                // imm = (InputMethodManager)
                // getSystemService(Context.INPUT_METHOD_SERVICE);
                imm!!.showSoftInput(mLoginAccount, 0)
            }
        }, 500)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        // 彈出輸入法
        popInputMethod()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.repeatCount == 0) { // 按下的如果是BACK，同时没有重复
            openWelcomeAct()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    fun cancel(view: View?) {
        openWelcomeAct()
    }

    fun openWelcomeAct() {
        ActivityManager.pop(this)
    }

    override fun onStop() {
        super.onStop()
        if (imm != null && mLoginAccount != null && mLoginPassword != null) {
            imm!!.hideSoftInputFromWindow(mLoginAccount.windowToken, 0)
            imm!!.hideSoftInputFromWindow(mLoginPassword.windowToken, 0)
            // imm.hideSoftInputFromInputMethod(mEditTextContent.getWindowToken(),
            // 0);
        }
    }

    override fun onPause() {
        super.onPause()
    }

    companion object {
        private val TAG = LoginAct::class.java.simpleName
    }
}