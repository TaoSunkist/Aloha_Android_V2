package com.wealoha.social.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import butterknife.ButterKnife
import butterknife.InjectView
import butterknife.OnClick
import com.squareup.picasso.Picasso
import com.tbruyelle.rxpermissions2.RxPermissions
import com.wealoha.social.ActivityManager
import com.wealoha.social.AppApplication
import com.wealoha.social.AsyncLoader
import com.wealoha.social.R
import com.wealoha.social.activity.MainAct
import com.wealoha.social.activity.ProFeatureAct
import com.wealoha.social.api.AlohaService.Companion.shared
import com.wealoha.social.api.ServerApi
import com.wealoha.social.beans.*
import com.wealoha.social.commons.GlobalConstants
import com.wealoha.social.impl.Listeners.MonitorMainUiBottomTagPostion
import com.wealoha.social.push.NoticeBarController
import com.wealoha.social.utils.*
import com.wealoha.social.utils.AMapUtil.LocationCallback
import com.wealoha.social.utils.ImageUtil.CropMode
import com.wealoha.social.view.custom.CircleImageView
import com.wealoha.social.view.custom.WaterView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.client.Response
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * @author:sunkist
 * @see:
 * @since:
 * @description 加载数据，加载完后交给详情显示或者没有更多数据
 * @copyright wealoha.com
 * @Date:2014-10-30
 */
