package com.wealoha.social.api

import com.wealoha.social.api.BaseListApiService.ApiListCallback
import com.wealoha.social.api.BaseListApiService.ListContextCallback
import com.wealoha.social.beans.*
import com.wealoha.social.beans.ApiErrorCode.Companion.fromResult
import com.wealoha.social.beans.PostComment.Companion.fromCommentDTO
import com.wealoha.social.inject.Injector
import org.apache.commons.collections4.CollectionUtils
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.client.Response
import java.util.*
import javax.inject.Inject

class Comment2ListApiService : AbsBaseListApiService<PostComment, String>() {
    @JvmField
    @Inject
    var comment2api: ServerApi? = null
    override fun supportContextByCursor(): Boolean {
        return true
    }

    override fun supportPrev(): Boolean {
        return true
    }

    override fun appendToHeader(): Boolean {
        return true
    }

    /**
     * 第一次定位的时候获取的数据.
     */
    override fun getList(
        cursor: String,
        count: Int,
        direct: Direct,
        postId: String,
        callback: ApiListCallback<PostComment?>
    ) {
        comment2api!!.byDirectGetComment2s(
            postId,
            cursor,
            count,
            direct.value,
            object : Callback<Result<Comment2GetData>> {
                override fun failure(arg0: RetrofitError) {}
                override fun success(result: Result<Comment2GetData>, response: Response) {
                    if (result.isOk) {
                        if (result.isOk) {
                            // 拼装数据
                            val cursorid: String = if (direct === Direct.Early) {
                                Direct.Early.value
                            } else {
                                Direct.Late.value
                            }
                            callback.success(trans(result.data!!), cursorid)
                        } else {
                            callback.fail(fromResult(result), null)
                        }
                    } else {
                        callback.fail(fromResult(result), null)
                    }
                }
            })
    }

    override fun getListWithContext(
        cursor: String,
        count: Int,
        param: String,
        callback: ListContextCallback<PostComment>
    ) {
        comment2api!!.getFirstComment2s(
            postId = param,
            cursor = cursor,
            count = count,
            needCtx = true,
            callback = object : Callback<Result<Comment2GetData>> {
                override fun failure(arg0: RetrofitError) {}
                override fun success(result: Result<Comment2GetData>?, arg1: Response) {
                    if (result != null && result.isOk) {
                        // 拼装数据
                        callback.success(
                            trans(result.data!!),
                            result.data!!.lateCursorId,
                            result.data!!.nextCursorId
                        )
                    } else {
                        callback.fail(fromResult(result), null)
                    }
                }
            })
    }

    fun trans(data: Comment2GetData): List<PostComment> {
        if (CollectionUtils.isEmpty(data.list)) {
            return emptyList()
        }
        val result: MutableList<PostComment> = ArrayList(
            data.list!!.size
        )
        data.list?.map { commentDTO ->
            val user2 = getUser(commentDTO.userId, data.userMap, data.imageMap)
            val replyUser = getUser(commentDTO.replyUserId, data.userMap, data.imageMap)
            result.add(fromCommentDTO(commentDTO, replyUser, user2))
        }
        return result
    } // private User getUser(PostComment comment2, Map<String, UserDTO> userMap, Map<String, ImageDTO>
    // imageMap) {
    // UserDTO userDTO = userMap.get(comment2.getUserId());
    // return User.fromDTO(userDTO, Image.fromDTO(imageMap.get(userDTO.avatarImageId)));
    // }
    /**
     * @Description:获取Comment2Data的数据
     * @return
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2015年3月11日
     */
    // protected PostComment getComment2Data(CommentDTO commentDTO) {
    // return new PostComment(commentDTO);
    // }
    init {
        Injector.inject(this)
    }
}