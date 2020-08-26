package com.wealoha.social.api

import android.content.Context
import com.wealoha.social.R
import com.wealoha.social.api.BaseListApiService.ApiListCallback
import com.wealoha.social.beans.*
import com.wealoha.social.beans.ApiErrorCode.Companion.fromResult
import com.wealoha.social.inject.Injector
import com.wealoha.social.push.notification.NotificationCount
import com.wealoha.social.utils.XL
import org.apache.commons.collections4.CollectionUtils
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.client.Response
import java.util.*
import javax.inject.Inject

/**
 * 取通知数据
 *
 * @author javamonk
 * @createTime 2015年2月25日 上午10:34:56
 */
class Notify2ListApiService : AbsBaseListApiService<Notify2, Boolean>() {
    @JvmField
    @Inject
    var notify2api: ServerApi? = null

    @JvmField
    @Inject
    var mContext: Context? = null

    protected fun trans(data: NotifyGetData): List<Notify2> {
        if (CollectionUtils.isEmpty(data.list)) {
            return emptyList()
        }
        val result: MutableList<Notify2> = ArrayList(data.list.size)
        for (dto in data.list) {
            val type = Notify2Type.fromValue(dto.type) ?: continue
            if (type === Notify2Type.PostLike) {
                val d = dto as PostLikeNotify2DTO
                val post = getPost(
                    data.postMap[d.postId]!!,
                    data.userMap,
                    data.imageMap,
                    data.videoMap,
                    data.commentCountMap,
                    data.likeCountMap
                )
                val user2s = getUsers(d.userIds!!, data.userMap, data.imageMap)
                result.add(
                    PostLikeNotify2(
                        type,
                        dto.unread,
                        dto.notifyId,
                        dto.updateTimeMillis,
                        post!!,
                        user2s,
                        dto.count
                    )
                )
            } else if (type === Notify2Type.NewAloha) {
                val d = dto as NewAlohaNotify2DTO
                val user2s = getUsers(d.userIds!!, data.userMap, data.imageMap)
                result.add(
                    NewAlohaNotify2(
                        type,
                        dto.unread,
                        dto.notifyId,
                        dto.updateTimeMillis,
                        dto.count,
                        user2s
                    )
                )
            } else if (type === Notify2Type.PostComment) {
                val d = dto as PostCommentNotify2DTO
                val fromUser = getUser(d.fromUser, data.userMap, data.imageMap)
                val post = getPost(
                    data.postMap[d.postId]!!,
                    data.userMap,
                    data.imageMap,
                    data.videoMap,
                    data.commentCountMap,
                    data.likeCountMap
                )
                result.add(
                    PostCommentNotify2(
                        type,
                        d.unread,
                        dto.notifyId,
                        d.updateTimeMillis,
                        d.replyMe,
                        d.comment!!,
                        d.commentId!!,
                        fromUser!!,
                        post!!,
                        d.count
                    )
                )
            } else if (type === Notify2Type.PostTag) {
                val d = dto as PostTagNotify2DTO
                val fromUser = getUser(d.fromUser, data.userMap, data.imageMap)
                val post = getPost(
                    data.postMap[d.postId]!!,
                    data.userMap,
                    data.imageMap,
                    data.videoMap,
                    data.commentCountMap,
                    data.likeCountMap
                )
                result.add(
                    PostTagNotify2(
                        type,
                        d.unread,
                        dto.notifyId,
                        d.updateTimeMillis,
                        fromUser!!,
                        post!!
                    )
                )
            } else if (type === Notify2Type.PostCommentReplyOnMyPost) {
                val d = dto as PostCommentReplyOnMyPost2DTO
                val fromUser = getUser(d.fromUser, data.userMap, data.imageMap)
                val replyUser = getUser(d.replyUser, data.userMap, data.imageMap)
                val post = getPost(
                    data.postMap[d.postId]!!,
                    data.userMap,
                    data.imageMap,
                    data.videoMap,
                    data.commentCountMap,
                    data.likeCountMap
                )
                val comment = mContext!!.getString(
                    R.string.aloha_post_comment_reply_on_my_post,
                    replyUser!!.name,
                    d.comment
                )
                result.add(
                    PostCommentReplyOnMyPost(
                        type, d.unread, d.notifyId, d.updateTimeMillis,  //
                        replyUser, fromUser, d.commentId, comment, post
                    )
                )
            } else if (type === Notify2Type.PostCommentReplyOnOthersPost) {
                val d = dto as PostCommentReplyOnOthersPost2DTO
                val postAuthor = getUser(d.fromUser, data.userMap, data.imageMap)
                val replyUser = getUser(d.postAuthor, data.userMap, data.imageMap)
                val post = getPost(
                    data.postMap[d.postId]!!,
                    data.userMap,
                    data.imageMap,
                    data.videoMap,
                    data.commentCountMap,
                    data.likeCountMap
                )
                result.add(
                    PostCommentReplyOnOthersPost(
                        type, d.unread, d.notifyId, d.updateTimeMillis,  //
                        replyUser!!, postAuthor!!, d.commentId!!, d.comment!!, post!!
                    )
                )
            }
            // 在这里添加其它类型
        }
        return result
    }

    override fun getList(
        cursor: String,
        count: Int,
        direct: Direct,
        param: Boolean,
        callback: ApiListCallback<Notify2?>
    ) {
        notify2api!!.getNotifies(cursor, count, param, object : Callback<ApiResponse<NotifyGetData>> {
            override fun success(apiResponse: ApiResponse<NotifyGetData>, response: Response) {
                XL.d(TAG, "加载数据成功")
                if (apiResponse.isOk) {
                    // 成功，拼装数据
                    callback.success(trans(apiResponse.data!!), apiResponse.data!!.nextCursorId)
                } else {
                    callback.fail(fromResult(apiResponse), null)
                }
            }

            override fun failure(error: RetrofitError) {
                callback.fail(null, error)
            }
        })
    }

    /**
     * 清理未读数
     */
    fun clearUnread() {
        notify2api?.clearUnread(object : Callback<ApiResponse<ResultData>> {
            override fun success(arg0: ApiResponse<ResultData>, arg1: Response) {
                XL.d(TAG, "clear成功")
                // 清理本地的未读数
                NotificationCount.setCommentCount(0)
            }

            override fun failure(e: RetrofitError) {
                XL.w(TAG, "clearUnread失败", e)
            }
        })
    }

    fun changeReadState(notify: Notify2, isread: Boolean) {
        notify.changeReadState(isread)
    }

    companion object {
        private val TAG = Notify2ListApiService::class.java.simpleName
    }

    init {
        Injector.inject(this)
    }
}