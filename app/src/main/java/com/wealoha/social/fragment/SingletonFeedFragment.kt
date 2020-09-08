package com.wealoha.social.fragment

import android.animation.LayoutTransition
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import butterknife.ButterKnife
import butterknife.InjectView
import butterknife.OnClick
import com.wealoha.social.BaseFragAct
import com.wealoha.social.R
import com.wealoha.social.activity.PicSendActivity
import com.wealoha.social.activity.PicSendActivity.PicSendActivityBundleKey
import com.wealoha.social.adapter.feed.BaseFeedHolder
import com.wealoha.social.adapter.feed.BaseFeedHolder.Holder2FragCallback
import com.wealoha.social.adapter.feed.Feed2Adapter
import com.wealoha.social.adapter.feed.Feed2Adapter.Adapter2FragmentCallback
import com.wealoha.social.adapter.feed.FeedHolder
import com.wealoha.social.adapter.feed.VideoFeedHolder
import com.wealoha.social.api.BaseListApiService.NoResultCallback
import com.wealoha.social.api.SingletonFeedListApiService
import com.wealoha.social.beans.ApiErrorCode
import com.wealoha.social.beans.Post
import com.wealoha.social.utils.XL
import com.wealoha.social.widget.BaseListApiAdapter.LoadCallback

