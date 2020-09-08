package com.wealoha.social.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Parcelable
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView.OnItemLongClickListener
import butterknife.InjectView
import butterknife.OnClick
import com.wealoha.social.BaseFragAct
import com.wealoha.social.R
import com.wealoha.social.activity.FeedCommentActivity
import com.wealoha.social.adapter.FeedCommentAdapter
import com.wealoha.social.adapter.FeedCommentAdapter.FeedCommentAdapterCallback
import com.wealoha.social.adapter.feed.BaseFeedHolder.Holder2FragCallback
import com.wealoha.social.adapter.feed.FeedHolder
import com.wealoha.social.api.AlohaService
import com.wealoha.social.api.Comment2ListApiService
import com.wealoha.social.api.ServerApi
import com.wealoha.social.beans.*
import com.wealoha.social.commons.GlobalConstants
import com.wealoha.social.extension.observeOnMainThread
import com.wealoha.social.fragment.Profile2Fragment
import com.wealoha.social.utils.*
import com.wealoha.social.view.custom.dialog.ListItemDialog
import com.wealoha.social.view.custom.dialog.ListItemDialog.ListItemCallback
import com.wealoha.social.view.custom.dialog.ListItemDialog.ListItemType
import com.wealoha.social.widget.BaseListApiAdapter.LoadCallback
import com.wealoha.social.widget.ScrollToLoadMoreListener
import io.reactivex.rxkotlin.addTo
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.client.Response
import javax.inject.Inject

