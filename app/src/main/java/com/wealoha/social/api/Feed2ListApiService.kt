package com.wealoha.social.api

import android.content.Context
import android.util.Log
import com.squareup.picasso.Picasso
import com.wealoha.social.api.BaseListApiService.*
import com.wealoha.social.beans.*
import com.wealoha.social.beans.ApiErrorCode.Companion.fromResult
import com.wealoha.social.beans.CommonImage.Companion.fromDTO
import com.wealoha.social.beans.CommonVideo.Companion.fromDTO
import com.wealoha.social.beans.HashTag.Companion.fromDTO
import com.wealoha.social.beans.Post.Companion.hasTagForMe
import com.wealoha.social.commons.GlobalConstants.ImageSize
import com.wealoha.social.inject.Injector
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.client.Response
import javax.inject.Inject
import kotlin.collections.ArrayList

open class Feed2ListApiService : AbsBaseListApiService<Post, String>() {

    @JvmField
    @Inject
    var feed2Api: ServerApi? = null

    @JvmField
    @Inject
    var context: Context? = null

    private var postList: List<Post> = arrayListOf()


    override fun getList(
        cursor: String,
        count: Int,
        direct: Direct,
        userid: String,
        callback: ApiListCallback<Post?>
    ) {
        val _result = ApiResponse.success(
            data = FeedGetData.fake(
                cursor = cursor,
                direct = direct,
                userID = userid,
                count = count
            )
        )
        callback.success(
            transResult2List(_result.data!!, ""),
            _result.data!!.nextCursorId
        )
    }

    /***
     * 赞post 的用户列表
     *
     * @param cursor
     * @param count
     * @param postid
     * @param callback
     * @return void
     */
    fun getPraiseList(
        cursor: String?,
        count: Int,
        postid: String?,
        callback: ApiCallback<List<User?>?>
    ) {
        feed2Api!!.getPraiseList(
            postid!!,
            cursor!!,
            count,
            object : Callback<ApiResponse<UserListGetData>> {
                override fun failure(error: RetrofitError) {
                    callback.fail(null, error)
                }

                override fun success(apiResponse: ApiResponse<UserListGetData>, arg1: Response) {
                    if (apiResponse == null || !apiResponse.isOk) {
                        callback.fail(fromResult(apiResponse), null)
                    } else {
                        callback.success(transUserListGetData2List(apiResponse.data!!))
                    }
                }
            })
    }

    private fun transUserListGetData2List(userListGetData: UserListGetData): List<User> {
        return getUsers(userListGetData.list, userListGetData.imageMap)
    }

    protected fun transResult2List(result: FeedGetData, currentUserid: String): List<Post> {
        val postList: ArrayList<Post> = ArrayList(result.list.size)
        for (postDto in result.list) {
            val type = FeedType.fromValue(postDto.type)!!
            val user2 = getUser(postDto.userId, result.userMap, result.imageMap)

            val userTagList = gerUserTagList(postDto.userTags, result.userMap, result.imageMap)
            val commonImage = fromDTO(result.imageMap[postDto.imageId]!!)
            val commonVideo = fromDTO(result.videoMap[postDto.videoId]!!)

            val recentCommentList = transCommentDTOList2PostCommentList(
                postDto.recentComments,
                result.userMap,
                result.imageMap
            )
            val post = Post( //
                postDto.postId,  //
                type,  //
                postDto.description,  //
                postDto.createTimeMillis,  //
                postDto.mine,  //
                postDto.liked,  //
                hasTagForMe(userTagList, currentUserid),  //
                postDto.venue,  //
                postDto.venueId,  //
                postDto.latitude,  //
                postDto.longitude,  //
                postDto.venueAbroad,  //
                user2,  //
                userTagList,  //
                commonImage,  //
                commonVideo,  //
                result.commentCountMap[postDto.postId]!!,  //
                result.likeCountMap[postDto.postId]!!,  //
                recentCommentList,  //
                fromDTO(postDto.hashtag),  //
                postDto.hasMoreComment
            )
            postList.add(post)
        }
        return postList
    }