class SingletonFeedFragment : BaseFragment(), Holder2FragCallback,
    View.OnClickListener, Adapter2FragmentCallback {

    var feedService = SingletonFeedListApiService()

    @JvmField
    @InjectView(R.id.list)
    var postList: ListView? = null

    @JvmField
    @InjectView(R.id.tags_method_container)
    var tagsMethodLayout: LinearLayout? = null

    @JvmField
    @InjectView(R.id.remove_tag)
    var mRemoveTagTv: TextView? = null

    @JvmField
    @InjectView(R.id.take_to_mine)
    var mTakeToMineTv: TextView? = null

    @JvmField
    @InjectView(R.id.config_details_back)
    var mBackTv: ImageView? = null
    private var rootView: View? = null
    private var mPost: Post? = null
    private var feedAdapter: Feed2Adapter? = null
    private var feedType = 0
    private var alertDialog: Dialog? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.frag_singleton_feed, container, false)
        return rootView
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        ButterKnife.inject(this, rootView)
        if (postData) {
            initMenuViewAnim()
            feedAdapter = Feed2Adapter(this, feedService)
            feedAdapter!!.setAdt2FragCallback(this)
            feedAdapter!!.setHolder2FragCallback(this) // 设置holder 的回调
            postList!!.adapter = feedAdapter
            loadNextPage(false)
        }
    }

    /***
     * 初始化移除和分享按钮的出现和隐藏动画
     *
     * @return void
     */
    private fun initMenuViewAnim() {
        val transition = LayoutTransition()
        transition.setDuration(300)
        transition.setAnimator(
            LayoutTransition.APPEARING,
            transition.getAnimator(LayoutTransition.APPEARING)
        )
        transition.setAnimator(
            LayoutTransition.DISAPPEARING,
            transition.getAnimator(LayoutTransition.DISAPPEARING)
        )
        tagsMethodLayout!!.layoutTransition = transition
    }

    protected fun loadNextPage(resetdata: Boolean) {
        if (resetdata) {
            feedAdapter!!.resetState()
        }
        feedAdapter!!.loadEarlyPage(1, mPost!!.postId, object : LoadCallback {
            override fun success(
                hasEarly: Boolean,
                hasLate: Boolean
            ) {
                if (!isVisible) {
                    return
                }
                if (feedAdapter?.listData != null && feedAdapter?.listData?.size ?: 0 > 0) {
                    mPost = feedAdapter?.listData?.get(0)
                }
                initView()
            }

            override fun fail(
                code: ApiErrorCode,
                exception: Exception
            ) {
                XL.i(Companion.TAG, "load fail")
            }

            override fun dataState(size: Int, isnull: Boolean) {}
        })
    }

    override fun onResume() {
        super.onResume()
        startVideo()
    }

    /***
     * 根据feed 类型来渲染不同的视图：是否包含分享功能
     *
     * @return void
     */
    fun initView() {
        if (feedType == FeedHolder.TAGS_HOLDER) {
            tagsMethodLayout!!.visibility = View.VISIBLE
            changeViewByTags(mPost!!.isTagMe)
        } else {
            tagsMethodLayout!!.visibility = View.GONE
        }
        startVideo()
    }

    private var videoHolder: VideoFeedHolder? = null

    /***
     * 播放视频
     *
     * @return void
     */
    @Synchronized
    fun startVideo() {
        postList!!.postDelayed(
            Runnable
            // 如果是视频 ，那么直接播放
            {
                if (!isVisible || postList == null) {
                    return@Runnable
                }
                val view = postList!!.getChildAt(0) as View
                if (view != null) {
                    val bfh = view.tag as BaseFeedHolder
                    if (bfh != null && bfh.contentHolder is VideoFeedHolder) {
                        videoHolder = bfh.contentHolder as VideoFeedHolder
                        if (videoHolder != null) {
                            (bfh.contentHolder as VideoFeedHolder).startMediaPlayer()
                        }
                    }
                }
            }, 200
        )
    }

    override fun onPause() {
        super.onPause()
        if (videoHolder != null) {
            videoHolder!!.stopPlayer() // 停掉视频
        }
    }

    /**
     * @Title: changeViewByTags
     * @Description: 根据当前feed有没有自己的标签来渲染不同的视图
     */
    private fun changeViewByTags(isDoubleView: Boolean) {
        if (isDoubleView) {
            mTakeToMineTv!!.visibility = View.VISIBLE
            mRemoveTagTv!!.visibility = View.VISIBLE
        } else {
            // 改变视图，转发和移除tag的两个textview
            mTakeToMineTv!!.visibility = View.GONE
            mRemoveTagTv!!.visibility = View.VISIBLE
            mRemoveTagTv!!.setText(R.string.tag_removed)
            mRemoveTagTv!!.setBackgroundColor(resources.getColor(R.color.light_gray))
            mRemoveTagTv!!.isClickable = false
        }
    }

    /***
     * 從開啟這個frag的組件那裡得到post數據
     *
     * @return void
     */
    val postData: Boolean
        get() {
            feedType = arguments!!.getInt(Companion.TAG, 2)
            mPost = arguments!![Post.TAG] as Post?
            return if (mPost != null) {
                true
            } else false
        }

    override fun getHolderType(): Int {
        return feedType
    }

    @OnClick(R.id.remove_tag, R.id.take_to_mine, R.id.config_details_back)
    override fun onClick(v: View) {
        when (v.id) {
            R.id.remove_tag ->                 // ((FeedHolder) postList.getChildAt(0).getTag()).removeMyTag();
                openAlertDialog(activity as BaseFragAct?)
            R.id.take_to_mine -> shareFeed()
            R.id.config_details_back -> activity!!.finish()
            else -> {
            }
        }
    }

    /**
     * @Title: copyTheFeedToMe
     * @Description: 转发
     */
    private fun shareFeed() {
        val post = feedAdapter!!.getItem(0) as Post ?: return
        val intent = Intent(activity, PicSendActivity::class.java)
        val bundle = Bundle()
        bundle.putString(PicSendActivityBundleKey.PIC_SEND_TYPE, PicSendActivity.SHARE_FEED)
        bundle.putParcelable(Post.TAG, post)
        intent.putExtras(bundle)
        activity!!.startActivityForResult(
            intent,
            OPEN_PIC_SEND_REQUESTCODE
        )
    }

    override fun onActivityResultCallback(
        requestcode: Int,
        resultcode: Int,
        result: Intent
    ) {
        if (resultcode != Activity.RESULT_OK) {
            return
        }
        when (requestcode) {
            OPEN_PIC_SEND_REQUESTCODE -> {
                activity!!.setResult(Activity.RESULT_OK)
                activity!!.finish()
            }
        }
    }

    /**
     * @Title: openGuideDialog
     * @Description: 移除你的標籤
     */
    fun openAlertDialog(baseAct: BaseFragAct?) {
        val view = LayoutInflater.from(baseAct)
            .inflate(R.layout.dialog_first_aloha_time, LinearLayout(baseAct), false)
        val title =
            view.findViewById<View>(R.id.first_aloha_title) as TextView
        title.setText(R.string.remove_tag_dialog)
        val message =
            view.findViewById<View>(R.id.first_aloha_message) as TextView
        message.gravity = Gravity.CENTER
        message.setText(R.string.remove_tag_dialog_message)
        val close = view.findViewById<View>(R.id.close_tv) as TextView
        close.setText(R.string.cancel)
        close.setOnClickListener {
            if (alertDialog != null && alertDialog!!.isShowing) {
                alertDialog!!.dismiss()
            }
        }
        val close02 = view.findViewById<View>(R.id.close_tv_02) as TextView
        close02.visibility = View.VISIBLE
        close02.setText(R.string.delete)
        close02.setOnClickListener {
            if (alertDialog != null && alertDialog!!.isShowing) {
                alertDialog!!.dismiss()
            }
            removeCurrentUserTag()
        }
        alertDialog = AlertDialog.Builder(baseAct) //
            .setView(view) //
            .setCancelable(false) //
            .create()
        alertDialog?.show()
    }

    private fun removeCurrentUserTag() {
        val post = feedAdapter!!.getItem(0) as Post
        feedService.removeTag(
            post.postId,
            contextUtil.currentUser.id,
            object : NoResultCallback {
                override fun success() {
                    loadNextPage(true)
                    mPost!!.removeTagMe()
                    changeViewByTags(false)
                }

                override fun fail(
                    code: ApiErrorCode?,
                    exception: Exception?
                ) {
                }
            })
    }

    /***
     * 单例feed 也删除post后，要关闭当前feed页，并刷新profile
     *
     * @return
     */
    override fun deletePostCallback() {
        if (isVisible) {
            activity!!.setResult(Activity.RESULT_OK)
            activity!!.finish()
        }
    }

    override fun praiseCallback() {}
    override fun deleteCallback() {
    }

    override fun commentClickCallback() {
    }

    companion object {
        @JvmField
        val TAG = SingletonFeedFragment::class.java.name
        const val OPEN_PIC_SEND_REQUESTCODE = 1002
    }
}