class FeedCommentActivity : BaseFragAct(), View.OnClickListener,
    OnItemClickListener, OnItemLongClickListener, ListItemCallback, FeedCommentAdapterCallback,
    Holder2FragCallback {

    @JvmField
    @Inject
    var mComment2Service: Comment2ListApiService? = null

    @JvmField
    @InjectView(R.id.feed_comment_back_iv)
    var mBack: ImageView? = null

    @JvmField
    @InjectView(R.id.feed_comment_content_lv)
    var mContentListView: ListView? = null

    @JvmField
    @InjectView(R.id.comments_content_et)
    var mContentEdit: EditText? = null

    @JvmField
    @InjectView(R.id.comments_send_tv)
    var mSendTv: TextView? = null

    @JvmField
    @InjectView(R.id.menu_bar)
    var mTitle: RelativeLayout? = null
    var mHeaderRoot: RelativeLayout? = null
    var loadMoreComment: TextView? = null
    var mProgressBar: ProgressBar? = null
    private var mFeedCommentAdapter: FeedCommentAdapter? = null
    private val pageSize = 15
    private var mPost: Post? = null
    private lateinit var headView: View
    private var removePosition = 0
    private var removeCommentId: String? = null
    private var mUser: User? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed_comment)
        if (data) { // 获取bundle中的值
            initHeadView()
        } else {
            return  // 没有数据无法渲染视图
        }
        mFeedCommentAdapter = FeedCommentAdapter(this, mComment2Service, this)
        mContentListView!!.adapter = mFeedCommentAdapter
        mContentListView!!.setOnScrollListener(
            ScrollToLoadMoreListener(
                4,
                ScrollToLoadMoreListener.Callback { loadLatePage() })
        )
        mContentListView!!.onItemClickListener = this
        mContentListView!!.onItemLongClickListener = this
        loadFirstPage(intent.getStringExtra(GlobalConstants.TAGS.COMMENT_ID))
        if (mUser != null) {
            mContentEdit!!.hint = resources.getString(
                R.string.comment_in_reply_hint,
                mUser!!.name
            )
        }
    }

    /***
     * 从bundle 中获取post 和user数据
     *
     * @return boolean 如果post 和user 都不为空那么返回true
     */
    private val data: Boolean
        private get() {
            val bundle = intent.extras
            if (bundle != null) {
                mPost = bundle.getParcelable<Parcelable>(GlobalConstants.TAGS.POST_TAG) as Post?
                mUser = bundle.getParcelable<Parcelable>(User.TAG) as User?
                return true
            }
            return false
        }

    override fun initTypeFace() {
        fontUtil.changeFonts(mTitle, FontUtil.Font.ENCODESANSCOMPRESSED_600_SEMIBOLD)
    }

    /**
     * 加载 “加载之前的留言” 视图
     *
     * @ listview
     * @return void
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun initHeadView() {
        headView =
            layoutInflater.inflate(R.layout.item_feed_comment_head, mContentListView, false)
        mHeaderRoot = headView.findViewById<View>(R.id.header_root) as RelativeLayout
        mHeaderRoot!!.setOnTouchListener { v, event ->
            v.performClick()
            loadEarlyPage(false)
            false
        }
        loadMoreComment =
            headView.findViewById<View>(R.id.item_feed_comment_head_load_tv) as TextView
        mProgressBar =
            headView.findViewById<View>(R.id.item_feed_comment_pb) as ProgressBar
        headView.visibility = View.INVISIBLE
        mContentListView!!.addHeaderView(headView)
    }

    /**
     * 是否显示正在加载
     *
     * @param isLoading
     * @return void
     */
    private fun loadingHeader(isLoading: Boolean) {
        if (isLoading) {
            mProgressBar!!.visibility = View.VISIBLE
        } else {
            mProgressBar!!.visibility = View.GONE
        }
    }

    /**
     * 移除加载更早的视图
     *
     * @return void
     */
    private fun removeHeadView() {
        mContentListView!!.removeHeaderView(headView)
    }

    /**
     * 滚到选中的评论，只在第一次加载数据的时候执行
     *
     * @param commentid
     * @return void
     */
    private fun smoothToTheComment(commentid: String) {
        if (TextUtils.isEmpty(commentid)) {
            return
        }
        for (i in 0 until mFeedCommentAdapter!!.count) {
            val postComment = mFeedCommentAdapter!!.getItem(i) as PostComment
            if (commentid == postComment.id) {
                mContentListView!!.smoothScrollToPosition(i)
                mContentEdit!!.hint = getString(
                    R.string.comment_in_reply_hint,
                    postComment.user.name
                )
                mContentEdit!!.tag = postComment
                break
            }
        }
    }

    private fun loadFirstPage(commentid: String) {
        mFeedCommentAdapter!!.loadContextByCursor(
            commentid,
            pageSize,
            mPost!!.postId,
            object : LoadCallback {
                override fun success(
                    hasPrev: Boolean,
                    hasNext: Boolean
                ) {
                    if (isFinishing) {
                        return
                    }
                    if (!hasPrev) {
                        removeHeadView()
                    } else if (headView != null) {
                        headView!!.visibility = View.VISIBLE
                    }
                    mContentListView!!.postDelayed({
                        smoothToTheComment(
                            intent.getStringExtra(
                                GlobalConstants.TAGS.COMMENT_ID
                            )
                        )
                    }, 500)
                }

                override fun fail(
                    code: ApiErrorCode,
                    exception: Exception
                ) {
                    ToastUtil.shortToast(this@FeedCommentActivity, R.string.Unkown_Error)
                }

                override fun dataState(size: Int, isnull: Boolean) {
                    // TODO Auto-generated method stub
                }
            })
    }

    /**
     * @Title: loadLatePage
     * @Description: 加载以前的数据
     */
    private fun loadEarlyPage(reset: Boolean) {
        XL.i("COMMENT_TEST", "loadNextPage")
        if (reset) {
            mFeedCommentAdapter!!.resetState()
        }
        loadingHeader(true)
        // 这里不需要判断
        mFeedCommentAdapter!!.loadEarlyPage(pageSize, mPost!!.postId, object : LoadCallback {
            override fun success(
                hasPrev: Boolean,
                hasNext: Boolean
            ) {
                if (!hasPrev) {
                    removeHeadView()
                } else {
                    loadingHeader(false)
                }
            }

            override fun fail(
                error: ApiErrorCode,
                exception: Exception
            ) {
                ToastUtil.shortToast(this@FeedCommentActivity, R.string.Unkown_Error)
            }

            override fun dataState(size: Int, isnull: Boolean) {
                // TODO Auto-generated method stub
            }
        })
    }

    /**
     * @Title: loadLatePage
     * @Description: 加载新数据
     */
    private fun loadLatePage() {
        XL.i("COMMENT_TEST", "loadLatePage")
        mFeedCommentAdapter!!.loadLatePage(pageSize, mPost!!.postId, object : LoadCallback {
            override fun success(
                hasPrev: Boolean,
                hasNext: Boolean
            ) {
                if (isFinishing) {
                    return
                }
                // if (!hasPrev || hasNext) {
                // }
            }

            override fun fail(
                error: ApiErrorCode,
                exception: Exception
            ) {
            }

            override fun dataState(size: Int, isnull: Boolean) {
                // TODO Auto-generated method stub
            }
        })
    }

    @OnClick(R.id.feed_comment_back_iv, R.id.comments_send_tv)
    override fun onClick(v: View) {
        when (v.id) {
            R.id.feed_comment_back_iv -> {
                overridePendingTransition(R.anim.right_out, R.anim.stop)
                val intent = Intent()
                intent.putExtra(FeedHolder.COMMENT_COUNT_DATA_KEY, mFeedCommentAdapter!!.count)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
            R.id.comments_send_tv -> sendComment(
                mContentEdit!!.text.toString(),
                mContentEdit!!.tag as PostComment
            )
            else -> {
            }
        }
    }

    override fun onBackPressed() {
        val intent = Intent()
        intent.putExtra(FeedHolder.COMMENT_COUNT_DATA_KEY, mFeedCommentAdapter!!.count)
        setResult(Activity.RESULT_OK, intent)
        finish()
        super.onBackPressed()
    }

    override fun onItemClick(
        parent: AdapterView<*>?,
        view: View,
        position: Int,
        id: Long
    ) {
        var position = position
        XL.i(TAG, "position:$position")
        if (mContentListView!!.headerViewsCount != 0) {
            position -= 1 // 要减去header view所占用的position
        }
        val postComment =
            mFeedCommentAdapter!!.getItem(position) as PostComment
                ?: return // 要减去header
        // view所占用的position
        XL.i(TAG, "user:" + postComment.user)
        XL.i(TAG, "reply user:" + postComment.replyUser)
        // 保存当前选中的评论信息，在发送评论的时候取出，省一个全局的变量
        mContentEdit!!.tag = postComment
        if (!postComment.user.me) {
            mContentEdit!!.isFocusable = true
            mContentEdit!!.requestFocus()
            UiUtils.showKeyBoard(this, mContentEdit, 0)
            mContentEdit!!.setText("")
            mContentEdit!!.hint =
                mResources.getString(R.string.report_hint) + postComment.user.name + ":"
            val fp = position
            Handler().postDelayed({
                val height =
                    window.findViewById<View>(Window.ID_ANDROID_CONTENT)
                        .height - UiUtils.dip2px(
                        this@FeedCommentActivity,
                        (48 + 45).toFloat()
                    ) - view.height
                mContentListView!!.setSelectionFromTop(fp, height)
            }, 300)
        } else {
            mContentEdit!!.setText("")
            mContentEdit!!.setHint(R.string.comment)
            UiUtils.hideKeyBoard(this)
        }
    }

    /**
     * @Title: sendComment
     * @Description: 发送评论
     * @param @param comment 评论内容
     * @return void 返回类型
     * @throws
     */
    fun sendComment(comment: String, postComment: PostComment?) {
        if (TextUtils.isEmpty(comment.trim { it <= ' ' }) || mPost == null) {
            return
        }
        var replyUserId: String? = null
        if (postComment != null) {
            if (!postComment.user.me) {
                replyUserId = postComment.user.id
            }
        }
        changeViewInSendTime(true)
        AlohaService.shared.postComment(
            mPost!!.postId,
            replyUserId!!,
            comment,
            object : Callback<ApiResponse<Comment2GetData>> {
                override fun failure(arg0: RetrofitError) {
                    ToastUtil.longToast(this@FeedCommentActivity, R.string.network_error)
                }

                override fun success(
                    apiResponse: ApiResponse<Comment2GetData>?,
                    response: Response
                ) {
                    changeViewInSendTime(false)
                    if (isFinishing) {
                        return
                    }
                    if (apiResponse != null) {
                        if (apiResponse.isOk) {
                            mContentEdit!!.setText("") // 重置
                            mContentEdit!!.setHint(R.string.leave_hint)
                            mContentEdit!!.tag = null
                            UiUtils.hideKeyBoard(this@FeedCommentActivity)
                            val postcommentlist =
                                mComment2Service!!.trans(apiResponse.data!!)
                            mFeedCommentAdapter!!.appendListItem(Direct.Late, postcommentlist)
                            mContentListView!!.smoothScrollToPosition(mFeedCommentAdapter!!.count - 1)
                        } else if (apiResponse.data!!.error == IResultDataErrorCode.ERROR_INVALID_COMMENT) {
                            ToastUtil.shortToast(
                                this@FeedCommentActivity,
                                getString(R.string.comment_has_illegalword)
                            )
                        } else if (apiResponse.data!!.error == IResultDataErrorCode.ERROR_BLOCK_BY_OTHER) {
                            ToastUtil.shortToastCenter(
                                this@FeedCommentActivity,
                                getString(R.string.otherside_black_current_user)
                            )
                        } else {
                            ToastUtil.shortToastCenter(
                                this@FeedCommentActivity,
                                getString(R.string.Unkown_Error)
                            )
                        }
                    } else {
                        ToastUtil.shortToastCenter(
                            this@FeedCommentActivity,
                            getString(R.string.Unkown_Error)
                        )
                    }
                }
            })
    }

    /***
     * 正在發送的時候，send 禁用send 按鈕
     *
     * @return void
     */
    fun changeViewInSendTime(isSending: Boolean) {
        if (isSending) {
            mSendTv!!.setBackgroundResource(R.drawable.chat_send_btn_down)
        } else {
            mSendTv!!.setBackgroundResource(R.drawable.chat_send_btn_selector)
        }
        mSendTv!!.isClickable = !isSending
    }

    override fun onItemLongClick(
        parent: AdapterView<*>?,
        view: View,
        position: Int,
        id: Long
    ): Boolean {
        var position = position
        if (mContentListView!!.headerViewsCount != 0) {
            position -= 1 // 要减去header view所占用的position
        }
        val postComment = mFeedCommentAdapter!!.getItem(position) as PostComment
        if (postComment != null) {
            ListItemDialog(this, view as ViewGroup).showListItemPopup(
                this,
                null,
                ListItemType.DELETE
            )
            removePosition = position
            removeCommentId = postComment.id
        }
        return false
    }

    private fun deleteComment(commentId: String?) {
        AlohaService.shared.deleteComment(
            if (mPost != null) mPost!!.postId else "",
            commentId!!
        ).observeOnMainThread(onSuccess = {
            mFeedCommentAdapter!!.removeItem(removePosition)
        }, onError = {
            mHandler?.sendEmptyMessage(GlobalConstants.AppConstact.SERVER_ERROR)
        }).addTo(compositeDisposable = compositeDisposable)
    }

    override fun itemCallback(listItemType: Int) {
        when (listItemType) {
            ListItemType.DELETE -> deleteComment(removeCommentId)
            else -> {
            }
        }
    }

    /**
     * 进入这个人的主页
     *
     * @param user2
     * 被开启主页的用户
     */
    override fun openSomeoneProfile(user2: User) {
        val bundle = Bundle()
        bundle.putParcelable(User.TAG, DockingBeanUtils.transUser(user2))
        startFragment(Profile2Fragment::class.java, bundle, true)
    }

    override fun onPause() {
        super.onPause()
    }

    override fun getHolderType(): Int {
        return 0
    }

    override fun praiseCallback() {}
    override fun deleteCallback() {
        // TODO Auto-generated method stub
    }

    override fun commentClickCallback() {
        // TODO Auto-generated method stub
    }

    override fun showmPrivacyCommentSign() {
        // TODO Auto-generated method stub
    }

    companion object {
        private val TAG = FeedCommentActivity::class.java.simpleName
    }
}