class LoadingFragment : BaseFragment(),
    LoaderManager.LoaderCallbacks<ApiResponse<MatchData>> {
    @JvmField
    @Inject
    var matchService: ServerApi? = null

    @JvmField
    @Inject
    var currentContextUtil: ContextUtil? = null

    @JvmField
    @Inject
    var userPromotionService: ServerApi? = null
    private var hasQuoraReset = false // 是否可以重置配额

    @JvmField
    @InjectView(R.id.circle1)
    var circle1: WaterView? = null

    //     @InjectView(R.id.circle2)
//     MatchCircleView circle2;
    @JvmField
    @InjectView(R.id.user_photo)
    var userPhoto: CircleImageView? = null

    @JvmField
    @InjectView(R.id.btn_reset_quota)
    var mRestAloha: TextView? = null

    @JvmField
    @InjectView(R.id.wait_layout)
    var mWaitLayout: LinearLayout? = null

    @JvmField
    @InjectView(R.id.countdown_tv)
    var mCountdown: TextView? = null

    @JvmField
    @InjectView(R.id.wait_text)
    var mWaitText: TextView? = null

    @JvmField
    @InjectView(R.id.wait_text_01)
    var mWaitText01: TextView? = null

    @JvmField
    @InjectView(R.id.wait_text_02)
    var mWaitText02: TextView? = null

    @JvmField
    @InjectView(R.id.loading_container_layout)
    var container: RelativeLayout? = null

    @JvmField
    @InjectView(R.id.net_error_tv)
    var mNetErrorTv: TextView? = null

    @JvmField
    @InjectView(R.id.user_photo_layout)
    var mUserPhotoRoot: RelativeLayout? = null

    @JvmField
    @InjectView(R.id.sign_icon)
    var mSignIcon: ImageView? = null
    var mainAct: MainAct? = null
    private var mBottomLocation = 0
    private var mHandler = Handler()
    private var exitAnim: Animation? = null
    private var fragexitAnim: Animation? = null
    private var alertDialog: Dialog? = null
    private var endOfScheduleRunble: Runnable? = null
    private var endOfScheduleHandler: Handler? = null
    private var aMapUtil: AMapUtil? = null
    private lateinit var appApplication: AppApplication
    private var mMyCounter: MyCounter? = null
    private var mCountDownTimer: Timer? = null
    private lateinit var rxPermissions: RxPermissions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (AppApplication.mUserList != null && AppApplication.mUserList.size > 0) {
            // 还有用户没看完，继续
            startingAloha()
        }
        aMapUtil = AMapUtil()
        appApplication = activity?.application as AppApplication
        rxPermissions = RxPermissions(this)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.frag_loading, container, false)
        mainAct = activity as MainAct
        ButterKnife.inject(this, view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        retainInstance = true
        userPhoto!!.visibility = View.VISIBLE
        circle1!!.visibility = View.VISIBLE
        // 計算居中的位置
        val params = RelativeLayout.LayoutParams(
            UiUtils.dip2px(activity, 120f),
            UiUtils.dip2px(activity, 120f)
        )
        val marginTop = (UiUtils.getScreenWidth(activity) - UiUtils.dip2px(activity, 120f)) / 2
        params.setMargins(marginTop, marginTop, 0, 0)
        mUserPhotoRoot!!.layoutParams = params
        mBottomLocation =
            UiUtils.getScreenHeight(activity) - UiUtils.getScreenWidth(activity) - UiUtils.dip2px(
                activity,
                45f
            )
        mWaitText!!.layoutParams.height = mBottomLocation
        mWaitLayout!!.layoutParams.height = mBottomLocation
    }

    override fun onStart() {
        super.onStart()
        // 头像缩小动画
        exitAnim = AnimationUtils.loadAnimation(activity, R.anim.aloha_load_finish)
        // frag变淡动画
        fragexitAnim = AnimationUtils.loadAnimation(activity, R.anim.loading_frag_out)
        loadUserPhoto()
    }

    private fun loadUserPhoto() {
        if (currentContextUtil?.getCurrentUser() != null) {
            Picasso.get().load(
                currentContextUtil?.getCurrentUser()?.avatarImage?.url
            ).placeholder(R.drawable.default_photo)
                .into(userPhoto)
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            bus.post(MonitorMainUiBottomTagPostion(0))
        } catch (e: Throwable) {
        }
        rxPermissions.request(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ).observeOn(
            AndroidSchedulers.mainThread()
        ).subscribe {
            if (it) {
                loadData()
            } else {
                ToastUtil.shortToast(context, R.string.failed)
            }
        }.addTo(compositeDisposable = compositeDisposable)
    }

    fun loadData() {
        // FIXME 暂时解决倒计时闪动的问题不知道是否会带来其他问题.
        mWaitLayout!!.visibility = View.INVISIBLE
        mWaitText!!.visibility = View.VISIBLE
        mSignIcon!!.visibility = View.INVISIBLE
        userPhoto!!.visibility = View.VISIBLE
        circle1!!.startLoadingAnimation()
        try {
            val loaderManager = loaderManager
            if (loaderManager != null && isAdded) {
                val start = System.currentTimeMillis()
                if (aMapUtil != null) {
                    aMapUtil!!.getLocation(activity, object : LocationCallback {
                        override fun locaSuccess() {
                            loaderManager.restartLoader(
                                LOADER_LOAD_MATCH,
                                null,
                                this@LoadingFragment
                            )
                        }

                        override fun locaError() {
                            XL.d("resetQuotaLoad", "false")
                            XL.d("LOCATION_TIME", "TIME:" + (System.currentTimeMillis() - start))
                            loaderManager.restartLoader(
                                LOADER_LOAD_MATCH,
                                null,
                                this@LoadingFragment
                            )
                        }
                    })
                }
            }
        } catch (e: IllegalStateException) {
            // 切换tab，Fragment隐藏了又调用会抛出异常，忽略
            XL.w(Companion.TAG, "加载数据时异常", e)
        } catch (e: NullPointerException) {
            XL.w(Companion.TAG, "加载数据时异常", e)
        }
    }

    fun random(fromInclusive: Int, toExclusive: Int): Long {
        val r = Random(System.currentTimeMillis())
        return (fromInclusive + r.nextInt(toExclusive - fromInclusive)).toLong()
    }

    /**
     * @Description: 开启及时界面
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2014-10-31
     */
    fun startTimingInterface(matchData: MatchData?) {
        openGuideDialog()
        // FIXME 這個地方從服務器去回的剩餘時間會有跳動 就是不准 ，誤差在1分鐘以內
        // 调整布局
        mNetErrorTv!!.visibility = View.GONE
        circle1!!.visibility = View.VISIBLE
        mUserPhotoRoot!!.visibility = View.VISIBLE
        circle1!!.stopAnimation()
        mWaitLayout!!.visibility = View.VISIBLE
        mWaitLayout!!.isEnabled = true
        mCountdown!!.visibility = View.VISIBLE
        mWaitText!!.visibility = View.INVISIBLE
        mRestAloha!!.setBackgroundResource(R.drawable.shape_aloha_filter_setting)
        val clock = resources.getDrawable(R.drawable.red_clock)
        clock.setBounds(0, 0, clock.minimumWidth, clock.minimumHeight)
        mRestAloha!!.setCompoundDrawables(clock, null, null, null)
        mRestAloha!!.refreshDrawableState()
        mRestAloha!!.setText(R.string.speed_up)
        mRestAloha!!.tag = TIME_DOWN
        hasQuoraReset = if (matchData == null) false else matchData.quotaReset > 0
        if (matchData!!.error == IResultDataErrorCode.ERROR_MATCH_NO_MORE_TODAY) {
            // 有倒计时和加速
            sendTimingNotice(matchData)
            mCountdown!!.visibility = View.VISIBLE
            returnLastUser()
            mCountdown!!.text = formatTime(matchData.quotaResetSeconds.toLong())
            if (mCountDownTimer != null) {
                mCountDownTimer!!.cancel()
                mCountDownTimer = null
            }
            mCountDownTimer = Timer()
            mMyCounter = MyCounter(matchData.quotaResetSeconds, false)
            mCountDownTimer!!.scheduleAtFixedRate(mMyCounter, 0, 1000)
        }
        // this.hasQuoraReset = matchData.quotaReset > 0;
    }

    private var moveX = 0f

    @SuppressLint("ClickableViewAccessibility")
    private fun returnLastUser() {
        val bundle = arguments
        if (bundle != null) {
            val flag = bundle.getBoolean("bool") // 是被Nope掉还是被Aloha掉了
            if (flag) container!!.setOnTouchListener { v, event ->
                val x = event.x
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> moveX = x
                    MotionEvent.ACTION_UP -> if (x - moveX > 300) {
                        if (isAdded && GlobalConstants.AppConstact.IS_LAST_USER) {
                            mHandler = mainAct!!.handler
                            val bundle = Bundle()
                            bundle.putParcelable(User.TAG, MainAct.mLastUser)
                            val message = mainAct!!.sendMsgToHandler(
                                GlobalConstants.AppConstact.RETURN_NEXT_USER_ALOHA,
                                bundle
                            )
                            mHandler.sendMessage(message)
                        }
                    }
                    MotionEvent.ACTION_MOVE -> {
                    }
                }
                v.performClick()
                true
            }
        }
    }

    fun openGuideDialog() {
        if (currentContextUtil?.currentUser != null && currentContextUtil?.currentUser?.isShowAlohaTimeDialog == false) {
            val view = LayoutInflater.from(activity)
                .inflate(R.layout.dialog_first_aloha_time, LinearLayout(activity), false)
            val close = view.findViewById<View>(R.id.close_tv) as TextView
            close.setOnClickListener {
                if (alertDialog != null && alertDialog!!.isShowing) {
                    alertDialog!!.dismiss()
                }
            }
            alertDialog = AlertDialog.Builder(activity) //
                .setView(view) //
                .setCancelable(false) //
                .create()
            alertDialog?.show()
            val user: User = currentContextUtil?.getCurrentUser()!!
            user.isShowAlohaTimeDialog = true
            currentContextUtil?.setCurrentUser(user)
        }
    }

    /**
     * @param matchData
     * @Description: 开启计时界面
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2014-11-25
     */
    private fun sendTimingNotice(matchData: MatchData?) {
        val bundle = Bundle()
        bundle.putParcelable(MatchData.TAG, matchData)
        bundle.putInt(
            GlobalConstants.AppConstact.ALARM_REQUEST_CODE,
            GlobalConstants.AppConstact.LOADING_SEND_TIMING_NOTICE
        )
        NoticeBarController.getInstance(mainAct)
            .timmingSendNotice(matchData!!.quotaDurationSeconds, bundle)
    }

    private fun formatTime(millis: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(millis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis - TimeUnit.HOURS.toMillis(hours))
        var seconds = TimeUnit.MILLISECONDS.toSeconds(
            millis - TimeUnit.HOURS.toMillis(hours) - TimeUnit.MINUTES.toMillis(minutes)
        )
        if (seconds < 0) {
            seconds = 0
        }
        return String.format(Locale.ENGLISH, "%02d:%02d:%02d", hours, minutes, seconds)
    }

    /**
     * 点了重置配额
     */
    private fun resetQuota() {
        if (hasQuoraReset) {
            // 重置配额
            mCountDownTimer!!.cancel()
            val seconds = mMyCounter!!.seconds
            mMyCounter = MyCounter(seconds, true)
            mCountDownTimer = Timer()
            mCountDownTimer!!.scheduleAtFixedRate(mMyCounter, 0, 20)
        } else {
            // 去高级设置在這裏先加載高級設置要用的數據
            // popup
            userPromotionService!!.get(object : Callback<ApiResponse<PromotionGetData>> {
                override fun success(apiResponse: ApiResponse<PromotionGetData>?, arg1: Response) {
                    if (apiResponse == null) {
                        return
                    }
                    if (apiResponse.isOk) {
                        val r = apiResponse.data
                        currentContextUtil?.profeatureEnable = !r!!.alohaGetLocked
                        val intent = Intent(activity, ProFeatureAct::class.java)
                        intent.putExtra(PromotionGetData.TAG, r)
                        startActivity(intent)
                        activity?.overridePendingTransition(R.anim.left_in, R.anim.stop)

                        // Bundle bundle = new Bundle();
                        // bundle.putParcelable(PromotionGetData.TAG, r);
                        // startActivity(GlobalConstants.IntentAction.INTENT_URI_ADVANCEDFEATURED, bundle);
                        // contextUtil.getMainAct().Accelerate(null, bundle);
                    }
                }

                override fun failure(arg0: RetrofitError) {}
            })
        }
    }

    private fun resetQuotaLoad() {
        if (hasQuoraReset) { // 是否可以重置配额？
            resetQuota()
            return
        }
        if (aMapUtil != null) {
            aMapUtil!!.getLocation(activity, object : LocationCallback {
                override fun locaSuccess() {
                    resetQuotaLoadResquest(
                        appApplication.locationXY[0],
                        appApplication.locationXY[1]
                    )
                }

                override fun locaError() {
                    resetQuotaLoadResquest(
                        appApplication.locationXY[0],
                        appApplication.locationXY[0]
                    )
                }
            })
        }
    }

    fun resetQuotaLoadResquest(latitude: Double?, longitude: Double?) {
        matchService!!.findRandom(latitude, longitude, object : Callback<ApiResponse<MatchData>> {
            override fun success(apiResponse: ApiResponse<MatchData>, arg1: Response) {
                hasQuoraReset = apiResponse.data!!.quotaReset > 0
                resetQuota()
            }

            override fun failure(arg0: RetrofitError) {}
        })
    }

    inner class MyCounter(var seconds: Int, boost: Boolean) : TimerTask() {
        private var boost = false
        override fun run() {
            if (boost) {
                seconds -= Math.max(seconds / 10, 5)
            } else {
                seconds--
            }
            mHandler.post {
                if (mCountdown != null) {
                    mCountdown!!.text = formatTime(seconds * 1000.toLong())
                }
            }
            if (seconds <= 0) {
                mCountDownTimer!!.cancel()
                onFinish()
            }
        }

        fun onFinish() {
            mHandler.post { endOfSchedule() }
        }

        init {
            this.boost = boost
        }
    }

    private fun endOfSchedule() {
        circle1!!.startLoadingAnimation()
        mWaitLayout!!.visibility = View.INVISIBLE
        mWaitLayout!!.isEnabled = false
        mWaitText!!.visibility = View.VISIBLE
        mWaitText01!!.setText(R.string.no_more_to_explore_now)
        mWaitText02!!.setText(R.string.next_aloha_time)
        mRestAloha!!.setBackgroundResource(R.drawable.shape_aloha_load)
        val clockDrawble = resources.getDrawable(R.drawable.red_clock)
        clockDrawble.setBounds(0, 0, clockDrawble.minimumWidth, clockDrawble.minimumHeight)
        mRestAloha!!.setCompoundDrawables(clockDrawble, null, null, null)
        mRestAloha!!.tag = TIME_DOWN // 在点击事件中判断要执行的方法
        endOfScheduleRunble = Runnable {
            val bundle = Bundle()
            bundle.putBoolean("reset", true)
            val baseFragAct = ActivityManager.current()
            if (isDetached) return@Runnable
            val restartLoader = loaderManager
            if (isAdded && restartLoader != null && !(baseFragAct as MainAct).isDestory) {
                // 获取位置，回调后拉取翻牌子数据
                if (aMapUtil != null) {
                    aMapUtil!!.getLocation(activity, object : LocationCallback {
                        override fun locaSuccess() {
                            restartLoader.restartLoader(
                                LOADER_LOAD_MATCH, bundle, this@LoadingFragment
                            )
                        }

                        override fun locaError() {
                            restartLoader.restartLoader(
                                LOADER_LOAD_MATCH, bundle, this@LoadingFragment
                            )
                        }
                    })
                }
            }
        }
        endOfScheduleHandler = Handler()
        endOfScheduleHandler!!.postDelayed(endOfScheduleRunble, 2000)
    }

    @OnClick(R.id.btn_reset_quota)
    fun onClick(view: View) {
        when (view.id) {
            R.id.btn_reset_quota -> {
                val btnType = view.tag as Int
                // 点了重置配额
                if (btnType == null || btnType == TIME_DOWN) {
                    rxPermissions.request(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ).observeOn(
                        AndroidSchedulers.mainThread()
                    ).subscribe {
                        if (it) {
                            resetQuotaLoad()
                        } else {
                            ToastUtil.shortToast(context, R.string.failed)
                        }
                    }.addTo(compositeDisposable = compositeDisposable)
                } else if (btnType == OPEN_FILTER) {
                    openFilterSetting()
                } else if (btnType == OPEN_PRIVACY) {
                    openPrivacy()
                }
            }
            else -> {
            }
        }
    }

    override fun onCreateLoader(loaderId: Int, bundle: Bundle?): Loader<ApiResponse<MatchData>> {
        if (!fragmentVisible || loaderId != LOADER_LOAD_MATCH) {
            return Loader(context)
        }
        val latitude: Double = appApplication.locationXY[0] ?: 0.0
        val longitude: Double = appApplication.locationXY[1] ?: 0.0
        XL.d(Companion.TAG, "onCreateLoader begin, latitude: $latitude, longitude: $longitude")

        return object : AsyncLoader<ApiResponse<MatchData>>(activity) {
            override fun loadInBackground(): ApiResponse<MatchData> {
                // 开始加载下一批数据..
                return if (bundle != null && bundle.getBoolean("reset")) {
                    // 重置配额
                    // matchService!!.findWithResetQuota(latitude, longitude, true)
                    shared.findWithResetQuota(latitude, longitude, true).blockingGet()
                } else {
                    shared.findRandom(latitude, longitude).blockingGet()
                }
            }
        }
    }

    override fun onLoaderReset(arg0: Loader<ApiResponse<MatchData>>) {}
    override fun onLoadFinished(
        loader: Loader<ApiResponse<MatchData>>,
        apiResponse: ApiResponse<MatchData>
    ) {
        XL.d(Companion.TAG, "加载下一批数据完成: $apiResponse")
        if (!isVisible) {
            return
        }
        if (!fragmentVisible) {
            XL.d(Companion.TAG, "视图不在了，忽略返回结果")
            return
        }
        if (apiResponse == null) {
            // 失败了，继续显示loading
            showNetworkFucked()
            return
        }
        if (loader.id == LOADER_LOAD_MATCH) {
            if (apiResponse != null) {
                if (apiResponse.isOk && apiResponse.data!!.list != null && apiResponse.data!!.list!!.size > 0) {
                    // 有下一批用户
                    AppApplication.mUserList = apiResponse.data!!.list
                    XL.i("HOME_KEY", "list size:" + AppApplication.mUserList.size)
                    // 设置标签的数据
                    mainAct!!.setRecommendSourceMap(apiResponse.data!!.recommendSourceMap)
                    Collections.shuffle(AppApplication.mUserList)
                    exitAnim!!.fillAfter = true
                    fragexitAnim!!.fillAfter = true
                    container!!.startAnimation(fragexitAnim)
                    mUserPhotoRoot!!.startAnimation(exitAnim)
                    // circle1.stopAnimation();
                    startingAloha()
                } else if (apiResponse.data!!.error == 200518) { // 当前时段没有更多匹配了，需要重置配额或者等下个时段
                    startTimingInterface(apiResponse.data)
                    // ToastUtil.shortToast(getActivity(), "200518");
                } else if (apiResponse.data!!.error == 200531) { // 没有搜寻到过滤条件范围内的人
                    // ToastUtil.shortToast(getActivity(), "200531");
                    showFilterResult()
                } else if (apiResponse.data!!.error == 200519) { // 没有更多了
                    // ToastUtil.shortToast(getActivity(), "200519");
                    showFilterNoResult()
                } else if (apiResponse.data!!.error == 200532) { // 服务挂了，稍候再试
                    // ToastUtil.shortToast(getActivity(), "200532");
                    showServerFucked()
                } else {
                    showNetworkFucked()
                }
            }
        }
    }

    /**
     * 服务器挂了的ui
     *
     * @return void
     */
    private fun showServerFucked() {
        // userPhoto.setVisibility(View.GONE);
        circle1!!.stopAnimation()
        userPhoto!!.visibility = View.GONE
        mSignIcon!!.visibility = View.VISIBLE
        mSignIcon!!.setImageResource(R.drawable.match_upgrading)
        circle1!!.visibility = View.GONE
        mWaitLayout!!.visibility = View.VISIBLE
        mCountdown!!.visibility = View.GONE
        mWaitLayout!!.isEnabled = true
        mWaitText!!.visibility = View.INVISIBLE
        mWaitText01!!.setText(R.string.filter_result_200532_01)
        mWaitText02!!.setText(R.string.filter_result_200532_02)
        mRestAloha!!.visibility = View.GONE
    }

    /**
     * 没有搜寻到过滤条件范围内的人时，显示提示界面
     */
    private fun showFilterResult() {
        userPhoto!!.visibility = View.INVISIBLE
        mSignIcon!!.visibility = View.VISIBLE
        mSignIcon!!.setImageResource(R.drawable.big_search)
        circle1!!.stopAnimation()
        circle1!!.visibility = View.GONE
        mWaitLayout!!.visibility = View.VISIBLE
        mWaitLayout!!.isEnabled = true
        mCountdown!!.visibility = View.GONE
        mWaitText!!.visibility = View.INVISIBLE
        mWaitText01!!.setText(R.string.filter_result_200531_01)
        mWaitText02!!.setText(R.string.filter_result_200531_02)
        mRestAloha!!.setBackgroundResource(R.drawable.shape_aloha_filter_setting)
        mRestAloha!!.refreshDrawableState()
        mRestAloha!!.setCompoundDrawables(null, null, null, null)
        mRestAloha!!.setText(R.string.change_setting)
        mRestAloha!!.tag = OPEN_FILTER
    }

    /**
     * 没有更多过滤条件范围内的人，显示提示界面
     */
    private fun showFilterNoResult() {
        userPhoto!!.visibility = View.INVISIBLE
        mSignIcon!!.visibility = View.VISIBLE
        mSignIcon!!.setImageResource(R.drawable.big_search)
        circle1!!.visibility = View.VISIBLE
        circle1!!.stopAnimation()
        mWaitLayout!!.visibility = View.VISIBLE
        mWaitLayout!!.isEnabled = true
        mCountdown!!.visibility = View.GONE
        mWaitText!!.visibility = View.INVISIBLE
        mWaitText01!!.setText(R.string.filter_result_200531_01)
        mWaitText02!!.setText(R.string.filter_result_200531_02)
        mRestAloha!!.visibility = View.GONE
    }

    private fun showNetworkFucked() {
        userPhoto!!.visibility = View.INVISIBLE
        mSignIcon!!.visibility = View.VISIBLE
        mSignIcon!!.setImageResource(R.drawable.match_network_disconnected)
        circle1!!.stopAnimation()
        circle1!!.visibility = View.GONE
        mWaitLayout!!.visibility = View.VISIBLE
        mWaitLayout!!.isEnabled = true
        mCountdown!!.visibility = View.GONE
        mWaitText!!.visibility = View.INVISIBLE
        mWaitText01!!.setText(R.string.filter_result_network_01)
        mWaitText02!!.setText(R.string.filter_result_network_02)
        mRestAloha!!.visibility = View.GONE
    }

    private fun showLocationFucked() {
        userPhoto!!.visibility = View.INVISIBLE
        mSignIcon!!.visibility = View.VISIBLE
        mSignIcon!!.setImageResource(R.drawable.match_network_disconnected)
        circle1!!.stopAnimation()
        circle1!!.visibility = View.GONE
        mWaitLayout!!.visibility = View.VISIBLE
        mWaitLayout!!.isEnabled = true
        mCountdown!!.visibility = View.GONE
        mWaitText!!.visibility = View.INVISIBLE
        mWaitText01!!.setText(R.string.location_service_down_result01)
        mWaitText02!!.setText(R.string.location_service_down_result02)
        mRestAloha!!.visibility = View.VISIBLE
        mRestAloha!!.refreshDrawableState()
        mRestAloha!!.setCompoundDrawables(null, null, null, null)
        mRestAloha!!.setText(R.string.private_set_str)
        mRestAloha!!.tag = OPEN_PRIVACY
    }

    private fun openFilterSetting() {
        if (mainAct != null) {
            mainAct!!.getUserMatchSetting()
        }
    }

    private fun openPrivacy() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = GlobalConstants.IntentAction.INTENT_URI_PRIVACY
        activity?.startActivity(intent)
        activity?.overridePendingTransition(R.anim.left_in, R.anim.stop)
    }

    /**
     * 加载到翻牌子数据，开启翻牌子界面
     */
    private fun startingAloha() {
        if (mainAct != null) {
            // 打开Aloha界面
            mainAct!!.handler.sendEmptyMessageDelayed(
                GlobalConstants.AppConstact.LOADING_MATCH_DATA_SUCCESS,
                750
            )
        }
    }

    override fun onPause() {
        super.onPause()
        if (endOfScheduleHandler != null && endOfScheduleRunble != null) {
            endOfScheduleHandler!!.removeCallbacks(endOfScheduleRunble)
        }
        // 记录计时数据
        if (mMyCounter != null) {
            mMyCounter!!.cancel()
        }
        circle1!!.stopAnimation()
        mWaitLayout!!.visibility = View.INVISIBLE
        mWaitLayout!!.isEnabled = false
        mWaitText!!.visibility = View.VISIBLE
    }

    companion object {
        private const val LOADER_LOAD_MATCH = 0

        // @InjectView(R.id.match_no_more_ll)
        // LinearLayout linearLayout;
        /* 及时界面显示 */ // @InjectView(R.id.loading_search)
        // TextView mLoadingSearch;
        // @InjectView(R.id.photo_container)
        // RelativeLayout mPhotoContainer;
        val TAG = LoadingFragment::class.java.simpleName
        private const val TIME_DOWN = 0x0001
        private const val OPEN_FILTER = 0x0002
        private const val OPEN_PRIVACY = 0x0003
        private val TRANSPARENT_DRAWABLE = ColorDrawable(Color.TRANSPARENT)
    }
}