    override fun setAdapterListCallback(callback: AdapterListDataCallback<Post>) {
        super.setAdapterListCallback(callback)
        postList = callback.listData
    }

    override fun fetchPhoto(
        firstVisibleItem: Int,
        totalItemCount: Int,
        direction: Boolean,
        mScreenWidth: Int
    ) {
        if (postList.isEmpty()) {
            return
        }
        var first = 0
        var end = 0
        // 正向
        if (direction) {
            first = firstVisibleItem
            end = if (firstVisibleItem + 10 > totalItemCount) {
                totalItemCount
            } else {
                firstVisibleItem + 10
            }
        } else {
            end = firstVisibleItem
            first = if (firstVisibleItem - 10 < 0) {
                0
            } else {
                firstVisibleItem - 10
            }
        }
        Log.i("LOAD_MEMORY", "$first=====$end=====$firstVisibleItem")
        end = if (end > postList!!.size - 1) postList!!.size - 1 else end
        for (i in first until end - 2) {
            Log.i("LOAD_MEMORY", "++++++$i")
            val post = postList!![i]
            Picasso.get().load(post!!.commonImage!!.getUrlSquare(mScreenWidth)).fetch()
            Picasso.get().load(post.user!!.avatarImage.getUrlSquare(ImageSize.AVATAR_ROUND_SMALL))
                .fetch()
        }
    }

    /***
     * 赞
     *
     * @param postId
     * @param callback
     * @return void
     */
    fun praiseFeed(postId: String?, callback: NoResultCallback) {
        feed2Api!!.praisePost(postId!!, object : Callback<ApiResponse<ResultData>> {
            override fun success(apiResponse: ApiResponse<ResultData>, arg1: Response) {
                if (apiResponse == null || !apiResponse.isOk) {
                    callback.fail(fromResult(apiResponse), null)
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
     * 取消赞
     *
     * @param postid
     * @param callback
     * @return void
     */
    fun canclePraiseFeed(postid: String?, callback: NoResultCallback) {
        feed2Api!!.dislikePost(postid!!, object : Callback<ApiResponse<ResultData>> {
            override fun success(apiResponse: ApiResponse<ResultData>, arg1: Response) {
                if (apiResponse == null || !apiResponse.isOk) {
                    callback.fail(fromResult(apiResponse), null)
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
     * 删除post
     *
     * @param postId
     * @param callback
     * @return void
     */
    fun deletePost(postId: String?, callback: NoResultCallback) {
        feed2Api!!.deletePost(postId!!, object : Callback<ApiResponse<ResultData>> {
            override fun success(apiResponse: ApiResponse<ResultData>, arg1: Response) {
                if (apiResponse == null || !apiResponse.isOk) {
                    callback.fail(fromResult(apiResponse), null)
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
     * 删除post
     *
     * @param postId
     * @param callback
     * @return void
     */
    fun reportPost(postId: String?, callback: NoResultCallback) {
        feed2Api!!.reportFeed(postId!!, "", "", object : Callback<ApiResponse<ResultData>> {
            override fun success(apiResponse: ApiResponse<ResultData>, arg1: Response) {
                if (apiResponse == null || !apiResponse.isOk) {
                    callback.fail(fromResult(apiResponse), null)
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
     * 移除标签
     *
     * @param postId
     * @param callback
     * @return void
     */
    fun removeTag(postId: String, tagUserId: String, callback: NoResultCallback) {
        feed2Api!!.removeTag(postId, tagUserId, object : Callback<ResultData> {
            override fun success(result: ResultData, arg1: Response) {
                callback.success()
            }

            override fun failure(error: RetrofitError) {
                callback.fail(null, error)
            }
        })
    }

    /***
     * 判断数据是否为空
     *
     * @return
     * @return boolean
     */
    val isDataEmpty: Boolean
        get() = postList == null || postList!!.size == 0

    companion object {
        val TAG = Feed2ListApiService::class.java.simpleName
    }

    init {
        Injector.inject(this)
    }

}