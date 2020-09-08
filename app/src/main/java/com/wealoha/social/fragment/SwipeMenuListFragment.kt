package com.wealoha.social.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView.OnItemLongClickListener
import butterknife.InjectView
import butterknife.OnClick
import com.squareup.otto.Subscribe
import com.wealoha.social.BaseFragAct
import com.wealoha.social.R
import com.wealoha.social.adapter.SwipeMenuAdapter
import com.wealoha.social.api.AlohaService
import com.wealoha.social.api.ServerApi
import com.wealoha.social.beans.*
import com.wealoha.social.commons.GlobalConstants
import com.wealoha.social.event.ControlUserEvent
import com.wealoha.social.extension.observeOnMainThread
import com.wealoha.social.utils.FontUtil
import com.wealoha.social.utils.ToastUtil
import com.wealoha.social.utils.XL
import com.wealoha.social.view.custom.dialog.ListItemDialog
import com.wealoha.social.view.custom.dialog.ListItemDialog.ListItemCallback
import com.wealoha.social.view.custom.dialog.ListItemDialog.ListItemType
import io.reactivex.rxkotlin.addTo
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.client.Response
import java.lang.ref.WeakReference
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class SwipeMenuListFragment : BaseFragment(), ListItemCallback, OnItemClickListener,
    View.OnClickListener, AbsListView.OnScrollListener, OnItemLongClickListener {
    @JvmField
    @Inject
    var mUserService: ServerApi? = null

    @JvmField
    @Inject
    var mProfileService: ServerApi? = null

    @JvmField
    @InjectView(R.id.swipe_list)
    var mSwipeList: ListView? = null

    @JvmField
    @InjectView(R.id.back)
    var mBackImg: ImageView? = null

    @JvmField
    @InjectView(R.id.title)
    var mTitle: TextView? = null
    var mBaseFragAct: BaseFragAct? = null

    @JvmField
    @InjectView(R.id.layout)
    var layout: ViewGroup? = null
    private lateinit var footerView: View
    private var bundle: Bundle? = null
    private var mListType = 0
    private var mSwipeAdapter: SwipeMenuAdapter? = null
    private var mUsers: ArrayList<User> = arrayListOf()
    private lateinit var mCurrentUser: User
    private var syncLastPageBool = false
    private val SUPER_CURSOR = "cursor"
    private var cursor: String = SUPER_CURSOR
    private val pageCount = 50

    // type==LISTTYPE_PRAISE
    private var feedLikeCount = 0
    private val unlockCount: TextView? = null
    private var mApiResponse: ApiResponse<UserListResult>? = null
    private var footerProgress: ProgressBar? = null
    private var footerImg: TextView? = null
    private var positionTemp = 0

    /**
     * 当前被点击的用户
     */
    var user: User? = null

    // private LoadingPopupWindow loading;
    enum class ListType {
        BLACK, LIKE, Popularity
    }

    class MenuListHandler(smlFrag: SwipeMenuListFragment) : Handler() {
        var frag: WeakReference<SwipeMenuListFragment> = WeakReference(smlFrag)
        override fun handleMessage(msg: Message) {
            val smlFrag = frag.get()
            smlFrag?.handlerService(msg)
        }

    }

    fun handlerService(msg: Message) {
        when (msg.what) {
            CREAT_ADAPTER -> {
                val showMatch =
                    mListType == LISTTYPE_LIKE || mListType == LISTTYPE_POPULARITY
                if (mApiResponse != null && mSwipeList != null) {
                    mSwipeAdapter =
                        SwipeMenuAdapter(mApiResponse!!.data, showMatch)
                    mSwipeList!!.adapter = mSwipeAdapter
                }
            }
            ADAPTER_NOTIFY -> if (mApiResponse != null && mSwipeList != null) {
                mSwipeAdapter!!.notifyDataSetChanged(mApiResponse)
                XL.i("LOADER_TEST", "ADAPTER_NOTIFY-----")
            }
            else -> {
            }
        }
        syncLastPageBool = true
    }

    var myHandler: Handler = MenuListHandler(this)
    var userlistCallback: Callback<ApiResponse<UserListResult>> =
        object : Callback<ApiResponse<UserListResult>> {
            override fun failure(arg0: RetrofitError?) {
                syncLastPageBool = true
                addFooterView(false)
                ToastUtil.longToast(context, R.string.network_error)
            }

            override fun success(
                apiResponse: ApiResponse<UserListResult>?,
                arg1: Response?
            ) {
                if (!isVisible) {
                    return
                }
                if (apiResponse != null && apiResponse.isOk) {
                    addFooterView(true)
                    val msg = Message()
                    if (mSwipeAdapter == null) {
                        msg.what = CREAT_ADAPTER
                    } else {
                        msg.what = ADAPTER_NOTIFY
                    }
                    mApiResponse = apiResponse
                    myHandler.sendMessage(msg)
                    mUsers.addAll(apiResponse.data!!.list!!)
                    cursor = apiResponse.data!!.nextCursorId ?: ""
                    if (TextUtils.isEmpty(cursor) || "null" == cursor) {
                        removeFooterView()
                    }
                    // 更新人氣列表表頭
                    if (mListType == LISTTYPE_POPULARITY && unlockCount != null) {
                        unlockCount.text = context.resources.getString(
                            R.string.showing_latest_followers,
                            apiResponse.data!!.alohaGetUnlockCount
                        )
                    }
                } else {
                    XL.i(TAG, apiResponse!!.data!!.error.toString() + "")
                    addFooterView(false)
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // loading = new LoadingPopupWindow();
        mBaseFragAct = activity as BaseFragAct?
        return inflater.inflate(R.layout.frag_swipemenu_list, container, false)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        bundle = arguments
        if (bundle != null) {
            mListType = bundle!!.getInt("listtype")
            if (mListType == LISTTYPE_PRAISE) {
                feedLikeCount = bundle!!.getInt("feedLikeCount")
            }
        }
        mCurrentUser = contextUtil.currentUser
        initView()
    }

    override fun initTypeFace() {
        fontUtil.changeViewFont(mTitle, FontUtil.Font.ENCODESANSCOMPRESSED_600_SEMIBOLD)
    }


    private fun initData() {
        // 處理title
        if (mListType == LISTTYPE_LIKE) {
            mTitle!!.text = resources.getString(
                R.string.profile_aloha_title,
                contextUtil.currentUser.alohaCount
            )
            loadLiksList()
        } else if (mListType == LISTTYPE_BLACK) {
            mTitle!!.setText(R.string.profile_blacklist_title)
            loadBlackList()
        } else if (mListType == LISTTYPE_POPULARITY) {
            // 处理标题栏显示的人气数量
            mTitle!!.text = resources.getString(
                R.string.profile_aloha_get_title,
                contextUtil.currentUser.alohaGetCount
            )
            mSwipeList!!.onItemClickListener = this
            loadPopularityList()
        } else if (mListType == LISTTYPE_PRAISE) {
            mTitle!!.text = resources.getString(R.string.feed_like_title, feedLikeCount)
            mSwipeList!!.onItemClickListener = this
            loadPraiseList()
        }
    }

    private fun initView() {
        if (mListType != LISTTYPE_POPULARITY && mListType != LISTTYPE_PRAISE) {
            loadSwipeDeleteUI()
        }
        initData()
        mSwipeList!!.setOnScrollListener(this)
        footerView = LayoutInflater.from(context)
            .inflate(R.layout.list_loader_footer, ListView(context), false)
        footerProgress =
            footerView.findViewById<View>(R.id.reload_progress) as ProgressBar
        footerImg = footerView.findViewById<View>(R.id.reload_img) as TextView
        footerView.setOnClickListener(View.OnClickListener {
            syncLastPageBool = false
            XL.i("USER_LIST_CURSOR", "loader:$cursor")
            if (mListType == LISTTYPE_LIKE) {
                loadLiksList()
            } else if (mListType == LISTTYPE_BLACK) {
                loadBlackList()
            } else if (mListType == LISTTYPE_POPULARITY) {
                loadPopularityList()
            } else if (mListType == LISTTYPE_PRAISE) {
                loadPraiseList()
            }
        })
        mSwipeList!!.addFooterView(footerView)
        addFooterView(true)
    }

    private fun removeFooterView() {
        if (footerView != null) {
            footerView!!.visibility = View.GONE
        }
    }

    private fun addFooterView(isloading: Boolean) {
        if (footerImg == null || footerProgress == null) {
            return
        }
        if (isloading) {
            footerProgress!!.visibility = View.VISIBLE
            footerImg!!.visibility = View.GONE
        } else {
            footerProgress!!.visibility = View.GONE
            footerImg!!.visibility = View.VISIBLE
        }
    }

    fun loadLiksList() {
        if (TextUtils.isEmpty(cursor) || "null" == cursor) {
            return
        }
        if (SUPER_CURSOR == cursor) {
            cursor = ""
        }
        mUserService!!.likeList(cursor, pageCount.toString() + "", userlistCallback)
    }

    fun loadBlackList() {
        if (TextUtils.isEmpty(cursor) || "null" == cursor) {
            return
        }
        if (SUPER_CURSOR == cursor) {
            cursor = ""
        }
        mUserService!!.blackList(cursor, pageCount.toString() + "", userlistCallback)
    }

    fun loadPopularityList() {
        if (TextUtils.isEmpty(cursor) || "null" == cursor) {
            return
        }
        if (SUPER_CURSOR == cursor) {
            cursor = ""
        }
        mUserService!!.popularityList(cursor, pageCount.toString() + "", userlistCallback)
    }

    fun loadPraiseList() {
        if (bundle == null) {
            return
        }
        if (TextUtils.isEmpty(cursor) || "null" == cursor) {
            return
        }
        if (SUPER_CURSOR == cursor) {
            cursor = ""
        }
        XL.i(TAG, "loadPopularityList")
        val postId = bundle!!.getString("postId")
        AlohaService.shared.likeFeedPersons(
            postId!!,
            cursor,
            pageCount.toString() + ""
        ).observeOnMainThread(onSuccess = {
            userlistCallback.success(it, null)
        }, onError = {
            userlistCallback.failure(null)
        }).addTo(compositeDisposable = compositeDisposable)
    }

    private fun loadSwipeDeleteUI() {
        mSwipeList!!.onItemClickListener = this
        mSwipeList!!.onItemLongClickListener = this
    }

    /**
     * @Title: deleteFromBlackList
     * @Description: 移出黑名单
     */
    private fun deleteFromBlackList(position: Int) {
        val (id) = mUsers!![position]
        mUserService!!.unblock(id, object : Callback<ApiResponse<ResultData>> {
            override fun failure(arg0: RetrofitError) {
                ToastUtil.longToast(mBaseFragAct, R.string.is_not_work)
                syncLastPageBool = true
            }

            override fun success(
                apiResponse: ApiResponse<ResultData>?,
                arg1: Response
            ) {
                if (apiResponse == null) {
                    return
                }
                if (apiResponse.isOk) {
                    if (mUsers != null && mUsers!!.size > position) {
                        mUsers.removeAt(position)
                        mSwipeAdapter!!.notifyDataSetChangedByList(mUsers)
                    }
                    syncLastPageBool = true
                    return
                }
            }
        })
    }

    /**
     * @Title: deleteFromBlackList
     * @Description: 移出喜欢列表
     */
    private fun deleteFromLikeList(position: Int) {
        XL.i("UNALOHA_SOMEONE", "deleteFromLikeList$position")
        mUserService!!.dislikeUser(
            mUsers!![position].id,
            object : Callback<ApiResponse<ResultData>> {
                override fun failure(arg0: RetrofitError) {
                    syncLastPageBool = true
                }

                override fun success(
                    apiResponse: ApiResponse<ResultData>,
                    arg1: Response
                ) {
                    try {
                        mUsers.removeAt(position)
                    } catch (e: IndexOutOfBoundsException) {
                        XL.d(TAG, e.message)
                        return
                    }
                    if (mSwipeAdapter != null) {
                        mSwipeAdapter!!.notifyDataSetChangedByList(mUsers)
                    }
                    if (mCurrentUser.alohaCount > 0) {
                        mCurrentUser.alohaCount = mCurrentUser.alohaCount - 1
                        mTitle!!.text = resources.getString(
                            R.string.profile_aloha_title,
                            mCurrentUser!!.alohaCount
                        )
                        contextUtil.currentUser = mCurrentUser
                    }
                    syncLastPageBool = true
                }
            })
    }

    override fun onItemClick(
        parent: AdapterView<*>?,
        view: View,
        position: Int,
        id: Long
    ) {
        val bundle = Bundle()
        user = mUsers[position]
        bundle.putParcelable(User.TAG, user)
        var requestcode = 0
        if (mListType == LISTTYPE_POPULARITY) {
            // 记录这个profile 界面时从人气界面跳转来的
            bundle.putString(
                "refer_key",
                GlobalConstants.WhereIsComeFrom.FOLLOWER_TO_PROFILE
            )
        } else if (mListType == LISTTYPE_LIKE) {
            requestcode = PROFIEL_REQUESTCODE
        }
        (activity as BaseFragAct).startFragmentForResult(
            Profile2Fragment::class.java, bundle, true, requestcode,  //
            R.anim.left_in, R.anim.stop
        )
    }

    override fun onActivityResultCallback(
        requestcode: Int,
        resultcode: Int,
        result: Intent?
    ) {
        result?.let {
            if (resultcode == Activity.RESULT_OK) {
                when (requestcode) {
                    PROFIEL_REQUESTCODE -> {
                        val userid = result.getStringExtra("userid")
                        var i = 0
                        while (i < mUsers.size) {
                            val (id) = mUsers[i]
                            if (id == userid) {
                                mUsers.removeAt(i)
                                break
                            }
                            i++
                        }
                        mSwipeAdapter!!.notifyDataSetChangedByList(mUsers)
                        if (mCurrentUser.alohaCount > 0) {
                            mCurrentUser.alohaCount = mCurrentUser.alohaCount - 1
                            mTitle!!.text = resources.getString(
                                R.string.profile_aloha_title, mCurrentUser.alohaCount
                            )
                            contextUtil.currentUser = mCurrentUser
                        }
                    }
                }
            }
        }
    }

    @OnClick(R.id.back)
    override fun onClick(v: View) {
        when (v.id) {
            R.id.back -> activity!!.finish()
            else -> {
            }
        }
    }

    override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {}
    override fun onScroll(
        view: AbsListView,
        firstVisibleItem: Int,
        visibleItemCount: Int,
        totalItemCount: Int
    ) {
        if (firstVisibleItem + visibleItemCount + 15 > totalItemCount - 5 && syncLastPageBool) {
            syncLastPageBool = false
            if (mListType == LISTTYPE_LIKE) {
                loadLiksList()
            } else if (mListType == LISTTYPE_BLACK) {
                loadBlackList()
            } else if (mListType == LISTTYPE_POPULARITY) {
                loadPopularityList()
            } else if (mListType == LISTTYPE_PRAISE) {
                loadPraiseList()
            }
        }
    }

    override fun onItemLongClick(
        parent: AdapterView<*>?,
        view: View,
        position: Int,
        id: Long
    ): Boolean {
        // if (mListType == LISTTYPE_LIKE) {
        // // new ReportBlackAlohaPopup().showPopup(PopupType.LISTTYPE_LIKE, PostType.LISTTYPE_LIKE,
        // // String.valueOf(position), null, null);
        // new ListItemDialog(getActivity(), parent).showListItemPopup(this, null, ListItemType.DELETE);
        // } else if (mListType == LISTTYPE_BLACK) {
        ListItemDialog(activity, parent).showListItemPopup(this, null, ListItemType.DELETE)
        positionTemp = position
        activity!!.setResult(Activity.RESULT_OK)
        // }
        return true
    }

    var controlUserEvent: Any = object : Any() {
        @Subscribe
        fun refreshUI(event: ControlUserEvent) {
            controlUser(event.postion)
        }

        private fun controlUser(position: Int) {
            syncLastPageBool = false
            if (mListType == LISTTYPE_LIKE) {
                deleteFromLikeList(position)
            } else if (mListType == LISTTYPE_BLACK) {
                deleteFromBlackList(position)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            bus.register(controlUserEvent)
        } catch (e: Throwable) {
        }
        if (user != null) {
            XL.i(TAG, "user: not null")
            refreshUser(user!!.id)
            user = null
        }
    }

    private fun refreshUser(userid: String) {
        if (mProfileService == null) {
            return
        }
        mProfileService!!.view(
            userid,
            object : Callback<ApiResponse<ProfileData>> {
                override fun success(
                    apiResponse: ApiResponse<ProfileData>?,
                    arg1: Response
                ) {
                    if (apiResponse == null || !apiResponse.isOk || mUsers == null || mSwipeAdapter == null) {
                        return
                    }
                    for (user in mUsers!!) {
                        if (user.id == userid) {
                            user.match = apiResponse.data!!.user!!.match
                            mSwipeAdapter!!.notifyDataSetChangedByList(mUsers)
                            break
                        }
                    }
                }

                override fun failure(arg0: RetrofitError) {}
            })
    }

    override fun onPause() {
        try {
            bus.unregister(controlUserEvent)
        } catch (e: Throwable) {
        }
        super.onPause()
        // 返回profile 刷新界面
        activity!!.setResult(Activity.RESULT_OK)
    }

    fun getListType(type: Int): Boolean {
        return type == mListType
    }

    override fun itemCallback(listItemType: Int) {
        when (listItemType) {
            ListItemType.DELETE -> if (mListType == LISTTYPE_LIKE) {
                deleteFromLikeList(positionTemp)
            } else if (mListType == LISTTYPE_BLACK) {
                deleteFromBlackList(positionTemp)
            }
            else -> {
            }
        }
    }

    companion object {
        const val LISTTYPE_BLACK = 1
        const val LISTTYPE_LIKE = 2
        const val LISTTYPE_POPULARITY = 3
        const val LISTTYPE_PRAISE = 4
        const val PROFIEL_REQUESTCODE = 5
        private const val CREAT_ADAPTER = 1
        private const val ADAPTER_NOTIFY = 2
    }
}